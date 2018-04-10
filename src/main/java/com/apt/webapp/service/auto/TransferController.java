 package com.apt.webapp.service.auto;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.apt.util.ChkUtil;
import com.apt.util.StaticData;
import com.apt.util.arith.ArithUtil;
import com.apt.util.date.DateUtil;
import com.apt.util.sms.SmsUtil;


@Controller
public class TransferController {
	//日志
	private static Logger logger = LoggerFactory.getLogger(TransferController.class);
	@Resource
	private AutoUpdateThread autoUpdateThread;
	@Resource
	private AutoUpdatePocFileRecord autoUpdatePocFileRecord;
	
	@RequestMapping("toTest")
	public  void toTest(HttpServletRequest request,HttpServletResponse response,String authCodes,String executeFaildPris) throws IOException{
		response.getWriter().print("success");
	}
	/**
	 * 功能说明：线上理财自动还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toNoticeTransfer")
	public synchronized void toExecuteOnLineEfAutopay(HttpServletRequest request,HttpServletResponse response,String authCodes,String executeFaildPris) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========BOC理财自动还款通知开始======");
		logger.warn("本次反馈的授权码数据:"+authCodes);
		logger.warn("本次反馈的错误数据:"+executeFaildPris);
		JSONArray shulouArray = new JSONArray();
		JSONArray noramlArray = new JSONArray();
		String[] shulouStr = null;
		String[] noramlStr = null;
		try{
			if(authCodes==null || "".equals(authCodes)){
				response.getWriter().print("success");
				return;
			}
			JSONArray baseArray = JSONArray.fromObject(authCodes);
			for(Object baseObj:baseArray){
				JSONObject baseJson = JSONObject.fromObject(baseObj);
				if(baseJson.getString("thredPriKey").split(Pattern.quote(":"))[0].equals("3_1")){
					noramlArray.add(baseJson);
				}
				if(baseJson.getString("thredPriKey").split(Pattern.quote(":"))[0].equals("3_3")){
					shulouArray.add(baseJson);
				}
			}
			if(executeFaildPris!=null && executeFaildPris.length()>1){
				String[] executeFaildPrisArray = executeFaildPris.split(",");
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", "batchUploadFiles_notice");
				smsJson.put("text", "boc理财明细在进行批量债权转让时部分失败!");
				SmsUtil.senErrorMsg(smsJson);
				shulouStr = new String[executeFaildPrisArray.length];
				int shulouCount=0;
				for(String baseString:executeFaildPrisArray){
					if(baseString.startsWith("3_3")){
						shulouStr[shulouCount] = baseString;
						shulouCount++;
						continue;
					}
				}
			}
			synchronized (this) {
				autoUpdateThread.run(noramlArray);
				/*
			int i=1;
			String date = DateUtil.getCurrentTime(DateUtil.STYLE_2);
			String beforedate = DateUtil.getBefore(new Date(), -1, DateUtil.STYLE_2);
			while(autoUpdateThread.tempMap.containsKey(beforedate+"_1")){
				for(int j=1;j<20;j++){
					if(autoUpdateThread.tempMap.containsKey(beforedate+"_"+j)){
						autoUpdateThread.tempMap.remove(beforedate+"_"+j);
					}
				}
			}
			while(autoUpdateThread.tempMap.containsKey(date+"_"+i)){
				i++;
			}
			autoUpdateThread.tempMap.put(date+"_"+i, noramlArray);
			if(autoUpdateThread.getState()==Thread.State.NEW){
				autoUpdateThread.start();
			}
			*/}
			logger.warn("==========BOC理财自动还款通知结束=====");
			response.getWriter().print("success");
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			response.getWriter().print("success");
			return;
		}
		return;
	}
	/**
	 * 功能说明：poc还款通知接口
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toPocPaymentNotice")
	public synchronized void toNoticePocTransfer(HttpServletRequest request,HttpServletResponse response,String data) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========POC理财还款通知开始======");
		logger.warn("POC/HF还款通知数据:"+data);
		try{
			JSONObject json = JSONObject.fromObject(data);
			String code = json.getString("code");
			JSONArray dataJson = json.getJSONArray("data");
			//开始处理数据发送到清结算
			if("PW03".equals(code)){
				//JSONArray  flowArray = getPW03Data(dataJson);
				//SmsUtil.sendFlowRecord(flowArray);
			}
			if("AC01".equals(code)){
				//JSONArray  flowArray = getAC01Data(dataJson);
				//SmsUtil.sendFlowRecord(flowArray);
			}
			JSONArray successJson = new JSONArray();
			String errorMsg = "";
			if(dataJson.size()<=0){
				response.getWriter().print("success");
				return;
			}
			for(Object baseString:dataJson){
				String[] str = baseString.toString().split(Pattern.quote("|"));
				if(str.length<5){
					continue;
				}
				String responseCode="";
				if("PW03".equals(code)){ //划拨的批量
					responseCode = str[10]; // 返回码;
					String hfResponseCode = str[11]; // 返回码;
					String errorResponseCode = "";
					if(str.length>12){
						errorResponseCode = str[12]; // 返回码;有可能多了一个
					}
					if("0000".equals(responseCode) || "0000".equals(hfResponseCode) || "0000".equals(errorResponseCode)){
						successJson.add(baseString);
					}else{
						errorMsg+=baseString.toString();
					}
				}
				if("AC01".equals(code)){ //代扣的批量
					responseCode = str[8]; // 返回码
					if(str.length>10) {
						responseCode= str[10];
					}
					if("202000".equals(responseCode)){
						successJson.add(baseString);
					}else{
						errorMsg+=baseString.toString();
					}
				}
			}
			if(!"".equals(errorMsg)){
				logger.warn("POC/HF还款通知失败数据:"+errorMsg);
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", "PocNoticeError");
				smsJson.put("text","POC/HF手动还款通知失败数据:"+errorMsg);
				SmsUtil.senErrorMsg(smsJson);
			}
			synchronized (this) {
				if("PW03".equals(code)){
					autoUpdatePocFileRecord.doSaveEffunderrecord(successJson,"PW03");
				}
				if("AC01".equals(code)){
					autoUpdatePocFileRecord.doSaveEffunderrecord(successJson,"AC01");
				}
			}
			logger.warn("==========POC理财还款通知结束=====");
			response.getWriter().print("success");
		}catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage(),e);
			response.getWriter().print("success");
			logger.warn("==========POC理财还款通知结束=====");
			return;
		}
		return;
	}
	/**
	 * 功能说明：信贷自动还款的数据
	 * yuanhao  2017-07-05
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public JSONArray getAC01Data(JSONArray data){
		JSONArray ac01JsonArray = new JSONArray();
		if(data ==null ||data.size()==0){return ac01JsonArray;}
		for(Object object:data){
			String[] str = object.toString().split(Pattern.quote("|"));
			String remark = str[6]; // 备注信息
			String returnCode = str[8]; // 返回码
			if(str.length>10) {
				returnCode= str[10];
			}
			JSONObject flowRecordJson = new JSONObject();
			flowRecordJson.put("orderType", "crm_paycontrol");
			flowRecordJson.put("status", "0");
			flowRecordJson.put("code", returnCode);
			flowRecordJson.put("rem", "");
			//说明是自动信贷还款
			if ("auto_cpc_id".equals(remark.split(":")[0])) {
				//扣款成功
				flowRecordJson.put("payControlId", remark.split(":")[1]);
				if ("202000".equals(returnCode)) {
					flowRecordJson.put("status", "1");
				} else { //扣款失败了
					flowRecordJson.put("status", "0");
				}
				ac01JsonArray.add(flowRecordJson);
			}
		}
		return ac01JsonArray;
	}
	/**
	 * 功能说明：理财自动还款的数据
	 * yuanhao  2017-07-05
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public JSONArray getPW03Data(JSONArray data){
		JSONArray pw03JsonArray = new JSONArray();
		if(data ==null ||data.size()==0){return pw03JsonArray;}
		for(Object object:data){
			String[] str = object.toString().split(Pattern.quote("|"));
			if(str.length<5){
				continue;
			}
			String remark = str[8]; // 备注信息
			String returnCode = str[10]; // 返回码
			String hfReturnCode = str[11]; // 返回码
			JSONObject flowRecordJson = new JSONObject();
			flowRecordJson.put("orderType", "bg_ef_paycontrol");
			flowRecordJson.put("status", "0");
			flowRecordJson.put("code", returnCode);
			flowRecordJson.put("rem", "");
			//说明是自动信贷还款
			if ("auto_bepc_id".equals(remark.split(":")[0])) {
				//扣款成功
				flowRecordJson.put("payControlId", remark.split(":")[1]);
				if ("0000".equals(returnCode) || "0000".equals(hfReturnCode)) {
					flowRecordJson.put("status", "1");
				} else { //扣款失败了
					flowRecordJson.put("status", "0");
				}
				pw03JsonArray.add(flowRecordJson);
			}
		}
		return pw03JsonArray;
	}
}
