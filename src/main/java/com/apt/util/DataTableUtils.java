package com.apt.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DataTableUtils {
	
	/**
	 * 把sql 查询出来的数据，转成Map
	 * 功能说明：该方法实现的功能			
	 * @author sky
	 * @param 方法里面接收的参数及其含义
	 * @return 该方法的返回值的类型，含义   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：最后修改时间
	 * date: 2015-10-30
	 * 修改内容：
	 * 修改注意点：
	 */
	public static List formart(List datas,String[] culumns){
		List returnMap = new ArrayList();
		// 遍历 集合
		for (int i = 0; i < datas.size(); i++) {
			List list = JSONArray.fromObject(datas.get(i)); // 获取对象
			Map map = new HashMap();
			for (int j = 0; j < list.size(); j++) {
				if(culumns.length > j){
					map.put(culumns[j], list.get(j));
				}else{
					map.put("culumns"+j, list.get(j));
				}
			}
			returnMap.add(map);
		}
		return returnMap;
	}
	
	
	
	
	public static void main(String[] args) {
		List list = new ArrayList();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		
		List arrList = new ArrayList();
		arrList.add(list);
		arrList.add(list);
		String [] arr = new String[]{"id","name","dept","company"};
		
		DataTableUtils dtu  = new DataTableUtils();
		System.out.println(JSONArray.fromObject(dtu.formart(arrList, arr)).toString());;
	}
}
