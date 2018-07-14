package com.jzoom.zoom.dao.driver;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
	
	@Override
	public StringBuilder protectColumn(StringBuilder sb, String name) {
	
		return sb.append(name);
	}

	
	@Override
	public StringBuilder protectTable(StringBuilder sb, String name) {
	
		return sb.append(name);
	}

	@Override
	public int position2page(int position, int pageSize) {
		return position / pageSize + 1;

	}

}
