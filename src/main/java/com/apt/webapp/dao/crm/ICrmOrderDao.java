package com.apt.webapp.dao.crm;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import com.apt.webapp.base.dao.IBaseHibernateDaoSupper;
import com.apt.webapp.model.crm.CrmOrder;

/**
 * 功能说明：信贷订单  crm_order  dao层   接口
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
public interface ICrmOrderDao extends IBaseHibernateDaoSupper {
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
	/**
	 * 功能说明：通过产品编号找到当日的信贷订单	
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
	public Map getCrmpaycontrolByProductNumber(String order_prd_number);
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
	 * 功能说明：获得定投BOC的信贷明细		
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
	 * 功能说明：获得定投boc的信贷明细		
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
	public boolean lastTimes(String crmOrderId,String controlId);
	/**
	 * 功能说明：获得直投Boc逾期的明细
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
	 * 功能说明：获得定投Boc逾期的明细
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
	 * 功能说明：获得Poc逾期的明细
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
	 * 功能说明：获得直投Poc逾期的明细
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
	 * 功能说明：获得定投Poc逾期的明细
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
	 * 功能说明：获得线下逾期的明细
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
	 * 功能说明：获得线下逾期的明细
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
	public boolean hasOver(String crm_order_id);
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
	 * 创建日期：2016年3月8日 15:22:35
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
	 * 创建人 程伟
	 * 创建日期：2016年3月8日 15:50:38
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
	 * 功能说明：v1逾期还款
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:12:54
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void overRepayByV1(String crmOrderId);
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
	public List<Map> getFtpCrmPayImmePoc();
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
	public List<Map> getFtpCrmPaySetePoc();
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
	public List<Map> getFtpCrmOverPayImmePoc();
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
	public List<Map> getFtpCrmOverPaySetePoc();
	
	/**
	 * 获取赎楼还款列表
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
	
	
	public List searchRefereeConnection(String cust_info_id) throws Exception;
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
	 * 查询理财直投数据
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
	public List<Map> getEfImmeByCrmOrderAndDay(String crmOrderId, String payTime);
	/**
	 * 查询赎楼的理财直投数据
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
	public List<Map> getA007EfByCrmOrder(String crmOrderId);
	/**
	 * 查询理财直投数据红包户
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
	public List<Map> getEfImmeByCrmOrderAndDayRedAccount(String crmOrderId, String payTime);
	/**
	 * 查询理财定投数据
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
	public List<Map> getEfDeteByCrmOrderAndDay(String crmOrderId, String payTime);
	/**
	 * 查询理财直投数据(手动)
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
	public List<Map> getEfImmeByCrmOrder(String crmOrderId);
	/**
	 * 查询理财定投数据(手动)
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
	public List<Map> getEfDeteByCrmOrder(String crmOrderId);
	
	/**
	 * 功能说明：获得逾期信息订单
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午10:32:09
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getOverOrderBF(String status, String type);
	
	/**
	 * 功能说明：获得逾期信息订单明细信息
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午10:32:09
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getOverPaycontrolBF(String crmOrderId, String status);
	
	/**
	 * 功能说明：获得需要宝付代扣订单信息
	 * @param crmOrderId 信贷订单id status 订单种类
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午10:32:09
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> queryOrders();
	
	/**
	 * 功能说明：根据电子账户查询接口用户信息
	 * @param
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：下午3:10:54
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public Map fandBankAccount(String bankAccount);
	
	/**
	 * 功能说明：获得赎楼到期订单信息
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> queryRansomFloorOrders();
	/**
	 * @author yuanhao
	 * @date 2018年4月10日 下午4:11:54
	 * 批量更新
	 */
	public void batchSaveOrUpdate(List<Object> bgEfPaycontrols, int i);
}

