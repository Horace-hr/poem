package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.jetty.server.UserIdentity;

import model.base.BaseRecharge;
import utils.DateUtils;
import utils.DicUtil;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;

import config.UserInter;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Recharge extends BaseRecharge<Recharge> {
	public static final Recharge dao = new Recharge();
	public static final int STATUS_PAYING = 0 ;
	public static final int STATUS_PAID = 1 ;
	public static final int PAYTYPE_ALIPAY = 1 ;
	public static final int PAYTYPE_WXPAY = 2 ;
	/*********************缓存存储*************************/
	//查询分页 - 仅限客户端查询充值记录时使用
	public Page<Record> getPage(Integer pageNum , String userId) {
		List<String> whereParams = new ArrayList<String>();
		String select = "SELECT tr.givings , tr.time ,tr.gets , tr.amount , tr.type " ;
		StringBuffer sBuffer = new StringBuffer(" FROM t_recharge tr ");
		sBuffer.append(" WHERE tr.status = 1 AND tr.userId = ? ");
		whereParams.add(userId) ;
		sBuffer.append(" ORDER BY tr.time DESC ");
		return Db.paginateByCache(DicUtil.CACHE_RECHARGE_RECORD, pageNum + userId , pageNum, 10, select, sBuffer.toString() , whereParams.toArray() ) ;
	}
	
	//查询用户充值记录，判断用户是否是已充值的用户 - 仅仅用于判断用户是否充值过
	public Recharge findRecord(String userId) {
		return this.findFirstByCache(DicUtil.CACHE_RECHARGE, userId, " SELECT id FROM t_recharge WHERE userId = ? AND status = 1 ", userId) ;
	}
	
	//充值记录，判断用户今日是否已充值
	public boolean notRechargedToday(String userId) {
		String date = DateUtils.getCurrentDate(new Date()) ;
		List<Recharge> list = this.findByCache(DicUtil.CACHE_RECHARGE_TODAY , userId + date , " SELECT id FROM t_recharge WHERE userId = ? AND status = 1 AND time LIKE ? ", userId , date + "%") ;
		return list.size() < 2 ? true : false ;
	}
	
	/*********************缓存清理*************************/
	@Before(Tx.class)
	public Recharge paid(String id , int amount , String transId , int payType) {
		Recharge recharge = Recharge.dao.findById(id);
		if (null == recharge || recharge.getAmount() != amount || recharge.getStatus() != Recharge.STATUS_PAYING) {
			return null ;
		}
		//更改用户信息
		User user = User.dao.findByIdInCache(recharge.getUserId());
		int coins = null == user.getCoins() ? 0 : user.getCoins() ; //当前读书币余额
		
		int gets = 0 ;
		int givings = 0 ;
		if (recharge.getType() == 1) { //充值
			gets = amount ; 
			givings = null == DicUtil.getMap().get(amount) ? 0 : DicUtil.getMap().get(amount);
			user.setCoins( coins + givings + gets ) ;
			user.setIsRecharged(true);
			user.setToken(User.dao.getToken(user));
			user.update();
			CacheKit.remove(DicUtil.CACHE_USER_INFO, user.getId()) ;
		}else{ //打赏
			Giving.dao.save(id , recharge.getUserId() , amount );
			user.setIsRecharged(true) ;
			user.update() ;
			CacheKit.remove(DicUtil.CACHE_USER_INFO, user.getId()) ;
		}
		
		//更改订单状态
		recharge.setStatus(Recharge.STATUS_PAID);
		recharge.setGets(gets);
		recharge.setGivings(givings);
		recharge.setStaticBalance(coins);
		recharge.setTransId(transId);
		recharge.setPayType(payType);
		recharge.setEndTime(new Date()) ;
		recharge.update();
		CacheKit.removeAll(DicUtil.CACHE_RECHARGE_RECORD);
		
		//保存充值统计数据
		Statistic.dao.saveInCache(recharge.getChannelId(), amount , recharge.getUserId() ) ;
		
		return recharge ;
	}
	
	/*********************普通业务*************************/
	//查询分页
	public Page<Record> getPage(Integer pageNum , Map<String, String> map) {
		List<String> whereParams = new ArrayList<String>();
		String select = "SELECT tr.* , tu.nickname " ;
		StringBuffer sBuffer = new StringBuffer(" FROM t_recharge tr ");
		sBuffer.append(" LEFT JOIN t_user tu ON tu.id = tr.userId "); 
		sBuffer.append(" WHERE tr.status = 1  ");
		this.addLikeCondition(sBuffer, whereParams , "tu.nickname", map.get("nickname"));
		this.addEqualCondition(sBuffer, whereParams , "tu.id", map.get("userId"));
		
		sBuffer.append(" ORDER BY tr.time DESC ");
		return Db.paginate(pageNum, DicUtil.PAGE_SIZE, select, sBuffer.toString() , whereParams.toArray() ) ;
	}
	
	
	//保存诗词充值记录
	
	public JSONObject save(String userId , int amount , int type ) {
		Recharge recharge = new Recharge();
		String id = getIds() ;
		recharge.setId(id) ;
		recharge.setNumber(1);
		recharge.setType(type);
		recharge.setStatus(0);
		recharge.setUserId(userId);
		recharge.setAmount(amount);
		recharge.setTime(new Date()) ;
		recharge.save() ;
		System.out.println("打印recharge："+recharge.toString());
		JSONObject jsonObject = sendSuccessJson() ;
		jsonObject.put("id", id) ;
		jsonObject.put("type", type) ;
		return jsonObject ;
	}
	
	
	
	
	//保存充值记录，如果chapters不为空，说明是从某本书的某个章节过来的充值
	/*public JSONObject save(String userId , int amount , String chapters , int type ) {
		Recharge recharge = new Recharge();
		Config config = Config.dao.findByKey(Config.KEY_DEVIDED_TYPE) ;
		int devidedType = config.getValue() ;
		User user = User.dao.findByIdInCache(userId) ;
		String channelId = user.getChannelId() ; //用户绑定的渠道id
		
		if (StrKit.notBlank(chapters)) {
			String[] arr = chapters.split("-") ;
			int number = Integer.parseInt(arr[0]);
			String novelId = arr[1] ;
			
			if (StrKit.notBlank(channelId)) {
				if (devidedType == Config.KEY_DEVIDED_MODLE_2) { //渠道增益，只要用户绑定了渠道，就算渠道收益
					recharge.setChannelId(channelId);
				}else{ //网站增益，只有该充值来源于渠道绑定的小说，才算该渠道的收益
					Channel channel = Channel.dao.findByIdInCache(channelId) ;
					if (null != channel && novelId.equals(channel.getNovelId())) {
						recharge.setChannelId(channelId);
					}
				}
			}
			recharge.setNumber(number);
			recharge.setNovelId(novelId);
		}else if (devidedType == Config.KEY_DEVIDED_MODLE_2 && StrKit.notBlank(channelId)) { //渠道增益模式下，用户绑定渠道算做该渠道的收益
			recharge.setChannelId(channelId) ;
		}
		
		String id = getIds() ;
		recharge.setId(id) ;
		recharge.setType(type);
		recharge.setStatus(0);
		recharge.setUserId(userId);
		recharge.setAmount(amount);
		recharge.setTime(new Date()) ;
		recharge.save() ;
		JSONObject jsonObject = sendSuccessJson() ;
		jsonObject.put("id", id) ;
		jsonObject.put("type", type) ;
		return jsonObject ;
	}*/
}

