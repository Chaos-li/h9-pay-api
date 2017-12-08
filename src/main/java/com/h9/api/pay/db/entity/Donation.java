package com.h9.api.pay.db.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;

/**
 * @Description: 捐赠订单
 * @Auther Demon
 * @Date 2017/11/15 14:47 星期三
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "donation", uniqueConstraints = {@UniqueConstraint(columnNames="order_no")})
public class Donation extends BaseEntity {

    @Column(name = "order_no", columnDefinition = "VARCHAR(255) COMMENT '订单号'", nullable = false)
    private String orderNo;

    @Column(name = "donor", columnDefinition = "VARCHAR(255) COMMENT '捐赠人'")
    private String donor;

    @Column(name = "enterprise", columnDefinition = "VARCHAR(255) COMMENT '捐赠企业'")
    private String enterprise;

    @Column(name = "personal_amount", columnDefinition = "DECIMAL(10, 2) DEFAULT 0.00 COMMENT '个人捐赠金额'")
    private BigDecimal personalAmount;

    @Column(name = "enterprise_amount", columnDefinition = "DECIMAL(10, 2) DEFAULT 0.00 COMMENT '企业捐赠金额'")
    private BigDecimal enterpriseAmount;

    @Column(name = "personal_mobile", columnDefinition = "VARCHAR(255) COMMENT '个人手机号'")
    private String personalMobile;

    @Column(name = "enterprise_mobile", columnDefinition = "VARCHAR(255) COMMENT '企业手机号'")
    private String enterpriseMobile;

    @Column(name = "type", columnDefinition = "SMALLINT DEFAULT 0 COMMENT '捐赠类型：0个人，1企业，2联合'")
    private Integer type;

    @Column(name = "total_amount", columnDefinition = "DECIMAL(10, 2) DEFAULT 0.00 COMMENT '总金额'")
    private BigDecimal totalAmount;

    @Column(name = "notify_log", columnDefinition = "VARCHAR(512) COMMENT '支付回调原始记录'")
    private String notifyLog;

    @Column(name = "status", columnDefinition = "SMALLINT DEFAULT 0 COMMENT '支付状态'", nullable = false)
    protected Integer status;

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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNotifyLog() {
        return notifyLog;
    }

    public void setNotifyLog(String notifyLog) {
        this.notifyLog = notifyLog;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
