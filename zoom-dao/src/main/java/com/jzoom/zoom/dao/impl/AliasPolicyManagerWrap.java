package com.jzoom.zoom.dao.impl;

import com.jzoom.zoom.dao.adapter.NameAdapter;
import com.jzoom.zoom.dao.alias.AliasPolicy;
import com.jzoom.zoom.dao.alias.AliasPolicyManager;
import com.jzoom.zoom.dao.alias.impl.ToLowerCaseAiias;

public class AliasPolicyManagerWrap implements AliasPolicyManager {
	
	private NameAdapter aliasPolicy;
	

	public AliasPolicyManagerWrap(NameAdapter aliasPolicy) {
		this.aliasPolicy = aliasPolicy;
	}

	@Override
	public NameAdapter getPolicy(String table) {
		return aliasPolicy;
	}

}
