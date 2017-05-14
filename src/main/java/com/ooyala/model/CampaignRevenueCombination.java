package com.ooyala.model;

import java.io.Serializable;

/**
 * Created by schinnas on 5/13/17.
 */
public class CampaignRevenueCombination implements Serializable {

    CampaignDetails campaignDetails;
    Double maxRevenue;
    Boolean isUpdatedRequired;

    public Boolean getIsUpdatedRequired() {
        return isUpdatedRequired;
    }

    public void setIsUpdatedRequired(Boolean isUpdatedRequired) {
        this.isUpdatedRequired = isUpdatedRequired;
    }

    public CampaignDetails getCampaignDetails() {
        return campaignDetails;
    }

    public void setCampaignDetails(CampaignDetails campaignDetails) {
        this.campaignDetails = campaignDetails;
    }

    public Double getMaxRevenue() {
        return maxRevenue;
    }

    public void setMaxRevenue(Double maxRevenue) {
        this.maxRevenue = maxRevenue;
    }
}
