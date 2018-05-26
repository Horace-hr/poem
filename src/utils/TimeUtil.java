package utils;

import org.beetl.core.Context;
import org.beetl.core.Function;

public class TimeUtil implements Function {

	@Override
	public Object call(Object[] arg0, Context arg1) {
		Long minute = (Long)arg0[0];
		if (null == minute || minute > 3*24*60) {
			return "-1" ;
		}else if (minute < 60) {
			return minute + "分钟前" ;
		}else if (minute > 24*60) {
			Long day = minute/(24*60) ;
			return day + "天前" ;
		}else {
			Long hour = minute/60 ;
			return hour + "小时前" ;
		}
	}

}
