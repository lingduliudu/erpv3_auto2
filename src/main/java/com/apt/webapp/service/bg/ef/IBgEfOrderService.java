package com.apt.webapp.service.bg.ef;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import net.sf.json.JSONObject;

import com.apt.webapp.model.bg.ef.BgEfOrders;
import com.apt.webapp.model.ef.EfFundRecord;

/**
 * 功能说明：成功客户等级service层
 * 创建人：袁浩
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * @author admin
 * 创建时间：2015-10-26
 *
 */
@WebService
public interface IBgEfOrderService {
	/**
	 * 功能说明：执行sql
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
	public void executeSql(String sql);
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
	public List<Map> getCurrentControl(Map efMap);
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
	public JSONObject updateAgainSuccessPocBatchRecord(String fileName,String unique_no);
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
	public List<Map> getCurrentImmeControlsBoc();
	public String getBgAutoTransferAuth();
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
	public List<Map> getCurrentImmeControlsBocRedAccount();
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
	public List<Map> getCurrentDeteControlsBoc();
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
	public List<Map> getCurrentDeteControlsBocRedAccount();
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
	public List<Map> getCurrentControlsPoc();
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
	public List<Map> getCurrentControlsHF();
	public void executeBatchUpdateOperateType();
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
	public List<Map> getCurrentImmeControlsPoc();
	public List<Map> getCurrentImmeControlsPocById(String efPaycontrolId);
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
	public List<Map> getCurrentDeteControlsPoc();
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
	public List<Map> getControls(String efOrderId);
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
	 */
	public void normalRepay(Map efPaycontrol);
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
	public void normalRepayByModel(Map efPaycontrol);
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
	public boolean isLast(Map efPaycontrol);
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
	public Map getRefereeInfoMap(String string);
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
	public List<Map> getCurrentOnLineTasteOrderControls();
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
	public Map getBgEfOrdersById(String efOrderId);
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
	public Map getBgAutoTransferAuth(String custInfoId);
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
	public JSONObject cleanInterest(Map efPaycontrol);
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
	public JSONObject cleanEfPaycontrol(Map efPaycontrol);
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
	public List<Map> findPocFileRecord(String type);
	/**
	 * 功能说明：查找poc长时间不处理的数据
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
	public List<Map> findToLaterDataToSms();
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
	public Object findById(Class className, String id);
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
	public JSONObject updateFailPocRecord(String uniqueNo, String id,String type,String failRemark);
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
	public JSONObject updateSuccessPocRecord(String preFileName, String fileName,String process);
	/**
	 * 功能说明：预授权文件直接成功
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
	public JSONObject updateSuccessPocBatchRecord(String fileName);
	/**
	 * 功能说明：更新数据告知拒绝文件名的更新
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
	public JSONObject updateRejectFileNamePocRecord(String preFileName, String newfileName,String process);
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
	public Map getLastBatchFileInfo(String keyId, String type);
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
	public Map getEfFundRecordInfo(String keyId, String type);
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
	public void save(Object obj);
	/**
	 * 功能说明：更新数据
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
	public void update(Object obj);
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
	public List<Map> findUploadFileFailRecords();
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
	public List<Map> findFailDataByUniqueno(String uniqueNo);
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
	public List<Map> BocBgEforderFinish();
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
	public boolean isOverAuthcode(String authCode);
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
	public void updateAuthCodeBgEforders(String authCode);
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
	public void savePaytransferrecord(List<JSONObject> list, String type);
	/**
	 * 功能说明：结清明细
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
	public void clearBepc(String order_id,String investment_model);
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
	public List<Map> findLineEfPaycontrolHasPrincipal(String efOrderId);
}
