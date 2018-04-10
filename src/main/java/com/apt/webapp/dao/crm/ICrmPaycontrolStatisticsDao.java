package com.apt.webapp.dao.crm;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.apt.webapp.base.dao.IBaseHibernateDaoSupper;
import com.apt.webapp.model.crm.CrmPaycontrolStatistics;

/**
 * 功能说明：信贷订单  crm_paycontrol_statistics  dao层   接口
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明
 * @author changsh
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2017-08-23
 * Copyright zzl-apt
 */
public interface ICrmPaycontrolStatisticsDao extends IBaseHibernateDaoSupper{
	
	//查出要添加的数据
	public List<Map> findCrmPaycontrolStatisticsToAdd(JSONObject paramJon)throws Exception;
	
	//查出
	public List<Map> queryCrmPaycontrolStatistics(JSONObject paramJson)throws Exception;
}
