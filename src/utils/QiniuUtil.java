package utils;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

public class QiniuUtil {
	
	public static String ACCESS_KEY = "";//your ACCESS_KEY
	public static String SECRET_KEY = "";//your SECRET_KEY


	static Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);

	// 简单上传，使用默认策略
	public static String getUpToken0() {
		return auth.uploadToken("static");
	}

	// 覆盖上传
	public static String getUpToken1() {
		return auth.uploadToken("static", "xiaowuzi");
	}

	// 设置指定上传策略
	public String getUpToken2() {
		return auth.uploadToken("static", null, 3600, new StringMap().put(
				"callbackUrl", "call back url").putNotEmpty("callbackHost", "")
				.put("callbackBody", "key=$(key)&hash=$(etag)"));
	}

	// 设置预处理、去除非限定的策略字段
	private String getUpToken3() {
		return auth.uploadToken("static", null, 3600, new StringMap()
				.putNotEmpty("persistentOps", "").putNotEmpty(
						"persistentNotifyUrl", "").putNotEmpty(
						"persistentPipeline", ""), true);
	}
	
	
	public static void main(String[] args){
		QiniuUtil qi = new QiniuUtil();
		System.out.println(qi.getUpToken0());
	}

}
