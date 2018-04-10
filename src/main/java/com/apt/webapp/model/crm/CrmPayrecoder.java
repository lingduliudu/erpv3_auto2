package com.apt.webapp.model.crm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


/**
 * 
 * 功能说明：信贷订单还款记录
 * 乔春峰 
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-13
 */
@Entity
@Table(name = "CRM_PAYRECODER")
public class CrmPayrecoder {
	@Id
	@GenericGenerator(name="systemUUID",strategy="uuid")
	@GeneratedValue(generator="systemUUID")
	@Column(name = "ID", unique = true, nullable = false,length=40)
	private String id;	
	
	@Column(name = "cust_id",length=40)
	private String custId;
	
	@Column(name = "cust_info_id",length=40)
	private String custInfoId;
	
	@Column(name = "crm_order_id",length=40)
	private String crmOrderId;
	
	@Column(name = "Should_MONEY",length=8,scale=2)
	private Double ShouldMONEY;
		
	@Column(name = "Should_CAPITAL",length=8,scale=2)
	private Double shouldCAPITAL;
	
	@Column(name = "Should_accrual",length=8,scale=2)
	private Double ShouldAccrual;
	
	@Column(name = "create_time",length=20)
	private String createTime;
	
	@Column(name = "emp_id",length=40)
	private String empId;
	
	@Column(name = "remain_fee",length=8,scale=2)
	private Double remainFee;
	
	@Column(name = "PAYCONTROL_ID",length=40)
	private String paycontrolId;
	
	@Column(name = "Remark",length=500)
	private String remark;
	
	@Column(name = "repayment_type",length=2)
	private Integer repaymentType;
	
	@Column(name = "Manage_fee",length=8,scale=2)
	private Double manageFee;
	
	@Column(name = "overdue_INTEREST",length=8,scale=2)
	private Double overdueInterest;
	
	@Column(name = "prepayment_violate_money",length=8,scale=2)
	private Double prepaymentViolateMoney;
	
	@Column(name = "overdue_violate_money",length=8,scale=2)
	private Double overdueViolateMoney;
	
	@Column(name = "should_interest",length=8,scale=2)
	private Double shouldInterest;
	
	@Column(name = "certificate_url",length=200)
	private String certificateUrl;

	@Column(name = "operation_platform",length=20)
	private String operationPlatform;
	
	@Column(name = "payment_channel",length=20)
	private String paymentChannel;
	
	@Column(name = "payment_platform",length=20)
	private String paymentPlatform;
	
	public String getOperationPlatform() {
		return operationPlatform;
	}

	public void setOperationPlatform(String operationPlatform) {
		this.operationPlatform = operationPlatform;
	}

	public String getPaymentChannel() {
		return paymentChannel;
	}

	public void setPaymentChannel(String paymentChannel) {
		this.paymentChannel = paymentChannel;
	}

	public String getPaymentPlatform() {
		return paymentPlatform;
	}

	public void setPaymentPlatform(String paymentPlatform) {
		this.paymentPlatform = paymentPlatform;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getCrmOrderId() {
		return crmOrderId;
	}

	public void setCrmOrderId(String crmOrderId) {
		this.crmOrderId = crmOrderId;
	}

	public Double getShouldMONEY() {
		return ShouldMONEY;
	}

	public void setShouldMONEY(Double shouldMONEY) {
		ShouldMONEY = shouldMONEY;
	}

	public Double getShouldCAPITAL() {
		return shouldCAPITAL;
	}

	public void setShouldCAPITAL(Double shouldCAPITAL) {
		this.shouldCAPITAL = shouldCAPITAL;
	}

	public Double getShouldAccrual() {
		return ShouldAccrual;
	}

	public void setShouldAccrual(Double shouldAccrual) {
		ShouldAccrual = shouldAccrual;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public Double getRemainFee() {
		return remainFee;
	}

	public void setRemainFee(Double remainFee) {
		this.remainFee = remainFee;
	}

	public String getPaycontrolId() {
		return paycontrolId;
	}

	public void setPaycontrolId(String paycontrolId) {
		this.paycontrolId = paycontrolId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getRepaymentType() {
		return repaymentType;
	}

	public void setRepaymentType(Integer repaymentType) {
		this.repaymentType = repaymentType;
	}

	public Double getManageFee() {
		return manageFee;
	}

	public void setManageFee(Double manageFee) {
		this.manageFee = manageFee;
	}

	public Double getOverdueInterest() {
		return overdueInterest;
	}

	public void setOverdueInterest(Double overdueInterest) {
		this.overdueInterest = overdueInterest;
	}

	public Double getPrepaymentViolateMoney() {
		return prepaymentViolateMoney;
	}

	public void setPrepaymentViolateMoney(Double prepaymentViolateMoney) {
		this.prepaymentViolateMoney = prepaymentViolateMoney;
	}

	public Double getOverdueViolateMoney() {
		return overdueViolateMoney;
	}

	public void setOverdueViolateMoney(Double overdueViolateMoney) {
		this.overdueViolateMoney = overdueViolateMoney;
	}

	public String getCertificateUrl() {
		return certificateUrl;
	}

	public void setCertificateUrl(String certificateUrl) {
		this.certificateUrl = certificateUrl;
	}
	public Double getShouldInterest() {
		return shouldInterest;
	}

	public void setShouldInterest(Double shouldInterest) {
		this.shouldInterest = shouldInterest;
	}

	public String getCustInfoId() {
		return custInfoId;
	}

	public void setCustInfoId(String custInfoId) {
		this.custInfoId = custInfoId;
	}

	public CrmPayrecoder(String id, String custId, String crmOrderId,
			Double shouldMONEY, Double shouldCAPITAL, Double shouldAccrual,
			String createTime, String empId, Double remainFee,
			String paycontrolId, String remark, Integer repaymentType,
			Double manageFee, Double overdueInterest,
			Double prepaymentViolateMoney, Double overdueViolateMoney,
			String certificateUrl,Double shouldInterest,String custInfoId) {
		super();
		this.id = id;
		this.custId = custId;
		this.crmOrderId = crmOrderId;
		ShouldMONEY = shouldMONEY;
		this.shouldCAPITAL = shouldCAPITAL;
		ShouldAccrual = shouldAccrual;
		this.createTime = createTime;
		this.empId = empId;
		this.remainFee = remainFee;
		this.paycontrolId = paycontrolId;
		this.remark = remark;
		this.repaymentType = repaymentType;
		this.manageFee = manageFee;
		this.overdueInterest = overdueInterest;
		this.prepaymentViolateMoney = prepaymentViolateMoney;
		this.overdueViolateMoney = overdueViolateMoney;
		this.certificateUrl = certificateUrl;
		this.shouldInterest = shouldInterest;
		this.custInfoId = custInfoId;
	}

	public CrmPayrecoder() {
		super();
	}

}
