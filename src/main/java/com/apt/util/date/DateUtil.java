package com.apt.util.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.apt.util.ChkUtil;
import com.apt.util.NumberFormat;
import com.apt.util.StaticData;
import com.apt.util.arith.ArithUtil;

/**
 * 
 * 功能说明：日期时间类
 * @author panye
 * 修改人: 
 * 修改原因：
 * 修改时间：
 * 修改内容：
 * 创建日期：2015-5-12
 * Copyright zzl-apt
 */
public class DateUtil {
	
	/** yyyy-MM-dd HH:mm:ss */
	public final static String STYLE_1 = "yyyy-MM-dd HH:mm:ss";
	/** yyyy-MM-dd */
	public final static String STYLE_2 = "yyyy-MM-dd";
	/** yyyyMMdd */
	public final static String STYLE_3 = "yyyyMMdd";
	/** yyyyMMddhh */
	public final static String STYLE_4 = "yyyyMMddhh";
	/** yyyyMMddhhmm */
	public final static String STYLE_5 = "yyyyMMddhhmm";
	/** yyyy年MM月dd日HH时mm分ss秒 */
	public final static String STYLE_6 = "yyyy年MM月dd日HH时mm分ss秒";
	/** yyyy年MM月dd日HH时mm分  */
	public final static String STYLE_7 = "yyyy年MM月dd日HH时mm分";
	/** yyyy年MM月dd日  */
	public final static String STYLE_8 = "yyyy年MM月dd日";
	/** hhmmss  */
	public final static String STYLE_9 = "hhmmss";
	
	public final static String STYLE_10 = "yyyyMMddhhmmss";
	
	//client版本是没有 下面5个 并且 clent的11 和 10是一样的的 将clent的10改成现在的11
	public final static String STYLE_11 = "HH:mm:ss";
	
	public final static String STYLE_12 = "yyyy";  
	
	public final static String STYLE_13 = "yyyy-MM";
	
	public final static String STYLE_14 = "dd";
	
