package com.h9.api.pay.util;

/**
 * @Description: redis key
 * @Auther Demon
 * @Date 2017/11/16 14:28 星期四
 */
public class RedisKeyUtil {

    /** 支付配置 */
    public static String getPaymentConfigKey(String businessAppId) {
        return "h9:pay:payment:config:" + businessAppId;
    }

    /** 订单自增序列号 */
    public static String getOrderSNKey() {
        return "h9:pay:order:sn";
    }

    /** 微信公众号预支付订单信息 */
    public static String getWXJSPrepayInfoKey(String businessAppId, String orderId) {
        return "h9:pay:wxjs:prepay:info:" + businessAppId + ":" + orderId;
    }
    /**
     * 订单回调次数Key h9:pay:order:notify:times:{appId}:{orderId}
     */
    public static String getOrderNotifyTimes(String businessAppId, String orderId) {
        return "h9:pay:order:notify:times:" + businessAppId + ":" + orderId;
    }

}
