 package com.apt.webapp.dao.impl.ef;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Repository;

import com.apt.util.ListTool;
import com.apt.util.NumberUtil;
import com.apt.webapp.base.dao.BaseHibernateDaoSupper;
import com.apt.webapp.dao.ef.IEfOrdersDao;
import com.apt.webapp.model.ef.EfApplay;
import com.apt.webapp.model.ef.EfOrders;

/**
 * 功能说明：理财订单表Dao层接口
 * @author 乔春峰
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-12
 * Copyright zzl-apt
 */
@Repository
public class EfOrdersDaoImpl extends BaseHibernateDaoSupper implements IEfOrdersDao{
	
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
	public String findLineAuthCode(String efOrderId){
		try {
			EfOrders ef = (EfOrders) findById(EfOrders.class, efOrderId);
			EfApplay efApply = (EfApplay)findById(EfApplay.class, ef.getEfApplayId());
			return efApply.getInvestAuzCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	/**
	 * 功能说明：获得总金额
	 * 乔春峰  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public Double findAllMoney(String efOrderId){
		String sql = "SELECT IFNULL(sum(money),0) allMoney from ef_fund_record where ef_order_id = '"+efOrderId+"' and record_status in(1)  GROUP BY  ef_order_id ";
		List<Map> list = queryBySqlReturnMapList(sql);
		return NumberUtil.parseDouble(list.get(0).get("allMoney"));
		
	}
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
	public Map  getEfApply(String efOrderId){
		try {
			EfOrders efOrder= (EfOrders) findById(EfOrders.class, efOrderId);
			String sql  = "SELECT * From pro_ef_product_detail where id='"+efOrder.getProDetailId()+"'";
			return queryBySqlReturnMapList(sql).get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 功能说明：查找到对应的冻结标
	 * yuanhao  2015-10-12
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public JSONObject findFreezenProduct(){
		String sql = "SELECT *from bg_auto_order_info where status='1' ";
		List<Map> list = queryBySqlReturnMapList(sql);
		if(ListTool.isNotNullOrEmpty(list)){
			return JSONObject.fromObject(list.get(0));
		}
		return new JSONObject();
	}
	
}
