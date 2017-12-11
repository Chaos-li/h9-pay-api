package com.h9.api.pay.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.h9.api.pay.base.Result;
import com.h9.api.pay.base.exception.PayException;
import com.h9.api.pay.db.entity.Donation;
import com.h9.api.pay.db.entity.Order;
import com.h9.api.pay.db.entity.PaymentConfig;
import com.h9.api.pay.db.repository.DonationRepository;
import com.h9.api.pay.db.repository.OrderRepository;
import com.h9.api.pay.db.repository.PaymentConfigRepository;
import com.h9.api.pay.enums.DonationTypeEnum;
import com.h9.api.pay.enums.OrderPayStatus;
import com.h9.api.pay.enums.PayMethodEnum;
import com.h9.api.pay.rest.model.*;
import com.h9.api.pay.util.DateTimeUtil;
import com.h9.api.pay.util.RedisKeyUtil;
import com.h9.api.pay.util.WechatUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Auther Demon
 * @Date 2017/11/16 14:20 星期四
 */
@Service
public class PayService {

    static Logger logger = LoggerFactory.getLogger(PayService.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private PaymentConfigRepository paymentConfigRepository;
    @Autowired
    private DonationRepository donationRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CallbackService callbackService;

    public PaymentConfig getPaymentConfig(String businessAppId) {
        String paymentConfigKey = RedisKeyUtil.getPaymentConfigKey(businessAppId);
        PaymentConfig paymentConfig;
        if(redisTemplate.hasKey(paymentConfigKey)) {
            paymentConfig = JSONObject.parseObject(redisTemplate.opsForValue().get(paymentConfigKey), PaymentConfig.class);
        } else {
            paymentConfig = paymentConfigRepository.findByBusinessAppId(businessAppId);
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
        WxPrepayInfo prepayInfo = getWxPrepayInfo(donation.getOrderNo(), donation.getTotalAmount(), model.getOpenid(), model.getBusinessAppId());

        return new Result(Result.SUCCESS, "获取预支付订单成功", prepayInfo);
    }

    /**
     * 生成订单，创建预支付订单
     * @param model
     * @return
     */
    public Result createPrepayOrder(OrderModel model) {
        Order order = generateOrder(model);
        // 获取预支付订单
        WxPrepayInfo prepayInfo = getWxPrepayInfo(order.getOrderNo(), order.getTotalAmount(), model.getOpenid(), model.getBusinessAppId());
        return new Result(Result.SUCCESS, "获取预支付订单成功", prepayInfo);
    }

    public WxPayResponse processWxNotification(WxPayNotification notification, String businessAppId) {
        PaymentConfig paymentConfig = getPaymentConfig(businessAppId);
        SortedMap<String, String> map = notification.getNotify_params();
        String log = JSON.toJSONString(map);
        logger.info("notify params from wx : " + log);
        WxPayResponse response = new WxPayResponse();
        try {
            if (!WechatUtil.verifySign(paymentConfig, map)) {
                logger.info("wxPayResponse: verifySign failed, return false ");
                response.setReturn_code(WechatUtil.FAILED);
                return response;
            }
            if (StringUtils.equals(notification.getReturn_code(), WechatUtil.SUCCESS)) {
                String prepayId = notification.getOut_trade_no();
                processOrder(notification, log, businessAppId, paymentConfig.getCallbackUrl());
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

    /**
     * 创建订单
     * @param model model
     * @return Order
     */
    private Order generateOrder(OrderModel model) {
        if(model == null) {
            throw new PayException("参数错误");
        }
        if(StringUtils.isBlank(model.getOpenid())) {
            throw new PayException("openid不能为空");
        }
        if(StringUtils.isBlank(model.getBusinessOrderId())) {
            throw new PayException("订单号不能为空");
        }
        if(model.getTotalAmount() == null || model.getTotalAmount().compareTo(BigDecimal.ZERO) != 1) {
            throw new PayException("订单金额错误");
        }
        if(StringUtils.isBlank(model.getBusinessAppId())) {
            throw new PayException("业务appid不能为空");
        }
        Order order = new Order();
        order.setBusinessOrderId(model.getBusinessOrderId());
        order.setTotalAmount(model.getTotalAmount());
        order.setUpdateTime(new Date());
        order.setOrderNo(generateOrderNo());
        order.setBusinessAppId(model.getBusinessAppId());
        return orderRepository.save(order);
    }

    private void processOrder(WxPayNotification notification, String log, String businessAppId, String callbackUrl) {
        if(businessAppId.equals("appidh9donateeqzkv")) {
            processDonation(notification, log);
            return;
        }
        String orderNo = notification.getOut_trade_no();
        Order order = orderRepository.findByOrderNo(orderNo);
        if(order == null) {
            logger.error("回调商户订单号找不到对应订单");
            throw new PayException( "没有对应订单");
        }
        if(order.getStatus() != OrderPayStatus.UN_PAY.getValue()) {
            logger.error("回调商户订单号对应订单非待支付状态");
            throw new PayException("回调商户订单号对应订单非待支付状态");
        }

        BigDecimal totalFee = new BigDecimal(notification.getTotal_fee());
        BigDecimal totalAmount = order.getTotalAmount().multiply(new BigDecimal(100)).setScale(0);
        if(totalAmount.compareTo(totalFee) != 0) {
            logger.error("回调商户订单支付金额异常:total_fee is " + notification.getTotal_fee() + "  but totalAmount is " + order.getTotalAmount());
            throw new PayException("回调商户订单支付金额异常");
        }
        order.setPayStatus(OrderPayStatus.PAID.getValue());
        order.setNotifyLog(log);
        order.setTransactionId(notification.getTransaction_id());
        order.setUpdateTime(new Date());
        order = orderRepository.save(order);

        // 回调业务系统
        callbackBusinessSystem(callbackUrl, order, notification);

    }

    private void callbackBusinessSystem(String callbackUrl, Order order, WxPayNotification notification) {
        PayNotifyObject payNotifyObject = new PayNotifyObject();
        payNotifyObject.setNotify_id(order.getId());
        payNotifyObject.setOrder_id(order.getBusinessOrderId());
        payNotifyObject.setPay_time(DateTimeUtil.dateToyyyyMMddHHmmss(order.getUpdateTime()));
        payNotifyObject.setPay_way(PayMethodEnum.WXJS.getKey());
        if(notification.getCash_fee() != null) {
            payNotifyObject.setCash_fee(new BigDecimal(notification.getCash_fee()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString());
        }
        payNotifyObject.setTotal_fee(new BigDecimal(notification.getTotal_fee()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString());
        if(notification.getCoupon_fee() != null) {
            payNotifyObject.setCoupon_fee(new BigDecimal(notification.getCoupon_fee()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString());
        }
        payNotifyObject.setCoupon_count(notification.getCoupon_count());
        payNotifyObject.setApp_id(order.getBusinessAppId());
        payNotifyObject.setPay_type("order");
        payNotifyObject.setTrade_no(notification.getTransaction_id());
        payNotifyObject.setNotify_time(DateTimeUtil.dateToyyyyMMddHHmmss(new Date()));
        payNotifyObject.setNotify_type("pay_notify");

        Result<?> rtn = callbackService.callback(callbackUrl, payNotifyObject);
        logger.info("call back result:{}", rtn.getMsg());
    }

    private void processDonation(WxPayNotification notification, String log) {
        String orderNo = notification.getOut_trade_no();
        Donation donation = donationRepository.findByOrderNo(orderNo);
        if(donation == null) {
            logger.error("回调商户订单号找不到对应订单");
            throw new PayException("没有对应订单");
        }
        if(donation.getStatus() != OrderPayStatus.UN_PAY.getValue()) {
            logger.error("回调商户订单号对应订单非待支付状态");
            throw new PayException("回调商户订单号对应订单非待支付状态");
        }

        BigDecimal totalFee = new BigDecimal(notification.getTotal_fee());
        BigDecimal totalAmount = donation.getTotalAmount().multiply(new BigDecimal(100)).setScale(0);
        if(totalAmount.compareTo(totalFee) != 0) {
            logger.error("回调商户订单支付金额异常:total_fee is " + notification.getTotal_fee() + "  but totalAmount is " + donation.getTotalAmount());
            throw new PayException("回调商户订单支付金额异常");
        }
        donation.setStatus(OrderPayStatus.PAID.getValue());
        donation.setNotifyLog(log);
        donation.setUpdateTime(new Date());
        donationRepository.save(donation);
    }

    /**
     * 生成已支付信息
     * @param orderNo orderNo
     * @param totalAmount totalAmount
     * @param openid openid
     * @return WxPrepayInfo
     */
    private WxPrepayInfo getWxPrepayInfo(String orderNo, BigDecimal totalAmount, String openid, String businessAppId) {
        String prepayInfoKey = RedisKeyUtil.getWXJSPrepayInfoKey(orderNo);
        if(redisTemplate.hasKey(prepayInfoKey)) {
            return JSONObject.parseObject(redisTemplate.opsForValue().get(prepayInfoKey), WxPrepayInfo.class);
        }

        PaymentConfig paymentConfig = getPaymentConfig(businessAppId);

        // 拼接xml参数
        Map<String, String> wxPrepayParams = new HashMap<>();
        wxPrepayParams.put("payAmount", String.valueOf(totalAmount));
        wxPrepayParams.put("orderId", orderNo);
        if(StringUtils.isNotBlank(openid)) {
            wxPrepayParams.put("openId", openid);
        }
        String payArgs = WechatUtil.getPayArgs(paymentConfig, wxPrepayParams);

        // 调用统一下单接口
        String result = WechatUtil.doUnifiedOrder(payArgs);

        Map<String, String> xmlMap = WechatUtil.decodeXml(result);

        if (!StringUtils.equals(xmlMap.get("return_code"), "SUCCESS")) {
            logger.error("获取预支付订单失败:" + JSONObject.toJSONString(xmlMap));
            throw new PayException(PayException.ERROR, "获取预支付订单失败:[" + xmlMap.get("return_msg")+"]");
        } else if(StringUtils.equals(xmlMap.get("result_code"), "FAIL")) {
            logger.error("获取预支付订单失败:" + JSONObject.toJSONString(xmlMap));
            throw new PayException(PayException.ERROR, "获取预支付订单失败:[" + xmlMap.get("err_code")+ ":" + xmlMap.get("err_code_des") + "]");
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
        donation.setOrderNo(generateOrderNo());
        donation.setTotalAmount(totalAmount);
        donation.setUpdateTime(new Date());
        donation.setEnterpriseMobile(model.getEnterpriseMobile());
        donation.setPersonalMobile(model.getPersonalMobile());
        return donationRepository.save(donation);
    }

    private String generateOrderNo() {
        return DateTimeUtil.dateToyyyyMMddHHmmss(new Date()).concat(redisTemplate.opsForValue().increment(RedisKeyUtil.getOrderSNKey(), 1L).toString());
    }

    private void validateDonation(DonationModel model) {
        if(StringUtils.isBlank(model.getOpenid())) {
            throw new PayException(PayException.ERROR, "缺少openid");
        }
        if(StringUtils.isBlank(model.getDonor()) && StringUtils.isBlank(model.getEnterprise())) {
            throw new PayException(PayException.ERROR, "请填写完整信息");
        }
        if((StringUtils.isNotBlank(model.getDonor()) && model.getPersonalAmount() == null)
                || (StringUtils.isNotBlank(model.getEnterprise()) && model.getEnterpriseAmount() == null)) {
            throw new PayException(PayException.ERROR, "请填写捐赠金额");
        }
        if(model.getPersonalAmount() != null && model.getPersonalAmount().compareTo(new BigDecimal(1000)) == -1) {
            throw new PayException(PayException.ERROR, "个人捐款金额不能低于1000元");
        }
        if(model.getEnterpriseAmount() != null && model.getEnterpriseAmount().compareTo(new BigDecimal(5000)) == -1) {
            throw new PayException(PayException.ERROR, "企业捐款金额不能低于5000元");
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
