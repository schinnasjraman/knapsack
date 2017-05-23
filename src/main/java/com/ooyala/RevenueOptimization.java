package com.ooyala;

import com.ooyala.model.CampaignCombination;
import com.ooyala.model.CampaignDetails;
import com.ooyala.model.Constants;
import com.ooyala.util.MemoizationUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * Created by schinnas on 5/13/17.
 */

public class RevenueOptimization {

    MemoizationUtil memoizationUtil = new MemoizationUtil();

    Map<String, CampaignDetails> idCampaignMap = new HashMap();

    List<CampaignDetails> campaignDetailsList = new ArrayList<CampaignDetails>();

    Long forecastedImpressions = 0l;

    private CampaignCombination maxRevenue(List<CampaignDetails> campaignDetailsList, Long forecastedImpressions) {

//        forecastedImpressions = 3000000l;

        TreeSet<Long> campaignCombination = getAllCampaignImpressionList(campaignDetailsList, forecastedImpressions);

        Long lastCalculatedValue = 0l;

        System.out.println("lastCalculatedValue = " + lastCalculatedValue);

        /** for every monetizable impression count, identify the max revenue **/

        //for (Long currentCapacity : campaignCombination)
        {
            {
//                System.out.println("Input=" + currentCapacity);

                Long count = campaignCombination.first() - 1;
                {
                    //  while (count * currentCapacity <= forecastedImpressions)
                    while (count <= forecastedImpressions) {
                        updateMaxPossibleRevenueForTheCapacity(count, campaignDetailsList);
                        count++;
                    }
                }
            }
        }

        updateMaxPossibleRevenueForTheCapacity(forecastedImpressions, campaignDetailsList);

        return memoizationUtil.getRevenue(forecastedImpressions);

    }

    private CampaignCombination getMaxRevenue() {

        return maxRevenue(campaignDetailsList, forecastedImpressions);
    }


    private void updateMaxPossibleRevenueForTheCapacity(Long currentCapacity, List<CampaignDetails> campaignDetailsList) {

        Double currentMaxValue = 0.0;

        for (CampaignDetails campaignDetails : campaignDetailsList) {

            if (campaignDetails.getImpressionsPerCampaign() <= currentCapacity) {

                Long remainingImpressions = currentCapacity - campaignDetails.getImpressionsPerCampaign();

                Double revenueForRemainingImpression = 0.0d;

                CampaignCombination campaignCombination = memoizationUtil.getRevenue(remainingImpressions);

                if (campaignCombination != null) {

                    revenueForRemainingImpression = campaignCombination.getTotalRevenue();
                }

                Double maxValue = campaignDetails.getRevenuePerCampaign() + revenueForRemainingImpression;

                /** check if maxvalue is better profit for the current capacity **/

                if (maxValue.compareTo(currentMaxValue) > 0) {

                    currentMaxValue = maxValue;

                    CampaignCombination newcampaignCombination = new CampaignCombination();

                    if (campaignCombination != null)
                        newcampaignCombination = campaignCombination.getNewCopy();

                    newcampaignCombination.addCampaign(campaignDetails, currentMaxValue);

                    memoizationUtil.updateRevenue(newcampaignCombination, currentCapacity);

                }
            }
        }

    }

    private TreeSet<Long> getAllCampaignImpressionList(List<CampaignDetails> campaignDetailsList, Long weightCapacity) {

        TreeSet<Long> campaignCombination = new TreeSet();

        for (CampaignDetails campaignDetails : campaignDetailsList) {

            if (campaignDetails.getImpressionsPerCampaign() == 0 && campaignDetails.getRevenuePerCampaign() != 0) {
                throw new RuntimeException("Input Error");
            }

            if (campaignDetails.getRevenuePerCampaign() > 0)
                campaignCombination.add(campaignDetails.getImpressionsPerCampaign());
        }

        return campaignCombination;

    }

    private void readFileContent(String fileName) {

        try {

            Scanner scanner = new Scanner(new File(fileName));

            forecastedImpressions = Long.valueOf(scanner.nextLine());

            while (scanner.hasNext()) {

                String line = scanner.nextLine();

                String[] word = line.split(",");

                CampaignDetails campaignDetails = new CampaignDetails(word[0], Long.valueOf(word[1]), Double.valueOf(word[2]));

                campaignDetailsList.add(campaignDetails);

            }

        } catch (IOException e) {

            e.printStackTrace();
            throw new RuntimeException("Error readinf file");
        }
    }

    private void createIdCampaignMap() {

        for (CampaignDetails campaignDetails : campaignDetailsList) {

            idCampaignMap.put(campaignDetails.getId(), campaignDetails);
        }
    }

    private void identifyNoOfCampaignsPerAdvertiser(CampaignCombination campaignCombination) {

        Map<String, Integer> idToNoOfCampaignMap = campaignCombination.getAdvertiserToNoOfCampaignMap();

        for (String key : idToNoOfCampaignMap.keySet()) {

            CampaignDetails campaignDetails = idCampaignMap.get(key);

            campaignDetails.setTotalNoOfCampaigns(idToNoOfCampaignMap.get(key));
        }

    }

    private void writeToOutputFile(CampaignCombination campaignCombination) {

        try {
            Writer fileWriter = new FileWriter(Constants.OUTPUT_FILE, false);

            for (CampaignDetails campaignDetails : campaignDetailsList) {
                fileWriter.write(campaignDetails.toString() + "\n");
            }

            String lastLine = campaignCombination.getTotalNoOfImpression() + "," + String.format("%.5f", campaignCombination.getTotalRevenue());
            fileWriter.write(lastLine);
            fileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void identifyMaximumRevenue(String fileName) {

        readFileContent(fileName);

        createIdCampaignMap(); // just for easier access.

        CampaignCombination campaignCombination = getMaxRevenue();

        identifyNoOfCampaignsPerAdvertiser(campaignCombination);

        writeToOutputFile(campaignCombination);
    }

    public static void main(String... args) {

        RevenueOptimization revenueOptimization = new RevenueOptimization();

        revenueOptimization.identifyMaximumRevenue(Constants.INPUT_FILE);

    }
}
