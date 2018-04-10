package com.apt.webapp.service.ef;



import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import com.apt.webapp.model.ef.ZZLEfPaycontrol;

/**
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明
 * @author yuanhao
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-12
 * Copyright zzl-apt
 */
@WebService
public interface IZZLEfPaycontrolService  {
	/**
	 * 功能说明：通过线上订单获得zzl的当期明细
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 袁浩
	 * 创建日期：2015年10月20日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	List<Map> getCurrentByCrmControl(String crm_order_id,String string);
	/**
	 * 功能说明：修改当期状态为冻结
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 袁浩
	 * 创建日期：2015年10月20日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public void repayNormal(Map zzlEfpaycontrol);
	/**
	 * 功能说明：通过id获得对象
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 袁浩
	 * 创建日期：2015年10月20日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public ZZLEfPaycontrol findById(String string);
	/**
	 * 功能说明：冻结所有的金额
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 袁浩
	 * 创建日期：2015年10月20日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public boolean frozen(List<Map> zzlEfpaycontrols);
	/**
	 * 功能说明：通过时间条件处理异常的信息
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 袁浩
	 * 创建日期：2015年10月20日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> getErrorZzlControls(String time);
	/**
	 * 功能说明：判断是否属于异常的zzl明细
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 袁浩
	 * 创建日期：2015年10月20日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */  
	public boolean isError(Map error);
	/**
	 * 功能说明：得到错误zzl明细的必要信息
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 袁浩
	 * 创建日期：2015年10月20日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */  
	public Map getNeedErrorInfo(Map error);
	
	
}
