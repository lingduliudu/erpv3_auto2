package com.apt.webapp.service.impl.ef;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apt.util.ChkUtil;
import com.apt.util.ListTool;
import com.apt.util.NumberFormat;
import com.apt.util.StaticData;
import com.apt.util.arith.ArithUtil;
import com.apt.util.date.DateUtil;
import com.apt.webapp.dao.ef.IZZLEfPaycontrolDao;
import com.apt.webapp.model.crm.CrmApplay;
import com.apt.webapp.model.crm.CrmOrder;
import com.apt.webapp.model.crm.CrmPaycontrol;
import com.apt.webapp.model.crm.CrmPayrecoder;
import com.apt.webapp.model.crm.PkgCustCrmorder;
import com.apt.webapp.model.ef.ZZLEfPaycontrol;
import com.apt.webapp.service.ef.IZZLEfPaycontrolService;


/**
 * 功能说明：
 * @author yuanhao
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-27
 * Copyright zzl-apt
 */
@Service
@Transactional
@WebService(serviceName = "zZLEfPaycontrolService", endpointInterface = "com.apt.webapp.service.ef.IZZLEfPaycontrolService")
public class ZZLEfPaycontrolServiceImpl implements IZZLEfPaycontrolService {
	//日志
	private Logger logger = LoggerFactory.getLogger(ZZLEfPaycontrolServiceImpl.class);
	@Resource
	private IZZLEfPaycontrolDao efPaycontrolDao;
	/**
	 * 功能说明：通过信贷订单id找到今日要还款的
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
	public List<Map> getCurrentByCrmControl(String crm_order_id,String repayment_time){
		
			return efPaycontrolDao.getCurrentByCrmControl(crm_order_id,repayment_time);
		
	}
	/**
	 * 功能说明：修改当期状态为冻结
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
	public void repayNormal(Map zzlEfpaycontrol){
		try {
			//1.产生记录
			//2.修改zzl为冻结
			//3.修改明细为结清
			CrmPaycontrol cpc = (CrmPaycontrol) efPaycontrolDao.findById(CrmPaycontrol.class,zzlEfpaycontrol.get("crmControlId").toString() );
			//产生记录
			CrmPayrecoder cpr = new CrmPayrecoder();
			cpr.setCertificateUrl("");
			cpr.setCreateTime(DateUtil.getCurrentTime());
			cpr.setCrmOrderId(zzlEfpaycontrol.get("crmOrderId").toString());
			cpr.setCustId(zzlEfpaycontrol.get("cust_id").toString());
			cpr.setCustInfoId(cpc.getCustInfoId());
			cpr.setEmpId("");
			cpr.setManageFee(cpc.getShouldPlatformManageMoney());
			cpr.setOverdueInterest(0d);
			cpr.setOverdueViolateMoney(0d);
			cpr.setPaycontrolId(zzlEfpaycontrol.get("crmControlId").toString());
			cpr.setPrepaymentViolateMoney(0d);
			cpr.setRemainFee(0d);
			cpr.setRemark("");
			cpr.setRepaymentType(0);
			cpr.setShouldAccrual(cpc.getRemainAccrual());
			cpr.setShouldCAPITAL(cpc.getRemainCapital());
			cpr.setShouldMONEY(ArithUtil.add(new Double[]{
					cpc.getRemainAccrual(),
					cpc.getRemainCapital(),
					cpc.getRemainInterest(),
					cpc.getRemainManageFee()
			}));
			cpr.setShouldInterest(cpc.getRemainInterest());
			efPaycontrolDao.add(cpr);
			//修改zzl
			ZZLEfPaycontrol zzl = (ZZLEfPaycontrol) efPaycontrolDao.findById(ZZLEfPaycontrol.class,zzlEfpaycontrol.get("id").toString());
			zzl.setPayStatus(-1);
			//准备计算多出的管理费利息等
			List<Map>currentZzlControls = efPaycontrolDao.getCurrentByCrmControl(cpc.getCrmOrderId(),cpc.getRepaymentTime());
			double totalManage = ListTool.getSumDouble(currentZzlControls, "surplus_management_amt");
			double totalInterest = ListTool.getSumDouble(currentZzlControls, "surplus_interest");
			//计算多出的利息和管理费
			double moreManage =  ArithUtil.sub(cpc.getRemainManageFee(), totalManage);
				   moreManage = ArithUtil.div(moreManage, currentZzlControls.size());
			double moreInterest  = ArithUtil.sub(cpc.getRemainInterest(), totalInterest);
				   moreInterest  = ArithUtil.div(moreInterest, currentZzlControls.size());
			zzl.setMoreInterest(moreInterest);
			zzl.setMoreManageAmt(moreManage);
			efPaycontrolDao.update(zzl);
			//修改明细
			cpc.setStatus(0);
			cpc.setRemainAccrual(0d);
			cpc.setRemainCapital(0d);
			cpc.setRemainInterest(0d);
			cpc.setRemainManageFee(0d);
			efPaycontrolDao.update(cpc);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("冻结成功后,在修改信贷明细时失败!");
		}
	}
	/**
	 * 功能说明：通过id获得对象
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
	public ZZLEfPaycontrol findById(String id){
		try {
			return (ZZLEfPaycontrol) efPaycontrolDao.findById(ZZLEfPaycontrol.class, id);
		} catch (Exception e) {
			return null;
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
	public boolean frozen(List<Map> zzlEfpaycontrols){
		boolean flag = false;
		//开始计算差值
		try {
			CrmPaycontrol cpc = (CrmPaycontrol) efPaycontrolDao.findByTime(zzlEfpaycontrols.get(0).get("crm_order_id").toString(),zzlEfpaycontrols.get(0).get("pay_time").toString() );
			//修改zzl
			//修改zzl
			double totalInterest = cpc.getRemainAccrual();// 剩余总利息
			double totalManage = cpc.getRemainManageFee();// 剩余管理费
			double totalOverCost = cpc.getRemainInterest();// 剩余罚息
			double totalOverPenalty = cpc.getOverdueViolateMoney();// 剩余逾期违约金
			double totalCapital = cpc.getRemainCapital();  //剩余总本金
			boolean manageStop = false;
			boolean interestStop = false;
			boolean overCostStop = false;
			boolean overPenaltyStop = false;
			boolean capitalStop = false;
			for(int i=0;i<zzlEfpaycontrols.size();i++){
				Map zzlEfpaycontrol = zzlEfpaycontrols.get(i);
				ZZLEfPaycontrol zzl = (ZZLEfPaycontrol) efPaycontrolDao.findById(ZZLEfPaycontrol.class,zzlEfpaycontrol.get("id").toString());
				//先设置相关金额为0元
				zzl.setTempPrincipal(0d);
				zzl.setMoreInterest(0d);  
				zzl.setMoreManageAmt(0d); 
				zzl.setSurplusManagementAmt(0d); 
				zzl.setSurplusInterest(0d);
				zzl.setOverCost(0d);
				zzl.setOverPenalty(0d);
				zzl.setSurplusPrincipal(0d);
				
				
				zzl.setPayStatus(-1);
				//准备计算管理费利息等
				//需要剩余管理费和应还本金>0
				double manage = 0d;
				if(cpc.getRemainManageFee()>0&&cpc.getShouldCapiital()>0){
					manage = NumberFormat.format(ArithUtil.div(cpc.getRemainManageFee()*zzl.getPrinciapl(), cpc.getShouldCapiital()));
				 }
				double interest  = 0d;
				if(cpc.getRemainAccrual()>0&&cpc.getShouldCapiital()>0){
					interest  = NumberFormat.format(ArithUtil.div(cpc.getRemainAccrual()*zzl.getPrinciapl(),cpc.getShouldCapiital()));
				}
				double overCost =0d;
				if(cpc.getRemainInterest()>0&&cpc.getShouldCapiital()>0){
					overCost = NumberFormat.format(ArithUtil.div(cpc.getRemainInterest()*zzl.getPrinciapl(),cpc.getShouldCapiital()));
					
				}
				
				double overPenalty =0d;
				if(cpc.getOverdueViolateMoney()>0&&cpc.getShouldCapiital()>0){
					overPenalty = NumberFormat.format(ArithUtil.div(cpc.getOverdueViolateMoney()*zzl.getPrinciapl(),cpc.getShouldCapiital()));
				}
				double capital =0d;
				if(cpc.getRemainCapital()>0&&cpc.getShouldCapiital()>0){
					capital = NumberFormat.format(ArithUtil.div(cpc.getRemainCapital()*zzl.getPrinciapl(),cpc.getShouldCapiital()));
				}
				
				
				
				
				zzl.setMoreInterest(0d);  //多出来的利息 直接赋值为0
				zzl.setMoreManageAmt(0d); //多出来的管理费直接赋值为0
				
				totalManage  = ArithUtil.sub(totalManage, manage);
				totalInterest  = ArithUtil.sub(totalInterest, interest);
				totalOverCost  = ArithUtil.sub(totalOverCost, overCost);
				totalOverPenalty  = ArithUtil.sub(totalOverPenalty, overPenalty);
				totalCapital = ArithUtil.sub(totalCapital,capital);
				
				zzl.setSurplusPrincipal(capital);//本金
				zzl.setTempPrincipal(capital);
				zzl.setSurplusManagementAmt(manage); //管理费
				zzl.setSurplusInterest(interest);//利息
				zzl.setOverCost(overCost);
				zzl.setOverPenalty(overPenalty);
				//是否继续本金的判断
				if(totalCapital>0){
					zzl.setSurplusPrincipal(capital); //本金
					zzl.setTempPrincipal(capital);//本金
				}
				if(totalCapital<0){
					capital = ArithUtil.add(capital,totalCapital);
					zzl.setSurplusPrincipal(capital); //本金
					zzl.setTempPrincipal(capital);//本金
				}
				if(capitalStop){
					zzl.setSurplusPrincipal(0d); //本金
					zzl.setTempPrincipal(0d);//本金
				}
				if(i == (zzlEfpaycontrols.size()-1)){
					if(totalCapital>0){
						zzl.setSurplusPrincipal(ArithUtil.add(capital,totalCapital)); //本金
						zzl.setTempPrincipal(ArithUtil.add(capital,totalCapital));//本金
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
				//-------
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
					manageStop  = true;
				}
				//开始验证本金是否大于了原出售的本金
				if(zzl.getSurplusPrincipal()>zzl.getPrinciapl()){
					double overPrinciapl = ArithUtil.sub(zzl.getSurplusPrincipal(), zzl.getPrinciapl());
					zzl.setSurplusPrincipal(zzl.getPrinciapl());
					zzl.setTempPrincipal(zzl.getPrinciapl());
					zzl.setMoreManageAmt(ArithUtil.add(zzl.getMoreManageAmt(),overPrinciapl));
				}
				efPaycontrolDao.update(zzl);
			}
			//产生记录
			CrmPayrecoder cpr = new CrmPayrecoder();
			cpr.setCertificateUrl("");
			cpr.setCreateTime(DateUtil.getCurrentTime());
			cpr.setCrmOrderId(cpc.getCrmOrderId());
			cpr.setCustId(cpc.getCustId());
			cpr.setCustInfoId(cpc.getCustInfoId());
			cpr.setEmpId("");
			cpr.setManageFee(cpc.getRemainManageFee());
			cpr.setOverdueInterest(0d);
			cpr.setOverdueViolateMoney(cpc.getOverdueViolateMoney());
			cpr.setPaycontrolId(cpc.getId());
			cpr.setPrepaymentViolateMoney(0d);
			cpr.setRemainFee(0d);
			cpr.setRemark("");
			cpr.setRepaymentType(0);
			cpr.setShouldAccrual(cpc.getRemainAccrual());
			cpr.setShouldCAPITAL(cpc.getRemainCapital());
			cpr.setShouldInterest(cpc.getRemainInterest());
			cpr.setShouldMONEY(ArithUtil.add(new Double[]{
					cpc.getRemainAccrual(),
					cpc.getRemainCapital(),
					cpc.getRemainInterest(),
					cpc.getRemainManageFee(),
					cpc.getOverdueViolateMoney()
			}));
			cpr.setShouldInterest(cpc.getRemainInterest());
			cpr.setOperationPlatform(StaticData.OP_ZDHK);/**自动还款*/
			cpr.setPaymentChannel(StaticData.NPC_OTHER);/**第三方虚拟账户*/
			cpr.setPaymentPlatform(StaticData.NPP_JXYH);/**江西银行*/
			
