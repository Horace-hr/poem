package controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import model.Recharge;
import utils.AlipayUtil;
import utils.WebContextUtil;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;

import config.BaseController;

public class AlipayController extends BaseController {
	
	public void index() throws AlipayApiException {
		//获取交易订单号
		String orderId = getPara("id");
		
		//创建API对应的request
		AlipayClient alipayClient = AlipayUtil.getClient();
	    AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
	    
	    //设置请求参数
	    String ctx = getCtx() ;
	    alipayRequest.setReturnUrl(ctx + "/alipay/dealResult");
	    alipayRequest.setNotifyUrl(ctx + "/alipay/getNotify"); //在公共参数中设置回跳和通知地址
	    JSONObject jsonObject = new JSONObject();
	    jsonObject.put("out_trade_no", orderId );
	    jsonObject.put("total_amount", Recharge.dao.findById(orderId).getAmount()/100 );
	    jsonObject.put("subject", "书币充值");
	    jsonObject.put("seller_id", PropKit.get("AlipayPID"));
	    alipayRequest.setBizContent(jsonObject.toJSONString());//填充业务参数
	    
	    //发起请求
	    String form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
	    renderHtml(form);
	}
	
	public void dealResult() throws NumberFormatException, ParseException, AlipayApiException {
		Map<String, String> paramsMap = this.getParaMaps() ;
		System.out.println( "支付宝支付成功通知参数：" + paramsMap);
		boolean signVerified = AlipaySignature.rsaCheckV1(paramsMap, PropKit.get("AlipayPublicKey") , "UTF-8"); //调用SDK验证签名
		if(signVerified){
			//获取支付宝交易号
			String trade_no = paramsMap.get("trade_no");
			
			//查询支付结果
			AlipayClient alipayClient = AlipayUtil.getClient();
			AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();//创建API对应的request类
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("trade_no", trade_no);
			request.setBizContent(jsonObject.toJSONString());//设置业务参数
			AlipayTradeQueryResponse response = alipayClient.execute(request);//通过alipayClient调用API，获得对应的response类
			
			//根据支付结果进行处理
			String out_trade_no = response.getOutTradeNo() ;
			String amount = response.getTotalAmount();
			BigDecimal aDecimal = new BigDecimal(amount) ;
			aDecimal = aDecimal.multiply(new BigDecimal(100)) ;
			
			String status = response.getTradeStatus();
			if (status.equals("TRADE_SUCCESS") || status.equals("TRADE_FINISHED")) {
				Recharge recharge = enhance(Recharge.class).paid( out_trade_no , aDecimal.intValue()  , trade_no , Recharge.PAYTYPE_ALIPAY );
				if (null == recharge) { //支付失败
					redirect("/novel?page=rechargeRecord") ;
				}else {
					String novelId = recharge.getNovelId() ;
					Integer number = recharge.getNumber() ;
					int type = recharge.getType() ;
					if (type == 2) {
						redirect("/novel?page=novelInfo&params=" + novelId ) ;
					}else{
						if (StrKit.notBlank(novelId) && null != number ) {
							redirect("/novel?page=chapterInfo&params=" + number + "-" + novelId ) ;
						}else{
							redirect("/novel?page=rechargeRecord") ;
						}
					}
				}
				
			}else {
				renderText("fail");
			}
		}else{
			renderText("fail");
		}
	}
	
	public void getNotify() throws AlipayApiException, NumberFormatException, ParseException {
		Map<String, String> paramsMap = this.getParaMaps() ;
		
		boolean signVerified = AlipaySignature.rsaCheckV1(paramsMap, PropKit.get("AlipayPublicKey") , "UTF-8"); //调用SDK验证签名
		if(signVerified){
			//获取支付宝交易号
			String trade_no = paramsMap.get("trade_no");
			
			//查询支付结果
			AlipayClient alipayClient = AlipayUtil.getClient();
			AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();//创建API对应的request类
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("trade_no", trade_no);
			request.setBizContent(jsonObject.toJSONString());//设置业务参数
			AlipayTradeQueryResponse response = alipayClient.execute(request);//通过alipayClient调用API，获得对应的response类
			
			//根据支付结果进行处理
			String out_trade_no = response.getOutTradeNo() ;
			String amount = response.getTotalAmount();
			BigDecimal aDecimal = new BigDecimal(amount) ;
			aDecimal = aDecimal.multiply(new BigDecimal(100)) ;
			String status = response.getTradeStatus();
			if (status.equals("TRADE_SUCCESS") || status.equals("TRADE_FINISHED")) {
				Recharge recharge = enhance(Recharge.class).paid( out_trade_no , aDecimal.intValue()  , trade_no , Recharge.PAYTYPE_ALIPAY );
				if (null == recharge) {
					renderText("fail");
				}else{
					renderText("success");
				}
			}else {
				renderText("fail");
			}
		}else{
			renderText("fail");
		}
	}
	
	public static void main(String[] args) {
		String amount = "100.10";
		BigDecimal aDecimal = new BigDecimal(amount) ;
		aDecimal = aDecimal.multiply(new BigDecimal(100)) ;
		System.out.println(aDecimal.intValue());
	}
}
