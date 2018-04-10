package com.apt.util.mail;


import net.sf.json.JSONObject;

import com.apt.util.MessageTemplete;
import com.apt.util.StaticData;
import com.apt.util.date.DateUtil;
import com.apt.util.encrypt.AESUtil;

public class Mail {
	
	//发送邮件
	public static String sentMail(String subject,String page,String email)throws Exception{
		if(StaticData.closeMail){
			return "success";
		}
	    MailSenderInfo mailInfo = setMail("","","","");    
	    mailInfo.setToAddress(email);    
	    mailInfo.setSubject(subject);    
	    mailInfo.setContent(page);
	    
	    //这个类主要来发送邮件   
	    SimpleMailSender.sendHtmlMail(mailInfo);//发送html格式
        return "success";    
   }
	
	public static MailSenderInfo setMail(String account ,String pwd,String server ,String port){
		account ="zzl_newbg@126.com";
		pwd="zzl123456";
		server="smtp.126.com";
		port="25";
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost(server);    
	    mailInfo.setMailServerPort(port);    
	    mailInfo.setValidate(true);    
	    mailInfo.setUserName(account);    
	    mailInfo.setPassword(pwd);//您的邮箱密码    
	    mailInfo.setFromAddress(account);
	    mailInfo.setSubject("来自贝尔在线的验证邮件");
	    return mailInfo;
	}
	public static String getUrl(String username,String email,String yuming,String random,String path) throws Exception{
		String param = username+"&"+email+"&"+DateUtil.getCurrentTime(DateUtil.STYLE_1);
		//加密
		byte[] byteRe = AESUtil.encrypt2(param, AESUtil.PASSWORD);
        //加密过的二进制数组转化成16进制的字符串  
        String encrytStr = AESUtil.parseByte2HexStr(byteRe);
		return yuming+path+"/secure/chkMailCode.html?checkcode="+encrytStr;
	}
	
	public static String getHtml(String username,String email,String yuming,String linkPath){
		StringBuffer url =new StringBuffer();
		url.append("<html xmlns='http://www.w3.org/1999/xhtml'>");
		url.append("<head>");
		url.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />");
		url.append("<title>无标题文档</title>");
		url.append("<style>");
		url.append(" *{ padding:0; margin:0; font-size:13px; font-family: '宋体' ,'Arial Black', Gadget, sans-serif;}");
		url.append(" .emailcont{ background:url("+yuming+"/common/images/emailcont.png) 0 0 no-repeat; width:645px;  min-height:200px; margin:10px auto;  border:#CCC solid 1px;}");
		url.append(" .emailmain{ padding:10px; margin-top:0px;}"); 
		url.append(".welcome{ font-weight:700; color:#333; margin-bottom:15px; }");
		url.append(" .desfont{ line-height:30px;  padding-bottom:15px;}");
		url.append(" .footdes{ background:#f7f7f7; text-align:center; line-height:30px; margin-top:10px; border-top:#ccc dashed 1px;}");
		url.append("</style>");
		url.append("</head>");
		url.append("<body>");  
		url.append("  <div class='emailcont' background='bg.jpg'><p><img src='"+yuming+"/common/images/emailcont.png'/></p>");
		url.append("   <div class='emailmain'>");
		url.append("     <div class='welcome'>亲爱的会员"+username+"，您好！</div>");
		url.append("    <p class='desfont'>请点击下面的链接，完成邮箱认证，提高贝尔在线账户的安全性。<br /><a href='"+linkPath+"'>"+linkPath+"</a><br />如果您不能点击上面的链接，还可以将以下链接的地址复制到浏览器中访问。<br /><a href='"+linkPath+"'></a>"+linkPath+"</p>");
		url.append("   </div>");
		url.append("    <div class='footdes'>");	
		url.append("       <p>此邮件为系统邮件，请勿回复</p>");
		url.append("       <p>本页面内容的最终解释权归苏州中资联投资管理有限公司拥有</p>");
		url.append("       <p>如有任何问题，请拨打我们的客服电话：400-0686-600</p>");
		url.append("    </div>");
		url.append("  </div>");
		url.append("</body>");
		url.append("</html>");
		return url.toString();
	}
	public static void main(String[] args) {
		try {
			JSONObject paramJson = new JSONObject();
			paramJson.put("number","21");
			paramJson.put("name","ha");
			paramJson.put("msgType","1");
			paramJson.put("Periods","1");
			paramJson.put("Princiapl", "10");
			paramJson.put("Interest", "10");
			paramJson.put("ManagementAmt", "10");
			paramJson.put("totalMoney", "10");
			paramJson.put("couponInterest", "10");
			paramJson  = MessageTemplete.getMsg(paramJson);
			System.out.println(sentMail(paramJson.getString("title"),paramJson.getString("content"),"389373935@qq.com"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
