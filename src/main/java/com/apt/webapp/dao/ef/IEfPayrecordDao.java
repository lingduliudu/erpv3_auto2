package com.apt.webapp.dao.ef;

import java.util.Map;

import net.sf.json.JSONObject;

import com.apt.webapp.base.dao.IBaseHibernateDaoSupper;
import com.apt.webapp.model.ef.EfPayrecord;

/**
 * 功能说明：理财还款记录表Dao层接口
 * @author 乔春峰
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-12
 * Copyright zzl-apt
 */
public interface IEfPayrecordDao  extends IBaseHibernateDaoSupper{
	/**
	 * 功能说明：理财还款记录表对象查询（多条件）
	 * yuanhao  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public void updateForNormal(Map efPaycontrol);
	/**
	 * 功能说明：根据类型产生记录
	 * yuanhao  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public void addByModel(Map efPaycontrol);
}
