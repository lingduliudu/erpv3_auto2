package com.apt.core.exception;

/**
 * 
 * ParameterException  异常拦截类  参数不合法
 * Version	  1.0
 * @author 潘尚斌
 * 2014-6-25
 * Copyright notice
 */
public class ParameterException extends RuntimeException {

	
    private static final long serialVersionUID = 6417641452178955756L;  
  
    public ParameterException() {  
        super();  
    }  
  
    public ParameterException(String message) {  
        super(message);  
    }  
  
    public ParameterException(Throwable cause) {  
        super(cause);  
    }  
  
    public ParameterException(String message, Throwable cause) {  
        super(message, cause);  
    }  
    
    
}
