package com.taragana.nclt.model;

import java.util.Date;

public class NCLATOrder {

    private String companyAppealNo;
    private Date dateOfOrder;
    private String party;
    private String section;
    private String courtName;
    private String orderPassedBy;
    private String pdfFileName;
    private String pdfFileSize;
    private String remark;

    public String getCompanyAppealNo() {
        return companyAppealNo;
    }

    public void setCompanyAppealNo(String companyAppealNo) {
        this.companyAppealNo = companyAppealNo;
    }

    public Date getDateOfOrder() {
        return dateOfOrder;
    }

    public void setDateOfOrder(Date dateOfOrder) {
        this.dateOfOrder = dateOfOrder;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getOrderPassedBy() {
        return orderPassedBy;
    }

    public void setOrderPassedBy(String orderPassedBy) {
        this.orderPassedBy = orderPassedBy;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "NCLATOrder{" +
                "companyAppealNo='" + companyAppealNo + '\'' +
                ", dateOfOrder=" + dateOfOrder +
                ", party='" + party + '\'' +
                ", section='" + section + '\'' +
                ", courtName='" + courtName + '\'' +
                ", orderPassedBy='" + orderPassedBy + '\'' +
                ", pdfFileName='" + pdfFileName + '\'' +
                ", pdfFileSize='" + pdfFileSize + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
