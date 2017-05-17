package com.ooyala.util;

import com.ooyala.model.CampaignCombination;
import com.ooyala.model.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by schinnas on 5/13/17.
 */

/**
 * this class helps in maintaining low memory footprint for the memoization table( map in our case )
 * <p>
 * for update :
 * <p>
 * 1) identify bin no
 * 2) check if bin no is seen before.
 * 3) if bin no is not seen, create new bin in the memory. eviction is done if cache is full.
 * 4) if bin no is seen, check if bin is in memory, if not load it from disk by checking eviction policy
 * <p>
 * for get :
 * 1) identify bin no.
 * 2) load the bins which gives floor entry for the impression queried and return the value.
 */

public class MemoizationUtil {

    FileUtil fileUtil = new FileUtil();

    private final Long MAP_SIZE = Constants.BIN_SIZE;

    private final String FILE_PATH = Constants.BIN_ON_DISK_LOCATION;

    private final int MAX_NO_OF_BINS = Constants.NO_OF_BINS;

    TreeSet<Long> seenBins = new TreeSet<Long>();

    TreeMap<Long, Long> binAccessTime = new TreeMap();

    HashMap<Long, TreeMap<Long, CampaignCombination>> impressionToMaxRevenueBin = new HashMap();

    public CampaignCombination getRevenue(Long impression) {

        Long binNo = impression / MAP_SIZE;

        if (seenBins.size() > 0)
            return getRevenueForBinNewMethod(binNo, impression);
        else
            return null;


    }

    public void updateRevenue(CampaignCombination campaignCombination, Long impression) {

        Long binNo = impression / MAP_SIZE;

        if (isBinSeenBefore(binNo)) {
            if (!isBinInMemory(binNo)) {
                loadBinInMemory(binNo);
            }
        } else {
            createNewBin(binNo);
        }

        updateRevenueForImpression(campaignCombination, binNo, impression);
    }

    private void createNewBin(Long binNo) {

        TreeMap<Long, CampaignCombination> impressionToMaxRevenue = new TreeMap<Long, CampaignCombination>();

        if (isBinCacheFull())
            evictLeastRecentlyUsedBin();

        impressionToMaxRevenueBin.put(binNo, impressionToMaxRevenue);

        updateBinAccessTime(binNo);
        seenBins.add(binNo);
    }

    public CampaignCombination updateRevenueForImpression(CampaignCombination campaignCombination, Long binNo, Long impression) {

        TreeMap<Long, CampaignCombination> impressionToMaxRevenue = getValueForBin(binNo);

        impressionToMaxRevenue.put(impression, campaignCombination);

        return campaignCombination;
    }

    private CampaignCombination getRevenueForBinNewMethod(Long binNo, Long impression) {

        Long firstBin = seenBins.first().longValue();

        if (binNo.longValue() == firstBin.longValue()) {

            if (!isBinInMemory(binNo)) {
                loadBinInMemory(binNo);
            }

            TreeMap<Long, CampaignCombination> impressionToMaxRevenue = getValueForBin(binNo);

            CampaignCombination campaignCombination = null;

            if (impressionToMaxRevenue.floorEntry(impression) != null) {

                campaignCombination = impressionToMaxRevenue.floorEntry(impression).getValue().getNewCopy();

            }

            return campaignCombination;
        } else {

            Long binToSearch = binNo.longValue();

            CampaignCombination campaignCombination = null;

//            TreeSet<Long> subSet = new TreeSet(seenBins.subSet(seenBins.first(), true, binToSearch, true));

            for (Long seenBinNo : seenBins.descendingSet())

//                for (Long seenBinNo : seenBins.descendingSet())

                {

                    if (seenBinNo <= binToSearch) {

                        if (!isBinInMemory(seenBinNo)) {
                            loadBinInMemory(seenBinNo);
                        }

                        TreeMap<Long, CampaignCombination> impressionToMaxRevenue = getValueForBin(seenBinNo);

                        if (impressionToMaxRevenue != null && impressionToMaxRevenue.floorEntry(impression) != null) {
                            campaignCombination = impressionToMaxRevenue.floorEntry(impression).getValue().getNewCopy();
                            return campaignCombination;
                        }
                    }
                }

            return campaignCombination;

        }
    }


    private TreeMap<Long, CampaignCombination> getValueForBin(Long binNo) {

        TreeMap<Long, CampaignCombination> impressionToMaxRevenue = impressionToMaxRevenueBin.get(binNo);

        if (impressionToMaxRevenue != null)
            updateBinAccessTime(binNo);

        return impressionToMaxRevenue;
    }

    private void updateBinAccessTime(Long binNo) {

        if (seenBins.contains(binNo))
            binAccessTime.put(binNo, System.currentTimeMillis());
    }

    private Boolean isBinCacheFull() {
        return impressionToMaxRevenueBin.size() >= MAX_NO_OF_BINS;
    }

    private String getFileName(Long binNo) {

        return FILE_PATH + binNo + ".txt";

    }

    private void loadBinInMemory(Long binNo) {

        if (isBinCacheFull())
            evictLeastRecentlyUsedBin();

        if (binNo == null) {
            return;
        }

        TreeMap<Long, CampaignCombination> campaignCombinationTreeMap = fileUtil.readFile(getFileName(binNo));

        impressionToMaxRevenueBin.put(binNo, campaignCombinationTreeMap);
        updateBinAccessTime(binNo);

    }

    private void evictLeastRecentlyUsedBin() {

        Long leastTime = Long.MAX_VALUE;
        Long leastKey = -1l;


        for (Map.Entry<Long, Long> binEntry : binAccessTime.entrySet()) {
            if (binEntry.getValue() < leastTime) {
                leastTime = binEntry.getValue();
                leastKey = binEntry.getKey();
            }
        }

        if (leastKey > -1 && leastTime.compareTo(Long.MAX_VALUE) < 0) {

            if (impressionToMaxRevenueBin.containsKey(leastKey)) {
                fileUtil.writeToAFile(impressionToMaxRevenueBin.get(leastKey), getFileName(leastKey));
                impressionToMaxRevenueBin.remove(leastKey);
                binAccessTime.remove(leastKey);
            } else {

                throw new RuntimeException("Bin not found");
            }
        }

        if (impressionToMaxRevenueBin.keySet().size() > MAX_NO_OF_BINS) {
            throw new RuntimeException("BinOverflow");
        }
    }

    private Boolean isBinSeenBefore(Long binNo) {
        return seenBins.contains(binNo);
    }

    private Boolean isBinInMemory(Long binNo) {
        return impressionToMaxRevenueBin.containsKey(binNo);
    }


}
