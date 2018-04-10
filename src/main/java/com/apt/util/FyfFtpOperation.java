package com.apt.util;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apt.util.SysConfig;





public class FyfFtpOperation {
	private static Logger logger = LoggerFactory.getLogger(FyfFtpOperation.class);
	//ftp连接地址
	public static final String SERVER = SysConfig.getInstance().getProperty("fy_ftp_host");//"172.16.10.5";
	
	//登陆用户名
	public static final String USERNAME = SysConfig.getInstance().getProperty("fy_ftp_username");//"erpftp";
	
	//登陆密码
	public static final String PASSWORD =SysConfig.getInstance().getProperty("fy_ftp_password");// "test123456zw";
//	public static final String SERVER = "172.18.1.33";
//	
//	//登陆用户名
//	public static final String USERNAME = "administrator";
//	
//	//登陆密码
//	public static final String PASSWORD = "0j0raFacaX2v8Fe";
//	
//	//服务器上文件夹名称
	public static final String FILE_PRE = "files";
	
	//ftp连接地址
	
	public static void main(String args[]) {
		//下载
		//getDataFiles("ERPV3/110105198707271517/f_cust_i/20160331222406-38899-01.pdf","e:"+File.separator+"erpv3"+File.separator+"an");
		//上传
		System.out.println(uploadFile("ERPV3/110105198707271517/f_cust_i/","a.txt","d:/abc.txt"));
	}
	
	
	/**
	 * 
	 * 功能说明：下载文件			
	 * yann  2015-10-12
	 * @param "fileUrl":"服务器上下载路径"，"destinationFolder":"下载文件后保存的路径"
	 * @return   
	 * @throws  
	 * 最后修改时间：最后修改时间
	 * 修改人：admin
	 * 修改内容：
	 * 修改注意点：
	 */
	public static void getDataFiles(String fileUrl,String destinationFolder) {
		FTPClient ftp = null;
		try {
			//创建一个新的ftp
			ftp = new FTPClient();
			//链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			//登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			//切换工作路径（服务器上某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);
			
			//循环切换文件路径
			String[] str = fileUrl.split("/");
			for (int i = 0; i < str.length; i++) {
				//切换工作路径（上传到某个文件夹）
				ftp.changeWorkingDirectory(str[i]);
			}
			//获取当前文件夹下面的所有文件
			//FTPFile[] files = ftp.listFiles();
			//编码转换
			String fileNameTmp = new String(str[str.length-1].getBytes("GBK"), "iso-8859-1");
			
			//创建文件(文件路径);
			File file = new File(destinationFolder);
			
			//文件夹如不存在则创建
			if(!file.exists()) {
				file.mkdirs();
			}
			
			//创建文件输出流(文件)
			FileOutputStream fos = new FileOutputStream(new File(destinationFolder + File.separator + str[str.length-1]));
			
			//从服务器检索命名文件并将其写入给定的OutputStream中
			boolean result = ftp.retrieveFile(fileNameTmp, fos);
			
			//关闭流
			fos.close();
		} catch (Exception e) {
//			e.printStackTrace();
		}finally{
			try{
				// 退出登录
				ftp.logout();
				//销毁连接
				ftp.disconnect();
			}catch(Exception e){
//				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * 功能说明：上传文件			
	 * yann  2015-10-12
	 * @param "fileUrl":"保存到服务器上的文件目录","fileName":"保存文件名称","sourceFile":"上传文件路径"
	 * @return   
	 * @throws  
	 * 最后修改时间：最后修改时间
	 * 修改人：admin
	 * 修改内容：
	 * 修改注意点：
	 */
	public static boolean uploadFile(String fileUrl,String fileName,String sourceFile) {

		FTPClient ftp = null;
		boolean result =false;
		try{
			//创建一个新的ftp
			ftp = new FTPClient();
			//链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			//登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			//创建一个文件保存上传文件（文件路径）
			File file = new File(sourceFile);
			
			//创建输入流（文件）
			FileInputStream fis = new FileInputStream(file);
			
			//切换工作路径（上传到某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);
			
			//截取要保存的文件目录
			String[] str = fileUrl.split("/");

			for (String string : str) {
				//创建新的文件夹
				ftp.makeDirectory(string);
				//切换工作路径(新创建的文件夹)
				ftp.changeWorkingDirectory(string);
			}

			//设置以二进制流的方式传输
			ftp.setFileType(FTP.BINARY_FILE_TYPE); // binary type
			//创建文件（文件名，文件），返回布尔类型
			result = ftp.storeFile(new String(fileName.getBytes("GBK"), "iso-8859-1"), fis);
			//关闭ftp
			fis.close();
		}catch(Exception e) {
//			e.printStackTrace();
		}finally{
			try{
				//退出登录
				ftp.logout();
				//销毁链接
				ftp.disconnect();
			}catch(Exception e){
//				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 
	 * 功能说明：上传文件			
	 * yann  2015-10-12
	 * @param "fileUrl":"保存到服务器上的文件目录","fileName":"保存文件名称","inputStream":"上传文件流"
	 * @return   
	 * @throws  
	 * 最后修改时间：最后修改时间
	 * 修改人：admin
	 * 修改内容：
	 * 修改注意点：
	 */
	public static void uploadFile(String fileUrl,String fileName,InputStream inputStream) {

		FTPClient ftp = null;
		try{
			//创建一个新的ftp
			ftp = new FTPClient();
			//链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			//登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			//切换工作路径（上传到某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);
			
			//截取要保存的文件目录
			String[] str = fileUrl.split("/");

			for (String string : str) {
				//创建新的文件夹
				ftp.makeDirectory(string);
				//切换工作路径(新创建的文件夹)
				ftp.changeWorkingDirectory(string);
			}
			
			//设置以二进制流的方式传输
			ftp.setFileType(FTP.BINARY_FILE_TYPE); // binary type
			//创建文件（文件名，文件），返回布尔类型
			boolean result = ftp.storeFile(new String(fileName.getBytes("GBK"), "iso-8859-1"), inputStream);
			
			//关闭流
			inputStream.close();
		}catch(Exception e) {
//			e.printStackTrace();
		}finally{
			try{
				//退出登录
				ftp.logout();
				//销毁链接
				ftp.disconnect();
			}catch(Exception e){
//				e.printStackTrace();
			}
		}
	}
	/**
	 * 
	 * 功能说明：上传文件			
	 * yann  2015-10-12
	 * @param "fileUrl":"保存到服务器上的文件目录","fileName":"保存文件名称","inputStream":"上传文件流"
	 * @return   
	 * @throws  
	 * 最后修改时间：最后修改时间
	 * 修改人：admin
	 * 修改内容：
	 * 修改注意点：
	 */
	public static boolean uploadFile(String fileUrl,String fileName,InputStream inputStream,JSONObject json) {
		
		FTPClient ftp = null;
		boolean result = false;
		try{
			//创建一个新的ftp
			ftp = new FTPClient();
			//链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			//登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			//切换工作路径（上传到某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);
			
			//截取要保存的文件目录
			String[] str = fileUrl.split("/");
			
			for (String string : str) {
				//创建新的文件夹
				ftp.makeDirectory(string);
				//切换工作路径(新创建的文件夹)
				ftp.changeWorkingDirectory(string);
			}
			
			//设置以二进制流的方式传输
			ftp.setFileType(FTP.BINARY_FILE_TYPE); // binary type
			//创建文件（文件名，文件），返回布尔类型
			result = ftp.storeFile(new String(fileName.getBytes("GBK"), "iso-8859-1"), inputStream);
			
			//关闭流
			inputStream.close();
		}catch(Exception e) {
//			e.printStackTrace();
		}finally{
			try{
				//退出登录
				ftp.logout();
				//销毁链接
				ftp.disconnect();
			}catch(Exception e){
//				e.printStackTrace();
			}
		}
		return result;
	}
	/**
	 * 
	 * 功能说明：删除文件			
	 * yann  2015-10-12
	 * @param "fileUrl":"保存到服务器上的文件目录","fileName":"保存文件名称","inputStream":"上传文件流"
	 * @return   
	 * @throws  
	 * 最后修改时间：最后修改时间
	 * 修改人：admin
	 * 修改内容：
	 * 修改注意点：
	 */
	public static boolean deleteFile(String fileUrl) {
		
		FTPClient ftp = null;
		boolean result = false;
		try{
			//创建一个新的ftp
			ftp = new FTPClient();
			//链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			//登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			//切换工作路径（上传到某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);
			//截取要保存的文件目录
			/*String[] str = fileUrl.split("/");
			
			for (String string : str) {
				//创建新的文件夹
				ftp.makeDirectory(string);
				//切换工作路径(新创建的文件夹)
				ftp.changeWorkingDirectory(string);
			}*/
			
			//设置以二进制流的方式传输
			ftp.setFileType(FTP.BINARY_FILE_TYPE); // binary type
			//创建文件（文件名，文件），返回布尔类型
			result = ftp.deleteFile(fileUrl);
		}catch(Exception e) {
			e.printStackTrace();
		}finally{
			try{
				//退出登录
				ftp.logout();
				//销毁链接
				ftp.disconnect();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return result;
	}
	/**
	 * 
	 * 功能说明：上传文件			
	 * yann  2015-10-12
	 * @param "fileUrl":"保存到服务器上的文件目录","fileName":"保存文件名称","inputStream":"上传文件流" map:后期扩充参数集合
	 * @return   boolean
	 * @throws  
	 * 最后修改时间：最后修改时间
	 * 修改人：admin
	 * 修改内容：
	 * 修改注意点：
	 */
	public static boolean uploadFile(String fileUrl,String fileName,InputStream inputStream,Map map) {
		boolean flag = false;
		FTPClient ftp = null;
		try{
			//创建一个新的ftp
			ftp = new FTPClient();
			//链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			//登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			//切换工作路径（上传到某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);
			
			//截取要保存的文件目录
			String[] str = fileUrl.split("/");
			
			for (String string : str) {
				//创建新的文件夹
				ftp.makeDirectory(string);
				//切换工作路径(新创建的文件夹)
				ftp.changeWorkingDirectory(string);
			}
			
			//设置以二进制流的方式传输
			ftp.setFileType(FTP.BINARY_FILE_TYPE); // binary type
			//创建文件（文件名，文件），返回布尔类型
			boolean result = ftp.storeFile(new String(fileName.getBytes("GBK"), "iso-8859-1"), inputStream);
			//关闭流
			inputStream.close();
			flag = result;
		}catch(Exception e) {
//			e.printStackTrace();
		}finally{
			try{
				//退出登录
				ftp.logout();
				//销毁链接
				ftp.disconnect();
			}catch(Exception e){
//				e.printStackTrace();
			}
		}
		return flag;
	}
	
	/**
	 * 
	 * 功能说明：下载文件			
	 * yann  2015-10-12
	 * @param "fileUrl":"服务器上下载路径"
	 * @return   
	 * @throws  
	 * 最后修改时间：最后修改时间
	 * 修改人：admin
	 * 修改内容：增加httprequest
	 * 修改注意点：位置
	 */
	public static void getDataFiles(HttpServletResponse response,HttpServletRequest request,String fileUrl) {
		FTPClient ftp = null;
		try {
			//创建一个新的ftp
			ftp = new FTPClient();
			//链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			//登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			//切换工作路径（上传到某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);
			fileUrl = URLDecoder.decode(fileUrl , "UTF-8");
			//循环切换文件路径
			String[] str = fileUrl.split("/");
			for (int i = 0; i < str.length; i++) {
				//切换工作路径（上传到某个文件夹）
				ftp.changeWorkingDirectory(str[i]);
			}
			//---------------费新玮2015-12-4修改
			//编码转换
			String fileNameTmp = new String(str[str.length-1].getBytes("GBK"), "iso-8859-1");
			//----------------
			//创建缓冲流读取ftp文件
			BufferedInputStream input = new BufferedInputStream(ftp.retrieveFileStream(fileNameTmp));
			//创建文件输出流
			OutputStream os = response.getOutputStream();
			//定义字节
			int read = 0; 
			byte[] buffBytes = new byte[1024];
			while ((read = input.read(buffBytes)) != -1) {
				os.write(buffBytes, 0, read); 
			}
			//清空流
			os.flush(); 
			//关闭流
			os.close(); 
		} catch (Exception e) {
//			e.printStackTrace();
		}finally{
			try{
				// 退出登录
				ftp.logout();
				//销毁链接
				ftp.disconnect();
			}catch(Exception e){
//				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * 功能说明：	从ftp服务器下载图片，并以BufferedImage返回
	 * 关福旺  2015-10-16 18:42:29
	 * @param "fileUrl":"服务器上下载路径"
	 * @return   
	 * @throws  
	 * 最后修改时间：最后修改时间
	 * 修改人：admin
	 * 修改内容：
	 * 修改注意点：
	 */
	public static BufferedImage getDataFilesByUrl(String fileUrl) {
		FTPClient ftp = null;
		
		BufferedImage file = null;
		try {
			//创建一个新的ftp
			ftp = new FTPClient();
			//链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			//登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			//切换工作路径（上传到某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);
			
			//循环切换文件路径
			String[] str = fileUrl.split("/");
			for (int i = 0; i < str.length; i++) {
				//切换工作路径（上传到某个文件夹）
				ftp.changeWorkingDirectory(str[i]);

			}
			
			//编码转换
			String fileNameTmp = new String(str[str.length-1].getBytes("GBK"), "iso-8859-1");
			
			//创建缓冲流读取ftp文件
			BufferedInputStream input = new BufferedInputStream(ftp.retrieveFileStream(fileNameTmp));
			file = ImageIO.read(input);
			

		} catch (Exception e) {
//			e.printStackTrace();
		}finally{
			try{
				// 退出登录
				ftp.logout();
				//销毁链接
				ftp.disconnect();
			}catch(Exception e){
//				e.printStackTrace();
			}
		}
		return file;
	}
	
	/** 
	 * httpclient上传文件 
	 * @author linwei 
	 * 
	 */
	
	public static String postFile(File file,String url) throws ClientProtocolException, IOException {
	
		FileBody bin = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		if(file != null) {
			bin = new FileBody(file);
		}
		
		StringBody username = new StringBody("13696900475");
		StringBody password = new StringBody("13696900475");
		
		//请记住，这边传递汉字会出现乱码，解决方法如下,设置好编码格式就好  
		//new StringBody("汉字",Charset.forName("UTF-8")));
		
		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("username", username);
		reqEntity.addPart("password", password);
		reqEntity.addPart("data", bin);
		
		httppost.setEntity(reqEntity);
		
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity resEntity = response.getEntity();
		if (resEntity != null) {
			InputStream in = resEntity.getContent();
		}
		if (resEntity != null) {
			resEntity.consumeContent();
		}
		return null;
	}
	

 
    /**
     * post请求
     * @param url         url地址
     * @param jsonParam     参数
     * @param noNeedResponse    不需要返回结果
     * @return
     */
    public static JSONObject httpPost(String url,JSONObject jsonParam, boolean noNeedResponse){
        //post请求返回结果
        DefaultHttpClient httpClient = new DefaultHttpClient();
        JSONObject jsonResult = null;
        HttpPost method = new HttpPost(url);
        try {
            if (null != jsonParam) {
                //解决中文乱码问题
                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /**请求发送成功，并得到响应**/
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    /**读取服务器返回过来的json字符串数据**/
                    str = EntityUtils.toString(result.getEntity());
                    if (noNeedResponse) {
                        return null;
                    }
                    /**把json字符串转换成json对象**/
                    jsonResult = JSONObject.fromObject(str);
                } catch (Exception e) {
//                	System.err.println("post请求提交失败:" + url);
//                	e.printStackTrace();
                }
            }
        } catch (IOException e) {
        	System.err.println("post请求提交失败:" + url);
//            e.printStackTrace();
        }
        return jsonResult;
    }
    public static Map getInputStreamAndFtp(String fileUrl){
    	Map json =new HashMap();
    	BufferedInputStream input=null;
    	FTPClient ftp = null;
    	try {
    		//创建一个新的ftp
    		ftp = new FTPClient();
    		//链接ftp服务（ftp地址）
    		ftp.connect(SERVER);
    		//登陆ftp（用户名，密码）
    		ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
    		//切换工作路径（上传到某个文件夹）
    		ftp.changeWorkingDirectory(FILE_PRE);
    		
    		//循环切换文件路径
    		String[] str = fileUrl.split("/");
    		for (int i = 0; i < str.length; i++) {
    			//切换工作路径（上传到某个文件夹）
    			ftp.changeWorkingDirectory(str[i]);
    			
    		}
    		
    		//编码转换
    		String fileNameTmp = new String(str[str.length-1].getBytes("GBK"), "ISO-8859-1");
    		//创建缓冲流读取ftp文件
    		input = new BufferedInputStream(ftp.retrieveFileStream(fileNameTmp));
    		json.put("BufferedInputStream", input);
    		json.put("ftp", ftp);
    		return json;
    	} catch (Exception e) {
//    		e.printStackTrace();
    	}
    	return json;
    }
    
    /**
     * 功能说明：删除文件
     * feixinwei  2016-3-23
     * @param 方法里面接收的参数及其含义
     * @return 该方法的返回值的类型，含义   
     * @throws  该方法可能抛出的异常，异常的类型、含义。
     * 最后修改时间：最后修改时间
     * 修改人：Administrator
     * 修改内容：
     * 修改注意点：
     */
    public static void deleteFileFtp(String filePath){
    	FTPClient ftp = null;
    	try {
    		//创建一个新的ftp
    		ftp = new FTPClient();
    		//链接ftp服务（ftp地址）
    		ftp.connect(SERVER);
    		//登陆ftp（用户名，密码）
    		ftp.login(USERNAME, PASSWORD);
    		ftp.deleteFile(filePath);
    		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				// 退出登录
				ftp.logout();
				//销毁连接
				ftp.disconnect();
			}catch(Exception e){
//				e.printStackTrace();
			}
		}
    }
}
