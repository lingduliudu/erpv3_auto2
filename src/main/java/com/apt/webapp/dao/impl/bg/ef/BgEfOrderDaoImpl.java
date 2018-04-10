package com.apt.webapp.dao.impl.bg.ef;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.apt.util.ChkUtil;
import com.apt.util.ListTool;
import com.apt.util.MessageTemplete;
import com.apt.util.NumberFormat;
import com.apt.util.NumberUtil;
import com.apt.util.StaticData;
import com.apt.util.arith.ArithUtil;
import com.apt.util.date.DateUtil;
import com.apt.util.mail.Mail;
import com.apt.webapp.base.dao.BaseHibernateDaoSupper;
import com.apt.webapp.dao.bg.ef.IBgEfOrderDao;
import com.apt.webapp.model.bg.ef.BgCustomer;
import com.apt.webapp.model.bg.ef.BgEfOrders;
import com.apt.webapp.model.bg.ef.BgEfPaycontrol;
import com.apt.webapp.model.bg.ef.BgEfPayrecord;
import com.apt.webapp.model.bg.ef.BgReferralIncomeRecord;
import com.apt.webapp.model.bg.ef.BgScoreRecord;
import com.apt.webapp.model.bg.ef.BgSysMessage;
import com.apt.webapp.model.bg.ef.BgSysMessageContent;

