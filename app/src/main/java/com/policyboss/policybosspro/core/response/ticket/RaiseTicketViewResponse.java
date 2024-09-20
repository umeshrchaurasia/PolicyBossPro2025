package com.policyboss.policybosspro.core.response.ticket;

import com.policyboss.policybosspro.core.response.APIResponse;

import java.util.List;



/**
 * Created by Rajeev Ranjan on 09/05/2019.
 */
public class RaiseTicketViewResponse extends APIResponse {


    private List<RaiseTickeViewEntity> MasterData;

    public List<RaiseTickeViewEntity> getMasterData() {
        return MasterData;
    }

    public void setMasterData(List<RaiseTickeViewEntity> MasterData) {
        this.MasterData = MasterData;
    }


}
