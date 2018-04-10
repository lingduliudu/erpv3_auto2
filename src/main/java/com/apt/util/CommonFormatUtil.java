package com.apt.util;
/**
 * 
 * 功能说明:通用格式化
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
public class CommonFormatUtil {
	/**
	 * 
	 * 功能说明：格式化身份证		
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String formatIc(Object obj){
		if(ChkUtil.isEmpty(obj) || "NULL".equals(String.valueOf(obj)) || "null".equals(String.valueOf(obj))){
			return "";
		}
		String ic = obj.toString();
		if(ic.length()<=3){
			return ic + "****" + ic;
		}
		if(ic.length()==4){
			return ic.substring(0, 3)+"****"+ic;
		}
		String preIc = ic.substring(0,3);
		String sufIc = ic.substring(ic.length()-4,ic.length());
		
		return preIc+"****"+sufIc;
	}
	/**
	 * 
	 * 功能说明：格式化手机		
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String formatMobile(Object obj){
		return formatIc(obj);
	}
	/**
	 * 
	 * 功能说明：格式化还款方式		
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String formatPayMent(Object obj){
		if(ChkUtil.isEmpty(obj)){
			return "";
		}
		String payMent =  obj.toString();
		if("1".equals(payMent)){
			payMent = "一次性还本付息";
		}
		if("2".equals(payMent)){
			payMent = "月付息到期还本";
		}
		if("3".equals(payMent)){
			payMent = "分期等额";
		}
		if("4".equals(payMent)){
			payMent = "等额本息";
		}
		if("5".equals(payMent)){
			payMent = "等额本金";
		}
		return payMent;
	}
	
	/**
	 * 功能说明：格式化数组,将数组格式化为'a','b','c'类似的字符串方便查询			
	 * feixinwei  2015-10-27
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * 修改人：Administrator
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String formatArray(String[] array){
		String resultString = "";
		for (int i = 0; i < array.length; i++) {
			if (i==0) {
				resultString = "'"+array[i]+"'";
			} else {
				resultString += ",'"+array[i]+"'";
			}
		}
		return resultString;
	}
}
