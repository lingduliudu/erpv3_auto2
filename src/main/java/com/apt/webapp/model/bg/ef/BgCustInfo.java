package com.apt.webapp.model.bg.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * BgCustInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "bg_cust_info")
public class BgCustInfo implements java.io.Serializable {

	// Fields

	private String id;
	private String bakMobile;
	private String birthday;
	private Integer sex;
	private String custName;
	private String custIc;
	private String custMobile;
	private Integer emailVerify;
	private String efCustInfoId;
	private String crmCustInfoId;
	private Integer realnameVerify;
	private Integer mobileVerify;
	private Integer bankVerify;
	private Integer questionsVerify;
	private Integer fyVerify;
	private String bankAccount;
	private String fyAccount;
	private String createTime;
	private String updateTime;
	private Integer caVerify;
	private String caAccount;
	private Integer accountGeneralVerify;
	private Integer withdrawPwStatus;
	private String accountGeneralAccount;
	private String clearingChannel;
	private String reservedDomain;
	private String bindCardMobile;
	private String investorType;

	// Constructors

	/** default constructor */
	public BgCustInfo() {
	}

	/** full constructor */
	public BgCustInfo(String bakMobile, String birthday, Integer sex,
			String custName, String custIc, String custMobile,
			Integer emailVerify, String efCustInfoId, String crmCustInfoId,
			Integer realnameVerify, Integer mobileVerify, Integer bankVerify,
			Integer questionsVerify, Integer fyVerify, String bankAccount,
			String fyAccount, String createTime, String updateTime,
			Integer caVerify, String caAccount, Integer accountGeneralVerify,
			Integer withdrawPwStatus, String accountGeneralAccount,
			String clearingChannel, String reservedDomain, String bindCardMobile,String investorType) {
		this.bakMobile = bakMobile;
		this.birthday = birthday;
		this.sex = sex;
		this.custName = custName;
		this.custIc = custIc;
		this.custMobile = custMobile;
		this.emailVerify = emailVerify;
		this.efCustInfoId = efCustInfoId;
		this.crmCustInfoId = crmCustInfoId;
		this.realnameVerify = realnameVerify;
		this.mobileVerify = mobileVerify;
		this.bankVerify = bankVerify;
		this.questionsVerify = questionsVerify;
		this.fyVerify = fyVerify;
		this.bankAccount = bankAccount;
		this.fyAccount = fyAccount;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.caVerify = caVerify;
		this.caAccount = caAccount;
		this.accountGeneralVerify = accountGeneralVerify;
		this.withdrawPwStatus = withdrawPwStatus;
		this.accountGeneralAccount = accountGeneralAccount;
		this.clearingChannel = clearingChannel;
		this.reservedDomain = reservedDomain;
		this.bindCardMobile = bindCardMobile;
		this.investorType = investorType;
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

	@Column(name = "bak_mobile", length = 20)
	public String getBakMobile() {
		return this.bakMobile;
	}

	public void setBakMobile(String bakMobile) {
		this.bakMobile = bakMobile;
	}

	@Column(name = "birthday", length = 20)
	public String getBirthday() {
		return this.birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	@Column(name = "sex")
	public Integer getSex() {
		return this.sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	@Column(name = "cust_name", length = 50)
	public String getCustName() {
		return this.custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	@Column(name = "cust_ic", length = 18)
	public String getCustIc() {
		return this.custIc;
	}

	public void setCustIc(String custIc) {
		this.custIc = custIc;
	}

	@Column(name = "cust_mobile", length = 11)
	public String getCustMobile() {
		return this.custMobile;
	}

	public void setCustMobile(String custMobile) {
		this.custMobile = custMobile;
	}

	@Column(name = "email_verify")
	public Integer getEmailVerify() {
		return this.emailVerify;
	}

	public void setEmailVerify(Integer emailVerify) {
		this.emailVerify = emailVerify;
	}

	@Column(name = "ef_cust_info_id", length = 40)
	public String getEfCustInfoId() {
		return this.efCustInfoId;
	}

	public void setEfCustInfoId(String efCustInfoId) {
		this.efCustInfoId = efCustInfoId;
	}

	@Column(name = "crm_cust_info_id", length = 40)
	public String getCrmCustInfoId() {
		return this.crmCustInfoId;
	}

	public void setCrmCustInfoId(String crmCustInfoId) {
		this.crmCustInfoId = crmCustInfoId;
	}

	@Column(name = "realname_verify")
	public Integer getRealnameVerify() {
		return this.realnameVerify;
	}

	public void setRealnameVerify(Integer realnameVerify) {
		this.realnameVerify = realnameVerify;
	}

	@Column(name = "mobile_verify")
	public Integer getMobileVerify() {
		return this.mobileVerify;
	}

	public void setMobileVerify(Integer mobileVerify) {
		this.mobileVerify = mobileVerify;
	}

	@Column(name = "bank_verify")
	public Integer getBankVerify() {
		return this.bankVerify;
	}

	public void setBankVerify(Integer bankVerify) {
		this.bankVerify = bankVerify;
	}

	@Column(name = "questions_verify")
	public Integer getQuestionsVerify() {
		return this.questionsVerify;
	}

	public void setQuestionsVerify(Integer questionsVerify) {
		this.questionsVerify = questionsVerify;
	}

	@Column(name = "fy_verify")
	public Integer getFyVerify() {
		return this.fyVerify;
	}

	public void setFyVerify(Integer fyVerify) {
		this.fyVerify = fyVerify;
	}

	@Column(name = "bank_account", length = 50)
	public String getBankAccount() {
		return this.bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	@Column(name = "fy_account", length = 20)
	public String getFyAccount() {
		return this.fyAccount;
	}

	public void setFyAccount(String fyAccount) {
		this.fyAccount = fyAccount;
	}

	@Column(name = "create_time", length = 20)
	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Column(name = "update_time", length = 20)
	public String getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "CA_VERIFY")
	public Integer getCaVerify() {
		return this.caVerify;
	}

	public void setCaVerify(Integer caVerify) {
		this.caVerify = caVerify;
	}

	@Column(name = "CA_ACCOUNT", length = 50)
	public String getCaAccount() {
		return this.caAccount;
	}

	public void setCaAccount(String caAccount) {
		this.caAccount = caAccount;
	}

	@Column(name = "ACCOUNT_GENERAL_VERIFY")
	public Integer getAccountGeneralVerify() {
		return this.accountGeneralVerify;
	}

	public void setAccountGeneralVerify(Integer accountGeneralVerify) {
		this.accountGeneralVerify = accountGeneralVerify;
	}

	@Column(name = "withdraw_pw_status")
	public Integer getWithdrawPwStatus() {
		return this.withdrawPwStatus;
	}

	public void setWithdrawPwStatus(Integer withdrawPwStatus) {
		this.withdrawPwStatus = withdrawPwStatus;
	}

	@Column(name = "ACCOUNT_GENERAL_ACCOUNT", length = 50)
	public String getAccountGeneralAccount() {
		return this.accountGeneralAccount;
	}

	public void setAccountGeneralAccount(String accountGeneralAccount) {
		this.accountGeneralAccount = accountGeneralAccount;
	}

	@Column(name = "clearing_channel", length = 60)
	public String getClearingChannel() {
		return this.clearingChannel;
	}

	public void setClearingChannel(String clearingChannel) {
		this.clearingChannel = clearingChannel;
	}

	@Column(name = "RESERVED_DOMAIN", length = 20)
	public String getReservedDomain() {
		return this.reservedDomain;
	}

	public void setReservedDomain(String reservedDomain) {
		this.reservedDomain = reservedDomain;
	}

	@Column(name = "bind_card_mobile", length = 20)
	public String getBindCardMobile() {
		return this.bindCardMobile;
	}

	public void setBindCardMobile(String bindCardMobile) {
		this.bindCardMobile = bindCardMobile;
	}
	@Column(name = "investor_type")
	public String getInvestorType() {
		return this.investorType;
	}
	
	public void setInvestorType(String investorType) {
		this.investorType = investorType;
	}

}