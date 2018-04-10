package com.apt.util.rest;

import java.util.LinkedHashMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.apt.util.SysConfig;
import com.apt.util.singature.SignatureUtil;

/**
 * REST请求服务器 工具类
 * @author Alan
 *
 */
public class RestReqUtil {

	
	/**
	 * 
	 * @param url
	 * @param reqMap
	 * @param method
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static  ResponseEntity reqPyConnection (String url ,LinkedHashMap<String, Object> reqMap,HttpMethod method) {
		 RestTemplate restTemplate = new RestTemplate();
		 //请求消息头
         HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_JSON);
         headers.set("Accept-Charset", "UTF-8");
         headers.set("Authorization",SignatureUtil.createSign());
		 HttpEntity entity = new HttpEntity(JSON.toJSON(reqMap),headers);
		 ResponseEntity response = restTemplate.exchange(url, method, entity, String.class);
		
		 return response;
	}
	
	
}
