package com.ooyala.util;

/**
 * Created by schinnas on 5/16/17.
 */

import com.google.gson.Gson;
import com.ooyala.model.CampaignCombination;
import com.ooyala.model.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by schinnas on 5/13/17.
 */


public class NormalMemoizationUtil {


    TreeMap<Long, CampaignCombination> impressionToMaxRevenue = new TreeMap<Long, CampaignCombination>();

    public CampaignCombination getRevenue(Long impression, Boolean debug) {

        CampaignCombination campaignCombination = null;


        if (impressionToMaxRevenue != null && impressionToMaxRevenue.floorEntry(impression) != null) {
            campaignCombination = impressionToMaxRevenue.floorEntry(impression).getValue().getNewCopy();

            if(debug){
                System.out.println("impressionToMaxRevenue key size=" + impressionToMaxRevenue.keySet().size() +" highest " + impressionToMaxRevenue.lastEntry());
                System.out.println("Get**** impression=" + impression +" new " + new Gson().toJson(campaignCombination));
            }

            return campaignCombination;
        }

        return null;

    }

    public CampaignCombination getLastRevenue(Long impression, Boolean debug) {

        CampaignCombination campaignCombination = null;

        Double revenue = 0d;

        while(impression>=0){

            System.out.println("impressionToMaxRevenue.first"+ impressionToMaxRevenue.firstKey().longValue());

            System.out.println("impressionToMaxRevenue.last"+ impressionToMaxRevenue.firstKey().longValue());

            if (impressionToMaxRevenue != null && impressionToMaxRevenue.floorEntry(impression) != null) {
                campaignCombination = impressionToMaxRevenue.floorEntry(impression).getValue().getNewCopy();


                revenue = revenue + campaignCombination.getTotalRevenue();

                if(debug){
                    System.out.println("impressionToMaxRevenue key size=" + impressionToMaxRevenue.keySet().size() +" highest " + impressionToMaxRevenue.lastEntry());
                    System.out.println("Get**** impression=" + impression +" new " + new Gson().toJson(campaignCombination));
                }

                impression = impression - campaignCombination.getTotalNoOfImpression();

                System.out.println(impression);

            }else{
                break;
            }

        }

        System.out.println("revenue-"+revenue);

        return null;

    }


    public void printDetails(Boolean debug) {

        if(debug){
            System.out.println(" impression " + impressionToMaxRevenue.keySet().size() +"first entry " + impressionToMaxRevenue.firstKey() + " last entry " + impressionToMaxRevenue.lastEntry());
        }

    }

    public void updateRevenue(CampaignCombination campaignCombination, Long impression, Boolean debug) {

        if(debug){
            System.out.println("update****** impression=" + impression +" new " + new Gson().toJson(campaignCombination));
        }

        impressionToMaxRevenue.put(impression, campaignCombination);

    }

}

