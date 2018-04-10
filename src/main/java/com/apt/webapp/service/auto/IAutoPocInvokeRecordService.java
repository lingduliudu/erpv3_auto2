package com.apt.webapp.service.auto;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import net.sf.json.JSONObject;

@WebService
public interface IAutoPocInvokeRecordService {
	public List<Map> getPocInvokeRecords(String paramString);
}
