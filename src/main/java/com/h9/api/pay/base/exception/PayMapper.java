package com.h9.api.pay.base.exception;


import com.h9.api.pay.base.Result;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @Description: 全局异常处理
 * @Auther Demon
 * @Date 2017/11/3 15:54 星期五
 */
@Provider
public class PayMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        e.printStackTrace();
        Response.ResponseBuilder ResponseBuilder;
        if (e instanceof PayException){
            //截取自定义类型
            PayException exp = (PayException) e;
            ResponseBuilder = Response.ok(Result.FailedResult(exp.getCode(), exp.getMessage()), MediaType.APPLICATION_JSON);
        }else {
            ResponseBuilder = Response.ok(Result.FailedResult(e.getMessage()), MediaType.APPLICATION_JSON);
        }
        return ResponseBuilder.build();
    }
}
