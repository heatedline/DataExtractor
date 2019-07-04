package com.taragana.nclt.model;

public class NCLTOrder {

    private String caseNo;
    private String status;
    private String petitionerVsRespondent;
    private String listingDate;

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPetitionerVsRespondent() {
        return petitionerVsRespondent;
    }

    public void setPetitionerVsRespondent(String petitionerVsRespondent) {
        this.petitionerVsRespondent = petitionerVsRespondent;
    }

    public String getListingDate() {
        return listingDate;
    }

    public void setListingDate(String listingDate) {
        this.listingDate = listingDate;
    }

    @Override
    public String toString() {
        return "NCLTOrder{" +
                "caseNo='" + caseNo + '\'' +
                ", status='" + status + '\'' +
                ", petitionerVsRespondent='" + petitionerVsRespondent + '\'' +
                ", listingDate='" + listingDate + '\'' +
                '}';
    }
}
