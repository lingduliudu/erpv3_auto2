package com.apt.webapp.model.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 
 * 功能说明：理财客户申请单
 * weiyingni 
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-26
 */
@Entity
@Table(name = "EF_APPLAY")
public class EfApplay {

	public static final Integer STATUS_STANDBY = 1; // 订单待命中
	public static final Integer STATUS_FINANCE_CONFIRM = 2; // 审批
	public static final Integer STATUS_SUCCESS = 3; // 成功/成交  进入还款
	public static final Integer STATUS_CLOSE_OFF = 4; // 已结清
	
	@Id
	@GeneratedValue(generator = "uuidGenerator")
    @GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@Column(name="ID", unique = true, nullable = false, length=40)
	private String id;	// id标识
	
	@Column(name="CUST_ID", length=40)
	private String custId;	// bg_customer表关联
	
	@Column(name="CUST_INFO_ID", length=40)
	private String custInfoId;	// bg_customer_info 表关联
	
	@Column(name="APPLAY_NUMBER", length=40)
	private String applayNumber;	// 申请单编号编号
	
	@Column(name="CONTRACT_MONEY")
	private Double contrantMoney; 	// 合同金额
	
	@Column(name="CONTRACT_TIME", length=20)
	private String contrantTime;	// 合同签署日期
	
	@Column(name="CONTRACT_REMARK", length=500)
	private String contrantRemark;	// 合同备注
	
	@Column(name="CREATE_TIME", length=20)
	private String createTime;	// 创建时间
	
	@Column(name="EMP_ID", length=40)
	private String empId;	//创建人
	
	@Column(name="UPDATE_TIME", length=20)
	private String updateTime;	//修改时间
	
	@Column(name="STATUS")
	private Integer status;	// 状态   查看上面静态函数
	
	@Column(name="PRO_TYPE_ID", length=40)
	private String proTypeId;	// 产品类型id
	
	@Column(name="PRO_DETAIL_ID", length=40)
	private String proDetailId;	// 产品id

	@Column(name = "EFFECTIVE_TIME",length=40,scale=0)
	private String effectiveTime;	// 理财生效时间(财务确认时间
	
	@Column(name = "TERMINAL_TIME",length=40,scale=0)
	private String terminalTime; // 理财单结束时间
	
	@Column(name = "clearing_channel")
	private String clearingChannel;	// 结算户
	
	@Column(name = "finance_confirmed")
	private String financeConfirmed;	//财务审批状态
	
	@Column(name = "PLATFORM_TYPE")
	private String platfromType;	//订单类型,0为线下,1为贝尔,2为中资联财富
	
	@Column(name = "invest_auz_code")
	private String investAuzCode;	//授权码

	
	
	public String getPlatfromType() {
		return platfromType;
	}

	public void setPlatfromType(String platfromType) {
		this.platfromType = platfromType;
	}

	public String getFinanceConfirmed() {
		return financeConfirmed;
	}

	public void setFinanceConfirmed(String financeConfirmed) {
		this.financeConfirmed = financeConfirmed;
	}

	public String getClearingChannel() {
		return clearingChannel;
	}

	public void setClearingChannel(String clearingChannel) {
		this.clearingChannel = clearingChannel;
	}
	
	public String getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public String getTerminalTime() {
		return terminalTime;
	}

	public void setTerminalTime(String terminalTime) {
		this.terminalTime = terminalTime;
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

	public String getApplayNumber() {
		return applayNumber;
	}

	public void setApplayNumber(String applayNumber) {
		this.applayNumber = applayNumber;
	}

	public Double getContrantMoney() {
		return contrantMoney;
	}

	public void setContrantMoney(Double contrantMoney) {
		this.contrantMoney = contrantMoney;
	}

	public String getContrantTime() {
		return contrantTime;
	}

	public void setContrantTime(String contrantTime) {
		this.contrantTime = contrantTime;
	}

	public String getContrantRemark() {
		return contrantRemark;
	}

	public void setContrantRemark(String contrantRemark) {
		this.contrantRemark = contrantRemark;
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

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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


	public String getInvestAuzCode() {
		return investAuzCode;
	}

	public void setInvestAuzCode(String investAuzCode) {
		this.investAuzCode = investAuzCode;
	}

	public EfApplay() {
		super();
	}

	public EfApplay(String id, String custId, String custInfoId,
			String applayNumber, String contrantNumber, Double contrantMoney,
			String contrantTime, String contrantRemark, String createTime,
			String empId, String updateTime, Integer status, String proTypeId,
			String proDetailId,String investAuzCode) {
		super();
		this.id = id;
		this.custId = custId;
		this.custInfoId = custInfoId;
		this.applayNumber = applayNumber;
		this.contrantMoney = contrantMoney;
		this.contrantTime = contrantTime;
		this.contrantRemark = contrantRemark;
		this.createTime = createTime;
		this.empId = empId;
		this.updateTime = updateTime;
		this.status = status;
		this.proTypeId = proTypeId;
		this.proDetailId = proDetailId;
		this.investAuzCode = investAuzCode;
	}

}
