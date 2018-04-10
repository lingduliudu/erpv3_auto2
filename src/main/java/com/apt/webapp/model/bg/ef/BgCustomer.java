package com.apt.webapp.model.bg.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * BgCustomer entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "bg_customer")
public class BgCustomer implements java.io.Serializable {

	// Fields

	private String id;
	private String createTime;
	private String custEmail;
	private String custIc;
	private String custInfoId;
	private String custName;
	private Integer custSource;
	private Integer custType;
	private String lastLoginIp;
	private String lastLoginTime;
	private String mobile;
	private String pawword;
	private String regIp;
	private String score;
	private Integer status;
	private String username;
	private String remark;
	private String creditLevel;
	private Integer loginNum;
	private Integer onlineLogin;
	private String updateTime;

	// Constructors

	/** default constructor */
	public BgCustomer() {
	}

	/** full constructor */
	public BgCustomer(String createTime, String custEmail, String custIc,
			String custInfoId, String custName, Integer custSource,
			Integer custType, String lastLoginIp, String lastLoginTime,
			String mobile, String pawword, String regIp, String score,
			Integer status, String username, String remark, String creditLevel,
			Integer loginNum, Integer onlineLogin, String updateTime) {
		this.createTime = createTime;
		this.custEmail = custEmail;
		this.custIc = custIc;
		this.custInfoId = custInfoId;
		this.custName = custName;
		this.custSource = custSource;
		this.custType = custType;
		this.lastLoginIp = lastLoginIp;
		this.lastLoginTime = lastLoginTime;
		this.mobile = mobile;
		this.pawword = pawword;
		this.regIp = regIp;
		this.score = score;
		this.status = status;
		this.username = username;
		this.remark = remark;
		this.creditLevel = creditLevel;
		this.loginNum = loginNum;
		this.onlineLogin = onlineLogin;
		this.updateTime = updateTime;
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

	@Column(name = "create_time", length = 50)
	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Column(name = "cust_email", length = 50)
	public String getCustEmail() {
		return this.custEmail;
	}

	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}

	@Column(name = "cust_ic", length = 20)
	public String getCustIc() {
		return this.custIc;
	}

	public void setCustIc(String custIc) {
		this.custIc = custIc;
	}

	@Column(name = "cust_info_id", length = 40)
	public String getCustInfoId() {
		return this.custInfoId;
	}

	public void setCustInfoId(String custInfoId) {
		this.custInfoId = custInfoId;
	}

	@Column(name = "cust_name", length = 30)
	public String getCustName() {
		return this.custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	@Column(name = "cust_source")
	public Integer getCustSource() {
		return this.custSource;
	}

	public void setCustSource(Integer custSource) {
		this.custSource = custSource;
	}

	@Column(name = "cust_type")
	public Integer getCustType() {
		return this.custType;
	}

	public void setCustType(Integer custType) {
		this.custType = custType;
	}

	@Column(name = "last_login_ip", length = 20)
	public String getLastLoginIp() {
		return this.lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	@Column(name = "last_login_time", length = 50)
	public String getLastLoginTime() {
		return this.lastLoginTime;
	}

	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	@Column(name = "mobile", length = 20)
	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name = "pawword", length = 50)
	public String getPawword() {
		return this.pawword;
	}

	public void setPawword(String pawword) {
		this.pawword = pawword;
	}

	@Column(name = "reg_ip", length = 50)
	public String getRegIp() {
		return this.regIp;
	}

	public void setRegIp(String regIp) {
		this.regIp = regIp;
	}

	@Column(name = "score", length = 50)
	public String getScore() {
		return this.score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "username", length = 50)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "remark", length = 500)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "credit_level", length = 40)
	public String getCreditLevel() {
		return this.creditLevel;
	}

	public void setCreditLevel(String creditLevel) {
		this.creditLevel = creditLevel;
	}

	@Column(name = "login_num")
	public Integer getLoginNum() {
		return this.loginNum;
	}

	public void setLoginNum(Integer loginNum) {
		this.loginNum = loginNum;
	}

	@Column(name = "online_login")
	public Integer getOnlineLogin() {
		return this.onlineLogin;
	}

	public void setOnlineLogin(Integer onlineLogin) {
		this.onlineLogin = onlineLogin;
	}

	@Column(name = "update_time", length = 50)
	public String getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

}