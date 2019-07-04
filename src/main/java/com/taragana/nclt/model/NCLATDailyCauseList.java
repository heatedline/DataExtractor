package com.taragana.nclt.model;

import java.util.Date;

public class NCLATDailyCauseList {

    private String courtName;
    private String description;
    private Date date;
    private String pdfFileName;
    private String pdfSize;

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

    public String getPdfSize() {
        return pdfSize;
    }

    public void setPdfSize(String pdfSize) {
        this.pdfSize = pdfSize;
    }

    @Override
    public String toString() {
        return "NCLATDailyCauseList{" +
                "courtName='" + courtName + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", pdfFileName='" + pdfFileName + '\'' +
                ", pdfSize='" + pdfSize + '\'' +
                '}';
    }
}
