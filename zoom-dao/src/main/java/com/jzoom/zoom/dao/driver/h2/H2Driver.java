package com.jzoom.zoom.dao.driver.h2;

import java.util.List;


import com.jzoom.zoom.caster.Caster;
import com.jzoom.zoom.dao.driver.AbsDriver;
import com.jzoom.zoom.dao.driver.SqlDriver;
import com.jzoom.zoom.dao.meta.TableMeta;

public class H2Driver extends AbsDriver{

	@Override
	public StringBuilder buildPage(StringBuilder sql, int position, int pageSize) {
		return sql.append(" LIMIT ").append(position).append(',').append(pageSize);
	}




}
