package com.h9.api.pay.rest.model;

import java.math.BigDecimal;

/**
 * @Description: 捐赠订单model
 * @Auther Demon
 * @Date 2017/11/16 9:48 星期四
 */
public class DonationModel extends BaseModel {

    private String openid;

    private String donor;

    private String enterprise;

    private BigDecimal personalAmount;

    private BigDecimal enterpriseAmount;

    private String personalMobile;

    private String enterpriseMobile;

    private Integer type;

    private String businessAppId = "appidh9donateeqzkv";

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getDonor() {
        return donor;
    }

    public void setDonor(String donor) {
        this.donor = donor;
    }

    public String getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(String enterprise) {
        this.enterprise = enterprise;
    }

    public BigDecimal getPersonalAmount() {
        return personalAmount;
    }

    public void setPersonalAmount(BigDecimal personalAmount) {
        this.personalAmount = personalAmount;
    }

    public BigDecimal getEnterpriseAmount() {
        return enterpriseAmount;
    }

    public void setEnterpriseAmount(BigDecimal enterpriseAmount) {
        this.enterpriseAmount = enterpriseAmount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPersonalMobile() {
        return personalMobile;
    }

    public void setPersonalMobile(String personalMobile) {
        this.personalMobile = personalMobile;
    }

    public String getEnterpriseMobile() {
        return enterpriseMobile;
    }

    public void setEnterpriseMobile(String enterpriseMobile) {
        this.enterpriseMobile = enterpriseMobile;
    }

    public String getBusinessAppId() {
        return businessAppId;
    }

    public void setBusinessAppId(String businessAppId) {
        this.businessAppId = businessAppId;
    }
}
