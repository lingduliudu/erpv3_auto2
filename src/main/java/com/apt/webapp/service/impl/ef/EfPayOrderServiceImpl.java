package com.apt.webapp.service.impl.ef;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.apt.util.ChkUtil;
import com.apt.util.ListTool;
import com.apt.util.MessageTemplete;
import com.apt.util.StaticData;
import com.apt.util.WebServiceUtil;
import com.apt.util.date.DateUtil;
import com.apt.util.singature.SignatureUtil;
import com.apt.util.sms.SmsUtil;
import com.apt.webapp.dao.ef.IEfOrdersDao;
import com.apt.webapp.model.bg.ef.BgCustInfo;
import com.apt.webapp.model.bg.ef.BgCustomer;
import com.apt.webapp.model.bg.ef.BgEfOrders;
import com.apt.webapp.model.bg.ef.BgEfPaycontrol;
import com.apt.webapp.model.bg.ef.BgEfPayrecord;
import com.apt.webapp.model.bg.ef.BgSysMessage;
import com.apt.webapp.model.bg.ef.BgSysMessageContent;
import com.apt.webapp.model.bg.pkg.PkgOrder;
import com.apt.webapp.model.ef.EfApplay;
import com.apt.webapp.model.ef.EfAuthcodeRecord;
import com.apt.webapp.model.ef.EfCustConnection;
import com.apt.webapp.model.ef.EfCustInfo;
import com.apt.webapp.model.ef.EfOrders;
import com.apt.webapp.service.ef.IEfFundRecordService;
import com.apt.webapp.service.ef.IEfOrderService;
import com.apt.webapp.service.ef.IEfPaycontrolService;

import net.sf.json.JSONObject;


/**
 * 功能说明： 合同扫描件素材 service层   实现类
 * @author weiyingni
 * 修改人:
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-10-27
 * Copyright zzl-apt
 */
