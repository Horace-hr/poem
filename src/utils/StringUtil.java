package utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

public class StringUtil {
	/**
	 * 重写getPara，进行二次decode解码
	 */
	public static String getDcodePara(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (null != value && !value.isEmpty()) {
			try {
				value = URLDecoder.decode(value, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	// 分页查询判断是否选择第一页
	public static boolean isPageFirst(HttpServletRequest request) {
		String uri = request.getRequestURI();
		// 判断URL最后是否是分页数
		int hopeLength = uri.lastIndexOf("/") + 1;
		int reallyLrnth = uri.length();
		if ((reallyLrnth - hopeLength) == 1) {
			return true;
		}
		return false;
	}

	/**
	 * 获取32位字符串
	 * 
	 * @return
	 */
	public static String getKey() {
		String uuid = UUID.randomUUID().toString();
		return uuid.toString().replace("-", "");
	}

	/**
	 * 生日的格式化：yyyy-MM-dd
	 * 
	 * @param str
	 * @return
	 */
	public java.sql.Date convertBirthday(String str) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		try {
			java.util.Date date = sdf.parse(str);
			java.sql.Date convertDate = new java.sql.Date(date.getTime());
			return convertDate;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	public static boolean equalsIgnoreCase(String str1, String str2) {
		return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
	}

	public static boolean isNumeric(String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isDigit(str.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	public static boolean isChenge(String str1, String str2) {
		return str1.equals(str2);
	}
	
	/**
	 * 拼接字符串
	 * @param str
	 * @return
	 */
	public static String join(String[] str){
		if(str.length<1){
			return "";
		}
		StringBuffer sb=new StringBuffer();
		for (String string : str) { 
			sb.append(string+",");
		}
		return sb.substring(0, sb.length()-1); 
	}

}
