package com.apt.webapp.dao.ef;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.apt.webapp.base.dao.IBaseHibernateDaoSupper;
import com.apt.webapp.model.crm.CrmPaycontrol;
/**
 * 功能说明：理财还款明细表Dao层接口
 * @author 乔春峰
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-12
 * Copyright zzl-apt
 */
public interface IZZLEfPaycontrolDao extends IBaseHibernateDaoSupper{
	/**
	 * 功能说明： 复制理财明细
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
	public void copyEfControl(Map efPaycontrol);
	/**
	 * 功能说明：通过信贷订单id找到今日要还款的
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
	public List<Map> getCurrentByCrmControl(String crm_order_id,String crmOrderId);
	/**
	 * 功能说明：针对信贷订单成功,直接进行修改
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
	public void rePayNormal(String crmOrderId,String ids);
	/**
	 * 功能说明：通过时间找到对应的信贷明细
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
	public CrmPaycontrol findByTime(String crm_order_id,String pay_time);
	/**
	 * 功能说明：通过时间条件处理异常的信息
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
	public List<Map> getErrorZzlControls(String time);
	/**
	 * 功能说明：判断是否属于异常的zzl明细
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
	public boolean isError(Map error);
	/**
	 * 功能说明：保存数据
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
	public void copyAdvanceClear(JSONObject clearJson);
	
}
