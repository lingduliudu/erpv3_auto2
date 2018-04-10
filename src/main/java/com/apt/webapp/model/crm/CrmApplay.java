package com.apt.webapp.model.crm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * CrmApplay entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "crm_applay")
public class CrmApplay implements java.io.Serializable {
	// Property accessors
	@Id
	@GenericGenerator(name="systemUUID",strategy="uuid")
	@GeneratedValue(generator="systemUUID")
	@Column(name = "id", unique = true, nullable = false,length=40)
	private String id;				//标识
	
	@Column(name = "EXPECT_MOMEY",length=10,scale=0)
	private Double momey;			//期待金额
	
	@Column(name = "EXPECT_DUETIME",length=3,scale=0)
	private Integer duetime;		//期待期数
	
	@Column(name = "RECEIVE_REPAYMENT_MOMEY",length=8,scale=2)
	private Double repaymentMomey;	//接收还款金额/月
	
	@Column(name = "EXPECT_PAYMENT",length=2,scale=0)
	private Integer payment;		//期待还款方式
	
	@Column(name = "PAYMENT_REMARK",length=500)
	private String paymentRemark;				//还款方式备注
	
	@Column(name = "REPAYMENT_PURPOSE",length=500)
	private String purpose;			//借款用途
	
	@Column(name = "STATUS",length=2,scale=0)
	private Integer status;			//订单状态, 1:待命 2:审批中 3:还款中 4:已结清 5:拒绝
	
	@Column(name = "REPAYMENT_STATUS",length=2,scale=0)
	private Integer repaymentStatus;//还款状态, 1:正常到期结清 2.主动结清 3.催收被动结清 4.代偿
	
	@Column(name = "REVIEW_STATUS",length=2,scale=0)
	private Integer reviewStatus;	//评审状态,订单当前的审批步骤节点. 1:一审,2:二审
	
	@Column(name = "ORDER_TRADE_STATUS",length=2,scale=0)
	private Integer tradeStatus;	//交易状态(上线后才存在,0交易中 1进入还款 2流标 3满标 4订单还款结束 5 异常 6 死亡

	@Column(name = "PROTYPE_ID",length=50,scale=0)
	private String proTypeId;//产品类型id
	
	 
	@Column(name = "PRODETAIL_ID",length=50,scale=0)
	private String proDetailId;//产品详情ID

	@Column(name = "PRO_NAME_ID",length=50,scale=0)
	private String proNameId;//产品名称id
	
	@Column(name = "CUST_ID",length=50,scale=0)
	private String custId;//客户ID
	
	@Column(name = "CUST_INFO_ID",length=50,scale=0)
	private String custInfoId;//客户详情ID
	
	@Column(name = "CONTRACT_MONEY",length=12,scale=4)
	private Double contractMoney;//合同金额
	
	 
	@Column(name = "CREDIT_MONEY",length=12,scale=4)
	private Double creditMoney;//授信额度
	
	
	@Column(name = "ORDER_NUMBER",length=50,scale=0)
	private String orderNumber;//订单编号
	
	@Column(name = "CREATE_TIME",length=50,scale=0)
	private String createTime;
	
	
	@Column(name = "PROV_ID",length=50,scale=0)
    private String proId;//省份id 
	
	@Column(name = "CITY_ID",length=50,scale=0)
	private String cityId;//chen
	
	@Column(name = "REMARK",length=50,scale=0)
	private String applyRemark;//订单备注
	
	
	@Column(name = "PURPOSE_REMARK",length=50,scale=0)
	private String purposeRemark;//借款用途其它备注
	
	@Column(name = "TRANSACTOR",length=40,scale=0)
	private String transactor;//处理人Id
	
	@Column(name = "PROCEDURES_TYPE_ID",length=40,scale=0)
	private String proceduresTypeId;//流程类型ID
	
	@Column(name = "PROCEDURES_NODE_ID",length=40,scale=0)
	private String proceduresNodeId;//流程节点ID
	
	@Column(name = "REVIEW_RESULT",length=40,scale=0)
	private Integer reviewResult;//评审结果
	
	@Column(name = "BEFOREHAND_CREDIT_MONEY",length=12,scale=4)
	private Double beforehandCreditMoney;
	
	@Column(name = "clearing_channel")
	private String clearingChannel;	// 结算户 1.poc,2boc
	
	@Column(name = "order_prd_number")
	private String orderPrdNumber; // 订单唯一编号
	
