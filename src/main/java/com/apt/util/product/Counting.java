package com.apt.util.product;

import java.text.DecimalFormat;
import com.apt.util.ChkUtil;
import com.apt.util.arith.ArithUtil;

/**
 * 
 * 功能说明：产品计算
 * 典型用法：
 * 特殊用法：	
 * @author 燕娜
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-9-24
 * Copyright zzl-apt
 */
public class Counting {
	
	/**
	 * 
	 * 功能说明：计算公司对应的前期服务费率 公式= 公司占比*P(前期服务费);			
	 * yann  2015-9-24
	 * @param serviceFeeRateP:当前对象占比 ,beforServiceFeeRateP：前期服务费 (为Text)
	 * @return    成功直接返回结果值，失败返回0
	 * @throws  
	 * 最后修改时间：最后修改时间
	 * 修改人：admin
	 * 修改内容：
	 * 修改注意点：
	 */
	public String beforServiceFeeRate(Double serviceFeeRateP,Double beforServiceFeeRateP){
		
		try {
			//计算结果
			Double result = ArithUtil.mul(serviceFeeRateP, ArithUtil.div(beforServiceFeeRateP, 100, 8));
			
			//将结果保留4位小数
			String decimalFormat = new DecimalFormat("#.####").format(result);
			
			//将结果返回
			return decimalFormat;
			
		} catch (Exception e) {
			e.printStackTrace();
			//有异常直接返回0
			return "0";
		}
	}
	
	/**
	 * 
	 * 功能说明： 计算前期服务费率
	 * weiyingni  2015-9-24
	 * @param 综合利率 G multipleRate
	 * @param 贷款期限 N period
	 * @param 合同月利率 F rate
	 * @param 保证金费率 V bailRate
	 * @param 月分期服务费 C stagingServicesRate
	 * @return   
	 * @throws  
	 * 最后修改时间：最后修改时间
	 * 修改人：admin
	 * 修改内容：
	 * 修改注意点：
	 */
	public Double beforServiceFeeRate(Double multipleRate,Integer period, Double rate, Double bailRate, Double stagingServicesRate){
		
		try{
				// 获取值，并判断不为空
				if(ChkUtil.isEmpty(multipleRate))multipleRate = 0.0;
				multipleRate = ArithUtil.div(multipleRate, 100, 10);
				
				if(ChkUtil.isEmpty(period))period = 0;
				
				if(ChkUtil.isEmpty(rate))rate = 0.0;
				rate = ArithUtil.div(rate, 100, 10);
				
				if(ChkUtil.isEmpty(bailRate))bailRate = 0.0;
				bailRate = ArithUtil.div(bailRate, 100, 10);
				
				if(ChkUtil.isEmpty(stagingServicesRate))stagingServicesRate = 0.0;
				stagingServicesRate =  ArithUtil.div(stagingServicesRate, 100, 10);
				
				// a1 = (G*N+1)
				Double a1 =  ArithUtil.add(ArithUtil.mul(multipleRate, period), 1);
				
				// b1 = (1+F)_N次
				Double b1 = Math.pow(ArithUtil.add(1, rate), period);
				
				// a2 = [(1+F)_N次-1]
				Double a2 = ArithUtil.sub(b1, 1);
				
				// a3 = (1-v)
				Double a3 = ArithUtil.sub(1, bailRate);
				
				// r1 = a*b*c
				Double r1 =  ArithUtil.mul(ArithUtil.mul(a1, a2), a3);
				
				// b2 = N*F*b1;
				Double b2 = ArithUtil.mul(ArithUtil.mul(period, rate), b1);
				
				// b3 = C*N*a2;
				Double b3 = ArithUtil.mul(ArithUtil.mul(stagingServicesRate, period), a2);
				
				// r2 = b2+b3;
				Double r2 = ArithUtil.add(b2, b3);
				
				// r3 = r1/r2;
				Double r3 = ArithUtil.div(r1, r2, 10);
				
				// r4 = r3 - 1;
				Double r4 = ArithUtil.sub(r3, 1);
				
				// result = r4 * N;
				Double result = ArithUtil.mul(r4, period);
				if(result < 0){
					result = 0.0;
				}
				return result;
		} catch (Exception e) {
			e.printStackTrace();
			//有异常直接返回0
			return 0.0;
		}
	}
	
	/**
	 * 
	 * 功能说明：分期服务费在合同金额中的占比:100*（分期服务费率/100）*期数	
	 * 简化为：分期服务费率 * 期数		
	 * weiyingni  2015-9-28
	 * @param ssRate : 分期服务费费率
	 * @param period 期数
	 * @return   
	 * @throws  
	 * 最后修改时间：最后修改时间
	 * 修改人：admin
	 * 修改内容：
	 * 修改注意点：
	 */
	public Double stagingProportion(Double ssRate, Integer period){
		
		try{
			Double result = ArithUtil.mul(ssRate, period);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
			return 0.0;
		}
	}
	
	/**
	 * 
	 * 功能说明：前期服务费在合同金额中的占比:100*（前期服务费率/100）*期数	
	 * 简化为：前期服务费率 * 期数
	 * weiyingni  2015-9-28
	 * @param bsfeeRate 前期服务费率
	 * @param period 期数
	 * @return   
	 * @throws  
	 * 最后修改时间：最后修改时间
	 * 修改人：admin
	 * 修改内容：
	 * 修改注意点：
	 */
	public Double beforProportion(Double bsfeeRate, Integer period){
		
		try{
			Double result = ArithUtil.mul(bsfeeRate, period);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
			return 0.0;
		}
	}
	
