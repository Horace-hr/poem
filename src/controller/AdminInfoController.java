package controller;

import java.util.Date;

import utils.DateUtils;
import model.Channel;
import model.Chapter;
import model.Classify;
import model.Config;
import model.Novel;
import model.Statistic;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

import config.AdminInter;
import config.BaseController;

@Before(AdminInter.class)
public class AdminInfoController extends BaseController {
	public void index() {
		renderNull() ;
	}
	
	//下载文件
	public void download() {
		renderFile("仙逝传说.txt");
	}
	
	//统计数据首页
	public void statistic() {
		//写回缓存
		Statistic.dao.saveAll() ;
		
		String today = DateUtils.getCurrentDate(new Date()) ;
		String yesterday = DateUtils.getYesterdayDate() ;
		setAttr("today", Statistic.dao.getRecord(today)) ; //今日数据
		setAttr("yesterday", Statistic.dao.getRecord(yesterday)) ; //昨日数据
		
		setAttr("todayList", Statistic.dao.getRecordList(today)) ; //今日明细数据
		setAttr("yesterdayList", Statistic.dao.getRecordList(yesterday)) ; //昨日明细数据
		
		setAttr("page", Statistic.dao.getPage(getParaToInt("pageNum" , 1), 7)) ;
		render("info_statistic.html");
	}
	
	//新增-修改小说信息
	public void novel() {
		setAttr("c", Novel.dao.findByIdInCache(getPara("id")));
		setAttr("list", Classify.dao.listAllInCache());
		render("info_novel.html");
	}
	
	//新增-修改章节
	public void chapter() {
		String id = getPara("id");//当前章节id
		Chapter chapter = Chapter.dao.findByIdInCache(id) ;
		String novelId = getPara("novelId") ;//小说id
		if (null != chapter) {
			novelId = chapter.getNovelId() ;
		}
		setAttr("c", chapter );
		setAttr("novel", Novel.dao.findByIdInCache(novelId));
		render("info_chapter.html");
	}
	
	//新增-修改渠道信息
	public void channel() {
		String id = getPara("id");//当前渠道id
		setAttr("c", Channel.dao.findByIdInCache(id));
		render("info_channel.html");
	}
	
	//搜索章节信息
	public void search() {
		renderPageJson(Chapter.dao.getPageInCache(getPara("title"))) ;
	}
	
	//系统参数设置
	public void system() {
		setAttr("price", Config.dao.findByKey(Config.KEY_PRICE)) ;
		setAttr("open", Config.dao.findByKey(Config.KEY_GET_OPEN_ID)) ;
		setAttr("devidedType", Config.dao.findByKey(Config.KEY_DEVIDED_TYPE)) ;
		render("info_system.html") ;
	}
	
}
