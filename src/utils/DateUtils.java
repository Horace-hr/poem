package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtils {
	
	public static String getLastMonth(int numbers) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -numbers);
		return new SimpleDateFormat("yyyy-MM").format(calendar.getTime());
	}
	
	//计算当前日期的星期天的日期
	public static String getLastWeek() {
		Calendar calendar = Calendar.getInstance() ;
		int days = Calendar.DAY_OF_WEEK ;
		calendar.add(Calendar.DATE, 7 - days ) ;
		return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()) ;
	}
	
	public static String getLastSevenDay() {
		Calendar calendar = Calendar.getInstance() ;
		calendar.add(Calendar.DATE, -7 ) ;
		return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()) ;
	}
	
	//计算当前日期
	public static String getCurrentDate(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}
	
	//计算昨日
	public static String getYesterdayDate() {
		Calendar calendar = Calendar.getInstance() ;
		calendar.add(Calendar.DATE, -1) ;
		return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
	}
	
	//返回当前小时数
	public static String getCurrentHours(Date date) {
		return new SimpleDateFormat("H").format(date);
	}
	
	public static void main(String[] args) {
		System.out.println(DateUtils.getCurrentHours(new Date()));
	}
}
