package com.apt.util.py;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.apt.util.SysConfig;
import com.apt.util.consts.MsgConsts;
import com.apt.util.rest.RestReqUtil;

import net.sf.json.JSONObject;

/**
 * 
 * 功能说明：关于鹏元的验证(主要是身份证)
 * 典型用法：
 * 特殊用法：
 * @author yuanhao
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-6-10
 * Copyright zzl-apt
 */
public class PengYuanUtil extends RestReqUtil{
	
	//鹏远身份证认证URL
		private final static String PY_CHK_IC_URL = SysConfig.getInstance().getProperty("PengYuanUrl") + "validateIcName";
		
		public static void main(String[] args) {
			System.out.println(validateIc("叙述B", "3402546465"));
		}
		
		/**
		 * 
		 * @param name	姓名
		 * @param ic	IC
		 * @return	{"responseCode":"1","info":"查询成功!","resultStatus":"1,2,3 （1表示成功其余均失败）"}		
		 */
		@SuppressWarnings("rawtypes")
		public static JSONObject validateIc (String name,String ic) {
			JSONObject resut = new JSONObject();
			try {
				LinkedHashMap<String, Object> reqMap = new LinkedHashMap<String, Object>();
				reqMap.put("name", name);
				reqMap.put("ic", ic);
				ResponseEntity entity = reqPyConnection(PY_CHK_IC_URL,reqMap, HttpMethod.POST);
				resut = JSONObject.fromObject(entity.getBody().toString());
				
			} catch (Exception e) {
				e.printStackTrace();
				resut.put(MsgConsts.RESPONSE_CODE, MsgConsts.ERROR_CODE);
				resut.put(MsgConsts.RESPONSE_INFO, MsgConsts.SYS_EXCEPTION);
			}
			return resut;
		}
		
		
	
}
