package com.apt.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.apt.webapp.service.auto.TestData;

import net.sf.json.JSONObject;

/**
 * 
 * 功能说明:用来处理List
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
public class ListTool {
	
	/**
	 * 功能说明：判断空
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static boolean isNullOrEmpty(List list){
		if(list == null || list.size()==0 ){
			return true;
		}
		return false;
	}
	/**
	 * 功能说明：判断非空
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static boolean isNotNullOrEmpty(List list){
		return !isNullOrEmpty(list);
	}
	/**
	 * 功能说明：获得总数值
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static double getSumDouble(List<Map> list ,String key){
		//用来返回的总值
		double sum=0d;
		//若为空则直接返回
		if(isNullOrEmpty(list)){return sum;}
		for(Map map:list){
			double temp = 0d;
			try{
				temp = Double.parseDouble(map.get(key).toString());
			}catch (Exception e) {
				continue;
			}
			sum += temp;
		}
		sum = NumberUtil.parseDouble(new DecimalFormat("0.00").format(sum));
		return sum;	
	}
	/**
	 * 功能说明：拆分list
	 * yuanhao  2015-6-16
	 * @param list 需要拆分的list  size 每个长度
	 * @return 到第几个数据   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception 
	 */
	public static synchronized List getList(Object obj){
		List<String> list = new ArrayList<String>();
		if(obj==null){
			return list;
		}
		String orginStr =obj.toString();
		while(true){
			int begin=orginStr.indexOf("[");
			int end=orginStr.indexOf("]");
			if(begin!=-1&& end!=-1){
				String  currStr = orginStr.substring(begin+1,end);
				String[] currStrs = currStr.split(Pattern.quote(","));
				if(currStrs!=null&& currStrs.length>0){
					for(String str:currStrs){
						list.add(str.trim().replaceAll("\"", ""));
					}
				}
				//list.add(orginStr.substring(begin+1,end));
				orginStr = orginStr.substring(end+1);
			}else{
				break;
			}
		}
		return list;
	}
	public static void main(String[] args) {
		System.out.println(getList(JSONObject.fromObject(TestData.preFile).get("data")).size());
		
	}
}
