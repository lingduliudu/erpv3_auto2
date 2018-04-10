package com.apt.webapp.service.crm;

import javax.jws.WebService;


/**
 * 功能说明：  审批材料类型表 service层   接口
 * @author weiyingni
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-13
 * Copyright zzl-apt
 */
@WebService
public interface ICrmPapertypeService {

	/**
	  *
	  * 功能说明：获取审批材料类型
	  * weiyingni  2015-10-19
	  * @param 
	  * @return   
	  * @throws Exception 
	  * @throws 
	  * 最后修改时间：最后修改时间
	  * 修改人：admin
	  * 修改内容：
	  * 修改注意点：
	  */
	public String getCrmPapertype(String paramJson, String signature);
	
	/**
	 * 功能说明：查询素材 tree	
	 * @author xsk
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2015-10-21
	 * 修改内容：
	 * 修改注意点：
	 */
	public String getPapersTree(String signature) throws Exception;
}
