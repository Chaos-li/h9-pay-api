package com.h9.api.pay.base.filter;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import java.io.IOException;

/**
 * @author: Demon
 * @date: 2017/8/28/0028 18:04.
 */
public class ResponseLoggerFilter implements ContainerResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(ResponseLoggerFilter.class);

    @Context
    private ResourceInfo resourceInfo;

    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        logger.debug("Response uri {} {}", requestContext.getMethod(), requestContext.getUriInfo().getRequestUri());
        logger.debug("Response Status:{}", responseContext.getStatus() + " " + responseContext.getStatusInfo() + "");
        logger.debug("Response Headers:{}", responseContext.getHeaders());
        logger.debug("Response Body:{}", JSONObject.toJSONString(responseContext.getEntity()));
    }
}
