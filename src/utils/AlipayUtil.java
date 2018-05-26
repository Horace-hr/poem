package utils;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.jfinal.kit.PropKit;

public class AlipayUtil {

	public static AlipayClient alipayClient = null ;
//	public static final String alipayUrl = "https://openapi.alipaydev.com/gateway.do" ;
	public static final String alipayUrl = "https://openapi.alipay.com/gateway.do" ;

	
	public static AlipayClient getClient() {
		if (null == alipayClient) {
			String appId = PropKit.get("AlipayAPPID");
			String priviteKey = PropKit.get("AlipayPrivateKey");
			String publicKey = PropKit.get("AlipayPublicKey");
			alipayClient = new DefaultAlipayClient( alipayUrl , appId , priviteKey ,"json" , "UTF-8" , publicKey );
		}
		return alipayClient ;
	}
}
