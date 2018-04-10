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
public interface IEfLinkmanInfoService  {

	/**
	  *
	  * 功能说明：添加或修改意向客户联系人信息
	  * liangyanming  2015-10-27
	  * @param 
	  * @return   
	  * @throws 
	  * 最后修改时间：最后修改时间
	  * 修改人：
	  * 修改内容：
	  * 修改注意点：
	  */
	public String addOrUpdateIEfLinkmanInfo(String paramJson,String signature) throws Exception;

}
