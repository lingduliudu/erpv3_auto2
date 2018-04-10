package com.apt.webapp.service.impl.auto;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSONObject;

import com.apt.webapp.dao.auto.IAutoPocInvokeRecordDao;
import com.apt.webapp.service.auto.IAutoPocInvokeRecordService;

@Service
@Transactional
@WebService(serviceName = "autoPocInvokeRecordService", endpointInterface = "com.apt.webapp.service.auto.IAutoPocInvokeRecordService")
public class AutoPocInvokeRecordServiceImpl implements IAutoPocInvokeRecordService {
	@Resource
	private IAutoPocInvokeRecordDao autoPocInvokeRecordDao;

	public List<Map> getPocInvokeRecords(String paramString) {
		JSONObject paramJson = JSONObject.fromObject(paramString);
		return autoPocInvokeRecordDao.getPocInvokeRecords(paramJson);
	}

}
