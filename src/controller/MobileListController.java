package controller;

import model.Classify;
import config.BaseController;

public class MobileListController extends BaseController {
	
	public void classify() {
		renderJson(Classify.dao.getJsonList());
	}
}
