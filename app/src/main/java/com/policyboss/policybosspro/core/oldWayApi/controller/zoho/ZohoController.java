package com.policyboss.policybosspro.core.oldWayApi.controller.zoho;

import android.content.Context;

import com.policyboss.policybosspro.core.model.ticketRaise.CreateTicketrequest;
import com.policyboss.policybosspro.core.oldWayApi.IResponseSubcriber;
import com.policyboss.policybosspro.core.oldWayApi.oldRequestBuilder.ZohoRequestBuilder;
import com.policyboss.policybosspro.core.response.ticket.RaiseTicketCommentResponse;
import com.policyboss.policybosspro.core.response.ticket.TicketCategoryResponse;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rajeev Ranjan on 01/03/2018.
 */

public class ZohoController implements IZoho {

    ZohoRequestBuilder.ZohoNetworkService zohoNetworkService;

    Context mContext;

    public ZohoController(Context context) {
        mContext = context;
        zohoNetworkService = new ZohoRequestBuilder().getService();

    }


    @Override
    public void getTicketCategories(IResponseSubcriber iResponseSubcriber) {

        zohoNetworkService.getTicketCategories().enqueue(new Callback<TicketCategoryResponse>() {
            @Override
            public void onResponse(Call<TicketCategoryResponse> call, Response<TicketCategoryResponse> response) {
                if (response.body() != null) {

                    //callback of data
                    if (response.body().getStatusNo() == 0) {
                        new AsyncZohoMaster(mContext, response.body().getMasterData()).execute();
                        iResponseSubcriber.OnSuccess(response.body(), response.body().getMessage());
                    } else {
                        iResponseSubcriber.OnFailure(new RuntimeException("" + response.body().getMessage()));
                    }


                } else {
                    //failure
                    iResponseSubcriber.OnFailure(new RuntimeException("Enable to reach server, Try again later"));
                }
            }

            @Override
            public void onFailure(Call<TicketCategoryResponse> call, Throwable t) {
                if (t instanceof ConnectException) {
                    iResponseSubcriber.OnFailure(t);
                } else if (t instanceof SocketTimeoutException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Check your internet connection"));
                } else if (t instanceof UnknownHostException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Check your internet connection"));
                } else if (t instanceof NumberFormatException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Unexpected server response"));
                } else {
                    iResponseSubcriber.OnFailure(new RuntimeException(t.getMessage()));
                }
            }
        });
    }

    @Override
    public void createTicket(CreateTicketrequest createTicketrequest, IResponseSubcriber iResponseSubcriber) {

    }

    @Override
    public void getListOfTickets(String fbaid, IResponseSubcriber iResponseSubcriber) {

    }

    @Override
    public void uploadDocuments(MultipartBody.Part document, HashMap<String, String> body, IResponseSubcriber iResponseSubcriber) {

    }

    @Override
    public void viewCommentOfTickets(String ticket_req_id, IResponseSubcriber iResponseSubcriber) {

    }

    @Override
    public void saveCommentOfTickets(String ticket_req_id, String comment, String docpath, String StatusID, IResponseSubcriber iResponseSubcriber) {

    }

    @Override
    public void uploadRaiseTicketDocWeb(MultipartBody.Part document, IResponseSubcriber iResponseSubcriber) {

    }

    @Override
    public void uploadCommonDocuments(MultipartBody.Part document, HashMap<String, String> body, IResponseSubcriber iResponseSubcriber) {

    }
}
