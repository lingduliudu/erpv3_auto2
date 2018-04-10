/**
 * 
 */
package com.apt.webapp.dao.fy;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * 功能说明：类功能说明 
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明	
 * @author feixinwei
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-11-18
 * Copyright zzl-apt
 */
public interface IFyCityDao {
	
	/**
	 * 功能说明：根据省份id查询下属城市			
	 * feixinwei  2015-11-18
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * 修改人：Administrator
	 * 修改内容：
	 * 修改注意点：
	 */
	public List<Map> findByProId(JSONObject jsonObject) throws Exception;
}
