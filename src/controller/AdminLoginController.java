package controller;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.captcha.CaptchaRender;
import com.jfinal.kit.StrKit;

import model.Admin;
import utils.WebContextUtil;
import config.AdminInter;
import config.BaseController;

public class AdminLoginController extends BaseController {
	
	//登录页面 - 已登录时，进入管理页面
	public void index() {
		String session = this.getAdminId() ;
		if (StrKit.notBlank(session)) {
			this.redirect("/admin/info/statistic");
		}else {
			this.render("login_index.html");
		}
	}
	
	//登录验证
	public void check() {
		this.renderJson(Admin.dao.check(this.getModel(Admin.class , "x") , getParaToBoolean("keepStatus" , false ) , getPara("code") , this )) ;
	}
	
	//验证码
	public void captcha() {
		render(new CaptchaRender());
	}
	
	//修改密码页面
	public void modifyPsw() {
		setAttr("uName", getPara("uName"));
		render("login_modifyPsw.html");
	}
	
	//修改密码验证
	public void resetPsw() {
		renderJson(Admin.dao.resetPsw(getPara("newPsw") , getPara("rePsw") , getPara("code") , getModel(Admin.class , "x") , this ));
	}
	
	//退出登录
	@Before(AdminInter.class)
	public void quit() {
		removeCookie(Admin.ADMIN_STATUS);
		removeSessionAttr(Admin.ADMIN_STATUS);
		redirect("/cangshuge");
	}
}
