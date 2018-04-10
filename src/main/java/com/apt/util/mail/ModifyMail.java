package com.apt.util.mail;

public class ModifyMail {
	//发送邮件
	public static String modifysentMail(String page,String email)throws Exception{  
     MailSenderInfo mailInfo = setMail("","","","");    
     mailInfo.setToAddress(email);    
    mailInfo.setSubject("来自贝尔在线的验证邮件");    
    mailInfo.setContent(page);    
            //这个类主要来发送邮件   
    SimpleMailSender sms = new SimpleMailSender();   
             sms.sendHtmlMail(mailInfo);//发送html格式
     return "success";    
   }
	public static MailSenderInfo setMail(String account ,String pwd,String server ,String port){
		account ="1668581344@qq.com";
		pwd="f65111047";
		server="smtp.qq.com";
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
	public String getUrl(String username,String email,String yuming,String random){
		return yuming+"modifyemailvaliadate.html?username="+username+"&email="+email+"&checkcode="+random;
		
	}
	public String getHtml(String username,String email,String yuming,String random){
		StringBuffer url =new StringBuffer();
		url.append("<html xmlns='http://www.w3.org/1999/xhtml'>");
		url.append("<head>");
		url.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />");
		url.append("<title>无标题文档</title>");
		url.append("<style>");
		url.append(" *{ padding:0; margin:0; font-size:13px; font-family: '宋体' ,'Arial Black', Gadget, sans-serif;}");
		url.append(" .emailcont{ background:url("+yuming+"beige/images/emailcont1.png) 0 0 no-repeat; width:645px;  min-height:200px; margin:10px auto;  border:#CCC solid 1px;}");
		url.append(" .emailmain{ padding:10px; margin-top:120px;}"); 
		url.append(".welcome{ font-weight:700; color:#333; margin-bottom:15px; }");
		url.append(" .desfont{ line-height:30px;  padding-bottom:15px;}");
		url.append(" .footdes{ background:#f7f7f7; text-align:center; line-height:30px; margin-top:10px; border-top:#ccc dashed 1px;}");
		url.append("</style>");
		url.append("</head>");
		url.append("<body>");
		url.append("  <div class='emailcont' background='bg.jpg'>");
		url.append("   <div class='emailmain'>");
		url.append("     <div class='welcome'>亲爱的会员"+username+"，您好！</div>");
//		url.append("    <p class='desfont'>感谢您注册贝尔在线,<br />如非你本人操作，请联系客服：400-0686-600</p>");
		url.append("    <p class='desfont'>请点击下面的链接，完成邮箱修改，祝您投资愉快。<br /><a href='"+yuming+"newbg/modifyEmailver.html?username="+username+"&email="+email+"&checkcode="+random+"'>"+yuming+"newbg/secure/chkMailCode.html?username="+username+"&email="+email+"&checkcode="+random+"</a><br />如果您不能点击上面的链接，还可以将一下链接的地址复制到浏览器中访问。<br /><a href='"+yuming+"newbg/modifyEmailver.html?username="+username+"&email="+email+"&checkcode="+random+"'>"+yuming+"newbg/secure/chkMailCode.html?username="+username+"&email="+email+"&checkcode="+random+"</a></p>");
		url.append("   </div>");
		url.append("    <div class='footdes'>");
		url.append("       <p>此邮件为系统邮件，请勿回复</p>");
		url.append("       <p>本页面内容的最终解释权归南京贝格金融有限公司拥有</p>");
		url.append("       <p>如有任何问题，请拨打我们的客服电话：400-0686-600</p>");
		url.append("    </div>");
		url.append("  </div>");
		url.append("</body>");
		url.append("</html>");
		return url.toString();
		
	}
	public static void main(String[] args) {
		ModifyMail mail =new ModifyMail();
		try {
			mail.modifysentMail("<html><body><a>http://192.168.2.234:8080/zzlp2p/emailvaliadate.do</a></body></html>", "445128223@qq.com");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println(mail. getUrl("admin1", "lazysoldier@163.com"));
	}
}
