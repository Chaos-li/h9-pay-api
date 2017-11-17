package com.h9.api.pay.rest.model;

/**
 * @Description: 预支付订单信息
 * @Auther Demon
 * @Date 2017/11/16 16:54 星期四
 */
public class WxPrepayInfo extends BaseModel {

    private String appId;
    /** 支付签名随机串，不长于 32 位 */
    private String nonceStr;
    /** 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=***） */
    private String packageParam;
    /**  签名方式:MD5 */
    private String signType;
    /** 支付签名 */
    private String paySign;
    private String timestamp;
    private String prepayId;
    private String partnerId;
    private String mchId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getPackageParam() {
        return packageParam;
    }

    public void setPackageParam(String packageParam) {
        this.packageParam = packageParam;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getPaySign() {
        return paySign;
    }

    public void setPaySign(String paySign) {
        this.paySign = paySign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }
}
