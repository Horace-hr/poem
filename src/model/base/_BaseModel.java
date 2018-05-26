package model.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import utils.StringUtil;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;

@SuppressWarnings("serial")
public abstract class _BaseModel<M extends Model<M>> extends Model<M> {
	
	/*
	 * 判断此参数不为空时，数据库字段【等于】非空的值
	 * @param 拼接字符串对象
	 * @param sql中的字段名
	 * @param 查询条件的值
	 */
	protected void addEqualCondition(StringBuffer sb ,  List<String> whereParams , String sqlKey , String sqlValues) {
		if (StrKit.notBlank(sqlValues)) {
			sb.append(" AND "+ sqlKey + " =  ? ");
			whereParams.add(sqlValues);
		}
	}
	
	/*
	 * 判断此参数不为空，且不等于第四个参数【allValues】时时，数据库字段【等于】非空的值
	 * @param 拼接字符串对象
	 * @param sql中的字段名
	 * @param 查询条件的值
	 */
	protected void addEqualCondition(StringBuffer sb ,  List<String> whereParams , String sqlKey , String sqlValues , String allValues) {
		if (StrKit.notBlank(sqlValues) && !sqlValues.equals(allValues)) {
			sb.append(" AND "+ sqlKey + " =  ? ");
			whereParams.add(sqlValues);
		}
	}
	
	/*
	 * 判断此参数不为空时，数据库字段等于非空的值,此处为【模糊查询】
	 * @param 拼接字符串对象
	 * @param sql中的字段名
	 * @param 查询条件的值
	 */
	protected void addLikeCondition(StringBuffer sb , List<String> whereParams ,  String sqlKey , String sqlValues) {
		if (StrKit.notBlank(sqlValues)) {
			sb.append(" AND "+ sqlKey + " LIKE ? ");
			whereParams.add("%"+sqlValues+"%");
		}
	}
	
	/*
	 * 判断此参数不为空时，数据库字段【大于等于】非空的值
	 * @param 拼接字符串对象
	 * @param sql中的字段名
	 * @param 查询条件的值
	 */
	protected void addNotLessThanCondition(StringBuffer sb ,  List<String> whereParams , String sqlKey , String sqlValues) {
		if (StrKit.notBlank(sqlValues)) {
			sb.append(" AND "+ sqlKey + " >= ? ");
			whereParams.add(sqlValues);
		}
	}
	
	/*
	 * 判断此参数不为空时，数据库字段【小于等于】非空的值
	 * @param 拼接字符串对象
	 * @param sql中的字段名
	 * @param 查询条件的值
	 */
	protected void addNotMoreThanCondition(StringBuffer sb , List<String> whereParams ,  String sqlKey , String sqlValues) {
		if (StrKit.notBlank(sqlValues)) {
			sb.append(" AND "+ sqlKey + " <= ? ");
			whereParams.add(sqlValues);
		}
	}
	
	/*
	 * 当从页面获取多个筛选值时，进行拼接
	 * @param 拼接字符串对象
	 * @param sql中的字段名
	 * @param 判断是否为空时筛选的数组
	 */
	protected void addMultipleEqualConditions(StringBuffer sb , List<String> whereParams , String sqlkey , String[] sqlValues) {
		if (sqlValues != null && sqlValues.length > 0) {
			if (sqlValues.length == 1) {
				sb.append(" AND "+ sqlkey +" = ? ");
				whereParams.add(sqlValues[0]);
			}else{
				for (int i = 0; i < sqlValues.length; i++) {
					if (i==0) {
						sb.append(" AND ( "+sqlkey+" = ? ");
						whereParams.add(sqlValues[i]);
					}else if ( i < sqlValues.length -1) {
						sb.append(" OR "+sqlkey+" = ?  ");
						whereParams.add(sqlValues[i]);
					}else{
						sb.append(" OR "+sqlkey+" = ? ) ");
						whereParams.add(sqlValues[i]);
					}
				}
			}
		}
	}
	
	
	/**
	 * 多个列名匹配查询
	 * @param sb
	 * @param sqlval 值
	 * @param kes 列名
	 */
	protected void addMultipleEqualConditionsForMorSingleKey(StringBuffer sb, List<String> whereParams , String key,String...vals) {
		if(vals==null||vals.length<1){
			return;
		}
		sb.append(" AND (");
		for (int i = 0; i < vals.length; i++) {
			sb.append((i>0?" OR ":"")+key+" = ?");
			whereParams.add(vals[i]);
		}
		sb.append(" )");
	}
	
	// ---------------------------------- <<< SQL METHOED BEGIN >>> ----------------------------------
	
