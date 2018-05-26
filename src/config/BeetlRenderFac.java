package config;

import java.io.IOException;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.WebAppResourceLoader;

import com.jfinal.kit.PathKit;
import com.jfinal.render.Render;
import com.jfinal.render.RenderFactory;

public class BeetlRenderFac extends RenderFactory {
public  GroupTemplate groupTemplate = null;
	
	public Render getRender(String view) {
		return new NewBeetlRender(groupTemplate,view);
	}
	
	public void config(){

		if (groupTemplate != null){
			groupTemplate.close();
		}
		String root = PathKit.getWebRootPath();
		WebAppResourceLoader resourceLoader = new WebAppResourceLoader(root);
		
		try{

			Configuration cfg = Configuration.defaultConfiguration();
			groupTemplate = new GroupTemplate(resourceLoader, cfg);
		}
		catch (IOException e){
			throw new RuntimeException("加载GroupTemplate失败", e);
		}
	}
}
