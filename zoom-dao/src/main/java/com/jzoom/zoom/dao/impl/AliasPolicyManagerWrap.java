package com.jzoom.zoom.dao.impl;

import com.jzoom.zoom.dao.adapter.NameAdapter;
import com.jzoom.zoom.dao.alias.AliasPolicyManager;

public class AliasPolicyManagerWrap implements AliasPolicyManager {
	
	private NameAdapter aliasPolicy;
	

	public AliasPolicyManagerWrap(NameAdapter aliasPolicy) {
		this.aliasPolicy = aliasPolicy;
	}

	@Override
	public NameAdapter getNameAdapter(String table) {
		return aliasPolicy;
	}

}
