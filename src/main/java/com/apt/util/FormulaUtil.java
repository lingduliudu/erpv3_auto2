package com.apt.util;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * 
 * 功能说明:v3 公式工具类
 * 典型用法：
 * 特殊用法：
 * @author yuanhao
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-6-10
 * Copyright zzl-apt
 */
public class FormulaUtil {
	
	
	
	/**
	 * 
	 * 功能说明：获得需要的计算值	
	 * panye  2015-6-10
	 * @param Map 参数集合
	 * @return   
	 * @throws  
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static JSONObject getFormulaValue(Map formulaKey){
		//返回结果
		JSONObject resultJson = getRealKey(formulaKey);
		//以下参数默认在keyMap中已经存在
		/*
		 R : 投资年化利率
	     P : 投资锁定期
		 C : 筹建期实得费率
		 M : 投资期客户管理月费率
		 I : 产品实得收益
		 X：即我们目标，所需求前期一次性扣除的服务费
		 D：授信额度，即客户到手本金，审批时输入，已知值
		 A:合同本金:合同本金=前期扣除的一次性费用+授信额度；注意：前期扣除的一次性费用可能包含前期服务费及其他费用，比如保证金。
		 V：保证金费率，则保证金金额=VA；如果保证金属于服务费的一种即不退回，则A=X+D+VA； 如果该保证金需要退回的，则A=X+D+VA，但是要把V=0处理计算。所以我们就可以当做A=X+D+VA永远成立，区别就是保证金退回时V=0，如果不退回，则带入实际数值计算，并且不退回时（X+VA）才是我们真正的服务费，保证金VA只是做了一下避税处理而已。
		 N：贷款期限，已知值
		 F：合同月利率，已知值
		 G：综合利率，已知值
		 C:月分期服务费率，已知值（人工输入）

		 * */
		//double P = 0;
		//double M = 0;
		double C =resultJson.getDouble("C");//1.建期实得费率=筹建期利率-筹建期管理费费率
		//double R = 0.0;//2. 投资年化利率 = 央行基准*（1+投资基准浮动值）
		//double I = 0.0;//3. 产品实得收益 =投资年化利率/12*投资锁定期+筹建期实得费率-投资期客户管理月费率*投资锁定期  
		//I= R/12*P+C-M*P;
		//前期服务费率
		double G = resultJson.getDouble("G");
		double N = resultJson.getDouble("N");
		double V = resultJson.getDouble("V");
		double F = resultJson.getDouble("F");
		double p = 0.0; //前期服务费费率：
		double A = 0.0;
		double D = 0.0;
		//临时变量
		double temp1 = 0.0;
	    double temp2 = 0.0;
	    	   temp1 = (G*N+1)*(Math.pow(1+F, N)-1)*(1-V);
	    	   temp2 = N*F*(Math.pow(1+F,N))+C*N*(Math.pow(1+F, N)-1);
	    double temp3 = (((G*N+1)*(Math.pow(1+F, N)-1)*(1-V))/(N*F*(Math.pow(1+F,N))+C*N*(Math.pow(1+F, N)-1))-1)*N;
	    	   p = (temp1/temp2-1)*N;
	    resultJson.put("p", p);
	   //如果知道的是合同本金则需要求的是合同的授信额度
	    if(!ChkUtil.isEmpty(resultJson.get("A"))){ 
	    	   A = resultJson.getDouble("A");
	    	   temp1 = (N*A*F*(Math.pow(1+F,N)))/(Math.pow(1+F, N)-1)+C*N*A;
	    	   temp2 = (G*N+1);
	    	   temp3 = ((N*A*F*(Math.pow(1+F,N)))/(Math.pow(1+F, N)-1)+C*N*A)/((G*N+1));
	    	   D = temp1/temp2;
	    	   resultJson.put("D", D);
	    }
	    //如果知道的是授信额度则需要求的是合同本金
	    if(!ChkUtil.isEmpty(resultJson.get("D"))){
	    	D = resultJson.getDouble("D");
	    	//前期一次性扣除的服务费
			double X = 0.0;
			  	   temp1 = (G*N+1)*D*(Math.pow(1+F, N)-1)*(1-V);
			  	   temp2 = N*F*(Math.pow(1+F,N))+C*N*(Math.pow(1+F,N)-1);
			  	   temp3 = ((G*N+1)*D*(Math.pow(1+F, N)-1)*(1-V))/(N*F*(Math.pow(1+F,N))+C*N*(Math.pow(1+F,N)-1))-D;
			  	   X = temp1/temp2-D;
			  	   resultJson.put("X", X);
			  	   A = (X+D)/(1-V);
			  	   resultJson.put("A", A);
	    }
		return resultJson;
		
	}
	/**
	 * 
	 * 功能说明：转换数字使用	
	 * panye  2015-6-10
	 * @param Map 参数集合
	 * @return   
	 * @throws  
	 * 最后修改时间：
	 * 修改人：yuanhao
	 * 修改内容：
	 * 修改注意点：
	 */
	public static JSONObject getRealKey(Map formulaKey){
		//返回结果
		JSONObject resultJson = new JSONObject();
		resultJson.put("F",Double.parseDouble(formulaKey.get("contractRate").toString())/100); //合同月利率
		resultJson.put("G", Double.parseDouble(formulaKey.get("multipleRate").toString())/100); //综合利率
		resultJson.put("N", formulaKey.get("periods")); //借款期限
		resultJson.put("C",Double.parseDouble(formulaKey.get("stagingServicesRate").toString())/100); //月分期服务费率
		resultJson.put("V",Double.parseDouble(formulaKey.get("bailRate").toString())/ 100); //保证金率
		resultJson.put("A", formulaKey.get("A")); //合同金额
		resultJson.put("D", formulaKey.get("D")); //授信额度
		return resultJson;
		
	}
	public static void main(String[] args) {
		Map formulaKey = new HashMap();
		formulaKey.put("contractRate","0.8"); // 合同月利率
		formulaKey.put("multipleRate","1"); // 综合利率
		formulaKey.put("periods","12"); // 借款期限
		formulaKey.put("stagingServicesRate","0"); // 月分期服务费率
		formulaKey.put("bailRate","1"); // 保证金率
		formulaKey.put("A","20000");// 合同额度 
		//formulaKey.put("D","20000"); // 授信额度 
		System.out.println(getFormulaValue(formulaKey));
	}
	
}
