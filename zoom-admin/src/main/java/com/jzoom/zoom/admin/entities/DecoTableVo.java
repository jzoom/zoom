package com.jzoom.zoom.admin.entities;

import java.util.List;

public class DecoTableVo {
	
	public static class DecoColumn{
		
		private String column;
		private String name;
		private String type;
		private String comment;
		private Object prop;
		
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
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
		public String getColumn() {
			return column;
		}
		public void setColumn(String column) {
			this.column = column;
		}
		public Object getProp() {
			return prop;
		}
		public void setProp(Object prop) {
			this.prop = prop;
		}
		
	}
	
	private String name;
	private String comment;
	private String primaryKey;
	
	private List<DecoColumn> columns;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<DecoColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<DecoColumn> columns) {
		this.columns = columns;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	
	
}
