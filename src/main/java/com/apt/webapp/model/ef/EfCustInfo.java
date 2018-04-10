package com.apt.webapp.model.ef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
@Table(name = "EF_CUST_INFO")
public class EfCustInfo {

	// 性别
	public final static Integer SEX_MANS = 1;				//男
	public final static Integer SEX_WOMAN = 2;				//女
	
	// 婚姻状态
	public final static Integer MARITAL_HAVE = 1;			//已婚
	public final static Integer MARITAL_NO = 2;				//未婚
	public final static Integer MARITAL_SPLITE = 3;			//离异
	public final static Integer MARITAL_OTHER= 4;			//其他
	
	// 是否有子女
	public final static Integer CHILD_YES = 1;				//有
	public final static Integer CHILD_NO = 0;				//无
	
	// 客户类型
	public static final int INTENT_CUST_STATUS = 1; // 意向客户
	public static final int SUCC_CUST_STATUS = 2; 	// 成功客户/准客户
	
	@Id
	@GeneratedValue(generator = "uuidGenerator")
    @GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@Column(name = "ID", unique = true, nullable = false,length=40)
	private String id;
	
	@Column(name = "CREATE_TIME",length=20)
	private String createTime;//创建时间
	
	@Column(name = "UPDATE_TIME",length=20)
	private String updateTime;//修改时间
	
	@Column(name = "BAK_MOBILE",length=20)
	private String bakMobile;//备用手机
	
	@Column(name = "SEX",length=2)
	private Integer sex;//性别（1 男  2 女 ）
	
	@Column(name = "BIRTHDAY",length=20)
	private String birthday;//生日
	
	@Column(name = "CUST_NAME",length=50)
	private String custName;			// 客户姓名
	
	@Column(name = "CUST_IC",length=20)
	private String custIc; 				// 客户身份证
	
	@Column(name = "cust_mobile",length=20)
	private String custMobile;				// 手机号码
	
	@Column(name = "NATIVE_PROVINCE",length=40)
	private String nativeProvince;		// 籍贯-省
	
	@Column(name = "NATIVE_CITY",length=40)
	private String nativeCity;			// 籍贯-市
	
	@Column(name = "NATIVE_AREA",length=40)
	private String nativeArea;			// 籍贯-区
	
	@Column(name = "FAMIL_MEMBERS",length=10)
	private String familMembers;		// 家庭构成人数
	
	@Column(name = "famil_children_status",length=3)
	private String familChildrenStatus;		// 家庭子女情况	1个  2个  3个 …
	
	@Column(name = "FAMIL_CHILDREN_AGE",length=3)
	private String familChildrenAge;	// 家庭子女年龄
	
	@Column(name = "FAMIL_SOURCE_OF_INCOME",length=50)
	private String familSourceOfIncome;  //	家庭收入来源
	
	@Column(name = "FAMIL_INCOME_STATUS",length=50)
	private String familIncomeStatus;    // 家庭收入状况
	
	@Column(name = "FAMIL_ASSET_ALLOCATION",length=50)
	private String familAssetAllocation;  // 家庭资产配置
	
	@Column(name = "FAMIL_CAPITAL_POWER",length=50)
	private String familCapitalPower;	 // 资金决策权
	
	@Column(name = "FAMIL_TEL_AREA",length=5)
	private String familTelArea;		// 家庭固话-区号
	
	@Column(name = "FAMIL_TEL_NO",length=10)
	private String familTelNo;			// 家庭电话-号码
	
	@Column(name = "FAMIL_TEL_EXTENSION",length=15)
	private String familTelExtension;	// 家庭号码-分机
	
	@Column(name = "QQ",length=20)
	private String qq;//qq
	
	@Column(name = "WECHAT",length=50)
	private String wechat;//微信号
	
	@Column(name = "EMAIL",length=50)
	private String email;//微信号
	
	@Column(name = "MOMO",length=50)
	private String momo;//陌陌
	
	@Column(name = "HOBBIES",length=200)
	private String hobbies;				// 爱好
	
	@Column(name = "NOW_LIVING_PROVINCE",length=20)
	private String nowLivingProvince;     // 现居住地省份
	
	@Column(name = "NOW_LIVING_CITY",length=20)
	private String nowLivingCity;     // 现居住地城市
	
	@Column(name = "NOW_LIVING_AREA",length=20)
	private String nowLivingArea;    // 现居住地县/ 镇
	
	@Column(name = "NOW_LIVING_ADDRESS",length=100)
	private String nowLivingAddress;       //现居住地详细地址
	
	@Column(name = "MARITAL",length=2)
	private Integer marital;//婚姻状态（1 结婚  2 未婚  3 离异 4 其他）
	
	@Column(name = "REST_TIME",length=40)
	private String restTime;			// 休息时间 : 周末双休 周末单休 工作日双休 工作日单休
	
	@Column(name = "REMARK",length=500)
	private String remark;			// 备注
	
	@Column(name = "CUST_CLASS",length=40)
	private String custClass;			// BG_cust_class 客户分类
	
