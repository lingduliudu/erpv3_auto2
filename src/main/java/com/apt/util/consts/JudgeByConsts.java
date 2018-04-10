package com.apt.util.consts;

/**
 * 功能说明：常用判断常量类
 * @author weiyingni 
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-09-15
 * 需要注意：
 * 当前类中所有变量为静态，常用常量定义，后期可能会修改为配置文件形式
 * 所有常量命名为大写，多个单词间用'_'线分割，不要使用驼峰命名
 * Copyright zzl-apt
 */
public class JudgeByConsts {

	/** 查询typeFrom时typeNumber：理财标识  */
	public static final String EF_PRODUCT = "E001";
	
	/** 查询信贷产品详细时typeNumber：V1标识 A001为LowerProduct */
	public static final String LOWER_PRODUCT = "A001";
	
	/** 查询信贷产品详细时typeNumber：V2标识  A002为CrmProductDetail */
	public static final String CRM_PRODUCT_DETAIL = "A002";
	
	/** 查询信贷产品详细时typeNumber：V3标识  A003为NewcrmProductDetail */
	public static final String NEW_CRM_PRODUCT_DETAIL = "A003";
	
	/** 理财产品编号的前缀  */
	public static final String EF_PRODUCT_MARK = "LCP";
	
	/** 信贷产品编号的前缀  */
	public static final String CRM_PRODUCT_MARK = "LCP";

	/** 无效 0 */
	public static final Integer INVALID = 1;
	
	/** 有效 1*/
	public static final Integer VALID = 0;
	
	/**  认证状态  未认证 0*/
	public final static Integer VERIFY_NO = 0;
	
	/**  认证状态  已认证 1*/
	public final static Integer VERIFY_YES = 1;
	
	/** 注册来源  网站 1*/
	public final static Integer REGISTER_WEB = 1;
	
	/** 注册来源  手机 2*/
	public final static Integer REGISTER_MOBILE = 2;
	
	/** 注册来源  第三方平台 3*/
	public final static Integer REGISTER_PLATFORM = 3;
	
	/**  客户类型  理财 1*/
	public final static Integer TYPE_FINANCE = 1;
	
	/**  客户类型  信贷 2*/
	public final static Integer TYPE_CREDIT = 2;
	
	/**  客户类型  均参与 3*/
	public final static Integer TYPE_ALL = 3;
}
