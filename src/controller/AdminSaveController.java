package controller;

import java.io.File;

import model.Channel;
import model.Chapter;
import model.Classify;
import model.Config;
import model.Novel;
import utils.DicUtil;
import utils.FileUtil;
import utils.ReadTxtUtil;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.upload.UploadFile;

import config.AdminInter;
import config.BaseController;

@Before(AdminInter.class)
public class AdminSaveController extends BaseController {
	
	public void upload() {
		String action = getPara("action");
		String route = "/uploadFile" ; //上传图片默认路径
		if ( StrKit.notBlank(action) && action.equals("config") ) {
			render("config.json");
			return ;
		}else if (action.equals("listimage")) { //查询路径
			return ;
		}else if (action.equals("uploadImages")) {
			route = "/uploadPic" ;
		}
		UploadFile file = getFile("upfile" , route );
		String fileName = file.getFileName() ;
		String fileType = fileName.substring(fileName.lastIndexOf("."));
		String newName = FileUtil.reName(file) ;
		String url = getCtx() + "/upload" + route + "/" + newName ;
		JSONObject jsonObject = new JSONObject() ;
		jsonObject.put("url", url) ;
		jsonObject.put("state", "SUCCESS") ;
		jsonObject.put("title", newName) ;
		jsonObject.put("original", file.getOriginalFileName());
		jsonObject.put("type", fileType) ;
		jsonObject.put("size", file.getFile().length() ) ;
		renderJson(jsonObject);
	}
	
	//上传图片
	public void pic() {
		String route = "/uploadPic" ; //上传图片默认路径
		UploadFile file = getFile("upfile" , route );
		String newName = FileUtil.reName(file) ;
		String url = "/upload" + route + "/" + newName ;
		JSONObject jsonObject = new JSONObject() ;
		jsonObject.put("url", url) ;
		jsonObject.put("state", "SUCCESS") ;
		renderJson(jsonObject);
	}
	
	//上传小说
	public void file() {
		String route = "/novels" ; //上传图片默认路径
		UploadFile file = getFile("upfile" , route );
		String fileName = file.getFileName() ;
		ReadTxtUtil.readNovel("/upload" + route + "/" , fileName ) ;
		JSONObject jsonObject = new JSONObject() ;
		jsonObject.put("url", "/upload" + route + "/" + fileName  ) ;
		jsonObject.put("state", "SUCCESS") ;
		renderJson(jsonObject);
	}
	
	public void novelPic() {
		String fileName = getPara("fileName") ;
		String picUrl = getPara("picUrl");
		//查询小说
		Novel novel = Novel.dao.findByNameInCache(ReadTxtUtil.filterNovelName(fileName)) ;
		if (null != novel) {
			novel.setPic(picUrl);
			novel.update();
			//清理缓存
			CacheKit.removeAll(DicUtil.CACHE_CHAPTER_NOVEL) ;
			Novel.dao.clearCache(novel.getId());
		}
		JSONObject jsonObject = new JSONObject() ;
		jsonObject.put("url", picUrl  ) ;
		jsonObject.put("state", "SUCCESS") ;
		renderJson(jsonObject);
	}
	
	public void classify() {
		Classify  classify = getModel(Classify.class , "e") ;
		renderJson(Classify.dao.save(classify));
	}
	
	public void novel() {
		renderJson(Novel.dao.save(getModel(Novel.class , "e")));
	}
	
	public void chapter() {
		renderJson(Chapter.dao.save(getModel(Chapter.class,"e")));
	}
	
	public void channel() {
		renderJson(Channel.dao.save(getModel(Channel.class,"e")));
	}
	
	//系统参数设置
	public void system() {
		renderJson(Config.dao.setValue(getModel(Config.class,"x"))) ;
	}
	
}
