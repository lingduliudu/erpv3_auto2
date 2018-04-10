package com.apt.webapp.model.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 功能说明：理财还款记录表
 * 创建人：乔春峰  
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-10
 */
@Entity
@Table(name = "EF_PAYRECORD")
public class EfPayrecord {
	
	@Id
	@GenericGenerator(name="systemUUID",strategy="uuid")
	@GeneratedValue(generator="systemUUID")
	@Column(name = "ID", unique = true, nullable = false,length=40)
	private String id;						//ID 属于 理财还款记录	
	
	@Column(name = "EF_PAYCONTROL_ID",length=40)
	private String efPaycontrolId;			//对应还款明细ID EF_PAYCONTTROL_ID
	
	@Column(name = "EF_ORDER_ID",length=40)
	private String efOrderId;				//理财订单ID EF_ORDER_ID
	
	@Column(name = "CUST_ID",length=40)
	private String custId;					//客户ID 关联 BG_CUSTOMER
	
	@Column(name = "CUST_INFO_ID",length=40)
	private String custInfoId;				//客户信息ID 
	
	@Column(name = "PRINCIPAL",length=12,scale=4)
	private Double principal;					//还款本金
	
	@Column(name = "INTEREST",length=12,scale=4)
	private Double inteRest;					//还款利息
	
	@Column(name = "PERIODS",length=2,scale=0)
	private Integer periods;					//期数
	
	@Column(name = "TOTAL_AMT",length=12,scale=4)
	private Double totalAmt;					//总投资金额
	
	@Column(name = "PRE_PAYMENT_PENALTY",length=12,scale=2)
	private Double prePaymentPenalty;			//提前还款违约金
	
	@Column(name = "OVER_PENALTY",length=12,scale=2)
	private Double overPenalty;					//逾期违约金
	
	@Column(name = "MANAGEMENT_AMT",length=12,scale=2)
	private Double managementAmt;				//管理费
	
	@Column(name = "rate_coupon",length=12,scale=2)
	private Double rateCoupon;				//加息卷利息
	
	
	@Column(name = "OPERATOR",length=40)
	private String operator;					//	操作人	 关联SYS_EMPLOYEE ID 线下财务操作
	
	@Column(name = "EVIDENCE_URL",length=40)
	private String evidenceUrl;					//还款凭证路径
		
	@Column(name = "CREATE_TIME",length=40)
	private String createTime;				//创建时间|还款时间
	
	@Column(name = "UPDATE_TIME",length=40)
	private String updateTime;				//更新时间

	public String getId() {
		return id;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getEvidenceUrl() {
		return evidenceUrl;
	}

	public void setEvidenceUrl(String evidenceUrl) {
		this.evidenceUrl = evidenceUrl;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEfPaycontrolId() {
		return efPaycontrolId;
	}

	public void setEfPaycontrolId(String efPaycontrolId) {
		this.efPaycontrolId = efPaycontrolId;
	}

	public String getEfOrderId() {
		return efOrderId;
	}

	public void setEfOrderId(String efOrderId) {
		this.efOrderId = efOrderId;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getCustInfoId() {
		return custInfoId;
	}

	public void setCustInfoId(String custInfoId) {
		this.custInfoId = custInfoId;
	}

	public Double getPrincipal() {
		return principal;
	}

	public void setPrincipal(Double principal) {
		this.principal = principal;
	}

	public Double getInteRest() {
		return inteRest;
	}

	public void setInteRest(Double inteRest) {
		this.inteRest = inteRest;
	}

	public Integer getPeriods() {
		return periods;
	}

	public void setPeriods(Integer periods) {
		this.periods = periods;
	}

	public Double getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(Double totalAmt) {
		this.totalAmt = totalAmt;
	}

	public Double getPrePaymentPenalty() {
		return prePaymentPenalty;
	}

	public void setPrePaymentPenalty(Double prePaymentPenalty) {
		this.prePaymentPenalty = prePaymentPenalty;
	}

	public Double getOverPenalty() {
		return overPenalty;
	}

	public void setOverPenalty(Double overPenalty) {
		this.overPenalty = overPenalty;
	}

	public Double getManagementAmt() {
		return managementAmt;
	}

	public void setManagementAmt(Double managementAmt) {
		this.managementAmt = managementAmt;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}


	public Double getRateCoupon() {
		return rateCoupon;
	}

	public void setRateCoupon(Double rateCoupon) {
		this.rateCoupon = rateCoupon;
	}

	public EfPayrecord() {
		super();
	}
}
