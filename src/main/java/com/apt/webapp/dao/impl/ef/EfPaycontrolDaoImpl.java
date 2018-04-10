package com.apt.webapp.dao.impl.ef;

import java.util.List;
import java.util.Map;


import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.apt.util.ListTool;
import com.apt.util.MessageTemplete;
import com.apt.util.NumberFormat;
import com.apt.util.NumberUtil;
import com.apt.util.date.DateUtil;
import com.apt.util.mail.Mail;
import com.apt.webapp.base.dao.BaseHibernateDaoSupper;
import com.apt.webapp.dao.ef.IEfPaycontrolDao;
import com.apt.webapp.model.bg.ef.BgCustomer;
import com.apt.webapp.model.bg.ef.BgEfOrders;
import com.apt.webapp.model.bg.ef.BgEfPaycontrol;
import com.apt.webapp.model.bg.ef.BgSysMessage;
import com.apt.webapp.model.bg.ef.BgSysMessageContent;

/**
 * 功能说明：理财还款明细表Dao层实现类
 * @author 乔春峰
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-12
 * Copyright zzl-apt
 */
@Repository
public class EfPaycontrolDaoImpl extends BaseHibernateDaoSupper implements IEfPaycontrolDao{
	//日志
	private Logger logger = LoggerFactory.getLogger(EfPaycontrolDaoImpl.class);
	/**
	 * 功能说明： 更新理财明细
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
	public void updateForNormal(Map efPaycontrol){
		BgEfPaycontrol epc = null;
		try {
			//订单线上类型 1 线上(贝格平台) 0 原线下老单子 2：线上（中资联财富)
			String online_type = efPaycontrol.get("online_type").toString();
			epc = (BgEfPaycontrol) findById(BgEfPaycontrol.class, efPaycontrol.get("id").toString());
			epc.setPayStatus(1); 				//结清
			epc.setSurplusManagementAmt(0d); 	//管理费
			epc.setCouponInterest(0d); 			//抵用卷利息
			epc.setSurplusInterest(0d); 		//利息
			if(efPaycontrol!=null && efPaycontrol.get("investment_model")!=null && "1".equals(efPaycontrol.get("investment_model").toString())){
				//开始进行站内信通知
				try{
					BgCustomer bgCustomer  = (BgCustomer) findById(BgCustomer.class,epc.getCustId());
					BgSysMessageContent bsmc = new BgSysMessageContent();
					bsmc.setCreateTime(DateUtil.getCurrentTime());
					String Princiapl = NumberFormat.formatDouble(epc.getPrinciapl());
					String Interest = NumberFormat.formatDouble(epc.getInterest());
					String ManagementAmt = NumberFormat.formatDouble(epc.getManagementAmt());
					String totalMoney="0";
					if(epc.getUseCouponInterest()!=null){
						totalMoney = NumberFormat.formatDouble(epc.getPrinciapl()+epc.getInterest()+epc.getUseCouponInterest()-epc.getManagementAmt());
					}else{
						totalMoney = NumberFormat.formatDouble(epc.getPrinciapl()+epc.getInterest()-epc.getManagementAmt());
					}
					String couponInterest = "0";
					if(epc.getUseCouponInterest()!=null && epc.getUseCouponInterest()>0){
						couponInterest = NumberFormat.formatDouble(epc.getUseCouponInterest());
					}
					JSONObject paramJson = new JSONObject();
					paramJson.put("msgType","1");
					String isLast  = "SELECT * from bg_ef_paycontrol where ef_order_id='"+epc.getEfOrderId()+"' and live_status=1 and pay_time>'"+epc.getPayTime()+"' and pay_status=0 ";
					List list  = queryBySqlReturnMapList(isLast);
					if(ListTool.isNullOrEmpty(list)){
						paramJson.put("msgType","2");
					}
					//
					paramJson.put("name", bgCustomer.getUsername());
					BgEfOrders  eo  = (BgEfOrders) findById(BgEfOrders.class, epc.getEfOrderId());
					paramJson.put("number", eo.getOrderNumber());
					paramJson.put("Periods",epc.getPeriods());
					paramJson.put("Princiapl", Princiapl);
					paramJson.put("Interest", Interest);
					paramJson.put("ManagementAmt", ManagementAmt);
					paramJson.put("totalMoney", totalMoney);
					
					paramJson.put("couponInterest", couponInterest);
					JSONObject resultJson  = MessageTemplete.getMsg(paramJson);
					//判断是否需要发邮件
					String emailSql = "select * from bg_sys_meg_receive_set where cust_id='"+bgCustomer.getId()+"' and sync_email='1'";
					List emailList = queryBySqlReturnMapList(emailSql);
					if(ListTool.isNotNullOrEmpty(emailList) && "1".equals(online_type)){ //不是空的话则需要发送邮件
						try {
							Mail.sentMail(resultJson.getString("title"), resultJson.getString("content"), bgCustomer.getCustEmail());
						} catch (Exception e) {
							logger.warn(e.getMessage(),e);
							logger.warn("发送邮件失败!明细id:"+epc.getId());
						}
					}
					bsmc.setMsgContent(resultJson.getString("content"));
					bsmc.setMsgTitle(resultJson.getString("title"));
					bsmc.setPublishId("");
					bsmc.setUpdateTime(DateUtil.getCurrentTime());
					if ("1".equals(online_type)) {
						bsmc.setPlatformType(1);
					}else if ("2".equals(online_type)) {
						bsmc.setPlatformType(2);
					}
					add(bsmc);
					BgSysMessage bsm  = new BgSysMessage();
					bsm.setCustId(epc.getCustId());
					bsm.setEfOrderId(epc.getEfOrderId());
					bsm.setEnabled(1);
					bsm.setMsgClass(2);
					bsm.setMsgContentId(bsmc.getId());
					bsm.setMsgType(5);
					bsm.setOrderId(efPaycontrol.get("crmOrderId").toString());
					bsm.setReadStatus(0);
					if ("1".equals(online_type)) {
						bsm.setPlatformType(1);
					}else if ("2".equals(online_type)) {
						bsm.setPlatformType(2);
					}
					add(bsm);
					logger.warn("站内信生成成功!明细id:"+epc.getId());
				}catch (Exception e) {
					logger.warn(e.getMessage(),e);
					logger.warn("站内信生成失败!明细id:"+epc.getId());
				}
			}
			epc.setSurplusPrincipal(0d);		//本金
			update(epc);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("还款明细更新失败!明细id:"+epc.getId());
		}
		
	}
	/**
	 * 功能说明：更加模式来更新明细并产生记录
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
	public void updateForNormalByModel(Map efPaycontrol){

		BgEfPaycontrol epc = null;
		try {
			//订单线上类型 1 线上(贝格平台) 0 原线下老单子 2：线上（中资联财富)
			String online_type = efPaycontrol.get("online_type").toString();
			epc = (BgEfPaycontrol) findById(BgEfPaycontrol.class, efPaycontrol.get("id").toString());
			epc.setPayStatus(1); 				//结清
			epc.setSurplusManagementAmt(0d); 	//管理费
			epc.setCouponInterest(0d); 			//抵用卷利息
			epc.setSurplusInterest(0d); 		//利息
				if("1".equals(efPaycontrol.get("investment_model").toString())){  //直投
				//开始进行站内信通知
				try{
					BgCustomer bgCustomer  = (BgCustomer) findById(BgCustomer.class,epc.getCustId());
					BgSysMessageContent bsmc = new BgSysMessageContent();
					bsmc.setCreateTime(DateUtil.getCurrentTime());
					String Princiapl = NumberFormat.formatDouble(epc.getPrinciapl());
					String Interest = NumberFormat.formatDouble(epc.getInterest());
					String ManagementAmt = NumberFormat.formatDouble(epc.getManagementAmt());
					String totalMoney="0";
					if(epc.getUseCouponInterest()!=null){
						totalMoney = NumberFormat.formatDouble(epc.getPrinciapl()+epc.getInterest()+epc.getUseCouponInterest()-epc.getManagementAmt());
					}else{
						totalMoney = NumberFormat.formatDouble(epc.getPrinciapl()+epc.getInterest()-epc.getManagementAmt());
					}
					String couponInterest = "0";
					if(epc.getUseCouponInterest()!=null && epc.getUseCouponInterest()>0){
						couponInterest = NumberFormat.formatDouble(epc.getUseCouponInterest());
					}
					JSONObject paramJson = new JSONObject();
					paramJson.put("msgType","1");
					String isLast  = "SELECT * from bg_ef_paycontrol where ef_order_id='"+epc.getEfOrderId()+"' and live_status=1 and pay_time>'"+epc.getPayTime()+"' and pay_status=0 ";
					List list  = queryBySqlReturnMapList(isLast);
					if(ListTool.isNullOrEmpty(list)){
						paramJson.put("msgType","2");
					}
					//
					paramJson.put("name", bgCustomer.getUsername());
					BgEfOrders  eo  = (BgEfOrders) findById(BgEfOrders.class, epc.getEfOrderId());
					paramJson.put("number", eo.getOrderNumber());
					paramJson.put("Periods",epc.getPeriods());
					paramJson.put("Princiapl", Princiapl);
					paramJson.put("Interest", Interest);
					paramJson.put("ManagementAmt", ManagementAmt);
					paramJson.put("totalMoney", totalMoney);
					
					paramJson.put("couponInterest", couponInterest);
					JSONObject resultJson  = MessageTemplete.getMsg(paramJson);
					//判断是否需要发邮件
					String emailSql = "select * from bg_sys_meg_receive_set where cust_id='"+bgCustomer.getId()+"' and sync_email='1'";
					List emailList = queryBySqlReturnMapList(emailSql);
					if(ListTool.isNotNullOrEmpty(emailList) && "1".equals(online_type)){ //不是空的话则需要发送邮件
						try {
							Mail.sentMail(resultJson.getString("title"), resultJson.getString("content"), bgCustomer.getCustEmail());
						} catch (Exception e) {
							logger.warn(e.getMessage(),e);
							logger.warn("发送邮件失败!明细id:"+epc.getId());
						}
					}
					bsmc.setMsgContent(resultJson.getString("content"));
					bsmc.setMsgTitle(resultJson.getString("title"));
					bsmc.setPublishId("");
					bsmc.setUpdateTime(DateUtil.getCurrentTime());
					if ("1".equals(online_type)) {
						bsmc.setPlatformType(1);
					}else if ("2".equals(online_type)) {
						bsmc.setPlatformType(2);
					}
					add(bsmc);
					BgSysMessage bsm  = new BgSysMessage();
					bsm.setCustId(epc.getCustId());
					bsm.setEfOrderId(epc.getEfOrderId());
					bsm.setEnabled(1);
					bsm.setMsgClass(2);
					bsm.setMsgContentId(bsmc.getId());
					bsm.setMsgType(5);
					bsm.setOrderId(efPaycontrol.get("crmOrderId").toString());
					bsm.setReadStatus(0);
					if ("1".equals(online_type)) {
						bsm.setPlatformType(1);
					}else if ("2".equals(online_type)) {
						bsm.setPlatformType(2);
					}
					add(bsm);
					logger.warn("站内信生成成功!明细id:"+epc.getId());
				}catch (Exception e) {
					logger.warn(e.getMessage(),e);
					logger.warn("站内信生成失败!明细id:"+epc.getId());
				}
			}
			epc.setSurplusPrincipal(0d);		//本金
			update(epc);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("还款明细更新失败!明细id:"+epc.getId());
		}
	}
	/**
	 * 功能说明： 获得Boc当日待还明细
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
	public List<Map> getCurrentControlsBoc(){
		String sql =	
						"SELECT "+
						"ep.*, "+
						"bci.bank_account, "+
						"bci.fy_account, "+
						"eo.clearing_channel "+ 
						"from  "+
						"ef_paycontrol ep, "+
						"bg_cust_info bci, "+
						"ef_orders eo "+
						"where  "+
						"ep.ef_order_id=eo.id AND "+
						"ep.cust_info_id = bci.id AND "+
						"ep.pay_status=0 AND "+
						"eo.clearing_channel='2' AND "+
						"ep.pay_time LIKE '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' ";
		return queryBySqlReturnMapList(sql);
		
	}
	/**
	 * 功能说明： 获得线下当日待还明细
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
	public List<Map> getCurrentControlsPoc(){
		String sql =	
						"SELECT "+
						"ep.*, "+
						"bci.bank_account, "+
						"bci.fy_account, "+
						"eo.clearing_channel "+ 
						"from  "+
						"ef_paycontrol ep, "+
						"bg_cust_info bci, "+
						"ef_orders eo "+
						"where  "+
						"ep.ef_order_id=eo.id AND "+
						"ep.cust_info_id = bci.id AND "+
						"ep.pay_status=0 AND "+
						"eo.clearing_channel='1' AND "+
						"ep.pay_time LIKE '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' ";
		return queryBySqlReturnMapList(sql);
		
	}
	/**
	 * 功能说明： 获得当日待还明细
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月31日 15:28:08
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getCurrentControlsPrincipal(){
		String sql =	
						"SELECT "+
						"ep.*, "+
						"bci.bank_account, "+
						"bci.fy_account, "+
						"eo.clearing_channel,eo.ef_applay_id "+ 
						"from  "+
						"ef_paycontrol ep, "+
						"bg_cust_info bci, "+
						"ef_orders eo "+
						"where  "+
						"ep.ef_order_id=eo.id AND "+
						"ep.cust_info_id = bci.id AND "+
						"ep.pay_status=0 AND "+
						"ep.surplus_principal>0 AND "+
						"ep.ef_order_id in (select ef_order_id from bg_ef_orders where pay_status<>1 and ef_order_id is not null GROUP BY ef_order_id ) AND "+
						"ep.pay_time <= '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"' ";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明： 获得当日待还明细
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月31日 15:28:08
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getCurrentControlsInterest(){
		String sql =	
				"SELECT "+
						"ep.*, "+
						"bci.bank_account, "+
						"bci.fy_account, "+
						"bci.hf_account, "+
						"bci.cust_name, "+
						"eo.platform_type,eo.clearing_channel,eo.order_number,eo.ef_applay_id "+ 
						"from  "+
						"ef_paycontrol ep, "+
						"bg_cust_info bci, "+
						"ef_orders eo "+
						"where  "+
						"ep.ef_order_id=eo.id AND "+
						"(eo.order_flag is null or eo.order_flag!=1) AND "+
						"ep.cust_info_id = bci.id AND "+
						"ep.pay_status=0 AND "+
						"ep.pay_time is not null AND "+
//						"ep.surplus_principal<=0 AND "+
//						"ep.ef_order_id in (select ef_order_id from bg_ef_orders where pay_status<>1 and ef_order_id is not null GROUP BY ef_order_id ) AND "+
						"ep.pay_time <= '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+" 23:59:59' ";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：获得自动投标的序列号
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
	public String findAutoSerino(String cust_info_id){
		String sql  = "select * from bg_automatic_bid where cust_info_id='"+cust_info_id+"' and status=1";
		return queryBySqlReturnMapList(sql).get(0).get("auto_serino").toString();
	}
	/**
	 * 功能说明：增加邀请人的收益查看
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
	public JSONObject getInviterData(String income_cust_info_id){
		String sql = "SELECT bci.* from bg_cust_info bci where bci.id='"+income_cust_info_id+"' ";
		List<Map> list  = queryBySqlReturnMapList(sql);
		if(ListTool.isNotNullOrEmpty(list)){ //不是空
			return JSONObject.fromObject(list.get(0));
		}
		return new JSONObject();
	}
	/**
	 * 功能说明：获得需要结清的资产包数据
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 yuanhao
	 * 创建日期：2016年8月31日 15:27:12
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getNeedClearPKOrders(){
		String sql = 	"SELECT id from pkg_order where id not in ( "+
						"	SELECT "+
						"		po.id "+
						"	FROM "+
						"		pkg_order po, "+
						"		pkg_cust_eforder pce, "+
						"		ef_orders eo "+
						"	WHERE "+
						"		pce.ef_orders_id = eo.id "+
						"	AND pce.invest_type = '1' "+
						"	AND pce.status  in (1,2) "+
						"	AND pce.pkg_order_id = po.id "+
						"	AND po.ORDER_TRADE_STATUS = '1' "+
						"	AND po.MATURE_TIME < '"+DateUtil.getCurrentTime()+"' "+
						"	AND eo.pay_status=1 GROUP BY po.id "+
						") and ORDER_TRADE_STATUS = '1' and MATURE_TIME < '"+DateUtil.getCurrentTime()+"' ";
		return queryBySqlReturnMapList(sql);
	}
}