	@Column(name = "ENROLL_SOURCE",length=2)
	private String enrollSource;			// 登记来源 线下团队、momo等字段信息
	
	@Column(name = "CUST_STATUS",length=2)
	private Integer custStatus;			// 客户类型  1.意向客户 2.准客户

	@Column(name = "credentials_type",length=2)
	private Integer credentialsType; // 证件类型 0身份证，1：护照 2：军官证
	
	@Column(name = "cust_info_type",length=2)
	private Integer custInfoType; // 客户信息类型  0.个人 1.法人
	
	@Column(name = "enterprise_name",length=2)
	private String enterpriseName; // 企业名称
	
	public EfCustInfo() {
		super();
	}
	
	public EfCustInfo(String createTime, String updateTime, String bakMobile,
			Integer sex, String birthday, String custName, String custIc,
			String custMobile, String nativeProvince, String nativeCity,
			String nativeArea, String familMembers, String familChildrenStatus,
			String familChildrenAge, String familSourceOfIncome,
			String familIncomeStatus, String familAssetAllocation,
			String familCapitalPower, String familTelArea, String familTelNo,
			String familTelExtension, String qq, String wechat, String email,
			String momo, String hobbies, String nowLivingProvince,
			String nowLivingCity, String nowLivingArea,
			String nowLivingAddress, Integer marital, String restTime,
			String remark, String custClass, String enrollSource,
			String custStatus) {
		super();
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.bakMobile = bakMobile;
		this.sex = sex;
		this.birthday = birthday;
		this.custName = custName;
		this.custIc = custIc;
		this.custMobile = custMobile;
		this.nativeProvince = nativeProvince;
		this.nativeCity = nativeCity;
		this.nativeArea = nativeArea;
		this.familMembers = familMembers;
		this.familChildrenStatus = familChildrenStatus;
		this.familChildrenAge = familChildrenAge;
		this.familSourceOfIncome = familSourceOfIncome;
		this.familIncomeStatus = familIncomeStatus;
		this.familAssetAllocation = familAssetAllocation;
		this.familCapitalPower = familCapitalPower;
		this.familTelArea = familTelArea;
		this.familTelNo = familTelNo;
		this.familTelExtension = familTelExtension;
		this.qq = qq;
		this.wechat = wechat;
		this.email = email;
		this.momo = momo;
		this.hobbies = hobbies;
		this.nowLivingProvince = nowLivingProvince;
		this.nowLivingCity = nowLivingCity;
		this.nowLivingArea = nowLivingArea;
		this.nowLivingAddress = nowLivingAddress;
		this.marital = marital;
		this.restTime = restTime;
		this.remark = remark;
		this.custClass = custClass;
		this.enrollSource = enrollSource;
	}

	//理财意向客户添加 构造函数
	public EfCustInfo(
			String id,Integer sex, String birthday, String custName, String custIc,
			String custMobile, String nativeProvince, String nativeCity,
			String nativeArea, String familMembers, String familChildrenStatus,
			String familChildrenAge, String familSourceOfIncome,
			String familIncomeStatus, String familAssetAllocation,
			String familCapitalPower, String familTelArea, String familTelNo,
			String familTelExtension, String qq, String wechat, String email,
			String hobbies, String nowLivingProvince,
			String nowLivingCity, String nowLivingArea,
			String nowLivingAddress, Integer marital, String restTime,
			String remark, String enrollSource,
			String custStatus) {
		super();
		this.id = id;
		this.sex = sex;
		this.birthday = birthday;
		this.custName = custName;
		this.custIc = custIc;
		this.custMobile = custMobile;
		this.nativeProvince = nativeProvince;
		this.nativeCity = nativeCity;
		this.nativeArea = nativeArea;
		this.familMembers = familMembers;
		this.familChildrenStatus = familChildrenStatus;
		this.familChildrenAge = familChildrenAge;
		this.familSourceOfIncome = familSourceOfIncome;
		this.familIncomeStatus = familIncomeStatus;
		this.familAssetAllocation = familAssetAllocation;
		this.familCapitalPower = familCapitalPower;
		this.familTelArea = familTelArea;
		this.familTelNo = familTelNo;
		this.familTelExtension = familTelExtension;
		this.qq = qq;
		this.wechat = wechat;
		this.email = email;
		this.hobbies = hobbies;
		this.nowLivingProvince = nowLivingProvince;
		this.nowLivingCity = nowLivingCity;
		this.nowLivingArea = nowLivingArea;
		this.nowLivingAddress = nowLivingAddress;
		this.marital = marital;
		this.restTime = restTime;
		this.remark = remark;
		this.enrollSource = enrollSource;
	}
	
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

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getBakMobile() {
		return bakMobile;
	}

