package utils.weixin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.SortedMap;
import java.util.TreeMap;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;

public class WeixinApiUtil {
	
	private static String unifyPayApiUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	private static String authAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";
	private static String auth_code_url = "https://open.weixin.qq.com/connect/oauth2/authorize";
	private static String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token";
	private static String jsapiUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
	private static String userInfo = "https://api.weixin.qq.com/cgi-bin/user/info";
	
	private static String auth_userinfo = "https://api.weixin.qq.com/sns/userinfo";
	private static String QRCODE_TICKET = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";
	
	
	

	/**
	 * 获得统一下单ID
	 * @param openid
	 * @param appid
	 * @param mch_id
	 * @param appsecret
	 * @param partnerkey
	 * @param key
	 * @param nonce_str
	 * @param body
	 * @param attach
	 * @param out_trade_no
	 * @param total_fee
	 * @param spbill_create_ip
	 * @param notify_url
	 * @param trade_type
	 * @param reqHandler
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String getPrepayid(String openid, String appid, String mch_id,
			String appsecret, String partnerkey, String key, String nonce_str,
			String body, String attach, String out_trade_no, String total_fee,
			String spbill_create_ip, String notify_url, String trade_type
			, RequestHandler reqHandler) throws UnsupportedEncodingException {
		String prepay_id  = "";
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		System.out.println("mch_id---->"+mch_id);
		packageParams.put("appid", appid);  
		packageParams.put("mch_id", mch_id);  
		packageParams.put("nonce_str", nonce_str);  
		packageParams.put("body", body);  
		packageParams.put("attach", attach);  
		packageParams.put("out_trade_no", out_trade_no);  
		//这里写的金额为1 分到时修改
//			packageParams.put("total_fee", "1");  
		packageParams.put("total_fee", total_fee);  
		packageParams.put("spbill_create_ip", spbill_create_ip);  
		packageParams.put("notify_url", notify_url);  
		
		packageParams.put("trade_type", trade_type);  
		packageParams.put("openid", openid);  
		packageParams.put("key", key);  

		
		reqHandler.init(appid, appsecret, partnerkey);
		
		String sign = reqHandler.createSign(packageParams);
		String xml = getPostXmlParam(openid, appid, mch_id, nonce_str,body, attach, out_trade_no, total_fee, spbill_create_ip,notify_url, trade_type, sign);
		
		@SuppressWarnings("unused")
		String allParameters = "";
		try {
			allParameters =  reqHandler.genPackage(packageParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
//			String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		
		try {
			prepay_id = GetWxOrderno.getPayNo(unifyPayApiUrl, xml);
			if(prepay_id.equals("")){
//					request.setAttribute("ErrorMsg", "统一支付接口获取预支付订单出错");
//					String saveMessage = "prepayIdEmpty";
				//response.sendRedirect("http://xiaoxiaowen.nat123.net/weixin/cxerp/error.jsp");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return prepay_id;
	}
	
	private static String getPostXmlParam(String openid, String appid, String mch_id,
			String nonce_str, String body, String attach, String out_trade_no,
			String total_fee, String spbill_create_ip, String notify_url,
			String trade_type, String sign) {
		String xml="<xml>"+
				"<appid>"+appid+"</appid>"+
				"<mch_id>"+mch_id+"</mch_id>"+
				"<nonce_str>"+nonce_str+"</nonce_str>"+
				"<body><![CDATA["+body+"]]></body>"+
				"<attach><![CDATA["+attach+"]]></attach>"+
				"<out_trade_no>"+out_trade_no+"</out_trade_no>"+
				//金额
				"<total_fee>"+total_fee+"</total_fee>"+
				"<spbill_create_ip>"+spbill_create_ip+"</spbill_create_ip>"+
				"<notify_url>"+notify_url+"</notify_url>"+
				"<trade_type>"+trade_type+"</trade_type>"+
				"<openid>"+openid+"</openid>"+
				"<sign><![CDATA["+sign+"]]></sign>"+
				"</xml>";
		try {
			xml = new String(xml.getBytes("UTF-8"),"ISO-8859-1");
//			xml = new String(xml.getBytes("GBK"),"ISO-8859-1");
//			xml = new String(xml.getBytes(),"ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return xml;
	}
	
	public static  SortedMap<String, String> getFinalpackage(String appid, String key,String nonce_str, String prepay_id) {
		SortedMap<String, String> finalpackage = new TreeMap<String, String>();
//	appId = appid;
		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		//nonceStr = nonce_str;
		String prepay_id2 = "prepay_id="+prepay_id;
		String packages = prepay_id2;
		finalpackage.put("appId", appid);  
		finalpackage.put("timeStamp", timestamp);  
		finalpackage.put("nonceStr", nonce_str);  
		finalpackage.put("package", packages);  
		finalpackage.put("signType", "MD5");
		finalpackage.put("key",key);
		return finalpackage;
	}
	
	//
	public static String getOpenId(String code) throws IOException{
		String appid = PropKit.get("appId");
		String secret= PropKit.get("appSecret");
		StringBuffer authAccessTokenUrlBuffer = new StringBuffer(authAccessTokenUrl);
		authAccessTokenUrlBuffer.append("?appid=").append(appid);
		authAccessTokenUrlBuffer.append("&secret=").append(secret);
		authAccessTokenUrlBuffer.append("&code=").append(code);
		authAccessTokenUrlBuffer.append("&grant_type=").append("authorization_code");
		
		String content = HttpUtil.getContentFromUrl(authAccessTokenUrlBuffer.toString());
		JSONObject json = JSONObject.parseObject(content);
		String openid = json.getString("openid");
		return openid;
	}
	
	public static String getUserOpenId(String code) throws IOException{
		String appid = PropKit.get("appIdUser");
		String secret= PropKit.get("appSecretUser");
		StringBuffer authAccessTokenUrlBuffer = new StringBuffer(authAccessTokenUrl);
		authAccessTokenUrlBuffer.append("?appid=").append(appid);
		authAccessTokenUrlBuffer.append("&secret=").append(secret);
		authAccessTokenUrlBuffer.append("&code=").append(code);
		authAccessTokenUrlBuffer.append("&grant_type=").append("authorization_code");
		
		String content = HttpUtil.getContentFromUrl(authAccessTokenUrlBuffer.toString());
		JSONObject json = JSONObject.parseObject(content);
		String openid = json.getString("openid");
		return openid;
	}
	
	/***********/
	public static JSONObject getWXUserInfo(String code) throws IOException{
		String appid = PropKit.get("appId");
		String secret= PropKit.get("appSecret");
		StringBuffer authAccessTokenUrlBuffer = new StringBuffer(authAccessTokenUrl);
		authAccessTokenUrlBuffer.append("?appid=").append(appid);
		authAccessTokenUrlBuffer.append("&secret=").append(secret);
		authAccessTokenUrlBuffer.append("&code=").append(code);
		authAccessTokenUrlBuffer.append("&grant_type=").append("authorization_code");
		String content = HttpUtil.getContentFromUrl(authAccessTokenUrlBuffer.toString());
		JSONObject json = JSONObject.parseObject(content);
		String openid = json.getString("openid");
		if(StrKit.notBlank(openid)){
			String access_token = json.getString("access_token");
			StringBuffer getUserInfo = new StringBuffer(auth_userinfo);
			getUserInfo.append("?access_token=").append(access_token);
			getUserInfo.append("&openid=").append(openid).append("&lang=zh_CN");
			String userInfoContent = HttpUtil.getContentFromUrl(getUserInfo.toString());
			JSONObject userinfoObject = JSONObject.parseObject(userInfoContent);
			return userinfoObject;
		}
		return null ;
	}
	
