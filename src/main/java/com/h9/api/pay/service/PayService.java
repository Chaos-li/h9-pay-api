package com.h9.api.pay.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.h9.api.pay.base.Result;
import com.h9.api.pay.base.exception.PayException;
import com.h9.api.pay.db.entity.Donation;
import com.h9.api.pay.db.entity.PaymentConfig;
import com.h9.api.pay.db.repository.DonationRepository;
import com.h9.api.pay.db.repository.PaymentConfigRepository;
import com.h9.api.pay.enums.DonationOrderStatus;
import com.h9.api.pay.enums.DonationTypeEnum;
import com.h9.api.pay.rest.model.*;
import com.h9.api.pay.util.DateTimeUtil;
import com.h9.api.pay.util.RedisKeyUtil;
import com.h9.api.pay.util.WechatUtil;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Auther Demon
 * @Date 2017/11/16 14:20 星期四
 */
@Service
public class PayService {

    Logger logger = LoggerFactory.getLogger(PayService.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private PaymentConfigRepository paymentConfigRepository;
    @Autowired
    private DonationRepository donationRepository;

    public PaymentConfig getPaymentConfig() {
        String paymentConfigKey = RedisKeyUtil.getPaymentConfigKey();
        PaymentConfig paymentConfig;
        if(redisTemplate.hasKey(paymentConfigKey)) {
            paymentConfig = JSONObject.parseObject(redisTemplate.opsForValue().get(paymentConfigKey), PaymentConfig.class);
        } else {
            paymentConfig = paymentConfigRepository.findAll().get(0);
            if(paymentConfig == null) {
                throw new PayException(PayException.ERROR, "未配置支付信息");
            } else {
                redisTemplate.opsForValue().set(paymentConfigKey, JSONObject.toJSONString(paymentConfig));
            }
        }
        return paymentConfig;
    }

    /**
     * 提交捐赠生成预支付订单
     * @param model
     * @return
     */
    @Transactional
    public Result submitDonation(DonationModel model) {
        // 参数校验
        validateDonation(model);

        // 生成捐赠信息
        Donation donation = generateDonation(model);

        // 获取预支付订单
        WxPrepayInfo prepayInfo = getWxPrepayInfo(donation, model.getOpenid());

        return new Result(Result.SUCCESS, "获取预支付订单成功", prepayInfo);
    }

    public WxPayResponse processWxNotification(WxPayNotification notification) {
        PaymentConfig paymentConfig = getPaymentConfig();
        SortedMap<String, String> map = notification.getNotify_params();
        String log = JSON.toJSONString(map);
        logger.info("notify params from wx : " + log);
        WxPayResponse response = new WxPayResponse();
        try {
           /* if (!WechatUtil.verifySign(paymentConfig, map)) {
                logger.info("wxPayResponse: verifySign failed, return false ");
                response.setReturn_code(WechatUtil.FAILED);
                return response;
            }
*/
            if (StringUtils.equals(notification.getReturn_code(), WechatUtil.SUCCESS)) {
                String prepayId = notification.getOut_trade_no();

                processOrder(notification, log);
                response.setReturn_code(WechatUtil.SUCCESS);
            } else {
                logger.error("get failed message from wxpay:" + notification.getReturn_msg());
                response.setReturn_code(WechatUtil.FAILED);
            }
        } catch (Exception ex) {
            logger.error("wxPayResponse error:", ex);
            response.setReturn_code(WechatUtil.FAILED);
        }
        return response;
    }

    private void processOrder(WxPayNotification notification, String log) {
        String orderNo = notification.getOut_trade_no();
        Donation donation = donationRepository.findByOrderNo(orderNo);
        if(donation == null) {
            logger.error("回调商户订单号找不到对应订单");
            throw new PayException(PayException.ERROR, "没有对应订单");
        }
        if(donation.getStatus() != DonationOrderStatus.UN_PAY.getValue()) {
            logger.error("回调商户订单号对应订单非待支付状态");
            throw new PayException(PayException.ERROR, "回调商户订单号对应订单非待支付状态");
        }

        BigDecimal totalFee = new BigDecimal(notification.getTotal_fee());
        BigDecimal totalAmount = donation.getTotalAmount().multiply(new BigDecimal(100)).setScale(0);
        if(totalAmount.compareTo(totalFee) != 0) {
            logger.error("回调商户订单支付金额异常:total_fee is " + notification.getTotal_fee() + "  but totalAmount is " + donation.getTotalAmount());
            throw new PayException(PayException.ERROR, "回调商户订单支付金额异常");
        }
        donation.setStatus(DonationOrderStatus.PAID.getValue());
        donation.setNotifyLog(log);
        donation.setUpdateTime(new Date());
        donationRepository.save(donation);
    }

    /**
     * 生成已支付信息
     * @param donation
     * @param openid
     * @return
     */
    private WxPrepayInfo getWxPrepayInfo(Donation donation, String openid) {
        String orderNo = donation.getOrderNo();
        String prepayInfoKey = RedisKeyUtil.getWXJSPrepayInfoKey(orderNo);
        if(redisTemplate.hasKey(prepayInfoKey)) {
            WxPrepayInfo prepayInfo = JSONObject.parseObject(redisTemplate.opsForValue().get(prepayInfoKey), WxPrepayInfo.class);
            return prepayInfo;
        }

        PaymentConfig paymentConfig = getPaymentConfig();

        // 拼接xml参数
        Map<String, String> wxPrepayParams = new HashMap<>();
        wxPrepayParams.put("payAmount", String.valueOf(donation.getTotalAmount()));
        wxPrepayParams.put("orderId", donation.getOrderNo());
        wxPrepayParams.put("openId", openid);
        String payArgs = WechatUtil.getPayArgs(paymentConfig, wxPrepayParams);

        // 调用统一下单接口
        String result = WechatUtil.doUnifiedOrder(payArgs);

        Map<String, String> xmlMap = WechatUtil.decodeXml(result);
        String return_code = xmlMap.get("return_code");

        if (!StringUtils.equals(return_code, "SUCCESS")) {
            logger.error("获取预支付订单失败:" + JSONObject.toJSONString(xmlMap));
            throw new PayException(PayException.ERROR, "获取预支付订单失败:[" + xmlMap.get("err_code_des")+"]");
        }
        String prePayId = xmlMap.get("prepay_id");
        String packageParam = "prepay_id=" + prePayId;
        String nonceStr = WechatUtil.createNonceStr();
        Long timeStamp = WechatUtil.genTimeStamp();
        String args = WechatUtil.getArgs(paymentConfig, prePayId, packageParam, nonceStr, timeStamp);

        String paySign = WechatUtil.generatePaySign(args);

        WxPrepayInfo prepayInfo = new WxPrepayInfo();
        prepayInfo.setAppId(paymentConfig.getAppId());
        prepayInfo.setMchId(paymentConfig.getMchId());
        prepayInfo.setPartnerId(paymentConfig.getMchId());
        prepayInfo.setNonceStr(nonceStr);
        prepayInfo.setPackageParam(packageParam);
        prepayInfo.setPaySign(paySign);
        prepayInfo.setSignType("MD5");
        prepayInfo.setTimestamp(timeStamp.toString());
        prepayInfo.setPrepayId(prePayId);

        // 缓存预支付信息
        redisTemplate.opsForValue().set(prepayInfoKey, JSONObject.toJSONString(prepayInfo), 5, TimeUnit.MINUTES);

        return prepayInfo;
    }

    private Donation generateDonation(DonationModel model) {
        Donation donation = new Donation();
        BeanUtils.copyProperties(model, donation);
        int type = donation.getType();
        BigDecimal totalAmount;
        if(type == DonationTypeEnum.PERSONAL.getValue()) {
            totalAmount = donation.getPersonalAmount();
        } else if(type == DonationTypeEnum.ENTERPRISE.getValue()) {
            totalAmount = donation.getEnterpriseAmount();
        } else {
            totalAmount = donation.getPersonalAmount().add(donation.getEnterpriseAmount());
        }
        try {
            // 生成支付订单号
            String orderNo = DateTimeUtil.dateToyyyyMMddHHmmss(new Date()).concat(redisTemplate.opsForValue().increment(RedisKeyUtil.getOrderSNKey(), 1L).toString());
            donation.setOrderNo(orderNo);
        } catch (ParseException e) {
            logger.error("生成支付订单号异常", e);
            throw new PayException(PayException.ERROR, "服务器繁忙");
        }
        donation.setTotalAmount(totalAmount);
        donation.setUpdateTime(new Date());
        return donationRepository.save(donation);
    }

    private void validateDonation(DonationModel model) {
        if(StringUtils.isBlank(model.getOpenid())) {
            throw new PayException(PayException.ERROR, "缺少openid");
        }
        if(StringUtils.isBlank(model.getDonor()) && StringUtils.isBlank(model.getEnterprise())) {
            throw new PayException(PayException.ERROR, "请填写完整信息");
        }
        if((StringUtils.isNotBlank(model.getDonor()) && (model.getPersonalAmount() == null || model.getPersonalAmount().compareTo(BigDecimal.ZERO) < 1))
                || (StringUtils.isNotBlank(model.getEnterprise()) && (model.getEnterpriseAmount() == null || model.getEnterpriseAmount().compareTo(BigDecimal.ZERO) < 1))) {
            throw new PayException(PayException.ERROR, "请填写正确的捐赠金额");
        }
        if(StringUtils.isNotBlank(model.getEnterprise()) && StringUtils.isNotBlank(model.getDonor())) {
            model.setType(DonationTypeEnum.PERSONAL_AND_ENTERPRISE.getValue());
        } else if(StringUtils.isNotBlank(model.getEnterprise())) {
            model.setType(DonationTypeEnum.ENTERPRISE.getValue());
        } else {
            model.setType(DonationTypeEnum.PERSONAL.getValue());
        }
    }

}
