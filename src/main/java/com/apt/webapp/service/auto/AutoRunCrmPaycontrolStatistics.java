package com.apt.webapp.service.auto;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.apt.util.sms.SmsUtil;
import com.apt.webapp.service.crm.ICrmPaycontrolStatisticsService;
/**
 * 信贷订单还款情况统计
 * @author chengwei
 *
 */
@Component
public class AutoRunCrmPaycontrolStatistics {
	
	//日志
		private Logger logger = LoggerFactory.getLogger(AutoRun.class);
		
		@Resource
		private ICrmPaycontrolStatisticsService crmPaycontrolStatisticsService;
		
		boolean excuteAdd = true;
		
		/**
		 * 进行前20分钟内的信贷订单还款情况统计
		 */
		public void toExecuteCrmPaycontrolStatisticsAdd(){
			try{
				if(!excuteAdd){
					return;
				}
				excuteAdd = false;
				crmPaycontrolStatisticsService.addCrmPaycontrolStatistics("");
			}catch(Exception e){
				logger.warn(e.getMessage());
				logger.warn("20分钟内的信贷订单还款情况统计 异常！");
				JSONObject smsJson = new JSONObject();
				smsJson.put("text", "20分钟内的信贷订单还款情况统计异常！");
				smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
				e.printStackTrace();
			}
			excuteAdd = true;
			
		}
		/**
		 * 进行全部的信贷订单还款情况统计
		 */
		public void toExecuteCrmPaycontrolStatisticsTotal(){
			try{
				if(!excuteAdd){
					return;
				}
				excuteAdd = false;
				crmPaycontrolStatisticsService.addCrmPaycontrolStatistics("total");
			}catch(Exception e){
				logger.warn(e.getMessage());
				e.printStackTrace();
			}
			excuteAdd = true;
			
		}
		
		

}
