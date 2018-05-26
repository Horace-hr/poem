package Xss;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.jfinal.kit.StrKit;

public class XssFilter {
	static final Whitelist myWhiteList = createContentWhitelist();
	static Whitelist createContentWhitelist() {
	return Whitelist.relaxed().removeProtocols("a", "href", "ftp", "http", "https", "mailto")
	.removeProtocols("img", "src", "http", "https")
	// 官方默认会将 target 给过滤掉
	.addAttributes("a", "href", "title", "target")
	// 在 Whitelist.relaxed() 之外添加额外的白名单规则
	.addTags("div", "span", "embed", "object", "param")
	.addAttributes(":all", "style", "class", "id", "name")
	.addAttributes("object", "width", "height", "classid", "codebase")
	.addAttributes("param", "name", "value")
	.addAttributes("embed", "src", "quality", "width", "height", "allowFullScreen",
			"allowScriptAccess", "flashvars", "name", "type", "pluginspage");
	}
	
	public static String filtrated(String name) {
		if (StrKit.isBlank(name)) {
			return name;
		//如果是json数组类型则手动设置
		}else if(name.startsWith("[{")&&name.endsWith("}]")){
			name=name.replace("SCRIPT", "").replace("script", "").replace(".","。").replace("<", "")
					.replace("FROM", "").replace("from", "").replace("UPDATE", "").replace("update", "").replace(" ", "");
			return name;
		}else{
			return Jsoup.clean( name ,  myWhiteList );
		}
	}
	
}
