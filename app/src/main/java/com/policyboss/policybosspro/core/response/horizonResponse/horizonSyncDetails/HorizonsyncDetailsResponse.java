package com.policyboss.policybosspro.core.response.horizonResponse.horizonSyncDetails;


import com.policyboss.policybosspro.core.model.sysncContact.POSPHorizonEnity;
import com.policyboss.policybosspro.core.model.sysncContact.SyncContactEntity;
import com.policyboss.policybosspro.core.response.APIResponse;

public class HorizonsyncDetailsResponse extends APIResponse {

    private String user_type;
    private String product;
//    private String status;
    private String channel;

    private SyncContactEntity SYNC_CONTACT;
    private POSPHorizonEnity POSP;


    public String getUserType() { return user_type; }
    public void setUserType(String value) { this.user_type = value; }

    public String getProduct() { return product; }
    public void setProduct(String value) { this.product = value; }

//    public String getStatus() { return status; }
//    public void setStatus(String value) { this.status = value; }

    public String getChannel() { return channel; }
    public void setChannel(String value) { this.channel = value; }





    public SyncContactEntity getResult() {
        return SYNC_CONTACT;
    }

    public void setResult(SyncContactEntity syncContact_result) {
        this.SYNC_CONTACT = syncContact_result;
    }

    public POSPHorizonEnity getPOSP() {
        return POSP;
    }

    public void setPOSP(POSPHorizonEnity POSP) {
        this.POSP = POSP;
    }

}


