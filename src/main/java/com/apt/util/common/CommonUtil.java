package com.apt.util.common;

import net.sf.json.JSONObject;

/**
 * 
 * 功能说明：通用
 * 典型用法：
 * 特殊用法：
 * @author yuanhao
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-5-13
 * Copyright zzl-apt
 */
public class CommonUtil {
	/**
	 * 
	 * 功能说明：格式化sql语句	
	 * yuanhao  2015-5-13
	 * @param 
	 * @return   double 
	 * @throws  
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String extendsSql(String sql ,JSONObject paramsJson){
		if(paramsJson !=null && !paramsJson.isEmpty()){
			for(Object key:paramsJson.keySet()){
				String keyString = key.toString();
				String value = paramsJson.getString(keyString);
				if(value == ""){continue;}
				if(keyString.endsWith("id")||keyString.endsWith("Id")){
					sql+=" and "+keyString+" ='"+value+"'";
					continue;
				}
				if(keyString.endsWith("btime")||keyString.endsWith("Btime")){
					
					keyString =keyString.replace("btime","");
					keyString =keyString.replace("Btime","");
					sql+=" and "+keyString+" >='"+value+" 00:00:00'";
					continue;
				}
				if(keyString.endsWith("etime")||keyString.endsWith("Etime")){
					keyString =keyString.replace("etime","");
					keyString =keyString.replace("Etime","");
					sql+=" and "+keyString+" <='"+value+" 23:59:59'";
					continue;
				}
				sql+=" and "+keyString+" like '%"+value+"%'";
			}
		}
		return sql;
	}
	public static void main(String[] args) {
	}
}
