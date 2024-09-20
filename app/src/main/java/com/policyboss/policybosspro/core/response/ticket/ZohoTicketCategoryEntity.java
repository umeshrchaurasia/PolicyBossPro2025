package com.policyboss.policybosspro.core.response.ticket;


import java.util.List;

public class ZohoTicketCategoryEntity {

    private List<ZohoCategoryEntity> category;
    private List<ZohoSubcategoryEntity> subcategory;
    private List<ZohoClassificationEntity> classification;

    public List<ZohoCategoryEntity> getCategory() {
        return category;
    }

    public void setCategory(List<ZohoCategoryEntity> category) {
        this.category = category;
    }

    public List<ZohoSubcategoryEntity> getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(List<ZohoSubcategoryEntity> subcategory) {
        this.subcategory = subcategory;
    }

    public List<ZohoClassificationEntity> getClassification() {
        return classification;
    }

    public void setClassification(List<ZohoClassificationEntity> classification) {
        this.classification = classification;
    }
}
