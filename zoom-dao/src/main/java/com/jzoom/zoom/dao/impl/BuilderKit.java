package com.jzoom.zoom.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jzoom.zoom.dao.DaoException;
import com.jzoom.zoom.dao.Entity;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.SqlDriver;
import com.jzoom.zoom.dao.adapter.EntityAdapter;


class BuilderKit {
	
	public static List<EntityAdapter> buildInsert(
			StringBuilder sql,
			List<Object> values,
			SqlDriver driver, 
			Entity entity,
			Object data
			) {

		EntityAdapter[] fields = entity.getAdapters();
		List<EntityAdapter> insertFields = new ArrayList<EntityAdapter>();
		sql.append("INSERT INTO ").append(entity.getTableName()).append(" (");
		boolean first = true;
		int count =0;
		for (EntityAdapter entityField : fields) {
			Object value = entityField.get(data);
			if(value!=null){
				values.add(value);
				insertFields.add(entityField);
				if(first){
					first = false;
				}else{
					sql.append(COMMA);
				}
				sql.append(entityField.getColumnName());
				++count;
			}
		}
		sql.append(") VALUES (?");
		for(int i=1; i < count; ++i){
			sql.append(",?");
		}
		sql.append(')');
		
		return insertFields;
	}
	
	/**
	 * 构建插入语句
	 * @param sql
	 * @param values
	 * @param driver
	 * @param table
	 * @param record
	 */
	public static void buildInsert(StringBuilder sql,List<Object> values,SqlDriver driver, String table,Record record) {
		
		sql.append("INSERT INTO ").append(table).append(" (");
		boolean first = true;
		for (Entry<String, Object> entry : record.entrySet()) {
			Object value = entry.getValue();
			String name = entry.getKey();
			if (first) {
				first = false;
			} else {
				sql.append(COMMA);
			}
			values.add(value);
			driver.protectName(sql,name);
		}
		//?
		join(sql.append(") VALUES ("),record.size()).append(')');
		
	}
	
	public static final char QM = '?';  //Question Mark
	public static final char COMMA = ',';  //comma
	/**
	 * 问号    组合成  ?,?   
	 * @param sql
	 * @param size   问号个数
	 * @return 
	 */
	private static StringBuilder join(StringBuilder sql, int size) {
		for(int i=0; i < size; ++i) {
			if(i>0) {
				sql.append(COMMA);
			}
			sql.append(QM);
		}
		return sql;
	}

	private static final Log log = LogFactory.getLog(BuilderKit.class);
	
	public static PreparedStatement prepareStatement(Connection connection,String sql, List<Object> values) throws SQLException {
		
		
		log.info(String.format(sql.replace("?", "'%s'"), values.toArray(new Object[values.size()])));
		
		PreparedStatement ps = connection.prepareStatement(sql);
		for(int index = 1,c=values.size(); index <= c; ++index) {
			ps.setObject(index, values.get(index-1));
		}
		return ps;
	}
	
	public static final Record buildOne(ResultSet rs) throws SQLException{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Record map = new Record();
		for (int i=1; i<=columnCount; i++) {
			map.put(rsmd.getColumnName(i), rs.getObject(i));
		}
		
		return map;
	}
	
	
	public static final Record build(int columnCount,ResultSet rs,String[] labelNames) throws SQLException{
		Record map = new Record();
		for (int i=1; i<=columnCount; i++) {
			map.put(labelNames[i],  rs.getObject(i));
		}
		return map;
	}
	
	public static final List<Record> build(ResultSet rs) throws SQLException{
		List<Record> result = new ArrayList<Record>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		String[] labelNames = new String[columnCount + 1];
		for (int i=1; i<labelNames.length; i++) {
			labelNames[i] = rsmd.getColumnLabel(i);
		}
		while (rs.next()) {
			result.add(build(columnCount, rs,labelNames));
		}
		return result;
	}



	public static void buildUpdate(StringBuilder sql, List<Object> values, SqlDriver driver, String table, StringBuilder where, Record record) {
		sql.append("UPDATE ").append(table);
		boolean first = true;
		int index = 0;
		for (Entry<String, Object> entry : record.entrySet()) {
			Object value = entry.getValue();
			if (first) {
				first = false;
				sql.append(" SET ");
			} else {
				sql.append(COMMA);
			}
			values.add( index++,value);
			driver.protectName(sql,entry.getKey()).append("=?");
		}

		sql.append(where);
	}

	/**
	 * 构建delete语句
	 * @param sql
	 * @param values
	 * @param table
	 * @param where
	 */
	public static void buildDelete(StringBuilder sql, String table, StringBuilder where) {
		if (where.length() <= 0) {
			throw new DaoException("Whole table delete is not valid!");
		}
		sql.append("DELETE FROM ").append(table).append(where);
	}
	
}
