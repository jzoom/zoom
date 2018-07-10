package com.jzoom.zoom.dao.alias.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.mutable.MutableInt;

import com.jzoom.zoom.dao.adapter.NameAdapter;
import com.jzoom.zoom.dao.alias.AliasPolicy;
import com.jzoom.zoom.dao.alias.AliasPolicyMaker;
import com.jzoom.zoom.dao.meta.ColumnMeta;
import com.jzoom.zoom.dao.meta.TableMeta;

/**
 * 自动检测字段的前缀，并以驼峰式来重命名
 * {@link com.jzoom.zoom.dao.alias.AliasPolicyMaker}
 * 
 * @author jzoom
 *
 */
public class DetectPrefixAliasPolicyMaker implements AliasPolicyMaker {

	
	
	public DetectPrefixAliasPolicyMaker() {
		
	}
	
	@Override
	public NameAdapter getColumnAliasPolicy(TableMeta table) {
		Map<String, MutableInt> countMap = new LinkedHashMap<String, MutableInt>();
		
		for (ColumnMeta columnInfo : table.getColumns()) {
			String[] arr = columnInfo.getName().split("_");
			String prefixThisColumn = arr[0];
			MutableInt value = countMap.get(prefixThisColumn);
			if(value == null) {
				value = new MutableInt(1);
				countMap.put(prefixThisColumn, value);
			}else {
				value.add(1);
			}
		}
		AliasPolicy aliasPolicy = null;
		//只有最大的为第一个的才行
		MutableInt first = null;
		String key = null;
		for (Entry<String, MutableInt> entry : countMap.entrySet()) {
			if(first==null) {
				first = entry.getValue();
				key = entry.getKey();
			}else {
				if(first.intValue() > entry.getValue().intValue()) {
					aliasPolicy =  new PrefixAliasPolicy(new StringBuilder(key).append("_").toString());
				}
				break;
			}
		}
		
		if(aliasPolicy!=null) {
			Map<String,String> map = new HashMap<String, String>();
			for (ColumnMeta columnInfo : table.getColumns()) {
				map.put(aliasPolicy.getAlias(columnInfo.getName()), columnInfo.getName());
			}
			return new MapNameAdapter(aliasPolicy, map,table);
		}else {
			return CamelNameAdapter.ADAPTER;
		}
		
	}

	

}
