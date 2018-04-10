package com.apt.webapp.dao.impl.crm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.apt.webapp.base.dao.BaseHibernateDaoSupper;
import com.apt.webapp.dao.crm.IPsCheckMoneyDao;


/**
 * 功能说明：还款记录 dao层   实现类
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明
 * @author yuanhao
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-13
 * Copyright zzl-apt
 */
@Repository
public class PsCheckMoneyDaoImpl extends BaseHibernateDaoSupper implements IPsCheckMoneyDao  {
	//日志
	private static Logger logger = LoggerFactory.getLogger(PsCheckMoneyDaoImpl.class);
}
