package com.apt.webapp.service.auto;

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
import com.apt.util.ListTool;
import com.apt.util.MessageTemplete;
import com.apt.util.NumberFormat;
import com.apt.util.NumberUtil;
import com.apt.util.StaticData;
import com.apt.util.WebServiceUtil;
import com.apt.util.arith.ArithUtil;
import com.apt.util.date.DateUtil;
import com.apt.util.mail.Mail;
import com.apt.util.singature.SignatureUtil;
import com.apt.util.sms.SmsUtil;
import com.apt.webapp.dao.bg.ef.IBgEfOrderDao;
import com.apt.webapp.model.bg.ef.BgCustomer;
import com.apt.webapp.model.bg.ef.BgEfOrders;
import com.apt.webapp.model.bg.ef.BgEfPaycontrol;
import com.apt.webapp.model.bg.ef.BgEfPayrecord;
import com.apt.webapp.model.bg.ef.BgSysMessage;
import com.apt.webapp.model.bg.ef.BgSysMessageContent;
import com.apt.webapp.model.ef.ZZLEfPaycontrol;
import com.apt.webapp.service.bg.ef.IBgEfOrderService;
import com.apt.webapp.service.crm.ICrmOrderService;
import com.apt.webapp.service.ef.IEfFundRecordService;
import com.apt.webapp.service.ef.IEfOrderService;
import com.apt.webapp.service.ef.IEfPaycontrolService;
import com.apt.webapp.service.ef.IEfPayrecordService;

@Component
public class AutoUpdateThread {

