package com.apt.webapp.dao.impl.auto;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import net.sf.json.JSONObject;

import com.apt.util.ChkUtil;
import com.apt.webapp.base.dao.BaseHibernateDaoSupper;
import com.apt.webapp.dao.auto.IAutoPocInvokeRecordDao;

@Repository
public class AutoPocInvokeRecordDaoImpl extends BaseHibernateDaoSupper implements IAutoPocInvokeRecordDao {

	public List<Map> getPocInvokeRecords(JSONObject paramJson) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select");
		sb.append(" id,");
		sb.append(" crm_order_id,");
		sb.append(" crm_paycontrol_id,");
		sb.append(" bg_ef_paycontrol_id,");
		sb.append(" preauth_contract,");
		sb.append(" state,");
		sb.append(" return_code,");
		sb.append(" return_info,");
		sb.append(" amount,");
		sb.append(" repay_type,");
		sb.append(" serial_no");
		sb.append(" from auto_poc_invoke_record");
		sb.append(" where 1=1 and state=1");
		Object obj = paramJson.get("crmOrderId");
		if (ChkUtil.isNotEmpty(obj)) {
			sb.append(" and crm_order_id='" + obj + "'");
		}
		obj = paramJson.get("crmPaycontrolId");
		if (ChkUtil.isNotEmpty(obj)) {
			sb.append(" and crm_paycontrol_id='" + obj + "'");
		}
		obj = paramJson.get("efPaycontrolId");
		if (ChkUtil.isNotEmpty(obj)) {
			sb.append(" and bg_ef_paycontrol_id='" + obj + "'");
		}
		obj = paramJson.get("preauthContract");
		if (ChkUtil.isNotEmpty(obj)) {
			sb.append(" and preauth_contract='" + obj + "'");
		}
		obj = paramJson.get("repayType");
		if (ChkUtil.isNotEmpty(obj)) {
			sb.append(" and repay_type=" + obj + "");
		}
		obj = paramJson.get("invokeTime");
		if (ChkUtil.isNotEmpty(obj)) {
			sb.append(" and invoke_time like '" + obj + "%'");
		}
		obj = paramJson.get("serialNo");
		if (ChkUtil.isNotEmpty(obj)) {
			sb.append(" and serial_no = '" + obj + "'");
		}
		return queryBySqlReturnMapList(sb.toString());
	}

}
