package utils;

import org.beetl.core.Context;
import org.beetl.core.Function;

import com.jfinal.kit.StrKit;

public class SpiteString implements Function {

	@Override
	public Object call(Object[] arg0, Context arg1) {
		String str = (String) arg0[0];
		if (StrKit.isBlank(str)) {
			return null ;
		}
		StringBuilder sBuilder = new StringBuilder(str);
		for( int i=4; i <= str.length(); i += 5 ){
			sBuilder.insert(i, ' ');
        }
		return sBuilder.toString();
	}
}
