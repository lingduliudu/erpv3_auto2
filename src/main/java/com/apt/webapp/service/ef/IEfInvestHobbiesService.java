package com.apt.webapp.service.ef;


import javax.jws.WebService;

/**
 * 功能说明：v3的投资偏好表      service层 实现类
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明
 * @author lym
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-29
 * Copyright zzl-apt
 */
@WebService
public interface IEfInvestHobbiesService  {
	/**
	 * 功能说明：saveOrUpdateCustInfo 添加或修改：v3的投资偏好信息
	 * liangyanming  2015-10-12
	 * @param {class}投资偏好信息实体对象
	 * @param 验签
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public String saveOrUpdateInvestHobbies(String investHobbiesInfo, String signature);
	
}
