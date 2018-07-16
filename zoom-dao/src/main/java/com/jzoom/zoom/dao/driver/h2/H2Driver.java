package com.jzoom.zoom.dao.driver.h2;

import com.jzoom.zoom.dao.driver.AbsDriver;

public class H2Driver extends AbsDriver{

	@Override
	public StringBuilder buildPage(StringBuilder sql, int position, int pageSize) {
		return sql.append(" LIMIT ").append(position).append(',').append(pageSize);
	}




}
