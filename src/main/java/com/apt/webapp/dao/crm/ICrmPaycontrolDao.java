package com.apt.webapp.dao.crm;

import net.sf.json.JSONObject;
import com.apt.webapp.base.dao.IBaseHibernateDaoSupper;
import com.apt.webapp.model.crm.CrmPaycontrol;
/**
 * 功能说明：信贷还款明细表     dao层
 * @author 乔春峰
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-13
 * Copyright zzl-apt
 */
public interface ICrmPaycontrolDao extends IBaseHibernateDaoSupper{
	/**
	 * 功能说明：信贷还款明细表对象查询
	 * 创建人：乔春峰
	 * 创建时间：2015-10-13
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public void updateForNormal(String crmOrderId);
}
