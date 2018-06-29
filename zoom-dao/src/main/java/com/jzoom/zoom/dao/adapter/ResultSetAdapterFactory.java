package com.jzoom.zoom.dao.adapter;

import java.sql.ResultSet;

public interface ResultSetAdapterFactory {
	ResultSetAdapter<?> create(ResultSet rs);
}
