package com.apt.webapp.model.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 理财资金记录表
 * 功能说明：类功能说明 
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明	
 * @author sky
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-12-10
 * Copyright zzl-apt
 */

@Entity
@Table(name = "EF_FUND_RECORD")
public class EfFundRecord {
	
	public static final int RECORD_STATUS_YES = 1; // 有效
	public static final int RECORD_STATUS_NO = 0;	// 无效
	
	@Id
	@GeneratedValue(generator = "sequenceGenerator")
    @GenericGenerator(name = "sequenceGenerator", strategy = "uuid")
	@Column(name = "ID", unique = true, nullable = false,length=40)
	private String id;		
	
	@Column(name="money")
	private double money;		// 金额
	
	@Column(name="record_type")
	private String recordType;	// 记录类型
	
	@Column(name="record_status")
	private int recordStatus;	// 状态
	
	@Column(name="cust_info_id")
	private String custInfoId;	// bg_cust_inf
	
	@Column(name="cust_id")
	private String custId;		// bg_customer
	
	@Column(name="ef_applay_id")
	private String efApplayId;	// ef_applay_id
	
	@Column(name="create_time")
	private String createTime;	// 创建时间
	
	@Column(name="emp_id")
	private String empId;		// 创建人
	
	@Column(name="ef_order_id")
	private String efOrderId;	// 理财订单ID
	@Column(name="bg_ef_order_id")
	private String bgEfOrderId; // 对应的匹配理财单
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public int getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(int recordStatus) {
		this.recordStatus = recordStatus;
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

	public String getEfApplayId() {
		return efApplayId;
	}

	public void setEfApplayId(String efApplayId) {
		this.efApplayId = efApplayId;
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

	public String getEfOrderId() {
		return efOrderId;
	}

	public void setEfOrderId(String efOrderId) {
		this.efOrderId = efOrderId;
	}

	public String getBgEfOrderId() {
		return bgEfOrderId;
	}

	public void setBgEfOrderId(String bgEfOrderId) {
		this.bgEfOrderId = bgEfOrderId;
	}

	public EfFundRecord(String id, double money, String recordType,
			int recordStatus, String custInfoId, String custId,
			String efApplayId, String createTime, String empId, String efOrderId,String bgEfOrderId) {
		super();
		this.id = id;
		this.money = money;
		this.recordType = recordType;
		this.recordStatus = recordStatus;
		this.custInfoId = custInfoId;
		this.custId = custId;
		this.efApplayId = efApplayId;
		this.createTime = createTime;
		this.empId = empId;
		this.efOrderId = efOrderId;
		this.bgEfOrderId = bgEfOrderId;
	}

	public EfFundRecord() {
		super();
	}
	
	
}
