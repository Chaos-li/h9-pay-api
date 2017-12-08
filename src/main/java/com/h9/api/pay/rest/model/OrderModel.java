package com.h9.api.pay.rest.model;

import java.math.BigDecimal;

/**
 * @Description:
 * @Auther Demon
 * @Date 2017/12/7 19:10 星期四
 */
public class OrderModel extends BaseModel {

    private String openid;

    private String orderNo;

    private String businessOrderId;

    private BigDecimal totalAmount;

    private String businessAppId;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

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

    public String getBusinessAppId() {
        return businessAppId;
    }

    public void setBusinessAppId(String businessAppId) {
        this.businessAppId = businessAppId;
    }
}
