package com.apt.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
public class pocTool {
	private static Logger logger = LoggerFactory.getLogger(pocTool.class);
	/**
	 * 功能说明：连接poc的接口		
	 * yuanhao  2015-5-29
	 * @param method 接口名 paramMap 参数集合 extendUrl 扩展url
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public synchronized static String connectToPoc(String method,Map paramMap) {
		if(paramMap==null){
			paramMap=new HashMap();
		}
		//sysNumber 默认 本系统
		paramMap.put("sysNumber", "erpv3_auto");
		String content="";
		HttpClient httpClient=new HttpClient();
		PostMethod postMethod = new PostMethod(StaticData.pocUrl+method+".html");
		postMethod.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");
		//遍历参数
		if(paramMap!=null){
			if(paramMap.containsKey("amt")){
				paramMap.put("amt", NumberUtil.duelmoney(NumberUtil.parseDouble(paramMap.get("amt"))));
			}
		Set<String>keys=paramMap.keySet();
		NameValuePair[] data = new NameValuePair[keys.size()];
		Iterator it=keys.iterator();
		int i=0;
		while(it.hasNext()){
			String key=String.valueOf(it.next());
			data[i]=new NameValuePair(key,paramMap.get(key).toString());
			i++;
		}
		postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8"); 
	    postMethod.setRequestBody(data);
		}
	    try {
	    	httpClient.executeMethod(postMethod);
	    	//获得响应内容String格式
	    	logger.warn("本次富友操作请求参数:"+JSONObject.fromObject(paramMap));
			content=postMethod.getResponseBodyAsString();
			logger.warn("本次富友操作返回结果:"+content);
			if(method.equals("BalanceAction")){//说明是进行了余额查询
				try{
					JSONObject json=JSONObject.fromObject(content);
					if("yes".equals(json.getString("success"))){//本次请求成功
						return json.getString("ca_balance");
					}else{
						logger.warn("本次富友查询余额失败!错误原因:"+json.getString("message"));
						return "0";
					}
				}catch(Exception e){
					return "0";
				}
				
			}
		} catch (IOException e) {
			logger.warn(e.getMessage(),e);
			e.printStackTrace();
		}
	   return content;
	}
	/**
	 * 功能说明：划拨操作(已经进行了预授权的情况下)	
	 * yuanhao  2015-5-29
	 * @param method 接口名 paramMap 参数集合 extendUrl 扩展url
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String transferBu(Map paramMap) {
		if(paramMap==null){
			paramMap=new HashMap();
		}
		//sysNumber 默认 本系统
		paramMap.put("sysNumber", "erpv3_auto");
		String content="";
		HttpClient httpClient=new HttpClient();
		PostMethod postMethod = new PostMethod(StaticData.pocUrl+"transferBu.html");
		postMethod.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");
		//遍历参数
		if(paramMap!=null){
		Set<String>keys=paramMap.keySet();
		NameValuePair[] data = new NameValuePair[keys.size()];
		Iterator it=keys.iterator();
		int i=0;
		while(it.hasNext()){
			String key=String.valueOf(it.next());
			data[i]=new NameValuePair(key,paramMap.get(key).toString());
			i++;
		}
		postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8"); 
	    postMethod.setRequestBody(data);
		}
	    try {
	    	httpClient.executeMethod(postMethod);
	    	//获得响应内容String格式
			content=postMethod.getResponseBodyAsString();
			logger.warn("划拨-->本次富友操作返回结果:"+content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	   return content;
	}
	/**
	 * 功能说明：撤销预授权操作(已经进行了预授权的情况下)	
	 * yuanhao  2015-5-29
	 * @param method 接口名 paramMap 参数集合 extendUrl 扩展url
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String preAuthCancel(Map paramMap) {
		if(paramMap==null){
			paramMap=new HashMap();
		}
		//sysNumber 默认 本系统
		paramMap.put("sysNumber", "erpv3_auto");
		String content="";
		HttpClient httpClient=new HttpClient();
		PostMethod postMethod = new PostMethod(StaticData.pocUrl+"preAuthCancel.html");
		postMethod.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");
		//遍历参数
		if(paramMap!=null){
			Set<String>keys=paramMap.keySet();
			NameValuePair[] data = new NameValuePair[keys.size()];
			Iterator it=keys.iterator();
			int i=0;
			while(it.hasNext()){
				String key=String.valueOf(it.next());
				data[i]=new NameValuePair(key,paramMap.get(key).toString());
				i++;
			}
			postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8"); 
			postMethod.setRequestBody(data);
		}
		try {
			httpClient.executeMethod(postMethod);
			//获得响应内容String格式
			content=postMethod.getResponseBodyAsString();
			logger.warn("撤销预授权-->本次富友操作返回结果:"+content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	/**
	 * 功能说明：连接poc的接口		
	 * yuanhao  2015-5-29
	 * @param method 接口名 paramMap 参数集合 extendUrl 扩展url
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public synchronized static JSONObject peerTopeer(Map paramMap) {
		JSONObject resultJson = new JSONObject();
		resultJson.put("responseCode", "0");
		resultJson.put("code", "");
		if(StaticData.closePoc){
			resultJson.put("responseCode", "1");
			return resultJson;
			
		}
		if(paramMap.containsKey("amt")){
			paramMap.put("amt", NumberUtil.duelmoney(NumberUtil.parseDouble(paramMap.get("amt"))));
		}
		//金额小于零直接算成功
		if(NumberUtil.parseDouble(paramMap.get("amt"))<=0){
			logger.warn("本次请求金额为0,直接默认成功!");
			resultJson.put("responseCode", "1");
			return resultJson;
		}
		//sysNumber 默认 本系统
		paramMap.put("sysNumber", "erpv3_auto");
		String content="";
		HttpClient httpClient=new HttpClient();
		
		PostMethod postMethod = new PostMethod(StaticData.pocUrl+"preAuth.html");
		postMethod.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");
		//遍历参数
		if(paramMap!=null){
		Set<String>keys=paramMap.keySet();
		NameValuePair[] data = new NameValuePair[keys.size()];
		Iterator it=keys.iterator();
		int i=0;
		while(it.hasNext()){
			String key=String.valueOf(it.next());
			data[i]=new NameValuePair(key,paramMap.get(key).toString());
			i++;
		}
		postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8"); 
	    postMethod.setRequestBody(data);
		}
	    try {
	    	httpClient.executeMethod(postMethod);
	    	//获得响应内容String格式
			content=postMethod.getResponseBodyAsString();
			logger.warn("预授权-->本次富友操作返回结果:"+content);
			try{
			JSONObject json = JSONArray.fromObject(content).getJSONObject(0);
			if(!"0000".equals(json.getString("responseCode"))){
				resultJson.put("code",json.getString("responseCode"));
				return resultJson;
			}
			}catch (Exception e) {
			}
			resultJson.putAll(JSONArray.fromObject(content).getJSONObject(0));
			if("0000".equals(resultJson.getString("responseCode"))){ //预授权成功  ---> 开始进行划拨操作
				String applyId = resultJson.getString("applyId");
				Map transferBuMap =  new HashMap();
				transferBuMap.put("applyId", applyId);
				String transferBuResult=transferBu(transferBuMap);
				if("0000".equals(transferBuResult)){
					resultJson.put("responseCode", "1");
				}else{ //划拨失败了,需要撤销预授权
					resultJson.put("code",transferBuResult);
					resultJson.put("responseCode", "0");
					preAuthCancel(transferBuMap);
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			resultJson.put("code","-9999");
			resultJson.put("responseCode", "0");
		}
	   return resultJson;
	}
	public static void main(String[] args) {
				Map paramMap = new HashMap();
				String content="";
				HttpClient httpClient=new HttpClient();
				List<String> list = new ArrayList<String>();
				//list.add("0102|6222021001040815703|周姣|0.01|16092218220805608|TEST|15001905891");
				//list.add("13275177201|袁浩|0.01|Test");
				//18306135500|苏州中资联信用管理有限公司|0|13601450027|周文祥|1|5235.15|bg_ef_order_id:ff8080815590f6a8015595dfc7387406|
				//list.add("13275177201|袁浩|0|13275177201|袁浩|1|1|Test|");
			    //list.add("15996289503|许舒凯|0|13275177201|袁浩|1|0.01|Test|");
				//list.add("15996289503|许舒凯|0|13275177201|袁浩|1|0.02|Test|");
				//18306135500|苏州中资联信用管理有限公司|0|13601450027|周文祥|1|5235.15|bg_ef_order_id:ff8080815590f6a8015595dfc7387406|
			//	paramMap.put("code", "PW03");
			//	list.add("18306135500|苏州中资联信用管理有限公司|0|13405066190|张翌|1|7803.00|bg_ef_order_id:ff8080815846adaa0158475850e33524|");
			//	list.add("18306135500|苏州中资联信用管理有限公司|0|13951111812|陈林昌|1|1155.39|bg_ef_order_id:ff8080815846adaa0158475850f03526|");
			//	list.add("18306135500|苏州中资联信用管理有限公司|0|18915408843|邹春龙|1|1468.18|bg_ef_order_id:ff8080815846adaa0158475850fb3528|");
			//	list.add("18306135500|苏州中资联信用管理有限公司|0|13995678715|徐延华|1|1245.53|bg_ef_order_id:ff8080815846adaa015847585110352a|");
			//	list.add("18306135500|苏州中资联信用管理有限公司|0|15295131456|卞楠楠|1|1027.43|bg_ef_order_id:ff8080815846adaa01584758511c352c|");
			//	paramMap.put("data", list);
			//	PostMethod postMethod = new PostMethod("http://erp.51bel.com/poc/uploadFilePW03.html"); //设置提现模式测试
				PostMethod postMethod = new PostMethod("http://192.168.23.49:8080/poc/downloadFileByName.html");
			//	postMethod.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");
				//paramMap.put("fileName", "PW13_20160825_0001.txt");
				
				//准备开始进行充值提现的查询
			/*	paramMap.put("busi_tp", "PW11");
				paramMap.put("start_time", "2014-01-01 00:00:00");
				paramMap.put("end_time", "2014-01-02 23:59:59");
				paramMap.put("cust_no", "13275177201");
				paramMap.put("page_no", "1");
				paramMap.put("page_size", "50");
				paramMap.put("txn_st", "1");*/
				//String canshu = "[{\"investAuthCode\":\"0000000000000\",\"seriNo\":\"0000092016101309462837503 \",\"thredPriKey\":\"3_1:1bda3f1ec9314804a6baee215646fc92\"},{\"investAuthCode\":\"0000000000000\",\"seriNo\":\"0000092016101309462826114 \",\"thredPriKey\":\"3_1:64be234d51fb4bd5bb9bcb7599118c82\"},{\"investAuthCode\":\"0000000000000\",\"seriNo\":\"0000092016101309462857160 \",\"thredPriKey\":\"3_1:50bab1fa1dbb4a77ba1cc281342c4949\"},{\"investAuthCode\":\"0000000000000\",\"seriNo\":\"0000092016101309462835854 \",\"thredPriKey\":\"3_1:48559bb2e03b4959b75f478603134682\"},{\"investAuthCode\":\"0000000000000\",\"seriNo\":\"0000092016101309462837741 \",\"thredPriKey\":\"3_1:070744193889457da3cda52ad1a38a8b\"},{\"investAuthCode\":\"0000000000000\",\"seriNo\":\"0000092016101309462836996 \",\"thredPriKey\":\"3_1:4f2d986a7c8646ce9063b4344d28e010\"}]";
				//String canshu2 = "[{\"investAuthCode\":\"0000000000000\",\"seriNo\":\"0000092016101309462837503 \",\"thredPriKey\":\"3_1:465b2e771f224d24b4f62f2afa4adb09\"},{\"investAuthCode\":\"0000000000000\",\"seriNo\":\"0000092016101309462826114 \",\"thredPriKey\":\"3_1:64be234d51fb4bd5bb9bcb7599118c82\"},{\"investAuthCode\":\"0000000000000\",\"seriNo\":\"0000092016101309462857160 \",\"thredPriKey\":\"3_1:50bab1fa1dbb4a77ba1cc281342c4949\"},{\"investAuthCode\":\"0000000000000\",\"seriNo\":\"0000092016101309462835854 \",\"thredPriKey\":\"3_1:48559bb2e03b4959b75f478603134682\"},{\"investAuthCode\":\"0000000000000\",\"seriNo\":\"0000092016101309462837741 \",\"thredPriKey\":\"3_1:070744193889457da3cda52ad1a38a8b\"},{\"investAuthCode\":\"0000000000000\",\"seriNo\":\"0000092016101309462836996 \",\"thredPriKey\":\"3_1:4f2d986a7c8646ce9063b4344d28e010\"}]";
				//paramMap.put("authCodes", canshu);
				//paramMap.put("authCodes", canshu2);
				if(paramMap!=null){
					Set<String>keys=paramMap.keySet();
					NameValuePair[] data = new NameValuePair[keys.size()];
					Iterator it=keys.iterator();
					int i=0;
					while(it.hasNext()){
						String key=String.valueOf(it.next());
						data[i]=new NameValuePair(key,paramMap.get(key).toString());
						i++;
					}
					postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8"); 
					postMethod.setRequestBody(data);
				}
				try {
					httpClient.executeMethod(postMethod);
					//获得响应内容String格式
					content=postMethod.getResponseBodyAsString();
					logger.warn(content);
					System.out.println(content);
				} catch (IOException e) {
					e.printStackTrace();
				}
	}
	public void fixData(){
		/*String[] fyAccounts = {};
		String[] custNames={};
		String[] moneys={};
		String[] bg_ef_order_ids={};
		String[] bg_ef_paycontrol_ids={};
		String[] uniques={};
		String[] types={};
		List<String> uniqueList = new ArrayList<String>();//找出唯一编号
		for(String uniqueString:uniques){
			if(uniqueList.size()==0){
				uniqueList.add(uniqueString);
				continue;
			}
			for(int i=0;i< uniqueList.size();i++){
				String baseString  = uniqueList.get(i);
				if(uniqueString.equals(baseString)){
					continue;
				}
				if(i==(uniqueList.size()-1)){ // 未找到 添加
					uniqueList.add(uniqueString);
				}
			}
		}
		//开始进行处理
		for(String unique:uniqueList){
			//先找出对应的位置
			List<Integer> indexs = new ArrayList<Integer>(); 
			for(int i=0;i<uniques.length;i++){ //开始查找
				String uniqueString =  uniques[i];
				if(uniqueString.equals(unique)){ //找到了一个位置
					indexs.add(i);
					continue;
				}
			}
			List<String> finalList = new ArrayList<String>();
			if(ListTool.isNotNullOrEmpty(indexs)){ //开始查找对应的数据进行处理
				//开始真正的处理
				for(int i=0;i<indexs.size();i++){
					String finalString="18306135500|苏州中资联信用管理有限公司|0|";
					Integer index=indexs.get(i);
					String fy = fyAccounts[index];
					String custName = custNames[index];
					String money = moneys[index];
					String bg_ef_order_id = bg_ef_order_ids[index];
					String bg_ef_paycontrol_id = bg_ef_paycontrol_ids[index];
					finalString+=fy+"|";
					finalString+=custName+"|1|";
					finalString+=money+"|";
				    String type= types[index];
					if("-1".equals(type)){ //提前结清
						finalString+="bg_ef_order_id:"+bg_ef_order_id+"|";
					}
					if("1".equals(type)){ //正常还款
						finalString+="bg_ef_paycontrol_id:"+bg_ef_paycontrol_id+"|";
					}
					finalList.add(finalString);
				}
				
				paramMap.put("code", "PW03");
				paramMap.put("data", finalList);
				PostMethod postMethod = new PostMethod("http://localhost:8888/poc/uploadFilePW03.html");
				postMethod.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");
				if(paramMap!=null){
						Set<String>keys=paramMap.keySet();
						NameValuePair[] data = new NameValuePair[keys.size()];
						Iterator it=keys.iterator();
						int i=0;
						while(it.hasNext()){
							String key=String.valueOf(it.next());
							data[i]=new NameValuePair(key,paramMap.get(key).toString());
							i++;
						}
						postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8"); 
						postMethod.setRequestBody(data);
					}
					try {
						httpClient.executeMethod(postMethod);
						//获得响应内容String格式
						content=postMethod.getResponseBodyAsString();
						logger.warn(content);
						System.out.println(content);
					} catch (IOException e) {
						e.printStackTrace();
					}
				System.out.println("UPDATE  poc_batch_record set process_result='1' ,process='2',transferbu_file_name='"+JSONObject.fromObject(content).getString("fileName")+"' where unique_no='"+unique+"';");
			}
			
		}*/
	}
}