@Repository
public class BgEfOrderDaoImpl  extends BaseHibernateDaoSupper  implements IBgEfOrderDao{
	//日志
	private Logger logger = LoggerFactory.getLogger(BgEfOrderDaoImpl.class);
	/**
	 * 功能说明： 获得线上当日待还明细
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
	public List<Map> getCurrentControls(Map efMap) {
		String sql = 
					"SELECT  "+
					"bepc.*,  "+
					"beo.invest_auz_code,  "+
					"beo.crm_order_id,"+
					"bci.bank_account "+
					"from  "+
					"bg_ef_paycontrol bepc, "+
					"bg_ef_orders beo, "+
					"crm_order co, "+ 
					"bg_cust_info bci "+ 
					"where  "+
					"bepc.ef_order_id = beo.id and  "+
					"beo.crm_order_id = co.id  and "+ 
					"bci.id = bepc.cust_info_id and "+ 
					"co.online_type =1  and "+ 
					"beo.pay_status in (1) AND "+
					"bepc.pay_status in (0) AND "+
					"bepc.live_status in (1) AND "+
					"bepc.pay_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' AND "+
					"beo.crm_order_id = '"+efMap.get("crm_order_id")+"' ";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明： 获得直投BOC当日待还明细
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
	public List<Map> getCurrentImmeControlsBoc(){
		String sql = 
				"SELECT  "+
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
				"co.id crmOrderId, "+
				"co.order_prd_number, "+
				"co.order_number, "+
				"co.online_type "+
				"from  "+
				"bg_ef_paycontrol bepc, "+
				"bg_ef_orders beo, "+
				"crm_order co, "+ 
				"bg_cust_info bci, "+ 
				"prd_view_crmproduct vcpi "+ 
				"where  "+
				"bepc.ef_order_id = beo.id and  "+
				"beo.crm_order_id = co.id  and "+ 
				"bci.id = bepc.cust_info_id and "+ 
				"co.pro_detail_id=vcpi.detailId AND "+ 
				"beo.investment_model ='1'  and "+ 
				"co.clearing_channel ='2'  and "+ 
				"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
				"beo.pay_status in (1,3) AND "+
				"bepc.pay_status in (0) AND "+
				"bepc.live_status in (1) AND "+
				"bepc.cust_info_id != '"+StaticData.redCustInfoId+"' AND "+
				"beo.ef_type not in (5) AND "+
				"(bepc.operate_type is null or bepc.operate_type = '0' )  and "+ 
				"bepc.pay_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		return queryBySqlReturnMapList(sql);
	}
	public String getBgAutoTransferAuth(){
		//查找买入方 债转签约流水号
		String sql = "select serino from bg_auto_transfer_auth bata,bg_cust_info bci where bata.cust_info_id=bci.id and bci.bank_account ='"+StaticData.risk+"'";
		String contOrderId = queryBySqlReturnMapList(sql).get(0).get("serino").toString();
		return contOrderId;
	}
	/**
	 * 功能说明： 获得直投BOC当日待还明细
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
	public List<Map> getCurrentImmeControlsBocRedAccount(){
		String sql = 
				"SELECT  "+
						"'1' Imme,  "+ //直投`
						"bepc.*,  "+
						"beo.invest_auz_code,  "+
						"beo.investment_model,  "+
						"beo.invest_seri_num,  "+
						"beo.ef_order_id lineEfOrderId,"+
						"beo.crm_order_id,"+
						"beo.order_number efOrderNumber,"+
						"bci.bank_account, "+
						"bci.fy_account, "+
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
						"beo.investment_model ='1'  and "+ 
						"beo.clearing_channel ='2'  and "+ 
						"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
						"beo.pay_status in (1) AND "+
						"bepc.pay_status in (0) AND "+
						"bepc.live_status in (1) AND "+
						"bepc.cust_info_id = '"+StaticData.redCustInfoId+"' AND "+
						"beo.ef_type not in (5) AND "+
						"(bepc.operate_type is null or bepc.operate_type = '0' )  and "+ 
						"bepc.pay_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		return queryBySqlReturnMapList(sql);
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
		String sql = 
				"SELECT  "+
						"bepc.*,  "+
						"beo.invest_auz_code,  "+
						"beo.ef_order_id lineEfOrderId,"+
						"beo.crm_order_id,"+
						"beo.order_number efOrderNumber,"+
						"bci.bank_account, "+
						"bci.cust_name, "+
						"bci.fy_account, "+
						"bci.investor_type, "+
						"co.id crmOrderId, "+
						"co.order_prd_number, "+
						"co.order_number, "+
						"beo.investment_model, "+
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
						"co.clearing_channel ='1'  and "+ 
						"co.online_type!='0'  and "+ 
						"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
						"beo.pay_status in (1) AND "+
						"bepc.pay_status in (0) AND "+
						"bepc.live_status in (1) AND "+
						"beo.ef_type not in (5) AND "+
						"(bepc.operate_type is null or bepc.operate_type='' or bepc.operate_type = '0' ) AND "+ //只找未批量处理或自动还款处理过的
						"bepc.pay_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明： 获得HF当日待还明细
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
	public List<Map> getCurrentControlsHF(){
		String sql = 
				"SELECT  "+
						"bepc.*,  "+
						"beo.invest_auz_code,  "+
						"beo.ef_order_id lineEfOrderId,"+
						"beo.crm_order_id,"+
						"beo.order_number efOrderNumber,"+
						"bci.bank_account, "+
						"bci.cust_name, "+
						"bci.fy_account, "+
						"bci.hf_account, "+
						"bci.investor_type, "+
						"co.id crmOrderId, "+
						"co.order_prd_number, "+
						"co.order_number, "+
						"beo.investment_model, "+
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
						"co.clearing_channel ='"+StaticData.HFBANK_CODE+"'  and "+ 
						"co.online_type!='0'  and "+ 
						"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
						"beo.pay_status in (1) AND "+
						"bepc.pay_status in (0) AND "+
						"bepc.live_status in (1) AND "+
						"beo.ef_type not in (5) AND "+
						"(bepc.operate_type is null or bepc.operate_type='' or bepc.operate_type = '0' ) AND "+ //只找未批量处理或自动还款处理过的
						"bepc.pay_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		return queryBySqlReturnMapList(sql);
	}
	public void executeBatchUpdateOperateType(){
		String sql = 
				"update "+
						"bg_ef_paycontrol bepc, "+
						"bg_ef_orders beo, "+
						"crm_order co, "+ 
						"bg_cust_info bci, "+ 
						"view_ef_union_product_info vepi, "+ 
						"prd_view_crmproduct vcpi "+ 
						"set bepc.operate_type='0' "+  
						"where  "+
						"bepc.ef_order_id = beo.id and  "+
						"beo.crm_order_id = co.id  and "+ 
						"bci.id = bepc.cust_info_id and "+ 
						"co.ef_prd_detail_id=vepi.pdId AND "+ 
						"co.pro_detail_id=vcpi.detailId AND "+ 
						"co.clearing_channel ='1'  and "+ 
						"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
						"beo.pay_status in (1) AND "+
						"bepc.pay_status in (0) AND "+
						"bepc.live_status in (1) AND "+
						"beo.ef_type not in (5) AND "+
						"(bepc.operate_type is null or bepc.operate_type='' or bepc.operate_type = '0' ) AND "+ //只找未批量处理或自动还款处理过的
						"bepc.pay_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		executeSql(sql);
	}
	/**
	 * 功能说明： 获得直投POC当日待还明细
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
	public List<Map> getCurrentImmeControlsPoc(){
		String sql = 
				"SELECT  "+
						"'1' Imme,  "+ //直投
						"bepc.*,  "+
						"beo.invest_auz_code,  "+
						"beo.ef_order_id lineEfOrderId,"+
						"beo.crm_order_id,"+
						"beo.order_number efOrderNumber,"+
						"bci.bank_account, "+
						"bci.cust_name, "+
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
						"beo.investment_model ='1'  and "+ 
						"co.clearing_channel ='1'  and "+ 
						"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
						"beo.pay_status in (1) AND "+
						"bepc.pay_status in (0) AND "+
						"bepc.live_status in (1) AND "+
						"beo.ef_type not in (5) AND "+
						"bepc.pay_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		return queryBySqlReturnMapList(sql);
	}
	public List<Map> getCurrentImmeControlsPocById(String efPaycontrolId){
		String sql = 
				"SELECT  "+
						"'1' Imme,  "+ //直投
						"bepc.*,  "+
						"beo.invest_auz_code,  "+
						"beo.ef_order_id lineEfOrderId,"+
						"beo.crm_order_id,"+
						"beo.order_number efOrderNumber,"+
						"bci.bank_account, "+
						"bci.investor_type, "+
						"co.id crmOrderId, "+
						"co.order_prd_number, "+
						"co.order_number, "+
						"beo.investment_model, "+
						"co.online_type, "+
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
						"bepc.id = '"+efPaycontrolId+"'";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明： 获得定投boc当日待还明细
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
	public List<Map> getCurrentDeteControlsBoc(){
		String sql = 
						"SELECT  "+
							"'0' Imme,  "+
							"bepc.*,  "+
							"beo.ef_order_id lineEfOrderId,  "+
							"beo.invest_auz_code,  "+
							"beo.investment_model,  "+
							"beo.invest_seri_num,  "+
							"beo.crm_order_id,"+
							"beo.principal,"+
							"beo.order_number efOrderNumber,"+
							"bci.bank_account, "+
							"co.clearing_channel, "+
							"co.order_number, "+
							"co.id crmOrderId, "+
							"co.order_prd_number, "+
							"co.online_type, "+
							"bci.fy_account "+
						"from  "+
						    "bg_ef_paycontrol bepc inner JOIN "+
					        "bg_ef_orders beo on  bepc.ef_order_id = beo.id  inner JOIN "+
					        "crm_order co on  beo.crm_order_id = co.id  inner JOIN "+
					        "bg_cust_info bci on  bci.id = bepc.cust_info_id LEFT JOIN  "+
					        "prd_view_crmproduct vcpi on  co.pro_detail_id=vcpi.detailId  "+ 
						"where  "+
							"beo.investment_model ='2'  and "+ 
							"co.clearing_channel ='2'  and "+ 
							"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
							"beo.pay_status in (1,3) AND "+
							"bepc.pay_status in (0) AND "+
							"bepc.live_status in (1) AND "+
							"bepc.cust_info_id != '"+StaticData.redCustInfoId+"' AND "+
							"(bepc.operate_type is null or bepc.operate_type = '0' )  and "+ 
							"bepc.pay_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明： 获得定投boc当日待还明细(红包)
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
	public List<Map> getCurrentDeteControlsBocRedAccount(){
		String sql = 
				"SELECT  "+
						"'0' Imme,  "+
						"bepc.*,  "+
						"beo.ef_order_id lineEfOrderId,  "+
						"beo.invest_auz_code,  "+
						"beo.invest_seri_num,  "+
						"beo.crm_order_id,"+
						"beo.order_number efOrderNumber,"+
						"bci.bank_account, "+
						"co.clearing_channel, "+
						"co.order_number, "+
						"co.id crmOrderId, "+
						"co.order_prd_number, "+
						"co.online_type, "+
						"bci.fy_account, "+
						"vepi.pdInvestRate invest_rate "+
						"from  "+
						"bg_ef_paycontrol bepc LEFT JOIN "+
						"bg_ef_orders beo on  bepc.ef_order_id = beo.id  LEFT JOIN "+
						"crm_order co on  beo.crm_order_id = co.id  LEFT JOIN "+
						"bg_cust_info bci on  bci.id = bepc.cust_info_id LEFT JOIN "+
						"view_ef_union_product_info vepi on  co.ef_prd_detail_id=vepi.pdId LEFT JOIN "+ 
						"prd_view_crmproduct vcpi on  co.ef_prd_detail_id=vcpi.detailId  "+ 
						"where  "+
						"beo.investment_model ='2'  and "+ 
						"co.clearing_channel ='2'  and "+ 
						"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
						"beo.pay_status in (1) AND "+
						"bepc.pay_status in (0) AND "+
						"bepc.live_status in (1) AND "+
						"bepc.cust_info_id = '"+StaticData.redCustInfoId+"' AND "+
						"(bepc.operate_type is null or bepc.operate_type = '0')  and "+ 
						"bepc.pay_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明： 获得线下当日待还明细
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
	public List<Map> getCurrentDeteControlsPoc(){
		String sql = 
				"SELECT  "+
						"'0' Imme,  "+
						"bepc.*,  "+
						"beo.ef_order_id lineEfOrderId,  "+
						"beo.invest_auz_code,  "+
						"beo.crm_order_id,"+
						"beo.order_number efOrderNumber,"+
						"bci.bank_account, "+
						"co.clearing_channel, "+
						"co.order_number, "+
						"co.id crmOrderId, "+
						"co.order_prd_number, "+
						"co.online_type, "+
						"bci.fy_account, "+
						"bci.cust_name, "+
						"vepi.pdInvestRate INVEST_RATE "+
					"from  "+
					    "bg_ef_paycontrol bepc LEFT JOIN "+
				        "bg_ef_orders beo on  bepc.ef_order_id = beo.id  LEFT JOIN "+
				        "crm_order co on  beo.crm_order_id = co.id  LEFT JOIN "+
				        "bg_cust_info bci on  bci.id = bepc.cust_info_id LEFT JOIN "+
				        "view_ef_union_product_info vepi on  co.ef_prd_detail_id=vepi.pdId LEFT JOIN "+ 
				        "prd_view_crmproduct vcpi on  co.ef_prd_detail_id=vcpi.detailId  "+ 
					"where  "+
						"beo.investment_model ='2'  and "+ 
						"co.clearing_channel ='1'  and "+ 
						"(vcpi.cptnumber !='A007' or vcpi.cptnumber is null )  and "+ 
						"beo.pay_status in (1) AND "+
						"bepc.pay_status in (0) AND "+
						"bepc.live_status in (1) AND "+
						"bepc.pay_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' and (co.order_type = 3 or co.order_type is null or co.order_type='') ";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明： 获得明细
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
	public List<Map> getControls(String efOrderId){
		String sql ="select * from bg_ef_paycontrol where pay_status = 0 and live_status=1 and ef_order_id = '"+efOrderId+"' ";
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
	public boolean isLast(Map efPaycontrol){
		boolean flag = false;
		String sql  ="select * from  bg_ef_paycontrol where ef_order_id='"+efPaycontrol.get("ef_order_id").toString()+"' and pay_time >'"+efPaycontrol.get("pay_time").toString()+"' and live_status=1 and pay_status = 0 ";
		List<Map> list = queryBySqlReturnMapList(sql);
		if(ListTool.isNullOrEmpty(list)){
			return true;
		}
		return flag;
	}
	/**
	 * 功能说明：获得邀请人的信息
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
	public Map getRefereeInfoMap(String cust_info_id){
		String sql = "SELECT * from bg_cust_info where id='"+cust_info_id+"'";
		return queryBySqlReturnMapList(sql).get(0);
	}
	/**
	 * 功能说明：获得当天的体验标
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
	public List<Map> getCurrentOnLineTasteOrderControls(){
		String sql = 
				"SELECT  "+
				"bepc.*,  "+
				"beo.invest_auz_code,  "+
				"beo.order_number efOrderNumber,  "+
				"bci.bank_account "+
				"from  "+
				"bg_ef_paycontrol bepc, "+
				"bg_ef_orders beo, "+
				"bg_cust_info bci "+ 
				"where  "+
				"bepc.ef_order_id = beo.id and  "+
				"bci.id = bepc.cust_info_id and "+ 
				"beo.ef_type=5  and "+ 
				"beo.pay_status in (1) AND "+
				"bepc.pay_status in (0) AND "+
				"bepc.live_status in (1) AND "+
				"bepc.pay_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%'";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：开始记录利息
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
	public JSONObject cleanInterest(Map efPaycontrol){
		JSONObject resultJson = new JSONObject();
		resultJson.put("refMoney",0d);
		try {
				BgEfPaycontrol bec = (BgEfPaycontrol) findById(BgEfPaycontrol.class,efPaycontrol.get("id").toString());
				//只有在利息大于零的情况下才进行记录,否则不进行记录
				//开始添加记录
				BgEfPayrecord epr = new BgEfPayrecord();
				epr.setCreateTime(DateUtil.getCurrentTime());										//创建时间
				epr.setCustId(efPaycontrol.get("cust_id").toString()); 								//bg_customer的id
				epr.setCustInfoId(efPaycontrol.get("cust_info_id").toString());						//bg_cust_info的id
				epr.setEfOrderId(efPaycontrol.get("ef_order_id").toString());						//理财订单id
				epr.setEfPaycontrolId(efPaycontrol.get("id").toString());						    //理财明细
				epr.setEvidenceUrl("");																//凭证url
				if("1".equals(efPaycontrol.get("Imme").toString())){
					epr.setInteRest(NumberUtil.parseDouble(efPaycontrol.get("interest")));				//利息
					epr.setManagementAmt(NumberUtil.parseDouble(efPaycontrol.get("management_amt")));	//管理费
					epr.setType(1);																		//类型 1线上 0 线下
				}
				if("0".equals(efPaycontrol.get("Imme").toString())){
					epr.setInteRest(0d);				//利息
					epr.setManagementAmt(0d);	//管理费
					epr.setType(0);																		//类型 1线上 0 线下
				}
				epr.setOperator("") ;																//操作人
				epr.setOverPenalty(0d);																//逾期金额	
				epr.setPeriods(NumberUtil.parseInteger(efPaycontrol.get("periods")));				//期数
				epr.setPrePaymentPenalty(0d);														//提前结清金额
				epr.setPrincipal(0d);																//本金(因为本次只是对利息进行记录所以本金为0)
				if (efPaycontrol.containsKey("isNewPoc") && "1".equals(efPaycontrol.get("isNewPoc"))) {
					epr.setPrincipal(NumberUtil.parseDouble(efPaycontrol.get("surplus_principal")));
				}
				epr.setUpdateTime(DateUtil.getCurrentTime());										//更新时间
				epr.setCouponAmount(bec.getCouponInterest()==null?0d:bec.getCouponInterest());
				epr.setTotalAmt(NumberFormat.format(ArithUtil.add(new Double[]{
						epr.getPrincipal(),
						epr.getCouponAmount(),
						epr.getInteRest(),
						epr.getPrePaymentPenalty(),
						-epr.getManagementAmt()
				})));				//投资总金额
				if(bec.getSurplusInterest()>0 || bec.getCouponInterest()>0){
					add(epr);
				}
				if((ChkUtil.isNotEmpty(bec.getSurplusInterest()) && bec.getSurplusInterest()>0) || (ChkUtil.isNotEmpty(bec.getCouponInterest()) && bec.getCouponInterest()>0)){
					if("1".equals(efPaycontrol.get("Imme").toString())){
						try{
						//开发准备进行修改推荐记录表
						if(!ChkUtil.isEmpty(bec.getRefereeInfoId())){  //如果推荐人的不是空的话
							BgReferralIncomeRecord bri  = new BgReferralIncomeRecord();
							bri.setCreateTime(DateUtil.getCurrentTime());
							bri.setCustInfoId(bec.getCustInfoId());
							bri.setIncomeSource("投资还款");
							bri.setRefereeInfoId(bec.getRefereeInfoId());
							bri.setReferralIncome(ArithUtil.mul(
									ArithUtil.add(bec.getSurplusInterest(),bec.getCouponInterest()),
									bec.getRefereeIncomeScale()/100d
									));
							bri.setUpdateTime(DateUtil.getCurrentTime());
							//如果金额>0才进行存储
							if(NumberFormat.format(bri.getReferralIncome())>0){
								resultJson.put("refMoney",bri.getReferralIncome());
								add(bri);
							}
						}
						//客户积分获取记录以及该用户的积分更新问题
						BgScoreRecord bsr = new  BgScoreRecord();
						bsr.setEfOrderId(bec.getEfOrderId());
						bsr.setCreateTime(DateUtil.getCurrentTime());
						bsr.setCustId(bec.getCustId());
						bsr.setIncomeSource("投资还款");
						bsr.setScore("0");
						if(ChkUtil.isNotEmpty(bec.getScoreScale())){
							bsr.setScore(String.valueOf(Math.round(ArithUtil.mul(
										ArithUtil.add(bec.getSurplusInterest(),bec.getCouponInterest()),
										bec.getScoreScale()/100d
										))));
						}
						bsr.setUpdateTime(DateUtil.getCurrentTime());
						//如果积分>0才进行存储
						//-------------------------------------------------------------------
						//修改积分
						BgCustomer bc  = (BgCustomer) findById(BgCustomer.class, bec.getCustId());
						String oldScore="0";
						if(ChkUtil.isNotEmpty(bc.getScore())){
							oldScore = bc.getScore();
						}
						String currentScore  = String.valueOf(Long.parseLong(oldScore)+Long.parseLong(bsr.getScore()));
						bc.setScore(currentScore);
						update(bc);
						//-------------------------------------------------------------------
						if(NumberUtil.parseDouble(bsr.getScore())>0){
							bsr.setRealTimeScore(bc.getScore());
							add(bsr);
						}
						
						}catch (Exception e) {
							e.printStackTrace();
							logger.warn("修改邀请人利息和用户积分时失败!明细id:"+efPaycontrol.get("id"));
						}
					}
					bec.setCouponInterest(0d);
					bec.setSurplusInterest(0d);
					//
					if(efPaycontrol.containsKey("POC")){//当标记是poc的时候代表这个明细
						bec.setPayStatus(1); 				//结清
						//订单线上类型 1 线上(贝格平台) 0 原线下老单子 2：线上（中资联财富)
						String online_type = efPaycontrol.get("online_type").toString();
						if("1".equals(efPaycontrol.get("Imme").toString())){
							bec.setSurplusManagementAmt(0d); 	//管理费
							bec.setCouponInterest(0d); 			//抵用卷利息
							bec.setSurplusInterest(0d); 		//利息
							//开始进行站内信通知
							try{
								BgCustomer bgCustomer  = (BgCustomer) findById(BgCustomer.class,bec.getCustId());
								BgSysMessageContent bsmc = new BgSysMessageContent();
								bsmc.setCreateTime(DateUtil.getCurrentTime());
								String Princiapl = NumberFormat.formatDouble(bec.getPrinciapl());
								String Interest = NumberFormat.formatDouble(bec.getInterest());
								String ManagementAmt = NumberFormat.formatDouble(bec.getManagementAmt());
								String totalMoney="0";
								if(bec.getUseCouponInterest()!=null){
									totalMoney = NumberFormat.formatDouble(bec.getPrinciapl()+bec.getInterest()+bec.getUseCouponInterest()-bec.getManagementAmt());
								}else{
									totalMoney = NumberFormat.formatDouble(bec.getPrinciapl()+bec.getInterest()-bec.getManagementAmt());
								}
								String couponInterest = "0";
								if(bec.getUseCouponInterest()!=null && bec.getUseCouponInterest()>0){
									couponInterest = NumberFormat.formatDouble(bec.getUseCouponInterest());
								}
								JSONObject paramJson = new JSONObject();
								paramJson.put("msgType","1");
								String isLast  = "SELECT * from bg_ef_paycontrol where ef_order_id='"+bec.getEfOrderId()+"' and live_status=1 and pay_time>'"+bec.getPayTime()+"' and pay_status=0 ";
								List list  = queryBySqlReturnMapList(isLast);
								if(ListTool.isNullOrEmpty(list)){
									paramJson.put("msgType","2");
								}
								//
								paramJson.put("name", bgCustomer.getUsername());
								BgEfOrders  eo  = (BgEfOrders) findById(BgEfOrders.class, bec.getEfOrderId());
								paramJson.put("number", eo.getOrderNumber());
								paramJson.put("Periods",bec.getPeriods());
								paramJson.put("Princiapl", Princiapl);
								paramJson.put("Interest", Interest);
								paramJson.put("ManagementAmt", ManagementAmt);
								paramJson.put("totalMoney", totalMoney);
								
								paramJson.put("couponInterest", couponInterest);
								JSONObject msgJson  = MessageTemplete.getMsg(paramJson);
								//判断是否需要发邮件
								String emailSql = "select * from bg_sys_meg_receive_set where cust_id='"+bgCustomer.getId()+"' and sync_email='1'";
								List emailList = queryBySqlReturnMapList(emailSql);
								if(ListTool.isNotNullOrEmpty(emailList) && "1".equals(online_type)){ //不是空的话则需要发送邮件
									try {
										Mail.sentMail(msgJson.getString("title"), msgJson.getString("content"), bgCustomer.getCustEmail());
										logger.warn("发送邮件成功!明细id:"+bec.getId());
									} catch (Exception e) {
										logger.warn(e.getMessage(),e);
										logger.warn("发送邮件失败!明细id:"+bec.getId());
									}
								}
								bsmc.setMsgContent(msgJson.getString("content"));
								bsmc.setMsgTitle(msgJson.getString("title"));
								bsmc.setPublishId("");
								bsmc.setUpdateTime(DateUtil.getCurrentTime());
								if ("1".equals(online_type)) {
									bsmc.setPlatformType(1);
								}else if ("2".equals(online_type)) {
									bsmc.setPlatformType(2);
								}
								add(bsmc);
								BgSysMessage bsm  = new BgSysMessage();
								bsm.setCustId(bec.getCustId());
								bsm.setEfOrderId(bec.getEfOrderId());
								bsm.setEnabled(1);
								bsm.setMsgClass(2);
								bsm.setMsgContentId(bsmc.getId());
								bsm.setMsgType(5);
								bsm.setOrderId(efPaycontrol.get("crmOrderId").toString());
								bsm.setReadStatus(0);
								if ("1".equals(online_type)) {
									bsm.setPlatformType(1);
								}else if ("2".equals(online_type)) {
									bsm.setPlatformType(2);
								}
								add(bsm);
								logger.warn("站内信生成成功!明细id:"+bec.getId());
							}catch (Exception e) {
								logger.warn(e.getMessage(),e);
								logger.warn("站内信生成失败!明细id:"+bec.getId());
							}
						}
						bec.setSurplusPrincipal(0d);		//本金
						
					}
					update(bec);
					//判断是否结束
					String overSql ="select * from bg_ef_paycontrol where pay_status = 0 and live_status=1 and ef_order_id = '"+efPaycontrol.get("ef_order_id").toString()+"' ";
					List overlist = queryBySqlReturnMapList(overSql);
					if(ListTool.isNullOrEmpty(overlist)){
							BgEfOrders efOrder = (BgEfOrders) findById(BgEfOrders.class, efPaycontrol.get("ef_order_id").toString());
							efOrder.setClearTime(DateUtil.getCurrentTime());
							efOrder.setUpdateTime(DateUtil.getCurrentTime());
							efOrder.setClearType(2);
							efOrder.setPayStatus(2);
							update(efOrder);
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("利息还款成功,但在进行数据库修改时失败!明细id:"+efPaycontrol.get("id"));
		}
		return resultJson;
	}
	/**
	 * 功能说明：开始记录利息
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
	public JSONObject cleanEfPaycontrol(Map efPaycontrol){
		JSONObject resultJson = new JSONObject();
		resultJson.put("refMoney",0d);
		try {
				BgEfPaycontrol bec = (BgEfPaycontrol) findById(BgEfPaycontrol.class,efPaycontrol.get("id").toString());
				String sql = "SELECT id from bg_ef_payrecord where ef_paycontrol_id='"+bec.getId()+"' ";
				List<Map> beprList = queryBySqlReturnMapList(sql);
				BgEfPayrecord epr  = null;
				if(beprList!=null && beprList.size()>0){
					epr = (BgEfPayrecord) findById(BgEfPayrecord.class,beprList.get(0).get("id").toString());
				}else{
					epr = new BgEfPayrecord();
					epr.setOperator("") ;																//操作人
					epr.setCreateTime(DateUtil.getCurrentTime());										//创建时间
				}
				//只有在利息大于零的情况下才进行记录,否则不进行记录
				//开始添加记录
				epr.setCustId(efPaycontrol.get("cust_id").toString()); 								//bg_customer的id
				epr.setCustInfoId(efPaycontrol.get("cust_info_id").toString());						//bg_cust_info的id
				epr.setEfOrderId(efPaycontrol.get("ef_order_id").toString());						//理财订单id
				epr.setEfPaycontrolId(efPaycontrol.get("id").toString());						    //理财明细
				epr.setEvidenceUrl("");																//凭证url
				if("1".equals(efPaycontrol.get("Imme").toString())){
					epr.setInteRest(NumberUtil.parseDouble(efPaycontrol.get("interest")));				//利息
					epr.setManagementAmt(NumberUtil.parseDouble(efPaycontrol.get("management_amt")));	//管理费
					epr.setType(1);																		//类型 1线上 0 线下
				}
				if("0".equals(efPaycontrol.get("Imme").toString())){
					epr.setInteRest(0d);				//利息
					epr.setManagementAmt(0d);	//管理费
					epr.setType(0);																		//类型 1线上 0 线下
				}
				epr.setOverPenalty(0d);																//逾期金额	
				epr.setPeriods(NumberUtil.parseInteger(efPaycontrol.get("periods")));				//期数
				epr.setPrePaymentPenalty(0d);														//提前结清金额
				epr.setPrincipal(0d);																//本金(因为本次只是对利息进行记录所以本金为0)
				if (efPaycontrol.containsKey("isNewPoc") && "1".equals(efPaycontrol.get("isNewPoc"))) {
					epr.setPrincipal(NumberUtil.parseDouble(efPaycontrol.get("surplus_principal")));
				}
				epr.setUpdateTime(DateUtil.getCurrentTime());										//更新时间
				epr.setCouponAmount(bec.getCouponInterest());
				epr.setTotalAmt(ArithUtil.add(new Double[]{epr.getPrincipal(),epr.getInteRest(),-epr.getManagementAmt(),epr.getCouponAmount()}));				//投资总金额
				if(epr.getId() !=null){
					update(epr);
				}else{
					add(epr);
				}
				if((ChkUtil.isNotEmpty(bec.getSurplusInterest()) && bec.getSurplusInterest()>0) || (ChkUtil.isNotEmpty(bec.getCouponInterest()) && bec.getCouponInterest()>0)){
					if("1".equals(efPaycontrol.get("Imme").toString())){
						try{
						//开发准备进行修改推荐记录表
						if(!ChkUtil.isEmpty(bec.getRefereeInfoId())){  //如果推荐人的不是空的话
							BgReferralIncomeRecord bri  = new BgReferralIncomeRecord();
							bri.setCreateTime(DateUtil.getCurrentTime());
							bri.setCustInfoId(bec.getCustInfoId());
							bri.setIncomeSource("投资还款");
							bri.setRefereeInfoId(bec.getRefereeInfoId());
							bri.setReferralIncome(ArithUtil.mul(
									ArithUtil.add(bec.getSurplusInterest(),bec.getCouponInterest()),
									bec.getRefereeIncomeScale()/100d
									));
							bri.setUpdateTime(DateUtil.getCurrentTime());
							//如果金额>0才进行存储
							if(NumberFormat.format(bri.getReferralIncome())>0){
								resultJson.put("refMoney",bri.getReferralIncome());
								add(bri);
							}
						}
						//客户积分获取记录以及该用户的积分更新问题
						BgScoreRecord bsr = new  BgScoreRecord();
						bsr.setEfOrderId(bec.getEfOrderId());
						bsr.setCreateTime(DateUtil.getCurrentTime());
						bsr.setCustId(bec.getCustId());
						bsr.setIncomeSource("投资还款");
						bsr.setScore("0");
						if(ChkUtil.isNotEmpty(bec.getScoreScale())){
							bsr.setScore(String.valueOf(Math.round(ArithUtil.mul(
										ArithUtil.add(bec.getSurplusInterest(),bec.getCouponInterest()),
										bec.getScoreScale()/100d
										))));
						}
						bsr.setUpdateTime(DateUtil.getCurrentTime());
						//如果积分>0才进行存储
						//-------------------------------------------------------------------
						//修改积分
						BgCustomer bc  = (BgCustomer) findById(BgCustomer.class, bec.getCustId());
						String oldScore="0";
						if(ChkUtil.isNotEmpty(bc.getScore())){
							oldScore = bc.getScore();
						}
						String currentScore  = String.valueOf(Long.parseLong(oldScore)+Long.parseLong(bsr.getScore()));
						bc.setScore(currentScore);
						update(bc);
						//-------------------------------------------------------------------
						if(NumberUtil.parseDouble(bsr.getScore())>0){
							bsr.setRealTimeScore(bc.getScore());
							add(bsr);
						}
						
						}catch (Exception e) {
							e.printStackTrace();
							logger.warn("修改邀请人利息和用户积分时失败!明细id:"+efPaycontrol.get("id"));
						}
					}
				}
			if (efPaycontrol.containsKey("POC")) {// 当标记是poc的时候代表这个明细
				bec.setPayStatus(1); // 结清
				//订单线上类型 1 线上(贝格平台) 0 原线下老单子 2：线上（中资联财富)
				String online_type = efPaycontrol.get("online_type").toString();
				if ("1".equals(efPaycontrol.get("Imme").toString())) {
					bec.setSurplusManagementAmt(0d); // 管理费
					bec.setCouponInterest(0d); // 抵用卷利息
					bec.setSurplusInterest(0d); // 利息
					// 开始进行站内信通知
					try {
						BgCustomer bgCustomer = (BgCustomer) findById(BgCustomer.class, bec.getCustId());
						BgSysMessageContent bsmc = new BgSysMessageContent();
						bsmc.setCreateTime(DateUtil.getCurrentTime());
						String Princiapl = NumberFormat.formatDouble(bec.getPrinciapl());
						String Interest = NumberFormat.formatDouble(bec.getInterest());
						String ManagementAmt = NumberFormat.formatDouble(bec.getManagementAmt());
						String totalMoney = "0";
						if (bec.getUseCouponInterest() != null) {
							totalMoney = NumberFormat.formatDouble(bec.getPrinciapl() + bec.getInterest() + bec.getUseCouponInterest() - bec.getManagementAmt());
						} else {
							totalMoney = NumberFormat.formatDouble(bec.getPrinciapl() + bec.getInterest() - bec.getManagementAmt());
						}
						String couponInterest = "0";
						if (bec.getUseCouponInterest() != null && bec.getUseCouponInterest() > 0) {
							couponInterest = NumberFormat.formatDouble(bec.getUseCouponInterest());
						}
						JSONObject paramJson = new JSONObject();
						paramJson.put("msgType", "1");
						String isLast = "SELECT * from bg_ef_paycontrol where ef_order_id='" + bec.getEfOrderId() + "' and live_status=1 and pay_time>'" + bec.getPayTime() + "' and pay_status=0 ";
						List list = queryBySqlReturnMapList(isLast);
						if (ListTool.isNullOrEmpty(list)) {
							paramJson.put("msgType", "2");
						}
						//
						paramJson.put("name", bgCustomer.getUsername());
						BgEfOrders eo = (BgEfOrders) findById(BgEfOrders.class, bec.getEfOrderId());
						paramJson.put("number", eo.getOrderNumber());
						paramJson.put("Periods", bec.getPeriods());
						paramJson.put("Princiapl", Princiapl);
						paramJson.put("Interest", Interest);
						paramJson.put("ManagementAmt", ManagementAmt);
						paramJson.put("totalMoney", totalMoney);

						paramJson.put("couponInterest", couponInterest);
						JSONObject msgJson = MessageTemplete.getMsg(paramJson);
						// 判断是否需要发邮件
						String emailSql = "select * from bg_sys_meg_receive_set where cust_id='" + bgCustomer.getId() + "' and sync_email='1'";
						List emailList = queryBySqlReturnMapList(emailSql);
						if (ListTool.isNotNullOrEmpty(emailList) && "1".equals(online_type)) { // 不是空的话则需要发送邮件
							try {
								Mail.sentMail(msgJson.getString("title"), msgJson.getString("content"), bgCustomer.getCustEmail());
								logger.warn("发送邮件成功!明细id:" + bec.getId());
							} catch (Exception e) {
								logger.warn(e.getMessage(), e);
								logger.warn("发送邮件失败!明细id:" + bec.getId());
							}
						}
						bsmc.setMsgContent(msgJson.getString("content"));
						bsmc.setMsgTitle(msgJson.getString("title"));
						bsmc.setPublishId("");
						bsmc.setUpdateTime(DateUtil.getCurrentTime());
						if ("1".equals(online_type)) {
							bsmc.setPlatformType(1);
						}else if ("2".equals(online_type)) {
							bsmc.setPlatformType(2);
						}
						add(bsmc);
						BgSysMessage bsm = new BgSysMessage();
						bsm.setCustId(bec.getCustId());
						bsm.setEfOrderId(bec.getEfOrderId());
						bsm.setEnabled(1);
						bsm.setMsgClass(2);
						bsm.setMsgContentId(bsmc.getId());
						bsm.setMsgType(5);
						bsm.setOrderId(efPaycontrol.get("crmOrderId").toString());
						bsm.setReadStatus(0);
						if ("1".equals(online_type)) {
							bsm.setPlatformType(1);
						}else if ("2".equals(online_type)) {
							bsm.setPlatformType(2);
						}
						add(bsm);
						logger.warn("站内信生成成功!明细id:" + bec.getId());
					} catch (Exception e) {
						logger.warn(e.getMessage(), e);
						logger.warn("站内信生成失败!明细id:" + bec.getId());
					}
				}
				bec.setSurplusPrincipal(0d); // 本金
			}
				
			update(bec);
			// 判断是否结束
			String overSql = "select * from bg_ef_paycontrol where pay_status = 0 and live_status=1 and ef_order_id = '" + efPaycontrol.get("ef_order_id").toString() + "' ";
			List overlist = queryBySqlReturnMapList(overSql);
			if (ListTool.isNullOrEmpty(overlist)) {
				BgEfOrders efOrder = (BgEfOrders) findById(BgEfOrders.class, efPaycontrol.get("ef_order_id").toString());
				efOrder.setClearTime(DateUtil.getCurrentTime());
				efOrder.setUpdateTime(DateUtil.getCurrentTime());
				efOrder.setClearType(2);
				efOrder.setPayStatus(2);
				update(efOrder);
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("利息还款成功,但在进行数据库修改时失败!明细id:"+efPaycontrol.get("id"));
		}
		return resultJson;
	}
	/**
	 * 功能说明：通过id查找对应的理财明细数据
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
	public Map getCurrentEfBocControls(String epcId){
		String sql = 
				"SELECT  "+
						"bepc.*,  "+
						"beo.invest_auz_code,  "+
						"beo.invest_seri_num,  "+
						"beo.ef_order_id lineEfOrderId,"+
						"beo.crm_order_id,"+
						"beo.order_number efOrderNumber,"+
						"bci.bank_account, "+
						"bci.investor_type, "+
						"co.id crmOrderId, "+
						"co.order_prd_number, "+
						"co.order_number, "+
						"beo.investment_model, "+
						"co.online_type "+
						"from  "+
						"bg_ef_paycontrol bepc, "+
						"bg_ef_orders beo, "+
						"crm_order co, "+ 
						"bg_cust_info bci "+ 
						"where  "+
						"bepc.ef_order_id = beo.id and  "+
						"beo.crm_order_id = co.id  and "+ 
						"bci.id = bepc.cust_info_id and "+ 
						"beo.pay_status in (1,3) AND "+
						"bepc.pay_status in (0) AND "+
						"bepc.live_status in (1) AND "+
						"beo.ef_type not in (5) AND "+
						"(co.order_type = 3 or co.order_type is null or co.order_type='') and bepc.id='"+epcId+"' ";
		if(queryBySqlReturnMapList(sql).size()>0){
			return queryBySqlReturnMapList(sql).get(0);
		}else{
			return null;
		}
	}
}
