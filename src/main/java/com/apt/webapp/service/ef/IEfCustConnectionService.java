package com.apt.webapp.service.ef;


import javax.jws.WebService;

/**
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明
 * @author liangyanming
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-12
 * Copyright zzl-apt
 */
@WebService
public interface IEfCustConnectionService  {

	/**
	  *
	  * 功能说明：查询EF理财客户集合
	  * liangyanming  2015-10-27
	  * @param 
	  * @return   
	  * @throws 
	  * 最后修改时间：最后修改时间
	  * 修改人：
	  * 修改内容：
	  * 修改注意点：
	  */
	public String addOrUpdateEfCustConnection(String paramJson,String signature) throws Exception;
	
	/**
	  *
	  * 功能说明：查询单个客户对象
	  * lym  2015-10-12
	  * @param 
	  * @return   
	  * @throws 
	  * 最后修改时间：最后修改时间
	  * 修改人：
	  * 修改内容：
	  * 修改注意点：
	  */
	public String getEfCustConnection(String paramMap,String signature) throws Exception;
}
