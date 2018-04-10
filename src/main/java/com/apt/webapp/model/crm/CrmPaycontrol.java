package com.apt.webapp.model.crm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


/**
 * 
 * 功能说明：信贷还款明细表
 * 乔春峰 
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-13
 */
@Entity
@Table(name = "CRM_PAYCONTROL")
public class CrmPaycontrol {
	@Id
	@GenericGenerator(name="systemUUID",strategy="uuid")
	@GeneratedValue(generator="systemUUID")
	@Column(name = "ID", unique = true, nullable = false,length=40)
	private String id;				//标识
	
	@Column(name = "crm_order_id",length=40)
	private String crmOrderId;			
	
	@Column(name = "cust_info_id",length=40)
	private String custInfoId;			
	@Column(name = "cust_id",length=40)
	private String custId;			
	
	@Column(name = "should_money",length=8,scale=2)
	private Double shouldMoney;		
	
	@Column(name = "should_capiital",length=8,scale=2)
	private Double shouldCapiital;		
	
	@Column(name = "should_accral",length=8,scale=2)
	private Double shouldAccral;
	
	@Column(name = "should_platform_manage_money",length=8,scale=2)
	private Double shouldPlatformManageMoney;
	
	@Column(name = "should_interest",length=8,scale=2)
	private Double shouldInterest;
	
	@Column(name = "should_violate_fee",length=8,scale=2)
	private Double shouldViolateFee;
	
	@Column(name = "repayment_time",length=20)
	private String repaymentTime;
	
	@Column(name = "status",length=2)
	private Integer status;
	
	@Column(name = "overdue_day",length=4)
	private Integer overdueDay;
	
	@Column(name = "overdue_interest",length=8,scale=2)
	private Double overdueInterest;
	
	@Column(name = "overdue_violate_money",length=8,scale=2)
	private Double overdueViolateMoney;
	
	@Column(name = "repayment_duetime",length=20)
	private Integer repaymentDuetime;
	
	@Column(name = "remain_interest",length=8,scale=2)
	private Double remainInterest;
	
	@Column(name = "remain_capital",length=8,scale=2)
	private Double remainCapital;
	
	
	@Column(name = "remain_accrual",length=8,scale=2)
	private Double remainAccrual;
	
	@Column(name = "remain_manage_fee",length=8,scale=2)
	private Double remainManageFee;
	
	@Column(name = "replace_status",length=2)
	private Integer replaceStatus;
	
	@Column(name = "exempt_status",length=2)
	private Integer exemptStatus;
	
	@Column(name = "abnormal_status",length=2)
	private Integer abnormalStatus;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCrmOrderId() {
		return crmOrderId;
	}

	public void setCrmOrderId(String crmOrderId) {
		this.crmOrderId = crmOrderId;
	}

	public String getCustInfoId() {
		return custInfoId;
	}

