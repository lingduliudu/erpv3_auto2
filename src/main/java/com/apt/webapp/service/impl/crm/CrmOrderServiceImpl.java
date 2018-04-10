package com.apt.webapp.service.impl.crm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apt.util.ChkUtil;
import com.apt.util.ListTool;
import com.apt.util.MessageTemplete;
import com.apt.util.NumberFormat;
import com.apt.util.NumberUtil;
import com.apt.util.StaticData;
import com.apt.util.SysConfig;
import com.apt.util.WebServiceUtil;
import com.apt.util.pocTool;
import com.apt.util.arith.ArithUtil;
import com.apt.util.date.DateUtil;
import com.apt.util.singature.SignatureUtil;
import com.apt.util.sms.SmsUtil;
import com.apt.webapp.dao.bg.ef.IBgEfOrderDao;
import com.apt.webapp.dao.crm.ICrmOrderDao;
import com.apt.webapp.dao.ef.IZZLEfPaycontrolDao;
import com.apt.webapp.model.bg.ef.BgCustInfo;
import com.apt.webapp.model.bg.ef.BgCustomer;
import com.apt.webapp.model.bg.ef.BgEfOrders;
import com.apt.webapp.model.bg.ef.BgEfPaycontrol;
import com.apt.webapp.model.bg.ef.BgEfPayrecord;
import com.apt.webapp.model.bg.ef.BgReferralIncomeRecord;
import com.apt.webapp.model.bg.ef.BgScoreRecord;
import com.apt.webapp.model.bg.ef.BgSysMessage;
import com.apt.webapp.model.bg.ef.BgSysMessageContent;
import com.apt.webapp.model.crm.CrmApplay;
import com.apt.webapp.model.crm.CrmOrder;
import com.apt.webapp.model.crm.CrmPaycontrol;
import com.apt.webapp.model.crm.CrmPayrecoder;
import com.apt.webapp.model.crm.PkgCustCrmorder;
import com.apt.webapp.model.ef.ZZLEfPaycontrol;
import com.apt.webapp.model.ef.ZZLEfPayrecord;
import com.apt.webapp.service.bg.ef.IBgEfOrderService;
import com.apt.webapp.service.crm.ICrmOrderService;

@Service
@Transactional
@WebService(serviceName = "crmOrderService", endpointInterface = "com.apt.webapp.service.crm.ICrmOrderService")
public class CrmOrderServiceImpl implements ICrmOrderService {
	//日志
	private static Logger logger = LoggerFactory.getLogger(CrmOrderServiceImpl.class);
	@Resource
	public ICrmOrderDao crmOrderDao;
	@Resource
	private IZZLEfPaycontrolDao efPaycontrolDao;
	@Resource
	private IBgEfOrderService bgEfOrderService;
	@Resource
	private IBgEfOrderDao bgEfOrderDao;
	
	/**
	 * 功能说明：执行v3BOC的service			
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
	public List<Map> getCurrentPaycontrol(){
		return crmOrderDao.getCurrentPaycontrol();
	}
	public Map getCrmOrderById(String crmOrderId){
		try {
			String sql ="select id,order_prd_number from crm_order where id ='"+crmOrderId+"'";
			return crmOrderDao.queryBySqlReturnMapList(sql).get(0);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("获得订单时报错:订单id="+crmOrderId);
		}
		return null;
	}
	/**
	 * 功能说明：获得POC的信贷明细		
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
	public List<Map> getCurrentPaycontrolsPoc(){
		return crmOrderDao.getCurrentPaycontrolsPoc();
	}
	/**
	 * 功能说明：获得定投boc的信贷明细	
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
	public List<Map> getCurrentImmePaycontrolsBoc(){
		return crmOrderDao.getCurrentImmePaycontrolsBoc();
	}
	/**
	 * 功能说明：获得定投poc的信贷明细	
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
	public List<Map> getCurrentImmePaycontrolsPoc(){
		return crmOrderDao.getCurrentImmePaycontrolsPoc();
	}
	/**
	 * 功能说明：获得线上的信贷明细	
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
	public List<Map> getCurrentDetePaycontrolsBoc(){
		return crmOrderDao.getCurrentDetePaycontrolsBoc();
	}
	/**
	 * 功能说明：获得线上的信贷明细	
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
	public List<Map> getCurrentDetePaycontrolsPoc(){
		return crmOrderDao.getCurrentDetePaycontrolsPoc();
	}
	/**
	 * 功能说明：判断是不是最后一期	
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
	public boolean lastTimes(String crmOrderId,String controlId){
		return crmOrderDao.lastTimes(crmOrderId,controlId);
	}
	/**
	 * 功能说明：结清当期明细
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
	public void clearCurrentPaycontrol(String paycontrolId){
		try {
			CrmPaycontrol cpc = (CrmPaycontrol) efPaycontrolDao.findById(CrmPaycontrol.class, paycontrolId);
			//产生记录 
			CrmPayrecoder cpr = new CrmPayrecoder();
			cpr.setCertificateUrl("");
			cpr.setCreateTime(DateUtil.getCurrentTime());
			cpr.setCrmOrderId(cpc.getCrmOrderId());
			cpr.setCustId(cpc.getCustId());
			cpr.setEmpId("");
			cpr.setManageFee(cpc.getRemainManageFee());
			cpr.setOverdueInterest(cpc.getOverdueInterest());
			cpr.setOverdueViolateMoney(cpc.getOverdueViolateMoney());
			cpr.setPaycontrolId(cpc.getId());
			cpr.setPrepaymentViolateMoney(0d);
			cpr.setRemark("");
			cpr.setRemainFee(0d);
			cpr.setRepaymentType(0);
			cpr.setShouldAccrual(cpc.getRemainAccrual());
			cpr.setShouldCAPITAL(cpc.getRemainCapital());
			cpr.setShouldInterest(cpc.getRemainInterest());
			cpr.setShouldMONEY(ArithUtil.add(new Double[]{
					cpc.getRemainAccrual(),
					cpc.getRemainCapital(),
					cpc.getRemainInterest(),
					cpc.getRemainManageFee()
			}));
			efPaycontrolDao.add(cpr);
			
			//更新信贷
			cpc.setStatus(1);
			cpc.setRemainAccrual(0d);
			cpc.setRemainCapital(0d);
			cpc.setRemainInterest(0d);
			cpc.setRemainManageFee(0d);
			efPaycontrolDao.update(cpc);
			//开始判断是否可以结清
			CrmOrder crmOrder = (CrmOrder) efPaycontrolDao.findById(CrmOrder.class, cpc.getCrmOrderId());
			String sql ="select * from crm_paycontrol where crm_order_id='"+cpc.getCrmOrderId()+"' and status in (0,2)";
			List list = efPaycontrolDao.queryBySqlReturnMapList(sql);
			//长度为0代表该笔订单已经结束了
			if(ListTool.isNullOrEmpty(list)){
				crmOrder.setOrderTradeStatus(4);
				crmOrder.setClearType(1);
				crmOrder.setFinishTime(DateUtil.getCurrentTime());
				efPaycontrolDao.update(crmOrder);
				try{
					CrmApplay apply = (CrmApplay) efPaycontrolDao.findById(CrmApplay.class,crmOrder.getCrmApplayId());
					if(ChkUtil.isNotEmpty(apply)){
						apply.setTradeStatus(4);
						apply.setRepaymentStatus(1);
						apply.setStatus(4);
						efPaycontrolDao.update(apply);
					}
				}catch (Exception e) {
				}
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("冻结成功,结清借款人明细时失败!明细id="+paycontrolId);
		}
		
	}
	/**
	 * 功能说明：结清当期明细
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
	public void repayNormalByPoc(String crmPaycontrolId){
		try {
			CrmPaycontrol cpc = (CrmPaycontrol) efPaycontrolDao.findById(CrmPaycontrol.class, crmPaycontrolId);
			if (cpc.getStatus() == 1) {
				return;
			}
			//产生记录 
			CrmPayrecoder cpr = new CrmPayrecoder();
			cpr.setCertificateUrl("");
			cpr.setCreateTime(DateUtil.getCurrentTime());
			cpr.setCrmOrderId(cpc.getCrmOrderId());
			cpr.setCustId(cpc.getCustId());
			cpr.setCustInfoId(cpc.getCustInfoId());
			cpr.setEmpId("");
			cpr.setManageFee(cpc.getRemainManageFee());
			cpr.setOverdueInterest(cpc.getOverdueInterest());
			cpr.setOverdueViolateMoney(cpc.getOverdueViolateMoney());
			cpr.setPaycontrolId(cpc.getId());
			cpr.setPrepaymentViolateMoney(0d);
			cpr.setRemark("");
			cpr.setRepaymentType(0);
			cpr.setRemainFee(0d);
			cpr.setShouldAccrual(cpc.getRemainAccrual());
			cpr.setShouldCAPITAL(cpc.getRemainCapital());
			cpr.setShouldInterest(cpc.getRemainInterest());
			cpr.setShouldMONEY(ArithUtil.add(new Double[]{
					cpc.getRemainAccrual(),
					cpc.getRemainCapital(),
					cpc.getRemainInterest(),
					cpc.getRemainManageFee()
			}));
			cpr.setShouldInterest(cpc.getRemainInterest());
			cpr.setOperationPlatform(StaticData.OP_ZDHK);/**自动还款*/
			cpr.setPaymentChannel(StaticData.NPC_BANKCARD);/**银行卡代扣*/
			cpr.setPaymentPlatform(StaticData.NPP_FUYOU);/**富有*/
			
