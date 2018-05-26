package controller;

import java.io.IOException;

import model.User;

import utils.weixin.WeixinApiUtil;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;

import config.BaseController;
import config.UserInter;

/**
 * IndexController
 */
public class IndexController extends BaseController {
	
	public void index() {
		redirect("/novel");
	}
	
	//获取用户信息CODE
	public void getCode() {
		String queryState = getQueryState() ;
		boolean isBase = getParaToBoolean("isBase" , false) ;
		String url = WeixinApiUtil.getUserCodeUrl( getCtx() + "/getUserInfo" , queryState , isBase);
		if (isBase) {
			url = WeixinApiUtil.getUserCodeUrl( getCtx() + "/getOpenId" , queryState , isBase);
		}
//		String url = WeixinApiUtil.getUserCodeUrl( "http://cps.lijitui.com/wx/getUserInfo" , queryState , isBase);
//		if (isBase) {
//			url = WeixinApiUtil.getUserCodeUrl( "http://cps.lijitui.com/wx/getOpenId" , queryState , isBase);
//		}
		redirect(url);
	}
	
	//获取微信用户openid - 新用户
	public void getOpenId() {
		String state = getPara("state");
		String code = getPara("code");
		if (StrKit.isBlank(code)) {
			redirect("/novel") ;
			return ;
		}
		
		String url = "/novel" ;
		String channelId = null ;
		if (StrKit.notBlank(state)) {
			JSONObject jsonObject = new JSONObject() ;
			state = parsingQueryState(state, jsonObject);
			url = "/novel?" + state ;
			channelId = jsonObject.getString("qd") ;
		}
		
		String openId = "" ;
		try {
			openId = WeixinApiUtil.getUserOpenId(code) ;
		} catch (IOException e) {
			e.printStackTrace();
			redirect(url) ;
			return ;
		}
		User.dao.save(openId , channelId ,  this ) ;
		redirect(url) ;
	}
	
	//获取微信用户信息
	public void getUserInfo() {
		String state = getPara("state");
		String code = getPara("code");
		String url = "/novel" ;
		if (StrKit.isBlank(code)) {
			redirect(url) ;
			return ;
		}
		try {
			JSONObject userInfo = WeixinApiUtil.getUserWXUserInfo(code) ;
			userInfo.put("id", getUserCookie()) ;
			User.dao.save(userInfo,this) ;
			if (StrKit.notBlank(state)) {
				state = 
				url = "/novel?" + parsingQueryState(state) ;
			}
			redirect(url);
			return ;
		} catch (IOException e) {
			e.printStackTrace();
			redirect(url);
		}
	}
}





