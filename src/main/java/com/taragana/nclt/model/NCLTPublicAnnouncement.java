package com.taragana.nclt.model;

import java.util.Date;

public class NCLTPublicAnnouncement {

    private String announcementDescription;
    private Date dateOfAnnouncement;
    private Date lastDateOfSubmission;
    private String corporateDebtorName;
    private String applicantName;
    private String insolvencyProfessionalName;
    private String insolvencyProfessionalAddress;
    private String pdfFileName;
    private String remark;

    public String getAnnouncementDescription() {
        return announcementDescription;
    }

    public void setAnnouncementDescription(String announcementDescription) {
        this.announcementDescription = announcementDescription;
    }

    public Date getDateOfAnnouncement() {
        return dateOfAnnouncement;
    }

    public void setDateOfAnnouncement(Date dateOfAnnouncement) {
        this.dateOfAnnouncement = dateOfAnnouncement;
    }

    public Date getLastDateOfSubmission() {
        return lastDateOfSubmission;
    }

    public void setLastDateOfSubmission(Date lastDateOfSubmission) {
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

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "NCLTPublicAnnouncement{" +
                "announcementDescription='" + announcementDescription + '\'' +
                ", dateOfAnnouncement=" + dateOfAnnouncement +
                ", lastDateOfSubmission=" + lastDateOfSubmission +
                ", corporateDebtorName='" + corporateDebtorName + '\'' +
                ", applicantName='" + applicantName + '\'' +
                ", insolvencyProfessionalName='" + insolvencyProfessionalName + '\'' +
                ", insolvencyProfessionalAddress='" + insolvencyProfessionalAddress + '\'' +
                ", pdfFileName='" + pdfFileName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
