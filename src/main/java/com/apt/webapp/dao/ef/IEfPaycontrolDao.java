package com.apt.webapp.dao.ef;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.apt.webapp.base.dao.IBaseHibernateDaoSupper;
import com.apt.webapp.model.ef.EfPaycontrol;
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
public interface IEfPaycontrolDao extends IBaseHibernateDaoSupper{
	/**
	 * 功能说明： 更新理财明细
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
	public void updateForNormal(Map efPaycontrol);
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
	public String findAutoSerino(String cust_info_id);
	/**
	 * 功能说明：更加模式来更新明细并产生记录
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
	public void updateForNormalByModel(Map efPaycontrol);
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
	public JSONObject getInviterData(String cust_info_id);
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
	
}
