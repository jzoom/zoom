package com.jzoom.zoom.dao.adapter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementAdapter {
	
	/**
	 * 适配PreparedStatement
	 * @param ps
	 * @param index
	 * @param value
	 */
	void adapt( PreparedStatement ps, int index, Object value  ) throws SQLException;

 }
