package com.h9.api.pay.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.yaml.snakeyaml.util.UriEncoder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.Base64;

/**
 * @Description:
 * @Auther Demon
 * @Date 2017/11/16 14:12 星期四
 */
public class StringUtil {

    public static String messageFormat(String var1, Object... var2) {
        if (var2 == null || var2.length == 0) {
            return var1;
        }
        Object[] var3 = new Object[var2.length];
        for (int i = 0; i < var2.length; i++) {
            if (var2[i] == null) {
                var3[i] = "";
                continue;
            }
            var3[i] = var2[i] instanceof String ? var2[i] : String.valueOf(var2[i]);
        }
        return MessageFormat.format(var1, var3);
    }

    public final static String MD5(byte[] buffer) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static String toJson(Object obj) {
        return JSONObject.toJSONString(obj);
    }


    public static void main(String[] args) {
        System.out.println(RandomStringUtils.random(5, true, false).toLowerCase());
    }


}
