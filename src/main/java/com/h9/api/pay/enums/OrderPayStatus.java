package com.h9.api.pay.enums;

/**
 * @Description: 订单支付状态
 * @Auther Demon
 * @Date 2017/11/16 20:21 星期四
 */
public enum OrderPayStatus {

    UN_PAY(0, "待支付"),
    PAID(1, "已支付");

    private int value;
    private String desc;

    OrderPayStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }


}
