package com.taragana.nclt.model;

public class IBBIAnnouncementData {

    private String id;
    private String dateOfAnnouncement;
    private String lastDateOfSubmission;
    private String corporateDebtorName;
    private String applicantName;
    private String insolvencyProfessionalName;
    private String insolvencyProfessionalAddress;
    private String remarks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateOfAnnouncement() {
        return dateOfAnnouncement;
    }

    public void setDateOfAnnouncement(String dateOfAnnouncement) {
        this.dateOfAnnouncement = dateOfAnnouncement;
    }

    public String getLastDateOfSubmission() {
        return lastDateOfSubmission;
    }

    public void setLastDateOfSubmission(String lastDateOfSubmission) {
        this.lastDateOfSubmission = lastDateOfSubmission;
    }

    public String getCorporateDebtorName() {
        return corporateDebtorName;
    }

    public void setCorporateDebtorName(String corporateDebtorName) {
        this.corporateDebtorName = corporateDebtorName;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getInsolvencyProfessionalName() {
        return insolvencyProfessionalName;
    }

    public void setInsolvencyProfessionalName(String insolvencyProfessionalName) {
        this.insolvencyProfessionalName = insolvencyProfessionalName;
    }

    public String getInsolvencyProfessionalAddress() {
        return insolvencyProfessionalAddress;
    }

    public void setInsolvencyProfessionalAddress(String insolvencyProfessionalAddress) {
        this.insolvencyProfessionalAddress = insolvencyProfessionalAddress;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "IBBIAnnouncementData{" +
                "id='" + id + '\'' +
                ", dateOfAnnouncement='" + dateOfAnnouncement + '\'' +
                ", lastDateOfSubmission='" + lastDateOfSubmission + '\'' +
                ", corporateDebtorName='" + corporateDebtorName + '\'' +
                ", applicantName='" + applicantName + '\'' +
                ", insolvencyProfessionalName='" + insolvencyProfessionalName + '\'' +
                ", insolvencyProfessionalAddress='" + insolvencyProfessionalAddress + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
