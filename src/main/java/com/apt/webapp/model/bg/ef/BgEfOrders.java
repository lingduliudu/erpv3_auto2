package com.apt.webapp.model.bg.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * BgEfOrders entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "bg_ef_orders")
public class BgEfOrders implements java.io.Serializable {

	// Fields

	private String id;
	private String clearTime;
	private Integer clearType;
	private String createTime;
	private String crmOrderId;
	private String custId;
	private String custInfoId;
	private Double efFectiveAmt;
	private Integer efFectiveCount;
	private Long efFectivePersiods;
	private Integer efType;
	private String effectTime;
	private Integer payStatus;
	private Double prePaymentPenalty;
	private Double principal;
	private String investAuzCode;
	private String investSeriNum;
	private Double couponAmount;
	private String updateTime;
	private String couponEfOrderId;
	private String clearingChannel;
	private String couponId;
	private String efOrderId;
	private Integer orderOwner;
	private Double useCouponAmount;
	private String orderNumber;
	private String investmentModel;

	// Constructors

	/** default constructor */
	public BgEfOrders() {
	}

	/** full constructor */
	public BgEfOrders(String clearTime, Integer clearType, String createTime,
			String crmOrderId, String custId, String custInfoId,
			Double efFectiveAmt, Integer efFectiveCount,
			Long efFectivePersiods, Integer efType, String effectTime,
			Integer payStatus, Double prePaymentPenalty, Double principal,
			String investAuzCode, Double couponAmount, String updateTime,
			String couponEfOrderId, String clearingChannel, String couponId,
			String efOrderId, Integer orderOwner, Double useCouponAmount,
			String orderNumber,String investSeriNum,String investmentModel) {
		this.clearTime = clearTime;
		this.clearType = clearType;
		this.createTime = createTime;
		this.crmOrderId = crmOrderId;
		this.custId = custId;
		this.custInfoId = custInfoId;
		this.efFectiveAmt = efFectiveAmt;
		this.efFectiveCount = efFectiveCount;
		this.efFectivePersiods = efFectivePersiods;
		this.efType = efType;
		this.effectTime = effectTime;
		this.payStatus = payStatus;
		this.prePaymentPenalty = prePaymentPenalty;
		this.principal = principal;
		this.investAuzCode = investAuzCode;
		this.couponAmount = couponAmount;
		this.updateTime = updateTime;
		this.couponEfOrderId = couponEfOrderId;
		this.clearingChannel = clearingChannel;
		this.couponId = couponId;
		this.efOrderId = efOrderId;
		this.orderOwner = orderOwner;
		this.useCouponAmount = useCouponAmount;
		this.orderNumber = orderNumber;
		this.investSeriNum = investSeriNum;
		this.investmentModel = investmentModel;
	}

