package com.h9.api.pay.rest;

import com.alibaba.fastjson.JSONObject;
import com.h9.api.pay.base.Result;
import com.h9.api.pay.rest.model.BaseXmlInfo;
import com.h9.api.pay.rest.model.DonationModel;
import com.h9.api.pay.rest.model.WxPayNotification;
import com.h9.api.pay.rest.model.WxPayResponse;
import com.h9.api.pay.service.PayService;
import com.h9.api.pay.util.WechatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @Description:
 * @Auther Demon
 * @Date 2017/11/16 9:32 星期四
 */
@Component
@Path("pay")
public class PayApi {

    @Autowired
    private PayService payService;

    @GET
    @Path("test")
    public Result test() {
        return Result.SucceedResult("well done!");
    }

    @POST
    @Path("submit")
    @Produces("application/json;charset=utf-8")
    public Result createPrepayOrder(DonationModel model) {
        return payService.submitDonation(model);
    }

    @POST
    @Path("notify/wxjs")
    @Consumes("text/xml;charset=utf-8")
    @Produces("text/xml;charset=utf-8")
    public WxPayResponse wxjsPayResponse(@Context HttpServletRequest request) {
        SortedMap<String, String> notifyParams = new TreeMap<>();
        Map<String, String> requestData = new HashMap<>();
        try {
            requestData = WechatUtil.parseXml(request);
            notifyParams.putAll(requestData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        WxPayNotification notification = JSONObject.parseObject(JSONObject.toJSONString(notifyParams), WxPayNotification.class);
        notification.setNotify_params(notifyParams);
        return payService.processWxNotification(notification);
    }



}
