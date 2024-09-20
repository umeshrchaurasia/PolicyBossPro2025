package com.policyboss.policybosspro.core.response.syncContact.syncContactDetailsResponse;


import com.policyboss.policybosspro.core.response.APIResponse;

public class synctransactionDetailReponse extends APIResponse {


    /**
     * MasterData : {"_id":"5ed4d0bcca302d5e254f0c02","Transaction_Id":56,"Transaction_Status":"Success","Modified_On":"2020-06-01T09:56:12.307Z","Created_On":"2020-06-01T09:56:12.307Z","Amount":1200,"Plan":30,"Email":"chandrapra_singh@yahoo.co.uk","Mobile":"8800320008","Name":"RAJESH  JOSHI","Fba_ID":459,"Ss_Id":1655,"__v":0,"PayId":"123456"}
     */

    private synctransactionDetailEntity MasterData;

    public synctransactionDetailEntity getMasterData() {
        return MasterData;
    }

    public void setMasterData(synctransactionDetailEntity MasterData) {
        this.MasterData = MasterData;
    }


}
