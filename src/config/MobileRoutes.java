package config;

import com.jfinal.config.Routes;

import controller.MobileDeleteController;
import controller.MobileInfoController;
import controller.MobileListController;
import controller.MobilePageController;
import controller.MobileSaveController;
import controller.MobileUpdateController;
import controller.NovelController;

public class MobileRoutes extends Routes {

	@Override
	public void config() {
		//addInterceptor(new AccessInter());

		add("/list" , MobileListController.class) ;
		add("/info" , MobileInfoController.class);
		add("/save" , MobileSaveController.class) ;
		add("/update" , MobileUpdateController.class) ;
		add("/delete" , MobileDeleteController.class) ;
		add("/page" , MobilePageController.class);
		add("/property" , NovelController.class);
	}

}
