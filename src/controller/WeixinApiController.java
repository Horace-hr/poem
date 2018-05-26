package controller;

import model.Chapter;
import utils.MD5;
import utils.StringUtil;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.JsTicketApi;
import com.jfinal.weixin.sdk.api.JsTicketApi.JsApiType;
import com.jfinal.weixin.sdk.jfinal.ApiController;

import config.AdminInter;

public class WeixinApiController extends ApiController {

	@Override
	public ApiConfig getApiConfig() {
		ApiConfig ac = new ApiConfig();
		
		// 配置微信 API 相关常量
		ac.setToken(PropKit.get("token"));
		ac.setAppId(PropKit.get("appId"));
		ac.setAppSecret(PropKit.get("appSecret"));
		
		/**
		 *  是否对消息进行加密，对应于微信平台的消息加解密方式：
		 *  1：true进行加密且必须配置 encodingAesKey
		 *  2：false采用明文模式，同时也支持混合模式
		 */
		ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey", "setting it in config file"));
		return ac ;
	}
	
	public void getSignature() {
		String noncestr = StringUtil.getKey();
		Long timestamp = System.currentTimeMillis();
		String url = getRequest().getHeader("referer");
		url = StrKit.notBlank(url) ?  url.split("#")[0] : "" ;
		String ticket = JsTicketApi.getTicket(JsApiType.jsapi).getTicket();
		
		StringBuffer sb = new StringBuffer("jsapi_ticket=" + ticket) ;
		sb.append("&noncestr=" + noncestr ) ;
		sb.append("&timestamp="+timestamp) ;
		sb.append("&url=" + url );
		String signature = MD5.sha(sb.toString());
		
		JSONObject jsonObject = new JSONObject() ;
		jsonObject.put("noncestr", noncestr);
		jsonObject.put("timestamp", timestamp);
		jsonObject.put("signature", signature);
		jsonObject.put("appid", PropKit.get("appId"));
		renderJson(jsonObject);
	}
	
	@Before(AdminInter.class)
	public void addChapter() {
		renderJson(Chapter.dao.pushMsg(getPara("id") , this ));
	}
	
}
