package controller;

import model.Chapter;
import model.Comment;
import model.History;
import model.Novel;
import model.OriginalPoem;
import model.Poem;
import model.PoemParagraph;
import model.Recharge;
import utils.DicUtil;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import config.BaseController;

public class MobilePageController extends BaseController {
	
	/*
	 * 主编推荐列表 - 完本精品列表
	 * 查询小说
	
	public void novel() {
		System.out.println("进入novel方法----》"+getParaMaps());
		renderPageJson(Novel.dao.getJsonPage(getParaToInt("pageNum",1) , getParaToInt("pageSize" , DicUtil.PAGE_SIZE) , getParaMaps() ));
	}
	 */
	
	public void novel() {
		renderPageJson(Poem.dao.getJsonPage(getParaToInt("pageNum",1) , getParaToInt("pageSize" , DicUtil.PAGE_SIZE) , getParaMaps() ));
	}
	
	/**
	 * 诗词详情页，根据诗词ID查询
	 */
	public void poemById() {
		renderPageJson(Poem.dao.findByIdInCache(getPara("poemId")));
	}

	
	/*
	 * 小说章节分页s
	 */
		
	public void chapter() {
		
		//查询小说章节分页
		Page<Record> page = Chapter.dao.getJsonPageInCache(getParaToInt("pageNum" , 1), getPara("novelId") ) ;
		//查询用户付费信息
		boolean needPay = Chapter.dao.needPay(page, getUserCookie()) ;
		if (needPay) {
			//renderPageJson(null);
			redirect("/inter?status=800&message=" + getParaToInt("pageNum" , 1) + "-" + getPara("novelId") );
		}else{
			renderPageJson(page) ;
		}
		//查询诗词分页
		
	}
	
	public void poemParagraph() {
		System.out.println("进入poemParagraph方法----》");
		//查询诗词分页
		Page<Record> page = PoemParagraph.dao.getJsonPageInCache(getParaToInt("pageNum" , 1), getPara("poemId") ) ;
		renderPageJson(page) ;
		
	}
	
	
	
	/*
	 * 小说章节分页 - 仅限查询基本信息  - 用于章节列表、章节基础信息查询
	 */
	public void chapters() {
		//查询小说章节分页
		Page<Record> page = Chapter.dao.getJsonPageInCache(getParaToInt("pageNum" , 1), getPara("novelId"),getParaToInt("pageSize",1) , getParaToInt("orderType",0)) ;
		renderPageJson(page) ;
	}
	
	//检索小说 - 根据小说标题
	public void chapterByTitle() {
		//查询小说章节分页
		Page<Record> page = Chapter.dao.getJsonPageInCache(getParaToInt("pageNum" , 1), getPara("novelId"),getParaToInt("pageSize",10) , getPara("title")) ;
		renderPageJson(page) ;
	}
	
	//评论分页
	public void comment() {
		renderPageJson(Comment.dao.page(getParaToInt("pageNum" ,1), getParaToInt("pageSize",1), getPara("novelId")));
	}
	
	//查询充值记录
	public void recharge() {
		renderPageJson(Recharge.dao.getPage(getParaToInt("pageNum" ,1) ,getUserCookie() ));
	}
	
	//查询我的书库、我的阅读记录
	public void history() {
		renderPageJson(History.dao.getPage(getParaToInt("pageNum" ,1) , getUserCookie() , getPara("isFavorite","0"))) ;
	}
	
	//查询我的原创诗词
	public void originalPoem() {
		System.out.println("进入查询我的原创诗词");
		renderPageJson(OriginalPoem.dao.getPage(getParaToInt("pageNum" ,1) , getUserCookie())) ;
	}
		
	
	//免费新手榜
	public void freeBooks() {
		renderPageJson(Novel.dao.freeBooksPageList(getParaToInt("pageNum" , 1))) ;
	}
	
	//本周人气榜
	public void maxReadTimes() {
		renderPageJson(Novel.dao.maxReadTimesPage(getParaToInt("pageNum" , 1 ))) ;
	}
	
	
}