	@Column(name = "ONLINE_TYPE")
	private Integer onlineType; // 线上类型
	
	@Column(name = "autoloan_type")
	private Integer autoloanType; //是否为车贷 默认0， 1代表是车贷借款
	
	@Column(name = "autoloan_compliance_status")
	private Integer autoloanComplianceStatus; //车贷 合规状态 进入合规 必须满足3
	

	@Column(name = "highest_money")
	private String highestMoney; // 可接受的最高还款
	
	
	public String getHighestMoney() {
		return highestMoney;
	}

	public void setHighestMoney(String highestMoney) {
		this.highestMoney = highestMoney;
	}

	@Column(name = "internal_review_status")
	private Integer internalReviewStatus; //内审状态,0未进入,1已进入
	
	@Column(name = "ver_internal_review")
	private Integer verInternalReview; //验证内审,0未处理,1有欺诈,2无欺诈
	
	@Column(name = "blacklist_remark")
	private String blacklistRemark;	//黑名单备注
	
	@Column(name = "garylist_remark")
	private String garylistRemark;	//黑名单备注
	
	@Column(name = "internal_review_remak")
	private String internalReviewRemak;	//进入内审备注当internal_review_status字段为1时存在
	
	@Column(name = "cheat_remark")
	private String cheatRemark;	//欺诈备注,
	
	@Column(name = "internal_review_empId")
	private String internalReviewEmpId;	//内审操作人员id
	
	
	
	public String getInternalReviewEmpId() {
		return internalReviewEmpId;
	}

	public void setInternalReviewEmpId(String internalReviewEmpId) {
		this.internalReviewEmpId = internalReviewEmpId;
	}

	public String getCheatRemark() {
		return cheatRemark;
	}

	public void setCheatRemark(String cheatRemark) {
		this.cheatRemark = cheatRemark;
	}

	public Integer getInternalReviewStatus() {
		return internalReviewStatus;
	}

	public void setInternalReviewStatus(Integer internalReviewStatus) {
		this.internalReviewStatus = internalReviewStatus;
	}

	public Integer getVerInternalReview() {
		return verInternalReview;
	}

	public void setVerInternalReview(Integer verInternalReview) {
		this.verInternalReview = verInternalReview;
	}

	public String getBlacklistRemark() {
		return blacklistRemark;
	}

	public void setBlacklistRemark(String blacklistRemark) {
		this.blacklistRemark = blacklistRemark;
	}

	public String getGarylistRemark() {
		return garylistRemark;
	}

	public void setGarylistRemark(String garylistRemark) {
		this.garylistRemark = garylistRemark;
	}

	public String getInternalReviewRemak() {
		return internalReviewRemak;
	}

	public void setInternalReviewRemak(String internalReviewRemak) {
		this.internalReviewRemak = internalReviewRemak;
	}

	public Integer getOnlineType() {
		return onlineType;
	}

	public void setOnlineType(Integer onlineType) {
		this.onlineType = onlineType;
	}

	public String getOrderPrdNumber() {
		return orderPrdNumber;
	}

	public void setOrderPrdNumber(String orderPrdNumber) {
		this.orderPrdNumber = orderPrdNumber;
	}
	
	public String getClearingChannel() {
		return clearingChannel;
	}

	public void setClearingChannel(String clearingChannel) {
		this.clearingChannel = clearingChannel;
	}

	public Double getBeforehandCreditMoney() {
		return beforehandCreditMoney;
	}

	public void setBeforehandCreditMoney(Double beforehandCreditMoney) {
		this.beforehandCreditMoney = beforehandCreditMoney;
	}

	public String getCustInfoId() {
		return custInfoId;
	}

	public void setCustInfoId(String custInfoId) {
		this.custInfoId = custInfoId;
	}

	public Integer getReviewResult() {
		return reviewResult;
	}

	public void setReviewResult(Integer reviewResult) {
		this.reviewResult = reviewResult;
	}

	public String getPurposeRemark() {
		return purposeRemark;
	}

	public void setPurposeRemark(String purposeRemark) {
		this.purposeRemark = purposeRemark;
	}

	public String getProNameId() {
		return proNameId;
	}

	public void setProNameId(String proNameId) {
		this.proNameId = proNameId;
	}

	public String getApplyRemark() {
		return applyRemark;
	}

	public void setApplyRemark(String applyRemark) {
		this.applyRemark = applyRemark;
	}

	public String getPaymentRemark() {
		return paymentRemark;
	}

