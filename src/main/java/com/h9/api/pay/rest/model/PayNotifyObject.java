package com.h9.api.pay.rest.model;

import com.alibaba.fastjson.JSON;
import com.h9.api.pay.util.DateTimeUtil;

import java.math.BigDecimal;
import java.util.Date;

public class PayNotifyObject {

    private String notify_time; // 通知时间 Date 通知的发送时间。格式为 yyyy-MM-dd HH:mm:ss
    private String notify_type; // 通知类型 String 通知的类型
    private long notify_id;   // 通知校验 ID String 通知校验 ID。notify_id 子系统必须记录用于退款业务
    private String order_id;    //订单号
    private String total_fee;   //订单总金额
    private String cash_fee;   // 实际支付金额
    private String coupon_fee; // 代金券或立减优惠金额<=订单总金额，订单总金额-代金券或立减优惠金额=现金支付金额，详见支付金额
    private String coupon_rate; //折扣率用于计算多次退款的实际退款金额
    private Integer coupon_count;// 代金券或立减优惠使用数量
    private String pay_time;
    private int pay_way;  //支付方式
    private String sub_mchid;

    /** 业务数据 */
    private String app_id;
    private String pay_type;
    
    /*三方支付方交易号*/
    private String trade_no;

    private int pay_channel;
    
    private String pay_order_id;//支付订单号
    
    public PayNotifyObject() {
		super();
	}
    
    public PayNotifyObject(long notify_id, String order_id, BigDecimal total_fee, BigDecimal cash_fee, Date pay_time, int pay_way) {
		super();
		this.notify_id = notify_id;
		this.order_id = order_id;
		this.total_fee = total_fee.toString();
		this.cash_fee = cash_fee.toString();
		this.pay_time = DateTimeUtil.dateToyyyyMMddHHmmss(pay_time);
		this.pay_way = pay_way;
	}

    public String getNotify_time() {
        return notify_time;
    }

    public void setNotify_time(String notify_time) {
        this.notify_time = notify_time;
    }

    public String getNotify_type() {
        return notify_type;
    }

    public void setNotify_type(String notify_type) {
        this.notify_type = notify_type;
    }

    public long getNotify_id() {
        return notify_id;
    }

    public void setNotify_id(long notify_id) {
        this.notify_id = notify_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getCash_fee() {
        return cash_fee;
    }

    public void setCash_fee(String cash_fee) {
        this.cash_fee = cash_fee;
    }

    public String getCoupon_fee() {
        return coupon_fee;
    }

    public void setCoupon_fee(String coupon_fee) {
        this.coupon_fee = coupon_fee;
    }

    public Integer getCoupon_count() {
        return coupon_count;
    }

    public void setCoupon_count(Integer coupon_count) {
        this.coupon_count = coupon_count;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public int getPay_way() {
        return pay_way;
    }

    public void setPay_way(int pay_way) {
        this.pay_way = pay_way;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getCoupon_rate() {
        return coupon_rate;
    }

    public void setCoupon_rate(String coupon_rate) {
        this.coupon_rate = coupon_rate;
    }

    public String getSub_mchid() {
        return sub_mchid;
    }

    public void setSub_mchid(String sub_mchid) {
        this.sub_mchid = sub_mchid;
    }

    public String getTrade_no() {
		return trade_no;
	}

	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}

    public int getPay_channel() {
        return pay_channel;
    }

    public void setPay_channel(int pay_channel) {
        this.pay_channel = pay_channel;
    }

    public String getPay_order_id() {
		return pay_order_id;
	}

	public void setPay_order_id(String pay_order_id) {
		this.pay_order_id = pay_order_id;
	}

	@Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
