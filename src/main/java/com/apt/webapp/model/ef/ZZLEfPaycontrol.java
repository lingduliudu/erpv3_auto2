package com.apt.webapp.model.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


/**
 * 功能说明：ZZL理财还款明细表
 * 创建人：袁浩
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-10
 */
@Entity
@Table(name = "zzl_ef_paycontrol")
public class ZZLEfPaycontrol {
	
	@Id
	@GenericGenerator(name="systemUUID",strategy="uuid")
	@GeneratedValue(generator="systemUUID")
	@Column(name = "id", unique = true, nullable = false,length=40)
	private String id;						//ID 标识 理财还款明细
	
	@Column(name = "ef_order_id",length=40)
	private String efOrderId;				//理财订单ID
	
	
	@Column(name = "cust_info_id",length=40)
	private String custInfoId;				//客户信息ID 关联BG_CUSTOMER_INFO
	
	@Column(name = "type",length=2)
	private Integer type;				
	
	@Column(name = "princiapl",length=14,scale=2)
	private Double princiapl;				//待还本金
	
	@Column(name = "interest",length=14,scale=2)
	private Double interest;				//待还利息
	
	@Column(name = "management_amt",length=14,scale=2)
	private Double managementAmt;			//待还管理费
	
	@Column(name = "pay_time",length=40)
	private String payTime;					//还款时间
	
	@Column(name = "periods",length=2)
	private Integer periods;				//还款期数
	
	
	@Column(name = "pay_status",length=2)
	private Integer payStatus;				//还款状态 0 未结清 1 结清 2 异常
	
	
	@Column(name = "surplus_principal",length=14,scale=2)
	private Double surplusPrincipal;		//剩余待还本金
	
	@Column(name = "surplus_interest",length=14,scale=2)
	private Double surplusInterest;			//剩余待还利息
	
	@Column(name = "surplus_management_amt",length=14,scale=2)
	private Double surplusManagementAmt;	//剩余管理费
	
	@Column(name = "create_time",length=40)
	private String createTime;				//创建时间
	
	@Column(name = "update_time",length=40)
	private String updateTime;				//更新时间

	@Column(name = "coupon_interest",length=14,scale=2)
	private Double couponInterest; 
	
	@Column(name = "control_id")
	private String controlId;
	
	@Column(name = "more_interest",length=14,scale=2)
	private Double moreInterest;
	
	@Column(name = "more_manage_amt",length=14,scale=2)
	private Double moreManageAmt; 
	
	@Column(name = "over_penalty",length=14,scale=2)
	private Double overPenalty;
	
	@Column(name = "over_cost",length=14,scale=2)
	private Double overCost; 
	
	@Column(name = "pre_payment_penalty",length=14,scale=2)
	private Double prePaymentPenalty;
	
	@Column(name = "auth_code")
	private String authCode;
	
	@Column(name = "clearing_channel")
	private Integer clearingChannel;
	
	@Column(name = "temp_principal",length=14,scale=2)
	private Double tempPrincipal;
	
	@Column(name = "crm_order_id")
	private String crmOrderId;
	
	@Column(name = "seri_no")
	private String seriNo;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Double getPrinciapl() {
		return princiapl;
	}

	public void setPrinciapl(Double princiapl) {
		this.princiapl = princiapl;
	}

	public Double getInterest() {
		return interest;
	}

	public void setInterest(Double interest) {
		this.interest = interest;
	}

	public Double getManagementAmt() {
		return managementAmt;
	}

