package com.apt.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apt.util.SysConfig;

/**
 * 功能说明：远程接口连接工具类
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明	
 * @author S
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-5-25
 * Copyright zzl-apt
 */
public class HttpUtil {
	//日志
	private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	//导入系统配置文件
	private static SysConfig sysConfig = SysConfig.getInstance();
	/**
	 * 功能说明：鹏远接口的认证		
	 * yuanhao  2015-5-29
	 * @param  
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String connectPyVerify(String url,Map<String,Object> paramsMap) {
		//返回的内容
		String content="";
		//参数值
		HttpClient httpClient=new HttpClient();
		PostMethod postMethod = new PostMethod(sysConfig.getProperty("PengYuanUrl")+url+".html");
		postMethod.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");
		//遍历参数
		if(paramsMap!=null){
		Set<String>keys=paramsMap.keySet();
		NameValuePair[] data = new NameValuePair[keys.size()];
		Iterator it=keys.iterator();
		int i=0;
		while(it.hasNext()){
			String key=String.valueOf(it.next());
			data[i]=new NameValuePair(key,paramsMap.get(key).toString());
			i++;
		}
	    postMethod.setRequestBody(data)  ;
		}
		//执行远程连接,并返回结果
	    try {
	    	httpClient.executeMethod(postMethod);
	    	//获得响应内容String格式
			content=postMethod.getResponseBodyAsString();
		} catch (IOException e) {
			e.printStackTrace();
		}
	   return content;
	}
	
	public static JSONObject sendPost(String url,Object[] params) {  
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
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
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
        } catch (Exception e) {  
            e.printStackTrace();  
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
        System.out.println(result.toString());
        return null;// JSONObject.fromObject(result.toString());
    } 
	/**
	 * 功能说明：接口的认证		
	 * yuanhao  2015-5-29
	 * @param  
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public synchronized static String connectByUrl(String url,Map<String,Object> paramsMap,boolean pathFlag) {
		//返回的内容
		String content="";
		String pathUrl ="";
		//参数值
		if(pathFlag){
			if(paramsMap !=null ){
				for(String key:paramsMap.keySet()){
					pathUrl+=key+"="+paramsMap.get(key)+"&";
				}
			}
		}
		
		HttpClient httpClient=new HttpClient();
		PostMethod postMethod = new PostMethod(url+"?"+pathUrl);
		postMethod.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");
		//遍历参数
		if(!pathFlag){
			if(paramsMap!=null){
				Set<String>keys=paramsMap.keySet();
				NameValuePair[] data = new NameValuePair[keys.size()];
				Iterator it=keys.iterator();
				int i=0;
				while(it.hasNext()){
					String key=String.valueOf(it.next());
					data[i]=new NameValuePair(key,paramsMap.get(key).toString());
					i++;
				}
				postMethod.setRequestBody(data)  ;
			}
		}
		//执行远程连接,并返回结果
		try {
			httpClient.executeMethod(postMethod);
			//获得响应内容String格式
			content=postMethod.getResponseBodyAsString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	/**
	 * 功能说明：接口的认证		
	 * yuanhao  2015-5-29
	 * @param  
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public synchronized static String connectByUrl(String url,Map<String,Object> paramsMap) {
		return connectByUrl(url, paramsMap,false);
	}
	
	
	
	public static void main(String[] args) {
		//返回的内容
				String content="";
				//参数值
				HttpClient httpClient=new HttpClient();
				httpClient.getHostConfiguration().setProxy("127.0.0.1", 8888);
				PostMethod postMethod = new PostMethod("http://192.168.23.39:9656/api/newboc/v1/p2p/autoinvest/transaction");
				postMethod.setRequestHeader("Content-Type","application/json;charset=utf-8");
				String jsonString = "{"
						   +" 'cardNbr': '6212461030000010862', "
						   +" 'product': '123456', "
						   +" 'amount': '100',"
						   +" 'authCode': '58022016063002170214933',"
						   +" 'intDate': '20160628',"
						   +" 'intType': '1',"
						   +" 'intPayDay': '12',"
						   +" 'endDate': '20170628',"
						   +" 'yield': '14.5',"
						   +" 'frzFlag': '1',"
						   +" 'remark': '1'"
						   +"}";
			    postMethod.setRequestBody(jsonString)  ;
				//执行远程连接,并返回结果
			    try {
			    	httpClient.executeMethod(postMethod);
			    	//获得响应内容String格式
					content=postMethod.getResponseBodyAsString();
				} catch (IOException e) {
					e.printStackTrace();
				}
			   System.out.println(content); 
	}
}
