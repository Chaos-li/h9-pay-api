package com.h9.api.pay.db.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;

/**
 * @Description: 订单信息
 * @Auther Demon
 * @Date 2017/12/7 17:14 星期四
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "order_info", uniqueConstraints = {@UniqueConstraint(columnNames="order_no")})
public class Order extends BaseEntity {

    @Column(name = "order_no", columnDefinition = "VARCHAR(255) COMMENT '支付订单号'", nullable = false)
    private String orderNo;

    @Column(name = "business_order_id", columnDefinition = "VARCHAR(255) COMMENT '业务订单号'")
    private String businessOrderId;

    @Column(name = "pay_status", columnDefinition = "SMALLINT DEFAULT 0 COMMENT '支付状态:0未支付，1已支付'", nullable = false)
    protected Integer payStatus;

    @Column(name = "total_amount", columnDefinition = "DECIMAL(12, 2) DEFAULT 0.00 COMMENT '总金额'")
    private BigDecimal totalAmount;

    @Column(name = "transaction_id", columnDefinition = "VARCHAR(512) COMMENT '第三方支付流水号'")
    private String transactionId;

    @Column(name = "notify_log", columnDefinition = "VARCHAR(512) COMMENT '支付回调原始记录'")
    private String notifyLog;

    @Column(name = "business_app_id", columnDefinition = "VARCHAR(32) COMMENT '业务appId'")
    private String businessAppId;

    @Column(name = "status", columnDefinition = "SMALLINT DEFAULT 0 COMMENT '状态:0默认，1已回调业务系统'", nullable = false)
    protected Integer status;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getBusinessOrderId() {
        return businessOrderId;
    }

    public void setBusinessOrderId(String businessOrderId) {
        this.businessOrderId = businessOrderId;
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getBusinessAppId() {
        return businessAppId;
    }

    public void setBusinessAppId(String businessAppId) {
        this.businessAppId = businessAppId;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
