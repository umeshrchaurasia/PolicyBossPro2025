package com.policyboss.policybosspro.core.response.ticket;

import com.policyboss.policybosspro.core.response.APIResponse;

import java.util.List;



/**
 * Created by Rajeev Ranjan on 01/03/2018.
 */

public class TicketListResponse extends APIResponse {
    private List<TicketEntity> MasterData;

    public List<TicketEntity> getMasterData() {
        return MasterData;
    }

    public void setMasterData(List<TicketEntity> MasterData) {
        this.MasterData = MasterData;
    }
}
