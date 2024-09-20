package com.policyboss.policybosspro.core.response.ticket;

public class CreateTicketEntity {
    /**
     * TicketId : 3
     * SavedStatus : 0
     */

    private int TicketId;
    private int SavedStatus;

    public int getTicketId() {
        return TicketId;
    }

    public void setTicketId(int TicketId) {
        this.TicketId = TicketId;
    }

    public int getSavedStatus() {
        return SavedStatus;
    }

    public void setSavedStatus(int SavedStatus) {
        this.SavedStatus = SavedStatus;
    }
}