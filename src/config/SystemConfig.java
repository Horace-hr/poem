package config;

import model.Novel;
import model.Statistic;
import model._MappingKit;
import Xss.XssHandler;

import org.apache.log4j.Logger;
import org.eclipse.jetty.util.log.Log;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.template.Engine;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;

import controller.AdminLoginController;
import controller.AlipayController;
import controller.IndexController;
import controller.InterController;
import controller.NovelController;
import controller.QiniuController;
import controller.WeixinApiController;
import controller.WxPayController;
import controller.ZPayController;

/**
 * API引导式配置
 */
public class SystemConfig extends JFinalConfig {
	
	private static Logger log = Logger.getLogger(SystemConfig.class);
	
	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		me.add("/", IndexController.class , "index");	// 第三个参数为该Controller的视图存放路径
		me.add("/novel",NovelController.class , "index");
		me.add("/pay" , WxPayController.class) ;
		me.add("/alipay" , AlipayController.class) ;
		me.add("/zpay" , ZPayController.class) ;
		me.add("/inter" , InterController.class) ; //ajax权限拦截
		
		me.add(new MobileRoutes());
		
		me.add("/cangshuge" , AdminLoginController.class) ;
		me.add(new AdminRoutes());
		
		me.add("/api" , WeixinApiController.class);
		me.add("/qiniu" , QiniuController.class) ;
	}
	
	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		// 加载少量必要配置，随后可用PropKit.get(...)获取值
		PropKit.use("a_little_config.properties");
		me.setDevMode(PropKit.getBoolean("devMode", false));
		
		BeetlRenderFac rf = new BeetlRenderFac();
		rf.config();
		me.setRenderFactory(rf);
		
		//根据gt可以添加扩展函数，格式化函数，共享变量等，
		//GroupTemplate gt = rf.groupTemplate;
		
		ApiConfigKit.setDevMode(me.getDevMode());
		ApiConfig ac = new ApiConfig();
		
		// 配置微信 API 相关常量
		ac.setToken(PropKit.get("token"));
		ac.setAppId(PropKit.get("appId"));
		ac.setAppSecret(PropKit.get("appSecret"));
		log.info("ac.getAppId()="+ac.getAppId());
		log.info("ac.setAppSecret()="+ac.getAppSecret());
		ApiConfigKit.setThreadLocalApiConfig(ac);
		
	}
	
	
	public static C3p0Plugin createC3p0Plugin() {
		return new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim(),PropKit.get("driverClass").trim());
	//	return new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
	}
	
	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		// 配置C3p0数据库连接池插件
		C3p0Plugin C3p0Plugin = createC3p0Plugin();
		me.add(C3p0Plugin);
		
		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(C3p0Plugin);
		me.add(arp);
		
		// 所有配置在 MappingKit 中搞定
		_MappingKit.mapping(arp);
		
		me.add(new EhCachePlugin());
		
		//定时任务
		Cron4jPlugin task = new Cron4jPlugin() ;
		task.addTask("* */1 * * *", new SaveReadTimesTask() ) ;
		me.add(task) ;
		
	}
	
	@Override
	public void beforeJFinalStop(){
		Novel.dao.saveReadTimes() ;
		Statistic.dao.saveAll() ;
	}
	
	@Override
	public void afterJFinalStart(){
		Statistic.dao.initData();
	}
	
	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		
	}
	
	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		me.add(new GlobalHandler());
		me.add(new XssHandler());
	}
	
	/**
	 * 建议使用 JFinal 手册推荐的方式启动项目
	 * 运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 */
	public static void main(String[] args) {
		JFinal.start("WebRoot", 80, "/", 5);
	}

	@Override
	public void configEngine(Engine arg0) {
		// TODO Auto-generated method stub
		
	}
}
