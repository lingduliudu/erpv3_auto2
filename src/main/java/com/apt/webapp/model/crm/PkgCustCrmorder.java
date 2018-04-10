package com.apt.webapp.model.crm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 资产包订单信息
 * @author yuanhao 2016-8-6
 *
 */
@Entity
@Table(name = "pkg_cust_crmorder")
public class PkgCustCrmorder {
	
	@Id
	@GenericGenerator(name="systemUUID",strategy="uuid")
	@GeneratedValue(generator="systemUUID")
	@Column(name = "ID", unique = true, nullable = false,length=40)
	private String id;
	
	@Column(name = "crm_order_id",length=50)
	private String crmOrderId;		//信贷ID
	
	@Column(name = "bg_ef_order_id",length=20)
	private String bgEfOrderId;	// 理财ID
	
	@Column(name = "pkg_order_id",length=50)
	private String pkgOrderId;		// 资产包id
	
	@Column(name = "cust_info_id",length=14,scale=4)
	private String custInfoId; //bg_cust_info id
	
	@Column(name = "cust_id",length=14,scale=4)
	private String custId; //cust_info_id
	
	@Column(name = "money",length=20)
	private Double money;	// 债权金额 或者直投的合同金额
	
	@Column(name = "be_match_money")
	private Double beMatchMoney;	// 待匹配的金额
	
	@Column(name = "status",length=20)
	private String status;	// 关联关系状态
	
	@Column(name = "order_type",length=20)
	private String orderType;	// 关联类型

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCrmOrderId() {
		return crmOrderId;
	}

	public void setCrmOrderId(String crmOrderId) {
		this.crmOrderId = crmOrderId;
	}

	public String getBgEfOrderId() {
		return bgEfOrderId;
	}

	public void setBgEfOrderId(String bgEfOrderId) {
		this.bgEfOrderId = bgEfOrderId;
	}

	public String getPkgOrderId() {
		return pkgOrderId;
	}

	public void setPkgOrderId(String pkgOrderId) {
		this.pkgOrderId = pkgOrderId;
	}

	

	public String getCustInfoId() {
		return custInfoId;
	}

	public void setCustInfoId(String custInfoId) {
		this.custInfoId = custInfoId;
	}

	

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public Double getBeMatchMoney() {
		return beMatchMoney;
	}

	public void setBeMatchMoney(Double beMatchMoney) {
		this.beMatchMoney = beMatchMoney;
	}

	
	public PkgCustCrmorder(String id, String crmOrderId, String bgEfOrderId,
			String pkgOrderId, String custInfoId, String custId, Double money,
			Double beMatchMoney, String status, String orderType) {
		super();
		this.id = id;
		this.crmOrderId = crmOrderId;
		this.bgEfOrderId = bgEfOrderId;
		this.pkgOrderId = pkgOrderId;
		this.custInfoId = custInfoId;
		this.custId = custId;
		this.money = money;
		this.beMatchMoney = beMatchMoney;
		this.status = status;
		this.orderType = orderType;
	}

	public PkgCustCrmorder() {
	}
}
