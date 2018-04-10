package com.apt.webapp.service.crm;

import javax.jws.WebService;


/**
 * 类功能说明：用户资产信息表Service接口
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
public interface ICrmAssetsService {

	
	/**
	 * 
	 * 功能说明：saveOrUpdateAssets 新增/修改用户资产信息    
	 * chuanqi 2015-10-12
	 * @param {"bgAssets":"用户资产信息实体对象"} 
	 * @param signature 验签标识
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public String saveOrUpdateAssets(String bgAssets, String signature);  
	
	
	 
	
	
	
	/**
	 * 
	 * 功能说明：getAssets 根据条件获取用户资产信息 
	 * chuanqi 2015-10-12
	 * @param paramJson{"id":"用户资产信息表ID" ,"custInfoId":"关联客户基本资料表"...}
	 * @param signature 验签标识
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public String getAssets(String paramJson, String signature);  
	
	
	
	
}
