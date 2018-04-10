package com.apt.webapp.service.crm;

import javax.jws.WebService;

import net.sf.json.JSONObject;

/**
 * 功能说明：客户交接  service层    接口
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明
 * @author fxw
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-14
 * Copyright zzl-apt
 */
@WebService
public interface ICrmCustTransferRecoderService {
	
	/**
	  *
	  * 功能说明：查询客户交接表的所有信息
	  * a：客户交接表 CrmCustTransferRecoder
	  * b：客户表 Customer
	  * c：客户信息表 CustomerInfo
	  * d：原持有人表 Employee
	  * e：现持有人表 Employee
	  * f：操作人表 Employee
	  * g：原持有人部门 Department
	  * h：原持有人分公司 Department
	  * i：现持有人部门Department
	  * j：现持有人分公司Department
	  * fxw  2015-10-12
	  * @param columnName 需要查询的列名(需要加上表别名,别名上面有说明)
	  * @return   
	  * @throws 
	  * 最后修改时间：最后修改时间
	  * 修改人：admin
	  * 修改内容：
	  * 修改注意点：
	  */
	public String getCustTransferRecoder(String columnName,String groupName,int pageNo, int pageSize, String paramMap) throws Exception ;
	
	/**
	 * 功能说明：保存客户交接记录		
	 * feixinwei  2015-10-23
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * 修改人：Administrator
	 * 修改内容：
	 * 修改注意点：
	 */
	public String saveCustomerTransfer(String paramsMap)throws Exception;
}
