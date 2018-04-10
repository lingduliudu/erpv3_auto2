package com.apt.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.apt.util.ChkUtil;

/**
 * 文件操作类
 * 
 * @author Alan
 * 
 */
public class FileUtil {

	/**
	 * 使用文件通道的方式复制文件 如果新文件不存在 系统默认自己创建
	 * 
	 * @param s
	 *            源文件
	 * @param t
	 *            复制到的新文件
	 */
	public static String fileChannelCopy(File s, File t) {

		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			if (!t.exists()){
				//创建文件夹
				File foldFile = new File(t.getParent());
				if(!foldFile.exists()){
					
					foldFile.mkdirs();
				}
				//创建 文件
				t.createNewFile();
			}

			//复制文件
			fi = new FileInputStream(s);
			fo = new FileOutputStream(t);
			in = fi.getChannel();// 得到对应的文件通道
			out = fo.getChannel();// 得到对应的文件通道
			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道

		} catch (IOException e) {

			e.printStackTrace();
			return "";
		} finally {

			try {
				if(fi != null)
					fi.close();
				
				if(in != null)
					in.close();
				
				if(fo != null)
					fo.close();
				
				if(out != null)
					out.close();
				
			} catch (IOException e) {

				e.printStackTrace();
				return "";
			}

		}
		return t.getPath();
	}

	/**
	 * 删除单个文件
	 * @param sPath   被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String sPath) {

		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param sPath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) { 
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			}else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 获得文件后缀名
	 * @param file
	 * @return
	 */
	public static String getFileSuffix(File file,String fileName) {
		if(ChkUtil.isEmpty(fileName))
			fileName = file.getName();
			
	    String prefix = fileName.substring(fileName.lastIndexOf(".")+1);
		return prefix;
	}

	public static void main(String[] args) throws Exception {
		String sourceFilePath = "F:\\apache-tomcat-6.0.29\\webapps\\erpv3_client\\erpv3\\hr\\fileUpload\\testZip.zip";

		File s = new File(sourceFilePath);
//		System.out.println(getFileSuffix(s));;
//		File t = new File(
//				"F:\\apache-tomcat-6.0.29\\webapps\\erpv3_client\\erpv3\\hr\\fileUpload\\sb.zip");
//		if (t.exists()) {
//			t.createNewFile();
//		}
//		fileChannelCopy(s, t);
	}
}
