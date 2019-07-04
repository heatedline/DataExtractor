package com.taragana.nclt.model;

import java.util.Date;

public class NCLTJudgement {

    private String caseNo;
    private String petitionerName;
    private Date judgementDate;
    private String pdfFileName;
    private String pdfFileSize;
    private String pdfFileLanguage;

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public String getPetitionerName() {
        return petitionerName;
    }

    public void setPetitionerName(String petitionerName) {
        this.petitionerName = petitionerName;
    }

    public Date getJudgementDate() {
        return judgementDate;
    }

    public void setJudgementDate(Date judgementDate) {
        this.judgementDate = judgementDate;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

    public String getPdfFileSize() {
        return pdfFileSize;
    }

    public void setPdfFileSize(String pdfFileSize) {
        this.pdfFileSize = pdfFileSize;
    }

    public String getPdfFileLanguage() {
        return pdfFileLanguage;
    }

    public void setPdfFileLanguage(String pdfFileLanguage) {
        this.pdfFileLanguage = pdfFileLanguage;
    }

    @Override
    public String toString() {
        return "NCLTJudgement{" +
                "caseNo='" + caseNo + '\'' +
                ", petitionerName='" + petitionerName + '\'' +
                ", judgementDate=" + judgementDate +
                ", pdfFileName='" + pdfFileName + '\'' +
                ", pdfFileSize='" + pdfFileSize + '\'' +
                ", pdfFileLanguage='" + pdfFileLanguage + '\'' +
                '}';
    }
}
