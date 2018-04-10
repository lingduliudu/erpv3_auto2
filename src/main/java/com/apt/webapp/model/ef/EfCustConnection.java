package com.apt.webapp.model.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

/**
 * 
 * 功能说明：类功能说明 
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明	
 * @author lym
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：请确保实体属性与数据库字段顺序一致，方便浏览查看
 * 创建日期：2015-11-18
 * Copyright zzl-apt
 */
@Entity
@Table(name = "EF_CUST_CONNECTION")
public class EfCustConnection {
	
	public static final Integer CUST_TYPE_EF = 1;	// 理财
	public static final Integer CUST_TYPE_CRM = 2;	// 信贷
	
	public static final Integer CUST_STATUS_NO = 0;		// 停用
	public static final Integer CUST_STATUS_YES = 1;	// 启用
	
	
	@Id
	@GeneratedValue(generator = "uuidGenerator")
    @GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@Column(name = "ID", unique = true, nullable = false,length=40)
	private String id;			//id 标识 客户和业务员关联表
	
	@Column(name = "cust_info_id",length=40)
	private String custInfoId;		//客户ID 关联 bg_customer_info
	
	@Column(name = "employee_id",length=40)
	private String employeeId;	//员工ID
	
	@Column(name = "status",length=2,scale=0)
	private Integer status;		//0：无效 1有效
	
	
	@Column(name = "update_time",length=50)
	private String updateTime;	//更新时间
	
	@Column(name = "create_time",length=50)
	private String createTime;	//创建时间
	
	
	@Column(name = "CUST_TYPE",length=2)
	private Integer custType;// 1:线上贝格  2.线下
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustInfoId() {
		return custInfoId;
	}

	public void setCustInfoId(String custInfoId) {
		this.custInfoId = custInfoId;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}


	public Integer getCustType() {
		return custType;
	}

	public void setCustType(Integer custType) {
		this.custType = custType;
	}

	public EfCustConnection(String id, String custInfoId, String employeeId,
			Integer status, String updateTime, String createTime,Integer custType) {
		super();
		this.id = id;
		this.custInfoId = custInfoId;
		this.employeeId = employeeId;
		this.status = status;
		this.updateTime = updateTime;
		this.createTime = createTime;
		this.custType = custType;
	}

	public EfCustConnection() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
