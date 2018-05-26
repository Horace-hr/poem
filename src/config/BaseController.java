package config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Admin;
import utils.StringUtil;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

/**
 * 公共Controller
 */
public abstract class BaseController extends Controller {

	/**
	 * 全局变量
	 */
	protected String id;			// 主键
	protected List<?> list;			// 公共list
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	/**
	 * 获取项目请求根路径
	 * @return
	 */
	protected String getCtx(){
		return getAttr("ctx");
	}
	
	/*
	 * 从前端页面接收数据并放入map中
	 * @param keys 接收的参数
	 */
	protected void putValuesIntoMap( String keys , Map<String, String> map ) {
		map.put(keys, this.getPara(keys));
	}
	
	protected void putValuesIntoMap( String keys , Map<String, String> map , boolean isClear) {
		if (isClear) {
			map.clear();
		}
		map.put(keys, this.getPara(keys));
	}
	
	/*
	 * 从前端页面接收数据并放入map中
	 * @param keys 接收的参数
	 * @param defaultValue 如果接收的参数为空时的默认值
	 */
	protected void putValuesIntoMap( String keys  , String defaultValue , Map<String, String> map  ) {
		map.put(keys, this.getPara(keys , defaultValue));
	}
	
	protected void putValuesIntoMap( String keys  , String defaultValue , Map<String, String> map , boolean isClear ) {
		if (isClear) {
			map.clear();
		}
		map.put(keys, this.getPara(keys , defaultValue));
	}
	
	protected String getParamsString(Map<String, String> map) {
		StringBuffer params = new StringBuffer();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (StrKit.notBlank(params.toString())) {
				if (StrKit.notBlank(entry.getValue())) {
					params.append("&"+entry.getKey()+"="+entry.getValue());
				}
			}else {
				if (StrKit.notBlank(entry.getValue())) {
					params.append(entry.getKey()+"="+entry.getValue());
				}
			}
		}
		return params.toString();
	}
	
	protected void renderAjaxText(String fileRoute) {
		String littlePath = "/WEB-INF/view" + fileRoute ;
		String filePath = getRequest().getSession().getServletContext().getRealPath(littlePath); 
		File file = new File(filePath);
		StringBuffer stringBuffer = new StringBuffer();
		 try {
            InputStreamReader read = new InputStreamReader(
            new FileInputStream(file),"utf-8");
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while((lineTxt = bufferedReader.readLine()) != null){
            	stringBuffer.append(lineTxt);
            }
            read.close();
        } catch (Exception e) {
        	System.out.println(fileRoute + "文件不存在");
            e.printStackTrace();
        }
		this.renderText(stringBuffer.toString());
	}
	
	protected JSONObject getJsonObject() {
		JSONObject jsonObject = new JSONObject() ;
		jsonObject.put("status", "200") ;
		jsonObject.put("message", "操作成功") ;
		return jsonObject ;
	}
	
	protected JSONObject getJsonObject(String msg) {
		JSONObject jsonObject = new JSONObject() ;
		jsonObject.put("status", "200") ;
		jsonObject.put("message", msg) ;
		return jsonObject ;
	}
	
	protected JSONObject getErrorJsonObject(String msg) {
		JSONObject jsonObject = new JSONObject() ;
		jsonObject.put("status", "400") ;
		jsonObject.put("message", msg) ;
		return jsonObject ;
	}
	
	protected JSONObject SystemErrorJson() {
		JSONObject jsonObject = new JSONObject() ;
		jsonObject.put("status", "400") ;
		jsonObject.put("message", "系统错误，请稍后再试") ;
		return jsonObject ;
	}
	
	protected void renderSuccessJson() {
		this.renderJson(this.getJsonObject()) ;
	}
	
	protected void renderSuccessJson(String msg) {
		this.renderJson(this.getJsonObject(msg));
	}
	
	protected void renderErrorJson(String msg) {
		this.renderJson(this.getErrorJsonObject(msg));
	}
	
	protected String getIds() {
		return StringUtil.getKey();
	}
	
	protected void setUserCookie(String openId) {
		this.setCookie("cid", openId, 30*24*60*60, true);
	}
	
	protected String getUserCookie() {
		return this.getCookie("cid");
	}
	
	protected String getAdminId() {
		return (String) (StrKit.isBlank(getCookie(Admin.ADMIN_STATUS)) ? getSessionAttr(Admin.ADMIN_STATUS) : getCookie(Admin.ADMIN_STATUS)) ;
	}
	
	protected Map<String, String> getParaMaps() {
		Map<String, String> map = new HashMap<String, String>() ;
		for (Entry<String, String[]> entity : getParaMap().entrySet()) {
			if (StrKit.notBlank(entity.getValue()[0]) && !"null".equals(entity.getValue()[0]) ) {
				map.put(entity.getKey(), entity.getValue()[0]);
			}
		}
		return map ;
	}
	
	protected void renderInfoJson(Object object) {
		JSONObject jsonObject = getJsonObject();
		jsonObject.put("data", object);
		renderJson(jsonObject);
	}
	
	protected void renderPageJson(Object object) {
		JSONObject jsonObject = getJsonObject();
		jsonObject.put("page", object);
		renderJson(jsonObject);
	}
	
	protected String getQueryState() {
		StringBuffer sBuffer = new StringBuffer() ;
		Map<String, String> map = getParaMaps() ;
		for (Entry<String, String> entry : map.entrySet()) {
			if (StrKit.notBlank(entry.getValue())) {
				if (StrKit.notBlank(sBuffer.toString())) {
					sBuffer.append("~~") ;
				}
				sBuffer.append(entry.getKey() + ":" + entry.getValue() ) ;
			}
		}
		return sBuffer.toString() ;
	}
	
	protected String parsingQueryState(String state , JSONObject jsonObject) {
		StringBuffer sBuffer = new StringBuffer() ;
		String[] params = state.split("~~") ;
		for (String param : params) {
			String[] paramStrings = param.split(":") ;
			jsonObject.put(paramStrings[0], paramStrings[1]) ;
			if (StrKit.notBlank(sBuffer.toString())) {
				sBuffer.append("&") ;
			}
			sBuffer.append(paramStrings[0] + "=" + paramStrings[1]);
		}
		return sBuffer.toString() ;
	}
	
	protected String parsingQueryState(String state) {
		return state.replace("~~", "&").replace(":", "=") ;
	}
}
