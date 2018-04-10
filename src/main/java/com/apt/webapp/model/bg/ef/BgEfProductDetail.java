package com.apt.webapp.model.bg.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


/**
 * 
 * 功能说明：理财产品详情表实体
 * 乔春峰
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015/12/7
 */
@Entity
@Table(name = "BG_EF_PRODUCT_DETAIL")
public class BgEfProductDetail {
	
	//状态
	public static final Integer STATUS_YES = 1;			// 启用
	public static final Integer STATUS_NO = 0;			// 禁用
	
	@Id
	@GenericGenerator(name="systemUUID",strategy="uuid")
	@GeneratedValue(generator="systemUUID")
	@Column(name = "ID", unique = true, nullable = false,length=40)
	private String id;					// 唯一标识（主键）
	
	@Column(name = "INVEST_RATE",length=7,scale=4)
	private Double investRate;			// 年化利率/合同利率
	
	@Column(name = "PERIODS",length=20)
	private Double periods;				// 期数
	
	@Column(name = "PAYMENT",length=2,scale=0)
	private Double payment;				// 还款类型 
	
	@Column(name = "CREATE_TIME",length=20)
	private String createTime;			// 创建时间
	
	@Column(name = "EMP_ID",length=50)
	private String empId;				// 对应员工SYS_EMPLOYEE ID
	
	@Column(name = "STATUS",length=2)
	private Integer status;				// 1 标示启动 0 标示停用
	
	@Column(name = "EF_PRODUCT_ID",length=50)
	private String efProductId;			// BG_EF_PRODUCT 的 ID
	
	@Column(name = "INVEST_MANAGE_RATE",length=7,scale=4)
	private Double investManageRate;	// 投资期客户管理月费率：单位  %/月
	
	@Column(name = "ACTIVITIES_RATE",length=7,scale=4)
	private Double activitiesRate;		// 活动利率   年为单位

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getInvestRate() {
		return investRate;
	}

	public void setInvestRate(Double investRate) {
		this.investRate = investRate;
	}

	public Double getPeriods() {
		return periods;
	}

	public void setPeriods(Double periods) {
		this.periods = periods;
	}

	public Double getPayment() {
		return payment;
	}

	public void setPayment(Double payment) {
		this.payment = payment;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getEfProductId() {
		return efProductId;
	}

	public void setEfProductId(String efProductId) {
		this.efProductId = efProductId;
	}

	public Double getInvestManageRate() {
		return investManageRate;
	}

	public void setInvestManageRate(Double investManageRate) {
		this.investManageRate = investManageRate;
	}

	public Double getActivitiesRate() {
		return activitiesRate;
	}

	public void setActivitiesRate(Double activitiesRate) {
		this.activitiesRate = activitiesRate;
	}
	
}
