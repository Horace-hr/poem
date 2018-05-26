package config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;

import utils.WebContextUtil;

import com.jfinal.handler.Handler;
import com.jfinal.kit.StrKit;

/**
 * 全局Handler，设置一些通用功能
 */
public class GlobalHandler extends Handler {

	private static Logger log = Logger.getLogger(GlobalHandler.class);

	@Override
	public void handle(String target, HttpServletRequest request,HttpServletResponse response, boolean[] isHandled) {
		log.info("设置 web 路径");
		String ctx = WebContextUtil.getContextPath(request);
		String host = WebContextUtil.getEditorHost(request);
		String uri = request.getRequestURI();
		if (uri.contains(".jpg") || uri.contains(".jpeg") || uri.contains(".png") || uri.contains(".gif")) {
			String referer = request.getHeader("referer");
			if (StrKit.notBlank(referer) && !referer.contains(ctx)) {
				try {
					request.getRequestDispatcher("/images/pic.png").forward(request, response);
					isHandled[0] = true ; 
				} catch (ServletException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				request.setAttribute("ctx", ctx);
				request.setAttribute("version", "20170106_10");
				request.setAttribute("host", host);
				next.handle(target, request, response, isHandled);
			}
		}else {
			request.setAttribute("ctx", ctx);
			request.setAttribute("version", "20170106_10");
			request.setAttribute("host", host);
			next.handle(target, request, response, isHandled);
		}
	}
}
