package com.apt.util.consts;

import com.alibaba.fastjson.JSONObject;
import com.apt.util.ChkUtil;
import com.apt.util.date.DateUtil;
import com.apt.web.model.publics.SysMessage;;
/**
 * 功能说明：站内消息内容枚举类
 * @author wangcy 
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-11-14
 * 需要注意：
 * 当前类中  getMsg 方法需要的参数需要根据内容提取变量
 * Copyright zzl-apt
 */
public enum MsgContentEnum {
	//注册
	REGESTERTemplet(1),
	//密码修改
	PASSWORDTemplet(2),	
	//存管电子账户开通
	AUTHENTICATIONTemplet(3),
	//邮箱认证
	EMAILAUTHTemplet(4),
	
	//投资成功提醒
	INVESTSUCCESSTemplet(5),
	//投资失败(流标 投资人)
	INVESTFAILEDTemplet(6),
	//投资失败(流标 借款人)
	INVESTFAILEDLOANTemplet(7),
	//满标划拨成功(投资人)
	INVESTFULLTemplet(8),
	//满标划拨成功(借款人)
	INVESTFULLLOANTemplet(9),
	
	
	//出售债权通知
	SALETRANSFERTemplet(10),
	//债权交易成功:(部分 - 出售人)
	PARTSALESUCCESSTemplet(11),
	//债权交易成功(全部 - 出售人)
	SALESUCCESSTemplet(12),
	//债权交易成功( 购买人 )
	SALESUCCESSBUYTemplet(13),
	//撤销债权- 手动撤销
	CANCLESALETemplet(14),
	//撤销债权 - 自动撤消
	CANCLESALEAUTOTemplet(15),	
	//新手标投资通知
	NEWHANDINVESTemplet(16),	
	//修改网站手机号码
	MOBILEUPDATETemplet(17),	
	//修改绑定银行卡
	BANKMOBILETemplet(18),
	//存管电子账户密码设置
	BANKPASSWORDTemplet(19),	
	//邀请好友成功提醒
	INVITEFRIENDSTemplet(20),
	//邀请好友红包奖励
	INVITEAWARDTemplet(21),
	
	
	
	/**  自动投资设置    暂无**/
	//自动投资修改设置成功提醒
	AUTOINVESTUPDATETemplet(30),
	//自动投资开启设置成功提醒
	AUTOINVESTSTARTTemplet(31),
	//自动投资关闭设置成功提醒
	AUTOINVESTCLOSETemplet(32),
	//自动投资成功提醒通知
	AUTOINVESTTemplet(33);
	/** 自动投资设置   暂无**/
	
	private Integer type;
	
