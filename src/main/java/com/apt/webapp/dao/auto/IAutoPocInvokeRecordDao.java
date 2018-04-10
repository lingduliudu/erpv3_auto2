package com.apt.webapp.dao.auto;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.apt.webapp.base.dao.IBaseHibernateDaoSupper;

public interface IAutoPocInvokeRecordDao  extends IBaseHibernateDaoSupper{
	public List<Map> getPocInvokeRecords(JSONObject paramJson);
}
