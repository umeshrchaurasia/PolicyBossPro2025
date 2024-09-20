package com.policyboss.policybosspro.core.response.razorPay;

import com.policyboss.policybosspro.core.model.razporPay.RazorPayEntity;
import com.policyboss.policybosspro.core.response.APIResponse;

import java.util.List;



public class RazorPayResponse extends APIResponse {


    private List<RazorPayEntity> MasterData;

    public List<RazorPayEntity> getMasterData() {
        return MasterData;
    }

    public void setMasterData(List<RazorPayEntity> MasterData) {
        this.MasterData = MasterData;
    }


}
