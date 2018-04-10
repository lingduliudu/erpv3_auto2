package com.apt.webapp.dao.impl.crm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;
import com.apt.util.ChkUtil;
import com.apt.util.ListTool;
import com.apt.webapp.base.dao.BaseHibernateDaoSupper;
import com.apt.webapp.dao.crm.ICrmPaycontrolStatisticsDao;

@Repository
public class CrmPaycontrolStatisticsDaoImpl extends BaseHibernateDaoSupper implements ICrmPaycontrolStatisticsDao{

	//日志
	private Logger logger = LoggerFactory.getLogger(CrmPaycontrolStatisticsDaoImpl.class);



	public List<Map> findCrmPaycontrolStatisticsToAdd(JSONObject paramJon)throws Exception{
		List<Map> resultList = new ArrayList<Map>();
		//还款记录同步
		List<Map> queryOrderList = new ArrayList<Map>();
		Object obj = paramJon.get("id");
		if(ChkUtil.isNotEmpty(obj)){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", obj);
			map.put("paycontrol_id", "1");
			queryOrderList.add(map);
		}else {
			StringBuffer queryCrmPayrecoderSQL = new StringBuffer();
			//统计页面获取最后一条还款时间
			obj = paramJon.get("updateLast");
			if(ChkUtil.isNotEmpty(obj)) {
				queryCrmPayrecoderSQL.append(" select crm_order_id,cust_info_id,paycontrol_id from crm_payrecoder where 1=1 order by create_time desc LIMIT 1 ");
			}else {			
				obj = paramJon.get("total");
				if("total".equals(obj)) {
					//查出所有记录
					logger.warn("------>>>查出所有未结清订单");
					queryCrmPayrecoderSQL.append(" select id as crm_order_id,cust_info_id,order_trade_status from crm_order co where co.order_trade_status!=4");
				}else{
					//查出20分钟内还款的所有记录
					logger.warn("------>>>查出20分钟内还款的所有记录");
					queryCrmPayrecoderSQL.append(" select crm_order_id,cp.cust_info_id,paycontrol_id,order_trade_status from crm_payrecoder cp LEFT JOIN crm_order co on co.id=cp.crm_order_id where cp.create_time>DATE_SUB(NOW(), interval 20 minute) GROUP BY crm_order_id");
				}
			}
			
			queryOrderList = queryBySqlReturnMapList(queryCrmPayrecoderSQL.toString());
		}
		
		for(Map queryCrmPayrecoderMap:queryOrderList) {
			//检查订单是否已结清
			String order_trade_status = queryCrmPayrecoderMap.get("order_trade_status").toString();
			if (!"4".equals(order_trade_status)) {//未结清
				//查出还款明细信息
				String crmPayControlSQL = "select cp.id,cp.crm_order_id,cp.repayment_time,cp.repayment_duetime,co.order_trade_status from crm_paycontrol cp LEFT JOIN crm_order co on cp.crm_order_id=co.id where cp.crm_order_id='"+queryCrmPayrecoderMap.get("crm_order_id")+"' and `status`=0 ORDER BY repayment_duetime";
				List<Map> queryCrmPaycontrolList = queryBySqlReturnMapList(crmPayControlSQL);
				if(ListTool.isNullOrEmpty(queryCrmPaycontrolList)) {
					crmPayControlSQL = "select cp.id,cp.crm_order_id,cp.repayment_time,cp.repayment_duetime,co.order_trade_status from crm_paycontrol cp LEFT JOIN crm_order co on cp.crm_order_id=co.id where crm_order_id='"+queryCrmPayrecoderMap.get("crm_order_id")+"' and `status`=2 ORDER BY repayment_time  desc";
					queryCrmPaycontrolList = queryBySqlReturnMapList(crmPayControlSQL);
				}
				if(queryCrmPaycontrolList.size()>0) {
					Map crmPaycontrolMap = queryCrmPaycontrolList.get(0);
					StringBuffer sb = new StringBuffer();
					sb.append(" select (select MAX(overdue_day) from crm_paycontrol where crm_order_id='"+queryCrmPayrecoderMap.get("crm_order_id")+"' and `status`=2) overdue_day,sum(status=2)status2,sum(status=1) status1,");
					sb.append(" sum(remain_capital) remain_capital,sum(remain_manage_fee) remain_manage_fee,");
					sb.append(" sum(should_accral)  should_accral,sum(remain_accrual) remain_accrual,");
					sb.append(" (select sum(remain_accrual) from crm_paycontrol where crm_order_id='"+queryCrmPayrecoderMap.get("crm_order_id")+"' and `status`=2) remain_overdue_accrual_total,sum(remain_interest) remain_interest,sum(overdue_violate_money) overdue_violate_money");
					sb.append(" from crm_paycontrol where crm_order_id='"+queryCrmPayrecoderMap.get("crm_order_id")+"';");
					List<Map> crmPaycontrolSumList = queryBySqlReturnMapList(sb.toString());
					Map crmPaycontrolSumMap = crmPaycontrolSumList.get(0);
					crmPaycontrolMap.putAll(crmPaycontrolSumMap);
					crmPaycontrolMap.put("orderStatus", "0".equals(crmPaycontrolSumMap.get("status2").toString())?"0":"2");
					resultList.add(crmPaycontrolMap);
				}
			}else{//订单已结清
				resultList.add(queryCrmPayrecoderMap);
			}
		}
		return resultList;
	}




	public List<Map> queryCrmPaycontrolStatistics(JSONObject paramJson) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(" select id,crm_order_id,repayment_time,repayment_duetime,paycontrol_id,overdue_day,overdue_number,closing_period,remain_capital_total,remain_overdue_accrual_total,remain_accrual_total,remain_overdue_violate_money_total,remain_interest_total,remain_manage_fee_total,status,update_time from crm_paycontrol_statistics where 1=1");
		Object obj = paramJson.get("specifiedTime");//
		if(ChkUtil.isNotEmpty(obj)) {
			sb.append(" and update_time >= "+obj);
		}
		obj = paramJson.get("id");//
		if(ChkUtil.isNotEmpty(obj)) {
			sb.append(" and id = '"+obj+"'");
		}
		obj = paramJson.get("crmOrderId");//
		if(ChkUtil.isNotEmpty(obj)) {
			sb.append(" and crm_order_id = '"+obj+"'");
		}
		return queryBySqlReturnMapList(sb.toString());
	}
	
}
