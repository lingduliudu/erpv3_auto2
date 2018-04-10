package com.apt.util;
/**
 * 功能说明：获得静态数据
 * 典型用法：
 * 特殊用法：	
 * @author 袁浩
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015年10月20日 
 * Copyright zzl-apt
 */
public class StaticData {
	/**红包  账户*/
	public static String redEnvelope = SysConfig.getInstance().getProperty("redEnvelope");
	/**风险户 账户*/
	public static String risk = SysConfig.getInstance().getProperty("risk");
	/**红包户的cust_info_id*/
	public static String redCustInfoId = SysConfig.getInstance().getProperty("redCustInfoId");
	/**信用公司  账户*/
	public static String creditAccount = SysConfig.getInstance().getProperty("creditAccount");
	/**信用公司  账户名称*/
	public static String creditAccountName = SysConfig.getInstance().getProperty("creditAccountName");
	/**恒丰信用公司  账户*/
	public static String HFcreditAccount = SysConfig.getInstance().getProperty("HFcreditAccount");
	/**恒丰公司  账户名称*/
	public static String HFcreditAccountName = SysConfig.getInstance().getProperty("HFcreditAccountName");
	/**恒丰通用产品*/
	public static String HFProjectNumber = SysConfig.getInstance().getProperty("HFProjectNumber");
	/**中资联公司  账户*/
	public static String zzlAccount = SysConfig.getInstance().getProperty("zzlAccount");
	/**poc  路径*/
	public static String pocUrl = SysConfig.getInstance().getProperty("pocUrl");
	/**boc2.0  路径*/
	public static String bocUrl = SysConfig.getInstance().getProperty("pay.center.api");
	/**清结算  路径*/
	public static String bcUrl = SysConfig.getInstance().getProperty("bcUrl");
	/**死标  */
	public static String A1 = SysConfig.getInstance().getProperty("A1");
	/**zzl地址  */
	public static String v3ZzlcfPostPath =SysConfig.getInstance().getProperty("v3ZzlcfPostPath");
	/**bel地址  */
	public static String v3BelPostPath =SysConfig.getInstance().getProperty("v3BelPostPath");
	/**划拨操作暂停  */
	public static String StopBank = SysConfig.getInstance().getProperty("StopBank");
	/**欺骗时间  */
	public static String fakeTime ="";
	/**是否关闭boc  */
	public static boolean closeBoc =Boolean.parseBoolean((SysConfig.getInstance().getProperty("closeBoc")));
	/**是否关闭boc  */
	public static boolean closeBocTransFile =false;
	/**是否关闭poc  */
	public static boolean closePoc =Boolean.parseBoolean((SysConfig.getInstance().getProperty("closePoc")));
	/**是否开启短信  */
	public static boolean closeSms =Boolean.parseBoolean((SysConfig.getInstance().getProperty("closeSms")));
	/**是否开启邮件  */
	public static boolean closeMail =Boolean.parseBoolean((SysConfig.getInstance().getProperty("closeMail")));
	/**项目名称*/
	public static String appName="erpv3_auto2";
	/**是否开启全局判断  */
	public static boolean pass = true;
	/** 恒丰银行code*/
	public final static String HFBANK_CODE  = "200007-0003";
	/** 恒丰银行*/
	public final static String HFBANK  = "恒丰银行";
	
	/**自动还款*/
	public static String OP_ZDHK = "200012-0003";
	/**ERPV3*/
	public static String OP_ERPV3 = "200012-0001";
	/**微信公众号*/
	public static String OP_WECHAT = "200012-0003";
	
	/**宝付*/
	public static String NPP_BAOFOO = "200013-0003";
	/**益倍嘉*/
	public static String NPP_YBJ = "200013-0004";
	/**现金*/
	public static String NPP_XIJIN = "200013-0099";
	/**易行通*/
	public static String NPP_YXT = "200013-0006";
	/**恒丰银行*/
	public static String NPP_HENGFENG = "200013-0007";
	/**富有*/
	public static String NPP_FUYOU = "200013-0001";
	/**江西银行*/
	public static String NPP_JXYH = "200013-0002";
	
	/**第三方虚拟账户*/
	public static String NPC_OTHER = "200014-0001";
	/**银行卡代扣*/
	public static String NPC_BANKCARD = "200014-0002";
	/**支付宝*/
	public static String NPC_ZHIFU = "200014-0003";
	/**微信支付*/
	public static String NPC_WECHAT = "200014-0004";
	/**现金*/
	public static String NPC_XIANJIN = "200014-0099";
	/**专业投资人*/
	public static String PROFESSIONAL_INVESTOR = "400001-00001";
}

