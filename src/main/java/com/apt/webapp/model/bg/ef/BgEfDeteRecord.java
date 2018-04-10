package com.apt.webapp.model.bg.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

/**
 * 功能说明：定投理财还款记录表
 * 创建人：yuanhao
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-10
 */
@Entity
@Table(name = "bg_ef_dete_record")
public class BgEfDeteRecord {
	
	
	@Id
	@GenericGenerator(name="systemUUID",strategy="uuid")
	@GeneratedValue(generator="systemUUID")
	@Column(name = "id", unique = true, nullable = false,length=60)
	private String id;						//ID 标识 理财订单表
	
	@Column(name = "crm_order_id")
	private String crmOrderId;					//crm_order ID
	
	@Column(name = "crm_paycontrol_id")
	private String crmPaycontrolId;				//crmPaycontrolId ID
	
	@Column(name = "create_time")
	private String createTime;					//创建时间
	
	@Column(name = "type")
	private String type;				 		 //类型
	
	@Column(name = "status")
	private String status;						//状态  0 未处理  1 已处理
	
	@Column(name = "serino")
	private String serino;						//批处理流水号
	
	@Column(name = "empid")
	private String empid;						//操作人
	
	@Column(name = "update_time")
	private String updateTime;					//更新时间

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCrmOrderId() {
		return crmOrderId;
	}

	public void setCrmOrderId(String crmOrderId) {
		this.crmOrderId = crmOrderId;
	}

	public String getCrmPaycontrolId() {
		return crmPaycontrolId;
	}

	public void setCrmPaycontrolId(String crmPaycontrolId) {
		this.crmPaycontrolId = crmPaycontrolId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	public String getSerino() {
		return serino;
	}

	public void setSerino(String serino) {
		this.serino = serino;
	}

	
	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
	}

	

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}


	public BgEfDeteRecord(String id, String crmOrderId, String crmPaycontrolId,
			String createTime, String type, String status, String serino,
			String empid, String updateTime) {
		super();
		this.id = id;
		this.crmOrderId = crmOrderId;
		this.crmPaycontrolId = crmPaycontrolId;
		this.createTime = createTime;
		this.type = type;
		this.status = status;
		this.serino = serino;
		this.empid = empid;
		this.updateTime = updateTime;
	}

	public BgEfDeteRecord() {
		super();
	}
	
	
}