			efPaycontrolDao.add(cpr);
			
			//更新信贷
			cpc.setStatus(1);
			cpc.setRemainAccrual(0d);
			cpc.setRemainCapital(0d);
			cpc.setRemainInterest(0d);
			cpc.setRemainManageFee(0d);
			cpc.setReplaceStatus(0);
			efPaycontrolDao.update(cpc);
			//开始判断是否可以结清
			CrmOrder crmOrder = (CrmOrder) efPaycontrolDao.findById(CrmOrder.class, cpc.getCrmOrderId());
			String sql ="select * from crm_paycontrol where crm_order_id='"+cpc.getCrmOrderId()+"' and status in (0,2)";
			List unClearlist = efPaycontrolDao.queryBySqlReturnMapList(sql);
			//长度为0代表该笔订单已经结束了
			if(ListTool.isNullOrEmpty(unClearlist)){
				crmOrder.setOrderTradeStatus(4);
				crmOrder.setClearType(1);
				crmOrder.setFinishTime(DateUtil.getCurrentTime());
				efPaycontrolDao.update(crmOrder);
				try{
				CrmApplay apply = (CrmApplay) efPaycontrolDao.findById(CrmApplay.class,crmOrder.getCrmApplayId());
				if(ChkUtil.isNotEmpty(apply)){
					apply.setTradeStatus(4);
					apply.setStatus(4);
					apply.setRepaymentStatus(1);
					efPaycontrolDao.update(apply);
				}
				}catch (Exception e) {
				}
				//更新资产包
				String sql2 ="select * from pkg_cust_crmorder where crm_order_id='"+cpc.getCrmOrderId()+"' ";
				List<Map> pkgList = efPaycontrolDao.queryBySqlReturnMapList(sql2);
				if(ListTool.isNullOrEmpty(pkgList)){
					for(Map pkgMap:pkgList){
						try{
							PkgCustCrmorder pcc = (PkgCustCrmorder) efPaycontrolDao.findById(PkgCustCrmorder.class, pkgMap.get("id").toString());
							pcc.setStatus("3");
							efPaycontrolDao.update(pcc);
						}catch (Exception e) {
							e.printStackTrace();
							logger.warn(e.getMessage(),e);
							logger.warn("更新资产包信贷订单时失败!信贷订单id:"+cpc.getCrmOrderId());
						}
					}
				}
				
			}
			String crmPaycontrolSql="SELECT * from zzl_ef_paycontrol where pay_status=-1 and pay_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and crm_order_id ='"+cpc.getCrmOrderId()+"' ";
			List<Map> list = efPaycontrolDao.queryBySqlReturnMapList(crmPaycontrolSql);
			if(ListTool.isNullOrEmpty(list)){logger.warn("本次更新未发现对应的中资联明细!");return;}
			logger.warn("本次更新中资联明细数量!"+list.size());
			for(Map zzlMap:list){
					ZZLEfPaycontrol zzl = (ZZLEfPaycontrol) efPaycontrolDao.findById(ZZLEfPaycontrol.class, zzlMap.get("id").toString());
					//增加记录
					ZZLEfPayrecord zzlR=new ZZLEfPayrecord();
					zzlR.setCreateTime(DateUtil.getCurrentTime());
					zzlR.setCustInfoId(zzl.getCustInfoId());
					zzlR.setEfOrderId(zzl.getEfOrderId());
					zzlR.setEvidenceUrl("");
					zzlR.setInteRest(zzl.getSurplusInterest());
					zzlR.setManagementAmt(zzl.getSurplusManagementAmt());
					zzlR.setOperator("");
					zzlR.setOverPenalty(zzl.getOverPenalty());
					zzlR.setPeriods(zzl.getPeriods());
					zzlR.setPrePaymentPenalty(zzl.getPrePaymentPenalty());
					zzlR.setPrincipal(zzl.getSurplusPrincipal());
					zzlR.setZzlPaycontrolId(zzl.getId());
					efPaycontrolDao.add(zzlR);
					//更新还款
					zzl.setPayStatus(1);//结清
					zzl.setSurplusInterest(0d);
					zzl.setSurplusManagementAmt(0d);
					zzl.setSurplusPrincipal(0d);
					efPaycontrolDao.update(zzl);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("poc还款成功,结清借款人明细时失败!明细id="+crmPaycontrolId);
		}
	}
	/**
	 * 功能说明：自动还款的boc通知
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
	public void autoNotice(String bocString){
		logger.warn("触发还款通知借口,开始---------");
		//解析数据
		JSONObject bocJson =JSONObject.fromObject(bocString);
		//信贷产品编号
		String order_prd_number = bocJson.getString("product");
		Map crmPaycontrol =  crmOrderDao.getCrmpaycontrolByProductNumber(order_prd_number);
		String ids="";
		if(!ChkUtil.isEmpty(bocJson.get("errorPriKeys"))){
			Object[] errorPriKeys= JSONArray.fromObject(bocJson.get("errorPriKeys")).toArray();
			for(Object error:errorPriKeys){
				ids+="'"+error.toString()+"',";
			}
			ids=ids.substring(0,ids.length()-1);
			logger.warn("部分还款失败,zzl的明细ids="+ids);
		}
		
		efPaycontrolDao.rePayNormal(crmPaycontrol.get("crm_order_id").toString(),ids);
		logger.warn("触发还款通知借口,结束---------");
			
	}
	/**
	 * 功能说明：获得直投Boc逾期的明细
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
	public List<Map> getOverImmePaycontrolsBoc(){
		return crmOrderDao.getOverImmePaycontrolsBoc();
	}
	/**
	 * 功能说明：获得定投Boc逾期的明细
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
	public List<Map> getOverDetePaycontrolsBoc(){
		return crmOrderDao.getOverDetePaycontrolsBoc();
	}
	/**
	 * 功能说明：获得Poc的逾期明细
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
	public List<Map> getOverPaycontrolsPoc(){
		return crmOrderDao.getOverPaycontrolsPoc();
	}
	/**
	 * 功能说明：获得直投Poc的明细
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
	public List<Map> getOverImmePaycontrolsPoc(){
		return crmOrderDao.getOverImmePaycontrolsPoc();
	}
	/**
	 * 功能说明：获得定投Poc的明细
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
	public List<Map> getOverDetePaycontrolsPoc(){
		return crmOrderDao.getOverDetePaycontrolsPoc();
	}
	/**
	 * 功能说明：获得线下逾期明细
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
	public List<Map> getOverLinePaycontrolsWithPoc(){
		return crmOrderDao.getOverLinePaycontrolsWithPoc();
	}
	/**
	 * 功能说明：获得线下逾期明细
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
	public List<Map> getOverLinePaycontrolsWithBoc(){
		return crmOrderDao.getOverLinePaycontrolsWithBoc();
	}
	/**
	 * 功能说明：线下逾期还款通过poc
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
	public void overRepayByPoc(String crmOrderId){
		crmOrderDao.overRepayByPoc(crmOrderId);
	}
	/**
	 * 功能说明：获取今日未结清订单
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
	public List<Map> getNotClearPaycontrols(){
		return crmOrderDao.getNotClearPaycontrols();
	}
	/**
	 * 功能说明：开始自动逾期
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
	public void autoOver(Map crmPaycontrol){
		crmOrderDao.autoOver(crmPaycontrol);
	}
	/**
	 * 功能说明：判断是否有逾期
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
	public boolean hasOver(String crm_order_id){
		return crmOrderDao.hasOver(crm_order_id);
	}
	/**
	 * 功能说明：获得v1线下的信贷明细	
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月8日 10:06:10
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getV1CurrentLinePaycontrols(){
		return crmOrderDao.getV1CurrentLinePaycontrols();
	}
	/**
	 * 功能说明：获取v1今日未结清订单
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年3月8日 15:24:55
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getV1NotClearPaycontrols(){
		return crmOrderDao.getV1NotClearPaycontrols();
	}
	/**
	 * 功能说明：开始v1自动逾期
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月8日 15:50:20
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void v1AutoOver(Map crmPaycontrol){
		crmOrderDao.v1AutoOver(crmPaycontrol);
	}
	/**
	 * 功能说明：获得v1逾期的明细
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月9日 10:41:59
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getV1OverPaycontrols(){
		return crmOrderDao.getV1OverPaycontrols();
	}
	/**
	 * 功能说明：结清当期明细
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
	public void repayNormalByV1(String crmPaycontrolId){
		try {
			CrmPaycontrol cpc = (CrmPaycontrol) crmOrderDao.findById(CrmPaycontrol.class, crmPaycontrolId);
			//产生记录 
			CrmPayrecoder cpr = new CrmPayrecoder();
			cpr.setCertificateUrl("");
			cpr.setCreateTime(DateUtil.getCurrentTime());
			cpr.setCrmOrderId(cpc.getCrmOrderId());
			cpr.setCustId(cpc.getCustId());
			cpr.setCustInfoId(cpc.getCustInfoId());
			cpr.setEmpId("");
			cpr.setManageFee(cpc.getRemainManageFee());
			cpr.setOverdueInterest(cpc.getOverdueInterest());
			cpr.setOverdueViolateMoney(cpc.getOverdueViolateMoney());
			cpr.setPaycontrolId(cpc.getId());
			cpr.setPrepaymentViolateMoney(0d);
			cpr.setRemark("");
			cpr.setRepaymentType(0);
			cpr.setRemainFee(0d);
			cpr.setShouldAccrual(cpc.getRemainAccrual());
			cpr.setShouldCAPITAL(cpc.getRemainCapital());
			cpr.setShouldInterest(cpc.getRemainInterest());
			cpr.setShouldMONEY(ArithUtil.add(new Double[]{
					cpc.getRemainAccrual(),
					cpc.getRemainCapital(),
					cpc.getRemainInterest(),
					cpc.getRemainManageFee()
			}));
			cpr.setOperationPlatform(StaticData.OP_ZDHK);/**自动还款*/
			cpr.setPaymentChannel(StaticData.NPC_BANKCARD);/**银行卡代扣*/
			cpr.setPaymentPlatform(StaticData.NPP_FUYOU);/**富有*/
			crmOrderDao.add(cpr);
			
			//更新信贷
			cpc.setStatus(1);
			cpc.setRemainAccrual(0d);
			cpc.setRemainCapital(0d);
			cpc.setRemainInterest(0d);
			cpc.setRemainManageFee(0d);
			crmOrderDao.update(cpc);
			//开始判断是否可以结清
			CrmOrder crmOrder = (CrmOrder) crmOrderDao.findById(CrmOrder.class, cpc.getCrmOrderId());
			String sql ="select * from crm_paycontrol where crm_order_id='"+cpc.getCrmOrderId()+"' and status in (0,2)";
			List unClearlist = crmOrderDao.queryBySqlReturnMapList(sql);
			//长度为0代表该笔订单已经结束了
			if(ListTool.isNullOrEmpty(unClearlist)){
				crmOrder.setOrderTradeStatus(4);
				crmOrder.setClearType(1);
				crmOrder.setFinishTime(DateUtil.getCurrentTime());
				crmOrderDao.update(crmOrder);
				CrmApplay apply = (CrmApplay) crmOrderDao.findById(CrmApplay.class,crmOrder.getCrmApplayId());
				if(ChkUtil.isNotEmpty(apply)){
					apply.setTradeStatus(4);
					apply.setStatus(4);
					apply.setRepaymentStatus(1);
					crmOrderDao.update(apply);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			CrmPaycontrol cpcc=new CrmPaycontrol();
			try {
				cpcc=(CrmPaycontrol) crmOrderDao.findById(CrmPaycontrol.class, crmPaycontrolId);
				cpcc.setAbnormalStatus(0);
				crmOrderDao.update(cpcc);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			//线上订单 正常还款成功,更新数据失败
			logger.warn("V1订单正常还款成功,更新数据失败，还款明细id:"+crmPaycontrolId+"");
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "V1订单正常还款成功,更新数据失败，还款明细id:"+crmPaycontrolId+"");
			SmsUtil.senErrorMsg(smsJson);
			logger.warn("v1还款成功,结清借款人明细时失败!明细id="+crmPaycontrolId);
		}
	}
	/**
	 * 功能说明：v1逾期还款
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:11:48 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void overRepayByV1(String crmOrderId){
		crmOrderDao.overRepayByV1(crmOrderId);
	}
	/**
	 * 功能说明：v1逾期还款
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public boolean hasReductionPaycontrol(String crmOrderId){
		String sql ="select * from crm_paycontrol where crm_order_id='"+crmOrderId+"' and status in (2) and exempt_status =1";
		List unClearlist = crmOrderDao.queryBySqlReturnMapList(sql);
		if(ListTool.isNullOrEmpty(unClearlist)){
			return false;
		}else{
			return true;
		}
	}
	/**
	 * 功能说明：获得直投的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpEfPayImmePoc(){
		return crmOrderDao.getFtpEfPayImmePoc();
	}
	/**
	 * 功能说明：获得定投的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpEfPaySetePoc(){
		return crmOrderDao.getFtpEfPaySetePoc();
	}
	/**
	 * 功能说明：获得crm直投的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpCrmPayImmePoc(){
		return crmOrderDao.getFtpCrmPayImmePoc();
	}
	/**
	 * 功能说明：获得crm定投的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpCrmPaySetePoc(){
		return crmOrderDao.getFtpCrmPaySetePoc();
	}
	/**
	 * 功能说明：获得crm直投逾期的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpCrmOverPayImmePoc(){
		return crmOrderDao.getFtpCrmOverPayImmePoc();
	}
	/**
	 * 功能说明：获得crm定投逾期的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpCrmOverPaySetePoc(){
		return crmOrderDao.getFtpCrmOverPaySetePoc();
	}
	
	/**
	 * 获取到期的赎楼订单
	 * @Title: 
	 * @auter sky 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @return    设定文件 
	 * @return     返回类型 
	 * @throws
	 */
	public List<Map> searchRansomFloorOrders() {
		return crmOrderDao.searchRansomFloorOrders();
	}
	
	/**
	 * 赎楼理财人赎回问题
	 * @Title: 
	 * @auter sky 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param pdInvestRate
	 * @param @param maxDay
	 * @param @param pdInvestManageRate
	 * @param @param fy_account
	 * @param @param beoId    设定文件 
	 * @return     返回类型 
	 * @throws
	 */
	public void executePocRansomFloor(double pdInvestRate, int maxDay,
			double pdInvestManageRate, String fy_account, String beoId,CrmOrder order) {
		logger.warn("赎楼理财还款:"+beoId+",");
		// 1. 获取相关理财订单
		BgEfOrders eo = null;
		BgCustInfo efCustInfo = null;
		BgCustomer efcustomer = null;
		try {
			eo = (BgEfOrders)crmOrderDao.findById(BgEfOrders.class, beoId);
			efCustInfo = (BgCustInfo)crmOrderDao.findById(BgCustInfo.class, eo.getCustInfoId());
			efcustomer = (BgCustomer)crmOrderDao.findById(BgCustomer.class, eo.getCustId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		// 计算利息
		Double interest = ArithUtil.mul(eo.getPrincipal()*pdInvestRate/100, maxDay); // 计算利息
		double managementAmt = ArithUtil.mul(eo.getPrincipal()*pdInvestManageRate/100, 1);// 计算管理费
		// 还给投资的金额计算
		double payMoney = ArithUtil.sub(eo.getPrincipal()+interest,managementAmt);
		logger.warn("信用公司购买借款人的理财订单ID:"+eo.getId());
		
		// 预授权
		JSONObject preAuthJson = new JSONObject();
		preAuthJson.put("int_cust_no", fy_account); // 入账人
		preAuthJson.put("out_cust_no", StaticData.creditAccount); // 出账人
		preAuthJson.put("amt", ""+payMoney); // 金额（分
		String preAuthResult = pocTool.connectToPoc("preAuth", preAuthJson);
		Object[] arr = JSONArray.fromObject(preAuthResult).toArray();
		preAuthJson = JSONObject.fromObject(arr[0]);
		if("0000".equals(preAuthJson.getString("responseCode"))){
		// 划拨
			JSONObject transferBuJson = new JSONObject();
			transferBuJson.put("applyId", preAuthJson.getString("applyId"));
			String transferBuResult = pocTool.connectToPoc("transferBu", transferBuJson);
			if(!"0000".equals(transferBuResult)){
				logger.warn("信用公司购买借款人的理财订单ID:"+eo.getId()+",划拨失败失败!");
			}
		}else{
			logger.warn("信用公司购买借款人的理财订单ID:"+eo.getId()+",预授权失败!");
		}
		
		if("0000".equals(preAuthJson.getString("responseCode"))){
			eo.setClearType(2);
			eo.setPayStatus(2);
			try {
				crmOrderDao.update(eo);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			// 生成相应的还款明细 和 还款记录
			BgEfPaycontrol bgEfPaycontrol = new BgEfPaycontrol();
			bgEfPaycontrol.setCustId(eo.getCustId());
			bgEfPaycontrol.setCustInfoId(eo.getCustInfoId());
			bgEfPaycontrol.setCreateTime(DateUtil.getCurrentTime());
			bgEfPaycontrol.setPayStatus(1);
			bgEfPaycontrol.setEfOrderId(eo.getId());
			bgEfPaycontrol.setPayTime(DateUtil.getCurrentTime());
			bgEfPaycontrol.setLiveStatus(1);
			// 金额
			bgEfPaycontrol.setPrinciapl(eo.getPrincipal());
			bgEfPaycontrol.setInterest(interest);
			bgEfPaycontrol.setManagementAmt(managementAmt);
			bgEfPaycontrol.setTotalAmt(ArithUtil.sub(eo.getPrincipal()+interest,managementAmt));
			bgEfPaycontrol.setPeriods(1);
			
			bgEfPaycontrol.setSurplusInterest(0.0);
			bgEfPaycontrol.setSurplusManagementAmt(0.0);
			bgEfPaycontrol.setSurplusPrincipal(0.0);
			try {
				crmOrderDao.add(bgEfPaycontrol);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			// 还款记录
			BgEfPayrecord bgEfPayrecord = new BgEfPayrecord();
			bgEfPayrecord.setCustId(eo.getCustId());
			bgEfPayrecord.setCustInfoId(eo.getCustInfoId());
			bgEfPayrecord.setEfOrderId(eo.getId());
			bgEfPayrecord.setEfPaycontrolId(bgEfPaycontrol.getId());
			// 金额
			bgEfPayrecord.setPeriods(1);
			bgEfPayrecord.setPrincipal(eo.getPrincipal());
			bgEfPayrecord.setInteRest(interest);
			bgEfPayrecord.setManagementAmt(managementAmt);
			bgEfPayrecord.setTotalAmt(ArithUtil.sub(eo.getPrincipal()+interest,managementAmt));
			bgEfPayrecord.setCreateTime(DateUtil.getCurrentTime());
			try {
				crmOrderDao.add(bgEfPayrecord);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			// 发送站内信
			try {
				JSONObject paramJson = new JSONObject();
				paramJson.put("msgType", 2);
				paramJson.put("name", efcustomer.getUsername());
				paramJson.put("number", order.getOrderNumber());
				paramJson.put("Periods", 1);
				paramJson.put("Princiapl", ArithUtil.round(eo.getPrincipal(), 2));
				paramJson.put("Interest", ArithUtil.round(interest, 2));
				paramJson.put("ManagementAmt", ArithUtil.round(managementAmt, 2));
				paramJson.put("totalMoney", ArithUtil.round(payMoney,2));
				paramJson = MessageTemplete.getMsg(paramJson);
				
				BgSysMessageContent bsmc = new BgSysMessageContent();
				bsmc.setCreateTime(DateUtil.getCurrentTime());
				bsmc.setMsgContent(paramJson.getString("content"));
				bsmc.setMsgTitle(paramJson.getString("title"));
				bsmc.setPublishId("");
				bsmc.setUpdateTime(DateUtil.getCurrentTime());
				bsmc.setPlatformType(2);
				crmOrderDao.add(bsmc);
				
				BgSysMessage bsm  = new BgSysMessage();
				bsm.setCustId(eo.getCustId());
				bsm.setEfOrderId(eo.getId());
				bsm.setEnabled(1);
				bsm.setMsgClass(2);
				bsm.setMsgContentId(bsmc.getId());
				bsm.setMsgType(5);
				bsm.setOrderId(order.getId());
				bsm.setReadStatus(0);
				bsm.setPlatformType(2);
				crmOrderDao.add(bsm);
			} catch (Exception e) {
				logger.warn("信用公司购买借款人的理财订单ID:"+eo.getId()+",站内信发送失败!");
			}
		}
	}
	/**
	 * boc 提前还款
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
	public void executeBocRansomFloor(double pdInvestRate, int maxDay,
			double pdInvestManageRate, String bank_account, String beoId,String crmOrderId,Double assignedFee) {}
	// 给理财人邀请人相关利息
		private void initRefereeInterest(BgEfOrders eo,double interest) throws Exception{
			List refereeList = crmOrderDao.searchRefereeConnection(eo.getCustInfoId());
			if(refereeList.size() > 0){
				JSONObject referee = JSONObject.fromObject(refereeList.get(0));
				String referee_info_id = referee.getString("referee_info_id"); // 推荐人ID
				double referee_income_scale = ChkUtil.isEmpty(referee.get("referee_income_scale")) ? 0.0:referee.getDouble("referee_income_scale"); // 推荐人利率
				// 邀请人
				BgCustInfo refereeCustInfo = (BgCustInfo)crmOrderDao.findById(BgCustInfo.class, referee_info_id);
				
				// 给邀请人发红包
				JSONObject bocJson = new JSONObject();
				bocJson.put("cardNbr", StaticData.redEnvelope);  					//转出账户--> 红包账户
				bocJson.put("cardNbrIn", refereeCustInfo.getFyAccount());  		//转入账户--> 投资人
				bocJson.put("amount",ArithUtil.mul(interest,referee_income_scale/100));  		//转入账户--> 投资人
				JSONObject bocResult =null;// WebServiceUtil.invokeBoc("bankService", "getBank7603", bocJson);
				if("0".equals(bocResult.getString("responseCode"))){
					logger.warn("给相关的理财邀请发红包!订单ID:"+eo.getId()+"，发邀请利息发送失败!");
				}
				if("1".equals(bocResult.getString("responseCode"))){//如果成功了则需要增加记录
					logger.warn("给相关的理财邀请发红包!订单ID:"+eo.getId()+"，发邀请利息发送成功!");
						//开发准备进行修改推荐记录表
						BgReferralIncomeRecord bri  = new BgReferralIncomeRecord();
						bri.setCreateTime(DateUtil.getCurrentTime());
						bri.setCustInfoId(eo.getCustInfoId());
						bri.setIncomeSource("投资还款");
						bri.setRefereeInfoId(referee_info_id);
						bri.setReferralIncome(ArithUtil.mul(
								interest,
								referee_income_scale/100d
								));
						bri.setUpdateTime(DateUtil.getCurrentTime());
						//如果金额>0才进行存储
						if(NumberFormat.format(bri.getReferralIncome())>0){
							crmOrderDao.add(bri);
						}
				}
				
			}
		}
	
	/**
	 * boc 提前还款
	 * 功能说明：该方法实现的功能			
	 * @author yuanhao
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2016-9-1
	 * 修改内容：
	 * 修改注意点：
	 */
	public void executeRansomFloorBoc(double pdInvestRate, int maxDay,
			double pdInvestManageRate, String bank_account, String beoId,String crmOrderId) {
		logger.warn("赎楼理财还款:"+beoId+",");
		// 1. 获取相关理财订单
		BgEfOrders eo = null;
		CrmOrder order = null;
		BgCustInfo crmCustInfo = null;
		try {
			eo = (BgEfOrders)crmOrderDao.findById(BgEfOrders.class, beoId);
			order = (CrmOrder)crmOrderDao.findById(CrmOrder.class, crmOrderId);
			crmCustInfo = (BgCustInfo)crmOrderDao.findById(BgCustInfo.class, order.getCustInfoId());
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		// 计算利息
		Double interest = ArithUtil.mul(eo.getPrincipal()*pdInvestRate/100, maxDay); // 计算利息
		double managementAmt = ArithUtil.mul(eo.getPrincipal()*pdInvestManageRate/100, 1);// 计算管理费
		// 还给投资的金额计算
		//double payMoney = ArithUtil.sub(eo.getPrincipal()+interest,managementAmt);
		double finalIncome = ArithUtil.sub(interest,managementAmt);
		//4.2.1.1 理财人利息红包户还款
		//开始发送红包
		try {
			//判断是否有明细表
			String checkBepcSql = "SELECT * from bg_ef_paycontrol where ef_order_id='"+eo.getId()+"'" ;
			List checkBepcList = crmOrderDao.queryBySqlReturnMapList(checkBepcSql);
			//说明有明细了已经进行手动还款了
			if(ListTool.isNotNullOrEmpty(checkBepcList)){
				return;
			}
			//首先判断是否利息已经还过了,查询记录即可
			String checkRecordSql = "SELECT * from bg_ef_payrecord where ef_order_id='"+eo.getId()+"'" ;
			List checkRecordList = crmOrderDao.queryBySqlReturnMapList(checkRecordSql);
			if(ListTool.isNullOrEmpty(checkRecordList)){
				JSONObject bocJson = new JSONObject();
				bocJson.put("cardNbrIn", bank_account);  		//转入账户--> 投资人
				bocJson.put("amount",finalIncome);  		//转入账户--> 投资人
				bocJson.put("deslineflag", "1");
				bocJson.put("desline", "赎楼贷投资人利息还款");
				JSONObject bocResult = null;// WebServiceUtil.invokeBoc("bankService", "getBank7603", bocJson);
				if("1".equals(bocResult.getString("responseCode")) ){
					//利息成功了,需要记录一比利息的记录
					// 还款记录
					BgEfPayrecord bgEfPayrecord = new BgEfPayrecord();
					bgEfPayrecord.setCustId(eo.getCustId());
					bgEfPayrecord.setCustInfoId(eo.getCustInfoId());
					bgEfPayrecord.setEfOrderId(eo.getId());
					bgEfPayrecord.setEfPaycontrolId("");
					// 金额
					bgEfPayrecord.setPeriods(1);
					bgEfPayrecord.setPrincipal(0d);
					bgEfPayrecord.setInteRest(interest);
					bgEfPayrecord.setManagementAmt(managementAmt);
					bgEfPayrecord.setTotalAmt(ArithUtil.sub(interest,managementAmt));
					bgEfPayrecord.setCreateTime(DateUtil.getCurrentTime());
					crmOrderDao.add(bgEfPayrecord);
					
					//4.2.1.2 给相关的理财邀请人利息
					initRefereeInterest(eo, interest);
					}else{
						logger.warn("给相关的理财邀请发红包!订单ID:"+eo.getId()+"，发邀请利息发送失败");
					}
			}
				// 4.2.1.3 购买相关理财的订单
				//开始购买债权
			JSONArray transArray = new JSONArray();
			JSONObject bocJson = new JSONObject();
			bocJson.put("cardNbrIn", StaticData.risk);												//承接方-->风险户
			bocJson.put("cardNbrOut",bank_account);								//转让方电子账号
			bocJson.put("oldSeriNo",eo.getInvestSeriNum());							//原债权投标流水号
			//总共可转让金额  = 本金
			bocJson.put("tblance",eo.getEfFectiveAmt());								//总共可转让金额
			bocJson.put("tranAmt",eo.getEfFectiveAmt()); 	//转让金额
			bocJson.put("tranPrice", eo.getEfFectiveAmt()); 	//转让价格
			bocJson.put("intDate", DateUtil.getCurrentTime(DateUtil.STYLE_3)); //起息日 YYYYMMDD
			bocJson.put("yield", DateUtil.formatInvestRate(NumberFormat.format(pdInvestRate*365d)));									//转让后预期年化收益率
			bocJson.put("feeWay", "0");																//手续费扣款方式 （0指定金额 1 同产品设置）
			bocJson.put("tranFee", "0");							//转让手续费(管理费)（feeWay为0时生效）
			bocJson.put("trdresv", "3_3:"+eo.getId());	                                           //第三方备注(存bg_ef_paycontrol的id)
			//准备链接BOC
			transArray.add(bocJson);
			//准备连接进行债权转让
			JSONObject bocResult = null;// WebServiceUtil.invokeBoc("batchTransferUploadService", "batchUploadFiles", new Object[]{transArray,DateUtil.getSerialNo(""),SignatureUtil.createSign()});
			//如果红包错误
			if("0".equals(bocResult.getString("responseCode"))){
				logger.warn("赎楼贷在进行批量债权转让时上传失败!理财订单id:"+eo.getId());
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", "batchTransferUploadService_batchUploadFiles");
				smsJson.put("text", "赎楼贷在进行批量债权转让时上传失败!理财订单id:"+eo.getId());
				SmsUtil.senErrorMsg(smsJson);
			}else{
				logger.warn("赎楼贷在进行批量债权转让时上传成功!"+eo.getId());
			}
			if(true){ //到此应该结束了,等通知
				return;
			}
			//
				bocJson.clear();
				bocJson.put("cardNbr", StaticData.risk);												//承接方-->风险户
				bocJson.put("authCode",eo.getInvestAuzCode());							//投标返回的授权码
				bocJson.put("cardNbro",bank_account);								//转让方电子账号
				//总共可转让金额  = 本金
				bocJson.put("tbalAce",eo.getPrincipal());								//总共可转让金额
				bocJson.put("amount", eo.getPrincipal()); 								//转让金额
				bocJson.put("trpRice", eo.getPrincipal()); 								//转让价格
				bocJson.put("intDate", DateUtil.getCurrentTime(DateUtil.STYLE_3)); 		//起息日 YYYYMMDD
				bocJson.put("yield", ArithUtil.mul(pdInvestRate, 365));					//转让后预期年化收益率
				bocJson.put("feeWay", "0");												//手续费扣款方式 （0指定金额 1 同产品设置）
				bocJson.put("fee", 	0);													//转让手续费(管理费)（feeWay为0时生效）
				//准备链接BOC
				bocResult.clear();
				bocResult = null;// WebServiceUtil.invokeBoc("bankService", "getBank5817", bocJson);
				if("0".equals(bocResult.getString("responseCode"))){
					logger.warn("理财明细在债权转让时失败!理财id:"+eo.getId());
					return;
				}
				
				// 4.2.1.4 生成相关风险费还款明细
				ZZLEfPaycontrol zzlEfPaycontrol = new ZZLEfPaycontrol();
				zzlEfPaycontrol.setEfOrderId(eo.getId());
				zzlEfPaycontrol.setClearingChannel(2);
				zzlEfPaycontrol.setCustInfoId(eo.getCustInfoId());
				zzlEfPaycontrol.setCrmOrderId(order.getId());
				zzlEfPaycontrol.setCreateTime(DateUtil.getCurrentTime());
				zzlEfPaycontrol.setPayStatus(0);
				// 金额
				zzlEfPaycontrol.setSurplusPrincipal(eo.getPrincipal()); // 本金
				zzlEfPaycontrol.setSurplusInterest(interest);	// 利息
				zzlEfPaycontrol.setSurplusManagementAmt(managementAmt); // 管理费
				zzlEfPaycontrol.setPayTime(DateUtil.getCurrentTime());
				zzlEfPaycontrol.setPeriods(1);
				zzlEfPaycontrol.setType(1);
				zzlEfPaycontrol.setPrinciapl(eo.getPrincipal());
				zzlEfPaycontrol.setInterest(interest);
				zzlEfPaycontrol.setManagementAmt(managementAmt);
				//zzlEfPaycontrol.setAuthCode(bocResult.getString("AUTHCODE")); // 授权编号
				zzlEfPaycontrol.setSurplusInterest(0d);
				zzlEfPaycontrol.setSurplusManagementAmt(0d);
				zzlEfPaycontrol.setSurplusPrincipal(eo.getPrincipal());
				
//				if(i == list.size() - 1){
//					zzlEfPaycontrol.setOverCost(overFee - assignedFee); // 总金额 - 已经分配出去的金额
//				}else{							
//					zzlEfPaycontrol.setOverCost(overCost);
//				}
				
				crmOrderDao.add(zzlEfPaycontrol);
				
				// 4.2.1.5 结清上一个理财人的订单
				/*JSONObject cuJson = new JSONObject();
				JSONArray jsonArray = new JSONArray();
				
				//通过理财人,获得借款人
				cuJson.put("type", "002"); 									//到期还款
				cuJson.put("outCardNo",crmCustInfo.getBankAccount());	//借款人
				cuJson.put("amount", "0");	//本金
				cuJson.put("intAmt", "0");										//利息
				cuJson.put("inCardNo",bank_account);		//理财人
				cuJson.put("inFeeAmt", "0");										//投资人手续费
				cuJson.put("inFeeWay", "0");									//借款人扣款方式
				cuJson.put("outFeeWay", "0");									//投资人手续费扣款方式 0 指定金额 1同产品设置
				cuJson.put("outFeeAmt", "0") ;
				cuJson.put("authCode", eo.getInvestAuzCode());
				cuJson.put("seriNo", eo.getInvestSeriNum());
				//查看本期还款是否结束
				cuJson.put("endFlg", "1");
				cuJson.put("priKey",eo.getId());
				jsonArray.add(cuJson);
				bocResult.clear();
				bocResult = WebServiceUtil.invokeBoc("batchPaymentFileService", "batchUploadFiles",new Object[]{jsonArray.toString(),eo.getOrderNumber(),"0",SignatureUtil.createSign()});
				if("0".equals(bocResult.getString("responseCode"))){
					logger.warn("理财明细在债权转让时失败!理财id:"+eo.getId());
					return;
				}*/
		} catch (Exception e) {
			logger.warn("给相关的理财利息异常!订单ID:"+eo.getId()+".");
		}
	}

	
	/**
	 * 查询信贷还款记录数据
	 * 功能说明：该方法实现的功能			
	 * @author yuanhao
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2016-9-1
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> findEfDeteData() {
		return crmOrderDao.findEfDeteData();
	}
	/**
	 * 更新资产包里面的信贷信息
	 * 功能说明：该方法实现的功能			
	 * @author yuanhao
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2016-9-1
	 * 修改内容：
	 * 修改注意点：
	 */
	public void removePkgCrmOrders(List<Map> crmorders){
		if(ListTool.isNullOrEmpty(crmorders)){
			return;
		}
		for(Map baseMap:crmorders){
				if(baseMap.containsKey("serino") &&  baseMap.get("crm_order_id")!=null && baseMap.get("type")!=null && "2".equals(baseMap.get("type").toString())  ){
				 //获得信贷订单的id
				 //查找到所有的该订单下的债权
				 //匹配金额直接归0
				 String crm_order_id = baseMap.get("crm_order_id").toString();
				 String sql2 ="select * from pkg_cust_crmorder where crm_order_id='"+crm_order_id+"' and status='2' ";
				//更新资产包
				List<Map> pkgList = efPaycontrolDao.queryBySqlReturnMapList(sql2);
				if(ListTool.isNotNullOrEmpty(pkgList)){
						for(Map pkgMap:pkgList){
							try{
								PkgCustCrmorder pcc = (PkgCustCrmorder) crmOrderDao.findById(PkgCustCrmorder.class, pkgMap.get("id").toString());
								pcc.setBeMatchMoney(0d);
								pcc.setStatus("0");
								//准备修改本次的还款值,然后重新赋值更新
								efPaycontrolDao.update(pcc);
							}catch (Exception e) {
								e.printStackTrace();
								logger.warn(e.getMessage(),e);
								logger.warn("更新资产包信贷订单时失败!信贷订单id:"+crm_order_id);
							}
						}
					}
					continue;
				}
				//非提前结清的剔除方法
				String bg_ef_order_id = baseMap.containsKey("ef_order_id")?baseMap.get("ef_order_id").toString():"";
				if("".equals(bg_ef_order_id)){
					bg_ef_order_id = baseMap.containsKey("bg_ef_order_id")?baseMap.get("bg_ef_order_id").toString():"";
				}
				if("".equals(bg_ef_order_id)){
					continue;
				}
				//更新资产包
				String sql2 ="select * from pkg_cust_crmorder where bg_ef_order_id='"+bg_ef_order_id+"' and status='2' ";
				List<Map> pkgList = efPaycontrolDao.queryBySqlReturnMapList(sql2);
				if(ListTool.isNotNullOrEmpty(pkgList)){
					for(Map pkgMap:pkgList){
						try{
							PkgCustCrmorder pcc = (PkgCustCrmorder) crmOrderDao.findById(PkgCustCrmorder.class, pkgMap.get("id").toString());
							double money = NumberFormat.format(baseMap.get("surplus_principal"));
							double AllMoney = NumberFormat.format(pcc.getBeMatchMoney());
							double remainMoney = ArithUtil.sub(AllMoney,money);
							if(remainMoney<=0){
								remainMoney = 0d;
								pcc.setStatus("0");
							}
							pcc.setBeMatchMoney(remainMoney);
							//准备修改本次的还款值,然后重新赋值更新
							efPaycontrolDao.update(pcc);
						}catch (Exception e) {
							e.printStackTrace();
							logger.warn(e.getMessage(),e);
							logger.warn("更新资产包信贷订单时失败!债权订单id:"+bg_ef_order_id);
						}
					}
				}
			}
		}
	/**
	 * 更新资产包里面的信贷信息
	 * 功能说明：该方法实现的功能			
	 * @author yuanhao
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2016-9-1
	 * 修改内容：
	 * 修改注意点：
	 */
/*	public void removePkgCrmOrders(String crmOrderId){
		if(crmOrderId==null || "".equals(crmOrderId)){
			return;
		}
		//更新资产包
		String sql2 ="select * from pkg_cust_crmorder where crm_order_id='"+crmOrderId+"' ";
		List<Map> pkgList = efPaycontrolDao.queryBySqlReturnMapList(sql2);
		if(ListTool.isNotNullOrEmpty(pkgList)){
			for(Map pkgMap:pkgList){
				try{
					PkgCustCrmorder pcc = (PkgCustCrmorder) crmOrderDao.findById("PkgCustCrmorder", pkgMap.get("id").toString());
					if ("2".equals(pcc.getStatus())) {
						pcc.setStatus("3");
						efPaycontrolDao.update(pcc);
					}
				}catch (Exception e) {
					e.printStackTrace();
					logger.warn(e.getMessage(),e);
					logger.warn("更新资产包信贷订单时失败!信贷订单id:"+crmOrderId);
				}
			}
		}
	}*/
	
	/**
	 * 功能说明：获取到期的赎楼订单
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> qureyRansomFloorOrders() {
		return crmOrderDao.queryRansomFloorOrders();
	}
	
	/**
	 * 功能说明：赎楼理财还款POC
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：下午5:58:43
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void hfRansomFloor(double pdInvestRate, int maxDay,
			double pdInvestManageRate, String hf_account, String beoId,CrmOrder order, String cust_name) {
		logger.warn("赎楼理财还款:"+beoId+",");
		
		// 1. 获取相关理财订单
		BgEfOrders eo = null;
		BgCustomer efcustomer = null;
		try {
			eo = (BgEfOrders)crmOrderDao.findById(BgEfOrders.class, beoId);
			efcustomer = (BgCustomer)crmOrderDao.findById(BgCustomer.class, eo.getCustId());
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("赎楼理财还款id:"+beoId+"失败!");
			return;
		}
		
		// 2.计算利息
		Double interest = ArithUtil.mul(eo.getPrincipal()*pdInvestRate/100, maxDay); // 计算利息
		double managementAmt = ArithUtil.mul(eo.getPrincipal()*pdInvestManageRate/100, 1);// 计算管理费
		// 还给投资的金额计算
		double payMoney = ArithUtil.sub(eo.getPrincipal()+interest,managementAmt);
		logger.warn("信用公司购买借款人的理财订单ID:"+eo.getId());
		
		//3.进行还给投资的金额划拨
		JSONObject json = new JSONObject();
		json.put("out_cust_no", StaticData.HFcreditAccount);	//付款登录账户
		json.put("in_cust_no", hf_account);	//收款登录账(保留2位小数)
		json.put("amt", ArithUtil.mul(payMoney, 100));	//划拨金额,以分为单位
		String str = new SimpleDateFormat("yyyyMMdd").format(new Date());
		json.put("trade_date", str);	//交易日期yyyyMMdd
		json.put("project_number", order.getOrderPrdNumber());	//项目编号order_prd_number
		json.put("out_login_name", StaticData.HFcreditAccountName);	//出账人平台用户名
		json.put("in_login_name", cust_name);	//入账人平台用户名
		json.put("amount", ArithUtil.mul(payMoney, 100));	//本金,以分为单位 (无小数位)
		json.put("orgin_login_id", hf_account);	//（原）投资人用户名
		json.put("orgin_login_name", cust_name);	//（原）投资人存管账户系统登陆用户名
		json.put("business_type", 2);	//业务类型： 0.投标 1.满标放款 2.转让 3.回款 4.其他 5.流标 6 平台手续费 7.平台代偿 8.平台营销 ，必传 ,
		
		//恒丰银行划拨功能
		JSONObject jSONObject = WebServiceUtil.sendPost(SysConfig.getInstance().getProperty("pay.center.api") + "hfTransferService/transfer", json.toString());

		if(jSONObject !=null && "0000".equals(jSONObject.getString("resultCode"))){
			
			BgEfPaycontrol bgEfPaycontrol = new BgEfPaycontrol();
			bgEfPaycontrol.setCustId(eo.getCustId());
			bgEfPaycontrol.setCustInfoId(eo.getCustInfoId());
			bgEfPaycontrol.setCreateTime(DateUtil.getCurrentTime());
			bgEfPaycontrol.setPayStatus(0);
			bgEfPaycontrol.setEfOrderId(eo.getId());
			bgEfPaycontrol.setPayTime(DateUtil.getCurrentTime());
			bgEfPaycontrol.setLiveStatus(1);
			// 金额
			bgEfPaycontrol.setPrinciapl(eo.getPrincipal());
			bgEfPaycontrol.setInterest(interest);
			bgEfPaycontrol.setManagementAmt(managementAmt);
			bgEfPaycontrol.setTotalAmt(ArithUtil.sub(eo.getPrincipal()+interest,managementAmt));
			bgEfPaycontrol.setPeriods(1);
			
			bgEfPaycontrol.setSurplusInterest(interest);
			bgEfPaycontrol.setSurplusManagementAmt(0D);
			bgEfPaycontrol.setSurplusPrincipal(0D);
			bgEfPaycontrol.setScoreScale(10D);//积分
			bgEfPaycontrol.setOperateType("0");//0自动
			bgEfPaycontrol.setUseCouponInterest(0D);
			bgEfPaycontrol.setCouponInterest(0D);
			
			try {
				crmOrderDao.add(bgEfPaycontrol);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			
			Map<String, Object> bgEfPaycontrolMap = new HashMap<String, Object>();
			//开始进行债权转让的记录
			bgEfPaycontrolMap = bgEfOrderDao.getCurrentEfBocControls(bgEfPaycontrol.getId());
			if(bgEfPaycontrolMap == null)
			{
				logger.warn("赎楼贷在进行批量债权转让时上传失败!理财订单id:"+eo.getId());
			}
			bgEfPaycontrolMap.put("clearing_channel","2");
			bgEfPaycontrolMap.put("onLine",bgEfPaycontrolMap.get("online_type").toString());
//			bgEfPaycontrolMap.put("authcode",bocResult.getString("investAuthCode"));
			bgEfPaycontrolMap.put("seriNo", jSONObject.get("mchnt_txn_ssn") == null? "":jSONObject.getString("mchnt_txn_ssn"));
			bgEfPaycontrolMap.put("investment_model",eo.getInvestmentModel());
			bgEfPaycontrolMap.put("surplus_principal",eo.getPrincipal());
			bgEfOrderService.normalRepayByModel(bgEfPaycontrolMap);
			
			// 4.修改理财订单，生成相应的还款明细 和 还款记录
			eo.setClearType(2);
			eo.setPayStatus(2);
			try {
				crmOrderDao.update(eo);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			// 5.发送站内信
			try {
				JSONObject paramJson = new JSONObject();
				paramJson.put("msgType", 2);
				paramJson.put("name", efcustomer.getUsername());
				paramJson.put("number", order.getOrderNumber());
				paramJson.put("Periods", 1);
				paramJson.put("Princiapl", ArithUtil.round(eo.getPrincipal(), 2));
				paramJson.put("Interest", ArithUtil.round(interest, 2));
				paramJson.put("ManagementAmt", ArithUtil.round(managementAmt, 2));
				paramJson.put("totalMoney", ArithUtil.round(payMoney,2));
				paramJson = MessageTemplete.getMsg(paramJson);
				
				BgSysMessageContent bsmc = new BgSysMessageContent();
				bsmc.setCreateTime(DateUtil.getCurrentTime());
				bsmc.setMsgContent(paramJson.getString("content"));
				bsmc.setMsgTitle(paramJson.getString("title"));
				bsmc.setPublishId("");
				bsmc.setUpdateTime(DateUtil.getCurrentTime());
				if(order.getOnlineType() == 1){
					bsmc.setPlatformType(1);
				}else if(order.getOnlineType() == 2){
					bsmc.setPlatformType(2);
				}
				
				crmOrderDao.add(bsmc);
				
				BgSysMessage bsm  = new BgSysMessage();
				bsm.setCustId(eo.getCustId());
				bsm.setEfOrderId(eo.getId());
				bsm.setEnabled(1);
				bsm.setMsgClass(2);
				bsm.setMsgContentId(bsmc.getId());
				bsm.setMsgType(5);
				bsm.setOrderId(order.getId());
				bsm.setReadStatus(0);
				if(order.getOnlineType() == 1){
					bsmc.setPlatformType(1);
				}else if(order.getOnlineType() == 2){
					bsmc.setPlatformType(2);
				}
				crmOrderDao.add(bsm);
				
				BgEfPaycontrol bgEfPaycontrolE = (BgEfPaycontrol)crmOrderDao.findById(BgEfPaycontrol.class, bgEfPaycontrol.getId());
				
				//5.客户积分获取记录以及该用户的积分更新问题
				BgScoreRecord bsr = new  BgScoreRecord();
				bsr.setEfOrderId(eo.getId());
				bsr.setCreateTime(DateUtil.getCurrentTime());
				bsr.setCustId(eo.getCustId());
				bsr.setIncomeSource("投资还款");
				if(ChkUtil.isNotEmpty(bgEfPaycontrolE.getScoreScale())){
					bsr.setScore(String.valueOf(Math.round(ArithUtil.mul(
								ArithUtil.add(interest,bgEfPaycontrolE.getCouponInterest()),
								bgEfPaycontrolE.getScoreScale()/100d
								))));
				}
				bsr.setUpdateTime(DateUtil.getCurrentTime());
				if(NumberUtil.parseDouble(bsr.getScore())>0){
					bsr.setRealTimeScore(efcustomer.getScore());
					crmOrderDao.add(bsr);
				}
				
				//如果积分>0才进行存储
				//-------------------------------------------------------------------
				//修改积分
				String oldScore="0";
				if(ChkUtil.isNotEmpty(efcustomer.getScore())){
					oldScore = efcustomer.getScore();
				}
				String currentScore  = String.valueOf(Long.parseLong(oldScore)+Long.parseLong(bsr.getScore()));
				efcustomer.setScore(currentScore);
				crmOrderDao.update(efcustomer);

			} catch (Exception e) {
				logger.warn("信用公司购买借款人的理财订单ID:"+eo.getId()+",站内信发送失败!");
			}
		}else{
			logger.warn("信用公司购买借款人的理财订单ID:"+eo.getId()+",恒丰划拨失败!");
		}
	}
	
	/**
	 * 功能说明：BOC赎楼理财还款
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void bocRansomFloor(double pdInvestRate, int maxDay,
			double pdInvestManageRate, String bank_account, String beoId,String crmOrderId) {
		logger.warn("赎楼理财还款:"+beoId+",开始：");
		// 1. 获取相关理财订单
		BgEfOrders eo = null;
		CrmOrder order = null;
		BgCustomer efcustomer = null;
		try {
			eo = (BgEfOrders)crmOrderDao.findById(BgEfOrders.class, beoId);
			order = (CrmOrder)crmOrderDao.findById(CrmOrder.class, crmOrderId);
			efcustomer = (BgCustomer)crmOrderDao.findById(BgCustomer.class, eo.getCustId());
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("赎楼理财还款:"+beoId+",失败！");
			return;
		}
		
		// 2.计算利息
		Double interest = ArithUtil.mul(eo.getPrincipal()*pdInvestRate/100, maxDay); // 计算利息
		double managementAmt = ArithUtil.mul(eo.getPrincipal()*pdInvestManageRate/100, 1);// 计算管理费
		// 还给投资的金额计算
		double finalIncome = ArithUtil.sub(interest,managementAmt);
		
		Map<String, Object> bgEfPaycontrolMap = new HashMap<String, Object>();
		
		Double scoreScale = null;
		String paycontrolId = "";
		try {
			//3.首先判断是否利息已经还过了,查询记录即可
			String checkRecordSql = "SELECT * from bg_ef_paycontrol where ef_order_id='"+eo.getId()+"'" ;
			List<Map> checkRecordList = crmOrderDao.queryBySqlReturnMapList(checkRecordSql);
			
			if(ListTool.isNullOrEmpty(checkRecordList)){
				//开始发送红包
				JSONObject bocJson = new JSONObject();
				bocJson.put("orderNo", crmOrderId);
				bocJson.put("remark", "");
				bocJson.put("acqRes", "");
				bocJson.put("sendAppName", "erpv3_auto2");
				bocJson.put("forAccountId", bank_account);  					//转入账户--> 投资人
				bocJson.put("txAmount",NumberFormat.format(finalIncome));  		//转入账户--> 投资人
				JSONObject bocResultHB = WebServiceUtil.sendPost(SysConfig.getInstance().getProperty("pay.center.api")+"payProcess/redPayment", new Object[]{bocJson});
				if("1".equals(bocResultHB.getString("responseCode")) ){
					//利息成功了,需要记录一比利息的记录
					BgEfPaycontrol bgEfPaycontrol = new BgEfPaycontrol();
					bgEfPaycontrol.setCustId(eo.getCustId());
					bgEfPaycontrol.setCustInfoId(eo.getCustInfoId());
					bgEfPaycontrol.setCreateTime(DateUtil.getCurrentTime());
					bgEfPaycontrol.setPayStatus(0);
					bgEfPaycontrol.setEfOrderId(eo.getId());
					bgEfPaycontrol.setPayTime(DateUtil.getCurrentTime());
					bgEfPaycontrol.setLiveStatus(1);
					// 金额
					bgEfPaycontrol.setPrinciapl(eo.getPrincipal());
					bgEfPaycontrol.setInterest(interest);
					bgEfPaycontrol.setManagementAmt(managementAmt);
					bgEfPaycontrol.setTotalAmt(ArithUtil.sub(eo.getPrincipal()+interest,managementAmt));
					bgEfPaycontrol.setPeriods(1);
					
					bgEfPaycontrol.setSurplusInterest(interest);
					bgEfPaycontrol.setSurplusManagementAmt(0D);
					bgEfPaycontrol.setSurplusPrincipal(0D);
					bgEfPaycontrol.setOperateType("0");
					bgEfPaycontrol.setScoreScale(10D);
					bgEfPaycontrol.setUseCouponInterest(0D);
					bgEfPaycontrol.setCouponInterest(0D);
					crmOrderDao.add(bgEfPaycontrol);
					
					paycontrolId = bgEfPaycontrol.getId();
					
					String bgEfPaycontrolSql = "SELECT * from bg_ef_paycontrol where id='"+paycontrolId+"'" ;
					List<Map> bgEfPaycontrolMaps = crmOrderDao.queryBySqlReturnMapList(bgEfPaycontrolSql);
					if(ListTool.isNullOrEmpty(bgEfPaycontrolMaps))
					{
						logger.warn("赎楼理财还款:"+beoId+",生成还款计划明细失败！");
						return;
					}
					
					scoreScale = bgEfPaycontrol.getScoreScale();
			}
		}else
		{
			if (checkRecordList.size() != 0 && checkRecordList.get(0) != null)
			{
				paycontrolId = checkRecordList.get(0).get("id").toString();
				scoreScale = (Double)checkRecordList.get(0).get("score_scale");
			}else
			{
				logger.warn("赎楼理财还款:"+beoId+",生成还款计划明细失败！");
				return;
			}
		}
		
		// 4.购买债权
		JSONObject bocParam = new JSONObject();
		
		//买入方自动债权转让签约订单号
		String contOrderId = bgEfOrderService.getBgAutoTransferAuth();
		bocParam.put("channel", "000002");//交易渠道 000002(网页)
		bocParam.put("accountId", StaticData.risk);//买入方账号
		bocParam.put("txAmount", NumberUtil.parseDouble(eo.getEfFectiveAmt()));//交易金额
		bocParam.put("txFee", 0);//向卖出方收取的手续费
		bocParam.put("tsfAmount", NumberUtil.parseDouble(eo.getEfFectiveAmt()));//转让金额
		bocParam.put("forAccountId", bank_account);//卖出方账号
		bocParam.put("orgOrderId", eo.getInvestSeriNum());//卖出方投标的原订单号 （或卖出方购买债权的原订单号）
		bocParam.put("orgTxAmount", NumberFormat.formatDouble(eo.getPrincipal()));//卖出方投标的原交易金额 （或卖出方购买债权的原转让金额）
		bocParam.put("productId", order.getOrderPrdNumber());//卖出方原标的号
		bocParam.put("contOrderId", contOrderId);//买入方自动债权转让签约订单号
//			bocJson.put("contOrderId", "00000920170519110326594721810B");//买入方自动债权转让签约订单号
		bocParam.put("trdresv", "3_1:"+paycontrolId);//第三方保留域
		bocParam.put("acqRes", "");
		bocParam.put("remark", "zt_sl_"+paycontrolId);
		bocParam.put("sendAppName", "erpv3_auto2");
		bocParam.put("sysTarget", "ERP");
		JSONObject bocResult = WebServiceUtil.sendPost(SysConfig.getInstance().getProperty("pay.center.api")+"payProcess/transfer", new Object[]{bocParam});

		//如果红包错误
		if(bocResult.get("responseCode") == null || bocResult.get("responseCode")=="" || "0".equals(bocResult.getString("responseCode"))){
			logger.warn("赎楼贷在进行批量债权转让时上传失败!理财订单id:"+eo.getId());
			JSONObject smsJson = new JSONObject();
			smsJson.put("project_number", "batchTransferUploadService_batchUploadFiles");
			smsJson.put("text", "赎楼贷在进行批量债权转让时上传失败!理财订单id:"+eo.getId());
			SmsUtil.senErrorMsg(smsJson);
		}else{
			logger.warn("赎楼贷在进行批量债权转让时上传成功!"+eo.getId());
			
			//开始进行债权转让的记录
			bgEfPaycontrolMap = bgEfOrderDao.getCurrentEfBocControls(paycontrolId);
			if(bgEfPaycontrolMap == null)
			{
				logger.warn("赎楼贷在进行批量债权转让时上传失败!理财订单id:"+eo.getId());
			}
			bgEfPaycontrolMap.put("clearing_channel","2");
			bgEfPaycontrolMap.put("onLine",bgEfPaycontrolMap.get("online_type").toString());
			bgEfPaycontrolMap.put("authcode",bocResult.getString("investAuthCode"));
			bgEfPaycontrolMap.put("seriNo",bocResult.getString("seriNo"));
			bgEfPaycontrolMap.put("investment_model",eo.getInvestmentModel());
			bgEfPaycontrolMap.put("surplus_principal",eo.getPrincipal());
			bgEfOrderService.normalRepayByModel(bgEfPaycontrolMap);
		}
		} catch (Exception e) {
			logger.warn("给相关的理财利息异常!订单ID:"+eo.getId()+".");
			return;
		}
		
		try {
			BgEfPaycontrol bgEfPaycontrolE = (BgEfPaycontrol)crmOrderDao.findById(BgEfPaycontrol.class, paycontrolId);

			//5.客户积分获取记录以及该用户的积分更新问题
			BgScoreRecord bsr = new  BgScoreRecord();
			bsr.setEfOrderId(eo.getId());
			bsr.setCreateTime(DateUtil.getCurrentTime());
			bsr.setCustId(eo.getCustId());
			bsr.setIncomeSource("投资还款");
			if(ChkUtil.isNotEmpty(scoreScale)){
				bsr.setScore(String.valueOf(Math.round(ArithUtil.mul(
							ArithUtil.add(interest,bgEfPaycontrolE.getCouponInterest()),
							bgEfPaycontrolE.getScoreScale()/100d
							))));
			}
			bsr.setUpdateTime(DateUtil.getCurrentTime());
			if(NumberUtil.parseDouble(bsr.getScore())>0){
				bsr.setRealTimeScore(efcustomer.getScore());
				crmOrderDao.add(bsr);
			}
			
			//如果积分>0才进行存储
			//-------------------------------------------------------------------
			//修改积分
			String oldScore="0";
			if(ChkUtil.isNotEmpty(efcustomer.getScore())){
				oldScore = efcustomer.getScore();
			}
			String currentScore  = String.valueOf(Long.parseLong(oldScore)+Long.parseLong(bsr.getScore()));
			efcustomer.setScore(currentScore);
			crmOrderDao.update(efcustomer);

		} catch (Exception e) {
			logger.warn("信用公司购买借款人的理财订单ID:"+eo.getId()+",站内信发送失败!");
		}
	}
}
