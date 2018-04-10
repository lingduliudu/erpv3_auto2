package com.apt.webapp.service.auto;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.apt.util.ListTool;
import com.apt.util.sms.SmsUtil;
import com.apt.webapp.service.crm.ICrmOrderService;
/**
 * 功能说明：v1的自动执行方法
 * 典型用法：
 * 特殊用法：	
 * @author chengwei
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2016年3月8日 09:37:53
 */
@Component
public class V1AutoRun {
	// 日志
	private Logger logger = LoggerFactory.getLogger(V1AutoRun.class);
	@Resource
	private ICrmOrderService crmOrderService;
	@Resource
	private transToZzl transToZzl;
		/**
		 * 功能说明：执行V1自动还款操作
		 * @param 
		 * @return
		 * @throws  
		 * 创建人 chengwei
		 * 创建日期：2016年3月8日 09:51:34
		 * 最后修改时间：
		 * 修改人：
		 * 修改内容：
		 * 修改注意点：
		 */
	public void toExecuteV1CrmAutopay() {
		logger.warn("V1信贷自动还款开始----");
		// 查找今天需要还款的信贷明细
		// 1.查找今日待还
		List<Map> crmPaycontrols = crmOrderService.getV1CurrentLinePaycontrols();
		JSONArray flowRecordJsonArray = new JSONArray();
		// 如果无数据则结束
		if (ListTool.isNullOrEmpty(crmPaycontrols)) {
			logger.warn("V1信贷自动还款结束----");
			return;
		}
		for (Map crmPaycontrol : crmPaycontrols) {
			// 开始判断改明细前面是否有逾期订单
			boolean hasOver = crmOrderService.hasOver(crmPaycontrol.get("crm_order_id").toString());
			if (hasOver) {
				continue;
			}
			//准备开始记录数据
			JSONObject flowRecordJson = new JSONObject();
			flowRecordJson.put("orderType", "crm_paycontrol");
			flowRecordJson.put("payControlId", crmPaycontrol.get("id"));
			flowRecordJson.put("status", "0");
			flowRecordJson.put("code", "");
			flowRecordJson.put("rem", "");
			// poc账户通道
			if ("1".equals(crmPaycontrol.get("clearing_channel").toString())) {
				JSONObject pocResult = transToZzl.payByPocDaikou(crmPaycontrol.get("cust_info_id").toString(), crmPaycontrol.get("frozenMoney").toString(),"5",crmPaycontrol.get("crm_order_id").toString());
				flowRecordJson.put("code", pocResult.getString("code"));
				if ("1".equals(pocResult.getString("responseCode"))) { // 扣款成功直接更新
					flowRecordJson.put("status", "1");
					// poc扣款成功,直接进行修改状态就可以了
					logger.warn("v1信贷订单还款成功,明细id:" + crmPaycontrol.get("id") + ",POC返回成功!");
					crmOrderService.repayNormalByV1(crmPaycontrol.get("id").toString());
				}else{
					logger.warn("V1信贷明细在自动还款时失败!信贷明细id:"+crmPaycontrol.get("id"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", crmPaycontrol.get("crm_order_id"));
					smsJson.put("text","V1信贷明细在自动还款时失败!"+pocResult.getString("responseCode")+",信贷明细id:"+crmPaycontrol.get("id"));
					if ("1051".equals(pocResult.getString("responseCode")) ||
							"100017".equals(pocResult.getString("responseCode"))) {
						//不发送短信
					}else{
						SmsUtil.senErrorMsgByZhiyun(smsJson);
					}
				}
			}
			//记录中
			flowRecordJsonArray.add(flowRecordJson);
		}
		//发送到清结算
		//SmsUtil.sendFlowRecord(flowRecordJsonArray);
		logger.warn("V1信贷自动还款结束----");
	}
	/**
	 * 功能说明：准备执行自动逾期操作
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年3月8日 15:20:27
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */ 
	public void toExecuteAutoOverV1CrmOrder(){
		logger.warn("V1自动逾期开始--");
		List<Map> crmPaycontrols  = crmOrderService.getV1NotClearPaycontrols();
		logger.warn("V1自动逾期条数"+crmPaycontrols.size());
		//针对每条明细进行逾期修改操作
		if(ListTool.isNotNullOrEmpty(crmPaycontrols)){
			for(Map crmPaycontrol:crmPaycontrols){
				crmOrderService.v1AutoOver(crmPaycontrol);
			}
		}
		logger.warn("V1自动逾期结束--");
	}
	/**
	 * 功能说明：执行V1自动逾期还款操作
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年3月8日 09:51:34
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void toExecuteV1OverCrmAutopay() {
		logger.warn("V1信贷自动逾期还款开始----");
		// 查找今天需要还款的信贷明细
		// 1.查找今日待还
		List<Map> crmPaycontrols = crmOrderService.getV1OverPaycontrols();
		// 如果无数据则结束
		if (ListTool.isNullOrEmpty(crmPaycontrols)) {
			logger.warn("V1信贷自动逾期还款结束----");
			return;
		}
		for (Map crmPaycontrol : crmPaycontrols) {
			// poc账户通道
			if ("1".equals(crmPaycontrol.get("clearing_channel").toString())) {
				JSONObject pocResult = transToZzl.payByPocDaikou(crmPaycontrol.get("cust_info_id").toString(), crmPaycontrol.get("frozenMoney").toString(),"3",crmPaycontrol.get("crm_order_id").toString());
				if ("1".equals(pocResult.getString("responseCode"))) { // 扣款成功直接更新
					// poc扣款成功,直接进行修改状态就可以了
					logger.warn("v1信贷订单逾期还款成功,明细id:" + crmPaycontrol.get("id") + ",POC返回成功!");
					crmOrderService.overRepayByV1(crmPaycontrol.get("crm_order_id").toString());
				}else{
					logger.warn("V1信贷明细在自动逾期还款时失败!信贷明细id:"+crmPaycontrol.get("id"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", crmPaycontrol.get("crm_order_id"));
					smsJson.put("text","V1信贷明细在自动逾期还款时失败!"+pocResult.getString("responseCode")+",信贷明细id:"+crmPaycontrol.get("id"));
					if ("1051".equals(pocResult.getString("responseCode")) ||
							"100017".equals(pocResult.getString("responseCode"))) {
						//不发送短信
					}else{
						SmsUtil.senErrorMsgByZhiyun(smsJson);
					}
				}
			}
		}
		logger.warn("V1信贷自动逾期还款结束----");
	}
}
