package com.jzoom.zoom.dao.driver.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.ConnectionExecutor;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.driver.DbStructFactory;
import com.jzoom.zoom.dao.meta.ColumnMeta;
import com.jzoom.zoom.dao.meta.ColumnMeta.KeyType;
import com.jzoom.zoom.dao.meta.TableMeta;
import com.jzoom.zoom.dao.utils.DaoUtils;

public class MysqlDbStrict implements DbStructFactory {

	private String dbName;

	public MysqlDbStrict(String dbName) {
		this.dbName = dbName;
	}

	@Override
	public TableMeta getTableMeta(Ar ar, final String table) {
		TableMeta tableMeta = ar.execute(new ConnectionExecutor() {

			@SuppressWarnings("unchecked")
			@Override
			public TableMeta execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				ResultSet rs = null;
				try {
					statement = connection.prepareStatement("select * from " + table + " where 1=2");
					rs = statement.executeQuery();
					ResultSetMetaData data = rs.getMetaData();
					List<ColumnMeta> columnMetas = new ArrayList<ColumnMeta>(data.getColumnCount());
					for (int i = 1, c = data.getColumnCount(); i <= c; ++i) {
						ColumnMeta column = new ColumnMeta();
						String className = data.getColumnClassName(i);
						column.setClazz(Classes.forName(className));
						column.setName(data.getColumnName(i));
						column.setType(data.getColumnType(i));
						column.setRawType(data.getColumnTypeName(i));
						columnMetas.add(column);
					}

					TableMeta meta = new TableMeta();
					meta.setName(table);
					meta.setColumns(columnMetas.toArray(new ColumnMeta[columnMetas.size()]));
					return meta;
				} finally {
					DaoUtils.close(statement);
					DaoUtils.close(rs);
				}

			}
		});


		return tableMeta;
	}

	private static final Log log = LogFactory.getLog(MysqlDbStrict.class);


	@Override
	public Collection<String> getTableNames(Ar ar) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Record> getNameAndComments(Ar ar) {
		List<Record> list = ar.executeQuery(
				"select table_comment as comment,table_name as name from information_schema.tables where table_schema=?",
				dbName);

		return list;
	}

	@Override
	public void fill(Ar ar, TableMeta meta) {
		List<Record> list = ar.executeQuery(
				"SELECT TABLE_COMMENT AS COMMENT,TABLE_NAME as NAME from information_schema.tables where table_schema=? AND TABLE_NAME=?",
				dbName,
				meta.getName());
		if (list.size() > 0) {
			Record record = list.get(0);
			meta.setComment(record.getString("comment"));
		}else {
			meta.setComment("");
		}

		list = ar.executeQuery(
				"SELECT TABLE_NAME,COLUMN_NAME,IS_NULLABLE,DATA_TYPE,CHARACTER_MAXIMUM_LENGTH,COLUMN_KEY,EXTRA,COLUMN_COMMENT,COLUMN_DEFAULT FROM information_schema.columns WHERE table_schema=? and TABLE_NAME=?",
				dbName, meta.getName());

		for (Record record : list) {
			String column = record.getString("COLUMN_NAME");
			ColumnMeta columnMeta = meta.getColumn(column);
			if (columnMeta == null) {
				// 没有？不可能
				log.warn("找不到对应的字段:" + column);
				continue;
			}

			columnMeta.setComment(record.getString("COLUMN_COMMENT"));
			// 常用的
			columnMeta.setAuto(record.getString("EXTRA").equals("auto_increment"));
			String key = record.getString("COLUMN_KEY");
			if (key.equals("PRI")) {
				columnMeta.setKeyType(KeyType.PRIMARY);
			} else if (key.equals("UNI")) {
				columnMeta.setKeyType(KeyType.UNIQUE);
			} else if (key.equals("MUL")) {
				columnMeta.setKeyType(KeyType.INDEX);
			}
			columnMeta.setDefaultValue(record.getString("COLUMN_DEFAULT"));
			columnMeta.setNullable(record.getString("IS_NULLABLE").equals("YES"));
			columnMeta.setMaxLen(record.getInt("CHARACTER_MAXIMUM_LENGTH"));
			columnMeta.setRawType(record.getString("DATA_TYPE"));

		}

	}

}
