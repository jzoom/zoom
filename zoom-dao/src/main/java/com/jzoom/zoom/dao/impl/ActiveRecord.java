package com.jzoom.zoom.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.ConnectionExecutor;
import com.jzoom.zoom.dao.ConnectionHolder;
import com.jzoom.zoom.dao.DaoException;
import com.jzoom.zoom.dao.EntityManager;
import com.jzoom.zoom.dao.Page;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.SqlBuilder.Sort;
import com.jzoom.zoom.dao.SqlDriver;
import com.jzoom.zoom.dao.Trans;

public class ActiveRecord extends ThreadLocalConnectionHolder implements Ar, ConnectionHolder, Trans {

	private SimpleSqlBuilder builder;
	
	private EntityManager entityManager;

	public ActiveRecord(DataSource dataSource, SqlDriver driver , EntityManager entityManager) {
		super(dataSource);
		this.builder = new SimpleSqlBuilder(driver);
		this.entityManager = entityManager;
	}

	public List<Record> executeQuery(String sql, List<Object> values) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			ps = DaoUtil.prepareStatement(connection, sql, values);
			rs = ps.executeQuery();
			return DaoUtil.build(rs);
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			DaoUtil.close(rs);
			DaoUtil.close(ps);
			releaseConnection();
			builder.clear();
		}
	}
	
	public Record fetch(String sql, List<Object> values) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			ps = DaoUtil.prepareStatement(connection, sql, values);
			rs = ps.executeQuery();
			if(rs.next()) {
				return DaoUtil.buildOne(rs);
			}
			return null;
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			DaoUtil.close(rs);
			DaoUtil.close(ps);
			releaseConnection();
			builder.clear();
		}
	}
	
	@Override
	public void execute(ConnectionExecutor executor) {
		try {
			executor.execute(getConnection());
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			releaseConnection();
		}
	}


	public int executeUpdate(String sql, List<Object> values)  {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = getConnection();
			ps = DaoUtil.prepareStatement(connection, sql, values);
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			DaoUtil.close(ps);
			releaseConnection();
			builder.clear();
		}
	}

	
	@Override
	public List<Record> get() {
		builder.buildSelect();
		return executeQuery(builder.sql.toString(), builder.getValues());
	}

	@Override
	public Record fetch() {
		builder.buildSelect();
		return fetch(builder.sql.toString(), builder.getValues());
	}
	@Override
	public List<Record> limit(int position, int pageSize) {

		return null;
	}

	@Override
	public Page<Record> page(int position, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}


	

	@Override
	public Ar table(String table) {
		builder.table(table);
		return this;
	}

	@Override
	public <T> List<T> get(Class<T> classOfT) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> limit(Class<T> classOfT, int position, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Page<T> page(Class<T> classOfT, int position, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T fetch(Class<T> classOfT) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int insert(Object data) {
		assert(data!=null);
		
		if(data instanceof Map) {
			builder.buildInsert(  (Map<String, Object>) data);
			return executeUpdate(builder.sql.toString(), builder.values);
		}else {
		//	builder.buildInsert(  );
			
		}
		
		return 0;
	}
	

	@Override
	public int update() {
		builder.buildUpdate();
		return executeUpdate(builder.sql.toString(), builder.values);
	}


	@Override
	public int update(Map<String, Object> record) {
		builder.buildUpdate(record);
		return executeUpdate(builder.sql.toString(), builder.values);
	}

	@Override
	public int delete() {
		builder.buildDelete();
		return executeUpdate(builder.sql.toString(), builder.values);
	}
	
	@Override
	public Ar setAll(Map<String, Object> record) {
		builder.setAll(record);
		return this;
	}

	@Override
	public Ar set(String key, Object value) {
		builder.set(key, value);
		return this;
	}

	@Override
	public Ar where(String key, Object value) {
		builder.where(key,value);
		return this;
	}

	@Override
	public Ar orderBy(String field, Sort sort) {
		builder.orderBy(field, sort);
		return this;
	}

	@Override
	public Ar select(String... select) {
		builder.select(select);
		return this;
	}

	

	
}
