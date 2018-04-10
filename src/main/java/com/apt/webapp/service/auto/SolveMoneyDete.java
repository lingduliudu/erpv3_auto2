package com.apt.webapp.service.auto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.apt.util.BusinessTool;
import com.apt.util.ChkUtil;
import com.apt.util.JsonUtil;
import com.apt.util.ListTool;
import com.apt.util.NumberFormat;
import com.apt.util.NumberUtil;
import com.apt.util.StaticData;
import com.apt.util.SysConfig;
import com.apt.util.WebServiceUtil;
import com.apt.util.pocTool;
import com.apt.util.arith.ArithUtil;
import com.apt.util.date.DateUtil;
import com.apt.util.http.HttpUtil;
import com.apt.util.singature.SignatureUtil;
import com.apt.util.sms.SmsUtil;
import com.apt.webapp.dao.bg.customer.ICustomerDao;
import com.apt.webapp.dao.bg.ef.IBgEfOrderDao;
import com.apt.webapp.dao.crm.ICrmOrderDao;
import com.apt.webapp.dao.ef.IZZLEfPaycontrolDao;
import com.apt.webapp.model.bg.ef.BgCustomer;
import com.apt.webapp.model.bg.ef.BgEfOrders;
import com.apt.webapp.model.bg.ef.BgEfPaycontrol;
import com.apt.webapp.model.bg.ef.BgEfPayrecord;
import com.apt.webapp.model.bg.ef.BgReferralIncomeRecord;
import com.apt.webapp.model.bg.ef.BgScoreRecord;
import com.apt.webapp.model.crm.CrmOrder;
import com.apt.webapp.model.crm.CrmPaycontrol;
import com.apt.webapp.model.ef.EfPaycontrol;
import com.apt.webapp.model.ef.ZZLEfPaycontrol;
import com.apt.webapp.service.bg.ef.IBgEfOrderService;
import com.apt.webapp.service.crm.ICrmOrderService;
import com.apt.webapp.service.crm.IPsCheckMoneyService;
import com.apt.webapp.service.ef.IEfFundRecordService;
import com.apt.webapp.service.ef.IEfOrderService;
import com.apt.webapp.service.ef.IEfPaycontrolService;
import com.apt.webapp.service.ef.IEfPayrecordService;
import com.apt.webapp.service.ef.IZZLEfPaycontrolService;
import com.apt.webapp.service.ef.IZZLEfPayrecordService;

/**
 * 功能说明：v3的定投自动执行方法
 * 典型用法：
 * 特殊用法：	
 * @author 袁浩
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015年10月20日 
 * Copyright zzl-apt
 */
@Component
public class SolveMoneyDete {
	//日志
	private Logger logger = LoggerFactory.getLogger(SolveMoneyDete.class);
	@Resource
	private ICrmOrderDao crmOrderDaoImpl;
	@Resource
	private IBgEfOrderDao bgEfOrderDaoImpl;
	@Resource
	private IBgEfOrderService bgEfOrderService;
	@Resource
	private IEfPaycontrolService efPaycontrolService;
	@Resource
	private IZZLEfPaycontrolDao zZLEfPaycontrolDaoImpl;
	
	@Resource
	private IPsCheckMoneyService psCheckMoneyService;
	
