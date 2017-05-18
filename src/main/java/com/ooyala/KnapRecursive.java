package com.ooyala;

import com.google.gson.Gson;
import com.ooyala.model.CampaignCombination;
import com.ooyala.model.CampaignDetails;
import com.ooyala.model.Constants;
import com.ooyala.util.NormalMemoizationUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * Created by schinnas on 5/13/17.
 */

public class KnapRecursive {

    NormalMemoizationUtil memoizationUtil = new NormalMemoizationUtil();

    Map<String, CampaignDetails> idCampaignMap = new HashMap();

    List<CampaignDetails> campaignDetailsList = new ArrayList<CampaignDetails>();

    Long forecastedImpressions = 0l;

    private CampaignCombination maxRevenue(List<CampaignDetails> campaignDetailsList, Long forecastedImpressions) {

        TreeSet<Long> remainderSet = new TreeSet<Long>();


        /** return revenue **/
        return memoizationUtil.getRevenue(forecastedImpressions, false);

    }


    private CampaignCombination getMaxRevenue() {

        return maxRevenue(campaignDetailsList, forecastedImpressions);
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

    private TreeSet<Long> getAllCampaignImpressionList1(List<CampaignDetails> campaignDetailsList, Long weightCapacity) {

        TreeSet<Long> campaignCombination = new TreeSet();

        for (CampaignDetails campaignDetails : campaignDetailsList) {

            Long count = 0l;

            while (campaignDetails.getImpressionsPerCampaign() * count <= weightCapacity) {
                if (campaignDetails.getImpressionsPerCampaign() * count > 0) {
                    campaignCombination.add(campaignDetails.getImpressionsPerCampaign() * count);
                }
                count++;
            }
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

            String type = campaignDetails.getType();

            if (type.equals("I")) {

                Integer totalNoOfCampaign = idToNoOfCampaignMap.get(key) * Constants.SCALING_FACTOR.intValue();
                String originalId = campaignDetails.getOriginalId();
                CampaignDetails originalCampaignDetails = idCampaignMap.get(originalId);
                originalCampaignDetails.setTotalNoOfCampaigns(originalCampaignDetails.getTotalNoOfCampaigns() + totalNoOfCampaign);
            }
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

        System.out.println(new Gson().toJson(campaignCombination));

        identifyNoOfCampaignsPerAdvertiser(campaignCombination);

        writeToOutputFile(campaignCombination);

    }

    public static void main(String... args) {

        KnapSackModified revenueOptimization = new KnapSackModified();

        revenueOptimization.identifyMaximumRevenue(Constants.INPUT_FILE);

    }
}
