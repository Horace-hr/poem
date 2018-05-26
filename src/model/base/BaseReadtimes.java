package model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseReadtimes<M extends BaseReadtimes<M>> extends _BaseModel<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setNovelId(java.lang.String novelId) {
		set("novelId", novelId);
	}

	public java.lang.String getNovelId() {
		return get("novelId");
	}

	public void setDate(java.util.Date date) {
		set("date", date);
	}

	public java.util.Date getDate() {
		return get("date");
	}

	public void setReadTimes(java.lang.Integer readTimes) {
		set("readTimes", readTimes);
	}

	public java.lang.Integer getReadTimes() {
		return get("readTimes");
	}

}
