package com.apt.webapp.model.bg.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


/**
 * 功能说明：理财类型/产品表实体表
 * 乔春峰
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015/12/7
 */
@Entity
@Table(name = "BG_EF_PRODUCT")
public class BgEfProduct {
	
	// 状态
	public final static Integer STATUS_YES= 1;				// 启用
	public final static Integer STATUS_NO = 0;				// 禁用
	@Id
	@GenericGenerator(name="systemUUID",strategy="uuid")
	@GeneratedValue(generator="systemUUID")
	@Column(name = "ID", unique = true, nullable = false,length=50)
	private String id;			// 唯一标识（主键）
	
	@Column(name = "PRO_NAME",length=20)
	private String proName;		// 产品名称
	
	@Column(name = "PRO_NUMBER",length=20)
	private String proNumber;	// 产品编号（唯一)
	
	@Column(name = "STATUS",length=2)
	private Integer status;		// 1 标示启动 0 标示停用
	
	@Column(name = "EMP_ID",length=50)
	private String empId;		// 对应员工SYS_EMPLOYEE ID  操作人
	
	@Column(name = "PARENT_ID",length=50)
	private String parentId;	// 0:产品类型  否则:产品名称
	
	@Column(name = "CREATE_TIME",length=50)
	private String createTime;	// 创建时间
	
	@Column(name = "REMARK",length=100)
	private String remark;		//备注

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public String getProNumber() {
		return proNumber;
	}

	public void setProNumber(String proNumber) {
		this.proNumber = proNumber;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
