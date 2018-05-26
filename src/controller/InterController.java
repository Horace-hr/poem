package controller;

import com.alibaba.fastjson.JSONObject;

import config.BaseController;

public class InterController extends BaseController {
	public void index() {
		JSONObject jsonObject = new JSONObject() ;
		jsonObject.put("status", getPara("status" , "100")) ;
		jsonObject.put("message", getPara("message"));
		renderJson(jsonObject);
	}
}
