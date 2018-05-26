package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import model.Recharge;
import model.User;
import utils.weixin.WeixinApiUtil;

import com.jfinal.aop.Before;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.api.PaymentApi.TradeType;
import com.jfinal.weixin.sdk.kit.IpKit;
import com.jfinal.weixin.sdk.kit.PaymentKit;

import config.BaseController;

public class WxPayController extends BaseController {
		Map<String, String> map=new HashMap<String, String>();
	    private static String notify_url = "/pay/getNotify";
	    
	    public void pre() {
			String orderId = getPara("id");
			String url = WeixinApiUtil.getCodeUrl( getCtx() + "/pay/" , orderId , true );
//			String url = WeixinApiUtil.getCodeUrl( "http://cps.lijitui.com/wx/pay/" , orderId , true );
			redirect(url) ;
		}

	    /**
	     * 公众号支付
	     */
	    public void index() {
	    	String code = getPara("code");
	    	String orderId = getPara("state");
	    	Recharge recharge = Recharge.dao.findById(orderId) ;
	    	int totalFee = recharge.getAmount();
	    	String openId = "";
			try {
				openId = WeixinApiUtil.getOpenId(code);
			} catch (IOException e) {
				e.printStackTrace();
				redirect("/novel?page=recharge") ;
				return ;
			}
			
			if (StrKit.isBlank(openId)) {
				renderHtml("当前获取到的微信id为空：" + openId);
				return ;
			}
			System.out.println("properties获取商户id---->"+PropKit.get("WeiXinShangHuId"));
	        // 统一下单文档地址：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1
	        String appid = PropKit.get("appId") ;
	        String paternerKey = PropKit.get("WeiXinShangHuPaySecret") ;

	        Map<String, String> params = new HashMap<String, String>();
	        params.put("appid", appid );
	        params.put("mch_id", PropKit.get("WeiXinShangHuId") );
	        params.put("body", "读书币");
	        params.put("out_trade_no", orderId );
	        params.put("total_fee", totalFee + "");
	        
	        String ip = IpKit.getRealIp(getRequest());
	        if (StrKit.isBlank(ip)) {
	            ip = "127.0.0.1";
	        }
	        ip = ip.replace("，", ",") ;
	        if (ip.indexOf(",") != -1) {
				ip = ip.split(",")[0] ;
			}

	        params.put("spbill_create_ip", ip);
	        params.put("trade_type", TradeType.JSAPI.name());
	        params.put("nonce_str", System.currentTimeMillis() / 1000 + "");
	        params.put("notify_url", getCtx() + notify_url );
	        params.put("openid", openId );

	        String sign = PaymentKit.createSign(params, paternerKey  );
	        System.out.println("获取sign--->"+sign);
	        params.put("sign", sign);
	        System.out.println("打印params"+params.toString());
	        String xmlResult = PaymentApi.pushOrder(params);

	        Map<String, String> result = PaymentKit.xmlToMap(xmlResult);

	        String return_code = result.get("return_code");
	        String return_msg = result.get("return_msg");
	        if (StrKit.isBlank(return_code) || !"SUCCESS".equals(return_code)) {
	            renderText(return_msg);
	            return;
	        }
	        String result_code = result.get("result_code");
	        if (StrKit.isBlank(result_code) || !"SUCCESS".equals(result_code)) {
	            renderText(return_msg);
	            return;
	        }
	        String prepay_id = result.get("prepay_id");

	        Map<String, String> packageParams = new HashMap<String, String>();
	        packageParams.put("appId", appid);
	        packageParams.put("timeStamp", System.currentTimeMillis() / 1000 + "");
	        packageParams.put("nonceStr", System.currentTimeMillis() + "");
	        packageParams.put("package", "prepay_id=" + prepay_id);
	        packageParams.put("signType", "MD5");
	        String packageSign = PaymentKit.createSign(packageParams, paternerKey);
	        packageParams.put("paySign", packageSign);
	        setAttr("finalpackage", packageParams);
	        setAttr("recharge", recharge);
	        render("pay.html");
	    }

	    /**
	     * 支付成功通知
	     */
	    @Before(Tx.class)
	    public void getNotify() {
	        // 支付结果通用通知文档: https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7
	        String xmlMsg = HttpKit.readData(getRequest());
	        System.out.println("支付通知="+xmlMsg);
	        Map<String, String> params = PaymentKit.xmlToMap(xmlMsg);

	        String result_code  = params.get("result_code");
	        // 总金额
	        String totalFee     = params.get("total_fee");
	        // 商户订单号
	        String orderNumber  = params.get("out_trade_no");
	        // 微信支付订单号
	        String transId      = params.get("transaction_id");
	        // 支付完成时间，格式为yyyyMMddHHmmss
	        String timeEnd      = params.get("time_end");

	        // 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态
	        // 避免已经成功、关闭、退款的订单被再次更新
	        String paternerKey = PropKit.get("WeiXinShangHuPaySecret") ;
	        if(PaymentKit.verifyNotify(params, paternerKey)){
	            if (("SUCCESS").equals(result_code)) {
	                //更新订单信息
	            	try {
	            		enhance(Recharge.class).paid( orderNumber , Integer.parseInt(totalFee)  , transId , Recharge.PAYTYPE_WXPAY );
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
	            	
	                System.out.println("更新订单信息");

	                Map<String, String> xml = new HashMap<String, String>();
	                xml.put("return_code", "SUCCESS");
	                xml.put("return_msg", "OK");
	                renderText(PaymentKit.toXml(xml));
	                return;
	            }
	        }
	        renderText("");
	    }
	
}
