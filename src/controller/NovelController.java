package controller;

import com.jfinal.aop.Before;

import config.BaseController;
import config.UserInter;
import model.Property;
import model.User;

public class NovelController extends BaseController {
	
	@Before(UserInter.class)
	public void index() {
	String cid = getUserCookie();
		System.out.println("cid="+cid);
		Property p = null;
		if(null != cid){
			User user = User.dao.findByIdInCache(cid);
			System.out.println("p.getBlogerId()="+user.getBlogerId());
			if (null != user.getBlogerId()) {
				p = Property.dao.findByIdInCache(user.getBlogerId());
			}else{
				p = Property.dao.findByIdInCache(10001);
			}
		}else{
			p = Property.dao.findByIdInCache(10001);
		}
	    setAttr("cid",p);
		render("index.html");
	}

	
	@Before(UserInter.class)
	public void qr(){
		String cid = getUserCookie();
		System.out.println("cid="+cid);
		Property p = null;
		if(null != cid){
			User user = User.dao.findByIdInCache(cid);
			System.out.println("p.getBlogerId()="+user.getBlogerId());
			if (null != user.getBlogerId()) {
				p = Property.dao.findByIdInCache(user.getBlogerId());
			}else{
				p = Property.dao.findByIdInCache(10001);
			}
		}else{
			p = Property.dao.findByIdInCache(10001);
		}
			renderJson("property",p);
		
	}


	
}
