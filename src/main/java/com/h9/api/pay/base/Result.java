package com.h9.api.pay.base;


import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class Result<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int SUCCESS = 0;
	public static final int FAILED = 1;

	private int statusCode = 0;
	private String msg;
	private T data;

	public Result() {

	}

	public Result(int statusCode, String msg) {
		this.statusCode = statusCode;
		this.msg = msg;
	}

	public Result(int statusCode, String msg, T data) {
		this.statusCode = statusCode;
		this.msg = msg;
		this.data = data;
	}

	public Result(String msg, T data) {
		this.statusCode = SUCCESS;
		this.msg = msg;
		this.data = data;
	}

	public Result(T data) {
		this.statusCode = SUCCESS;
		this.msg = "成功";
		this.data = data;
	}

	public static Result SucceedResult() {
		return new Result(SUCCESS, "成功");
	}

	public static Result SucceedResult(String msg) {
		return new Result(SUCCESS, msg);
	}

	public static Result SucceedResult(int statusCode, String msg) {
		return new Result(statusCode, msg);
	}

	public static Result FailedResult() {
		return new Result(FAILED, "失败");
	}

	public static Result FailedResult(String msg) {
		return new Result(FAILED, msg);
	}

	public static Result FailedResult(int statusCode, String msg) {
		return new Result(statusCode, msg);
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}
