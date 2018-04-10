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
public interface ICrmCustConnectionService {
	
	
	/**
	 * 功能说明：根据业务员id查询旗下所有客户的基础信息			
	 * feixinwei  2015-10-21
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * 修改人：Administrator
	 * 修改内容：
	 * 修改注意点：
	 */
	public String findByEmpId(String columnName,int pageNo, int pageSize, String paramMap) throws Exception ;
	
	
	/**
	 * 功能说明：saveOrUpdateCustInfo 添加或修改客户业务员联系详细信息
	 * weiyingni  2015-10-12
	 * @param {class}客户详细信息实体对象
	 * @param 验签
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public String saveOrUpdateCustConnection(String custConnectionInfo, String signature);
	
	/**
	 * 查询业务员关联信息
	 * 功能说明：该方法实现的功能			
	 * @author sky
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2015-11-1
	 * 修改内容：
	 * 修改注意点：
	 */
	public String getCustConnection(String paramJson, String signature) throws Exception;
	
	
	/**
	 * 查询业务员关联信息
	 * 功能说明：该方法实现的功能			
	 * @author lym
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2015-11-1
	 * 修改内容：
	 * 修改注意点：
	 */
	public String getSingleCustConnection(String paramJson, String signature) throws Exception;
	
	/**
	 * 根据客户ID修改业务员关联关系
	 * @param custId
	 * @param employeeId
	 * @param signature
	 * @return
	 */
	public String updateByCustId(String custId,String username,String signature);
	
}
