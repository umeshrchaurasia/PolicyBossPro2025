package com.policyboss.policybosspro.core.oldWayApi.controller.zoho;

import com.policyboss.policybosspro.core.model.ticketRaise.CreateTicketrequest;
import com.policyboss.policybosspro.core.oldWayApi.IResponseSubcriber;

import java.util.HashMap;


import okhttp3.MultipartBody;

/**
 * Created by Rajeev Ranjan on 01/03/2018.
 */

public interface IZoho {
    void getTicketCategories(IResponseSubcriber iResponseSubcriber);

    void createTicket(CreateTicketrequest createTicketrequest, IResponseSubcriber iResponseSubcriber);

    void getListOfTickets(String fbaid, IResponseSubcriber iResponseSubcriber);

    void uploadDocuments(MultipartBody.Part document, HashMap<String, String> body, final IResponseSubcriber iResponseSubcriber);

    void  viewCommentOfTickets(String ticket_req_id, IResponseSubcriber iResponseSubcriber);

    void  saveCommentOfTickets(String ticket_req_id,String comment , String docpath ,String StatusID , IResponseSubcriber iResponseSubcriber);

    void uploadRaiseTicketDocWeb(MultipartBody.Part document, final IResponseSubcriber iResponseSubcriber);

    void uploadCommonDocuments(MultipartBody.Part document, HashMap<String, String> body, final IResponseSubcriber iResponseSubcriber);

}
