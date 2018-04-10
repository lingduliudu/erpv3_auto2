package com.apt.webapp.service.crm;

import javax.jws.WebService;

/**
 * 类功能说明：v3 联系人信息表Service接口
 * 典型用法：
 * @author chuanqi
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-12
 * Copyright zzl-apt
 */
@WebService
public interface ICrmLinkmanInfoService {

	/**
	 * 
	 * 功能说明：saveOrUpdateLinkmanInfo 新增/修改用户联系人信息表   
	 * chuanqi 2015-10-12
	 * @param {"bgLinkmanInfo":"用户联系人信息实体对象"} 
	 * @param signature 验签标识
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public String saveOrUpdateLinkmanInfo(String bgLinkmanInfo, String signature);  
	
	
	 
	
	
	/**
	 * 
	 * 功能说明：getLinkmanInfo 根据条件获取用户联系人信息信息 
	 * chuanqi 2015-10-12
	 * @param paramJson{"id":"用户工作信息表ID" ,"custInfoId":"关联客户基本资料表"...}
	 * @param signature 验签标识
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public String getLinkmanInfo(String paramJson, String signature);  
	
}
