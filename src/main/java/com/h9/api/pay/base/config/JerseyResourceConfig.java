package com.h9.api.pay.base.config;

import com.h9.api.pay.base.exception.PayMapper;
import com.h9.api.pay.base.filter.RequestLoggerFilter;
import com.h9.api.pay.base.filter.ResponseLoggerFilter;
import com.h9.api.pay.rest.OauthApi;
import com.h9.api.pay.rest.PayApi;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

/**
 * @description: Jersey资源配置
 * @author: Demon
 * @date: 2017/8/10/0010 15:33.
 */
public class JerseyResourceConfig extends ResourceConfig {

    // 两种注册方式，注册类，注册包
    public JerseyResourceConfig() {
        register(PayMapper.class);
        register(RequestContextFilter.class);
        register(PayApi.class);
        register(OauthApi.class);
        //register(RequestLoggerFilter.class);
        register(ResponseLoggerFilter.class);

    }

}
