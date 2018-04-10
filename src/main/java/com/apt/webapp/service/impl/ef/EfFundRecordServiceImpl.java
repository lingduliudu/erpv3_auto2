package com.apt.webapp.service.impl.ef;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apt.util.ListTool;
import com.apt.util.NumberUtil;
import com.apt.util.date.DateUtil;
import com.apt.webapp.dao.ef.IEfFundRecordDao;
import com.apt.webapp.model.ef.EfFundRecord;
import com.apt.webapp.service.ef.IEfFundRecordService;


/**
 * 功能说明：v3的客户表       service层 实现类
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明
 * @author weiyz
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-12
 * Copyright zzl-apt
 */
@Service
@Transactional
@WebService(serviceName = "efFundRecordService", endpointInterface = "com.apt.webapp.service.ef.IEfFundRecordService")
public class EfFundRecordServiceImpl  implements IEfFundRecordService {
	private static Logger logger = LoggerFactory.getLogger(EfFundRecordServiceImpl.class);
	@Resource
	private IEfFundRecordDao efFundRecordDao; 
	
	/**
	 * 功能说明：增加金额		
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 袁浩
	 * 创建日期：2015年10月20日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void addNormal(Map efPaycontrol){
		try {
			EfFundRecord efr = new EfFundRecord();
			efr.setCreateTime(DateUtil.getCurrentTime());
			efr.setCustId(efPaycontrol.get("cust_id").toString());
			efr.setCustInfoId(efPaycontrol.get("cust_info_id").toString());
			efr.setEfApplayId("");
			efr.setEfOrderId(efPaycontrol.get("lineEfOrderId").toString());
			String sql = "SELECT ef_applay_id from ef_orders where id='"+efPaycontrol.get("lineEfOrderId").toString()+"'";
			List<Map> list =  efFundRecordDao.queryBySqlReturnMapList(sql);
			if(ListTool.isNotNullOrEmpty(list)){
				efr.setEfApplayId(list.get(0).get("ef_applay_id").toString());
			}
			efr.setEmpId("");
			efr.setBgEfOrderId(efPaycontrol.get("ef_order_id").toString());
			efr.setMoney(NumberUtil.parseDouble(efPaycontrol.get("surplus_principal")));
			efr.setRecordStatus(1);
			efr.setRecordType("还款");
			efFundRecordDao.add(efr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
		}
	}
	/**
	 * 功能说明：提前结清的金额	
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 袁浩
	 * 创建日期：2015年10月20日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void addAdvance(Map efPaycontrol){
		try {
			EfFundRecord efr = new EfFundRecord();
			efr.setCreateTime(DateUtil.getCurrentTime());
			efr.setCustId(efPaycontrol.get("cust_id").toString());
			efr.setCustInfoId(efPaycontrol.get("cust_info_id").toString());
			efr.setEfApplayId("");
			efr.setEfOrderId(efPaycontrol.get("lineEfOrderId").toString());
			String sql = "SELECT ef_applay_id from ef_orders where id='"+efPaycontrol.get("lineEfOrderId").toString()+"'";
			List<Map> list =  efFundRecordDao.queryBySqlReturnMapList(sql);
			if(ListTool.isNotNullOrEmpty(list)){
				efr.setEfApplayId(list.get(0).get("ef_applay_id").toString());
			}
			efr.setEmpId("");
			efr.setBgEfOrderId(efPaycontrol.get("ef_order_id").toString());
			efr.setMoney(NumberUtil.parseDouble(efPaycontrol.get("advanceMoney")));
			efr.setRecordStatus(1);
			efr.setRecordType("还款");
			efFundRecordDao.add(efr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
		}
	}
	
}
