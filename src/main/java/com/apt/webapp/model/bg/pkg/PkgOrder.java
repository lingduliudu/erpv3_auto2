package com.apt.webapp.model.bg.pkg;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 资产包订单信息
 * @author huangyuanlong 2016-8-2
 *
 */
@Entity
@Table(name = "PKG_ORDER")
public class PkgOrder {
	
	//订单线上类型
	public static final Integer PLATFORM_TYPE_ONLINE = 1;//线上(贝格)
	public static final Integer PLATFORM_TYPE_BELOWLINE = 0;//线下 （中资联平台）
	
	
	@Id
	@GeneratedValue(generator = "uuidGenerator")
    @GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@Column(name = "ID", unique = true, nullable = false,length=40)
	private String id;
	
	@Column(name = "ORDER_NUMBER",length=50)
	private String orderNumber;		// 资产包编号
	
	@Column(name = "PRO_TYPE_ID",length=20)
	private String proTypeId;	// 资产包类型的id
	
	@Column(name = "PRO_DETAIL_ID",length=50)
	private String proDetailId;		// 资产包产品详细的id
	
	@Column(name = "CONTRACT_MONEY",length=14,scale=4)
	private Double contractMoney; //合同金额
	
	@Column(name = "CREDIT_MONEY",length=14,scale=4)
	private Double creditMoney; //授信额度,客户拿到手的额度
	
	@Column(name = "CREATE_TIME",length=20)
	private String createTime;	// 生成时间
	
	@Column(name = "EXPIRE_TIME",length=20)
	private String expireTime;	// 满标时间
	
	@Column(name = "LOAN_TIME",length=20)
	private String loanTime;	// 放款时间
	
	@Column(name = "REMARK",length=500)
	private String remark;	// 备注
	
	@Column(name = "CLEAR_TYPE")
	private Integer clearType;//结清类型      1 正常到期结清 2.主动结清 3.催收被动结清 4.代偿 5.提前结清
	
	@Column(name = "FINISH_TIME",length=20)
	private String finishTime;	//订单结清时间      撤销投资人，冻结金额时间
	
	@Column(name = "PROGRESS",length=5,scale=2)
	private Double progress; //投资进度
	
	@Column(name = "MATURE_TIME",length=20)
	private String matureTime;//到期时间
	
	@Column(name = "ONLINE_TIME",length=20)
	private String onlineTime;//上线时间
	
	@Column(name = "PLATFORM_TYPE" )
	private Integer platformType;//平台类型    1 线上 0 线下 （中资联平台） 2 资产包订单
	
	@Column(name = "ORDER_TRADE_STATUS" )
	private Integer orderTradeStatus; //订单状态	0 交易中 1 进入还款 2 流标 3 满标 4 交易成功 5 订单异常 6 死亡 7 满标放款中 9 待匹配
	
	@Column(name = "HAS_INVEST_AMT",length=14,scale=4)
	private Double hasInvestAmt; //已投金额
	
	@Column(name = "EF_PRD_TYPE_ID",length=40)
	private String efPrdTypeId;	// 贝格对应产品类型的id
	
	@Column(name = "EF_PRD_DETAIL_ID",length=40)
	private String efPrdDetailId;		// 贝格对应产品详细的id
	
	@Column(name = "CLEARING_CHANNEL",length=40)
	private String clearingChannel;	// 结算通道	1.poc  2.boc 
	
	@Column(name = "company_a_server_fund",length=14,scale=4)
	private Double companyAServerFund; //信息（总服务费
	
	@Column(name = "company_b_server_fund",length=14,scale=4)
	private Double companyBServerFund; //信用（总服务费
	
	@Column(name = "company_c_server_fund",length=14,scale=4)
	private Double companyCServerFund; //财富投资（总服务费

	@Column(name = "company_d_server_fund",length=14,scale=4)
	private Double companyDServerFund; //投资管理（总服务费

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
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

	public Double getContractMoney() {
		return contractMoney;
	}

	public void setContractMoney(Double contractMoney) {
		this.contractMoney = contractMoney;
	}

	public Double getCreditMoney() {
		return creditMoney;
	}

	public void setCreditMoney(Double creditMoney) {
		this.creditMoney = creditMoney;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(String expireTime) {
		this.expireTime = expireTime;
	}

	public String getLoanTime() {
		return loanTime;
	}

	public void setLoanTime(String loanTime) {
		this.loanTime = loanTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getClearType() {
		return clearType;
	}

	public void setClearType(Integer clearType) {
		this.clearType = clearType;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}

	public Double getProgress() {
		return progress;
	}

	public void setProgress(Double progress) {
		this.progress = progress;
	}

	public String getMatureTime() {
		return matureTime;
	}

	public void setMatureTime(String matureTime) {
		this.matureTime = matureTime;
	}

	public String getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(String onlineTime) {
		this.onlineTime = onlineTime;
	}

	public Integer getPlatformType() {
		return platformType;
	}

	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}

	public Integer getOrderTradeStatus() {
		return orderTradeStatus;
	}

	public void setOrderTradeStatus(Integer orderTradeStatus) {
		this.orderTradeStatus = orderTradeStatus;
	}

	public Double getHasInvestAmt() {
		return hasInvestAmt;
	}

	public void setHasInvestAmt(Double hasInvestAmt) {
		this.hasInvestAmt = hasInvestAmt;
	}

	public String getEfPrdTypeId() {
		return efPrdTypeId;
	}

	public void setEfPrdTypeId(String efPrdTypeId) {
		this.efPrdTypeId = efPrdTypeId;
	}

	public String getEfPrdDetailId() {
		return efPrdDetailId;
	}

	public void setEfPrdDetailId(String efPrdDetailId) {
		this.efPrdDetailId = efPrdDetailId;
	}

	public String getClearingChannel() {
		return clearingChannel;
	}

	public void setClearingChannel(String clearingChannel) {
		this.clearingChannel = clearingChannel;
	}

	public Double getCompanyAServerFund() {
		return companyAServerFund;
	}

	public void setCompanyAServerFund(Double companyAServerFund) {
		this.companyAServerFund = companyAServerFund;
	}

	public Double getCompanyBServerFund() {
		return companyBServerFund;
	}

	public void setCompanyBServerFund(Double companyBServerFund) {
		this.companyBServerFund = companyBServerFund;
	}

	public Double getCompanyCServerFund() {
		return companyCServerFund;
	}

	public void setCompanyCServerFund(Double companyCServerFund) {
		this.companyCServerFund = companyCServerFund;
	}

	public Double getCompanyDServerFund() {
		return companyDServerFund;
	}

	public void setCompanyDServerFund(Double companyDServerFund) {
		this.companyDServerFund = companyDServerFund;
	}

}
