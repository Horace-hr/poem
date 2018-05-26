package Xss;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

public class HttpServletRequestWrapper extends
		javax.servlet.http.HttpServletRequestWrapper {

	public HttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	/**
	 * 重写并过滤getParameter方法
	 */
	@Override
	public String getParameter(String name) {
		return XssFilter.filtrated(super.getParameter(name));
	}

	/**
	 * 重写并过滤getParameterValues方法
	 */
	@Override
	public String[] getParameterValues(String name) {
		String[] values = super.getParameterValues(name);
		if (null == values) {
			return null;
		}
		for (int i = 0; i < values.length; i++) {
			values[i] = XssFilter.filtrated(values[i]);
		}
		return values;
	}

	/**
	 * 重写并过滤getParameterMap方法
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map getParameterMap() {
		Map<String, String[]> result = new HashMap<String, String[]>();
		Map paraMap = super.getParameterMap();
		if (null == paraMap || paraMap.isEmpty()) {
			return paraMap;
		}
		Set<Entry<String, String>> entrys = paraMap.entrySet();
		for (Entry<String, String> entry : entrys) {
			String key = entry.getKey();
			String[] values = (String[]) paraMap.get(key);
			if (null == values) {
				continue;
			}
			String[] newValues = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				newValues[i] = XssFilter.filtrated(values[i]);
			}
			result.put(key, newValues);
		}
		return result;
	}

}
