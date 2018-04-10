package com.apt.util;

public  class IcUtil {
	/**
	 * 从身份证中获取生日
	 * @param ic
	 * @return
	 */
	public static String getBirthbyIc(String ic){
		String year=ic.substring(6, 10);
		String month =ic.substring(10, 12);
		String day=ic.substring(12, 14);
		return year+"-"+month+"-"+day;
	}
	
	/**
	 * 从身份证中获取性别
	 * @param ic
	 * @return
	 */
	public static int getSexbyic(String ic){
		int gender=0;
		int sex=0;
		gender=Integer.parseInt(ic.substring(16,17));
		if(gender%2==0){
			sex=2;
		}else{
			sex=1;
		}
		return sex ;
	}
}
