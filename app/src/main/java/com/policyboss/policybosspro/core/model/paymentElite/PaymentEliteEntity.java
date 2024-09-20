package com.policyboss.policybosspro.core.model.paymentElite;

public  class PaymentEliteEntity {
        /**
         * CustID : 14
         * Fullname : Platinum50
         * Email : platinumplan50@gmail.com
         * Mobile : 9066778978
         * image : https://www.rupeeboss.com/image/logo.png
         * Status : 0
         * planname : ELITE PLATINUM
         * subplan : PLATINUM50
         * sumassured : 50000
         * displayamount : 2044
         * amount : 204400
         */

        private int CustID;
        private String Fullname;
        private String Email;
        private String Mobile;
        private String image;


        private String Status;

        private String planname;
        private String subplan;
        private String sumassured;
        private String displayamount;
        private int amount;

        public int getCustID() {
            return CustID;
        }

        public void setCustID(int CustID) {
            this.CustID = CustID;
        }

        public String getFullname() {
            return Fullname;
        }

        public void setFullname(String Fullname) {
            this.Fullname = Fullname;
        }

        public String getEmail() {
            return Email;
        }

        public void setEmail(String Email) {
            this.Email = Email;
        }

        public String getMobile() {
            return Mobile;
        }

        public void setMobile(String Mobile) {
            this.Mobile = Mobile;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }


        public String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            Status = status;
        }

        public String getPlanname() {
            return planname;
        }

        public void setPlanname(String planname) {
            this.planname = planname;
        }

        public String getSubplan() {
            return subplan;
        }

        public void setSubplan(String subplan) {
            this.subplan = subplan;
        }

        public String getSumassured() {
            return sumassured;
        }

        public void setSumassured(String sumassured) {
            this.sumassured = sumassured;
        }

        public String getDisplayamount() {
            return displayamount;
        }

        public void setDisplayamount(String displayamount) {
            this.displayamount = displayamount;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }