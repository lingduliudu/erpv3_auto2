package com.apt.webapp.service.auto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apt.util.ChkUtil;
import com.apt.util.NumberUtil;
import com.apt.util.StaticData;
import com.apt.webapp.dao.bg.customer.ICustomerDao;
import com.apt.webapp.model.bg.ef.BgCustInfo;
import com.apt.webapp.model.bg.ef.BgCustomer;
import com.apt.webapp.service.crm.IPsCheckMoneyService;



/**
 * 功能说明：用来代扣的工具类
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明	
 * @author S
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-5-25
 * Copyright zzl-apt
 */
@Service
@Transactional
public class transToZzl {
	private static Logger logger = LoggerFactory.getLogger(transToZzl.class);
	@Resource
	private ICustomerDao customerDao;
	@Resource
	private IPsCheckMoneyService psCheckMoneyService;
	/**
	 * 功能说明：连接poc的接口		
	 * yuanhao  2015-5-29
	 * @param method 接口名 paramMap 参数集合 extendUrl 扩展url
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public synchronized static String connectToPoc(String method,Map paramMap) {
		if(paramMap==null){
			paramMap=new HashMap();
		}
		logger.warn("本次富友地址:"+method+",请求参数:"+JSONObject.fromObject(paramMap).toString());
		//sysNumber 默认 本系统
		paramMap.put("sysNumber", "erpv3_auto");
		String content="";
		HttpClient httpClient=new HttpClient();
		PostMethod postMethod = new PostMethod(StaticData.pocUrl+method+".html");
		postMethod.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");
		//遍历参数
		if(paramMap!=null){
		Set<String>keys=paramMap.keySet();
		NameValuePair[] data = new NameValuePair[keys.size()];
		Iterator it=keys.iterator();
		int i=0;
		while(it.hasNext()){
			String key=String.valueOf(it.next());
			data[i]=new NameValuePair(key,paramMap.get(key).toString());
			i++;
		}
		postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8"); 
	    postMethod.setRequestBody(data);
		}
	    try {
	    	httpClient.executeMethod(postMethod);
	    	//获得响应内容String格式
			content=postMethod.getResponseBodyAsString();
			logger.warn("本次富友操作返回结果:"+content);
			if(method.equals("BalanceAction")){//说明是进行了余额查询
				try{
					JSONObject json=JSONObject.fromObject(content);
					if("yes".equals(json.getString("success"))){//本次请求成功
						return json.getString("ca_balance");
					}else{
						logger.warn("本次富友查询余额失败!错误原因:"+json.getString("message"));
						return "0";
					}
				}catch(Exception e){
					return "0";
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	   return content;
	}
	/**
	 * 功能说明：连接poc的接口		
	 * yuanhao  2015-5-29
	 * @param method 接口名 paramMap 参数集合 extendUrl 扩展url 1,提前结清2提前收回3,逾期还款4,部分逾期,5正常还款	
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public  synchronized JSONObject payByPocDaikou(String custInfoId,String amt,String type,String crm_order_id){
		JSONObject resultJson = new JSONObject();
		resultJson.put("responseCode", "0");
		resultJson.put("code", "");
		if(StaticData.closePoc){
			resultJson.put("responseCode", "1");
			return resultJson;
			
		}
		try {
			Map map = customerDao.getDaikouInfo(custInfoId);
			BgCustInfo info  = (BgCustInfo) customerDao.findById(BgCustInfo.class,custInfoId);
			String bankno=map.get("bankno").toString();//总行代码
			//判断总行代码是否是招商银行的,如果是则直接不通过
			if(!ChkUtil.isEmpty(StaticData.StopBank)&&bankno.contains(StaticData.StopBank)){
				logger.warn("本卡银行已被暂时停止使用!");
				return resultJson;
			}
			Map paramMap=new HashMap();
			paramMap.put("bankno",map.get("bankno"));
			//paramMap.put("branchnm",map.get("branchnm"));
			paramMap.put("accntno",map.get("accntno"));
			paramMap.put("accntnm",map.get("accntnm"));
			paramMap.put("certno",map.get("certno"));
			if(info!=null){
				paramMap.put("certno",info.getCustIc());
			}
			paramMap.put("amt",NumberUtil.duelmoney(amt));
			paramMap.put("mobile",map.get("mobile"));
			String result=connectToPoc("withHolding",paramMap);
			//开始产生资金记录
			JSONObject checkMoneyJson = new JSONObject();
			checkMoneyJson.put("cardNo",map.get("accntno"));
			checkMoneyJson.put("cust_info_id",custInfoId);			//客户id
			checkMoneyJson.put("money",amt);					// 金额
			checkMoneyJson.put("money_type","2"); 			// 1金账户2,银行卡3,现场交钱4,电子账户
			checkMoneyJson.put("operation_type","2"); 		//1入账2出账
			checkMoneyJson.put("status","0");				//1成功0失败
			checkMoneyJson.put("person_type","2");			//1,投资人2借款人3,邀请人
			checkMoneyJson.put("type",type);					//1,提前结清2提前收回3,逾期还款4,部分逾期,5正常还款	
			checkMoneyJson.put("crm_order_id", crm_order_id);
			String checkMoneyId = psCheckMoneyService.save(checkMoneyJson);
			logger.warn("用户"+custInfoId+",本次代扣返回码:"+result);
			resultJson.put("code", result);
			if("0000".equals(result)){
				//资金记录修改
				psCheckMoneyService.updateStatusById(checkMoneyId);
				resultJson.put("responseCode", "1");
			}else{
				resultJson.put("responseCode", result);
			}
		}catch(Exception e){
			logger.warn(e.getMessage(),e);
		}
		return resultJson;
		}
}
