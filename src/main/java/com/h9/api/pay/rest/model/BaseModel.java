package com.h9.api.pay.rest.model;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @Description:
 * @Auther Demon
 * @Date 2017/11/1 16:45 星期三
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class BaseModel {

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
