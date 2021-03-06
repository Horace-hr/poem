package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.base.BaseNovel;
import utils.DateUtils;
import utils.DicUtil;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.template.expr.ast.Id;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Novel extends BaseNovel<Novel> {
	public static final Novel dao = new Novel().dao();
	
	/*********************缓存存储*************************/
	//主编推荐 - 完本精品 - 分类id
	public Page<Record> getJsonPage(Integer pageNum , Integer pageSize , Map<String, String> map) {
		List<String> whereParams = new ArrayList<String>();
		String select = "SELECT tn.* , tc.cName " ;
		StringBuffer sBuffer = new StringBuffer(" FROM t_novel tn ");
		sBuffer.append(" LEFT JOIN t_classify tc ON tc.id = tn.classifyId ");
		sBuffer.append(" WHERE tn.updateTime IS NOT NULL ");
		this.addEqualCondition(sBuffer, whereParams , "tn.isEnd", map.get("isEnd") , "99");
		this.addEqualCondition(sBuffer, whereParams ,"tn.classifyId", map.get("classifyId"),"99");
		String count = map.get("count") ;
		if (StrKit.notBlank(count)) {
			if (count.equals("1")) {
				this.addNotMoreThanCondition(sBuffer, whereParams, "tn.count", 500000 + "") ;
			}else if (count.equals("2")) {
				this.addNotLessThanCondition(sBuffer, whereParams, "tn.count", 500000 + "") ;
				this.addNotMoreThanCondition(sBuffer, whereParams, "tn.count", 1000000+"") ;
			}else if (count.equals("3")) {
				this.addNotLessThanCondition(sBuffer, whereParams, "tn.count", 1000000 + "") ;
			}
		}
		String novelName = map.get("nName") ;
		System.out.println("进入后台 nName="+novelName);
		
		if (StrKit.notBlank(novelName)) {
			sBuffer.append(" AND ( tn.nName LIKE ? OR tn.author LIKE ? )  ");
			whereParams.add("%" + novelName + "%") ;
			whereParams.add("%" + novelName + "%") ;
		}
		String orderType = StrKit.isBlank(map.get("orderType")) ? "0" : map.get("orderType") ;
		String isRecommend = map.get("isRecommend") ;
		if (StrKit.notBlank(isRecommend) && isRecommend.equals("10")) {
			this.addNotLessThanCondition(sBuffer, whereParams, "tn.recommendNum", isRecommend ) ;
			sBuffer.append(" ORDER BY tn.recommendNum DESC ");
		}else if (orderType.equals("1")) {
			sBuffer.append(" ORDER BY tn.updateTime DESC ");
		}else if (orderType.equals("2")) {
			sBuffer.append(" ORDER BY tn.income DESC ") ;
		}
		String key = pageNum + pageNum + mapToString(map) ;
		return Db.paginateByCache(DicUtil.CACHE_PAGE_NOVEL, key, pageNum, pageSize, select, sBuffer.toString() , whereParams.toArray() ) ;
	}
	
	//【缓存】查询小说信息By - id 
	public Novel findByIdInCache(String id) {
		return this.findFirstByCache(DicUtil.CACHE_NOVEL, id, "SELECT tn.*,tc.cName FROM t_novel tn LEFT JOIN t_classify tc ON tc.id = tn.classifyId WHERE tn.id = ? " , id );
	}
	
	public Novel findByNameInCache(String nName) {
		return this.findFirstByCache(DicUtil.CACHE_NOVEL, nName, " SELECT tn.id FROM t_novel tn WHERE tn.nName = ? " , nName ) ;
	}
	
	//缓存：查询免费新书榜 - 人气榜
	public Page<Record> freeBooksPageList(Integer pageNum) {
		String select = "SELECT tn.orderNum , tn.id , tn.pic , tn.desc , tn.nName " ;
		StringBuffer sBuffer = new StringBuffer(" FROM t_novel tn ") ;
		sBuffer.append(" WHERE tn.orderNum > 10 ") ;
		sBuffer.append(" ORDER BY tn.orderNum DESC , tn.updateTime DESC ") ;
		return Db.paginateByCache(DicUtil.CACHE_FREEBOOKS, pageNum, pageNum, 10, select, sBuffer.toString()) ;
	}
	
	//缓存：本周人气榜
	public Page<Record> maxReadTimesPage(Integer pageNum) {
		String select = "SELECT tn.pic , tn.id , tn.desc , tn.nName " ;
		StringBuffer sBuffer = new StringBuffer(" FROM t_novel tn ") ;
		sBuffer.append(" LEFT JOIN t_readTimes tr ON ( tr.novelId = tn.id AND tr.date = ? ) ") ;
		String date = DateUtils.getLastWeek() ;
		sBuffer.append(" WHERE tn.updateTime IS NOT NULL ") ;
		sBuffer.append(" GROUP BY tn.id ORDER BY COUNT(tr.id) DESC , tn.updateTime DESC ") ;
		return Db.paginateByCache(DicUtil.CACHE_MAX_READTIMES, pageNum + date, pageNum, 10, select, sBuffer.toString() , date ) ;
	}
	
	//缓存：统计小说的点击次数
	public JSONObject readTimes(String novelId) {
		int readTimes = (Integer) (null == CacheKit.get(DicUtil.CACHE_READTIMES, novelId) ? 0 : CacheKit.get(DicUtil.CACHE_READTIMES, novelId)) ;
		readTimes ++ ;
		CacheKit.put(DicUtil.CACHE_READTIMES, novelId, readTimes) ;
		return sendSuccessJson() ;
	}
	
	
	
	/************************缓存清理****************************/
	//调整新书榜的位置
	public JSONObject exchangePos(String id , String exId) {
		Novel novel = this.findByIdInCache(id) ; 
		Novel novel2 = this.findByIdInCache(exId) ;
		int novelOrderNum = novel.getOrderNum() ;
		novel.setOrderNum(novel2.getOrderNum());
		novel.update() ;
		novel2.setOrderNum(novelOrderNum) ;
		novel2.update() ;
		CacheKit.remove(DicUtil.CACHE_NOVEL, id);
		CacheKit.remove(DicUtil.CACHE_NOVEL, exId);
		CacheKit.removeAll(DicUtil.CACHE_FREEBOOKS) ;
		return sendSuccessJson() ;
	}
	
	//调整主编推荐的位置
	public JSONObject exchangeRecom(String id , String exId) {
		Novel novel = this.findByIdInCache(id) ; 
		Novel novel2 = this.findByIdInCache(exId) ;
		int novelOrderNum = novel.getRecommendNum() ;
		novel.setRecommendNum(novel2.getRecommendNum());
		novel.update() ;
		novel2.setRecommendNum(novelOrderNum) ;
		novel2.update() ;
		CacheKit.remove(DicUtil.CACHE_NOVEL, id);
		CacheKit.remove(DicUtil.CACHE_NOVEL, exId);
		CacheKit.removeAll(DicUtil.CACHE_FREEBOOKS) ;
		CacheKit.removeAll(DicUtil.CACHE_PAGE_NOVEL) ;
		return sendSuccessJson() ;
	}
	
	//从缓存取出小说的点击次数，存入数据库
	public void saveReadTimes() {
		List<String> keys =  CacheKit.getKeys(DicUtil.CACHE_READTIMES);
		String sunday = DateUtils.getLastWeek() ;
		for (String key : keys) {
			Readtimes.dao.save(key, sunday) ;
		}
	}
	
	//加入、移出免费新书榜
	public JSONObject freeBooks(String novelId) {
		Novel novel = this.findByIdInCache(novelId) ; 
		if (null == novel) {
			return sendErrorJson() ;
		}
		int orderNum = novel.getOrderNum() ;
		if (orderNum > 10 ) {
			novel.setOrderNum(0);
		}else {
			Page<Record> freeBooks = this.freeBooksPageList(1) ;
			orderNum = freeBooks.getList().size() > 0 ? freeBooks.getList().get(0).getInt("orderNum") + 1 : 11 ;
			novel.setOrderNum(orderNum) ;
		}
		novel.update() ;
		CacheKit.removeAll(DicUtil.CACHE_FREEBOOKS);
		CacheKit.remove(DicUtil.CACHE_NOVEL, novelId);
		return sendSuccessJson() ;
	}
	
	public String createNovelId() {
		String sql = "SELECT id FROM t_novel WHERE id = ? " ;
		String id ;
		do {
			id = getIds().substring(0,8);
		} while (null != this.findFirst(sql , id));
		return id ;
	}
	
	//保存小说信息
	public JSONObject save(Novel novel) {
		String id = novel.getId() ;
		if (StrKit.isBlank(id)) {
			id = this.createNovelId() ;
			novel.setCount(0);
			novel.setId(id);
			novel.save() ;
		}else{
			Novel exsitedNovel = this.findByIdInCache(id);
			int curFreeNum = exsitedNovel.getFreeNum() ;
			int newFreeNum = novel.getFreeNum() ;
			//重新设置小说章节的价格 
			if (curFreeNum > newFreeNum) {
				//当前免费5章，需要更改为免费2章，那么>2且<=5的变为应有的价格
				Config config = Config.dao.findByKey(Config.KEY_PRICE) ;
				List<Chapter> chapters = Chapter.dao.find("SELECT id , count FROM t_chapter WHERE number > ? AND number <= ? AND novelId = ? " , newFreeNum , curFreeNum , id ) ;
				for (Chapter chapter : chapters) {
					chapter.setPrice(Math.round( (chapter.getCount() * config.getValue()/1000) ));
					chapter.update();
					CacheKit.remove(DicUtil.CACHE_CHAPTER, chapter.getId());
				}
			}else if(curFreeNum < newFreeNum){
				//当前免费2章，需要更改为免费5章，那么>2且<=5的变为应有的价格
				String sql = "SELECT id FROM t_chapter WHERE number > ? AND number <= ? " ;
				List<Chapter> chapters = Chapter.dao.find(sql , curFreeNum , newFreeNum ) ;
				for (Chapter chapter : chapters) {
					chapter.setPrice(0);
					chapter.update();
					CacheKit.remove(DicUtil.CACHE_CHAPTER, chapter.getId());
				}
			}
			novel.update();
			//清理缓存
			CacheKit.removeAll(DicUtil.CACHE_CHAPTER_NOVEL) ;
		}
		this.clearCache(id);
		return sendSuccessJson(id) ;
	}
	
	public JSONObject complete(String id) {
		Novel novel = new Novel() ;
		novel.setId(id);
		novel.setIsEnd(true);
		novel.update();
		//清理缓存
		this.clearCache(id);
		return sendSuccessJson() ;
	}
	
	public JSONObject novelRecommend(String id , int recommendNum ) {
		Novel novel = new Novel() ;
		novel.setId(id);
		if (recommendNum > 0) {
			String sqlString = " SELECT MAX(recommendNum) FROM t_novel " ;
			int max = Db.queryInt(sqlString) ;
			if (max < 10) {
				max = 10 ;
			}
			max ++ ;
			novel.setRecommendNum(max);
		}else {
			novel.setRecommendNum(0);
		}
		novel.update();
		//清理缓存
		this.clearCache(id);
		return sendSuccessJson() ;
	}
	
	public JSONObject delete(String id) {
		Chapter.dao.deleteByNovelId(id);
		Novel.dao.deleteById(id);
		this.clearCache(id);
		return sendSuccessJson() ;
	}
	
	public void resetStartAndEnd(String novelId , int lastNum) {
		List<Chapter> chapters = Chapter.dao.findStartAndEnd(novelId, lastNum);
		Novel novel = this.findByIdInCache(novelId);
		if (chapters.size() == 0) {
			//将小说的更新时间置空。不会再前端显示了。
			novel.setUpdateTime(null);
			novel.setLChapterId(null);
			novel.setLChapterNum(0);
			novel.setLChapterTitle(null);
			novel.setFChapterId(null);
			novel.update();
			return ;
		}
		Chapter fChapter = chapters.get(0);
		Chapter lChapter = chapters.get(0);
		
		if (chapters.size() == 2) {
			lChapter = chapters.get(1);
		}
		novel.setFChapterId(fChapter.getId());
		novel.setLChapterId(lChapter.getId());
		novel.setLChapterNum(lChapter.getNumber());
		novel.setLChapterTitle(lChapter.getTitle());
		novel.setUpdateTime(lChapter.getTime());
		novel.update();
		//清理小说查询缓存
		this.clearCache(novelId);
	}
	
	public void clearCache(String id) {
		CacheKit.remove(DicUtil.CACHE_NOVEL, id);
		CacheKit.removeAll(DicUtil.CACHE_PAGE_NOVEL);
	}
	
	
	/*********************普通业务*************************/
	//查询分页
	public Page<Record> getPage(Integer pageNum , Map<String, String> map) {
		List<String> whereParams = new ArrayList<String>();
		String select = "SELECT tn.* , tc.cName " ;
		StringBuffer sBuffer = new StringBuffer(" FROM t_novel tn ");
		sBuffer.append(" LEFT JOIN t_classify tc ON tc.id = tn.classifyId "); 
		sBuffer.append(" WHERE 1=1 ");
		this.addEqualCondition(sBuffer,whereParams, "tn.isEnd", map.get("isEnd") , "99");
		this.addLikeCondition(sBuffer,whereParams, "tn.nName", map.get("nName"));
		String free = StrKit.isBlank(map.get("freeBooks")) ? "99" : map.get("freeBooks");
		
		String ordersqlString = " ORDER BY tn.updateTime DESC " ;
		String orderType = StrKit.isBlank(map.get("orderType")) ? "0" : map.get("orderType");
		if (orderType.equals("1")) {
			ordersqlString = " ORDER BY tn.income DESC  " ;
		}else if (orderType.equals("2")) {
			ordersqlString = " ORDER BY tn.exIncome DESC " ;
		}else if (orderType.equals("3")) {
			ordersqlString = " ORDER BY tn.price DESC " ;
		}else if (orderType.equals("4")) {
			ordersqlString = " ORDER BY tn.freeNum DESC " ;
		}else if (orderType.equals("0")) {
			ordersqlString = " ORDER BY tn.updateTime DESC " ;
		}
		
		if ("1".equals(free)) {
			sBuffer.append(" AND tn.orderNum > 10 ") ;
			ordersqlString = " ORDER BY tn.orderNum DESC , tn.recommendNum DESC " ;
		}else if("2".equals(free)) {
			sBuffer.append(" AND tn.recommendNum > 10 ") ;
			ordersqlString = " ORDER BY tn.recommendNum DESC , tn.orderNum DESC " ;
		}
		
		sBuffer.append(ordersqlString);
		return Db.paginate(pageNum, DicUtil.PAGE_SIZE, select, sBuffer.toString() , whereParams.toArray() ) ;
	}
	
	
	
	
	
}
