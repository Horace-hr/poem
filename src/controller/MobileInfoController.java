package controller;

import model.Config;
import model.Giving;
import model.History;
import model.Novel;
import model.Poem;
import model.SignIn;
import model.User;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;

import config.BaseController;

public class MobileInfoController extends BaseController {
	
	//查询小说信息 - 阅读记录
	/*public void novel() {
		JSONObject jsonObject = getJsonObject() ;
		jsonObject.put("data", Novel.dao.findByIdInCache(getPara("id")));
		jsonObject.put("history", History.dao.findRecordInCache(getUserCookie(), getPara("id"))) ;
		jsonObject.put("givings", Giving.dao.findRecord(getUserCookie(),getPara("id"))) ;
		renderJson(jsonObject);
	}*/
	

	//查询诗词信息 - 阅读记录
	public void novel() {
		JSONObject jsonObject = getJsonObject() ;
		jsonObject.put("data", Poem.dao.findByIdInCache(getPara("id")));
		jsonObject.put("history", History.dao.findRecordInCache(getUserCookie(), getPara("id"))) ;
		jsonObject.put("givings", Giving.dao.findRecord(getUserCookie(),getPara("id"))) ;
		renderJson(jsonObject);
	}
	
	
	
	
	
	/*
	 * 微信获取用户信息条件：
	 * 1.付费用户
	 * 2.微信浏览器
	 * 3.系统配置获取
	 * 4.个人中心页面
	 * 5.上次获取用户信息的时间大于3天
	 */
	public void user() {
		String page = getPara("page");
		User user = User.dao.findByIdInCache(getUserCookie()) ;
		Config config = Config.dao.findByKey(Config.KEY_GET_OPEN_ID) ;
		String userAgent = this.getRequest().getHeader("user-agent").toLowerCase();
		boolean needGetWxUserInfo = ( StrKit.notBlank(page) && "userCenter".equals(page) ) && User.dao.overdue(user) && user.getIsRecharged()
				&& ( userAgent.indexOf("micromessenger") > -1 ) && config.getValue() == 1  ; 
		if ( needGetWxUserInfo ) {
			JSONObject jsonObject = new JSONObject() ;
			jsonObject.put("status", "100") ;
			jsonObject.put("message", page ) ;
			renderJson(jsonObject) ;
		}else{
			renderInfoJson(user) ;
		}
	}
	
	//查询签到信息
	public void signIn() {
		renderInfoJson(SignIn.dao.findByUserIdInCache(getUserCookie())) ;
	}
	
	//阅读记录
	public void history() {
		renderInfoJson(History.dao.findLastRecordInCache(getUserCookie())) ;
	}
	
}
	
	
