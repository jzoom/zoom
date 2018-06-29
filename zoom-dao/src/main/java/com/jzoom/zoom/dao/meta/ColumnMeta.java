package com.jzoom.zoom.dao.meta;

/**
 * 字段属性
 * @author jzoom
 *
 */
public class ColumnMeta {
	
	/**
	 * 数据库中的类型
	 */
	private Class<?> type;
	
	/**
	 * 是否主键
	 */
	private boolean primary;
	/**
	 * 是否自动提交
	 */
	private boolean auto;
	
	/**
	 * 长度
	 */
	private int length;
	
	/**
	 * 默认值
	 */
	private Object defaultValue;
	
	/**
	 * 字段名称
	 */
	private String name;
	
	/**
	 * 数据库中的原始类型，比如mysql的字符串: varchar(100) oracle的VARCHAR2(200)
	 */
	private String rawType;

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public boolean isAuto() {
		return auto;
	}

	public void setAuto(boolean auto) {
		this.auto = auto;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRawType() {
		return rawType;
	}

	public void setRawType(String rawType) {
		this.rawType = rawType;
	}
	
}
