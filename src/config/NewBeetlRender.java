package config;

import org.beetl.core.GroupTemplate;
import org.beetl.ext.web.WebRender;

import com.jfinal.render.Render;

public class NewBeetlRender extends Render {

	GroupTemplate groupTemplate;
	public NewBeetlRender( GroupTemplate groupTemplate,String view){
		super();
		super.setView(view);
		this.groupTemplate = groupTemplate;
	}
	
	@Override
	public void render() {
		response.setContentType("text/html; charset=" + this.getEncoding());
		WebRender web = new WebRender(groupTemplate);
		web.render(this.view, this.request, this.response, null);

	}
	
}
