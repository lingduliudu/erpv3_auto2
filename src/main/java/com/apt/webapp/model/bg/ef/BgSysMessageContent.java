package com.apt.webapp.model.bg.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * BgSysMessageContent entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "bg_sys_message_content")
public class BgSysMessageContent implements java.io.Serializable {

	// Fields

	private String id;
	private String createTime;
	private String msgContent;
	private String msgTitle;
	private String publishId;
	private String updateTime;
	private Integer platformType;

	// Constructors

	/** default constructor */
	public BgSysMessageContent() {
	}

	/** full constructor */
	public BgSysMessageContent(String createTime, String msgContent,
			String msgTitle, String publishId, String updateTime,Integer platformType) {
		this.createTime = createTime;
		this.msgContent = msgContent;
		this.msgTitle = msgTitle;
		this.publishId = publishId;
		this.updateTime = updateTime;
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

	@Column(name = "create_time", length = 50)
	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Column(name = "msg_content", length = 2000)
	public String getMsgContent() {
		return this.msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	@Column(name = "msg_title", length = 50)
	public String getMsgTitle() {
		return this.msgTitle;
	}

	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}

	@Column(name = "publish_id", length = 40)
	public String getPublishId() {
		return this.publishId;
	}

	public void setPublishId(String publishId) {
		this.publishId = publishId;
	}

	@Column(name = "update_time", length = 50)
	public String getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	@Column(name = "platform_type")
	public Integer getPlatformType() {
		return platformType;
	}

	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}

}