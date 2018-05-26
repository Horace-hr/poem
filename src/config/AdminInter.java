package config;

import model.Admin;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

public class AdminInter implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		String userId = controller.getCookie(Admin.ADMIN_STATUS);
		userId = (String) (StrKit.isBlank(userId) ? controller.getSessionAttr(Admin.ADMIN_STATUS) : userId) ;
		if (StrKit.isBlank(userId)) {
			controller.redirect("/cangshuge");
		}else{
			inv.invoke(); 
		}
	}

}