	/*public JSONArray updateDataJsonarArray;
	public Map tempMap = new HashMap();
	public Map hasOverMap = new HashMap();*/
	@Resource
	private IBgEfOrderService bgEfOrderService;
	@Resource
	private IBgEfOrderDao bgEfOrderDao;
	@Resource
	private IEfPaycontrolService efPaycontrolService;
	@Resource
	private IEfFundRecordService efFundRecordService;
	@Resource
	private IEfOrderService efOrderService;
	@Resource
	private ICrmOrderService crmOrderService;
	@Resource
	private IEfPayrecordService efPayrecordService;
	//日志
	private static Logger logger = LoggerFactory.getLogger(AutoUpdateThread.class);
	public  void run(JSONArray tempJsonarArray) {
/*		String date = DateUtil.getCurrentTime(DateUtil.STYLE_2);
		int beginCount=1;
		while(tempMap.containsKey(date+"_"+beginCount)){
		if(hasOverMap.containsKey(date+"_"+beginCount)){
			beginCount++;
			continue;
		}else{
			hasOverMap.put(date+"_"+beginCount,"");
		}
		JSONArray tempJsonarArray =JSONArray.fromObject(tempMap.get(date+"_"+beginCount));
		*/
		logger.warn("本次债权转让返回的成功明细值:"+tempJsonarArray.toString());
		//如果有成功的则进行更新
		if(tempJsonarArray!=null && tempJsonarArray.size()>0){
			for(int i=0;i<tempJsonarArray.size();i++){
				logger.warn("数据:"+tempJsonarArray.getJSONObject(i));
				JSONObject baseJson = tempJsonarArray.getJSONObject(i);
				try{
					
					//开始进行明细转移和状态修改和记录
					//准备处理赎楼的投资人 -->暂时注释掉,后期在说
					/*if(baseJson.getString("thredPriKey").split(Pattern.quote(":"))[0].equals("3_3")){ //代表是赎楼的产品
						toSolveRansomFloorOrders(baseJson);
						continue;
					}*/
					if(!baseJson.getString("thredPriKey").split(Pattern.quote(":"))[0].equals("3_1")){ //必须是3,否则不处理
						continue;
					}
					Map efPaycontrol = bgEfOrderDao.getCurrentEfBocControls(baseJson.getString("thredPriKey").split(Pattern.quote(":"))[1]);
					if(efPaycontrol == null){
						continue;
					}
					if(efPaycontrol!=null && efPaycontrol.containsKey("pay_status") && "1".equals(efPaycontrol.get("pay_status").toString())){ //不为空存在还款状态并且已经是还款结束状态
						continue;
					}
					efPaycontrol.put("clearing_channel","2");
					efPaycontrol.put("onLine",efPaycontrol.get("online_type").toString());
					efPaycontrol.put("authcode",baseJson.getString("investAuthCode"));
					efPaycontrol.put("seriNo",baseJson.getString("seriNo"));
					efPaycontrol.put("investment_model",efPaycontrol.get("investment_model").toString());
					bgEfOrderService.normalRepayByModel(efPaycontrol);
					//开始进行可用资金存储
					if("2".equals(efPaycontrol.get("investment_model"))){ //定投
						if(BusinessTool.isNotProfessionalInvestor(efPaycontrol.get("investor_type"))){  //不是专业投资人才需要进行处理
							efOrderService.lockRowForEforderAuth(efPaycontrol); //处理
						}
					}
				}catch (Exception e) { //如果修改异常了
					e.printStackTrace();
					logger.warn(e.getMessage(),e);
					logger.warn("理财明细债权转让成功,但是修改数据时失败!明细id:"+baseJson.getString("thredPriKey"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", baseJson.getString("thredPriKey"));
					smsJson.put("text","理财明细债权转让成功,但是修改数据时失败!明细id:"+baseJson.getString("thredPriKey"));
					SmsUtil.senErrorMsg(smsJson);
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
	public void toSolveRansomFloorOrders(JSONObject baseJson){
		//1,产生记录,2,结清订单,3,产生明细,4,更新记录或产生记录
		String bgEfOrderId = baseJson.getString("thredPriKey").split(Pattern.quote(":"))[1];
		BgEfOrders eo = (BgEfOrders) efOrderService.findById(BgEfOrders.class,bgEfOrderId);
		eo.setClearType(2);
		eo.setPayStatus(2);
		efOrderService.update(eo);
		//开始生成对应的zzl
		ZZLEfPaycontrol zepc = new ZZLEfPaycontrol();
		if(!ChkUtil.isEmpty(baseJson.get("investAuthCode"))){
			zepc.setAuthCode(baseJson.getString("investAuthCode"));  //授权码
		}
		if(!ChkUtil.isEmpty(baseJson.get("seriNo"))){
			zepc.setSeriNo(baseJson.getString("seriNo"));
		}
		zepc.setClearingChannel(2);  //结算通道
		//zepc.setControlId(efPaycontrol.get("id").toString());					//id
		zepc.setCouponInterest(0d);
		zepc.setCreateTime(DateUtil.getCurrentTime());
		zepc.setCustInfoId(eo.getCustInfoId());
		zepc.setEfOrderId(eo.getId());
		zepc.setInterest(0d);
		zepc.setManagementAmt(0d);
		zepc.setMoreInterest(0d);  //多出的利息,暂时记为0
		zepc.setOverPenalty(0d);
		zepc.setPayStatus(0);	  //默认赋值0  未结清
		zepc.setPayTime(DateUtil.getCurrentTime()); //还款时间
		zepc.setPeriods(1);
		zepc.setPrePaymentPenalty(0d); 
		zepc.setPrinciapl(eo.getEfFectiveAmt());
		zepc.setSurplusInterest(0d);
		zepc.setSurplusManagementAmt(0d);
		zepc.setSurplusPrincipal(eo.getEfFectiveAmt());
		zepc.setType(1);
		zepc.setUpdateTime(DateUtil.getCurrentTime());
		zepc.setCrmOrderId(eo.getCrmOrderId());
		try {
			efPaycontrolService.add(zepc);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("债权转让数据拷贝失败!理财订单id:"+eo.getId()+",boc对应信息:"+baseJson);
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
		//查找对应的还款记录,如果没有的话,直接
		List<Map> recordList = efPayrecordService.findListByEfOrderId(eo.getId());
		if(ListTool.isNotNullOrEmpty(recordList)){
			bgEfPaycontrol.setPrinciapl(eo.getPrincipal());
			bgEfPaycontrol.setInterest(Double.parseDouble(recordList.get(0).get("interest").toString()));
			bgEfPaycontrol.setManagementAmt(Double.parseDouble(recordList.get(0).get("management_amt").toString()));
			bgEfPaycontrol.setTotalAmt(ArithUtil.sub(eo.getEfFectiveAmt()+Double.parseDouble(recordList.get(0).get("interest").toString()),Double.parseDouble(recordList.get(0).get("management_amt").toString())));
			bgEfPaycontrol.setPeriods(1);
			bgEfPaycontrol.setSurplusInterest(0.0);
			bgEfPaycontrol.setSurplusManagementAmt(0.0);
			bgEfPaycontrol.setSurplusPrincipal(0.0);
			efPaycontrolService.add(bgEfPaycontrol);
			//zzl的更新
			zepc.setInterest(Double.parseDouble(recordList.get(0).get("interest").toString()));
			zepc.setManagementAmt(Double.parseDouble(recordList.get(0).get("management_amt").toString()));
			zepc.setSurplusInterest(Double.parseDouble(recordList.get(0).get("interest").toString()));
			zepc.setSurplusManagementAmt(Double.parseDouble(recordList.get(0).get("management_amt").toString()));
			//更新记录
			BgEfPayrecord ber = (BgEfPayrecord) efOrderService.findById(BgEfPayrecord.class,recordList.get(0).get("id").toString());
			ber.setPrincipal(eo.getEfFectiveAmt());
			ber.setTotalAmt(ArithUtil.add(ber.getPrincipal()-ber.getManagementAmt(),ber.getInteRest()));
			efOrderService.update(ber);
		}else{
			bgEfPaycontrol.setPrinciapl(eo.getPrincipal());
			bgEfPaycontrol.setInterest(0d);
			bgEfPaycontrol.setManagementAmt(0d);
			bgEfPaycontrol.setTotalAmt(eo.getPrincipal());
			bgEfPaycontrol.setPeriods(1);
			bgEfPaycontrol.setSurplusInterest(0.0);
			bgEfPaycontrol.setSurplusManagementAmt(0.0);
			bgEfPaycontrol.setSurplusPrincipal(0.0);
			efPaycontrolService.add(bgEfPaycontrol);
			// 还款记录
			BgEfPayrecord bgEfPayrecord = new BgEfPayrecord();
			bgEfPayrecord.setCustId(eo.getCustId());
			bgEfPayrecord.setCustInfoId(eo.getCustInfoId());
			bgEfPayrecord.setEfOrderId(eo.getId());
			bgEfPayrecord.setEfPaycontrolId(bgEfPaycontrol.getId());
			// 金额
			bgEfPayrecord.setPeriods(1);
			bgEfPayrecord.setPrincipal(eo.getEfFectiveAmt());
			bgEfPayrecord.setInteRest(0d);
			bgEfPayrecord.setManagementAmt(0d);
			bgEfPayrecord.setTotalAmt(ArithUtil.add(bgEfPayrecord.getPrincipal()-bgEfPayrecord.getManagementAmt(),bgEfPayrecord.getInteRest()));
			bgEfPayrecord.setCreateTime(DateUtil.getCurrentTime());
			efPaycontrolService.add(bgEfPayrecord);
		}
		zepc.setControlId(bgEfPaycontrol.getId());
		efPaycontrolService.update(zepc);
		
		//开始进行站内信通知
		try{
			BgCustomer bgCustomer  = (BgCustomer) efOrderService.findById(BgCustomer.class,eo.getCustId());
			BgSysMessageContent bsmc = new BgSysMessageContent();
			bsmc.setCreateTime(DateUtil.getCurrentTime());
			String Princiapl = NumberFormat.formatDouble(bgEfPaycontrol.getPrinciapl());
			String Interest = NumberFormat.formatDouble(bgEfPaycontrol.getInterest());
			String ManagementAmt = NumberFormat.formatDouble(bgEfPaycontrol.getManagementAmt());
			String totalMoney="0";
			if(bgEfPaycontrol.getUseCouponInterest()!=null){
				totalMoney = NumberFormat.formatDouble(bgEfPaycontrol.getPrinciapl()+bgEfPaycontrol.getInterest()+bgEfPaycontrol.getUseCouponInterest()-bgEfPaycontrol.getManagementAmt());
			}else{
				totalMoney = NumberFormat.formatDouble(bgEfPaycontrol.getPrinciapl()+bgEfPaycontrol.getInterest()-bgEfPaycontrol.getManagementAmt());
			}
			String couponInterest = "0";
			if(bgEfPaycontrol.getUseCouponInterest()!=null && bgEfPaycontrol.getUseCouponInterest()>0){
				couponInterest = NumberFormat.formatDouble(bgEfPaycontrol.getUseCouponInterest());
			}
			JSONObject paramJson = new JSONObject();
			paramJson.put("msgType","2");
			//
			paramJson.put("name", bgCustomer.getUsername());
			paramJson.put("number", eo.getOrderNumber());
			paramJson.put("Periods",1);
			paramJson.put("Princiapl", Princiapl);
			paramJson.put("Interest", Interest);
			paramJson.put("ManagementAmt", ManagementAmt);
			paramJson.put("totalMoney", totalMoney);
			
			paramJson.put("couponInterest", couponInterest);
			JSONObject resultJson  = MessageTemplete.getMsg(paramJson);
			//
			Map map  = efOrderService.getNecessaryInfo(eo.getId(),eo.getCustId());
			//判断是否需要发邮件
			if(map.containsKey("mail")){ //不是空的话则需要发送邮件
				try {
					Mail.sentMail(resultJson.getString("title"), resultJson.getString("content"), bgCustomer.getCustEmail());
				} catch (Exception e) {
					logger.warn(e.getMessage(),e);
					logger.warn("发送邮件失败!理财订单id:"+eo.getId());
				}
			}
			bsmc.setMsgContent(resultJson.getString("content"));
			bsmc.setMsgTitle(resultJson.getString("title"));
			bsmc.setPublishId("");
			bsmc.setUpdateTime(DateUtil.getCurrentTime());
			if(map.containsKey("online_type")){
				if ("1".equals(map.get("online_type").toString())) {
					bsmc.setPlatformType(1);
				}else if ("2".equals(map.get("online_type").toString())) {
					bsmc.setPlatformType(2);
				}
			}
			
			efPaycontrolService.add(bsmc);
			BgSysMessage bsm  = new BgSysMessage();
			bsm.setCustId(eo.getCustId());
			bsm.setEfOrderId(eo.getId());
			bsm.setEnabled(1);
			bsm.setMsgClass(2);
			bsm.setMsgContentId(bsmc.getId());
			bsm.setMsgType(5);
			bsm.setOrderId(eo.getCrmOrderId());
			bsm.setReadStatus(0);
			if(map.containsKey("online_type")){
				if ("1".equals(map.get("online_type").toString())) {
					bsmc.setPlatformType(1);
				}else if ("2".equals(map.get("online_type").toString())) {
					bsmc.setPlatformType(2);
				}
			}
			efPaycontrolService.add(bsm);
			logger.warn("赎楼贷站内信生成成功!订单id:"+eo.getId());
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			logger.warn("赎楼贷站内信生成失败!明细id:"+eo.getId());
		}
		
	}
	/*public void start(JSONArray updateDataJsonarArray) {
		this.updateDataJsonarArray = updateDataJsonarArray;
	}*/
}
