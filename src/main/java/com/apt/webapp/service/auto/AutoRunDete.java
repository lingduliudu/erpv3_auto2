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
import com.apt.webapp.model.bg.ef.BgEfDeteRecord;
import com.apt.webapp.model.bg.ef.BgEfPaycontrol;
import com.apt.webapp.model.crm.CrmOrder;
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
public class AutoRunDete {
	//日志
	private Logger logger = LoggerFactory.getLogger(AutoRunDete.class);
	@Resource
	private ICrmOrderService crmOrderService;
	@Resource
	private IBgEfOrderDao bgEfOrderDao;
	@Resource
	private SolveMoneyDete solveMoneyDete;
	@Resource
	private IPsCheckMoneyService psCheckMoneyService;
	/**
	 * 功能说明：查找信贷手动还款的
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
	public void test(){
		System.out.println("12");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void toExecuteEfDeteRecord(){
		//1.查询未处理数据
		//2.处理理财人明细
		//3.判断是否可以直接进行借款人还款
		//4.更新
		logger.warn("BOC手动信贷还款数据处理开始");
		List<Map> dataList = crmOrderService.findEfDeteData();
		//踢出资产包相关数据
		crmOrderService.removePkgCrmOrders(dataList);
		if(ListTool.isNullOrEmpty(dataList)){
			logger.warn("BOC手动信贷还款数据处理结束");
			return;
		}
		//开始处理
		for(Map baseMap:dataList){
			try{
				String type =baseMap.get("type").toString();
				baseMap.put("crmOrderId", baseMap.get("crm_order_id").toString());
				baseMap.put("serialNo", baseMap.get("serino").toString());
				baseMap.put("empId", baseMap.get("empid").toString());
				if("2".equals(type)||"3".equals(type)){  //2提前结清 3提前收回
					advanceData(baseMap);
				}
				if("1".equals(type)){
					normalData(baseMap);
				}
				//赎楼带特殊
			//	if("0".equals(type)){
			//		A007Data(baseMap);
			//	}
				BgEfDeteRecord bedr = (BgEfDeteRecord) bgEfOrderDao.findById(BgEfDeteRecord.class, baseMap.get("id").toString());
				bedr.setStatus("1");
				bedr.setUpdateTime(DateUtil.getCurrentTime());
				bgEfOrderDao.update(bedr);
				
			}catch (Exception e) {
				e.printStackTrace();
				logger.warn(e.getMessage(),e);
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", "erpv3_auto2");
				smsJson.put("text","BOC手动信贷还款数据处理异常!id:"+baseMap.get("id"));
				SmsUtil.senErrorMsgByZhiyun(smsJson);
			}
		}
		
		logger.warn("BOC手动信贷还款数据处理结束");
		
	}
	
	/**
	 * 功能说明：查找信贷手动还款的提前还款
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
	public void advanceData(Map baseMap){
		//开始处理直投投资人,开始处理定投投资人
		//1查找直投和定投的数据 
		//2如果是直投的则进行直投的还款
		//3如果是定投的则进行定投的还款
		//4如果直投定投单子都是空的 则直接进行还款操作
		JSONObject immeJson  = solveMoneyDete.toExecuteAdvanceImme(baseMap);
		JSONObject deteJson  = solveMoneyDete.toExecuteAdvanceDete(baseMap);
		advanceCrmOrder(baseMap.get("crmOrderId").toString(),baseMap.get("type").toString());
	}
	/**
	 * 功能说明：查找信贷手动还款的正常还款
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
	public void normalData(Map baseMap){
		//开始处理直投投资人,开始处理定投投资人
		CrmPaycontrol cpc;
		try {
			cpc = (CrmPaycontrol) bgEfOrderDao.findById(CrmPaycontrol.class, baseMap.get("crm_paycontrol_id").toString());
			baseMap.put("payTime", cpc.getRepaymentTime().substring(0,10));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//1查找直投和定投的数据 
		//2如果是直投的则进行直投的还款
		//3如果是定投的则进行定投的还款
		//4如果直投定投单子都是空的 则直接进行还款操作
		JSONObject immeJson  = solveMoneyDete.toExecuteNormalImme(baseMap);
		JSONObject deteJson  = solveMoneyDete.toExecuteNormalDete(baseMap);
		solveMoneyDete.toExecuteNormalCrmBoc(JSONObject.fromObject(baseMap));
	}
	/**
	 * 功能说明：查找赎楼手动还款的正常还款
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
	public void A007Data(Map baseMap){
		//开始处理直投投资人,开始处理定投投资人
		//1查找直投和定投的数据 
		//2如果是直投的则进行直投的还款
		//3如果是定投的则进行定投的还款
		//4如果直投定投单子都是空的 则直接进行还款操作
		solveMoneyDete.toExecuteA007(baseMap);
		toExecuteA007CrmBoc(JSONObject.fromObject(baseMap));
		
		
	}
	
	
	
	
	public JSONObject advanceCrmOrder(String crmOrderId,String type) {
		JSONObject paramJson = new JSONObject();
		CrmOrder crmOrder=null;
		try {
			crmOrder = (CrmOrder) bgEfOrderDao.findById(CrmOrder.class,crmOrderId);
			paramJson.put("order_prd_number", crmOrder.getOrderPrdNumber());
			String custInfoSql = "SELECT * from bg_cust_info where id='"+crmOrder.getCustInfoId()+"'";
			Map custInfo=bgEfOrderDao.queryBySqlReturnMapList(custInfoSql).get(0);
			paramJson.put("bank_account", custInfo.get("bank_account"));
			paramJson.put("jieFyaccount", custInfo.get("fy_account"));
			paramJson.put("cust_info_id", custInfo.get("id"));
			String crmRecoderSql="select emp_id from crm_payrecoder where paycontrol_id='-1' and crm_order_id='"+crmOrder.getId()+"' order by create_time desc";
			Map crmRecoderInfo=bgEfOrderDao.queryBySqlReturnMapList(crmRecoderSql).get(0);
			paramJson.put("empId", crmRecoderInfo.get("emp_id"));
			paramJson.put("crmOrderId", crmOrder.getId());
		} catch (Exception e) {
			logger.warn(e.getMessage(),e);
			paramJson.put("info", "系统异常!");
			paramJson.put("status", "0");
			return paramJson;
		}
		//获得违约金和退回的服务费 -- >>投资人的暂时不知道怎么处理
		JSONObject advanceInfo = getAdvanceInfo(crmOrder.getId());
		paramJson.putAll(advanceInfo);
		paramJson.put("type", type);
		//准备赋值给params
		//
		String  lastCprSql = 
				
				"SELECT " +
						"	bci.bank_account bank_account, " +
						"	bci.id cust_info_id, " +
						"	cpr.should_accrual totalAccrual, " +
						"	cpr.manage_fee totalManage, " +
						"	cpr.should_interest totalInterest, " +
						"	cpr.overdue_violate_money totalOverdueViolate, " +
						"	cpr.prepayment_violate_money totalAdvanceMoney, " +
						"	cpr.should_capital totalCapital, " +
						"	co.contract_money capitalMoney " +
						"FROM " +
						"	crm_payrecoder cpr, " +
						"	crm_order co, " +
						"	bg_cust_info bci " +
						"WHERE " +
						"	co.id = cpr.crm_order_id " +
						"and bci.id=co.cust_info_id " +
						"AND cpr.crm_order_id = '"+crmOrderId+"' " +
						"AND cpr.paycontrol_id = '-1' "; 
		paramJson.putAll(bgEfOrderDao.queryBySqlReturnMapList(lastCprSql).get(0));
		return toExecuteAdvanceBocCrm(paramJson);
	}
	/**
	 * 功能说明：计算提前结清数据
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
	public JSONObject getAdvanceInfo(String crmOrderId){
		JSONObject resultJson = new JSONObject();
		//提前结清的违约金
		resultJson.put("companyA", 0d);
		resultJson.put("companyB", 0d);
		resultJson.put("companyC", 0d);
		resultJson.put("companyD", 0d);
		resultJson.put("touAdvanceMoney", 0d);
		//开始计算投资人的提前还款违约金
		String isLineSql ="SELECT investment_model,clearing_channel,order_type from crm_order where id='"+crmOrderId+"' ";
		Map isLineMap=bgEfOrderDao.queryBySqlToLower(isLineSql).get(0);
		String investment_model = "";
		if(isLineMap.get("investment_model")!=null){
			investment_model = isLineMap.get("investment_model").toString();
		}
		resultJson.put("clearing_channel", isLineMap.get("clearing_channel"));
		String orderType="";
		if(isLineMap.get("order_type")!=null){
			orderType = isLineMap.get("order_type").toString();
		}
		if("1".equals(investment_model)){
			String touSql = 
							"SELECT  "+
							"co.contract_money*bedp.pre_manage_rate/100 touAdvanceMoney "+
							"from "+
							"crm_order co, "+
							"bg_ef_product_detail bedp "+
							"where co.ef_prd_detail_id = bedp.id and co.id='"+crmOrderId+"'";
			resultJson.putAll(bgEfOrderDao.queryBySqlReturnMapList(touSql).get(0));
		}
		//退回的服务费
		resultJson.put("companyAReturn", 0d);
		resultJson.put("companyBReturn", 0d);
		resultJson.put("companyCReturn", 0d);
		resultJson.put("companyDReturn", 0d);
		String sql = 
					"SELECT  "+
					"co.contract_money, "+
					"co.credit_money, "+
					"IFNULL(co.company_a_server_fund+co.company_b_server_fund+co.company_c_server_fund+co.company_d_server_fund,0) x, "+
					"pwpd.contract_rate, "+
					"pwpd.multiple_rate, "+
					"pwpd.periods, "+
					"pwpd.staging_services_rate, "+
					"pwpd.bail_rate, "+
					"pwpd.duetime_type, "+
					"pwpr.* "+
					"from  "+
					"crm_order co, "+
					"pro_working_product_detail pwpd, "+
					"pro_working_prd_company_rate pwpr "+
					"where "+
					"co.pro_detail_id = pwpd.id AND "+
					"co.id='"+crmOrderId+"' AND "+
					"pwpd.id= pwpr.detail_id " ;
		List<Map> list = bgEfOrderDao.queryBySqlToLower(sql);
		Map map  = list.get(0);
		//前期一次性服务费>> 直接从四家公司之和即可
		double serviceMoney = Double.parseDouble(map.get("x").toString());
		String totalPeriodsSql = "SELECT * from crm_paycontrol where crm_order_id='"+crmOrderId+"' ";
		int totalPeriods = bgEfOrderDao.queryBySqlReturnMapList(totalPeriodsSql).size();
		//获得最后一期的时间
		String lastSql = "SELECT loan_time from crm_order where id='"+crmOrderId+"'  ";
		String lastTime = bgEfOrderDao.queryBySqlReturnMapList(lastSql).get(0).get("loan_time").toString();
		//总期数
		int returnPeriods = 0;
		//查看剩余的待还总期数
		String countSql = "SELECT count(*) count from crm_paycontrol where crm_order_id='"+crmOrderId+"' and status in (0)";
		List<Map> countList = bgEfOrderDao.queryBySqlReturnMapList(countSql);
		returnPeriods = Integer.parseInt(countList.get(0).get("count").toString())-1;
		//开始检测当天的状态
		String checkSql  = "SELECT status from crm_paycontrol where crm_order_id='"+crmOrderId+"' and repayment_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%';";
		List<Map> checkList  =  bgEfOrderDao.queryBySqlToLower(checkSql);
		if(ListTool.isNotNullOrEmpty(checkList)){
			Map checkMap = checkList.get(0);
			if("0".equals(checkMap.get("status").toString())){ //当天的状态是待还
				returnPeriods--;
			}
		}
		if(returnPeriods <= 0){
			returnPeriods=0;
		}
		//相差天数  //当天所在的期数
		long days = 1; //默认第一期
		String currentDueTimeSql = "SELECT * from crm_paycontrol  where crm_order_id= '"+crmOrderId+"' and repayment_time <='"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' ORDER BY repayment_duetime desc limit 1 ";
		List<Map> currentDueTimeList = bgEfOrderDao.queryBySqlReturnMapList(currentDueTimeSql);
		if(ListTool.isNotNullOrEmpty(currentDueTimeList)){ //只有不为空的情况下
			days = Long.parseLong(currentDueTimeList.get(0).get("repayment_duetime").toString());
			//判断是否需要继续加一天
			if(currentDueTimeList.get(0).get("repayment_time").toString().substring(0, 10).compareTo(DateUtil.getCurrentTime(DateUtil.STYLE_2))<0){
				days++;
			}
		}
		if(days>totalPeriods){ //如果大与总期数则需要直接赋值为总期数
			days = totalPeriods;
		}
		
		if(ListTool.isNotNullOrEmpty(list)){
			for(Map forMap:list){
				//计算退回服务费 (前期一次性服务费*占比*(退回的期数/总期数))
				if("A".equals(forMap.get("identify_company"))){ //A公司的退回服务费和提前结清违约金
					//服务费
					resultJson.put("companyAReturn",
							ArithUtil.mul(ArithUtil.mul(serviceMoney,Double.parseDouble(forMap.get("service_fee_rate").toString())), (returnPeriods*1d/totalPeriods/100d)));
					//违约金
					//1.获得真正的利率
					//2.计算违约金
					double rate = getAdvanceClearRate(forMap, days);
					resultJson.put("companyA",NumberFormat.format( 
								ArithUtil.mul(Double.parseDouble(map.get("contract_money").toString()), rate))
							);
				}
				if("B".equals(forMap.get("identify_company"))){//B公司的退回服务费和提前结清违约金
					resultJson.put("companyBReturn",
							ArithUtil.mul(ArithUtil.mul(serviceMoney,Double.parseDouble(forMap.get("service_fee_rate").toString())), (returnPeriods*1d/totalPeriods/100d)));
					//违约金
					//1.获得真正的利率
					//2.计算违约金
					double rate = getAdvanceClearRate(forMap, days);
					resultJson.put("companyB",NumberFormat.format( 
							ArithUtil.mul(Double.parseDouble(map.get("contract_money").toString()), rate)));
				}
				if("C".equals(forMap.get("identify_company"))){//C公司的退回服务费和提前结清违约金
					resultJson.put("companyCReturn",
							ArithUtil.mul(ArithUtil.mul(serviceMoney,Double.parseDouble(forMap.get("service_fee_rate").toString())), (returnPeriods*1d/totalPeriods/100d)));
					//违约金
					//1.获得真正的利率
					//2.计算违约金
					double rate = getAdvanceClearRate(forMap, days);
					resultJson.put("companyC",NumberFormat.format(ArithUtil.mul(Double.parseDouble(map.get("contract_money").toString()), rate)));
				}
				if("D".equals(forMap.get("identify_company"))){//D公司的退回服务费和提前结清违约金
					resultJson.put("companyDReturn",
							ArithUtil.mul(ArithUtil.mul(serviceMoney,Double.parseDouble(forMap.get("service_fee_rate").toString())), (returnPeriods*1d/totalPeriods/100d)));
					//违约金
					//1.获得真正的利率
					//2.计算违约金
					double rate = getAdvanceClearRate(forMap, days);
					resultJson.put("companyD",NumberFormat.format( 
							ArithUtil.mul(Double.parseDouble(map.get("contract_money").toString()), rate)));
				}
			}
		}
		//总的退回服务费
		double companyAReturn = resultJson.getDouble("companyBReturn");
		double companyBReturn = resultJson.getDouble("companyAReturn");
		resultJson.put("companyAReturn", companyAReturn);
		resultJson.put("companyBReturn", companyBReturn);
		JSONObject removeJson =new JSONObject();
		removeJson.put("clearing_channel", "");
		JsonUtil.format(resultJson,removeJson);
		resultJson.put("returnServiceMoney", ArithUtil.add(new Double[]{
				resultJson.getDouble("companyAReturn"),
				resultJson.getDouble("companyBReturn"),
				resultJson.getDouble("companyCReturn"),
				resultJson.getDouble("companyDReturn")
		}));
		return resultJson;
	}
	/**
	 * 功能说明：获得对应的提前结清违约金的利率
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
	private double getAdvanceClearRate(Map map,long days){
		//小于或者等于初始天数
		if(days <= Long.parseLong(map.get("violate_initial_day").toString())){
			return Double.parseDouble(map.get("violate_initial_rate").toString())/100d;
		}
		if(days >= Long.parseLong(map.get("violate_transcend_begin_day").toString()) &&
		   days <= Long.parseLong(map.get("violate_transcend_end_day").toString())){
			return Double.parseDouble(map.get("violate_transcend_rate").toString())/100d;
		}
		return Double.parseDouble(map.get("violate_by_rate").toString())/100d;
	}
	/**
	 * 获得申请的金额
	 * 功能说明：该方法实现的功能			
	 * @author yuanhao
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2016-1-12
	 * 修改内容：
	 * 修改注意点：
	 */
	public JSONObject getPayApplyInfoFromType(String crmOrderId,String type){
		JSONObject resultJson = new JSONObject();
		String sql  = 	"SELECT " +
						"INITIAL_PLATFORM_MANAGE_MONEY manage, "+
						"INITIAL_INTEREST interest, "+
						"INITIAL_CONTRACT_PENALTY overDueViolateMoney, "+
						"INITIAL_OVERDUE_INTEREST overCost, "+
						"INITIAL_CAPITAL capital, "+
						"FINAL_PLATFORM_MANAGE_MONEY remainManage, "+
						"FINAL_INTEREST remainInterest, "+
						"FINAL_CONTRACT_PENALTY remainOverDueViolateMoney, "+
						"FINAL_OVERDUE_INTEREST remainOverCost, "+
						"INITIAL_OVERDUE_INTEREST+INITIAL_CONTRACT_PENALTY beforeOverMoney, "+
						"FINAL_OVERDUE_INTEREST+FINAL_CONTRACT_PENALTY remainOverMoney, "+
						"FINAL_OVERDUE_INTEREST+FINAL_OVERDUE_INTEREST_CURRENT+FINAL_CONTRACT_PENALTY+FINAL_CONTRACT_PENALTY_CURRENT+FINAL_PLATFORM_MANAGE_MONEY+FINAL_PLATFORM_MANAGE_MONEY_CURRENT+FINAL_INTEREST+FINAL_INTEREST_CURRENT+FINAL_CAPITAL+ifnull(FINAL_ADVANCE_PENALTY,0) totalMoney, "+
						"FINAL_CAPITAL remainCapital, "+
						"FINAL_CAPITAL capitalMoney, "+
						"THE_SCENE_MONEY liveMoney, "+
						"BASE_MONEY baseMoney, "+
						"FINAL_INTEREST+FINAL_INTEREST_CURRENT remainAllInterest, "+
						"INITIAL_INTEREST+INITIAL_INTEREST_CURRENT beforeAllInterest, "+
						"FINAL_CONTRACT_PENALTY+FINAL_CONTRACT_PENALTY_CURRENT remainAllOverDueViolate, "+
						"INITIAL_CONTRACT_PENALTY+INITIAL_CONTRACT_PENALTY_CURRENT beforeAllOverDueViolate, "+
						"FINAL_OVERDUE_INTEREST+FINAL_OVERDUE_INTEREST_CURRENT remainAllOverCost, "+
						"INITIAL_OVERDUE_INTEREST+INITIAL_OVERDUE_INTEREST_CURRENT beforeAllOverCost, "+
						"FINAL_PLATFORM_MANAGE_MONEY+FINAL_PLATFORM_MANAGE_MONEY_CURRENT remainAllManage, "+
						"INITIAL_PLATFORM_MANAGE_MONEY+INITIAL_PLATFORM_MANAGE_MONEY_CURRENT beforeAllManage, "+
						"adm.* , "+
						"BANKCARD_MONEY cardMoney "+
						"from acc_derate_manage adm where CRM_ORDER_ID='"+crmOrderId+"' and APPLY_TYPE ='"+type+"' and STATUS !=0  and CEO_APPROVE_STATUS =1 ";
		List<Map> list;
		resultJson.put("has", "0"); //默认不含有 后期添加处理
		resultJson.put("totalSize", "0");
		try {
			list = bgEfOrderDao.queryBySqlReturnMapList(sql);
			if(ListTool.isNotNullOrEmpty(list)){
				resultJson.put("has", "1"); //默认不含有 后期添加处理
				resultJson.put("totalSize", list.size());
				resultJson.putAll(list.get(0));
				resultJson.put("INITIAL_RETURN_SERVICE", chkValue(resultJson, "INITIAL_RETURN_SERVICE"));
				resultJson.put("FINAL_RETURN_SERVICE", chkValue(resultJson, "FINAL_RETURN_SERVICE"));
				resultJson.put("INITIAL_SURPLUS_INTEREST", chkValue(resultJson, "INITIAL_SURPLUS_INTEREST"));
				resultJson.put("FINAL_SURPLUS_INTEREST", chkValue(resultJson, "FINAL_SURPLUS_INTEREST"));
				resultJson.put("INITIAL_ADVANCE_PENALTY", chkValue(resultJson, "INITIAL_ADVANCE_PENALTY"));
				resultJson.put("FINAL_ADVANCE_PENALTY", chkValue(resultJson, "FINAL_ADVANCE_PENALTY"));
				resultJson.put("DERATE_RETURN_SERVICE", chkValue(resultJson, "DERATE_RETURN_SERVICE"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultJson;
	}
	private double chkValue(JSONObject parseJson,String arg){
		if (parseJson.containsKey(arg)) {
			return parseJson.getDouble(arg);
		}else{
			return 0;
		}
	}
	/**
	 * 功能说明：借款人线下还款
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
	public JSONObject toExecuteAdvanceBocCrm(JSONObject paramJson) {
		if("1".equals(paramJson.getString("type"))){paramJson.put("checkMoneyType", "5");}
		if("2".equals(paramJson.getString("type"))){paramJson.put("checkMoneyType", "1");}
		if("3".equals(paramJson.getString("type"))){paramJson.put("checkMoneyType", "2");}
		//需要将zzl的逾期的也改成状态3/
		changeZZLStatus(paramJson.getString("crmOrderId"));
		JSONObject resultJson = new JSONObject();
		resultJson.put("info","");
		resultJson.put("status", "1");
		String info="";
		String cprSql = "SELECT * from crm_payrecoder where paycontrol_id='-1' and crm_order_id ='"+paramJson.getString("crmOrderId")+"' ";
		List<Map> cprList = bgEfOrderDao.queryBySqlReturnMapList(cprSql);
		double returnFee = 0d;
		if(ListTool.isNotNullOrEmpty(cprList)){
			if(cprList.get(0).get("remain_fee")!=null){
				returnFee = NumberFormat.format(Double.parseDouble(cprList.get(0).get("remain_fee").toString()));
			}
		}
		paramJson.put("returnFee",returnFee);
		//开始进行boc的退回服务费的判断
		boolean returnServiceFlag = true;
		String flagSql = "SELECT *from  sys_param where code='erpv3.service.repayment.returnservice.boc.enable' ";
		List<Map> flagList  = bgEfOrderDao.queryBySqlReturnMapList(flagSql);
		if(ListTool.isNotNullOrEmpty(flagList)){
			String flagString = flagList.get(0).get("value").toString();
			if(flagString.equals("1")){ //说明是需要减免的
				returnServiceFlag = true;
			}
			if(flagString.equals("0")){ //说明不需要减免
				returnServiceFlag = false;
			}
		}
		if(returnServiceFlag){ //需要进行减免的情况下本金需要重新赋值
			paramJson = toResetAdvaneMoney(paramJson);
		}
		
		String sql = "SELECT * from zzl_ef_paycontrol where crm_order_id='"+paramJson.getString("crmOrderId")+"' and pay_status='3' ";
		List<Map> zzlEfpaycontrols = bgEfOrderDao.queryBySqlReturnMapList(sql);
		if(ListTool.isNullOrEmpty(zzlEfpaycontrols)){
			resultJson.put("info","未发现债权转让的记录!");
			resultJson.put("status", "0");
			return resultJson;
		}
		JSONArray jsonArray = new JSONArray();
		JSONObject clearJson = new JSONObject();
		clearJson.put("totalAccrual", paramJson.getString("totalAccrual"));
		clearJson.put("totalManage", paramJson.getString("totalManage"));
		clearJson.put("totalInterest", paramJson.getString("totalInterest"));
		clearJson.put("totalOverdueViolate", paramJson.getString("totalOverdueViolate"));
		clearJson.put("totalAdvanceMoney", paramJson.getString("totalAdvanceMoney"));
		clearJson.put("totalCapital", paramJson.getString("totalCapital"));
		clearJson.put("capitalMoney", paramJson.getString("capitalMoney"));
		boolean forzenFlag = frozenForAdvanceClear(zzlEfpaycontrols, clearJson);
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
			try {
				zzl = (ZZLEfPaycontrol) bgEfOrderDao.findById(ZZLEfPaycontrol.class,zzlEfpaycontrol.get("id").toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			JSONObject cuJson = new JSONObject();
			cuJson.put("orderId", "");
			cuJson.put("accountId",paramJson.getString("bank_account"));	//扣款账户
			cuJson.put("txAmount", zzl.getTempPrincipal());					//本金
			cuJson.put("intAmount", "0");										//利息
			cuJson.put("forAccountId",StaticData.risk);							//入账用户(风险户)
			//如果投资人是红包户
			if (StaticData.redCustInfoId.equals(zzl.getCustInfoId())) {
				logger.warn("投资人为红包账户，还款入账用户改为红包账户！");
				cuJson.put("forAccountId",StaticData.redEnvelope);						//入账用户(红包户)
			}
			cuJson.put("txFeeIn", "0");										//投资人手续费
			cuJson.put("txFeeOut", ArithUtil.add(new Double[]{				//借款人手续费 = 剩余管理费+多出的管理费+剩余利息+多出来的利息+提前结清违约金
										zzl.getSurplusInterest(),
										zzl.getSurplusManagementAmt(),
										zzl.getMoreInterest(),
										zzl.getMoreManageAmt(),
										zzl.getOverCost(),
										zzl.getOverPenalty(),
										zzl.getPrePaymentPenalty()
									})) ;
			cuJson.put("txFeeIn", "0");											//收款手续费
			cuJson.put("productId", paramJson.get("order_prd_number").toString());	//产品号
			cuJson.put("authCode", zzlEfpaycontrol.get("auth_code"));
			if(cuJson.getString("authCode").endsWith("_needRemove")){
				cuJson.put("authCode", cuJson.getString("authCode").split(Pattern.quote("_needRemove"))[0]);
			}
			cuJson.put("trdresv",zzlEfpaycontrol.get("id"));
			if(cuJson.get("txAmount")!=null && cuJson.get("txFeeOut")!=null){
				if(cuJson.getDouble("txAmount")==0 && cuJson.getDouble("txFeeOut")>0){ //金额为0需要从手续费中获得
					cuJson.put("txAmount", 0.01d);
					cuJson.put("txFeeOut",NumberFormat.format(ArithUtil.sub(cuJson.getDouble("txFeeOut"), 0.01)));
				}
			}
			jsonArray.add(cuJson);
		}
		//还款
		JSONObject transMap = new JSONObject();
		transMap.put("acqRes","");//第三方保留域
		transMap.put("subPacks", jsonArray.toString());
		transMap.put("signature", SignatureUtil.createSign());
		transMap.put("sendAppName",StaticData.appName);//
		transMap.put("remark","");//保留域
		try{
		JSONObject bocResultJson  = WebServiceUtil.sendPost(StaticData.bocUrl+"payProcess/batchRepay", new Object[]{transMap});
		//开始产生资金记录
		JSONObject checkMoneyJson = new JSONObject();
		checkMoneyJson.put("cardNo",paramJson.get("bank_account"));
		checkMoneyJson.put("cust_info_id",paramJson.get("cust_info_id"));			//客户id
		checkMoneyJson.put("money",ArithUtil.add(JsonUtil.getSum(jsonArray, "txAmount"),JsonUtil.getSum(jsonArray, "txFeeOut")));					// 金额
		checkMoneyJson.put("money_type","4"); 			// 1金账户2,银行卡3,现场交钱4,电子账户
		checkMoneyJson.put("operation_type","2"); 		//1入账2出账
		checkMoneyJson.put("status","0");				//1成功0失败
		checkMoneyJson.put("person_type","2");			//1,投资人2借款人3,邀请人
		checkMoneyJson.put("type","1");					//1,提前结清2提前收回3,逾期还款4,部分逾期,5正常还款	
		checkMoneyJson.put("crm_order_id",paramJson.getString("crmOrderId"));
		checkMoneyJson.put("operator", paramJson.getString("empId"));
		String checkMoneyId = psCheckMoneyService.save(checkMoneyJson);
		//债权转让失败了
		if(!bocResultJson.containsKey("responseCode") || !"1".equals(bocResultJson.getString("responseCode"))){
			logger.warn("线上信贷订单冻结成功,订单id:"+paramJson.getString("crmOrderId")+",BOC返回失败!");
			JSONObject smsJson = new JSONObject();
			smsJson.put("project_number", paramJson.getString("crmOrderId"));
			smsJson.put("text","线上信贷订单冻结成功,订单id:"+paramJson.getString("crmOrderId")+",BOC返回失败!");
			SmsUtil.senErrorMsg(smsJson);
		}else{
			//资金记录修改
			psCheckMoneyService.updateStatusById(checkMoneyId);
			logger.warn("提前结清线上信贷订单冻结成功,订单id:"+paramJson.getString("crmOrderId")+",BOC返回成功!");
		}
		}catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("线上信贷订单冻结成功,明细id:"+paramJson.getString("crmOrderId")+",BOC返回失败!");
			JSONObject smsJson = new JSONObject();
			smsJson.put("project_number", paramJson.getString("crmOrderId"));
			smsJson.put("text","线上信贷订单冻结成功,明细id:"+paramJson.getString("crmOrderId")+",BOC返回失败!");
			SmsUtil.senErrorMsg(smsJson);
		}
		return resultJson;
	}
	/**
	 * 功能说明：更改逾期的zzl
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
	public void changeZZLStatus(String crmOrderId){
				String sql = "SELECT id from zzl_ef_paycontrol where crm_order_id='"+crmOrderId+"' and pay_status=0 ";
				List<Map> list = bgEfOrderDao.queryBySqlReturnMapList(sql);
				if(ListTool.isNotNullOrEmpty(list)){
					for(Map map:list){
						try {
							ZZLEfPaycontrol zepc =  (ZZLEfPaycontrol) bgEfOrderDao.findById(ZZLEfPaycontrol.class, map.get("id").toString());
							zepc.setPayStatus(3);
							bgEfOrderDao.update(zepc);
						} catch (Exception e) {
							e.printStackTrace();
							logger.warn(e.getMessage(),e);
						}
					}
				}
	}
	/**
	 * 功能说明：重置金额
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
	private JSONObject toResetAdvaneMoney(JSONObject paramJson) {
		//退回服务费 从本金上扣除
		double totalCapital = 0;
		double returnFee = 0;
		if (paramJson.containsKey("totalCapital") && ChkUtil.isDouble(paramJson.get("totalCapital").toString())) {
			totalCapital = paramJson.getDouble("totalCapital");
		}
		if (paramJson.containsKey("returnFee") && ChkUtil.isDouble(paramJson.get("returnFee").toString())) {
			returnFee = paramJson.getDouble("returnFee");
		}
		paramJson.put("totalCapital", ArithUtil.sub(totalCapital, returnFee));
		return paramJson;
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
	public boolean frozenForAdvanceClear(List<Map> zzlEfpaycontrols,JSONObject jieJson){
		boolean flag = false;
		//开始计算差值
		try {
			//修改zzl
			double totalInterest =jieJson.getDouble("totalAccrual");// 剩余总利息
			double totalManage = jieJson.getDouble("totalManage");// 剩余管理费
			double totalOverCost = jieJson.getDouble("totalInterest");// 剩余罚息
			double totalOverPenalty = jieJson.getDouble("totalOverdueViolate");// 剩余逾期违约金
			double totalPrePenalty = jieJson.getDouble("totalAdvanceMoney");// 提前结清违约金
			double totalCapital = jieJson.getDouble("totalCapital"); //本次还款本金
			double CapitalMoney  = jieJson.getDouble("capitalMoney");
			
			double temptotalInterest = totalInterest;
			double temptotalManage = totalManage;
			double temptotalOverCost = totalOverCost;
			double temptotalOverPenalty = totalOverPenalty;
			double temptotalPrePenalty = totalPrePenalty;
			double temptotalCapital = totalCapital;
			
			boolean manageStop = false;
			boolean interestStop = false;
			boolean overCostStop = false;
			boolean overPenaltyStop = false;
			boolean prePenaltyStop = false;
			boolean capitalStop = false;
			for(int i=0;i<zzlEfpaycontrols.size();i++){
				Map zzlEfpaycontrol = zzlEfpaycontrols.get(i);
				ZZLEfPaycontrol zzl = (ZZLEfPaycontrol)bgEfOrderDao.findById(ZZLEfPaycontrol.class,zzlEfpaycontrol.get("id").toString());
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
				double prePenalty =0d;
				if(totalPrePenalty>0){
					prePenalty = NumberFormat.format(ArithUtil.div(temptotalPrePenalty*zzl.getPrinciapl(),CapitalMoney));
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
				totalPrePenalty  = ArithUtil.sub(totalPrePenalty, prePenalty);
				
				totalCapital  = ArithUtil.sub(totalCapital, capital);
				zzl.setSurplusManagementAmt(manage); //管理费
				zzl.setSurplusInterest(interest);//利息
				zzl.setOverCost(overCost);
				zzl.setOverPenalty(overPenalty);
				zzl.setTempPrincipal(capital);
				zzl.setPrePaymentPenalty(prePenalty);
				
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
				//是否继续提前结清违约金
				if(totalPrePenalty>0){
					zzl.setPrePaymentPenalty(prePenalty); //违约金
				}
				if(totalPrePenalty<0){
					prePenalty = ArithUtil.add(prePenalty,totalPrePenalty);
					zzl.setPrePaymentPenalty(prePenalty); //违约金
				}
				if(i == (zzlEfpaycontrols.size()-1)){
					if(totalPrePenalty>0){
						zzl.setPrePaymentPenalty(ArithUtil.add(prePenalty,totalPrePenalty)); 
					}
				}
				if(prePenaltyStop){
					zzl.setPrePaymentPenalty(0d); //违约金
				}
				if(totalPrePenalty<=0){
					prePenaltyStop  = true;
				}
				if(zzl.getTempPrincipal()>zzl.getPrinciapl()){
					double overPrinciapl = ArithUtil.sub(zzl.getTempPrincipal(), zzl.getPrinciapl());
					zzl.setTempPrincipal(zzl.getPrinciapl());
					zzl.setMoreManageAmt(ArithUtil.add(zzl.getMoreManageAmt(),overPrinciapl));
				}
				bgEfOrderDao.update(zzl);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("冻结时失败!信贷订单id:"+zzlEfpaycontrols.get(0).get("crm_order_id").toString());
		}
		return flag;
	}
	/**
	 * 功能说明：赎楼贷的信用还款
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
	public JSONObject toExecuteA007CrmBoc(JSONObject paramJson){
		
		//开始赋值
		String  lastCprSql = 
				
				"SELECT " +
						"	bci.bank_account bank_account, " +
						"	bci.id cust_info_id, " +
						"	cpr.should_accrual totalAccrual, " +
						"	cpr.manage_fee totalManage, " +
						"	cpr.should_interest totalInterest, " +
						"	cpr.overdue_violate_money totalOverdueViolate, " +
						"	cpr.prepayment_violate_money totalAdvanceMoney, " +
						"	cpr.should_capital totalCapital, " +
						"	co.contract_money capitalMoney, " +
						"	co.order_prd_number order_prd_number " +
						"FROM " +
						"	crm_payrecoder cpr, " +
						"	crm_order co, " +
						"	bg_cust_info bci " +
						"WHERE " +
						"	co.id = cpr.crm_order_id " +
						"and bci.id=co.cust_info_id " +
						"AND cpr.crm_order_id = '"+paramJson.getString("crmOrderId")+"' ";
		paramJson.putAll(bgEfOrderDao.queryBySqlReturnMapList(lastCprSql).get(0));
		paramJson.put("checkMoneyType", "1");
		//需要将zzl的逾期的也改成状态3/
		changeZZLStatus(paramJson.getString("crmOrderId"));
		JSONObject resultJson = new JSONObject();
		resultJson.put("info","");
		resultJson.put("status", "1");
		String cprSql = "SELECT * from crm_payrecoder where  crm_order_id ='"+paramJson.getString("crmOrderId")+"' ";
		List<Map> cprList = bgEfOrderDao.queryBySqlReturnMapList(cprSql);
		double returnFee = 0d;
		if(ListTool.isNotNullOrEmpty(cprList)){
			if(cprList.get(0).get("remain_fee")!=null){
				returnFee = NumberFormat.format(Double.parseDouble(cprList.get(0).get("remain_fee").toString()));
			}
		}
		paramJson.put("returnFee",returnFee);
		//开始进行boc的退回服务费的判断
		boolean returnServiceFlag = true;
		String flagSql = "SELECT *from  sys_param where code='erpv3.service.repayment.returnservice.boc.enable' ";
		List<Map> flagList  = bgEfOrderDao.queryBySqlReturnMapList(flagSql);
		if(ListTool.isNotNullOrEmpty(flagList)){
			String flagString = flagList.get(0).get("value").toString();
			if(flagString.equals("1")){ //说明是需要减免的
				returnServiceFlag = true;
			}
			if(flagString.equals("0")){ //说明不需要减免
				returnServiceFlag = false;
			}
		}
		if(returnServiceFlag){ //需要进行减免的情况下本金需要重新赋值
			paramJson = toResetAdvaneMoney(paramJson);
		}
		
		String sql = "SELECT * from zzl_ef_paycontrol where crm_order_id='"+paramJson.getString("crmOrderId")+"'  ";
		List<Map> zzlEfpaycontrols = bgEfOrderDao.queryBySqlReturnMapList(sql);
		if(ListTool.isNullOrEmpty(zzlEfpaycontrols)){
			resultJson.put("info","未发现债权转让的记录!");
			resultJson.put("status", "0");
			return resultJson;
		}
		JSONArray jsonArray = new JSONArray();
		JSONObject clearJson = new JSONObject();
		clearJson.put("totalAccrual", paramJson.getString("totalAccrual"));
		clearJson.put("totalManage", paramJson.getString("totalManage"));
		clearJson.put("totalInterest", paramJson.getString("totalInterest"));
		clearJson.put("totalOverdueViolate", paramJson.getString("totalOverdueViolate"));
		clearJson.put("totalAdvanceMoney", paramJson.getString("totalAdvanceMoney"));
		clearJson.put("totalCapital", paramJson.getString("totalCapital"));
		clearJson.put("capitalMoney", paramJson.getString("capitalMoney"));
		boolean forzenFlag = frozenForAdvanceClear(zzlEfpaycontrols, clearJson);
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
			try {
				zzl = (ZZLEfPaycontrol) bgEfOrderDao.findById(ZZLEfPaycontrol.class,zzlEfpaycontrol.get("id").toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			JSONObject cuJson = new JSONObject();
			cuJson.put("orderId", "");
			cuJson.put("accountId",paramJson.getString("bank_account"));	//扣款账户
			cuJson.put("txAmount", zzl.getTempPrincipal());					//本金
			cuJson.put("intAmount", "0");										//利息
			cuJson.put("forAccountId",StaticData.risk);							//入账用户(风险户)
			//如果投资人是红包户
			if (StaticData.redCustInfoId.equals(zzl.getCustInfoId())) {
				logger.warn("投资人为红包账户，还款入账用户改为红包账户！");
				cuJson.put("forAccountId",StaticData.redEnvelope);						//入账用户(红包户)
			}
			cuJson.put("txFeeIn", "0");										//投资人手续费
			cuJson.put("txFeeOut", ArithUtil.add(new Double[]{				//借款人手续费 = 剩余管理费+多出的管理费+剩余利息+多出来的利息+提前结清违约金
										zzl.getSurplusInterest(),
										zzl.getSurplusManagementAmt(),
										zzl.getOverCost(),
										zzl.getOverPenalty(),
										zzl.getPrePaymentPenalty()
									})) ;
			cuJson.put("txFeeIn", "0");											//收款手续费
			cuJson.put("productId", paramJson.get("order_prd_number").toString());	//产品号
			cuJson.put("authCode", zzlEfpaycontrol.get("auth_code"));
			if(cuJson.getString("authCode").endsWith("_needRemove")){
				cuJson.put("authCode", cuJson.getString("authCode").split(Pattern.quote("_needRemove"))[0]);
			}
			cuJson.put("trdresv",zzlEfpaycontrol.get("id"));
			if(cuJson.get("txAmount")!=null && cuJson.get("txFeeOut")!=null){
				if(cuJson.getDouble("txAmount")==0 && cuJson.getDouble("txFeeOut")>0){ //金额为0需要从手续费中获得
					cuJson.put("txAmount", 0.01d);
					cuJson.put("txFeeOut",NumberFormat.format(ArithUtil.sub(cuJson.getDouble("txFeeOut"), 0.01)));
				}
			}
			jsonArray.add(cuJson);
		}
		//还款
		JSONObject transMap = new JSONObject();
		transMap.put("acqRes","");//第三方保留域
		transMap.put("subPacks", jsonArray.toString());
		transMap.put("signature", SignatureUtil.createSign());
		transMap.put("sendAppName",StaticData.appName);//
		transMap.put("remark","");//保留域
		try{
		JSONObject bocResultJson  = WebServiceUtil.sendPost(StaticData.bocUrl+"payProcess/batchRepay", new Object[]{transMap});
		//开始产生资金记录
		JSONObject checkMoneyJson = new JSONObject();
		checkMoneyJson.put("cardNo",paramJson.get("bank_account"));
		checkMoneyJson.put("cust_info_id",paramJson.get("cust_info_id"));			//客户id
		checkMoneyJson.put("money",ArithUtil.add(JsonUtil.getSum(jsonArray, "txAmount"),JsonUtil.getSum(jsonArray, "txFeeOut")));					// 金额
		checkMoneyJson.put("money_type","4"); 			// 1金账户2,银行卡3,现场交钱4,电子账户
		checkMoneyJson.put("operation_type","2"); 		//1入账2出账
		checkMoneyJson.put("status","0");				//1成功0失败
		checkMoneyJson.put("person_type","2");			//1,投资人2借款人3,邀请人
		checkMoneyJson.put("type","1");					//1,提前结清2提前收回3,逾期还款4,部分逾期,5正常还款	
		checkMoneyJson.put("crm_order_id",paramJson.getString("crmOrderId"));
		checkMoneyJson.put("operator", paramJson.getString("empId"));
		String checkMoneyId = psCheckMoneyService.save(checkMoneyJson);
		//债权转让失败了
		if(!bocResultJson.containsKey("responseCode") || !"1".equals(bocResultJson.getString("responseCode"))){
			logger.warn("线上信贷订单冻结成功,订单id:"+paramJson.getString("crmOrderId")+",BOC返回失败!");
			JSONObject smsJson = new JSONObject();
			smsJson.put("project_number", paramJson.getString("crmOrderId"));
			smsJson.put("text","线上信贷订单冻结成功,订单id:"+paramJson.getString("crmOrderId")+",BOC返回失败!");
			SmsUtil.senErrorMsg(smsJson);
		}else{
			//资金记录修改
			psCheckMoneyService.updateStatusById(checkMoneyId);
			logger.warn("提前结清线上信贷订单冻结成功,订单id:"+paramJson.getString("crmOrderId")+",BOC返回成功!");
		}
		}catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("线上信贷订单冻结成功,明细id:"+paramJson.getString("crmOrderId")+",BOC返回失败!");
			JSONObject smsJson = new JSONObject();
			smsJson.put("project_number", paramJson.getString("crmOrderId"));
			smsJson.put("text","线上信贷订单冻结成功,明细id:"+paramJson.getString("crmOrderId")+",BOC返回失败!");
			SmsUtil.senErrorMsg(smsJson);
		}
		return resultJson;
	}
}

