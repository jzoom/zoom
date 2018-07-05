package com.jzoom.zoom.dao.driver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.ConnectionExecutor;
import com.jzoom.zoom.dao.meta.ColumnMeta;
import com.jzoom.zoom.dao.meta.TableMeta;
import com.jzoom.zoom.dao.utils.DaoUtils;

public abstract class AbsDbStruct implements DbStructFactory{

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
					for (int i = 1, c = data.getColumnCount(); i < c; ++i) {
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

}
