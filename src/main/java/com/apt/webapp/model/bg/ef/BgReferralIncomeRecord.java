package com.apt.webapp.model.bg.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "bg_referral_income_record")
public class BgReferralIncomeRecord implements java.io.Serializable {

	// Fields

	private String id;
	private String refereeInfoId;
	private String incomeSource;
	private Double referralIncome;
	private String updateTime;
	private String createTime;
	private String custInfoId;

	// Constructors

	/** default constructor */
	public BgReferralIncomeRecord() {
	}

	/** minimal constructor */
	public BgReferralIncomeRecord(String id) {
		this.id = id;
	}

	/** full constructor */
	public BgReferralIncomeRecord(String id, String refereeInfoId,
			String incomeSource, Double referralIncome, String updateTime,
			String createTime, String custInfoId) {
		this.id = id;
		this.refereeInfoId = refereeInfoId;
		this.incomeSource = incomeSource;
		this.referralIncome = referralIncome;
		this.updateTime = updateTime;
		this.createTime = createTime;
		this.custInfoId = custInfoId;
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

	@Column(name = "referee_info_id", length = 50)
	public String getRefereeInfoId() {
		return this.refereeInfoId;
	}

	public void setRefereeInfoId(String refereeInfoId) {
		this.refereeInfoId = refereeInfoId;
	}

	@Column(name = "income_source", length = 50)
	public String getIncomeSource() {
		return this.incomeSource;
	}

	public void setIncomeSource(String incomeSource) {
		this.incomeSource = incomeSource;
	}

	@Column(name = "referral_income", precision = 14)
	public Double getReferralIncome() {
		return this.referralIncome;
	}

	public void setReferralIncome(Double referralIncome) {
		this.referralIncome = referralIncome;
	}

	@Column(name = "update_time", length = 20)
	public String getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "create_time", length = 20)
	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Column(name = "cust_info_id", length = 50)
	public String getCustInfoId() {
		return this.custInfoId;
	}

	public void setCustInfoId(String custInfoId) {
		this.custInfoId = custInfoId;
	}


}