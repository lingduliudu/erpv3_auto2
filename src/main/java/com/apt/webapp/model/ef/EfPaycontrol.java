package com.apt.webapp.model.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 功能说明：理财还款明细表
 * 创建人：乔春峰  
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-10
 */
@Entity
@Table(name = "EF_PAYCONTROL")
public class EfPaycontrol {

	// 状态
	public static final Integer STATUS_NOT_FINISH = 0; 		// 未结清
	public static final Integer STATUS_FINISH = 1;			// 结清
	public static final Integer STATUS_ABNORMAL = 2;		// 异常
	
	// 收益方式
	public static final Integer INCOME_TYPE_PRINCIPAL = 1; // 本金
	public static final Integer INCOME_TYPE_INTEREST = 2;	// 利息
	public static final Integer INCOME_TYPE_PRINCIPAL_AND_INTEREST = 3;	// 本息
	
	@Id
	@GenericGenerator(name="systemUUID",strategy="uuid")
	@GeneratedValue(generator="systemUUID")
	@Column(name = "id", unique = true, nullable = false,length=40)
	private String id;						//ID 标识 理财还款明细
	
	@Column(name = "ef_order_id",length=40)
	private String efOrderId;				//理财订单ID
	
	@Column(name = "cust_id",length=40)
	private String custId;					//客户ID 关联BG_CUSTOMER
	
	@Column(name = "cust_info_id",length=40)
	private String custInfoId;				//客户信息ID 关联BG_CUSTOMER_INFO
	
	@Column(name = "princiapl",length=12,scale=2)
	private Double princiapl;				//待还本金
	
	@Column(name = "interest",length=12,scale=2)
	private Double interest;				//待还利息
	
	@Column(name = "management_amt",length=12,scale=2)
	private Double managementAmt;			//待还管理费
	
	@Column(name = "total_amt",length=12,scale=2)
	private Double totalAmt;				//待还总额
	
	@Column(name = "pay_time",length=40)
	private String payTime;					//还款时间
	
	@Column(name = "periods",length=2,scale=0)
	private Integer periods;				//还款期数
	
	
	@Column(name = "pay_status",length=2,scale=0)
	private Integer payStatus;				//还款状态 0 未结清 1 结清 2 异常
	
	
	@Column(name = "surplus_principal",length=12,scale=2)
	private Double surplusPrincipal;		//剩余待还本金
	
	@Column(name = "surplus_interest",length=12,scale=2)
	private Double surplusInterest;			//剩余待还利息
	
	@Column(name = "surplus_management_amt",length=12,scale=2)
	private Double surplusManagementAmt;	//剩余管理费
	
	
	@Column(name = "rate_coupon",length=12,scale=2)
	private Double rateCoupon;	//加息卷利息
	
	@Column(name = "surplus_rate_coupon",length=12,scale=2)
	private Double surplusRateCoupon;	//剩余加息卷利息
	
	@Column(name = "create_time",length=40)
	private String createTime;				//创建时间
	
	@Column(name = "update_time",length=40)
	private String updateTime;				//更新时间

	@Column(name = "income_type")
	private Integer incomeType; // 收益类型
	
	
	public Integer getIncomeType() {
		return incomeType;
	}

	public void setIncomeType(Integer incomeType) {
		this.incomeType = incomeType;
	}

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

	public Double getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(Double totalAmt) {
		this.totalAmt = totalAmt;
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

	public Double getRateCoupon() {
		return rateCoupon;
	}

	public void setRateCoupon(Double rateCoupon) {
		this.rateCoupon = rateCoupon;
	}

	public Double getSurplusRateCoupon() {
		return surplusRateCoupon;
	}

	public void setSurplusRateCoupon(Double surplusRateCoupon) {
		this.surplusRateCoupon = surplusRateCoupon;
	}

	public EfPaycontrol() {
		super();
	}
}
