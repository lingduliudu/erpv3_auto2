package com.apt.webapp.service.auto;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.apt.util.StaticData;
import com.apt.util.WebServiceUtil;
import com.apt.util.arith.ArithUtil;
import com.apt.util.singature.SignatureUtil;
import com.apt.util.sms.SmsUtil;
import com.apt.webapp.model.bg.ef.BgCustInfo;
import com.apt.webapp.model.ef.EfOrders;
import com.apt.webapp.service.ef.IEfOrderService;
import com.apt.webapp.service.ef.IEfPaycontrolService;
import com.apt.webapp.service.impl.ef.EfPayOrderServiceImpl;
import com.apt.webapp.task.ransomFloor.IRansomFloorTask;
import com.apt.webapp.task.ransomFloor.IRansomFloorTaskNew;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Controller
public class TestController {
	//日志
	private static Logger logger = LoggerFactory.getLogger(TestController.class);
	@Resource
	private AutoRun autoRun;
	@Resource
	private V2AutoRepayment v2AutoRepayment;
	@Resource
	private V1AutoRun v1autoRun;
	@Resource
	private IRansomFloorTask ransomFloorTask;
	@Resource
	private AutoUpdatePocFileRecord autoUpdatePocFileRecord;
	@Resource
	private AutoRunBoc autoRunBoc;
	@Resource
	private AutoRunDete autoRunDete;
	@Resource
	private IEfOrderService efOrderService;
	@Resource
	private AutoRunBF autoRunBF;
	@Resource
	private AutoRunHF autoRunHF;
	@Resource
	private AutoRunCrmPaycontrolStatistics autoRunCrmPaycontrolStatistics;
	@Resource
	private IRansomFloorTaskNew ransomFloorTaskNew;
	@Resource
	private IEfPaycontrolService efPaycontrolService;
	/**
	 * 功能说明：手动理财的还款触发
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteNotAutoCrmPay")
	public void toExecuteNotAutoCrmPay(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========boc手动信贷还款债权转让开始======");
		//StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoRunDete.toExecuteEfDeteRecord();
				logger.warn("==========boc手动信贷还款债权转让结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				//StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass=true;
		}
		//StaticData.fakeTime="";
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		return;
	}
	/**
	 * 功能说明：恒丰银行的还款处理
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteEfAutopayHFBatchUpload")
	public void toExecuteEfAutopayPocBatchUpload(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========恒丰理财自动还款（批量划拨）开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoRunHF.EfAutopayHFBatchUpload();
				logger.warn("==========恒丰理财自动还款（批量划拨）结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=true;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass=true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：boc直投债权转让文件的触发
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteImmeEfBocTransfer")
	public void toExecuteImmeEfBocTransfer(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========boc直投债权转让开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoRunBoc.ImmeEfBocTransfer();
				logger.warn("==========boc直投债权转让结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass=true;
		}
		StaticData.fakeTime="";
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		return;
	}
	/**
	 * 功能说明：boc定投债权转让文件的触发
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteDeteEfBocTransfer")
	public void toExecuteDeteEfBocTransfer(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========boc定投债权转让开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoRunBoc.DeteEfBocTransfer();
				logger.warn("==========boc定投债权转让结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass=true;
		}
		StaticData.fakeTime="";
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		return;
	}
	/**
	 * 功能说明：poc批量文件长时间未处理短信通知处理
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteLaterDataNotice")
	public void toExecuteLaterDataNotice(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========poc批量文件长时间未处理短信通知处理开始======");
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoUpdatePocFileRecord.scanToLaterDataToSms();
				logger.warn("==========poc批量文件长时间未处理短信通知处理结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass=true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		return;
	}
	/**
	 * 功能说明：预授权文件的扫描处理
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecutePreauthFilerecordToUpdate")
	public void toExecutePreauthFilerecordToUpdate(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========预授权文件的扫描处理开始======");
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoUpdatePocFileRecord.scanPreauthFilerecordToUpdate();
				logger.warn("==========预授权文件的扫描处理结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass=true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		return;
	}
	/**
	 * 功能说明：划拨文件的扫描处理
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteTransferBuFilerecordToUpdate")
	public void toExecuteTransferBuFilerecordToUpdate(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========划拨文件的扫描处理开始======");
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoUpdatePocFileRecord.scanTransferBuFilerecordToUpdate();
				logger.warn("==========划拨文件的扫描处理结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass=true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		return;
	}
	/**
	 * 功能说明：冻结文件的扫描处理
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteFreezeFilerecordToUpdate")
	public void toExecuteFreezeFilerecordToUpdate(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========冻结文件的扫描处理开始======");
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoUpdatePocFileRecord.scanFreezeFilerecordToUpdate();
				logger.warn("==========冻结文件的扫描处理结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass=true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		return;
	}
	/**
	 * 功能说明：结清授权码的扫描处理
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteScanBocAuthClear")
	public void toExecuteScanBocAuthClear(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========结清授权码的扫描处理开始======");
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoUpdatePocFileRecord.scanBocBgEforderFinishAuthCode();
				logger.warn("==========结清授权码的扫描处理结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass=true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		return;
	}
	/**
	 * 功能说明：冻结文件的扫描处理
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecutePocFileRecordToUpdateForOneStep")
	public void toExecutePocFileRecordToUpdateForOneStep(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========冻结文件的扫描处理开始======");
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoUpdatePocFileRecord.scanPocFileRecordToUpdateForOneStep();
				logger.warn("==========冻结文件的扫描处理结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass=true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		return;
	}
	/**
	 * 功能说明：直投BOC理财自动还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteFtpCrmPayImmePoc")
	public void toExecuteFtpCrmPayImmePoc(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========toExecuteFtpCrmPayImmePoc开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoRun.ftpCrmPayImmePoc();
				logger.warn("==========toExecuteFtpCrmPayImmePoc结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass=true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：直投BOC理财自动还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteImmeEfAutopayBoc")
	public void toExecuteImmeEfAutopayBoc(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========直投BOC理财自动还款开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoRun.toExecuteImmeEfAutopayBoc();
				logger.warn("==========直投BOC理财自动还款结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass=true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：直投Poc理财自动还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteImmeEfAutopayPoc")
	public void toExecuteImmeEfAutopayPoc(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========直投Poc理财自动还款开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoRun.toExecuteImmeEfAutopayPoc();
				logger.warn("==========直投Poc理财自动还款结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass=true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：定投BOC理财自动还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteDeteEfAutopayBoc")
	public void toExecuteDeteEfAutopayBoc(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========定投BOC理财自动还款开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoRun.toExecuteDeteEfAutopayBoc();
				logger.warn("==========定投BOC理财自动还款结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass=true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：定投POC理财自动还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteDeteEfAutopayPoc")
	public void toExecuteDeteEfAutopayPoc(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========定投POC理财自动还款开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoRun.toExecuteDeteEfAutopayPoc();
				logger.warn("==========定投POC理财自动还款结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass=true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：新手标还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteEfAutopayTasteOrder")
	public void toExecuteEfAutopayTasteOrder(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========新手标还款开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRun.toExecuteEfAutopayTasteOrder();
				logger.warn("==========新手标还款结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：直投Boc信贷自动还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteImmeCrmAutopayBoc")
	public void toExecuteImmeCrmAutopayBoc(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========直投Boc信贷自动还款开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRun.toExecuteImmeCrmAutopayBoc();
				logger.warn("==========直投Boc信贷自动还款结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：直投Poc信贷自动还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteImmeCrmAutopayPoc")
	public void toExecuteImmeCrmAutopayPoc(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========直投Poc信贷自动还款开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRun.toExecuteImmeCrmAutopayPoc();
				logger.warn("==========直投Poc信贷自动还款结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：真实理财利息还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteEfOrderPkgOrderInterest")
	public void toExecuteEfOrderPkgOrderInterest(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========真实理财利息还款开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRun.toExecuteEfOrderPkgOrderInterest();
				logger.warn("==========真实理财利息还款结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：定投BOC信贷自动还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteDeteCrmAutopayBoc")
	public void toExecuteDeteCrmAutopayBoc(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========定投BOC信贷自动还款开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
			autoRun.toExecuteDeteCrmAutopayBoc();
			logger.warn("==========定投BOC信贷自动还款结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：定投POC信贷自动还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteDeteCrmAutopayPoc")
	public void toExecuteDeteCrmAutopayPoc(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========定投POC信贷自动还款开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRun.toExecuteDeteCrmAutopayPoc();
				logger.warn("==========定投POC信贷自动还款结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：直投BOC信贷逾期还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteOverImmeCrmAutopayBoc")
	public void toExecuteOverImmeCrmAutopayBoc(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========直投BOC信贷逾期还款开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRun.toExecuteOverImmeCrmAutopayBoc();
				logger.warn("==========直投BOC信贷逾期还款结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：直投POC信贷逾期还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteOverImmeCrmAutopayPoc")
	public void toExecuteOverImmeCrmAutopayPoc(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========直投POC信贷逾期还款开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRun.toExecuteOverImmeCrmAutopayPoc();
				logger.warn("==========直投POC信贷逾期还款结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：定投Boc逾期自动还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteOverDeteCrmAutopayBoc")
	public void toExecuteOverDeteCrmAutopayBoc(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========定投Boc逾期自动还款开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRun.toExecuteOverDeteCrmAutopayBoc();
				logger.warn("==========定投Boc逾期自动还款结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：定投Poc逾期自动还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteOverDeteCrmAutopayPoc")
	public void toExecuteOverDeteCrmAutopayPoc(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========定投Poc逾期自动还款开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRun.toExecuteOverDeteCrmAutopayPoc();
				logger.warn("==========定投Poc逾期自动还款结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：信贷逾期
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteAutoOverCrmOrder")
	public void toExecuteAutoOverCrmOrder(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========信贷逾期开始======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRun.toExecuteAutoOverCrmOrder();
				logger.warn("==========信贷逾期结束======");
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**********************************************************************************************************************************************************/
	/**
	 * 功能说明：v2逾期还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("overdueRepayment2")
	public void overdueRepayment2(HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				v2AutoRepayment.overdueRepayment();
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：v2自动逾期
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception 
	 */
	@RequestMapping("autoOverdue2")
	public void autoOverdue2(HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				v2AutoRepayment.autoOverdue();
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：v2自动还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("dueRepayment2")
	public void dueRepayment2(HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				v2AutoRepayment.dueRepayment();
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**********************************************************************************************************************************************************/
	/**
	 * 功能说明：v1逾期还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("overdueRepayment1")
	public void overdueRepayment1(HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				v1autoRun.toExecuteV1OverCrmAutopay();
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：v1自动逾期
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 * @throws Exception 
	 */
	@RequestMapping("autoOverdue1")
	public void autoOverdue1(HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				v1autoRun.toExecuteAutoOverV1CrmOrder();
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：v1自动还款
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("dueRepayment1")
	public void dueRepayment1(HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				v1autoRun.toExecuteV1CrmAutopay();
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：修改boc接口状态
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("testSms")
	public void testSms(HttpServletRequest request,HttpServletResponse response) throws IOException{
		JSONObject smsJson = new JSONObject();
		smsJson.put("project_number", "autoTestSms");
		smsJson.put("text","测试sms接口是否可用!");
		SmsUtil.senErrorMsg(smsJson);
	}
	/**
	 * 功能说明：修改boc接口状态
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("changeParams")
	public void changeParams(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		String closeBoc  = request.getParameter("closeBoc");
		String closePoc  = request.getParameter("closePoc");
		String closeBocTransFile  = request.getParameter("closeBocTransFile");
		String pass = request.getParameter("pass");
		response.setContentType("text/html;charset=utf-8");
		if("yes".equals(closeBoc)){
			StaticData.closeBoc = true;
		}
		if("no".equals(closeBoc)){
			StaticData.closeBoc = false;
		}
		if("yes".equals(closePoc)){
			StaticData.closePoc = true;
		}
		if("no".equals(closePoc)){
			StaticData.closePoc = false;
		}
		if("yes".equals(pass)){
			StaticData.pass = true;
		}
		if("no".equals(pass)){
			StaticData.pass = false;
		}
		if("yes".equals(closeBocTransFile)){
			StaticData.closeBocTransFile = true;
		}
		if("no".equals(closeBocTransFile)){
			StaticData.closeBocTransFile = false;
		}
		
		response.getWriter().println("当前boc的端口状态:"+!StaticData.closeBoc+"<br/>");
		response.getWriter().println("当前Poc的端口状态:"+!StaticData.closePoc+"<br/>");
		response.getWriter().println("当前操作状态:"+StaticData.pass+"<br/>");
		response.getWriter().println("当前债权转让操作状态:"+!StaticData.closeBocTransFile+"<br/>");
		return;
	}
	public static void main(String[] args) {
	}
	public static String formatDouble(double num){
		DecimalFormat  df=new DecimalFormat("0.00");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(num);
	}
	
	@RequestMapping("executeRansomFloorEfPaycontrol")
	public void executeRansomFloorEfPaycontrol(HttpServletRequest request,HttpServletResponse response) throws IOException{
		response.setContentType("text/html;charset=utf-8");
		ransomFloorTask.executeRansomFloorEfPaycontrol();
		response.getWriter().println("处理结束!");
		return;
	}
	@RequestMapping("zhixingchengwei")
	public void zhixingchengwei(HttpServletRequest request,HttpServletResponse response) throws IOException{
		response.setContentType("text/html;charset=utf-8");
		JSONObject bocJson = new JSONObject();
		
		String[] bank_account={
				"6212461030000597751"
		};
		String[] principal={
				
				"1000.00"
		};
		String[] auto_serino={
				"20170731093929834814"
		};
		String[] serialno={
				"00000920170712095131827403300A"
				
		};
		String[] efOrderId={
				"0757c45ed03742fba2a918420f932477"
		};
		String[] efApplyId={
				"703af5654b4949479b8a64cc677d0335"
		};
		JSONObject bocResult = new JSONObject();
		try {
			String str="";
			for (int i = 0; i < bank_account.length; i++) {
				//准备查找原始的授权码
				bocJson.clear();
				bocResult.clear();
				bocJson.put("cardNbr", bank_account[i]);
				bocJson.put("authCode",auto_serino[i]);
				bocJson.put("sendAppName","cw");
				bocJson.put("remark","");
				//请求撤销
				bocResult = WebServiceUtil.sendPost(StaticData.bocUrl+"payProcess/bocBidCancel",new Object[]{bocJson});;
				if("0".equals(bocResult.getString("responseCode"))){
					logger.warn("code:撤销失败;efOrderId:"+efOrderId[i]+";efApplyId:"+efApplyId[i]);
				}
				if("1".equals(bocResult.getString("responseCode"))||"2".equals(bocResult.getString("responseCode"))){
					logger.warn("code:撤销成功;efOrderId:"+efOrderId[i]+";efApplyId:"+efApplyId[i]);
					
					
					bocJson.clear();
					bocResult.clear();
					bocJson.put("accountId", bank_account[i]);
					bocJson.put("productId","ZZZZZ1");
					bocJson.put("txAmount",principal[i]);
					//查找对应的投标交易流水号
					bocJson.put("contOrderId", serialno[i]);
					//查找期数
					bocJson.put("acqRes", "");
					bocJson.put("sendAppName","cw");//
					bocJson.put("signature", SignatureUtil.createSign());
					bocJson.put("remark","");//保留域
					bocResult =  WebServiceUtil.sendPost(StaticData.bocUrl+"payProcess/bocIdAutoApply",new Object[]{bocJson});
					if("0".equals(bocResult.getString("responseCode"))){
						logger.warn("code:冻结失败;efOrderId:"+efOrderId[i]+";efApplyId:"+efApplyId[i]);
					}
					if("1".equals(bocResult.getString("responseCode"))){
						logger.warn("code:冻结成功;efOrderId:"+efOrderId[i]+";efApplyId:"+efApplyId[i]);
						logger.warn(bocResult.toString());
						//开始修改理财订单表
						if(bocResult.containsKey("response_map")){
							efOrderService.updateAuthCode(efOrderId[i],bocResult.getJSONObject("response_map").getString("AUTHCODE"));
						}else{
							efOrderService.updateAuthCode(efOrderId[i],bocResult.getString("AUTHCODE"));
						}
					}
//				}
			}
		} 
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
		}
		response.getWriter().println("处理结束!");
	}
	@RequestMapping("fixEfOrder")
	public void fixEfOrder(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		response.setContentType("text/html;charset=utf-8");
		JSONObject bocJson = new JSONObject();
		String efOrdersId=request.getParameter("id");
		JSONObject bocResult = new JSONObject();
		try {
			//准备查找原始的授权码
			String autoCode = efOrderService.findLineAuthCode(efOrdersId);
			EfOrders eo = (EfOrders) efOrderService.findById(EfOrders.class, efOrdersId);
			BgCustInfo bci = (BgCustInfo) efOrderService.findById(BgCustInfo.class, eo.getCustInfoId());
			bocJson.put("cardNbr",bci.getBankAccount());
			bocJson.put("authCode",autoCode);
			bocJson.put("sendAppName",StaticData.appName);
			bocJson.put("remark","");
			bocJson.put("signature",SignatureUtil.createSign());
			//请求撤销
			bocResult = WebServiceUtil.sendPost(StaticData.bocUrl+"payProcess/bocBidCancel",new Object[]{bocJson});;
			if("0".equals(bocResult.getString("responseCode"))){
				logger.warn("撤销定投Boc投资订单时失败!线下理财id:"+efOrdersId);
				JSONObject smsJson = new JSONObject();
				smsJson.put("project_number", eo.getOrderNumber());
				smsJson.put("text","定投Boc撤销投资订单时失败!线下理财id:"+efOrdersId+efOrdersId+bocResult.toString());
				SmsUtil.senErrorMsg(smsJson);
			}
			if("1".equals(bocResult.getString("responseCode"))||"2".equals(bocResult.getString("responseCode"))){ //2的意思是授权码已经撤销过了
				//获得冻结标信息
				JSONObject A1Json = efOrderService.findFreezenProduct();
				Double allMoney = efOrderService.findAllMoney(efOrdersId);
				bocJson.clear();
				bocResult.clear();
				bocJson.put("accountId", bci.getBankAccount());
				bocJson.put("productId",A1Json.get("product"));
				bocJson.put("txAmount",allMoney);
				//查找对应的投标交易流水号
				String auto_serino = efPaycontrolService.findAutoSerino(eo.getCustInfoId());
				bocJson.put("contOrderId", auto_serino);
				//查找期数
				bocJson.put("acqRes", "");
				bocJson.put("signature", SignatureUtil.createSign());
				bocJson.put("sendAppName",StaticData.appName);//
				bocJson.put("remark","");//保留域
				bocResult =  WebServiceUtil.sendPost(StaticData.bocUrl+"payProcess/bocIdAutoApply",new Object[]{bocJson});
				if("0".equals(bocResult.getString("responseCode"))){
					logger.warn("定投Boc重新投资订单时失败!线下理财id:"+efOrdersId+bocResult.toString());
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", eo.getOrderNumber());
					smsJson.put("text","定投Boc重新投资订单时失败!线下理财id:"+efOrdersId+efOrdersId+bocResult.toString());
					SmsUtil.senErrorMsg(smsJson);
				}
				if("1".equals(bocResult.getString("responseCode"))){
					logger.warn("定投Boc重新投资订单时成功!线下理财id:"+efOrdersId+bocResult.toString());
					//开始修改理财订单表
					if(bocResult.containsKey("response_map")){
						efOrderService.updateAuthCode(efOrdersId,bocResult.getJSONObject("response_map").getString("AUTHCODE"));
					}else{
						efOrderService.updateAuthCode(efOrdersId,bocResult.getString("AUTHCODE"));
					}
				}
			}
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			response.getWriter().print("<script>alert('Mission failure!')</script>");
			return;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
	}
	@RequestMapping("fixEfOrderAll")
	public void fixEfOrderAll(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		response.setContentType("text/html;charset=utf-8");
		JSONObject bocJson = new JSONObject();
		String efOrdersIds=request.getParameter("ids");
		String[] efIds = efOrdersIds.split(Pattern.quote(","));
		if(efIds == null || efIds.length==0){
			response.getWriter().print("<script>alert('Mission complete!')</script>");
			return;
		}

		try {
			for(String efOrdersId:efIds){
				JSONObject bocResult = new JSONObject();
				//准备查找原始的授权码
				String autoCode = efOrderService.findLineAuthCode(efOrdersId);
				EfOrders eo = (EfOrders) efOrderService.findById(EfOrders.class, efOrdersId);
				BgCustInfo bci = (BgCustInfo) efOrderService.findById(BgCustInfo.class, eo.getCustInfoId());
				bocJson.put("cardNbr",bci.getBankAccount());
				bocJson.put("authCode",autoCode);
				bocJson.put("sendAppName",StaticData.appName);
				bocJson.put("remark","");
				bocJson.put("signature",SignatureUtil.createSign());
				//请求撤销
				bocResult = WebServiceUtil.sendPost(StaticData.bocUrl+"payProcess/bocBidCancel",new Object[]{bocJson});;
				if("0".equals(bocResult.getString("responseCode"))){
					logger.warn("撤销定投Boc投资订单时失败!线下理财id:"+efOrdersId);
					JSONObject smsJson = new JSONObject();
					smsJson.put("project_number", eo.getOrderNumber());
					smsJson.put("text","定投Boc撤销投资订单时失败!线下理财id:"+efOrdersId+efOrdersId+bocResult.toString());
					SmsUtil.senErrorMsg(smsJson);
				}
				if("1".equals(bocResult.getString("responseCode"))||"2".equals(bocResult.getString("responseCode"))){ //2的意思是授权码已经撤销过了
					//获得冻结标信息
					JSONObject A1Json = efOrderService.findFreezenProduct();
					Double allMoney = efOrderService.findAllMoney(efOrdersId);
					bocJson.clear();
					bocResult.clear();
					bocJson.put("accountId", bci.getBankAccount());
					bocJson.put("productId",A1Json.get("product"));
					bocJson.put("txAmount",allMoney);
					//查找对应的投标交易流水号
					String auto_serino = efPaycontrolService.findAutoSerino(eo.getCustInfoId());
					bocJson.put("contOrderId", auto_serino);
					//查找期数
					bocJson.put("acqRes", "");
					bocJson.put("signature", SignatureUtil.createSign());
					bocJson.put("sendAppName",StaticData.appName);//
					bocJson.put("remark","");//保留域
					bocResult =  WebServiceUtil.sendPost(StaticData.bocUrl+"payProcess/bocIdAutoApply",new Object[]{bocJson});
					if("0".equals(bocResult.getString("responseCode"))){
						logger.warn("定投Boc重新投资订单时失败!线下理财id:"+efOrdersId+bocResult.toString());
						JSONObject smsJson = new JSONObject();
						smsJson.put("project_number", eo.getOrderNumber());
						smsJson.put("text","定投Boc重新投资订单时失败!线下理财id:"+efOrdersId+efOrdersId+bocResult.toString());
						SmsUtil.senErrorMsg(smsJson);
					}
					if("1".equals(bocResult.getString("responseCode"))){
						logger.warn("定投Boc重新投资订单时成功!线下理财id:"+efOrdersId+bocResult.toString());
						//开始修改理财订单表
						if(bocResult.containsKey("response_map")){
							efOrderService.updateAuthCode(efOrdersId,bocResult.getJSONObject("response_map").getString("AUTHCODE"));
						}else{
							efOrderService.updateAuthCode(efOrdersId,bocResult.getString("AUTHCODE"));
						}
					}
				}
			}
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			response.getWriter().print("<script>alert('Mission failure!')</script>");
			return;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
	}
	
	@RequestMapping("fixJie")
	public void fixJie(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String info="处理成功";
		response.setContentType("text/html;charset=utf-8");
		JSONObject bocJson = new JSONObject();
		String remark=request.getParameter("remark");
		String accountId=request.getParameter("accountId");
		String txAmount=request.getParameter("txAmount");
		String txFeeOut=request.getParameter("txFeeOut");
		String productId=request.getParameter("productId");
		String authCode=request.getParameter("authCode");
		try{
			JSONObject cuJson = new JSONObject();
			cuJson.put("orderId", "");
			cuJson.put("accountId",accountId);			//扣款账户
			cuJson.put("txAmount", txAmount);	//交易金额
			cuJson.put("intAmount", 0d);										//交易利息
			cuJson.put("txFeeOut", txFeeOut) ;
			cuJson.put("txFeeIn", "0");											//收款手续费
			cuJson.put("forAccountId", StaticData.risk);						//入款账号
			cuJson.put("productId", productId);	//产品号
			cuJson.put("authCode", authCode);
			cuJson.put("trdresv", "auto_fix:"+remark);//第三方保留域
			//查看本期还款是否结束
			JSONArray jsonArray = new JSONArray();
			jsonArray.add(cuJson);
			JSONObject transMap = new JSONObject();
			transMap.put("acqRes","");//第三方保留域
			transMap.put("subPacks", jsonArray.toString());
			transMap.put("signature", SignatureUtil.createSign());
			transMap.put("sendAppName",StaticData.appName);//
			transMap.put("remark","");//保留域
			JSONObject bocResultJson  = WebServiceUtil.sendPost(StaticData.bocUrl+"payProcess/batchRepay", new Object[]{transMap});
			info = bocResultJson.toString();
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			info="处理异常";
		}
		response.getWriter().println(info);
	}
	@RequestMapping("updateOnlineCustInfo")
	public void updateOnlineCustInfo(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String info="处理成功";
		response.setContentType("text/html;charset=utf-8");
		try {
			efOrderService.updateOnlineEfCustInfoIdByBgCustInfo();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			info="处理异常";
		}
		response.getWriter().println(info);
	}
	@RequestMapping("updateLineCustInfo")
	public void updateLineCustInfo(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String info="处理成功";
		response.setContentType("text/html;charset=utf-8");
		try {
			efOrderService.updateLineEfCustInfoIdByBgCustInfo();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			info="处理异常";
		}
		response.getWriter().println(info);
	}
	
	/**
	 * 功能说明：宝付信贷订单
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午9:28:34
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("BFPayFunction")
	public void AutoRunBF(HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRunBF.toBFPayFunction();
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	
	/**
	 * 功能说明：宝付信贷订单(正常订单)
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午9:28:34
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("BFNaturalOrder")
	public void BFNaturalOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRunBF.toBFNaturalOrder();
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	
	/**
	 * 功能说明：信贷订单HF(正常订单)
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午9:28:34
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("HFNaturalOrder")
	public void HFNaturalOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRunBF.toHFNaturalOrder();
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	
	/**
	 * 功能说明：宝付信贷订单(正常订单)
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午9:28:34
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toBFWithholdingFunction")
	public void toBFWithholdingFunction(HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRunBF.toBFNaturalOrder();
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	
	/**
	 * 功能说明：宝付信贷订单(逾期订单)
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午9:28:34
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("BFOverdueOrder")
	public void BFOverdueOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRunBF.toBFOverdueOrder();
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	
	/**
	 * 功能说明：宝付信贷订单(逾期订单)
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午9:28:34
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("HFOverdueOrder")
	public void HFOverdueOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRunBF.toHFOverdueOrder();
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('Mission failure!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('Mission complete!')</script>");
		StaticData.fakeTime="";
		return;
	}
	
	/**
	 * 功能说明：宝付信贷订单
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午9:28:34
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("BFWithholdingFindFunction")
	public void BFWithholdingFindFunction(HttpServletRequest request,HttpServletResponse response,String mobile,String transSerialNo,String transNo, String time) throws Exception{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		StaticData.fakeTime=request.getParameter("time");
		JSONObject json = new JSONObject();
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				String orderid = "";
				json = autoRunBF.bFWithholdingFindFunction(mobile, orderid,transSerialNo, transNo, time);
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('"+json+"!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('"+json+"!')</script>");
		StaticData.fakeTime="";
		return;
	}
	
	/**
	 * 功能说明：宝付信贷订单
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午9:28:34
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("BFPayFindFunction")
	public void BFPayFindFunction(HttpServletRequest request,HttpServletResponse response,String transBatchid,String transNo) throws Exception{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		StaticData.fakeTime=request.getParameter("time");
		JSONObject json = new JSONObject();
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				json = autoRunBF.toBFPayFindFunction(transBatchid, transNo);
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('"+ json +"!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('"+ json +"!')</script>");
		StaticData.fakeTime="";
		return;
	}
	/**
	 * 功能说明：信贷还款明细统计
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午9:28:34
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("crmPaySta")
	public void CrmPaySta(HttpServletRequest request,HttpServletResponse response,String total) throws Exception{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		StaticData.fakeTime=request.getParameter("time");
		JSONObject json = new JSONObject();
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				if ("total".equals(total)) {
					autoRunCrmPaycontrolStatistics.toExecuteCrmPaycontrolStatisticsTotal();
				}else{
					autoRunCrmPaycontrolStatistics.toExecuteCrmPaycontrolStatisticsAdd();
				}
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('fail!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('success!')</script>");
		StaticData.fakeTime="";
		return;
	}
	
	/**
	 * 功能说明：赎楼贷直投订单
	 * @param 
	 * @return
	 * @throws  
	 * 创建人 wbk
	 * 创建日期：上午9:28:34
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("RansomFloorTask")
	public void RansomFloorTask(HttpServletRequest request,HttpServletResponse response,String mobile,String transSerialNo,String transNo, String time) throws Exception{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		StaticData.fakeTime=request.getParameter("time");
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				ransomFloorTaskNew.ransomFloorEfPaycontrol();
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
				StaticData.pass=false;
				StaticData.fakeTime="";
				response.getWriter().print("<script>alert('fail!')</script>");
				return;
			}
			StaticData.pass = true;
		}
		response.getWriter().print("<script>alert('success!')</script>");
		StaticData.fakeTime="";
		return;
	}
}
