package com.apt.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import net.sf.json.JSONObject;
/**
 * 功能说明：v3的webService的方法
 * 典型用法：
 * 特殊用法：	
 * @author 袁浩
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015年10月20日 
 * Copyright zzl-apt
 */
public class WebServiceUtil {
	private static Logger logger = LoggerFactory.getLogger(WebServiceUtil.class);
	//加载sysconfig
	private SysConfig sysConfig = SysConfig.getInstance();
	
	public synchronized static JSONObject sendPost(String url,Object[] params) {
		logger.warn("调用支付中心路径:"+url);
		if(params!=null && params.length>0){
			logger.warn("调用支付中心参数:"+params[0].toString());
		}
		if(url.contains("payProcess/redPayment")){ //说明是红包利息还款 判断金额是否大于0元,否则直接算成功即可
			Object obj = params[0];
			JSONObject baseJson  = JSONObject.fromObject(obj);
			if(baseJson.containsKey("txAmount") && baseJson.getDouble("txAmount") ==0){ //金额是0元则直接成功
				JSONObject speJson = new JSONObject();
				speJson.put("responseCode", "1");
				speJson.put("info", "0元红包放款直接默认成功!");
	            return speJson;
			}
		}
        PrintWriter out = null;  
        BufferedReader in = null;  
        StringBuffer result = new StringBuffer();  
        try {  
            URL realUrl = new URL(url);
            // 打开和URL之间的连接  
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();  
            // 设置通用的请求属性  
            conn.setRequestProperty("accept", "*/*");  
            conn.setRequestProperty("connection", "Keep-Alive");  
            conn.setRequestProperty("user-agent",  
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");  
            // 发送POST请求必须设置如下两行  
            conn.setRequestMethod("POST");   //设置POST方式连接
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            conn.setRequestProperty("Accept-Charset", "utf-8");
            // 获取URLConnection对象对应的输出流  
            out = new PrintWriter(conn.getOutputStream());  
            // 发送请求参数  
            for (Object object : params) {
            	 out.print(object);  
			}
            // flush输出流的缓冲  
            out.flush();  
            // 定义BufferedReader输入流来读取URL的响应  
            in = new BufferedReader(  
                    new InputStreamReader(conn.getInputStream(),"UTF-8"));  
            String line;  
            while ((line = in.readLine()) != null) {  
                result.append(line);  
            }
            logger.warn("支付中心返回:"+result);
        } catch (Exception e) {  
            e.printStackTrace();
            logger.warn(e.getMessage(),e);
            JSONObject errorJson = new JSONObject();
            errorJson.put("responseCode", "0");
            errorJson.put("info", "http请求返回错误!");
            errorJson.put("data", "http请求返回错误!");
            errorJson.put("responseBankCode", "");
            result = new StringBuffer();
            result.append(errorJson.toString());
        }  
        // 使用finally块来关闭输出流、输入流  
        finally {  
            try {  
                if (out != null) {  
                    out.close();  
                }  
                if (in != null) {  
                    in.close();  
                }  
            } catch (IOException ex) {  
                ex.printStackTrace();  
            }  
        }
        logger.warn("支付中心返回:"+result);
    	JSONObject resultJson = JSONObject.fromObject(result.toString());
		if(!resultJson.containsKey("responseBankCode")){
			resultJson.put("responseBankCode", "");
		}
		if(!resultJson.containsKey("info")){
			resultJson.put("info", "");
		}
		if(!resultJson.containsKey("data")){
			resultJson.put("data", "");
		}
		if(!resultJson.containsKey("responseCode")){
			resultJson.put("responseCode", "0");
		}
		return resultJson;
    } 
	
	/**
	 * 功能说明：用来格式化金额	
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 袁浩
	 * 创建日期：2015年10月20日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String formatDouble(double num){
		/*DecimalFormat  df=new DecimalFormat("0.00");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(num);*/
		//改进型
		String aNew= String.valueOf(num);
		BigDecimal bd =new  BigDecimal(aNew);
		bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
		return bd.toString();
	}
	public static void main(String[] args) {
		StringBuffer result = new StringBuffer();
		JSONObject errorJson = new JSONObject();
        errorJson.put("responseCode", "0");
        errorJson.put("info", "http请求返回错误!");
        errorJson.put("data", "http请求返回错误!");
        errorJson.put("responseBankCode", "");
        result = new StringBuffer();
        result.append(errorJson.toString());
		JSONObject resultJson = JSONObject.fromObject(result.toString());
		if(!resultJson.containsKey("responseBankCode")){
			resultJson.put("responseBankCode", "");
		}
		if(!resultJson.containsKey("info")){
			resultJson.put("info", "");
		}
		if(!resultJson.containsKey("data")){
			resultJson.put("data", "");
		}
		if(!resultJson.containsKey("responseCode")){
			resultJson.put("responseCode", "0");
		}
		System.out.println(resultJson);
	}
	public synchronized static JSONObject sendPostHF(String url, JSONObject json){
		JSONObject resultJson = new JSONObject();
		//返回的内容
        logger.warn("支付中心地址:"+url);
        logger.warn("支付中心参数:"+json.toString());
		String content="";
		//参数值
		HttpClient httpClient=new HttpClient();
		PostMethod postMethod = new PostMethod(url);
		postMethod.setRequestHeader("Content-Type","application/json;charset=UTF-8");
		//遍历参数
	    postMethod.setRequestBody(json.toString());
		//执行远程连接,并返回结果
	    try {
	    	httpClient.executeMethod(postMethod);
	    	//获得响应内容String格式
			content=postMethod.getResponseBodyAsString();
			resultJson = JSONObject.fromObject(content.toString());
		} catch (IOException e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
            JSONObject errorJson = new JSONObject();
            errorJson.put("responseCode", "0");
            errorJson.put("info", "http请求返回错误!");
            errorJson.put("data", "http请求返回错误!");
            errorJson.put("responseBankCode", "");
            return errorJson;
		}
        logger.warn("支付中心返回:"+content);
        return resultJson;
	}
	
