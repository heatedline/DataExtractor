package com.taragana.nclt.model;

import java.util.Date;

public class NCLATTentativeList {

    private String caseNo;
    private String partyName;
    private String section;
    private Date date;
    private String remark;

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "NCLATTentativeList{" +
                "caseNo='" + caseNo + '\'' +
                ", partyName='" + partyName + '\'' +
                ", section='" + section + '\'' +
                ", date=" + date +
                ", remark='" + remark + '\'' +
                '}';
    }
}
