package com.policyboss.policybosspro.core.model.sysncContact;

public class SyncContactEntity {

    private long contact;
    private long erp_id;
    private long fba_id;
    private long ss_id;
    private String ACTION_NEEDED;
    private String FIRST_SYNC_CAMPAIGN_CREATIVE;
    private String RE_SYNC_CAMPAIGN_CREATIVE;
    private long Days_From_Last_Sync;


    public long getContact() {
        return contact;
    }

    public void setContact(long value) {
        this.contact = value;
    }

    public long getERPID() {
        return erp_id;
    }

    public void setERPID(long value) {
        this.erp_id = value;
    }

    public long getFbaID() {
        return fba_id;
    }

    public void setFbaID(long value) {
        this.fba_id = value;
    }

    public long getSsID() {
        return ss_id;
    }

    public void setSsID(long value) {
        this.ss_id = value;
    }

    public String getActionNeeded() {
        return ACTION_NEEDED;
    }

    public void setActionNeeded(String value) {
        this.ACTION_NEEDED = value;
    }

    public String getFirstSyncCampaignCreative() {
        return FIRST_SYNC_CAMPAIGN_CREATIVE;
    }

    public void setFirstSyncCampaignCreative(String value) {
        this.FIRST_SYNC_CAMPAIGN_CREATIVE = value;
    }

    public String getReSyncCampaignCreative() {
        return RE_SYNC_CAMPAIGN_CREATIVE;
    }

    public void setReSyncCampaignCreative(String value) {
        this.RE_SYNC_CAMPAIGN_CREATIVE = value;
    }

    public long getDaysFromLastSync() {
        return Days_From_Last_Sync;
    }

    public void setDaysFromLastSync(long value) {
        this.Days_From_Last_Sync = value;
    }
}
