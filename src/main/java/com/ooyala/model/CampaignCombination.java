package com.ooyala.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by schinnas on 5/13/17.
 */

public class CampaignCombination implements Serializable {

    public Map<String, Integer> advertiserToNoOfCampaignMap = new HashMap<String, Integer>();

    public Long totalNoOfImpression = 0l;
    public Double totalRevenue = 0.0;

    public void addCampaign(CampaignDetails campaignDetails, Double revenue) {

        if (advertiserToNoOfCampaignMap.containsKey(campaignDetails.getId())) {
            Integer noOfCampaigns = advertiserToNoOfCampaignMap.get(campaignDetails.getId());
            noOfCampaigns = noOfCampaigns + 1;
            advertiserToNoOfCampaignMap.put(campaignDetails.getId(), noOfCampaigns);
        } else {
            advertiserToNoOfCampaignMap.put(campaignDetails.getId(), 1);
        }

        totalNoOfImpression = totalNoOfImpression + campaignDetails.getImpressionsPerCampaign();
        totalRevenue = revenue;
    }

    public Map<String, Integer> getAdvertiserToNoOfCampaignMap() {
        return advertiserToNoOfCampaignMap;
    }

    public void setAdvertiserToNoOfCampaignMap(Map<String, Integer> advertiserToNoOfCampaignMap) {
        this.advertiserToNoOfCampaignMap = advertiserToNoOfCampaignMap;
    }

    public Long getTotalNoOfImpression() {
        return totalNoOfImpression;
    }

    public void setTotalNoOfImpression(Long totalNoOfImpression) {
        this.totalNoOfImpression = totalNoOfImpression;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public CampaignCombination getNewCopy() {

        final CampaignCombination campaignCollection = new CampaignCombination();
        campaignCollection.advertiserToNoOfCampaignMap = new HashMap<String, Integer>();

        advertiserToNoOfCampaignMap.forEach(new BiConsumer<String, Integer>() {
            public void accept(String s, Integer integer) {
                campaignCollection.advertiserToNoOfCampaignMap.put(s, integer);
            }
        });

        campaignCollection.totalNoOfImpression = totalNoOfImpression.longValue();
        campaignCollection.totalRevenue = totalRevenue.doubleValue();
        return campaignCollection;
    }

    public String toString() {

        return String.format("%.5f",totalRevenue);

    }
}
