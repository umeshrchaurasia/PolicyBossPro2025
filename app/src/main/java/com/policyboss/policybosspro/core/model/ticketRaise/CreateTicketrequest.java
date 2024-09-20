package com.policyboss.policybosspro.core.model.ticketRaise;

/**
 * Created by Rajeev Ranjan on 01/03/2018.
 */

public class CreateTicketrequest {
    /**
     * CategoryId : 1
     * SubCategoryId : 2
     * classification : 10
     * Message : ;lkuydtfghjkl
     * FBAID : 1
     */

    private int CategoryId;
    private int SubCategoryId;
    private int classification;
    private String Message;
    private int FBAID;

    private String productname;
    private String crnloan;

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int CategoryId) {
        this.CategoryId = CategoryId;
    }

    public int getSubCategoryId() {
        return SubCategoryId;
    }

    public void setSubCategoryId(int SubCategoryId) {
        this.SubCategoryId = SubCategoryId;
    }

    public int getClassification() {
        return classification;
    }

    public void setClassification(int classification) {
        this.classification = classification;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public int getFBAID() {
        return FBAID;
    }

    public void setFBAID(int FBAID) {
        this.FBAID = FBAID;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getCrnloan() {
        return crnloan;
    }

    public void setCrnloan(String crnloan) {
        this.crnloan = crnloan;
    }
}
