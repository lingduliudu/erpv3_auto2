package com.apt.webapp.service.impl.ef;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.jws.WebService;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apt.util.ChkUtil;
import com.apt.util.ListTool;
import com.apt.util.NumberFormat;
import com.apt.util.StaticData;
import com.apt.util.pocTool;
import com.apt.util.arith.ArithUtil;
import com.apt.util.date.DateUtil;
import com.apt.util.http.HttpUtil;
import com.apt.util.sms.SmsUtil;
import com.apt.webapp.base.dao.ISecondHibernateDaoSupper;
import com.apt.webapp.dao.bg.ef.IBgEfOrderDao;
import com.apt.webapp.dao.crm.ICrmOrderDao;
import com.apt.webapp.dao.crm.ICrmPaycontrolDao;
import com.apt.webapp.dao.crm.ICrmPayrecoderDao;
import com.apt.webapp.dao.ef.IEfPaycontrolDao;
import com.apt.webapp.dao.ef.IEfPayrecordDao;
import com.apt.webapp.dao.ef.IZZLEfPaycontrolDao;
import com.apt.webapp.model.bg.ef.BgCustomer;
import com.apt.webapp.model.bg.ef.BgEfOrders;
import com.apt.webapp.model.bg.ef.BgEfPaycontrol;
import com.apt.webapp.model.bg.ef.BgEfPayrecord;
import com.apt.webapp.model.crm.CrmOrder;
import com.apt.webapp.model.ef.EfOrders;
import com.apt.webapp.service.bg.ef.IBgEfOrderService;



/**
 * 贝格产品
 * 功能说明：类功能说明 
 * 典型用法：该类的典型使用方法和用例
 * 特殊用法：该类在系统中的特殊用法的说明	
 * @author sky
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-12-11
 * Copyright zzl-apt
 */
