package com.taragana.nclt.model;

import java.util.Date;

public class IBBIOrder {

    private Date dateOfOrder;
    private String subject;
    private String pdfFileName;
    private String orderRemarks;

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

    public String getOrderRemarks() {
        return orderRemarks;
    }

    public void setOrderRemarks(String orderRemarks) {
        this.orderRemarks = orderRemarks;
    }

    @Override
    public String toString() {
        return "IBBIOrder{" +
                "dateOfOrder=" + dateOfOrder +
                ", subject='" + subject + '\'' +
                ", pdfFileName='" + pdfFileName + '\'' +
                ", orderRemarks='" + orderRemarks + '\'' +
                '}';
    }

}
