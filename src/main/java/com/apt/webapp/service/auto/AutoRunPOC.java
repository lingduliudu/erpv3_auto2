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
public class AutoRunPOC {
	private static final int THRESHOLD = 1900; // 阈值
	// 日志
	private Logger logger = LoggerFactory.getLogger(AutoRunPOC.class);
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
	 * 功能说明：生成Poc信贷还款list
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月15日 13:58:05
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */ 	
	public void crmAutopayPoc() {
		logger.warn("Poc信贷订单的还款开始---");
		Map pocMap = new HashMap();
		pocMap.put("code", "AC01");
		List<JSONObject> list = new ArrayList<JSONObject>();
		JSONArray flowRecordJsonArray = new JSONArray();
		try {
			// 明细序列|扣款人开户行行别|扣款人银行帐号|户名|金额|企业流水号|备注|手机号
			// 获得当日的需要还款的crm信贷明细
			List<Map> crmPaycontrols = crmOrderService.getCurrentPaycontrolsPoc();
			if (ListTool.isNotNullOrEmpty(crmPaycontrols)) {
				for (Map crmPaycontrol : crmPaycontrols) {
					// 开始判断改明细前面是否有逾期订单
					boolean hasOver = crmOrderService.hasOver(crmPaycontrol.get("crm_order_id").toString());
					if (hasOver) {
						continue;
					}
					//准备开始记录数据
					JSONObject flowRecordJson = new JSONObject();
					flowRecordJson.put("orderType", "crm_paycontrol");
					flowRecordJson.put("payControlId", crmPaycontrol.get("id"));
					flowRecordJson.put("status", "-1");
					flowRecordJson.put("code", "");
					flowRecordJson.put("rem", "");
					String masSource = "";
					String bankno = crmPaycontrol.get("bankno").toString().trim();// 总行代码
					String stopBank = StaticData.StopBank;
					boolean isStop= false;
					if(stopBank !=null && !"".equals(stopBank)){
						String[] stopBanks = stopBank.split(Pattern.quote(","));
						for(String stopbank:stopBanks){
							if(bankno.equals(stopbank)){
								isStop = true;
								break;
							}
						}
					}
					if(isStop){
						continue;
					}
					masSource += bankno + "|";
					String accntno = crmPaycontrol.get("accntno").toString().trim();// 卡号
					masSource += accntno + "|";
					String accntnm = crmPaycontrol.get("accntnm").toString().trim();// 户名
					masSource += accntnm + "|";
					double money = ArithUtil.add(new Double[] { Double.parseDouble(crmPaycontrol.get("frozenMoney").toString()) });
					if(money<=0){
						continue;
					}
					masSource += money + "|";
					String Serialno = DateUtil.getserialnoCheckUnique("");
					masSource += Serialno + "|";
					String remark = "auto_cpc_id:" + crmPaycontrol.get("id");
					masSource += remark + "|";
					String mobile = crmPaycontrol.get("cust_mobile") == null ? "" : crmPaycontrol.get("cust_mobile").toString().trim();
					masSource += mobile;
					if (ChkUtil.isEmpty(bankno) || ChkUtil.isEmpty(accntno) || ChkUtil.isEmpty(accntnm) || 
							ChkUtil.isEmpty(Serialno) || ChkUtil.isEmpty(remark) || ChkUtil.isEmpty(mobile)) {
						continue;
					}
					JSONObject uploadJson = new JSONObject();
					uploadJson.put("macSource", masSource);
					uploadJson.put("remark", remark);
					uploadJson.put("serialNumber", UUID.randomUUID().toString());
					list.add(uploadJson);
					//记录中
					flowRecordJsonArray.add(flowRecordJson);
				}
				if (list.size() > 0) {
					try{
						bgEfOrderService.savePaytransferrecord(list, "AC01");
						SmsUtil.sendFlowRecord(flowRecordJsonArray);
						logger.warn("Poc信贷正常还款的操作（uploadFileAC01）上传文件成功---");
					}
					catch (Exception e) {
						logger.warn("Poc信贷正常还款的操作（uploadFileAC01）上传文件失败---");
						logger.warn("Poc信贷订单的还款结束---");
						JSONObject smsJson = new JSONObject();
						smsJson.put("text", "Poc信贷正常还款的操作（uploadFileAC01）保存文件失败");
						smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
					}
				}
			}
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			logger.warn("Poc信贷正常还款的操作（uploadFileAC01）抛出异常---");
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "信贷订单自动还款，Poc信贷正常还款的操作（uploadFileAC01）抛出异常");
			smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
		}
		logger.warn("Poc信贷订单的还款结束---");
	}
	//信贷还款备份
	public void crmAutopayPocBackUp() {
		logger.warn("Poc信贷订单的还款开始---");
		Map pocMap = new HashMap();
		pocMap.put("code", "AC01");
		List<String> list = new ArrayList<String>();
		try {
			// 明细序列|扣款人开户行行别|扣款人银行帐号|户名|金额|企业流水号|备注|手机号
			// 获得当日的需要还款的crm信贷明细
			List<Map> crmPaycontrols = crmOrderService.getCurrentPaycontrolsPoc();
			if (ListTool.isNotNullOrEmpty(crmPaycontrols)) {
				for (Map crmPaycontrol : crmPaycontrols) {
					// 开始判断改明细前面是否有逾期订单
					boolean hasOver = crmOrderService.hasOver(crmPaycontrol.get("crm_order_id").toString());
					if (hasOver) {
						continue;
					}
					String masSource = "";
					String bankno = crmPaycontrol.get("bankno").toString().trim();// 总行代码
					masSource += bankno + "|";
					String accntno = crmPaycontrol.get("accntno").toString().trim();// 卡号
					masSource += accntno + "|";
					String accntnm = crmPaycontrol.get("accntnm").toString().trim();// 户名
					masSource += accntnm + "|";
					double money = ArithUtil.add(new Double[] { Double.parseDouble(crmPaycontrol.get("frozenMoney").toString()) });
					masSource += money + "|";
					String Serialno = DateUtil.getserialnoCheckUnique("");
					masSource += Serialno + "|";
					String remark = "crm_paycontrol_id:" + crmPaycontrol.get("id");
					masSource += remark + "|";
					String mobile = crmPaycontrol.get("cust_mobile") == null ? "" : crmPaycontrol.get("cust_mobile").toString().trim();
					masSource += mobile;
					if (ChkUtil.isEmpty(bankno) || ChkUtil.isEmpty(accntno) || ChkUtil.isEmpty(accntnm) || 
							ChkUtil.isEmpty(Serialno) || ChkUtil.isEmpty(remark) || ChkUtil.isEmpty(mobile)) {
						continue;
					}
					list.add(masSource);
				}
				if (list.size() > 0) {
					//如果大于一万条数据，每一万条数据上传一次文件
					if (list.size() > THRESHOLD) {
						List<String> subList=new ArrayList<String>();
						for (int i = 0, n = list.size(); i < n; i++) {
							subList.add(list.get(i));
							if (i > 0 && i % THRESHOLD == 0) {
								pocMap.put("data", subList);
								logger.warn("Poc信贷正常还款的操作（uploadFileAC01）: 开始请求POC, 请求数据 "+subList.size()+"条---");
								// 准备请求fuyou
								String response = pocTool.connectToPoc("uploadFileAC01", pocMap);
								JSONObject json = JSONObject.fromObject(response);
								String responseCode = json.getString("responseCode");
								// 上传失败
								if ("0".equals(responseCode)) {
									logger.warn("Poc信贷正常还款的操作（uploadFileAC01）上传文件失败---");
									logger.warn("Poc信贷订单的还款结束---");
									JSONObject smsJson = new JSONObject();
									smsJson.put("text", "Poc信贷正常还款的操作（uploadFileAC01）上传文件失败");
									smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
									return;
								}
								logger.warn("Poc信贷正常还款的操作（uploadFileAC01）上传文件成功---");
								subList.clear();
								continue;
							}
							//循环到最后的时候 全部上传
							if (i==n-1) {
								pocMap.put("data", subList);
								logger.warn("Poc信贷正常还款的操作（uploadFileAC01）: 开始请求POC, 请求数据 "+subList.size()+"条---");
								// 准备请求fuyou
								String response = pocTool.connectToPoc("uploadFileAC01", pocMap);
								JSONObject json = JSONObject.fromObject(response);
								String responseCode = json.getString("responseCode");
								// 上传失败
								if ("0".equals(responseCode)) {
									logger.warn("Poc信贷正常还款的操作（uploadFileAC01）上传文件失败---");
									logger.warn("Poc信贷订单的还款结束---");
									JSONObject smsJson = new JSONObject();
									smsJson.put("text", "Poc信贷正常还款的操作（uploadFileAC01）上传文件失败");
									smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
									return;
								}
								logger.warn("Poc信贷正常还款的操作（uploadFileAC01）上传文件成功---");
								subList.clear();
							}
						}
					}else{
						pocMap.put("data", list);
						logger.warn("Poc信贷正常还款的操作（uploadFileAC01）: 开始请求POC, 请求数据 "+list.size()+"条---");
						// 准备请求fuyou
						String response = pocTool.connectToPoc("uploadFileAC01", pocMap);
						JSONObject json = JSONObject.fromObject(response);
						String responseCode = json.getString("responseCode");
						// 上传失败
						if ("0".equals(responseCode)) {
							logger.warn("Poc信贷正常还款的操作（uploadFileAC01）上传文件失败---");
							logger.warn("Poc信贷订单的还款结束---");
							JSONObject smsJson = new JSONObject();
							smsJson.put("text", "Poc信贷正常还款的操作（uploadFileAC01）上传文件失败");
							smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
							return;
						}
						logger.warn("Poc信贷正常还款的操作（uploadFileAC01）上传文件成功---");
					}
				}
			}
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			logger.warn("Poc信贷正常还款的操作（uploadFileAC01）抛出异常---");
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "信贷订单自动还款，Poc信贷正常还款的操作（uploadFileAC01）抛出异常");
			smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
		}
		logger.warn("Poc信贷订单的还款结束---");
	}
	/**
	 * 功能说明：Poc信贷还款update(空参数方法)
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月15日 13:58:05
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */ 	
	public void crmAutopayPocUpdate() {
		crmAutopayPocUpdate("","0");
	}
	/**
	 * 功能说明：Poc信贷还款update
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月15日 13:58:05
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */ 	
	public void crmAutopayPocUpdate(String uploadTime,String total) {
		try{
			List<Object> os = new ArrayList<Object>();
			AutoPocInvokeRecord invokeRecord = null;
			JSONObject paramJson = new JSONObject();
			logger.warn("Poc信贷订单的正常还款开始---");
			Map paramMap = new HashMap();
			paramMap.put("code", "AC01");
			if (ChkUtil.isNotEmpty(uploadTime)) {
				paramMap.put("date", uploadTime);
			}
			if (ChkUtil.isNotEmpty(total)) {
				paramMap.put("total", total);
			}
			List<String> list = new ArrayList<String>();
			String response = pocTool.connectToPoc("downloadFileAC01", paramMap);
			JSONObject json = JSONObject.fromObject(response);
			String responseCode = json.getString("responseCode");
			// 下载失败
			if ("0".equals(responseCode)) {
				logger.warn("Poc信贷还款的操作（downloadFileAC01）下载文件失败---");
				logger.warn("Poc信贷订单的还款结束---");
				JSONObject smsJson = new JSONObject();
				smsJson.put("text", "信贷订单自动还款，Poc信贷还款的操作（downloadFileAC01）下载文件失败");
				smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
				return;
			}
			if (ChkUtil.isNotEmpty(json.get("data"))){
				JSONArray array = json.getJSONArray("data");
				// 明细序列|扣款人开户行行别|扣款人银行帐号|户名|金额|企业流水号|备注|手机号|返回码|返回说明
				// 获得当日的需要还款的crm信贷明细
				for (Object object : array) {
					if (ChkUtil.isEmpty(object)) {
						continue;
					}
					String[] str = object.toString().split(Pattern.quote("|"));
					String bankType = str[1]; // 扣款人开户行行别
					String bankNo = str[2]; // 扣款人银行帐号
					String custName = str[3]; // 户名
					String amt = str[4]; // 金额
					String serialNo = str[5]; // 企业流水号
					String remark = str[6]; // 备注信息
					String mobile = str[7]; // 手机号
					String returnCode = str[8]; // 返回码
					String returnInfo = str[9]; // 说明
					if(str.length>10) {
						returnCode= str[10];
						returnInfo = str[11]; // 说明
					}
					if ("auto_cpc_id".equals(remark.split(":")[0])) {
						//正常还款
						// 如果是下载全部文件,去除掉已处理成功的明细
						if ("1".equals(total)) {
							String crmPaycontrolId = remark.split(":")[1];
							paramJson.put("crmPaycontrolId", crmPaycontrolId);
							paramJson.put("serialNo", serialNo);
							paramJson.put("repayType", 3);// 还款类型 1理财还款预授权 2理财还款划拨
							// 3信贷正常还款代扣 4信贷逾期还款代扣
							// 5理财邀请人预授权 6理财邀请人划拨
							List<Map> recordList = pocInvokeRecordService.getPocInvokeRecords(paramJson.toString());
							if (ListTool.isNotNullOrEmpty(recordList)) {
								logger.warn("该POC信贷明细已代扣成功，不再进行划扣操作！,crm_paycontrol_d:" + remark + "，企业流水号：" + serialNo);
								continue;
							}
						}
						invokeRecord = new AutoPocInvokeRecord();// 初始化POC调用记录
						invokeRecord.setCrmPaycontrolId(remark.split(":")[1]);// 信贷明细id
						invokeRecord.setReturnCode(returnCode);// 返回码
						invokeRecord.setReturnInfo(returnInfo);// 返回信息
						invokeRecord.setRepayType(3);// 还款类型
						invokeRecord.setSerialNo(serialNo);
						invokeRecord.setAmount(Double.parseDouble(amt));// 金额
						invokeRecord.setInvokeTime(DateUtil.getCurrentTime());
						if ("202000".equals(returnCode)) {
							invokeRecord.setState(1);
							// 更新数据
							// poc扣款成功,直接进行修改状态就可以了
							logger.warn("线下信贷订单正常还款成功,明细id:" + remark + ",POC返回成功!");
							crmOrderService.repayNormalByPoc(remark.split(":")[1]);
						} else {
							invokeRecord.setState(0);
							logger.warn("Poc信贷订单的正常还款操作失败：" + remark + "，失败原因：" + returnInfo);
							if (ChkUtil.isNotEmpty(returnInfo) && !returnInfo.contains("不足")) {
								JSONObject smsJson = new JSONObject();
								smsJson.put("text", "Poc信贷订单的正常还款操作失败：" + remark + "，失败原因：" + returnInfo);
								smsJson.put("project_number", "erpv3_auto2");
								SmsUtil.senErrorMsgByZhiyun(smsJson);
							}
						}
						os.add(invokeRecord);
					}else if ("auto_co_id".equals(remark.split(":")[0])) {
						//逾期还款
						// 如果是下载全部文件,去除掉已处理成功的明细
						if ("1".equals(total)) {
							String crmOrderId = remark.split(":")[1];
							paramJson.put("crmOrderId", crmOrderId);
							paramJson.put("repayType", 4);//还款类型  1理财还款预授权 2理财还款划拨  3信贷正常还款代扣  4信贷逾期还款代扣 5理财邀请人预授权 6理财邀请人划拨
							paramJson.put("serialNo", serialNo);
							List<Map> recordList = pocInvokeRecordService.getPocInvokeRecords(paramJson.toString());
							if (ListTool.isNotNullOrEmpty(recordList)) {
								logger.warn("该POC逾期信贷订单已经代扣成功，不再进行划扣操作！,crmOrderId:"+remark+",企业流水号："+serialNo);
								continue;
							}
						}
						invokeRecord=new AutoPocInvokeRecord();//初始化POC调用记录
						invokeRecord.setCrmOrderId(remark.split(":")[1]);//信贷明细id
						invokeRecord.setReturnCode(returnCode);//返回码
						invokeRecord.setReturnInfo(returnInfo);//返回信息
						invokeRecord.setRepayType(4);//还款类型
						invokeRecord.setSerialNo(serialNo);
						invokeRecord.setAmount(Double.parseDouble(amt));//金额
						invokeRecord.setInvokeTime(DateUtil.getCurrentTime());
						if ("202000".equals(returnCode)) {
							invokeRecord.setState(1);
							//更新数据
							//poc扣款成功,直接进行修改状态就可以了
							logger.warn("POC逾期信贷订单POC扣款成功,crmOrderId:"+remark+",POC返回成功!");
							crmOrderService.overRepayByPoc(remark.split(":")[1]);
						}else{
							invokeRecord.setState(0);
							logger.warn("Poc逾期信贷订单的扣款操作失败："+remark+"，失败原因："+returnInfo);
							if (ChkUtil.isNotEmpty(returnInfo) && !returnInfo.contains("不足")) {
								JSONObject smsJson = new JSONObject();
								smsJson.put("text", "Poc逾期信贷订单的扣款操作失败："+remark+"，失败原因："+returnInfo);
								smsJson.put("project_number", "erpv3_auto2");
								SmsUtil.senErrorMsgByZhiyun(smsJson);
							}
						}
						os.add(invokeRecord);
					}
				}
			}
			if (os.size() > 0) {
				try {
					crmOrderDao.batchSaveOrUpdate(os, 500);
				} catch (Exception e) {
					// TODO: handle exception
					logger.warn(e.getMessage(), e);
				}
			}
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			logger.warn("Poc信贷正常还款的操作（downloadFileAC01）抛出异常---");
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "信贷订单自动还款，Poc信贷正常还款的操作（downloadFileAC01）抛出异常");
			smsJson.put("project_number", "erpv3_auto2");
			SmsUtil.senErrorMsgByZhiyun(smsJson);
		}
		logger.warn("Poc信贷订单的还款结束---");
	}
	/**
	 * 功能说明：生成Poc信贷逾期还款list
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月15日 17:01:17
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */ 
	public void overCrmAutopayPoc(){
		logger.warn("POC逾期信贷订单的还款开始---");
		Map pocMap = new HashMap();
		pocMap.put("code", "AC01");
		List<JSONObject> list = new ArrayList<JSONObject>();
		try{
			//获得当日的需要还款的crm信贷明细
			List<Map> crmPaycontrols =  crmOrderService.getOverPaycontrolsPoc();
			if(ListTool.isNotNullOrEmpty(crmPaycontrols)){
				//先开始循环冻结
				for(Map crmPaycontrol:crmPaycontrols){
					String masSource = "";
					String bankno = crmPaycontrol.get("bankno").toString().trim();// 总行代码
					masSource += bankno + "|";
					String accntno = crmPaycontrol.get("accntno").toString().trim();// 卡号
					masSource += accntno + "|";
					String accntnm = crmPaycontrol.get("accntnm").toString().trim();// 户名
					masSource += accntnm + "|";
					double money = ArithUtil.add(new Double[] { Double.parseDouble(crmPaycontrol.get("frozenMoney").toString()) });
					masSource += money + "|";
					String Serialno = DateUtil.getserialnoCheckUnique("");
					masSource += Serialno + "|";
					String remark = "auto_co_id:" + crmPaycontrol.get("crm_order_id");
					masSource += remark + "|";
					String mobile = crmPaycontrol.get("cust_mobile") == null ? "" : crmPaycontrol.get("cust_mobile").toString().trim();
					masSource += mobile;
					if (ChkUtil.isEmpty(bankno) || ChkUtil.isEmpty(accntno) || ChkUtil.isEmpty(accntnm) || 
							ChkUtil.isEmpty(Serialno) || ChkUtil.isEmpty(remark) || ChkUtil.isEmpty(mobile)) {
						continue;
					}
					JSONObject uploadJson = new JSONObject();
					uploadJson.put("macSource", masSource);
					uploadJson.put("remark", remark);
					uploadJson.put("serialNumber", UUID.randomUUID().toString());
					list.add(uploadJson);
				}
				if (list.size()>0) {
					try{
						bgEfOrderService.savePaytransferrecord(list, "AC01");
						logger.warn("Poc信贷逾期还款的操作（uploadFileAC01）上传文件成功---");
					}catch (Exception e) {
						logger.warn("Poc信贷逾期还款的操作（uploadFileAC01）失败---");
						logger.warn("Poc信贷逾期订单的还款结束---");
						JSONObject smsJson = new JSONObject();
						smsJson.put("text", "信贷订单自动还款，Poc信贷逾期还款的操作（uploadFileAC01）失败");
						smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
					}
				}
			}
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			logger.warn("Poc信贷逾期还款的操作（uploadFileAC01）抛出异常---");
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "信贷订单自动还款，生成Poc信贷逾期还款的操作（uploadFileAC01）抛出异常");
			smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
		}
		logger.warn("POC逾期信贷订单的还款结束---");
	}
	/**
	 * 功能说明：生成Poc信贷逾期还款list
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月15日 17:01:17
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */ 
	public void overCrmAutopayPocBackUp(){
		logger.warn("POC逾期信贷订单的还款开始---");
		Map pocMap = new HashMap();
		pocMap.put("code", "AC01");
		List<String> list = new ArrayList<String>();
		try{
			//获得当日的需要还款的crm信贷明细
			List<Map> crmPaycontrols =  crmOrderService.getOverPaycontrolsPoc();
			if(ListTool.isNotNullOrEmpty(crmPaycontrols)){
				//先开始循环冻结
				for(Map crmPaycontrol:crmPaycontrols){
					String masSource = "";
					String bankno = crmPaycontrol.get("bankno").toString().trim();// 总行代码
					masSource += bankno + "|";
					String accntno = crmPaycontrol.get("accntno").toString().trim();// 卡号
					masSource += accntno + "|";
					String accntnm = crmPaycontrol.get("accntnm").toString().trim();// 户名
					masSource += accntnm + "|";
					double money = ArithUtil.add(new Double[] { Double.parseDouble(crmPaycontrol.get("frozenMoney").toString()) });
					masSource += money + "|";
					String Serialno = DateUtil.getserialnoCheckUnique("");
					masSource += Serialno + "|";
					String remark = "crm_order_id:" + crmPaycontrol.get("crm_order_id");
					masSource += remark + "|";
					String mobile = crmPaycontrol.get("cust_mobile") == null ? "" : crmPaycontrol.get("cust_mobile").toString().trim();
					masSource += mobile;
					if (ChkUtil.isEmpty(bankno) || ChkUtil.isEmpty(accntno) || ChkUtil.isEmpty(accntnm) || 
							ChkUtil.isEmpty(Serialno) || ChkUtil.isEmpty(remark) || ChkUtil.isEmpty(mobile)) {
						continue;
					}
					list.add(masSource);
				}
				if (list.size()>0) {
					//如果大于一万条数据，每一万条数据上传一次文件
					if (list.size() > THRESHOLD) {
						List<String> subList=new ArrayList<String>();
						for (int i = 0, n = list.size(); i < n; i++) {
							subList.add(list.get(i));
							if (i > 0 && i % THRESHOLD == 0) {
								pocMap.put("data", subList);
								// 准备请求fuyou
								logger.warn("Poc信贷逾期还款的操作（uploadFileAC01）: 开始请求POC, 请求数据 " + subList.size() + "条---");
								String response = pocTool.connectToPoc("uploadFileAC01", pocMap);
								JSONObject json = JSONObject.fromObject(response);
								String responseCode = json.getString("responseCode");
								// 上传失败
								if ("0".equals(responseCode)) {
									logger.warn("Poc信贷逾期还款的操作（uploadFileAC01）失败---");
									logger.warn("Poc信贷逾期订单的还款结束---");
									JSONObject smsJson = new JSONObject();
									smsJson.put("text", "信贷订单自动还款，Poc信贷逾期还款的操作（uploadFileAC01）失败");
									smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson); 
									return;
								}
								logger.warn("Poc信贷逾期还款的操作（uploadFileAC01）上传文件成功---");
								subList.clear();
								continue;
							}
							//循环到最后的时候 全部上传
							if (i==n-1) {
								pocMap.put("data", subList);
								// 准备请求fuyou
								logger.warn("Poc信贷逾期还款的操作（uploadFileAC01）: 开始请求POC, 请求数据 " + subList.size() + "条---");
								String response = pocTool.connectToPoc("uploadFileAC01", pocMap);
								JSONObject json = JSONObject.fromObject(response);
								String responseCode = json.getString("responseCode");
								// 上传失败
								if ("0".equals(responseCode)) {
									logger.warn("Poc信贷逾期还款的操作（uploadFileAC01）失败---");
									logger.warn("Poc信贷逾期订单的还款结束---");
									JSONObject smsJson = new JSONObject();
									smsJson.put("text", "信贷订单自动还款，Poc信贷逾期还款的操作（uploadFileAC01）失败");
									smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
									return;
								}
								logger.warn("Poc信贷逾期还款的操作（uploadFileAC01）上传文件成功---");
								subList.clear();
							}
						}
					}else{
						pocMap.put("data", list);
						// 准备请求fuyou
						logger.warn("Poc信贷逾期还款的操作（uploadFileAC01）: 开始请求POC, 请求数据 "+list.size()+"条---");
						String response = pocTool.connectToPoc("uploadFileAC01", pocMap);
						JSONObject json = JSONObject.fromObject(response);
						String responseCode = json.getString("responseCode");
						// 上传失败
						if ("0".equals(responseCode)) {
							logger.warn("Poc信贷逾期还款的操作（uploadFileAC01）失败---");
							logger.warn("Poc信贷逾期订单的还款结束---");
							JSONObject smsJson = new JSONObject();
							smsJson.put("text", "信贷订单自动还款，Poc信贷逾期还款的操作（uploadFileAC01）失败");
							smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
							return;
						}
						logger.warn("Poc信贷逾期还款的操作（uploadFileAC01）上传文件成功---");
					}
				}
			}
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			logger.warn("Poc信贷逾期还款的操作（uploadFileAC01）抛出异常---");
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "信贷订单自动还款，生成Poc信贷逾期还款的操作（uploadFileAC01）抛出异常");
			smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
		}
		logger.warn("POC逾期信贷订单的还款结束---");
	}
	/**
	 * 功能说明：Poc信贷逾期还款update(空参数方法)
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月15日 13:58:05
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */ 	
	public void overCrmAutopayPocUpdate() {
		overCrmAutopayPocUpdate("","0");
	}
	/**
	 * 功能说明：Poc信贷逾期还款update
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月15日 13:58:05
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */ 	
	public void overCrmAutopayPocUpdate(String uploadTime,String total) {
		try{
			List<Object> os=new ArrayList<Object>();
			AutoPocInvokeRecord invokeRecord=null;
			JSONObject paramJson=new JSONObject();
			logger.warn("Poc逾期信贷订单的还款开始---");
			Map paramMap = new HashMap();
			paramMap.put("code", "AC01");
			if (ChkUtil.isNotEmpty(uploadTime)) {
				paramMap.put("date", uploadTime);
			}
			if (ChkUtil.isNotEmpty(total)) {
				paramMap.put("total", total);
			}
			String response = pocTool.connectToPoc("downloadFileAC01", paramMap);
			JSONObject json = JSONObject.fromObject(response);
			String responseCode = json.getString("responseCode");
			// 下载失败
			if ("0".equals(responseCode)) {
				logger.warn("Poc信贷逾期还款的操作（downloadFileAC01）失败---");
				logger.warn("Poc信贷逾期订单的还款结束---");
				JSONObject smsJson = new JSONObject();
				smsJson.put("text", "信贷订单自动还款，Poc信贷逾期还款的操作（downloadFileAC01）失败");
				smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
				return;
			}
			if (ChkUtil.isNotEmpty(json.get("data"))){
				JSONArray array = json.getJSONArray("data");
				// 明细序列|扣款人开户行行别|扣款人银行帐号|户名|金额|企业流水号|备注|手机号
				// 获得当日的需要还款的crm信贷明细
				for (Object object : array) {
					if (ChkUtil.isEmpty(object)) {
						continue;
					}
					String[] str = object.toString().split(Pattern.quote("|"));
					String bankType = str[1];		// 扣款人开户行行别
					String bankNo = str[2];			// 扣款人银行帐号
					String custName = str[3];		// 户名
					String amt = str[4];			// 金额
					String serialNo = str[5];		// 企业流水号
					String remark = str[6];			// 备注信息
					if (!"crm_order_id".equals(remark.split(":")[0])) {
						continue;
					}
					String mobile = str[7];			// 手机号
					String returnCode = str[8];		// 返回码
					String returnInfo = str[9];		// 说明
					// 如果是下载全部文件,去除掉已处理成功的明细
					if ("1".equals(total)) {
						String crmOrderId = remark.split(":")[1];
						paramJson.put("crmOrderId", crmOrderId);
						paramJson.put("repayType", 4);//还款类型  1理财还款预授权 2理财还款划拨  3信贷正常还款代扣  4信贷逾期还款代扣 5理财邀请人预授权 6理财邀请人划拨
						paramJson.put("serialNo", serialNo);
						List<Map> recordList = pocInvokeRecordService.getPocInvokeRecords(paramJson.toString());
						if (ListTool.isNotNullOrEmpty(recordList)) {
							logger.warn("该POC逾期信贷订单已经代扣成功，不再进行划扣操作！,crmOrderId:"+remark+",企业流水号："+serialNo);
							continue;
						}
					}
					invokeRecord=new AutoPocInvokeRecord();//初始化POC调用记录
					invokeRecord.setCrmOrderId(remark.split(":")[1]);//信贷明细id
					invokeRecord.setReturnCode(returnCode);//返回码
					invokeRecord.setReturnInfo(returnInfo);//返回信息
					invokeRecord.setRepayType(4);//还款类型
					invokeRecord.setSerialNo(serialNo);
					invokeRecord.setAmount(Double.parseDouble(amt));//金额
					invokeRecord.setInvokeTime(DateUtil.getCurrentTime());
					if ("202000".equals(returnCode)) {
						invokeRecord.setState(1);
						//更新数据
						//poc扣款成功,直接进行修改状态就可以了
						logger.warn("POC逾期信贷订单POC扣款成功,crmOrderId:"+remark+",POC返回成功!");
						crmOrderService.overRepayByPoc(remark.split(":")[1]);
					}else{
						invokeRecord.setState(0);
						logger.warn("Poc逾期信贷订单的扣款操作失败："+remark+"，失败原因："+returnInfo);
						JSONObject smsJson = new JSONObject();
						smsJson.put("text", "Poc逾期信贷订单的扣款操作失败："+remark+"，失败原因："+returnInfo);
						smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
					}
					os.add(invokeRecord);
				}
			}
			if (os.size()>0) {
				try {
					crmOrderDao.batchSaveOrUpdate(os, 500);
				} catch (Exception e) {
					// TODO: handle exception
					logger.warn(e.getMessage(),e);
				}
			}
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			logger.warn("Poc信贷逾期还款的操作（downloadFileAC01）抛出异常---");
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "信贷订单自动还款，生成Poc信贷逾期还款的操作（downloadFileAC01）抛出异常");
			smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
		}
		logger.warn("Poc逾期信贷订单的还款结束---");
	}
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
	public void EfAutopayPocBatchUpload(){
		logger.warn("Poc理财自动还款（批量划拨）上传文件开始----");
		Map pocMap = new HashMap();
		pocMap.put("code", "PW03");
		List<JSONObject> list = new ArrayList<JSONObject>();
		JSONArray flowRecordJsonArray = new JSONArray();
		try{
			//查找今天需要还款的理财明细并通过债券转让的方式还款
			//1.查找今日待还
			List<Map> efPaycontrols =  bgEfOrderService.getCurrentControlsPoc();
			//踢出资产包相关数据
			crmOrderService.removePkgCrmOrders(efPaycontrols);
			//如果无数据则结束
			if(ListTool.isNullOrEmpty(efPaycontrols)){logger.warn("Poc理财自动还款（批量划拨）结束----");return;}
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
					String out_cust_no = StaticData.creditAccount;// 付款方登录名
					String out_cust_name = StaticData.creditAccountName;// 付款方中文名称
					String int_cust_no = efPaycontrol.get("fy_account").toString().trim();// 收款方登录名
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
							efFundRecordService.addNormal(efP);
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
						if(BusinessTool.isProfessionalInvestor(efPaycontrol.get("investor_type"))){
							instantlyFreeze = "0";
						}
					}
					String masSource = out_cust_no + 
									"|" + out_cust_name + 
									"|" + fromFreeze + 
									"|" + int_cust_no + 
									"|" + int_cust_name + 
									"|" + instantlyFreeze + 
									"|" + money + 
									"|" + remark + "|";
					JSONObject uploadJson = new JSONObject();
					uploadJson.put("macSource", masSource);
					uploadJson.put("remark", remark);
					uploadJson.put("serialNumber", UUID.randomUUID().toString());
					list.add(uploadJson);
					//记录中
					flowRecordJsonArray.add(flowRecordJson);
					//如果存在邀请人  并且是直投
					if(!ChkUtil.isEmpty(efPaycontrol.get("referee_info_id")) && "1".equals(efPaycontrol.get("investment_model"))){
						Map refereeInfoMap = bgEfOrderService.getRefereeInfoMap(efPaycontrol.get("referee_info_id").toString());
						// 计算邀请人金额
						Double refMoney = NumberFormat.format(ArithUtil.mul(ArithUtil.add(NumberUtil.parseDouble(efPaycontrol.get("surplus_interest")), NumberUtil.parseDouble(efPaycontrol.get("coupon_interest"))),
								ArithUtil.div(NumberUtil.parseDouble(efPaycontrol.get("referee_income_scale")), 100d)));
						String refFyAccount = refereeInfoMap.get("fy_account").toString();
						String refCustName = refereeInfoMap.get("cust_name").toString();
						String refReamrk = "auto_bepcrefer_id:" + efPaycontrol.get("id");
						String refereeSource = out_cust_no + "|" + out_cust_name + "|0|" + refFyAccount + "|" + refCustName + "|0|" + refMoney + "|" + refReamrk + "|";
						if (refMoney == 0) {//金额等于0不进行处理
							continue;
						}
						JSONObject refuploadJson = new JSONObject();
						refuploadJson.put("macSource", refereeSource);
						refuploadJson.put("remark", refReamrk);
						refuploadJson.put("serialNumber", UUID.randomUUID().toString());
						list.add(refuploadJson);
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
				SmsUtil.sendFlowRecord(flowRecordJsonArray);
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
	//原始上传备份
	public void EfAutopayPocBatchUploadBack(){
		logger.warn("Poc理财自动还款（批量划拨）上传文件开始----");
		Map pocMap = new HashMap();
		pocMap.put("code", "PW03");
		List<String> list = new ArrayList<String>();
		try{
			//查找今天需要还款的理财明细并通过债券转让的方式还款
			//1.查找今日待还
			List<Map> efPaycontrols =  bgEfOrderService.getCurrentControlsPoc();
			//如果无数据则结束
			if(ListTool.isNullOrEmpty(efPaycontrols)){logger.warn("Poc理财自动还款（批量划拨）结束----");return;}
//			try {
//				bgEfOrderService.executeBatchUpdateOperateType();
//			} catch (Exception e) {
//				logger.warn(e.getMessage(),e);
//				logger.warn("理财明细在修改自动状态时失败!");
//			}
			List<Object> bgEfPaycontrols = new ArrayList<Object>();
			for(Map efPaycontrol:efPaycontrols){
				//更新明细，表示该条数据已被自动还款处理
				if(efPaycontrol.containsKey("id")){
					BgEfPaycontrol bec =  (BgEfPaycontrol) bgEfOrderService.findById(BgEfPaycontrol.class, efPaycontrol.get("id").toString());
					try{
						bec.setOperateType("0");// 0自动还款  1手动还款
//						efPaycontrolService.update(bec);
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
					String out_cust_no = StaticData.creditAccount;// 付款方登录名
					String out_cust_name = StaticData.creditAccountName;// 付款方中文名称
					String int_cust_no = efPaycontrol.get("fy_account").toString().trim();// 收款方登录名
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
					String remark = "bg_ef_paycontrol_id:" + efPaycontrol.get("id");
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
							efFundRecordService.addNormal(efP);
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
					}
					String masSource = out_cust_no + 
							"|" + out_cust_name + 
							"|" + fromFreeze + 
							"|" + int_cust_no + 
							"|" + int_cust_name + 
							"|" + instantlyFreeze + 
							"|" + money + 
							"|" + remark + "|";
					list.add(masSource);
					//如果存在邀请人  并且是直投
					if(!ChkUtil.isEmpty(efPaycontrol.get("referee_info_id")) && "1".equals(efPaycontrol.get("investment_model"))){
						Map refereeInfoMap = bgEfOrderService.getRefereeInfoMap(efPaycontrol.get("referee_info_id").toString());
						// 计算邀请人金额
						Double refMoney = NumberFormat.format(ArithUtil.mul(ArithUtil.add(NumberUtil.parseDouble(efPaycontrol.get("surplus_interest")), NumberUtil.parseDouble(efPaycontrol.get("coupon_interest"))),
								ArithUtil.div(NumberUtil.parseDouble(efPaycontrol.get("referee_income_scale")), 100d)));
						String refFyAccount = refereeInfoMap.get("fy_account").toString();
						String refCustName = refereeInfoMap.get("cust_name").toString();
						String refReamrk = "bepc_referee_info_id:" + efPaycontrol.get("id");
						String refereeSource = out_cust_no + "|" + out_cust_name + "|0|" + refFyAccount + "|" + refCustName + "|0|" + refMoney + "|" + refReamrk + "|";
						if (refMoney == 0) {//金额等于0不进行处理
							continue;
						}
						list.add(refereeSource);
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
				//如果大于一万条数据，每一万条数据上传一次文件
				if (list.size() > THRESHOLD) {
					List<String> subList=new ArrayList<String>();
					for (int i = 0, n = list.size(); i < n; i++) {
						subList.add(list.get(i));
						if (i > 0 && i % THRESHOLD == 0) {
							pocMap.put("data", subList);
							// 准备请求fuyou
							logger.warn("请求POC，上传划拨文件-上传数据:"+subList.size()+"条--");
							String response = pocTool.connectToPoc("uploadFilePW03", pocMap);
							logger.warn("上传划拨文件完成---");
							JSONObject json = JSONObject.fromObject(response);
							String responseCode = json.getString("responseCode");
							// 请求失败
							if ("0".equals(responseCode)) {
								logger.warn("Poc理财自动还款（批量划拨）（uploadFilePW03）失败---");
								logger.warn("Poc理财自动还款（批量划拨）结束---");
								JSONObject smsJson = new JSONObject();
								smsJson.put("text", "Poc理财自动还款（批量划拨）（uploadFilePW03）失败");
								smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
								return;
							}
							if (json.containsKey("fileName") && ChkUtil.isNotEmpty(json.getString("fileName"))) {
								String fileName = json.getString("fileName");
								PocBatchRecord pocBatchRecord = new PocBatchRecord();
								pocBatchRecord.setTransferbuFileName(fileName);// 预授权文件名
								pocBatchRecord.setCreateTime(DateUtil.getCurrentTime());
								pocBatchRecord.setType("0");
								pocBatchRecord.setProcess("2");// 2划拨文件上传
								crmOrderDao.add(pocBatchRecord);
							}
							subList.clear();
							continue;
						}
						//循环到最后的时候 全部上传
						if (i==n-1) {
							pocMap.put("data", subList);
							// 准备请求fuyou
							logger.warn("请求POC，上传划拨文件-上传数据:"+subList.size()+"条--");
							String response = pocTool.connectToPoc("uploadFilePW03", pocMap);
							logger.warn("上传划拨文件完成---");
							JSONObject json = JSONObject.fromObject(response);
							String responseCode = json.getString("responseCode");
							// 请求失败
							if ("0".equals(responseCode)) {
								logger.warn("Poc理财自动还款（批量划拨）（uploadFilePW03）失败---");
								logger.warn("Poc理财自动还款（批量划拨）结束---");
								JSONObject smsJson = new JSONObject();
								smsJson.put("text", "Poc理财自动还款（批量划拨）（uploadFilePW03）失败");
								smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
								return;
							}
							if (json.containsKey("fileName") && ChkUtil.isNotEmpty(json.getString("fileName"))) {
								String fileName = json.getString("fileName");
								PocBatchRecord pocBatchRecord = new PocBatchRecord();
								pocBatchRecord.setTransferbuFileName(fileName);// 划拨文件名
								pocBatchRecord.setCreateTime(DateUtil.getCurrentTime());
								pocBatchRecord.setType("0");
								pocBatchRecord.setProcess("2");// 2划拨文件上传
								crmOrderDao.add(pocBatchRecord);
							}
							subList.clear();
						}
					}
				}else{
					pocMap.put("data", list);
					// 准备请求fuyou
					logger.warn("请求POC，上传划拨文件-上传数据:"+list.size()+"条--");
					String response = pocTool.connectToPoc("uploadFilePW03", pocMap);
					logger.warn("上传划拨文件完成---");
					JSONObject json = JSONObject.fromObject(response);
					String responseCode = json.getString("responseCode");
					// 请求失败重新请求
					if ("0".equals(responseCode)) {
						logger.warn("Poc理财自动还款（批量划拨）（uploadFilePW03）上传文件失败---");
						logger.warn("Poc理财自动还款（批量划拨）结束---");
						JSONObject smsJson = new JSONObject();
						smsJson.put("text", "Poc理财自动还款（批量划拨）（uploadFilePW03）上传文件失败");
						smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
						return;
					}
					if (json.containsKey("fileName") && ChkUtil.isNotEmpty(json.getString("fileName"))) {
						String fileName=json.getString("fileName");
						PocBatchRecord pocBatchRecord = new PocBatchRecord();
						pocBatchRecord.setTransferbuFileName(fileName);//划拨文件名
						pocBatchRecord.setCreateTime(DateUtil.getCurrentTime());
						pocBatchRecord.setType("0");
						pocBatchRecord.setProcess("2");//2划拨文件上传
						crmOrderDao.add(pocBatchRecord);
					}
				}
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
	/**
	 * 功能说明：POC理财还款----批量下载文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月15日 15:49:01
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void EfAutopayPocBatchDownload() {
		logger.warn("Poc理财自动还款（下载批量划拨文件、更新数据）开始----");
		List<Object> os = new ArrayList<Object>();
		AutoPocInvokeRecord invokeRecord = null;
		try {
			// 查找划拨的文件名
			String sql = "select id,transferbu_file_name,process,create_time from poc_batch_record where process='2' and type='0'";
			List<Map> transferbuFileNames = crmOrderDao.queryBySqlReturnMapList(sql);
			for (Map map : transferbuFileNames) {
				String transferbuFileName = map.get("transferbu_file_name").toString();
				String id = map.get("id").toString();
				Map paramMap = new HashMap();
				paramMap.put("fileName", transferbuFileName);// 划拨文件名
				logger.warn("Poc理财自动还款（下载批量划拨文件、更新数据）poc_batch_record_id:" + id + ",下载划拨文件：" + transferbuFileName + "---");
				String response = pocTool.connectToPoc("downloadFileByName", paramMap);
				JSONObject json = JSONObject.fromObject(response);
				String responseCode = json.getString("responseCode");
				// 请求失败
				while ("0".equals(responseCode)) {
					logger.warn("Poc理财自动还款（下载批量划拨文件、更新数据）（downloadFilePW03）下载划拨文件失败,poc_batch_record_id:" + id + ",划拨文件：" + transferbuFileName + "---");
					logger.warn("Poc理财自动还款（下载批量划拨文件、更新数据）结束---");
					JSONObject smsJson = new JSONObject();
					smsJson.put("text", "Poc理财自动还款（更新数据、提交批量冻结）（downloadFilePW03）下载划拨文件失败,poc_batch_record_id:" + id + ",划拨文件：" + transferbuFileName);
					smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
					return;
				}
				// 如果数据为空，取文件名
				if (ChkUtil.isEmpty(json.get("data")) || json.getString("data").length() < 10) {
					if (json.containsKey("fileName") && ChkUtil.isNotEmpty(json.getString("fileName"))) {
						String fileName = json.getString("fileName");
						// 添加新记录
						PocBatchRecord pocBatchRecord = new PocBatchRecord();
						pocBatchRecord.setTransferbuFileName(fileName);// 划拨文件名
						pocBatchRecord.setCreateTime(DateUtil.getCurrentTime());
						pocBatchRecord.setProcess("2");// 2划拨文件上传
						pocBatchRecord.setType("0");
						crmOrderDao.add(pocBatchRecord);
						// 原记录进度改成异常提前结束
						String updateSql = "update poc_batch_record set process='-1',fail_remark='文件批量划拨失败' where id='" + id + "'";
						crmOrderDao.executeSql(updateSql);
					}
				} else {
					JSONArray array = json.getJSONArray("data");
					for (Object object : array) {
						if (ChkUtil.isEmpty(object)) {
							continue;
						}
						// 批量划拨回盘:序号|付款方登录名|付款方中文名|付款资金来自冻结|收款方登录名|收款方中文名|收款后立即冻结|交易金额|备注|预授权合同号|返回码|说明|收款方证件号|付款方证件号|成功冻结付款冻结金
						String[] str = object.toString().split(Pattern.quote("|"));
						String out_cust_no = str[1]; // 付款方登录名
						String out_cust_name = str[2]; // 付款方中文名称
						String int_cust_no = str[4]; // 收款方登录名
						String int_cust_name = str[5]; // 收款方中文名称
						String amt = str[7]; // 交易金额
						String remark = str[8]; // 备注信息
						String returnCode = str[10]; // 返回码
						String returnInfo = str[11]; // 说明
						String int_cust_ic = str[12]; // 收款方证件号
						String out_cust_ic = str[13]; // 付款方证件号
						// String succeedFreezeAmt = str[14]; // 成功冻结付款冻结金

						String efPaycontrolId = remark.split(":")[1];

						invokeRecord = new AutoPocInvokeRecord();// 初始化POC调用记录
						invokeRecord.setBgEfPaycontrolId(efPaycontrolId);// 理财明细id
						invokeRecord.setReturnCode(returnCode);// 返回码
						invokeRecord.setReturnInfo(returnInfo);// 返回信息
						invokeRecord.setAmount(Double.parseDouble(amt));// 金额
						invokeRecord.setInvokeTime(DateUtil.getCurrentTime());
						if ("bg_ef_paycontrol_id".equals(remark.split(":")[0])) {
							invokeRecord.setRepayType(2);// 还款类型：理财还款划拨
						} else {
							invokeRecord.setRepayType(6);// 还款类型:邀请人
						}
						if ("0000".equals(returnCode)) {
							invokeRecord.setState(1);// 执行状态
							if ("bg_ef_paycontrol_id".equals(remark.split(":")[0])) {
								Map efPaycontrol = bgEfOrderService.getCurrentImmeControlsPocById(efPaycontrolId).get(0);
								String investment_model = efPaycontrol.get("investment_model").toString();
								// 直接更新数据
								if (ChkUtil.isNotEmpty(investment_model) && "1".equals(investment_model)) {
									// 如果成功需要记录并修改明细状态
									efPaycontrol.put("POC", "1");
									efPaycontrol.put("isNewPoc", "1");
									JSONObject refJson = bgEfOrderService.cleanEfPaycontrol(efPaycontrol);
									// 定投更新数据
								} else if (ChkUtil.isNotEmpty(investment_model) && "2".equals(investment_model)) {
									// 如果成功需要记录并修改明细状态
									// 开始进行明细转移和状态修改和记录
									efPaycontrol.put("onLine", "0");
									bgEfOrderService.normalRepay(efPaycontrol);
									// 开始进行可用资金存储
									efFundRecordService.addNormal(efPaycontrol);
								}
							}
						} else {
							invokeRecord.setState(0);// 执行状态
							JSONObject smsJson = new JSONObject();
							if ("bg_ef_paycontrol_id".equals(remark.split(":")[0])) {
								logger.warn("Poc理财自动还款划拨操作失败：" + remark + "，失败原因：" + returnInfo);
								logger.warn("Poc投资人本金还款失败!理财明细:id=" + efPaycontrolId);
								smsJson.put("text", "Poc投资人本金还款失败!(划拨操作)理财明细:id=" + efPaycontrolId);
								// 邀请人
							} else if ("bepc_referee_info_id".equals(remark.split(":")[0])) {
								logger.warn("直投Poc投资人邀请人还款失败!理财明细:id=" + efPaycontrolId);
								smsJson.put("text", "直投Poc投资人邀请人本金还款失败!（划拨操作）理财明细:id=" + efPaycontrolId);
							}
							smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
						}
						os.add(invokeRecord);
					}
					if (os.size() > 0) {
						try {
							crmOrderDao.batchSaveOrUpdate(os, 500);
						} catch (Exception e) {
							// TODO: handle exception
							logger.warn(e.getMessage(), e);
						}
					}

					// 更新记录表
					String updateSql = "update poc_batch_record set process='4' where id='" + id + "'";
					crmOrderDao.executeSql(updateSql);
				}
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			logger.warn("Poc理财自动还款（下载批量划拨文件、更新数据）抛出异常---");
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "信贷订单自动还款，Poc理财自动还款（下载批量划拨文件、更新数据）抛出异常");
			smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
		}
		logger.warn("Poc理财自动还款（下载批量划拨文件、更新数据）结束----");
	}
	/**
	 * 功能说明：POC理财还款----批量预授权
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
	public void EfAutopayPocBatchPreAuth(){
		logger.warn("Poc理财自动还款（批量预授权）开始----");
		//序号|付款方登录名|付款方中文名称|收款方登录名|收款方中文名称|预授权金额|备注信息
		//示例:0001|13912345678|王二|14112123123|张三|1000|备注
		Map pocMap = new HashMap();
		pocMap.put("code", "PW13");
		List<String> list = new ArrayList<String>();
		try{
			//查找今天需要还款的理财明细并通过债券转让的方式还款
			//1.查找今日待还
			List<Map> efPaycontrols =  bgEfOrderService.getCurrentControlsPoc();
			//如果无数据则结束
			if(ListTool.isNullOrEmpty(efPaycontrols)){logger.warn("Poc理财自动还款（批量预授权）结束----");return;}
			for(Map efPaycontrol:efPaycontrols){
				//更新明细，表示该条数据已被自动还款处理
				if(efPaycontrol.containsKey("id")){
					BgEfPaycontrol bec =  (BgEfPaycontrol) bgEfOrderService.findById(BgEfPaycontrol.class, efPaycontrol.get("id").toString());
					try{
						bec.setOperateType("0");// 0自动还款  1手动还款
						efPaycontrolService.update(bec);
					}catch (Exception e) {
						logger.warn("理财明细在修改自动状态时失败!理财明细id:"+efPaycontrol.get("id").toString());
					}
				}
			}
			for(Map efPaycontrol:efPaycontrols){
				String masSource = "";
				String out_cust_no = StaticData.creditAccount;// 付款方登录名
				masSource += out_cust_no + "|";
				String out_cust_name = StaticData.creditAccountName;// 付款方中文名称
				masSource += out_cust_name + "|";
				String int_cust_no = efPaycontrol.get("fy_account").toString().trim();// 收款方登录名
				masSource += int_cust_no + "|";
				String int_cust_name = efPaycontrol.get("cust_name").toString().trim();// 收款方中文名称
				masSource += int_cust_name + "|";
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
				masSource += money + "|";
				String remark = "bg_ef_paycontrol_id:" + efPaycontrol.get("id");
				masSource += remark;
				if (ChkUtil.isEmpty(out_cust_no) || ChkUtil.isEmpty(out_cust_name) || ChkUtil.isEmpty(int_cust_no) || ChkUtil.isEmpty(int_cust_name)) {
					continue;
				}
				list.add(masSource);
				//如果存在邀请人  并且是直投
				if(!ChkUtil.isEmpty(efPaycontrol.get("referee_info_id")) && "1".equals(efPaycontrol.get("investment_model"))){
					Map refereeInfoMap = bgEfOrderService.getRefereeInfoMap(efPaycontrol.get("referee_info_id").toString());
					// 计算邀请人金额
					Double refMoney = NumberFormat.format(ArithUtil.mul(ArithUtil.add(NumberUtil.parseDouble(efPaycontrol.get("surplus_interest")), NumberUtil.parseDouble(efPaycontrol.get("coupon_interest"))),
							ArithUtil.div(NumberUtil.parseDouble(efPaycontrol.get("referee_income_scale")), 100d)));
					String refFyAccount = refereeInfoMap.get("fy_account").toString();
					String refCustName = refereeInfoMap.get("cust_name").toString();
					String refReamrk = "bepc_referee_info_id:" + efPaycontrol.get("id");
					String refereeSource = StaticData.creditAccount + "|" + StaticData.creditAccountName + "|" + refFyAccount + "|" + refCustName + "|" + refMoney + "|" + refReamrk;
					list.add(refereeSource);
				}
			}
			if (list.size()>0) {
				//如果大于一万条数据，每一万条数据上传一次文件
				if (list.size() > THRESHOLD) {
					List<String> subList=new ArrayList<String>();
					for (int i = 0, n = list.size(); i < n; i++) {
						subList.add(list.get(i));
						if (i > 0 && i % THRESHOLD == 0) {
							pocMap.put("data", subList);
							// 准备请求fuyou
							logger.warn("请求POC，上传预授权文件-上传数据:"+subList.size()+"条--");
							String response = pocTool.connectToPoc("uploadFilePW13", pocMap);
							logger.warn("上传预授权文件完成---");
							JSONObject json = JSONObject.fromObject(response);
							String responseCode = json.getString("responseCode");
							// 请求失败
							if ("0".equals(responseCode)) {
								logger.warn("Poc理财自动还款（批量预授权）（uploadFilePW13）失败---");
								logger.warn("Poc理财自动还款（批量预授权）结束---");
								JSONObject smsJson = new JSONObject();
								smsJson.put("text", "Poc理财自动还款（批量预授权）（uploadFilePW13）失败");
								smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
								return;
							}
							if (json.containsKey("fileName") && ChkUtil.isNotEmpty(json.getString("fileName"))) {
								String fileName = json.getString("fileName");
								PocBatchRecord pocBatchRecord = new PocBatchRecord();
								pocBatchRecord.setPreauthFileName(fileName);// 预授权文件名
								pocBatchRecord.setCreateTime(DateUtil.getCurrentTime());
								pocBatchRecord.setType("0");
								pocBatchRecord.setProcess("1");// 1预授权文件上传
								crmOrderDao.add(pocBatchRecord);
							}
							subList.clear();
							continue;
						}
						//循环到最后的时候 全部上传
						if (i==n-1) {
							pocMap.put("data", subList);
							// 准备请求fuyou
							logger.warn("请求POC，上传预授权文件-上传数据:"+subList.size()+"条--");
							String response = pocTool.connectToPoc("uploadFilePW13", pocMap);
							logger.warn("上传预授权文件完成---");
							JSONObject json = JSONObject.fromObject(response);
							String responseCode = json.getString("responseCode");
							// 请求失败
							if ("0".equals(responseCode)) {
								logger.warn("Poc理财自动还款（批量预授权）（uploadFilePW13）失败---");
								logger.warn("Poc理财自动还款（批量预授权）结束---");
								JSONObject smsJson = new JSONObject();
								smsJson.put("text", "Poc理财自动还款（批量预授权）（uploadFilePW13）失败");
								smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
								return;
							}
							if (json.containsKey("fileName") && ChkUtil.isNotEmpty(json.getString("fileName"))) {
								String fileName = json.getString("fileName");
								PocBatchRecord pocBatchRecord = new PocBatchRecord();
								pocBatchRecord.setPreauthFileName(fileName);// 预授权文件名
								pocBatchRecord.setCreateTime(DateUtil.getCurrentTime());
								pocBatchRecord.setType("0");
								pocBatchRecord.setProcess("1");// 1预授权文件上传
								crmOrderDao.add(pocBatchRecord);
							}
							subList.clear();
						}
					}
				}else{
					pocMap.put("data", list);
					// 准备请求fuyou
					logger.warn("请求POC，上传预授权文件-上传数据:"+list.size()+"条--");
					String response = pocTool.connectToPoc("uploadFilePW13", pocMap);
					logger.warn("上传预授权文件完成---");
					JSONObject json = JSONObject.fromObject(response);
					String responseCode = json.getString("responseCode");
					// 请求失败重新请求
					if ("0".equals(responseCode)) {
						logger.warn("Poc理财自动还款（批量预授权）（uploadFilePW13）上传文件失败---");
						logger.warn("Poc理财自动还款（批量预授权）结束---");
						JSONObject smsJson = new JSONObject();
						smsJson.put("text", "Poc理财自动还款（批量预授权）（uploadFilePW13）上传文件失败");
						smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
						return;
					}
					if (json.containsKey("fileName") && ChkUtil.isNotEmpty(json.getString("fileName"))) {
						String fileName=json.getString("fileName");
						PocBatchRecord pocBatchRecord = new PocBatchRecord();
						pocBatchRecord.setPreauthFileName(fileName);//预授权文件名
						pocBatchRecord.setCreateTime(DateUtil.getCurrentTime());
						pocBatchRecord.setType("0");
						pocBatchRecord.setProcess("1");//1预授权文件上传
						crmOrderDao.add(pocBatchRecord);
					}
				}
			}
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			logger.warn("Poc理财自动还款（批量预授权）（uploadFilePW13）抛出异常---");
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "信贷订单自动还款，Poc理财自动还款（批量预授权）（uploadFilePW13）抛出异常");
			smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
		}
		logger.warn("Poc理财自动还款（批量预授权）结束----");
	}
	/**
	 * 功能说明：POC理财还款----批量划拨(空参数方法)
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月15日 15:49:01
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void EfAutopayPocBatchTransfer() {
		EfAutopayPocBatchTransfer("","0");
	}
	/**
	 * 功能说明：POC理财还款----批量划拨
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月15日 15:49:01
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void EfAutopayPocBatchTransfer(String uploadTime,String total) {
		logger.warn("Poc理财自动还款（批量划拨）开始----");
		List<Object> os=new ArrayList<Object>();
		AutoPocInvokeRecord invokeRecord=null;
		JSONObject paramJson=new JSONObject();
		try{
		//查找预授权的文件名
		String sql="select id,preauth_file_name,process,create_time from poc_batch_record where process='1' and type='0'";
		List<Map> preauthFileNames = crmOrderDao.queryBySqlReturnMapList(sql);
		for (Map map : preauthFileNames) {
			String preauthFileName=map.get("preauth_file_name").toString();
			String id=map.get("id").toString();
			//序号|付款方登录名|付款方中文名称|付款资金来自冻结|收款方登录名|收款后立即冻结|交易金额|备注信息|预授权合同号
			Map pocMap = new HashMap();
			pocMap.put("code", "PW03");
			List<String> list = new ArrayList<String>();
			Map paramMap = new HashMap();
			paramMap.put("fileName", preauthFileName);//预授权文件名
			/*paramMap.put("code", "PW13");
			if (ChkUtil.isNotEmpty(uploadTime)) {
				paramMap.put("date", uploadTime);
			}
			if (ChkUtil.isNotEmpty(total)) {
				paramMap.put("total", total);
			}*/
			//下载预授权文件
			logger.warn("Poc理财自动还款（批量划拨）poc_batch_record_id:"+id+",下载预授权文件名："+preauthFileName+"---");
			String response = pocTool.connectToPoc("downloadFileByName", paramMap);
			JSONObject json = JSONObject.fromObject(response);
			String responseCode = json.getString("responseCode");
			// 请求失败
			if ("0".equals(responseCode)) {
				logger.warn("Poc理财自动还款（批量划拨）（downloadFilePW13）预授权文件下载失败,预授权文件名："+preauthFileName+"---");
				logger.warn("Poc理财自动还款（批量划拨）结束---");
				JSONObject smsJson = new JSONObject();
				smsJson.put("text", "Poc理财自动还款（批量划拨）（downloadFilePW13）预授权文件下载失败,预授权文件名："+preauthFileName+"---");
				smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
				return;
			}
			//如果数据为空，取文件名
			if (ChkUtil.isEmpty(json.get("data")) || json.getString("data").length()<10) {
				if (json.containsKey("fileName") && ChkUtil.isNotEmpty(json.getString("fileName"))) {
					String fileName=json.getString("fileName");
					//添加新记录
					PocBatchRecord pocBatchRecord = new PocBatchRecord();
					pocBatchRecord.setPreauthFileName(fileName);//预授权文件名
					pocBatchRecord.setCreateTime(DateUtil.getCurrentTime());
					pocBatchRecord.setProcess("1");//1预授权文件上传
					pocBatchRecord.setType("0");
					crmOrderDao.add(pocBatchRecord);
					//原记录改成异常提前结束
					String updateSql="update poc_batch_record set process='-1',fail_remark='文件批量预授权失败' where id='"+id+"'";
					crmOrderDao.executeSql(updateSql);
				}
			}else{
			JSONArray array = json.getJSONArray("data");
			for (Object object : array) {
				if (ChkUtil.isEmpty(object)) {
					continue;
				}
				String[] str = object.toString().split(Pattern.quote("|"));
				String out_cust_no = str[1];	// 付款方登录名
				String out_cust_name = str[2];	// 付款方中文名称
				String fromFreeze = "0";		// 付款资金来自冻结
				String int_cust_no = str[3];	// 收款方登录名
				String int_cust_name = str[4];	// 收款方中文名称
				String instantlyFreeze = "0";	// 收款后立即冻结
				String amt = str[5];			// 预授权金额
				String remark = str[6];			// 备注信息
				String returnCode = str[7];		// 返回码
				String returnInfo = str[8];		// 说明
				String contract = str[9];		// 预授权合同号
				// 如果是下载全部文件,去除掉已处理成功的明细
				if ("1".equals(total)) {
					String efPaycontrolId = remark.split(":")[1];
					paramJson.put("efPaycontrolId", efPaycontrolId);
					if ("bg_ef_paycontrol_id".equals(remark.split(":")[0])) {
						paramJson.put("repayType", 2);//还款类型  1理财还款预授权 2理财还款划拨  3信贷正常还款代扣  4信贷逾期还款代扣 5理财邀请人预授权 6理财邀请人划拨
					}else{
						paramJson.put("repayType", 6);//还款类型:邀请人
					}
					List<Map> recordList = pocInvokeRecordService.getPocInvokeRecords(paramJson.toString());
					if (ListTool.isNotNullOrEmpty(recordList)) {
						logger.warn("该POC理财订单已划扣成功，不再进行划扣操作！,bg_ef_paycontrol_id:"+remark+"");
						continue;
					}
				}
				invokeRecord=new AutoPocInvokeRecord();//初始化POC调用记录
				invokeRecord.setBgEfPaycontrolId(remark.split(":")[1]);//理财明细id
				invokeRecord.setPreauthContract(contract);//预授权合同号
				invokeRecord.setReturnCode(returnCode);//返回码
				invokeRecord.setReturnInfo(returnInfo);//返回信息
				invokeRecord.setInvokeTime(DateUtil.getCurrentTime());
				if ("bg_ef_paycontrol_id".equals(remark.split(":")[0])) {
					invokeRecord.setRepayType(1);//还款类型
				}else{
					invokeRecord.setRepayType(5);//还款类型:邀请人
				}
				invokeRecord.setAmount(Double.parseDouble(amt));//金额
				if ("0000".equals(returnCode)) {
					invokeRecord.setState(1);//执行状态
					String masSource = out_cust_no + "|" + out_cust_name + "|" + fromFreeze + "|" + int_cust_no + "|" + int_cust_name + "|" + instantlyFreeze + "|" + amt + "|" + remark + "|" + contract;
					list.add(masSource);
				}else{
					invokeRecord.setState(0);
					logger.warn("Poc理财自动还款预授权操作失败："+remark+"，失败原因："+returnInfo);
					JSONObject smsJson = new JSONObject();
					smsJson.put("text", "Poc理财自动还款预授权操作失败："+remark+"，失败原因："+returnInfo);
					smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
				}
				os.add(invokeRecord);
			}
			if (os.size()>0) {
				/*try {
					crmOrderDao.batchSaveOrUpdate(os, 500);
				} catch (Exception e) {
					// TODO: handle exception
					logger.warn(e.getMessage(),e);
				}*/
			}
			if (array.size()>0 && list.size()>0) {
				pocMap.put("data", list);
				// 准备请求fuyou
				response = pocTool.connectToPoc("uploadFilePW03", pocMap);
				json = JSONObject.fromObject(response);
				responseCode = json.getString("responseCode");
				// 请求失败
				if ("0".equals(responseCode)) {
					logger.warn("Poc理财自动还款（批量划拨）（uploadFilePW03）上传划拨文件失败,poc_batch_record_id:"+id+"---");
					logger.warn("Poc理财自动还款（批量划拨）结束---");
					JSONObject smsJson = new JSONObject();
					smsJson.put("text", "Poc理财自动还款（批量划拨）（uploadFilePW03）上传划拨文件失败,poc_batch_record_id:"+id);
					smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
					return;
				}
				if (json.containsKey("fileName") && ChkUtil.isNotEmpty(json.getString("fileName"))) {
					String fileName=json.getString("fileName");
					//更新记录表
					String updateSql="update poc_batch_record set process='2',transferbu_file_name='"+fileName+"' where id='"+id+"'";
					crmOrderDao.executeSql(updateSql);
				}
			}
			}
		}
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			logger.warn("Poc理财自动还款（批量划拨）抛出异常---");
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "信贷订单自动还款，Poc理财自动还款（批量划拨）抛出异常");
			smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
		}		
		logger.warn("Poc理财自动还款（批量划拨）结束----");
	}
	/**
	 * 功能说明：POC理财还款----更新数据、批量冻结(空参数方法)
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月15日 15:49:01
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void EfAutopayPocBatchUpdate(){
		EfAutopayPocBatchUpdate("","0");
	}
	/**
	 * 功能说明：POC理财还款----更新数据、批量冻结
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月15日 15:49:01
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void EfAutopayPocBatchUpdate(String uploadTime,String total){
		logger.warn("Poc理财自动还款（更新数据、提交批量冻结）开始----");
		List<Object> os=new ArrayList<Object>();
		AutoPocInvokeRecord invokeRecord=null;
		try{
		// 查找划拨的文件名
		String sql = "select id,transferbu_file_name,process,create_time from poc_batch_record where process='2' and type='0'";
		List<Map> transferbuFileNames = crmOrderDao.queryBySqlReturnMapList(sql);
		for (Map map : transferbuFileNames) {
			String transferbuFileName = map.get("transferbu_file_name").toString();
			String id = map.get("id").toString();
			// 序号|冻结目标登录名|冻结目标中文名称|交易金额|备注信息
			Map pocMap = new HashMap();
			pocMap.put("code", "PWDJ");
			List<String> list = new ArrayList<String>();
			Map paramMap = new HashMap();
			paramMap.put("fileName", transferbuFileName);//划拨文件名
			/*paramMap.put("code", "PW03");
			if (ChkUtil.isNotEmpty(uploadTime)) {
				paramMap.put("date", uploadTime);
			}
			if (ChkUtil.isNotEmpty(total)) {
				paramMap.put("total", total);
			}*/
			logger.warn("Poc理财自动还款（更新数据、提交批量冻结）poc_batch_record_id:"+id+",下载划拨文件："+transferbuFileName+"---");
			String response = pocTool.connectToPoc("downloadFileByName", paramMap);
			JSONObject json = JSONObject.fromObject(response);
			String responseCode = json.getString("responseCode");
			// 请求失败
			while ("0".equals(responseCode)) {
				logger.warn("Poc理财自动还款（更新数据、提交批量冻结）（downloadFilePW03）下载划拨文件失败,poc_batch_record_id:"+id+",划拨文件："+transferbuFileName+"---");
				logger.warn("Poc理财自动还款（更新数据、提交批量冻结）结束---");
				JSONObject smsJson = new JSONObject();
				smsJson.put("text", "Poc理财自动还款（更新数据、提交批量冻结）（downloadFilePW03）下载划拨文件失败,poc_batch_record_id:"+id+",划拨文件："+transferbuFileName);
				smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
				return;
			}
			//如果数据为空，取文件名
			if (ChkUtil.isEmpty(json.get("data")) || json.getString("data").length()<10) {
				if (json.containsKey("fileName") && ChkUtil.isNotEmpty(json.getString("fileName"))) {
					String fileName=json.getString("fileName");
					// 根据划拨文件名找到预授权文件名
					String selectSql = "select preauth_file_name from poc_batch_record where id='"+id+"'";
					String preauth_file_name = crmOrderDao.queryBySqlReturnMapList(selectSql).get(0).get("preauth_file_name").toString();
					//添加新记录
					PocBatchRecord pocBatchRecord = new PocBatchRecord();
					pocBatchRecord.setPreauthFileName(preauth_file_name);//预授权文件名
					pocBatchRecord.setTransferbuFileName(fileName);//划拨文件名
					pocBatchRecord.setCreateTime(DateUtil.getCurrentTime());
					pocBatchRecord.setProcess("2");//2划拨文件上传
					pocBatchRecord.setType("0");
					crmOrderDao.add(pocBatchRecord);
					//原记录进度改成异常提前结束
					String updateSql="update poc_batch_record set process='-1',fail_remark='文件批量划拨失败' where id='"+id+"'";
					crmOrderDao.executeSql(updateSql);
				}
			}else{
			JSONArray array = json.getJSONArray("data");
			for (Object object : array) {
				if (ChkUtil.isEmpty(object)) {
					continue;
				}
				//批量划拨回盘:序号|付款方登录名|付款方中文名|付款资金来自冻结|收款方登录名|收款方中文名|收款后立即冻结|交易金额|备注|预授权合同号|返回码|说明|收款方证件号|付款方证件号|成功冻结付款冻结金
				String[] str = object.toString().split(Pattern.quote("|"));
				String out_cust_no = str[1];	// 付款方登录名
				String out_cust_name = str[2];	// 付款方中文名称
				String int_cust_no = str[4];	// 收款方登录名
				String int_cust_name = str[5];	// 收款方中文名称
				String amt = str[7];			// 交易金额
				String remark = str[8];			// 备注信息
				String returnCode = str[10];		// 返回码
				String returnInfo = str[11];		// 说明
				String int_cust_ic = str[12];		// 收款方证件号
				String out_cust_ic = str[13];		// 付款方证件号
				//			String succeedFreezeAmt = str[14];		// 成功冻结付款冻结金
				
				String efPaycontrolId=remark.split(":")[1];
				
				invokeRecord=new AutoPocInvokeRecord();//初始化POC调用记录
				invokeRecord.setBgEfPaycontrolId(efPaycontrolId);//理财明细id
				invokeRecord.setReturnCode(returnCode);//返回码
				invokeRecord.setReturnInfo(returnInfo);//返回信息
				invokeRecord.setAmount(Double.parseDouble(amt));//金额
				invokeRecord.setInvokeTime(DateUtil.getCurrentTime());
				if ("bg_ef_paycontrol_id".equals(remark.split(":")[0])) {
					invokeRecord.setRepayType(2);//还款类型
				}else{
					invokeRecord.setRepayType(6);//还款类型:邀请人
				}
				if ("0000".equals(returnCode)) {
					invokeRecord.setState(1);//执行状态
					if ("bg_ef_paycontrol_id".equals(remark.split(":")[0])) {
						Map efPaycontrol = bgEfOrderService.getCurrentImmeControlsPocById(efPaycontrolId).get(0);
						String investment_model = efPaycontrol.get("investment_model").toString();
						//直接更新数据不进行冻结
						if (ChkUtil.isNotEmpty(investment_model) && "1".equals(investment_model)) {
							//如果成功需要记录并修改明细状态
							efPaycontrol.put("POC","1");
							efPaycontrol.put("isNewPoc", "1");
							JSONObject refJson = bgEfOrderService.cleanEfPaycontrol(efPaycontrol);
						//定投更新数据并且冻结资金
						}else if (ChkUtil.isNotEmpty(investment_model) && "2".equals(investment_model)) {
							//如果成功需要记录并修改明细状态
							//开始进行明细转移和状态修改和记录
							efPaycontrol.put("onLine","0");
							bgEfOrderService.normalRepay(efPaycontrol);
							//开始进行可用资金存储
							efFundRecordService.addNormal(efPaycontrol);
							//然后进行冻结操作
							String masSource = int_cust_no + "|" + int_cust_name + "|" + amt + "|" + remark;
							list.add(masSource);
						}
					}
				}else{
					invokeRecord.setState(0);//执行状态
					JSONObject smsJson = new JSONObject();
					if ("bg_ef_paycontrol_id".equals(remark.split(":")[0])) {
						logger.warn("Poc理财自动还款划拨操作失败：" + remark + "，失败原因：" + returnInfo);
						logger.warn("Poc投资人本金还款失败!理财明细:id=" + efPaycontrolId);
						smsJson.put("text", "Poc投资人本金还款失败!(划拨操作)理财明细:id=" + efPaycontrolId);
						//邀请人
					}else if ("bepc_referee_info_id".equals(remark.split(":")[0])) {
						logger.warn("直投Poc投资人邀请人还款失败!理财明细:id="+efPaycontrolId);
						smsJson.put("text", "直投Poc投资人邀请人本金还款失败!（划拨操作）理财明细:id="+efPaycontrolId);
					}
					smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
				}
				os.add(invokeRecord);
			}
			if (os.size()>0) {
				try {
					crmOrderDao.batchSaveOrUpdate(os, 500);
				} catch (Exception e) {
					// TODO: handle exception
					logger.warn(e.getMessage(),e);
				}
			}
			if (list.size()>0) {
				pocMap.put("data", list);
				// 准备请求fuyou
				response = pocTool.connectToPoc("uploadFilePWDJ", pocMap);
				json = JSONObject.fromObject(response);
				responseCode = json.getString("responseCode");
				// 请求失败
				while ("0".equals(responseCode)) {
					logger.warn("Poc理财自动还款（更新数据、提交批量冻结）（uploadFilePWDJ）上传冻结失败，poc_batch_record_id:"+id+"---");
					logger.warn("Poc理财自动还款（更新数据、提交批量冻结）结束---");
					JSONObject smsJson = new JSONObject();
					smsJson.put("text", "Poc理财自动还款（更新数据、提交批量冻结）（uploadFilePWDJ）上传冻结文件失败，poc_batch_record_id:"+id);
					smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
					return;
				}
				if (json.containsKey("fileName") && ChkUtil.isNotEmpty(json.getString("fileName"))) {
					String fileName=json.getString("fileName");
					//更新记录表
					String updateSql="update poc_batch_record set process='3',freeze_file_name='"+fileName+"' where id='"+id+"'";
					crmOrderDao.executeSql(updateSql);
				}
			}else{
				// 更新记录表 直投划拨成功直接结束 不需要冻结
				String updateSql = "update poc_batch_record set process='4' where id='"+id+"'";
				crmOrderDao.executeSql(updateSql);
			}
			}
		}
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			logger.warn("Poc理财自动还款（更新数据、提交批量冻结）抛出异常---");
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "信贷订单自动还款，Poc理财自动还款（更新数据、提交批量冻结）抛出异常");
			smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
		}		
		logger.warn("Poc理财自动还款（更新数据、提交批量冻结）结束----");
	}
	/**
	 * 功能说明：POC理财还款----查询批量冻结结果(空参数方法)
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月15日 15:49:01
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void EfAutopayPocBatchFreeze(){
		EfAutopayPocBatchFreeze("","0");
	}
	/**
	 * 功能说明：POC理财还款----查询批量冻结结果
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年8月15日 15:49:01
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void EfAutopayPocBatchFreeze(String uploadTime,String total){
		logger.warn("Poc理财自动还款（查询批量冻结结果）开始----");
		try{
		// 查找划拨的文件名
		String sql = "select id,freeze_file_name,process,create_time from poc_batch_record where process='3' and type='0'";
		List<Map> freezeFileNames = crmOrderDao.queryBySqlReturnMapList(sql);
		for (Map map : freezeFileNames) {
			String freezeFileName = map.get("freeze_file_name").toString();
			String id = map.get("id").toString();
			//序号|冻结目标登录名|冻结目标中文名称|交易金额|备注信息
			Map paramMap = new HashMap();
			paramMap.put("fileName", freezeFileName);
			/*paramMap.put("code", "PWDJ");
			if (ChkUtil.isNotEmpty(uploadTime)) {
				paramMap.put("date", uploadTime);
			}
			if (ChkUtil.isNotEmpty(total)) {
				paramMap.put("total", total);
			}*/
			String response = pocTool.connectToPoc("downloadFileByName", paramMap);
			JSONObject json = JSONObject.fromObject(response);
			String responseCode = json.getString("responseCode");
			// 请求失败
			while ("0".equals(responseCode)) {
				logger.warn("Poc理财自动还款（查询批量冻结结果）（downloadFilePWDJ）下载冻结文件失败，poc_batch_record_id:"+id+"，冻结文件名:"+freezeFileName+"---");
				logger.warn("Poc理财自动还款（查询批量冻结结果）结束---");
				JSONObject smsJson = new JSONObject();
				smsJson.put("text", "Poc理财自动还款（查询批量冻结结果）（downloadFilePWDJ）下载冻结文件失败，poc_batch_record_id:"+id+"，冻结文件名:"+freezeFileName);
				smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
				return;
			}
			//如果数据为空，取文件名
			if (ChkUtil.isEmpty(json.get("data")) || json.getString("data").length()<10) {
				if (json.containsKey("fileName") && ChkUtil.isNotEmpty(json.getString("fileName"))) {
					String fileName=json.getString("fileName");
					// 根据冻结文件名查找 划拨文件名、预授权文件名
					String selectSql = "select preauth_file_name,transferbu_file_name from poc_batch_record where id='"+id+"'";
					Map m = crmOrderDao.queryBySqlReturnMapList(selectSql).get(0);
					String preauth_file_name = m.get("preauth_file_name").toString();
					String transferbu_file_name = m.get("transferbu_file_name").toString();
					//添加新记录
					PocBatchRecord pocBatchRecord = new PocBatchRecord();
					pocBatchRecord.setPreauthFileName(preauth_file_name);//预授权文件名
					pocBatchRecord.setTransferbuFileName(transferbu_file_name);//划拨文件名
					pocBatchRecord.setFreezeFileName(fileName);//冻结文件名
					pocBatchRecord.setCreateTime(DateUtil.getCurrentTime());
					pocBatchRecord.setProcess("3");//3冻结文件上传
					pocBatchRecord.setType("0");
					crmOrderDao.add(pocBatchRecord);
					//原记录进度改成异常提前结束
					String updateSql="update poc_batch_record set process='-1',fail_remark='文件批量冻结失败' where id='"+id+"'";
					crmOrderDao.executeSql(updateSql);
				}
			}else{
			JSONArray array = json.getJSONArray("data");
			for (Object object : array) {
				if (ChkUtil.isEmpty(object)) {
					continue;
				}
				// 批量冻结回盘:序号|冻结目标登录名|冻结目标中文名称|交易金额|备注|返回码|说明
				String[] str = object.toString().split(Pattern.quote("|"));
				String cust_no = str[1]; // 登录名
				String cust_name = str[2]; // 中文名称
				String amt = str[3]; // 交易金额
				String remark = str[4]; // 备注信息
				String returnCode = str[5]; // 返回码
				String returnInfo = str[6]; // 说明
				
				String efPaycontrolId = remark.split(":")[1];
				if ("0000".equals(returnCode)) {
					logger.warn("定投Poc投资人本金还款成功,在进行冻结时成功!理财明细:id=" + efPaycontrolId);
				} else {
					JSONObject smsJson = new JSONObject();
					logger.warn("定投Poc投资人本金还款成功,在进行冻结时失败!理财明细:id=" + efPaycontrolId);
					smsJson.put("text", "定投Poc投资人本金还款成功,在进行冻结时失败!理财明细:id=" + efPaycontrolId);
					smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
				}
				//进度改成已结束
				String updateSql="update poc_batch_record set process='4' where id='"+id+"'";
				crmOrderDao.executeSql(updateSql);
			}
			}
		}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			logger.warn("Poc理财自动还款（查询批量冻结结果）抛出异常---");
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "信贷订单自动还款，Poc理财自动还款（查询批量冻结结果）抛出异常");
			smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
		}
		logger.warn("Poc理财自动还款（查询批量冻结结果）结束----");
	}
}
