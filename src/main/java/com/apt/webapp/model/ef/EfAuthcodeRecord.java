package com.apt.webapp.model.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 功能说明：理财授权码
 * 创建人：yuanhao
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-10
 */
@Entity
@Table(name = "ef_authcode_record")
public class EfAuthcodeRecord {
	
	@Id
	@GenericGenerator(name="systemUUID",strategy="uuid")
	@GeneratedValue(generator="systemUUID")
	@Column(name = "id", unique = true, nullable = false,length=40)
	private String id;						//ID 标识 理财订单表
	
	@Column(name = "ef_order_id",length=40)
	private String efOrderId;
	
	@Column(name = "create_time",length=40)
	private String createTime;
	
	@Column(name = "auth_code",length=40)
	private String authCode;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEfOrderId() {
		return efOrderId;
	}

	public void setEfOrderId(String efOrderId) {
		this.efOrderId = efOrderId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public EfAuthcodeRecord(String id, String efOrderId, String createTime,
			String authCode) {
		super();
		this.id = id;
		this.efOrderId = efOrderId;
		this.createTime = createTime;
		this.authCode = authCode;
	}

	public EfAuthcodeRecord() {
		super();
	}
	
	
}
