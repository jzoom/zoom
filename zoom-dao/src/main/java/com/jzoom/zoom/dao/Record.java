package com.jzoom.zoom.dao;

import java.util.HashMap;
import java.util.Map;

import com.jzoom.zoom.caster.Caster;
import com.sun.tools.doclets.internal.toolkit.resources.doclets;

public class Record extends HashMap<String, Object> {
	
	public static class Builder {

		private Record record = new Record();

		public Builder put(String key, Object value) {
			record.put(key, value);
			return this;
		}

		public Record build() {
			return record;
		}
	}
	

	public Record(Map<? extends String, ? extends Object> m) {
		super(m);
	}

	public Record() {
		super();
	}

	public Record(int initialCapacity) {
		super(initialCapacity);
	}
	
	
	public Record set(String key,Object value) {
		put(key, value);
		return this;
	}
	
	public Record setAll(Map<String, Object> data) {
		assert(data!=null);
		putAll(data);
		return this;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8983230317786957763L;

	public String getString(String key) {
		return Caster.to(get(key), String.class);
	}

	public long getLong(String key) {
		return Caster.to(get(key), long.class);
	}

	public double getDouble(String key) {
		return Caster.to(get(key), double.class);
	}

	public int getInt(String key) {
		return Caster.to(get(key), int.class);
	}

	public boolean getBoolean(String key) {
		return Caster.to(get(key), boolean.class);
	}
	
	public float getFloat(String key) {
		return Caster.to(get(key), float.class);
	}
	
	public short getShort(String key) {
		return Caster.to(get(key), short.class);
	}
	
	public byte getByte(String key) {
		return Caster.to(get(key), Byte.class);
	}
	
	public char getChar(String key) {
		return Caster.to(get(key), Character.class);
	}
	
	public byte[] getBytes(String key) {
		return Caster.to(get(key), byte[].class);
	}

	public <T> T get(String key, Class<?> classOfT) {
		return Caster.to(get(key), classOfT);
	}
}
