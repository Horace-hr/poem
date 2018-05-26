package controller;

import utils.DicUtil;

import com.jfinal.plugin.ehcache.CacheKit;

import model.History;
import model.Novel;
import config.BaseController;

public class MobileDeleteController extends BaseController {
	
	//删除阅读记录
	public void history() {
		History history = History.dao.findById(getPara("id"));
		if (null == history) {
			renderErrorJson("操作过于频繁，请稍后再试");
		}else{
			String novelId = history.getNovelId() ;
			history.delete() ;
			Novel novel = Novel.dao.findByIdInCache(novelId);
			int collectNum = novel.getCollectNum() ;
			if (collectNum > 0) {
				collectNum -- ;
			}else {
				collectNum = 0 ;
			}
			novel.setCollectNum( collectNum );
			novel.update() ;
			CacheKit.remove(DicUtil.CACHE_NOVEL, novelId) ;
			CacheKit.remove(DicUtil.CACHE_HISTORY, getUserCookie() + novelId ) ;
			CacheKit.remove(DicUtil.CACHE_HISTORY_LASTEST , history.getUserId() ) ;
			renderSuccessJson(novelId);
		}
	}
	
	//退出登录
	public void userCookie() {
		this.removeCookie("cid") ;
		this.renderSuccessJson() ;
	}
	
}
