package com.apt.webapp.service.ef;


import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.transaction.annotation.Transactional;


/**
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明
 * @author yuanhao
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-12
 * Copyright zzl-apt
 */
@WebService
@Transactional
public interface IEfPayrecordService  {
	/**
	 * 功能说明： 获得线下当日待还明细
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 袁浩
	 * 创建日期：2015年10月20日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	@Transactional
	public void addNormal(Map efPaycontrol);
	/**
	 * 功能说明：获得理财记录
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 袁浩
	 * 创建日期：2015年10月20日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	@Transactional
	public List<Map> findListByEfOrderId(String id);
	
}
