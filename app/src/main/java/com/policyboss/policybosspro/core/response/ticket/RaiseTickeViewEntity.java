package com.policyboss.policybosspro.core.response.ticket;

public  class RaiseTickeViewEntity {
        /**
         * ticket_req_id : 104
         * comment : clossed
         * docpath : 
         */

        private int ticket_req_id;
        private String comment;
        private String docpath;

        public int getTicket_req_id() {
            return ticket_req_id;
        }

        public void setTicket_req_id(int ticket_req_id) {
            this.ticket_req_id = ticket_req_id;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getDocpath() {
            return docpath;
        }

        public void setDocpath(String docpath) {
            this.docpath = docpath;
        }
    }