	public void setBakMobile(String bakMobile) {
		this.bakMobile = bakMobile;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustIc() {
		return custIc;
	}

	public void setCustIc(String custIc) {
		this.custIc = custIc;
	}

	public String getCustMobile() {
		return custMobile;
	}

	public void setCustMobile(String custMobile) {
		this.custMobile = custMobile;
	}

	public String getNativeProvince() {
		return nativeProvince;
	}

	public void setNativeProvince(String nativeProvince) {
		this.nativeProvince = nativeProvince;
	}

	public String getNativeCity() {
		return nativeCity;
	}

	public void setNativeCity(String nativeCity) {
		this.nativeCity = nativeCity;
	}

	public String getNativeArea() {
		return nativeArea;
	}

	public void setNativeArea(String nativeArea) {
		this.nativeArea = nativeArea;
	}

	public String getFamilMembers() {
		return familMembers;
	}

	public void setFamilMembers(String familMembers) {
		this.familMembers = familMembers;
	}

	public String getFamilChildrenStatus() {
		return familChildrenStatus;
	}

	public void setFamilChildrenStatus(String familChildrenStatus) {
		this.familChildrenStatus = familChildrenStatus;
	}

	public String getFamilChildrenAge() {
		return familChildrenAge;
	}

	public void setFamilChildrenAge(String familChildrenAge) {
		this.familChildrenAge = familChildrenAge;
	}

	public String getFamilSourceOfIncome() {
		return familSourceOfIncome;
	}

	public void setFamilSourceOfIncome(String familSourceOfIncome) {
		this.familSourceOfIncome = familSourceOfIncome;
	}

	public String getFamilIncomeStatus() {
		return familIncomeStatus;
	}

	public void setFamilIncomeStatus(String familIncomeStatus) {
		this.familIncomeStatus = familIncomeStatus;
	}

	public String getFamilAssetAllocation() {
		return familAssetAllocation;
	}

	public void setFamilAssetAllocation(String familAssetAllocation) {
		this.familAssetAllocation = familAssetAllocation;
	}

	public String getFamilCapitalPower() {
		return familCapitalPower;
	}

	public void setFamilCapitalPower(String familCapitalPower) {
		this.familCapitalPower = familCapitalPower;
	}

	public String getFamilTelArea() {
		return familTelArea;
	}

	public void setFamilTelArea(String familTelArea) {
		this.familTelArea = familTelArea;
	}

	public String getFamilTelNo() {
		return familTelNo;
	}

	public void setFamilTelNo(String familTelNo) {
		this.familTelNo = familTelNo;
	}

	public String getFamilTelExtension() {
		return familTelExtension;
	}

	public void setFamilTelExtension(String familTelExtension) {
		this.familTelExtension = familTelExtension;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getWechat() {
		return wechat;
	}

	public void setWechat(String wechat) {
		this.wechat = wechat;
	}

	public String getMomo() {
		return momo;
	}

	public void setMomo(String momo) {
		this.momo = momo;
	}

	public String getHobbies() {
		return hobbies;
	}

	public void setHobbies(String hobbies) {
		this.hobbies = hobbies;
	}

	public String getNowLivingProvince() {
		return nowLivingProvince;
	}

	public void setNowLivingProvince(String nowLivingProvince) {
		this.nowLivingProvince = nowLivingProvince;
	}

	public String getNowLivingCity() {
		return nowLivingCity;
	}

	public void setNowLivingCity(String nowLivingCity) {
		this.nowLivingCity = nowLivingCity;
	}

	public String getNowLivingArea() {
		return nowLivingArea;
	}

	public void setNowLivingArea(String nowLivingArea) {
		this.nowLivingArea = nowLivingArea;
	}

	public String getNowLivingAddress() {
		return nowLivingAddress;
	}

	public void setNowLivingAddress(String nowLivingAddress) {
		this.nowLivingAddress = nowLivingAddress;
	}

	public Integer getMarital() {
		return marital;
	}

	public void setMarital(Integer marital) {
		this.marital = marital;
	}

	public String getRestTime() {
		return restTime;
	}

	public void setRestTime(String restTime) {
		this.restTime = restTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCustClass() {
		return custClass;
	}

	public void setCustClass(String custClass) {
		this.custClass = custClass;
	}

	public String getEnrollSource() {
		return enrollSource;
	}

	public void setEnrollSource(String enrollSource) {
		this.enrollSource = enrollSource;
	}

	

	public Integer getCustStatus() {
		return custStatus;
	}

	public void setCustStatus(Integer custStatus) {
		this.custStatus = custStatus;
	}

	public static Integer getSexMans() {
		return SEX_MANS;
	}

	public static Integer getSexWoman() {
		return SEX_WOMAN;
	}

	public static Integer getMaritalHave() {
		return MARITAL_HAVE;
	}

	public static Integer getMaritalNo() {
		return MARITAL_NO;
	}

	public static Integer getMaritalSplite() {
		return MARITAL_SPLITE;
	}

	public static Integer getMaritalOther() {
		return MARITAL_OTHER;
	}

	public static Integer getChildYes() {
		return CHILD_YES;
	}

	public static Integer getChildNo() {
		return CHILD_NO;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getCredentialsType() {
		return credentialsType;
	}

	public void setCredentialsType(Integer credentialsType) {
		this.credentialsType = credentialsType;
	}

	public Integer getCustInfoType() {
		return custInfoType;
	}

	public void setCustInfoType(Integer custInfoType) {
		this.custInfoType = custInfoType;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}
	
}
