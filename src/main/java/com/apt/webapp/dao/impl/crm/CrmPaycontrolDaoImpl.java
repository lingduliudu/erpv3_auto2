package com.apt.webapp.dao.impl.crm;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.apt.util.ChkUtil;
import com.apt.util.date.DateUtil;
import com.apt.webapp.base.dao.BaseHibernateDaoSupper;
import com.apt.webapp.dao.crm.ICrmPaycontrolDao;
import com.apt.webapp.model.crm.CrmPaycontrol;
import com.apt.webapp.service.impl.ef.BgEfOrderServiceImpl;
/**
 * 功能说明：信贷还款明细表     dao层实现类
 * @author 乔春峰
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-13
 * Copyright zzl-apt
 */
@Repository
public class CrmPaycontrolDaoImpl extends BaseHibernateDaoSupper implements ICrmPaycontrolDao{
	//日志
	private Logger logger = LoggerFactory.getLogger(CrmPaycontrolDaoImpl.class);
	/**
	 * 功能说明：信贷还款明细表对象查询
	 * 创建人：yuanhao
	 * 创建时间：2015-10-13
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public void updateForNormal(String crmOrderId){
		try {
			if(ChkUtil.isEmpty(crmOrderId)){return ;}
			String sql = "SELECT * from crm_paycontrol where crm_order_id= '"+crmOrderId+"' and status =0 and repayment_time like '"+DateUtil.getCurrentTime()+"%'";
			List<Map> list = queryBySqlReturnMapList(sql);
			String id = list.get(0).get("id").toString();
			CrmPaycontrol cpc = (CrmPaycontrol) findById(CrmPaycontrol.class, id);
			cpc.setStatus(1); 			//状态修改为结清
			cpc.setRemainCapital(0d);	//剩余本金归零
			cpc.setRemainAccrual(0d);	//剩余利息归零
			cpc.setRemainManageFee(0d);	//剩余管理费归零
			update(cpc);				//更新
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
		}
		
	}
}
