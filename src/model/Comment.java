package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import utils.DicUtil;

import model.base.BaseComment;

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
public class Comment extends BaseComment<Comment> {
	public static final Comment dao = new Comment();
	
	public JSONObject delete(String commentId , String userId) {
		User user = User.dao.findById(userId);
		Comment.dao.deleteById(commentId);
		return sendSuccessJson() ;
	}
	
	/************************创建缓存****************************/
	public Page<Record> page(Integer pageNumber , Integer pageSize , String novelId) {
		List<String> whereParams = new ArrayList<String>();
		String select = " SELECT tc.* , tc.id AS commentId , tu.nickname " ;
		StringBuffer sBuffer = new StringBuffer() ;
		sBuffer.append(" FROM t_comment tc ");
		sBuffer.append(" LEFT JOIN t_user tu ON tu.id = tc.userId ");
		sBuffer.append(" WHERE tc.novelId = ? ");
		whereParams.add(novelId);
		sBuffer.append(" ORDER BY tc.time DESC ");
		return Db.paginateByCache( DicUtil.CACHE_PAGE_COMMENT , novelId + pageNumber + pageSize , pageNumber, pageSize, select, sBuffer.toString() , whereParams.toArray() ) ;
	}
	
	/************************缓存清理****************************/
	public JSONObject save( Comment comment , String userId) {
		if (StrKit.isBlank(comment.getContent())) {
			return sendErrorJson("评论内容不能为空");
		}
		if (comment.getContent().length() > 200) {
			return sendErrorJson("评论内容太长了");
		}
		comment.setId(getIds());
		comment.setUserId(userId);
		comment.setTime(new Date());
		comment.save();
		//清理评论分页查询缓存
		CacheKit.removeAll(DicUtil.CACHE_PAGE_COMMENT) ;
		return sendSuccessJson(comment.getNovelId());
	}
}
