package com.apt.webapp.model.crm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


/**
 * 功能说明：信贷订单  crm_order
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明
 * @author weiyz
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-13
 * Copyright zzl-apt
 */
@Entity
@Table(name = "CRM_ORDER")
public class CrmOrder {

	@Id
	@GenericGenerator(name="systemUUID",strategy="uuid")
	@GeneratedValue(generator="systemUUID")
	@Column(name = "ID", unique = true, nullable = false,length=50)
	private String id;			// 唯一标识（主键）
	
	@Column(name = "ORDER_NUMBER",length=50)
	private String orderNumber;	// 订单编号
	
	@Column(name = "CUST_ID",length=50)
	private String custId;		// bg_customer
	
	@Column(name = "CUST_INFO_ID",length=50)
	private String custInfoId;	// bg_cust_info
	
	@Column(name = "PRO_TYPE_ID",length=50)
	private String proTypeId;	// 产品类型ID
	
	@Column(name = "PRO_DETAIL_ID",length=50)
	private String proDetailId;	// 产品详情ID
	
    @Column(name = "CONTRACT_MONEY",length=10,scale=4)    // 保留两位小数
	private String contractMoney;	// 合同金额
	
	@Column(name = "CREDIT_MONEY",length=10,scale=4)
	private Double creditMoney;		// 授信额度
	
	@Column(name = "CREATE_TIME",length=20)
	private String createTime;		// 生成时间
	
	@Column(name = "EXPIRE_TIME",length=20)
	private String expireTime;		// 满标时间
	
	@Column(name = "DUETIME_TYPE",length=50)
	private String duetimeType;		// 期数类型（弃用
	
	@Column(name = "REMARK",length=500)
	private String remark;			// 备注
	
	@Column(name = "EMP_ID",length=50)
	private String empId;			// 创建人
	
	
	@Column(name = "SIGN_ID",length=50)
	private String signId;			// 公司ID
	
	@Column(name = "DEPT_ID",length=50)
	private String deptId;			// 部门ID
	
	@Column(name = "FINISH_TIME",length=20)
	private String finishTime;		// 订单结清时间
	
	@Column(name = "PROGRESS",length=5)
	private Integer progress;			// 投资进度
	
	
	@Column(name = "IS_RECOME",length=2)
	private Integer isRecome;			// 是否推荐
	
	@Column(name = "MATURE_TIME",length=20)
	private String matureTime;		// 到期时间
	
	@Column(name = "ONLINE_TIME",length=20)
	private String onlineTime;		// 上线时间
	
	@Column(name = "ONLINE_TYPE",length=2)
	private Integer onlineType;		// 订单线上，还是线下

	@Column(name = "crm_applay_id")
	private String crmApplayId;		// 关联的申请单ID
	
	@Column(name = "order_trade_status")
	private Integer orderTradeStatus;	// 订单状态
	
	@Column(name = "has_invest_amt")
	private Double hasInvestAmt;	// 已投金额
	
	@Column(name = "order_prd_number")
	private String orderPrdNumber;	// 产品编号
	
	@Column(name = "ef_prd_type_id")
	private String efPrdTypeId;		// 贝格产品类型编号
	
	@Column(name = "ef_prd_detail_id")
	private String efPrdDetailId;	// 贝格产品详细的ID
	
	@Column(name = "clearing_channel")
	private String clearingChannel;	// 结算户
	
	@Column(name = "clear_type")
	private Integer clearType;

	
	public String getCrmApplayId() {
		return crmApplayId;
	}

	public void setCrmApplayId(String crmApplayId) {
		this.crmApplayId = crmApplayId;
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

	public void setHasInvestAmt(double hasInvestAmt) {
		this.hasInvestAmt = hasInvestAmt;
	}

	public String getOrderPrdNumber() {
		return orderPrdNumber;
	}

	public void setOrderPrdNumber(String orderPrdNumber) {
		this.orderPrdNumber = orderPrdNumber;
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

	public String getContractMoney() {
		return contractMoney;
	}

	public void setContractMoney(String contractMoney) {
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

	public String getDuetimeType() {
		return duetimeType;
	}

	public void setDuetimeType(String duetimeType) {
		this.duetimeType = duetimeType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getSignId() {
		return signId;
	}

	public void setSignId(String signId) {
		this.signId = signId;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}


	public Integer getIsRecome() {
		return isRecome;
	}

	public void setIsRecome(Integer isRecome) {
		this.isRecome = isRecome;
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

	public Integer getOnlineType() {
		return onlineType;
	}

	public void setOnlineType(Integer onlineType) {
		this.onlineType = onlineType;
	}

	public Integer getClearType() {
		return clearType;
	}

	public void setClearType(Integer clearType) {
		this.clearType = clearType;
	}

	public CrmOrder(String id, String orderNumber, String custId,
			String custInfoId, String proTypeId, String proDetailId,
			String contractMoney, Double creditMoney, String createTime,
			String expireTime, String duetimeType, String remark, String empId,
			String signId, String deptId, String finishTime, Integer progress,
			Integer isRecome, String matureTime, String onlineTime,
			Integer onlineType, String crmApplayId, Integer orderTradeStatus,
			Double hasInvestAmt, String orderPrdNumber, String efPrdTypeId,
			String efPrdDetailId, String clearingChannel, Integer clearType) {
		super();
		this.id = id;
		this.orderNumber = orderNumber;
		this.custId = custId;
		this.custInfoId = custInfoId;
		this.proTypeId = proTypeId;
		this.proDetailId = proDetailId;
		this.contractMoney = contractMoney;
		this.creditMoney = creditMoney;
		this.createTime = createTime;
		this.expireTime = expireTime;
		this.duetimeType = duetimeType;
		this.remark = remark;
		this.empId = empId;
		this.signId = signId;
		this.deptId = deptId;
		this.finishTime = finishTime;
		this.progress = progress;
		this.isRecome = isRecome;
		this.matureTime = matureTime;
		this.onlineTime = onlineTime;
		this.onlineType = onlineType;
		this.crmApplayId = crmApplayId;
		this.orderTradeStatus = orderTradeStatus;
		this.hasInvestAmt = hasInvestAmt;
		this.orderPrdNumber = orderPrdNumber;
		this.efPrdTypeId = efPrdTypeId;
		this.efPrdDetailId = efPrdDetailId;
		this.clearingChannel = clearingChannel;
		this.clearType = clearType;
	}

	public CrmOrder() {
		super();
	}

	
	
}

















