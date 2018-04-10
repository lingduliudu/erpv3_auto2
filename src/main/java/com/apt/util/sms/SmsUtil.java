package com.apt.util.sms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.apt.util.ChkUtil;
import com.apt.util.StaticData;
import com.apt.util.SysConfig;
import com.apt.util.http.HttpUtil;
import com.apt.util.rest.RestReqUtil;

public class SmsUtil extends RestReqUtil {

	public final static String SMS_SEND_SIGNRE_URL = SysConfig.getInstance().getProperty("SmsUrl") + "sendMsg.html";
	//日志
	private static Logger logger = LoggerFactory.getLogger(SmsUtil.class);
	
	/**
	 * 		
	 * @param mobile	手机号码
	 * @param number	短信编号（详情短信编码文档）
	 * @param params	参数（JSON格式  详情 短信参数文档）
	 * @return	参考短信返回参数 200 表示发送成功 2001系统异常 其它返回值 参考文档
	 */
	@SuppressWarnings("rawtypes")
	public static String senMsg(String mobile,String number,JSONObject params) {
		String result = "";
		LinkedHashMap<String, Object> reqMap = new LinkedHashMap<String, Object>();
		reqMap.put("mobile", mobile);
		reqMap.put("number", number);
		reqMap.put("params", ChkUtil.isEmpty(params)? "" : params);
		reqMap.put("sysNumber", "erpv3_auto");
		try {
			logger.warn("手机号:"+mobile+",编号:"+number+"本次短信接口请求参数:"+params.toString());
			ResponseEntity entity = reqPyConnection(SysConfig.getInstance().getProperty("SmsUrl") + "sendMsg.html",reqMap, HttpMethod.POST);
			result = entity.getBody().toString();
			logger.warn("本次短信接口返回值:"+result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("本次短信接口失败!");
			result = "2001";
		}
		return result;
	}
	@SuppressWarnings("rawtypes")
	public static void senErrorMsg(JSONObject params) {
		if(StaticData.closeSms){
			return;
		}
		try{
		senErrorMsgByZhiyun(params);
		}catch (Exception e) {
		}
/*		String result = "";
		LinkedHashMap<String, Object> reqMap = new LinkedHashMap<String, Object>();
		String errorMobiles  = SysConfig.getInstance().getProperty("errorMobiles");
		if(StringUtil.isNotEmptyOrNull(errorMobiles)){
			for(String mobile:errorMobiles.split(Pattern.quote(","))){
				reqMap.put("mobile", mobile);
				reqMap.put("number", "050");
				reqMap.put("params", ChkUtil.isEmpty(params)? "" : params);
				reqMap.put("sysNumber", "erpv3_auto");
				try {
					logger.warn("手机号:"+mobile+",编号:050本次短信接口请求参数:"+params.toString());
					ResponseEntity entity = reqPyConnection(SMS_SEND_SIGNRE_URL,reqMap, HttpMethod.POST);
					result = entity.getBody().toString();
					logger.warn("本次短信接口返回值:"+result);
				} catch (Exception e) {
					e.printStackTrace();
					logger.warn(e.getMessage(),e);
					logger.warn("本次短信接口失败!");
					result = "2001";
				}
			}
		}*/
	}
	/**
	 * 通过指云方式 发送信息
	 * @param paramMap
	 */
	public static void senErrorMsgByZhiyun(JSONObject paramMap) {
		if (StaticData.closeSms)
		{
			return;
		}
		if (paramMap == null)
		{
			return;
		}
		StringBuffer contentBuffer = new StringBuffer();
		//拼接 指云推送 content
		if (paramMap.containsKey("text"))
		{
			contentBuffer.append(paramMap.get("text") + "&&");
		}
		if (paramMap.containsKey("project_number"))
		{
			contentBuffer.append(paramMap.get("project_number"));
		}
		paramMap.put("content", contentBuffer.toString());
		//获取指云发送url
		String errorMobiles = SysConfig.getInstance().getProperty("errorZhiyun");
		//开始发送信息
		for (String mobile : errorMobiles.split(Pattern.quote(",")))
		{
			String content="";
			paramMap.put("username", mobile);
			HttpClient httpClient = new HttpClient();
			PostMethod postMethod = new PostMethod(SysConfig.getInstance().getProperty("SmsZhiyunUrl"));
			postMethod.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");
			//遍历参数
			if(paramMap!=null){
			Set<String>keys=paramMap.keySet();
			NameValuePair[] data = new NameValuePair[keys.size()];
			Iterator it=keys.iterator();
			int i=0;
			while (it.hasNext())
			{
				String key=String.valueOf(it.next());
				data[i]=new NameValuePair(key,paramMap.get(key).toString());
				i++;
			}
			postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8"); 
		    postMethod.setRequestBody(data);
			}
		    try 
		    {
		    	httpClient.executeMethod(postMethod);
		    	//获得响应内容String格式
				content=postMethod.getResponseBodyAsString();
				logger.warn("本次短信反馈内容:"+content);
			} 
		    catch (IOException e) 
		    {
				logger.warn(e.getMessage(),e);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 发送数据给清结算系统记录
	 * @param paramMap
	 */
	public static void sendFlowRecord(JSONArray data){
		Map dataMap = new HashMap();
		dataMap.put("orderArr", data);
		dataMap.put("rem", "");
		//合并代码后开启测试
		logger.warn("本次清结算接口请求内容:"+data);
		String result = "";
		try{
			//result = HttpUtil.connectByUrl(StaticData.bcUrl+"tbFlowRecordService/addFlowRecoder", dataMap, false);
		}catch (Exception e) {
			//logger.warn(e.getMessage(),e);
			logger.warn("本次清结算接口返回异常!");
		}
		logger.warn("本次清结算接口返回内容:"+result);
		
	}
	public static void main(String[] args) {
		String errorMobiles = SysConfig.getInstance().getProperty("errorMobiles");
		for(String mobile:errorMobiles.split(Pattern.quote(","))){
			JSONObject json = new JSONObject();
			json.put("project_number", "123");
			json.put("text", "测试使用");
			senErrorMsgByZhiyun(json);
		}
	}
	
}