	@Resource
	private IEfOrderService efOrderService;
	@Resource
	private ICrmOrderService crmOrderService;
	/**
	 * 功能说明：查找信贷手动还款的正常还款直投
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
	public JSONObject toExecuteNormalImme(Map baseMap){
		//返回结果
		JSONObject resultJson = new JSONObject();
		resultJson.put("size", 0);
		//获得数据
		List<Map> immeList  =  crmOrderDaoImpl.getEfImmeByCrmOrderAndDay(baseMap.get("crm_order_id").toString(),baseMap.get("payTime").toString());
		List<Map> immeRedAccountList  =  crmOrderDaoImpl.getEfImmeByCrmOrderAndDayRedAccount(baseMap.get("crm_order_id").toString(),baseMap.get("payTime").toString());
		//
		
		if(ListTool.isNotNullOrEmpty(immeList)){
			resultJson.put("size",immeList.size());
		}
		
		toSolveNormalImmeRedAccound(immeRedAccountList);
		toSolveNormalImme(immeList, baseMap.get("serino").toString());
		
		
		return resultJson;
	}
	/**
	 * 功能说明：查找信贷手动还款的正常还款直投
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
	public JSONObject toExecuteA007(Map baseMap){
		//返回结果
		JSONObject resultJson = new JSONObject();
		resultJson.put("size", 0);
		//获得数据
		List<Map> eList  =  crmOrderDaoImpl.getA007EfByCrmOrder(baseMap.get("crm_order_id").toString());
		if(ListTool.isNotNullOrEmpty(eList)){
			resultJson.put("size",eList.size());
		}
		toSolveA007(eList, baseMap.get("serino").toString());
		return resultJson;
	}
	/**
	 * 功能说明：查找信贷手动还款的提前结清还款直投
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
	public JSONObject toExecuteAdvanceImme(Map baseMap){
		//返回结果
		JSONObject resultJson = new JSONObject();
		resultJson.put("size", 0);
		//获得数据
		List<Map> immeList  =  crmOrderDaoImpl.getEfImmeByCrmOrder(baseMap.get("crm_order_id").toString());
		if(ListTool.isNotNullOrEmpty(immeList)){
			resultJson.put("size",immeList.size());
		}
		
		toSolveAdvanceImme(immeList, JSONObject.fromObject(baseMap));
		
		
		return resultJson;
	}
	
	/**
	 * 功能说明：执行线上信贷还款
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
	public JSONObject toExecuteNormalCrmBoc(JSONObject paramJson){
		if("1".equals(paramJson.getString("type"))){paramJson.put("checkMoneyType", "5");}
		if("2".equals(paramJson.getString("type"))){paramJson.put("checkMoneyType", "1");}
		if("3".equals(paramJson.getString("type"))){paramJson.put("checkMoneyType", "2");}
		JSONObject resultJson  = new JSONObject();
		String info = "";
		resultJson.put("status", "1");
		//获得当日的需要还款的crm信贷明细
		List<Map> zzlEfpaycontrols = zZLEfPaycontrolDaoImpl.getCurrentByCrmControl(paramJson.getString("crmOrderId").toString(),paramJson.getString("payTime"));
		if(ListTool.isNotNullOrEmpty(zzlEfpaycontrols)){
			//判断有没有非红包户，如果没有，则不进行还款
			boolean flagz=false;
			for(Map zzlEfpaycontrol:zzlEfpaycontrols){
				String cust_info_id = zzlEfpaycontrol.get("cust_info_id").toString();
				if (!StaticData.redCustInfoId.equals(cust_info_id)) {
					flagz=true;
				}
			}
			if (!flagz) {
				resultJson.put("info", info);
				return resultJson;
			}
			//开始还款
			JSONArray jsonArray = new JSONArray();
			String recordSql = 
							"SELECT " +
							"	bci.bank_account, " +
							"	bci.id cust_info_id, " +
							"	cpr.should_accrual accrual, " +
							"	cpr.manage_fee manage, " +
							"	cpr.should_interest interest, " +
							"	cpr.overdue_violate_money overdueViolate, " +
							"	cpr.should_capital capital, " +
							"	cpc.should_capiital capitalMoney " +
							"FROM " +
							"	crm_payrecoder cpr, " +
							"	bg_cust_info bci, " +
							"	crm_paycontrol cpc " +
							"WHERE " +
							" cpc.id=cpr.paycontrol_id " +
							"and cpc.id='"+paramJson.getString("crm_paycontrol_id")+"' " +
							"and bci.id=cpc.cust_info_id " +
							"AND cpr.batch_transfer_serialno = '"+paramJson.getString("serino")+"' ";
			paramJson.putAll(bgEfOrderDaoImpl.queryBySqlReturnMapList(recordSql).get(0));
			boolean forzenFlag = frozen(zzlEfpaycontrols,paramJson);
			if(!forzenFlag){
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", paramJson.getString("crmOrderId"));
				smsJson.put("text","线上信贷订单冻结成功但在进行zzl债权数据修改时失败,订单id:"+paramJson.getString("crmOrderId"));
				SmsUtil.senErrorMsg(smsJson);
				resultJson.put("status", "0");
				resultJson.put("info", "线上信贷订单冻结成功但在进行zzl债权数据修改时失败!");
				return resultJson;
			}
			for(Map zzlEfpaycontrol:zzlEfpaycontrols){
				ZZLEfPaycontrol zzl=null;
				CrmOrder crmOrder = null;
				try {
					zzl = (ZZLEfPaycontrol) zZLEfPaycontrolDaoImpl.findById(ZZLEfPaycontrol.class,zzlEfpaycontrol.get("id").toString());
					//开始判断是否需要往下走
					double totalMoney = ArithUtil.add(new Double[]{
							zzl.getTempPrincipal(),
							zzl.getSurplusInterest(),
							zzl.getSurplusManagementAmt(),
							zzl.getMoreManageAmt(),
							zzl.getOverCost(),
							zzl.getOverPenalty()
					});
					if(totalMoney<=0){
						continue;
					}
					crmOrder = (CrmOrder) bgEfOrderDaoImpl.findById(CrmOrder.class,zzl.getCrmOrderId());
				} catch (Exception e) {
					e.printStackTrace();
				}
				JSONObject cuJson = new JSONObject();
				cuJson.put("forAccountId", StaticData.risk);//入账账户
				if (StaticData.redCustInfoId.equals(zzl.getCustInfoId())) {
					/*
					 * 如果是红包户 
					 */
					logger.warn("投资人为红包账户，还款入账用户改为红包账户！");
					cuJson.put("forAccountId",StaticData.redEnvelope);			
				}
				cuJson.put("txAmount", zzl.getTempPrincipal());//交易金额
				cuJson.put("intAmount", 0d);//交易利息
				cuJson.put("txFeeOut", ArithUtil.add(new Double[]{				//借款人手续费 = 剩余管理费+多出的管理费+剩余利息+多出来的利息
						zzl.getSurplusInterest(),
						zzl.getSurplusManagementAmt(),
						zzl.getMoreInterest(),
						zzl.getMoreManageAmt(),
						zzl.getOverCost(),
						zzl.getOverPenalty()
				}));//还款手续费
				cuJson.put("txFeeIn", 0d);//收款手续费
				cuJson.put("accountId", paramJson.getString("bank_account"));//扣款账户
				cuJson.put("productId", crmOrder.getOrderPrdNumber());//标的号
				String auth_code = zzlEfpaycontrol.get("auth_code").toString();
				cuJson.put("authCode", auth_code);//授权码
				if (auth_code.contains("_needRemove")) {
					cuJson.put("authCode", auth_code.substring(0, auth_code.indexOf("_needRemove")));//授权码
				}
				cuJson.put("trdresv",zzlEfpaycontrol.get("id"));
				//开始检测本金是否为0元
				if(cuJson.get("txAmount")!=null && cuJson.get("txFeeOut")!=null){
					if(cuJson.getDouble("txAmount")==0 && cuJson.getDouble("txFeeOut")>0){ //金额为0需要从手续费中获得
						cuJson.put("txAmount", 0.01d);
						cuJson.put("txFeeOut",NumberFormat.format(ArithUtil.sub(cuJson.getDouble("txFeeOut"), 0.01)));
					}
				}
				jsonArray.add(cuJson);
			}
			JSONObject transMap = new JSONObject();
			transMap.put("acqRes","");//第三方保留域
			transMap.put("subPacks", jsonArray.toString());
			transMap.put("signature", SignatureUtil.createSign());
			transMap.put("sendAppName",StaticData.appName);//
			transMap.put("remark","");//保留域
			JSONObject	bocResultJson  = WebServiceUtil.sendPost(StaticData.bocUrl+"payProcess/batchRepay", new Object[]{transMap});
			//开始产生资金记录
			JSONObject checkMoneyJson = new JSONObject();
			checkMoneyJson.put("cardNo",paramJson.get("bank_account"));
			checkMoneyJson.put("cust_info_id",paramJson.get("cust_info_id"));			//客户id
			checkMoneyJson.put("money",ArithUtil.add(JsonUtil.getSum(jsonArray, "txAmount"),JsonUtil.getSum(jsonArray, "txFeeOut")));					// 金额
			checkMoneyJson.put("money_type","4"); 			// 1金账户2,银行卡3,现场交钱4,电子账户
			checkMoneyJson.put("operation_type","2"); 		//1入账2出账
			checkMoneyJson.put("status","0");				//1成功0失败
			checkMoneyJson.put("person_type","2");			//1,投资人2借款人3,邀请人
			checkMoneyJson.put("type",paramJson.getString("checkMoneyType"));					//1,提前结清2提前收回3,逾期还款4,部分逾期,5正常还款	
			checkMoneyJson.put("crm_order_id",paramJson.getString("crmOrderId"));
			checkMoneyJson.put("operator", paramJson.getString("empId"));
			String checkMoneyId = psCheckMoneyService.save(checkMoneyJson);
			//债权转让失败了
			if(!bocResultJson.containsKey("responseCode") || !"1".equals(bocResultJson.getString("responseCode"))){
				logger.warn("线上信贷订单冻结成功,明细id:"+paramJson.get("crm_order_id")+",BOC返回失败!");
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", paramJson.get("crm_order_id"));
				smsJson.put("text","线上信贷订单冻结成功,明细id:"+paramJson.get("crm_order_id")+",BOC返回失败!");
				SmsUtil.senErrorMsg(smsJson);
			}else{
				//资金记录修改
				psCheckMoneyService.updateStatusById(checkMoneyId);
			}
		}
		resultJson.put("info", info);
		return resultJson;
	}
	
	/**
	 * 功能说明：查找信贷手动还款的提前结清还款定投
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
	public JSONObject toExecuteAdvanceDete(Map baseMap){
		//返回结果
		JSONObject resultJson = new JSONObject();
		resultJson.put("size", 0);
		//获得数据
		List<Map> deteList  =  crmOrderDaoImpl.getEfDeteByCrmOrder(baseMap.get("crm_order_id").toString());
		if(ListTool.isNotNullOrEmpty(deteList)){
			resultJson.put("size",deteList.size());
			toSolveAdvanceDete(deteList, JSONObject.fromObject(baseMap));
		}
		return resultJson;
	}
	/**
	 * 功能说明：查找信贷手动还款的正常还款定投
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
	public JSONObject toExecuteNormalDete(Map baseMap){
		//返回结果
		JSONObject resultJson = new JSONObject();
		resultJson.put("size", 0);
		//获得数据
		List<Map> deteList  =  crmOrderDaoImpl.getEfDeteByCrmOrderAndDay(baseMap.get("crm_order_id").toString(),baseMap.get("payTime").toString());
		if(ListTool.isNotNullOrEmpty(deteList)){
			resultJson.put("size",deteList.size());
			toSolveNormalDete(deteList, baseMap.get("serino").toString());
		}
		return resultJson;
	}
	
	/**
	 * 功能说明：开始处理定投的正常还款
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
	public void toSolveNormalDete(List<Map> deteList,String seriNo){
		//查找今天需要还款的理财明细并通过债券转让的方式还款
		//1.查找今日待还
		//3.购买债券
		//4.更新操作  >> 借款人(订单明细记录) >> 投资人(订单明细记录) >>ZZL (订单明细记录)
		//踢出资产包相关数据
		crmOrderService.removePkgCrmOrders(deteList);
		if(ListTool.isNotNullOrEmpty(deteList)){ //这个列表来判断是否需要进行债权转让
			//查找买入方 债转签约流水号
			String contOrderId = bgEfOrderService.getBgAutoTransferAuth();
			JSONArray transArray = new JSONArray();
			for(int index=0;index<deteList.size();index++){
				Map needTransMap = deteList.get(index);
				//开始检测是否已经被处理过了
				if(needTransMap.containsKey("id")){
					BgEfPaycontrol bec =  (BgEfPaycontrol) bgEfOrderService.findById(BgEfPaycontrol.class, needTransMap.get("id").toString());
					if(bec != null &&  bec.getPayStatus()!=null && bec.getPayStatus()==1 ){ //说明已经被处理过了
						continue;
					}
					if(bec.getOperateType()!=null && "1".equals(bec.getOperateType().toString())){ //说明在操作的时候已经被手动操作过
						continue;
					}
					try{
						bec.setOperateType("1");
						efPaycontrolService.update(bec);
					}catch (Exception e) {
						logger.warn("理财明细在修改自动状态时失败!理财明细id:"+needTransMap.get("id").toString());
					}
				}
				//本金0元直接算结束
				if( NumberUtil.parseDouble(needTransMap.get("surplus_principal"))<=0){
					needTransMap.put("tasteOrder","1");
					bgEfOrderService.normalRepayByModel(needTransMap);
					continue;
				}
				//改为直接债权成功接口
				JSONObject trans =new JSONObject();
				trans.put("channel", "000002");
				trans.put("accountId",StaticData.risk);
				trans.put("txAmount",NumberUtil.parseDouble(needTransMap.get("surplus_principal")));
				trans.put("txFee",0);
				trans.put("tsfAmount",NumberUtil.parseDouble(needTransMap.get("surplus_principal")));
				trans.put("forAccountId",needTransMap.get("bank_account"));
				trans.put("orgOrderId",needTransMap.get("invest_seri_num"));
				trans.put("orgTxAmount",NumberFormat.formatDouble(Double.parseDouble(needTransMap.get("principal").toString())));
				trans.put("productId",needTransMap.get("order_prd_number"));
				trans.put("contOrderId",contOrderId);
				trans.put("sysTarget","ERP");
				trans.put("sendAppName",StaticData.appName);
				trans.put("remark","Ndt_:"+needTransMap.get("id"));
				JSONObject resultJson = WebServiceUtil.sendPost(StaticData.bocUrl+"payProcess/transfer", new Object[]{trans});
				//如果红包错误
				if("0".equals(resultJson.getString("responseCode"))){
					logger.warn("手动BOC理财明细在进行批量债权转让时失败!id:"+needTransMap.get("id")+";错误信息:"+resultJson.getString("data")+";银行返回错误码："+resultJson.getString("responseBankCode"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", "transfer_error");
					smsJson.put("text", "定投BOC理财明细在进行债权转让时失败!id:"+needTransMap.get("id")+";错误信息:"+resultJson.getString("data")+";银行返回错误码："+resultJson.getString("responseBankCode"));
					SmsUtil.senErrorMsg(smsJson);
				}else{
					logger.warn("定投BOC理财明细在进行债权转让时成功!id:"+needTransMap.get("id"));
					//开始进行债权转让的记录
					needTransMap = bgEfOrderDaoImpl.getCurrentEfBocControls(needTransMap.get("id").toString());
					needTransMap.put("clearing_channel","2");
					needTransMap.put("onLine",needTransMap.get("online_type").toString());
					needTransMap.put("authcode",resultJson.getString("investAuthCode"));
					needTransMap.put("seriNo",resultJson.getString("seriNo"));
					needTransMap.put("investment_model",needTransMap.get("investment_model").toString());
					bgEfOrderService.normalRepayByModel(needTransMap);
					//开始进行可用资金存储
					if("2".equals(needTransMap.get("investment_model"))){ //定投
						//非专业投资人才需要重新处理
						if(BusinessTool.isNotProfessionalInvestor(needTransMap.get("investor_type"))){
							efOrderService.lockRowForEforderAuth(needTransMap); //处理
						}
						if(BusinessTool.isProfessionalInvestor(needTransMap.get("investor_type"))){
							//查询明细
							List<Map> epcList =  bgEfOrderService.findLineEfPaycontrolHasPrincipal(needTransMap.get("lineEfOrderId").toString());
							if(ListTool.isNotNullOrEmpty(epcList)){
								Map  epcMap = epcList.get(0);
								EfPaycontrol epc = (EfPaycontrol) bgEfOrderService.findById(EfPaycontrol.class, epcMap.get("id").toString());
								epc.setSurplusPrincipal(NumberFormat.format(ArithUtil.sub(epc.getSurplusPrincipal(), Double.parseDouble(needTransMap.get("princiapl").toString()))));
								bgEfOrderService.update(epc);
							}
						}
						
					}
				}
			} //需要进行债权转让的循环结束
		}
	}
	
	/**
	 * 功能说明：开始处理直投的正常还款
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
	public JSONObject toSolveAdvanceImme(List<Map> immeList,JSONObject immeJson){
		String serialNo = immeJson.getString("serino"); 
		JSONObject resultJson = new JSONObject();
		List<Map> needTransList = new ArrayList<Map>(); //可以进行债权转让的列表
		JSONArray transArray = new JSONArray();
		//查找对应的理财订单
		if(ListTool.isNullOrEmpty(immeList)){
			return resultJson;
		}
		for(Map eoMap:immeList){
			//准备订单还款类型
			String efOrderSql ="UPDATE  bg_ef_orders set pay_status=1 WHERE id = '"+eoMap.get("id").toString()+"' ";
			try {
				//更新转让订单状态
				crmOrderDaoImpl.executeSql(efOrderSql);
				String transSql ="UPDATE  bg_transfer_sellapply set status='1', cancle_time='"+DateUtil.getCurrentTime()+"' WHERE ef_order_id = '"+eoMap.get("id").toString()+"' ";
				// 更新转让记录
				crmOrderDaoImpl.executeSql(transSql);
				//开始计算利息
				JSONObject infoJson = getInterest(eoMap.get("id").toString());
				infoJson.put("cust_info_id", eoMap.get("cust_info_id"));
				infoJson.put("cust_id", eoMap.get("cust_id"));
				infoJson.put("efOrderId", eoMap.get("id"));
				infoJson.put("bank_account", eoMap.get("bank_account"));
				infoJson.put("crmOrderId", immeJson.getString("crmOrderId"));
				infoJson.put("empId", immeJson.getString("empId"));
				infoJson.put("lineEfOrderId", eoMap.get("lineEfOrderId"));
				//开始计算违约金
				String touSql = 
						"SELECT  "+
						"bedp.pre_manage_rate/100 advanceRate "+
						"from "+
						"crm_order co, "+
						"bg_ef_product_detail bedp "+
						"where co.ef_prd_detail_id = bedp.id and co.id='"+immeJson.getString("crmOrderId")+"'";
				immeJson.putAll(crmOrderDaoImpl.queryBySqlReturnMapList(touSql).get(0));
				infoJson.put("prePenalty", Double.parseDouble(eoMap.get("ef_fective_amt").toString())*immeJson.getDouble("advanceRate"));
				JSONObject bocJson = new JSONObject();
				JSONObject bocResult = new JSONObject();
				if (StaticData.redCustInfoId.equals(eoMap.get("cust_info_id"))) {
					/*
					 * 如果是红包户 ，不发红包，直接进行转账
					 */
					logger.warn("理财订单投资人为红包账户!不进行红包发放！理财订单id:"+eoMap.get("id"));
				}else{
					bocJson.put("cardNbr", StaticData.redEnvelope);  					//转出账户--> 红包账户
					bocJson.put("cardNbrIn", eoMap.get("bank_account"));  				//转入账户--> 投资人
					double touMoney =0d;
					if("no".equals(infoJson.getString("has"))){
						touMoney = ArithUtil.add(new Double[]{infoJson.getDouble("interest"),
									infoJson.getDouble("prePenalty")
									});
					}else{
						bocJson.put("amount",0);
					}
					if(touMoney>0){
						//
						JSONObject redBocJson = new JSONObject();
						redBocJson.put("orderNo", eoMap.get("id"));
						redBocJson.put("remark", "");
						redBocJson.put("acqRes", "");
						redBocJson.put("sendAppName", "erpv3_auto2");
						redBocJson.put("forAccountId", eoMap.get("bank_account").toString());  		//转入账户--> 投资人
						redBocJson.put("txAmount",NumberFormat.format(touMoney));  		//转入账户--> 投资人
						JSONObject redBocResult = WebServiceUtil.sendPost(SysConfig.getInstance().getProperty("pay.center.api")+"payProcess/redPayment", new Object[]{redBocJson});
						//如果红包错误
						if("0".equals(redBocResult.getString("responseCode"))){
							logger.warn("手动BOC理财明细在发放红包时失败!订单id:"+eoMap.get("id")+";错误信息:"+bocResult.getString("data")+";银行返回错误码："+bocResult.getString("responseBankCode"));
							JSONObject smsJson = new JSONObject();
							smsJson.put("project_number", eoMap.get("id"));
							smsJson.put("text", "手动BOC理财明细在发放红包时失败!订单id:"+eoMap.get("id"));
							SmsUtil.senErrorMsg(smsJson);
						}else{
							logger.warn("手动BOC理财明细在发放红包时成功!订单id:"+eoMap.get("id"));
						}
					}
				}
				//准备修改明细和记录(针对利息的记录)
				cleanInterestAdvanceClear(infoJson);
				//开始发送邀请人红包
				bocJson.clear();
				if(infoJson.containsKey("invite_bank_account")){
					double inviterMoney = 0d;
					bocJson.put("cardNbr", StaticData.redEnvelope);  					//转出账户--> 红包账户
					bocJson.put("cardNbrIn", infoJson.getString("invite_bank_account"));  		//转入账户--> 邀请人
					if("no".equals(infoJson.getString("has"))){
						inviterMoney = infoJson.getDouble("invite");
						bocJson.put("amount",infoJson.getDouble("invite"));  				//转入账户--> 投资人
					}else{
						bocJson.put("amount",0);  				//转入账户--> 投资人
					}
					bocResult.clear();
					if(inviterMoney>0){
						try{
							//
							JSONObject redBocJson = new JSONObject();
							redBocJson.put("orderNo", eoMap.get("id"));
							redBocJson.put("remark", "");
							redBocJson.put("acqRes", "");
							redBocJson.put("sendAppName", "erpv3_auto2");
							redBocJson.put("forAccountId", infoJson.getString("invite_bank_account"));  		//转入账户--> 邀请人
							redBocJson.put("txAmount",NumberFormat.format(inviterMoney));  		//转入账户--> 投资人
							JSONObject redBocResult = WebServiceUtil.sendPost(SysConfig.getInstance().getProperty("pay.center.api")+"payProcess/redPayment", new Object[]{redBocJson});
							//如果红包错误
							if("0".equals(redBocResult.getString("responseCode"))){
								logger.warn("提前结清发放邀请人收益红包时失败!邀请人金额:"+NumberFormat.format(inviterMoney)+"订单id:"+eoMap.get("id"));
								JSONObject smsJson = new JSONObject();
								smsJson.put("project_number", eoMap.get("efOrderNumber"));
								smsJson.put("text", "提前结清发放邀请人收益红包时失败!订单id:"+eoMap.get("id"));
								SmsUtil.senErrorMsg(smsJson);
							}else{
								logger.warn("提前结清发放邀请人收益红包时成功!订单id:"+eoMap.get("id"));
							}
						}catch (Exception e) {
							logger.warn(e.getMessage(),e);
						}
					}
				}
				if (StaticData.redCustInfoId.equals(eoMap.get("cust_info_id"))) {
					/*
					 * 如果是红包户 ，不发红包，直接进行转账
					 */
					// 开始计算利息
					infoJson.put("AUTHCODE", eoMap.get("invest_auz_code"));
					infoJson.put("seriNo", eoMap.get("invest_seri_num"));
					advanceClearNormalOnLine(infoJson);
					continue;
				}
				//开始购买债权
				bocJson.clear();
				String riskContorderSql = "SELECT bata.serino from bg_cust_info bci,bg_auto_transfer_auth bata where bci.id=bata.cust_info_id and  bci.bank_account='"+StaticData.risk+"'";
				String riskContorder = "";
				try{
					riskContorder = bgEfOrderDaoImpl.queryBySqlReturnMapList(riskContorderSql).get(0).get("serino").toString();
				}catch (Exception e) {
					e.printStackTrace();
					logger.warn(e.getMessage(),e);
					logger.warn("查找风险户签约订单号失败!");
				}
				//改为直接债权成功接口
				double tbalAce = Double.parseDouble(NumberFormat.formatDouble(eoMap.get("principal")));
				JSONObject trans =new JSONObject();
				trans.put("channel", "000002");
				trans.put("accountId",StaticData.risk);
				trans.put("txAmount",NumberUtil.parseDouble(infoJson.get("capital")));
				trans.put("txFee",NumberUtil.parseDouble(infoJson.get("manage")));
				trans.put("tsfAmount",NumberUtil.parseDouble(infoJson.get("capital")));
				trans.put("forAccountId",eoMap.get("bank_account"));
				trans.put("orgOrderId",eoMap.get("invest_seri_num"));
				trans.put("orgTxAmount",NumberFormat.formatDouble(tbalAce));
				trans.put("productId",eoMap.get("order_prd_number"));
				trans.put("contOrderId",riskContorder);
				trans.put("sysTarget","ERP");
				trans.put("sendAppName",StaticData.appName);
				trans.put("remark","Nzta_:"+eoMap.get("id"));
				resultJson = WebServiceUtil.sendPost(StaticData.bocUrl+"payProcess/transfer", new Object[]{trans});
				//如果错误
				if("0".equals(resultJson.getString("responseCode"))){
					logger.warn("手动BOC理财明细在进行债权转让时失败!id:"+eoMap.get("id")+";错误信息:"+resultJson.getString("data")+";银行返回错误码："+resultJson.getString("responseBankCode"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", "transfer_error");
					smsJson.put("text", "直投BOC理财明细在进行债权转让时失败!id:"+eoMap.get("id")+";错误信息:"+resultJson.getString("data")+";银行返回错误码："+resultJson.getString("responseBankCode"));
					SmsUtil.senErrorMsg(smsJson);
				}else{
					logger.warn("直投BOC理财明细在进行债权转让时成功!订单id:"+eoMap.get("id"));
					//开始进行债权转让的记录
					//1.结清订单
					//2.重新冻结
					infoJson.put("AUTHCODE", resultJson.getString("investAuthCode"));
					infoJson.put("seriNo", resultJson.getString("seriNo"));
					advanceClearNormalOnLine(infoJson);
				}
				
			} catch (Exception e2) {
				e2.printStackTrace();
				logger.warn(e2.getMessage(),e2);
				logger.warn("借款人线上投资人提前结清还款时失败!理财订单id:"+eoMap.get("id").toString());
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", "erpv3_service");
				smsJson.put("text", "借款人线上投资人提前结清还款时失败!理财订单id:"+eoMap.get("id").toString());
				SmsUtil.senErrorMsg(smsJson);
			}
		}
		return resultJson;
	
	}
	/**
	 * 功能说明：开始处理定投的提前结清还款
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
	public JSONObject toSolveAdvanceDete(List<Map> deteList,JSONObject deteJson){
		JSONObject resultJson = new JSONObject();
		//查找对应的理财订单
		if(ListTool.isNullOrEmpty(deteList)){
			return resultJson;
		}
		for(Map eoMap:deteList){
			//开始计算利息
			JSONObject infoJson = getInterest(eoMap.get("id").toString());
			infoJson.put("lineEfOrderId", eoMap.get("lineEfOrderId"));
			infoJson.put("ef_order_id", eoMap.get("id"));
			infoJson.put("cust_info_id", eoMap.get("cust_info_id"));
			infoJson.put("cust_id", eoMap.get("cust_id"));
			infoJson.put("efOrderId", eoMap.get("id"));
			infoJson.put("bank_account", eoMap.get("bank_account"));
			infoJson.put("crmOrderId", deteJson.getString("crmOrderId"));
			infoJson.put("empId", deteJson.getString("empId"));
			//因为是定投,所以利息和管理费和提前结清直接归零
			infoJson.put("interestReal", 0d);
			infoJson.put("manage", 0d);
			infoJson.put("prePenalty", 0d);
			infoJson.put("coupon", 0d);
			
			
			//准备订单还款类型
			String efOrderSql ="UPDATE  bg_ef_orders set pay_status=1 WHERE id = '"+eoMap.get("id").toString()+"' ";
			try {
				bgEfOrderDaoImpl.executeSql(efOrderSql);
			} catch (Exception e2) {
				e2.printStackTrace();
				logger.warn(e2.getMessage(),e2);
				logger.warn("更新转让订单状态时失败!理财订单id:"+eoMap.get("id").toString());
			}
			String transSql ="UPDATE  bg_transfer_sellapply set status='1' , cancle_time='"+DateUtil.getCurrentTime()+"' WHERE ef_order_id = '"+eoMap.get("id").toString()+"' ";
			try {
				bgEfOrderDaoImpl.executeSql(transSql);
				
			} catch (Exception e2) {
				e2.printStackTrace();
				logger.warn(e2.getMessage(),e2);
				logger.warn("更新转让记录时失败!理财订单id:"+eoMap.get("id").toString());
			}
			String epcsql = 	"SELECT "+
								"IFNULL(sum(surplus_principal),0) totalCapital "+
								"FROM bg_ef_paycontrol where ef_order_id='"+eoMap.get("id").toString()+"' and live_status=1 and pay_status='0' and (operate_type is null or operate_type='' or operate_type='1') ";
			List<Map> epcList = bgEfOrderDaoImpl.queryBySqlReturnMapList(epcsql);
			Map epcMap  = epcList.get(0);
			if(Double.parseDouble(epcMap.get("totalCapital").toString())<=0){
				continue;
			}
			//年华利率
			String rateSql = 
							"SELECT  "+
								"bepd.invest_rate "+ 
							"from  "+
								"bg_ef_orders beo   LEFT JOIN "+
								"crm_order co on  beo.crm_order_id = co.id  LEFT JOIN "+
								"pro_ef_product_detail bepd on  co.ef_prd_detail_id=bepd.id "+  
							"where  "+
								"beo.id='"+eoMap.get("id")+"' ";
			//eoMap.putAll(bgEfOrderDaoImpl.queryBySqlToLower(rateSql).get(0));
			String riskContorderSql = "SELECT bata.serino from bg_cust_info bci,bg_auto_transfer_auth bata where bci.id=bata.cust_info_id and  bci.bank_account='"+StaticData.risk+"'";
			String riskContorder = "";
			try{
				riskContorder = bgEfOrderDaoImpl.queryBySqlReturnMapList(riskContorderSql).get(0).get("serino").toString();
			}catch (Exception e) {
				e.printStackTrace();
				logger.warn(e.getMessage(),e);
				logger.warn("查找风险户签约订单号失败!");
			}
			//开始购买债权
			//改为直接债权成功接口
			try{
				double tbalAce = Double.parseDouble(NumberFormat.formatDouble(eoMap.get("principal")));
				JSONObject trans =new JSONObject();
				trans.put("channel", "000002");
				trans.put("accountId",StaticData.risk);
				trans.put("txAmount",NumberUtil.parseDouble(infoJson.get("capital")));
				trans.put("txFee",0d);
				trans.put("tsfAmount",NumberUtil.parseDouble(infoJson.get("capital")));
				trans.put("forAccountId",eoMap.get("bank_account"));
				trans.put("orgOrderId",eoMap.get("invest_seri_num"));
				trans.put("orgTxAmount",NumberFormat.formatDouble(tbalAce));
				trans.put("productId",eoMap.get("order_prd_number"));
				trans.put("contOrderId",riskContorder);
				trans.put("sysTarget","ERP");
				trans.put("sendAppName",StaticData.appName);
				trans.put("remark","Nzta_:"+eoMap.get("id"));
				resultJson = WebServiceUtil.sendPost(StaticData.bocUrl+"payProcess/transfer", new Object[]{trans});
				//如果错误
				if("0".equals(resultJson.getString("responseCode"))){
					logger.warn("手动BOC理财明细在进行债权转让时失败!id:"+eoMap.get("id")+";错误信息:"+resultJson.getString("data")+";银行返回错误码："+resultJson.getString("responseBankCode"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", "transfer_error");
					smsJson.put("text", "直投BOC理财明细在进行债权转让时失败!id:"+eoMap.get("id")+";错误信息:"+resultJson.getString("data")+";银行返回错误码："+resultJson.getString("responseBankCode"));
					SmsUtil.senErrorMsg(smsJson);
				}else{
					logger.warn("直投BOC理财明细在进行债权转让时成功!订单id:"+eoMap.get("id"));
					//开始进行债权转让的记录
					//1.结清订单
					//2.重新冻结
					infoJson.put("AUTHCODE", resultJson.getString("investAuthCode"));
					infoJson.put("seriNo", resultJson.getString("seriNo"));
					advanceClearNormalOnLine(infoJson);
					infoJson.put("advanceMoney", infoJson.getDouble("capital"));
					//非专业投资人
					if(BusinessTool.isNotProfessionalInvestor(eoMap.get("investor_type"))){
						efOrderService.lockRowForEforderAuthAdvance(infoJson); //处理
					}
					if(BusinessTool.isProfessionalInvestor(eoMap.get("investor_type"))){
						//查询明细
						List<Map> lineEpcList =  bgEfOrderService.findLineEfPaycontrolHasPrincipal(eoMap.get("lineEfOrderId").toString());
						if(ListTool.isNotNullOrEmpty(lineEpcList)){
							Map  lineepcMap = lineEpcList.get(0);
							EfPaycontrol epc = (EfPaycontrol) bgEfOrderService.findById(EfPaycontrol.class, lineepcMap.get("id").toString());
							epc.setSurplusPrincipal(NumberFormat.format(ArithUtil.sub(epc.getSurplusPrincipal(), Double.parseDouble(infoJson.get("capital").toString()))));
							bgEfOrderService.update(epc);
						}
					}
				}
			} catch (Exception e2) {
			e2.printStackTrace();
			logger.warn(e2.getMessage(),e2);
			logger.warn("借款人线上投资人提前结清还款时失败!理财订单id:"+eoMap.get("id").toString());
			JSONObject smsJson = new JSONObject();
			smsJson.put("project_number", "erpv3_service");
			smsJson.put("text", "借款人线上投资人提前结清还款时失败!理财订单id:"+eoMap.get("id").toString());
			SmsUtil.senErrorMsg(smsJson);
		}	
		}
		return resultJson;
	}
	/**
	 * 功能说明：开始处理直投的正常还款
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
	public void toSolveA007(List<Map> immeList,String seriNo){
		if(ListTool.isNullOrEmpty(immeList)){return;}
		//踢出资产包相关数据
		crmOrderService.removePkgCrmOrders(immeList);
		//循环-->发送红包-->购买债券-->生成对应的记录
		List<Map> needTransList = new ArrayList<Map>(); //可以进行债权转让的列表
		//查找买入方 债转签约流水号
		String contOrderId = bgEfOrderService.getBgAutoTransferAuth();
		for(Map efPaycontrol:immeList){
			//开始检测是否已经被处理过了
			if(efPaycontrol.containsKey("id")){
				BgEfPaycontrol bec =  (BgEfPaycontrol) bgEfOrderService.findById(BgEfPaycontrol.class, efPaycontrol.get("id").toString());
				if(bec != null &&  bec.getPayStatus()!=null && bec.getPayStatus()==1 ){ //说明已经被处理过了
					continue;
				}
				if(bec.getOperateType()!=null && "1".equals(bec.getOperateType().toString())){ //说明在操作的时候已经被手动操作过
					continue;
				}
				try{
					bec.setOperateType("1");
					efPaycontrolService.update(bec);
				}catch (Exception e) {
					logger.warn(e.getMessage(),e);
					logger.warn("理财明细在修改自动状态时失败!理财明细id:"+efPaycontrol.get("id").toString());
				}
			}
			//投资模式
			String investment_model = efPaycontrol.get("investment_model").toString() ;
			JSONObject bocJson = new JSONObject();
			if("1".equals(investment_model)){
				//开始发送红包
				bocJson.put("orderNo", efPaycontrol.get("id"));
				bocJson.put("remark", "");
				bocJson.put("acqRes", "");
				bocJson.put("sendAppName", "erpv3_auto2");
				bocJson.put("forAccountId", efPaycontrol.get("bank_account"));  		//转入账户--> 投资人
				bocJson.put("txAmount",NumberFormat.format(ArithUtil.add(new Double[]{
											NumberUtil.parseDouble(efPaycontrol.get("surplus_interest")),
											NumberUtil.parseDouble(efPaycontrol.get("coupon_interest"))
											})));  		//转入账户--> 投资人
				JSONObject bocResult = WebServiceUtil.sendPost(SysConfig.getInstance().getProperty("pay.center.api")+"payProcess/redPayment", new Object[]{bocJson});
				//如果红包错误
				if("0".equals(bocResult.getString("responseCode"))){
					logger.warn("手动BOC理财明细在发放红包时失败!理财明细id:"+efPaycontrol.get("id")+";错误信息:"+bocResult.getString("data")+";银行返回错误码："+bocResult.getString("responseBankCode"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
					smsJson.put("text", "手动BOC理财明细在发放红包时失败!理财明细id:"+efPaycontrol.get("id"));
					SmsUtil.senErrorMsg(smsJson);
					continue;
				}else{
					logger.warn("手动BOC理财明细在发放红包时成功!理财明细id:"+efPaycontrol.get("id"));
				}
				//准备修改明细和记录(针对利息的记录)
				efPaycontrol.put("clearing_channel","2");
				efPaycontrol.put("onLine","1");
				efPaycontrol.put("Imme", "1");
				JSONObject refJson = bgEfOrderService.cleanInterest(efPaycontrol);
			}
			//本金0元直接算结束
			if(NumberUtil.parseDouble(efPaycontrol.get("surplus_principal"))<=0){
				efPaycontrol.put("tasteOrder","1");
				bgEfOrderService.normalRepayByModel(efPaycontrol);
				continue;
			}
			//改为直接债权成功接口
			JSONObject trans =new JSONObject();
			trans.put("channel", "000002");
			trans.put("accountId",StaticData.risk);
			trans.put("txAmount",NumberUtil.parseDouble(efPaycontrol.get("surplus_principal")));
			if("1".equals(investment_model)){
				trans.put("txFee",NumberUtil.parseDouble(efPaycontrol.get("surplus_management_amt")));
			}else{
				trans.put("txFee",0);
			}
			trans.put("tsfAmount",NumberUtil.parseDouble(efPaycontrol.get("surplus_principal")));
			trans.put("forAccountId",efPaycontrol.get("bank_account"));
			trans.put("orgOrderId",efPaycontrol.get("invest_seri_num"));
			trans.put("orgTxAmount",NumberFormat.formatDouble(Double.parseDouble(efPaycontrol.get("principal").toString())));
			trans.put("productId",efPaycontrol.get("order_prd_number"));
			trans.put("contOrderId",contOrderId);
			trans.put("sysTarget","ERP");
			trans.put("sendAppName",StaticData.appName);
			trans.put("remark","Nzt_:"+efPaycontrol.get("id"));
			JSONObject resultJson = WebServiceUtil.sendPost(StaticData.bocUrl+"payProcess/transfer", new Object[]{trans});
			if("0".equals(resultJson.getString("responseCode"))){
				logger.warn("手动BOC理财明细在进行批量债权转让时失败!"+resultJson.toString());
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", "transfer_error");
				smsJson.put("text", "直投BOC理财明细在进行债权转让时失败!id:"+efPaycontrol.get("id"));
				SmsUtil.senErrorMsg(smsJson);
			}else{
				logger.warn("直投BOC理财明细在进行债权转让时成功!id:"+efPaycontrol.get("id"));
				//开始进行债权转让的记录
				efPaycontrol = bgEfOrderDaoImpl.getCurrentEfBocControls(efPaycontrol.get("id").toString());
				efPaycontrol.put("clearing_channel","2");
				efPaycontrol.put("onLine",efPaycontrol.get("online_type").toString());
				efPaycontrol.put("authcode",resultJson.getString("investAuthCode"));
				efPaycontrol.put("seriNo",resultJson.getString("seriNo"));
				efPaycontrol.put("investment_model",efPaycontrol.get("investment_model").toString());
				bgEfOrderService.normalRepayByModel(efPaycontrol);
				//开始进行可用资金存储
				if("2".equals(efPaycontrol.get("investment_model"))){ //定投
					//非专业投资人才需要处理
					if(BusinessTool.isNotProfessionalInvestor(efPaycontrol.get("investor_type"))){
						efOrderService.lockRowForEforderAuth(efPaycontrol); //处理
					}
					if(BusinessTool.isProfessionalInvestor(efPaycontrol.get("investor_type"))){
						//查询明细
						List<Map> epcList =  bgEfOrderService.findLineEfPaycontrolHasPrincipal(efPaycontrol.get("lineEfOrderId").toString());
						if(ListTool.isNotNullOrEmpty(epcList)){
							Map  epcMap = epcList.get(0);
							EfPaycontrol epc = (EfPaycontrol) bgEfOrderService.findById(EfPaycontrol.class, epcMap.get("id").toString());
							epc.setSurplusPrincipal(NumberFormat.format(ArithUtil.sub(epc.getSurplusPrincipal(), Double.parseDouble(efPaycontrol.get("princiapl").toString()))));
							bgEfOrderService.update(epc);
						}
					}
				}
				
			}
		}
	}
	
	/**
	 * 功能说明：开始处理直投的正常还款
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
	public void toSolveNormalImme(List<Map> immeList,String seriNo){
		if(ListTool.isNullOrEmpty(immeList)){return;}
		//踢出资产包相关数据
		crmOrderService.removePkgCrmOrders(immeList);
		//循环-->发送红包-->购买债券-->生成对应的记录
		List<Map> needTransList = new ArrayList<Map>(); //可以进行债权转让的列表
		//查找买入方 债转签约流水号
		String contOrderId = bgEfOrderService.getBgAutoTransferAuth();
		for(Map efPaycontrol:immeList){
			//开始检测是否已经被处理过了
			if(efPaycontrol.containsKey("id")){
				BgEfPaycontrol bec =  (BgEfPaycontrol) bgEfOrderService.findById(BgEfPaycontrol.class, efPaycontrol.get("id").toString());
				if(bec != null &&  bec.getPayStatus()!=null && bec.getPayStatus()==1 ){ //说明已经被处理过了
					continue;
				}
				if(bec.getOperateType()!=null && "1".equals(bec.getOperateType().toString())){ //说明在操作的时候已经被手动操作过
					continue;
				}
				try{
					bec.setOperateType("1");
					efPaycontrolService.update(bec);
				}catch (Exception e) {
					logger.warn("理财明细在修改自动状态时失败!理财明细id:"+efPaycontrol.get("id").toString());
				}
			}
			//开始发送红包
			JSONObject bocJson = new JSONObject();
			bocJson.put("orderNo", efPaycontrol.get("id"));
			bocJson.put("remark", "");
			bocJson.put("acqRes", "");
			bocJson.put("sendAppName", "erpv3_auto2");
			bocJson.put("forAccountId", efPaycontrol.get("bank_account"));  		//转入账户--> 投资人
			bocJson.put("txAmount",NumberFormat.format(ArithUtil.add(new Double[]{
										NumberUtil.parseDouble(efPaycontrol.get("surplus_interest")),
										NumberUtil.parseDouble(efPaycontrol.get("coupon_interest"))
										})));  		//转入账户--> 投资人
			JSONObject bocResult = WebServiceUtil.sendPost(SysConfig.getInstance().getProperty("pay.center.api")+"payProcess/redPayment", new Object[]{bocJson});
			//如果红包错误
			if("0".equals(bocResult.getString("responseCode"))){
				logger.warn("手动BOC理财明细在发放红包时失败!理财明细id:"+efPaycontrol.get("id")+";错误信息:"+bocResult.getString("data")+";银行返回错误码："+bocResult.getString("responseBankCode"));
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
				smsJson.put("text", "手动BOC理财明细在发放红包时失败!理财明细id:"+efPaycontrol.get("id"));
				SmsUtil.senErrorMsg(smsJson);
			}else{
				logger.warn("手动BOC理财明细在发放红包时成功!理财明细id:"+efPaycontrol.get("id"));
			}
			//准备修改明细和记录(针对利息的记录)
			efPaycontrol.put("clearing_channel","2");
			efPaycontrol.put("onLine","1");
			JSONObject refJson = bgEfOrderService.cleanInterest(efPaycontrol);
			//开始发送邀请人红包
			if(!ChkUtil.isEmpty(efPaycontrol.get("referee_info_id"))){
				Map refereeInfoMap = bgEfOrderService.getRefereeInfoMap(efPaycontrol.get("referee_info_id").toString());
				bocJson.clear();
				//开始发送红包
				bocJson.put("orderNo", efPaycontrol.get("id"));
				bocJson.put("remark", "referee");
				bocJson.put("acqRes", "");
				bocJson.put("sendAppName", "erpv3_auto2");
				bocJson.put("forAccountId", refereeInfoMap.get("bank_account"));  		//转入账户--> 投资人
				bocJson.put("txAmount",NumberFormat.format(refJson.getDouble("refMoney")));  				//转入金额
				bocResult = WebServiceUtil.sendPost(SysConfig.getInstance().getProperty("pay.center.api")+"payProcess/redPayment", new Object[]{bocJson});
				//如果红包错误
				if("0".equals(bocResult.getString("responseCode"))){
					logger.warn("手动BOC理财明细在发放邀请人收益红包时失败!理财明细id:"+efPaycontrol.get("id")+";错误信息:"+bocResult.getString("data")+";银行返回错误码："+bocResult.getString("responseBankCode"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
					smsJson.put("text", "手动BOC理财明细在发放邀请人收益红包时失败!理财明细id:"+efPaycontrol.get("id"));
					SmsUtil.senErrorMsg(smsJson);
				}else{
					//资金记录修改
					logger.warn("手动BOC理财明细在发放邀请人收益红包时成功!理财明细id:"+efPaycontrol.get("id"));
				}
			}
			//本金0元直接算结束
			if(NumberUtil.parseDouble(efPaycontrol.get("surplus_principal"))<=0){
				efPaycontrol.put("tasteOrder","1");
				bgEfOrderService.normalRepayByModel(efPaycontrol);
				continue;
			}
			//改为直接债权成功接口
			JSONObject trans =new JSONObject();
			trans.put("channel", "000002");
			trans.put("accountId",StaticData.risk);
			trans.put("txAmount",NumberUtil.parseDouble(efPaycontrol.get("surplus_principal")));
			trans.put("txFee",NumberUtil.parseDouble(efPaycontrol.get("surplus_management_amt")));
			trans.put("tsfAmount",NumberUtil.parseDouble(efPaycontrol.get("surplus_principal")));
			trans.put("forAccountId",efPaycontrol.get("bank_account"));
			trans.put("orgOrderId",efPaycontrol.get("invest_seri_num"));
			trans.put("orgTxAmount",NumberFormat.formatDouble(Double.parseDouble(efPaycontrol.get("principal").toString())));
			trans.put("productId",efPaycontrol.get("order_prd_number"));
			trans.put("contOrderId",contOrderId);
			trans.put("sysTarget","ERP");
			trans.put("sendAppName",StaticData.appName);
			trans.put("remark","Nzt_:"+efPaycontrol.get("id"));
			JSONObject resultJson = WebServiceUtil.sendPost(StaticData.bocUrl+"payProcess/transfer", new Object[]{trans});
			//如果红包错误
			if("0".equals(resultJson.getString("responseCode"))){
				logger.warn("手动BOC理财明细在进行批量债权转让时失败!id:"+efPaycontrol.get("id")+";错误信息:"+bocResult.getString("data")+";银行返回错误码："+bocResult.getString("responseBankCode"));
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", "transfer_error");
				smsJson.put("text", "直投BOC理财明细在进行债权转让时失败!id:"+efPaycontrol.get("id")+";错误信息:"+bocResult.getString("data")+";银行返回错误码："+bocResult.getString("responseBankCode"));
				SmsUtil.senErrorMsg(smsJson);
			}else{
				logger.warn("直投BOC理财明细在进行债权转让时成功!id:"+efPaycontrol.get("id"));
				//开始进行债权转让的记录
				efPaycontrol = bgEfOrderDaoImpl.getCurrentEfBocControls(efPaycontrol.get("id").toString());
				efPaycontrol.put("clearing_channel","2");
				efPaycontrol.put("onLine",efPaycontrol.get("online_type").toString());
				efPaycontrol.put("authcode",resultJson.getString("investAuthCode"));
				efPaycontrol.put("seriNo",resultJson.getString("seriNo"));
				efPaycontrol.put("investment_model",efPaycontrol.get("investment_model").toString());
				bgEfOrderService.normalRepayByModel(efPaycontrol);
				//开始进行可用资金存储
				if("2".equals(efPaycontrol.get("investment_model"))){ //定投
					//非专业投资人才需要处理
					if(BusinessTool.isNotProfessionalInvestor(efPaycontrol.get("investor_type"))){
						efOrderService.lockRowForEforderAuth(efPaycontrol); //处理
					}
				}
				
			}
		}
	}
	
	/**
	 * 功能说明：执行线上红包户的钱,直接进行债权转让成功,到时候还款的时候使用红包户进行还款
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
	public void toSolveNormalImmeRedAccound(List<Map> redAccountList){
		if(ListTool.isNullOrEmpty(redAccountList)){logger.warn("直投BOC理财红包户还款结束----");return;}
		//踢出资产包相关数据
		crmOrderService.removePkgCrmOrders(redAccountList);
		for(Map efPaycontrol:redAccountList){
			//开始检测是否已经被处理过了
			if(efPaycontrol.containsKey("id")){
				BgEfPaycontrol bec =  (BgEfPaycontrol) bgEfOrderService.findById(BgEfPaycontrol.class, efPaycontrol.get("id").toString());
				if(bec != null &&  bec.getPayStatus()!=null && bec.getPayStatus()==1 ){ //说明已经被处理过了
					continue;
				}
				if(bec.getOperateType()!=null && "1".equals(bec.getOperateType().toString())){ //说明在操作的时候已经被手动操作过
					continue;
				}
				try{
					bec.setOperateType("1");
					efPaycontrolService.update(bec);
				}catch (Exception e) {
					logger.warn("理财明细在修改自动状态时失败!理财明细id:"+efPaycontrol.get("id").toString());
				}
			}
			//本金0元直接算结束
			if( NumberUtil.parseDouble(efPaycontrol.get("surplus_principal"))<=0){
				efPaycontrol.put("tasteOrder","1");
				bgEfOrderService.normalRepayByModel(efPaycontrol);
				continue;
			}
			//红包默认直接成功
			bgEfOrderService.cleanInterest(efPaycontrol);
			//开始进行明细转移和状态修改和记录
			efPaycontrol.put("authcode", efPaycontrol.get("invest_auz_code").toString()+"_needRemove");
			efPaycontrol.put("seriNo",efPaycontrol.get("invest_seri_num"));
			efPaycontrol.put("clearing_channel","2");
			efPaycontrol.put("onLine","1");
			bgEfOrderService.normalRepay(efPaycontrol);
		}
		logger.warn("直投BOC理财红包户还款结束----");
	}
	
	/**
	 * 功能说明：计算利息
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
	private JSONObject getInterest(String eoId){
		JSONObject resultJson = new JSONObject();
		double interest = 0;
		double interestReal = 0;
		double coupon = 0;
		resultJson.put("interest", 0);
		resultJson.put("capital", 0);
		resultJson.put("prePenalty", 0);
		resultJson.put("manage", 0);
		resultJson.put("invite", 0);
		resultJson.put("score",0);
		resultJson.put("inviteId","");
		resultJson.put("interestReal",0);
		resultJson.put("coupon",0);
		resultJson.put("principal",0);
		resultJson.put("has","no");
		String checkSql ="SELECT id from bg_ef_payrecord where ef_order_id='"+eoId+"' and ef_paycontrol_id='-1' ";
		List<Map> list= crmOrderDaoImpl.queryBySqlReturnMapList(checkSql);
		if(ListTool.isNotNullOrEmpty(list)){
			resultJson.put("has","yes");
		}
		//计算剩余总本金
		String allCapital  = 
							"SELECT "+
							"ifnull(sum(surplus_principal),0) capital "+
							"FROM "+
							"bg_ef_paycontrol "+
							"WHERE "+
							"ef_order_id='"+eoId+"' AND "+
							"live_status=1 AND "+
							"(operate_type is null or operate_type='' or operate_type='1') AND "+
							"pay_status =0 ";
		resultJson.put("capital", crmOrderDaoImpl.queryBySqlReturnMapList(allCapital).get(0).get("capital"));
		resultJson.put("principal",resultJson.getDouble("capital"));
		String sql = 
					"SELECT "+
					"* "+
					"FROM "+
					"bg_ef_paycontrol "+
					"WHERE "+
					"ef_order_id='"+eoId+"' AND "+
					"live_status=1 AND "+
					"pay_time >='"+DateUtil.getCurrentTime()+"' ORDER BY pay_time asc limit 1 ";
		List<Map> mapList = crmOrderDaoImpl.queryBySqlToLower(sql);
		if(ListTool.isNullOrEmpty(mapList)){
			return resultJson;
		}
		//开始判断状态
		if("1".equals(mapList.get(0).get("pay_status").toString())){
			return resultJson;
		}
		if("0".equals(mapList.get(0).get("pay_status").toString())){
			//开始进入判断
			long days = 0;
			long day = 0;
			String preSql = 
							"SELECT "+
							"* "+
							"FROM "+
							"bg_ef_paycontrol "+
							"WHERE "+
							"ef_order_id='"+eoId+"' AND "+
							"live_status=1 AND "+
							"pay_time <'"+DateUtil.getCurrentTime()+"' ORDER BY pay_time desc limit 1 ";
			List<Map> preList = crmOrderDaoImpl.queryBySqlToLower(preSql);
			if(ListTool.isNotNullOrEmpty(preList)){
				day = DateUtil.getBetweenDays(DateUtil.getCurrentTime(), preList.get(0).get("pay_time").toString());
				days = DateUtil.getBetweenDays(preList.get(0).get("pay_time").toString(), mapList.get(0).get("pay_time").toString());
			}else{
				String btimeSql = "SELECT effect_time from bg_ef_orders where id='"+eoId+"' ";
				List<Map> btimeList = crmOrderDaoImpl.queryBySqlToLower(btimeSql);
				String effectTime = btimeList.get(0).get("effect_time").toString();
				//如果是消费金融的话,需要查询订单通过合规检查的时间
				String crmApplyIdSql = 
									"SELECT "+
									"	ifnull(co.accr_time,'') accrTime "+
									"FROM "+
									"	bg_ef_orders beo, "+
									"	crm_order co "+
									"WHERE "+
									"	beo.crm_order_id = co.id "+
									"AND beo.id = '"+eoId+"' ";
					String accr_time = crmOrderDaoImpl.queryBySqlReturnMapList(crmApplyIdSql).get(0).get("accrTime").toString();
					if(accr_time !=null && !"".equals(accr_time)){
						effectTime = accr_time;
					}
					day = DateUtil.getBetweenDays(DateUtil.getCurrentTime(), effectTime);
					days = DateUtil.getBetweenDays(effectTime, mapList.get(0).get("pay_time").toString());
				}
			//开始计算利息
			double r = day*1.0/days*1.0;
			if(r>1){
				r =1;
			}
			double coupon_interest = ChkUtil.isEmpty(mapList.get(0).get("coupon_interest")) ? 0d:Double.parseDouble(mapList.get(0).get("coupon_interest").toString());
			interest  =ArithUtil.mul(r, Double.parseDouble(mapList.get(0).get("surplus_interest").toString())+coupon_interest);
			interestReal  =ArithUtil.mul(r, Double.parseDouble(mapList.get(0).get("surplus_interest").toString()));
			coupon  =ArithUtil.mul(r,coupon_interest);
			resultJson.put("manage", mapList.get(0).get("surplus_management_amt"));
			if(mapList.get(0).get("referee_income_scale") !=null && Double.parseDouble(mapList.get(0).get("referee_income_scale").toString())>0){ //邀请人利息
				resultJson.put("invite", ArithUtil.mul(interest, Double.parseDouble(mapList.get(0).get("referee_income_scale").toString())/100d));
				String inSql ="SELECT bank_account,fy_account,cust_name from bg_cust_info where id='"+mapList.get(0).get("referee_info_id")+"' ";
				resultJson.put("inviteId", mapList.get(0).get("referee_info_id"));
				resultJson.put("invite_bank_account", crmOrderDaoImpl.queryBySqlReturnMapList(inSql).get(0).get("bank_account"));
				resultJson.put("invite_fy_account", crmOrderDaoImpl.queryBySqlReturnMapList(inSql).get(0).get("fy_account"));
				resultJson.put("invite_cust_name", crmOrderDaoImpl.queryBySqlReturnMapList(inSql).get(0).get("cust_name"));
				
			}
			if(mapList.get(0).get("score_scale") !=null && Double.parseDouble(mapList.get(0).get("score_scale").toString())>0){ //邀请人利息
				resultJson.put("score", Math.round(ArithUtil.mul(interest, Double.parseDouble(mapList.get(0).get("score_scale").toString())/100d)));
			}
			
			resultJson.put("interest", interest);
			resultJson.put("interestReal", interestReal);
			resultJson.put("coupon", coupon);
			return resultJson;
		}
		
		return resultJson;
	}
	
	/**
	 * 功能说明：提前结清
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
	public void cleanInterestAdvanceClear(JSONObject clearJson) throws Exception{
			String sql = "SELECT id from bg_ef_paycontrol where ef_order_id='"+clearJson.getString("efOrderId")+"' and live_status=1 and pay_status=0 ";
			List<Map> epcsList = crmOrderDaoImpl.queryBySqlToLower(sql);
			if(ListTool.isNotNullOrEmpty(epcsList)){
				for(Map epcsMap:epcsList){
					BgEfPaycontrol bec = (BgEfPaycontrol) crmOrderDaoImpl.findById(BgEfPaycontrol.class,epcsMap.get("id").toString());
					bec.setSurplusInterest(0d);
					bec.setCouponInterest(0d);
					bec.setOperateType("1");
					crmOrderDaoImpl.update(bec);
				}
			}
			//只有在利息大于零的情况下才进行记录,否则不进行记录
			if(clearJson.getDouble("interest") >0){
				//开始添加记录
				BgEfPayrecord epr = new BgEfPayrecord();
				epr.setCreateTime(DateUtil.getCurrentTime());										//创建时间
				epr.setCustId(clearJson.get("cust_id").toString()); 								//bg_customer的id
				epr.setCustInfoId(clearJson.get("cust_info_id").toString());						//bg_cust_info的id
				epr.setEfOrderId(clearJson.get("efOrderId").toString());							//理财订单id
				epr.setEfPaycontrolId("-1");						    							//理财明细
				epr.setEvidenceUrl("");																//凭证url
				epr.setInteRest(clearJson.getDouble("interestReal"));								//利息
				epr.setManagementAmt(0d);															//管理费
				epr.setType(1);																		//类型 1线上 0 线下
				if(clearJson.containsKey("empId")){
					epr.setOperator(clearJson.getString("empId")) ;										//操作人
				}
				epr.setOverPenalty(0d);																//逾期金额	
				epr.setPeriods(-1);																	//期数
				epr.setPrePaymentPenalty(0d);														//提前结清金额
				epr.setPrincipal(0d);																//本金(因为本次只是对利息进行记录所以本金为0)
				epr.setTotalAmt(ArithUtil.add(new Double[]{
						clearJson.getDouble("interestReal"),
						clearJson.getDouble("coupon"),
						clearJson.getDouble("prePenalty")
						
				}));
				epr.setPrePaymentPenalty(clearJson.getDouble("prePenalty"));
				epr.setUpdateTime(DateUtil.getCurrentTime());										//更新时间
				epr.setCouponAmount(clearJson.getDouble("coupon"));
				crmOrderDaoImpl.add(epr);
					try{
						//开发准备进行修改推荐记录表
						if(clearJson.getDouble("invite") >0){  //如果推荐人的不是空的话
							BgReferralIncomeRecord bri  = new BgReferralIncomeRecord();
							bri.setCreateTime(DateUtil.getCurrentTime());
							bri.setCustInfoId(clearJson.getString("cust_info_id"));
							bri.setIncomeSource("投资还款");
							bri.setRefereeInfoId(clearJson.getString("inviteId"));
							bri.setReferralIncome(clearJson.getDouble("invite"));
							bri.setUpdateTime(DateUtil.getCurrentTime());
							//如果金额>0才进行存储
							if(NumberFormat.format(bri.getReferralIncome())>0){
								crmOrderDaoImpl.add(bri);
							}
						}
						//客户积分获取记录以及该用户的积分更新问题
						BgScoreRecord bsr = new  BgScoreRecord();
						bsr.setEfOrderId(clearJson.getString("efOrderId"));
						bsr.setCreateTime(DateUtil.getCurrentTime());
						bsr.setCustId(clearJson.getString("cust_id"));
						bsr.setIncomeSource("投资还款");
						bsr.setScore(clearJson.getString("score"));
						bsr.setUpdateTime(DateUtil.getCurrentTime());
						//修改积分
						//---------------------------------------------------------------------------------------
						BgCustomer bc  = (BgCustomer) crmOrderDaoImpl.findById(BgCustomer.class, clearJson.getString("cust_id"));
						String oldScore="0";
						if(ChkUtil.isNotEmpty(bc.getScore())){
							oldScore = bc.getScore();
						}
						String currentScore  = String.valueOf(Long.parseLong(oldScore)+Long.parseLong(bsr.getScore()));
						bc.setScore(currentScore);
						crmOrderDaoImpl.update(bc);
						//---------------------------------------------------------------------------------------
						//如果积分>0才进行存储
						if(NumberUtil.parseDouble(bsr.getScore())>0){
							bsr.setRealTimeScore(bc.getScore());
							crmOrderDaoImpl.add(bsr);
						}
					}catch (Exception e) {
						e.printStackTrace();
					}
			}
	}
	
	/**
	 * 功能说明：数据复制,结清明细,产生记录,结清订单
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
	public void advanceClearNormalOnLine(JSONObject clearJson) throws Exception{
		//1.数据拷贝
		//2.结清明细
		//4.结清订单
		//
		if(!clearJson.containsKey("poc")){
			zZLEfPaycontrolDaoImpl.copyAdvanceClear(clearJson);
		}
		//
		String sql = "SELECT id from bg_ef_paycontrol where ef_order_id='"+clearJson.getString("efOrderId")+"' and live_status=1 and pay_status=0 and (operate_type is null or operate_type='' or operate_type='1') ";
		List<Map> mapList = crmOrderDaoImpl.queryBySqlToLower(sql);
		if(ListTool.isNotNullOrEmpty(mapList)){
			for(Map forMap:mapList){
				BgEfPaycontrol epc;
					epc = (BgEfPaycontrol) crmOrderDaoImpl.findById(BgEfPaycontrol.class, forMap.get("id").toString());
					epc.setPayStatus(1);
					epc.setSurplusPrincipal(0d);
					epc.setSurplusInterest(0d);
					epc.setSurplusManagementAmt(0d);
					epc.setOperateType("1");
					crmOrderDaoImpl.update(epc);
			}
		}
		//记录
		String recordsql ="SELECT id from bg_ef_payrecord where ef_paycontrol_id='-1' and ef_order_id='"+clearJson.getString("efOrderId")+"' ";
		List<Map> list = crmOrderDaoImpl.queryBySqlReturnMapList(recordsql);
		if(ListTool.isNotNullOrEmpty(list)){
			BgEfPayrecord epr = (BgEfPayrecord) crmOrderDaoImpl.findById(BgEfPayrecord.class,crmOrderDaoImpl.queryBySqlReturnMapList(recordsql).get(0).get("id").toString());
			epr.setPrincipal(clearJson.getDouble("principal"));
			epr.setManagementAmt(clearJson.getDouble("manage"));
			epr.setTotalAmt(ArithUtil.sub(ArithUtil.add(new Double[]{
					NumberFormat.format(epr.getPrincipal()),
					NumberFormat.format(epr.getInteRest()),
					NumberFormat.format(epr.getCouponAmount()),
					NumberFormat.format(epr.getPrePaymentPenalty())
			}),NumberFormat.format(epr.getManagementAmt())));
			//重新格式化
			epr.setPrincipal(NumberFormat.format(epr.getPrincipal()));
			epr.setInteRest(NumberFormat.format(epr.getInteRest()));
			epr.setCouponAmount(NumberFormat.format(epr.getCouponAmount()));
			epr.setPrePaymentPenalty(NumberFormat.format(epr.getPrePaymentPenalty()));
			epr.setManagementAmt(NumberFormat.format(epr.getManagementAmt()));
			crmOrderDaoImpl.update(epr);
		}
		if(ListTool.isNullOrEmpty(list)){
			BgEfPayrecord epr = new BgEfPayrecord();
			epr.setCouponAmount(clearJson.getDouble("coupon"));
			epr.setCreateTime(DateUtil.getCurrentTime());
			epr.setCustId(clearJson.getString("cust_id"));
			epr.setCustInfoId(clearJson.getString("cust_info_id"));
			epr.setEfOrderId(clearJson.getString("efOrderId"));
			epr.setEfPaycontrolId("-1");
			epr.setEvidenceUrl("");
			epr.setInteRest(clearJson.getDouble("interestReal"));
			epr.setManagementAmt(clearJson.getDouble("manage"));
			epr.setOperator(clearJson.getString("empId"));
			epr.setOverPenalty(0d);
			epr.setPeriods(-1);
			epr.setPrePaymentPenalty(clearJson.getDouble("prePenalty"));
			epr.setPrincipal(clearJson.getDouble("capital"));
			epr.setTotalAmt(ArithUtil.sub(ArithUtil.add(new Double[]{
					NumberFormat.format(epr.getPrincipal()),
					NumberFormat.format(epr.getInteRest()),
					NumberFormat.format(epr.getCouponAmount()),
					NumberFormat.format(epr.getPrePaymentPenalty())
			}),NumberFormat.format(epr.getManagementAmt())));
			epr.setPrincipal(NumberFormat.format(epr.getPrincipal()));
			epr.setInteRest(NumberFormat.format(epr.getInteRest()));
			epr.setCouponAmount(NumberFormat.format(epr.getCouponAmount()));
			epr.setPrePaymentPenalty(NumberFormat.format(epr.getPrePaymentPenalty()));
			epr.setManagementAmt(NumberFormat.format(epr.getManagementAmt()));
			epr.setType(1);
			epr.setUpdateTime(DateUtil.getCurrentTime());
			if(clearJson.containsKey("empId")){
				epr.setOperator(clearJson.getString("empId"));
			}
			crmOrderDaoImpl.add(epr);
		}
		//结清订单
		BgEfOrders eo = (BgEfOrders) crmOrderDaoImpl.findById(BgEfOrders.class, clearJson.getString("efOrderId"));
		eo.setPayStatus(2); //结清
		eo.setClearType(1);//提前结清
		crmOrderDaoImpl.update(eo);
		//开始发送提前结清的站内信
		try{
			BgCustomer  customer = (BgCustomer) crmOrderDaoImpl.findById(BgCustomer.class, eo.getCustId());
			CrmOrder co = (CrmOrder) crmOrderDaoImpl.findById(CrmOrder.class, eo.getCrmOrderId());
			JSONObject msgJson = new JSONObject();
			msgJson.put("custId",eo.getCustId());
			msgJson.put("custInfoId",eo.getCustInfoId());
			msgJson.put("amount",eo.getEfFectiveAmt());
			msgJson.put("interest", clearJson.getDouble("interestReal"));
			msgJson.put("prePaymentPenality", clearJson.getDouble("prePenalty"));
			msgJson.put("principal", clearJson.getDouble("capital"));
			msgJson.put("couponAmount", clearJson.getDouble("coupon"));
			msgJson.put("rateCoupon", 0d);
			msgJson.put("managementAmt", clearJson.getDouble("manage"));
			msgJson.put("orderNumber",eo.getOrderNumber());
			msgJson.put("type", 2);
			msgJson.put("efOrderId", eo.getId());
			msgJson.put("username", customer.getUsername());
			
			String online_type = co.getOnlineType()==null ?"0":co.getOnlineType().toString();
			if(!"0".equals(online_type)){ //只要不是线下的就需要发送这个站内信
				String path ="message/addSysMessage"; 
				//根据不同的平台发送不同的地址
				if("1".equals(online_type)){ //贝格
					path=StaticData.v3BelPostPath+path;
				}
				if("2".equals(online_type)){ //中资联财务
					path=StaticData.v3ZzlcfPostPath+path;
				}
				logger.warn("提前结清发送站内地址:"+path);
				logger.warn("提前结清发送站内信参数:"+msgJson.toString());
				HttpUtil.connectByUrl(path,msgJson,false);
			}
			
		}catch (Exception e) {
			logger.warn("提前结清发送站内信异常!");
			logger.warn(e.getMessage(),e);
		}
	}
	
	/**
	 * 功能说明：冻结所有的金额
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
	public boolean frozen(List<Map> zzlEfpaycontrols,JSONObject jieJson){
		boolean flag = false;
		//开始计算差值
		try {
			//修改zzl
			double totalInterest =jieJson.getDouble("accrual");// 剩余总利息
			double totalManage = jieJson.getDouble("manage");// 剩余管理费
			double totalOverCost = jieJson.getDouble("interest");// 剩余罚息
			double totalOverPenalty = jieJson.getDouble("overdueViolate");// 剩余逾期违约金
			double totalCapital = jieJson.getDouble("capital"); //本次还款本金
			double CapitalMoney  = jieJson.getDouble("capitalMoney");
			
			double temptotalInterest = totalInterest;
			double temptotalManage = totalManage;
			double temptotalOverCost = totalOverCost;
			double temptotalOverPenalty = totalOverPenalty;
			double temptotalCapital = totalCapital;
			
			boolean manageStop = false;
			boolean interestStop = false;
			boolean overCostStop = false;
			boolean overPenaltyStop = false;
			boolean capitalStop = false;
			for(int i=0;i<zzlEfpaycontrols.size();i++){
				Map zzlEfpaycontrol = zzlEfpaycontrols.get(i);
				ZZLEfPaycontrol zzl = (ZZLEfPaycontrol)bgEfOrderDaoImpl.findById(ZZLEfPaycontrol.class,zzlEfpaycontrol.get("id").toString());
				//准备计算管理费利息等
				double manage = 0d;
				if(totalManage>0){
					manage = NumberFormat.format(ArithUtil.div(temptotalManage*zzl.getPrinciapl(), CapitalMoney));
				 }
				double interest  = 0d;
				if(totalInterest>0){
					interest  = NumberFormat.format(ArithUtil.div(temptotalInterest*zzl.getPrinciapl(),CapitalMoney));
				}
				double overCost =0d;
				if(totalOverCost>0){
					overCost = NumberFormat.format(ArithUtil.div(temptotalOverCost*zzl.getPrinciapl(),CapitalMoney));
					
				}
				double overPenalty =0d;
				if(totalOverPenalty>0){
					overPenalty = NumberFormat.format(ArithUtil.div(temptotalOverPenalty*zzl.getPrinciapl(),CapitalMoney));
				}
				double capital = 0;
				if(totalCapital>0){
					capital = NumberFormat.format(ArithUtil.div(temptotalCapital*zzl.getPrinciapl(),CapitalMoney));
				}
				
				zzl.setMoreInterest(0d);  //多出来的利息 直接赋值为0
				zzl.setMoreManageAmt(0d); //多出来的管理费直接赋值为0
				
				totalManage  = ArithUtil.sub(totalManage, manage);
				totalInterest  = ArithUtil.sub(totalInterest, interest);
				totalOverCost  = ArithUtil.sub(totalOverCost, overCost);
				totalOverPenalty  = ArithUtil.sub(totalOverPenalty, overPenalty);
				totalCapital  = ArithUtil.sub(totalCapital, capital);
				zzl.setSurplusManagementAmt(manage); //管理费
				zzl.setSurplusInterest(interest);//利息
				zzl.setOverCost(overCost);
				zzl.setOverPenalty(overPenalty);
				zzl.setTempPrincipal(capital);
				// 是否继续本金的判断
				if(totalCapital>0){
					zzl.setTempPrincipal(capital); // 临时本金
				}
				if(totalCapital<0){
					capital = ArithUtil.add(capital,totalCapital);
					zzl.setTempPrincipal(capital); //临时本金
				}
				if(capitalStop){
					zzl.setTempPrincipal(0d); //临时本金
				}
				if(i == (zzlEfpaycontrols.size()-1)){
					if(totalCapital>0){
						zzl.setTempPrincipal(ArithUtil.add(capital,totalCapital)); //临时本金
					}
				}
				if(totalCapital<=0){
					capitalStop  = true;
				}
				// 是否继续管理费的判断
				if(totalManage>0){
					zzl.setSurplusManagementAmt(manage); //管理费
				}
				if(totalManage<0){
					manage = ArithUtil.add(manage,totalManage);
					zzl.setSurplusManagementAmt(manage); //管理费
				}
				if(manageStop){
					zzl.setSurplusManagementAmt(0d); //管理费
				}
				if(i == (zzlEfpaycontrols.size()-1)){
					if(totalManage>0){
						zzl.setSurplusManagementAmt(ArithUtil.add(manage,totalManage)); //管理费
					}
				}
				if(totalManage<=0){
					manageStop  = true;
				}
				// 是否继续利息
				if(totalInterest>0){
					zzl.setSurplusInterest(interest); //利息
				}
				if(totalInterest<0){
					interest = ArithUtil.add(interest,totalInterest);
					zzl.setSurplusInterest(interest); //利息
				}
				if(i == (zzlEfpaycontrols.size()-1)){
					if(totalInterest>0){
						zzl.setSurplusInterest(ArithUtil.add(interest,totalInterest)); //管理费
					}
				}
				if(interestStop){
					zzl.setSurplusInterest(0d); //利息
				}
				if(totalInterest<=0){
					interestStop  = true;
				}
				// 是否继续罚息的判断
				if(totalOverCost>0){
					zzl.setOverCost(overCost); //罚息
				}
				if(totalOverCost<0){
					overCost = ArithUtil.add(overCost,totalOverCost);
					zzl.setOverCost(overCost); //罚息
				}
				if(i == (zzlEfpaycontrols.size()-1)){
					if(totalOverCost>0){
						zzl.setOverCost(ArithUtil.add(overCost,totalOverCost)); //管理费
					}
				}
				if(overCostStop){
					zzl.setOverCost(0d); //罚息
				}
				if(totalOverCost<=0){
					overCostStop  = true;
				}
				// 是否继续违约金
				if(totalOverPenalty>0){
					zzl.setOverPenalty(overPenalty); //违约金
				}
				if(totalOverPenalty<0){
					overPenalty = ArithUtil.add(overPenalty,totalOverPenalty);
					zzl.setOverPenalty(overPenalty); //违约金
				}
				if(i == (zzlEfpaycontrols.size()-1)){
					if(totalOverPenalty>0){
						zzl.setOverPenalty(ArithUtil.add(overPenalty,totalOverPenalty)); //管理费
					}
				}
				if(overPenaltyStop){
					zzl.setOverPenalty(0d); //违约金
				}
				if(totalOverPenalty<=0){
					overPenaltyStop  = true;
				}
				try{
					//该方法是为了判断是否可以结束掉zzl
					String sql = "select * from crm_paycontrol where crm_order_id='"+jieJson.getString("crmOrderId")+"' and repayment_time like '"+jieJson.getString("payTime")+"%' and status =1";
					List<Map> currOverList  = bgEfOrderDaoImpl.queryBySqlReturnMapList(sql);
					String currentSql2 = "SELECT * from bg_ef_dete_record where crm_paycontrol_id='"+currOverList.get(0).get("id")+"' and status=0  ";
					List<Map> currOverList2  = bgEfOrderDaoImpl.queryBySqlReturnMapList(currentSql2);
					if(ListTool.isNotNullOrEmpty(currOverList) && currOverList2.size()<2){ //本次明细结清
						zzl.setPayStatus(-1); //代表结束了
					}
				}catch (Exception e) {
					logger.warn(e.getMessage(),e);
				}
				if(zzl.getTempPrincipal()>zzl.getPrinciapl()){
					double overPrinciapl = ArithUtil.sub(zzl.getTempPrincipal(), zzl.getPrinciapl());
					zzl.setTempPrincipal(zzl.getPrinciapl());
					zzl.setMoreManageAmt(ArithUtil.add(zzl.getMoreManageAmt(),overPrinciapl));
				}
				bgEfOrderDaoImpl.update(zzl);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("修改债权明细时失败!信贷订单id:"+zzlEfpaycontrols.get(0).get("crm_order_id").toString());
		}
		return flag;
	}
}

