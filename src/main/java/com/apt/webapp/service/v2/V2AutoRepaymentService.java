package com.apt.webapp.service.v2;

import java.util.List;
import java.util.Map;

import com.apt.webapp.model.crm.CrmApplay;
import com.apt.webapp.model.crm.CrmPaycontrol;
import com.apt.webapp.model.crm.CrmPayrecoder;

/**
 * 功能说明：v2到期还款 逾期还款 自动逾期
 * 典型用法：
 * 特殊用法：	
 * @author 王明振
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2016年3月8日 
 * Copyright zzl-apt
 */
public interface V2AutoRepaymentService {
	
	/**
	 * 功能说明：执行到期还款操作
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 王明振
	 * 创建日期：2015年10月20日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void dueRepayment()throws Exception;
	/**
	 * 功能说明：执行逾期还款操作
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 王明振
	 * 创建日期：2016年3月8日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> overdueRepayment()throws Exception;
	/**
	 * 功能说明：执行自动逾期操作
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 王明振
	 * 创建日期：2016年3月8日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void autoOverdue(Map crmPaycontrol)throws Exception;
	/**
	 * 功能说明：获取v2今日未结清订单
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
	public List<Map> getV2NotClearPaycontrols();

}
