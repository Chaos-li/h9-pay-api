package com.h9.api.pay.enums;

/**
 * @Description: 捐赠订单状态
 * @Auther Demon
 * @Date 2017/11/16 20:21 星期四
 */
public enum DonationOrderStatus {

    UN_PAY(0, "待支付"),
    PAID(1, "已支付");

    private int value;
    private String desc;

    DonationOrderStatus(int value, String desc) {
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
