package com.apt.webapp.model.bg.ef;

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
@Table(name = "bg_ef_paycontrol")
public class BgEfPaycontrol {

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
	
	@Column(name = "type",length=2,scale=0)
	private Integer type;					//还款類型		1 線上 2 線下(v1)
	
	@Column(name = "pay_status",length=2,scale=0)
	private Integer payStatus;				//还款状态 0 未结清 1 结清 2 异常
	
	@Column(name = "live_status",length=2,scale=0)
	private Integer liveStatus;				//明细状态 0 死亡 1 活动中 默认1
	
	@Column(name = "surplus_principal",length=12,scale=2)
	private Double surplusPrincipal;		//剩余待还本金
	
	@Column(name = "surplus_interest",length=12,scale=2)
	private Double surplusInterest;			//剩余待还利息
	
	@Column(name = "surplus_management_amt",length=12,scale=2)
	private Double surplusManagementAmt;	//剩余管理费
	
	@Column(name = "transfer_record_id",length=40)
	private String transferRecordId;		//债权转让记录ID(用于V2老债权 查询)
	
	@Column(name = "create_time",length=40)
	private String createTime;				//创建时间
	
	@Column(name = "update_time",length=40)
	private String updateTime;				//更新时间
	
	@Column(name = "coupon_interest",length=12,scale=2)
	private Double couponInterest;				//抵用卷利息
	
	@Column(name = "referee_info_id")
	private String refereeInfoId;				//推荐人id  bg_cust_ifno
	
	@Column(name = "referee_income_scale",length=12,scale=2)
	private Double refereeIncomeScale;				//推荐人的收益比率
	
	@Column(name = "score_scale",length=12,scale=2)
	private Double scoreScale;				//积分比率
	
	@Column(name = "use_coupon_interest",length=12,scale=2)
	private Double useCouponInterest;				//抵用卷
	
	@Column(name = "operate_type")
	private String operateType;				//操作状态  1 是手动用的, 0 是自动用的
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEfOrderId() {
		return efOrderId;
	}
	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	public Integer getLiveStatus() {
		return liveStatus;
	}

	public void setLiveStatus(Integer liveStatus) {
		this.liveStatus = liveStatus;
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

	public String getTransferRecordId() {
		return transferRecordId;
	}

	public void setTransferRecordId(String transferRecordId) {
		this.transferRecordId = transferRecordId;
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

	public BgEfPaycontrol() {
		super();
	}

	public Double getCouponInterest() {
		return couponInterest;
	}

	public void setCouponInterest(Double couponInterest) {
		this.couponInterest = couponInterest;
	}


	public String getRefereeInfoId() {
		return refereeInfoId;
	}

	public void setRefereeInfoId(String refereeInfoId) {
		this.refereeInfoId = refereeInfoId;
	}

	public Double getRefereeIncomeScale() {
		return refereeIncomeScale;
	}

	public void setRefereeIncomeScale(Double refereeIncomeScale) {
		this.refereeIncomeScale = refereeIncomeScale;
	}

	public Double getScoreScale() {
		return scoreScale;
	}

	public void setScoreScale(Double scoreScale) {
		this.scoreScale = scoreScale;
	}

	public Double getUseCouponInterest() {
		return useCouponInterest;
	}

	public void setUseCouponInterest(Double useCouponInterest) {
		this.useCouponInterest = useCouponInterest;
	}
	
	

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public BgEfPaycontrol(String id, String efOrderId, String custId,
			String custInfoId, Double princiapl, Double interest,
			Double managementAmt, Double totalAmt, String payTime,
			Integer periods, Integer type, Integer payStatus,
			Integer liveStatus, Double surplusPrincipal,
			Double surplusInterest, Double surplusManagementAmt,
			String transferRecordId, String createTime, String updateTime,
			Double couponInterest, String refereeInfoId, Double refereeIncomeScale,Double scoreScale,Double useCouponInterest,String operateType) {
		super();
		this.id = id;
		this.efOrderId = efOrderId;
		this.custId = custId;
		this.custInfoId = custInfoId;
		this.princiapl = princiapl;
		this.interest = interest;
		this.managementAmt = managementAmt;
		this.totalAmt = totalAmt;
		this.payTime = payTime;
		this.periods = periods;
		this.type = type;
		this.payStatus = payStatus;
		this.liveStatus = liveStatus;
		this.surplusPrincipal = surplusPrincipal;
		this.surplusInterest = surplusInterest;
		this.surplusManagementAmt = surplusManagementAmt;
		this.transferRecordId = transferRecordId;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.couponInterest = couponInterest;
		this.refereeInfoId = refereeInfoId;
		this.refereeIncomeScale = refereeIncomeScale;
		this.scoreScale = scoreScale;
		this.useCouponInterest = useCouponInterest;
		this.operateType =  operateType;
	}

}
