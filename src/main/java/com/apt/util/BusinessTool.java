package com.apt.util;
/**
 * 业务通用类
 * 功能说明：该方法实现的功能			
 * @author yuanhao
 * @param 方法里面接收的参数及其含义
 * @return 该方法的返回值的类型，含义   
 * @throws  该方法可能抛出的异常，异常的类型、含义。
 * 最后修改时间：最后修改时间
 * date: 2016-9-1
 * 修改内容：
 * 修改注意点：
 */
public class BusinessTool {

	/**
	 * 判断是否是专业投资人
	 * 功能说明：该方法实现的功能			
	 * @author yuanhao
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2016-9-1
	 * 修改内容：
	 * 修改注意点：
	 */
	public static boolean isProfessionalInvestor(Object obj){
		boolean flag = false;
		if(ChkUtil.isNotEmpty(obj) && StaticData.PROFESSIONAL_INVESTOR.equals(obj.toString())){
			flag = true;
		}
		return flag;
	}
	/**
	 * 判断是否是专业投资人
	 * 功能说明：该方法实现的功能			
	 * @author yuanhao
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2016-9-1
	 * 修改内容：
	 * 修改注意点：
	 */
	public static boolean isNotProfessionalInvestor(Object obj){
		return  !isProfessionalInvestor(obj);
	}
}
