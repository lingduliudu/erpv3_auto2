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
@Table(name = "zzl_ef_payrecord")
public class ZZLEfPayrecord {
	
	@Id
	@GenericGenerator(name="systemUUID",strategy="uuid")
	@GeneratedValue(generator="systemUUID")
	@Column(name = "id", unique = true, nullable = false,length=40)
	private String id;						//ID 属于 理财还款记录	
	
	@Column(name = "zzl_paycontrol_id",length=40)
	private String zzlPaycontrolId;
	
	@Column(name = "ef_order_id",length=40)
	private String efOrderId;
	
	
	@Column(name = "cust_info_id",length=40)
	private String custInfoId;	 
	
	@Column(name = "principal",length=12,scale=4)
	private Double principal;					//还款本金
	
	@Column(name = "interest",length=12,scale=4)
	private Double inteRest;					//还款利息
	
	@Column(name = "periods",length=2)
	private Integer periods;					//期数
	
	@Column(name = "pre_payment_penalty",length=12,scale=2)
	private Double prePaymentPenalty;			//提前还款违约金
	
	@Column(name = "over_penalty",length=12,scale=2)
	private Double overPenalty;					//逾期违约金
	
	@Column(name = "management_amt",length=12,scale=2)
	private Double managementAmt;				//管理费
	
	
	@Column(name = "operator",length=40)
	private String operator;					//	操作人	 关联SYS_EMPLOYEE ID 线下财务操作
	
	@Column(name = "evidence_url",length=40)
	private String evidenceUrl;					//还款凭证路径
		
	@Column(name = "create_time",length=40)
	private String createTime;				//创建时间|还款时间

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getZzlPaycontrolId() {
		return zzlPaycontrolId;
	}

	public void setZzlPaycontrolId(String zzlPaycontrolId) {
		this.zzlPaycontrolId = zzlPaycontrolId;
	}

	public String getEfOrderId() {
		return efOrderId;
	}

	public void setEfOrderId(String efOrderId) {
		this.efOrderId = efOrderId;
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

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public ZZLEfPayrecord(String id, String zzlPaycontrolId, String efOrderId,
			String custInfoId, Double principal, Double inteRest,
			Integer periods, Double prePaymentPenalty, Double overPenalty,
			Double managementAmt, String operator, String evidenceUrl,
			String createTime) {
		super();
		this.id = id;
		this.zzlPaycontrolId = zzlPaycontrolId;
		this.efOrderId = efOrderId;
		this.custInfoId = custInfoId;
		this.principal = principal;
		this.inteRest = inteRest;
		this.periods = periods;
		this.prePaymentPenalty = prePaymentPenalty;
		this.overPenalty = overPenalty;
		this.managementAmt = managementAmt;
		this.operator = operator;
		this.evidenceUrl = evidenceUrl;
		this.createTime = createTime;
	}

	public ZZLEfPayrecord() {
		super();
	}
	
	
}
