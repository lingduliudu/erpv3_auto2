package com.apt.webapp.dao.impl.crm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.apt.util.ChkUtil;
import com.apt.util.ListTool;
import com.apt.util.NumberFormat;
import com.apt.util.NumberUtil;
import com.apt.util.StaticData;
import com.apt.util.SysConfig;
import com.apt.util.arith.ArithUtil;
import com.apt.util.date.DateUtil;
import com.apt.util.http.HttpUtil;
import com.apt.util.sms.SmsUtil;
import com.apt.webapp.base.dao.BaseHibernateDaoSupper;
import com.apt.webapp.dao.crm.ICrmOrderDao;
import com.apt.webapp.model.crm.CrmApplay;
import com.apt.webapp.model.crm.CrmOrder;
import com.apt.webapp.model.crm.CrmPaycontrol;
import com.apt.webapp.model.crm.CrmPayrecoder;
import com.apt.webapp.model.crm.PkgCustCrmorder;

/**
 * 功能说明：信贷订单  crm_order  dao层   实现类
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明
 * @author weiyz
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-13
 * Copyright zzl-apt
 */
@Repository
public class CrmOrderDaoImpl extends BaseHibernateDaoSupper   implements ICrmOrderDao  {
	//日志
	private static Logger logger = LoggerFactory.getLogger(CrmOrderDaoImpl.class);
	/**
	 * 功能说明：执行v3BOC的service			
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
	public List<Map> getCurrentPaycontrol(){
		String sql =
					"SELECT  "+
					"cpc.*, "+
					"co.order_prd_number, "+
					"(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee) frozenMoney "+ // 冻结的金额
					"from  "+
					"crm_paycontrol cpc, "+
					"crm_order co "+ 
					"where  "+
					"co.id = cpc.crm_order_id and "+
					"co.online_type = 1 and "+
					"cpc.repayment_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' ";
		
		return queryBySqlReturnMapList(sql);
	}
	
	/**
	 * 功能说明：通过产品编号找到信贷订单		
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
	public Map getCrmpaycontrolByProductNumber(String order_prd_number){
		if(ChkUtil.isEmpty(order_prd_number)){
			return null;
		}
		String sql = "select * from crm_order where order_prd_number = '"+order_prd_number+"'";
		List<Map> list = queryBySqlReturnMapList(sql);
		String paycontrolSql = "SELECT * from crm_paycontrol where crm_order_id='"+list.get(0).get("id")+"' and repayment_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and status =0";
		return queryBySqlReturnMapList(paycontrolSql).get(0);
	}
	/**
	 * 功能说明：获得定投BOC的信贷明细		
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
	public List<Map> getCurrentImmePaycontrolsBoc(){
		String sql =
				"SELECT  "+
				"cpc.*, "+
				"co.order_prd_number, "+
				"co.order_number, "+
				"bci.bank_account, "+
				"bci.fy_account, "+
				"(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee) frozenMoney "+ // 冻结的金额
				"from  "+
				"crm_paycontrol cpc, "+
				"bg_cust_info bci, "+
				"crm_order co, "+ 
				"prd_view_crmproduct vcpi "+ 
				"where  "+
				"co.id = cpc.crm_order_id and "+
				"co.pro_detail_id=vcpi.detailId and "+
				"cpc.cust_info_id = bci.id and "+
				"cpc.status = 0 and "+
				"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null ) and "+
				"co.clearing_channel = '2' and "+
				"co.investment_model = '1' and "+
				"cpc.repayment_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
	
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：获得POC的信贷明细		
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
	public List<Map> getCurrentPaycontrolsPoc(){
		String sql =
				"SELECT  DISTINCT "+
						"cpc.*, "+
						"co.order_prd_number, "+
						"co.order_number, "+
						"bci.bank_account, "+
						"bci.fy_account, "+
						"bci.cust_mobile, "+
						"sbc.card_number  accntno, "+
						"sbc.cust_name  accntnm, "+
						"sbc.bank_number  bankno, "+
						"(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee) frozenMoney "+ // 冻结的金额
						"from  "+
						"crm_paycontrol cpc, "+
						"bg_cust_info bci, "+
						"crm_order co, "+ 
						"prd_view_crmproduct vcpi, "+ 
						"sys_bank_card sbc "+ 
						"where  "+
						"co.id = cpc.crm_order_id and "+
						"co.pro_detail_id=vcpi.detailId and "+
						"cpc.cust_info_id = bci.id and "+
						"sbc.cust_info_id = bci.id and "+
						"sbc.source = 1 and "+
						"sbc.bind_status = 1 and "+
						"cpc.status = 0 and "+
						"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null ) and "+
						"co.clearing_channel = '1' and "+
						"cpc.repayment_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：获得定投POC的信贷明细		
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
	public List<Map> getCurrentImmePaycontrolsPoc(){
		String sql =
				"SELECT  "+
						"cpc.*, "+
						"co.order_prd_number, "+
						"co.order_number, "+
						"bci.bank_account, "+
						"bci.fy_account, "+
						"(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee) frozenMoney "+ // 冻结的金额
						"from  "+
						"crm_paycontrol cpc, "+
						"bg_cust_info bci, "+
						"crm_order co, "+ 
						"prd_view_crmproduct vcpi "+ 
						"where  "+
						"co.id = cpc.crm_order_id and "+
						"co.pro_detail_id=vcpi.detailId and "+
						"cpc.cust_info_id = bci.id and "+
						"cpc.status = 0 and "+
						"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null ) and "+
						"co.clearing_channel = '1' and "+
						"co.investment_model = '1' and "+
						"cpc.repayment_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：获得线上的信贷明细		
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
	public List<Map> getCurrentDetePaycontrolsBoc(){
		String sql =
						"SELECT  "+
						"cpc.*, "+
						"co.order_prd_number, "+
						"co.order_number, "+
						"co.clearing_channel, "+
						"bci.bank_account, "+
						"bci.fy_account, "+
						"(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee) frozenMoney "+ // 冻结的金额
						"from  "+
						"crm_paycontrol cpc, "+
						"bg_cust_info bci, "+
						"crm_order co, "+
						"prd_view_crmproduct vcpi "+ 
						"where  "+
						"co.pro_detail_id=vcpi.detailId AND "+ 
						"co.id = cpc.crm_order_id and "+
						"cpc.cust_info_id = bci.id and "+
						"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
						"cpc.status = 0 and "+
						"co.investment_model ='2' and "+
						"co.clearing_channel ='2' and "+
						"cpc.repayment_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：获得线上的信贷明细		
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
	public List<Map> getCurrentDetePaycontrolsPoc(){
		String sql =
				"SELECT  "+
						"cpc.*, "+
						"co.order_prd_number, "+
						"co.order_number, "+
						"co.clearing_channel, "+
						"bci.bank_account, "+
						"bci.fy_account, "+
						"(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee) frozenMoney "+ // 冻结的金额
						"from  "+
						"crm_paycontrol cpc, "+
						"bg_cust_info bci, "+
						"crm_order co, "+
						"prd_view_crmproduct vcpi "+ 
						"where  "+
						"co.id = cpc.crm_order_id and "+
						"co.pro_detail_id=vcpi.detailId AND "+ 
						"cpc.cust_info_id = bci.id and "+
						"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
						"cpc.status = 0 and "+
						"co.investment_model ='2' and "+
						"co.clearing_channel ='1' and "+
						"cpc.repayment_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：判断是不是最后一期	
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
	public boolean lastTimes(String crmOrderId,String controlId){
		String sql  = "select  * from crm_paycontrol where  crm_order_id ='"+crmOrderId+"' ORDER BY repayment_duetime desc limit 1  ";
		List<Map> list = queryBySqlReturnMapList(sql);
		if(controlId.equals(list.get(0).get("id").toString())){
			return true;
		}
		return false;
	}
	/**
	 * 功能说明：获得逾期的明细
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
	public List<Map> getOverDetePaycontrolsBoc(){
		String sql =
				"SELECT  "+
				"cpc.*, "+
				"co.order_prd_number, "+
				"co.order_number, "+
				"bci.bank_account, "+
				"bci.fy_account, "+
				"(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee+cpc.remain_interest+cpc.overdue_violate_money) frozenMoney "+ // 冻结的金额
				"from  "+
				"crm_paycontrol cpc, "+
				"bg_cust_info bci, "+
				"crm_order co, "+ 
				"prd_view_crmproduct vcpi "+ 
				"where  "+
				"co.id = cpc.crm_order_id and "+
				"cpc.cust_info_id = bci.id and "+
				"co.pro_detail_id=vcpi.detailId AND "+ 
				"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
				"cpc.status = 2 and "+
				"bci.bank_account is not null and "+
				"co.investment_model ='2'  and "+ 
				"cpc.exempt_status =0 AND "+
				"co.clearing_channel = '2' and (co.order_type = 3 or co.order_type is null or co.order_type='') order by cpc.repayment_time asc";
	
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：获得直投BOC逾期的明细
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
	public List<Map> getOverImmePaycontrolsBoc(){
		String sql =
				"SELECT  "+
				"cpc.*, "+
				"co.order_prd_number, "+
				"co.order_number, "+
				"bci.bank_account, "+
				"bci.fy_account, "+
				"(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee+cpc.remain_interest+cpc.overdue_violate_money) frozenMoney "+ // 冻结的金额
				"from  "+
				"crm_paycontrol cpc, "+
				"bg_cust_info bci, "+
				"crm_order co, "+ 
				"prd_view_crmproduct vcpi "+ 
				"where  "+
				"co.id = cpc.crm_order_id and "+
				"cpc.cust_info_id = bci.id and "+
				"co.pro_detail_id=vcpi.detailId AND "+ 
				"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
				"cpc.status = 2 and "+
				"bci.bank_account is not null and "+
				"cpc.exempt_status =0 AND "+
				"co.investment_model ='1'  and "+ 
				"co.clearing_channel = '2' and (co.order_type = 3 or co.order_type is null or co.order_type='') order by cpc.repayment_time asc";
	
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：获得POC逾期的明细
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
	public List<Map> getOverPaycontrolsPoc(){
		String sql =
				"SELECT DISTINCT "+
				"cpc.*, "+
				"co.order_prd_number, "+
				"co.order_number, "+
				"bci.bank_account, "+
				"bci.fy_account, "+
				"bci.cust_mobile, "+
				"sbc.card_number  accntno, "+
				"sbc.cust_name  accntnm, "+
				"sbc.bank_number  bankno, "+
				"sum(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee+cpc.remain_interest+cpc.overdue_violate_money) frozenMoney "+ // 冻结的金额
				"from  "+
				"crm_paycontrol cpc, "+
				"bg_cust_info bci, "+
				"crm_order co, "+ 
				"prd_view_crmproduct vcpi, "+ 
				"sys_bank_card sbc "+ 
				"where  "+
				"co.id = cpc.crm_order_id and "+
				"cpc.cust_info_id = bci.id and "+
				"co.pro_detail_id=vcpi.detailId AND "+ 
				"sbc.cust_info_id = bci.id and "+
				"sbc.source = 1 and "+
				"sbc.bind_status = 1 and "+
				"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
				"cpc.status = 2 and "+
//				"bci.bank_account is not null and "+
				"cpc.exempt_status = 0 AND "+
				"co.clearing_channel = '1' and (co.order_type = 3 or co.order_type is null or co.order_type='') GROUP BY cpc.crm_order_id order by cpc.repayment_time asc";
	
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：获得直投POC逾期的明细
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
	public List<Map> getOverImmePaycontrolsPoc(){
		String sql =
				"SELECT  "+
				"cpc.*, "+
				"co.order_prd_number, "+
				"co.order_number, "+
				"bci.bank_account, "+
				"bci.fy_account, "+
				"(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee+cpc.remain_interest+cpc.overdue_violate_money) frozenMoney "+ // 冻结的金额
				"from  "+
				"crm_paycontrol cpc, "+
				"bg_cust_info bci, "+
				"crm_order co, "+ 
				"prd_view_crmproduct vcpi "+ 
				"where  "+
				"co.id = cpc.crm_order_id and "+
				"cpc.cust_info_id = bci.id and "+
				"co.pro_detail_id=vcpi.detailId AND "+ 
				"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
				"cpc.status = 2 and "+
				"bci.bank_account is not null and "+
				"cpc.exempt_status =0 AND "+
				"co.investment_model ='1'  and "+ 
				"co.clearing_channel = '1' and (co.order_type = 3 or co.order_type is null or co.order_type='') order by cpc.repayment_time asc";
	
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：获得逾期的明细
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
	public List<Map> getOverDetePaycontrolsPoc(){
		String sql =
						"SELECT  "+
						"cpc.*, "+
						"co.order_prd_number, "+
						"co.order_number, "+
						"bci.bank_account, "+
						"bci.fy_account, "+
						"(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee+cpc.remain_interest+cpc.overdue_violate_money) frozenMoney "+ // 冻结的金额
						"from  "+
						"crm_paycontrol cpc, "+
						"bg_cust_info bci, "+
						"crm_order co, "+ 
						"prd_view_crmproduct vcpi "+ 
						"where  "+
						"co.id = cpc.crm_order_id and "+
						"cpc.cust_info_id = bci.id and "+
						"co.pro_detail_id=vcpi.detailId AND "+ 
						"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
						"cpc.status = 2 and "+
						"bci.bank_account is not null and "+
						"cpc.exempt_status =0 AND "+
						"co.investment_model ='2'  and "+ 
						"co.clearing_channel = '1' and (co.order_type = 3 or co.order_type is null or co.order_type='') order by cpc.repayment_time asc";
		
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：获得逾期的明细
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
	public List<Map> getOverLinePaycontrols(){
		String sql =
				"SELECT  "+
						"cpc.*, "+
						"co.order_prd_number, "+
						"bci.bank_account, "+
						"bci.fy_account, "+
						"(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee+cpc.remain_interest+cpc.overdue_violate_money) frozenMoney "+ // 冻结的金额
						"from  "+
						"crm_paycontrol cpc, "+
						"bg_cust_info bci, "+
						"crm_order co "+ 
						"where  "+
						"co.id = cpc.crm_order_id and "+
						"cpc.cust_info_id = bci.id and "+
						"cpc.status = 2 and "+
						"cpc.exempt_status =0 AND "+
						"co.online_type = 0  order by cpc.repayment_time asc";
		
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：获得线下逾期的明细
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
	public List<Map> getOverLinePaycontrolsWithPoc(){
		String sql =
				"SELECT  "+
						"cpc.*, "+
						"bci.fy_account, "+
						"sum(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee+cpc.remain_interest+cpc.overdue_violate_money) frozenMoney "+ // 冻结的金额
						"from  "+
						"crm_paycontrol cpc, "+
						"bg_cust_info bci, "+
						"crm_order co "+ 
						"where  "+
						"co.id = cpc.crm_order_id and "+
						"cpc.cust_info_id = bci.id and "+
						"cpc.status = 2 and "+
						"cpc.exempt_status =0 AND "+
						"co.clearing_channel = 1 AND "+
						"co.online_type = 0 and " +
						"(co.order_type = 3 or co.order_type is null or co.order_type='') " +
						"GROUP BY cpc.crm_order_id   order by cpc.repayment_time asc";
		
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：获得线下逾期的明细
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
	public List<Map> getOverLinePaycontrolsWithBoc(){
		String sql =
				"SELECT  "+
						"cpc.*, "+
						"co.order_prd_number, "+
						"co.order_number, "+
						"bci.bank_account, "+
						"bci.fy_account, "+
						"(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee+cpc.remain_interest+cpc.overdue_violate_money) frozenMoney "+ // 冻结的金额
						"from  "+
						"crm_paycontrol cpc, "+
						"bg_cust_info bci, "+
						"crm_order co "+ 
						"where  "+
						"co.id = cpc.crm_order_id and "+
						"cpc.cust_info_id = bci.id and "+
						"cpc.status = 2 and "+
						"cpc.exempt_status =0 AND "+
						"co.clearing_channel = 2 AND "+
						"co.online_type = 0 and (co.order_type = 3 or co.order_type is null or co.order_type='')  order by cpc.repayment_time asc";
		
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：线下逾期还款通过poc
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
	public void overRepayByPoc(String crmOrderId){
		//准备获得所有的逾期明细
		String overSql  = 
						"SELECT  "+
						"cpc.* "+
						"from  "+
						"crm_paycontrol cpc, "+
						"crm_order co "+ 
						"where  "+
						"co.id = cpc.crm_order_id and "+
						"cpc.status = 2 and "+
						"cpc.exempt_status in (0,2) AND "+
						"co.clearing_channel = '1' AND "+
						"cpc.crm_order_id = '"+crmOrderId+"'  order by cpc.repayment_time asc";
		List<Map> crmPaycontrols = queryBySqlReturnMapList(overSql);
		if(ListTool.isNullOrEmpty(crmPaycontrols)){logger.warn("查找该订单对应的明细时发现为空!订单id:"+crmOrderId);return;}
		//1.添加记录
		//2.修改zzl明细
		//3.添加zzl记录
		//4.修改crm明细
		//5.判断订单是否结束
		for(Map crmPaycontrol:crmPaycontrols){
			try {
				CrmPaycontrol cpc = (CrmPaycontrol) findById(CrmPaycontrol.class, crmPaycontrol.get("id").toString());
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
				cpr.setShouldMONEY(ArithUtil.add(new Double[]{
						cpc.getRemainAccrual(),
						cpc.getRemainCapital(),
						cpc.getRemainInterest(),
						cpc.getRemainManageFee(),
						cpc.getOverdueViolateMoney()
				}));
				cpr.setOperationPlatform(StaticData.OP_ZDHK);/**自动还款*/
				cpr.setPaymentChannel(StaticData.NPC_BANKCARD);/**银行卡代扣*/
				cpr.setPaymentPlatform(StaticData.NPP_FUYOU);/**富有*/
				
