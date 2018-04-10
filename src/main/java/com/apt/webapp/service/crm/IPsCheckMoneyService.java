package com.apt.webapp.service.crm;

import javax.jws.WebService;

import net.sf.json.JSONObject;

/**
 * 功能说明：  还款记录   接口
 * @author yuanhao
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-13
 * Copyright zzl-apt
 */
@WebService
public interface IPsCheckMoneyService {
	/**
	 * 功能说明：保存
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 yuanhao
	 * 创建日期：2016年3月14日 11:12:54
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public String save(JSONObject json);
	/**
	 * 功能说明：更新
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 yuanhao
	 * 创建日期：2016年3月14日 11:12:54
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void updateStatusById(String id);
}
