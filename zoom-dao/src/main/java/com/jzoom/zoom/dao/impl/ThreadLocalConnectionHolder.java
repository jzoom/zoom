package com.jzoom.zoom.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.jzoom.zoom.dao.ConnectionHolder;
import com.jzoom.zoom.dao.DaoException;
import com.jzoom.zoom.dao.Trans;

public class ThreadLocalConnectionHolder implements ConnectionHolder,Trans {
	private int level;
	private Integer oldLevel;
	private boolean trans;
	private DataSource dataSource;
	private Connection connection;
	
	public ThreadLocalConnectionHolder(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public void beginTransaction(int level) {
		this.level = level;
		this.trans = true;
	}

	@Override
	public void commit() {
		try {
			if (this.connection != null) {
				try {
					this.connection.commit();
				} finally {
					this.connection.setAutoCommit(true);
					if (oldLevel != null) {
						this.connection.setTransactionIsolation(oldLevel);
					}
				}

			}
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			this.trans = false;
			this.oldLevel = null;
		}

	}

	@Override
	public void rollback() {
		try {
			if (this.connection != null) {
				try {
					this.connection.rollback();
				} finally {
					this.connection.setAutoCommit(true);
					if (oldLevel != null) {
						this.connection.setTransactionIsolation(oldLevel);
					}
				}
			}
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			this.trans = false;
			this.oldLevel = null;
		}

	}

	public Connection getConnection() {
		if (trans) {
			if (connection == null) {
				try {
					this.connection = dataSource.getConnection();
					this.connection.setAutoCommit(false);
					int oldLevel = this.connection.getTransactionIsolation();
					if (oldLevel != level) {
						this.connection.setTransactionIsolation(level);
						this.oldLevel = oldLevel;
					}
				} catch (SQLException e) {
					throw new DaoException(e);
				}
			}
		} else {
			if (connection == null) {
				try {
					this.connection = dataSource.getConnection();
				} catch (SQLException e) {
					throw new DaoException(e);
				}
			}
		}

		return this.connection;
	}

	public void releaseConnection() {
		final Connection connection = this.connection;
		if (connection != null) {
			if (trans) {
				return;
			}
			try {
				connection.close();
			} catch (Throwable e) {
				
			} finally {
				this.connection = null;
			}
		}
	}
}
