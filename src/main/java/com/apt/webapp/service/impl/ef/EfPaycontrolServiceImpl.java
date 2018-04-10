package com.apt.webapp.service.impl.ef;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apt.util.ChkUtil;
import com.apt.util.ListTool;
import com.apt.util.NumberFormat;
import com.apt.util.NumberUtil;
import com.apt.util.StaticData;
import com.apt.util.WebServiceUtil;
import com.apt.util.pocTool;
import com.apt.util.arith.ArithUtil;
import com.apt.util.date.DateUtil;
import com.apt.util.sms.SmsUtil;
import com.apt.webapp.dao.ef.IEfOrdersDao;
import com.apt.webapp.dao.ef.IEfPaycontrolDao;
import com.apt.webapp.model.bg.ef.BgEfOrders;
import com.apt.webapp.model.bg.ef.BgEfPaycontrol;
import com.apt.webapp.model.bg.ef.BgReferralIncomeRecord;
import com.apt.webapp.model.crm.CrmApplay;
import com.apt.webapp.model.crm.CrmOrder;
import com.apt.webapp.model.crm.CrmPaycontrol;
import com.apt.webapp.model.crm.PkgCustCrmorder;
import com.apt.webapp.model.ef.EfFundRecord;
import com.apt.webapp.model.ef.EfPaycontrol;
import com.apt.webapp.service.ef.IEfPaycontrolService;


/**
 * 功能说明： 合同扫描件素材 service层   实现类
 * @author weiyingni
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-27
 * Copyright zzl-apt
 */
