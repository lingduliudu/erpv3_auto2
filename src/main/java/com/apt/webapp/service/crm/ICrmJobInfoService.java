package com.apt.webapp.service.crm;

import javax.jws.WebService;




/**
 * 类功能说明：v3工作信息表Service接口
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
public interface ICrmJobInfoService {

	
	/**
	 * 
	 * 功能说明：saveOrUpdateJobInfo 新增/修改用户工作信息表   
	 * chuanqi 2015-10-12
	 * @param {"bgJobInfo":"用户工作信息实体对象"} 
	 * @param signature 验签标识
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public String saveOrUpdateJobInfo(String bgJobInfo, String signature);  
	
	 
	
	/**
	 * 
	 * 功能说明：getJobInfo 根据条件获取用户工作信息信息 
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
	public String getJobInfo(String paramJson, String signature);  
	
	
	
	
	
	
	
	
	
}
