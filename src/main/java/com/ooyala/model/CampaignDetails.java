package com.ooyala.model;

import java.io.Serializable;

/**
 * Created by schinnas on 5/13/17.
 */

public class CampaignDetails implements Serializable {

    Long impressionsPerCampaign;
    Double revenuePerCampaign;
    String id;
    String type = "O";
    Long maxBound = 0l;
    String name;
    Integer totalNoOfCampaigns = 0; // output
    Long totalNoOfImpressions = 0l;  // output
    Double totalRevenue = 0d; // output

    static int i = 0;

    public Integer getTotalNoOfCampaigns() {
        return totalNoOfCampaigns;
    }

    public void setTotalNoOfCampaigns(Integer totalNoOfCampaigns) {
        this.totalNoOfCampaigns = totalNoOfCampaigns;
        this.totalNoOfImpressions = this.totalNoOfCampaigns * impressionsPerCampaign;
        this.totalRevenue = this.totalNoOfCampaigns * revenuePerCampaign;
    }

    public Long getTotalNoOfImpressions() {
        return totalNoOfImpressions;
    }

    public void setTotalNoOfImpressions(Long totalNoOfImpressions) {
        this.totalNoOfImpressions = totalNoOfImpressions;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public CampaignDetails(String name, Long impressionsPerCampaign, Double revenuePerCampaign) {
        this.impressionsPerCampaign = impressionsPerCampaign;
        this.revenuePerCampaign = revenuePerCampaign;
        this.name = name;
        this.id = String.valueOf(++i);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getImpressionsPerCampaign() {
        return impressionsPerCampaign;
    }

    public void setImpressionsPerCampaign(Long impressionsPerCampaign) {
        this.impressionsPerCampaign = impressionsPerCampaign;
    }

    public Double getRevenuePerCampaign() {
        return revenuePerCampaign;
    }

    public void setRevenuePerCampaign(Double revenuePerCampaign) {
        this.revenuePerCampaign = revenuePerCampaign;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getMaxBound() {
        return maxBound;
    }

    public void setMaxBound(Long maxBound) {
        this.maxBound = maxBound;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name + "," + totalNoOfCampaigns + "," + totalNoOfImpressions + "," + String.format("%.5f",totalRevenue);
    }
}
