package com.jzoom.zoom.admin.models;

import java.util.List;

import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.SqlBuilder.Sort;
import com.jzoom.zoom.dao.utils.TreeUtils;

public class ModDao extends BaseDao {

	public ModDao() {
		super("sys_mod");
	}

	
	public List<Record> getList(){
		return TreeUtils.toTree(getDao()
				.table("sys_mod")
				.select("id,title as label,p_id,url")
				.orderBy("sort", Sort.ASC )
				.get(), "id", "p_id", "children", 0);
	}

	
	
	
}
