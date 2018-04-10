package com.apt.webapp.service.ef;

import javax.jws.WebService;


/**
 * 功能说明：  合同扫描件素材 service层   接口
 * @author weiyingni
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-27
 * Copyright zzl-apt
 */
@WebService
public interface IEfContractPapersService {

	/**
	  *
	  * 功能说明：添加审批材料
	  * weiyingni  2015-10-19
	  * @param paramJson {class}
	  * @return   
	  * @throws Exception 
	  * @throws 
	  * 最后修改时间：最后修改时间
	  * 修改人：admin
	  * 修改内容：
	  * 修改注意点：
	  */
	public String addOrUpdateEfContractPapers(String paramJson, String signature);
	
	/**
	  *
	  * 功能说明：删除审批材料
	  * weiyingni  2015-10-19
	  * @param paperId
	  * @return   
	  * @throws Exception 
	  * @throws 
	  * 最后修改时间：最后修改时间
	  * 修改人：admin
	  * 修改内容：
	  * 修改注意点：
	  */
	public String delContractPaper(String paperId, String signature);
}
