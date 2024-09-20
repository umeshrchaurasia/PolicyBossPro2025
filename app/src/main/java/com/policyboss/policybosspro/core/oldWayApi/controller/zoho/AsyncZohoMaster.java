package com.policyboss.policybosspro.core.oldWayApi.controller.zoho;

import android.content.Context;
import android.os.AsyncTask;

import com.policyboss.policybosspro.core.response.ticket.ZohoTicketCategoryEntity;
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager;

import javax.inject.Inject;



/**
 * Created by Nilesh Birhade on 29-11-2017.
 */

public class AsyncZohoMaster extends AsyncTask<Void, Void, Void> {


    @Inject
    PolicyBossPrefsManager prefManager;
    Context mContext;
    ZohoTicketCategoryEntity zohoTicketCategoryEntity;

    public AsyncZohoMaster(Context context, ZohoTicketCategoryEntity list) {
        this.zohoTicketCategoryEntity = list;
        mContext = context;

    }


    @Override
    protected Void doInBackground(Void... voids) {


        try {



        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        prefManager.setIsZohoMaster(false);
    }
}