				add(cpr);
				//更新信贷
				cpc.setStatus(1);
				cpc.setRemainAccrual(0d);
				cpc.setRemainCapital(0d);
				cpc.setRemainInterest(0d);
				cpc.setRemainManageFee(0d);
				cpc.setOverdueViolateMoney(0d);
				update(cpc);
				//开始判断是否可以结清
				CrmOrder crmOrder = (CrmOrder) findById(CrmOrder.class, cpc.getCrmOrderId());
				String sql ="select * from crm_paycontrol where crm_order_id='"+cpc.getCrmOrderId()+"' and status in (0,2)";
				List unClearlist = queryBySqlReturnMapList(sql);
				//长度为0代表该笔订单已经结束了
				if(ListTool.isNullOrEmpty(unClearlist)){
					crmOrder.setOrderTradeStatus(4);
					crmOrder.setClearType(1);
					crmOrder.setFinishTime(DateUtil.getCurrentTime());
					update(crmOrder);
					try{
					CrmApplay apply = (CrmApplay) findById(CrmApplay.class,crmOrder.getCrmApplayId());
					if(ChkUtil.isNotEmpty(apply)){
						apply.setTradeStatus(4);
						apply.setRepaymentStatus(1);
						apply.setStatus(4);
						update(apply);
					}
					}catch (Exception e) {
					}
					//更新资产包
					String sql2 ="select * from pkg_cust_crmorder where crm_order_id='"+cpc.getCrmOrderId()+"' ";
					List<Map> pkgList = queryBySqlReturnMapList(sql2);
					if(ListTool.isNullOrEmpty(pkgList)){
						for(Map pkgMap:pkgList){
							try{
								PkgCustCrmorder pcc = (PkgCustCrmorder) findById(PkgCustCrmorder.class, pkgMap.get("id").toString());
								pcc.setStatus("3");
								update(pcc);
							}catch (Exception e) {
								e.printStackTrace();
								logger.warn(e.getMessage(),e);
								logger.warn("更新资产包信贷订单时失败!信贷订单id:"+cpc.getCrmOrderId());
							}
						}
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.warn(e.getMessage(),e);
				logger.warn("富友代扣成功,但是在修改明细时失败!订单id:"+crmOrderId);
			}
			
		}
	}
	/**
	 * 功能说明：获取今日未结清订单
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
	public List<Map> getNotClearPaycontrols(){
		String sql  = "SELECT vcpi.cptnumber,cpc.* "+
					  "From " +
					  "crm_paycontrol cpc,crm_order co,prd_view_crmproduct vcpi where co.pro_detail_id=vcpi.detailId AND (vcpi.cptnumber !='A007' or vcpi.cptnumber is null ) and co.id=cpc.crm_order_id and  cpc.status in(0,2) and cpc.repayment_time < '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		String checkSql  =    "SELECT cpc.* " +
							  "From " +
							  "crm_paycontrol cpc,crm_order co,prd_view_crmproduct vcpi where co.pro_detail_id=vcpi.detailId AND (vcpi.cptnumber !='A007' or vcpi.cptnumber is null ) and co.id=cpc.crm_order_id and cpc.status in(0,2) and cpc.repayment_time < '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"' and (co.order_type = 3 or co.order_type is null or co.order_type='') "; //用来检测 的语句
		if(queryBySqlReturnMapList(checkSql).size() == 0){
			return new ArrayList();
		}
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：开始自动逾期
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
	public void autoOver(Map crmPaycontrol){
		try {
			CrmPaycontrol cpc =(CrmPaycontrol) findById(CrmPaycontrol.class, crmPaycontrol.get("id").toString());
			int moreDay  = DateUtil.getQuot(DateUtil.getCurrentTime(), cpc.getRepaymentTime());
			//准备获取各种利率问题
			String sql ="SELECT  "+
						"pd.overdue_protect_day, "+
						"pd.interest_rate, "+
						"pd.contract_violate_rate, "+
						"pd.id productId "+
						"from  "+
						"crm_order co, "+
						"pro_working_product_detail pd "+
						"where co.pro_detail_id = pd.id and co.id ='"+cpc.getCrmOrderId()+"'" ;
			Map rateMap = queryBySqlReturnMapList(sql).get(0);
			String totalRemainCapitalSql = "SELECT sum(should_capiital) totalRemainCapital from crm_paycontrol where crm_order_id='"+cpc.getCrmOrderId()+"'";
			double totalRemainCapital = NumberUtil.parseDouble(queryBySqlReturnMapList(totalRemainCapitalSql).get(0).get("totalRemainCapital"));
			double overcost =NumberFormat.format(ArithUtil.mul(totalRemainCapital,NumberUtil.parseDouble(rateMap.get("interest_rate"))/100d ));
			double overDue =NumberFormat.format(ArithUtil.mul(totalRemainCapital, NumberUtil.parseDouble(rateMap.get("contract_violate_rate"))/100d));
			int protectDay  = Integer.parseInt(rateMap.get("overdue_protect_day").toString());
			String cptNumber = crmPaycontrol.get("cptnumber")==null?"":crmPaycontrol.get("cptnumber").toString();
			if(!"A010".equals(cptNumber)){
				cpc.setShouldInterest(ArithUtil.add(overcost,cpc.getShouldInterest()));
				//开始计算违约金
				if(!"2".equals(cpc.getStatus().toString())&&cpc.getShouldViolateFee()==0){
					cpc.setShouldViolateFee(overDue);
				}
				if((moreDay-1)==protectDay){
					cpc.setOverdueViolateMoney(cpc.getShouldViolateFee());
					cpc.setRemainInterest(cpc.getShouldInterest());
				}
				if((moreDay-1)>protectDay){
					cpc.setRemainInterest(ArithUtil.add(overcost,cpc.getRemainInterest()));
				}
			}
			/*//查询下是不是宽贷的项目
			String checkA010Sql = 
							"SELECT "+
							"	pd.cptnumber "+
							"FROM "+
							"	crm_order co, "+
							"	prd_view_crmproduct pd "+
							"WHERE "+
							"	co.pro_detail_id = pd.detailId "+
							"AND co.id = '"+cpc.getCrmOrderId()+"'   "+
							"AND pd.cptnumber = 'A010'   ";
			List<Map> checkA010List = queryBySqlReturnMapList(checkA010Sql);*/
			//说明是宽贷的,算法不同,重新计算
			//if(ListTool.isNotNullOrEmpty(checkA010List)){ 
			if("A010".equals(cptNumber)){
				Map productMap = new HashMap();
				productMap.put("capital",cpc.getShouldCapiital());
				productMap.put("productId",rateMap.get("productId").toString());
				productMap.put("expirationDays",moreDay);
				try{
					String result = HttpUtil.connectByUrl(SysConfig.getInstance().getProperty("productUrl")+"getKuanDaiInfo", productMap,true);
					JSONObject resultJson = JSONObject.fromObject(result);
					double interestRoundMoney = ArithUtil.sub(cpc.getShouldInterest(), cpc.getRemainInterest());
					double overdueRoundMoney = ArithUtil.sub(cpc.getShouldViolateFee(), cpc.getOverdueViolateMoney());
					//罚息
					cpc.setShouldInterest(resultJson.getDouble("defaultInterest"));
					cpc.setRemainInterest(ArithUtil.sub(resultJson.getDouble("defaultInterest"), interestRoundMoney));
					//违约金
					cpc.setShouldViolateFee(resultJson.getDouble("liquidatedDamages"));
					cpc.setOverdueViolateMoney(ArithUtil.sub(resultJson.getDouble("liquidatedDamages"), overdueRoundMoney));
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			cpc.setStatus(2);
			//如果逾期天数和记录的相同,则说明极有可能 已经进行了逾期操作
			if((moreDay == cpc.getOverdueDay())&&cpc.getOverdueDay()>0){
				//不进行任何操作
			}else{
				cpc.setOverdueDay(moreDay);
				update(cpc);
				logger.warn("在执行自动逾期成功,明细id:"+ crmPaycontrol.get("id").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("在执行自动逾期是出现异常,明细id:"+ crmPaycontrol.get("id").toString());
		}
		
	}
	/**
	 * 功能说明：判断是否有逾期
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
	public boolean hasOver(String crm_order_id){
		String sql  = "SELECT * from crm_paycontrol where status = 2 and crm_order_id ='"+crm_order_id+"'";
		List list = queryBySqlReturnMapList(sql);
		if(ListTool.isNotNullOrEmpty(list)){  //如果找到了逾期明细
			logger.warn("发现该订单有逾期明细,订单id:"+crm_order_id);
			return true;
		}
		return false;
	}
	/**
	 * 功能说明：获得v1线下的信贷明细	
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月8日 10:06:10
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getV1CurrentLinePaycontrols(){
		StringBuilder sb=new StringBuilder();
		sb.append(" SELECT  ");
		sb.append(" cpc.id,");
		sb.append(" cpc.cust_info_id,");
		sb.append(" cpc.crm_order_id,");
		sb.append(" co.order_number, ");
		sb.append(" co.clearing_channel, ");
		sb.append(" bci.bank_account, ");
		sb.append(" bci.fy_account, ");
		sb.append(" (ifnull(cpc.remain_capital,0) + ifnull(cpc.remain_accrual,0) + ifnull(cpc.remain_manage_fee,0)) frozenMoney ");//总金额
		sb.append(" from  ");
		sb.append(" crm_paycontrol cpc");
		sb.append(" inner JOIN bg_cust_info bci on cpc.cust_info_id = bci.id");
		sb.append(" inner JOIN crm_order co on co.id = cpc.crm_order_id");
		sb.append(" WHERE");
		sb.append(" cpc. STATUS = 0");//待还
		sb.append(" AND co.order_type = 1");//v1
		sb.append(" AND cpc.Exempt_status = 0");//正常
		sb.append(" AND bci.fy_account IS NOT NULL");//富有账户不为空
		sb.append(" AND cpc.repayment_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%'");//还款日期
		return queryBySqlReturnMapList(sb.toString());
	}
	/**
	 * 功能说明：获取v1今日未结清订单
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年3月14日 10:53:00
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getV1NotClearPaycontrols(){
		String sql  = "SELECT cpc.* "+
					  "From " +
					  "crm_paycontrol cpc,crm_order co where co.id = cpc.crm_order_id and co.order_type = 1 and cpc.status in(0,2) and cpc.Exempt_status = 0 and cpc.repayment_time < '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"' ";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：开始v1自动逾期
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 chengwei
	 * 创建日期：2016年3月8日 15:50:07
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void v1AutoOver(Map crmPaycontrol){
		try {
			CrmPaycontrol cpc =(CrmPaycontrol) findById(CrmPaycontrol.class, crmPaycontrol.get("id").toString());
			int moreDay  = DateUtil.getQuot(DateUtil.getCurrentTime(), cpc.getRepaymentTime());
			//准备获取各种利率问题
			String sql ="SELECT  "+
						"co.credit_money,co.contract_money, "+
						"(pro.over_rate + pro.over_manage_rate) as interestRate "+
						"from  "+
						"crm_order co, "+
						"pro_lower_product pro "+
						"where co.pro_detail_id = pro.id and co.id ='"+cpc.getCrmOrderId()+"'" ;
			Map rateMap = queryBySqlReturnMapList(sql).get(0);
			double contractMoney = NumberUtil.parseDouble(rateMap.get("contract_money"));
			//计算罚息：合同额度*（罚息利率+逾期管理费率）
			double interest =NumberFormat.format(ArithUtil.mul(contractMoney,NumberUtil.parseDouble(rateMap.get("interestRate"))/100d ));
			cpc.setShouldInterest(ArithUtil.add(interest,cpc.getShouldInterest()));
			cpc.setRemainInterest(ArithUtil.add(interest,cpc.getRemainInterest()));
			cpc.setStatus(2);
			//如果逾期天数和记录的相同,则说明极有可能 已经进行了逾期操作
			if((moreDay == cpc.getOverdueDay())&&cpc.getOverdueDay()>0){
				//不进行任何操作
			}else{
				cpc.setOverdueDay(moreDay);
				update(cpc);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("在执行v1自动逾期是出现异常,明细id:"+ crmPaycontrol.get("id").toString());
		}
	}
	/**
	 * 功能说明：获得v1逾期的明细
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月9日 10:41:59
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getV1OverPaycontrols(){
		StringBuilder sb=new StringBuilder();
		sb.append(" SELECT  ");
		sb.append(" cpc.id,");
		sb.append(" cpc.cust_info_id,");
		sb.append(" cpc.crm_order_id,");
		sb.append(" co.order_number, ");
		sb.append(" co.clearing_channel, ");
		sb.append(" bci.bank_account, ");
		sb.append(" bci.fy_account, ");
		sb.append(" sum(ifnull(cpc.remain_capital,0)+ifnull(cpc.remain_accrual,0)+ifnull(cpc.remain_manage_fee,0)+ifnull(cpc.remain_interest,0)) frozenMoney");//总金额
		sb.append(" from  ");
		sb.append(" crm_paycontrol cpc");
		sb.append(" inner JOIN bg_cust_info bci on cpc.cust_info_id = bci.id");
		sb.append(" inner JOIN crm_order co on co.id = cpc.crm_order_id");
		sb.append(" WHERE");
		sb.append(" cpc. STATUS = 2");//待还
		sb.append(" AND co.order_type = 1");//v1
		sb.append(" AND cpc.Exempt_status = 0");//正常
		sb.append(" AND bci.fy_account IS NOT NULL");//富有账户不为空
		sb.append(" GROUP BY cpc.crm_order_id  order by cpc.repayment_time asc");//还款日期
		/*String sql =
				"SELECT  "+
						"cpc.*, "+
						"bci.fy_account, "+
						"sum(ifnull(cpc.remain_capital,0)+ifnull(cpc.remain_accrual,0)+ifnull(cpc.remain_manage_fee,0)+ifnull(cpc.remain_interest,0)) frozenMoney "+ // 金额
						"from  "+
						"crm_paycontrol cpc, "+
						"bg_cust_info bci, "+
						"crm_order co "+ 
						"where  "+
						"co.id = cpc.crm_order_id and "+
						"cpc.cust_info_id = bci.id and "+
						"cpc.status = 2 and "+
						"cpc.exempt_status =0 AND "+
						"co.clearing_channel = 1 AND "+
						"co.order_type = 1 " +
						"GROUP BY cpc.crm_order_id  order by cpc.repayment_time asc";*/
		return queryBySqlReturnMapList(sb.toString());
	}
	/**
	 * 功能说明：v1逾期还款
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:12:54
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void overRepayByV1(String crmOrderId){
		//准备获得所有的逾期明细
		String overSql  = 
						"SELECT  "+
						"cpc.* "+
						"from  "+
						"crm_paycontrol cpc, "+
						"crm_order co "+ 
						"where  "+
						"co.id = cpc.crm_order_id and "+
						"cpc.status = 2 and "+
						"cpc.exempt_status =0 AND "+
						"co.clearing_channel = 1 AND "+
						"co.online_type = 0 and "+ 
						"cpc.crm_order_id = '"+crmOrderId+"'  order by cpc.repayment_time asc";
		List<Map> crmPaycontrols = queryBySqlReturnMapList(overSql);
		if(ListTool.isNullOrEmpty(crmPaycontrols)){logger.warn("查找该订单对应的明细时发现为空!订单id:"+crmOrderId);return;}
		//1.添加记录
		//2.修改zzl明细
		//3.添加zzl记录
		//4.修改crm明细
		//5.判断订单是否结束
		for(Map crmPaycontrol:crmPaycontrols){
			try {
				CrmPaycontrol cpc = (CrmPaycontrol) findById(CrmPaycontrol.class, crmPaycontrol.get("id").toString());
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
				cpr.setShouldMONEY(ArithUtil.add(new Double[]{
						cpc.getRemainAccrual(),
						cpc.getRemainCapital(),
						cpc.getRemainInterest(),
						cpc.getRemainManageFee(),
						cpc.getOverdueViolateMoney()
				}));
				add(cpr);
				//更新信贷
				cpc.setStatus(1);
				cpc.setRemainAccrual(0d);
				cpc.setRemainCapital(0d);
				cpc.setRemainInterest(0d);
				cpc.setRemainManageFee(0d);
				cpc.setOverdueViolateMoney(0d);
				update(cpc);
				//开始判断是否可以结清
				CrmOrder crmOrder = (CrmOrder) findById(CrmOrder.class, cpc.getCrmOrderId());
				String sql ="select * from crm_paycontrol where crm_order_id='"+cpc.getCrmOrderId()+"' and status in (0,2)";
				List unClearlist = queryBySqlReturnMapList(sql);
				//长度为0代表该笔订单已经结束了
				if(ListTool.isNullOrEmpty(unClearlist)){
					crmOrder.setOrderTradeStatus(4);
					crmOrder.setClearType(1);
					crmOrder.setFinishTime(DateUtil.getCurrentTime());
					update(crmOrder);
					try{
					CrmApplay apply = (CrmApplay) findById(CrmApplay.class,crmOrder.getCrmApplayId());
					if(ChkUtil.isNotEmpty(apply)){
						apply.setTradeStatus(4);
						apply.setRepaymentStatus(1);
						apply.setStatus(4);
						update(apply);
					}
					}catch (Exception e) {
						CrmPaycontrol cpcc=new CrmPaycontrol();
						cpcc=(CrmPaycontrol) findById(CrmPaycontrol.class, crmPaycontrol.get("id").toString());
						cpcc.setAbnormalStatus(0);
						update(cpcc);
						//线上订单 正常还款成功，转信用账户失败
						logger.warn("V1订单正常还款成功,更新数据失败，还款明细id:"+crmPaycontrol.get("id").toString()+"");
						JSONObject smsJson = new JSONObject();
						smsJson.put("text", "V1订单正常还款成功,更新数据失败，还款明细id:"+crmPaycontrol.get("id").toString()+"");
						SmsUtil.senErrorMsg(smsJson);
					}
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.warn(e.getMessage(),e);
				logger.warn("富友代扣成功,但是在修改明细时失败!订单id:"+crmOrderId);
			}
			
		}
	}
	/**
	 * 功能说明：获得直投的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpEfPayImmePoc(){
		String ImmePocSql  = 
						"SELECT  "+
						"'1' Imme,  "+ //直投
						"bepc.*,  "+
						"beo.invest_auz_code,  "+
						"beo.ef_order_id lineEfOrderId,"+
						"beo.crm_order_id,"+
						"beo.order_number efOrderNumber,"+
						"bci.bank_account, "+
						"bci.fy_account, "+
						"co.id crmOrderId, "+
						"co.order_prd_number, "+
						"co.order_number, "+
						"vepi.pdInvestRate INVEST_RATE "+
						"from  "+
						"bg_ef_paycontrol bepc, "+
						"bg_ef_orders beo, "+
						"crm_order co, "+ 
						"bg_cust_info bci, "+ 
						"view_ef_union_product_info vepi, "+ 
						"prd_view_crmproduct vcpi "+ 
						"where  "+
						"bepc.ef_order_id = beo.id and  "+
						"beo.crm_order_id = co.id  and "+ 
						"bci.id = bepc.cust_info_id and "+ 
						"co.ef_prd_detail_id=vepi.pdId AND "+ 
						"co.pro_detail_id=vcpi.detailId AND "+ 
						"co.investment_model ='1'  and "+ 
						"co.clearing_channel ='2'  and "+ 
						"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
						"beo.pay_status in (1) AND "+
						"bepc.pay_status in (0) AND "+
						"bepc.live_status in (1) AND "+
						"beo.ef_type not in (5) AND "+
						"bepc.pay_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		return queryBySqlReturnMapList(ImmePocSql);
		
	}
	/**
	 * 功能说明：获得定投的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpEfPaySetePoc(){
		String ImmePocSql  = 
				"SELECT  "+
				"'1' Imme,  "+ //直投
				"bepc.*,  "+
				"beo.invest_auz_code,  "+
				"beo.ef_order_id lineEfOrderId,"+
				"beo.crm_order_id,"+
				"beo.order_number efOrderNumber,"+
				"bci.bank_account, "+
				"bci.fy_account, "+
				"co.id crmOrderId, "+
				"co.order_prd_number, "+
				"co.order_number, "+
				"vepi.pdInvestRate INVEST_RATE "+
				"from  "+
				"bg_ef_paycontrol bepc, "+
				"bg_ef_orders beo, "+
				"crm_order co, "+ 
				"bg_cust_info bci, "+ 
				"view_ef_union_product_info vepi, "+ 
				"prd_view_crmproduct vcpi "+ 
				"where  "+
				"bepc.ef_order_id = beo.id and  "+
				"beo.crm_order_id = co.id  and "+ 
				"bci.id = bepc.cust_info_id and "+ 
				"co.ef_prd_detail_id=vepi.pdId AND "+ 
				"co.pro_detail_id=vcpi.detailId AND "+ 
				"co.investment_model ='2'  and "+ 
				"co.clearing_channel ='2'  and "+ 
				"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
				"beo.pay_status in (1) AND "+
				"bepc.pay_status in (0) AND "+
				"bepc.live_status in (1) AND "+
				"beo.ef_type not in (5) AND "+
				"bepc.pay_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		return queryBySqlReturnMapList(ImmePocSql);
	}
	/**
	 * 功能说明：获得直投的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpCrmPayImmePoc(){
			String  ImmePocSql=	"SELECT  "+
				"cpc.*, "+
				"co.order_prd_number, "+
				"co.order_number, "+
				"bci.bank_account, "+
				"bci.fy_account, "+
				"bci.cust_mobile, "+
				"(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee) frozenMoney "+ // 冻结的金额
				"from  "+
				"crm_paycontrol cpc, "+
				"bg_cust_info bci, "+
				"crm_order co, "+ 
				"prd_view_crmproduct vcpi "+ 
				"where  "+
				"co.id = cpc.crm_order_id and "+
				"co.pro_detail_id=vcpi.detailId and "+
				"cpc.cust_info_id = bci.id and "+
				"cpc.status = 0 and "+
				"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null ) and "+
				"co.clearing_channel = '1' and "+
				"co.investment_model = '1' and "+
				"cpc.repayment_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
			return queryBySqlReturnMapList(ImmePocSql);
	}
	/**
	 * 功能说明：获得定投的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpCrmPaySetePoc(){
		String  ImmePocSql=	"SELECT  "+
				"cpc.*, "+
				"co.order_prd_number, "+
				"co.order_number, "+
				"bci.bank_account, "+
				"bci.fy_account, "+
				"(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee) frozenMoney "+ // 冻结的金额
				"from  "+
				"crm_paycontrol cpc, "+
				"bg_cust_info bci, "+
				"crm_order co, "+ 
				"prd_view_crmproduct vcpi "+ 
				"where  "+
				"co.id = cpc.crm_order_id and "+
				"co.pro_detail_id=vcpi.detailId and "+
				"cpc.cust_info_id = bci.id and "+
				"cpc.status = 0 and "+
				"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null ) and "+
				"co.clearing_channel = '1' and "+
				"co.investment_model = '2' and "+
				"cpc.repayment_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
			return queryBySqlReturnMapList(ImmePocSql);
	}
	/**
	 * 功能说明：获得定投的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpCrmOverPayImmePoc(){
		String sql =
				"SELECT  "+
				"cpc.*, "+
				"co.order_prd_number, "+
				"co.order_number, "+
				"bci.bank_account, "+
				"bci.fy_account, "+
				"(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee+cpc.remain_interest+cpc.overdue_violate_money) frozenMoney "+ // 冻结的金额
				"from  "+
				"crm_paycontrol cpc, "+
				"bg_cust_info bci, "+
				"crm_order co, "+ 
				"prd_view_crmproduct vcpi "+ 
				"where  "+
				"co.id = cpc.crm_order_id and "+
				"cpc.cust_info_id = bci.id and "+
				"co.pro_detail_id=vcpi.detailId AND "+ 
				"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
				"cpc.status = 2 and "+
				"bci.bank_account is not null and "+
				"cpc.exempt_status =0 AND "+
				"co.investment_model ='1'  and "+ 
				"co.clearing_channel = '1' and (co.order_type = 3 or co.order_type is null or co.order_type='') order by cpc.repayment_time asc";
	
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：获得定投的poc当日文件
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2016年3月14日 11:34:29
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getFtpCrmOverPaySetePoc(){
		String sql =
				"SELECT  "+
				"cpc.*, "+
				"co.order_prd_number, "+
				"co.order_number, "+
				"bci.bank_account, "+
				"bci.fy_account, "+
				"(cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee+cpc.remain_interest+cpc.overdue_violate_money) frozenMoney "+ // 冻结的金额
				"from  "+
				"crm_paycontrol cpc, "+
				"bg_cust_info bci, "+
				"crm_order co, "+ 
				"prd_view_crmproduct vcpi "+ 
				"where  "+
				"co.id = cpc.crm_order_id and "+
				"cpc.cust_info_id = bci.id and "+
				"co.pro_detail_id=vcpi.detailId AND "+ 
				"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
				"cpc.status = 2 and "+
				"bci.bank_account is not null and "+
				"cpc.exempt_status =0 AND "+
				"co.investment_model ='2'  and "+ 
				"co.clearing_channel = '1' and (co.order_type = 3 or co.order_type is null or co.order_type='') order by cpc.repayment_time asc";
	
		return queryBySqlReturnMapList(sql);
	}

	/**
	 * 获取赎楼理财到期列表
	 * @Title: 
	 * @auter sky 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @return    设定文件 
	 * @return     返回类型 
	 * @throws
	 */
	public List<Map> searchRansomFloorOrders() {
		String sql = 
						" SELECT "+
						" 	co.online_type, "+
						" 	co.clearing_channel, "+
						" 	co.pro_type_id, "+
						" 	co.pro_detail_id, "+
						" 	efPro.pdInvestRate, "+
						" 	co.loan_time, "+
						" 	pro.maxDay, "+
						" 	beo.ef_fective_amt, "+
						" 	bci.fy_account, "+
						" 	beo.invest_auz_code, "+
						" 	beo.investment_model, "+
						" 	beo.id beoId, "+
						" 	bci.bank_account, "+
						" 	co.id crmOrderId, "+
						" 	efPro.pdInvestManageRate, "+
						" 	co.id orderId "+
						" FROM "+
						" 	crm_order co "+
						" INNER JOIN prd_view_crmv3 pro ON co.pro_detail_id = pro.id "+
						" INNER JOIN bg_ef_orders beo ON beo.crm_order_id = co.id "+
						" INNER JOIN bg_cust_info bci ON bci.id = beo.cust_info_id "+
						" INNER JOIN view_ef_union_product_info efPro ON efPro.pdId = co.ef_prd_detail_id "+
						" WHERE "+
						" 	co.order_trade_status = 1 "+
						" AND pro.cptnumber = 'A007' "+
						" AND beo.pay_status = 1 "+
						" HAVING "+
						" datediff(NOW(), co.loan_time) = pro.maxDay; ";
		List<Map> list =queryBySqlReturnMapList(sql); 
		return list;
	}
	
	/**
	 * 查看推荐人
	 * @Title: 
	 * @auter sky 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return     返回类型 
	 * @throws
	 */
	public List searchRefereeConnection(String cust_info_id) throws Exception {
		String sql = "select referee_info_id,referee_income_scale from BG_REFEREE_CONNECTION where status = 1 and cust_info_id ='"+cust_info_id+"'";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 查询信贷还款记录数据
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
	public List<Map> findEfDeteData(){
		String sql = "SELECT * from bg_ef_dete_record where status =0 ";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 查询理财定投数据(手动)
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
	public List<Map> getEfImmeByCrmOrderAndDay(String crmOrderId, String payTime){
		String sql ="SELECT  "+
					"'1' Imme,  "+ //直投
					"bepc.*,  "+
					"beo.invest_auz_code,  "+
					"beo.investment_model,  "+
					"beo.invest_seri_num,  "+
					"beo.ef_order_id lineEfOrderId,"+
					"beo.principal,"+
					"beo.crm_order_id,"+
					"beo.order_number efOrderNumber,"+
					"bci.bank_account, "+
					"bci.fy_account, "+
					"bci.investor_type, "+
					"co.id crmOrderId, "+
					"co.order_prd_number, "+
					"co.order_number, "+
					"co.online_type, "+
					"co.clearing_channel, "+
					//"vepi.pdInvestRate invest_rate "+
					"vcpi.rate*12 invest_rate "+
					"from  "+
					"bg_ef_paycontrol bepc, "+
					"bg_ef_orders beo, "+
					"crm_order co, "+ 
					"bg_cust_info bci, "+ 
				//	"view_ef_union_product_info vepi, "+ 
					"prd_view_crmproduct vcpi "+ 
					"where  "+
					"bepc.ef_order_id = beo.id and  "+
					"beo.crm_order_id = co.id  and "+ 
					"bci.id = bepc.cust_info_id and "+ 
				//	"co.ef_prd_detail_id=vepi.pdId AND "+ 
					"co.pro_detail_id=vcpi.detailId AND "+ 
					"beo.investment_model ='1'  and "+ 
					"co.clearing_channel ='2'  and "+ 
					"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
					"beo.pay_status in (1,3) AND "+
					"bepc.pay_status in (0) AND "+
					"bepc.live_status in (1) AND "+
					"bepc.cust_info_id != '"+StaticData.redCustInfoId+"' AND "+
					"beo.ef_type not in (5) AND "+
					"beo.crm_order_id ='"+crmOrderId+"' AND "+
					"(bepc.operate_type is null or bepc.operate_type = '1' )  and "+ 
					"bepc.pay_time like '"+payTime+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 查询赎楼的理财直投数据
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
	public List<Map> getA007EfByCrmOrder(String crmOrderId){
		String sql ="SELECT  "+
					"bepc.*,  "+
					"beo.invest_auz_code,  "+
					"beo.invest_seri_num,  "+
					"beo.ef_order_id lineEfOrderId,"+
					"beo.principal,"+
					"beo.investment_model,"+
					"beo.crm_order_id,"+
					"beo.order_number efOrderNumber,"+
					"bci.bank_account, "+
					"bci.fy_account, "+
					"bci.investor_type, "+
					"co.id crmOrderId, "+
					"co.order_prd_number, "+
					"co.order_number, "+
					"co.online_type, "+
					"vepi.pdInvestRate invest_rate "+
					"from  "+
					"bg_ef_paycontrol bepc, "+
					"bg_ef_orders beo, "+
					"crm_order co, "+ 
					"bg_cust_info bci, "+ 
					"view_ef_union_product_info vepi, "+ 
					"prd_view_crmproduct vcpi "+ 
					"where  "+
					"bepc.ef_order_id = beo.id and  "+
					"beo.crm_order_id = co.id  and "+ 
					"bci.id = bepc.cust_info_id and "+ 
					"co.ef_prd_detail_id=vepi.pdId AND "+ 
					"co.pro_detail_id=vcpi.detailId AND "+ 
					"co.clearing_channel ='2'  and "+ 
					"beo.pay_status in (1) AND "+
					"bepc.pay_status in (0) AND "+
					"bepc.live_status in (1) AND "+
					"bepc.cust_info_id != '"+StaticData.redCustInfoId+"' AND "+
					"beo.ef_type not in (5) AND "+
					"beo.crm_order_id ='"+crmOrderId+"' AND "+
					"(bepc.operate_type is null or bepc.operate_type = '1' )  and "+ 
					"(co.order_type = 3 or co.order_type is null or co.order_type='') ";
	return queryBySqlReturnMapList(sql);
	}
	/**
	 * 查询理财直投数据(手动)
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
	public List<Map> getEfImmeByCrmOrder(String crmOrderId){
		String sql =
						"SELECT "+
						"	co.order_prd_number, "+
						"	eo.id id, "+
						"	bci.bank_account bank_account, "+
						"	bci.investor_type investor_type, "+
						"	eo.invest_auz_code invest_auz_code, "+
						"	eo.invest_seri_num, "+
						"	eo.principal principal, "+
						"	eo.ef_fective_amt ef_fective_amt, "+
						"	eo.cust_info_id cust_info_id, "+
						"	eo.cust_id cust_id "+
						"FROM "+
						"	bg_ef_orders eo, "+
						"	bg_cust_info bci, "+
						"	crm_order co  "+
						"WHERE "+
						"	eo.investment_model = '1' "+
						"AND eo.crm_order_id=co.id "+
						"AND eo.cust_info_id = bci.id "+
						"AND eo.pay_status IN (1, 3) "+
						"AND eo.crm_order_id = '"+crmOrderId+"' ";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 查询理财定投数据(手动)
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
	public List<Map> getEfDeteByCrmOrder(String crmOrderId){
		String sql =
						"SELECT "+
						"	co.order_prd_number, "+
						"	eo.id id, "+
						"	bci.bank_account bank_account, "+
						"	bci.investor_type investor_type, "+
						"	eo.invest_auz_code invest_auz_code, "+
						"	eo.invest_seri_num, "+
						"	eo.principal principal, "+
						"	eo.cust_info_id cust_info_id, "+
						"	eo.cust_id cust_id, "+
						"	eo.ef_order_id lineEfOrderId "+
						"FROM "+
						"	bg_ef_orders eo, "+
						"	bg_cust_info bci, "+
						"	crm_order co "+
						"WHERE "+
						"	eo.investment_model = '2' "+
						"AND co.id=eo.crm_order_id "+
						"AND eo.cust_info_id = bci.id "+
						"AND eo.pay_status IN (1, 3) "+
						"AND eo.crm_order_id = '"+crmOrderId+"'; ";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 查询理财定投数据
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
	public List<Map> getEfImmeByCrmOrderAndDayRedAccount(String crmOrderId, String payTime){
		String sql ="SELECT  "+
				"'1' Imme,  "+ //直投
				"bepc.*,  "+
				"beo.invest_auz_code,  "+
				"beo.investment_model,  "+
				"beo.invest_seri_num,  "+
				"beo.ef_order_id lineEfOrderId,"+
				"beo.principal,"+
				"beo.crm_order_id,"+
				"beo.order_number efOrderNumber,"+
				"bci.bank_account, "+
				"bci.fy_account, "+
				"bci.investor_type, "+
				"co.id crmOrderId, "+
				"co.order_prd_number, "+
				"co.order_number, "+
				"co.online_type, "+
			//	"vepi.pdInvestRate invest_rate "+
				"vcpi.rate*12 invest_rate "+
				"from  "+
				"bg_ef_paycontrol bepc, "+
				"bg_ef_orders beo, "+
				"crm_order co, "+ 
				"bg_cust_info bci, "+ 
			//	"view_ef_union_product_info vepi, "+ 
				"prd_view_crmproduct vcpi "+ 
				"where  "+
				"bepc.ef_order_id = beo.id and  "+
				"beo.crm_order_id = co.id  and "+ 
				"bci.id = bepc.cust_info_id and "+ 
			//  "co.ef_prd_detail_id=vepi.pdId AND "+ 
				"co.pro_detail_id=vcpi.detailId AND "+ 
				"beo.investment_model ='1'  and "+ 
				"co.clearing_channel ='2'  and "+ 
				"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
				"beo.pay_status in (1) AND "+
				"bepc.pay_status in (0) AND "+
				"bepc.live_status in (1) AND "+
				"bepc.cust_info_id = '"+StaticData.redCustInfoId+"' AND "+
				"beo.ef_type not in (5) AND "+
				"beo.crm_order_id ='"+crmOrderId+"' AND "+
				"(bepc.operate_type is null or bepc.operate_type = '1' )  and "+ 
				"bepc.pay_time like '"+payTime+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 查询理财定投数据
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
	public List<Map> getEfDeteByCrmOrderAndDay(String crmOrderId, String payTime){
		String sql ="SELECT  "+
				"'0' Imme,  "+ //直投
				"bepc.*,  "+
				"beo.invest_auz_code,  "+
				"beo.investment_model,  "+
				"beo.invest_seri_num,  "+
				"beo.ef_order_id lineEfOrderId,"+
				"beo.principal,"+
				"beo.crm_order_id,"+
				"beo.order_number efOrderNumber,"+
				"bci.bank_account, "+
				"bci.fy_account, "+
				"bci.investor_type, "+
				"co.id crmOrderId, "+
				"co.order_prd_number, "+
				"co.order_number, "+
				"co.online_type, "+
			//	"vepi.pdInvestRate invest_rate "+
				"vcpi.rate*12 invest_rate "+
				"from  "+
				"bg_ef_paycontrol bepc, "+
				"bg_ef_orders beo, "+
				"crm_order co, "+ 
				"bg_cust_info bci, "+ 
			//	"view_ef_union_product_info vepi, "+ 
				"prd_view_crmproduct vcpi "+ 
				"where  "+
				"bepc.ef_order_id = beo.id and  "+
				"beo.crm_order_id = co.id  and "+ 
				"bci.id = bepc.cust_info_id and "+ 
			//	"co.ef_prd_detail_id=vepi.pdId AND "+ 
				"co.pro_detail_id=vcpi.detailId AND "+ 
				"beo.investment_model ='2'  and "+ 
				"co.clearing_channel ='2'  and "+ 
				"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
				"beo.pay_status in (1,3) AND "+
				"bepc.pay_status in (0) AND "+
				"bepc.live_status in (1) AND "+
				"bepc.cust_info_id != '"+StaticData.redCustInfoId+"' AND "+
				"beo.ef_type not in (5) AND "+
				"beo.crm_order_id ='"+crmOrderId+"' AND "+
				"(bepc.operate_type is null or bepc.operate_type = '1' )  and "+ 
				"bepc.pay_time like '"+payTime+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		return queryBySqlReturnMapList(sql);
	}
	
	/**
	 * 功能说明：获得信息订单
	 * @param status 查询订单中了2逾期订单  0正常还款订单
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午10:32:09
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getOverOrderBF(String status, String type)
	{
		String sql =
				" SELECT cpc.crm_order_id crmOrderId, cpc.cust_id custId, cpc.cust_info_id custInfoId, bci.CUST_IC idCard , sbc.bank_name bankName , sbc.CUST_NAME idHolder ,sbc.CARD_NUMBER accNo ,bci.bind_card_mobile mobile ,cpc.repayment_time " +
				" from crm_order co,crm_paycontrol cpc, sys_bank_card sbc, bg_cust_info bci " +
				" where " +
				" co.id=cpc.crm_order_id " +
				" and cpc.status = '"+ status + "'" +
				" and co.clearing_channel = 2 " +
				" and co.order_trade_status = 1 " +
				" and cpc.cust_info_id = bci.id " +
				" and sbc.cust_info_id = bci.id " +
				" and (sbc.bank_name is not null and sbc.bank_name != '') " +
				" and (bci.bind_card_mobile is not null and bci.bind_card_mobile != '')" +
				" and sbc.SOURCE = 2 " ;
				if ("0".equals(status))
				{
					sql += " and cpc.repayment_TIME like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' ";
				}
			String sql1 = " and sbc.BIND_STATUS = 1 GROUP BY crm_order_id ORDER BY repayment_time asc" ;
			
			String sql2 = 
				" SELECT cpc.crm_order_id crmOrderId, cpc.cust_id custId, cpc.cust_info_id custInfoId, bci.CUST_IC idCard , sbc.bank_name bankName , sbc.CUST_NAME idHolder ,sbc.CARD_NUMBER accNo ,bci.bind_card_mobile mobile ,cpc.repayment_time " +
				" from crm_order co,crm_paycontrol cpc, sys_bank_card sbc, bg_cust_info bci " +
				" where " +
				" co.id=cpc.crm_order_id " +
				" and cpc.status = '"+ status + "'" +
				" and co.clearing_channel = '200007-0003' " +
				" and co.order_trade_status = 1 " +
				" and cpc.cust_info_id = bci.id " +
				" and sbc.cust_info_id = bci.id " +
				" and (sbc.bank_name is not null and sbc.bank_name != '') " +
				" and (bci.bind_card_mobile is not null and bci.bind_card_mobile != '')" +
				" and sbc.SOURCE = '200007-0003' " ;
				if ("0".equals(status))
				{
					sql2 += " and cpc.repayment_TIME like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' ";
				}
				
			String sql3 =	" and sbc.BIND_STATUS = 1 GROUP BY crm_order_id  ORDER BY repayment_time asc";

		if ("HF".equals(type))
		{
			return queryBySqlReturnMapList(sql2 +sql3);
		}
		return queryBySqlReturnMapList(sql + sql1);
	}
	
	/**
	 * 功能说明：获得逾期或者需要正常还款信息订单明细信息
	 * @param crmOrderId 信贷订单id status 订单种类
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午10:32:09
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getOverPaycontrolBF(String crmOrderId, String status)
	{
		String sql =
				" SELECT cpc.*,  " +
				" (cpc.remain_capital+cpc.remain_accrual+cpc.remain_manage_fee+cpc.remain_interest+cpc.overdue_violate_money) frozenMoney " +
				" from crm_paycontrol cpc " +
				" where " +
				" cpc.crm_order_id = '"+ crmOrderId +"' " ;
		
		String sqlp =	" ORDER BY repayment_time asc ";

		if ("2".equals(status))
		{
			String sqlz = " and cpc.status = 2 " ;
			
			return queryBySqlReturnMapList(sql + sqlz + sqlp);
		}
		
		String sqlz = " and cpc.status in (0, 2) " ;
		
		return queryBySqlReturnMapList(sql + sqlz + sqlp);
	}
	
	/**
	 * 功能说明：获得需要宝付代扣订单信息
	 * @param crmOrderId 信贷订单id status 订单种类
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午10:32:09
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> queryOrders()
	{
		String sql =
				" SELECT * FROM crm_payrecoder WHERE " +
				" operation_platform = '200012-0003' " +
				" AND payment_channel = '200014-0002' " +
				" AND create_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' " +
				" AND payment_platform = '200013-0003'" ;
		
		return queryBySqlReturnMapList(sql);
	}
	
	/**
	 * 功能说明：根据电子账户查询接口用户信息
	 * @param
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：下午3:10:54
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public Map fandBankAccount(String bankAccount)
	{
		String sql =
				" SELECT *,CARD_NUMBER FROM bg_cust_info bci ,sys_bank_card sbc  WHERE " +
				" bci.id = sbc.CUST_INFO_ID " +
				" and bank_account = '"+ bankAccount +"' ";
		List<Map> maps = queryBySqlReturnMapList(sql);
		if (ListTool.isNotNullOrEmpty(maps))
		{
			return maps.get(0);
		}
		return null;
	}
	
	/**
	 * 功能说明：获得赎楼到期订单信息
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> queryRansomFloorOrders() {
		String sql = " select " +
				" co.online_type online_type," +
				" co.clearing_channel clearing_channel," +
				" co.pro_type_id pro_type_id," +
				" co.pro_detail_id pro_detail_id," +
				" efPro.pdInvestRate pdInvestRate," +
				" co.loan_time loan_time," +
				" pro.maxDay maxDay," +
				" beo.ef_fective_amt ef_fective_amt," +
				" bci.hf_account hf_account," +
				" beo.invest_auz_code invest_auz_code," +
				" beo.id beoId," +
				" bci.bank_account bank_account," +
				" efPro.pdInvestManageRate pdInvestManageRate," +
				" co.id orderId ," +
				" bci.cust_name cust_name" +
				" from crm_order co "+
				" inner join prd_view_crmv3 pro on co.pro_detail_id = pro.id "+
				" inner join bg_ef_orders beo on beo.crm_order_id = co.id "+
				" inner join bg_cust_info bci on bci.id = beo.cust_info_id "+
				" inner join view_ef_union_product_info efPro on efPro.pdId = co.ef_prd_detail_id "+
				" where co.order_trade_status = 1 " +
				" and pro.cptnumber = 'A007' " +
				" and beo.pay_status = 1 " +
				" having datediff(NOW(), co.loan_time)>=pro.maxDay ";
		
		List<Map> list =queryBySqlReturnMapList(sql); 
		return list;
	}
	/**
	 * @author yuanhao
	 * @date 2018年4月10日 下午4:12:28
	 * 批量保存
	 */
	public void batchSaveOrUpdate(List<Object> os,int count) {
		Session session = null;
		if (os != null && os.size() > 0) {
			try {
				session = this.getSession(); // 获取Session
				session.beginTransaction(); // 开启事物
				// 循环获取对象
				for (int i = 0, n = os.size(); i < n; i++) {
					session.saveOrUpdate(os.get(i)); // 保存对象
					// 批插入的对象立即写入数据库并释放内存
					if (i % count == 0) {
						session.flush();
						session.clear();
					}
				}
				session.getTransaction().commit(); // 提交事物
			} catch (Exception e) {
				logger.warn(e.getMessage(),e);
				session.getTransaction().rollback(); // 出错将回滚事物
				e.printStackTrace();
			} finally {
				if(session!=null){
					session.close();
				}
			}
		}
	}
}
