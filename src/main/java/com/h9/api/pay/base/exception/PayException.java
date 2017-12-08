package com.h9.api.pay.base.exception;

/**
 * @author: Demon
 * @date: 2017/8/29/0029 20:20.
 */
public class PayException extends RuntimeException {

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;

    private int code;
    private String message;

    public PayException() {
    }

    /**
     * 构造异常类
     * @param message
     */
    public PayException(String message) {
        this.code = ERROR;
        this.message = message;
    }

 /**
     * 构造异常类
     * @param code
     * @param message
     */
    public PayException(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
