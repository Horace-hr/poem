package controller;

import model.Channel;
import model.Chapter;
import model.Classify;
import model.Novel;

import com.jfinal.aop.Before;

import config.AdminInter;
import config.BaseController;

@Before(AdminInter.class)
public class AdminDeleteController extends BaseController {
	
	public void classify() {
		renderJson(Classify.dao.delete(getPara("id")));
	}
	
	public void novel() {
		renderJson(Novel.dao.delete(getPara("id")));
	}
	
	public void chapter() {
		renderJson(Chapter.dao.delete(getPara("id")));
	}
	
	public void channel() {
		renderJson(Channel.dao.delete(getPara("id")));
	}
	
	
}
