package controller;

import model.Novel;
import config.BaseController;

public class AdminUpdateController extends BaseController {
	
	public void novelComplete() {
		renderJson(Novel.dao.complete(getPara("id")));
	}
	
	public void novelRecommend() {
		renderJson(Novel.dao.novelRecommend(getPara("id") , getParaToInt("recommend",0) ));
	}

	//免费新书榜
	public void freeBooks() {
		renderJson(Novel.dao.freeBooks(getPara("id"))) ;
	}
	
	//调整免费新书的位置
	public void exchangePos() {
		renderJson(Novel.dao.exchangePos(getPara("id") , getPara("exId")) ) ;
	}
	
	//调整主编推荐的位置
	public void exchangeRecom() {
		renderJson(Novel.dao.exchangeRecom(getPara("id") , getPara("exId")) ) ;
	}
	
}
