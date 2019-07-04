package com.taragana.nclt.model;

public class NCLTInsolvencyProfessionalEntites {

    private String recognitionNumber;
    private String ipeName;
    private String ipeConstitution;
    private String ipeAddress;
    private String ipePartnersName;
    private String ipeContactDetails;

    public String getRecognitionNumber() {
        return recognitionNumber;
    }

    public void setRecognitionNumber(String recognitionNumber) {
        this.recognitionNumber = recognitionNumber;
    }

    public String getIpeName() {
        return ipeName;
    }

    public void setIpeName(String ipeName) {
        this.ipeName = ipeName;
    }

    public String getIpeConstitution() {
        return ipeConstitution;
    }

    public void setIpeConstitution(String ipeConstitution) {
        this.ipeConstitution = ipeConstitution;
    }

    public String getIpeAddress() {
        return ipeAddress;
    }

    public void setIpeAddress(String ipeAddress) {
        this.ipeAddress = ipeAddress;
    }

    public String getIpePartnersName() {
        return ipePartnersName;
    }

    public void setIpePartnersName(String ipePartnersName) {
        this.ipePartnersName = ipePartnersName;
    }

    public String getIpeContactDetails() {
        return ipeContactDetails;
    }

    public void setIpeContactDetails(String ipeContactDetails) {
        this.ipeContactDetails = ipeContactDetails;
    }

    @Override
    public String toString() {
        return "NCLTInsolvencyProfessionalEntites{" +
                "recognitionNumber='" + recognitionNumber + '\'' +
                ", ipeName='" + ipeName + '\'' +
                ", ipeConstitution='" + ipeConstitution + '\'' +
                ", ipeAddress='" + ipeAddress + '\'' +
                ", ipePartnersName='" + ipePartnersName + '\'' +
                ", ipeContactDetails='" + ipeContactDetails + '\'' +
                '}';
    }
}
