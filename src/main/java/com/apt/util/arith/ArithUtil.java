package com.apt.util.arith;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.apt.util.NumberFormat;

/**
 * 
 * 功能说明：提供一些精确的运算
 * 典型用法：
 * 特殊用法：
 * @author panye
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-5-13
 * Copyright zzl-apt
 */
public class ArithUtil {
	//默认除法运算精度
	private static final int DEF_DIV_SCALE =2;
	
	/**
	 * 
	 * 功能说明：提供精确的加法运算。			
	 * panye  2015-5-13
	 * @param 
	 * @return   double 
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static double add(double v1,double v2){
		
		if(v1 == 0 && v2 == 0){return 0.0;}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}
	/**
	 * 
	 * 功能说明：提供精确的加法运算。			
	 * panye  2015-5-13
	 * @param 
	 * @return   double 
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static double add(Double[] ints){
		
		double sum = 0d;
		if(ints == null || ints.length ==0){
			return sum;
		}
		for(Double temp:ints){
			BigDecimal b1 = new BigDecimal(Double.toString(temp));
			BigDecimal b2 = new BigDecimal(Double.toString(sum));
			sum = b1.add(b2).doubleValue();
		}
		
		return sum;
	}
	
	/**
	 * 
	 * 功能说明：提供精确的加法运算。(参数数组里的值可以是null)			
	 * panye  2015-5-13
	 * @param 
	 * @return   double 
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static double addHaveNull(Double[] ints){
		
		double sum = 0d;
		if(ints == null || ints.length ==0){
			return sum;
		}
		for(Double temp:ints){
			if(temp==null){
				continue;
			}
			BigDecimal b1 = new BigDecimal(Double.toString(temp));
			BigDecimal b2 = new BigDecimal(Double.toString(sum));
			sum = b1.add(b2).doubleValue();
		}
		
		return sum;
	}
	
	/**
	 * 
	 * 功能说明：提供精确的减法运算。			
	 * panye  2015-5-13
	 * @param  v1 减数 v2 被减数
	 * @return   double 
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static double sub(double v1,double v2){
		
		if(v1 == 0 && v2 == 0){return 0.0;}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}
	
	
	/**
	 * 
	 * 功能说明：提供精确的乘法运算。			
	 * panye  2015-5-13
	 * @param  
	 * @return   double 
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static double mul(double v1,double v2){
		
		if(v1 == 0 && v2 == 0){return 0.0;}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}
	
	
	/**
	 * 
	 * 功能说明：提供精确的除法运算	当发生除不尽的情况时，精确到2数小数。	
	 * panye  2015-5-13
	 * @param  v1 被除数 v2 除数
	 * @return   double 
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static double div(double v1,double v2){
		if(v1 == 0 && v2 == 0){return 0.0;}
		return div(v1,v2,DEF_DIV_SCALE);
	}
	
	
	
	/**
	 * 
	 * 功能说明：提供（相对）精确的除法运算。当发生除不尽的情况时
	 * panye  2015-5-13
	 * @param  v1 被除数 v2 除数 scale 表示表示需要精确到小数点以后几位。
	 * @return   double 
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static double div(double v1,double v2,int scale){
		
		if(v1 == 0 && v2 == 0){return 0.0;}
		if(scale<0){
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
			}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	
	/**
	 * 
	 * 功能说明：提供精确的小数位四舍五入处理。
	 * panye  2015-5-13
	 * @param  v被处理的数据  scale 小数点后保留几位
	 * @return  double 
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static double round(double v,int scale){
		
		if(scale<0){
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	
	/**
	 * 
	 * 功能说明：截取整数位
	 * panye  2015-5-13
	 * @param  num double类型
	 * @return  int 
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static int getInteg(double num){
		String s=round(num,2)+"";
		int index=s.indexOf('.');
		String l=s.substring(0,index);
		return Integer.parseInt(l);
	}
	
	
	
	/**
	 * 
	 * 功能说明：将无转换成分			
	 * panye  2015-5-13
	 * @param 
	 * @return   String 转换后的分的字符串
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String toPenny(double money) {
		
		String amt = mul(money, 100) + "";
		BigDecimal decimal = new BigDecimal(amt);
		amt = decimal.toPlainString();
		int index = amt.lastIndexOf(".");
		if (index > 0) {
			amt = amt.substring(0, index);
		}
		return amt;
	}
	
	
	/**
	 * 	
	 * 功能说明：将DOUBLE转换成字符串		
	 * panye  2015-5-13
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static  String formatDouble(double data){
		/*DecimalFormat df=new DecimalFormat("0.00");
		return df.format(data);*/
		//改进型
		String aNew= String.valueOf(data);
		BigDecimal bd =new  BigDecimal(aNew);
		bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
		return bd.toString();
	}
	
	
	/**
	 * 功能说明：四舍五入
	 * yuanhao  2015-6-19
	 * @param
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	*/
	public static double getDouble(double amt){
		BigDecimal b =BigDecimal.valueOf(amt); 
		return  b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
