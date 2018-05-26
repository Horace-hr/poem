package Xss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;

/*
 * 此功能需要依赖jsoup.jar
 */
public class XssHandler extends Handler {

	@Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, boolean[] isHandled) {
		 	if (target.indexOf('.') == -1){
	            request = new HttpServletRequestWrapper(request);
	        }
	        next.handle(target, request, response, isHandled);
	}

}
