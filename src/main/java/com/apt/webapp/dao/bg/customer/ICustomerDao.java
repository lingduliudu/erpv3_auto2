package com.apt.webapp.dao.bg.customer;

import java.util.Map;

import com.apt.webapp.base.dao.IBaseHibernateDaoSupper;

/**
 * 功能说明：v3的客户表       dao层
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明
 * @author weiyz
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-12
 * Copyright zzl-apt
 */
public interface ICustomerDao extends IBaseHibernateDaoSupper{

	public Map getDaikouInfo(String custInfoId);
	
}