	/**
	 * 根据填充的值查询相应的LIST
	 * @return
	 */
	public List<M> list(){
		String sql = "SELECT * " + getQuerySql();
		//Object[] params = this.whereParams.toArray();
		return find(sql);
	}
	
	/**
	 * 根据填充的值查询相应的LIST
	 * @return
	 */
//	public List<M> listByCache(){
//		String sql =  "SELECT * " +getQuerySql();
//		Object[] params = whereParams.toArray();
//		String tableName = this.getTable().getName();
//		String cacheName = tableName;
//		String keyName = "";
//		return this.findByCache(cacheName, keyName, sql, params);
//	}
	
	/**
	 * 分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
//	public Page<M> list(Integer pageNumber,int pageSize){
//		String whereSql = getQuerySql();
//		Object[] params = this.whereParams.toArray();
//		return this.paginate(pageNumber==null?1:pageNumber, pageSize,"SELECT * " ,whereSql,params);
//	}
	
	/**
	 * 分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
//	public Page<M> listByCache(int pageNumber,int pageSize){
//		String whereSql = getQuerySql();
//		String tableName = this.getTable().getName();
//		Object[] params = this.whereParams.toArray();
//		String cacheName = tableName + "_page";
//		String cacheKey = this.getCacheKey();
//		return paginateByCache(cacheName, cacheKey, pageNumber, pageSize, "SELECT * ", whereSql, params);
//	}
	
	
	
	
	// ---------------------------------- <<< SQL METHOED END >>> ----------------------------------
	
	
	//protected List<Object> whereParams = new ArrayList<Object>();
	private StringBuffer whereSql = new StringBuffer();
	private String orderByStr = "";
	
	
	/**
	 * 获得查询的SQL
	 * @return
	 */
	public String getQuerySql(){
		whereSql = new StringBuffer();
		//whereParams.clear();
		String tableName = this.getTable().getName();
		String sql = " FROM " + tableName  ;
		return sql;
	}
	
	
	/**
	 * 获取表映射对象
	 * @return
	 */
	public Table getTable() {
		return TableMapping.me().getTable(getClass());
	}
	
	/**
	 * 拼接SQL
	 * @param op
	 * @param columnName
	 * @param compareOP
	 * @param columnValue
	 */
//	public void appendSql(String logic,String columnName,String compareSql,Object columnValue){
//		whereSql.append(" ").append(logic).append(" ").append(columnName).append(" ").append(compareSql).append(" ? ");
//		whereParams.add(columnValue);
//	}
	
	/**
	 * 排序
	 * @return
	 */
	public String orderBy(){
		return orderByStr;
	}
	
	/**
	 * 获得缓存的Key,这个Key
	 * @return
	 */
//	public String getCacheKey(){
//		StringBuffer cacheKey = new StringBuffer(whereSql + " AND AAA=?");
//		Object[] params = this.whereParams.toArray();
//		for(Object param : params){
//			int index = cacheKey.indexOf("?");
//			if(index>=0){
//				cacheKey.replace(index, (index+1), "'" + param.toString()+ "'");
//			}
//		}
//		return cacheKey.toString();
//	}
	
	public JSONObject sendErrorJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", "404");
		jsonObject.put("message", "操作失败");
		return jsonObject ;
	}
	
	public JSONObject sendErrorJson(String message) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", "404");
		jsonObject.put("message", message );
		return jsonObject ;
	}
	
	public JSONObject sendSuccessJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", "200");
		jsonObject.put("message", "操作成功");
		return jsonObject ;
	}
	
	public JSONObject sendSuccessJson(String message) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", "200");
		jsonObject.put("message", message );
		return jsonObject ;
	}
	
	protected static String getIds() {
		return StringUtil.getKey();
	}
	
	protected static String mapToString(Map<String, String> map) {
		StringBuffer sBuffer = new StringBuffer() ;
		for (Entry<String, String> entity : map.entrySet()) {
			if (null != entity.getValue()) {
				sBuffer.append(entity.getValue()) ;
			}
		}
		return sBuffer.toString() ;
	}
	
	protected static int getIntValue(Map<String, Integer> cacheMap , String key) {
		if (null == cacheMap) {
			cacheMap = new HashMap<String, Integer>() ;
		}
		return null == cacheMap.get(key) ? 0 : cacheMap.get(key) ;
	}
	
	protected static int getIntValue(Integer value) {
		return null == value ? 0 : value ;
	}
	
	protected String getKeys(Map<String, String> map) {
		StringBuffer sBuffer = new StringBuffer() ;
		for (Entry<String, String> entity : map.entrySet()) {
			if (StrKit.notBlank(sBuffer.toString())) {
				sBuffer.append("&") ;
			}
			sBuffer.append(entity.getKey()+"="+entity.getValue()) ;
		}
		return sBuffer.toString() ;
	}
	
}
