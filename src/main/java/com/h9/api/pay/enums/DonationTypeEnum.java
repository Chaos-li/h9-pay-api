package com.h9.api.pay.enums;

/**
 * @Description: 捐赠类型
 * @Auther Demon
 * @Date 2017/11/16 15:42 星期四
 */
public enum DonationTypeEnum {

    PERSONAL(0, "个人捐赠"),
    ENTERPRISE(1, "企业捐赠"),
    PERSONAL_AND_ENTERPRISE(2, "个人与企业");

    private int value;
    private String desc;

    DonationTypeEnum(int value, String desc) {
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
