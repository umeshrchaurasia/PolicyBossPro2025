package com.policyboss.policybosspro.core.oldWayApi.controller.registerController;

import com.policyboss.policybosspro.core.oldWayApi.IResponseSubcriber;

public interface IRegister {


    void getDataForPayment_elite(String custid , IResponseSubcriber iResponseSubcriber);

    void addToRazorPay_elite(String FBAID, String CustId,String PayId, IResponseSubcriber iResponseSubcriber);

}
