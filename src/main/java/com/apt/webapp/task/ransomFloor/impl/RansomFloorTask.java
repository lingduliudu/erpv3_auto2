package com.apt.webapp.task.ransomFloor.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import net.sf.json.JSONObject;

import com.apt.webapp.dao.crm.ICrmOrderDao;
import com.apt.webapp.model.crm.CrmOrder;
import com.apt.webapp.service.crm.ICrmOrderService;
import com.apt.webapp.service.impl.crm.CrmOrderServiceImpl;
import com.apt.webapp.task.ransomFloor.IRansomFloorTask;

@Component
public class RansomFloorTask implements IRansomFloorTask {
	
	@Resource
	public ICrmOrderService crmOrderService;
	@Resource
	public ICrmOrderDao crmOrderDao;
	
	private Logger logger = LoggerFactory.getLogger(CrmOrderServiceImpl.class);
	/**
	 * 执行赎楼还款
	 * 功能说明：该方法实现的功能			
	 * @author sky
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2016-9-1
	 * 修改内容：
	 * 修改注意点：
	 */
	public void executeRansomFloorEfPaycontrol(){
		// 获取所有理财投资列表
		List<Map> efCustList = crmOrderService.searchRansomFloorOrders();
		for (int i = 0; i < efCustList.size(); i++) {
			// 获取对象
			JSONObject obj = JSONObject.fromObject(efCustList.get(i));
			String clearingChannel = obj.getString("clearing_channel"); 	// 结算通道
			double pdInvestRate = obj.getDouble("pdInvestRate"); 			// 理财投资利率
			int maxDay = obj.getInt("maxDay"); 								// 最大期限
			double pdInvestManageRate = obj.getDouble("pdInvestManageRate");// 投资管理费
			double ef_fective_amt = obj.getDouble("ef_fective_amt"); 		// 理财人投资金额
			String fy_account = obj.getString("fy_account"); 				// 理财人富有账户
			String invest_auz_code = "";									// boc 投资编号
			String beoId = obj.getString("beoId");							// 理财订单ID
			String orderId = obj.getString("orderId");							// 理财订单ID
			if("1".equals(clearingChannel)){ // poc ，信用公司购买所有
				try {
					CrmOrder order = (CrmOrder)crmOrderDao.findById(CrmOrder.class, orderId);
					crmOrderService.executePocRansomFloor( pdInvestRate, maxDay, pdInvestManageRate, fy_account, beoId,order);
				} catch (Exception e) {
					logger.warn("赎楼信用公司买投资人："+beoId+",处理失败！");
				}
			}else if("2".equals(clearingChannel)){ // boc 特殊处理
				String bank_account = obj.getString("bank_account");
				String crmOrderId = obj.getString("crmOrderId");
				// 方法待定
				crmOrderService.executeRansomFloorBoc(pdInvestRate, maxDay, pdInvestManageRate, bank_account, beoId, crmOrderId);
			}
		}
	}
}
