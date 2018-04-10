package com.apt.util;

import java.lang.reflect.Method;

/**
 * 
 * 功能说明:用来处理一个对象
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
public class ObjectUtil {
	
	/**
	 * 
	 * 功能说明：转成update语句 通过id更新	
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String getUpdateSql(Object clazz) {
		//基础的update语句
		String className = clazz.getClass().getName();
		String baseSql="update "+className.substring(className.lastIndexOf(".")+1)+" set ";
		//更新条件
		String whereSql="";
		//获得所有的方法
		Method[] methods = clazz.getClass().getMethods();
		//循环method 得到名称 拼成update 语句
		for(Method meth:methods){
			String name=meth.getName();
			if(!name.startsWith("get")||name.equals("getClass")){
				continue;
			}
			if(name.toLowerCase().equals("getid")){
				Object obj = null;
				try {
					obj = meth.invoke(clazz, null);
					if(ChkUtil.isEmpty(obj)||String.valueOf(obj).equals("0")){
						return null;
					}
					whereSql +=" where id = "+String.valueOf(obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				try {
					Object obj = meth.invoke(clazz, null);
					if(ChkUtil.isEmpty(obj)){
						continue;
					}
					baseSql+=" "+StringUtil.properName(name) +" = '"+String.valueOf(obj)+"' ,";
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}
		baseSql = StringUtil.removeLastChar(baseSql);
		
		return baseSql + whereSql;
	} 
}
