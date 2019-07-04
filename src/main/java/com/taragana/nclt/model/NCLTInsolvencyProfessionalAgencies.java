package com.taragana.nclt.model;

public class NCLTInsolvencyProfessionalAgencies {

    private String ipaRegistrationNumber;
    private String ipaName;
    private String ipaAddress;
    private String ipaWebsite;
    private String ipaChiefExecutiveName;
    private String ipaContactDetails;

    public String getIpaRegistrationNumber() {
        return ipaRegistrationNumber;
    }

    public void setIpaRegistrationNumber(String ipaRegistrationNumber) {
        this.ipaRegistrationNumber = ipaRegistrationNumber;
    }

    public String getIpaName() {
        return ipaName;
    }

    public void setIpaName(String ipaName) {
        this.ipaName = ipaName;
    }

    public String getIpaAddress() {
        return ipaAddress;
    }

    public void setIpaAddress(String ipaAddress) {
        this.ipaAddress = ipaAddress;
    }

    public String getIpaWebsite() {
        return ipaWebsite;
    }

    public void setIpaWebsite(String ipaWebsite) {
        this.ipaWebsite = ipaWebsite;
    }

    public String getIpaChiefExecutiveName() {
        return ipaChiefExecutiveName;
    }

    public void setIpaChiefExecutiveName(String ipaChiefExecutiveName) {
        this.ipaChiefExecutiveName = ipaChiefExecutiveName;
    }

    public String getIpaContactDetails() {
        return ipaContactDetails;
    }

    public void setIpaContactDetails(String ipaContactDetails) {
        this.ipaContactDetails = ipaContactDetails;
    }

    @Override
    public String toString() {
        return "NCLTInsolvencyProfessionalAgencies{" +
                "ipaRegistrationNumber='" + ipaRegistrationNumber + '\'' +
                ", ipaName='" + ipaName + '\'' +
                ", ipaAddress='" + ipaAddress + '\'' +
                ", ipaWebsite='" + ipaWebsite + '\'' +
                ", ipaChiefExecutiveName='" + ipaChiefExecutiveName + '\'' +
                ", ipaContactDetails='" + ipaContactDetails + '\'' +
                '}';
    }
}