	//获取用户信息专用
	public static JSONObject getUserWXUserInfo(String code) throws IOException{
		String appid = PropKit.get("appIdUser");
		String secret= PropKit.get("appSecretUser");
		StringBuffer authAccessTokenUrlBuffer = new StringBuffer(authAccessTokenUrl);
		authAccessTokenUrlBuffer.append("?appid=").append(appid);
		authAccessTokenUrlBuffer.append("&secret=").append(secret);
		authAccessTokenUrlBuffer.append("&code=").append(code);
		authAccessTokenUrlBuffer.append("&grant_type=").append("authorization_code");
		System.out.println("两个参数的appIdUser="+appid);
		System.out.println("两个参数的appSecretUser="+secret);
		String content = HttpUtil.getContentFromUrl(authAccessTokenUrlBuffer.toString());
		JSONObject json = JSONObject.parseObject(content);
		String openid = json.getString("openid");
		if(StrKit.notBlank(openid)){
			String access_token = json.getString("access_token");
			StringBuffer getUserInfo = new StringBuffer(auth_userinfo);
			getUserInfo.append("?access_token=").append(access_token);
			getUserInfo.append("&openid=").append(openid).append("&lang=zh_CN");
			String userInfoContent = HttpUtil.getContentFromUrl(getUserInfo.toString());
			JSONObject userinfoObject = JSONObject.parseObject(userInfoContent);
			return userinfoObject;
		}
		return null ;
	}
	
