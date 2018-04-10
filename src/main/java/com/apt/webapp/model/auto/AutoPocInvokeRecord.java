package com.apt.webapp.model.auto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "auto_poc_invoke_record")
public class AutoPocInvokeRecord {
	// 状态
	public final static Integer STATUS_SUCCESS = 1; // 成功
	public final static Integer STATUS_FAIL = 0; // 失败
	@Id
	@GenericGenerator(name = "systemUUID", strategy = "uuid")
	@GeneratedValue(generator = "systemUUID")
	@Column(name = "ID", unique = true, nullable = false, length = 50)
	private String id; // 唯一标识（主键）

	@Column(name = "crm_order_id", length = 50)
	private String crmOrderId; // 信贷明细ID
	@Column(name = "crm_paycontrol_id", length = 50)
	private String crmPaycontrolId; // 信贷明细ID
	@Column(name = "bg_ef_paycontrol_id", length = 50)
	private String bgEfPaycontrolId; // 理财明细ID
	@Column(name = "preauth_contract", length = 50)
	private String preauthContract; // 预授权合同号
	@Column(name = "return_info", length = 200)
	private String returnInfo; // 预授权返回信息
	@Column(name = "return_code", length = 50)
	private String returnCode; // 预授权返回码
	@Column(name = "serial_no", length = 50)
	private String serialNo; // 企业流水号
	@Column(name = "invoke_time", length = 20)
	private String invokeTime; // 调用时间
	@Column(name = "amount")
	private Double amount; // 预授权金额
	@Column(name = "state", length = 1)
	private Integer state; // 执行状态
	@Column(name = "repay_type", length = 1)
	private Integer repayType; // 还款类型  1理财还款预授权 2理财还款划拨  3信贷正常还款代扣  4信贷逾期还款代扣 5理财邀请人预授权 6 理财邀请人划拨
	
	public String getInvokeTime() {
		return invokeTime;
	}
	public void setInvokeTime(String invokeTime) {
		this.invokeTime = invokeTime;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getCrmOrderId() {
		return crmOrderId;
	}
	public void setCrmOrderId(String crmOrderId) {
		this.crmOrderId = crmOrderId;
	}
	public Integer getRepayType() {
		return repayType;
	}
	public void setRepayType(Integer repayType) {
		this.repayType = repayType;
	}
	public String getReturnInfo() {
		return returnInfo;
	}
	public void setReturnInfo(String returnInfo) {
		this.returnInfo = returnInfo;
	}
	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCrmPaycontrolId() {
		return crmPaycontrolId;
	}
	public void setCrmPaycontrolId(String crmPaycontrolId) {
		this.crmPaycontrolId = crmPaycontrolId;
	}
	public String getBgEfPaycontrolId() {
		return bgEfPaycontrolId;
	}
	public void setBgEfPaycontrolId(String bgEfPaycontrolId) {
		this.bgEfPaycontrolId = bgEfPaycontrolId;
	}
	public String getPreauthContract() {
		return preauthContract;
	}
	public void setPreauthContract(String preauthContract) {
		this.preauthContract = preauthContract;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	
	
}
