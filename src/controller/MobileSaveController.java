package controller;

import model.BuyRecord;
import model.Comment;
import model.History;
import model.Novel;
import model.OriginalPoem;
import model.Recharge;
import model.SignIn;
import model.Statistic;
import model.User;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.jfinal.aop.Clear;

import config.BaseController;

public class MobileSaveController extends BaseController {
	
	//保存用户阅读历史记录
	public void history() {
		renderJson(History.dao.save(getUserCookie() , getPara("poemId") , 
				getParaToInt("chapterNum",1) ,getParaToBoolean("isFavorite",false) ));
	}
	
	//保存用户的原创诗词
		public void createPoem() {
			renderJson(OriginalPoem.dao.save(getModel(OriginalPoem.class,"x") , getUserCookie()));
		}
	
	
	//保存用户评论
	public void comment() {
		renderJson(Comment.dao.save(getModel(Comment.class,"x") , getUserCookie() ));
	}
	
	//保存用户购买记录
	public void buyRecord() {
		renderJson(enhance(BuyRecord.class).save(getPara("chapterId") , getUserCookie() ));
	}
	
	//保存用户签到记录，并奖励积分
	public void signIn() {
		renderJson(SignIn.dao.save(getUserCookie()));
	}
	
	//积分兑换读书币
	public void exchangeCoins() {
		renderJson(User.dao.exchangeCoins(getUserCookie()));
	}
	
	//充值订单
	public void recharge() {
		System.out.println("进入充值方法");
		System.out.println("getPara()----》"+getPara());
		int amount = getParaToInt("amount") ;
		//String chapters = getPara("chapterId") ; //用户章节信息，此处接收的是章节的number-novelId
		int type = getParaToInt("type",1);
		int payType = getParaToInt("payType" , 2 );
		String userId = getUserCookie() ;
		if (!User.dao.checkToken(User.dao.findByIdInCache(userId))) {
			renderHtml("账户信息异常，请联系管理员");
			return ;
		}
		JSONObject jsonObject = Recharge.dao.save(getUserCookie() , amount, type ) ;
		jsonObject.put("payType", payType) ;
		renderJson(jsonObject);
	}
	
	//统计小说阅读次数
	public void readTimes() {
		renderJson(Novel.dao.readTimes(getPara("id"))) ;
	}
	
	public void pv() {
		renderJson(Statistic.dao.savePvInCache(getUserCookie())) ;
	}
	
	
}
