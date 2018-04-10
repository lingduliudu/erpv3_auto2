package com.apt.webapp.dao.impl.bg.customer;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Repository;

import com.apt.util.ChkUtil;
import com.apt.util.CommonFormatUtil;
import com.apt.util.DataTableUtils;
import com.apt.util.consts.JudgeByConsts;
import com.apt.webapp.base.dao.BaseHibernateDaoSupper;
import com.apt.webapp.dao.bg.customer.ICustomerDao;

/**
 * 
 * 功能说明：v3的客户表       dao层实现类
 * weiyz  
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-12
 */
@Repository
public class CustomerDaoImpl extends BaseHibernateDaoSupper implements ICustomerDao {

	
	/**
	 * 获得该用户富有要用到的信息
	 * 功能说明：该方法实现的功能			
	 * @author sky
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2015-12-8
	 * 修改内容：
	 * 修改注意点：
	 */
	public Map getDaikouInfo(String custInfoId){
		String sql = "SELECT "+ 
					"bci.cust_ic  certno, "+
					"sbc.card_number  accntno, "+
					"sbc.bank_subbranch  branchnm, "+
					"sbc.cust_name  accntnm, "+
					"sbc.bank_number  bankno, "+
					"bci.fy_account  mobile "+
					"from  "+
					"sys_bank_card sbc, "+ 
					"bg_cust_info bci "+ 
					"where  "+
					"sbc.cust_info_id =bci.id and  "+
					"source =1 and  "+
					"bind_status=1 AND "+
					"cust_info_id ='"+custInfoId+"'";
		return queryBySqlReturnMapList(sql).get(0);
	}
}