	public final static String STYLE_15 = "ddMMyyyyhhmmssSSS";
	/**
	 * 
	 * 功能说明：获得当前时间			
	 * panye  2014-11-29
	 * @param style 时间类型		如果style 则默认返回yyyy-MM-dd HH:mm:ss
	 * @return String 时间字符串   
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String getCurrentTime(String style) {
		if(StringUtils.isEmpty(style)){
			style = STYLE_1;
		}
		if(style.equals(STYLE_3)){
			SimpleDateFormat sdf = new SimpleDateFormat(style);
			String str = sdf.format(new Date());
			return str;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(style);
		String str = sdf.format(new Date());
		if(!"".equals(StaticData.fakeTime)&&StaticData.fakeTime!=null){
			return StaticData.fakeTime.substring(0,10);
		}
		return str;
	}
	
	/**
	 * 
	 * 功能说明：获得当前时间			
	 * panye  2014-11-29
	 * @param style 时间类型		如果style 则默认返回yyyy-MM-dd HH:mm:ss
	 * @return String 时间字符串   
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat(STYLE_1);
		String str = sdf.format(new Date());
		if(!"".equals(StaticData.fakeTime)&&StaticData.fakeTime!=null){
			return StaticData.fakeTime;
		}
		return str;
	}

	
	/**
	 * 
	 * 功能说明：获得指定时间的样式			
	 * panye  2014-11-29
	 * @param times 		指定时间
	 * @return String 时间样式
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String getDateStyle(String times) {
		
		try {
			formatDate(times,STYLE_1);
			return STYLE_1;
		} catch (Exception e) {
//			e.printStackTrace();
		}
		
		try {
			formatDate(times,STYLE_2);
			return STYLE_2;
			
		} catch (Exception e) {
//			e.printStackTrace();
		}
		try {
			formatDate(times,STYLE_3);
			return STYLE_3;
			
		} catch (Exception e) {
//			e.printStackTrace();
		}
		
		try {
			formatDate(times,STYLE_4);
			return STYLE_4;
			
		} catch (Exception e) {
//			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	/**
	 * 
	 * 功能说明：格式化日期			
	 * panye  2014-11-29
	 * @param time 被格式化的日期   fmtStyle  格式化前的样式  wantStyle 格式化后的样式
	 * @return String 格式化后的日期
	 * @throws  ParseException 
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String formatDate(String time,String fmtStyle,String wantStyle) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(fmtStyle);
		Date date = sdf.parse(time);
		sdf = new SimpleDateFormat(wantStyle);
		
		return sdf.format(date);
	}
	
	/**
	 * 
	 * 功能说明：格式化日期  	
	 * panye  2014-11-29
	 * @param  times 预格式化的日期字符串   style 格式化后的样式  默认是 yyyy-MM-dd HH:mm:ss
	 * @return Date   
	 * @throws  ParseException
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String formatDate(String times, String style){
		if (style == null || "".equals(style)) {
			style = STYLE_1;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(STYLE_1);
		SimpleDateFormat sdf2 = new SimpleDateFormat(style);
		try {
			return sdf2.format(sdf.parse(times));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 
	 * 功能说明：根据生日求年龄 周岁			
	 * panye  2014-11-29
	 * @param birthDay  生日
	 * @return int 周岁  
	 * @throws  ParseException
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static int getAgeByBirthDay(String birthDay) throws ParseException {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(STYLE_2);
		Date date = sdf.parse(birthDay);
		if (cal.before(date)) {
			return 0;
		}

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(date);

		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				}
			} else {
				age--;
			}
		} else {
			return 0;
		}

		return age;
	}
	
	
	/**
	 * 
	 * 功能说明：比较两个日期 大小			
	 * panye  2014-11-29
	 * @param DATE1 日期1  DATE2 日期2
	 * @return   返回 int (-1 ：日期1 大于 日期2,0 ：日期1 小于日期2, 1： 日期1 等于日期2) 
	 * @throws 
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static int compareDate(String DATE1, String DATE2,String style) {
		int i = 0;
		if(style == null)
			style = STYLE_1;
		
		SimpleDateFormat df = new SimpleDateFormat(style);
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				i = -1;
			} else if (dt1.getTime() < dt2.getTime()) {
				i = 0;
			} else if (dt1.getTime() == dt2.getTime()) {
				i = 1;
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return i;
	}
	/**
	 * 
	 * 功能说明：比较两个日期 大小			
	 * panye  2014-11-29
	 * @param DATE1 日期1  DATE2 日期2
	 * @return   返回 int (-1 ：日期1 大于 日期2,0 ：日期1 小于日期2, 1： 日期1 等于日期2) 
	 * @throws 
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static int newCompareDate(String DATE1, String DATE2,String style) {
		int i = 0;
		if(style == null)
			style = STYLE_1;
		
		SimpleDateFormat df = new SimpleDateFormat(style);
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				i = 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				i = -1;
			} else if (dt1.getTime() == dt2.getTime()) {
				i = 0;
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return i;
	}
	
	
	
	/**
	 * 
	 * 功能说明：获得指定日期的最后一天			
	 * panye  2014-11-29
	 * @param  date 指定日期
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static Date lastDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.roll(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}
	
	/**
	 * 
	 * 功能说明：获得本月第一天日期	 	
	 * panye  2014-11-29
	 * @param 
	 * @return   String 2015-05-01
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String getMonthBegin() {
		Calendar localTime = Calendar.getInstance();
		String strY = null;
		int x = localTime.get(Calendar.YEAR);
		int y = localTime.get(Calendar.MONTH) + 1;
		strY = y >= 10 ? String.valueOf(y) : ("0" + y);
		return x + "-" + strY + "-01";
	}
	
	
	/**
	 * 
	 * 功能说明：获得本月最后一天日期	 	
	 * panye  2014-11-29
	 * @param 
	 * @return   String 2015-05-01
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String getMonthEnd() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		int endday = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		return calendar.get(Calendar.YEAR) + "-"+ (calendar.get(Calendar.MONTH) + 1) + "-" + endday;
	}
	
	/**
	 * 
	 * 功能说明：判断指定日期是否为周六			
	 * panye  2014-11-29
	 * @param  date  日期字符串
	 * @return   
	 * @throws  ParseException
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static boolean isWeekOfSaturday(String date) throws ParseException {

		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		Date bdate = format1.parse(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(bdate);
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
			return true;
		return false;
	}
	
	/**
	 * 
	 * 功能说明：判断指定日期是否为周日			
	 * panye  2014-11-29
	 * @param  date  日期字符串
	 * @return   
	 * @throws  ParseException
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static boolean isWeekOfSunday(String bDate) throws ParseException {

		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		Date bdate = format1.parse(bDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(bdate);
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			return true;
		return false;
	}
	
	
	/**
	 * 
	 * 功能说明：求得指定日期的前N天日期			
	 * panye  2014-11-29
	 * @param  date 日期 ，day 天数 ，style 预转换的日期格式 （默认为 yyyy-MM-dd HH:mm:ss）
	 * @return  String  
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String getBefore(Date date, int day,String style) {
		if(ChkUtil.isEmpty(style)){
			style = STYLE_1;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(style);
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
		sdf.format(date);
		return  sdf.format(now.getTime());
	}
	
	
	/**
	 * 
	 * 功能说明：是否为闰年			
	 * panye  2015-5-13
	 * @param year 年份	
	 * @return   boolean 
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static boolean leapYear(int year) {
		boolean leap;
		if (year % 4 == 0) {
			if (year % 100 == 0) {
				if (year % 400 == 0)
					leap = true;
				else
					leap = false;
			} else
				leap = true;
		} else
			leap = false;
		return leap;
	}
	
	
	
	/**
	 * 
	 * 功能说明：根据当前日期计算出后面几个月后的日期或者 几天后的日期			
	 * panye  2015-5-13
	 * @param nowTime 当时日期     type{2 代表月 其它代表 日}   style 指定格式化日期样式 默认 yyyy-MM-dd HH:mm:ss	
	 * @return   
	 * @throws	 ParseException
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String getLastTime(String nowTime, int type, int increment,String style){
		String sdate ="";
		try{
			if (style == null || "".equals(style)) {
				style = STYLE_1;
			}
			SimpleDateFormat sdf = new SimpleDateFormat(style);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(nowTime));
			if (type == 2)
				calendar.add(Calendar.MONTH, increment);
			else
				calendar.add(Calendar.DAY_OF_MONTH, increment);
	
			sdate = sdf.format(calendar.getTime());
			calendar.setTime(sdf.parse(sdate));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return sdate;
	}
	
	
	/**
	 * 
	 * 功能说明：求两个日期之间相隔天数	
	 * panye  2015-5-13
	 * @param  time1 日期1   time2 日期2
	 * @return long   相差天数
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static long getBetweenDays(String time1, String time2) {
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat(STYLE_2);
		try {
			Date date1 = ft.parse(time1);
			Date date2 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();
			quot = quot / 1000 / 60 / 60 / 24;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return Math.abs(quot);
	}
	
	
	/**
	 * 
	 * 功能说明：求两个日期相差的天数			
	 * panye  2015-5-13
	 * @param 
	 * @return   天数（int）
	 * @throws  ParseException
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static int diffDays(String startDate, String endDate)throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(startDate));
		long startTime = cal.getTimeInMillis();
		cal.setTime(sdf.parse(endDate));
		long endTime = cal.getTimeInMillis();
		long between_days = (endTime - startTime) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}
	
	
	/**
	 * 
	 * 功能说明：求两个日期相差的秒/分/时		
	 * panye  2015-5-13
	 * @param date1 日期1  date2 日期2  type（1 秒 2 分 3 时）
	 * @return   
	 * @throws  ParseException
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static long getBetweenTimes(String date1,String date2,int type)throws ParseException{
		SimpleDateFormat s = new SimpleDateFormat(STYLE_1);
		long t1 = s.parse(date1).getTime();
		long t2 = s.parse(date2).getTime();
		long result = 0;
		switch (type) {
		//秒
		case 1:
			result = (t2 - t1)/1000;
			break;
			
		//分	
		case 2:	
			result = (t2 - t1)/1000/60;
			break;
		
		//时
		case 3:
			result = (t2 - t1)/1000/60/60;
			break;
		default:
			break;
		}
		return result;
	}
	
	
	
	/**
	 * 
	 * 功能说明：比较两个日期  			
	 * panye  2015-5-13
	 * @param x天x小时x分
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 * @throws ParseException 
	 */
	public static String getDistanceTime(String str1, String str2) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		one = df.parse(str1);
		two = df.parse(str2);
		long time1 = one.getTime();
		long time2 = two.getTime();
		if (time2 <= time1)
			return 0 + "天" + 0 + "小时" + 0 + "分";

