package controller;

import java.util.Map;

import model.Channel;
import model.Chapter;
import model.Classify;
import model.History;
import model.Novel;
import model.Recharge;
import model.Statistic;
import model.User;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import config.AdminInter;
import config.BaseController;

@Before(AdminInter.class)
public class AdminListController extends BaseController {
	
	public void classify() {
		String id = getPara("id");//当前分类id
		String parentId = getPara("parentId") ;//父级分类id
		Classify classify = null ;
		Classify parent = null ;
		if (StrKit.notBlank(id)) {
			classify = Classify.dao.findByIdInCache(id);
		}
		if (StrKit.notBlank(parentId)) {
			parent = Classify.dao.findByIdInCache(parentId);
		}else if (null != classify) {
			parent = Classify.dao.findByIdInCache(classify.getParentId());
		}
		setAttr("c", classify );
		setAttr("parent", parent );
		setAttr("list", Classify.dao.listAllInCache());
		render("list_classify.html");
	}
	
	public void novel() {
		Map<String, String> map = getParaMaps() ;
		setAttr("page", Novel.dao.getPage(getParaToInt("pageNum" , 1), map ));
		setAttr("map", map);
		render("list_novel.html");
	}
	
	public void chapter() {
		Map<String, String> map = getParaMaps() ;
		setAttr("page", Chapter.dao.getPage(getParaToInt("pageNum" , 1), map ));
		setAttr("map", map);
		render("list_chapter.html");
	}
	
	public void channel() {
		Map<String, String> map = getParaMaps() ;
		setAttr("page", Channel.dao.getPage(getParaToInt("pageNum" , 1), map ));
		setAttr("map", map);
		render("list_channel.html");
	}
	
	public void recharge() {
		Map<String, String> map = getParaMaps() ;
		setAttr("page", Recharge.dao.getPage(getParaToInt("pageNum" , 1), map ));
		setAttr("map", map);
		render("list_recharge.html");
	}
	
	public void channelStatistic() {
		//写回缓存
		Statistic.dao.saveAll() ;
		Map<String, String> map = getParaMaps() ;
		setAttr("page", Channel.dao.getPage(getParaToInt("pageNum" , 1), 10 , map));
		setAttr("map", map);
		render("list_channelStatistic.html") ;
	}

}