	public void setManagementAmt(Double managementAmt) {
		this.managementAmt = managementAmt;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public Integer getPeriods() {
		return periods;
	}

	public void setPeriods(Integer periods) {
		this.periods = periods;
	}

	public Integer getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	public Double getSurplusPrincipal() {
		return surplusPrincipal;
	}

	public void setSurplusPrincipal(Double surplusPrincipal) {
		this.surplusPrincipal = surplusPrincipal;
	}

	public Double getSurplusInterest() {
		return surplusInterest;
	}

	public void setSurplusInterest(Double surplusInterest) {
		this.surplusInterest = surplusInterest;
	}

	public Double getSurplusManagementAmt() {
		return surplusManagementAmt;
	}

	public void setSurplusManagementAmt(Double surplusManagementAmt) {
		this.surplusManagementAmt = surplusManagementAmt;
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

	public Double getCouponInterest() {
		return couponInterest;
	}

	public void setCouponInterest(Double couponInterest) {
		this.couponInterest = couponInterest;
	}

	public String getControlId() {
		return controlId;
	}

	public void setControlId(String controlId) {
		this.controlId = controlId;
	}

	public Double getMoreInterest() {
		return moreInterest;
	}

	public void setMoreInterest(Double moreInterest) {
		this.moreInterest = moreInterest;
	}

	public Double getOverPenalty() {
		return overPenalty;
	}

	public void setOverPenalty(Double overPenalty) {
		this.overPenalty = overPenalty;
	}

	public Double getPrePaymentPenalty() {
		return prePaymentPenalty;
	}

	public void setPrePaymentPenalty(Double prePaymentPenalty) {
		this.prePaymentPenalty = prePaymentPenalty;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public Integer getClearingChannel() {
		return clearingChannel;
	}

	public void setClearingChannel(Integer clearingChannel) {
		this.clearingChannel = clearingChannel;
	}

	public String getCrmOrderId() {
		return crmOrderId;
	}

	public void setCrmOrderId(String crmOrderId) {
		this.crmOrderId = crmOrderId;
	}

	
	public Double getMoreManageAmt() {
		return moreManageAmt;
	}

	public void setMoreManageAmt(Double moreManageAmt) {
		this.moreManageAmt = moreManageAmt;
	}

	public Double getOverCost() {
		return overCost;
	}

	public void setOverCost(Double overCost) {
		this.overCost = overCost;
	}
	
	public String getSeriNo() {
		return seriNo;
	}

	public void setSeriNo(String seriNo) {
		this.seriNo = seriNo;
	}
	
	

	public Double getTempPrincipal() {
		return tempPrincipal;
	}

	public void setTempPrincipal(Double tempPrincipal) {
		this.tempPrincipal = tempPrincipal;
	}

	
	public ZZLEfPaycontrol(String id, String efOrderId, String custInfoId,
			Integer type, Double princiapl, Double interest,
			Double managementAmt, String payTime, Integer periods,
			Integer payStatus, Double surplusPrincipal, Double surplusInterest,
			Double surplusManagementAmt, String createTime, String updateTime,
			Double couponInterest, String controlId, Double moreInterest,
			Double moreManageAmt, Double overPenalty, Double overCost,
			Double prePaymentPenalty, String authCode, Integer clearingChannel,
			Double tempPrincipal, String crmOrderId, String seriNo) {
		super();
		this.id = id;
		this.efOrderId = efOrderId;
		this.custInfoId = custInfoId;
		this.type = type;
		this.princiapl = princiapl;
		this.interest = interest;
		this.managementAmt = managementAmt;
		this.payTime = payTime;
		this.periods = periods;
		this.payStatus = payStatus;
		this.surplusPrincipal = surplusPrincipal;
		this.surplusInterest = surplusInterest;
		this.surplusManagementAmt = surplusManagementAmt;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.couponInterest = couponInterest;
		this.controlId = controlId;
		this.moreInterest = moreInterest;
		this.moreManageAmt = moreManageAmt;
		this.overPenalty = overPenalty;
		this.overCost = overCost;
		this.prePaymentPenalty = prePaymentPenalty;
		this.authCode = authCode;
		this.clearingChannel = clearingChannel;
		this.tempPrincipal = tempPrincipal;
		this.crmOrderId = crmOrderId;
		this.seriNo = seriNo;
	}

	public ZZLEfPaycontrol() {
		super();
	} 
	
}