@Service
@Transactional
@WebService(serviceName = "bgEfOrderService", endpointInterface = "com.apt.webapp.service.bg.ef.IBgEfOrderService")
public class BgEfOrderServiceImpl implements IBgEfOrderService{
	//日志
	private Logger logger = LoggerFactory.getLogger(BgEfOrderServiceImpl.class);
	@Resource
	private IBgEfOrderDao bgEfOrderDao;
	@Resource
	private IEfPaycontrolDao efPaycontrolDao;
	@Resource
	private IEfPayrecordDao efPayrecordDao;
	@Resource
	private IZZLEfPaycontrolDao izzlEfPaycontrolDao;
	@Resource
	private ISecondHibernateDaoSupper secondHibernateDao;
	/**
	 * 功能说明：直接执行sql
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
	public void executeSql(String sql){
		bgEfOrderDao.executeSql(sql);
	}
	/**
	 * 功能说明：普通的还款结束后的转换
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 袁浩
	 * 创建日期：2015年10月20日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws  
	 */
	public void normalRepay(Map efPaycontrol){
		//判断是不是新手标如果是新手标就不需要copy了
		if(!efPaycontrol.containsKey("tasteOrder")){
			//状态不能是空,并且是线上的
			if(!ChkUtil.isEmpty(efPaycontrol.get("clearing_channel"))&&"2".equals(efPaycontrol.get("clearing_channel").toString())){
				//1.转移理财明细到zzl-ef
				izzlEfPaycontrolDao.copyEfControl(efPaycontrol);
			}
		}
		//2,结束掉该笔理财明细
		efPaycontrolDao.updateForNormal(efPaycontrol);
		//3.产生理财还款记录
		efPayrecordDao.updateForNormal(efPaycontrol);
		
	}
	/**
	 * 功能说明：BOC普通的结清
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 袁浩
	 * 创建日期：2015年10月20日 
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 * @throws  
	 */
	public void normalRepayByModel(Map efPaycontrol){
		//判断是不是新手标如果是新手标就不需要copy了
		if(!efPaycontrol.containsKey("tasteOrder")){
			//状态不能是空,并且是线上的
			if(!ChkUtil.isEmpty(efPaycontrol.get("clearing_channel"))&&"2".equals(efPaycontrol.get("clearing_channel").toString())){
				//1.转移理财明细到zzl-ef
				izzlEfPaycontrolDao.copyEfControl(efPaycontrol);
			}
		}
		//2,结束掉该笔理财明细
		efPaycontrolDao.updateForNormalByModel(efPaycontrol);
		//3.产生理财还款记录
		efPayrecordDao.addByModel(efPaycontrol);
	}
	/**
	 * 功能说明： 获得线上当日待还明细
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
	public List<Map> getCurrentControl(Map efMap){
		return bgEfOrderDao.getCurrentControls(efMap);
	}
	/**
	 * 功能说明： 获得剩余金额 ,
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
	public List<Map> getControls(String efOrderId){
		return bgEfOrderDao.getControls(efOrderId);
	}
	/**
	 * 功能说明： 获得剩余金额 ,
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
	public Map getBgEfOrdersById(String efOrderId){
		try {
			String sql ="select * from bg_ef_orders where id ='"+efOrderId+"'";
			return bgEfOrderDao.queryBySqlReturnMapList(sql).get(0);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("获得订单本金时报错:订单id="+efOrderId);
		}
		return null;
	}
	/**
	 * 功能说明： 获得自动债权转让签约订单号
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 程伟
	 * 创建日期：2017年5月25日 10:07:30
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	public Map getBgAutoTransferAuth(String custInfoId){
		try {
			String sql ="select cust_info_id,serino from bg_auto_transfer_auth where cust_info_id='"+custInfoId+"'";
			return bgEfOrderDao.queryBySqlReturnMapList(sql).get(0);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("获得自动债权转让签约订单号时报错:custInfoId="+custInfoId);
		}
		return null;
	}
	/**
	 * 功能说明： 获得直投boc当日待还明细
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
	public List<Map> getCurrentImmeControlsBoc(){
		return bgEfOrderDao.getCurrentImmeControlsBoc();
	}
	public String getBgAutoTransferAuth(){
		return bgEfOrderDao.getBgAutoTransferAuth();
	}
	/**
	 * 功能说明： 获得线上当日待还明细(红包)
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
	public List<Map> getCurrentImmeControlsBocRedAccount(){
		return bgEfOrderDao.getCurrentImmeControlsBocRedAccount();
	}
	/**
	 * 功能说明： 获得Poc当日待还明细
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
	public List<Map> getCurrentControlsPoc(){
		return bgEfOrderDao.getCurrentControlsPoc();
	}
	/**
	 * 功能说明： 获得hf当日待还明细
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
	public List<Map> getCurrentControlsHF(){
		return bgEfOrderDao.getCurrentControlsHF();
	}
	public void executeBatchUpdateOperateType(){
		bgEfOrderDao.executeBatchUpdateOperateType();
	}
	/**
	 * 功能说明： 获得直投Poc当日待还明细
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
	public List<Map> getCurrentImmeControlsPoc(){
		return bgEfOrderDao.getCurrentImmeControlsPoc();
	}
	public List<Map> getCurrentImmeControlsPocById(String efPaycontrolId){
		return bgEfOrderDao.getCurrentImmeControlsPocById(efPaycontrolId);
	}
	/**
	 * 功能说明： 获得定投BOC当日待还明细
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
	public List<Map> getCurrentDeteControlsBoc(){
		return bgEfOrderDao.getCurrentDeteControlsBoc();
	}
	/**
	 * 功能说明： 获得定投BOC当日待还明细(红包)
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
	public List<Map> getCurrentDeteControlsBocRedAccount(){
		return bgEfOrderDao.getCurrentDeteControlsBocRedAccount();
	}
	/**
	 * 功能说明： 获得定投POC当日待还明细
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
	public List<Map> getCurrentDeteControlsPoc(){
		return bgEfOrderDao.getCurrentDeteControlsPoc();
	}
	/**
	 * 功能说明：判断是不是最后一期
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
	public boolean isLast(Map efPaycontrol){
		return bgEfOrderDao.isLast(efPaycontrol);
	}
	/**
	 * 功能说明：获得邀请人的信息
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
	public Map getRefereeInfoMap(String cust_info_id){
		return bgEfOrderDao.getRefereeInfoMap(cust_info_id);
		
	}
	/**
	 * 功能说明：获得当天的体验标
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
	public List<Map> getCurrentOnLineTasteOrderControls(){
		return bgEfOrderDao.getCurrentOnLineTasteOrderControls();
	}
	/**
	 * 功能说明：开始记录利息
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
	public JSONObject cleanInterest(Map efPaycontrol){
		return bgEfOrderDao.cleanInterest(efPaycontrol);
		
	}
	//
	/**
	 * 功能说明：开始记录利息
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
	public JSONObject cleanEfPaycontrol(Map efPaycontrol){
		return bgEfOrderDao.cleanEfPaycontrol(efPaycontrol);
		
	}
	/**
	 * 功能说明：自动更新预授权poc的批量文件并处理
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
	public List<Map> findPocFileRecord(String type) {
		String sql = "";
		if("1".equals(type)){
			sql = "SELECT * from poc_batch_record where process='1' and process_result='1' and type in ('1','-1') and (preauth_file_name!='' or  preauth_file_name is not null ) GROUP BY preauth_file_name;";
		}
		if("2".equals(type)){
			sql = "SELECT * from poc_batch_record where process='2' and process_result='1' and type in ('1','-1') and (transferbu_file_name!='' or  transferbu_file_name is not null )  and preauth_file_name!='' and  preauth_file_name is not null     GROUP BY transferbu_file_name;";
		}
		if("3".equals(type)){
			sql = "SELECT * from poc_batch_record where process='3' and process_result='1' and type in ('1','-1') and (freeze_file_name!='' or  freeze_file_name is not null )  GROUP BY freeze_file_name;";
		}
		if("5".equals(type)){
			sql = "SELECT * from poc_batch_record where process='2' and process_result='1' and type in ('1','-1') and (transferbu_file_name!='' or  transferbu_file_name is not null ) and (preauth_file_name ='' or  preauth_file_name is  null )  GROUP BY transferbu_file_name;";
		}
		if("".equals(sql)){
			return new ArrayList<Map>();
		}else{
			return bgEfOrderDao.queryBySqlReturnMapList(sql);
		}
	}
	/**
	 * 功能说明：查找失败数据进行2次处理
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
	public List<Map> findUploadFileFailRecords() {
		List<Map> resultList = new ArrayList<Map>();
		String sql = "SELECT * from poc_batch_record where process='2' and process_result='0' and type in ('1','-1') and (transferbu_file_name='' or  transferbu_file_name is  null ) and (preauth_file_name ='' or  preauth_file_name is  null )  GROUP BY unique_no  ORDER BY create_time desc ";
		List<Map> list =  bgEfOrderDao.queryBySqlReturnMapList(sql);
		if(ListTool.isNotNullOrEmpty(list)){
			//查找是否已经产生文件了
			//查找这条数据的上一个和下一个文件
			//是否跨天
			for(Map baseMap:list){
				String preSql ="SELECT * from poc_batch_record where unique_no!='"+baseMap.get("unique_no").toString()+"' and type in (1,-1) and create_time<'"+baseMap.get("create_time").toString()+"' and ( transferbu_file_name !='' and transferbu_file_name is not null)   ORDER BY create_time desc  limit 1 ";
				List<Map> preList = bgEfOrderDao.queryBySqlReturnMapList(preSql);
				if(ListTool.isNullOrEmpty(preList)){ // 找不到前面的数据
					continue;
				}
				String preFileName = preList.get(0).get("transferbu_file_name").toString(); //前面的文件名
				//将这个前置文件数量+1 看是否可以找到
				String fileName = preFileName.split(Pattern.quote("_"))[2];
				String nextFileName=preFileName.split(Pattern.quote("_"))[0]+preFileName.split(Pattern.quote("_"))[1]+NumberFormat.formatDouble(Double.parseDouble(fileName),"0000");
				String nextSql = "SELECT * from poc_batch_record where transferbu_file_name='"+nextFileName+"' limit 1";
				List<Map> nextList = bgEfOrderDao.queryBySqlReturnMapList(nextSql);
				//如果找到了下一个文件说明这个是可以直接进行重新上传的
				if(ListTool.isNotNullOrEmpty(nextList)){
					resultList.add(baseMap);
					continue;
				}else{ //如果没有找到下个文件则去查找poc的ftp服务器看下是否含有
					try{
						
						Map pocMap = new HashMap();
						pocMap.put("fileName", nextFileName);
						JSONObject pocJson  = JSONObject.fromObject(pocTool.connectToPoc("downloadFileByName", pocMap));
						if(pocJson.containsKey("info") && pocJson.getString("info").contains("未找到")){ //未找到上传的文件则说明文件不存在可以直接重新上传
							resultList.add(baseMap);
							continue;
						}
						//不存在还有一种可能是文件已经到第二天了,需要查找下是否含有第二天的文件
						String nextDateFileName = "PW03_"+DateUtil.getBefore(new SimpleDateFormat(DateUtil.STYLE_3).parse("20160101") ,-1,DateUtil.STYLE_3)+"_0001.txt";
						//先确认本地是否已经存在该文件
						String nextDateSql = "SELECT * from poc_batch_record where transferbu_file_name='"+nextFileName+"' limit 1";
						List<Map> nextDateList =  bgEfOrderDao.queryBySqlReturnMapList(nextDateSql);
						if(ListTool.isNotNullOrEmpty(nextDateList)){ //文件已经存在该失败的数据可以重新上传
							resultList.add(baseMap);
							continue;
						}
						//剩下的无法处理的数据只能发短信处理
						logger.warn("在进行富友ftp上传失败的数据进行重新上传时,该数据无法处理,请手动处理!唯一编码:"+baseMap.get("unique_no").toString());
						JSONObject smsJson = new JSONObject();
						smsJson.put("text","在进行富友ftp上传失败的数据进行重新上传时,该数据无法处理,请手动处理!唯一编码:"+baseMap.get("unique_no").toString());
						SmsUtil.senErrorMsg(smsJson);
						continue;
					}catch (Exception e) {
						//如果连接poc异常了则直接本条数据不在处理
						logger.warn(e.getMessage(),e);
						continue;
					}
				}
				
			}
			
		}
		return resultList;
	}
	/**
	 * 功能说明：根据唯一编码查找失败数据
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
	public List<Map> findFailDataByUniqueno(String uniqueNo){
		String sql = "SELECT bci.cust_name,bci.fy_account,pbr.bg_ef_order_id,pbr.bg_ef_paycontrol_id,pbr.money,pbr.type from poc_batch_record pbr,bg_ef_orders beo,bg_cust_info bci where pbr.bg_ef_order_id=beo.id and beo.cust_info_id=bci.id and pbr.unique_no='"+uniqueNo+"' ";
		return bgEfOrderDao.queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：自动查询长时间不处理的数据短信通知
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
	public List<Map> findToLaterDataToSms(){
		String sql = "SELECT unique_no from poc_batch_record where process !='4' and process_result !='-1' and TIMESTAMPDIFF(HOUR, create_time, now())>5 and informed !='1'   GROUP BY unique_no ";
		return bgEfOrderDao.queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：通过id找到对应的数据
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
	public Object findById(Class className, String id){
		try {
			return bgEfOrderDao.findById(className, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 功能说明：更新数据告知富友失败的数据
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
	public JSONObject updateFailPocRecord(String uniqueNo, String id,String type,String failRemark){
		JSONObject resultJson = new JSONObject();
		try{
			if("1".equals(type)){ //正常还款
				String sql = "update  poc_batch_record set process_result='-1' ,fail_remark = '"+failRemark+"' where unique_no='"+uniqueNo+"' and type='1' and bg_ef_paycontrol_id='"+id+"' ;";
				bgEfOrderDao.executeSql(sql);
			}
			if("-1".equals(type)){ //提前结清还款
				String sql = "update poc_batch_record set process_result='-1',fail_remark = '"+failRemark+"'  where unique_no='"+uniqueNo+"' and type='-1' and bg_ef_order_id='"+id+"';";
				bgEfOrderDao.executeSql(sql);
			}
			logger.warn("更新批量错误数据时成功,唯一编码:"+uniqueNo+",id:"+id+",类型:"+type);
		}catch (Exception e) { //修改失败了
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("更新批量错误数据时失败,唯一编码:"+uniqueNo+",id:"+id+",类型:"+type);
		}
		return resultJson;
	}
	/**
	 * 功能说明：更新数据告知富友成功的数据
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
	public JSONObject updateSuccessPocRecord(String preFileName, String fileName,String process){
		JSONObject resultJson = new JSONObject();
		try{
			if("1".equals(process)){ //划拨文件名 更新
				String sql = "update  poc_batch_record set transferbu_file_name='"+fileName+"',process='2' ,process_result='1' where preauth_file_name='"+preFileName+"' and process='"+process+"' and process_result='1' ";
				bgEfOrderDao.executeSql(sql);
			}
			if("2".equals(process)){ //冻结文件名 
				String sql = "update  poc_batch_record set freeze_file_name='"+fileName+"',process='3' ,process_result='1' where transferbu_file_name='"+preFileName+"' and process='"+process+"' and process_result='1'";
				bgEfOrderDao.executeSql(sql);
			}
			if("3".equals(process)){ //最终结束 
				String sql = "update  poc_batch_record set process='4',process_result='1' where freeze_file_name='"+preFileName+"' and process='3' and process_result='1'";
				bgEfOrderDao.executeSql(sql);
			}
			logger.warn("更新批量成功数据时成功,上一进度文件名:"+preFileName+",文件名:"+fileName+",进度:"+process);
		}catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("更新批量成功数据时失败,上一进度文件名:"+preFileName+",文件名:"+fileName+",进度:"+process);
		}
		return resultJson;
	}
	/**
	 * 功能说明：更新数据告知富友成功的数据
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
	public JSONObject updateSuccessPocBatchRecord(String fileName){
		JSONObject resultJson = new JSONObject();
		try{
			String sql = "update  poc_batch_record set process='4' ,process_result='1' where transferbu_file_name='"+fileName+"' and process_result='1' ";
			bgEfOrderDao.executeSql(sql);
			logger.warn("更新批量成功数据时成功,上一进度文件名:"+fileName+",文件名:"+fileName+",进度:4");
		}catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("更新批量成功数据时失败,上一进度文件名:"+fileName+",文件名:"+fileName+",进度:4");
		}
		return resultJson;
	}
	/**
	 * 功能说明：重复上传成功之后的更新
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
	public JSONObject updateAgainSuccessPocBatchRecord(String fileName,String unique_no){
		JSONObject resultJson = new JSONObject();
		try{
			String sql = "update  poc_batch_record set process='2' ,process_result='1',transferbu_file_name='"+fileName+"' where unique_no='"+unique_no+"' ";
			bgEfOrderDao.executeSql(sql);
			logger.warn("重复上传成功修改数据时成功,文件名:"+fileName+",唯一编码:"+unique_no);
		}catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("重复上传成功数据修改时失败,文件名:"+fileName+",唯一编码:"+unique_no);
			JSONObject smsJson = new JSONObject();
			smsJson.put("project_number", "uploadFilePW03");
			smsJson.put("text", "重复上传成功数据修改时失败,文件名:"+fileName+",唯一编码:"+unique_no);
			SmsUtil.senErrorMsg(smsJson);
		}
		return resultJson;
	}
	/**
	 * 功能说明：更新数据告知富友拒绝新文件名的数据
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
	public JSONObject updateRejectFileNamePocRecord(String preFileName, String newfileName,String process){
		JSONObject resultJson = new JSONObject();
		try{
			if("1".equals(process)){ //划拨文件名 更新
				String sql = "update  poc_batch_record set preauth_file_name='"+newfileName+"',process='1' ,process_result='1' where preauth_file_name='"+preFileName+"' and process='"+process+"' and process_result='1' ";
				bgEfOrderDao.executeSql(sql);
			}
			if("2".equals(process)){ //冻结文件名 
				String sql = "update  poc_batch_record set transferbu_file_name='"+newfileName+"',process='2' ,process_result='1' where transferbu_file_name='"+preFileName+"' and process='"+process+"' and process_result='1'";
				bgEfOrderDao.executeSql(sql);
			}
			if("3".equals(process)){ //最终结束 
				String sql = "update  poc_batch_record set freeze_file_name='"+newfileName+"',process='3',process_result='1' where freeze_file_name='"+preFileName+"' and process='3' and process_result='1'";
				bgEfOrderDao.executeSql(sql);
			}
			logger.warn("更新批量拒绝数据时成功,原文件名:"+preFileName+",更新后文件名:"+newfileName+",进度:"+process);
		}catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("更新批量拒绝数据时失败,原文件名:"+preFileName+",更新后文件名:"+newfileName+",进度:"+process);
		}
		return resultJson;
	}
	/**
	 * 功能说明：更新数据告知富友拒绝新文件名的数据
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
	public void updateAuthCodeBgEforders(String authCode){
		try{
			String sql = "UPDATE bg_ef_orders set auth_status='1' where invest_auz_code='"+authCode+"' ";
			bgEfOrderDao.executeSql(sql);
			logger.warn("解绑债权关系成功,修改债权理财订单时成功 !授权码:"+authCode);
		}catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("解绑债权关系成功,修改债权理财订单时失败 !授权码:"+authCode);
			JSONObject smsJson = new JSONObject();
			smsJson.put("project_number", "结清授权码");
			smsJson.put("text", "解绑债权关系成功,修改债权理财订单时失败 !授权码:"+authCode);
			SmsUtil.senErrorMsg(smsJson);
		}
	}
	/**
	 * 功能说明：查询数据
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
	public Map getLastBatchFileInfo(String keyId, String type){
		try{
		if("1".equals(type)){ //正常还款
			String sql = "SELECT beo.cust_info_id,beo.cust_id,eo.ef_applay_id,eo.id ef_order_id,bpr.money money,bpr.emp_id emp_id,beo.id bg_ef_order_id from poc_batch_record bpr, bg_ef_paycontrol bepc, bg_ef_orders beo,ef_orders eo where bpr.bg_ef_paycontrol_id=bepc.id and beo.ef_order_id=eo.id and bepc.ef_order_id=beo.id  and bepc.id='"+keyId+"' and bpr.type='1'";
			return bgEfOrderDao.queryBySqlReturnMapList(sql).get(0);
		}
		if("-1".equals(type)){ //提前结清还款
			String sql = "SELECT beo.cust_info_id,beo.cust_id,eo.ef_applay_id,eo.id ef_order_id,bpr.money money,bpr.emp_id emp_id,beo.id bg_ef_order_id from poc_batch_record bpr, bg_ef_orders beo,ef_orders eo where bpr.bg_ef_order_id=beo.id and beo.ef_order_id=eo.id and beo.id='"+keyId+"' and bpr.type='-1' ";
			return bgEfOrderDao.queryBySqlReturnMapList(sql).get(0);
		}
		}catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("添加站岗资金记录时失败!id:"+keyId+",类型:"+type);
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "添加站岗资金记录时失败!id:"+keyId+",类型:"+type);
			SmsUtil.senErrorMsg(smsJson);
			return null;
		}
		return null;
	}
	/**
	 * 功能说明：查询手动还款的poc的站岗资金需要的数据
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
	public Map getEfFundRecordInfo(String keyId, String type){
		try{
			if("1".equals(type)){ //正常还款
				String sql =
								"SELECT "+
								"	bepc.cust_info_id, "+
								"	bepr.id bepr_id, "+
								"	bepc.cust_id, "+
								"	beo.ef_order_id, "+
								"	beo.id bg_ef_order_id, "+
								"	bepr.principal money, "+
								"	eo.id, "+
								"	IFNULL(bepr.operator,'') emp_id, "+
								"	eo.ef_applay_id "+
								"FROM "+
								"	ef_orders eo, "+
								"	bg_ef_paycontrol bepc, "+
								"	bg_ef_orders beo, "+
								"	bg_ef_payrecord bepr "+
								"WHERE "+
								"	bepc.ef_order_id = beo.id "+
								"AND bepc.id = bepr.ef_paycontrol_id "+
								"AND beo.investment_model='2' "+
								"AND (bepr.notice_status is null or bepr.notice_status='') "+
								"AND eo.id=beo.ef_order_id  "+
								"AND bepc.id = '"+keyId+"' ";
				List<Map> resultList = bgEfOrderDao.queryBySqlReturnMapList(sql);
				if(ListTool.isNotNullOrEmpty(resultList)){
					return resultList.get(0);
				}else{
					return null;
				}
			}
			if("-1".equals(type)){ //提前结清还款
				String sql = 
								"SELECT "+
								"	beo.cust_info_id, "+
								"	bepr.id bepr_id, "+
								"	beo.cust_id, "+
								"	beo.ef_order_id, "+
								"	bepr.principal money, "+
								"	beo.id bg_ef_order_id, "+
								"	IFNULL(bepr.operator,'') emp_id, "+
								"	eo.ef_applay_id "+
								"FROM "+
								"	ef_orders eo, "+
								"	bg_ef_orders beo, "+
								"	bg_ef_payrecord bepr "+
								"WHERE "+
								"  bepr.ef_paycontrol_id = '-1' "+
								"AND bepr.ef_order_id = beo.id "+
								"AND (bepr.notice_status is null or bepr.notice_status='') "+
								"AND beo.investment_model='2' "+
								"AND eo.id = beo.ef_order_id "+
								"AND beo.id = '"+keyId+"' ";
				List<Map> resultList = bgEfOrderDao.queryBySqlReturnMapList(sql);
				if(ListTool.isNotNullOrEmpty(resultList)){
					return resultList.get(0);
				}else{
					return null;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			logger.warn("添加站岗资金记录时失败!id:"+keyId+",类型:"+type);
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "添加站岗资金记录时失败!id:"+keyId+",类型:"+type);
			SmsUtil.senErrorMsg(smsJson);
			return null;
		}
		return null;
	}
	/**
	 * 功能说明：查询数据
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
	public void save(Object obj){
		try {
			bgEfOrderDao.add(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 功能说明：更新
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
	public void update(Object obj){
		try {
			bgEfOrderDao.update(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 功能说明：查找已经结清的理财订单,并且结清流水标识是未结清
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
	public List<Map> BocBgEforderFinish(){
		String sql = 	"SELECT "+
						"	co.id crmOrderId, "+
						"	co.order_prd_number order_prd_number, "+
						"	beo.invest_auz_code, "+
						"	bciIn.bank_account InBankAccount, "+
						"	bciOut.bank_account outBankAccount, "+
						"	beo.invest_seri_num "+
						"FROM "+
						"	bg_ef_orders beo, "+
						"	bg_cust_info bciIn, "+
						"	bg_cust_info bciOut, "+
						"	crm_order co "+
						"WHERE "+
						"	beo.crm_order_id = co.id "+
						"AND bciIn.id=beo.cust_info_id   "+
						"AND bciOut.id=co.cust_info_id "+
						"AND beo.cust_info_id !='coupon520' "+
						"AND beo.pay_status in (2,5) "+
						"AND co.clearing_channel = 2 "+
						"AND (beo.auth_status =0 OR  beo.auth_status IS NULL) "+
						"GROUP BY "+
						"	beo.invest_auz_code; ";
		return bgEfOrderDao.queryBySqlReturnMapList(sql);
	}
	/**
	 * 功能说明：通过授权码判断订单是否已经全部结清
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
	public boolean isOverAuthcode(String authCode){
		//判断条件.只要订单状态不是2,5 则代表不能结清
		String sql  ="select id from bg_ef_orders where invest_auz_code='"+authCode+"' and pay_status not in (2,5)";
		List<Map> list = bgEfOrderDao.queryBySqlReturnMapList(sql);
		if(ListTool.isNotNullOrEmpty(list)){
			return false;
		}
		return true;
	}
	/**
	 * 功能说明：保存第二数据源
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
	public void savePaytransferrecord(List<JSONObject> list, String type){
		secondHibernateDao.paycenterSave(list, type);
	}
	/**
	 * 功能说明：更新明细
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
	public void clearBepc(String order_id,String investment_model) {
		//修改订单的状态
		BgEfOrders eo = null;
		try {
			eo = (BgEfOrders) bgEfOrderDao.findById(BgEfOrders.class, order_id);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		double preFee = 0d;
		double preRate = 0d;
		double manageFee = 0d;
		boolean manageFlag = true;
		//开始计算违约金
		if("1".equals(investment_model)){
			String touSql = 
							"SELECT  "+
							"ifnull(bedp.pre_manage_rate/100,0) advanceRate "+
							"from "+
							"crm_order co, "+
							"bg_ef_product_detail bedp "+
							"where co.ef_prd_detail_id = bedp.id and co.id='"+eo.getCrmOrderId()+"'";
			List<Map> advanceRateList = bgEfOrderDao.queryBySqlReturnMapList(touSql);
			if(ListTool.isNotNullOrEmpty(advanceRateList)){
				preRate = Double.parseDouble(advanceRateList.get(0).get("advanceRate").toString());
			}
			preFee =  ArithUtil.mul(eo.getEfFectiveAmt(), preRate);
			String manageSql = "select * from bg_ef_paycontrol where ef_order_id='"+order_id+"' and live_status=1 and pay_time >='"+DateUtil.getCurrentTime()+"' ORDER BY pay_time asc limit 1 ";
			List<Map> manageList = bgEfOrderDao.queryBySqlReturnMapList(manageSql);
			if(ListTool.isNotNullOrEmpty(manageList)){
				if("1".equals(manageList.get(0).get("pay_status").toString())){
					manageFlag = false;
				}
			}
		}
		//本金成功的情况下,可以直接修改明细和生成记录
		String epcsSql = "select * from bg_ef_paycontrol where ef_order_id='"+order_id+"' and live_status=1 and pay_status='0' ORDER BY pay_time asc ";
		List<Map> epcsList  = bgEfOrderDao.queryBySqlToLower(epcsSql);
		
		
		
		
		String cust_info_id=  "";
		String cust_id=  "";
		double capital = 0d; 

		if(ListTool.isNotNullOrEmpty(epcsList)){
			String uniqueKey = UUID.randomUUID().toString();
			for(Map epcsMap:epcsList){
				try {
					BgEfPaycontrol epc = (BgEfPaycontrol) bgEfOrderDao.findById(BgEfPaycontrol.class, epcsMap.get("id").toString());
					//结清明细
					capital = ArithUtil.add(capital,epc.getSurplusPrincipal());
					if(manageFee <=0d){
						int compare = DateUtil.newCompareDate(new SimpleDateFormat(DateUtil.STYLE_1).format(new Date()), epc.getPayTime(), DateUtil.STYLE_1);
						if(compare ==-1 && manageFlag){
							manageFee = epc.getSurplusManagementAmt();
						}
					}
					
					cust_info_id = epc.getCustInfoId();
					cust_id=epc.getCustId();
					epc.setSurplusPrincipal(0d);
					epc.setSurplusInterest(0d);
					epc.setSurplusManagementAmt(0d);
					epc.setCouponInterest(0d);
					epc.setPayStatus(1);
					epc.setOperateType("1");
					bgEfOrderDao.update(epc); 
				} catch (Exception e) {
					logger.warn(e.getMessage(),e);
				}
			}
		}
		if("2".equals(investment_model)){
			manageFee = 0d;
		}
		String beprSql  = "SELECT * from bg_ef_payrecord where ef_paycontrol_id='-1' and ef_order_id='"+order_id+"' ";
		
		List<Map> beprs = bgEfOrderDao.queryBySqlReturnMapList(beprSql);
		//利息,提前结清违约金,抵用卷,本金,管理费
		double interest =0d;
		double prePaymentPenality =0d;
		double principal =0d;
		double couponAmount =0d;
		double managementAmt =0d;
		
		if(ListTool.isNullOrEmpty(beprs)){
			//产生记录
			BgEfPayrecord epr = new BgEfPayrecord();
			epr.setCouponAmount(0d);
			epr.setCreateTime(DateUtil.getCurrentTime());
			epr.setCustId(cust_id);
			epr.setCustInfoId(cust_info_id);
			epr.setEfOrderId(order_id);
			epr.setEfPaycontrolId("-1");
			epr.setEvidenceUrl("");
			epr.setInteRest(0d);
			epr.setManagementAmt(manageFee);
			epr.setOperator("");
			epr.setOverPenalty(0d);
			epr.setPeriods(-1);
			epr.setPrePaymentPenalty(preFee);
			epr.setPrincipal(capital);
			epr.setTotalAmt(ArithUtil.add(new Double[]{
					epr.getCouponAmount(),
					epr.getInteRest(),
					epr.getPrePaymentPenalty(),
					-epr.getManagementAmt(),
					epr.getPrincipal()
					
			}));
			epr.setType(2);
			epr.setUpdateTime(DateUtil.getCurrentTime());
			try {
				bgEfOrderDao.add(epr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//赋值
			principal = capital;
		}else{
			BgEfPayrecord ber = null;
			try {
				ber = (BgEfPayrecord) bgEfOrderDao.findById(BgEfPayrecord.class, beprs.get(0).get("id").toString());
				ber.setPrincipal(capital);
				ber.setTotalAmt(capital);
				//重新格式化
				ber.setPrincipal(NumberFormat.format(ber.getPrincipal()));
				ber.setInteRest(NumberFormat.format(ber.getInteRest()));
				ber.setCouponAmount(NumberFormat.format(ber.getCouponAmount()));
				ber.setPrePaymentPenalty(NumberFormat.format(ber.getPrePaymentPenalty()));
				ber.setManagementAmt(NumberFormat.format(ber.getManagementAmt()));
				
				if(ber.getManagementAmt()<=0){
					ber.setManagementAmt(manageFee);
				}
				if(ber.getPrePaymentPenalty()<=0){
					ber.setPrePaymentPenalty(preFee);
				}
				ber.setTotalAmt(ArithUtil.sub(ArithUtil.add(new Double[]{
						NumberFormat.format(ber.getPrincipal()),
						NumberFormat.format(ber.getInteRest()),
						NumberFormat.format(ber.getCouponAmount()),
						NumberFormat.format(ber.getPrePaymentPenalty())
				}),NumberFormat.format(ber.getManagementAmt())));
				bgEfOrderDao.update(ber);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//赋值
			principal = capital;
			interest =ber.getInteRest();
			prePaymentPenality = ber.getPrePaymentPenalty();
			couponAmount =ber.getCouponAmount();
			managementAmt =ber.getManagementAmt();
			
		}
		eo.setPayStatus(2);
		eo.setClearTime(DateUtil.getCurrentTime());
		try {
			bgEfOrderDao.update(eo);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if("1".equals(investment_model)){ //直接投资的单子
			//开始发送提前结清的站内信
			try{
				BgCustomer  customer = (BgCustomer) bgEfOrderDao.findById(BgCustomer.class, eo.getCustId());
				CrmOrder co = (CrmOrder) bgEfOrderDao.findById(CrmOrder.class, eo.getCrmOrderId());
				JSONObject msgJson = new JSONObject();
				msgJson.put("custId",eo.getCustId());
				msgJson.put("custInfoId",eo.getCustInfoId());
				msgJson.put("amount",eo.getEfFectiveAmt());
				msgJson.put("interest", interest);
				msgJson.put("prePaymentPenality",prePaymentPenality);
				msgJson.put("principal", principal);
				msgJson.put("couponAmount", couponAmount);
				msgJson.put("rateCoupon", 0d);
				msgJson.put("managementAmt", managementAmt);
				msgJson.put("orderNumber",eo.getOrderNumber());
				msgJson.put("type", 2);
				msgJson.put("efOrderId", eo.getId());
				msgJson.put("username", customer.getUsername());
				
				String online_type = co.getOnlineType()==null ?"0":co.getOnlineType().toString();
				if(!"0".equals(online_type)){ //只要不是线下的就需要发送这个站内信
					String path ="message/addSysMessage"; 
					//根据不同的平台发送不同的地址
					if("1".equals(online_type)){ //贝格
						path=StaticData.v3BelPostPath+path;
					}
					if("2".equals(online_type)){ //中资联财务
						path=StaticData.v3ZzlcfPostPath+path;
					}
					logger.warn("提前结清发送站内地址:"+path);
					logger.warn("提前结清发送站内信参数:"+msgJson.toString());
					HttpUtil.connectByUrl(path,msgJson,false);
				}
				
			}catch (Exception e) {
				//e.printStackTrace();
				logger.warn("提前结清发送站内信异常!");
				logger.warn(e.getMessage(),e);
			}
		}
	}
	/**
	 * 功能说明：获得线下订单明细的(含有本金)
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
	public List<Map> findLineEfPaycontrolHasPrincipal(String efOrderId){
		String sql  = "SELECT * from ef_paycontrol where surplus_principal>0 and ef_order_id='"+efOrderId+"' ";
		return bgEfOrderDao.queryBySqlReturnMapList(sql);
	}
}
