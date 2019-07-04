package com.taragana.nclt.model;

import java.util.Date;

public class NCLTRegisteredIP {

    private String regulation;
    private String registrationNumber;
    private String ipName;
    private String ipAddress;
    private String ipEmail;
    private String enrolledWithIPAName;
    private Date registrationDate;
    private String remarks;

    public String getRegulation() {
        return regulation;
    }

    public void setRegulation(String regulation) {
        this.regulation = regulation;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getIpName() {
        return ipName;
    }

    public void setIpName(String ipName) {
        this.ipName = ipName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpEmail() {
        return ipEmail;
    }

    public void setIpEmail(String ipEmail) {
        this.ipEmail = ipEmail;
    }

    public String getEnrolledWithIPAName() {
        return enrolledWithIPAName;
    }

    public void setEnrolledWithIPAName(String enrolledWithIPAName) {
        this.enrolledWithIPAName = enrolledWithIPAName;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "NCLTRegisteredIP{" +
                "regulation='" + regulation + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", ipName='" + ipName + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", ipEmail='" + ipEmail + '\'' +
                ", enrolledWithIPAName='" + enrolledWithIPAName + '\'' +
                ", registrationDate=" + registrationDate +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
