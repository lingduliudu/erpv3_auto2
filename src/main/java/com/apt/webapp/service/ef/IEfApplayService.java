package com.apt.webapp.service.ef;

import javax.jws.WebService;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 功能说明：理财订单  service层    接口
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明
 * @author weiyingni
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-26
 * Copyright zzl-apt
 */
@WebService
public interface IEfApplayService {

	
	/**
	 * 功能说明：querySuccessCusts 查询成功客户信息    
	 * weiyingni  2015-10-26
	 * @param pageNo
	 * @param pageSize
	 * @param paramJson
	 * @param signature
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public String getSuccessCusts(int pageNo, int pageSize, String paramJson, String signature);
	
	/**
	 * 功能说明：addOrUpdateEfApplay 添加或修改理财申请单	    
	 * weiyingni  2015-10-26
	 * @param paramJson
	 * @param signature
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public String addOrUpdateEfApplay(String paramJson, String signature);
	
	/**
	 * 功能说明：getCustApplays 获取客户理财信息列表    	    
	 * weiyingni  2015-10-27
	 * @param paramJson
	 * @param signature
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public String getCustApplays(String paramJson, String signature);
	
	/**
	 * 功能说明：getCustEfApplayDetail 获取客户理财信息详细 	    
	 * weiyingni  2015-10-28
	 * @param applayId
	 * @param signature
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public String getCustEfApplayDetail(String applayId, String signature);
	
	/**
	 * 添加合同编号
	 * 功能说明：该方法实现的功能			
	 * @author sky
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2015-11-28
	 * 修改内容：
	 * 修改注意点：
	 */
	public String addContractNumber(String applayId, String signature) throws Exception;
	
	/**
	 * 合同编号删除
	 * 功能说明：该方法实现的功能			
	 * @author sky
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2015-11-28
	 * 修改内容：
	 * 修改注意点：
	 */
	public String delContractNumber(String contractNumberId, String signature) throws Exception;
	
	/**
	 * 进入财务
	 * 功能说明：该方法实现的功能			
	 * @author sky
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2015-11-28
	 * 修改内容：
	 * 修改注意点：
	 */
	public String accessToFinance(String applayId, String signature) throws Exception;
	
	
	/**
	 * 退回到待命
	 * 功能说明：该方法实现的功能			
	 * @author sky
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2015-12-7
	 * 修改内容：
	 * 修改注意点：
	 */
	public String exitProspectCustomer(String applayId, String signature)
			throws Exception;
	/**
	 * 进入财务
	 * 功能说明：该方法实现的功能			
	 * @author sky
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2015-12-7
	 * 修改内容：
	 * 修改注意点：
	 */
	public String toFinancerepayment(String applayId, String signature)
			throws Exception;
	/**
	 * 查找线下理财在投客户
	 * 功能说明：该方法实现的功能			
	 * @author yuanhao
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2015-12-7
	 * 修改内容：
	 * 修改注意点：
	 */
	public String getLineVotingCustomer(String paramString, String speString)throws Exception;
	/**
	 * 查找线上理财在投客户
	 * 功能说明：该方法实现的功能			
	 * @author yuanhao
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2015-12-7
	 * 修改内容：
	 * 修改注意点：
	 */
	public String getOnLineVotingCustomer(String paramString, String speString)throws Exception;
	/**
	 * 查找线下投资订单
	 * 功能说明：该方法实现的功能			
	 * @author yuanhao
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2015-12-7
	 * 修改内容：
	 * 修改注意点：
	 */
	public String getLineOrderList(String paramString, String speString)throws Exception;
	/**
	 * 查找线上投资订单
	 * 功能说明：该方法实现的功能			
	 * @author yuanhao
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2015-12-7
	 * 修改内容：
	 * 修改注意点：
	 */
	public String getOnLineOrderList(String paramString, String speString)throws Exception;
	
	/**
	 * get product info in the financing apply.
	 * 功能说明：该方法实现的功能			
	 * @author sky
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2015-12-12
	 * 修改内容：
	 * 修改注意点：
	 */
	public String getEfApplyProductInfo(int pageNo,int pageSize,String jsonParam, String signature)
			throws Exception;
	
	
}
