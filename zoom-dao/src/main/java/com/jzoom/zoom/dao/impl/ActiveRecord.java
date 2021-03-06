package com.jzoom.zoom.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.jzoom.zoom.caster.Caster;
import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.ConnectionExecutor;
import com.jzoom.zoom.dao.ConnectionHolder;
import com.jzoom.zoom.dao.DaoException;
import com.jzoom.zoom.dao.EntityManager;
import com.jzoom.zoom.dao.Page;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.SqlBuilder;
import com.jzoom.zoom.dao.SqlBuilder.Like;
import com.jzoom.zoom.dao.SqlBuilder.Sort;
import com.jzoom.zoom.dao.SqlBuilder.Symbo;
import com.jzoom.zoom.dao.alias.AliasPolicy;
import com.jzoom.zoom.dao.alias.AliasPolicyMaker;
import com.jzoom.zoom.dao.alias.AliasPolicyManager;
import com.jzoom.zoom.dao.driver.SqlDriver;
import com.jzoom.zoom.dao.Trans;
import com.jzoom.zoom.dao.meta.TableMeta;
import com.jzoom.zoom.dao.utils.DaoUtils;

public class ActiveRecord extends ThreadLocalConnectionHolder implements Ar, ConnectionHolder, Trans {

	private AliasSqlBuilder builder;
	private EntityManager entityManager;
	private AliasPolicyManager aliasPolicyManager;
	
	
	public ActiveRecord(
			DataSource dataSource, 
			SqlDriver driver ,
			EntityManager entityManager,
			AliasPolicyManager aliasPolicyManager) {
		super(dataSource);
		this.builder = new AliasSqlBuilder(driver,aliasPolicyManager);
		this.entityManager = entityManager;
		this.aliasPolicyManager = aliasPolicyManager;
	}

	public List<Record> executeQuery(String sql, List<Object> values,boolean all) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			ps = BuilderKit.prepareStatement(connection, sql, values); 
			rs = ps.executeQuery();
			return BuilderKit.build(rs,builder.nameAdapter);
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			DaoUtils.close(rs);
			DaoUtils.close(ps);
			releaseConnection();
			builder.clear(all);
		}
	}
	
	public Record fetch(String sql, List<Object> values) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			ps = BuilderKit.prepareStatement(connection, sql, values);
			rs = ps.executeQuery();
			if(rs.next()) {
				return BuilderKit.buildOne(rs,builder.nameAdapter);
			}
			return null;
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			DaoUtils.close(rs);
			DaoUtils.close(ps);
			releaseConnection();
			builder.clear(true);
		}
	}
	
	public ResultSet execute( final String sql, final List<Object> values,final boolean all ) {
		return execute(new ConnectionExecutor() {
			
			@SuppressWarnings("unchecked")
			@Override
			public ResultSet execute(Connection connection) throws SQLException {
				ResultSet rs = null;
				PreparedStatement ps = null;
				try {
					connection = getConnection();
					ps = BuilderKit.prepareStatement(connection, sql, values);
					rs = ps.executeQuery();
					return rs;
				} catch (SQLException e) {
					throw new DaoException(e);
				} finally {
					DaoUtils.close(ps);
					builder.clear(all);
				}
			}
		});
	}
	
	@Override
	public <T> T execute(ConnectionExecutor executor) {
		try {
			return executor.execute(getConnection());
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
			ps = BuilderKit.prepareStatement(connection, sql, values);
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			DaoUtils.close(ps);
			releaseConnection();
			builder.clear(true);
		}
	}

	
	@Override
	public List<Record> get() {
		builder.buildSelect();
		return executeQuery(builder.sql.toString(), builder.getValues(),true);
	}

	@Override
	public Record fetch() {
		builder.buildSelect();
		return fetch(builder.sql.toString(), builder.getValues());
	}
	
	@Override
	public List<Record> limit(int position, int pageSize) {
		builder.buildLimit(position,pageSize);
		return executeQuery(builder.sql.toString(), builder.values, true);
	}

	@Override
	public Page<Record> page(int page, int pageSize) {
		if (page == 0)
			page = 1;
		return position( (page - 1) * pageSize, pageSize);
	}
	
	@Override
	public Page<Record> position(int position, int pageSize) {
		builder.buildLimit(position,pageSize);
		
		try {
			List<Record> list = executeQuery(builder.sql.toString(), builder.values, false);
			int total = getCount();
			int page = builder.getPageFromPosition(position, pageSize);
			return new Page<Record>( list,page,pageSize,total  );
		}finally {
			builder.clear(true);
		}
	
		
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
			builder.setAll( (Map<String, Object>) data  );
			builder.buildInsert( );
			return executeUpdate(builder.sql.toString(), builder.values);
		}else {
		//	builder.buildInsert(  );
			
		}
		
		return 0;
	}
	@Override
	public int insert() {
		builder.buildInsert( );
		return executeUpdate(builder.sql.toString(), builder.values);
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
	
	public Ar selectRaw(String select) {
		builder.selectRaw(select);
		return this;
	}

	@Override
	public List<Record> executeQuery(String sql, Object... args) {
		return executeQuery(sql, Arrays.asList(args),true);
	}

	@Override
	public Ar join(String table, String on) {
		builder.innerJoin(table, on);
		return this;
	}

	@Override
	public Ar orWhere(String key, Object value) {
		builder.orWhere(key, value);
		return this;
	}

	@Override
	public <T> T getValue(String select, Class<T> classOfT) {
		Record record = select(select).fetch();
		if(record==null) {
			return Caster.to(null, classOfT);
		}
		String as = BuilderKit.parseAs(select);
		return record.get(as,classOfT);
	}
	
	public int getCount() {
		Record record = selectRaw("count(*)").fetch();
		if(record==null) {
			return Caster.to(null, int.class);
		}
		return record.get("count(*)",int.class);
	}

	@Override
	public Ar whereIn(String key, Object... values) {
		builder.whereIn(key, values);
		return this;
	}

	@Override
	public Ar like(String name, Like like, Object value) {
		builder.like(name, like, value);
		return this;
	}

	@Override
	public Ar whereCondition(String key, Object... values) {
		builder.whereCondition(key, values);
		return this;
	}

	@Override
	public Ar where(String key, Symbo symbo, Object value) {
		builder.where(key, symbo,value);
		return this;
	}

	
	

	

	
}
