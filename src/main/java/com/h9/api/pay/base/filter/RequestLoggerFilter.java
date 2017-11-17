package com.h9.api.pay.base.filter;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author: Demon
 * @date: 2017/8/28/0028 18:04.
 */
public class RequestLoggerFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggerFilter.class);

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        MultivaluedMap<String, String> headers = requestContext.getHeaders();

        String requestMethod = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();
        logger.debug("Request {} {}", requestMethod, uri);
        logger.debug("Request MediaType:" + requestContext.getMediaType());
        logger.debug("Request Headers:" + headers);
        try {
            if ("POST".equals(requestMethod) || "PUT".equals(requestMethod) && requestContext.getEntityStream() != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copy(requestContext.getEntityStream(), baos);
                byte[] bytes = baos.toByteArray();
                if(requestContext.getMediaType() != null && requestContext.getMediaType().toString().contains("multipart/form-data")){
                    logger.info("正在上传文件...");
                } else {
                    logger.debug(" Body:{}", new String(bytes, "UTF-8"));
                }
                requestContext.setEntityStream(new ByteArrayInputStream(bytes));
            }
        } catch (Exception e) {
            logger.error("log:", e);
        }
    }
}
