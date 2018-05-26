package utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class WebContextUtil {
	/**
	 * 获取上下文URL全路径
	 * 
	 * @param request
	 * @author lishunfeng
	 * @return
	 */
	public static String getContextPath(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		int port = request.getServerPort();
		if(80==port){
			sb.append(request.getScheme()).append("://").append(request.getServerName()).append(request.getContextPath());
		}else{
			sb.append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(port).append(request.getContextPath());
		}

		String path = sb.toString();
		return path;
	}
	
	public static String getFullPath(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		int port = request.getServerPort();
		if(80==port){
			sb.append(request.getScheme()).append("://").append(request.getServerName()).append(request.getContextPath());
		}else{
			sb.append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(port).append(request.getContextPath());
		}
		sb.append(request.getServletPath() + request.getQueryString());
		String path = sb.toString();
		return path;
	}
	
	/**
	 * 获取上本机地址
	 * 
	 * @param request
	 * @author lishunfeng
	 * @return
	 */
	public static String getEditorHost(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(request.getServerPort());
		String path = sb.toString();
		return path;
	}
	/**
	 *註銷session
	 * @param request
	 */
	public static void logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}
	
	/**
	 * 获取访问者IP
	 * @param request
	 * @return
	 */
	
	public static String getClientIpAddr(HttpServletRequest request) {
		//如果通过了多级反向代理的话
		String ip = request.getHeader("X-Forwarded-For");  
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		    ip = request.getHeader("Proxy-Client-IP");  
		}  
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		    ip = request.getHeader("WL-Proxy-Client-IP");  
		}  
		if (ip == null || ip.length() == 0  || "unknown".equalsIgnoreCase(ip)) {  
		    ip = request.getHeader("HTTP_CLIENT_IP");  
		}  
		if (ip == null || ip.length() == 0  || "unknown".equalsIgnoreCase(ip)) {  
		    ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
		}  
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		    ip = request.getRemoteAddr();  
		}  
        return ip;
    }
	
}