	// Property accessors
	@Id
	@GenericGenerator(name="systemUUID",strategy="uuid")
	@GeneratedValue(generator="systemUUID")
	@Column(name = "id", unique = true, nullable = false, length = 40)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "clear_time", length = 40)
	public String getClearTime() {
		return this.clearTime;
	}

	public void setClearTime(String clearTime) {
		this.clearTime = clearTime;
	}

	@Column(name = "clear_type")
	public Integer getClearType() {
		return this.clearType;
	}

	public void setClearType(Integer clearType) {
		this.clearType = clearType;
	}

	@Column(name = "create_time", length = 40)
	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Column(name = "crm_order_id", length = 40)
	public String getCrmOrderId() {
		return this.crmOrderId;
	}

	public void setCrmOrderId(String crmOrderId) {
		this.crmOrderId = crmOrderId;
	}

	@Column(name = "cust_id", length = 40)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "cust_info_id", length = 40)
	public String getCustInfoId() {
		return this.custInfoId;
	}

	public void setCustInfoId(String custInfoId) {
		this.custInfoId = custInfoId;
	}

	@Column(name = "ef_fective_amt", precision = 65)
	public Double getEfFectiveAmt() {
		return this.efFectiveAmt;
	}

	public void setEfFectiveAmt(Double efFectiveAmt) {
		this.efFectiveAmt = efFectiveAmt;
	}

	@Column(name = "ef_fective_count")
	public Integer getEfFectiveCount() {
		return this.efFectiveCount;
	}

	public void setEfFectiveCount(Integer efFectiveCount) {
		this.efFectiveCount = efFectiveCount;
	}

	@Column(name = "ef_fective_persiods", precision = 12, scale = 0)
	public Long getEfFectivePersiods() {
		return this.efFectivePersiods;
	}

	public void setEfFectivePersiods(Long efFectivePersiods) {
		this.efFectivePersiods = efFectivePersiods;
	}

	@Column(name = "ef_type")
	public Integer getEfType() {
		return this.efType;
	}

	public void setEfType(Integer efType) {
		this.efType = efType;
	}

	@Column(name = "effect_time", length = 50)
	public String getEffectTime() {
		return this.effectTime;
	}

	public void setEffectTime(String effectTime) {
		this.effectTime = effectTime;
	}

	@Column(name = "pay_status")
	public Integer getPayStatus() {
		return this.payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	@Column(name = "pre_payment_penalty", precision = 65)
	public Double getPrePaymentPenalty() {
		return this.prePaymentPenalty;
	}

	public void setPrePaymentPenalty(Double prePaymentPenalty) {
		this.prePaymentPenalty = prePaymentPenalty;
	}

	@Column(name = "principal", precision = 65)
	public Double getPrincipal() {
		return this.principal;
	}

	public void setPrincipal(Double principal) {
		this.principal = principal;
	}

	@Column(name = "invest_auz_code", length = 40)
	public String getInvestAuzCode() {
		return this.investAuzCode;
	}
	
	public void setInvestAuzCode(String investAuzCode) {
		this.investAuzCode = investAuzCode;
	}
	
	@Column(name = "invest_seri_num", length =60)
	public String getInvestSeriNum() {
		return this.investSeriNum;
	}
	
	public void setInvestSeriNum(String investSeriNum) {
		this.investSeriNum = investSeriNum;
	}

	@Column(name = "coupon_amount", precision = 8)
	public Double getCouponAmount() {
		return this.couponAmount;
	}

	public void setCouponAmount(Double couponAmount) {
		this.couponAmount = couponAmount;
	}

	@Column(name = "update_time", length = 40)
	public String getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "coupon_ef_order_id", length = 40)
	public String getCouponEfOrderId() {
		return this.couponEfOrderId;
	}

	public void setCouponEfOrderId(String couponEfOrderId) {
		this.couponEfOrderId = couponEfOrderId;
	}

	@Column(name = "clearing_channel", length = 20)
	public String getClearingChannel() {
		return this.clearingChannel;
	}

	public void setClearingChannel(String clearingChannel) {
		this.clearingChannel = clearingChannel;
	}

	@Column(name = "coupon_id", length = 40)
	public String getCouponId() {
		return this.couponId;
	}

	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}

	@Column(name = "ef_order_id", length = 60)
	public String getEfOrderId() {
		return this.efOrderId;
	}

	public void setEfOrderId(String efOrderId) {
		this.efOrderId = efOrderId;
	}

	@Column(name = "order_owner")
	public Integer getOrderOwner() {
		return this.orderOwner;
	}

	public void setOrderOwner(Integer orderOwner) {
		this.orderOwner = orderOwner;
	}

	@Column(name = "use_coupon_amount", precision = 8)
	public Double getUseCouponAmount() {
		return this.useCouponAmount;
	}

	public void setUseCouponAmount(Double useCouponAmount) {
		this.useCouponAmount = useCouponAmount;
	}

	@Column(name = "order_number", length = 40)
	public String getOrderNumber() {
		return this.orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	@Column(name = "investment_model", length = 40)
	public String getInvestmentModel() {
		return investmentModel;
	}

	public void setInvestmentModel(String investmentModel) {
		this.investmentModel = investmentModel;
	}

}