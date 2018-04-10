package com.apt.webapp.dao.impl.ef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.apt.webapp.base.dao.BaseHibernateDaoSupper;
import com.apt.webapp.dao.ef.IZZLEfPayrecordDao;
/**
 * 功能说明：理财还款记录表Dao层实现类
 * @author 乔春峰
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-12
 * Copyright zzl-apt
 */
@Repository
public class ZZLEfPayrecordDaoImpl extends BaseHibernateDaoSupper implements IZZLEfPayrecordDao{
	//日志
	private Logger logger = LoggerFactory.getLogger(ZZLEfPayrecordDaoImpl.class);
}
