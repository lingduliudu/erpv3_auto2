package com.apt.webapp.model.crm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "trans_payment_record")
public class TransPaymentRecord implements java.io.Serializable
{
	private static final long serialVersionUID = -5248099990005939414L;
	
	@Id
	@GenericGenerator(name="systemUUID",strategy="uuid")
	@GeneratedValue(generator="systemUUID")
	@Column(name = "ID", unique = true, nullable = false,length=40)
	private String id;
	
	@Column(name = "create_time",length=50)
	private String createTime;
	@Column(name = "answer_time",length=50)
	private String answerTime;
	@Column(name = "trans_way",length=10)
	private String transWay;
	@Column(name = "trans_type",length=10)
	private String transType;
	@Column(name = "trans_from",length=10)
	private String transFrom;
	@Column(name = "trans_req_content",length=8000)
	private String transReqContent;
	@Column(name = "trans_resp_content",length=2000)
	private String transRespContent;
	@Column(name = "data_type",length=10)
	private String dataType;
	@Column(name = "return_code",length=50)
	private String returnCode;
	@Column(name = "return_msg",length=50)
	private String returnMsg;
	@Column(name = "return_time",length=50)
	private String returnTime;
	@Column(name = "trans_no",length=100)
	private String transNo;
	@Column(name = "trans_money",length=50)
	private String transMoney;
	@Column(name = "to_acc_name",length=50)
	private String toAccName;
	@Column(name = "to_acc_no",length=50)
	private String toAccNo;
	@Column(name = "trans_card_id",length=50)
	private String transCardId;
	@Column(name = "trans_mobile",length=50)
	private String transMobile;
	@Column(name = "crm_order_id",length=50)
	private String crmOrderId;
	@Column(name = "trans_batchid",length=50)
	private String transBatchid;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getAnswerTime() {
		return answerTime;
	}
	public void setAnswerTime(String answerTime) {
		this.answerTime = answerTime;
	}
	public String getTransWay() {
		return transWay;
	}
	public void setTransWay(String transWay) {
		this.transWay = transWay;
	}
	public String getTransType() {
		return transType;
	}
	public void setTransType(String transType) {
		this.transType = transType;
	}
	public String getTransFrom() {
		return transFrom;
	}
	public void setTransFrom(String transFrom) {
		this.transFrom = transFrom;
	}
	public String getTransReqContent() {
		return transReqContent;
	}
	public void setTransReqContent(String transReqContent) {
		this.transReqContent = transReqContent;
	}
	public String getTransRespContent() {
		return transRespContent;
	}
	public void setTransRespContent(String transRespContent) {
		this.transRespContent = transRespContent;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public String getReturnMsg() {
		return returnMsg;
	}
	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}
	public String getReturnTime() {
		return returnTime;
	}
	public void setReturnTime(String returnTime) {
		this.returnTime = returnTime;
	}
	public String getTransNo() {
		return transNo;
	}
	public void setTransNo(String transNo) {
		this.transNo = transNo;
	}
	public String getTransMoney() {
		return transMoney;
	}
	public void setTransMoney(String transMoney) {
		this.transMoney = transMoney;
	}
	public String getToAccName() {
		return toAccName;
	}
	public void setToAccName(String toAccName) {
		this.toAccName = toAccName;
	}
	public String getToAccNo() {
		return toAccNo;
	}
	public void setToAccNo(String toAccNo) {
		this.toAccNo = toAccNo;
	}
	public String getTransCardId() {
		return transCardId;
	}
	public void setTransCardId(String transCardId) {
		this.transCardId = transCardId;
	}
	public String getTransMobile() {
		return transMobile;
	}
	public void setTransMobile(String transMobile) {
		this.transMobile = transMobile;
	}
	public String getCrmOrderId() {
		return crmOrderId;
	}
	public void setCrmOrderId(String crmOrderId) {
		this.crmOrderId = crmOrderId;
	}
	public String getTransBatchid() {
		return transBatchid;
	}
	public void setTransBatchid(String transBatchid) {
		this.transBatchid = transBatchid;
	}
}