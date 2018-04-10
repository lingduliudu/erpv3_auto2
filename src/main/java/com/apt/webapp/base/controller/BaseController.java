package com.apt.webapp.base.controller;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.web.bind.annotation.ExceptionHandler;

import com.apt.core.exception.BusinessException;
import com.apt.core.exception.ParameterException;
import com.apt.util.singature.SignatureUtil;


/**
 * 
 * 功能说明：控制器基类
 * 典型用法：异常抛出处理等
 * 特殊用法：该类在系统中的特殊用法的说明	
 * @author panye
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-5-12
 * Copyright zzl-apt
 */
public abstract class BaseController {

	
	/**
	 * 
	 * 功能说明：展示捕获及处理		
	 * panye  2015-5-13
	 * @param 
	 * @return   
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	@ExceptionHandler  
    public String exp(HttpServletRequest request, Exception ex,HttpServletResponse res) throws Exception {
        
		res.setContentType("text/html;charset=utf-8");
        request.setAttribute("ex", ex);  
          
        if(ex instanceof BusinessException) {
            return "500";  
            
        }else if(ex instanceof ParameterException) {  
            return "404";  
            
        }else {  
        	
            return "error";  
        } 
    }
	
	
	/**
	 * 
	 * 功能说明：对外接口验签		
	 * panye  2015-5-13
	 * @param    signature 验签字符串
	 * @return   boolean
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public boolean isEffectiveAccess(String signature){
    	JSONObject json = JSONObject.fromObject(signature);
    	String applyId = json.getString("apply_id");
    	String time = json.getString("time");
    	return SignatureUtil.chkSignature(applyId, time, signature);
    }
	
}
