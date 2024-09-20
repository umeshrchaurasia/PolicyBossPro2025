package com.policyboss.policybosspro.core.response.ticket;

import com.policyboss.policybosspro.core.response.APIResponse;

import java.util.List;



/**
 * Created by Rajeev Ranjan on 01/03/2018.
 */

public class CreateTicketResponse extends APIResponse {
    private List<CreateTicketEntity> MasterData;

    public List<CreateTicketEntity> getMasterData() {
        return MasterData;
    }

    public void setMasterData(List<CreateTicketEntity> MasterData) {
        this.MasterData = MasterData;
    }


}
