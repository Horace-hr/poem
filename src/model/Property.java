package model;

import model.base.BaseProperty;
import utils.DicUtil;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Property extends BaseProperty<Property> {
	public static final Property dao = new Property().dao();
	
	//【缓存】查询渠道信息By - id 
		public Property findByIdInCache(int id) {
			return this.findFirstByCache(DicUtil.CACHE_PROPERTY, id, "SELECT tp.* FROM t_property tp WHERE tp.id = ? " , id );
		}

}