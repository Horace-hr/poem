package controller;

import model.Giving;
import model.Recharge;
import model.User;

import com.jfinal.kit.StrKit;

import config.BaseController;

public class MobileUpdateController extends BaseController {
	//测试接口
	public void index() {
		Recharge recharge = Recharge.dao.findById(getPara("id")) ;
		Recharge.dao.paid(recharge.getId(), recharge.getAmount(), "testid"  , 3 ) ;
		String novelId = recharge.getNovelId() ;
		Integer number = recharge.getNumber() ;
		int type = recharge.getType() ;
		if (type == 2) {
			redirect("/novel?page=novelInfo&params=" + novelId ) ;
		}else{
			if (StrKit.notBlank(novelId) && null != number ) {
				redirect("/novel?page=chapterInfo&params=" + number + "-" + novelId ) ;
			}else{
				redirect("/novel?page=rechargeRecord") ;
			}
		}
	}
	
	//领取赠送的金币
	public void givings() {
		renderJson(Giving.dao.finished(getUserCookie() , getPara("novelId") , getParaToInt("type",1)));
	}
	
	//领取新手礼包
	public void gift() {
		renderJson(User.dao.changeNewUserStatus(getUserCookie())) ;
	}
	
}
