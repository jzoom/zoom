package com.jzoom.zoom.dao.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表属性
 * @author jzoom
 *
 */
public class TableMeta {
	
	/**
	 * 如果有comment，表示的是已经拿到注释了
	 */
	private String comment;
	
	private String name;
	
	private ColumnMeta[] columns;
	
	private Map<String, ColumnMeta> columnMap;

	public ColumnMeta[] getColumns() {
		return columns;
	}

	/**
	 * 根据字段原始名称查找字段
	 * @param name
	 * @return
	 */
	public ColumnMeta getColumn(String name) {
		return columnMap.get(name);
	}
	
	public void setColumns(ColumnMeta[] columns) {
		columnMap = new HashMap<String, ColumnMeta>();
		this.columns = columns;
		for (ColumnMeta columnMeta : columns) {
			columnMap.put(columnMeta.getName(), columnMeta);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	private ColumnMeta[] primaryKeys;
	
	public ColumnMeta[] getPrimaryKeys() {
		if(primaryKeys==null) {
			synchronized (this) {
				List<ColumnMeta> list = new ArrayList<ColumnMeta>(2);
				for (ColumnMeta columnMeta : columns) {
					if(columnMeta.isPrimary()) {
						list.add(columnMeta);
					}
				}
				primaryKeys = list.toArray(new ColumnMeta[ list.size() ]);
			}
		}
		return primaryKeys;
	}
	

	
}
