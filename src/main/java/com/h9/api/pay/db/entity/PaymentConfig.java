package com.h9.api.pay.db.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Description: 支付配置
 * @Auther Demon
 * @Date 2017/11/15 16:15 星期三
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "payment_config")
public class PaymentConfig extends BaseEntity {

    @Column(name = "app_id", columnDefinition = "VARCHAR(255) COMMENT 'appid'", nullable = false)
    private String appId;

    @Column(name = "app_secret", columnDefinition = "VARCHAR(255) COMMENT 'secret'")
    private String appSecret;

    @Column(name = "mch_id", columnDefinition = "VARCHAR(255) COMMENT '商户号'", nullable = false)
    private String mchId;

    @Column(name = "api_key", columnDefinition = "VARCHAR(255) COMMENT '支付密钥'", nullable = false)
    private String apiKey;

    @Column(name = "body", columnDefinition = "VARCHAR(255) COMMENT '商品描述'", nullable = false)
    private String body;

    @Column(name = "enable_credit_cart", columnDefinition = "BIT(1) DEFAULT 0 COMMENT '是否可用信用卡'", nullable = false)
    private Boolean enableCreditCart;

    @Column(name = "notify_url", columnDefinition = "VARCHAR(255) COMMENT '支付回调地址'", nullable = false)
    private String notifyUrl;


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getEnableCreditCart() {
        return enableCreditCart;
    }

    public void setEnableCreditCart(Boolean enableCreditCart) {
        this.enableCreditCart = enableCreditCart;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
}
