package config;

import utils.IpUtil;
import model.Channel;
import model.Statistic;
import model.User;

import java.lang.ProcessBuilder.Redirect;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.kit.IpKit;

public class UserInter implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		Controller con = inv.getController();
		String cid  = con.getCookie("cid");
		User user = User.dao.findByIdInCache(cid);
		String channelId = con.getPara("qd") ; //如果qd不为空，那么会绑定与用户的关系
		String ip = IpUtil.getRealIp(con.getRequest()) ;
		if (StrKit.isBlank(cid) || null == user) {
			con.removeCookie("cid") ;
			String userAgent = con.getRequest().getHeader("user-agent").toLowerCase();
			if(userAgent.indexOf("micromessenger") > -1){ //微信客户端
				//用户未注册 - 获取用户微信的openid
				System.out.println("微信客户端");
				System.out.println("/getCode?page=" + con.getPara("page") + "&params=" + con.getPara("params") + "&qd=" + channelId + "&isBase=true");
				con.redirect("/getCode?page=" + con.getPara("page") + "&params=" + con.getPara("params") + "&qd=" + channelId + "&isBase=true" ) ;
			}else{ //其他浏览器
				System.out.println("其他浏览器");
				String id = StrKit.getRandomUUID() ;
				String params = con.getPara("params") ;
				System.out.println("params="+params);
				if (StrKit.notBlank(params) && params.indexOf("-") > -1 && StrKit.notBlank(channelId)) {
					String[] arrString = params.split("-") ;
					User.dao.checkUser(id, channelId , arrString[1] , Integer.parseInt(arrString[0]) , ip ) ;
				}else{
					User.dao.checkUser(id, ip , channelId ) ;
				}
				con.setCookie("cid" , id , 30*24*60*60 ) ;
				inv.invoke();
				String focus = con.getPara("focus");
				User u = User.dao.findByIdInCache(id);
				if(focus != null){
					if(focus.equals("1") && (u.getFocus()==null || u.getFocus()==1)){
						User.dao.updateById(id,u.getCoins());
					}
				}
				Statistic.dao.saveInCache( id , ip , channelId) ;
			}
		}else if (StrKit.notBlank(channelId)) {
			String params = con.getPara("params") ;
			if (StrKit.notBlank(params) && params.indexOf("-") > -1) {
				String[] arrString = params.split("-") ;
				User.dao.saveChannel(cid, channelId , arrString[1] , Integer.parseInt(arrString[0]) );
			}
			//用户已标记、渠道id不为空时，继续
			inv.invoke();
			String focus = con.getPara("focus");
			User u = User.dao.findByIdInCache(cid);
			if(focus != null){
				if(focus.equals("1") && (u.getFocus()==null || u.getFocus()==1)){
					User.dao.updateById(cid,u.getCoins());
				}
			}
			Statistic.dao.saveInCache(cid , ip , channelId) ;
			
		}else {
			//用户已标记、渠道id为空时，继续
			inv.invoke();
			String focus = con.getPara("focus");
			User u = User.dao.findByIdInCache(cid);
			if(focus != null){
				if(focus.equals("1") && (u.getFocus()==null || u.getFocus()==1)){
					User.dao.updateById(cid,u.getCoins());
				}
			}
			Statistic.dao.saveInCache(cid , ip , channelId) ;
		}
	}
	
}
