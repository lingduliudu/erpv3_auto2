package com.apt.util;

import com.apt.util.arith.ArithUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * 功能说明:用来处理Json
 * 典型用法：
 * 特殊用法：
 * @author yuanhao
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-6-10
 * Copyright zzl-apt
 */
public class JsonUtil {
	/**
	 * 
	 * 功能说明：获得json的字符串
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String  getString(JSONObject json ) {
		if(json != null){
			return json.toString();
		}
		return "";
	}
	/**
	 * 
	 * 功能说明：解析jsonArray的并获得其中一个的值
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static double  getSum(JSONArray jsonArray ,String key) {
		double sum=0d;
		if(jsonArray == null ||jsonArray.size() ==0){
			return 0;
		}
		for(Object json:jsonArray){
			JSONObject j = (JSONObject) json;
			sum = ArithUtil.add(sum, j.getDouble(key));
		}
		return sum;
	}
	/**
	 * 
	 * 功能说明：格式化json的数值 remove 中的不预处理
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static void format(JSONObject json,JSONObject remove){
		if(json !=null){
			for(Object key:json.keySet()){
				String str = key.toString();
				if(remove.containsKey(key.toString())){
					continue;
				}
				try{
					double value = json.getDouble(str);
					json.put(str, NumberFormat.format(value));
				}catch (Exception e) {
					continue;
				}
			}
		}
	}
}
