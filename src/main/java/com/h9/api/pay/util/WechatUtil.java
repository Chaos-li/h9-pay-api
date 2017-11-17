package com.h9.api.pay.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.h9.api.pay.db.entity.PaymentConfig;
import com.h9.api.pay.rest.model.WxOauthInfo;
import com.squareup.okhttp.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @Description:
 * @Auther Demon
 * @Date 2017/11/16 14:09 星期四
 */
public class WechatUtil {

    static Logger logger = LoggerFactory.getLogger(WechatUtil.class);

    public final static String SUCCESS = "SUCCESS";
    public final static String FAILED = "FAIL";

    /** 统一下单url */
    public static final String WX_UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /** 根据code换取access_token url */
    private static String GET_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid={0}&secret={1}&code={2}&grant_type=authorization_code";

    public static WxOauthInfo getWxOauthInfo(String appid, String secret, String code) {
        if (!StringUtils.isEmpty(appid) && !StringUtils.isEmpty(secret) && !StringUtils.isEmpty(code)) {
            GET_ACCESS_TOKEN_URL = StringUtil.messageFormat(GET_ACCESS_TOKEN_URL, appid, secret, code);
            String result = HttpUtilClient.httpGet(GET_ACCESS_TOKEN_URL);
            if (result != null) {
                WxOauthInfo wxOauthInfo = JSONObject.parseObject(result, new TypeToken<WxOauthInfo>(){}.getType());
                wxOauthInfo.setAppid(appid);
                wxOauthInfo.setCode(code);
                return wxOauthInfo;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static String getPayArgs(PaymentConfig paymentConfig, Map<String, String> wxPrepayParams) {

        BigDecimal payAmount = new BigDecimal(wxPrepayParams.get("payAmount"));
        int amount = payAmount.multiply(new BigDecimal("100")).intValue();
        List<NameValuePair> packageParams = new LinkedList<>();
        packageParams.add(new BasicNameValuePair("appid", paymentConfig.getAppId()));
        packageParams.add(new BasicNameValuePair("body", paymentConfig.getBody()));
        if(!paymentConfig.getEnableCreditCart()) {
            packageParams.add(new BasicNameValuePair("limit_pay", "no_credit"));
        }
        packageParams.add(new BasicNameValuePair("mch_id", paymentConfig.getMchId()));
        packageParams.add(new BasicNameValuePair("nonce_str", createNonceStr()));
        packageParams.add(new BasicNameValuePair("notify_url", paymentConfig.getNotifyUrl()));
        if(StringUtils.isNotBlank(wxPrepayParams.get("openId"))) {
            packageParams.add(new BasicNameValuePair("openid", wxPrepayParams.get("openId")));
        }
        packageParams.add(new BasicNameValuePair("out_trade_no", wxPrepayParams.get("orderId")));
        packageParams.add(new BasicNameValuePair("spbill_create_ip","127.0.0.1"));
        packageParams.add(new BasicNameValuePair("total_fee", String.valueOf(amount)));
        packageParams.add(new BasicNameValuePair("trade_type", wxPrepayParams.get("openId") != null ? "JSAPI": "APP"));
        String sign = generatePackageSign(packageParams, paymentConfig.getApiKey());
        packageParams.add(new BasicNameValuePair("sign", sign));

        return toXml(packageParams);

    }

    public static boolean verifySign(PaymentConfig paymentConfig, SortedMap<String, String> map) {
        logger.debug("verifySign {0}, apiKey is {1}", map, paymentConfig.getApiKey());

        if (!StringUtils.equals(paymentConfig.getMchId(), map.get("mch_id"))
                || !StringUtils.equals(paymentConfig.getAppId(), map.get("appid"))) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            String value = map.get(key);
            if (StringUtils.isNotBlank(value) && !(StringUtils.equals(key, "sign") || StringUtils.equals(key, "formDataSource"))) {
                sb.append(key);
                sb.append('=');
                sb.append(value);
                sb.append('&');
            }
        }
        sb.append("key=").append(paymentConfig.getApiKey());
        logger.debug("=====sb.toString()====" + sb.toString());
        String signNew = generatePaySign(sb.toString());
        logger.debug("signNew is {0}, sign from wx is {1}", signNew, map.get("sign"));
        return StringUtils.equals(signNew, map.get("sign"));
    }

    public static String doUnifiedOrder(String content) {
        return httpPost("application/xml", WX_UNIFIED_ORDER_URL, content);
    }

    public static String httpPost(String mediaType, String url, String content) {
        logger.debug("httpsPost: {}", url);
        logger.debug("httpsPost content: {}", content);

        OkHttpClient client = new OkHttpClient();
        RequestBody bodyContent = RequestBody.create(
                MediaType.parse(mediaType), content);
        Request request = new Request.Builder().url(url).post(bodyContent)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            logger.debug("httpsPost: response code {}", response.code());
            logger.debug("httpsPost: response body {}", body);
            if (response.code() == 200) {
                return body;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String createNonceStr() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static String getArgs(PaymentConfig paymentConfig, String prepayId, String packageParam, String nonceStr, long timeStamp) {
        StringBuffer args = new StringBuffer()
                .append("appId=").append(paymentConfig.getAppId())
                .append("&nonceStr=").append(nonceStr)
                .append("&package=").append(packageParam)
                .append("&signType=MD5")
                .append("&timeStamp=").append(timeStamp)
                .append("&key=").append(paymentConfig.getApiKey());
           return args.toString();
    }

    public static String generatePaySign(String signStr) {
        return StringUtil.MD5(signStr.getBytes()).toUpperCase();
    }

    public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<>();
        InputStream inStream = request.getInputStream();
        String result = null;
        try {
            if(inStream != null){
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] tempBytes = new byte[2048];
                int count = -1;
                while((count = inStream.read(tempBytes, 0, 2048)) != -1){
                    outStream.write(tempBytes, 0, count);
                }
                tempBytes = null;
                outStream.flush();
                result = new String(outStream.toByteArray(), "UTF-8");
            }
        } catch (Exception e) {
            result = null;
        }


		// 读取输入流
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new ByteArrayInputStream(result.getBytes("UTF-8")));
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();
        // 遍历所有子节点
        for (Element e : elementList)
            map.put(e.getName(), e.getText());
        // 释放资源
      /*  inputStream.close();
        inputStream = null;*/
        return map;
    }

    private static String createOutTradePrepayId(String orderId) {
        DecimalFormat prepayIdDf = new DecimalFormat("0000000000");
        return prepayIdDf.format(orderId);
    }

    private static String generatePackageSign(List<NameValuePair> params, String apiKey) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=").append(apiKey);

        return generatePaySign(sb.toString());
    }

    private static String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sb.append("<xml>\n");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).getName() + ">");

            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">\n");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    public static Map<String, String> decodeXml(String content) {
        try {
            Map<String, String> xml = new HashMap<>();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            // 设置支持命名空间
            factory.setNamespaceAware(true);
            // 2.生成parser对象
            XmlPullParser parser = factory.newPullParser();
            // 3.设置输入
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (!"xml".equals(nodeName)) {
                            // 实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}
