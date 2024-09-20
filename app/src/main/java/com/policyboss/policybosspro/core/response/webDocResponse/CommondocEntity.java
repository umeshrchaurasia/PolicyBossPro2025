package com.policyboss.policybosspro.core.response.webDocResponse;

public class CommondocEntity {

    private String crn;
    private String document_id;
    private String document_type;
    private String insurer_id;
    private String file_path;

    public String getCrn() { return crn; }
    public void setCrn(String value) { this.crn = value; }

    public String getDocumentID() { return document_id; }
    public void setDocumentID(String value) { this.document_id = value; }

    public String getDocumentType() { return document_type; }
    public void setDocumentType(String value) { this.document_type = value; }

    public String getInsurerID() { return insurer_id; }
    public void setInsurerID(String value) { this.insurer_id = value; }

    public String getFilePath() { return file_path; }
    public void setFilePath(String value) { this.file_path = value; }
}
