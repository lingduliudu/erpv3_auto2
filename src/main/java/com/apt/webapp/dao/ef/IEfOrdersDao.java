package com.apt.webapp.dao.ef;

import java.util.Map;

import net.sf.json.JSONObject;

import com.apt.webapp.base.dao.IBaseHibernateDaoSupper;

/**
 * 功能说明：理财订单表Dao层实现类
 * @author 乔春峰
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-12
 * Copyright zzl-apt
 */
public interface IEfOrdersDao extends IBaseHibernateDaoSupper{
	/**
	 * 功能说明：查找原始的授权码
	 * yuanhao  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public String findLineAuthCode(String efOrderId);
	/**
	 * 功能说明：获得总金额
	 * yuanhao  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public Double findAllMoney(String efOrderId);
	/**
	 * 功能说明：线下理财申请
	 * yuanhao  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public Map getEfApply(String efOrderId);
	/**
	 * 功能说明：查找到对应的冻结标
	 * yuanhao  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public JSONObject findFreezenProduct();
}
