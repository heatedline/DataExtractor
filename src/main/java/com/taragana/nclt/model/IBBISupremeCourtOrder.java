package com.taragana.nclt.model;

import java.util.Date;

public class IBBISupremeCourtOrder {

    private Date dateOfOrder;
    private String subject;
    private String pdfFileName;
    private String ordersRemarks;

    public Date getDateOfOrder() {
        return dateOfOrder;
    }

    public void setDateOfOrder(Date dateOfOrder) {
        this.dateOfOrder = dateOfOrder;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

    public String getOrdersRemarks() {
        return ordersRemarks;
    }

    public void setOrdersRemarks(String ordersRemarks) {
        this.ordersRemarks = ordersRemarks;
    }

    @Override
    public String toString() {
        return "IBBIHighCourtOrder{" +
                "dateOfOrder=" + dateOfOrder +
                ", subject='" + subject + '\'' +
                ", pdfFileName='" + pdfFileName + '\'' +
                ", ordersRemarks='" + ordersRemarks + '\'' +
                '}';
    }

}
