package com.taragana.nclt.model;

public class JudgementData {

    private String id;
    private String companyAppealNo;
    private String dateOfOrder;
    private String party;
    private String section;
    private String courtName;
    private String orderPassedBy;
    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyAppealNo() {
        return companyAppealNo;
    }

    public void setCompanyAppealNo(String companyAppealNo) {
        this.companyAppealNo = companyAppealNo;
    }

    public String getDateOfOrder() {
        return dateOfOrder;
    }

    public void setDateOfOrder(String dateOfOrder) {
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "JudgementData{" +
                "id='" + id + '\'' +
                ", companyAppealNo='" + companyAppealNo + '\'' +
                ", dateOfOrder='" + dateOfOrder + '\'' +
                ", party='" + party + '\'' +
                ", section='" + section + '\'' +
                ", courtName='" + courtName + '\'' +
                ", orderPassedBy='" + orderPassedBy + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }

}
