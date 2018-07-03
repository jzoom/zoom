package com.jzoom.zoom.token.hex;

import java.io.Serializable;

public class ClientToken implements Serializable  {
	
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7720258257358471282L;

	/**
	 * 用户id
	 */
	private String id;
	
	/**
	 * 过期时间（秒）,从发布时间开始计算
	 */
	private Integer exp;
	
	/**
	 * 发布时间
	 */
	private Integer pub;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getExp() {
		return exp;
	}

	public void setExp(Integer exp) {
		this.exp = exp;
	}

	public Integer getPub() {
		return pub;
	}

	public void setPub(Integer pub) {
		this.pub = pub;
	}
	
	
	public boolean isTimeout(){
		return TokenUtil.isTimeout(pub, exp);
	}
	


}
