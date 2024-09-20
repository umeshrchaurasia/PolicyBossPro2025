package com.policyboss.policybosspro.core.oldWayApi.controller.dynamicController;

import android.content.Context;

import com.policyboss.policybosspro.core.oldWayApi.IResponseSubcriber;

/**
 * Created by Nilesh Birhade on 06-08-2018.
 */

public class DynamicController implements IDynamic {

    DynamicUrlBuilder.GenericUrlNetworkService genericUrlNetworkService;
    Context mContext;

    public DynamicController(Context context) {
        genericUrlNetworkService = new DynamicUrlBuilder().getService();
        mContext = context;
    }


    @Override
    public void getSync_trascat_detail(String transaction_Id, IResponseSubcriber iResponseSubcriber) {

    }

    @Override
    public void getSync_trascat_Cancle(String transaction_Id, IResponseSubcriber iResponseSubcriber) {

    }

    @Override
    public void getSync_razor_payment(String transaction_Id, String PayId, IResponseSubcriber iResponseSubcriber) {

    }

    @Override
    public void getsyncDetailshorizon_java(String ss_id, IResponseSubcriber iResponseSubcriber) {

    }

    @Override
    public void getsalesmaterial_contentclick(String app_version, String product_id, String product_name, String device_code, String fbaid, String ssid, String type_of_content, String content_url, String language, String content_source, IResponseSubcriber iResponseSubcriber) {

    }
}
