package com.apt.util;

import com.apt.util.arith.ArithUtil;

import net.sf.json.JSONObject;

public class MessageTemplete {

	/**
	 * 获取消息信息  （大类 小类  标题 内容）
	 * @param param 内容中需要的变量
	 * @return
	 */
	public static JSONObject getMsg(JSONObject paramJson){
		JSONObject resultJson = new JSONObject();
		String pread = "<p style='padding-left:25px'>";
		String pfoot = "</p>";
		String titleX = "";
		String contentX = "";
		String title = "";
		String content = "";
		//自动投资设置成功提醒 关闭
		if(ChkUtil.isNotEmpty(paramJson.get("couponInterest"))&&paramJson.getDouble("couponInterest")>0){
			contentX = "代金券利息：￥"+paramJson.getString("couponInterest")+"元,";
		}
		double  Interest =0d;
		String  ManagementAmt="0.00";
		if(paramJson.containsKey("ManagementAmt")&& paramJson.containsKey("Interest")){
			Interest = NumberFormat.format(ArithUtil.sub(paramJson.getDouble("Interest"), paramJson.getDouble("ManagementAmt")));
		}
		switch (paramJson.getInt("msgType")) {
		case 1:
			title = "还款成功通知"+titleX;
			content = "尊敬的"+ paramJson.getString("name") +"会员："+pread+"您的投资项目（编号为:"+paramJson.getString("number")+"）第"+paramJson.getInt("Periods")+"期回款已到账。本金:￥"+paramJson.getString("Princiapl")+"元,利息:￥"+Interest+"元,"+contentX+"平台服务费:￥"+ManagementAmt+"元,总金额:￥"+paramJson.getString("totalMoney")+"元。"+pfoot+pread+"祝您投资愉快！"+pfoot;
			break;
		case 2:
			//项目结清
			title = "项目结清通知"+titleX;
			content = "尊敬的"+ paramJson.getString("name") +"会员："+pread+"您的投资项目（编号为:"+paramJson.getString("number")+"）第"+paramJson.getInt("Periods")+"期回款已到账。本金:￥"+paramJson.getString("Princiapl")+"元,利息:￥"+Interest+"元,"+contentX+"平台服务费:￥"+ManagementAmt+"元,总金额:￥"+paramJson.getString("totalMoney")+"元。该笔投资项目已全部结清。"+pfoot+pread+"祝您投资愉快！"+pfoot;
			break;
		case 3:
			//项目结清
			title = "项目结清通知"+titleX;
			content = "尊敬的"+ paramJson.getString("name") +"会员："+pread+"您的投资项目（编号为:"+paramJson.getString("number")+"）。该笔投资项目已全部结清。"+pfoot+pread+"祝您投资愉快！"+pfoot;
			break;
		default:
			break;
		}
		resultJson.put("title", title);
		resultJson.put("content", content);
		return resultJson;
	}
}
