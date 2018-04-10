package com.apt.webapp.task.ransomFloor.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.sf.json.JSONObject;

import com.apt.webapp.dao.crm.ICrmOrderDao;
import com.apt.webapp.model.crm.CrmOrder;
import com.apt.webapp.service.crm.ICrmOrderService;
import com.apt.webapp.service.impl.crm.CrmOrderServiceImpl;
import com.apt.webapp.task.ransomFloor.IRansomFloorTaskNew;

@Component
public class RansomFloorTaskNew implements IRansomFloorTaskNew {
	
	@Resource
	public ICrmOrderService crmOrderService;
	@Resource
	public ICrmOrderDao crmOrderDao;
	
	private Logger logger = LoggerFactory.getLogger(CrmOrderServiceImpl.class);


	/**
	 * 功能说明：赎楼还款任务
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void ransomFloorEfPaycontrol(){
		// 获取所有理财投资列表
		List<Map> efCustList = crmOrderService.qureyRansomFloorOrders();
		if (efCustList!=null && efCustList.size()!=0){
			for (int i = 0; i < efCustList.size(); i++){
				// 获取对象
				JSONObject obj = JSONObject.fromObject(efCustList.get(i));
				String clearingChannel = obj.get("clearing_channel")==null? "":obj.getString("clearing_channel"); 	// 结算通道
				double pdInvestRate = obj.get("pdInvestRate")==null? 0.0:obj.getDouble("pdInvestRate"); 			// 理财投资利率
				int maxDay = obj.get("maxDay")==null ? 0:obj.getInt("maxDay"); 										// 最大期限
				double pdInvestManageRate = obj.get("pdInvestManageRate")==null? 0.0:obj.getDouble("pdInvestManageRate");// 投资管理费
				String hf_account = obj.get("hf_account")==null? "":obj.getString("hf_account"); 				// 理财人恒丰账户
				String beoId = obj.get("beoId")==null? "":obj.getString("beoId");								// 理财订单ID
				String orderId = obj.get("orderId")==null? "":obj.getString("orderId");							// 理财订单ID
				String cust_name = obj.get("cust_name")==null? "":obj.getString("cust_name");							// 理财人用户名
				if("200007-0003".equals(clearingChannel)){ 
					//hf ，信用公司购买所有
					try {
						CrmOrder order = (CrmOrder)crmOrderDao.findById(CrmOrder.class, orderId);
						crmOrderService.hfRansomFloor(pdInvestRate, maxDay, pdInvestManageRate, hf_account, beoId,order, cust_name);
					} catch (Exception e) {
						logger.warn("赎楼信用公司买投资人："+beoId+",处理失败！");
					}
				}else if("2".equals(clearingChannel)){
					// BOC
					String bank_account = obj.get("bank_account") == null? "":obj.getString("bank_account");
					crmOrderService.bocRansomFloor(pdInvestRate, maxDay, pdInvestManageRate, bank_account, beoId, orderId);
				}
			}
		}
	}
}
