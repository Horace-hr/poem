package controller;

import com.jfinal.aop.Clear;

import utils.QiniuUtil;
import config.BaseController;

/**
 * 服务费用
 * 
 * @author lishunfeng
 * 
 */
@Clear
public class QiniuController extends BaseController {
	/**
	 * 生成Token
	 */
	public void index() {
		renderJson("{\"uptoken\":\""+QiniuUtil.getUpToken0()+"\"}");
	}
}
