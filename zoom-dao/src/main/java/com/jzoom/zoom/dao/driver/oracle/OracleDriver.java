package com.jzoom.zoom.dao.driver.oracle;

import com.jzoom.zoom.dao.driver.AbsDriver;

public class OracleDriver extends AbsDriver {

	@Override
	public StringBuilder buildPage(StringBuilder sql, int position, int pageSize) {
		return sql.insert(0, "SELECT * FROM(SELECT A.*, rownum r FROM (").append(") A WHERE rownum <= ")
		.append(position + pageSize).append(" ) B WHERE r > ").append(position);
	}

	@Override
	public int position2page(int position, int pageSize) {
		++position;
		if (position % pageSize == 0) {
			return position / pageSize;
		}
		return position / pageSize + 1;
	}
}
