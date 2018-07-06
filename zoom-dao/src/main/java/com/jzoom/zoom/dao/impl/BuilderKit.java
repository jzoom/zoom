package com.jzoom.zoom.dao.impl;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jzoom.zoom.caster.Caster;
import com.jzoom.zoom.caster.ValueCaster;
import com.jzoom.zoom.dao.DaoException;
import com.jzoom.zoom.dao.Entity;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.adapter.EntityAdapter;
import com.jzoom.zoom.dao.adapter.NameAdapter;
import com.jzoom.zoom.dao.alias.AliasPolicy;
import com.jzoom.zoom.dao.driver.SqlDriver;


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
			driver.protectColumn(sql,name);
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
	
	private static ValueCaster blobCaster;
	private static ValueCaster clobCaster;
	static {
		
		blobCaster = Caster.wrap(Blob.class, String.class);
		clobCaster = Caster.wrap(Clob.class, String.class);
		
	}
	
	public static final Record buildOne(ResultSet rs,NameAdapter policy) throws SQLException{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Record map = new Record();
		for (int i=1; i<=columnCount; i++) {
			int type = rsmd.getColumnType(i);
			String name = rsmd.getColumnName(i);
			map.put( policy.getFieldName(name) ,  getValue(type, rs, i) );
		}
		
		return map;
	}
	
	private static Object getValue(int type,ResultSet rs,int i) throws SQLException {
		if (type < Types.BLOB)
			return rs.getObject(i);
		else if (type == Types.CLOB)
			return clobCaster.to(rs.getClob(i));
		else if (type == Types.NCLOB)
			return clobCaster.to(rs.getNClob(i));
		else if (type == Types.BLOB)
			return blobCaster.to(rs.getBlob(i));
		else
			return rs.getObject(i);
	}
	
	public static final Record build(int columnCount,ResultSet rs,int[] types,String[] labelNames) throws SQLException{
		Record map = new Record();
		for (int i=1; i<=columnCount; i++) {
			map.put(labelNames[i], getValue(types[i],rs,i));
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
	public static final List<Record> build(ResultSet rs,NameAdapter policy) throws SQLException{
		List<Record> result = new ArrayList<Record>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		String[] labelNames = new String[columnCount + 1];
		int[] types = new int[columnCount + 1];
		buildLabelNamesAndTypes(rsmd, labelNames, types,policy);
		while (rs.next()) {
			result.add(build(columnCount, rs, types, labelNames));
		}
		return result;
	}
	
	private static final void buildLabelNamesAndTypes(ResultSetMetaData rsmd, String[] labelNames, int[] types, NameAdapter policy) throws SQLException {
		if(policy!=null) {
			for (int i=1; i<labelNames.length; i++) {
				labelNames[i] = policy.getFieldName(rsmd.getColumnLabel(i));
				types[i] = rsmd.getColumnType(i);
			}
		}else {
			for (int i=1; i<labelNames.length; i++) {
				labelNames[i] = rsmd.getColumnLabel(i);
				types[i] = rsmd.getColumnType(i);
			}
		}
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
			driver.protectColumn(sql,entry.getKey()).append("=?");
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
	
	public static final Pattern AS_PATTERN = Pattern.compile("([a-zA-Z_\\(\\)\\.\\[\\]]+[\\s]+as[\\s]+)([a-zA-Z_]+)",Pattern.CASE_INSENSITIVE);
	

	/**
	 * 将select中的as解析出来
	 * @param select
	 * @return
	 */
	public static String parseAs(String select) {
		Matcher matcher = null;
		if( (matcher =  BuilderKit.AS_PATTERN.matcher(select) ) .matches()) {
			return matcher.group(2);
		}
		return select;
	}
	
}