	public void setPaymentRemark(String paymentRemark) {
		this.paymentRemark = paymentRemark;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
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

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
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

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getMomey() {
		return momey;
	}

	public void setMomey(Double momey) {
		this.momey = momey;
	}

	public Integer getDuetime() {
		return duetime;
	}

	public void setDuetime(Integer duetime) {
		this.duetime = duetime;
	}

	public Double getRepaymentMomey() {
		return repaymentMomey;
	}

	public void setRepaymentMomey(Double repaymentMomey) {
		this.repaymentMomey = repaymentMomey;
	}

	public Integer getPayment() {
		return payment;
	}

	public void setPayment(Integer payment) {
		this.payment = payment;
	}

	 

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getRepaymentStatus() {
		return repaymentStatus;
	}

	public void setRepaymentStatus(Integer repaymentStatus) {
		this.repaymentStatus = repaymentStatus;
	}

	public Integer getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(Integer reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

	public Integer getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(Integer tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public String getTransactor() {
		return transactor;
	}

	public void setTransactor(String transactor) {
		this.transactor = transactor;
	}

	public String getProceduresTypeId() {
		return proceduresTypeId;
	}

	public void setProceduresTypeId(String proceduresTypeId) {
		this.proceduresTypeId = proceduresTypeId;
	}

	public String getProceduresNodeId() {
		return proceduresNodeId;
	}

	public void setProceduresNodeId(String proceduresNodeId) {
		this.proceduresNodeId = proceduresNodeId;
	}

	public Integer getAutoloanType() {
		return autoloanType;
	}

	public void setAutoloanType(Integer autoloanType) {
		this.autoloanType = autoloanType;
	}
	
	public Integer getAutoloanComplianceStatus() {
		return autoloanComplianceStatus;
	}

	public void setAutoloanComplianceStatus(Integer autoloanComplianceStatus) {
		this.autoloanComplianceStatus = autoloanComplianceStatus;
	}

	

	public CrmApplay(String id, Double momey, Integer duetime,
			Double repaymentMomey, Integer payment, String paymentRemark,
			String purpose, Integer status, Integer repaymentStatus,
			Integer reviewStatus, Integer tradeStatus, String proTypeId,
			String proDetailId, String proNameId, String custId,
			String custInfoId, Double contractMoney, Double creditMoney,
			String orderNumber, String createTime, String proId, String cityId,
			String applyRemark, String purposeRemark, String transactor,
			String proceduresTypeId, String proceduresNodeId,
			Integer reviewResult, Double beforehandCreditMoney,
			String clearingChannel, String orderPrdNumber, Integer onlineType,
			Integer autoloanType, Integer autoloanComplianceStatus,
			Integer internalReviewStatus, Integer verInternalReview,
			String blacklistRemark, String garylistRemark,
			String internalReviewRemak) {
		super();
		this.id = id;
		this.momey = momey;
		this.duetime = duetime;
		this.repaymentMomey = repaymentMomey;
		this.payment = payment;
		this.paymentRemark = paymentRemark;
		this.purpose = purpose;
		this.status = status;
		this.repaymentStatus = repaymentStatus;
		this.reviewStatus = reviewStatus;
		this.tradeStatus = tradeStatus;
		this.proTypeId = proTypeId;
		this.proDetailId = proDetailId;
		this.proNameId = proNameId;
		this.custId = custId;
		this.custInfoId = custInfoId;
		this.contractMoney = contractMoney;
		this.creditMoney = creditMoney;
		this.orderNumber = orderNumber;
		this.createTime = createTime;
		this.proId = proId;
		this.cityId = cityId;
		this.applyRemark = applyRemark;
		this.purposeRemark = purposeRemark;
		this.transactor = transactor;
		this.proceduresTypeId = proceduresTypeId;
		this.proceduresNodeId = proceduresNodeId;
		this.reviewResult = reviewResult;
		this.beforehandCreditMoney = beforehandCreditMoney;
		this.clearingChannel = clearingChannel;
		this.orderPrdNumber = orderPrdNumber;
		this.onlineType = onlineType;
		this.autoloanType = autoloanType;
		this.autoloanComplianceStatus = autoloanComplianceStatus;
		this.internalReviewStatus = internalReviewStatus;
		this.verInternalReview = verInternalReview;
		this.blacklistRemark = blacklistRemark;
		this.garylistRemark = garylistRemark;
		this.internalReviewRemak = internalReviewRemak;
	}

	public CrmApplay() {
		super();
	}

}