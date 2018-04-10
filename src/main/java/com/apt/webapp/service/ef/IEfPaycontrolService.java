package com.apt.webapp.service.ef;


import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import net.sf.json.JSONObject;

import com.apt.webapp.model.bg.ef.BgEfPaycontrol;
import com.apt.webapp.model.crm.CrmApplay;
import com.apt.webapp.model.crm.CrmOrder;
import com.apt.webapp.model.crm.CrmPaycontrol;

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
public interface IEfPaycontrolService  {
	/**
	 * 功能说明： 获得Boc当日待还明细
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
	public List<Map> getCurrentControlsBoc();
	/**
	 * 功能说明： 更新
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
	public void update(Object obj);
	/**
	 * 功能说明： 获得Poc当日待还明细
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
	public List<Map> getCurrentControlsPoc();
	/**
	 * 功能说明：获得需要结清的资产包数据
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 yuanhao
	 * 创建日期：2016年8月31日 15:27:12
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getNeedClearPKOrders();
	/**
	 * 功能说明： 获得当日待还明细
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月31日 15:27:12
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getCurrentControlsPrincipal();
	/**
	 * 功能说明： 获得当日待还明细
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月31日 15:27:12
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getCurrentControlsInterest();
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
	public void updateNormal(Map efPaycontrol);
	/**
	 * 功能说明：获得自动投标的序列号
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
	public String findAutoSerino(String string);
	/**
	 * 功能说明：获得借款人信息
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
	public Map getCrmManInfo(String ef_order_id);
	/**
	 * 功能说明：获得借款人信息
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
	public BgEfPaycontrol findById(String id);
	/**
	 * 功能说明：增加记录
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
	public void add(Object obj);
	/**
	 * 功能说明：增加邀请人的收益查看
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
	public JSONObject getInviterData(String id);
	/**
	 * 功能说明：增加邀请人的收益查看
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
	public void pkgIncomeRecord(JSONObject inviterRecord);
	
	/**
	 * 功能说明：更新对应的订单，申请单和资产包信息
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午11:17:30
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void updateInfo(CrmOrder crmOrder, CrmApplay apply);
	
	/**
	 * 功能说明：
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午10:08:05
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void updateCrmorder(String CrmOrderId);
}
