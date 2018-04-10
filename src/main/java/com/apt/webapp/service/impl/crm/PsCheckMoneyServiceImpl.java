package com.apt.webapp.service.impl.crm;

import javax.annotation.Resource;
import javax.jws.WebService;


import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apt.util.ChkUtil;
import com.apt.util.date.DateUtil;
import com.apt.webapp.dao.crm.IPsCheckMoneyDao;
import com.apt.webapp.model.crm.PsCheckMoney;
import com.apt.webapp.service.crm.IPsCheckMoneyService;



/**
 * 功能说明：  还款记录   接口 实现类
 * @author yuanhao
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-13
 * Copyright zzl-apt
 */
@Service
@Transactional
@WebService(serviceName = "psCheckMoneyService", endpointInterface = "com.apt.webapp.service.crm.IPsCheckMoneyService")
public class PsCheckMoneyServiceImpl  implements IPsCheckMoneyService{
	@Resource
	private IPsCheckMoneyDao checkMoneyDao;
	//日志
	private static Logger logger = LoggerFactory.getLogger(PsCheckMoneyServiceImpl.class);
	/**
	 * 功能说明：保存
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 yuanhao
	 * 创建日期：2016年3月14日 11:12:54
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public synchronized String save(JSONObject json){
		try{
			if(json.getDouble("money")<=0){
				return "";
			}
			PsCheckMoney psCheckMoney = new PsCheckMoney();
			psCheckMoney.setCardNo(json.getString("cardNo"));
			psCheckMoney.setCreateTime(DateUtil.getCurrentTime());
			psCheckMoney.setCustInfoId(json.getString("cust_info_id"));
			psCheckMoney.setMoney(json.getDouble("money"));
			psCheckMoney.setMoneyType(json.getString("money_type"));
			psCheckMoney.setOperationType(json.getString("operation_type"));
			psCheckMoney.setOperator("");
			psCheckMoney.setStatus(json.getString("status"));
			psCheckMoney.setPersonType(json.getString("person_type"));
			psCheckMoney.setType(json.getString("type"));
			psCheckMoney.setCrmOrderId(json.getString("crm_order_id"));
			checkMoneyDao.add(psCheckMoney);
			return psCheckMoney.getId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("资金记录失败!");
		}
		return "";
	}
	/**
	 * 功能说明：更新
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 yuanhao
	 * 创建日期：2016年3月14日 11:12:54
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public synchronized void updateStatusById(String id){
		try{
			if(ChkUtil.isEmpty(id)){
				return;
			}
			PsCheckMoney psCheckMoney = (PsCheckMoney) checkMoneyDao.findById(PsCheckMoney.class, id);
			psCheckMoney.setStatus("1");
			checkMoneyDao.update(psCheckMoney);
		}catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("资金记录修改状态失败!");
		}
	}
}
