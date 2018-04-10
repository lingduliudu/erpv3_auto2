package com.apt.webapp.service.auto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Component;

import com.apt.util.BusinessTool;
import com.apt.util.ChkUtil;
import com.apt.util.ListTool;
import com.apt.util.NumberFormat;
import com.apt.util.NumberUtil;
import com.apt.util.StaticData;
import com.apt.util.WebServiceUtil;
import com.apt.util.pocTool;
import com.apt.util.arith.ArithUtil;
import com.apt.util.date.DateUtil;
import com.apt.util.singature.SignatureUtil;
import com.apt.util.sms.SmsUtil;
import com.apt.webapp.dao.crm.ICrmOrderDao;
import com.apt.webapp.model.auto.AutoPocInvokeRecord;
import com.apt.webapp.model.bg.ef.BgCustInfo;
import com.apt.webapp.model.bg.ef.BgEfOrders;
import com.apt.webapp.model.ef.EfFundRecord;
import com.apt.webapp.model.ef.EfOrders;
import com.apt.webapp.model.ef.EfPaycontrol;
import com.apt.webapp.service.bg.ef.IBgEfOrderService;
import com.apt.webapp.service.crm.ICrmOrderService;
import com.apt.webapp.service.ef.IEfFundRecordService;

@Component
public class AutoUpdatePocFileRecord {
	@Resource
	private IBgEfOrderService  bgEfOrderService;
	@Resource
	private IEfFundRecordService efFundRecordService;
	@Resource
	private ICrmOrderDao crmOrderDao;
	@Resource
	private ICrmOrderService crmOrderService;
	@Resource
	private IAutoPocInvokeRecordService pocInvokeRecordService;
	// 日志
	private Logger logger = LoggerFactory.getLogger(AutoUpdatePocFileRecord.class);
	
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
	public void scanPreauthFilerecordToUpdate(){
		logger.warn("手动还款的预授权文件的处理开始!");
		//得到批量的预授权成功的文件
		List<Map> preauthList =  bgEfOrderService.findPocFileRecord("1"); // 得到所有预授权成功的记录并分组
		if(ListTool.isNullOrEmpty(preauthList)){logger.warn("手动还款的预授权文件的处理结束!");return ;}
		//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		for(Map baseMap:preauthList){ 
			String fileName = baseMap.get("preauth_file_name").toString();
			Map pocMap = new HashMap();
			pocMap.put("fileName", fileName);
			List<String> dataList = new ArrayList<String>();
			List<String> rejectdataList = new ArrayList<String>();
			try{
				JSONObject pocJson  = JSONObject.fromObject(pocTool.connectToPoc("downloadFileByName", pocMap));
				if(pocJson.containsKey("responseCode") && "0".equals(pocJson.getString("responseCode"))){
					continue;
				}
				dataList =ListTool.getList(pocJson.get("data"));
				try{
					if(pocJson.containsKey("rejectData")){
						rejectdataList = ListTool.getList(pocJson.get("rejectData"));
						for(String rejectString:rejectdataList){
							String[] rejectArray = rejectString.split(Pattern.quote("|"));
							//需要将本次的明细直接改成结束
							bgEfOrderService.updateFailPocRecord(baseMap.get("unique_no").toString(), rejectArray[6].split(Pattern.quote(":"))[1], baseMap.get("type").toString(),rejectArray[8]);
						}
						//更新文件名
						if(pocJson.containsKey("fileName")){
							//更新文件名
							bgEfOrderService.updateRejectFileNamePocRecord(baseMap.get("preauth_file_name").toString(),pocJson.getString("fileName"),"1");
						}
					}
				}catch (Exception e) {
					logger.warn(e.getMessage(),e);
					
				}
				if(dataList.size()==0){
					continue;
				}
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				logger.warn("手动还款的预授权文件处理异常!文件名:"+fileName);
				JSONObject smsJson = new JSONObject();
				smsJson.put("text", "手动还款的预授权文件处理异常!文件名:"+fileName);
				SmsUtil.senErrorMsg(smsJson);
				continue;
			}
			//开始将预授权返回的文件处理成划拨文件
			//预授权返回样例:    000001|15106216176|胡方晶|13275177201|袁浩|0.31|bg_ef_paycontrol_id:ff80808156a0c2210156a0e6af4b00a0|0000|成功|000533436026
			//划拨文件上传样例:	000001|15106216176|胡方晶|0|13275177201|袁浩|0|0.01|bg_ef_paycontrol_id:ff80808156a0c2210156a0e6af4a009f|000532981813
			List<String> uploadList = new ArrayList<String>();
			for(String baseString:dataList){
				String[] baseData = baseString.split(Pattern.quote("|")); //拆解基础数据
				if(!"0000".equals(baseData[7])){ //预授权失败了
					//需要将本次的明细直接改成结束
					bgEfOrderService.updateFailPocRecord(baseMap.get("unique_no").toString(), baseData[6].split(Pattern.quote(":"))[1], baseMap.get("type").toString(),baseData[8]);
					continue;
				}
				String resultString = baseData[1]+"|"+ //出账账户
									  baseData[2]+"|"+//出账人姓名
									  "0|"+//付款资金来自冻结 (1是,0否)--只支持否
									  baseData[3]+"|"+//入账账户
									  baseData[4]+"|"+//入账人姓名
									  "0|"+//收款后立即冻结(1是，0否) ,1测试时失败
									  baseData[5]+"|"+//金额
									  baseData[6]+"|"+//备注
									  baseData[9];//合同号
				logger.warn("本次数据:"+resultString);
				uploadList.add(resultString);
			}
			//准备上传划拨文件
			pocMap.clear();
			pocMap.put("code", "PW03");
			pocMap.put("data", uploadList);
			try{
				if(uploadList.size()>0){
					JSONObject pocResult = JSONObject.fromObject(pocTool.connectToPoc("uploadFilePW03.html", pocMap));
					if("1".equals(pocResult.getString("responseCode"))){ //文件上传成功
						logger.warn("划拨文件上传成功!");
						//文件名
						fileName = pocResult.getString("fileName");
						bgEfOrderService.updateSuccessPocRecord(baseMap.get("preauth_file_name").toString(), fileName,"1");
						
					}else{
						logger.warn("划拨文件上传失败!");
					}
				}
			}catch (Exception e) { //上传文件报错
				e.printStackTrace();
				logger.warn(e.getMessage(),e);
				logger.warn("划拨文件上传异常!");
			}
			
			
		}
		logger.warn("手动还款的预授权文件的处理结束!");
	}
	/**
	 * 功能说明：自动更新划拨poc的批量文件并处理
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
	public void scanTransferBuFilerecordToUpdate(){
		logger.warn("手动还款的划拨文件的处理开始!");
		//得到批量的预授权成功的文件
		List<Map> transList =  bgEfOrderService.findPocFileRecord("2"); // 得到所有预授权成功的记录并分组
		if(ListTool.isNullOrEmpty(transList)){logger.warn("手动还款的划拨文件的处理结束!");return ;}
		//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		for(Map baseMap:transList){ 
			String fileName = baseMap.get("transferbu_file_name").toString();
			Map pocMap = new HashMap();
			pocMap.put("fileName", fileName);
			List<String> dataList = new ArrayList<String>();
			List<String> rejectdataList = new ArrayList<String>();
			try{
				JSONObject pocJson  = JSONObject.fromObject(pocTool.connectToPoc("downloadFileByName", pocMap));
				if(pocJson.containsKey("responseCode") && "0".equals(pocJson.getString("responseCode"))){
					continue;
				}
				dataList = ListTool.getList(pocJson.get("data"));
				try{
					if(pocJson.containsKey("rejectData")){
						rejectdataList = ListTool.getList(pocJson.get("rejectData"));
						for(String rejectString:rejectdataList){
							String[] rejectArray = rejectString.split(Pattern.quote("|"));
							//需要将本次的明细直接改成结束
							bgEfOrderService.updateFailPocRecord(baseMap.get("unique_no").toString(), rejectArray[8].split(Pattern.quote(":"))[1], baseMap.get("type").toString(),rejectArray[11]);
						}
						//更新文件名
						if(pocJson.containsKey("fileName")){
							//更新文件名
							bgEfOrderService.updateRejectFileNamePocRecord(baseMap.get("transferbu_file_name").toString(),pocJson.getString("fileName"),"2");
						}
					}
				}catch (Exception e) {
					logger.warn(e.getMessage(),e);
					
				}
				if(dataList.size()==0){
					continue;
				}
			}catch (Exception e) {
				logger.warn("手动还款的划拨文件处理异常!文件名:"+fileName);
				JSONObject smsJson = new JSONObject();
				smsJson.put("text", "手动还款的划拨文件处理异常!文件名:"+fileName);
				SmsUtil.senErrorMsg(smsJson);
				continue;
			}
			//开始将划拨返回的文件处理成冻结文件
			//划拨返回样例:   "000001|13376006679|秦杰|0|13262812381|翁家锋|0|16.10|bg_ef_paycontrol_id:8ed77bbaa36c4362816c3fac36b3e919|000635099706|0000|成功|342401199509063012|321282198906054230
			//冻结文件上传样例:	000001|13275177201|袁浩|0.01|bg_ef_paycontrol_id:ff80808156a0c2210156a0df8009004c
			List<String> uploadList = new ArrayList<String>();
			for(String baseString:dataList){
				String[] baseData = baseString.split(Pattern.quote("|")); //拆解基础数据
				if(!"0000".equals(baseData[10])){ //划拨失败了
					//需要将本次的明细直接改成结束
					bgEfOrderService.updateFailPocRecord(baseMap.get("unique_no").toString(), baseData[8].split(Pattern.quote(":"))[1], baseMap.get("type").toString(),baseData[11]);
					continue;
				}
				String resultString = baseData[4]+"|"+//入账账户
									  baseData[5]+"|"+//入账人姓名
									  baseData[7]+"|"+//金额
									  baseData[8];//备注
				logger.warn("本次数据:"+resultString);
				uploadList.add(resultString);
			}
			//准备上传划拨文件
			pocMap.clear();
			pocMap.put("code", "PWDJ");
			pocMap.put("data", uploadList);
			try{
				if(uploadList.size()>0){
					JSONObject pocResult = JSONObject.fromObject(pocTool.connectToPoc("uploadFilePWDJ.html", pocMap));
					if("1".equals(pocResult.getString("responseCode"))){ //文件上传成功
						logger.warn("冻结文件上传成功!");
						//文件名
						fileName = pocResult.getString("fileName");
						bgEfOrderService.updateSuccessPocRecord(baseMap.get("transferbu_file_name").toString(), fileName,"2"); 
						
					}else{
						logger.warn("冻结文件上传失败!");
					}
				}
			}catch (Exception e) { //上传文件报错
				e.printStackTrace();
				logger.warn(e.getMessage(),e);
				logger.warn("冻结文件上传异常!");
			}
			
			
		}
		logger.warn("手动还款的划拨文件的处理结束!");
	
	}
	/**
	 * 功能说明：自动更新冻结poc的批量文件并处理
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
	public void scanFreezeFilerecordToUpdate(){

		logger.warn("手动还款的冻结文件的处理开始!");
		//得到冻结的预授权成功的文件
		List<Map> transList =  bgEfOrderService.findPocFileRecord("3"); // 得到所有冻结成功的记录并分组
		if(ListTool.isNullOrEmpty(transList)){logger.warn("手动还款的冻结文件的处理结束!");;return ;}
		//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		for(Map baseMap:transList){ 
			String fileName = baseMap.get("freeze_file_name").toString();
			Map pocMap = new HashMap();
			pocMap.put("fileName", fileName);
			List<String> dataList = new ArrayList<String>();
			List<String> rejectdataList = new ArrayList<String>();
			try{
				JSONObject pocJson  = JSONObject.fromObject(pocTool.connectToPoc("downloadFileByName", pocMap));
				if(pocJson.containsKey("responseCode") && "0".equals(pocJson.getString("responseCode"))){
					continue;
				}
				dataList = ListTool.getList(pocJson.get("data"));
				try{
					if(pocJson.containsKey("rejectData")){
						rejectdataList = ListTool.getList(pocJson.get("rejectData"));
						for(String rejectString:rejectdataList){
							String[] rejectArray = rejectString.split(Pattern.quote("|"));
							//需要将本次的明细直接改成结束
							bgEfOrderService.updateFailPocRecord(baseMap.get("unique_no").toString(), rejectArray[4].split(Pattern.quote(":"))[1], baseMap.get("type").toString(),rejectArray[6]);
						}
						//更新文件名
						if(pocJson.containsKey("fileName")){
							//更新文件名
							bgEfOrderService.updateRejectFileNamePocRecord(baseMap.get("freeze_file_name").toString(),pocJson.getString("fileName"),"3");
						}
					}
				}catch (Exception e) {
					logger.warn(e.getMessage(),e);
					
				}
				if(dataList.size()==0){
					continue;
				}
			}catch (Exception e) {
				logger.warn("手动还款的冻结文件处理异常!文件名:"+fileName);
				JSONObject smsJson = new JSONObject();
				smsJson.put("text", "手动还款的冻结文件处理异常!文件名:"+fileName);
				SmsUtil.senErrorMsg(smsJson);
				continue;
			}
			//开始将冻结返回的文件处理
			//冻结返回样例:   000001|13275177201|袁浩|0.01|bg_ef_paycontrol_id:ff80808156a0c2210156a0df8009004c|0000|成功|000371397193
			List<String> uploadList = new ArrayList<String>();
			for(String baseString:dataList){
				String[] baseData = baseString.split(Pattern.quote("|")); //拆解基础数据
				if(!"0000".equals(baseData[5])){ //冻结失败了
					//需要将本次的明细直接改成结束
					bgEfOrderService.updateFailPocRecord(baseMap.get("unique_no").toString(), baseData[4].split(Pattern.quote(":"))[1], baseMap.get("type").toString(),baseData[6]);
				}else{
					uploadList.add( baseData[4]);
				}
			}
			//开始处理数据
			//成功了则需要结束了,增加冻结记录
			//开始进行记录
			if(ListTool.isNotNullOrEmpty(uploadList)){
				for(String keyId:uploadList){
					try {
						String Id = keyId.split(Pattern.quote(":"))[1];
						String type = keyId.split(Pattern.quote(":"))[0];
						String baseType="1";
						if("bg_ef_order_id".equals(type)){
							baseType="-1";
						}
						Map freezeInfo = bgEfOrderService.getLastBatchFileInfo(Id,baseType);
						if(freezeInfo == null ){
							continue;
						}
						EfFundRecord efr = new EfFundRecord();
						efr.setCreateTime(DateUtil.getCurrentTime());
						efr.setCustId(freezeInfo.get("cust_id").toString());
						efr.setCustInfoId(freezeInfo.get("cust_info_id").toString());
						efr.setEfApplayId(freezeInfo.get("ef_applay_id").toString());
						efr.setEfOrderId(freezeInfo.get("ef_order_id").toString());
						efr.setBgEfOrderId(freezeInfo.get("bg_ef_order_id").toString());
						efr.setEmpId(freezeInfo.get("emp_id").toString());
						efr.setMoney(NumberUtil.parseDouble(freezeInfo.get("money").toString()));
						efr.setRecordStatus(1);
						efr.setRecordType("还款");
						bgEfOrderService.save(efr);
					} catch (Exception e) {
						e.printStackTrace();
						logger.warn(e.getMessage(),e);
					}
				}
			}
			bgEfOrderService.updateSuccessPocRecord(baseMap.get("freeze_file_name").toString(), fileName,"3"); 
		}
		logger.warn("手动还款的冻结文件的处理结束!");
	}
	/**
	 * 功能说明：自动更新冻结poc的批量文件并处理
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
	public void doSaveEffunderrecord(JSONArray dataArray,String reqType){
		if(dataArray == null || dataArray.size() ==0 ){return ;}
		//非空继续
		if("PW03".equals(reqType)){
			//分2种1个是手动一个是自动
			JSONArray autoJsonArray = new JSONArray();
			JSONArray nonAutoJsonArray = new JSONArray();
			for(int i=0;i<dataArray.size();i++){
				//准备获得对应的数据
					String[] str = dataArray.get(i).toString().split(Pattern.quote("|"));
				String checkKey = str[8].split(Pattern.quote(":"))[0];
				//line_order_id
				//bg_ef_order_id
				if(checkKey.startsWith("line_order_id") || checkKey.startsWith("bg_ef_order_id")){  //提前结清走手动
					nonAutoJsonArray.add(dataArray.get(i));
					continue;
				}
				//if(checkKey.startsWith("auto")){
					autoJsonArray.add(dataArray.get(i));
				//	continue;
			//	}
				
			}
			//开始进行处理
			nonAutoNotince(nonAutoJsonArray);
			autoNotinceEf(autoJsonArray);
		}
		if("AC01".equals(reqType)){
			autoNotinceCpcAndCpcOver(dataArray);
		}
	}
	
	
	/**
	 * 功能说明：手动还款通知
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
	public void nonAutoNotince(JSONArray dataArray){
		if(dataArray == null || dataArray.size() ==0 ){return ;}
		//数据为空直接返回
		//开始针对数据进行解析
		for(int i=0;i<dataArray.size();i++){
			//准备获得对应的数据
			String[] str = dataArray.get(i).toString().split(Pattern.quote("|"));
			String checkKey = str[8].split(Pattern.quote(":"))[0];
			String order_id = str[8].split(Pattern.quote(":"))[1];
			String money = str[7];
			double unFreezenMoney = NumberFormat.format(Double.parseDouble(money));
			//提前结清的理财订单
			String type = "";
			if("line_paycontrol_id".equals(checkKey)){
				type = "1";
			}
			//提前结清的理财订单
			if("line_order_id".equals(checkKey)){
				type = "-1";
			}
			if("bg_ef_order_id".equals(checkKey)){
				type = "-1";
			}
			//是否是专业的投资人
			boolean isProfessionalInvestor = false;
			//开始进行数据的修改
			if ("1".equals(type)) {  //手动正常理财还款
				Map efPaycontrol = bgEfOrderService.getCurrentImmeControlsPocById(order_id).get(0);
				//判断是否是专业投资人
				isProfessionalInvestor = BusinessTool.isProfessionalInvestor(efPaycontrol.get("investor_type"));
				if("0".equals(efPaycontrol.get("pay_status").toString())){  //未结清
					String investment_model = efPaycontrol.get("investment_model").toString();
					// 直接更新数据
					if (ChkUtil.isNotEmpty(investment_model) && "1".equals(investment_model)) {
						// 如果成功需要记录并修改明细状态
						efPaycontrol.put("POC", "1");
						efPaycontrol.put("isNewPoc", "1");
						JSONObject refJson = bgEfOrderService.cleanEfPaycontrol(efPaycontrol);
						// 定投更新数据
					} else if (ChkUtil.isNotEmpty(investment_model) && "2".equals(investment_model)) {
						// 如果成功需要记录并修改明细状态
						// 开始进行明细转移和状态修改和记录
						efPaycontrol.put("onLine", "0");
						bgEfOrderService.normalRepay(efPaycontrol);
					}
				}
			}
			//开始进行数据的修改
			if ("-1".equals(type)) {  //手动提前理财还款
				BgEfOrders beo = (BgEfOrders) bgEfOrderService.findById(BgEfOrders.class,order_id);
				BgCustInfo custInfo = (BgCustInfo) bgEfOrderService.findById(BgCustInfo.class, beo.getCustInfoId());
				//判断是否是专业投资人
				isProfessionalInvestor = BusinessTool.isProfessionalInvestor(custInfo.getInvestorType());
				if(beo.getPayStatus()!=2){  //未结清
					String investment_model =beo.getInvestmentModel();
					// 直接更新数据
					if (ChkUtil.isNotEmpty(investment_model) && "1".equals(investment_model)) {
						//直投的,需要结清明细,结清订单,
						bgEfOrderService.clearBepc(order_id,"1");
					} else if (ChkUtil.isNotEmpty(investment_model) && "2".equals(investment_model)) {
						//需要结清明细,结清订单
						bgEfOrderService.clearBepc(order_id,"2");
					}
				}
			}
			//如果是专业的投资人则需要直接跳过
			//专业投资人即时是定投也是直接解冻的
			Map freezeInfo = bgEfOrderService.getEfFundRecordInfo(order_id, type);
			if(freezeInfo == null ){
				continue;
			}
			if(isProfessionalInvestor){ 
				//如果是专业的投资人需要直接去掉金额
				if(freezeInfo.get("ef_order_id")==null){
					continue;
				}
				//查询明细
				//修改明细
				List<Map> epcList =  bgEfOrderService.findLineEfPaycontrolHasPrincipal(freezeInfo.get("ef_order_id").toString());
				if(ListTool.isNotNullOrEmpty(epcList)){
					Map  epcMap = epcList.get(0);
					EfPaycontrol epc = (EfPaycontrol) bgEfOrderService.findById(EfPaycontrol.class, epcMap.get("id").toString());
					epc.setSurplusPrincipal(NumberFormat.format(ArithUtil.sub(epc.getSurplusPrincipal(), unFreezenMoney)));
					bgEfOrderService.update(epc);
				}
				continue;
			}
			EfFundRecord efr = new EfFundRecord();
			efr.setCreateTime(DateUtil.getCurrentTime());
			efr.setCustId(freezeInfo.get("cust_id").toString());
			efr.setCustInfoId(freezeInfo.get("cust_info_id").toString());
			efr.setEfApplayId(freezeInfo.get("ef_applay_id").toString());
			efr.setEfOrderId(freezeInfo.get("ef_order_id").toString());
			efr.setBgEfOrderId(freezeInfo.get("bg_ef_order_id").toString());
			efr.setEmpId(freezeInfo.get("emp_id").toString());
			efr.setMoney(NumberUtil.parseDouble(freezeInfo.get("money").toString()));
			efr.setRecordStatus(1);
			efr.setRecordType("还款");
			bgEfOrderService.save(efr);
			try{
				bgEfOrderService.executeSql("update bg_ef_payrecord set notice_status='1' where id='"+freezeInfo.get("bepr_id").toString()+"'");
			}catch (Exception e) {
				e.printStackTrace();
				logger.warn(e.getMessage(),e);
			}
		}
	}
	/**
	 * 功能说明：自动还款通知(理财)
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
	public void autoNotinceEf(JSONArray dataArray){
		AutoPocInvokeRecord invokeRecord = null;
		JSONArray array = dataArray;
		List<Object> os = new ArrayList<Object>();
		for (Object object : array) {
			if (ChkUtil.isEmpty(object)) {
				continue;
			}
			// 批量划拨回盘:序号|付款方登录名|付款方中文名|付款资金来自冻结|收款方登录名|收款方中文名|收款后立即冻结|交易金额|备注|预授权合同号|返回码|说明|收款方证件号|付款方证件号|成功冻结付款冻结金
			String[] str = object.toString().split(Pattern.quote("|"));
			String out_cust_no = str[1]; // 付款方登录名
			String out_cust_name = str[2]; // 付款方中文名称
			String int_cust_no = str[4]; // 收款方登录名
			String int_cust_name = str[5]; // 收款方中文名称
			String amt = str[7]; // 交易金额
			String remark = str[8]; // 备注信息
			String returnCode = str[10]; // 返回码
			String hfReturnCode = str[11]; // 可能是恒丰的返回码
			String returnInfo = str[11]; // 说明
			String int_cust_ic = str[12]; // 收款方证件号
			String out_cust_ic = str[13]; // 付款方证件号
			String errorResponseCode = "";
			if(str.length>12){
				errorResponseCode = str[12]; // 返回码;有可能多了一个
			}
			// String succeedFreezeAmt = str[14]; // 成功冻结付款冻结金

			String efPaycontrolId = remark.split(":")[1];

			invokeRecord = new AutoPocInvokeRecord();// 初始化POC调用记录
			invokeRecord.setBgEfPaycontrolId(efPaycontrolId);// 理财明细id
			invokeRecord.setReturnCode(returnCode);// 返回码
			invokeRecord.setReturnInfo(returnInfo);// 返回信息
			invokeRecord.setAmount(Double.parseDouble(amt));// 金额
			invokeRecord.setInvokeTime(DateUtil.getCurrentTime());
			if(remark.split(":")[0].contains("ref")){
				invokeRecord.setRepayType(6);// 还款类型:邀请人
				continue;
			}else{
				invokeRecord.setRepayType(2);// 还款类型：理财还款划拨
			}
			if ("0000".equals(returnCode) || "0000".equals(hfReturnCode) || "0000".equals(errorResponseCode)) {
				invokeRecord.setState(1);// 执行状态
			//	if ("auto_bepc_id".equals(remark.split(":")[0])) {
					Map efPaycontrol = bgEfOrderService.getCurrentImmeControlsPocById(efPaycontrolId).get(0);
					if(efPaycontrol == null){
						continue;
					}
					if(efPaycontrol.get("pay_status")==null ||  "1".equals(efPaycontrol.get("pay_status").toString())){
						continue;
					}
					String investment_model = efPaycontrol.get("investment_model").toString();
					// 直接更新数据
					if (ChkUtil.isNotEmpty(investment_model) && "1".equals(investment_model)) {
						// 如果成功需要记录并修改明细状态
						efPaycontrol.put("POC", "1");
						efPaycontrol.put("isNewPoc", "1");
						JSONObject refJson = bgEfOrderService.cleanEfPaycontrol(efPaycontrol);
						// 定投更新数据
					} else if (ChkUtil.isNotEmpty(investment_model) && "2".equals(investment_model)) {
						// 如果成功需要记录并修改明细状态
						// 开始进行明细转移和状态修改和记录
						efPaycontrol.put("onLine", "0");
						bgEfOrderService.normalRepay(efPaycontrol);
						// 开始进行可用资金存储
						//定投新加规则  专业投资人将不在冻结
						if(BusinessTool.isNotProfessionalInvestor(efPaycontrol.get("investor_type"))){
							efFundRecordService.addNormal(efPaycontrol);
						}
						//如果是专业投资人则需要减去本金
						if(BusinessTool.isProfessionalInvestor(efPaycontrol.get("investor_type"))){ 
							//查询明细
							List<Map> epcList =  bgEfOrderService.findLineEfPaycontrolHasPrincipal(efPaycontrol.get("lineEfOrderId").toString());
							if(ListTool.isNotNullOrEmpty(epcList)){
								Map  epcMap = epcList.get(0);
								EfPaycontrol epc = (EfPaycontrol) bgEfOrderService.findById(EfPaycontrol.class, epcMap.get("id").toString());
								epc.setSurplusPrincipal(NumberFormat.format(ArithUtil.sub(epc.getSurplusPrincipal(), Double.parseDouble(efPaycontrol.get("princiapl").toString()))));
								bgEfOrderService.update(epc);
							}
						}
					}
			//	}
			} else {
				invokeRecord.setState(0);// 执行状态
				JSONObject smsJson = new JSONObject();
				if ("auto_bepc_id".equals(remark.split(":")[0])) {
					logger.warn("Poc理财自动还款划拨操作失败：" + remark + "，失败原因：" + returnInfo);
					logger.warn("Poc投资人本金还款失败!理财明细:id=" + efPaycontrolId);
					smsJson.put("text", "Poc投资人本金还款失败!(划拨操作)理财明细:id=" + efPaycontrolId);
					// 邀请人
				} else if ("auto_bepcrefer_id".equals(remark.split(":")[0])) {
					logger.warn("直投Poc投资人邀请人还款失败!理财明细:id=" + efPaycontrolId);
					smsJson.put("text", "直投Poc投资人邀请人本金还款失败!（划拨操作）理财明细:id=" + efPaycontrolId);
				}
				smsJson.put("project_number", "erpv3_auto2");SmsUtil.senErrorMsgByZhiyun(smsJson);
			}
			//os.add(invokeRecord);
		}
		if (os.size() > 0) {
			try {
				crmOrderDao.batchSaveOrUpdate(os, 500);
			} catch (Exception e) {
				// TODO: handle exception
				logger.warn(e.getMessage(), e);
			}
		}

	
	}
	/**
	 * 功能说明：自动还款通知(信贷正常)
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
	public void autoNotinceCpcAndCpcOver(JSONArray dataArray){
		if(dataArray==null || dataArray.size()==0){return;}
		try{
			List<Object> os = new ArrayList<Object>();
			AutoPocInvokeRecord invokeRecord = null;
			JSONObject paramJson = new JSONObject();
			logger.warn("Poc信贷订单的正常还款开始---");
			List<String> list = new ArrayList<String>();
			if (ChkUtil.isNotEmpty(dataArray)){
				JSONArray array = dataArray;
				// 明细序列|扣款人开户行行别|扣款人银行帐号|户名|金额|企业流水号|备注|手机号|返回码|返回说明
				// 获得当日的需要还款的crm信贷明细
				for (Object object : array) {
					if (ChkUtil.isEmpty(object)) {
						continue;
					}
					String[] str = object.toString().split(Pattern.quote("|"));
					String bankType = str[1]; // 扣款人开户行行别
					String bankNo = str[2]; // 扣款人银行帐号
					String custName = str[3]; // 户名
					String amt = str[4]; // 金额
					String serialNo = str[5]; // 企业流水号
					String remark = str[6]; // 备注信息
					String mobile = str[7]; // 手机号
					String returnCode = str[8]; // 返回码
					String returnInfo = str[9]; // 说明
					if(str.length>10) {
						returnCode= str[10];
						returnInfo = str[11]; // 说明
					}
					if ("auto_cpc_id".equals(remark.split(":")[0])) {
						//正常还款
						invokeRecord = new AutoPocInvokeRecord();// 初始化POC调用记录
						invokeRecord.setCrmPaycontrolId(remark.split(":")[1]);// 信贷明细id
						invokeRecord.setReturnCode(returnCode);// 返回码
						invokeRecord.setReturnInfo(returnInfo);// 返回信息
						invokeRecord.setRepayType(3);// 还款类型
						invokeRecord.setSerialNo(serialNo);
						invokeRecord.setAmount(Double.parseDouble(amt));// 金额
						invokeRecord.setInvokeTime(DateUtil.getCurrentTime());
						if ("202000".equals(returnCode)) {
							invokeRecord.setState(1);
							// 更新数据
							// poc扣款成功,直接进行修改状态就可以了
							logger.warn("线下信贷订单正常还款成功,明细id:" + remark + ",POC返回成功!");
							crmOrderService.repayNormalByPoc(remark.split(":")[1]);
						} else {
							invokeRecord.setState(0);
							logger.warn("Poc信贷订单的正常还款操作失败：" + remark + "，失败原因：" + returnInfo);
							if (ChkUtil.isNotEmpty(returnInfo) && !returnInfo.contains("不足")) {
								JSONObject smsJson = new JSONObject();
								smsJson.put("text", "Poc信贷订单的正常还款操作失败：" + remark + "，失败原因：" + returnInfo);
								smsJson.put("project_number", "erpv3_auto2");
								SmsUtil.senErrorMsgByZhiyun(smsJson);
							}
						}
						os.add(invokeRecord);
					}else if ("auto_co_id".equals(remark.split(":")[0])) {
						//逾期还款
						invokeRecord=new AutoPocInvokeRecord();//初始化POC调用记录
						invokeRecord.setCrmOrderId(remark.split(":")[1]);//信贷明细id
						invokeRecord.setReturnCode(returnCode);//返回码
						invokeRecord.setReturnInfo(returnInfo);//返回信息
						invokeRecord.setRepayType(4);//还款类型
						invokeRecord.setSerialNo(serialNo);
						invokeRecord.setAmount(Double.parseDouble(amt));//金额
						invokeRecord.setInvokeTime(DateUtil.getCurrentTime());
						if ("202000".equals(returnCode)) {
							invokeRecord.setState(1);
							//更新数据
							//poc扣款成功,直接进行修改状态就可以了
							logger.warn("POC逾期信贷订单POC扣款成功,crmOrderId:"+remark+",POC返回成功!");
							crmOrderService.overRepayByPoc(remark.split(":")[1]);
						}else{
							invokeRecord.setState(0);
							logger.warn("Poc逾期信贷订单的扣款操作失败："+remark+"，失败原因："+returnInfo);
							if (ChkUtil.isNotEmpty(returnInfo) && !returnInfo.contains("不足")) {
								JSONObject smsJson = new JSONObject();
								smsJson.put("text", "Poc逾期信贷订单的扣款操作失败："+remark+"，失败原因："+returnInfo);
								smsJson.put("project_number", "erpv3_auto2");
								SmsUtil.senErrorMsgByZhiyun(smsJson);
							}
						}
						os.add(invokeRecord);
					}
				}
			}
			if (os.size() > 0) {
				try {
					crmOrderDao.batchSaveOrUpdate(os, 500);
				} catch (Exception e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			logger.warn("Poc信贷正常还款的操作（downloadFileAC01）抛出异常---");
			JSONObject smsJson = new JSONObject();
			smsJson.put("text", "信贷订单自动还款，Poc信贷正常还款的操作（downloadFileAC01）抛出异常");
			smsJson.put("project_number", "erpv3_auto2");
			SmsUtil.senErrorMsgByZhiyun(smsJson);
		}
		logger.warn("Poc信贷订单的还款结束---");
	}
	/**
	 * 功能说明：自动短信通知
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
	public void scanToLaterDataToSms(){
		
		logger.warn("长时间不处理的数据查询开始!");
		//得到冻结的预授权成功的文件
		List<Map> laterList =  bgEfOrderService.findToLaterDataToSms(); // 得到所有冻结成功的记录并分组
		if(ListTool.isNotNullOrEmpty(laterList)){
			for(Map baseMap:laterList){
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", baseMap.get("unique_no"));
				smsJson.put("text","富友批量数据长时间未处理,请知晓!唯一编号:"+baseMap.get("unique_no"));
				SmsUtil.senErrorMsg(smsJson);
				try{
				bgEfOrderService.executeSql("UPDATE poc_batch_record set informed = '1' where unique_no='"+baseMap.get("unique_no")+"'");
				}catch (Exception e) {
					logger.warn(e.getMessage(),e);
				}
			}
		}
		logger.warn("长时间不处理的数据查询结束!");
	}
	/**
	 * 功能说明：寻找可以结清的理财订单的流水号进行boc的结清处理
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
	public void scanBocBgEforderFinishAuthCode(){
		logger.warn("寻找可以结清的理财订单的流水号进行boc的结清处理开始!");
		List<Map> finishBgEfordersList  =  bgEfOrderService.BocBgEforderFinish();
		//未找到则直接结束
		if(ListTool.isNullOrEmpty(finishBgEfordersList)){logger.warn("寻找可以结清的理财订单的流水号进行boc的结清处理结束!");return;}
		//开始逐个进行判断处理
		for(Map baseMap:finishBgEfordersList){
			try{
				//1.获得授权码
				//2.判断授权码对应的所有的理财订单都已经结清
				//3.若没有则跳过,否则修改订单的授权码结清状态字段
				String authCode = baseMap.get("invest_auz_code").toString();
				boolean authCodeOverFlag = bgEfOrderService.isOverAuthcode(authCode);
				if(authCodeOverFlag){ //结清了 需要发送结清申请,成功后修改结清状态
					JSONArray jsonArray = new JSONArray();
					JSONObject cuJson = new JSONObject();
					String accountId = baseMap.get("outBankAccount").toString(); //借款人电子账号
					String forAccountId = baseMap.get("InBankAccount").toString();//投资人账号
					String productId = baseMap.get("order_prd_number").toString(); //标的号
					cuJson.put("accountId", accountId);
					cuJson.put("forAccountId", forAccountId);
					cuJson.put("productId", productId);
					cuJson.put("authCode", authCode);
					jsonArray.add(cuJson);
					JSONObject paramJson = new JSONObject();
					paramJson.put("subPacks", jsonArray);
					paramJson.put("sendAppName", StaticData.appName);
					paramJson.put("serialNo", authCode);
					paramJson.put("remark", "boc批次结束债权");
					//JSONObject bocResult = WebServiceUtil.invokeBoc("batchPaymentFileService", "batchUploadFiles",new Object[]{jsonArray.toString(),baseMap.get("order_prd_number").toString(),"0",SignatureUtil.createSign()});
					JSONObject bocResult = WebServiceUtil.sendPost(StaticData.bocUrl+"payProcess/bocBatchCreditEnd", new Object[]{paramJson});
					if("0".equals(bocResult.getString("responseCode"))){
						logger.warn("自动解绑债权关系时失败!授权码:"+baseMap.get("invest_auz_code"));
						JSONObject smsJson = new JSONObject();
						smsJson.put("project_number", baseMap.get("crmOrderId"));
						smsJson.put("text", "自动解绑债权关系时失败!授权码:"+baseMap.get("invest_auz_code"));
						SmsUtil.senErrorMsg(smsJson);
					}
					if("1".equals(bocResult.getString("responseCode"))){
						logger.warn("自动解绑债权关系时成功!授权码:"+baseMap.get("invest_auz_code"));
						//开始更新数据
						bgEfOrderService.updateAuthCodeBgEforders(authCode);
					}
				}
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				e.printStackTrace();
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", baseMap.get("crmOrderId"));
				smsJson.put("text", "自动解绑债权关系时异常!授权码:"+baseMap.get("invest_auz_code"));
				SmsUtil.senErrorMsg(smsJson);
			}
		}
		logger.warn("寻找可以结清的理财订单的流水号进行boc的结清处理结束!");
	}
	/**
	 * 功能说明：自动更新划拨poc的批量文件并处理
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
	public void scanPocFileRecordToUpdateForOneStep(){
		logger.warn("手动还款的文件的处理开始!");
		//得到批量的预授权成功的文件
		List<Map> transList =  bgEfOrderService.findPocFileRecord("5"); // 得到所有预授权成功的记录并分组
		if(ListTool.isNullOrEmpty(transList)){logger.warn("手动还款的文件的处理结束!");return ;}
		//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		for(Map baseMap:transList){ 
			String fileName = baseMap.get("transferbu_file_name").toString();
			Map pocMap = new HashMap();
			pocMap.put("fileName", fileName);
			List<String> dataList = new ArrayList<String>();
			List<String> rejectdataList = new ArrayList<String>();
			try{
				JSONObject pocJson  = JSONObject.fromObject(pocTool.connectToPoc("downloadFileByName", pocMap));
				if(pocJson.containsKey("responseCode") && "0".equals(pocJson.getString("responseCode"))){
					continue;
				}
				dataList = ListTool.getList(pocJson.get("data"));
				try{
					if(pocJson.containsKey("rejectData")){
						rejectdataList = ListTool.getList(pocJson.get("rejectData"));
						for(String rejectString:rejectdataList){
							String[] rejectArray = rejectString.split(Pattern.quote("|"));
							//需要将本次的明细直接改成结束
							bgEfOrderService.updateFailPocRecord(baseMap.get("unique_no").toString(), rejectArray[8].split(Pattern.quote(":"))[1], baseMap.get("type").toString(),rejectArray[11]);
						}
						//更新文件名
						if(pocJson.containsKey("fileName")){
							//更新文件名
							bgEfOrderService.updateRejectFileNamePocRecord(baseMap.get("transferbu_file_name").toString(),pocJson.getString("fileName"),"2");
						}
					}
				}catch (Exception e) {
					logger.warn(e.getMessage(),e);
					
				}
				if(dataList.size()==0){
					continue;
				}
			}catch (Exception e) {
				logger.warn("手动还款的划拨文件处理异常!文件名:"+fileName);
				JSONObject smsJson = new JSONObject();
				smsJson.put("text", "自动处理时手动还款的划拨文件处理异常!文件名:"+fileName);
				SmsUtil.senErrorMsg(smsJson);
				continue;
			}
			//开始将划拨返回的文件处理
			List<String> uploadList = new ArrayList<String>();
			for(String baseString:dataList){
				String[] baseData = baseString.split(Pattern.quote("|")); //拆解基础数据
				if(!"0000".equals(baseData[10])){ //划拨失败了
					//需要将本次的明细直接改成结束
					bgEfOrderService.updateFailPocRecord(baseMap.get("unique_no").toString(), baseData[8].split(Pattern.quote(":"))[1], baseMap.get("type").toString(),baseData[11]);
					continue;
				}
				String resultString = baseData[8];//备注
				logger.warn("本次数据:"+resultString);
				uploadList.add(resultString);
			}
			//准备上传划拨文件
			//开始处理数据
			//成功了则需要结束了,增加冻结记录
			//开始进行记录
			if(ListTool.isNotNullOrEmpty(uploadList)){
				for(String keyId:uploadList){
					try {
						String Id = keyId.split(Pattern.quote(":"))[1];
						String type = keyId.split(Pattern.quote(":"))[0];
						String baseType="1";
						if("bg_ef_order_id".equals(type)){
							baseType="-1";
						}
						Map freezeInfo = bgEfOrderService.getLastBatchFileInfo(Id,baseType);
						if(freezeInfo == null ){
							continue;
						}
						EfFundRecord efr = new EfFundRecord();
						efr.setCreateTime(DateUtil.getCurrentTime());
						efr.setCustId(freezeInfo.get("cust_id").toString());
						efr.setCustInfoId(freezeInfo.get("cust_info_id").toString());
						efr.setEfApplayId(freezeInfo.get("ef_applay_id").toString());
						efr.setEfOrderId(freezeInfo.get("ef_order_id").toString());
						efr.setBgEfOrderId(freezeInfo.get("bg_ef_order_id").toString());
						efr.setEmpId(freezeInfo.get("emp_id").toString());
						efr.setMoney(NumberUtil.parseDouble(freezeInfo.get("money").toString()));
						efr.setRecordStatus(1);
						efr.setRecordType("还款");
						bgEfOrderService.save(efr);
					} catch (Exception e) {
						e.printStackTrace();
						logger.warn(e.getMessage(),e);
						JSONObject smsJson = new JSONObject();
						smsJson.put("text", "自动处理时手动还款的划拨文件时站岗资金添加异常!文件名:"+fileName+",id:"+keyId);
						SmsUtil.senErrorMsg(smsJson);
					}
				}
			}
			bgEfOrderService.updateSuccessPocBatchRecord(fileName); 
			
			
		}
		logger.warn("手动还款的划拨文件的处理结束!");
	
	}
	/**
	 * 功能说明：自动短信通知
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
	public void solveUploadFileFailRecords(){
		
		logger.warn("查询上传失败的数据开始!");
		// 得到上传失败的数据
		List<Map> failList =  bgEfOrderService.findUploadFileFailRecords(); 
		if(ListTool.isNullOrEmpty(failList)){logger.warn("查询上传失败的数据开始结束!");}
		//循环查找对应的数据进行重新上传处理
		for(Map baseMap:failList){
			//查找唯一编码下的所有数据进行上传处理
			List<Map> failDataList =  bgEfOrderService.findFailDataByUniqueno(baseMap.get("unique_no").toString());
			if(ListTool.isNotNullOrEmpty(failDataList)){
				List<String> dataList = new ArrayList<String>();
				for(Map failMap:failDataList){ //准备拼数据上传
					String outAccount = StaticData.creditAccount;
					String outName = StaticData.creditAccountName;
					String inAccount = failMap.get("fy_account").toString();
					String inName = failMap.get("cust_name").toString();
					String money = NumberFormat.formatDouble(Double.parseDouble(failMap.get("money").toString()));
					String remark = "";
					if(failMap.get("type").toString().equals("1")){ // 正常还款
						remark = "bg_ef_paycontrol_id:"+ failMap.get("bg_ef_paycontrol_id").toString();
						//18306135500|苏州中资联信用管理有限公司|0|13275177201|袁浩|1|0.02|Test|
						String line = outAccount+"|"+outName+"|0|"+inAccount+"|"+inName+"|1|"+money+"|"+remark+"|";
						dataList.add(line);

					}
					if(failMap.get("type").toString().equals("-1")){ // 正常还款
						remark = "bg_ef_order_id:"+ failMap.get("bg_ef_order_id").toString();
						//18306135500|苏州中资联信用管理有限公司|0|13275177201|袁浩|1|0.02|Test|
						String line = outAccount+"|"+outName+"|0|"+inAccount+"|"+inName+"|1|"+money+"|"+remark+"|";
						dataList.add(line);
					}
				}
				//
				if(ListTool.isNullOrEmpty(dataList)){continue;}
				Map pocMap = new HashMap();
				pocMap.put("code", "PW03");
				pocMap.put("data", dataList);
				try{
					//数据测试
					JSONObject pocResult = JSONObject.fromObject(pocTool.connectToPoc("uploadFilePW03.html", pocMap));
					if("1".equals(pocResult.getString("responseCode"))){ //文件上传成功
						logger.warn("文件上传成功!");
						//文件名
						String fileName = pocResult.getString("fileName");
						bgEfOrderService.updateAgainSuccessPocBatchRecord(fileName,baseMap.get("unique_no").toString());
					}else{
						logger.warn("文件上传失败!");
						JSONObject smsJson = new JSONObject();
						smsJson.put("project_number", "uploadFilePW03");
						smsJson.put("text", "理财还款文件上传失败!唯一编号:");
						SmsUtil.senErrorMsg(smsJson);
					}
				}catch (Exception e) { //上传文件报错
					e.printStackTrace();
					logger.warn(e.getMessage(),e);
					logger.warn("理财还款文件重复上传失败!唯一编号:"+baseMap.get("unique_no").toString());
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", "uploadFilePW03");
					smsJson.put("text", "理财还款文件重复上传失败!唯一编号:"+baseMap.get("unique_no").toString());
					SmsUtil.senErrorMsg(smsJson);
				}
			}
		}
		
		
		
		logger.warn("查询上传失败的数据开始结束!");
	}
	
}