@Service
@Transactional
@WebService(serviceName = "efOrderService", endpointInterface = "com.apt.webapp.service.ef.IEfOrderService")
public class EfPayOrderServiceImpl implements IEfOrderService {
	//日志
	private Logger logger = LoggerFactory.getLogger(EfPayOrderServiceImpl.class);
	@Resource
	private IEfOrdersDao efOrdersDao;
	@Resource
	private IEfPaycontrolService efPaycontrolService;
	@Resource
	private IEfFundRecordService efFundRecordService;
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
	public boolean isOver(String id){
		boolean flag = false;
		try {
			String sql  = "SELECT * from ef_paycontrol where pay_status = 0 AND ef_order_id ='"+id+"' ";
			List list = efOrdersDao.queryBySqlReturnMapList(sql);
			if(ListTool.isNullOrEmpty(list)){
				EfOrders efOrder = (EfOrders) efOrdersDao.findById(EfOrders.class, id);
				//开始进行站内信通知
				try{
					BgCustomer bgCustomer  = (BgCustomer) efOrdersDao.findById(BgCustomer.class,efOrder.getCustId());
					BgSysMessageContent bsmc = new BgSysMessageContent();
					bsmc.setCreateTime(DateUtil.getCurrentTime());
					JSONObject paramJson = new JSONObject();
					paramJson.put("msgType", "3");
					paramJson.put("name", bgCustomer.getUsername());
					paramJson.put("number", ChkUtil.isEmpty(efOrder.getOrderNumber()) ? "" : efOrder.getOrderNumber());
					JSONObject resultJson  = MessageTemplete.getMsg(paramJson);
					bsmc.setMsgContent(resultJson.getString("content"));
					bsmc.setMsgTitle(resultJson.getString("title"));
					bsmc.setPublishId("");
					bsmc.setUpdateTime(DateUtil.getCurrentTime());
					bsmc.setPlatformType(Integer.parseInt(efOrder.getPlatformType()));
					efOrdersDao.add(bsmc);
					BgSysMessage bsm  = new BgSysMessage();
					bsm.setCustId(efOrder.getCustId());
					bsm.setEfOrderId(efOrder.getId());
					bsm.setEnabled(1);
					bsm.setMsgClass(2);
					bsm.setMsgContentId(bsmc.getId());
					bsm.setMsgType(5);
					bsm.setOrderId(efOrder.getCrmOrderId());
					bsm.setReadStatus(0);
					bsm.setPlatformType(Integer.parseInt(efOrder.getPlatformType()));
					efOrdersDao.add(bsm);
					logger.warn("站内信生成成功!订单id:"+efOrder.getId());
				}catch (Exception e) {
					logger.warn(e.getMessage(),e);
					logger.warn("站内信生成失败!订单id:"+efOrder.getId());
				}
				efOrder.setClearTime(DateUtil.getCurrentTime());
				efOrder.setPayStatus(2);
				efOrder.setClearType(2);
				//更新ef_orders
				efOrdersDao.update(efOrder);
				EfApplay efApplay = (EfApplay) efOrdersDao.findById(EfApplay.class, efOrder.getEfApplayId());
				efApplay.setStatus(EfApplay.STATUS_CLOSE_OFF);
				//更新ef_applay
				efOrdersDao.update(efApplay);
				//未看懂,   暂时屏蔽
				//String updateSql="update pkg_cust_eforder set status=0 where ef_applay_id='"+efOrder.getEfApplayId()+"'";
				//efOrdersDao.executeSql(updateSql);
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
		}
		return flag;
	}
	/**
	 * 功能说明：判断是剩余本金、剩余利息、剩余管理费是否为0
	 * chengwei 2016年9月20日 18:19:09
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public boolean isExistBgEfOrder(String ef_order_id){
		boolean flag = true;
		try {
		//	String sql  = "select sum(ifnull(bepc.surplus_principal,0)+ifnull(bepc.surplus_interest,0)+ifnull(bepc.surplus_management_amt,0)) money from bg_ef_paycontrol bepc,bg_ef_orders beo  where bepc.ef_order_id=beo.id and  bepc.live_status=1 and bepc.pay_status=0 and  beo.ef_order_id = '"+ef_order_id+"'";
		String sql  = "select bepc.* from bg_ef_paycontrol bepc,bg_ef_orders beo  where bepc.ef_order_id=beo.id and  bepc.live_status=1 and bepc.pay_status=0 and  beo.ef_order_id = '"+ef_order_id+"'";
			List<Map> list = efOrdersDao.queryBySqlReturnMapList(sql);
			if(ListTool.isNullOrEmpty(list)){
				return false;
			}else{
				//需要判断订单是否已经全部结清,如果没有的话,则需要全部结清,并
				String checkMoneySql  = "select sum(ifnull(bepc.surplus_principal,0)+ifnull(bepc.surplus_interest,0)+ifnull(bepc.surplus_management_amt,0)) money from bg_ef_paycontrol bepc,bg_ef_orders beo  where bepc.ef_order_id=beo.id and  bepc.live_status=1 and bepc.pay_status=0 and  beo.ef_order_id = '"+ef_order_id+"'";
				List<Map> checkMoneyList = efOrdersDao.queryBySqlReturnMapList(checkMoneySql);
				Double money = Double.parseDouble(checkMoneyList.get(0).get("money").toString());
				if (money == 0) { //说明订单的明细金额都是0了,需要特殊处理
					for(Map bepcMap:list){
						BgEfPaycontrol bepc = efPaycontrolService.findById(bepcMap.get("id").toString());
						bepc.setPayStatus(1);//结清
						efPaycontrolService.update(bepc); 
						BgEfOrders beo = (BgEfOrders) efOrdersDao.findById(BgEfOrders.class,bepcMap.get("ef_order_id").toString());
						if(beo.getPayStatus() ==1 || beo.getPayStatus() ==3){ //代表还款中或者债权转让
							beo.setPayStatus(2);
							efPaycontrolService.update(bepc); 
						}
					}
					return false;
				}
				return true;
			}
			/*Double money = Double.parseDouble(list.get(0).get("money").toString());
			if (money == 0) {
				flag = false;
			}
			if(money>0){
				flag = true;
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
		}
		return flag;
	}
	
	/**
	 * 功能说明：结清资产包订单
	 * chengwei 2016年9月1日 16:08:40
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public void clearPkgOrderList(List<Map> list){
		if(list !=null && list.size()>0){
			for(int i=0;i<list.size();i++){
				try{
					Map map = list.get(i);
					String id = map.get("id").toString();
					PkgOrder pkgOrder = (PkgOrder)efOrdersDao.findById(PkgOrder.class, id);
					pkgOrder.setClearType(1);
					pkgOrder.setOrderTradeStatus(4);
					pkgOrder.setFinishTime(DateUtil.getCurrentTime());
					efOrdersDao.update(pkgOrder);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 功能说明：结清资产包订单
	 * chengwei 2016年9月1日 16:08:40
	 * @return 
	 * 最后修改时间：最后修改时间
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception
	 */
	public boolean clearPkgOrder(String id){
		boolean flag = false;
		try {
			//查找出所对应的资产包ID
			String sql  = "select pkg_order_id from pkg_cust_eforder where 1=1 ";
			if (ChkUtil.isNotEmpty(id)) {
				sql  += " and ef_applay_id='"+id+"' ";
			}
			List<Map> list = efOrdersDao.queryBySqlReturnMapList(sql);
			//不为空
			if(ListTool.isNotNullOrEmpty(list)){
				for (Map map : list) {
					//查找该资产包是否有未结清的理财订单
					sql="select po.id,eo.ef_applay_id,pay_status " +
							" from ef_orders eo,pkg_cust_eforder pce,pkg_order po " +
							" where eo.ef_applay_id=pce.ef_applay_id " +
							" and pce.pkg_order_id=po.id " +
							" and eo.pay_status=1 " +
							" and po.id='"+map.get("pkg_order_id")+"'";
					List<Map> li = efOrdersDao.queryBySqlReturnMapList(sql);
					//如果没有未结清的理财订单，结清资产包订单
					if (ListTool.isNullOrEmpty(li)) {
						PkgOrder pkgOrder = (PkgOrder)efOrdersDao.findById(PkgOrder.class, map.get("pkg_order_id").toString());
						pkgOrder.setClearType(1);
						pkgOrder.setOrderTradeStatus(4);
						pkgOrder.setFinishTime(DateUtil.getCurrentTime());
						efOrdersDao.update(pkgOrder);
					}
				}
				flag = true;
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
		}
		return flag;
	}
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
		return efOrdersDao.findLineAuthCode(efOrderId);
	}
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
	public Double findAllMoney(String efOrderId){
		return efOrdersDao.findAllMoney(efOrderId);
	}
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
	public JSONObject findFreezenProduct(){
		return efOrdersDao.findFreezenProduct();
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
	public Map getEfApply(String efOrderId){
		return efOrdersDao.getEfApply(efOrderId);
	}
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
	public void updateAuthCode(String id,String authCode){
		try {
			EfOrders efOrder = (EfOrders) efOrdersDao.findById(EfOrders.class,id);
			efOrder.setInvestAuzCode(authCode);
			efOrdersDao.update(efOrder);
			EfApplay efApply = (EfApplay) efOrdersDao.findById(EfApplay.class,efOrder.getEfApplayId());
			efApply.setInvestAuzCode(authCode);
			efOrdersDao.update(efApply);
			EfAuthcodeRecord eacr = new EfAuthcodeRecord();
			eacr.setAuthCode(authCode);
			eacr.setCreateTime(DateUtil.getCurrentTime());
			eacr.setEfOrderId(efOrder.getId());
			efOrdersDao.add(eacr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("重新设置线下理财订单授权码失败,订单id:"+id);
		}
		
	}
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
	public Object findById(Class string, String bgEfOrderId){
		try {
			return efOrdersDao.findById(string, bgEfOrderId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
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
	public void update(Object eo){
		try {
			efOrdersDao.update(eo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
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
	public Map getNecessaryInfo(String id,String cust_Id){
		Map info  = new HashMap();
		//判断是否需要发邮件
		String emailSql = "select * from bg_sys_meg_receive_set where cust_id='"+cust_Id+"' and sync_email='1'";
		String msgSql  = "SELECT  online_type from crm_order co ,bg_ef_orders beo where co.id=beo.crm_order_id and beo.id='"+id+"' ";
		
		List emailList = efOrdersDao.queryBySqlReturnMapList(emailSql);
		List<Map> msgList = efOrdersDao.queryBySqlReturnMapList(msgSql);
		if(ListTool.isNotNullOrEmpty(msgList)){
			info.put("online_type",msgList.get(0).get("online_type")==null?"":msgList.get(0).get("online_type").toString());
		}
		if(ListTool.isNotNullOrEmpty(emailList) &&info.containsKey("online_type") && "1".equals(info.get("online_type").toString())){ //不是空的话则需要发送邮件
				info.put("mail", "");
		}
		return info;
	}
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
	@Transactional(isolation =Isolation.SERIALIZABLE)
	public void  lockRowForEforderAuth(Map efPaycontrol){
		String boc_url=StaticData.bocUrl; 
		efFundRecordService.addNormal(efPaycontrol);
		//开始撤销
		JSONObject bocJson =new JSONObject();
		//准备查找原始的授权码
		String autoCode = findLineAuthCode(efPaycontrol.get("lineEfOrderId").toString());
		bocJson.put("cardNbr", efPaycontrol.get("bank_account"));
		bocJson.put("authCode",autoCode);
		bocJson.put("sendAppName",StaticData.appName);
		bocJson.put("remark","");
		bocJson.put("signature",SignatureUtil.createSign());
		//请求撤销
		JSONObject bocResult = WebServiceUtil.sendPost(boc_url+"payProcess/bocBidCancel",new Object[]{bocJson});;
		if("0".equals(bocResult.getString("responseCode"))){
			logger.warn("撤销定投Boc投资订单时失败!线下理财id:"+efPaycontrol.get("lineEfOrderId"));
			JSONObject smsJson = new JSONObject();
			smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
			smsJson.put("text","定投Boc撤销投资订单时失败!线下理财id:"+efPaycontrol.get("lineEfOrderId"));
			SmsUtil.senErrorMsg(smsJson);
		}
		if("1".equals(bocResult.getString("responseCode"))||"2".equals(bocResult.getString("responseCode"))){ //2的意思是授权码已经撤销过了
			logger.warn("撤销定投Boc投资订单时成功!线下理财id:"+efPaycontrol.get("lineEfOrderId"));
			//查询可用金额进行冻结
			//获得冻结标信息
			JSONObject A1Json = findFreezenProduct();
			Double allMoney = findAllMoney(efPaycontrol.get("lineEfOrderId").toString());
			bocJson.clear();
			bocResult.clear();
			bocJson.put("accountId", efPaycontrol.get("bank_account"));
			bocJson.put("productId",A1Json.get("product"));
			bocJson.put("txAmount",allMoney);
			//查找对应的投标交易流水号
			String auto_serino = efPaycontrolService.findAutoSerino(efPaycontrol.get("cust_info_id").toString());
			bocJson.put("contOrderId", auto_serino);
			//查找期数
			bocJson.put("acqRes", "");
			bocJson.put("signature", SignatureUtil.createSign());
			bocJson.put("sendAppName",StaticData.appName);//
			bocJson.put("remark","");//保留域
			bocResult =  WebServiceUtil.sendPost(boc_url+"payProcess/bocIdAutoApply",new Object[]{bocJson});
			if("0".equals(bocResult.getString("responseCode"))){
				logger.warn("定投Boc重新投资订单时失败!线下理财id:"+efPaycontrol.get("lineEfOrderId"));
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", efPaycontrol.get("efOrderNumber"));
				smsJson.put("text","定投Boc重新投资订单时失败!线下理财id:"+efPaycontrol.get("lineEfOrderId"));
				SmsUtil.senErrorMsg(smsJson);
			}
			if("1".equals(bocResult.getString("responseCode"))){
				logger.warn("定投Boc重新投资订单时成功!线下理财id:"+efPaycontrol.get("lineEfOrderId"));
				//开始修改理财订单表
				if(bocResult.containsKey("response_map")){
					updateAuthCode(efPaycontrol.get("lineEfOrderId").toString(),bocResult.getJSONObject("response_map").getString("AUTHCODE"));
				}else{
					updateAuthCode(efPaycontrol.get("lineEfOrderId").toString(),bocResult.getString("AUTHCODE"));
				}
			}
		}
		
		
	}
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
	@Transactional(isolation =Isolation.SERIALIZABLE)
	public void  lockRowForEforderAuthAdvance(Map infoJson){
		String boc_url=StaticData.bocUrl; 
		efFundRecordService.addAdvance(infoJson);
		//开始撤销
		JSONObject bocJson =new JSONObject();
		//准备查找原始的授权码
		String autoCode = findLineAuthCode(infoJson.get("lineEfOrderId").toString());
		bocJson.put("cardNbr", infoJson.get("bank_account"));
		bocJson.put("authCode",autoCode);
		bocJson.put("sendAppName",StaticData.appName);
		bocJson.put("remark","");
		bocJson.put("signature",SignatureUtil.createSign());
		//请求撤销
		JSONObject bocResult = WebServiceUtil.sendPost(boc_url+"payProcess/bocBidCancel",new Object[]{bocJson});;
		if("0".equals(bocResult.getString("responseCode"))){
			logger.warn("撤销定投Boc投资订单时失败!线下理财id:"+infoJson.get("lineEfOrderId"));
			JSONObject smsJson = new JSONObject();
			smsJson.put("project_number", infoJson.get("efOrderNumber"));
			smsJson.put("text","定投Boc撤销投资订单时失败!线下理财id:"+infoJson.get("lineEfOrderId"));
			SmsUtil.senErrorMsg(smsJson);
		}
		if("1".equals(bocResult.getString("responseCode"))||"2".equals(bocResult.getString("responseCode"))){ //2的意思是授权码已经撤销过了
			logger.warn("撤销定投Boc投资订单时成功!线下理财id:"+infoJson.get("lineEfOrderId"));
			//查询可用金额进行冻结
			//获得冻结标信息
			JSONObject A1Json = findFreezenProduct();
			Double allMoney = findAllMoney(infoJson.get("lineEfOrderId").toString());
			bocJson.clear();
			bocResult.clear();
			bocJson.put("accountId", infoJson.get("bank_account"));
			bocJson.put("productId",A1Json.get("product"));
			bocJson.put("txAmount",allMoney);
			//查找对应的投标交易流水号
			String auto_serino = efPaycontrolService.findAutoSerino(infoJson.get("cust_info_id").toString());
			bocJson.put("contOrderId", auto_serino);
			//查找期数
			bocJson.put("acqRes", "");
			bocJson.put("signature", SignatureUtil.createSign());
			bocJson.put("sendAppName",StaticData.appName);//
			bocJson.put("remark","");//保留域
			bocResult =  WebServiceUtil.sendPost(boc_url+"payProcess/bocIdAutoApply",new Object[]{bocJson});
			if("0".equals(bocResult.getString("responseCode"))){
				logger.warn("定投Boc重新投资订单时失败!线下理财id:"+infoJson.get("lineEfOrderId"));
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", infoJson.get("efOrderNumber"));
				smsJson.put("text","定投Boc重新投资订单时失败!线下理财id:"+infoJson.get("lineEfOrderId"));
				SmsUtil.senErrorMsg(smsJson);
			}
			if("1".equals(bocResult.getString("responseCode"))){
				logger.warn("定投Boc重新投资订单时成功!线下理财id:"+infoJson.get("lineEfOrderId"));
				//开始修改理财订单表
				if(bocResult.containsKey("response_map")){
					updateAuthCode(infoJson.get("lineEfOrderId").toString(),bocResult.getJSONObject("response_map").getString("AUTHCODE"));
				}else{
					updateAuthCode(infoJson.get("lineEfOrderId").toString(),bocResult.getString("AUTHCODE"));
				}
			}
		}
		
		
	}
	//线上
	public void updateOnlineEfCustInfoIdByBgCustInfo() throws Exception{
		//先查询ef_cust_info_id为空的BgCustInfo
		StringBuffer sb1 = new StringBuffer();
		sb1.append(" select beo.cust_info_id,bc.cust_name,bci.cust_ic,bci.ef_cust_info_id");
		sb1.append(" 		from bg_ef_orders beo ");
		sb1.append(" 		join bg_customer bc on bc.id = beo.cust_id");
		sb1.append(" 		JOIN bg_cust_info bci on beo.cust_info_id = bci.id");
		sb1.append(" 		where ef_cust_info_id is null or ef_cust_info_id =''");
		sb1.append(" 		GROUP BY beo.cust_info_id");
		List<Map> list1 = efOrdersDao.queryBySqlReturnMapList(sb1.toString());
		for (Map map : list1) {
			String cust_info_id = map.get("cust_info_id").toString();
			String cust_ic = map.get("cust_ic").toString();
			BgCustInfo bgCustInfo = (BgCustInfo) efOrdersDao.findById(BgCustInfo.class, cust_info_id);
			//根据身份证查询ef_cust_info是否存在该客户
			String sql1="select id from ef_cust_info where CUST_IC = '"+cust_ic+"' or CUST_MOBILE ='"+bgCustInfo.getCustMobile()+"'";
			List<Map> list2 = efOrdersDao.queryBySqlReturnMapList(sql1);
			if (list2.size()>0) {
				String id1 = list2.get(0).get("id").toString();
				bgCustInfo.setEfCustInfoId(id1);
				EfCustInfo efCustInfo  = (EfCustInfo) efOrdersDao.findById(EfCustInfo.class, id1);
				efCustInfo.setCustIc(bgCustInfo.getCustIc());
				efCustInfo.setCustMobile(bgCustInfo.getCustMobile());
				efOrdersDao.update(efCustInfo);
				//根据ef_cust_info_id查询ef_cust_connection是否存在数据
				String sql2 = "select * from ef_cust_connection where cust_info_id = '"+id1+"'";
				List<Map> list3 = efOrdersDao.queryBySqlReturnMapList(sql2);
				if (list3.size()>0) {
					
				}else{
					EfCustConnection efCustConnection = new EfCustConnection();
					efCustConnection.setCreateTime(bgCustInfo.getCreateTime());
					efCustConnection.setCustInfoId(id1);
					efCustConnection.setEmployeeId("20088");
					efCustConnection.setStatus(1);
					efCustConnection.setCustType(1);
					efOrdersDao.add(efCustConnection);
				}
			}else{
				EfCustInfo EfCustInfo = new EfCustInfo();
				EfCustInfo.setBirthday(bgCustInfo.getBirthday());
				EfCustInfo.setCreateTime(bgCustInfo.getCreateTime());
				EfCustInfo.setCustIc(bgCustInfo.getCustIc());
				EfCustInfo.setCustMobile(bgCustInfo.getCustMobile());
				EfCustInfo.setCustName(bgCustInfo.getCustName());
				efOrdersDao.add(EfCustInfo);
				
				bgCustInfo.setEfCustInfoId(EfCustInfo.getId());
				
				EfCustConnection efCustConnection = new EfCustConnection();
				efCustConnection.setCreateTime(bgCustInfo.getCreateTime());
				efCustConnection.setCustInfoId(EfCustInfo.getId());
				efCustConnection.setEmployeeId("20088");
				efCustConnection.setStatus(1);
				efCustConnection.setCustType(1);
				efOrdersDao.add(efCustConnection);
			}
			efOrdersDao.update(bgCustInfo);
		}
	}
	//线下
	public void updateLineEfCustInfoIdByBgCustInfo() throws Exception{
		//先查询ef_cust_info_id为空的BgCustInfo
		StringBuffer sb1 = new StringBuffer();
		sb1.append(" select beo.cust_info_id,bc.cust_name,bci.cust_ic,bci.ef_cust_info_id");
		sb1.append(" 		from ef_orders beo ");
		sb1.append(" 		join bg_customer bc on bc.id = beo.cust_id");
		sb1.append(" 		JOIN bg_cust_info bci on beo.cust_info_id = bci.id");
		sb1.append(" 		where ef_cust_info_id is null or ef_cust_info_id =''");
		sb1.append(" 		GROUP BY beo.cust_info_id");
		List<Map> list1 = efOrdersDao.queryBySqlReturnMapList(sb1.toString());
		for (Map map : list1) {
			String cust_info_id = map.get("cust_info_id").toString();
			String cust_ic = map.get("cust_ic").toString();
			BgCustInfo bgCustInfo = (BgCustInfo) efOrdersDao.findById(BgCustInfo.class, cust_info_id);
			//根据身份证查询ef_cust_info是否存在该客户
			String sql1="select id from ef_cust_info where CUST_IC = '"+cust_ic+"' or CUST_MOBILE ='"+bgCustInfo.getCustMobile()+"'";
			List<Map> list2 = efOrdersDao.queryBySqlReturnMapList(sql1);
			if (list2.size()>0) {
				String id1 = list2.get(0).get("id").toString();
				bgCustInfo.setEfCustInfoId(id1);
				EfCustInfo efCustInfo  = (EfCustInfo) efOrdersDao.findById(EfCustInfo.class, id1);
				efCustInfo.setCustIc(bgCustInfo.getCustIc());
				efCustInfo.setCustMobile(bgCustInfo.getCustMobile());
				efOrdersDao.update(efCustInfo);
				//根据ef_cust_info_id查询ef_cust_connection是否存在数据
				String sql2 = "select * from ef_cust_connection where cust_info_id = '"+id1+"'";
				List<Map> list3 = efOrdersDao.queryBySqlReturnMapList(sql2);
				if (list3.size()>0) {
					
				}else{
					EfCustConnection efCustConnection = new EfCustConnection();
					efCustConnection.setCreateTime(bgCustInfo.getCreateTime());
					efCustConnection.setCustInfoId(id1);
					efCustConnection.setEmployeeId("20088");
					efCustConnection.setStatus(1);
					efCustConnection.setCustType(1);
					efOrdersDao.add(efCustConnection);
				}
			}else{
				EfCustInfo EfCustInfo = new EfCustInfo();
				EfCustInfo.setBirthday(bgCustInfo.getBirthday());
				EfCustInfo.setCreateTime(bgCustInfo.getCreateTime());
				EfCustInfo.setCustIc(bgCustInfo.getCustIc());
				EfCustInfo.setCustMobile(bgCustInfo.getCustMobile());
				EfCustInfo.setCustName(bgCustInfo.getCustName());
				efOrdersDao.add(EfCustInfo);
				
				bgCustInfo.setEfCustInfoId(EfCustInfo.getId());
				
				EfCustConnection efCustConnection = new EfCustConnection();
				efCustConnection.setCreateTime(bgCustInfo.getCreateTime());
				efCustConnection.setCustInfoId(EfCustInfo.getId());
				efCustConnection.setEmployeeId("20088");
				efCustConnection.setStatus(1);
				efCustConnection.setCustType(2);
				efOrdersDao.add(efCustConnection);
			}
			efOrdersDao.update(bgCustInfo);
		}
	}
}
