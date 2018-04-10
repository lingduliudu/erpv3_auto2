package com.apt.webapp.service.auto;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.apt.util.ListTool;
import com.apt.util.StaticData;
import com.apt.util.StringUtil;
import com.apt.util.WebServiceUtil;
import com.apt.util.date.DateUtil;
import com.apt.util.sms.SmsUtil;
import com.apt.webapp.dao.crm.ICrmOrderDao;
import com.apt.webapp.dao.crm.ICrmPaycontrolDao;
import com.apt.webapp.dao.ef.IZZLEfPaycontrolDao;
import com.apt.webapp.model.crm.CrmApplay;
import com.apt.webapp.model.crm.CrmOrder;
import com.apt.webapp.model.crm.CrmPaycontrol;
import com.apt.webapp.model.crm.CrmPayrecoder;
import com.apt.webapp.model.crm.TransPaymentRecord;
import com.apt.webapp.service.crm.IPsCheckMoneyService;
import com.apt.webapp.service.ef.IEfPaycontrolService;

/**
 * 功能说明：宝付代扣功能
 * 典型用法：
 * 特殊用法：	
 * @author wbk
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：下午5:12:39
 */
@Component
public class AutoRunBF
{
	//日志
	private Logger logger = LoggerFactory.getLogger(AutoRunBF.class);
	@Resource
	public ICrmOrderDao crmOrderDaoImpl;
	@Resource
	public ICrmPaycontrolDao crmPaycontrolDaoImpl;
	@Resource
	private IZZLEfPaycontrolDao efPaycontrolDao;
	@Resource
	private IEfPaycontrolService efPaycontrolService;
	@Resource
	private IPsCheckMoneyService psCheckMoneyService;
	