	/**
	 * 功能说明：
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：下午5:33:21
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public synchronized static JSONObject sendPost(String urls, String bocJson)
	{
		StringBuffer buffer = new StringBuffer();
		
		try
		{
//		 String s= "http://192.168.23.54:8182/pay/baofooPayProcess/withhold";
		 URL url = new URL(urls);
		 HttpURLConnection http = (HttpURLConnection) url.openConnection();
		 http.setDoOutput(true);  
		 http.setDoInput(true);  
	     http.setRequestMethod("POST"); 
	     http.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		 http.connect();  
		 OutputStreamWriter out = new OutputStreamWriter(http.getOutputStream(), "UTF-8"); 
		 out.append(bocJson);  
	     out.flush();  
	     out.close();  
	 	 BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream(),"UTF-8"));
		 String line;
		 while ((line = reader.readLine()) != null) {
			 buffer.append(line);
		 }
		 reader.close();
		 http.disconnect();
		}
		catch (Exception e)
		{
			e.printStackTrace();
            logger.warn(e.getMessage(),e);
            JSONObject errorJson = new JSONObject();
            errorJson.put("responseCode", "0");
            errorJson.put("info", "http请求返回错误!");
            errorJson.put("data", "http请求返回错误!");
            errorJson.put("responseBankCode", "");
            buffer = new StringBuffer();
            buffer.append(errorJson.toString());
		}
		if (StringUtil.isEmptyOrNull(buffer.toString()))
		{
			return null;
		}
		JSONObject resultJson = JSONObject.fromObject(buffer.toString());
		if(!resultJson.containsKey("responseBankCode")){
			resultJson.put("responseBankCode", "");
		}
		if(!resultJson.containsKey("info")){
			resultJson.put("info", "");
		}
		if(!resultJson.containsKey("data")){
			resultJson.put("data", "");
		}
		if(!resultJson.containsKey("responseCode")){
			resultJson.put("responseCode", "0");
		}
		return resultJson;
	}
}
