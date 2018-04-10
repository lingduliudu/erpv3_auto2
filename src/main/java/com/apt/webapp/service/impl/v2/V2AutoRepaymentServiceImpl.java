package com.apt.webapp.service.impl.v2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apt.util.ChkUtil;
import com.apt.util.NumberUtil;
import com.apt.util.StaticData;
import com.apt.util.arith.ArithUtil;
import com.apt.util.date.DateUtil;
import com.apt.util.sms.SmsUtil;
import com.apt.webapp.dao.v2.V2AutoRepaymentDao;
import com.apt.webapp.model.crm.CrmApplay;
import com.apt.webapp.model.crm.CrmOrder;
import com.apt.webapp.model.crm.CrmPaycontrol;
import com.apt.webapp.model.crm.CrmPayrecoder;
import com.apt.webapp.service.auto.AutoRun;
import com.apt.webapp.service.auto.transToZzl;
import com.apt.webapp.service.v2.V2AutoRepaymentService;

/**
 * 功能说明：v2到期还款 逾期还款 自动逾期
 * 典型用法：
 * 特殊用法：	
 * @author 王明振
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2016年3月8日 
 * Copyright zzl-apt
 */
@Service("v2AutoRepaymentService")
public class V2AutoRepaymentServiceImpl implements V2AutoRepaymentService{
	//日志
	private Logger logger = LoggerFactory.getLogger(AutoRun.class);
	@Autowired
	private V2AutoRepaymentDao v2AutoRepaymentDao;
	@Resource
	private transToZzl transToZzl;