	public void setCustInfoId(String custInfoId) {
		this.custInfoId = custInfoId;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public Double getShouldMoney() {
		return shouldMoney;
	}

	public void setShouldMoney(Double shouldMoney) {
		this.shouldMoney = shouldMoney;
	}

	public Double getShouldCapiital() {
		return shouldCapiital;
	}

	public void setShouldCapiital(Double shouldCapiital) {
		this.shouldCapiital = shouldCapiital;
	}

	public Double getShouldAccral() {
		return shouldAccral;
	}

	public void setShouldAccral(Double shouldAccral) {
		this.shouldAccral = shouldAccral;
	}

	public Double getShouldPlatformManageMoney() {
		return shouldPlatformManageMoney;
	}

	public void setShouldPlatformManageMoney(Double shouldPlatformManageMoney) {
		this.shouldPlatformManageMoney = shouldPlatformManageMoney;
	}

	public Double getShouldInterest() {
		return shouldInterest;
	}

	public void setShouldInterest(Double shouldInterest) {
		this.shouldInterest = shouldInterest;
	}

	public Double getShouldViolateFee() {
		return shouldViolateFee;
	}

	public void setShouldViolateFee(Double shouldViolateFee) {
		this.shouldViolateFee = shouldViolateFee;
	}

	public String getRepaymentTime() {
		return repaymentTime;
	}

	public void setRepaymentTime(String repaymentTime) {
		this.repaymentTime = repaymentTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getOverdueDay() {
		return overdueDay;
	}

	public void setOverdueDay(Integer overdueDay) {
		this.overdueDay = overdueDay;
	}

	public Double getOverdueInterest() {
		return overdueInterest;
	}

	public void setOverdueInterest(Double overdueInterest) {
		this.overdueInterest = overdueInterest;
	}

	public Double getOverdueViolateMoney() {
		return overdueViolateMoney;
	}

	public void setOverdueViolateMoney(Double overdueViolateMoney) {
		this.overdueViolateMoney = overdueViolateMoney;
	}

	public Integer getRepaymentDuetime() {
		return repaymentDuetime;
	}

	public void setRepaymentDuetime(Integer repaymentDuetime) {
		this.repaymentDuetime = repaymentDuetime;
	}

	public Double getRemainInterest() {
		return remainInterest;
	}

	public void setRemainInterest(Double remainInterest) {
		this.remainInterest = remainInterest;
	}

	public Double getRemainCapital() {
		return remainCapital;
	}

	public void setRemainCapital(Double remainCapital) {
		this.remainCapital = remainCapital;
	}

	public Double getRemainAccrual() {
		return remainAccrual;
	}

	public void setRemainAccrual(Double remainAccrual) {
		this.remainAccrual = remainAccrual;
	}

	public Double getRemainManageFee() {
		return remainManageFee;
	}

	public void setRemainManageFee(Double remainManageFee) {
		this.remainManageFee = remainManageFee;
	}

	public Integer getReplaceStatus() {
		return replaceStatus;
	}

	public void setReplaceStatus(Integer replaceStatus) {
		this.replaceStatus = replaceStatus;
	}

	public Integer getExemptStatus() {
		return exemptStatus;
	}

	public void setExemptStatus(Integer exemptStatus) {
		this.exemptStatus = exemptStatus;
	}

	public Integer getAbnormalStatus() {
		return abnormalStatus;
	}

	public void setAbnormalStatus(Integer abnormalStatus) {
		this.abnormalStatus = abnormalStatus;
	}

	public CrmPaycontrol(String id, String crmOrderId, String custInfoId,
			String custId, Double shouldMoney, Double shouldCapiital,
			Double shouldAccral, Double shouldPlatformManageMoney,
			Double shouldInterest, Double shouldViolateFee,
			String repaymentTime, Integer status, Integer overdueDay,
			Double overdueInterest, Double overdueViolateMoney,
			Integer repaymentDuetime, Double remainInterest,
			Double remainCapital, Double remainAccrual, Double remainManageFee,
			Integer replaceStatus, Integer exemptStatus, Integer abnormalStatus) {
		super();
		this.id = id;
		this.crmOrderId = crmOrderId;
		this.custInfoId = custInfoId;
		this.custId = custId;
		this.shouldMoney = shouldMoney;
		this.shouldCapiital = shouldCapiital;
		this.shouldAccral = shouldAccral;
		this.shouldPlatformManageMoney = shouldPlatformManageMoney;
		this.shouldInterest = shouldInterest;
		this.shouldViolateFee = shouldViolateFee;
		this.repaymentTime = repaymentTime;
		this.status = status;
		this.overdueDay = overdueDay;
		this.overdueInterest = overdueInterest;
		this.overdueViolateMoney = overdueViolateMoney;
		this.repaymentDuetime = repaymentDuetime;
		this.remainInterest = remainInterest;
		this.remainCapital = remainCapital;
		this.remainAccrual = remainAccrual;
		this.remainManageFee = remainManageFee;
		this.replaceStatus = replaceStatus;
		this.exemptStatus = exemptStatus;
		this.abnormalStatus = abnormalStatus;
	}

	public CrmPaycontrol() {
		super();
	}

	
}
