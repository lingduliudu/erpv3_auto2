package com.apt.webapp.service.impl.ef;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apt.util.NumberUtil;
import com.apt.util.date.DateUtil;
import com.apt.webapp.dao.ef.IEfPaycontrolDao;
import com.apt.webapp.dao.ef.IEfPayrecordDao;
import com.apt.webapp.model.ef.EfOrders;
import com.apt.webapp.model.ef.EfPaycontrol;
import com.apt.webapp.model.ef.EfPayrecord;
import com.apt.webapp.service.ef.IEfPayrecordService;


/**
 * 功能说明： 合同扫描件素材 service层   实现类
 * @author weiyingni
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-27
 * Copyright zzl-apt
 */
@Service
@Transactional
@WebService(serviceName = "efPayrecordService", endpointInterface = "com.apt.webapp.service.ef.IEfPayrecordService")
public class EfPayrecordServiceImpl implements IEfPayrecordService {
	//日志
	private Logger logger = LoggerFactory.getLogger(EfPayrecordServiceImpl.class);
	@Resource
	private IEfPayrecordDao efPayrecordDao;
	/**
	 * 功能说明： 结清
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
	@Transactional
	public void addNormal(Map efPaycontrol){
		EfPayrecord efPayrecord = new EfPayrecord();
		try {
			EfPaycontrol epc  = (EfPaycontrol) efPayrecordDao.findById(EfPaycontrol.class, efPaycontrol.get("id").toString());
			if(NumberUtil.parseDouble(efPaycontrol.get("surplus_interest"))>0 || NumberUtil.parseDouble(efPaycontrol.get("surplus_principal"))>0 || NumberUtil.parseDouble(efPaycontrol.get("princiapl"))>0){
				efPayrecord.setCreateTime(DateUtil.getCurrentTime());
				efPayrecord.setCustId(efPaycontrol.get("cust_id").toString());
				efPayrecord.setCustInfoId(efPaycontrol.get("cust_info_id").toString());
				efPayrecord.setEfOrderId(efPaycontrol.get("ef_order_id").toString());
				efPayrecord.setEfPaycontrolId(efPaycontrol.get("id").toString());
				efPayrecord.setEvidenceUrl("");
				efPayrecord.setInteRest(NumberUtil.parseDouble(efPaycontrol.get("surplus_interest")));
				efPayrecord.setManagementAmt(NumberUtil.parseDouble(efPaycontrol.get("surplus_management_amt")));
				efPayrecord.setOperator("");
				efPayrecord.setOverPenalty(0d);
				efPayrecord.setPeriods(NumberUtil.parseInteger(efPaycontrol.get("periods")));
				efPayrecord.setPrePaymentPenalty(0d);
				efPayrecord.setPrincipal(0d);
				efPayrecord.setRateCoupon(0d);
				if(epc.getSurplusRateCoupon() !=null ){
					efPayrecord.setRateCoupon(NumberUtil.parseDouble(efPaycontrol.get("surplus_rate_coupon")));
				}
				if(epc.getSurplusPrincipal() == 0){
					efPayrecord.setPrincipal(epc.getPrinciapl());
				}
				efPayrecord.setTotalAmt(NumberUtil.parseDouble(efPaycontrol.get("total_amt")));
				efPayrecord.setUpdateTime(DateUtil.getCurrentTime());
				if (efPayrecord.getInteRest()>0 || efPayrecord.getPrincipal()>0) {
					efPayrecordDao.add(efPayrecord);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
		}
	
	}
	/**
	 * 功能说明：获得理财记录
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
	@Transactional
	public List<Map> findListByEfOrderId(String id){
		String sql =  "SELECT * from bg_ef_payrecord where ef_order_id='"+id+"' ";
		return efPayrecordDao.queryBySqlReturnMapList(sql);
	}
}
