package com.jzoom.zoom.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionExecutor {
	void execute( Connection connection ) throws SQLException;
}
