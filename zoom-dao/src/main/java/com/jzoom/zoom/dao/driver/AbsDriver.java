package com.jzoom.zoom.dao.driver;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.jzoom.zoom.dao.SqlDriver;
import com.jzoom.zoom.dao.adapter.StatementAdapter;

public abstract class AbsDriver implements SqlDriver {
	private static DefaultStatementAdapter defaultStatementAdapter = new DefaultStatementAdapter();

	/**
	 * 应付绝大部分情况够了
	 * 
	 * @author jzoom
	 *
	 */
	private static class DefaultStatementAdapter implements StatementAdapter {

		@Override
		public void adapt(PreparedStatement ps, int index, Object value) throws SQLException {
			ps.setObject(index, value);
		}

	}

	public StatementAdapter get(Class<?> dataClass, Class<?> columnClass) {
		return defaultStatementAdapter;
	}

}
