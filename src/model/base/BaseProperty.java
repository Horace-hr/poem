package model.base;

import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseProperty<M extends BaseProperty<M>> extends _BaseModel<M> implements IBean {
	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setAppid(java.lang.String appid) {
		set("appid", appid);
	}

	public java.lang.String getAppid() {
		return get("appid");
	}
	
	public void setAppsecret(java.lang.String appsecret) {
		set("appsecret", appsecret);
	}

	public java.lang.String getAppsecret() {
		return get("appsecret");
	}
	
	public void setPublicName(java.lang.String publicName) {
		set("publicName", publicName);
	}

	public java.lang.String getPublicName() {
		return get("publicName");
	}
	
	public void setQrcode(java.lang.String qrcode) {
		set("qrcode", qrcode);
	}

	public java.lang.String getQrcode() {
		return get("qrcode");
	}
	
	public void setBookshopName(java.lang.String bookshopName) {
		set("bookshopName", bookshopName);
	}

	public java.lang.String getBookshopName() {
		return get("bookshopName");
	}
	
	public void setBlogerName(java.lang.String blogerName) {
		set("blogerName", blogerName);
	}

	public java.lang.String getBlogerName() {
		return get("blogerName");
	}
	
	public void setQq(java.lang.String qq) {
		set("qq", qq);
	}

	public java.lang.String getQq() {
		return get("qq");
	}
	
	public void setTelephone(java.lang.String telephone) {
		set("telephone", telephone);
	}

	public java.lang.String getTelephone() {
		return get("telephone");
	}
}