	/**
	 * 功能说明：执行到期还款操作
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 王明振
	 * 创建日期：2015年10月20日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	@SuppressWarnings("unchecked")
	public void dueRepayment()throws Exception{
		//1 查询需要还款的明细
		List<Map> ml=v2AutoRepaymentDao.findDueData();
		if(ml!=null&&ml.size()>0){
			//2 循环处理账户信息
			for(Map<String,Object> map:ml){
				//3 计算扣款金额REMAIN_INTEREST+REMAIN_CAPITAL+REMAIN_accrual+REMAIN_Manage_FEE
				Double sum=NumberUtil.parseDouble(map.get("remain_interest"))+NumberUtil.parseDouble(map.get("remain_capital"))+NumberUtil.parseDouble(map.get("remain_accrual"))+NumberUtil.parseDouble(map.get("remain_manage_fee"));
				JSONObject pocResult = transToZzl.payByPocDaikou(map.get("cust_info_id").toString(),sum+"","5",map.get("crm_order_id").toString());
				
				if("1".equals(pocResult.getString("responseCode"))){ //扣款成功直接更新
					//poc扣款成功,直接进行修改状态就可以了
					logger.warn("V2正常还款成功,"+map.get("payid")+",POC返回成功!");
					//如果是线上订单 将钱转至公司信用账户
					if("1".equals(map.get("online_type"))){
						//中资联转信用账户
						Map<String,Object> paramMap=new HashMap<String, Object>();
						paramMap.put("out_cust_no",StaticData.zzlAccount);
						paramMap.put("int_cust_no",StaticData.creditAccount );
						paramMap.put("amt",NumberUtil.duelmoney(sum) );
						String result=transToZzl.connectToPoc("transferBmu", paramMap);
						if(!"0000".equals(result)){
							CrmPaycontrol cpc=(CrmPaycontrol) v2AutoRepaymentDao.findById(CrmPaycontrol.class, map.get("payid").toString());
							cpc.setAbnormalStatus(0);
							v2AutoRepaymentDao.update(cpc);
							//线上订单 正常还款成功，转信用账户失败
							logger.warn("V2线上订单正常还款成功,转信用账户失败，还款明细id:"+map.get("payid")+"");
							JSONObject smsJson = new JSONObject();
							smsJson.put("text", "V2线上订单正常还款成功,转信用账户失败，还款明细id:"+map.get("payid")+"");
							SmsUtil.senErrorMsg(smsJson);
						}
						
					}
					try{
					CrmPaycontrol cpc=(CrmPaycontrol) v2AutoRepaymentDao.findById(CrmPaycontrol.class, map.get("payid").toString());
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
					cpr.setShouldMONEY(ArithUtil.addHaveNull(new Double[]{
							cpc.getRemainAccrual(),
							cpc.getRemainCapital(),
							cpc.getRemainInterest(),
							cpc.getRemainManageFee()
					}));
					v2AutoRepaymentDao.add(cpr);
					//更新扣款明细 					
					cpc.setRemainAccrual(0d);
					cpc.setRemainCapital(0d);
					cpc.setRemainInterest(0d);
					cpc.setRemainManageFee(0d);
					cpc.setStatus(1);
					v2AutoRepaymentDao.update(cpc);
					//判断是不是最后一单明细，如果是 将订单变成已结清
					String sql="select * from crm_paycontrol where crm_order_id='"+map.get("crm_order_id")+"' and (status=0 or status=2)";
					List<Map> mml=v2AutoRepaymentDao.queryBySqlReturnMapList(sql);
					//如果没有代还的明细 单子结束
					if(mml==null||mml.size()==0){
						CrmOrder crmOrder = (CrmOrder) v2AutoRepaymentDao.findById(CrmOrder.class, map.get("crm_order_id").toString());
						crmOrder.setOrderTradeStatus(4);
						crmOrder.setClearType(1);
						crmOrder.setFinishTime(DateUtil.getCurrentTime());
						v2AutoRepaymentDao.update(crmOrder);
						// 更新crm_applay
						CrmApplay apply = (CrmApplay) v2AutoRepaymentDao.findById(CrmApplay.class, crmOrder.getCrmApplayId());
						if (ChkUtil.isNotEmpty(apply)) {
							apply.setTradeStatus( 4);
							apply.setRepaymentStatus(1);
							apply.setStatus( 4);
							v2AutoRepaymentDao.update(apply);
						}
					}
					}catch(Exception e){
						CrmPaycontrol cpc=new CrmPaycontrol();
						cpc=(CrmPaycontrol) v2AutoRepaymentDao.findById(CrmPaycontrol.class, map.get("payid").toString());
						cpc.setAbnormalStatus(0);
						v2AutoRepaymentDao.update(cpc);
						//线上订单 正常还款成功，转信用账户失败
						logger.warn("V2订单正常还款成功,更新数据失败，还款明细id:"+map.get("payid")+"");
						JSONObject smsJson = new JSONObject();
						smsJson.put("text", "V2订单正常还款成功,更新数据失败，还款明细id:"+map.get("payid")+"");
						SmsUtil.senErrorMsg(smsJson);
					}
				}
				
			}
		}
		
		
	}
	/**
	 * 功能说明：执行逾期还款操作
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 王明振
	 * 创建日期：2016年3月8日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> overdueRepayment() throws Exception {
		// 1 查询需要还款的明细
		return v2AutoRepaymentDao.findOverdueData();
	}
	/**
	 * 功能说明：执行自动逾期操作
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 王明振
	 * 创建日期：2016年3月8日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void autoOverdue(Map crmPaycontrol)throws Exception{
		
		v2AutoRepaymentDao.autoOverdue(crmPaycontrol);
		
	}
	/**
	 * 功能说明：获取v2今日未结清订单
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
	public List<Map> getV2NotClearPaycontrols(){
		return v2AutoRepaymentDao.getV2NotClearPaycontrols();
	}
	/**
	 * 功能说明：迭代执行逾期扣款操作
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 王明振
	 * 创建日期：2016年3月8日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	private void iterativeOverdue(Map<String,Object> map){

		//3 计算扣款金额REMAIN_INTEREST+REMAIN_CAPITAL+REMAIN_accrual+REMAIN_Manage_FEE
		Double sum=NumberUtil.parseDouble(map.get("summoney"));
		//循环判定余额是不是够 如果余额直接充足 所有的逾期都还掉 如果不充足 优先还前面几个月
		JSONObject pocResult = transToZzl.payByPocDaikou(map.get("cust_info_id").toString(),sum+"","3",map.get("crm_order_id").toString());
		if("1".equals(pocResult.getString("responseCode"))){ //扣款成功直接更新
			//poc扣款成功,直接进行修改状态就可以了
			logger.warn("V2正常还款成功,"+map.get("payid")+",POC返回成功!");
			//如果是线上订单 将钱转至公司信用账户
			if("1".equals(map.get("online_type"))){
				//中资联转信用账户
				Map<String,Object> paramMap=new HashMap<String, Object>();
				paramMap.put("out_cust_no",StaticData.zzlAccount);
				paramMap.put("int_cust_no", StaticData.creditAccount);
				paramMap.put("amt",NumberUtil.duelmoney(sum) );
				String result=transToZzl.connectToPoc("transferBmu", paramMap);
				if(!"0000".equals(result)){
					String[] ids=map.get("allid").toString().split(",");
					for(int i=0;i<ids.length;i++){
						try {
							CrmPaycontrol cpc = (CrmPaycontrol) v2AutoRepaymentDao.findById(CrmPaycontrol.class,ids[i] );
							cpc.setAbnormalStatus(0);
							v2AutoRepaymentDao.update(cpc);
							//线上订单 正常还款成功，转信用账户失败
							logger.warn("V2线上订单正常还款成功,转信用账户失败，还款明细id:"+ids[i]+"");
							JSONObject smsJson = new JSONObject();
							smsJson.put("text","V2线上订单正常还款成功,转信用账户失败，还款明细id:"+ids[i]+"");
							SmsUtil.senErrorMsg(smsJson);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					
				}
				
			}
			String[] ids=map.get("allid").toString().split(",");
			for(int i=0;i<ids.length;i++){
			CrmPaycontrol cpc = null;
			try{
			cpc=(CrmPaycontrol) v2AutoRepaymentDao.findById(CrmPaycontrol.class, ids[i]);
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
			cpr.setShouldMONEY(ArithUtil.addHaveNull(new Double[]{
					cpc.getRemainAccrual(),
					cpc.getRemainCapital(),
					cpc.getRemainInterest(),
					cpc.getRemainManageFee()
			}));
			v2AutoRepaymentDao.add(cpr);
            //更新扣款明细 插入还款记录 	
			cpc.setRemainAccrual(0d);
			cpc.setRemainCapital(0d);
			cpc.setRemainInterest(0d);
			cpc.setRemainManageFee(0d);
			cpc.setStatus(1);
			v2AutoRepaymentDao.update(cpc);
			//判断是不是最后一单明细，如果是 将订单变成已结清
			String sql="select * from crm_paycontrol where crm_order_id='"+map.get("crm_order_id")+"' and (status=0 or status=2)";
			List<Map> mml=v2AutoRepaymentDao.queryBySqlReturnMapList(sql);
			//如果没有代还的明细 单子结束
			if(mml==null||mml.size()==0){
				CrmOrder crmOrder = (CrmOrder) v2AutoRepaymentDao.findById(CrmOrder.class, map.get("crm_order_id").toString());
				crmOrder.setOrderTradeStatus(4);
				crmOrder.setClearType(1);
				crmOrder.setFinishTime(DateUtil.getCurrentTime());
				v2AutoRepaymentDao.update(crmOrder);
				// 更新crm_applay
				CrmApplay apply = (CrmApplay) v2AutoRepaymentDao.findById(CrmApplay.class, crmOrder.getCrmApplayId());
				if (ChkUtil.isNotEmpty(apply)) {
					apply.setTradeStatus(4);
					apply.setRepaymentStatus(1);
					apply.setStatus(4);
					v2AutoRepaymentDao.update(apply);
				}
			}
			}catch(Exception e){
				cpc.setAbnormalStatus(0);
				try {
					v2AutoRepaymentDao.update(cpc);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				//线上订单 正常还款成功，转信用账户失败
				logger.warn("V2订单还款成功,更新数据库信息失败,还款明细id:"+ids[i]);
				JSONObject smsJson = new JSONObject();
				smsJson.put("text","V2订单还款成功,更新数据库信息失败,还款明细id:"+ids[i]);
				SmsUtil.senErrorMsg(smsJson);
			}
			}
		}else if("1051".equals(pocResult.getString("responseCode"))){
			//如果余额不足 而且count>1(是多个逾期联合起来扣款的) 尝试合并逾期单数-1 
			if(Integer.parseInt(map.get("count").toString())>1){
				Integer count=Integer.parseInt(map.get("count").toString())-1;
				String allid=map.get("allid").toString();
				String allmoney=map.get("allmoney").toString();
				Double summoney=Double.parseDouble(map.get("summoney").toString());
				allid=allid.substring(0,allid.lastIndexOf(","));
				//获取最后一个逾期的金额
				Double last=Double.parseDouble(allmoney.substring(allmoney.lastIndexOf(",")+1));
				summoney=summoney-last;
				allmoney=allmoney.substring(0,allmoney.lastIndexOf(","));
				map.put("count",count );
				map.put("allid",allid );
				map.put("allmoney",allmoney );
				map.put("summoney",summoney );
				iterativeOverdue(map);
			}
		}
		
	
	}
	

}
