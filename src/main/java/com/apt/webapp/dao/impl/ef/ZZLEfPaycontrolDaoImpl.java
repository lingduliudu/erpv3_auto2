package com.apt.webapp.dao.impl.ef;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.apt.util.ChkUtil;
import com.apt.util.ListTool;
import com.apt.util.NumberUtil;
import com.apt.util.date.DateUtil;
import com.apt.webapp.base.dao.BaseHibernateDaoSupper;
import com.apt.webapp.dao.ef.IZZLEfPaycontrolDao;
import com.apt.webapp.model.crm.CrmPaycontrol;
import com.apt.webapp.model.ef.ZZLEfPaycontrol;
import com.apt.webapp.model.ef.ZZLEfPayrecord;

/**
 * 功能说明：理财还款明细表Dao层实现类
 * @author 乔春峰
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-12
 * Copyright zzl-apt
 */
@Repository
public class ZZLEfPaycontrolDaoImpl extends BaseHibernateDaoSupper implements IZZLEfPaycontrolDao{
	//日志
	private Logger logger = LoggerFactory.getLogger(ZZLEfPaycontrolDaoImpl.class);
	/**
	 * 功能说明： 复制理财明细
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 袁浩
	 * 创建日期：2015年10月20日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception 
	 */
	public void copyEfControl(Map efPaycontrol){
		ZZLEfPaycontrol zepc = new ZZLEfPaycontrol();
		if(!ChkUtil.isEmpty(efPaycontrol.get("authcode"))){
			zepc.setAuthCode(efPaycontrol.get("authcode").toString());  //授权码
		}
		if(!ChkUtil.isEmpty(efPaycontrol.get("clearing_channel"))){
			zepc.setClearingChannel(NumberUtil.parseInteger(efPaycontrol.get("clearing_channel")));  //结算通道
		}
		zepc.setControlId(efPaycontrol.get("id").toString());					//id
		zepc.setCouponInterest(NumberUtil.parseDouble(efPaycontrol.get("coupon_interest")));
		zepc.setCreateTime(DateUtil.getCurrentTime());
		zepc.setCustInfoId(efPaycontrol.get("cust_info_id").toString());
		zepc.setEfOrderId(efPaycontrol.get("ef_order_id").toString());
		zepc.setInterest(NumberUtil.parseDouble(efPaycontrol.get("interest")));
		zepc.setManagementAmt(NumberUtil.parseDouble(efPaycontrol.get("management_amt")));
		zepc.setMoreInterest(0d);  //多出的利息,暂时记为0
		zepc.setOverPenalty(0d);
		zepc.setPayStatus(0);	  //默认赋值0  未结清
		zepc.setPayTime(efPaycontrol.get("pay_time").toString()); //还款时间
		zepc.setPeriods(NumberUtil.parseInteger(efPaycontrol.get("periods")));
		zepc.setPrePaymentPenalty(0d); 
		zepc.setPrinciapl(NumberUtil.parseDouble(efPaycontrol.get("princiapl")));
		zepc.setSurplusInterest(0d);
		zepc.setSurplusManagementAmt(0d);
		zepc.setSurplusPrincipal(NumberUtil.parseDouble(efPaycontrol.get("surplus_principal")));
		zepc.setType(NumberUtil.parseInteger(efPaycontrol.get("onLine")));
		zepc.setUpdateTime(DateUtil.getCurrentTime());
		zepc.setCrmOrderId(efPaycontrol.get("crmOrderId").toString());
		zepc.setSeriNo(efPaycontrol.get("seriNo").toString());
		try {
			add(zepc);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("债权转让数据拷贝失败!明细id:"+efPaycontrol.get("id")+",授权码:"+efPaycontrol.get("AUTHCODE"));
		}
	}
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
		String sql  = "select * from zzl_ef_paycontrol where crm_order_id ='"+crm_order_id+"' and pay_status = 0 and pay_time like '"+repayment_time.substring(0,10)+"%'";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：针对信贷订单成功,直接进行修改
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
	public void rePayNormal(String crmOrderId,String ids){
		String sql="";
		if("".equals(ids)){
				sql="SELECT * from zzl_ef_paycontrol where pay_status=-1 and pay_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and crm_order_id ='"+crmOrderId+"' and id not in ("+ids+")";
		}else{
				sql="SELECT * from zzl_ef_paycontrol where pay_status=-1 and pay_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and crm_order_id ='"+crmOrderId+"' ";
		}
		List<Map> list = queryBySqlReturnMapList(sql);
		if(ListTool.isNullOrEmpty(list)){logger.warn("本次更新未发现对应的中资联明细!");return;}
		logger.warn("本次更新中资联明细数量!"+list.size());
		for(Map zzlMap:list){
			try {
				ZZLEfPaycontrol zzl = (ZZLEfPaycontrol) findById(ZZLEfPaycontrol.class, zzlMap.get("id").toString());
				//增加记录
				ZZLEfPayrecord zzlR=new ZZLEfPayrecord();
				zzlR.setCreateTime(DateUtil.getCurrentTime());
				zzlR.setCustInfoId(zzl.getCustInfoId());
				zzlR.setEfOrderId(zzl.getEfOrderId());
				zzlR.setEvidenceUrl("");
				zzlR.setInteRest(zzl.getSurplusInterest());
				zzlR.setManagementAmt(zzl.getSurplusManagementAmt());
				zzlR.setOperator("");
				zzlR.setOverPenalty(zzl.getOverPenalty());
				zzlR.setPeriods(zzl.getPeriods());
				zzlR.setPrePaymentPenalty(zzl.getPrePaymentPenalty());
				zzlR.setPrincipal(zzl.getSurplusPrincipal());
				zzlR.setZzlPaycontrolId(zzl.getId());
				add(zzlR);
				//更新还款
				zzl.setPayStatus(1);//结清
				zzl.setSurplusInterest(0d);
				zzl.setSurplusManagementAmt(0d);
				zzl.setSurplusPrincipal(0d);
				update(zzl);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.warn(e.getMessage(),e);
				logger.warn("在对中资联明细id="+zzlMap.get("id").toString()+"时,还款失败!");
			}
			
		}
	}
	/**
	 * 功能说明：通过时间找到对应的信贷明细
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
	public CrmPaycontrol findByTime(String crm_order_id,String pay_time){
		String sql  = "SELECT * From crm_paycontrol where crm_order_id='"+crm_order_id+"' and repayment_time like '"+pay_time.substring(0,10)+"%' and status in (0,2);";
		try {
			return (CrmPaycontrol) findById(CrmPaycontrol.class, queryBySqlReturnMapList(sql).get(0).get("id").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
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
		String sql =	"SELECT "+ 
						"*  "+
						"FROM "+
						"zzl_ef_paycontrol "+
						"WHERE "+
						"pay_status=0 AND "+
						"pay_time like '"+time+"%';";
		return queryBySqlReturnMapList(sql);
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
	   String sql = "SELECT "+ 
					"*  "+
					"FROM "+
					"zzl_ef_paycontrol "+
					"WHERE "+
					"pay_status=-1 AND "+
					"pay_time like '"+error.get("pay_time").toString().substring(0,10)+"%';";
		List list = queryBySqlReturnMapList(sql);
		if(ListTool.isNullOrEmpty(list)){ //如果未发现已经冻结的则说明该明细目前还是未还款的
			return false;
		}
		return true;
		
	}
	/**
	 * 功能说明：拷贝结清的订单
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
	public void copyAdvanceClear(JSONObject clearJson){
		ZZLEfPaycontrol zepc = new ZZLEfPaycontrol();
		zepc.setAuthCode(clearJson.getString("AUTHCODE"));  //授权码
		zepc.setControlId("-1");					
		zepc.setCouponInterest(0d);
		zepc.setCreateTime(DateUtil.getCurrentTime());
		zepc.setCustInfoId(clearJson.getString("cust_info_id"));
		zepc.setEfOrderId(clearJson.getString("efOrderId"));
		zepc.setInterest(0d);
		zepc.setManagementAmt(0d);
		zepc.setMoreInterest(0d);  //多出的利息,暂时记为0
		zepc.setOverPenalty(0d);
		zepc.setPayStatus(3);	  //默认赋值0  未结清
		zepc.setPayTime(""); //还款时间
		zepc.setPeriods(-1);
		zepc.setPrePaymentPenalty(0d); 
		zepc.setPrinciapl(clearJson.getDouble("principal"));
		zepc.setSurplusInterest(0d);
		zepc.setSurplusManagementAmt(0d);
		zepc.setSurplusPrincipal(clearJson.getDouble("principal"));
		zepc.setUpdateTime(DateUtil.getCurrentTime());
		zepc.setCrmOrderId(clearJson.getString("crmOrderId"));
		if (clearJson.containsKey("seriNo")) {//
			zepc.setSeriNo(clearJson.getString("seriNo"));
		}
		try {
			add(zepc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
