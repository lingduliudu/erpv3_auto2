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
import com.apt.webapp.model.crm.CrmPaycontrol;
import com.apt.webapp.model.ef.ZZLEfPaycontrol;
import com.apt.webapp.service.bg.ef.IBgEfOrderService;
import com.apt.webapp.service.crm.ICrmOrderService;
import com.apt.webapp.service.crm.IPsCheckMoneyService;
import com.apt.webapp.service.ef.IEfFundRecordService;
import com.apt.webapp.service.ef.IEfOrderService;
import com.apt.webapp.service.ef.IEfPaycontrolService;
import com.apt.webapp.service.ef.IEfPayrecordService;
import com.apt.webapp.service.ef.IZZLEfPaycontrolService;

/**
 * 功能说明：v3的自动执行方法
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
public class AutoRun {
	//日志
	private Logger logger = LoggerFactory.getLogger(AutoRun.class);
	//导入线上理财订单
	@Resource
	private IBgEfOrderService bgEfOrderService;
	@Resource
	private IEfPaycontrolService efPaycontrolService;
	@Resource
	private IEfPayrecordService efPayrecordService;
	@Resource
	private ICrmOrderService crmOrderService;
	@Resource
	private IEfOrderService efOrderService;
	@Resource
	private IEfFundRecordService efFundRecordService;
	@Resource
	private IZZLEfPaycontrolService zzlEfPaycontrolService;
	@Resource
	private transToZzl transToZzl;
	@Resource
	private IPsCheckMoneyService psCheckMoneyService;
	@Resource
	private ICustomerDao customerDao;
	@Resource
	private IBgEfOrderDao bgEfOrderDao;
	/**
	 * 功能说明：执行直投BOC自动还款操作
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
	public void toExecuteImmeEfAutopayBoc(){
		logger.warn("直投Boc理财人自动还款开始----");
		//查找今天需要还款的理财明细并通过债券转让的方式还款
		//1.查找今日待还
		//2.发送红包
		//3.购买债券
		//4.更新操作  >> 借款人(订单明细记录) >> 投资人(订单明细记录) >>ZZL (订单明细记录)
		List<Map> efPaycontrols =  bgEfOrderService.getCurrentImmeControlsBoc();
		//如果无数据则结束
		if(ListTool.isNullOrEmpty(efPaycontrols)){logger.warn("直投Boc理财人自动还款结束----");return;}
		//循环-->发送红包-->购买债券-->生成对应的记录
		for(Map efPaycontrol:efPaycontrols){
			//开始发送红包
			JSONObject bocJson = new JSONObject();
			bocJson.put("cardNbr", StaticData.redEnvelope);  					//转出账户--> 红包账户
			bocJson.put("cardNbrIn", efPaycontrol.get("bank_account"));  		//转入账户--> 投资人
			bocJson.put("amount",ArithUtil.add(new Double[]{
										NumberUtil.parseDouble(efPaycontrol.get("surplus_interest")),
										NumberUtil.parseDouble(efPaycontrol.get("coupon_interest"))
										}));  		//转入账户--> 投资人
			JSONObject bocResult = null;// WebServiceUtil.invokeBoc("bankService", "getBank7603", bocJson);
			//如果红包错误
			if("0".equals(bocResult.getString("responseCode"))){
				logger.warn("直投Boc理财明细在发放红包时失败!理财明细id:"+efPaycontrol.get("id"));
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
				smsJson.put("text", "直投Boc理财明细在发放红包时失败!理财明细id:"+efPaycontrol.get("id"));
				SmsUtil.senErrorMsg(smsJson);
				continue;
			}
			//
			logger.warn("直投Boc理财明细在发放红包时成功!理财明细id:"+efPaycontrol.get("id"));
			//准备修改明细和记录(针对利息的记录)
			efPaycontrol.put("clearing_channel","2");
			JSONObject refJson = bgEfOrderService.cleanInterest(efPaycontrol);
			//开始发送邀请人红包
			if(!ChkUtil.isEmpty(efPaycontrol.get("referee_info_id"))){
				Map refereeInfoMap = bgEfOrderService.getRefereeInfoMap(efPaycontrol.get("referee_info_id").toString());
				bocJson.clear();
				bocJson.put("cardNbr", StaticData.redEnvelope);  					//转出账户--> 红包账户
				bocJson.put("cardNbrIn", refereeInfoMap.get("bank_account"));  		//转入账户--> 投资人
				bocJson.put("amount",refJson.getDouble("refMoney"));  		//转入账户--> 投资人
				bocResult.clear();
				bocResult = null;// WebServiceUtil.invokeBoc("bankService", "getBank7603", bocJson);
				//如果红包错误
				if("0".equals(bocResult.getString("responseCode"))){
					logger.warn("直投Boc理财明细在发放邀请人收益红包时失败!理财明细id:"+efPaycontrol.get("id"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
					smsJson.put("text", "直投Boc理财明细在发放邀请人收益红包时失败!理财明细id:"+efPaycontrol.get("id"));
					SmsUtil.senErrorMsg(smsJson);
				}else{
				logger.warn("直投Boc理财明细在发放邀请人收益红包时成功!理财明细id:"+efPaycontrol.get("id"));
				}
			}
			//开始购买债权
			bocJson.clear();
			bocJson.put("cardNbr", StaticData.risk);												//承接方-->风险户
			bocJson.put("authCode",efPaycontrol.get("invest_auz_code"));							//投标返回的授权码
			bocJson.put("cardNbro",efPaycontrol.get("bank_account"));								//转让方电子账号
			//总共可转让金额  = 本金
			Map efOrder = bgEfOrderService.getBgEfOrdersById(efPaycontrol.get("ef_order_id").toString());
			double tbalAce = Double.parseDouble(efOrder.get("principal").toString());
			bocJson.put("tbalAce",NumberFormat.formatDouble(tbalAce));								//总共可转让金额
			bocJson.put("amount", NumberUtil.parseDouble(efPaycontrol.get("surplus_principal"))); 	//转让金额
			bocJson.put("trpRice", NumberUtil.parseDouble(efPaycontrol.get("surplus_principal"))); 	//转让价格
			bocJson.put("intDate", DateUtil.formatDate(efPaycontrol.get("pay_time").toString(), DateUtil.STYLE_3)); //起息日 YYYYMMDD
			bocJson.put("yield", efPaycontrol.get("INVEST_RATE"));									//转让后预期年化收益率
			bocJson.put("feeWay", "0");																//手续费扣款方式 （0指定金额 1 同产品设置）
			bocJson.put("fee", efPaycontrol.get("surplus_management_amt"));							//转让手续费(管理费)（feeWay为0时生效）
			//准备链接BOC
			bocResult.clear();
			bocResult = null;//WebServiceUtil.invokeBoc("bankService", "getBank5817", bocJson);
			if("0".equals(bocResult.getString("responseCode"))){
				logger.warn("直投Boc理财明细在债权转让时失败!理财明细id:"+efPaycontrol.get("id"));
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
				smsJson.put("text", "直投Boc理财明细在债权转让时失败!理财明细id:"+efPaycontrol.get("id"));
				SmsUtil.senErrorMsg(smsJson);
				continue;
			}
			logger.warn("直投Boc理财明细在债权转让时成功!理财明细id:"+efPaycontrol.get("id"));
			//开始进行明细转移和状态修改和记录
			efPaycontrol.put("AUTHCODE", bocResult.get("AUTHCODE"));
			efPaycontrol.put("clearing_channel","2");
			efPaycontrol.put("onLine","1");
			bgEfOrderService.normalRepay(efPaycontrol);
		}
		logger.warn("直投Boc理财人自动还款结束----");
	}
	/**
	 * 功能说明：执行直投BOC自动还款操作
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
	public void toExecuteImmeEfAutopayPoc(){
		logger.warn("直投Poc理财自动还款开始----");
		//查找今天需要还款的理财明细并通过债券转让的方式还款
		//1.查找今日待还
		//4.更新操作  >> 借款人(订单明细记录) >> 投资人(订单明细记录) >>ZZL (订单明细记录)
		List<Map> efPaycontrols =  bgEfOrderService.getCurrentImmeControlsBoc();
		//如果无数据则结束
		if(ListTool.isNullOrEmpty(efPaycontrols)){logger.warn("直投Poc理财自动还款结束----");return;}
		for(Map efPaycontrol:efPaycontrols){
			Map peerTopeer = new HashMap();
			//计算金额
			double amt = ArithUtil.add(new Double[]{NumberUtil.parseDouble(efPaycontrol.get("surplus_principal")),NumberUtil.parseDouble(efPaycontrol.get("surplus_interest"))});
			amt = ArithUtil.sub(amt,NumberUtil.parseDouble(efPaycontrol.get("surplus_management_amt")));
			peerTopeer.put("out_cust_no", StaticData.creditAccount);
			peerTopeer.put("int_cust_no", efPaycontrol.get("fy_account"));
			peerTopeer.put("amt",amt);
			JSONObject pocResult = pocTool.peerTopeer(peerTopeer);
			if("0".equals(pocResult.getString("responseCode"))){
				logger.warn("直投Poc投资人本金还款失败!理财明细:id="+efPaycontrol.get("id"));
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
				smsJson.put("text", "直投Poc投资人本金还款失败!理财明细:id="+efPaycontrol.get("id"));
				SmsUtil.senErrorMsg(smsJson);
				continue;
			}
			//如果成功需要记录并修改明细状态
			efPaycontrol.put("POC","1");
			JSONObject refJson = bgEfOrderService.cleanInterest(efPaycontrol);
			if(!ChkUtil.isEmpty(efPaycontrol.get("referee_info_id"))){
				try{
				Map refereeInfoMap = bgEfOrderService.getRefereeInfoMap(efPaycontrol.get("referee_info_id").toString());
				peerTopeer = new HashMap();
				//计算金额
				peerTopeer.put("out_cust_no", StaticData.creditAccount);
				peerTopeer.put("int_cust_no", refereeInfoMap.get("fy_account"));
				peerTopeer.put("amt",refJson.getDouble("refMoney"));
				pocResult = pocTool.peerTopeer(peerTopeer);
				if("0".equals(pocResult.getString("responseCode"))){
					logger.warn("直投Poc投资人邀请人还款失败!理财明细:id="+efPaycontrol.get("id"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
					smsJson.put("text", "直投Poc投资人邀请人本金还款失败!理财明细:id="+efPaycontrol.get("id"));
					SmsUtil.senErrorMsg(smsJson);
				}
				}catch (Exception e) {
					logger.warn(e.getMessage(),e);
				}
			}
			logger.warn("投资人本金还款成功!理财明细:id="+efPaycontrol.get("id"));
		}
		logger.warn("直投Poc理财自动还款结束----");
	}
	/**
	 * 功能说明：执行线上自动还款操作新手标
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
	public void toExecuteEfAutopayTasteOrder(){
		logger.warn("新手标自动还款开始----");
		//查找今天需要还款的理财明细并通过债券转让的方式还款
		//1.查找今日待还
		//2.发送红包
		//3.购买债券
		//4.更新操作  >> 借款人(订单明细记录) >> 投资人(订单明细记录) >>ZZL (订单明细记录)
		List<Map> efPaycontrols =  bgEfOrderService.getCurrentOnLineTasteOrderControls();
		//如果无数据则结束
		if(ListTool.isNullOrEmpty(efPaycontrols)){logger.warn("新手标自动还款结束----");return;}
		//循环-->发送红包-->购买债券-->生成对应的记录
		for(Map efPaycontrol:efPaycontrols){
			//开始发送红包
			JSONObject bocJson = new JSONObject();
			//bocJson.put("cardNbr", StaticData.redEnvelope);  					//转出账户--> 红包账户
			bocJson.put("forAccountId", efPaycontrol.get("bank_account"));  		//转入账户--> 投资人
			bocJson.put("txAmount",ArithUtil.add(new Double[]{
					NumberUtil.parseDouble(efPaycontrol.get("surplus_interest")),
					NumberUtil.parseDouble(efPaycontrol.get("coupon_interest"))
			}));  		//转入账户--> 投资人
			
			bocJson.put("remark", "发红包");
			bocJson.put("orderNo", efPaycontrol.get("ef_order_id"));
			bocJson.put("acqRes", "");
			bocJson.put("sendAppName", "erpv3_auto2");
			//JSONObject bocResult = WebServiceUtil.invokeBoc("bankService", "getBank7603", bocJson);
			JSONObject bocResult = WebServiceUtil.sendPost(SysConfig.getInstance().getProperty("pay.center.api")+"payProcess/redPayment", new Object[]{bocJson});
			//如果红包错误
			if("0".equals(bocResult.getString("responseCode"))){
				logger.warn("新手标理财明细在发放红包时失败!理财明细id:"+efPaycontrol.get("id"));
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
				smsJson.put("text", "新手标理财明细在发放红包时失败!理财明细id:"+efPaycontrol.get("id"));
				SmsUtil.senErrorMsg(smsJson);
				continue;
			}
			//资金记录修改
			logger.warn("新手标理财明细在发放红包时成功!理财明细id:"+efPaycontrol.get("id"));
			//准备修改明细和记录(针对利息的记录)
			efPaycontrol.put("clearing_channel","2");
			efPaycontrol.put("onLine","1");
			efPaycontrol.put("online_type","1");
			bgEfOrderService.cleanInterest(efPaycontrol);
			//开始发送邀请人红包
			if(!ChkUtil.isEmpty(efPaycontrol.get("referee_info_id"))){
				Map refereeInfoMap = bgEfOrderService.getRefereeInfoMap(efPaycontrol.get("referee_info_id").toString());
				bocJson.clear();
				//bocJson.put("cardNbr", StaticData.redEnvelope);  					//转出账户--> 红包账户
				bocJson.put("forAccountId", refereeInfoMap.get("bank_account"));  		//转入账户--> 投资人
				bocJson.put("txAmount",ArithUtil.mul(
						ArithUtil.add(Double.parseDouble(efPaycontrol.get("surplus_interest").toString()),Double.parseDouble(efPaycontrol.get("coupon_interest").toString())),
						Double.parseDouble(efPaycontrol.get("referee_income_scale").toString())/100d
						));  		//转入账户--> 投资人
				bocResult.clear();
				//bocResult = WebServiceUtil.invokeBoc("bankService", "getBank7603", bocJson);
				bocJson.put("remark", "转入");
				bocJson.put("orderNo", efPaycontrol.get("ef_order_id"));
				bocJson.put("acqRes", "");
				bocJson.put("sendAppName", "erpv3_auto2");
				 bocResult = WebServiceUtil.sendPost(SysConfig.getInstance().getProperty("pay.center.api")+"payProcess/redPayment", new Object[]{bocJson});
				//如果红包错误
				if("0".equals(bocResult.getString("responseCode"))){
					logger.warn("新手标理财明细在发放邀请人收益红包时失败!理财明细id:"+efPaycontrol.get("id"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
					smsJson.put("text", "新手标理财明细在发放邀请人收益红包时失败!理财明细id:"+efPaycontrol.get("id"));
					SmsUtil.senErrorMsg(smsJson);
				}else{
				//资金记录修改
				logger.warn("新手标理财明细在发放邀请人收益红包时成功!理财明细id:"+efPaycontrol.get("id"));
				}
			}
			//开始进行明细转移和状态修改和记录
			efPaycontrol.put("onLine", "1"); //赋值新手标
			efPaycontrol.put("online_type", "1"); //赋值新手标
			efPaycontrol.put("tasteOrder", "yes"); //赋值新手标
			bgEfOrderService.normalRepay(efPaycontrol);
		}
		logger.warn("新手标自动还款结束----");
	}
	/**
	 * 功能说明：执行线下理财自动还款操作
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
	public void toExecuteDeteEfAutopayBoc(){
		logger.warn("定投Boc理财自动还款开始----");
		//查找今天需要还款的理财明细并通过债券转让的方式还款
		//1.查找今日待还
		//2.发送红包
		//3.购买债券
		//4.更新操作  >> 借款人(订单明细记录) >> 投资人(订单明细记录) >>ZZL (订单明细记录)
		List<Map> efPaycontrols =  bgEfOrderService.getCurrentDeteControlsBoc();
		//如果无数据则结束
		if(ListTool.isNullOrEmpty(efPaycontrols)){logger.warn("定投Boc理财自动还款结束----");return;}
		//循环-->发送红包-->购买债券-->生成对应的记录
		for(Map efPaycontrol:efPaycontrols){
				//开始购买债权
				JSONObject bocJson = new JSONObject();
				bocJson.put("cardNbr", StaticData.risk);												//承接方-->风险户
				bocJson.put("authCode",efPaycontrol.get("invest_auz_code"));							//投标返回的授权码
				bocJson.put("cardNbro",efPaycontrol.get("bank_account"));								//转让方电子账号
				//总共可转让金额  = 本金
				Map efOrder = bgEfOrderService.getBgEfOrdersById(efPaycontrol.get("ef_order_id").toString());
				double tbalAce = Double.parseDouble(efOrder.get("principal").toString());
				bocJson.put("tbalAce",NumberFormat.formatDouble(tbalAce));								//总共可转让金额
				bocJson.put("amount", NumberUtil.parseDouble(efPaycontrol.get("surplus_principal"))); 	//转让金额
				bocJson.put("trpRice", NumberUtil.parseDouble(efPaycontrol.get("surplus_principal"))); 	//转让价格
				bocJson.put("intDate", DateUtil.formatDate(efPaycontrol.get("pay_time").toString(), DateUtil.STYLE_3)); //起息日 YYYYMMDD
				bocJson.put("yield", efPaycontrol.get("INVEST_RATE"));									//转让后预期年化收益率
				bocJson.put("feeWay", "0");																//手续费扣款方式 （0指定金额 1 同产品设置）
				bocJson.put("fee", "0");																//转让手续费(管理费)（feeWay为0时生效） 线下因为已经还过利息了,所以直接算成功 
				//准备链接BOC
				JSONObject bocResult = null;// WebServiceUtil.invokeBoc("bankService", "getBank5817", bocJson);
				//开始产生资金记录
				JSONObject checkMoneyJson = new JSONObject();
				checkMoneyJson.put("cardNo",bocJson.getString("cardNbro"));
				checkMoneyJson.put("cust_info_id",efPaycontrol.get("cust_info_id"));			//客户id
				checkMoneyJson.put("money",bocJson.get("amount"));					// 金额
				checkMoneyJson.put("money_type","4"); 			// 1金账户2,银行卡3,现场交钱4,电子账户
				checkMoneyJson.put("operation_type","1"); 		//1入账2出账
				checkMoneyJson.put("status","0");				//1成功0失败
				checkMoneyJson.put("person_type","1");			//1,投资人2借款人
				checkMoneyJson.put("type","5");					//1,提前结清2提前收回3,逾期还款4,部分逾期,5正常还款	
				checkMoneyJson.put("crm_order_id", efPaycontrol.get("crmOrderId"));
				String checkMoneyId = psCheckMoneyService.save(checkMoneyJson);
				if("0".equals(bocResult.getString("responseCode"))){
					logger.warn("定投Boc理财明细在债权转让时失败!理财明细id:"+efPaycontrol.get("id"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
					smsJson.put("text", "定投Boc理财明细在债权转让时失败!理财明细id:"+efPaycontrol.get("id"));
					SmsUtil.senErrorMsg(smsJson);
					continue;
				}
				//资金记录修改
				psCheckMoneyService.updateStatusById(checkMoneyId);
				logger.warn("定投Boc理财明细在债权转让时成功!理财明细id:"+efPaycontrol.get("id"));
				efPaycontrol.put("AUTHCODE", bocResult.get("AUTHCODE"));
				//开始进行明细转移和状态修改和记录
				efPaycontrol.put("onLine","0");
				bgEfOrderService.normalRepay(efPaycontrol);
				//开始进行可用资金存储
				efFundRecordService.addNormal(efPaycontrol);
				//开始撤销
				bocJson.clear();
				//准备查找原始的授权码
				String autoCode = efOrderService.findLineAuthCode(efPaycontrol.get("lineEfOrderId").toString());
				bocJson.put("cardNbr", efPaycontrol.get("bank_account"));
				bocJson.put("authCode",autoCode);
				bocResult.clear();
				bocResult =  null;//WebServiceUtil.invokeBoc("bankService", "getBank5807", bocJson);
				if("0".equals(bocResult.getString("responseCode"))){
					logger.warn("撤销定投Boc投资订单时失败!线下理财id:"+efPaycontrol.get("lineEfOrderId"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
					smsJson.put("text","定投Boc撤销投资订单时失败!线下理财id:"+efPaycontrol.get("lineEfOrderId"));
					SmsUtil.senErrorMsg(smsJson);
				}
				if("1".equals(bocResult.getString("responseCode"))){
					logger.warn("撤销定投Boc投资订单时成功!线下理财id:"+efPaycontrol.get("lineEfOrderId"));
					//查询可用金额进行冻结
					Double allMoney = efOrderService.findAllMoney(efPaycontrol.get("lineEfOrderId").toString());
					bocJson.clear();
					bocResult.clear();
					bocJson.put("cardNbr", efPaycontrol.get("bank_account"));
					bocJson.put("product",StaticData.A1);
					bocJson.put("amount",allMoney);
					bocJson.put("intDate",DateUtil.getCurrentTime(DateUtil.STYLE_3));
					bocJson.put("intType","1");
					//查找对应的投标交易流水号
					String auto_serino = efPaycontrolService.findAutoSerino(efPaycontrol.get("cust_info_id").toString());
					bocJson.put("authCode", auto_serino);
					//查找期数
					Map efApply = efOrderService.getEfApply(efPaycontrol.get("lineEfOrderId").toString());
					bocJson.put("intPayDay",DateUtil.getCurrentTime(DateUtil.STYLE_3).substring(6,8));
					bocJson.put("endDate",DateUtil.getLastTime(DateUtil.getCurrentTime(), 2, NumberUtil.parseInteger(efApply.get("invest_period")),DateUtil.STYLE_3));
					bocJson.put("yield",efApply.get("invest_rate"));
					bocJson.put("frzFlag", "1");
					bocJson.put("remark", "");
					bocResult =  null;//WebServiceUtil.invokeBoc("bankService", "getBank5805", bocJson);
					if("0".equals(bocResult.getString("responseCode"))){
						logger.warn("定投Boc重新投资订单时失败!线下理财id:"+efPaycontrol.get("lineEfOrderId"));
						JSONObject smsJson = new JSONObject();
						smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
						smsJson.put("text","定投Boc重新投资订单时失败!线下理财id:"+efPaycontrol.get("lineEfOrderId"));
						SmsUtil.senErrorMsg(smsJson);
					}
					if("1".equals(bocResult.getString("responseCode"))){
						logger.warn("定投Boc重新投资订单时成功!线下理财id:"+efPaycontrol.get("lineEfOrderId"));
						//开始修改理财订单表
						efOrderService.updateAuthCode(efPaycontrol.get("lineEfOrderId").toString(),bocResult.getString("AUTHCODE"));
					}
				}
		}
		logger.warn("定投Boc理财自动还款结束----");
	}
	/**
	 * 功能说明：执行线下理财自动还款操作
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
	public void toExecuteDeteEfAutopayPoc(){
		logger.warn("定投Poc理财自动还款开始----");
		//查找今天需要还款的理财明细并通过债券转让的方式还款
		//1.查找今日待还
		//2.发送红包
		//3.购买债券
		//4.更新操作  >> 借款人(订单明细记录) >> 投资人(订单明细记录) >>ZZL (订单明细记录)
		List<Map> efPaycontrols =  bgEfOrderService.getCurrentDeteControlsPoc();
		//如果无数据则结束
		if(ListTool.isNullOrEmpty(efPaycontrols)){logger.warn("定投Poc理财自动还款结束----");return;}
		//循环-->发送红包-->购买债券-->生成对应的记录
		for(Map efPaycontrol:efPaycontrols){
				Map peerTopeer = new HashMap();
				peerTopeer.put("out_cust_no", StaticData.creditAccount);
				peerTopeer.put("int_cust_no", efPaycontrol.get("fy_account"));
				peerTopeer.put("amt",NumberUtil.parseDouble(efPaycontrol.get("surplus_principal")));
				JSONObject pocResult = pocTool.peerTopeer(peerTopeer);
				if("0".equals(pocResult.getString("responseCode"))){
					logger.warn("定投Poc投资人本金还款失败!理财明细:id="+efPaycontrol.get("id"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
					smsJson.put("text", "定投Poc投资人本金还款失败!理财明细:id="+efPaycontrol.get("id"));
					SmsUtil.senErrorMsg(smsJson);
					continue;
				}
				logger.warn("定投Poc投资人本金还款成功!理财明细:id="+efPaycontrol.get("id"));
				//然后进行冻结操作
				Map freeze = new HashMap();
				freeze.put("cust_no", efPaycontrol.get("fy_account"));
				freeze.put("amt", NumberUtil.parseDouble(efPaycontrol.get("surplus_principal")));
				String freezeResult = pocTool.connectToPoc("freeze", freeze);
				//开始进行明细转移和状态修改和记录
				efPaycontrol.put("onLine","0");
				bgEfOrderService.normalRepay(efPaycontrol);
				//开始进行可用资金存储
				efFundRecordService.addNormal(efPaycontrol);
				if(!"0000".equals(freezeResult)){
					logger.warn("定投Poc投资人本金还款成功,在进行冻结时失败!理财明细:id="+efPaycontrol.get("id"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
					smsJson.put("text", "定投Poc投资人本金还款成功,在进行冻结时失败!理财明细:id="+efPaycontrol.get("id"));
					SmsUtil.senErrorMsg(smsJson);
					continue;
				}//冻结失败
				logger.warn("定投Poc投资人本金还款成功,在进行冻结时成功!理财明细:id="+efPaycontrol.get("id"));
		}
		logger.warn("定投Poc理财自动还款结束----");
	}
	/**
	 * 功能说明：真实理财利息还款
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年9月1日 10:14:48
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 */
	public void toExecuteEfOrderPkgOrderInterest(){
		logger.warn("真实理财利息还款开始----");
		Map paramMap = null;
		JSONObject bocJson = null;
		//1.查找今日要还的线下理财明细
		List<Map> efPaycontrols  = efPaycontrolService.getCurrentControlsInterest();
		logger.warn("本次真实理财利息还款个数:"+efPaycontrols.size());
		//清结算数据
		JSONArray flowRecordJsonArray = new JSONArray();
		if(ListTool.isNullOrEmpty(efPaycontrols)){logger.warn("真实理财利息还款结束----");return;}
		//开始循环开始进行利息的还款
		for(Map efPaycontrol:efPaycontrols){
			//准备开始记录数据
			JSONObject flowRecordJson = new JSONObject();
			flowRecordJson.put("orderType", "ef_paycontrol");
			flowRecordJson.put("payControlId", efPaycontrol.get("id"));
			flowRecordJson.put("status", "0");
			flowRecordJson.put("code", "");
			flowRecordJson.put("rem", "");
			//开始判断还款类型 1.poc  2.boc 
			String clearing_channel = efPaycontrol.get("clearing_channel").toString();
			//判断是否有邀请人
			JSONObject inviter = efPaycontrolService.getInviterData(efPaycontrol.get("income_cust_info_id")==null?"":efPaycontrol.get("income_cust_info_id").toString());
			double inviterMoney = 0d;
			if(ChkUtil.isNotEmpty(efPaycontrol.get("income_cust_info_id"))){ //开始计算金额
				double inviterScale = efPaycontrol.get("referee_income_scale")==null?0:Double.parseDouble(efPaycontrol.get("referee_income_scale").toString());
				inviterMoney = ArithUtil.mul(ArithUtil.sub(							//金额=(利息-管理费)*10%
						NumberUtil.parseDouble(efPaycontrol.get("surplus_interest")),
						NumberUtil.parseDouble(efPaycontrol.get("surplus_management_amt"))
						),inviterScale/100d );
			inviterMoney = NumberFormat.format(inviterMoney);
			}
			// 1.poc
			double rateCoupon = 0d;
			if(efPaycontrol.get("surplus_interest") !=null ) {
				rateCoupon = NumberUtil.parseDouble(efPaycontrol.get("surplus_rate_coupon"));
			}
			//富友的
			if ("1".equals(clearing_channel)) {
				paramMap = new HashMap();
				paramMap.put("out_cust_no", StaticData.creditAccount);
				paramMap.put("int_cust_no", efPaycontrol.get("fy_account"));
				paramMap.put("amt",  ArithUtil.sub(							//金额=利息-管理费
						NumberUtil.parseDouble(efPaycontrol.get("surplus_interest"))+rateCoupon,
						NumberUtil.parseDouble(efPaycontrol.get("surplus_management_amt"))
						));
				logger.warn("Poc理财明细id:"+efPaycontrol.get("id")+", 开始请求POC");
				JSONObject pocResult = pocTool.peerTopeer(paramMap);
				//开始产生资金记录
				if("0".equals(pocResult.getString("responseCode"))){
					flowRecordJson.put("status", "0");
					flowRecordJson.put("code", pocResult.get("code"));
					//记录中
					flowRecordJsonArray.add(flowRecordJson);
					logger.warn("Poc理财明细在发放利息时失败!理财明细id:"+efPaycontrol.get("id"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", "erpv3_auto2");
					smsJson.put("text","Poc真实理财明细在发放红包时失败!理财明细id:"+efPaycontrol.get("id"));
					smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
					continue;
				}
				flowRecordJson.put("status", "1");
				flowRecordJson.put("code", "0000");
				//记录中
				flowRecordJsonArray.add(flowRecordJson);
				logger.warn("Poc理财明细在发放利息时成功!理财明细id:"+efPaycontrol.get("id"));
				//platform_type  1 是贝格, 2是中资联平台 需要进行利息的还款 需要进行利息的还款
				if(inviter.containsKey("fy_account")&& ChkUtil.isNotEmpty(inviter.get("fy_account"))  && inviterMoney>0){  //有邀请人
					try{
						if(efPaycontrol.get("platform_type") !=null && 
								("1".equals(efPaycontrol.get("platform_type").toString()) || 
								 "2".equals(efPaycontrol.get("platform_type").toString())
								)
						){ 
							Map yqrMap = new HashMap();
							yqrMap.put("out_cust_no", StaticData.creditAccount);
							yqrMap.put("int_cust_no", inviter.get("fy_account")); //邀请人的
							yqrMap.put("amt", inviterMoney);
							JSONObject inviterResult = pocTool.peerTopeer(yqrMap);
							if("0".equals(inviterResult.getString("responseCode"))){
								logger.warn("Poc理财明细在发放邀请人利息时失败!理财明细id:"+efPaycontrol.get("id"));
								JSONObject smsJson = new JSONObject();
								smsJson.put("project_number", "erpv3_auto2");
								smsJson.put("text","Poc真实理财明细在发放邀请人收益时失败!理财明细id:"+efPaycontrol.get("id"));
								SmsUtil.senErrorMsgByZhiyun(smsJson);
							}else{
								logger.warn("Poc理财明细在发放邀请人利息时成功!理财明细id:"+efPaycontrol.get("id"));
								//开始增加邀请人的记录之类的
								JSONObject inviterRecord = new JSONObject();
								inviterRecord.put("ef_order_id", efPaycontrol.get("ef_order_id"));
								inviterRecord.put("referral_income", inviterMoney);
								inviterRecord.put("cust_info_id",efPaycontrol.get("cust_info_id").toString());
								inviterRecord.put("referee_info_id",inviter.get("id").toString());
								efPaycontrolService.pkgIncomeRecord(inviterRecord);
							}
							
							
						}
					}catch (Exception e) {
						logger.warn("Poc理财明细在发放邀请人利息时失败!理财明细id:"+efPaycontrol.get("id"));
						JSONObject smsJson = new JSONObject();
						smsJson.put("project_number", "erpv3_auto2");
						smsJson.put("text","Poc真实理财明细在发放邀请人收益时失败!理财明细id:"+efPaycontrol.get("id"));
						SmsUtil.senErrorMsgByZhiyun(smsJson);
					}
				}
			}else if (StaticData.HFBANK_CODE.equals(clearing_channel)){
				JSONObject transMap = new JSONObject();
				//恒丰扣款参数
				double amt = ArithUtil.sub(							//金额=利息-管理费
						NumberUtil.parseDouble(efPaycontrol.get("surplus_interest"))+rateCoupon,
						NumberUtil.parseDouble(efPaycontrol.get("surplus_management_amt"))
						);
				transMap.put("remark", "资产包理财订单到期还款");
				transMap.put("out_cust_no", StaticData.HFcreditAccount);
				transMap.put("in_cust_no", efPaycontrol.get("hf_account"));
				transMap.put("amt", NumberUtil.duelmoney(amt));
				transMap.put("contract_no", "");
				transMap.put("rem", "资产包理财订单到期还款");
				transMap.put("project_number", StaticData.HFProjectNumber);
				transMap.put("out_login_name", StaticData.HFcreditAccountName);
				transMap.put("in_login_name", efPaycontrol.get("cust_name"));
				transMap.put("amount", NumberUtil.duelmoney(amt));
				transMap.put("fx_amt", "0");
				transMap.put("business_type", "8");
				transMap.put("orderId", "");
				transMap.put("orgin_login_id", "");
				transMap.put("orgin_login_name", "");
				transMap.put("fullString", "");
				transMap.put("summary", "");
				transMap.put("interfaceType", "");
				transMap.put("trade_date", DateUtil.getCurrentTime(DateUtil.STYLE_3));
				if(amt>0){
					JSONObject pocResult = WebServiceUtil.sendPostHF(StaticData.bocUrl+"hfTransferService/transfer",transMap);
					//开始产生资金记录
					if("0".equals(pocResult.getString("responseCode"))){
						flowRecordJson.put("status", "0");
						flowRecordJson.put("code", pocResult.get("code"));
						//记录中
						//flowRecordJsonArray.add(flowRecordJson);
						logger.warn("恒丰理财明细在发放利息时失败!理财明细id:"+efPaycontrol.get("id"));
						JSONObject smsJson = new JSONObject();
						smsJson.put("project_number", "erpv3_auto2");
						smsJson.put("text","恒丰真实理财明细在发放红包时失败!理财明细id:"+efPaycontrol.get("id"));
						smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
						continue;
					}
				}
				flowRecordJson.put("status", "1");
				flowRecordJson.put("code", "0000");
				//记录中
				//flowRecordJsonArray.add(flowRecordJson);
				logger.warn("恒丰理财明细在发放利息时成功!理财明细id:"+efPaycontrol.get("id"));
				//platform_type  1 是贝格, 2是中资联平台 需要进行利息的还款 需要进行利息的还款
				if(inviter.containsKey("hf_account")&& ChkUtil.isNotEmpty(inviter.get("hf_account"))  && inviterMoney>0){  //有邀请人
					try{
						if(efPaycontrol.get("platform_type") !=null && 
								("1".equals(efPaycontrol.get("platform_type").toString()) || 
								 "2".equals(efPaycontrol.get("platform_type").toString())
								)
						){ 
							transMap.put("remark", "资产包理财订单到期还款");
							transMap.put("out_cust_no", StaticData.HFcreditAccount);
							transMap.put("in_cust_no", inviter.get("hf_account"));
							transMap.put("amt", NumberUtil.duelmoney(inviterMoney));
							transMap.put("contract_no", "");
							transMap.put("rem", "资产包理财订单到期还款");
							transMap.put("project_number", StaticData.HFProjectNumber);
							transMap.put("out_login_name", StaticData.HFcreditAccountName);
							transMap.put("in_login_name", inviter.get("cust_name"));
							transMap.put("amount", NumberUtil.duelmoney(inviterMoney));
							transMap.put("fx_amt", "0");
							transMap.put("business_type", "8");
							transMap.put("orderId", "");
							transMap.put("orgin_login_id", "");
							transMap.put("orgin_login_name", "");
							transMap.put("fullString", "");
							transMap.put("summary", "");
							transMap.put("interfaceType", "");
							transMap.put("trade_date", DateUtil.getCurrentTime(DateUtil.STYLE_3));
							JSONObject inviterResult = WebServiceUtil.sendPostHF(StaticData.bocUrl+"hfTransferService/transfer",transMap);
							if("0".equals(inviterResult.getString("responseCode"))){
								logger.warn("恒丰理财明细在发放邀请人利息时失败!理财明细id:"+efPaycontrol.get("id"));
								JSONObject smsJson = new JSONObject();
								smsJson.put("project_number", "erpv3_auto2");
								smsJson.put("text","恒丰真实理财明细在发放邀请人收益时失败!理财明细id:"+efPaycontrol.get("id"));
								SmsUtil.senErrorMsgByZhiyun(smsJson);
							}else{
								logger.warn("恒丰理财明细在发放邀请人利息时成功!理财明细id:"+efPaycontrol.get("id"));
								//开始增加邀请人的记录之类的
								JSONObject inviterRecord = new JSONObject();
								inviterRecord.put("ef_order_id", efPaycontrol.get("ef_order_id"));
								inviterRecord.put("referral_income", inviterMoney);
								inviterRecord.put("cust_info_id",efPaycontrol.get("cust_info_id").toString());
								inviterRecord.put("referee_info_id",inviter.get("id").toString());
								efPaycontrolService.pkgIncomeRecord(inviterRecord);
							}
							
							
						}
					}catch (Exception e) {
						logger.warn("恒丰理财明细在发放邀请人利息时失败!理财明细id:"+efPaycontrol.get("id"));
						JSONObject smsJson = new JSONObject();
						smsJson.put("project_number", "erpv3_auto2");
						smsJson.put("text","恒丰真实理财明细在发放邀请人收益时失败!理财明细id:"+efPaycontrol.get("id"));
						SmsUtil.senErrorMsgByZhiyun(smsJson);
					}
				}
				
			//2.boc 授权码不为空
			}
			else if ("2".equals(clearing_channel)) {
				String url = StaticData.bocUrl;
				bocJson = new JSONObject();
				bocJson.put("remark", "boc真实理财利息还款（红包还款）");//备注
				bocJson.put("txAmount", ArithUtil.sub(
						NumberUtil.parseDouble(efPaycontrol.get("surplus_interest"))+rateCoupon,
						NumberUtil.parseDouble(efPaycontrol.get("surplus_management_amt"))
						));//交易金额
				bocJson.put("orderNo", efPaycontrol.get("id"));//明细id
				bocJson.put("forAccountId", efPaycontrol.get("bank_account"));//电子账号 bank_account
				bocJson.put("acqRes", efPaycontrol.get("id"));//请求方保留,明细id
				bocJson.put("sendAppName", StaticData.appName);//明细id
				JSONObject bocResult = WebServiceUtil.sendPost(url+"payProcess/redPayment", new Object[]{bocJson});
				//如果红包错误
				if("0".equals(bocResult.getString("responseCode"))){
					logger.warn("Boc真实理财明细在发放红包时失败!理财明细id:"+efPaycontrol.get("id"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", "erpv3_auto2");
					smsJson.put("text","Boc真实理财明细在发放红包时失败!理财明细id:"+efPaycontrol.get("id"));
					smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
					continue;
				}
				//资金记录修改
				logger.warn("Boc理财明细在发放红包时成功!理财明细id:"+efPaycontrol.get("id"));
				//开始进行邀请人的收益的赋值
				//platform_type  1 是贝格, 2是中资联平台 需要进行利息的还款 需要进行利息的还款
				if(inviter.containsKey("bank_account") && ChkUtil.isNotEmpty(inviter.get("bank_account"))   && inviterMoney>0 ){  //有邀请人
					try{
						if(efPaycontrol.get("platform_type") !=null && 
								("1".equals(efPaycontrol.get("platform_type").toString()) || 
								 "2".equals(efPaycontrol.get("platform_type").toString())
								)
						){ 
							JSONObject inviterJson = new JSONObject();
							inviterJson.put("remark", "邀请人boc真实理财利息还款（红包还款）");//备注
							inviterJson.put("txAmount", inviterMoney);//交易金额
							inviterJson.put("orderNo", efPaycontrol.get("id"));//明细id
							inviterJson.put("forAccountId", inviter.get("bank_account"));//电子账号 bank_account
							inviterJson.put("acqRes", efPaycontrol.get("id"));//请求方保留,明细id
							inviterJson.put("sendAppName", StaticData.appName);//明细id
							JSONObject inviterResult = WebServiceUtil.sendPost(url+"payProcess/redPayment", new Object[]{inviterJson});
							//如果红包错误
							if("0".equals(inviterResult.getString("responseCode"))){
								logger.warn("Boc理财明细在发放邀请人利息时失败!理财明细id:"+efPaycontrol.get("id"));
								JSONObject smsJson = new JSONObject();
								smsJson.put("project_number", "erpv3_auto2");
								smsJson.put("text","Boc理财明细在发放邀请人利息时失败!理财明细id:"+efPaycontrol.get("id"));
								SmsUtil.senErrorMsgByZhiyun(smsJson);
							}else{
								logger.warn("Boc理财明细在发放邀请人利息时成功!理财明细id:"+efPaycontrol.get("id"));
								//开始增加邀请人的记录之类的
								JSONObject inviterRecord = new JSONObject();
								inviterRecord.put("ef_order_id", efPaycontrol.get("ef_order_id"));
								inviterRecord.put("referral_income", inviterMoney);
								inviterRecord.put("cust_info_id",efPaycontrol.get("cust_info_id").toString());
								inviterRecord.put("referee_info_id",inviter.get("id").toString());
								efPaycontrolService.pkgIncomeRecord(inviterRecord);
							}
						}
					}catch (Exception e) {
						logger.warn("Boc理财明细在发放邀请人利息时失败!理财明细id:"+efPaycontrol.get("id"));
						JSONObject smsJson = new JSONObject();
						smsJson.put("project_number", "erpv3_auto2");
						smsJson.put("text","Boc真实理财明细在发放邀请人收益时失败!理财明细id:"+efPaycontrol.get("id"));
						SmsUtil.senErrorMsgByZhiyun(smsJson);
					}
				}
				//邀请人收益记录结束
			}
			//如果成功则需要修改对于的理财明细和记录并判断是否可以结束
			boolean rst=efOrderService.isExistBgEfOrder(efPaycontrol.get("ef_order_id").toString());
			efPaycontrol.put("hasBgEfOrders", rst);
			efPaycontrolService.updateNormal(efPaycontrol);
			efPayrecordService.addNormal(efPaycontrol);
			//判断是剩余本金、剩余利息、剩余管理费是否为0
			if (!rst) {
				efOrderService.isOver(efPaycontrol.get("ef_order_id").toString());
				//efOrderService.clearPkgOrder(efPaycontrol.get("ef_applay_id").toString());
			}
		}
		//开始结清资产包
		clearPkgOrders();
		//SmsUtil.sendFlowRecord(flowRecordJsonArray);
		//结清资产包
//		efOrderService.clearPkgOrder(null);
		logger.warn("真实理财利息还款结束----");
	}
	
	/**
	 * 功能说明：结清资产包
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年9月1日 10:14:52
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 */
	public void clearPkgOrders(){
		List<Map> list =  efPaycontrolService.getNeedClearPKOrders();
		efOrderService.clearPkgOrderList(list);
	}
	
	/**
	 * 功能说明：真实理财本金还款
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年9月1日 10:14:52
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 */
	/*public void toExecuteEfOrderPkgOrderPrincipal (){
		logger.warn("真实理财本金还款开始----");
		Map paramMap = null;
		JSONObject bocJson = null;
		//1.查找今日要还的线下理财明细
		List<Map> efPaycontrols  = efPaycontrolService.getCurrentControlsPrincipal();
		logger.warn("本次真实理财本金还款个数:"+efPaycontrols.size());
		if(ListTool.isNullOrEmpty(efPaycontrols)){logger.warn("真实理财本金还款结束----");return;}
		//开始循环开始进行利息的还款
		for(Map efPaycontrol:efPaycontrols){
			//开始判断还款类型 1.poc  2.boc 
			String clearing_channel = efPaycontrol.get("clearing_channel").toString();
			// 1.poc
			if ("1".equals(clearing_channel)) {
				paramMap = new HashMap();
				paramMap.put("cust_no", efPaycontrol.get("fy_account"));
				paramMap.put("amt", NumberUtil.parseDouble(efPaycontrol.get("surplus_principal")));   
				String pocString = pocTool.connectToPoc("unFreeze", paramMap);
				JSONObject pocResult=JSONObject.fromObject(pocString);
				//开始产生资金记录
				if("0".equals(pocResult.getString("responseCode"))){
					logger.warn("Poc理财明细在解冻时失败!理财明细id:"+efPaycontrol.get("id"));
					continue;
				}
				logger.warn("Poc理财明细在解冻时成功!理财明细id:"+efPaycontrol.get("id"));
			//2.boc 授权码不为空
			}else if ("2".equals(clearing_channel) && ChkUtil.isNotEmpty(efPaycontrol.get("invest_auz_code"))) {
				bocJson = new JSONObject();
				bocJson.put("cardNbr", efPaycontrol.get("bank_account"));  			//电子账号
				bocJson.put("authCode", efPaycontrol.get("invest_auz_code"));  		//授权码（投标返回）
				JSONObject bocResult = WebServiceUtil.invokeBoc("bankService", "getBank5807", bocJson);
				//如果红错误
				if("0".equals(bocResult.getString("responseCode"))){
					logger.warn("Boc真实理财明细在投标撤销时失败!理财明细id:"+efPaycontrol.get("id"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", efPaycontrol.get("ef_order_id"));
					smsJson.put("text","Boc真实理财明细在投标撤销时失败!理财明细id:"+efPaycontrol.get("id"));
					SmsUtil.senErrorMsg(smsJson);
					continue;
				}
				//资金记录修改
				logger.warn("Boc理财明细在投标撤销时成功!理财明细id:"+efPaycontrol.get("id"));
			}
			//如果成功则需要修改对于的理财明细和记录并判断是否可以结束
			efPaycontrolService.updateNormal(efPaycontrol);
			efPayrecordService.addNormal(efPaycontrol);
			efOrderService.isOver(efPaycontrol.get("ef_order_id").toString());
			efOrderService.clearPkgOrder(efPaycontrol.get("ef_applay_id").toString());
		}
		logger.warn("真实理财本金还款结束----");
	}*/
	/**
	 * 功能说明：执行定投Boc信贷还款
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
	public void toExecuteImmeCrmAutopayBoc(){
		String boc_url=StaticData.bocUrl;
		logger.warn("直投Boc信贷订单的还款开始---");
		//获得当日的需要还款的crm信贷明细
		List<Map> crmPaycontrols =  crmOrderService.getCurrentImmePaycontrolsBoc();
		//踢出资产包相关数据
		crmOrderService.removePkgCrmOrders(crmPaycontrols);
		if(ListTool.isNotNullOrEmpty(crmPaycontrols)){
			//先开始循环冻结
			for(Map crmPaycontrol:crmPaycontrols){
				//开始检测是否已经被处理过了
				if(crmPaycontrol.containsKey("id")){
					CrmPaycontrol cpc;
					try {
						cpc = (CrmPaycontrol) bgEfOrderDao.findById(CrmPaycontrol.class, crmPaycontrol.get("id").toString());
						if(cpc != null && cpc.getStatus()!=null && cpc.getStatus()==1 ){ //说明已经被处理过了
							continue;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//开始判断改明细前面是否有逾期订单
				boolean hasOver = crmOrderService.hasOver(crmPaycontrol.get("crm_order_id").toString());
				if(hasOver){
					continue;
				}
				List<Map> zzlEfpaycontrols = zzlEfPaycontrolService.getCurrentByCrmControl(crmPaycontrol.get("crm_order_id").toString(),crmPaycontrol.get("repayment_time").toString());
				if(ListTool.isNullOrEmpty(zzlEfpaycontrols)){continue;}
					//开始针对借款人进行还款
					//因为查询的是线上订单所以结算户通道必然是BOC
					JSONObject bocJson = new JSONObject();
					try{
						bocJson.put("accountId",crmPaycontrol.get("bank_account").toString());//电子账号
						bocJson.put("txAmount",NumberUtil.parseDouble(crmPaycontrol.get("frozenMoney")));//冻结金额
						bocJson.put("acqRes","直投正常还款明细id:"+crmPaycontrol.get("id").toString());//保留域
						bocJson.put("sendAppName",StaticData.appName);//
						bocJson.put("productId",crmPaycontrol.get("order_prd_number").toString());
						bocJson.put("frzType","0");
						bocJson.put("signature",SignatureUtil.createSign());
						bocJson.put("remark","");//保留域
						//开始请求冻结接口
						JSONObject bocResultJson =WebServiceUtil.sendPost(boc_url+"payProcess/bocBalanceFreeze", new Object[]{bocJson});
						//冻结成功则直接算成功
						if(!bocResultJson.containsKey("responseCode") ||  !"1".equals(bocResultJson.getString("responseCode"))){
							logger.warn("直投Boc信贷订单冻结失败,明细id:"+crmPaycontrol.get("id")+",冻结金额:"+crmPaycontrol.get("frozenMoney"));
							continue;
						}
						//冻结成功的情况下
						logger.warn("直投Boc上信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",冻结金额:"+crmPaycontrol.get("frozenMoney"));
						//开始还款
						JSONArray jsonArray = new JSONArray();
						boolean forzenFlag = zzlEfPaycontrolService.frozen(zzlEfpaycontrols);
						if(!forzenFlag){
							JSONObject smsJson = new JSONObject();
							smsJson.put("project_number", crmPaycontrol.get("order_number"));
							logger.warn("直投Boc信贷订单冻结成功但在进行zzl债权数据修改时失败,明细id:"+crmPaycontrol.get("id"));
							smsJson.put("text","直投Boc信贷订单冻结成功但在进行zzl债权数据修改时失败,明细id:"+crmPaycontrol.get("id"));
							SmsUtil.senErrorMsg(smsJson);
							continue;
							}
						for(Map zzlEfpaycontrol:zzlEfpaycontrols){
							ZZLEfPaycontrol  zzl = zzlEfPaycontrolService.findById(zzlEfpaycontrol.get("id").toString());
							JSONObject cuJson = new JSONObject();
							cuJson.put("orderId", "");
							cuJson.put("accountId",crmPaycontrol.get("bank_account"));			//扣款账户
							cuJson.put("txAmount", zzl.getSurplusPrincipal());	//交易金额
							cuJson.put("intAmount", 0d);										//交易利息
							cuJson.put("txFeeOut", ArithUtil.add(new Double[]{					//还款手续费 = 剩余管理费+多出的管理费+剩余利息+多出来的利息
									zzl.getSurplusInterest(),
									zzl.getSurplusManagementAmt(),
									zzl.getMoreInterest(),
									zzl.getMoreManageAmt()
							})) ;
							cuJson.put("txFeeIn", "0");											//收款手续费
							cuJson.put("forAccountId", StaticData.risk);						//入款账号
							cuJson.put("productId", crmPaycontrol.get("order_prd_number").toString());	//产品号
							cuJson.put("authCode", zzlEfpaycontrol.get("auth_code"));
							if(cuJson.getString("authCode").endsWith("_needRemove")){
								cuJson.put("authCode", cuJson.getString("authCode").split(Pattern.quote("_needRemove"))[0]);
								cuJson.put("forAccountId", StaticData.redEnvelope);
							}
							cuJson.put("trdresv", "auto:"+zzlEfpaycontrol.get("id"));//第三方保留域
							//查看本期还款是否结束
							
							jsonArray.add(cuJson);
						}
						//开始产生资金记录
						JSONObject checkMoneyJson = new JSONObject();
						checkMoneyJson.put("cardNo",crmPaycontrol.get("bank_account"));
						checkMoneyJson.put("cust_info_id",crmPaycontrol.get("cust_info_id"));			//客户id
						checkMoneyJson.put("money",ArithUtil.add(JsonUtil.getSum(jsonArray, "txAmount"),JsonUtil.getSum(jsonArray, "txFeeOut")));					// 金额
						checkMoneyJson.put("money_type","4"); 			// 1金账户2,银行卡3,现场交钱4,电子账户
						checkMoneyJson.put("operation_type","2"); 		//1入账2出账
						checkMoneyJson.put("status","0");				//1成功0失败
						checkMoneyJson.put("person_type","2");			//1,投资人2借款人3,邀请人
						checkMoneyJson.put("type","5");					//1,提前结清2提前收回3,逾期还款4,部分逾期,5正常还款	
						checkMoneyJson.put("crm_order_id", crmPaycontrol.get("crm_order_id"));
						String checkMoneyId = psCheckMoneyService.save(checkMoneyJson);
						
						JSONObject transMap = new JSONObject();
						transMap.put("acqRes","");//第三方保留域
						transMap.put("subPacks", jsonArray.toString());
						transMap.put("signature", SignatureUtil.createSign());
						transMap.put("sendAppName",StaticData.appName);//
						transMap.put("remark","");//保留域
						try{
						bocResultJson  = WebServiceUtil.sendPost(boc_url+"payProcess/batchRepay", new Object[]{transMap});
						//债权转让失败了
						if(!bocResultJson.containsKey("responseCode") || !"1".equals(bocResultJson.getString("responseCode"))){
							logger.warn("线上信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回失败!");
							JSONObject smsJson = new JSONObject();
							smsJson.put("project_number", crmPaycontrol.get("order_number"));
							smsJson.put("text","线上信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回失败!");
							SmsUtil.senErrorMsg(smsJson);
							continue;
						}
						}catch (Exception e) {
							e.printStackTrace();
							logger.warn(e.getMessage(),e);
							logger.warn("线上信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回失败!");
							JSONObject smsJson = new JSONObject();
							smsJson.put("project_number", crmPaycontrol.get("order_number"));
							smsJson.put("text","线上信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回失败!");
							SmsUtil.senErrorMsg(smsJson);
							continue;
						}
						//能到这一步代表成功了
						//资金记录修改
						psCheckMoneyService.updateStatusById(checkMoneyId);
						logger.warn("直投Boc信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回成功!");
					}catch (Exception e) {
						e.printStackTrace();
						logger.warn(e.getMessage(),e);
						logger.warn("直投Boc信贷订单还款异常,明细id:"+crmPaycontrol.get("id")+",BOC返回成功!");
					}
			}
			
			
		}
		logger.warn("定投Boc信贷订单的还款结束---");
	}
	/**
	 * 功能说明：执行直投Poc信贷还款
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
	public void toExecuteImmeCrmAutopayPoc(){
		logger.warn("直投Poc信贷订单的还款开始---");
		//获得当日的需要还款的crm信贷明细
		List<Map> crmPaycontrols =  crmOrderService.getCurrentImmePaycontrolsPoc();
		if(ListTool.isNotNullOrEmpty(crmPaycontrols)){
			//先开始循环冻结
			for(Map crmPaycontrol:crmPaycontrols){
				//开始判断改明细前面是否有逾期订单
				boolean hasOver = crmOrderService.hasOver(crmPaycontrol.get("crm_order_id").toString());
				if(hasOver){
					continue;
				}
				JSONObject pocResult = transToZzl.payByPocDaikou(crmPaycontrol.get("cust_info_id").toString(),crmPaycontrol.get("frozenMoney").toString(),"5",crmPaycontrol.get("crm_order_id").toString());
				if("1".equals(pocResult.getString("responseCode"))){ //扣款成功直接更新
					//poc扣款成功,直接进行修改状态就可以了
					logger.warn("线下信贷订单还款成功,明细id:"+crmPaycontrol.get("id")+",POC返回成功!");
					crmOrderService.repayNormalByPoc(crmPaycontrol.get("id").toString());
				}else{
					logger.warn("线下信贷订单还款失败,明细id:"+crmPaycontrol.get("id")+",POC返回失败!");
				}
			}
			
			
		}
		logger.warn("直投Poc信贷订单的还款结束---");
	}
	
	
	/**
	 * 功能说明：执行线下信贷还款
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
	public void toExecuteDeteCrmAutopayBoc(){
		String boc_url=StaticData.bocUrl;
		logger.warn("定投BOC信贷订单的还款开始---");
		//获得当日的需要还款的crm信贷明细
		List<Map> crmPaycontrols =  crmOrderService.getCurrentDetePaycontrolsBoc();
		if(ListTool.isNullOrEmpty(crmPaycontrols)){logger.warn("定投BOC信贷订单的还款结束---");return;}
		//先开始循环冻结
		for(Map crmPaycontrol:crmPaycontrols){
			try{
				//开始检测是否已经被处理过了
				if(crmPaycontrol.containsKey("id")){
					CrmPaycontrol cpc =  (CrmPaycontrol) bgEfOrderDao.findById(CrmPaycontrol.class, crmPaycontrol.get("id").toString());
					if(cpc != null && cpc.getStatus()!=null && cpc.getStatus()==1 ){ //说明已经被处理过了
						continue;
					}
				}
				//开始判断改明细前面是否有逾期订单
				boolean hasOver = crmOrderService.hasOver(crmPaycontrol.get("crm_order_id").toString());
				if(hasOver){
					continue;
				}
				//开始针对借款人进行还款
				List<Map> zzlEfpaycontrols = zzlEfPaycontrolService.getCurrentByCrmControl(crmPaycontrol.get("crm_order_id").toString(),crmPaycontrol.get("repayment_time").toString());
				if(ListTool.isNullOrEmpty(zzlEfpaycontrols)){continue;}
				JSONObject bocJson = new JSONObject();
				
				bocJson.put("accountId",crmPaycontrol.get("bank_account").toString());//电子账号
				bocJson.put("txAmount",NumberUtil.parseDouble(crmPaycontrol.get("frozenMoney")));//冻结金额
				bocJson.put("acqRes","定投正常还款明细id:"+crmPaycontrol.get("id").toString());//保留域
				bocJson.put("sendAppName",StaticData.appName);//
				bocJson.put("productId",crmPaycontrol.get("order_prd_number").toString());
				bocJson.put("frzType","0");
				bocJson.put("signature",SignatureUtil.createSign());
				bocJson.put("remark","");//保留域
				//开始请求冻结接口
				JSONObject bocResultJson =   WebServiceUtil.sendPost(boc_url+"payProcess/bocBalanceFreeze",new Object[]{bocJson});
				//冻结成功则直接算成功
				if(!bocResultJson.containsKey("responseCode") ||  !"1".equals(bocResultJson.getString("responseCode"))){
					logger.warn("定投Boc信贷订单冻结失败,明细id:"+crmPaycontrol.get("id")+",冻结金额:"+crmPaycontrol.get("frozenMoney"));
					continue;
				}
				//冻结成功的情况下就准备
				logger.warn("定投BOC信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",冻结金额:"+crmPaycontrol.get("frozenMoney"));
				//开始冻结明细
				boolean forzenFlag = zzlEfPaycontrolService.frozen(zzlEfpaycontrols);
				//开始还款
				JSONArray jsonArray = new JSONArray();
				if(!forzenFlag){
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", crmPaycontrol.get("order_number"));
					logger.warn("定投BOC信贷订单冻结成功但在进行zzl债权修改时失败,明细id:"+crmPaycontrol.get("id"));
					smsJson.put("text","定投BOC信贷订单冻结成功但在进行zzl债权修改时失败,明细id:"+crmPaycontrol.get("id"));
					SmsUtil.senErrorMsg(smsJson);	
					continue;
				}
				for(Map zzlEfpaycontrol:zzlEfpaycontrols){
					ZZLEfPaycontrol  zzl = zzlEfPaycontrolService.findById(zzlEfpaycontrol.get("id").toString());
					JSONObject cuJson = new JSONObject();
					cuJson.put("orderId", "");
					cuJson.put("accountId",crmPaycontrol.get("bank_account"));			//扣款账户
					cuJson.put("txAmount", zzl.getSurplusPrincipal());	//交易金额
					cuJson.put("intAmount", 0d);										//交易利息
					cuJson.put("txFeeOut", ArithUtil.add(new Double[]{					//还款手续费 = 剩余管理费+多出的管理费+剩余利息+多出来的利息
							zzl.getSurplusInterest(),
							zzl.getSurplusManagementAmt(),
							zzl.getMoreInterest(),
							zzl.getMoreManageAmt()
					})) ;
					cuJson.put("txFeeIn", "0");											//收款手续费
					cuJson.put("forAccountId", StaticData.risk);						//入款账号
					cuJson.put("productId", crmPaycontrol.get("order_prd_number").toString());	//产品号
					cuJson.put("authCode", zzlEfpaycontrol.get("auth_code"));
					if(cuJson.getString("authCode").endsWith("_needRemove")){
						cuJson.put("authCode", cuJson.getString("authCode").split(Pattern.quote("_needRemove"))[0]);
						cuJson.put("forAccountId", StaticData.redEnvelope);
					}
					cuJson.put("trdresv", "auto:"+zzlEfpaycontrol.get("id"));//第三方保留域
					
					jsonArray.add(cuJson);
				}
				//开始产生资金记录
				JSONObject checkMoneyJson = new JSONObject();
				checkMoneyJson.put("cardNo",crmPaycontrol.get("bank_account"));
				checkMoneyJson.put("cust_info_id",crmPaycontrol.get("cust_info_id"));			//客户id
				checkMoneyJson.put("money",ArithUtil.add(JsonUtil.getSum(jsonArray, "txAmount"),JsonUtil.getSum(jsonArray, "txFeeOut")));					// 金额
				checkMoneyJson.put("money_type","4"); 			// 1金账户2,银行卡3,现场交钱4,电子账户
				checkMoneyJson.put("operation_type","2"); 		//1入账2出账
				checkMoneyJson.put("status","0");				//1成功0失败
				checkMoneyJson.put("person_type","2");			//1,投资人2借款人3,邀请人
				checkMoneyJson.put("type","5");					//1,提前结清2提前收回3,逾期还款4,部分逾期,5正常还款	
				checkMoneyJson.put("crm_order_id", crmPaycontrol.get("crm_order_id"));
				String checkMoneyId = psCheckMoneyService.save(checkMoneyJson);
				
				JSONObject transMap = new JSONObject();
				transMap.put("acqRes","");//第三方保留域
				transMap.put("subPacks", jsonArray.toString());
				transMap.put("signature", SignatureUtil.createSign());
				transMap.put("remark", "");
				transMap.put("sendAppName",StaticData.appName);
				
				try{
					
					bocResultJson  =WebServiceUtil.sendPost(boc_url+"payProcess/batchRepay",new Object[]{transMap});
					//债权转让失败了
					if(!bocResultJson.containsKey("responseCode") || !"1".equals(bocResultJson.getString("responseCode"))){
						logger.warn("线上信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回失败!");
						JSONObject smsJson = new JSONObject();
						smsJson.put("project_number", crmPaycontrol.get("order_number"));
						smsJson.put("text","线上信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回失败!");
						SmsUtil.senErrorMsg(smsJson);
						continue;
					}
				}catch (Exception e) {
					e.printStackTrace();
					logger.warn(e.getMessage(),e);
					logger.warn("线上信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回失败!");
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", crmPaycontrol.get("order_number"));
					smsJson.put("text","线上信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回失败!");
					SmsUtil.senErrorMsg(smsJson);
					continue;
				}
				//资金记录修改
				psCheckMoneyService.updateStatusById(checkMoneyId);
				logger.warn("定投BOC信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回成功!");
			}catch (Exception e) {
				e.printStackTrace();
				logger.warn(e.getMessage(),e);
				logger.warn("定投Boc信贷订单还款异常,明细id:"+crmPaycontrol.get("id")+",BOC返回成功!");
			}
		}
		logger.warn("定投BOC信贷订单的还款结束---");
	}
	/**
	 * 功能说明：执行线下信贷还款
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
	public void toExecuteDeteCrmAutopayPoc(){
		logger.warn("线下信贷订单的还款开始---");
		//获得当日的需要还款的crm信贷明细
		List<Map> crmPaycontrols =  crmOrderService.getCurrentDetePaycontrolsPoc();
		if(ListTool.isNotNullOrEmpty(crmPaycontrols)){
			//先开始循环冻结
			for(Map crmPaycontrol:crmPaycontrols){
				//开始判断改明细前面是否有逾期订单
				boolean hasOver = crmOrderService.hasOver(crmPaycontrol.get("crm_order_id").toString());
				if(hasOver){
					continue;
				}
				//poc账户通道
				JSONObject pocResult = transToZzl.payByPocDaikou(crmPaycontrol.get("cust_info_id").toString(),crmPaycontrol.get("frozenMoney").toString(),"5",crmPaycontrol.get("crm_order_id").toString());
				if("1".equals(pocResult.getString("responseCode"))){ //扣款成功直接更新
					//poc扣款成功,直接进行修改状态就可以了
					logger.warn("线下信贷订单还款成功,明细id:"+crmPaycontrol.get("id")+",POC返回成功!");
					crmOrderService.repayNormalByPoc(crmPaycontrol.get("id").toString());
				}else{
					logger.warn("线下信贷订单还款失败,明细id:"+crmPaycontrol.get("id")+",POC返回失败!");
				}
			}
		}
		logger.warn("线下信贷订单的还款结束---");
	}
	/**
	 * 功能说明：执行直投Boc信贷还款
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
	public void toExecuteOverImmeCrmAutopayBoc(){
		List<String> deteCrmOrderIdsList = new ArrayList<String>();
		String boc_url=StaticData.bocUrl;
		logger.warn("直投BOC逾期信贷订单的还款开始---");
		//获得当日的需要还款的crm信贷明细
		List<Map> crmPaycontrols =  crmOrderService.getOverImmePaycontrolsBoc();
		if(ListTool.isNotNullOrEmpty(crmPaycontrols)){
			//先开始循环冻结
			for(Map crmPaycontrol:crmPaycontrols){
						boolean canSkip = false;
						try{
							if(ListTool.isNotNullOrEmpty(deteCrmOrderIdsList)){
								for(String crmOrderId:deteCrmOrderIdsList){
									if(crmOrderId.equals(crmPaycontrol.get("crm_order_id").toString())){
										canSkip = true;
									}
								}
							}	
						if(canSkip){
							continue;
						}
						//查看是否有免息的逾期
						boolean has = crmOrderService.hasReductionPaycontrol(crmPaycontrol.get("crm_order_id").toString());
						if(has){
							continue;
						}
						List<Map> zzlEfpaycontrols = zzlEfPaycontrolService.getCurrentByCrmControl(crmPaycontrol.get("crm_order_id").toString(),crmPaycontrol.get("repayment_time").toString());
						if(ListTool.isNullOrEmpty(zzlEfpaycontrols)){continue;}
					
						//开始针对借款人进行还款
						//因为查询的是线上订单所以结算户通道必然是BOC
						JSONObject bocJson = new JSONObject();
						bocJson.put("accountId",crmPaycontrol.get("bank_account").toString());//电子账号
						bocJson.put("txAmount",NumberUtil.parseDouble(crmPaycontrol.get("frozenMoney")));//冻结金额
						bocJson.put("acqRes","直投逾期还款明细id:"+crmPaycontrol.get("id").toString());//保留域
						bocJson.put("sendAppName",StaticData.appName);//
						bocJson.put("productId",crmPaycontrol.get("order_prd_number").toString());
						bocJson.put("frzType","0");
						bocJson.put("signature",SignatureUtil.createSign());
						bocJson.put("remark","");//保留域
						//开始请求冻结接口
						JSONObject bocResultJson =  WebServiceUtil.sendPost(boc_url+"payProcess/bocBalanceFreeze",new Object[]{ bocJson});
						//冻结成功则直接算成功
						if(!bocResultJson.containsKey("responseCode") ||  !"1".equals(bocResultJson.getString("responseCode"))){
							deteCrmOrderIdsList.add(crmPaycontrol.get("crm_order_id").toString());
							logger.warn("直投Boc信贷订单冻结失败,明细id:"+crmPaycontrol.get("id")+",冻结金额:"+crmPaycontrol.get("frozenMoney"));
							continue;
						}
						//冻结成功的情况下
						logger.warn("直投BOC逾期信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",冻结金额:"+crmPaycontrol.get("frozenMoney"));
						//开始冻结明细
						JSONArray jsonArray = new JSONArray();
						boolean forzenFlag = zzlEfPaycontrolService.frozen(zzlEfpaycontrols);
						if(!forzenFlag){
							JSONObject smsJson = new JSONObject();
							logger.warn("直投BOC逾期信贷订单冻结成功但在进行zzl债权修改时失败,明细id:"+crmPaycontrol.get("id"));
							smsJson.put("project_number", crmPaycontrol.get("order_number"));
							smsJson.put("text","直投BOC逾期信贷订单冻结成功但在进行zzl债权修改时失败,明细id:"+crmPaycontrol.get("id"));
							SmsUtil.senErrorMsg(smsJson);
							continue;
						}
						for(Map zzlEfpaycontrol:zzlEfpaycontrols){
							ZZLEfPaycontrol  zzl = zzlEfPaycontrolService.findById(zzlEfpaycontrol.get("id").toString());
							JSONObject cuJson = new JSONObject();
							cuJson.put("orderId", "");
							cuJson.put("accountId",crmPaycontrol.get("bank_account"));			//扣款账户
							cuJson.put("txAmount", zzl.getSurplusPrincipal());	//交易金额
							cuJson.put("intAmount", 0d);										//交易利息
							cuJson.put("txFeeOut", ArithUtil.add(new Double[]{				//借款人手续费 = 剩余管理费+多出的管理费+剩余利息+多出来的利息
														zzl.getSurplusInterest(),
														zzl.getSurplusManagementAmt(),
														zzl.getMoreInterest(),
														zzl.getMoreManageAmt(),
														zzl.getOverCost(),
														zzl.getOverPenalty()
													})) ;
							cuJson.put("txFeeIn", "0");											//收款手续费
							cuJson.put("forAccountId", StaticData.risk);						//入款账号
							cuJson.put("productId", crmPaycontrol.get("order_prd_number").toString());	//产品号
							cuJson.put("authCode", zzlEfpaycontrol.get("auth_code"));
							cuJson.put("trdresv", "auto:"+zzlEfpaycontrol.get("id"));//第三方保留域
							if(cuJson.getString("authCode").endsWith("_needRemove")){
								cuJson.put("authCode", cuJson.getString("authCode").split(Pattern.quote("_needRemove"))[0]);
								cuJson.put("forAccountId", StaticData.redEnvelope);
							}
							jsonArray.add(cuJson);
						}
						//开始产生资金记录
						JSONObject checkMoneyJson = new JSONObject();
						checkMoneyJson.put("cardNo",crmPaycontrol.get("bank_account"));
						checkMoneyJson.put("cust_info_id",crmPaycontrol.get("cust_info_id"));			//客户id
						checkMoneyJson.put("money",ArithUtil.add(JsonUtil.getSum(jsonArray, "txAmount"),JsonUtil.getSum(jsonArray, "txFeeOut")));					// 金额
						checkMoneyJson.put("money_type","4"); 			// 1金账户2,银行卡3,现场交钱4,电子账户
						checkMoneyJson.put("operation_type","2"); 		//1入账2出账
						checkMoneyJson.put("status","0");				//1成功0失败
						checkMoneyJson.put("person_type","2");			//1,投资人2借款人3,邀请人
						checkMoneyJson.put("type","3");					//1,提前结清2提前收回3,逾期还款4,部分逾期,5正常还款	
						checkMoneyJson.put("crm_order_id", crmPaycontrol.get("crm_order_id"));
						String checkMoneyId = psCheckMoneyService.save(checkMoneyJson);
						
						JSONObject transMap = new JSONObject();
						transMap.put("acqRes","");//第三方保留域
						transMap.put("subPacks", jsonArray.toString());
						transMap.put("signature", SignatureUtil.createSign());
						transMap.put("sendAppName",StaticData.appName);//
						transMap.put("remark","");//保留域
						try{
							bocResultJson  = WebServiceUtil.sendPost(boc_url+"payProcess/batchRepay", new Object[]{transMap});
							//债权转让失败了
							if(!bocResultJson.containsKey("responseCode") || !"1".equals(bocResultJson.getString("responseCode"))){
								logger.warn("线上信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回失败!");
								JSONObject smsJson = new JSONObject();
								smsJson.put("project_number", crmPaycontrol.get("order_number"));
								smsJson.put("text","线上信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回失败!");
								SmsUtil.senErrorMsg(smsJson);
								continue;
							}
						}catch (Exception e) {
							e.printStackTrace();
							logger.warn(e.getMessage(),e);
							logger.warn("线上信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回失败!");
							JSONObject smsJson = new JSONObject();
							smsJson.put("project_number", crmPaycontrol.get("order_number"));
							smsJson.put("text","线上信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回失败!");
							SmsUtil.senErrorMsg(smsJson);
							continue;
						}
						//资金记录修改
						psCheckMoneyService.updateStatusById(checkMoneyId);
						logger.warn("直投BOC逾期信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回成功!");
				}catch (Exception e) {
				e.printStackTrace();
				logger.warn(e.getMessage(),e);
				logger.warn("直投BOC逾期信贷订单还款异常,明细id:"+crmPaycontrol.get("id")+",BOC返回成功!");
				}
			}
		}
		logger.warn("直投BOC逾期信贷订单的还款结束---");
	}
	/**
	 * 功能说明：执行直投Poc信贷还款
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
	public void toExecuteOverImmeCrmAutopayPoc(){
		logger.warn("直投POC逾期信贷订单的还款开始---");
		//获得当日的需要还款的crm信贷明细
		List<Map> crmPaycontrols =  crmOrderService.getOverImmePaycontrolsPoc();
		if(ListTool.isNotNullOrEmpty(crmPaycontrols)){
			//先开始循环冻结
			for(Map crmPaycontrol:crmPaycontrols){
				//查看是否有免息的逾期
				boolean has = crmOrderService.hasReductionPaycontrol(crmPaycontrol.get("crm_order_id").toString());
				if(has){
					continue;
				}
				JSONObject pocResult = transToZzl.payByPocDaikou(crmPaycontrol.get("cust_info_id").toString(),crmPaycontrol.get("frozenMoney").toString(),"3",crmPaycontrol.get("crm_order_id").toString());
				if("1".equals(pocResult.getString("responseCode"))){ //扣款成功直接更新
					//poc扣款成功,直接进行修改状态就可以了
					logger.warn("直投POC逾期信贷订单POC扣款成功,明细id:"+crmPaycontrol.get("id")+",POC返回成功!");
					crmOrderService.overRepayByPoc(crmPaycontrol.get("crm_order_id").toString());
				}else{
					logger.warn("直投POC逾期信贷订单POC扣款失败,明细id:"+crmPaycontrol.get("id")+",POC返回失败!");
				}
			}
		}
		logger.warn("直投POC逾期信贷订单的还款结束---");
	}
	/**
	 * 功能说明：执行定投BOC信贷还款
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
	public void toExecuteOverDeteCrmAutopayBoc(){
		List<String> deteCrmOrderIdsList = new ArrayList<String>();
		String boc_url=StaticData.bocUrl;
		logger.warn("定投BOC逾期信贷订单的还款开始---");
		//获得当日的需要还款的crm信贷明细
		List<Map> crmPaycontrolsBoc =  crmOrderService.getOverDetePaycontrolsBoc();
		if(ListTool.isNotNullOrEmpty(crmPaycontrolsBoc)){
			//先开始循环冻结
			for(Map crmPaycontrol:crmPaycontrolsBoc){
				boolean canSkip = false;
				try{
					if(ListTool.isNotNullOrEmpty(deteCrmOrderIdsList)){
						for(String crmOrderId:deteCrmOrderIdsList){
							if(crmOrderId.equals(crmPaycontrol.get("crm_order_id").toString())){
								canSkip = true;
							}
						}
					}	
				if(canSkip){
					continue;
				}	
				//查看是否有免息的逾期
				boolean has = crmOrderService.hasReductionPaycontrol(crmPaycontrol.get("crm_order_id").toString());
				if(has){
					continue;
				}
				List<Map> zzlEfpaycontrols = zzlEfPaycontrolService.getCurrentByCrmControl(crmPaycontrol.get("crm_order_id").toString(),crmPaycontrol.get("repayment_time").toString());
				if(ListTool.isNullOrEmpty(zzlEfpaycontrols)){continue;}
					//开始针对借款人进行还款
					//因为查询的是线上订单所以结算户通道必然是BOC
					JSONObject bocJson = new JSONObject();
					bocJson.put("accountId",crmPaycontrol.get("bank_account").toString());//电子账号
					bocJson.put("txAmount",NumberUtil.parseDouble(crmPaycontrol.get("frozenMoney")));//冻结金额
					bocJson.put("acqRes","定投BOC逾期还款明细id:"+crmPaycontrol.get("id").toString());//保留域
					bocJson.put("sendAppName",StaticData.appName);//
					bocJson.put("productId",crmPaycontrol.get("order_prd_number").toString());
					bocJson.put("frzType","0");
					bocJson.put("signature",SignatureUtil.createSign());
					bocJson.put("remark","");//保留域
					//开始请求冻结接口
					JSONObject bocResultJson =  WebServiceUtil.sendPost(boc_url+"payProcess/bocBalanceFreeze",new Object[]{bocJson});
					//冻结成功则直接算成功
					if(!bocResultJson.containsKey("responseCode") ||  !"1".equals(bocResultJson.getString("responseCode"))){
						//将订单id加入到缓存中
						deteCrmOrderIdsList.add(crmPaycontrol.get("crm_order_id").toString());
						logger.warn("定投Boc信贷订单冻结失败,明细id:"+crmPaycontrol.get("id")+",冻结金额:"+crmPaycontrol.get("frozenMoney"));
						continue;
					}
					//冻结成功的情况下就准备
					logger.warn("定投BOC逾期信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",冻结金额:"+crmPaycontrol.get("frozenMoney"));
					//开始冻结明细
					//开始还款
					boolean forzenFlag = zzlEfPaycontrolService.frozen(zzlEfpaycontrols);
					if(!forzenFlag){
						JSONObject smsJson = new JSONObject();
						smsJson.put("project_number", crmPaycontrol.get("order_prd_number"));
						logger.warn("定投BOC逾期信贷订单冻结成功但在进行zzl债权修改时失败,明细id:"+crmPaycontrol.get("id")+",冻结金额:"+crmPaycontrol.get("frozenMoney"));
						smsJson.put("text","定投BOC逾期信贷订单冻结成功但在进行zzl债权修改时失败,明细id:"+crmPaycontrol.get("id")+",冻结金额:"+crmPaycontrol.get("frozenMoney"));
						SmsUtil.senErrorMsg(smsJson);
						continue;
					}
					bocJson.clear();
					JSONArray jsonArray = new JSONArray();
					for(Map zzlEfpaycontrol:zzlEfpaycontrols){
						ZZLEfPaycontrol  zzl = zzlEfPaycontrolService.findById(zzlEfpaycontrol.get("id").toString());
						JSONObject cuJson = new JSONObject();
						cuJson.put("orderId", "");
						cuJson.put("accountId",crmPaycontrol.get("bank_account"));			//扣款账户
						cuJson.put("txAmount", zzl.getSurplusPrincipal());	//交易金额
						cuJson.put("intAmount", 0d);										//交易利息
						cuJson.put("txFeeOut", ArithUtil.add(new Double[]{				//借款人手续费 = 剩余管理费+多出的管理费+剩余利息+多出来的利息
													zzl.getSurplusInterest(),
													zzl.getSurplusManagementAmt(),
													zzl.getMoreInterest(),
													zzl.getMoreManageAmt(),
													zzl.getOverCost(),
													zzl.getOverPenalty()
												})) ;
						cuJson.put("txFeeIn", "0");											//收款手续费
						cuJson.put("forAccountId", StaticData.risk);						//入款账号
						cuJson.put("productId", crmPaycontrol.get("order_prd_number").toString());	//产品号
						cuJson.put("authCode", zzlEfpaycontrol.get("auth_code"));
						if(cuJson.getString("authCode").endsWith("_needRemove")){
							cuJson.put("authCode", cuJson.getString("authCode").split(Pattern.quote("_needRemove"))[0]);
							cuJson.put("forAccountId", StaticData.redEnvelope);
						}
						cuJson.put("trdresv", "auto:"+zzlEfpaycontrol.get("id"));//第三方保留域
						jsonArray.add(cuJson);
					}
					//开始产生资金记录
					JSONObject checkMoneyJson = new JSONObject();
					checkMoneyJson.put("cardNo",crmPaycontrol.get("bank_account"));
					checkMoneyJson.put("cust_info_id",crmPaycontrol.get("cust_info_id"));			//客户id
					checkMoneyJson.put("money",ArithUtil.add(JsonUtil.getSum(jsonArray, "txAmount"),JsonUtil.getSum(jsonArray, "txFeeOut")));					// 金额
					checkMoneyJson.put("money_type","4"); 			// 1金账户2,银行卡3,现场交钱4,电子账户
					checkMoneyJson.put("operation_type","2"); 		//1入账2出账
					checkMoneyJson.put("status","0");				//1成功0失败
					checkMoneyJson.put("person_type","2");			//1,投资人2借款人3,邀请人
					checkMoneyJson.put("type","3");					//1,提前结清2提前收回3,逾期还款4,部分逾期,5正常还款	
					checkMoneyJson.put("crm_order_id", crmPaycontrol.get("crm_order_id"));
					String checkMoneyId = psCheckMoneyService.save(checkMoneyJson);
					JSONObject transMap = new JSONObject();
					transMap.put("acqRes","");//第三方保留域
					transMap.put("subPacks", jsonArray.toString());
					transMap.put("signature", SignatureUtil.createSign());
					transMap.put("sendAppName",StaticData.appName);//
					transMap.put("remark","");//保留域
					try{
						bocResultJson  = WebServiceUtil.sendPost(boc_url+"payProcess/batchRepay",new Object[]{transMap});
						//债权转让失败了
						if(!bocResultJson.containsKey("responseCode") || !"1".equals(bocResultJson.getString("responseCode"))){
							logger.warn("线上信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回失败!");
							JSONObject smsJson = new JSONObject();
							smsJson.put("project_number", crmPaycontrol.get("order_number"));
							smsJson.put("text","线上信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回失败!");
							SmsUtil.senErrorMsg(smsJson);
							continue;
						}
					}catch (Exception e) {
						e.printStackTrace();
						logger.warn(e.getMessage(),e);
						logger.warn("线上信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回失败!");
						JSONObject smsJson = new JSONObject();
						smsJson.put("project_number", crmPaycontrol.get("order_number"));
						smsJson.put("text","线上信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回失败!");
						SmsUtil.senErrorMsg(smsJson);
						continue;
					}
					//资金记录修改
					psCheckMoneyService.updateStatusById(checkMoneyId);
					logger.warn("定投BOC逾期信贷订单冻结成功,明细id:"+crmPaycontrol.get("id")+",BOC返回成功!");
				}catch (Exception e) {
					e.printStackTrace();
					logger.warn(e.getMessage(),e);
					logger.warn("定投Boc逾期信贷订单还款异常,明细id:"+crmPaycontrol.get("id")+",BOC返回成功!");
				}
			}
		}
		logger.warn("定投BOC逾期信贷订单的还款结束---");
	}
	/**
	 * 功能说明：执行定投POC信贷还款
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
	public void toExecuteOverDeteCrmAutopayPoc(){
		logger.warn("定投POC逾期信贷订单的还款开始---");
		//开始线下poc还款
		//获得当日的需要还款的crm信贷明细-->已经通过订单分组
		List<Map> crmPaycontrolsPoc =  crmOrderService.getOverDetePaycontrolsPoc();
		if(ListTool.isNotNullOrEmpty(crmPaycontrolsPoc)){
			//先开始循环冻结
			for(Map crmPaycontrol:crmPaycontrolsPoc){
				//查看是否有免息的逾期
				boolean has = crmOrderService.hasReductionPaycontrol(crmPaycontrol.get("crm_order_id").toString());
				if(has){
					continue;
				}
				JSONObject pocResult = transToZzl.payByPocDaikou(crmPaycontrol.get("cust_info_id").toString(),crmPaycontrol.get("frozenMoney").toString(),"3",crmPaycontrol.get("crm_order_id").toString());
				if("1".equals(pocResult.getString("responseCode"))){ //扣款成功直接更新
					//poc扣款成功,直接进行修改状态就可以了
					logger.warn("定投POC逾期信贷订单POC扣款成功,明细id:"+crmPaycontrol.get("id")+",POC返回成功!");
					crmOrderService.overRepayByPoc(crmPaycontrol.get("crm_order_id").toString());
				}else{
					logger.warn("定投POC逾期信贷订单POC扣款失败,明细id:"+crmPaycontrol.get("id")+",POC返回失败!");
				}
			}
		}
		
		logger.warn("定投POC逾期信贷订单的还款结束---");
	}
	/**
	 * 功能说明：准备执行自动逾期操作
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
	public void toExecuteAutoOverCrmOrder(){
		logger.warn("自动逾期开始--");
		List<Map> crmPaycontrols  = crmOrderService.getNotClearPaycontrols();
		logger.warn("自动逾期条数"+crmPaycontrols.size());
		//针对每条明细进行逾期修改操作
		if(ListTool.isNotNullOrEmpty(crmPaycontrols)){
			for(Map crmPaycontrol:crmPaycontrols){
				crmOrderService.autoOver(crmPaycontrol);
			}
		}
		logger.warn("自动逾期结束--");
	}
	/**
	 * 功能说明：ef直投Poc的还款问题
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
	public  void ftpEfPayImmePoc (){
		List<Map> crmPaycontrols  = crmOrderService.getFtpEfPayImmePoc();
		
	}
	/**
	 * 功能说明：ef定投Poc的还款问题
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
	public  void ftpEfPaySetePoc (){
		List<Map> crmPaycontrols  = crmOrderService.getFtpEfPaySetePoc();
	}
	/**
	 * 功能说明：定投Poc的还款问题
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
	public  void ftpCrmPaySetePoc (){
		List<Map> crmPaycontrols  = crmOrderService.getFtpCrmPaySetePoc();
	}
	/**
	 * 功能说明：直投Poc的还款问题
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
	public  void ftpCrmPayImmePoc (){
		logger.warn("直投POC信贷订单的还款开始---");
		Map pocMap = new HashMap() ;
		pocMap.put("code", "AC01");
		List<String> list = new ArrayList<String>();
		//  商户号|业务代码|交易日期|当日序列号|明细数目|汇总金额
		//	明细序列|扣款人开户行行别|扣款人银行帐号|户名|金额|企业流水号|备注|手机号

		List<Map> crmPaycontrols  = crmOrderService.getFtpCrmPayImmePoc();
		//判断是否是空
		//long i = 1;
		if(ListTool.isNotNullOrEmpty(crmPaycontrols)){
			//准备拼装
			for(Map cpc:crmPaycontrols){
				try{
				String masSource = "";
				Map map = customerDao.getDaikouInfo(cpc.get("cust_info_id").toString());
				//String number = NumberFormat.formatLong6(i);
				//masSource+=number+"|";
				String bankno=map.get("bankno").toString();//总行代码
				masSource+=bankno+"|";
				String accntno=map.get("accntno").toString();//卡号
				masSource+=accntno+"|";
				String accntnm=map.get("accntnm").toString();//户名
				masSource+=accntnm+"|";
				double money = ArithUtil.add(new Double[]{Double.parseDouble(cpc.get("frozenMoney").toString())});
				masSource+=money+"|";
				String Serialno=DateUtil.getserialnoCheckUnique("");
				masSource+=Serialno+"|";
				String remark = "crm_paycontrol_id:"+cpc.get("id");
				masSource+=remark+"|";
				String mobile = cpc.get("cust_mobile")==null?"":cpc.get("cust_mobile").toString();
				masSource+=mobile;
				//i++;
				list.add(masSource);
				}catch (Exception e) {
					logger.warn(e.getMessage(),e);
					continue;
				}
			}
			pocMap.put("data", list);
			//准备请求fuyou
			pocTool.connectToPoc("ftpFileAC01", pocMap);
			
		}
		logger.warn("直投POC信贷订单的还款结束---");
	}
	/**
	 * 功能说明：定投Poc的还款问题
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
	public  void ftpCrmOverPayImmePoc (){
		List<Map> crmPaycontrols  = crmOrderService.getFtpCrmOverPayImmePoc();
	}
	/**
	 * 功能说明：定投Poc的还款问题
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
	public  void ftpCrmOverPaySetePoc (){
		List<Map> crmPaycontrols  = crmOrderService.getFtpCrmOverPaySetePoc();
	}
}

