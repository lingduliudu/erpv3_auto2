package com.apt.util.ip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;


/**
 * 
 * 功能说明：根据IP找到对象的城市及省份
 * 典型用法：
 * 特殊用法：
 * @author panye
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-5-13
 * Copyright zzl-apt
 */
public class SinaGetPlaceByIp {
	
	public static String [] province={
	                             "china",
	                             // 23个省
	                             "广东", "青海", "四川", "海南", "陕西", 
	                             "甘肃", "云南", "湖南", "湖北", "黑龙江",
	                             "贵州", "山东", "江西", "河南", "河北",
	                             "山西", "安徽", "福建", "浙江", "江苏", 
	                             "吉林", "辽宁", "台湾",
	                             // 5个自治区
	                             "新疆", "广西", "宁夏", "内蒙古", "西藏", 
	                             // 4个直辖市
	                             "北京", "天津", "上海", "重庆",
	                             // 2个特别行政区
	                             "香港", "澳门"
	};
	public static String [] city={
	
	};

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	  InputStream is = new URL(url).openStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = JSONObject.fromObject(jsonText);
      is.close();
      return json;
  }

  
  /**
   * 		{province=江苏, district=, city=苏州}
   * @param IP
   * @return
   * @throws Exception
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static Map getAddressDetail(String IP) throws Exception{
	  Map m=new HashMap();
	  JSONObject json = readJsonFromUrl("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip="+IP);
	  String str=json.getString("ret").toString();
	  if(str.equals("1")){
		 m.put("province", json.get("province"));
		 m.put("city", json.get("city"));
		 m.put("district", json.get("district"));
		}
	  return m;
  }
  
  public static void main(String[] args) throws Exception{
	  //这里调用百度的ip定位api服务 详见 http://api.map.baidu.com/lbsapi/cloud/ip-location-api.htm
//    JSONObject json = readJsonFromUrl("http://api.map.baidu.com/location/ip?ak=Gs0nxnVirwLLZPqrLHHEiZ1u&ip=202.198.16.3");
//    System.out.println(json.toString());
//    JSONObject obj=(JSONObject) ((JSONObject) json.get("content")).get("address_detail");
//    System.out.println(obj.get("province"));
//    System.out.println(obj.get("city"));
	  Map m=new HashMap();
	  m=getAddressDetail("49.72.211.56");
	  System.out.println(m.toString());
  }
}

