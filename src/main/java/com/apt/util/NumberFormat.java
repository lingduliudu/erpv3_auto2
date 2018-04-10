package com.apt.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 
 * 功能说明:用来处理Number
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
public class NumberFormat {
	/**
	 * 功能说明：格式化double
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String formatDouble(double num){
		/*DecimalFormat  df=new DecimalFormat("0.00");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(num);*/
		//改进型
		String aNew= String.valueOf(num);
//		BigDecimal bd =new  BigDecimal(aNew);
		BigDecimal bd =BigDecimal.valueOf(num);
		bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
		return bd.toString();
	}
	/**
	 * 功能说明：格式化double
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static double format(double num){
		//DecimalFormat  df=new DecimalFormat("0.00");
		//df.setRoundingMode(RoundingMode.HALF_UP);
		//return Double.parseDouble(df.format(num));
		//改进型
		String aNew= String.valueOf(num);
		BigDecimal bd =new  BigDecimal(aNew);
		bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
	/**
	 * 功能说明：格式化double
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static double format(Object num){
		//DecimalFormat  df=new DecimalFormat("0.00");
		//df.setRoundingMode(RoundingMode.HALF_UP);
		//return Double.parseDouble(df.format(num));
		//改进型
		if(num == null || "".equals(num.toString())){
			return 0d;
		}
		String aNew= String.valueOf(num);
		BigDecimal bd =new  BigDecimal(aNew);
		bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
	/**
	 * 功能说明：格式化double
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String formatDouble(double num,String style){
		DecimalFormat  df=new DecimalFormat(style);
		return df.format(num);
	}
	/**
	 * 功能说明：格式化Object
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String formatDouble(Object num){
		/*DecimalFormat  df=new DecimalFormat("0.00");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(num);*/
		//改进型
		String aNew= String.valueOf(num);
		BigDecimal bd =new  BigDecimal(aNew);
		bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
		return bd.toString();
	}
	/**
	 * 功能说明：格式化Object
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String formatLong6(Object num){
		DecimalFormat  df=new DecimalFormat("000000");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(num);
	}
}
