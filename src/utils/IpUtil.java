package utils;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.kit.IpKit;

public class IpUtil {
	
	public static String getCity(HttpServletRequest request) {
		String realIp = IpUtil.getRealIp(request) ;
		String json = null ;
		try {
			json = HttpKit.get("http://ip.taobao.com/service/getIpInfo.php?ip=" + realIp);
		} catch (Exception e) {
			return "" ;
		}
		JSONObject jsonObject = JSONObject.parseObject(json);		
		if (jsonObject.getString("code").equals("1")) {
			return "" ;
		}
		return jsonObject.getJSONObject("data").getString("city");
	}
	
	public static String getCity(String realIp) {
		String json = null ;
		try {
			json = HttpKit.get("http://ip.taobao.com/service/getIpInfo.php?ip=" + realIp);
		} catch (Exception e) {
			return "" ;
		}
		JSONObject jsonObject = JSONObject.parseObject(json);		
		if (jsonObject.getString("code").equals("1")) {
			return "" ;
		}
		return jsonObject.getJSONObject("data").getString("city");
	}
	
	public static JSONObject parsing(String realIp) {
		String json = null ;
		try {
			json = HttpKit.get("http://ip.taobao.com/service/getIpInfo.php?ip=" + realIp);
		} catch (Exception e) {
			return null ;
		}
		JSONObject jsonObject = JSONObject.parseObject(json);		
		if (jsonObject.getString("code").equals("1")) {
			return null ;
		}
		return jsonObject.getJSONObject("data");
	}
	
	public static String getRealIp(HttpServletRequest request) {
		String ip = IpKit.getRealIp(request) ;
		if (StrKit.isBlank(ip)) {
            ip = "127.0.0.1";
        }
        ip = ip.replace("，", ",") ;
        if (ip.indexOf(",") != -1) {
			ip = ip.split(",")[0] ;
		}
        return ip ;
	}
	
	public static String getRealIp(String ip) {
		if (StrKit.isBlank(ip)) {
            ip = "127.0.0.1";
        }
        ip = ip.replace("，", ",") ;
        if (ip.indexOf(",") != -1) {
			ip = ip.split(",")[0] ;
		}
        return ip ;
	}
}
