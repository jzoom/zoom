package com.jzoom.zoom.admin.models;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.jzoom.zoom.caster.Caster;
import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Page;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.SqlBuilder.Like;
import com.jzoom.zoom.dao.SqlBuilder.Sort;
import com.jzoom.zoom.dao.SqlBuilder.Symbo;
import com.jzoom.zoom.dao.adapter.NameAdapter;
import com.jzoom.zoom.dao.meta.ColumnMeta;
import com.jzoom.zoom.dao.meta.TableMeta;

public class BaseDao implements AdminModel<Record> {
	
	public static interface DaoId{
		//默认orderBy
		Ar order(Ar ar);
		Ar where(Ar ar,Object id);
	}
	
	private static class MutilPrimaryKeyDaoId implements DaoId{

		private String[] idNames;
		
		public MutilPrimaryKeyDaoId(String[] idNames) {
			this.idNames = idNames;
		}
		
		@Override
		public Ar order(Ar ar) {
			for (String name : idNames) {
				ar.orderBy(name, Sort.DESC);
			}
			return ar;
		}

		@Override
		public Ar where(Ar ar, Object id) {
			assert(id instanceof String);
			String str = (String)id;
			String[] parts = str.split("__");
			if(parts.length != idNames.length) {
				throw new AdminException(String.format("条件值 %s 和 主键 %s 不一致", str, StringUtils.join(idNames) ));
			}
			
			for(int i=0; i < parts.length ; ++i) {
				ar.where(idNames[i], parts[i]);
			}
			
			
			return ar;
			
		}
		
	}
	
	
	private static class OnePrimaryKeyDaoId implements DaoId{
		private String idName;

		OnePrimaryKeyDaoId( String idName){
			this.idName = idName;
			
		}
		@Override
		public Ar order(Ar ar) {
			ar.orderBy(idName, Sort.DESC);
			return ar;
		}

		@Override
		public Ar where(Ar ar, Object id) {
			return ar.where(idName, id);
		}
		
		
	}
	
	public static DaoId getDaoId( TableMeta tableMeta, NameAdapter adapter) {
		ColumnMeta[] primaryKeys = tableMeta.getPrimaryKeys();
		if(primaryKeys==null || primaryKeys.length == 0) {
			//将第一个列设置为key
			ColumnMeta[] columns = tableMeta.getColumns();
			if(columns == null || columns.length == 0) {
				throw new RuntimeException("本表没有定义列");
			}
			return new OnePrimaryKeyDaoId( adapter.getFieldName(columns[0].getName()) );
		}
		
		if(primaryKeys.length == 0) {
			return new OnePrimaryKeyDaoId( adapter.getFieldName(primaryKeys[0].getName()) );
		}
		int index = 0;
		String[] idNames = new String[ primaryKeys.length];
		for (ColumnMeta meta : primaryKeys) {
			idNames[index++] = adapter.getFieldName(meta.getName());
		}
		
		return new MutilPrimaryKeyDaoId(idNames  );
		
		
	}

	protected Dao dao;

	protected String table;
	DaoId id;
	
	public BaseDao(String table, String id) {
		this(table, newDaoId(id));
	}
	public BaseDao(String table, DaoId id) {
		this.table = table;
		this.id = id;
	}

	public List<Record> getList() {
		return id.order(dao.table(table).select("*")).get();
	}
	public List<Record> getList(Map<String, Object> search) {
		Ar ar = parseSearch(search);
		return ar.get();
	}
	
	protected Ar parseSearch(Map<String, Object> search) {
		Record record = new Record(search);
		String select = record.getString("_select");
		String order = Caster.to(search.get("_order"), String.class);
		String sort = Caster.to(search.get("_sort"), String.class);

		Ar ar = dao.table(table).select(select == null ? "*" : select);
		if(order!=null && sort!=null) {
			ar.orderBy(order,  Sort.parse(sort) );
		}else {
			id.order(ar);
		}
		
		parseWhere(ar, search);
		return ar;
		
	}
	
	public static void parseWhere(Ar ar,Map<String, Object> search) {
		for (Entry<String, Object> entry : search.entrySet()) {
			parseKeyAndValue(ar, entry.getKey(), entry.getValue());
		}
	}
	
	
	public static void parseKeyAndValue( Ar ar, String key,Object value ) {
		if(key.contains("@")) {
			String[] parts = key.split("@");
			parseKeyAndValue(ar,parts[0],parts[1],value);
		}else {
			if(!key.startsWith("_")) {
				ar.where(key, value);
			}
		}
	}
	
	public static void parseKeyAndValue(Ar ar, String sign, String key, Object value) {
		if("like".equals(sign)) {
			ar.like(key, Like.BOTH, value);
		}else{
			Symbo symbo = Symbo.parse(sign);
			if(symbo!=null) {
				ar.where(key,symbo ,value );
			}else {
				throw new RuntimeException("不支持的操作符:"+sign);
			}
			
		}
	}

	public Page<Record> getPage(Map<String, Object> search){
		Ar ar = parseSearch(search);
		int pageSize = Caster.to(search.get("_pageSize"), int.class);
		if(pageSize <=0 ) {
			pageSize = 30;
		}
		int page = Caster.to(search.get("_page"), int.class);
		return ar.page(page, pageSize);
	}


	public int put(String id, Map<String, Object> data) {
		return this.id.where( dao.table(table) ,id) .setAll(data).update();
	}

	public int add(Map<String, Object> data) {
		return dao.table(table).insert(data);
	}

	public Record fetch(String id) {
		return this.id.where(dao.table(table),id).fetch();
	}

	public int del(String id) {
		return this.id.where(dao.table(table), id).delete();
	}

	public Dao getDao() {
		return dao;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}

	public static DaoId newDaoId(String string) {
		return new OnePrimaryKeyDaoId(string);
	}

}
