package com.apt.webapp.base.dao;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.apt.util.date.DateUtil;

/**
 * 
 * @Title: 第二数据源
 * @Description
 * @throws
 */
@Repository
public class SecondHibernateDaoSupper implements ISecondHibernateDaoSupper{
	@Resource(name="dataSourcePaycenter")
	private com.alibaba.druid.pool.DruidDataSource dataSource;
	/**
	 * 
	 * @Title: 
	 * @Description: 针对第二个数据源进行批量保存
	 * @param @param List<String> macSource数据
	 * @param @throws Exception    设定文件 
	 * @return     JSONObject 
	 * @throws
	 */
	public  void paycenterSave(List<JSONObject> data, String type){
		String uuid = UUID.randomUUID().toString();
		try{
			for(JSONObject json:data){
				String macSource = json.getString("macSource");
				String remark = json.getString("remark");
				String serialNumber = UUID.randomUUID().toString();
				String platform = "";
				if(json.containsKey("serialNumber")){
					serialNumber = json.getString("serialNumber");
				}
				if(json.containsKey("platform")){
					platform = json.getString("platform");
				}
				new JdbcTemplate(dataSource).update("insert into pay_transfer_record(id,mac_source,status,batch_seri_no,create_time,remark,type,serial_number,platform) values (uuid(),'"+macSource+"','1','"+uuid+"','"+DateUtil.getCurrentTime()+"','"+remark+"','"+type+"','"+serialNumber+"','"+platform+"');");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
