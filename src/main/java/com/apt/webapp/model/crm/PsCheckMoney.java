package com.apt.webapp.model.crm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * PsCheckMoney entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "ps_check_money")
public class PsCheckMoney implements java.io.Serializable {

	// Fields

	private String id;
	private String custInfoId;
	private String createTime;
	private String operationType;
	private String type;
	private String personType;
	private String status;
	private String operator;
	private Double money;
	private String moneyType;
	private String cardNo;
	private String crmOrderId;

	// Constructors

	/** default constructor */
	public PsCheckMoney() {
	}

	/** full constructor */
	public PsCheckMoney(String custInfoId, String createTime,
			String operationType, String type, String personType,
			String status, String operator, Double money, String moneyType,
			String cardNo,String crmOrderId) {
		this.custInfoId = custInfoId;
		this.createTime = createTime;
		this.operationType = operationType;
		this.type = type;
		this.personType = personType;
		this.status = status;
		this.operator = operator;
		this.money = money;
		this.moneyType = moneyType;
		this.cardNo = cardNo;
		this.crmOrderId = crmOrderId;
	}

	// Property accessors
	@Id
	@GenericGenerator(name="systemUUID",strategy="uuid")
	@GeneratedValue(generator="systemUUID")
	@Column(name = "id", unique = true, nullable = false, length = 50)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "cust_info_id")
	public String getCustInfoId() {
		return this.custInfoId;
	}

	public void setCustInfoId(String custInfoId) {
		this.custInfoId = custInfoId;
	}

	@Column(name = "create_time")
	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Column(name = "operationType")
	public String getOperationType() {
		return this.operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	@Column(name = "type")
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	@Column(name = "crm_order_id")
	public String getCrmOrderId() {
		return this.crmOrderId;
	}
	
	public void setCrmOrderId(String crmOrderId) {
		this.crmOrderId = crmOrderId;
	}

	@Column(name = "personType")
	public String getPersonType() {
		return this.personType;
	}

	public void setPersonType(String personType) {
		this.personType = personType;
	}

	@Column(name = "status")
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "operator")
	public String getOperator() {
		return this.operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Column(name = "money", precision = 14)
	public Double getMoney() {
		return this.money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	@Column(name = "moneyType")
	public String getMoneyType() {
		return this.moneyType;
	}

	public void setMoneyType(String moneyType) {
		this.moneyType = moneyType;
	}

	@Column(name = "cardNo")
	public String getCardNo() {
		return this.cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

}