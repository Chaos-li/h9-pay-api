/*
 * *
 *
 *     Created by OuYangX.
 *     Copyright (c) 2017, ouyangxian@gmail.com All Rights Reserved.
 *
 * /
 */

package com.h9.api.pay.enums;

/**
 * 支付方式
 */
public enum PayMethodEnum {
    BALANCE(0, "balance"), ALIPAY(1, "alipay"), WX(2, "wx"), WXJS(3, "wxjs"), YWT(4, "ywt"),
    WXSCAN(5, "wxscan"), ALISCAN(6, "aliscan"), WXMINI(7, "wxmini"), ALINATIVE(8, "alinative");

    private int key;
    private String value;

    public static PayMethodEnum valueOf(int key) {
        switch (key) {
            case 0:
                return BALANCE;
            case 1:
                return ALIPAY;
            case 2:
                return WX;
            case 3:
                return WXJS;
            case 4:
                return YWT;
            case 5:
                return WXSCAN;
            case 6:
                return ALISCAN;
            case 7:
                return WXMINI;
            case 8:
                return ALINATIVE;
            default:
                return null;
        }
    }

    public static PayMethodEnum getByValue(String value) {
        PayMethodEnum[] season = values();
        for (PayMethodEnum s : season) {
            if (s.getValue().equals(value)) {
                return s;
            }
        }
        return null;
    }

    public static boolean contains(int key) {
        //所有的枚举值
        PayMethodEnum[] season = values();
        //遍历查找
        for (PayMethodEnum s : season) {
            if (s.getKey() == key) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(String value) {
        //所有的枚举值
        PayMethodEnum[] season = values();
        //遍历查找
        for (PayMethodEnum s : season) {
            if (s.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private PayMethodEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
