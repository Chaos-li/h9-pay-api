package com.h9.api.pay.util;

import com.squareup.okhttp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ps_an on 2016/9/24.
 */
public class HttpUtilClient {

	private static final Logger logger = LoggerFactory.getLogger(HttpUtilClient.class);

	public static String httpPost(String url, String content, Headers headers) {
		logger.info("httpPost request url: " + url);
		logger.info("httpPost request body: " + content);
		logger.info("httpPost: request headers " + headers.toString());
		OkHttpClient client = new OkHttpClient();
		RequestBody bodyContent = RequestBody.create(MediaType.parse("application/json"), content);
		Request request = new Request.Builder().url(url).post(bodyContent).headers(headers).build();
		try {
			Response response = client.newCall(request).execute();
			String body = response.body().string();
			Headers headersResponse = response.headers();
			logger.info("httpPost: response code " + response.code());
			logger.info("httpPost: response headers " + headersResponse.toString());
			logger.info("httpPost: response body " + body);
			if (response.code() == 200) {
				return body;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static String httpPost(String url, String content) {
		return httpPost(url, content, getHeaders());
	}
	
	public static String httpGet(String url, String token) {
		return httpGet(url, getHeaders(token));
	}

	public static String httpPost(String url, String content, String token) {
		return httpPost(url, content, getHeaders(token));
	}

	public static String httpPost(String url, byte[] input, Headers headers) {
		logger.debug("httpPost request url: " + url);
		logger.debug("httpPost request body: " + input.length);
		logger.debug("httpPost: request headers " + headers.toString());
		OkHttpClient client = new OkHttpClient();
		RequestBody bodyContent = new MultipartBuilder().type(MultipartBuilder.FORM).addFormDataPart("uploadedFile", "", RequestBody.create(MediaType.parse("image/png"), input))
				.build();

		Request request = new Request.Builder().url(url).post(bodyContent).headers(headers).build();
		try {
			Response response = client.newCall(request).execute();
			String body = response.body().string();
			Headers headersResponse = response.headers();
			logger.debug("httpPost: response code " + response.code());
			logger.debug("httpPost: response headers " + headersResponse.toString());
			logger.debug("httpPost: response body " + body);
			if (response.code() == 200) {
				return body;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static String httpGet(String url) {
		return httpGet(url, getHeaders(null));
	}

	public static String httpGet(String url, Headers headers) {

		logger.debug("http get: " + url);
		OkHttpClient client = new OkHttpClient();
		Request.Builder builder = new Request.Builder().url(url);
		if (headers != null) {
			builder.headers(headers);
		}
		Request request = builder.build();
		try {
			Response response = client.newCall(request).execute();
			logger.debug("response.code() is " + response.code());

			if (response.code() == 200) {
				String body = response.body().string();
				logger.debug("response.body is " + body);

				return body;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static Response httpRedirect(String url) {
		logger.debug("http redirect: " + url);
		OkHttpClient client = new OkHttpClient();
		Request.Builder builder = new Request.Builder().url(url);
		Request request = builder.build();
		try {
			Response response = client.newCall(request).execute();
			logger.debug("response.isRedirect() = " + response.isRedirect());
			if (response != null) {
				return response;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	private static Headers getHeaders(String token) {
		Map<String, String> headers = new HashMap<String, String>();
		if (token != null) {
			headers.put("token", token);
		}
		headers.put("Content-Type", "application/json");
		return Headers.of(headers);
	}

	private static Headers getHeaders() {
		return getHeaders(null);
	}
}
