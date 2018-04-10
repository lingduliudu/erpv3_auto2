package com.apt.util.ip;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * 功能说明：获取IP地址 
 * @author 潘尚斌
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-5-12
 * Copyright zzl-apt
 */
public class IpUtil {
	
	/**
	 * 			获取IP地址
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip;
		try {
			ip = request.getHeader("x-forwarded-for");
			if (ip == null || ip.length() == 0
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("PRoxy-Client-IP");
			}
			if (ip == null || ip.length() == 0
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
			if (ip == null || ip.length() == 0
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			ip = "";
		}
		return ip;
	}
}
