package com.apt.webapp.service.crm;

import java.util.List;
import java.util.Map;
import javax.jws.WebService;

import com.apt.webapp.model.crm.CrmOrder;

/**
 * 功能说明：信贷订单  crm_order  service层   接口
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明
 * @author weiyz
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-13
 * Copyright zzl-apt
 */
@WebService
public interface ICrmOrderService  {
	/**
	 * 功能说明：执行v3BOC的service			
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
	public List<Map> getCurrentPaycontrol();
	public Map getCrmOrderById(String crmOrderId);
	/**
	 * 功能说明：获得POC的信贷明细		
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
	public List<Map> getCurrentPaycontrolsPoc();
	/**
	 * 功能说明：获得定投Boc的信贷明细	
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
	public List<Map> getCurrentImmePaycontrolsBoc();
	/**
	 * 功能说明：获得定投Poc的信贷明细	
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
	public List<Map> getCurrentImmePaycontrolsPoc();
	/**
	 * 功能说明：获得定投Boc的信贷明细	
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
	public List<Map> getCurrentDetePaycontrolsBoc();
	/**
	 * 功能说明：获得定投poc的信贷明细	
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
	public List<Map> getCurrentDetePaycontrolsPoc();
	/**
	 * 功能说明：判断是不是最后一期	
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
	public boolean lastTimes(String crmOrderId,String string);
	/**
	 * 功能说明：自动还款的boc通知
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
	public void autoNotice(String bocJson);
	/**
	 * 功能说明：结清当期明细
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
	public void clearCurrentPaycontrol(String string);
	/**
	 * 功能说明：结清当期明细
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
	public void repayNormalByPoc(String string);
	/**
	 * 功能说明：获得直投Boc逾期明细
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
	public List<Map> getOverImmePaycontrolsBoc();
	/**
	 * 功能说明：获得定投Boc逾期明细
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
	public List<Map> getOverDetePaycontrolsBoc();
	/**
	 * 功能说明：获得Poc逾期明细
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
	public List<Map> getOverPaycontrolsPoc();
	/**
	 * 功能说明：获得直投Poc逾期明细
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
	public List<Map> getOverImmePaycontrolsPoc();
	/**
	 * 功能说明：获得定投Poc逾期明细
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
	public List<Map> getOverDetePaycontrolsPoc();
	/**
	 * 功能说明：获得线下逾期明细
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
	public List<Map> getOverLinePaycontrolsWithPoc();
	/**
	 * 功能说明：获得线下逾期明细
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
	public List<Map> getOverLinePaycontrolsWithBoc();
	/**
	 * 功能说明：线下逾期还款通过poc
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
	public void overRepayByPoc(String crmOrderId);
	/**
	 * 功能说明：获取今日未结清订单
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
	public List<Map> getNotClearPaycontrols();
	/**
	 * 功能说明：开始自动逾期
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
	public void autoOver(Map crmPaycontrol);
	/**
	 * 功能说明：判断是否有逾期
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
	public boolean hasOver(String string);
	/**
	 * 功能说明：获得v1线下的信贷明细	
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月8日 10:06:10
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getV1CurrentLinePaycontrols();
	/**
	 * 功能说明：获取v1今日未结清订单
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年3月8日 15:24:32
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getV1NotClearPaycontrols();
	/**
	 * 功能说明：开始v1自动逾期
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月8日 15:50:32
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void v1AutoOver(Map crmPaycontrol);
	/**
	 * 功能说明：获得v1逾期的明细
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月9日 10:41:59
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getV1OverPaycontrols();
	/**
	 * 功能说明：结清v1当期明细
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年3月14日 10:04:19
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void repayNormalByV1(String string);
	/**
	 * 功能说明：v1逾期还款
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void overRepayByV1(String crmOrderId);
	/**
	 * 功能说明：v1逾期还款
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public boolean hasReductionPaycontrol(String crmOrderId);
	/**
	 * 功能说明：获得直投的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpEfPayImmePoc();
	/**
	 * 功能说明：获得定投的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpEfPaySetePoc();
	/**
	 * 功能说明：获得crm直投的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpCrmPayImmePoc();
	/**
	 * 功能说明：获得crm定投的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpCrmPaySetePoc();
	/**
	 * 功能说明：获得crm直投逾期的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpCrmOverPayImmePoc();
	/**
	 * 功能说明：获得crm定投逾期的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpCrmOverPaySetePoc();
	
	/**
	 * 获取赎楼到期订单
	 * 功能说明：该方法实现的功能			
	 * @author sky
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2016-9-1
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> searchRansomFloorOrders();
	
	/**
	 * 赎楼到期理财赎回 
	 * 功能说明：该方法实现的功能			
	 * @author sky
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2016-9-1
	 * 修改内容：
	 * 修改注意点：
	 */
	public void executePocRansomFloor(double pdInvestRate,int maxDay,double pdInvestManageRate,String fy_account,String beoId,CrmOrder order);
	
	/**
	 * 赎楼理财boc 处理
	 * 功能说明：该方法实现的功能			
	 * @author sky
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2016-9-1
	 * 修改内容：
	 * 修改注意点：
	 */
	public void executeBocRansomFloor(double pdInvestRate, int maxDay,
			double pdInvestManageRate, String bank_account, String beoId,String crmOrderId,Double assignedFee);
	/**
	 * 赎楼理财boc 
	 * 功能说明：该方法实现的功能			
	 * @author yuanhao
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2016-9-1
	 * 修改内容：
	 * 修改注意点：
	 */
	public void executeRansomFloorBoc(double pdInvestRate, int maxDay,
			double pdInvestManageRate, String bank_account, String beoId,String crmOrderId);
	/**
	 * 查询信贷还款记录数据
	 * 功能说明：该方法实现的功能			
	 * @author yuanhao
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2016-9-1
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> findEfDeteData();
	/**
	 * 更新资产包里面的信贷信息
	 * 功能说明：该方法实现的功能			
	 * @author yuanhao
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2016-9-1
	 * 修改内容：
	 * 修改注意点：
	 */
	public void removePkgCrmOrders(List<Map> crmorders);
	/**
	 * 更新资产包里面的信贷信息
	 * 功能说明：该方法实现的功能			
	 * @author yuanhao
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2016-9-1
	 * 修改内容：
	 * 修改注意点：
	 */
/*	public void removePkgCrmOrders(String crmorderId);*/
	
	/**
	 * 功能说明：获取到期的赎楼订单
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> qureyRansomFloorOrders();
	
	/**
	 * 功能说明：赎楼理财还款POC
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：下午5:58:43
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void hfRansomFloor(double pdInvestRate, int maxDay,
			double pdInvestManageRate, String fy_account, String beoId,CrmOrder order, String cust_name);
	
	/**
	 * 功能说明：BOC赎楼理财还款
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void bocRansomFloor(double pdInvestRate, int maxDay,
			double pdInvestManageRate, String bank_account, String beoId,String crmOrderId);
}
