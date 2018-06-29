package com.jzoom.zoom.dao.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

import com.jzoom.zoom.dao.Record;

/**
 * 树形的数据经常看到，这里封装一些基本操作
 * @author jzoom
 *
 */
public class TreeUtils {

	/**
	 * 将列表转成树形列表   
	 *   [
	 *     {id:1,children:[ {id:2},{id:3} ]} 
	 *     ...
	 *   ]
	 * 
	 * @param list				输入源
	 * @param id					id字段名称
	 * @param pId				parent_id字段名称
	 * @param children			children字段名称
	 * @param rootValue		    当parent_id字段值=rootValue的时候保留为一级
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static List<Record> toTree(List<Record> list,String id,String pId,String children,Object rootValue){
		Map<Object, Record> map = new HashMap<Object, Record>();
		List<Record> result = new ArrayList<Record>();
		for (Record record : list) {
			Object vId = record.get(id);
			Object vPId = record.get(pId);
			if(ObjectUtils.equals(vPId, rootValue)) {
				result.add(record);
			}
			map.put(vId, record);
		}
		
		for (Record record : list) {
			Object vPId = record.get(pId);
			Record parentNode = map.get(vPId);
			if(parentNode!=null) {
				List<Record> vChildren = (List<Record>) parentNode.get(children);
				if(vChildren == null) {
					vChildren = new ArrayList<Record>();
					parentNode.put(children, vChildren);
				}
				vChildren.add(record);
			}
		}
		
		map.clear();
		
		return result;
	}
	

}
