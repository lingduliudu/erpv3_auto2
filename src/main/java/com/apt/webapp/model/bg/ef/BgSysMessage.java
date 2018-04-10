package com.apt.webapp.model.bg.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * BgSysMessage entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "bg_sys_message")
public class BgSysMessage implements java.io.Serializable {

	// Fields

	private String id;
	private String custId;
	private String efOrderId;
	private Integer enabled;
	private Integer msgClass;
	private String msgContentId;
	private Integer msgType;
	private String orderId;
	private Integer readStatus;
	private Integer platformType;
	

	// Constructors

	/** default constructor */
	public BgSysMessage() {
	}

	/** full constructor 
	 * @param platformType */
	public BgSysMessage(String custId, String efOrderId, Integer enabled,
			Integer msgClass, String msgContentId, Integer msgType,
			String orderId, Integer readStatus, Integer platformType) {
		this.custId = custId;
		this.efOrderId = efOrderId;
		this.enabled = enabled;
		this.msgClass = msgClass;
		this.msgContentId = msgContentId;
		this.msgType = msgType;
		this.orderId = orderId;
		this.readStatus = readStatus;
		this.platformType = platformType;
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

	@Column(name = "ef_order_id", length = 40)
	public String getEfOrderId() {
		return this.efOrderId;
	}

	public void setEfOrderId(String efOrderId) {
		this.efOrderId = efOrderId;
	}

	@Column(name = "enabled")
	public Integer getEnabled() {
		return this.enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	@Column(name = "msg_class")
	public Integer getMsgClass() {
		return this.msgClass;
	}

	public void setMsgClass(Integer msgClass) {
		this.msgClass = msgClass;
	}

	@Column(name = "msg_content_id", length = 40)
	public String getMsgContentId() {
		return this.msgContentId;
	}

	public void setMsgContentId(String msgContentId) {
		this.msgContentId = msgContentId;
	}

	@Column(name = "msg_type")
	public Integer getMsgType() {
		return this.msgType;
	}

	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}

	@Column(name = "order_id", length = 40)
	public String getOrderId() {
		return this.orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@Column(name = "read_status")
	public Integer getReadStatus() {
		return this.readStatus;
	}

	public void setReadStatus(Integer readStatus) {
		this.readStatus = readStatus;
	}
	
	@Column(name = "platform_type")
	public Integer getPlatformType() {
		return platformType;
	}

	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}
	

}