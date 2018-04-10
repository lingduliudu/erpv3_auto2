package com.apt.webapp.dao.v2;

import java.util.List;
import java.util.Map;

import com.apt.webapp.base.dao.IBaseHibernateDaoSupper;

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
public interface V2AutoRepaymentDao extends IBaseHibernateDaoSupper{
	
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
	void autoOverdue(Map crmPaycontrol)throws Exception;
	
	/**
	 * 功能说明：查询正常结款的数据
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
	List<Map> findDueData()throws Exception;
	/**
	 * 功能说明：查询需要逾期结款的单子
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
	List<Map> findOverdueData()throws Exception;
	/**
	 * 功能说明：获取v2今日未结清订单
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年3月8日 15:24:55
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	List<Map> getV2NotClearPaycontrols();

}
