package com.policyboss.policybosspro.core.response.webDocResponse;


import com.policyboss.policybosspro.core.response.APIResponse;

/**
 * Created by Rajeev Ranjan on 05/03/2020.
 */
public class CommonWebDocResponse extends APIResponse {
    /**
     {
     "Msg": "doc saved",
     "Data": {
     "crn": "1098058",
     "document_id": "FAGPS8388E",
     "document_type": "PAN",
     "insurer_id": "10",
     "file_path": "/tmp/kyc_documents/1098058_FAGPS8388E_10.JPG"
     },
     "Status": "Success"
     }
     */

    private String Msg;



    public String getMsg() { return Msg; }
    public void setMsg(String value) { this.Msg = value; }



    private CommondocEntity Data;

    public CommondocEntity getMasterData() {
        return Data;
    }

    public void setMasterData(CommondocEntity Data) {
        this.Data = Data;
    }

}