	public static String getAccessToken(){
		String appid = "";
		String secret= "";
		StringBuffer authAccessTokenUrlBuffer = new StringBuffer(accessTokenUrl);
		authAccessTokenUrlBuffer.append("?grant_type=client_credential");
		authAccessTokenUrlBuffer.append("&appid=").append(appid);
		authAccessTokenUrlBuffer.append("&secret=").append(secret);
		String content="{access_token:''}";
		try {
			content = HttpUtil.getContentFromUrl(authAccessTokenUrlBuffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject json = JSONObject.parseObject(content);
		String access_token = json.getString("access_token");
		System.out.println("获取access_token="+access_token);
		return access_token;
	}
	
	public static String getJsapiTicket(){
		StringBuffer authAccessTokenUrlBuffer = new StringBuffer(jsapiUrl);
		authAccessTokenUrlBuffer.append("?type=jsapi");
		authAccessTokenUrlBuffer.append("&access_token=").append(getAccessToken());
		String content="{\"errcode\":0,\"errmsg\":\"ok\",\"ticket\":\"sM4AOVdWfPE4DxkXGEs8VGEjQ7-2g5ySRgAru-v6EtCshWSYuErJGo3lg34Me_RMiNUbKWvadNQ1dsOgiuBp8A\",\"expires_in\":7200}";
		try {
			content = HttpUtil.getContentFromUrl(authAccessTokenUrlBuffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject json = JSONObject.parseObject(content);
		String ticket = json.getString("ticket");
		return ticket;
	}
	
	public static String getSignature(String url,String noncestr,String timestamp,RequestHandler reqHandler){
		String jsapi_ticket = getJsapiTicket(); 
		
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("jsapi_ticket", jsapi_ticket);
		packageParams.put("noncestr", noncestr);  
		packageParams.put("timestamp", timestamp);  
		packageParams.put("url", url);  
		reqHandler.init();
		String sign = reqHandler.createJsSign(packageParams);
		return sign;
	}
	
	/*
	 * 获取code
	 */
	public static String getCodeUrl(String orderId){
		String appid = PropKit.get("appId");
		StringBuffer authAccessTokenUrlBuffer = new StringBuffer(auth_code_url);
		authAccessTokenUrlBuffer.append("?appid=").append(appid);
		try {
			authAccessTokenUrlBuffer.append("&redirect_uri=").append(java.net.URLEncoder.encode(PropKit.get("weixinPayUrl"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		authAccessTokenUrlBuffer.append("&response_type=code&scope=snsapi_base&state="+orderId+"#wechat_redirect");
		return authAccessTokenUrlBuffer.toString();
	}
	
	public static String getCodeUrl(String url,String args){
		String appid = PropKit.get("appId");
		StringBuffer authAccessTokenUrlBuffer = new StringBuffer(auth_code_url);
		authAccessTokenUrlBuffer.append("?appid=").append(appid);
		try {
			authAccessTokenUrlBuffer.append("&redirect_uri=").append(java.net.URLEncoder.encode(url, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		authAccessTokenUrlBuffer.append("&response_type=code&scope=snsapi_userinfo&state="+args+"#wechat_redirect");
		return authAccessTokenUrlBuffer.toString();
	}
	
	//
	public static String getCodeUrl(String url,String args , boolean isBase){
		String base = "snsapi_userinfo" ;
		if (isBase) {
			base = "snsapi_base" ;
		}
		String appid = PropKit.get("appId");
		StringBuffer authAccessTokenUrlBuffer = new StringBuffer(auth_code_url);
		authAccessTokenUrlBuffer.append("?appid=").append(appid);
		try {
			authAccessTokenUrlBuffer.append("&redirect_uri=").append(java.net.URLEncoder.encode(url, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		authAccessTokenUrlBuffer.append("&response_type=code&scope="+base+"&state="+args+"#wechat_redirect");
		return authAccessTokenUrlBuffer.toString();
	}
	
	public static String getUserCodeUrl(String url,String args , boolean isBase){
		String base = "snsapi_userinfo" ;
		if (isBase) {
			base = "snsapi_base" ;
		}
		String appid = PropKit.get("appIdUser");
		StringBuffer authAccessTokenUrlBuffer = new StringBuffer(auth_code_url);
		authAccessTokenUrlBuffer.append("?appid=").append(appid);
		try {
			authAccessTokenUrlBuffer.append("&redirect_uri=").append(java.net.URLEncoder.encode(url, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		authAccessTokenUrlBuffer.append("&response_type=code&scope="+base+"&state="+args+"#wechat_redirect");
		return authAccessTokenUrlBuffer.toString();
	}
	
	public static String getRandomStr() {
		String currTime = TenpayUtil.getCurrTime();
		//8位日期
		String strTime = currTime.substring(8, currTime.length());
		//四位随机数
		String strRandom = TenpayUtil.buildRandom(4) + "";
		//10位序列号,可以自行调整
		String strReq = strTime + strRandom ;
		return strReq ;
	}
	
	public static String getQrCodeUrl(String qrcode) {
		return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + qrcode  ;
	}
	
	
	
	
}
