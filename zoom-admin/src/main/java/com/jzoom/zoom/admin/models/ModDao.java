package com.jzoom.zoom.admin.models;

import java.util.List;

import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.SqlBuilder.Sort;
import com.jzoom.zoom.dao.utils.TreeUtils;
import com.jzoom.zoom.web.action.ActionContext;

public class ModDao extends BaseDao {

	public ModDao() {
		super("sys_mod","id");
	}
	
	public List<Record> getMenu(  ){
		ActionContext context = ActionContext.get();
		String[] mods = context.get("mods", String[].class);
		
		return TreeUtils.toTree(getDao()
				.table("sys_mod")
				.whereIn("id",mods)
				.where("menu", 1)
				.select("id,title as label,p_id,url,icon")
				.orderBy("sort", Sort.ASC )
				.get(), "id", "p_id", "children", 0);
	}

	
	public List<Record> getList(  ){
		return TreeUtils.toTree(getDao()
				.table("sys_mod")
				.select("id,title as label,p_id,url,icon")
				.orderBy("sort", Sort.ASC )
				.get(), "id", "p_id", "children", 0);
	}

	
	
	
}
