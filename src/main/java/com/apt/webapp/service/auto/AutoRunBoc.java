package com.apt.webapp.service.auto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.jws.WebService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.apt.util.BusinessTool;
import com.apt.util.ChkUtil;
import com.apt.util.ListTool;
import com.apt.util.NumberFormat;
import com.apt.util.NumberUtil;
import com.apt.util.StaticData;
import com.apt.util.SysConfig;
import com.apt.util.WebServiceUtil;
import com.apt.util.arith.ArithUtil;
import com.apt.util.sms.SmsUtil;
import com.apt.webapp.dao.bg.ef.IBgEfOrderDao;
import com.apt.webapp.model.bg.ef.BgEfPaycontrol;
import com.apt.webapp.model.ef.EfPaycontrol;
import com.apt.webapp.service.bg.ef.IBgEfOrderService;
import com.apt.webapp.service.crm.ICrmOrderService;
import com.apt.webapp.service.ef.IEfFundRecordService;
import com.apt.webapp.service.ef.IEfOrderService;
import com.apt.webapp.service.ef.IEfPaycontrolService;

/**
 * 功能说明：v3的自动执行方法(仅限boc)
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
@WebService(serviceName = "autoRunBoc", endpointInterface = "com.apt.webapp.service.auto.AutoRunBoc")
public class AutoRunBoc {
	//日志
	private Logger logger = LoggerFactory.getLogger(AutoRunBoc.class);
	@Resource
	private IBgEfOrderService bgEfOrderService;
	@Resource
	private IEfFundRecordService efFundRecordService;
	@Resource
	private IEfOrderService efOrderService;
	@Resource
	private ICrmOrderService crmOrderService; 
	@Resource
	private IBgEfOrderDao bgEfOrderDao;
	@Resource
	private IEfPaycontrolService efPaycontrolService;
	/**
	 * 功能说明：定投执行线上红包户的钱,直接进行债权转让成功,到时候还款的时候使用红包户进行还款
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
	public void DeteEfBocTransferRedAccound(){}
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
	public void ImmeEfBocTransferRedAccound(){
		logger.warn("直投BOC理财红包户还款开始----");
		List<Map> efPaycontrols =  bgEfOrderService.getCurrentImmeControlsBocRedAccount();
		//踢出资产包相关数据
		crmOrderService.removePkgCrmOrders(efPaycontrols);
		if(ListTool.isNullOrEmpty(efPaycontrols)){logger.warn("直投BOC理财红包户还款结束----");return;}
		for(Map efPaycontrol:efPaycontrols){
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
					bec.setOperateType("0");
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
	 * 功能说明：Boc理财人进行债权转让
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
	public void ImmeEfBocTransfer(){
		logger.warn("直投BOC理财人自动还款开始----");
		//先处理红包户
		ImmeEfBocTransferRedAccound();
		//查找今天需要还款的理财明细并通过债券转让的方式还款
		//1.查找今日待还
		//2.发送红包
		//3.购买债券
		//4.更新操作  >> 借款人(订单明细记录) >> 投资人(订单明细记录) >>ZZL (订单明细记录)
		List<Map> efPaycontrols =  bgEfOrderService.getCurrentImmeControlsBoc();
		//踢出资产包相关数据
		crmOrderService.removePkgCrmOrders(efPaycontrols);
		//如果无数据则结束
		if(ListTool.isNullOrEmpty(efPaycontrols)){logger.warn("直投BOC理财人自动还款结束----");return;}
		//循环-->发送红包-->购买债券-->生成对应的记录
		List<Map> needTransList = new ArrayList<Map>(); //可以进行债权转让的列表
		for(Map efPaycontrol:efPaycontrols){
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
					bec.setOperateType("0");
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
//			System.out.println(bocJson.toString());
			JSONObject bocResult = WebServiceUtil.sendPost(SysConfig.getInstance().getProperty("pay.center.api")+"payProcess/redPayment", new Object[]{bocJson});
			//如果红包错误
			if("0".equals(bocResult.getString("responseCode"))){
				logger.warn("直投BOC理财明细在发放红包时失败!理财明细id:"+efPaycontrol.get("id")+";错误信息:"+bocResult.getString("data")+";银行返回错误码："+bocResult.getString("responseBankCode"));
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
				smsJson.put("text", "直投BOC理财明细在发放红包时失败!理财明细id:"+efPaycontrol.get("id"));
				SmsUtil.senErrorMsg(smsJson);
				continue;
			}
			logger.warn("直投BOC理财明细在发放红包时成功!理财明细id:"+efPaycontrol.get("id"));
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
					logger.warn("直投BOC理财明细在发放邀请人收益红包时失败!理财明细id:"+efPaycontrol.get("id")+";错误信息:"+bocResult.getString("data")+";银行返回错误码："+bocResult.getString("responseBankCode"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
					smsJson.put("text", "直投BOC理财明细在发放邀请人收益红包时失败!理财明细id:"+efPaycontrol.get("id"));
					SmsUtil.senErrorMsg(smsJson);
				}else{
					//资金记录修改
					logger.warn("直投BOC理财明细在发放邀请人收益红包时成功!理财明细id:"+efPaycontrol.get("id"));
				}
			}
			//本金0元直接算结束
			if( NumberUtil.parseDouble(efPaycontrol.get("surplus_principal"))<=0){
				efPaycontrol.put("tasteOrder","1");
				bgEfOrderService.normalRepayByModel(efPaycontrol);
				continue;
			}
			needTransList.add(efPaycontrol);
		}
		if(ListTool.isNotNullOrEmpty(needTransList)){ //这个列表来判断是否需要进行债权转让
			//查找买入方 债转签约流水号
			String contOrderId = bgEfOrderService.getBgAutoTransferAuth();
			for(int index=0;index<needTransList.size();index++){
				Map needTransMap = needTransList.get(index);
				//总共可转让金额  = 本金
				double tbalAce = Double.parseDouble(needTransMap.get("principal").toString());
				//开始购买债权
				JSONObject bocParam = new JSONObject();
				
				bocParam.put("channel", "000002");//交易渠道 000002(网页)
				bocParam.put("accountId", StaticData.risk);//买入方账号
				bocParam.put("txAmount", NumberUtil.parseDouble(needTransMap.get("surplus_principal")));//交易金额
				bocParam.put("txFee", needTransMap.get("surplus_management_amt"));//向卖出方收取的手续费
				bocParam.put("tsfAmount", NumberUtil.parseDouble(needTransMap.get("surplus_principal")));
				bocParam.put("forAccountId", needTransMap.get("bank_account"));//卖出方账号
				bocParam.put("orgOrderId", needTransMap.get("invest_seri_num"));//卖出方投标的原订单号 （或卖出方购买债权的原订单号）
				bocParam.put("orgTxAmount", NumberFormat.formatDouble(tbalAce));//卖出方投标的原交易金额 （或卖出方购买债权的原转让金额）
				bocParam.put("productId", needTransMap.get("order_prd_number"));//卖出方原标的号
				bocParam.put("contOrderId", contOrderId);//买入方自动债权转让签约订单号
//				bocJson.put("contOrderId", "00000920170519110326594721810B");//买入方自动债权转让签约订单号
				bocParam.put("trdresv", "3_1:"+needTransMap.get("id"));//第三方保留域
				bocParam.put("acqRes", "");
				bocParam.put("remark", "zhitou_ddid_"+needTransMap.get("id"));
				bocParam.put("sendAppName", "erpv3_auto2");
				bocParam.put("sysTarget", "ERP");
				JSONObject bocResult = WebServiceUtil.sendPost(SysConfig.getInstance().getProperty("pay.center.api")+"payProcess/transfer", new Object[]{bocParam});
				//如果红包错误
				if("0".equals(bocResult.getString("responseCode")))
				{
					logger.warn("直投BOC理财明细id:"+needTransMap.get("id")+"在进行债权转让时失败!"+";错误信息:"+bocResult.getString("data")+";银行返回错误码："+bocResult.getString("responseBankCode"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", "transferUploadService_UploadFiles");
					smsJson.put("text", "直投BOC理财明细id:"+needTransMap.get("id")+"在进行债权转让时失败!"+";错误信息:"+bocResult.getString("data")+";银行返回错误码："+bocResult.getString("responseBankCode"));
					SmsUtil.senErrorMsg(smsJson);
				}
				else
				{
					logger.warn("直投BOC理财明细在进行债权转让时成功!");
					bocParam.put("investAuthCode", bocResult.get("investAuthCode"));
					bocParam.put("seriNo", bocResult.get("seriNo"));
					bocParam.put("empId", needTransMap.get("id"));
					this.run(bocParam);
				}
			} //需要进行债权转让的循环结束
		}//非空判断结束
		logger.warn("直投BOC理财人自动还款结束----");
	}
	/**
	 * 功能说明：Boc理财人进行债权转让
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
	public void DeteEfBocTransfer(){
		logger.warn("定投BOC理财人自动还款开始----");
		//先处理红包
		DeteEfBocTransferRedAccound();
		//查找今天需要还款的理财明细并通过债券转让的方式还款
		//1.查找今日待还
		//2.发送红包
		//3.购买债券
		//4.更新操作  >> 借款人(订单明细记录) >> 投资人(订单明细记录) >>ZZL (订单明细记录)
		List<Map> efPaycontrols =  bgEfOrderService.getCurrentDeteControlsBoc();
		//踢出资产包相关数据
		crmOrderService.removePkgCrmOrders(efPaycontrols);
		if(ListTool.isNotNullOrEmpty(efPaycontrols)){ //这个列表来判断是否需要进行债权转让
			//查找买入方 债转签约流水号
			String contOrderId = bgEfOrderService.getBgAutoTransferAuth();
			for(int index=0;index<efPaycontrols.size();index++){
				Map needTransMap = efPaycontrols.get(index);
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
						bec.setOperateType("0");
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
				//总共可转让金额  = 本金
				double tbalAce = Double.parseDouble(needTransMap.get("principal").toString());
				//开始购买债权
				JSONObject bocParam = new JSONObject();
				bocParam.put("channel", "000002");//买入方账号
				bocParam.put("accountId", StaticData.risk);//买入方账号
				bocParam.put("txAmount", NumberUtil.parseDouble(needTransMap.get("surplus_principal")));//交易金额
				bocParam.put("txFee", 0);//向卖出方收取的手续费
				bocParam.put("tsfAmount", NumberUtil.parseDouble(needTransMap.get("surplus_principal")));
				bocParam.put("forAccountId", needTransMap.get("bank_account"));//卖出方账号
				bocParam.put("orgOrderId", needTransMap.get("invest_seri_num"));//卖出方投标的原订单号 （或卖出方购买债权的原订单号）
				bocParam.put("orgTxAmount", NumberFormat.formatDouble(tbalAce));//卖出方投标的原交易金额 （或卖出方购买债权的原转让金额）
				bocParam.put("productId", needTransMap.get("order_prd_number"));//卖出方原标的号
				bocParam.put("contOrderId", contOrderId);//买入方自动债权转让签约订单号
//				bocJson.put("contOrderId", "00000920170519110326594721810B");//买入方自动债权转让签约订单号
				bocParam.put("trdresv", "3_1:"+needTransMap.get("id"));//第三方保留域
				bocParam.put("acqRes", "");
				bocParam.put("remark", "dingtou_ddid_"+needTransMap.get("id"));
				bocParam.put("sendAppName", "erpv3_auto2");
				bocParam.put("sysTarget", "ERP");
				JSONObject bocResult = WebServiceUtil.sendPost(SysConfig.getInstance().getProperty("pay.center.api")+"payProcess/transfer", new Object[]{bocParam});
				//如果红包错误
				if("0".equals(bocResult.getString("responseCode")))
				{
					logger.warn("定投BOC理财明细在进行id:"+needTransMap.get("id")+"债权转让时失败!"+";错误信息:"+bocResult.getString("data")+";银行返回错误码："+bocResult.getString("responseBankCode"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", "transferUploadService_UploadFiles");
					smsJson.put("text", "定投BOC理财明细id:"+needTransMap.get("id")+"在进行债权转让时失败!"+";错误信息:"+bocResult.getString("data")+";银行返回错误码："+bocResult.getString("responseBankCode"));
					SmsUtil.senErrorMsg(smsJson);
				}
				else
				{
					logger.warn("定投BOC理财明细id:"+needTransMap.get("id")+"在进行债权转让时成功!");
					bocParam.put("investAuthCode", bocResult.get("investAuthCode"));
					bocParam.put("seriNo", bocResult.get("seriNo"));
					bocParam.put("empId", needTransMap.get("id"));
					this.run(bocParam);
				}
			} //需要进行债权转让的循环结束
		}//非空判断结束
		logger.warn("定投BOC理财人自动还款结束----");
	}
	
	/**
	 * 功能说明：更新理财订单明细
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：下午2:57:02
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void run(JSONObject baseJson)
	{
		logger.warn("本次债权转让返回的成功明细值:"+baseJson.toString());
		//如果有成功的则进行更新
		logger.warn("数据:"+baseJson);
		try
		{
			//开始进行明细转移和状态修改和记录
			if(ChkUtil.isEmpty(baseJson.getString("empId")))
			{ //必须有该标识
				return;
			}
			Map efPaycontrol = bgEfOrderDao.getCurrentEfBocControls(baseJson.getString("empId"));
			if(efPaycontrol == null)
			{
				return;
			}
			if(efPaycontrol!=null && efPaycontrol.containsKey("pay_status") && "1".equals(efPaycontrol.get("pay_status").toString()))
			{ //不为空存在还款状态并且已经是还款结束状态
				return;
			}
			efPaycontrol.put("clearing_channel","2");
			efPaycontrol.put("onLine",efPaycontrol.get("online_type").toString());
			efPaycontrol.put("authcode",baseJson.getString("investAuthCode"));
			efPaycontrol.put("seriNo",baseJson.getString("seriNo"));
			efPaycontrol.put("investment_model",efPaycontrol.get("investment_model").toString());
			bgEfOrderService.normalRepayByModel(efPaycontrol);
			//开始进行可用资金存储
			if("2".equals(efPaycontrol.get("investment_model")))
			{ //定投
				//不是专业投资人才
				if(BusinessTool.isNotProfessionalInvestor(efPaycontrol.get("investor_type"))){
					efOrderService.lockRowForEforderAuth(efPaycontrol); //处理
				}
				//如果是专业的投资人则需要减去本金
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
		}catch (Exception e)
		{ //如果修改异常了
			logger.warn(e.getMessage(),e);
			logger.warn("理财明细债权转让成功,但是修改数据时失败!明细id:"+baseJson.getString("empId"));
			JSONObject smsJson = new JSONObject();
			smsJson.put("project_number", baseJson.getString("thredPriKey"));
			smsJson.put("text","理财明细债权转让成功,但是修改数据时失败!明细id:"+baseJson.getString("empId"));
			SmsUtil.senErrorMsg(smsJson);
		}
	}
}