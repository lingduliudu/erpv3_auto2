package com.apt.webapp.dao.impl.ef;

import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.apt.util.ListTool;
import com.apt.util.NumberFormat;
import com.apt.util.NumberUtil;
import com.apt.util.arith.ArithUtil;
import com.apt.util.date.DateUtil;
import com.apt.webapp.base.dao.BaseHibernateDaoSupper;
import com.apt.webapp.dao.ef.IEfPayrecordDao;
import com.apt.webapp.model.bg.ef.BgEfOrders;
import com.apt.webapp.model.bg.ef.BgEfPayrecord;
/**
 * 功能说明：理财还款记录表Dao层实现类
 * @author 乔春峰
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-12
 * Copyright zzl-apt
 */
@Repository
public class EfPayrecordDaoImpl extends BaseHibernateDaoSupper implements IEfPayrecordDao{
	//日志
	private Logger logger = LoggerFactory.getLogger(EfPayrecordDaoImpl.class);
	/**
	 * 功能说明：理财还款记录(正常还款)
	 * yuanhao  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public void updateForNormal(Map efPaycontrol){
		try {
		//开始判断是否已经含有记录了
		String sql  = "select * from bg_ef_payrecord where ef_paycontrol_id='"+efPaycontrol.get("id").toString()+"'";
		List<Map> list = queryBySqlReturnMapList(sql);
		if(ListTool.isNotNullOrEmpty(list)){
			BgEfPayrecord epr = (BgEfPayrecord) findById(BgEfPayrecord.class, list.get(0).get("id").toString());
			epr.setPrincipal(NumberUtil.parseDouble(efPaycontrol.get("princiapl")));			//本金
			epr.setTotalAmt(NumberUtil.parseDouble(efPaycontrol.get("total_amt")));				//投资总金额
			epr.setManagementAmt(NumberUtil.parseDouble(efPaycontrol.get("management_amt")));// 管理费
			if("1".equals(efPaycontrol.get("onLine").toString())){
				epr.setInteRest(NumberUtil.parseDouble(efPaycontrol.get("interest")));				//利息
				epr.setManagementAmt(NumberUtil.parseDouble(efPaycontrol.get("management_amt")));	//管理费
				epr.setType(1);																		//类型 1线上 0 线下
			}
			if("0".equals(efPaycontrol.get("onLine").toString())){
				epr.setInteRest(0d);				//利息
				epr.setManagementAmt(0d);	//管理费
				epr.setCouponAmount(0d);
				epr.setType(0);																		//类型 1线上 0 线下
			}
			epr.setTotalAmt(ArithUtil.add(new Double[]{epr.getPrincipal(),epr.getInteRest(),epr.getCouponAmount(),-epr.getManagementAmt()}));
			update(epr);
		}else{
			BgEfPayrecord epr = new BgEfPayrecord();
			epr.setCreateTime(DateUtil.getCurrentTime());										//创建时间
			epr.setCustId(efPaycontrol.get("cust_id").toString()); 								//bg_customer的id
			epr.setCustInfoId(efPaycontrol.get("cust_info_id").toString());						//bg_cust_info的id
			epr.setEfOrderId(efPaycontrol.get("ef_order_id").toString());						//理财订单id
			epr.setEfPaycontrolId(efPaycontrol.get("id").toString());						    //理财明细
			epr.setEvidenceUrl("");																//凭证url
			if("1".equals(efPaycontrol.get("onLine").toString())){
				epr.setInteRest(NumberUtil.parseDouble(efPaycontrol.get("interest")));				//利息
				epr.setManagementAmt(NumberUtil.parseDouble(efPaycontrol.get("management_amt")));	//管理费
				epr.setCouponAmount(NumberUtil.parseDouble(efPaycontrol.get("use_coupon_interest")));
				epr.setType(1);																		//类型 1线上 0 线下
			}
			if("0".equals(efPaycontrol.get("onLine").toString())){
				epr.setInteRest(0d);				//利息
				epr.setManagementAmt(0d);	//管理费
				epr.setCouponAmount(0d);
				epr.setType(0);																		//类型 1线上 0 线下
			}
			epr.setOperator("") ;																//操作人
			epr.setOverPenalty(0d);																//逾期金额	
			epr.setPeriods(NumberUtil.parseInteger(efPaycontrol.get("periods")));				//期数
			epr.setPrePaymentPenalty(0d);														//提前结清金额
			epr.setPrincipal(NumberUtil.parseDouble(efPaycontrol.get("princiapl")));			//本金
			epr.setTotalAmt(ArithUtil.add(new Double[]{epr.getPrincipal(),epr.getInteRest(),epr.getCouponAmount(),-epr.getManagementAmt()}));
			epr.setUpdateTime(DateUtil.getCurrentTime());										//更新时间
			add(epr);//添加
		
		}
		//判断是否结束
		String overSql ="select * from bg_ef_paycontrol where pay_status = 0 and live_status=1 and ef_order_id = '"+efPaycontrol.get("ef_order_id").toString()+"' ";
		List overlist = queryBySqlReturnMapList(overSql);
		if(ListTool.isNullOrEmpty(overlist)){
				BgEfOrders efOrder = (BgEfOrders) findById(BgEfOrders.class, efPaycontrol.get("ef_order_id").toString());
				efOrder.setClearTime(DateUtil.getCurrentTime());
				efOrder.setUpdateTime(DateUtil.getCurrentTime());
				efOrder.setClearType(2);
				efOrder.setPayStatus(2);
				update(efOrder);
		}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("还款记录生成失败!明细id:"+efPaycontrol.get("id"));
			
		}
	}
	/**
	 * 功能说明：根据类型产生记录
	 * yuanhao  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public void addByModel(Map efPaycontrol){

		try {
		//开始判断是否已经含有记录了
		String sql  = "select * from bg_ef_payrecord where ef_paycontrol_id='"+efPaycontrol.get("id").toString()+"'";
		List<Map> list = queryBySqlReturnMapList(sql);
		if(ListTool.isNotNullOrEmpty(list)){
			BgEfPayrecord epr = (BgEfPayrecord) findById(BgEfPayrecord.class, list.get(0).get("id").toString());
			epr.setPrincipal(NumberUtil.parseDouble(efPaycontrol.get("princiapl")));			//本金
			epr.setManagementAmt(NumberUtil.parseDouble(efPaycontrol.get("management_amt")));// 管理费
			epr.setTotalAmt(NumberFormat.format(ArithUtil.add(new Double[]{
					epr.getPrincipal(),
					epr.getCouponAmount(),
					epr.getInteRest(),
					epr.getPrePaymentPenalty(),
					-epr.getManagementAmt()
			})));				//投资总金额
			update(epr);
		}else{
			BgEfPayrecord epr = new BgEfPayrecord();
			epr.setCreateTime(DateUtil.getCurrentTime());										//创建时间
			epr.setCustId(efPaycontrol.get("cust_id").toString()); 								//bg_customer的id
			epr.setCustInfoId(efPaycontrol.get("cust_info_id").toString());						//bg_cust_info的id
			epr.setEfOrderId(efPaycontrol.get("ef_order_id").toString());						//理财订单id
			epr.setEfPaycontrolId(efPaycontrol.get("id").toString());						    //理财明细
			epr.setEvidenceUrl("");																//凭证url
			if("1".equals(efPaycontrol.get("investment_model").toString())){
				epr.setInteRest(NumberUtil.parseDouble(efPaycontrol.get("interest")));				//利息
				epr.setManagementAmt(NumberUtil.parseDouble(efPaycontrol.get("management_amt")));	//管理费
				epr.setType(1);																		//类型 1线上 0 线下
			}
			if("2".equals(efPaycontrol.get("investment_model").toString())){
				epr.setInteRest(0d);				//利息
				epr.setManagementAmt(0d);	//管理费
				epr.setType(2);																		//类型 1线上 0 线下
			}
			epr.setOperator("") ;																//操作人
			epr.setOverPenalty(0d);																//逾期金额	
			epr.setPeriods(NumberUtil.parseInteger(efPaycontrol.get("periods")));				//期数
			epr.setPrePaymentPenalty(0d);														//提前结清金额
			epr.setPrincipal(NumberUtil.parseDouble(efPaycontrol.get("princiapl")));			//本金
			epr.setUpdateTime(DateUtil.getCurrentTime());										//更新时间
			epr.setCouponAmount(NumberUtil.parseDouble(efPaycontrol.get("use_coupon_interest")));
			epr.setTotalAmt(NumberFormat.format(ArithUtil.add(new Double[]{
					epr.getPrincipal(),
					epr.getCouponAmount(),
					epr.getInteRest(),
					epr.getPrePaymentPenalty(),
					-epr.getManagementAmt()
			})));				//投资总金额
			add(epr);//添加
		
		}
		//判断是否结束
		String overSql ="select * from bg_ef_paycontrol where pay_status = 0 and live_status=1 and ef_order_id = '"+efPaycontrol.get("ef_order_id").toString()+"' ";
		List overlist = queryBySqlReturnMapList(overSql);
		if(ListTool.isNullOrEmpty(overlist)){
				BgEfOrders efOrder = (BgEfOrders) findById(BgEfOrders.class, efPaycontrol.get("ef_order_id").toString());
				efOrder.setClearTime(DateUtil.getCurrentTime());
				efOrder.setUpdateTime(DateUtil.getCurrentTime());
				efOrder.setClearType(2);
				efOrder.setPayStatus(2);
				update(efOrder);
		}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("还款记录生成失败!明细id:"+efPaycontrol.get("id"));
			
		}
	}
}