	/**
	 * 功能说明：boc通道宝付代扣功能(逾期订单)
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：下午5:15:30
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void toBFOverdueOrder()
	{
		// 信贷逾期订单代扣功能
		this.overdueOrder("BF");
	}
	
	/**
	 * 功能说明：boc通道宝付代扣功能(正常订单)
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：下午5:15:30
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void toBFNaturalOrder()
	{
		// 信贷正常订单代扣功能
		this.naturalOrder("BF");
	}
	
	/**
	 * 功能说明：boc通道HF(宝付)代扣功能(逾期订单)
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：下午5:15:30
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void toHFOverdueOrder()
	{
		// 信贷逾期订单代扣功能
		this.overdueOrder("HF");
	}
	
	/**
	 * 功能说明：boc通道HF(宝付)代扣功能(正常订单)
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：下午5:15:30
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void toHFNaturalOrder()
	{
		// 信贷正常订单代扣功能
		this.naturalOrder("HF");
	}
	
	/**
	 * 功能说明：信贷逾期订单代扣功能
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：下午5:22:47
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	private void overdueOrder(String type)
	{
		logger.warn("boc信贷逾期订单宝付代扣功能自动开始----");
		
		// 查询存在逾期信贷订单信息
		List<Map> crmOrdersMaps = crmOrderDaoImpl.getOverOrderBF("2", type);
		
		if (ListTool.isNotNullOrEmpty(crmOrdersMaps))
		{
			
			for (Map crmOrderMap : crmOrdersMaps)
			{
				logger.warn("boc信贷逾期订单宝付代扣功能自动开始----,处理的订单id:"+ crmOrderMap.get("crmOrderId"));
				
				try
				{
					String crmOrderId = (String) crmOrderMap.get("crmOrderId");
					String custId = (String) crmOrderMap.get("custId");
					String custInfoId = (String) crmOrderMap.get("custInfoId");
					String idCard = (String)crmOrderMap.get("idCard");				//身份证号
					String idHolder = (String)crmOrderMap.get("idHolder");			//持卡人姓名
					String mobile = (String)crmOrderMap.get("mobile");				//银行卡绑定手机号
					String accNo = (String)crmOrderMap.get("accNo");	//卡号
					String bankName = (String)crmOrderMap.get("bankName");	//银行
					//获得银行信息
					String payCode = this.banks(bankName);
					//银行卡为空的情况下
					if (StringUtil.isEmptyOrNull(payCode))
					{
						logger.warn("boc信贷逾期订单银行卡信息不能为空,订单还款失败,订单id:"+ crmOrderId);
						continue;
					}
					//信贷订单
					CrmOrder crmOrder = (CrmOrder) efPaycontrolDao.findById(CrmOrder.class, crmOrderId);
					if (crmOrder == null)
					{
						//信贷订单不存在
						logger.warn("boc信贷逾期订单,信贷订单信息不存在,订单还款失败,订单id:"+ crmOrderId);
						continue;
					}
					if (StringUtil.isNotEmptyOrNull(crmOrderId))
					{
						// 查询存在的逾期信贷订单明细
						List<Map> crmPaycontrolsMaps = crmOrderDaoImpl.getOverPaycontrolBF(crmOrderId, "2");
						if (ListTool.isNotNullOrEmpty(crmPaycontrolsMaps))
						{
							//申请单
							CrmApplay apply = (CrmApplay) efPaycontrolDao.findById(CrmApplay.class,crmOrder.getCrmApplayId());

							for (Map crmPaycontrol : crmPaycontrolsMaps)
							{
								CrmPaycontrol paycontrol = (CrmPaycontrol)crmPaycontrolDaoImpl.findById(CrmPaycontrol.class , (String)crmPaycontrol.get("id"));
								
								// 如果订单免息则不进行订单扣款操作
								if (paycontrol == null || paycontrol.getExemptStatus() == 1)
								{
									logger.warn("boc信贷逾期订单,该订单存在免息操作,订单还款失败,订单id:"+ crmOrderMap.get("id")+"明细id"+paycontrol.getId());
									continue;
								}
								
								Double remainManageFee = paycontrol.getRemainManageFee();
								Double overdueViolateMoney = paycontrol.getOverdueViolateMoney();
								String id = paycontrol.getId();
								Double remainAccrual = paycontrol.getRemainAccrual();
								Double remainCapital = paycontrol.getRemainCapital();
								Double remainInterest = paycontrol.getRemainInterest();
								
								//计算金额总数
								BigDecimal frozenMoney = (BigDecimal) crmPaycontrol.get("frozenMoney");
								
								SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
								String time = sdf.format(System.currentTimeMillis());
								String transSerialNo = "BFDK" + System.currentTimeMillis(); 
								//银行卡扣款方法
								JSONObject bocResultJson = this.create(crmOrderId, transSerialNo, payCode, accNo, idCard, idHolder, mobile, frozenMoney, paycontrol, time);		
								
								String resultCode = (String)bocResultJson.get("resultCode"); 
								// 需要查询的返回条件
								Map<String, String> codes = this.codes();
								// 判断查询结果
								if (resultCode != null && resultCode != "")
								{
									//判断是否存在失败信息
									if (codes.get(resultCode) != null)
									{
										//未知原因需要再次查询订单
										JSONObject jSONObject = this.bFWithholdingFindFunction(mobile,crmOrderId, transSerialNo, paycontrol.getId(), time);
										
										if (!"0000".equals(jSONObject.get("resultCode")) || "0".equals(jSONObject.get("responseCode")))
										{
											//还款失败
											logger.warn("BOC正常还款信贷订单宝付代扣失败,明细id:" + crmPaycontrol.get("id")+",BOC返回成功!");
											break;
										}
									}
									
									//操作成功信息
									if (!"0000".equals(resultCode)&&!"BF00114".equals(resultCode))
									{
										//还款失败
										logger.warn("BOC正常还款信贷订单宝付代扣失败,明细id:" + crmPaycontrol.get("id")+",BOC返回成功!");
										break;
									}
								}
								else
								{
									//查询方法
									JSONObject jSONObject = this.bFWithholdingFindFunction(mobile,crmOrderId, transSerialNo, paycontrol.getId(), time);
									
									if (!"0000".equals(jSONObject.get("resultcode")) || "0".equals(jSONObject.get("responseCode")))
									{
										//还款失败
										logger.warn("BOC正常还款信贷订单宝付代扣失败,明细id:" + crmPaycontrol.get("id")+",BOC返回成功!");
										break;
									}
								}
								
								//开始产生资金记录
								JSONObject checkMoneyJson = new JSONObject();
								checkMoneyJson.put("cardNo",accNo);
								checkMoneyJson.put("cust_info_id",paycontrol.getCustInfoId());			//客户id
								checkMoneyJson.put("money", frozenMoney);		// 金额
								checkMoneyJson.put("money_type","2"); 			// 1金账户2,银行卡3,现场交钱4,电子账户
								checkMoneyJson.put("operation_type","2"); 		//1入账2出账
								checkMoneyJson.put("status","0");				//1成功0失败
								checkMoneyJson.put("person_type","2");			//1,投资人2借款人3,邀请人
								checkMoneyJson.put("type","3");					//1,提前结清2提前收回3,逾期还款4,部分逾期,5正常还款	
								checkMoneyJson.put("crm_order_id", paycontrol.getCrmOrderId());
								String checkMoneyId = psCheckMoneyService.save(checkMoneyJson);
								
//								//资金记录修改
								psCheckMoneyService.updateStatusById(checkMoneyId);
								
								//产生记录
								CrmPayrecoder cpr = new CrmPayrecoder();
								cpr.setCertificateUrl("");
								cpr.setCreateTime(DateUtil.getCurrentTime());
								cpr.setCrmOrderId(crmOrderId);
								cpr.setCustId(custId);
								cpr.setCustInfoId(custInfoId);
								cpr.setEmpId("");
								cpr.setManageFee(remainManageFee);
								cpr.setOverdueInterest(0d);
								cpr.setOverdueViolateMoney(overdueViolateMoney);
								cpr.setPaycontrolId(id);
								cpr.setPrepaymentViolateMoney(0d);
								cpr.setRemainFee(0d);
								cpr.setRemark(custInfoId);
								cpr.setRepaymentType(0);
								cpr.setShouldAccrual(remainAccrual);
								cpr.setShouldCAPITAL(remainCapital);
								cpr.setShouldInterest(remainInterest);
								cpr.setShouldMONEY(frozenMoney.doubleValue());
								cpr.setOperationPlatform("200012-0003");//自动还款  200012-0003
								cpr.setPaymentPlatform("200013-0003");//baofoo 200013-0003
								if(crmPaycontrol.get("clearing_channel")!= null && "200007-0003".equals(crmPaycontrol.get("clearing_channel").toString()))
								{
									cpr.setPaymentChannel("200014-0001");//银行卡代扣200014-0002
								}
								else
								{
									cpr.setPaymentChannel("200014-0002");//第三方虚拟账户200014-0001
								}
								
								efPaycontrolDao.add(cpr);
								
								//更新信贷还款明细
								paycontrol.setStatus(1);					//还款状态 0：待还，1:结清，2:逾期,3:提前结清,4:失效
								paycontrol.setRemainAccrual(0d);			//剩余利息
								paycontrol.setRemainCapital(0d);			//剩余本金
								paycontrol.setRemainInterest(0d);			//还款期数
								paycontrol.setRemainManageFee(0d);			//剩余管理费
								paycontrol.setOverdueViolateMoney(0d);		//逾期违约金
								paycontrol.setReplaceStatus(0);				//是否垫付
								
								efPaycontrolDao.update(paycontrol);
								
								//更新资产包信息
								efPaycontrolService.updateCrmorder(crmOrder.getId());
							}
							// 更新对应订单信息
							efPaycontrolService.updateInfo(crmOrder, apply);
						}
					}
				}
				catch (Exception e)
				{
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", crmOrderMap.get("id"));
					smsJson.put("text", "BOC逾期信贷订单还款异常,明细id:" + crmOrderMap.get("id"));
					SmsUtil.senErrorMsg(smsJson);
					logger.warn(e.getMessage(),e);
					logger.warn("BOC逾期信贷订单还款异常,明细id:"+crmOrderMap.get("id")+",BOC返回成功!");
				}
			}
		}
		
		logger.warn("boc信贷逾期订单宝付代扣功能还款结束---");
	}
	
	/**
	 * 功能说明：信贷正常订单代扣功能
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：下午5:24:09
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	private void naturalOrder(String type)
	{
		logger.warn("boc信贷正常订单宝付代扣功能还款开始---");
		// 1.查询存在正常信贷订单信息
		List<Map> crmOrdersMaps = crmOrderDaoImpl.getOverOrderBF("0", type);
		
		if (ListTool.isNotNullOrEmpty(crmOrdersMaps))
		{
			for (Map crmOrderMap : crmOrdersMaps)
			{
				logger.warn("boc信贷正常订单宝付代扣功能自动开始----,处理的订单id:"+ crmOrderMap.get("crmOrderId"));

				try
				{
					String crmOrderId = (String) crmOrderMap.get("crmOrderId");
					String custId = (String) crmOrderMap.get("custId");
					String custInfoId = (String) crmOrderMap.get("custInfoId");
					String idCard = (String)crmOrderMap.get("idCard");				//身份证号
					String idHolder = (String)crmOrderMap.get("idHolder");			//持卡人姓名
					String mobile = (String)crmOrderMap.get("mobile");				//银行卡绑定手机号
					String accNo = (String)crmOrderMap.get("accNo");	//卡号
					String bankName = (String)crmOrderMap.get("bankName");	//银行
					String payCode = this.banks(bankName);
					//银行卡为空的情况下
					if (StringUtil.isEmptyOrNull(payCode))
					{
						logger.warn("boc信贷正常订单银行卡信息不能为空,订单还款失败,订单id:"+ crmOrderId);
						continue;
					}
					
					//信贷订单
					CrmOrder crmOrder = (CrmOrder) efPaycontrolDao.findById(CrmOrder.class, crmOrderId);
					if (crmOrder == null)
					{
						//信贷订单不存在
						logger.warn("boc信贷正常订单银行卡信息不能为空,订单还款失败,订单id:"+ crmOrderId);
						continue;
					}
					
					if (StringUtil.isNotEmptyOrNull(crmOrderId))
					{
						// 查询存在的正常还款订单信息
						List<Map> crmPaycontrolsMaps = crmOrderDaoImpl.getOverPaycontrolBF(crmOrderId, "0");
						
						if (ListTool.isNotNullOrEmpty(crmPaycontrolsMaps))
						{
							Map crmPaycontrol = crmPaycontrolsMaps.get(0);
							
							//申请单
//							TransPaymentRecord
							CrmApplay apply = (CrmApplay) efPaycontrolDao.findById(CrmApplay.class,crmOrder.getCrmApplayId());
							
							CrmPaycontrol paycontrol = (CrmPaycontrol)crmPaycontrolDaoImpl.findById(CrmPaycontrol.class , (String)crmPaycontrol.get("id"));
							BigDecimal frozenMoney = (BigDecimal) crmPaycontrol.get("frozenMoney");//计算金额总数
							
							// 如果订单免息则不进行订单扣款操作
							if (crmPaycontrol != null && paycontrol.getExemptStatus() != 1)
							{
								// 正常还款订单如果存在存在逾期订单
								if (paycontrol.getStatus() == 2)
								{
									logger.warn("BOC正常还款信贷订单还款订单存在未还款的逾期订单,明细id:" + crmPaycontrol.get("id")+",BOC返回成功!");
									continue;
								}
								
								Double remainManageFee = paycontrol.getRemainManageFee();
								Double overdueViolateMoney = paycontrol.getOverdueViolateMoney();
								String id = paycontrol.getId();
								Double remainAccrual = paycontrol.getRemainAccrual();
								Double remainCapital = paycontrol.getRemainCapital();
								Double remainInterest = paycontrol.getRemainInterest();
								
								SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
								String time = sdf.format(System.currentTimeMillis());
								String transSerialNo = "BFDK" + System.currentTimeMillis();
								JSONObject bocResultJson = this.create(crmOrderId, transSerialNo, payCode, accNo, idCard, idHolder, mobile, frozenMoney, paycontrol, time);		
								String resultCode = (String)bocResultJson.get("resultCode");
								
								// 需要查询的返回条件
								Map<String, String> codes = this.codes();
								// 判断查询结果
								if (StringUtil.isNotEmptyOrNull(resultCode))
								{
									//判断是否存在失败需要查询的订单信息
									if (codes.get(resultCode) != null)
									{
										//未知原因需要再次查询订单
										JSONObject jSONObject = this.bFWithholdingFindFunction(mobile, crmOrderId, transSerialNo, paycontrol.getId(), time);
										
										if (!"0000".equals(jSONObject.get("resultCode")) || "0".equals(jSONObject.get("responseCode")))
										{
											//还款失败
											logger.warn("BOC正常还款信贷订单宝付代扣失败,明细id:" + crmPaycontrol.get("id")+",BOC返回成功!");
											continue;
										}
									}
									
									//操作成功信息
									if (!"0000".equals(resultCode)&&!"BF00114".equals(resultCode))
									{
										//还款失败
										logger.warn("BOC正常还款信贷订单宝付代扣失败,明细id:" + crmPaycontrol.get("id")+",BOC返回成功!");
										continue;
									}
								}
								else
								{
									//未知原因需要再次查询订单
									JSONObject jSONObject = this.bFWithholdingFindFunction(mobile, crmOrderId, transSerialNo, paycontrol.getId(), time);
									
									if (!"0000".equals(jSONObject.get("resultcode")) || "0".equals(jSONObject.get("responseCode")))
									{
										//还款失败
										logger.warn("BOC正常还款信贷订单宝付代扣失败,明细id:" + crmPaycontrol.get("id")+",BOC返回成功!");
										continue;
									}
								}
								
								//开始产生资金记录
								JSONObject checkMoneyJson = new JSONObject();
								checkMoneyJson.put("cardNo",accNo);
								checkMoneyJson.put("cust_info_id",paycontrol.getCustInfoId());			//客户id
								checkMoneyJson.put("money", frozenMoney);		// 金额
								checkMoneyJson.put("money_type","2"); 			// 1金账户2,银行卡3,现场交钱4,电子账户
								checkMoneyJson.put("operation_type","2"); 		//1入账2出账
								checkMoneyJson.put("status","0");				//1成功0失败
								checkMoneyJson.put("person_type","2");			//1,投资人2借款人3,邀请人
								checkMoneyJson.put("type","5");					//1,提前结清2提前收回3,逾期还款4,部分逾期,5正常还款	
								checkMoneyJson.put("crm_order_id", paycontrol.getCrmOrderId());
								String checkMoneyId = psCheckMoneyService.save(checkMoneyJson);
							
//									//资金记录修改
								psCheckMoneyService.updateStatusById(checkMoneyId);
								
								//产生记录
								CrmPayrecoder cpr = new CrmPayrecoder();
								cpr.setCertificateUrl("");
								cpr.setCreateTime(DateUtil.getCurrentTime());
								cpr.setCrmOrderId(crmOrderId);
								cpr.setCustId(custId);
								cpr.setCustInfoId(custInfoId);
								cpr.setEmpId("");
								cpr.setManageFee(remainManageFee);
								cpr.setOverdueInterest(0d);
								cpr.setOverdueViolateMoney(overdueViolateMoney);
								cpr.setPaycontrolId(id);
								cpr.setPrepaymentViolateMoney(0d);
								cpr.setRemainFee(0d);
								cpr.setRemark("");
								cpr.setRepaymentType(0);
								cpr.setShouldAccrual(remainAccrual);
								cpr.setShouldCAPITAL(remainCapital);
								cpr.setShouldInterest(remainInterest);
								cpr.setShouldMONEY(frozenMoney.doubleValue());
								cpr.setOperationPlatform("200012-0003");//自动还款  200012-0003
								cpr.setPaymentPlatform("200013-0003");//baofoo 200013-0003
								if(crmPaycontrol.get("clearing_channel")!= null && "200007-0003".equals(crmPaycontrol.get("clearing_channel").toString()))
								{
									cpr.setPaymentChannel("200014-0001");//第三方虚拟账户200014-0001
								}
								else
								{
									cpr.setPaymentChannel("200014-0002");//银行卡代扣200014-0002
								}
								
								efPaycontrolDao.add(cpr);
								
								//更新信贷还款明细
								paycontrol.setStatus(1);					//还款状态 0：待还，1:结清，2:逾期,3:提前结清,4:失效
								paycontrol.setRemainAccrual(0d);			//剩余利息
								paycontrol.setRemainCapital(0d);			//剩余本金
								paycontrol.setRemainInterest(0d);			//还款期数
								paycontrol.setRemainManageFee(0d);			//剩余管理费
								paycontrol.setOverdueViolateMoney(0d);		//逾期违约金
								paycontrol.setReplaceStatus(0);				//是否垫付
								
								efPaycontrolDao.update(paycontrol);
								
								//更新资产包信息
								efPaycontrolService.updateCrmorder(crmOrder.getId());
								
								// 更新对应订单信息
								efPaycontrolService.updateInfo(crmOrder, apply);
							}
						}
					}
				}
				catch (Exception e)
				{
					logger.warn(e.getMessage(),e);
					JSONObject smsJson = new JSONObject();
					smsJson.put("text", "BOC正常还款信贷订单还款异常,明细id:" + crmOrderMap.get("id")+",BOC返回成功!");
					SmsUtil.senErrorMsg(smsJson);
					logger.warn("BOC正常还款信贷订单还款异常,明细id:" + crmOrderMap.get("id")+",BOC返回成功!");
				}
			}
		}
		
		logger.warn("boc信贷正常订单宝付代扣功能还款结束---");
	}
	
	/**
	 * 功能说明：宝付代扣结果接口查询接口
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午10:59:48
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public JSONObject bFWithholdingFindFunction(String mobile,String orderId, String transSerialNo, String transNo, String time)
	{
		String boc_url=StaticData.bocUrl;
		
		JSONObject bocJson = new JSONObject();
		
//		bocJson.put("additional_info", "");			//附加字段
		bocJson.put("biz_type", "0000");			//接入类型
		bocJson.put("mobile", mobile);				//手机号
//		bocJson.put("req_reserved", "");			//请求方保留域
//		bocJson.put("trans_serial_no", "BFDK"+time);		//商户流水号 测试
		bocJson.put("trans_serial_no", transSerialNo);		//商户流水号
		bocJson.put("orig_trade_date", time);		//订单日期
		bocJson.put("orig_trans_id", transNo+time);			//原始商户订单号
		TransPaymentRecord transPaymentRecord = new TransPaymentRecord();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
		transPaymentRecord.setCreateTime(sdf.format(System.currentTimeMillis()));
		transPaymentRecord.setTransWay("baofoo");
		transPaymentRecord.setTransType("自动");
		transPaymentRecord.setTransFrom("erp");
		transPaymentRecord.setTransReqContent(bocJson.toString());
		transPaymentRecord.setTransNo(transNo+time);
		transPaymentRecord.setTransMobile(mobile);//手机号
		transPaymentRecord.setCrmOrderId(orderId);//订单id
		
		efPaycontrolDao.add(transPaymentRecord);
		}
		catch (Exception e)
		{
			logger.warn(e.getMessage(),e);
			logger.warn("BOC新增记录失败!");
			JSONObject errorJson = new JSONObject();
	        errorJson.put("responseCode", "0");
	        errorJson.put("info", "新增宝付代扣记录失败!");
	        errorJson.put("data", "新增宝付代扣记录失败!");
	        errorJson.put("responseBankCode", "");
	        JSONObject smsJson = new JSONObject();
			smsJson.put("text", "新增宝付代扣记录失败!");
			SmsUtil.senErrorMsg(smsJson);
			return errorJson;
		}
		
		JSONObject bocResultJson = WebServiceUtil.sendPost(boc_url+"baofooPayProcess/queryWithholdResult", bocJson.toString());
		this.modfiy(transPaymentRecord, sdf, bocResultJson);
		
		return bocResultJson;
	}
	
	/**
	 * 功能说明：宝付代扣方法
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午10:59:48
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	private JSONObject create(String orderId, String transSerialNo, String payCode, String accNo, String idCard, String idHolder, String mobile, BigDecimal frozenMoney, CrmPaycontrol paycontrol, String time)
	{
		String boc_url=StaticData.bocUrl;
		
		JSONObject bocJson = new JSONObject();
		
		bocJson.put("txn_sub_type", "13");		//交易子类    取值:13
		bocJson.put("biz_type", "0000");			//接入类型    其他：不填写和默认0000,表示为储蓄卡支付。
//		bocJson.put("terminal_id", "");		//终端号
//		bocJson.put("member_id", "");			//商户号    宝付提供给商户的唯一编号
		bocJson.put("pay_code", payCode + "");			//银行编码   具体参数见附录  
		bocJson.put("pay_cm", "2");				//安全标识   默认为：2 1:不进行信息严格验证 2:对四要素（身份证号、持卡人姓名、银行卡绑定手机、卡号）进行严格校验
		bocJson.put("acc_no", accNo + "");				//卡号 BG_CUST_INFO BANK_ACCOUNT 电子账户
		bocJson.put("id_card_type", "01");	//固定值：01  01认为身份证号
		bocJson.put("id_card", idCard + "");			//身份证     BG_CUST_INFO CUST_IC
		bocJson.put("id_holder", idHolder + "");			//持卡人姓名  BG_CUST_INFO  CUST_NAME
//		bocJson.put("id_holder", "KFDG");			//持卡人姓名  BG_CUST_INFO  CUST_NAME
		if (StringUtil.isNotEmptyOrNull(mobile))
		{
			bocJson.put("mobile", mobile + "");				//银行卡绑定手机号   BG_CUST_INFO  bind_card_mobile
		}
//		bocJson.put("valid_date", );			//卡有效期  格式：YYMM 如：07月/18年则写成1807
//		bocJson.put("valid_no", );			//卡安全码
		bocJson.put("trans_id", paycontrol.getId()+time); 			//商户订单号
		
		BigDecimal number = new BigDecimal("100");
		bocJson.put("txn_amt", frozenMoney.multiply(number).doubleValue() + "");	//交易金额  1元则提交100
		bocJson.put("trade_date", time + "");			//订单日期 14 位定长。格式：年年年年月月日日时时分分秒秒
//		bocJson.put("additional_info", );    //附加字段
//		bocJson.put("req_reserved", );		//请求方保留域
		bocJson.put("trans_serial_no", transSerialNo);	//商户流水号
		TransPaymentRecord transPaymentRecord = new TransPaymentRecord();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
			transPaymentRecord.setCreateTime(sdf.format(System.currentTimeMillis()));
			transPaymentRecord.setTransWay("baofoo");
			transPaymentRecord.setTransType("自动");
			transPaymentRecord.setTransFrom("erp");
			transPaymentRecord.setTransReqContent(bocJson.toString());
			transPaymentRecord.setTransNo(paycontrol.getId());
			transPaymentRecord.setTransMoney(frozenMoney.toString());
			transPaymentRecord.setTransMobile(mobile);//手机号
			transPaymentRecord.setCrmOrderId(orderId);//订单id
			
			efPaycontrolDao.add(transPaymentRecord);
		}
		catch (Exception e)
		{
			logger.warn(e.getMessage(),e);
			logger.warn("BOC新增记录失败!");
			JSONObject errorJson = new JSONObject();
            errorJson.put("responseCode", "0");
            errorJson.put("info", "新增宝付代扣记录失败!");
            errorJson.put("data", "新增宝付代扣记录失败!");
            errorJson.put("responseBankCode", "");
            JSONObject smsJson = new JSONObject();
			smsJson.put("text", "新增宝付代扣记录失败!");
			SmsUtil.senErrorMsg(smsJson);
			
			return errorJson;
		}
		
		JSONObject bocResultJson = WebServiceUtil.sendPost(boc_url+"baofooPayProcess/withhold", bocJson.toString());
		this.modfiy(transPaymentRecord, sdf, bocResultJson);
		return bocResultJson;
	}
	
	/**
	 * 功能说明：
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午11:51:10
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	private	String banks(String bankName)
	{
		if (bankName.indexOf("工商银行") != -1)
		{
			return "ICBC";
		}
		if (bankName.indexOf("农业银行") != -1)
		{
			return "ABC";
		}
		if (bankName.indexOf("建设银行") != -1)
		{
			return "CCB";
		}
		if (bankName.indexOf("中国银行") != -1)
		{
			return "BOC";
		}
		if (bankName.indexOf("交通银行") != -1)
		{
			return "BCOM";
		}
		if (bankName.indexOf("兴业银行") != -1)
		{
			return "CIB";
		}
		if (bankName.indexOf("中信银行") != -1)
		{
			return "CITIC";
		}
		if (bankName.indexOf("光大银行") != -1)
		{
			return "CEB";
		}
		if (bankName.indexOf("平安银行") != -1)
		{
			return "PAB";
		}
		if (bankName.indexOf("邮政储蓄银行") != -1)
		{
			return "PSBC";
		}
		if (bankName.indexOf("上海银行") != -1)
		{
			return "SHB";
		}
		if (bankName.indexOf("浦东发展银行") != -1)
		{
			return "SPDB";
		}
		if (bankName.indexOf("民生银行") != -1)
		{
			return "CMBC";
		}
		if (bankName.indexOf("招商银行") != -1)
		{
			return "CMB";
		}
		if (bankName.indexOf("广发银行") != -1)
		{
			return "GDB";
		}
		if (bankName.indexOf("华夏银行") != -1)
		{
			return "HXB";
		}
		
		return null;
	}
	
	/**
	 * 功能说明：
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午11:51:10
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	private	Map<String, String> codes()
	{
		Map<String, String> codes = new HashMap<String, String>();
		
		codes.put("BF00100", "系统异常，请联系宝付");
		codes.put("BF00112", "系统繁忙，请稍后再试");
		codes.put("BF00113", "交易结果未知，请稍后查询");
		codes.put("BF00115", "交易处理中，请稍后查询");
		codes.put("BF00144", "该交易有风险,订单处理中");
		codes.put("BF00202", "交易超时，请稍后查询");
		
		return codes;
	}
	
	/**
	 * 功能说明：宝付代付功能
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午9:19:36
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public JSONObject toBFPayFunction()
	{
		logger.warn("boc信贷订单宝付代付功能自动开始----");
		String boc_url=StaticData.bocUrl;
		String transNo = "BFDF" + System.currentTimeMillis();
		
		try
		{
			//查询需要处理的订单明细
			List<Map> crmPayrecoders = crmOrderDaoImpl.queryOrders();
			
			if (ListTool.isNotNullOrEmpty(crmPayrecoders))
			{
				BigDecimal ShouldMoney = new BigDecimal("0.0000");
				
				for (Map crmPayrecoder : crmPayrecoders)
				{
					BigDecimal payrecoderMoney = (BigDecimal)crmPayrecoder.get("should_money");
					
					ShouldMoney = ShouldMoney.add(payrecoderMoney);
				}
				
				JSONObject bocJson = new JSONObject();
				String bankAccount = StaticData.risk;
				
				bocJson.put("trans_no", transNo);			//商户订单号
				bocJson.put("trans_money", ShouldMoney);				//转账金额
				Map map = crmOrderDaoImpl.fandBankAccount(bankAccount);
//				bocJson.put("to_acc_name", "王宝");		//收款人姓名
//				bocJson.put("to_acc_no", "6228480444455553333");					//收款人银行帐号
//				bocJson.put("to_bank_name", "中国农业银行");		//收款人银行名称
//				bocJson.put("to_pro_name", "上海市");						//收款人开户行省名
//				bocJson.put("to_city_name", "上海市");					//收款人开户行市名
//				bocJson.put("to_acc_dept", "支行");					//收款人开户行机构名
//				bocJson.put("trans_card_id", "320301198502169142");	//银行卡身份证件号码
//				bocJson.put("trans_mobile", "");						//银行卡预留手机号
				bocJson.put("trans_summary", "");						//摘要
				bocJson.put("to_acc_name", map.get("cust_name"));		//收款人姓名
				bocJson.put("to_acc_no", bankAccount);
				bocJson.put("to_bank_name", "江西银行股份有限公司总行营业部");		//收款人银行名称
				bocJson.put("to_pro_name", "江西省");						//收款人开户行省名
				bocJson.put("to_city_name", "南昌市");					//收款人开户行市名
				bocJson.put("to_acc_dept", "总行营业部");					//收款人开户行机构名
				bocJson.put("trans_card_id", map.get("CARD_NUMBER"));	//银行卡身份证件号码
				TransPaymentRecord transPaymentRecord = new TransPaymentRecord();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try
				{
					transPaymentRecord.setCreateTime(sdf.format(System.currentTimeMillis()));
					transPaymentRecord.setTransWay("baofoo");
					transPaymentRecord.setTransType("自动");
					transPaymentRecord.setTransFrom("erp");
					transPaymentRecord.setTransMoney(ShouldMoney.toString());
					transPaymentRecord.setTransReqContent(bocJson.toString());
//					transPaymentRecord.setTransNo(transNo);
					
					efPaycontrolDao.add(transPaymentRecord);
				}
				catch (Exception e)
				{
					logger.warn(e.getMessage(),e);
					logger.warn("BOC新增记录失败!");
					JSONObject errorJson = new JSONObject();
		            errorJson.put("responseCode", "0");
		            errorJson.put("info", "新增宝付代扣记录失败!");
		            errorJson.put("data", "新增宝付代扣记录失败!");
		            errorJson.put("responseBankCode", "");
		            
		            JSONObject smsJson = new JSONObject();
					smsJson.put("text", "BOC正常还款信贷订单还款异常,宝付代扣订单:" + transNo + "BOC返回成功!");
					SmsUtil.senErrorMsg(smsJson);
					return new JSONObject();
				}
				
				JSONObject json = WebServiceUtil.sendPost(boc_url + "baofooPayProcess/payforAnother", bocJson.toString());
				this.modfiy(transPaymentRecord, sdf, json);
				logger.warn("boc信贷订单宝付代付功能自动结束----");
				return json;
			}
			return new JSONObject();
		}
		catch (Exception e)
		{
			logger.warn(e.getMessage(),e);
			logger.warn("BOC正常还款信贷订单还款异常,明细id:,BOC返回成功!");
			
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "BOC正常还款信贷订单还款异常,宝付代扣订单:" + transNo + "BOC返回成功!");
			SmsUtil.senErrorMsg(smsJson);
			return new JSONObject();
		}
	}
	
	/**
	 * 功能说明：宝付代付结果查询功能
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午9:19:36
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public JSONObject toBFPayFindFunction(String transBatchid, String transNo)
	{
		logger.warn("boc信贷订单宝代付结果查询功能自动开始----");
		String boc_url=StaticData.bocUrl;
		
		try
		{
			JSONObject bocJson = new JSONObject();
			bocJson.put("trans_no", transNo);		//付款方商户订单号
			bocJson.put("trans_batchid", transBatchid);			//宝付批次号
			
			TransPaymentRecord transPaymentRecord = new TransPaymentRecord();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try
			{
				transPaymentRecord.setCreateTime(sdf.format(System.currentTimeMillis()));
				transPaymentRecord.setTransWay("baofoo");
				transPaymentRecord.setTransType("自动");
				transPaymentRecord.setTransFrom("erp");
				transPaymentRecord.setTransReqContent(bocJson.toString());
//				transPaymentRecord.setTransNo(transNo);
				
				efPaycontrolDao.add(transPaymentRecord);
			}
			catch (Exception e)
			{
				logger.warn(e.getMessage(),e);
				logger.warn("BOC新增记录失败!");
				JSONObject errorJson = new JSONObject();
	            errorJson.put("responseCode", "0");
	            errorJson.put("info", "新增宝付代扣记录失败!");
	            errorJson.put("data", "新增宝付代扣记录失败!");
	            errorJson.put("responseBankCode", "");
	            JSONObject smsJson = new JSONObject();
				smsJson.put("text", "BOC新增记录失败!");
				SmsUtil.senErrorMsg(smsJson);
				
				return new JSONObject();
			}
			
			JSONObject jSONObject = WebServiceUtil.sendPost(boc_url + "baofooPayProcess/queryPayforAnotherResult", bocJson.toString());
			this.modfiy(transPaymentRecord, sdf, jSONObject);
			logger.warn("boc信贷订单代付结果查询功能自动结束----");
			return jSONObject;
		}
		catch (Exception e)
		{
			logger.warn(e.getMessage(),e);
			logger.warn("boc信贷订单代付结果查询功能异常!");
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "boc信贷订单代付结果查询功能异常!");
			SmsUtil.senErrorMsg(smsJson);
			return new JSONObject();
		}
	}
	
	/**
	 * 功能说明：更新调用宝付接口后的记录信息
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：下午2:30:11
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	private boolean modfiy(TransPaymentRecord transPaymentRecord, SimpleDateFormat sdf, JSONObject jSONObject)
	{
		if (jSONObject == null)
		{
			return false;
		}
		
		try
		{
			transPaymentRecord.setAnswerTime(sdf.format(System.currentTimeMillis()));
			if (jSONObject.get("resultCode") != null && jSONObject.get("resultCode") != ""){transPaymentRecord.setTransRespContent(jSONObject.toString());}
			if (jSONObject.get("dataType") != null && jSONObject.get("dataType") != ""){transPaymentRecord.setDataType(jSONObject.get("dataType").toString());}//请求响应格式，xml/json
			if (jSONObject.get("resultCode") != null && jSONObject.get("resultCode") != ""){transPaymentRecord.setReturnCode(jSONObject.get("resultCode").toString());}//返回状态码
			if (jSONObject.get("resultMessage") != null && jSONObject.get("resultMessage") != ""){transPaymentRecord.setReturnMsg(jSONObject.get("resultMessage").toString());}//返回信息
			transPaymentRecord.setReturnTime(sdf.format(System.currentTimeMillis()));//返回时间
			if (jSONObject.get("transBatchid") != null && jSONObject.get("transBatchid") != ""){transPaymentRecord.setTransBatchid(jSONObject.get("transBatchid").toString());}//批次号transBatchid
			if (jSONObject.get("txn_amt") != null && jSONObject.get("txn_amt") != ""){transPaymentRecord.setTransMoney(jSONObject.get("txn_amt").toString());}//转账金额
			if (jSONObject.get("id_holder") != null && jSONObject.get("id_holder") != ""){transPaymentRecord.setToAccName(jSONObject.get("id_holder").toString());}//收款人姓名
			if (jSONObject.get("acc_no") != null && jSONObject.get("acc_no") != ""){transPaymentRecord.setToAccNo(jSONObject.get("acc_no").toString());}//收款人银行帐号
			if (jSONObject.get("id_card") != null && jSONObject.get("id_card") != ""){transPaymentRecord.setTransCardId(jSONObject.get("id_card").toString());}//身份证号码
			
			efPaycontrolDao.update(transPaymentRecord);
			return true;
		}
		catch (Exception e)
		{
			logger.warn(e.getMessage(),e);
			logger.warn("更新调用宝付接口后的记录信息失败!");
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "更新调用宝付接口后的记录信息失败!");
			SmsUtil.senErrorMsg(smsJson);
			return false;
		}
	}
}