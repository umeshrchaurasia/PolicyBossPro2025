package com.policyboss.policybosspro.core.response.othere;


import com.policyboss.policybosspro.core.response.APIResponse;

public class syncrazorsucessReponse extends APIResponse {

    /**
     * Transaction_Id : 56
     * Msg : Data Updated Successfully
     */

    private int Transaction_Id;
    private String Msg;

    public int getTransaction_Id() {
        return Transaction_Id;
    }

    public void setTransaction_Id(int Transaction_Id) {
        this.Transaction_Id = Transaction_Id;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }
}
