package com.apt.webapp.service.auto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import com.apt.util.NumberFormat;
import com.apt.util.NumberUtil;
import com.apt.util.StaticData;
import com.apt.util.pocTool;
import com.apt.util.arith.ArithUtil;
import com.apt.util.date.DateUtil;
import com.apt.util.sms.SmsUtil;
import com.apt.webapp.dao.crm.ICrmOrderDao;
import com.apt.webapp.model.auto.AutoPocInvokeRecord;
import com.apt.webapp.model.bg.ef.BgEfPaycontrol;
import com.apt.webapp.model.ef.PocBatchRecord;
import com.apt.webapp.service.bg.ef.IBgEfOrderService;
import com.apt.webapp.service.crm.ICrmOrderService;
import com.apt.webapp.service.ef.IEfFundRecordService;
import com.apt.webapp.service.ef.IEfPaycontrolService;

@Component
public class AutoRunHF {
	private static final int THRESHOLD = 1900; // 阈值
	// 日志
	private Logger logger = LoggerFactory.getLogger(AutoRunHF.class);
	// 导入线上理财订单
	@Resource
	private IBgEfOrderService bgEfOrderService;
	@Resource
	private ICrmOrderService crmOrderService;
	@Resource
	private IEfFundRecordService efFundRecordService;
	@Resource
	public ICrmOrderDao crmOrderDao;
	@Resource
	private IAutoPocInvokeRecordService pocInvokeRecordService;
	@Resource
	private IEfPaycontrolService efPaycontrolService;
	/**
	 * 功能说明：POC理财还款----批量数据上传文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年11月8日 16:06:21
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void EfAutopayHFBatchUpload(){
		logger.warn("恒丰理财自动还款（批量划拨）上传文件开始----");
		Map pocMap = new HashMap();
		pocMap.put("code", "PW03");
		List<JSONObject> list = new ArrayList<JSONObject>();
		JSONArray flowRecordJsonArray = new JSONArray();
		try{
			//查找今天需要还款的理财明细并通过债券转让的方式还款
			//1.查找今日待还
			List<Map> efPaycontrols =  bgEfOrderService.getCurrentControlsHF();
			//踢出资产包相关数据
			crmOrderService.removePkgCrmOrders(efPaycontrols);
			//如果无数据则结束
			if(ListTool.isNullOrEmpty(efPaycontrols)){logger.warn("恒丰理财自动还款（批量划拨）结束----");return;}
			List<Object> bgEfPaycontrols = new ArrayList<Object>();
			for(Map efPaycontrol:efPaycontrols){
				//更新明细，表示该条数据已被自动还款处理
				if(efPaycontrol.containsKey("id")){
					BgEfPaycontrol bec =  (BgEfPaycontrol) bgEfOrderService.findById(BgEfPaycontrol.class, efPaycontrol.get("id").toString());
					try{
						bec.setOperateType("0");// 0自动还款  1手动还款
						bgEfPaycontrols.add(bec);
					}catch (Exception e) {
						logger.warn("理财明细在修改自动状态时失败!理财明细id:"+efPaycontrol.get("id").toString());
					}
				}
			}
			//批量更新数据
			crmOrderDao.batchSaveOrUpdate(bgEfPaycontrols,500);
			for(Map efPaycontrol:efPaycontrols){
				try {
					//准备开始记录数据
					JSONObject flowRecordJson = new JSONObject();
					flowRecordJson.put("orderType", "bg_ef_paycontrol");
					flowRecordJson.put("payControlId", efPaycontrol.get("id"));
					flowRecordJson.put("status", "-1");
					flowRecordJson.put("code", "");
					flowRecordJson.put("rem", "");
					//数据
					String out_cust_no = StaticData.HFcreditAccount;// 付款方登录名
					String out_cust_name = StaticData.HFcreditAccountName;// 付款方中文名称
					String int_cust_no = efPaycontrol.get("hf_account").toString().trim();// 收款方登录名
					String int_cust_name = efPaycontrol.get("cust_name").toString().trim();// 收款方中文名称
					//计算金额
					double money = 0;
					if ("1".equals(efPaycontrol.get("investment_model"))) {	//直投
						//本金+利息+抵用券利息-管理费
						money = NumberFormat.format(ArithUtil.add(new Double[]{NumberUtil.parseDouble(efPaycontrol.get("surplus_principal")),NumberUtil.parseDouble(efPaycontrol.get("surplus_interest")),NumberUtil.parseDouble(efPaycontrol.get("coupon_interest"))}));
						money = NumberFormat.format(ArithUtil.sub(money,NumberUtil.parseDouble(efPaycontrol.get("surplus_management_amt"))));
					}
					else if ("2".equals(efPaycontrol.get("investment_model"))) {	//定投
						//本金
						money=NumberUtil.parseDouble(efPaycontrol.get("surplus_principal"));
					}
					String remark = "auto_bepc_id:" + efPaycontrol.get("id");
					if (ChkUtil.isEmpty(out_cust_no) || ChkUtil.isEmpty(out_cust_name) || ChkUtil.isEmpty(int_cust_no) || ChkUtil.isEmpty(int_cust_name)) {
						continue;
					}
					if (money == 0) {//金额等于0 不上传处理 直接更新明细
						Map efP = bgEfOrderService.getCurrentImmeControlsPocById(efPaycontrol.get("id").toString()).get(0);
						String investment_model = efP.get("investment_model").toString();
						// 直接更新数据
						if (ChkUtil.isNotEmpty(investment_model) && "1".equals(investment_model)) {
							// 如果成功需要记录并修改明细状态
							efP.put("POC", "1");
							efP.put("isNewPoc", "1");
							JSONObject refJson = bgEfOrderService.cleanEfPaycontrol(efP);
							// 定投更新数据
						} else if (ChkUtil.isNotEmpty(investment_model) && "2".equals(investment_model)) {
							// 如果成功需要记录并修改明细状态
							// 开始进行明细转移和状态修改和记录
							efP.put("onLine", "0");
							bgEfOrderService.normalRepay(efP);
							// 开始进行可用资金存储
							//efFundRecordService.addNormal(efP);
						}
						continue;
					}
					String fromFreeze = "0";		// 付款资金来自冻结
					String instantlyFreeze = "1";	// 收款后立即冻结
					String investment_model = efPaycontrol.get("investment_model").toString();
					//直接不进行冻结
					if (ChkUtil.isNotEmpty(investment_model) && "1".equals(investment_model)) {
						instantlyFreeze = "0";
					//定投冻结资金
					}else if (ChkUtil.isNotEmpty(investment_model) && "2".equals(investment_model)) {
						instantlyFreeze = "1";
						if(BusinessTool.isProfessionalInvestor(efPaycontrol.get("investor_type"))){ //如果是专业投资人则不需要冻结
							instantlyFreeze = "0";	
						}
					}
					String seriNo = DateUtil.getSerialNo("B2P");
					String masSource = out_cust_no + 
									"|" + out_cust_name + 
									"|" + fromFreeze + 
									"|" + int_cust_no + 
									"|" + int_cust_name + 
									"|" + instantlyFreeze + 
									"|" + money + 
									"|" + remark + "|"+  //多的这个是预授权合同号不需要
									"|"+seriNo;
					JSONObject uploadJson = new JSONObject();
					uploadJson.put("macSource", masSource);
					//因为是恒丰,所以备注需要特殊处理(D2D1710251047110458|20171025|PWDZ|A09951|112325646|18325631259|麻孜娴|100||15017102303|裴宏霖|||3)
					String hfremark = seriNo+"|"+
									   DateUtil.getCurrentTime(DateUtil.STYLE_3)+"|PWDZ|"+
									   efPaycontrol.get("order_prd_number").toString()+"|"+
									   DateUtil.getCurrentTime(DateUtil.STYLE_3)+"|"+
									   StaticData.HFcreditAccount+"|"+
									   StaticData.HFcreditAccountName+"|"+
									   NumberUtil.duelmoney(money)+"|"+
									   "|"+
									   efPaycontrol.get("hf_account").toString()+"|"+
									   efPaycontrol.get("cust_name").toString()+"|"+
									   efPaycontrol.get("hf_account").toString()+"|"+
									   efPaycontrol.get("cust_name").toString()+"|"+
									   "2"
									   ;
					uploadJson.put("remark", hfremark);
					uploadJson.put("serialNumber", seriNo);
					uploadJson.put("platform", "2");
					list.add(uploadJson);
					//记录中
					flowRecordJsonArray.add(flowRecordJson);
					//如果存在邀请人  并且是直投
					if(!ChkUtil.isEmpty(efPaycontrol.get("referee_info_id")) && "1".equals(efPaycontrol.get("investment_model"))){
						Map refereeInfoMap = bgEfOrderService.getRefereeInfoMap(efPaycontrol.get("referee_info_id").toString());
						// 计算邀请人金额
						Double refMoney = NumberFormat.format(ArithUtil.mul(ArithUtil.add(NumberUtil.parseDouble(efPaycontrol.get("surplus_interest")), NumberUtil.parseDouble(efPaycontrol.get("coupon_interest"))),
								ArithUtil.div(NumberUtil.parseDouble(efPaycontrol.get("referee_income_scale")), 100d)));
						String refHfAccount = refereeInfoMap.get("hf_account").toString();
						String refCustName = refereeInfoMap.get("cust_name").toString();
						String refReamrk = "auto_bepcrefer_id:" + efPaycontrol.get("id");
						String yqSeriNo = DateUtil.getSerialNo("YQ");
						String refereeSource = out_cust_no + "|" + out_cust_name + "|0|" + refHfAccount + "|" + refCustName + "|0|" + refMoney + "|" + refReamrk + "||"+yqSeriNo;
						String YQhfremark = yqSeriNo+"|"+
								   DateUtil.getCurrentTime(DateUtil.STYLE_3)+"|PWDZ|"+
								   StaticData.HFProjectNumber+"|"+
								   DateUtil.getCurrentTime(DateUtil.STYLE_3)+"|"+
								   StaticData.HFcreditAccount+"|"+
								   StaticData.HFcreditAccountName+"|"+
								   NumberUtil.duelmoney(refMoney)+"|"+
								   "|"+
								   refHfAccount+"|"+
								   refCustName+"|"+
								   ""+"|"+
								   ""+"|"+
								   "8"
								   ;
						if (refMoney == 0) {//金额等于0不进行处理
							continue;
						}else{
							JSONObject refuploadJson = new JSONObject();
							refuploadJson.put("macSource", refereeSource);
							refuploadJson.put("remark", YQhfremark);
							refuploadJson.put("platform", "2");
							refuploadJson.put("serialNumber", yqSeriNo);
							list.add(refuploadJson);
						}
					}
				} catch (Exception e) {
					logger.warn(e.getMessage(),e);
					logger.warn("Poc理财自动还款（批量划拨）失败!理财明细ID："+efPaycontrol.get("id"));
					JSONObject smsJson = new JSONObject();
					smsJson.put("text", "Poc理财自动还款（批量划拨）失败!理财明细ID："+efPaycontrol.get("id"));
					smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
				}
			}
			if (list.size()>0) {
				bgEfOrderService.savePaytransferrecord(list,"PW03");
				//发送给清结算系统
				//SmsUtil.sendFlowRecord(flowRecordJsonArray);
			}
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			logger.warn("Poc理财自动还款（批量划拨）（uploadFilePW03）抛出异常---");
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "信贷订单自动还款，Poc理财自动还款（批量划拨）（uploadFilePW03）抛出异常");
			smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
		}
		logger.warn("Poc理财自动还款（批量划拨）上传文件结束----");
	}
	
}
