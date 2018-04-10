package com.apt.webapp.model.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 功能说明：理财订单表
 * 创建人：乔春峰  
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-10
 */
@Entity
@Table(name = "EF_ORDERS")
public class EfOrders {
	
	
	// 还款状态
	public static final int PAY_STATUS_REPAYMENT = 1; // 还款
	public static final int PAY_STATUS_SETTLEMENT  = 2; // 结清
	public static final int PAY_STATUS_SELL  = 3; // 出售
	public static final int PAY_STATUS_FLOW  = 4; // 流标
	public static final int PAY_STATUS_ABNORMAL  = 5; // 异常
	
	// 结清类型
	public static final int CLEAR_TYPE_ADVANCE = 1; // 提前结清
	public static final int CLEAR_TYPE_NORMAL = 2; // 提前结清
	
	@Id
	@GeneratedValue(generator = "uuidGenerator")
    @GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@Column(name = "ID", unique = true, nullable = false,length=40)
	private String id;						//ID 标识 理财订单表
	
	@Column(name = "CUST_ID",length=40)
	private String custId;					//BG_CUSTOMER ID
	
	@Column(name = "CUST_INFO_ID",length=40)
	private String custInfoId;				//BG_CUSTOMER_INFO ID
	
	@Column(name = "PRINCIPAL",length=12,scale=2)
	private Double principal;				//本金
	
	@Column(name = "EFFECT_TIME",length=50)
	private String effectTime;				//生效时间
	
	@Column(name = "CRM_ORDER_ID",length=40)
	private String crmOrderId;				//CRM_ORDER_ID 信貸訂單ID
	
	@Column(name = "PAY_STATUS",length=2,scale=0)
	private Integer payStatus;						//訂單状态	 0 投資中(V1的待处理)  1 還款中(V1的筹备中)  2 已結清(V1的結清)  3 出售中   4 流標 （V1的拒绝）5 异常  
		
	@Column(name = "EF_TYPE",length=2,scale=0)
	private Integer efType;					//订单类型 1 原始订单 2.被债权转让过(V2老数据债权) 3 债权订单 4 線下訂單(V1)
	
	@Column(name = "CLEAR_TYPE",length=2,scale=0)
	private Integer clearType;				//结清类型 1 为提前结清 2 正常结清 默认NULL 未结清
	
	@Column(name = "CLEAR_TIME",length=40)
	private String clearTime;				//结清时间
	
	@Column(name = "EF_FECTIVE_AMT",length=12,scale=2)
	private Double efFectiveAmt;			//有效投资金额  (债权转让部分后 其实际投资金额发生变化)
	
	@Column(name = "EF_FECTIVE_PERSIODS",length=2,scale=0)
	private Integer efFectivePersiods;		//有效投资期数 (默认是等于投资期数  仅区别V2老债权 里面的期数)
	
	@Column(name = "PRE_PAYMENT_PENALTY",length=12,scale=2)
	private Double prePaymentPenalty;		//提前还款违约金
	
	@Column(name = "CREATE_TIME",length=40)
	private String createTime;				//创建时间 | 投资时间
	
	@Column(name = "UPDATE_TIME",length=40)
	private String updateTime;				//更新时间
	
	@Column(name = "clearing_channel")
	private String clearingChannel;	// 结算户
	
	@Column(name = "pro_type_id")
	private String proTypeId;	  // 产品类型ID
	
	@Column(name = "pro_detail_id")
	private String proDetailId;		// 产品详情ID
	
	@Column(name = "ef_applay_id")
	private String efApplayId;
	
	@Column(name = "invest_auz_code")
	private String investAuzCode;	//	投资授权码
	
	@Column(name = "end_time")
	private String endTime;	//	订单到期时间
	
	@Column(name = "PLATFORM_TYPE")
	private String platformType;
	
	@Column(name = "order_number")
	private String orderNumber;
	
	
	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getPlatformType() {
		return platformType;
	}

	public void setPlatformType(String platformType) {
		this.platformType = platformType;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getInvestAuzCode() {
		return investAuzCode;
	}

	public void setInvestAuzCode(String investAuzCode) {
		this.investAuzCode = investAuzCode;
	}

	public String getEfApplayId() {
		return efApplayId;
	}

	public void setEfApplayId(String efApplayId) {
		this.efApplayId = efApplayId;
	}

	public String getProTypeId() {
		return proTypeId;
	}

	public void setProTypeId(String proTypeId) {
		this.proTypeId = proTypeId;
	}

	public String getProDetailId() {
		return proDetailId;
	}

	public void setProDetailId(String proDetailId) {
		this.proDetailId = proDetailId;
	}

	public String getClearingChannel() {
		return clearingChannel;
	}

	public void setClearingChannel(String clearingChannel) {
		this.clearingChannel = clearingChannel;
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

	public String getEffectTime() {
		return effectTime;
	}

	public void setEffectTime(String effectTime) {
		this.effectTime = effectTime;
	}

	public String getCrmOrderId() {
		return crmOrderId;
	}

	public void setCrmOrderId(String crmOrderId) {
		this.crmOrderId = crmOrderId;
	}

	public Integer getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	public Integer getClearType() {
		return clearType;
	}

	public void setClearType(Integer clearType) {
		this.clearType = clearType;
	}

	public String getClearTime() {
		return clearTime;
	}

	public void setClearTime(String clearTime) {
		this.clearTime = clearTime;
	}

	public Integer getEfType() {
		return efType;
	}

	public void setEfType(Integer efType) {
		this.efType = efType;
	}

	public Double getEfFectiveAmt() {
		return efFectiveAmt;
	}

	public void setEfFectiveAmt(Double efFectiveAmt) {
		this.efFectiveAmt = efFectiveAmt;
	}

	public Integer getEfFectivePersiods() {
		return efFectivePersiods;
	}

	public void setEfFectivePersiods(Integer efFectivePersiods) {
		this.efFectivePersiods = efFectivePersiods;
	}

	public Double getPrePaymentPenalty() {
		return prePaymentPenalty;
	}

	public void setPrePaymentPenalty(Double prePaymentPenalty) {
		this.prePaymentPenalty = prePaymentPenalty;
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

	public EfOrders(String id, String custId, String custInfoId,
			Double principal, String effectTime, String crmOrderId,
			Integer payStatus, Integer clearType, String clearTime,
			Integer efType, Double efFectiveAmt, Integer efFectivePersiods,
			Double prePaymentPenalty, String createTime, String updateTime) {
		super();
		this.id = id;
		this.custId = custId;
		this.custInfoId = custInfoId;
		this.principal = principal;
		this.effectTime = effectTime;
		this.crmOrderId = crmOrderId;
		this.payStatus = payStatus;
		this.clearType = clearType;
		this.clearTime = clearTime;
		this.efType = efType;
		this.efFectiveAmt = efFectiveAmt;
		this.efFectivePersiods = efFectivePersiods;
		this.prePaymentPenalty = prePaymentPenalty;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public EfOrders() {
		super();
		// TODO Auto-generated constructor stub
	}
}