		long diff = time2 - time1;
		day = diff / (24 * 60 * 60 * 1000);
		hour = (diff / (60 * 60 * 1000) - day * 24);
		min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
		return day + "天" + hour + "小时" + min + "分";
	}
	
	
	/**
	 * 
	 * 功能说明：得到当前时间所处的时间段			
	 * panye  2015-5-13
	 * @param 
	 * @return   1  凌晨 2 早上 3中午 4下午 5 晚上
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static int PeriodOfTime(){
		Calendar calendar =  Calendar.getInstance(); 
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int result = 0;
		//早上
		if(hours >=0 && hours <= 5){
			//凌晨
			result = 1;
		}else if(hours >=6 && hours <= 10){
			//早上
			result = 2;
		}else if(hours >=11 && hours <= 13){
			//中午
			result = 3;
		}else if(hours >=14 && hours <= 18){
			//下午
			result = 4;
		}else if(hours >=19 && hours <= 24){
			//晚上
			result = 5;
		}
		return result;
	}
	
	/**
	 * 
	 * 功能说明：获得当前年份			
	 * panye  2015-5-13
	 * @param 
	 * @return   年份
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static int getCurrentYear(){
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR);
	}
	
	/**
	 * 
	 * 功能说明：获得当前月份			
	 * panye  2015-5-13
	 * @param 
	 * @return  月份
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static int getCurrentMonth(){
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.MONTH) + 1;
	}
	
	
	/**
	 * 
	 * 功能说明：获得当前日份			
	 * panye  2015-5-13
	 * @param 
	 * @return  日
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static int getCurrentDay(){
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DATE);
	}
	
	
	/**
	 * 
	 * 功能说明：该方法实现的功能			
	 * panye  2015-5-13
	 * @param 
	 * @return  日
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static int getCurrentHour(){
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.HOUR_OF_DAY);
	}
	/**
	 * 
	 * 功能说明：计算日期相差的天数			
	 * panye  2015-5-13
	 * @param 
	 * @return  日
	 * @throws  
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static int getQuot(String time1, String time2) {
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date1 = ft.parse(time1);
			Date date2 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();
			quot = quot / 1000 / 60 / 60 / 24;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return Math.abs((int)quot);
	}
	/**
	 * 
	 * 功能说明：获得 当前时间的前几分钟时间		
	 * panye  2015-6-1
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String getBeforeSeconds(int second,String style){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(style);
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.SECOND, now.get(Calendar.SECOND) - second);
		sdf.format(date);
		return sdf.format(now.getTime());
	}

	public static String getserialnoCheckUnique(String pre) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		String str = sdf.format(new Date());
		String random=NumberFormat.formatDouble(Math.random()*10000,"00000");
		String result=pre+str+random;
		return result;
	}
	/**
	 * 
	 * 功能说明：获得唯一流水号		
	 * panye  2015-6-1
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String getSerialNo(String preString){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String serialNo =  sdf.format(date);
		String nextString =String.valueOf((int)Math.floor(Math.random()*10000));
		serialNo = serialNo+nextString;
		if(preString!=null){
			serialNo =preString+serialNo;
		}
		return serialNo;
	}
	
	/**
	 * 
	 * 功能说明：格式化工具类	
	 * panye  2015-6-1
	 * @param 
	 * @return   
	 * @throws  该方法可能抛出的异常，异常的类型、含义。
	 * 最后修改时间：
	 * 修改人：panye
	 * 修改内容：
	 * 修改注意点：
	 */
	public static String formatInvestRate(Object obj){
		if(obj==null){
			return "00000000";
		}
		String a =NumberFormat.formatDouble(String.valueOf(ArithUtil.mul(Double.parseDouble(obj.toString()),100000d)));
		a = a.split(Pattern.quote("."))[0];
		if(a.length()>8){
			a = a.substring(0,8);
		}
		if(a.length()<8){
			int diff = 8-a.length();
			 for(int i=0;i<diff;i++){
				 a = "0"+a;
			 }
		}
		return a;
	}
	public static void main(String[] args) {
		System.out.println(String.valueOf(ArithUtil.mul(Double.parseDouble("185"),100000d)));
		System.out.println(formatInvestRate(5.2));
		System.out.println(newCompareDate("2017-12-12","2017-12-11 00:22:32", null));
	}
}
