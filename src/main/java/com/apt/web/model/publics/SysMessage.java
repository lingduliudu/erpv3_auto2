package com.apt.web.model.publics;

public class SysMessage {
	
	//是否查看的状态
	public final static Integer STATUS_READE_TRUE = 1;	   //已查看
	public final static Integer STATUS_READE_FALSE = 0;   //未查看
	
	// 信息类型（信息小类
	public final static Integer MSG_TYPE_INVEST_MANUAL = 1; // 手动投资
	public final static Integer MSG_TYPE_INVEST_AUTO = 2; // 自动投资
	public final static Integer MSG_TYPE_INVEST_FULL = 3; // 满表
	public final static Integer MSG_TYPE_INVEST_TRANSFER = 4; // 划拨
	public final static Integer MSG_TYPE_INVEST_REPAYMENT = 5; // 还款
	public final static Integer MSG_TYPE_INVEST_OTHER = 6; // 其他
	public final static Integer MSG_TYPE_INVEST_FLOWORDER = 7; // 流标
	public final static Integer MSG_TYPE_INVEST_TRANSFERBMU = 8; // 债权转让
	
	
	//  信息大类
	public final static Integer MSG_CLASS_LOAN = 1; // 借款
	public final static Integer MSG_CLASS_INVEST = 2; // 投资
	public final static Integer MSG_CLASS_SYS = 3; // 系统
	
	private String id;					// id
	private String custId;				// 接收人
	private String efOrderId;			// 关联订单id
	private Integer enabled;			// 有效 无效状态
	private Integer msgClass;			// 消息大类
	private String msgContentId;		// 内容id
	private Integer msgType;			// 消息小类
	private String orderId;				// 订单id
	private Integer readStatus;			// 是否查看
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getEfOrderId() {
		return efOrderId;
	}
	public void setEfOrderId(String efOrderId) {
		this.efOrderId = efOrderId;
	}
	public Integer getEnabled() {
		return enabled;
	}
	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}
	public Integer getMsgClass() {
		return msgClass;
	}
	public void setMsgClass(Integer msgClass) {
		this.msgClass = msgClass;
	}
	public String getMsgContentId() {
		return msgContentId;
	}
	public void setMsgContentId(String msgContentId) {
		this.msgContentId = msgContentId;
	}
	public Integer getMsgType() {
		return msgType;
	}
	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Integer getReadStatus() {
		return readStatus;
	}
	public void setReadStatus(Integer readStatus) {
		this.readStatus = readStatus;
	}
	
	
}
