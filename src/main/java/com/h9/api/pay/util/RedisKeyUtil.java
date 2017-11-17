package com.h9.api.pay.util;

/**
 * @Description: redis key
 * @Auther Demon
 * @Date 2017/11/16 14:28 星期四
 */
public class RedisKeyUtil {

    /** 支付配置 */
    public static String getPaymentConfigKey() {
        return "h9:pay:payment:config";
    }

    /** 订单自增序列号 */
    public static String getOrderSNKey() {
        return "h9:pay:order:sn";
    }

    /** 微信公众号预支付订单信息 */
    public static String getWXJSPrepayInfoKey(String orderNo) {
        return "h9:pay:wxjs:prepay:info:" + orderNo;
    }

}
