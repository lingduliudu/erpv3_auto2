package com.apt.webapp.model.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


/**
 * 功能说明：
 * 创建人：袁浩
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-10
 */
@Entity
@Table(name = "poc_batch_record")
public class PocBatchRecord {
	
	@Id
	@GenericGenerator(name="systemUUID",strategy="uuid")
	@GeneratedValue(generator="systemUUID")
	@Column(name = "id", unique = true, nullable = false,length=40)
	private String id;				
	
	@Column(name = "bg_ef_order_id",length=60)
	private String bgEfOrderId;				
	
	
	@Column(name = "bg_ef_paycontrol_id",length=60)
	private String bgEfPaycontrolId;				
	
	@Column(name = "crm_order_id",length=60)
	private String crmOrderId;				
	
	@Column(name = "type",length=60)
	private String type;				//类型：1正常还款  -1提前结清
	
	@Column(name = "unique_no",length=60)
	private String uniqueNo;				
	
	@Column(name = "preauth_file_name",length=1000)
	private String preauthFileName;		
	
	@Column(name = "transferbu_file_name",length=1000)
	private String transferbuFileName;		
	
	@Column(name = "freeze_file_name",length=1000)
	private String freezeFileName;		
	
	@Column(name = "create_time",length=60)
	private String createTime;					
	
	@Column(name = "process",length=60)
	private String process;// 进度 : 0 未处理，1预授权文件上传，2划拨文件上传， 3冻结文件上传，4结束
	
	@Column(name = "process_result",length=60)
	private String process_result; //1成功 0失败 -1异常
	
	@Column(name = "emp_id",length=60)
	private String empId;
	
	@Column(name = "fail_remark",length=1000)
	private String failRemark;

	@Column(name = "money",length=20,scale=2)
	private Double money;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBgEfOrderId() {
		return bgEfOrderId;
	}

	public void setBgEfOrderId(String bgEfOrderId) {
		this.bgEfOrderId = bgEfOrderId;
	}

	public String getBgEfPaycontrolId() {
		return bgEfPaycontrolId;
	}

	public void setBgEfPaycontrolId(String bgEfPaycontrolId) {
		this.bgEfPaycontrolId = bgEfPaycontrolId;
	}

	public String getCrmOrderId() {
		return crmOrderId;
	}

	public void setCrmOrderId(String crmOrderId) {
		this.crmOrderId = crmOrderId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUniqueNo() {
		return uniqueNo;
	}

	public void setUniqueNo(String uniqueNo) {
		this.uniqueNo = uniqueNo;
	}

	public String getPreauthFileName() {
		return preauthFileName;
	}

	public void setPreauthFileName(String preauthFileName) {
		this.preauthFileName = preauthFileName;
	}

	public String getTransferbuFileName() {
		return transferbuFileName;
	}

	public void setTransferbuFileName(String transferbuFileName) {
		this.transferbuFileName = transferbuFileName;
	}

	public String getFreezeFileName() {
		return freezeFileName;
	}

	public void setFreezeFileName(String freezeFileName) {
		this.freezeFileName = freezeFileName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getProcess_result() {
		return process_result;
	}

	public void setProcess_result(String process_result) {
		this.process_result = process_result;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getFailRemark() {
		return failRemark;
	}

	public void setFailRemark(String failRemark) {
		this.failRemark = failRemark;
	}

	public PocBatchRecord(String id, String bgEfOrderId,
			String bgEfPaycontrolId, String crmOrderId, String type,
			String uniqueNo, String preauthFileName, String transferbuFileName,
			String freezeFileName, String createTime, String process,
			String process_result,double money,String empId,String failRemark) {
		super();
		this.id = id;
		this.bgEfOrderId = bgEfOrderId;
		this.bgEfPaycontrolId = bgEfPaycontrolId;
		this.crmOrderId = crmOrderId;
		this.type = type;
		this.uniqueNo = uniqueNo;
		this.preauthFileName = preauthFileName;
		this.transferbuFileName = transferbuFileName;
		this.freezeFileName = freezeFileName;
		this.createTime = createTime;
		this.process = process;
		this.process_result = process_result;
		this.money = money;
		this.empId = empId;
		this.failRemark = failRemark;
	}

	public PocBatchRecord() {
		super();
	}
	
}
