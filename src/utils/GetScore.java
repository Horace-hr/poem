package utils;

import model.Config;

import org.beetl.core.Context;
import org.beetl.core.Function;

public class GetScore implements Function{

	@Override
	public Object call(Object[] arg0, Context arg1) {
		String keyString = (String) arg0[0];
		Config config = Config.dao.findByKey(keyString);
		return config.getValue();
	}

}
