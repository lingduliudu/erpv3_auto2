package com.apt.webapp.service.impl.crm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.apt.util.ChkUtil;
import com.apt.util.ListTool;
import com.apt.util.date.DateUtil;
import com.apt.webapp.dao.crm.ICrmPaycontrolStatisticsDao;
import com.apt.webapp.model.crm.CrmPaycontrolStatistics;
import com.apt.webapp.service.crm.ICrmPaycontrolStatisticsService;

@Service
@Transactional
@WebService(serviceName = "crmPaycontrolStatisticsService", endpointInterface = "com.apt.webapp.service.crm.ICrmPaycontrolStatisticsService")
public class CrmPaycontrolStatisticsService implements ICrmPaycontrolStatisticsService{

	// 日志
	private Logger logger = LoggerFactory.getLogger(CrmPaycontrolStatisticsService.class);

	@Resource
	private ICrmPaycontrolStatisticsDao crmPaycontrolStatisticsDao;

	public void addCrmPaycontrolStatistics(String total) throws Exception{
		logger.warn("-------------->>>开始同步CrmPaycontrolStatistics");
		JSONObject paramJson = new JSONObject();
			//查出近二十分钟内已添加的数据
/*			List<String> crmOrderIdList = new ArrayList<String>();
			List<String> repaymentTimeList = new ArrayList<String>();
			JSONObject  querySpecifiedTimeJson = new JSONObject();
			querySpecifiedTimeJson.put("specifiedTime", "(select date_sub(now(),interval 20 minute))");
			List<Map> crmPaycontrolStatisticsList = crmPaycontrolStatisticsDao.queryCrmPaycontrolStatistics(querySpecifiedTimeJson);
			for(Map crmPaycontrolStatisticsMap:crmPaycontrolStatisticsList) {
				if(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("crm_order_id")) && ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("repayment_time"))) {
					crmOrderIdList.add(crmPaycontrolStatisticsMap.get("crm_order_id").toString());
					repaymentTimeList.add(crmPaycontrolStatisticsMap.get("repayment_time").toString());
				}
			}*/
			List<Object> addCrmPaycontrolStatisticsList = new ArrayList<Object>();
			paramJson.put("total", total);
			//查出最近xx时间内的还款信息
			List<Map> crmPaycontrolStatisticsToAddList = crmPaycontrolStatisticsDao.findCrmPaycontrolStatisticsToAdd(paramJson);
			for(Map crmPaycontrolStatisticsMap:crmPaycontrolStatisticsToAddList){
				//如果已经存在就不添加
/*				if(crmOrderIdList.contains(crmPaycontrolStatisticsMap.get("crm_order_id")) && repaymentTimeList.contains(crmPaycontrolStatisticsMap.get("repayment_time"))) {
					crmPaycontrolStatisticsToAddList.remove(crmPaycontrolStatisticsMap);
				}*/
				//订单状态
				String order_trade_status = crmPaycontrolStatisticsMap.get("order_trade_status").toString();
				paramJson = new JSONObject();
				paramJson.put("crmOrderId", crmPaycontrolStatisticsMap.get("crm_order_id").toString());
				List<Map> pcs = crmPaycontrolStatisticsDao.queryCrmPaycontrolStatistics(paramJson);
				if (ListTool.isNullOrEmpty(pcs)) {
					if ("4".equals(order_trade_status)) continue;
					//添加数据
					CrmPaycontrolStatistics paycontrolStatistics = new CrmPaycontrolStatistics();
					paycontrolStatistics.setCrmOrderId(crmPaycontrolStatisticsMap.get("crm_order_id").toString());
					paycontrolStatistics.setRepaymentTime(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("repayment_time"))?crmPaycontrolStatisticsMap.get("repayment_time").toString():"");//本期应还日期
					paycontrolStatistics.setRepaymentDuetime(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("repayment_duetime"))?Integer.parseInt(crmPaycontrolStatisticsMap.get("repayment_duetime").toString()):-1);//本期应还期数
					paycontrolStatistics.setPaycontrolId(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("id"))?crmPaycontrolStatisticsMap.get("id").toString():"");//本期应还明细id
					paycontrolStatistics.setOverdueDay(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("overdue_day"))?Integer.parseInt(crmPaycontrolStatisticsMap.get("overdue_day").toString()):0);//当前逾期天数
					paycontrolStatistics.setOverdueNumber(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("status2"))?Integer.parseInt(crmPaycontrolStatisticsMap.get("status2").toString()):-1);//当前逾期期数
					paycontrolStatistics.setClosingPeriod(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("status1"))?Integer.parseInt(crmPaycontrolStatisticsMap.get("status1").toString()):-1);//已结清期数
					paycontrolStatistics.setRemainCapitalTotal(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("remain_capital"))?Double.valueOf(crmPaycontrolStatisticsMap.get("remain_capital").toString()):-1);//剩余本金总和
					paycontrolStatistics.setRemainManageFeeTotal(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("remain_manage_fee"))?Double.valueOf(crmPaycontrolStatisticsMap.get("remain_manage_fee").toString()):-1);//剩余分期服务费总和/剩余管理费总和
					paycontrolStatistics.setRemainOverdueAccrualTotal(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("remain_overdue_accrual_total"))?Double.valueOf(crmPaycontrolStatisticsMap.get("remain_overdue_accrual_total").toString()):0);//逾期利息总和
					paycontrolStatistics.setRemainAccrualTotal(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("remain_accrual"))?Double.valueOf(crmPaycontrolStatisticsMap.get("remain_accrual").toString()):-1);//剩余利息总和
					paycontrolStatistics.setRemainInterestTotal(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("remain_interest"))?Double.valueOf(crmPaycontrolStatisticsMap.get("remain_interest").toString()):-1);//剩余罚息总和
					paycontrolStatistics.setRemainOverdueViolateMoneyTotal(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("overdue_violate_money"))?Double.valueOf(crmPaycontrolStatisticsMap.get("overdue_violate_money").toString()):-1);//剩余逾期违约金总和
					paycontrolStatistics.setStatus(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("orderStatus"))?Integer.parseInt(crmPaycontrolStatisticsMap.get("orderStatus").toString()):-1);
					paycontrolStatistics.setUpdateTime(DateUtil.getCurrentTime(DateUtil.STYLE_1));
					addCrmPaycontrolStatisticsList.add(paycontrolStatistics);
				}
				for (Map map : pcs) {
					String id = map.get("id").toString();
					CrmPaycontrolStatistics paycontrolStatistics = (CrmPaycontrolStatistics) crmPaycontrolStatisticsDao.findById(CrmPaycontrolStatistics.class,id);
					if ("4".equals(order_trade_status)) {	
						//删除结清订单 
						logger.warn("------>>>删除结清订单,crm_order_id:"+crmPaycontrolStatisticsMap.get("crm_order_id").toString());
						crmPaycontrolStatisticsDao.delete(paycontrolStatistics);
					}else{
						//更新数据
						paycontrolStatistics.setRepaymentTime(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("repayment_time"))?crmPaycontrolStatisticsMap.get("repayment_time").toString():"");//本期应还日期
						paycontrolStatistics.setRepaymentDuetime(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("repayment_duetime"))?Integer.parseInt(crmPaycontrolStatisticsMap.get("repayment_duetime").toString()):-1);//本期应还期数
						paycontrolStatistics.setPaycontrolId(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("id"))?crmPaycontrolStatisticsMap.get("id").toString():"");//本期应还明细id
						paycontrolStatistics.setOverdueDay(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("overdue_day"))?Integer.parseInt(crmPaycontrolStatisticsMap.get("overdue_day").toString()):0);//当前逾期天数
						paycontrolStatistics.setOverdueNumber(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("status2"))?Integer.parseInt(crmPaycontrolStatisticsMap.get("status2").toString()):-1);//当前逾期期数
						paycontrolStatistics.setClosingPeriod(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("status1"))?Integer.parseInt(crmPaycontrolStatisticsMap.get("status1").toString()):-1);//已结清期数
						paycontrolStatistics.setRemainCapitalTotal(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("remain_capital"))?Double.valueOf(crmPaycontrolStatisticsMap.get("remain_capital").toString()):-1);//剩余本金总和
						paycontrolStatistics.setRemainManageFeeTotal(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("remain_manage_fee"))?Double.valueOf(crmPaycontrolStatisticsMap.get("remain_manage_fee").toString()):-1);//剩余分期服务费总和/剩余管理费总和
						paycontrolStatistics.setRemainOverdueAccrualTotal(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("remain_overdue_accrual_total"))?Double.valueOf(crmPaycontrolStatisticsMap.get("remain_overdue_accrual_total").toString()):0);//逾期利息总和
						paycontrolStatistics.setRemainAccrualTotal(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("remain_accrual"))?Double.valueOf(crmPaycontrolStatisticsMap.get("remain_accrual").toString()):-1);//剩余利息总和
						paycontrolStatistics.setRemainInterestTotal(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("remain_interest"))?Double.valueOf(crmPaycontrolStatisticsMap.get("remain_interest").toString()):-1);//剩余罚息总和
						paycontrolStatistics.setRemainOverdueViolateMoneyTotal(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("overdue_violate_money"))?Double.valueOf(crmPaycontrolStatisticsMap.get("overdue_violate_money").toString()):-1);//剩余逾期违约金总和
						paycontrolStatistics.setStatus(ChkUtil.isNotEmpty(crmPaycontrolStatisticsMap.get("orderStatus"))?Integer.parseInt(crmPaycontrolStatisticsMap.get("orderStatus").toString()):-1);
						paycontrolStatistics.setUpdateTime(DateUtil.getCurrentTime(DateUtil.STYLE_1));
						addCrmPaycontrolStatisticsList.add(paycontrolStatistics);
					}
				}
			}
			//批量添加
			crmPaycontrolStatisticsDao.batchSave(addCrmPaycontrolStatisticsList);
	}

}