	MsgContentEnum(Integer type){
		this.type = type;
	}
	/**
	 * 获取消息信息  （大类 小类  标题 内容）
	 * @param param 内容中需要的变量
	 * @return
	 */
	public String[] getMsg(String param){
		Integer type = this.type;
		String[] message =null;
		String title="";
		String text = "";
		String pread = "<p style='padding-left:25px'>";
		String pfoot = "</p>";
		String msgClass = "";
		String msgType = "";
		JSONObject paramJson = JSONObject.parseObject(param);
		String name = paramJson.getString("userName");
		String time = DateUtil.getCurrentTime();
		switch (type) {
		case 1:
			//注册
			msgClass = SysMessage.MSG_CLASS_SYS.toString();
			title = "注册成功通知";
			text = "尊敬的"+ name +"会员：" + pread + "感谢您注册贝尔在线，为了更好的服务，请您尽快完成各项<a href='../safety' class='redword'>安全认证</a>，祝您投资愉快！" + pfoot;
			break;
		case 2:
			//密码修改
			msgClass = SysMessage.MSG_CLASS_SYS.toString();
			title="修改密码成功通知";
			text = "尊敬的"+ name +"会员："+pread+"您于 " + time + "成功重置密码！"+pfoot;
			break;
		case 3:
			//存管电子账户激活成功 
			msgClass = SysMessage.MSG_CLASS_SYS.toString();
			title="存管电子账户激活成功通知";
			text = "尊敬的"+ name +"会员："+pread+"您于 " + time + "时，存管电子账户已激活成功，如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;
			break;
		case 4:
			//邮箱认证 
			msgClass = SysMessage.MSG_CLASS_SYS.toString();
			title="邮箱认证成功通知";
			text = "尊敬的"+ name +"会员："+pread+"您于 " + time + "邮箱认证成功，如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;
			break;
		case 5:
			// 投资  手动
			msgClass = SysMessage.MSG_CLASS_INVEST.toString();
			msgType = SysMessage.MSG_TYPE_INVEST_AUTO.toString();
			title="投资成功通知";
			String coupon = paramJson.getString("coupon");
			if(ChkUtil.isEmpty(coupon)){
				//未使用代金券
				text = "尊敬的"+ name +"会员："+pread+"您于 " + time + "时，成功投资项目(编号为"+ paramJson.getString("orderNum") + ")，投资金额：¥"+ paramJson.getString("amount") +"元。(如有任何疑问，请咨询网站在线客服。)邀请好友，得更多奖励！"+pfoot+pread+"祝您投资愉快！"+pfoot;			
			}else{
				//使用代金券
				text = "尊敬的"+ name +"会员："+pread+"您于 " + time + "时，成功投资项目(编号为"+ paramJson.getString("orderNum") + ")，投资金额：¥"+ paramJson.getString("amount") +"元  + "+coupon+"元代金券。(如有任何疑问，请咨询网站在线客服。)邀请好友，得更多奖励！"+pfoot+pread+"祝您投资愉快！"+pfoot;			
			}
			break;
		case 6:
			//投资失败提醒  投资人
			msgClass = SysMessage.MSG_CLASS_INVEST.toString();
			msgType = SysMessage.MSG_TYPE_INVEST_FLOWORDER.toString();
			title="投资失败通知";
			String couponFailed = paramJson.getString("coupon");
			if(ChkUtil.isEmpty(couponFailed)){
				//未使用代金券
				text = "尊敬的"+ name +"会员："+pread+"您投资的项目(编号:"+ paramJson.getString("orderNum") + ")，金额：¥"+ paramJson.getString("amount") +"元。筹资失败,资金已经解冻,您可以选择其它理财产品，如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;			
			}else{
				//使用代金券
				text = "尊敬的"+ name +"会员："+pread+"您投资的项目(编号:"+ paramJson.getString("orderNum") + ")，金额：¥"+ paramJson.getString("amount") +"元 + "+couponFailed+"元代金券。筹资失败,资金已经解冻,您可以选择其它理财产品，如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;			
			}
			break;
		case 7:
			//投资失败提醒通知  借款人
			msgClass = SysMessage.MSG_CLASS_INVEST.toString();
			msgType = SysMessage.MSG_TYPE_INVEST_FLOWORDER.toString();
			title="筹资失败通知";
			text = "尊敬的"+ name +"会员："+pread+"您筹资的项目(编号:"+ paramJson.getString("orderNum") + ")，金额：¥"+ paramJson.getString("amount") +"元，筹资失败。如有任何疑问，请咨询网站在线客服。"+pfoot;			
			break;
		case 8:
			// 满标划拨成功(投资人)
			msgClass = SysMessage.MSG_CLASS_INVEST.toString();
			msgType = SysMessage.MSG_TYPE_INVEST_FULL.toString();
			title="项目投满划拨通知";
			text = "尊敬的"+ name +"会员："+pread+"您投资的项目(编号:"+ paramJson.getString("orderNum") + ")，金额：¥"+ paramJson.getString("amount") +"元。已经投满划拨成功！您可以进入我的投资-投资记录-回款中的项目进行查看，如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;			
			break;
		case 9:
			// 满标划拨成功(借款人)
			msgClass = SysMessage.MSG_CLASS_INVEST.toString();
			msgType = SysMessage.MSG_TYPE_INVEST_FULL.toString();
			title="筹资成功通知";
			text = "尊敬的"+ name +"会员："+pread+"您筹资的项目(编号:" + paramJson.getString("orderNum") + ")，金额：¥"+paramJson.getString("amount")+"元，订单筹资成功,资金已到账,请您如约按时还款,如有任何疑问，请咨询网站在线客服。"+pfoot;			
			break;
		case 10:
			// 出售债权通知
			msgClass = SysMessage.MSG_CLASS_INVEST.toString();
			msgType = SysMessage.MSG_TYPE_INVEST_TRANSFERBMU.toString();
			title="出售债权通知";
			text = "尊敬的"+ name +"会员："+pread+"您于 " + time + "时，您已成功发起一笔债权出让操作，出售金额:¥"+ paramJson.getString("amount") +"元,打折率："+paramJson.getString("discount")+"%。如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;			
			break;		
		case 11:
			// 债权交易成功:(部分 - 出售人)
			msgClass = SysMessage.MSG_CLASS_INVEST.toString();
			msgType = SysMessage.MSG_TYPE_INVEST_TRANSFERBMU.toString();
			title="债权出售成功通知";
			text = "尊敬的"+ name +"会员："+pread+"您发起的金额为¥"+paramJson.getString("totalAmount")+"元的转让标已于："+ time + "时部分出售成功，本次交易金额：¥"+paramJson.getString("amount")+"元,打折率："+paramJson.getString("discount")+"%。如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;			
			break;
		case 12:
			// 债权交易成功:(全部 - 出售人)
			msgClass = SysMessage.MSG_CLASS_INVEST.toString();
			msgType = SysMessage.MSG_TYPE_INVEST_TRANSFERBMU.toString();
			title="债权出售成功通知";
			text = "尊敬的"+ name +"会员："+pread+"您发起的金额为¥"+paramJson.getString("totalAmount")+"元的转让标已于："+ time + "时全部出售成功，本次交易金额：¥"+paramJson.getString("amount")+"元,打折率："+paramJson.getString("discount")+"%。如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;			
			break;
		case 13:
			// 债权交易成功:(购买人)
			msgClass = SysMessage.MSG_CLASS_INVEST.toString();
			msgType = SysMessage.MSG_TYPE_INVEST_TRANSFERBMU.toString();
			title="购买债权成功通知";
			text = "尊敬的"+ name +"会员："+pread+"您于 " + time + "时，购买债权成功，交易金额：¥"+paramJson.getString("amount")+"元。如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;			
			break;
		case 14:
			// 撤销债权- 手动撤销
			msgClass = SysMessage.MSG_CLASS_INVEST.toString();
			msgType = SysMessage.MSG_TYPE_INVEST_TRANSFERBMU.toString();
			title="撤销债权通知";
			text = "尊敬的"+ name +"会员："+pread+"您于 " + time + "时，撤销出售债权成功，金额：¥"+paramJson.getString("amount")+"元。如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;			
			break;
		case 15:
			// 撤销债权失败通知(系统撤销)
			msgClass = SysMessage.MSG_CLASS_INVEST.toString();
			msgType = SysMessage.MSG_TYPE_INVEST_TRANSFERBMU.toString();
			title="撤销债权通知";
			text = "尊敬的"+ name +"会员："+pread+"因交易时间内未完成交易，系统于 " + time + "时，撤销出售债权成功，金额：¥"+paramJson.getString("amount")+"元。如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;			
			break;
		case 16:
			//体验金投资成功通知
			msgClass = SysMessage.MSG_CLASS_INVEST.toString();
			msgType = SysMessage.MSG_TYPE_INVEST_MANUAL.toString();
			title="体验金投资成功通知";
			text = "尊敬的"+ name +"会员："+pread+"您的10000元体验金已投资成功！您可以进入我的投资-投资记录-回款中的项目进行查看。(如有任何疑问，请咨询网站在线客服。)邀请好友，得更多奖励！"+pfoot+pread+"祝您投资愉快！"+pfoot;			
			break;
		case 17:
			//修改网站手机号码
			msgClass = SysMessage.MSG_CLASS_SYS.toString();
			title="修改网站手机号码成功通知";
			text = "尊敬的"+ name +"会员："+pread+"您于 " + time + "时，成功修改网站登录手机号码,如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;			
			break;
		case 18:
			//修改第三方存管账户绑定银行卡及手机号码
			msgClass = SysMessage.MSG_CLASS_SYS.toString();
			title="修改绑定银行卡成功通知";
			text = "尊敬的"+ name +"会员："+pread+"您于 " + time + "时，成功修改绑定银行卡,如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;			
			break;
		case 19:
			//存管电子账户密码设置成功通知
			msgClass = SysMessage.MSG_CLASS_SYS.toString();
			title="存管电子账户密码设置成功通知";
			text = "尊敬的"+ name +"会员："+pread+"您于 " + time + "时，成功设置存管电子账户密码,如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;			
			break;
		case 20:
			//邀请好友成功通知
			msgClass = SysMessage.MSG_CLASS_SYS.toString();
			title="邀请好友成功通知";
			text = "尊敬的"+ name +"会员："+pread+"恭喜您成功邀请一位好友！好友成功投资您更可获得高达10%的佣金奖励！如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;			
			break;
		case 21:
			//邀请好友红包奖励
			msgClass = SysMessage.MSG_CLASS_SYS.toString();
			title="邀请好友红包奖励";
			text = "尊敬的"+ name +"会员："+pread+"恭喜您获得2元好友认证红包奖励！好友成功投资您更可获得高达10%的佣金奖励！如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;			
			break;

		case 30:
			//自动投资设置成功提醒  修改
			msgClass = SysMessage.MSG_CLASS_SYS.toString();
			title="自动投资设置成功提醒";
			text = "尊敬的"+ name +"会员："+pread+"您于 " + time + "时，成功修改自动投资。如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;
			break;
		case 31:
			//自动投资设置成功提醒 开启
			msgClass = SysMessage.MSG_CLASS_SYS.toString();
			title="自动投资设置成功提醒";
			text = "尊敬的"+ name +"会员："+pread+"您于 " + time + "时，成功开启自动投资。如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;
			break;
		case 32:
			//自动投资设置成功提醒 关闭
			msgClass = SysMessage.MSG_CLASS_SYS.toString();
			title="自动投资设置成功提醒";
			text = "尊敬的"+ name +"会员："+pread+"您于 " + time + "时，成功关闭自动投资。如有任何疑问，请咨询网站在线客服。"+pfoot+pread+"祝您投资愉快！"+pfoot;
			break;
		default:
			break;
		}
		message=new String[]{msgClass,msgType,title,text};
		return message;
	}
}
