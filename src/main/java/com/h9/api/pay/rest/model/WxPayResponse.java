package com.h9.api.pay.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @Description: 微信支付返回对象
 * @Auther Demon
 * @Date 2017/11/16 19:51 星期四
 */
@XmlRootElement(name = "xml")
public class WxPayResponse {

    private String return_code;
    private String return_msg;

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

}
