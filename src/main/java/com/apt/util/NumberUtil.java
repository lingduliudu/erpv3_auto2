package com.apt.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import com.apt.util.arith.ArithUtil;

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
public class NumberUtil {
	/**
	 * 
	 * 功能说明：object  to  double
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static double parseDouble(Object obj){
		if(obj == null ||"".equals(obj) ){
			obj="0";
		}
		String o = obj.toString();
		return Double.parseDouble(o);
	}
	/**
	 * 
	 * 功能说明：object  to  int
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static int parseInteger(Object obj){
		String o = obj.toString();
		return Integer.parseInt(o);
	}
	/**
	 * 
	 * 功能说明：格式化金额  元to分
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String duelmoney(double money) {
		double baseAmt= ArithUtil.mul(money, 100);
		DecimalFormat  df=new DecimalFormat("0");
		df.setRoundingMode(RoundingMode.HALF_UP);
		String amt=df.format(baseAmt);
		return amt;
	}
	/**
	 * 
	 * 功能说明：格式化金额  元to分
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String duelmoney(String money) {
		double baseAmt= ArithUtil.mul(Double.parseDouble(money), 100);
		DecimalFormat  df=new DecimalFormat("0");
		df.setRoundingMode(RoundingMode.HALF_UP);
		String amt=df.format(baseAmt);
		return amt;
	}

}
