package com.apt.webapp.base.dao;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * 
 * Classname  	DAO层基类查询 
 * Version	  	1.2
 * @author 第二数据源专属
 * 2014-7-20
 * Copyright notice
 */
@SuppressWarnings("rawtypes")
public interface ISecondHibernateDaoSupper {
	/**
	 * 
	 * @Title: 
	 * @Description: 针对第二个数据源进行批量保存
	 * @param @param List<String> macSource数据
	 * @param @throws Exception    设定文件 
	 * @return     JSONObject 
	 * @throws
	 */
	public  void paycenterSave(List<JSONObject> data, String type);
}