	/**
	 * 
	 * 功能说明：总服务费在合同金额中的占比:分期服务费在合同金额中的占比+前期服务费在合同金额中的占比
	 * weiyingni  2015-9-28
	 * @param stagingProportion：总服务费占比
	 * @param beforProportion 前期占比
	 * @return   
	 * @throws  
	 * 最后修改时间：最后修改时间
	 * 修改人：admin
	 * 修改内容：
	 * 修改注意点：
	 */
	public Double totalFeeProportion(Double stagingProportion, Double beforProportion){
		
		try{
			Double result = ArithUtil.add(stagingProportion, beforProportion);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
			return 0.0;
		}
	}
	
	/**
	 * 
	 * 功能说明：A公司拨备金在合同金额中占比：
	 * 100*（前期服务费在合同金额中的占比/100）*（A公司在前期服务费中的占比/100）*（公司拨备金在A公司服务费中占比/100）	
	 * yann  2015-9-28
	 * @param beforProportion：前期服务费在合同金额中的占比
	 * @param bsfeeRate A公司在前期服务费中的占比
	 * @param rfRate 
	 * @return   
	 * @throws  
	 * 最后修改时间：最后修改时间
	 * 修改人：admin
	 * 修改内容：
	 * 修改注意点：
	 */
	public Double riskProportion(Double beforProportion, Double bsfeeRate, Double rfRate){
		
		try{
			beforProportion = ArithUtil.div(beforProportion, 100, 10);
			bsfeeRate = ArithUtil.div(bsfeeRate, 100, 10);
			rfRate = ArithUtil.div(rfRate, 100, 10);
			Double result = ArithUtil.mul(100, beforProportion);
			result = ArithUtil.mul(result, bsfeeRate);
			result = ArithUtil.mul(result, rfRate);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
			return 0.0;
		}
	}
	
	/**
	 * 
	 * 功能说明：TODO 公式修改   
	 * 前期服务费在合同金额中的占比  *（A公司在前期服务费中的占比/100）*（A公司拨备金在A公司服务费中占比/100）/总服务费在合同金额中的占比*100
	 * 简化为：A公司拨备金在合同金额中占比 /总服务费在合同金额中的占比*100
	 * yann  2015-9-24
	 * @param serviceFee : A公司拨备金在合同金额中占比,serviceFeea : 总服务费在合同金额中的占比
	 * @return   成功直接返回结果值，失败返回0
	 * @throws  
	 * 最后修改时间：最后修改时间
	 * 修改人：admin
	 * 修改内容：
	 * 修改注意点：
	 */	
	public String serviceFee(Double riskProportion, Double totalFeeProportion){
		try {
			
			//计算结果
			Double result = ArithUtil.div(riskProportion, totalFeeProportion, 8);
			result = ArithUtil.mul(result, 100);
			
			//将结果保留4位小数
			String decimalFormat = new DecimalFormat("#.####").format(result);
			
			//将结果返回
			return decimalFormat;
			
		} catch (Exception e) {
			e.printStackTrace();
			//有异常直接返回0
			return "0";
		}
	}

	/**
	 * 
	 * 功能说明：判断是否是数字包括小数点			
	 * yann  2015-9-24
	 * @param str：字符串
	 * @return   
	 * @throws  
	 * 最后修改时间：最后修改时间
	 * 修改人：admin
	 * 修改内容：
	 * 修改注意点：
	 */
	public static boolean isNumeric(String str){
		int index = 0;
		//循环字符串判断每个字是否是数字
		for (int i = 0; i < str.length(); i++){
			if(str.charAt(i) == '.'){
				index += 1;
			}
			//如果有一个不是数字直接返回
			if (!Character.isDigit(str.charAt(i))  && str.charAt(i) != '.'){
				return false;
			}
			//如果有一个以上的小数点直接返回
			if(index > 1){
				return false;
			}
		}
	  return true;
	}
	/**
	 * 风险拨备金  在合同金额中占比
	X：即我们目标，所需求前期一次性扣除的服务费
	D：授信额度，即客户到手本金，审批时输入，已知值
	A:合同本金:合同本金=前期扣除的一次性费用+授信额度；注意：前期扣除的一次性费用可能包含前期服务费及其他费用，比如保证金。
	V：保证金费率，则保证金金额=VA；如果保证金属于服务费的一种即不退回，则A=X+D+VA； 如果该保证金需要退回的，则A=X+D+VA，但是要把V=0处理计算。所以我们就可以当做A=X+D+VA永远成立，区别就是保证金退回时V=0，如果不退回，则带入实际数值计算，并且不退回时（X+VA）才是我们真正的服务费，保证金VA只是做了一下避税处理而已。
	N：贷款期限，已知值
	F：合同月利率，已知值
	G：综合利率，已知值
	C:月分期服务费率，已知值（人工输入）
	 */
	public static double getContractAmountAccounted(double d,double v,double n,double f,double g,double c){
//		double result = (g*n+1)*d*(Math.pow((1+f),n)-1)*(1-v)/(n*f*Math.pow((1+f),n)+c*n*(Math.pow(1+f, n)-1))-d;
		double result = (g*n+1)*(Math.pow((1+f),n)-1)*(1-v)/(n*f*Math.pow((1+f),n)+c*n*(Math.pow(1+f, n)))-1+c;
		return result;
	}
	public static void main(String[] args) {
		// 授信额度 =20000   保证金利率=1%/一次性   期数=12  合同月利率 0.8%/月   综合利率1%/月  ，分期服务费率 0.1%   
		double result = Counting.getContractAmountAccounted(20000,0.01, 12, 0.008, 0.01, 0.001);
		
		System.out.println(result);
	}
}
