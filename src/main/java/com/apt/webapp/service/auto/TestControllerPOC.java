package com.apt.webapp.service.auto;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.apt.util.StaticData;


@Controller
@RequestMapping("/autoPoc/")
public class TestControllerPOC {
	//日志
	private static Logger logger = LoggerFactory.getLogger(TestControllerPOC.class);
	@Resource
	private AutoRunPOC autoRunPoc;
	/**
	 * 功能说明：Poc理财自动还款--批量划拨
	 * chengwei 2016年11月8日 17:24:12
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteEfAutopayPocBatchUpload")
	public void toExecuteEfAutopayPocBatchUpload(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========Poc理财自动还款（批量划拨）开始======");
		StaticData.fakeTime=request.getParameter("time");
		StaticData.pass=true;
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoRunPoc.EfAutopayPocBatchUpload();
				logger.warn("==========Poc理财自动还款（批量划拨）结束======");
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
	 * 功能说明：Poc理财自动还款--批量更新数据
	 * chengwei 2016年11月8日 17:24:17
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toExecuteEfAutopayPocBatchDownload")
	public void toExecuteEfAutopayPocBatchDownload(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========Poc理财自动还款（下载批量划拨文件、更新数据）开始======");
		StaticData.fakeTime=request.getParameter("time");
		StaticData.pass=true;
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoRunPoc.EfAutopayPocBatchDownload();
				logger.warn("==========Poc理财自动还款（下载批量划拨文件、更新数据）结束======");
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
	 * 功能说明：Poc理财自动还款--批量预授权
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
/*	@RequestMapping("toExecuteImmeEfAutopayPocBatchPreAuth")
	public void toExecuteImmeEfAutopayPocBatchPreAuth(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========直投Poc理财自动还款（批量预授权）开始======");
		StaticData.fakeTime=request.getParameter("time");
		StaticData.pass=true;
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoRunPoc.EfAutopayPocBatchPreAuth();
				logger.warn("==========直投Poc理财自动还款（批量预授权）结束======");
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
	}*/
	/**
	 * 功能说明：Poc理财自动还款--批量划拨
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
/*	@RequestMapping("toExecuteImmeEfAutopayPocBatchTransfer")
	public void toExecuteImmeEfAutopayPocBatchTransfer(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========Poc理财自动还款（批量划拨）开始======");
		StaticData.fakeTime=request.getParameter("time");
		String uploadTime=request.getParameter("uploadTime");
		String total=request.getParameter("total");
		StaticData.pass=true;
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoRunPoc.EfAutopayPocBatchTransfer(uploadTime,total);
				logger.warn("==========Poc理财自动还款（批量划拨）结束======");
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
	}*/
	/**
	 * 功能说明：Poc理财自动还款--批量更新数据
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
/*	@RequestMapping("toExecuteImmeEfAutopayPocBatchUpdate")
	public void toExecuteImmeEfAutopayPocBatchUpdate(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========Poc理财自动还款（批量更新数据）开始======");
		StaticData.fakeTime=request.getParameter("time");
		String uploadTime=request.getParameter("uploadTime");
		String total=request.getParameter("total");
		StaticData.pass=true;
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoRunPoc.EfAutopayPocBatchUpdate(uploadTime,total);
				logger.warn("==========Poc理财自动还款（批量更新数据）结束======");
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
	}*/
	/**
	 * 功能说明：POC理财自动还款---批量冻结
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
/*	@RequestMapping("toExecuteDeteEfAutopayPocBatchFreeze")
	public void toExecuteDeteEfAutopayPocBatchFreeze(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========POC理财自动还款（批量冻结）开始======");
		StaticData.fakeTime=request.getParameter("time");
		String uploadTime=request.getParameter("uploadTime");
		String total=request.getParameter("total");
		StaticData.pass=true;
		if(StaticData.pass){
			StaticData.pass=false;
			try{
				autoRunPoc.EfAutopayPocBatchFreeze(uploadTime,total);
				logger.warn("==========POC理财自动还款（批量冻结）结束======");
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
	}*/
	
	/**
	 * 功能说明：Poc信贷自动还款
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
		logger.warn("==========直投Poc信贷自动还款（发送list到POC）开始======");
		StaticData.fakeTime=request.getParameter("time");
		StaticData.pass=true;
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRunPoc.crmAutopayPoc();
				logger.warn("==========直投Poc信贷自动还款（发送list到POC）结束======");
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
	 * 功能说明：Poc信贷自动还款update
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toImmeCrmAutopayPocUpdate")
	public void toImmeCrmAutopayPocUpdate(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========直投Poc信贷自动还款开始======");
		StaticData.fakeTime=request.getParameter("time");
		String uploadTime=request.getParameter("uploadTime");
		String total=request.getParameter("total");
		StaticData.pass=true;
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRunPoc.crmAutopayPocUpdate(uploadTime,total);
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
	 * 功能说明：POC信贷逾期还款
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
		logger.warn("==========直投POC信贷逾期还款（发送list到POC）开始======");
		StaticData.fakeTime=request.getParameter("time");
		StaticData.pass = true;
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRunPoc.overCrmAutopayPoc();
				logger.warn("==========直投POC信贷逾期还款（发送list到POC）结束======");
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
	 * 功能说明：POC信贷逾期还款update
	 * panye  2015-6-10
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	@RequestMapping("toOverImmeCrmAutopayPocUpdate")
	public void toOverImmeCrmAutopayPocUpdate(HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.warn("==========本次请求ip:"+request.getRemoteAddr()+"======");
		logger.warn("==========直投POC信贷逾期还款（从POC下载list并更新数据）开始======");
		StaticData.fakeTime=request.getParameter("time");
		String uploadTime=request.getParameter("uploadTime");
		String total=request.getParameter("total");
		StaticData.pass=true;
		if(StaticData.pass){
			StaticData.pass = false;
			try{
				autoRunPoc.overCrmAutopayPocUpdate(uploadTime,total);
				logger.warn("==========直投POC信贷逾期还款（从POC下载list并更新数据）结束======");
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
	public static void main(String[] args) {
	}
	public static String formatDouble(double num){
		DecimalFormat  df=new DecimalFormat("0.00");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(num);
	}
}
