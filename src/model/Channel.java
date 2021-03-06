package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import model.base.BaseChannel;
import utils.DicUtil;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Channel extends BaseChannel<Channel> {
	public static final Channel dao = new Channel().dao();
	/*********************缓存存储*************************/
	//【缓存】查询渠道信息By - id 
	public Channel findByIdInCache(String id) {
		return this.findFirstByCache(DicUtil.CACHE_CHANNEL, id, "SELECT tc.* FROM t_channel tc WHERE tc.id = ? " , id );
	}
	
	//查询渠道数据统计信息
	public Page<Record> getPage(Integer pageNum , int pageSize  , Map<String, String> map ) {
		List<String> whereParams = new ArrayList<String>();
		String select = "SELECT tc.cName , SUM(IFNULL(ts.income,0)) AS income , SUM(IFNULL(ts.clicks,0)) AS clicks , SUM(IFNULL(ts.registers,0)) AS registers , SUM(IFNULL(ts.rechargers,0)) AS rechargers , SUM(IFNULL(ts.pv,0)) AS pv " ;
		StringBuffer sBuffer = new StringBuffer(" FROM t_channel tc  ");
		sBuffer.append(" LEFT JOIN t_statistic ts ON ts.channelId = tc.id ") ;
		sBuffer.append(" WHERE 1 = 1  ") ;
		this.addNotLessThanCondition(sBuffer, whereParams, "ts.date", map.get("startTime")) ;
		this.addNotMoreThanCondition(sBuffer, whereParams, "ts.date", map.get("endTime")) ;
		sBuffer.append(" GROUP BY tc.id ORDER BY SUM(ts.income) DESC ") ;
		return Db.paginate( pageNum, pageSize, select, sBuffer.toString() , whereParams.toArray() ) ;
	}
	
	/*********************缓存清理*************************/
	public String createId() {
		String sql = "SELECT id FROM t_channel WHERE id = ? " ;
		String id ;
		do {
			id = getIds().substring(0,6);
		} while (null != this.findFirst(sql , id));
		return id ;
	}
	
	public JSONObject save(Channel channel) {
		String id = channel.getId() ;
		if (StrKit.isBlank(id)) {
			id = createId() ;
			channel.setId(id);
			channel.setAddTime(new Date());
			channel.save();
		}else{
			channel.update();
			//清理缓存:查询渠道信息
			this.clearCache(id);
		}
		return sendSuccessJson() ;
	}
	
	public String filterChannelId(String channelId) {
		if (StrKit.notBlank(channelId) && channelId.indexOf("-") > -1 ) {
			return channelId.split("-")[0] ;
		}
		return channelId ;
	}
	
	//该方法用于自生成渠道链接时，监测渠道是否已存在
	public void save(String channelId , String novelId , int chapterNum) {
		Channel channel = Channel.dao.findByIdInCache(channelId);
		if (null == channel) {
			channel = new Channel() ;
			channel.setId(channelId) ;
			channel.setAddTime(new Date()) ;
			channel.setNovelId(novelId);
			channel.setNumber(chapterNum);
			channel.setCName("渠道：" + channelId ) ;
			List<Record> list = Chapter.dao.getJsonPageInCache(chapterNum , novelId ).getList() ;
			if (list.size() > 0) {
				Record chapter = list.get(0) ;
				channel.setNovelMsg("《" + chapter.getStr("nName") + "》 第" + chapterNum + "章 " + chapter.getStr("title")) ;
			}
			channel.save() ;
		}
	}
	
	public JSONObject delete(String id) {
		this.deleteById(id);
		this.clearCache(id);
		return sendSuccessJson() ;
	}
	
	public void clearCache(String id) {
		CacheKit.remove(DicUtil.CACHE_CHANNEL, id);
	}
	
	/*********************普通业务*************************/
	//查询分页
	public Page<Record> getPage(Integer pageNum , Map<String, String> map) {
		List<String> whereParams = new ArrayList<String>();
		StringBuffer select = new StringBuffer("SELECT tc.* " );
		StringBuffer sBuffer = new StringBuffer(" FROM t_channel tc ") ;
		sBuffer.append(" WHERE 1=1 ");
		this.addLikeCondition(sBuffer,whereParams, "tc.cName", map.get("cName"));
		sBuffer.append(" ORDER BY tc.addTime DESC ");
		return Db.paginate(pageNum, DicUtil.PAGE_SIZE, select.toString() , sBuffer.toString() , whereParams.toArray() ) ;
	}
	
}