@Service
@Transactional
@WebService(serviceName = "efPaycontrolService", endpointInterface = "com.apt.webapp.service.ef.IEfPaycontrolService")
public class EfPaycontrolServiceImpl implements IEfPaycontrolService {
	//日志
	private Logger logger = LoggerFactory.getLogger(EfPaycontrolServiceImpl.class);
	@Resource
	private IEfPaycontrolDao efPaycontrolDao;
	@Resource
	private IEfOrdersDao efOrdersDao;
	/**
	 * 功能说明： 更新
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
	public void update(Object obj){
		try {
			efPaycontrolDao.update(obj);
		} catch (Exception e) {
			e.printStackTrace();
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
		return efPaycontrolDao.getCurrentControlsBoc();
	}
	/**
	 * 功能说明： 获得Poc当日待还明细
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
		return efPaycontrolDao.getCurrentControlsPoc();
	}
	/**
	 * 功能说明： 获得当日待还明细
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月31日 15:27:12
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getCurrentControlsPrincipal(){
		return efPaycontrolDao.getCurrentControlsPrincipal();
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
		return efPaycontrolDao.getNeedClearPKOrders();
	}
	/**
	 * 功能说明： 获得当日待还明细
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月31日 15:27:12
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getCurrentControlsInterest(){
		return efPaycontrolDao.getCurrentControlsInterest();
	}
	/**
	 * 功能说明： 结清
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
	public void updateNormal(Map efPaycontrol) {
		EfPaycontrol efpaycontrol;
		try {
			efpaycontrol = (EfPaycontrol) efPaycontrolDao.findById(EfPaycontrol.class, efPaycontrol.get("id").toString());
			efpaycontrol.setSurplusInterest(0d);
			efpaycontrol.setSurplusManagementAmt(0d);
			efpaycontrol.setSurplusRateCoupon(0d);
			if (ChkUtil.isEmpty(efpaycontrol.getSurplusPrincipal()) || efpaycontrol.getSurplusPrincipal() <= 0) {
				efpaycontrol.setPayStatus(1);
			} else if (efPaycontrol.containsKey("platform_type") && !"0".equals(efPaycontrol.get("platform_type"))) {
				if (!Boolean.valueOf(efPaycontrol.get("hasBgEfOrders").toString())) { // 如果没有了债权订单则进行站岗资金的解冻
					String efOrderId = efpaycontrol.getEfOrderId();
					// 判断是否有匹配中的资金
					String sqlString = "select 1  from ef_fund_record where ef_order_id='" + efOrderId + "' and record_status=2 ";
					List<Map> pipei = efPaycontrolDao.queryBySqlReturnMapList(sqlString);
					if (ListTool.isNotNullOrEmpty(pipei)) {
						//有匹配中的资金 不做任何处理
						logger.warn("该理财明细在解冻时发现对应的理财订单中有匹配中的资金，不做解冻处理!理财明细id:" + efPaycontrol.get("id"));
					} else {
						// 查询该订单下的站岗资金
						String sql = "select sum(money) money from ef_fund_record where ef_order_id='" + efOrderId + "' and record_status=1 ";
						Double money = NumberUtil.parseDouble(efPaycontrolDao.queryBySqlReturnMapList(sql).get(0).get("money"));
						// 站岗资金不能大于剩余本金
						if(NumberFormat.format(money) > NumberFormat.format(efpaycontrol.getSurplusPrincipal()) ||
						   NumberFormat.format(money) < NumberFormat.format(efpaycontrol.getSurplusPrincipal())	
						 ){
							logger.warn("资产包到期还款本金与站岗资金不符,站岗资金:"+money+",明细剩余本金:"+efpaycontrol.getSurplusPrincipal()+",ef_paycontrol_id:"+efPaycontrol.get("id"));
							JSONObject smsJson = new JSONObject();
							smsJson.put("project_number", efPaycontrol.get("order_number"));
							smsJson.put("text", "资产包到期还款本金与站岗资金不符,站岗资金:"+money+",明细剩余本金:"+efpaycontrol.getSurplusPrincipal()+",ef_paycontrol_id:"+efPaycontrol.get("id"));
							SmsUtil.senErrorMsg(smsJson);
						}
						if (money <= efpaycontrol.getSurplusPrincipal() && money > 0) {
							// 解冻站岗资金
							boolean flag = false;
							// 开始判断还款类型 1.poc 2.boc
							String clearing_channel = efPaycontrol.get("clearing_channel").toString();
							// 1.poc
							if ("1".equals(clearing_channel) && money > 0) {
								Map paramMap = new HashMap();
								paramMap.put("cust_no", efPaycontrol.get("fy_account"));
								paramMap.put("amt", money);
								String pocResult = pocTool.connectToPoc("unFreeze", paramMap);
								// JSONObject json =
								// JSONObject.fromObject(pocResult);
								if (!"0000".equals(pocResult)) {
									logger.warn("Poc理财明细在解冻时失败!理财明细id:" + efPaycontrol.get("id"));
									JSONObject smsJson = new JSONObject();
									smsJson.put("project_number", efPaycontrol.get("order_number"));
									smsJson.put("text", "Poc理财明细在解冻时失败!理财明细id:" + efPaycontrol.get("id"));
									SmsUtil.senErrorMsg(smsJson);
									flag = false;
								} else {
									logger.warn("Poc理财明细在解冻时成功!理财明细id:" + efPaycontrol.get("id"));
									flag = true;
								}
								// 2.boc
							}else if(StaticData.HFBANK_CODE.equals(clearing_channel) && money > 0){
								JSONObject transMap = new JSONObject();
								//恒丰扣款参数
								transMap.put("remark", "理财订单到期解冻");
								transMap.put("cust_no", efPaycontrol.get("hf_account"));
								transMap.put("amt", NumberUtil.duelmoney(money));
								transMap.put("rem", "理财订单到期解冻");
								transMap.put("project_number", StaticData.HFProjectNumber);
								transMap.put("out_login_name", efPaycontrol.get("cust_name"));
								transMap.put("business_type", "4");
								transMap.put("amount", NumberUtil.duelmoney(money));
								transMap.put("fx_amt", "0");
								transMap.put("in_login_id", "");
								transMap.put("in_login_name", "");
								transMap.put("orgin_login_id", "");
								transMap.put("orgin_login_name", "");
								transMap.put("fullString", "");
								transMap.put("summary", "");
								transMap.put("interfaceType", "");
								transMap.put("trade_date", DateUtil.getCurrentTime(DateUtil.STYLE_3));
								JSONObject pocResult = WebServiceUtil.sendPostHF(StaticData.bocUrl+"hfTransferService/unfreeze", transMap);
								if ("1".equals(pocResult.getString("responseCode"))) {
									logger.warn("恒丰理财明细在解冻时成功!理财明细id:" + efPaycontrol.get("id"));
									flag = true;
								} else {
									logger.warn("恒丰理财明细在解冻时失败!理财明细id:" + efPaycontrol.get("id"));
									JSONObject smsJson = new JSONObject();
									smsJson.put("project_number", efPaycontrol.get("order_number"));
									smsJson.put("text", "恒丰理财明细在解冻时失败!理财明细id:" + efPaycontrol.get("id"));
									SmsUtil.senErrorMsg(smsJson);
									flag = false;
								}
							} else if ("2".equals(clearing_channel) && money > 0) {
								String url = StaticData.bocUrl;
								JSONObject bocJson = new JSONObject();
								// 准备查找原始的授权码
								String autoCode = efOrdersDao.findLineAuthCode(efOrderId);
								bocJson.put("cardNbr", efPaycontrol.get("bank_account"));
								bocJson.put("authCode", autoCode);
								bocJson.put("sendAppName", StaticData.appName);
								bocJson.put("serialNo", efPaycontrol.get("id"));
								bocJson.put("remark", "boc投标申请撤销");
								JSONObject bocResult = new JSONObject();
								bocResult = WebServiceUtil.sendPost(url+"payProcess/bocBidCancel", new Object[]{bocJson});
								if ("0".equals(bocResult.getString("responseCode"))) {
									logger.warn("Boc理财明细在解冻时失败!理财明细id:" + efPaycontrol.get("id"));
									JSONObject smsJson = new JSONObject();
									smsJson.put("project_number", efPaycontrol.get("order_number"));
									smsJson.put("text", "Boc理财明细在解冻时失败!理财明细id:" + efPaycontrol.get("id"));
									SmsUtil.senErrorMsg(smsJson);
									flag = false;
								} else {
									logger.warn("Boc理财明细在解冻时成功!理财明细id:" + efPaycontrol.get("id"));
									flag = true;
									//解冻成功了,需要判断解冻的金额是否是站岗资金的金额
									if(bocResult.containsKey("data")){
										//开始比较此次解冻
										double resultMoney = NumberFormat.format(bocResult.get("data"));
										if(resultMoney !=money){
											JSONObject smsJson = new JSONObject();
											smsJson.put("project_number", efPaycontrol.get("order_number"));
											smsJson.put("text", "资产包到期授权码冻结金额与站岗资金不符,站岗资金:"+money+",授权码金额:"+resultMoney+",ef_paycontrol_id:"+efPaycontrol.get("id"));
											SmsUtil.senErrorMsg(smsJson);
										}
									}
								}
							}
							if (flag) {
								if (money > 0) {
									// 开始产生资金记录
									EfFundRecord efFundRecord = new EfFundRecord();
									efFundRecord.setMoney(-money);
									efFundRecord.setRecordType("解冻");
									efFundRecord.setRecordStatus(EfFundRecord.RECORD_STATUS_YES);
									efFundRecord.setCreateTime(DateUtil.getCurrentTime());
									efFundRecord.setCustId(efpaycontrol.getCustId());
									efFundRecord.setCustInfoId(efpaycontrol.getCustInfoId());
									efFundRecord.setEfOrderId(efOrderId);
									efFundRecord.setEfApplayId(efPaycontrol.get("ef_applay_id").toString());
									// 添加资金记录
									efPaycontrolDao.add(efFundRecord);
								}
								// 剩余本金-解冻资金
								// double count =
								// ArithUtil.sub(efpaycontrol.getSurplusPrincipal(),money);
								// efpaycontrol.setSurplusPrincipal(count);
								// if (count == 0) {
								// efpaycontrol.setPayStatus(1);
								// }
								efpaycontrol.setSurplusPrincipal(0d);
								efpaycontrol.setPayStatus(1);
							}
						}
						if (money == 0) {
							efpaycontrol.setSurplusPrincipal(0d);
							efpaycontrol.setPayStatus(1);
						}
					}
				}
			}
			efPaycontrolDao.update(efpaycontrol);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(), e);
		}

	}
	/*public static void main(String[] args) {
		double money= 234;
		EfFundRecord efFundRecord= new EfFundRecord();
		efFundRecord.setMoney(-money);
		System.out.println(efFundRecord.getMoney());
	}*/
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
		return efPaycontrolDao.findAutoSerino(cust_info_id);
	}
	/**
	 * 功能说明：获得借款人信息
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
	public Map getCrmManInfo(String ef_order_id){
		Map info = new HashMap();
		info.put("bank_account", "");
		try{
			BgEfOrders eo = (BgEfOrders) efPaycontrolDao.findById(BgEfOrders.class, ef_order_id);
			CrmOrder co = (CrmOrder) efPaycontrolDao.findById(CrmOrder.class, eo.getCrmOrderId());
			String sql = "SELECT * from bg_cust_info where id='"+co.getCustInfoId()+"'";
			List<Map> list = efPaycontrolDao.queryBySqlReturnMapList(sql);
			if(ListTool.isNullOrEmpty(list)){
				return info;
			}
			info = list.get(0);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
	/**
	 * 功能说明：获得借款人信息
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
	public BgEfPaycontrol findById(String id){
		try {
			return (BgEfPaycontrol) efPaycontrolDao.findById(BgEfPaycontrol.class, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 功能说明：增加记录
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
	public void add(Object obj){
		try {
			efPaycontrolDao.add(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		return efPaycontrolDao.getInviterData(income_cust_info_id);
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
	public void pkgIncomeRecord(JSONObject inviterRecord){
		try{
			BgReferralIncomeRecord bri  = new BgReferralIncomeRecord();
			bri.setCreateTime(DateUtil.getCurrentTime());
			bri.setCustInfoId(inviterRecord.getString("cust_info_id"));
			bri.setIncomeSource("投资还款");
			bri.setRefereeInfoId(inviterRecord.getString("referee_info_id"));
			bri.setReferralIncome(inviterRecord.getDouble("referral_income"));
			bri.setUpdateTime(DateUtil.getCurrentTime());
			add(bri);
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			logger.warn("理财明细在存储邀请人收益数据时失败!理财订单id:"+inviterRecord.get("ef_order_id"));
			JSONObject smsJson = new JSONObject();
			smsJson.put("project_number", "erpv3_auto2");
			smsJson.put("text","理财明细在存储邀请人收益数据时失败!理财订单id:"+inviterRecord.get("ef_order_id"));
			SmsUtil.senErrorMsgByZhiyun(smsJson);
		}
	}
	
	/**
	 * 功能说明：
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午10:08:05
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void updateCrmorder(String CrmOrderId)
	{
		//更新资产包信息
		//更新资产包

	}
	
	/**
	 * 功能说明：更新对应的订单，申请单和资产包信息
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午11:17:30
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void updateInfo(CrmOrder crmOrder, CrmApplay apply)
	{
		try
		{
			//更新信贷订单信息
			String sql ="select * from crm_paycontrol where crm_order_id='"+crmOrder.getId()+"' and status in (0,2)";
			List list = efPaycontrolDao.queryBySqlReturnMapList(sql);
			
			if (ListTool.isNullOrEmpty(list))
			{
				try
				{
					crmOrder.setOrderTradeStatus(4);				  //订单状态 0 交易中 1 进入还款 2 流标 3 满标 4 交易成功 5 订单异常 6 死亡 7 满标放款中 8 进入开户 9 订单匹配中
					crmOrder.setClearType(1);					      //结清类型 1 正常到期结清 2.主动结清 3.催收被动结清 4.代偿 5.提前结清
					crmOrder.setFinishTime(DateUtil.getCurrentTime());//订单结清时间
					efPaycontrolDao.update(crmOrder);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					logger.warn(e.getMessage(),e);
					logger.warn("更新信贷订单时失败!信贷订单id:"+ crmOrder.getId());
				}
				
				try
				{
					//更新申请单状态
					if(ChkUtil.isNotEmpty(apply))
					{
						apply.setTradeStatus(4);		//订单状态 0 交易中 1 进入还款 2 流标 3 满标 4 交易成功 5 订单异常 6 死亡  7 满标放款中
						apply.setStatus(4);				//订单状态 1:待命 2:审批中 3:还款中 4:已结清 5:拒绝 6.匹配中(线上投资中 7.满标 8 满标放款中 9流标 10 退件
						apply.setRepaymentStatus(1);	//还款状态 1:正常到期结清 2.主动结清 3.催收被动结清 4.代偿
						efPaycontrolDao.update(apply);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					logger.warn(e.getMessage(),e);
					logger.warn("更新信贷申请单时失败!信贷订单id:"+ crmOrder.getId());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("更新信贷订单时失败!信贷订单id:"+ crmOrder.getId());
		}
	}
}
