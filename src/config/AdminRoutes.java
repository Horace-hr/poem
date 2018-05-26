package config;

import com.jfinal.config.Routes;

import controller.AdminDeleteController;
import controller.AdminInfoController;
import controller.AdminListController;
import controller.AdminSaveController;
import controller.AdminUpdateController;


public class AdminRoutes extends Routes {

	@Override
	public void config() {
		addInterceptor(new AdminInter());
		
		add("/admin/update" , AdminUpdateController.class);
		add("/admin/list" , AdminListController.class) ; 
		add("/admin/save" , AdminSaveController.class) ;
		add("/admin/delete" , AdminDeleteController.class);
		
		add("/admin/info" , AdminInfoController.class);
		
	}


}