			efPaycontrolDao.add(cpr);
			//修改明细
			cpc.setStatus(1);
			cpc.setRemainAccrual(0d);
			cpc.setRemainCapital(0d);
			cpc.setRemainInterest(0d);
			cpc.setRemainManageFee(0d);
			cpc.setOverdueViolateMoney(0d);
			cpc.setReplaceStatus(0);
			efPaycontrolDao.update(cpc);
			//开始判断是否可以结清
			CrmOrder crmOrder = (CrmOrder) efPaycontrolDao.findById(CrmOrder.class, cpc.getCrmOrderId());
			String sql ="select * from crm_paycontrol where crm_order_id='"+cpc.getCrmOrderId()+"' and status in (0,2)";
			List list = efPaycontrolDao.queryBySqlReturnMapList(sql);
			//长度为0代表该笔订单已经结束了
			if(ListTool.isNullOrEmpty(list)){
				crmOrder.setOrderTradeStatus(4);
				crmOrder.setClearType(1);
				crmOrder.setFinishTime(DateUtil.getCurrentTime());
				efPaycontrolDao.update(crmOrder);
				try{
				CrmApplay apply = (CrmApplay) efPaycontrolDao.findById(CrmApplay.class,crmOrder.getCrmApplayId());
				if(ChkUtil.isNotEmpty(apply)){
					apply.setTradeStatus(4);
					apply.setStatus(4);
					apply.setRepaymentStatus(1);
					efPaycontrolDao.update(apply);
				}
				}catch (Exception e) {
					logger.warn(e.getMessage(),e);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("冻结明细时失败!信贷订单id:"+zzlEfpaycontrols.get(0).get("crm_order_id").toString());
		}
		return flag;
	}
	/**
	 * 功能说明：通过时间条件处理异常的信息
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
	public List<Map> getErrorZzlControls(String time){
		return efPaycontrolDao.getErrorZzlControls(time);
	}
	/**
	 * 功能说明：判断是否属于异常的zzl明细
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
	public boolean isError(Map error){
		return efPaycontrolDao.isError(error);
	}
	/**
	 * 功能说明：得到错误zzl明细的必要信息
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
	public Map getNeedErrorInfo(Map error){
		Map info = new HashMap();
		try {
			ZZLEfPaycontrol zzl = (ZZLEfPaycontrol) efPaycontrolDao.findById(ZZLEfPaycontrol.class, error.get("id").toString());
			CrmOrder co = (CrmOrder) efPaycontrolDao.findById(CrmOrder.class,zzl.getCrmOrderId());
			String sql = "select * from bg_cust_info where id ='"+co.getCustInfoId()+"'";
			info.put("order_prd_number",co.getOrderPrdNumber()); //产品编号
			info.put("bank_account", efPaycontrolDao.queryBySqlReturnMapList(sql).get(0).get("bank_account").toString()); //电子账户
			info.put("cust_info_id", co.getCustInfoId()); 
			info.put("crm_order_id", co.getId()); 
			String isLastSql = "SELECT * from crm_paycontrol where crm_order_id='"+co.getId()+"' and repayment_time > '"+zzl.getPayTime().substring(0,10)+"' and status =0";
			List list  = efPaycontrolDao.queryBySqlReturnMapList(isLastSql);
			info.put("endFlg", 0);
			if(ListTool.isNullOrEmpty(list)){ //确定是最后一期
				info.put("endFlg", 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
}
