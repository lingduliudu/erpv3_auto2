package com.apt.webapp.service.ef;



import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSONObject;


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
public interface IEfOrderService  {
	/**
	 * 功能说明：判断是否可以结束
	 * 乔春峰  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public boolean isOver(String string);
	/**
	 * 功能说明：判断是否有债权订单
	 * chengwei 2016年9月20日 18:19:09
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public boolean isExistBgEfOrder(String ef_order_id);
	/**
	 * 功能说明：查找到对应的冻结标
	 * 乔春峰  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public JSONObject findFreezenProduct();
	/**
	 * 功能说明：结清资产包订单
	 * chengwei 2016年9月1日 16:08:26
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public void clearPkgOrderList(List<Map> list);
	/**
	 * 功能说明：结清资产包订单
	 * chengwei 2016年9月1日 16:08:26
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public boolean clearPkgOrder(String string);
	/**
	 * 功能说明：查找原始的授权码
	 * 乔春峰  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public String findLineAuthCode(String string);
	/**
	 * 功能说明：查找原始的授权码
	 * 乔春峰  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public Double findAllMoney(String string);
	/**
	 * 功能说明：线下理财申请
	 * yuanhao  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public Map getEfApply(String efOrderId);
	/**
	 * 功能说明：修改理财订单的授权码并记录
	 * yuanhao  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public void updateAuthCode(String string,String authCode);
	/**
	 * 功能说明：查找对应的实体类
	 * yuanhao  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public Object findById(Class string, String bgEfOrderId);
	/**
	 * 功能说明：更新的实体类
	 * yuanhao  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public void update(Object eo);
	/**
	 * 功能说明：一些站内信和邮件的必要信息
	 * yuanhao  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public Map getNecessaryInfo(String id,String cust_Id);
	/**
	 * 功能说明：行锁防止授权码被覆盖
	 * yuanhao  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public void  lockRowForEforderAuth(Map efPaycontrol);
	
	/**
	 * 功能说明：行锁防止授权码被覆盖(提前结清)
	 * yuanhao  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public void  lockRowForEforderAuthAdvance(Map infoJson);
	/**
	 * 功能说明：更新线上客户信息
	 * chengwei  2017年8月7日 15:29:20
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public void updateOnlineEfCustInfoIdByBgCustInfo() throws Exception;
	/**
	 * 功能说明：更新线下客户信息
	 * chengwei  2017年8月7日 15:29:18
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public void updateLineEfCustInfoIdByBgCustInfo() throws Exception;
	
}
