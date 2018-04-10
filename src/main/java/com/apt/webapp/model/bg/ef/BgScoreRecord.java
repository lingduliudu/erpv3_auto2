package com.apt.webapp.model.bg.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * BgScoreRecord entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "bg_score_record")
public class BgScoreRecord implements java.io.Serializable {

	// Fields

	private String id;
	private String custId;
	private String score;
	private String incomeSource;
	private String createTime;
	private String updateTime;
	private String realTimeScore;
	private String efOrderId;

	// Constructors

	/** default constructor */
	public BgScoreRecord() {
	}

	/** full constructor */
	public BgScoreRecord(String custId, String score, String incomeSource,
			String createTime, String updateTime,String realTimeScore,String efOrderId) {
		this.custId = custId;
		this.score = score;
		this.incomeSource = incomeSource;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.realTimeScore = realTimeScore;
		this.efOrderId = efOrderId;
	}

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

	@Column(name = "cust_id", length = 40)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "score", length = 100)
	public String getScore() {
		return this.score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	@Column(name = "income_source", length = 50)
	public String getIncomeSource() {
		return this.incomeSource;
	}

	public void setIncomeSource(String incomeSource) {
		this.incomeSource = incomeSource;
	}

	@Column(name = "create_time", length = 50)
	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Column(name = "update_time", length = 50)
	public String getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "real_time_score", length = 50)
	public String getRealTimeScore() {
		return realTimeScore;
	}

	public void setRealTimeScore(String realTimeScore) {
		this.realTimeScore = realTimeScore;
	}
	@Column(name = "ef_order_id", length = 50)
	public String getEfOrderId() {
		return efOrderId;
	}

	public void setEfOrderId(String efOrderId) {
		this.efOrderId = efOrderId;
	}
	
	
}