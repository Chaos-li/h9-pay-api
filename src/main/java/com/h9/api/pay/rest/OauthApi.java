package com.h9.api.pay.rest;

import com.h9.api.pay.base.exception.PayException;
import com.h9.api.pay.db.entity.PaymentConfig;
import com.h9.api.pay.rest.model.WxOauthInfo;
import com.h9.api.pay.service.PayService;
import com.h9.api.pay.util.WechatUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @Description: 微信授权api
 * @Auther Demon
 * @Date 2017/11/16 13:53 星期四
 */
@Component
@Path("oauth")
public class OauthApi {

    Logger logger = LoggerFactory.getLogger(OauthApi.class);

    @Value("${donation.url}")
    private String donationPageUrl;
    @Autowired
    private PayService payService;

    @GET
    @Path("redirect/{pageNo}")
    public Response oauthRedirect(@PathParam("pageNo") String pageNo, @QueryParam("code") String code) {
        logger.info(">>>>>>>>>>>>>>oauthRedirect:code:" + code);
        if(StringUtils.isBlank(code)) {
            throw new PayException(PayException.ERROR, "网页授权错误:code为空");
        }
        PaymentConfig paymentConfig = payService.getPaymentConfig();
        WxOauthInfo oauthInfo = new WechatUtil().getWxOauthInfo(paymentConfig.getAppId(), paymentConfig.getAppSecret(), code);
        if(oauthInfo == null) {
            throw new PayException(PayException.ERROR, "网页授权错误:获取openid失败");
        }
        String openid = oauthInfo.getOpenid();
        URI uri;
        try {
            uri = new URI(donationPageUrl+"?openid=" + openid + "&type=" + pageNo);
        } catch (URISyntaxException e) {
            throw new PayException(PayException.ERROR, "服务器繁忙");
        }
        return Response.temporaryRedirect(uri).build();
    }

}
