package com.apt.webapp.dao.impl.v2;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.apt.util.NumberFormat;
import com.apt.util.NumberUtil;
import com.apt.util.arith.ArithUtil;
import com.apt.util.date.DateUtil;
import com.apt.webapp.base.dao.BaseHibernateDaoSupper;
import com.apt.webapp.dao.v2.V2AutoRepaymentDao;
import com.apt.webapp.model.crm.CrmPaycontrol;

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
@Repository("v2AutoRepaymentDao")
public class V2AutoRepaymentDaoImpl extends BaseHibernateDaoSupper implements V2AutoRepaymentDao {
	private Logger logger = LoggerFactory.getLogger(V2AutoRepaymentDaoImpl.class);
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
	public void autoOverdue(Map crmPaycontrol) throws Exception {
		try {
			CrmPaycontrol cpc =(CrmPaycontrol) findById(CrmPaycontrol.class, crmPaycontrol.get("id").toString());
			int moreDay  = DateUtil.getQuot(DateUtil.getCurrentTime(), cpc.getRepaymentTime());
			//准备获取各种利率问题
			String sql ="SELECT  "+
						"pd.OVER_PENALSUM as overPenalsum, "+
						"pd.OVER_RATE as overRate "+
						"from  "+
						"crm_order co, "+
						"PRO_CRM_PRODUCT_DETAIL pd "+
						"where co.pro_detail_id = pd.id and co.id ='"+cpc.getCrmOrderId()+"'" ;
			Map rateMap = queryBySqlReturnMapList(sql).get(0);
			String totalRemainCapitalSql = "SELECT sum(should_capiital) totalRemainCapital from crm_paycontrol where crm_order_id='"+cpc.getCrmOrderId()+"'";
			double totalRemainCapital = NumberUtil.parseDouble(queryBySqlReturnMapList(totalRemainCapitalSql).get(0).get("totalRemainCapital"));
			double overcost =NumberFormat.format(ArithUtil.mul(totalRemainCapital,NumberUtil.parseDouble(rateMap.get("overRate"))/100d ));
			double overDue =NumberFormat.format(ArithUtil.mul(totalRemainCapital, NumberUtil.parseDouble(rateMap.get("overPenalsum"))/100d));
			cpc.setShouldInterest(ArithUtil.add(overcost,cpc.getShouldInterest()));
			cpc.setRemainInterest(ArithUtil.add(overcost,cpc.getRemainInterest()));
			//开始计算违约金
			if(!"2".equals(cpc.getStatus().toString())&&cpc.getShouldViolateFee()==0){
				cpc.setShouldViolateFee(overDue);
				cpc.setOverdueViolateMoney(overDue);
			}
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
			logger.warn("在执行自动逾期是出现异常,明细id:"+ crmPaycontrol.get("id").toString());
		}
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
	public List<Map> getV2NotClearPaycontrols(){
		String sql  = "SELECT cpc.* "+
					  "From " +
					  "crm_paycontrol cpc,crm_order co where co.id = cpc.crm_order_id and co.order_type = 2 and cpc.status in(0,2) and cpc.Exempt_status = 0 and cpc.repayment_time < '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"' ";
		return queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：查询正常扣款数据（应缴纳的费用，缴纳需要的客户信息等）
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
	public List<Map> findDueData() throws Exception {
		StringBuffer sb=new StringBuffer();
		sb.append("select ");
		sb.append("a.id as payid,");
		sb.append("a.should_interest,");
		sb.append("a.remain_interest,");
		sb.append("a.should_violate_fee,");
		sb.append("a.overdue_violate_money,");
		sb.append("a.remain_capital,");
		sb.append("a.remain_accrual,");
		sb.append("a.remain_manage_fee,");
		sb.append("o.cust_info_id,");
		sb.append("o.online_type,");
		sb.append("a.crm_order_id");
		sb.append(" from crm_paycontrol a ");
		sb.append("LEFT JOIN crm_order o ON a.crm_order_id = o.id ");
		sb.append("LEFT JOIN pro_crm_product_detail p ON o.pro_detail_id = p.id ");
		sb.append("WHERE ");
		sb.append("o.order_type = 2 ");
		sb.append("AND a.`status` = 0  ");
		sb.append("AND a.exempt_status = 0 and (a.abnormal_status is null or (a.abnormal_status is not null and a.abnormal_status<>0)) ");
		sb.append("and a.repayment_time like '"+DateUtil.getCurrentTime(DateUtil.STYLE_2)+"%' ");
		return queryBySqlReturnMapList(sb.toString());
	}
	/**
	 * 功能说明：查询逾期扣款数据 可能牵扯多个逾期 一起扣款 但是还要保证 如果余额不足 优先还前面的逾期
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
	public List<Map> findOverdueData() throws Exception {
		StringBuffer sb=new StringBuffer();
		sb.append("select ");
		sb.append("a.id as payid,");
		sb.append("sum(ifnull(a.remain_interest,0)+ifnull(a.overdue_violate_money,0)+ifnull(a.remain_capital,0)+ifnull(a.remain_accrual,0)+ifnull(a.remain_manage_fee,0)) as summoney,");
		sb.append("o.cust_info_id,");
		sb.append("o.online_type,");
		sb.append("a.crm_order_id,");
		sb.append("count(*) as count,");
		sb.append("GROUP_CONCAT(a.id) as allid,");
		sb.append("GROUP_CONCAT(ifnull(a.remain_interest,0)+ifnull(a.overdue_violate_money,0)+ifnull(a.remain_capital,0)+ifnull(a.remain_accrual,0)+ifnull(a.remain_manage_fee,0)) as allmoney");
		sb.append(" from (select * from crm_paycontrol order by repayment_duetime) a ");
		sb.append("LEFT JOIN crm_order o ON a.crm_order_id = o.id ");
		sb.append("LEFT JOIN pro_crm_product_detail p ON o.pro_detail_id = p.id ");
		sb.append("WHERE ");
		sb.append("o.order_type = 2 ");
		sb.append("AND a.`status` = 2  ");
		sb.append("AND a.exempt_status = 0 and (a.abnormal_status is null or (a.abnormal_status is not null and a.abnormal_status<>0)) group by a.crm_order_id ");
		return queryBySqlReturnMapList(sb.toString());
	}

}
