package com.policyboss.policybosspro.core.response.ticket;

public class TicketEntity {
    /**
     * TicketRequestId : 3
     * Message : test
     * Status : null
     * FBAID : 1
     * CategoryId : 3
     * SubCategoryId : 21
     * ClassificationId : 1
     * DocPath : null
     * StatusChangedBy : null
     * CateName : Product
     * QuerType : Personal Loan
     * Description : Proposal Link
     */

    private int TicketRequestId;
    private String Message;
    private String Status;
    private int FBAID;
    private int CategoryId;


    private int StatusId;
    private int SubCategoryId;
    private int ClassificationId;
    private String DocPath;
    private String StatusChangedBy;
    private String CateName;
    private String QuerType;
    private String Description;



    private String productname;
    private String crnloan;
    public int getTicketRequestId() {
        return TicketRequestId;
    }

    public void setTicketRequestId(int TicketRequestId) {
        this.TicketRequestId = TicketRequestId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public int getFBAID() {
        return FBAID;
    }

    public void setFBAID(int FBAID) {
        this.FBAID = FBAID;
    }

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

    public int getClassificationId() {
        return ClassificationId;
    }

    public void setClassificationId(int ClassificationId) {
        this.ClassificationId = ClassificationId;
    }

    public String getDocPath() {
        return DocPath;
    }

    public void setDocPath(String DocPath) {
        this.DocPath = DocPath;
    }

    public String getStatusChangedBy() {
        return StatusChangedBy;
    }

    public void setStatusChangedBy(String StatusChangedBy) {
        this.StatusChangedBy = StatusChangedBy;
    }

    public String getCateName() {
        return CateName;
    }

    public void setCateName(String CateName) {
        this.CateName = CateName;
    }

    public String getQuerType() {
        return QuerType;
    }

    public void setQuerType(String QuerType) {
        this.QuerType = QuerType;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
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

    public int getStatusId() {
        return StatusId;
    }

    public void setStatusId(int statusId) {
        StatusId = statusId;
    }

}