package com.jzoom.zoom.dao.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.ConnectionExecutor;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Databases;
import com.jzoom.zoom.dao.Entity;
import com.jzoom.zoom.dao.EntityManager;
import com.jzoom.zoom.dao.adapter.NameAdapter;
import com.jzoom.zoom.dao.alias.AliasPolicy;
import com.jzoom.zoom.dao.alias.AliasPolicyManager;
import com.jzoom.zoom.dao.alias.impl.DetectPrefixAliasPolicyMaker;
import com.jzoom.zoom.dao.alias.impl.EmptyAlias;
import com.jzoom.zoom.dao.alias.impl.ToLowerCaseAiias;
import com.jzoom.zoom.dao.alias.impl.ToLowerCaseNameAdapter;
import com.jzoom.zoom.dao.driver.DbStructFactory;
import com.jzoom.zoom.dao.driver.SqlDriver;
import com.jzoom.zoom.dao.driver.h2.H2DbStrict;
import com.jzoom.zoom.dao.driver.h2.H2Driver;
import com.jzoom.zoom.dao.driver.mysql.MysqlDbStrict;
import com.jzoom.zoom.dao.driver.mysql.MysqlDriver;
import com.jzoom.zoom.dao.meta.TableMeta;
import com.jzoom.zoom.dao.utils.DaoUtils;

/**
 * dao
 * @author jzoom
 *
 */
public class ZoomDao implements Dao, AliasPolicyManager {
	
	private static final Log log = LogFactory.getLog(Dao.class);
	
	private SqlDriver sqlDriver;
	private DataSource dataSource;
	private EntityManager entityManager;
	private DbStructFactory dbStructFactory;
	private boolean lazyLoad;
	private AliasPolicyManager aliasPolicyManager;
	private String tableCat;
	private Collection<String> names;
	
	
	public ZoomDao( DataSource dataSource ) {
		this(dataSource, false);
	}
	
	public ZoomDao(DataSource dataSource,boolean lazyLoad) {
		this( dataSource,lazyLoad, new AliasPolicyManagerWrap(new ToLowerCaseNameAdapter()) );
	}
	
	/**
	 * 创建一个Dao对象
	 * @param dataSource
	 * @param lazyLoad  是否在需要使用的时候才创建各种相关对象：如绑定实体类等,改成true，启动时间将缩减500ms左右！
	 */
	public ZoomDao(DataSource dataSource,boolean lazyLoad,AliasPolicyManager aliasPolicyManager) {
		this.dataSource = dataSource;
		this.entityManager = new SimpleEntityManager();
		this.lazyLoad =lazyLoad;
		this.aliasPolicyManager = aliasPolicyManager;
		if(lazyLoad) {
			return;
		}
		load();
	}
	
	public void execute( ConnectionExecutor executor  ) {
		ar().execute( executor );
	}
	

	public DbStructFactory getDbStructFactory() {
		lazyLoad();
		return dbStructFactory;
	}


	
	
	private void load() {
		Connection connection = null;
		//需要绑定entities
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData metaData = connection.getMetaData();
			String name = metaData.getDatabaseProductName();
			log.info(String.format("检测到数据库产品名称%s",name));
			SqlDriver sqlDriver = createDriver(name);
			parseDatabaseStruct(metaData);
			this.sqlDriver = sqlDriver;
		} catch (SQLException e) {
			throw new RuntimeException("创建Dao失败,连接数据库错误",e);
		}finally {
			DaoUtils.close(connection);
		}
		
	}
	private void parseDatabaseStruct(DatabaseMetaData metaData) throws SQLException {
		ResultSet rs = null;
		try {
			rs = metaData.getTables(null, null, null, null);
			List<String> names = new ArrayList<String>();
			String cat = null;
			while(rs.next()) {
				String name = rs.getString("TABLE_NAME");
				cat = rs.getString("TABLE_CAT");
				names.add(name);
			}
			this.names = names;
			this.tableCat = cat;
			dbStructFactory = createDbStructFactory(metaData.getDatabaseProductName() );
		}finally {
			DaoUtils.close(rs);
		}
		
	}
	
	private DbStructFactory createDbStructFactory(String productName) {
		if(Databases.MYSQL.equals(productName)) {
			return new MysqlDbStrict(tableCat);
		}
		
		if(Databases.H2.equals(productName)) {
			return new H2DbStrict(tableCat);
		}
		throw new RuntimeException(String.format("不支持的数据库产品:%s",productName));
	}
	
	
	private SqlDriver createDriver(String productName) {
		if(Databases.MYSQL.equals(productName)) {
			return new MysqlDriver();
		}
		
		if(Databases.H2.equals(productName)) {
			return new H2Driver();
		}
		
		throw new RuntimeException(String.format("不支持的数据库产品:%s",productName));
	}
	
	private ThreadLocal<Ar> local = new ThreadLocal<Ar>();

	
	public Ar ar() {
		Ar ar = local.get();
		if(ar==null) {
			ar = createAr();
			local.set(ar);
		}
		return ar;
	}


	private void lazyLoad() {
		if(sqlDriver==null) {
			synchronized (this) {
				if(sqlDriver == null) {
					load();
				}
			}
		}
	}
	
	private Ar createAr() {
		lazyLoad();
		if(aliasPolicyManager==null) {
			aliasPolicyManager = this ;
		}
		return new ActiveRecord(dataSource, sqlDriver,entityManager,aliasPolicyManager);
	}


	@Override
	public Entity getEntity(Class<?> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ar table(String table) {
		return ar().table(table);
	}

	public String getTableCat() {
		return tableCat;
	}

	public void setTableCat(String tableCat) {
		this.tableCat = tableCat;
	}

	public Collection<String> getTableNames() {
		return names;
	}

	public void setTableNames(Collection<String> names) {
		this.names = names;
	}

	
	private DetectPrefixAliasPolicyMaker maker = new DetectPrefixAliasPolicyMaker();
	@Override
	public NameAdapter getPolicy(String table) {
		TableMeta meta = getDbStructFactory().getTableMeta(ar(), table);
		return maker.getColumnAliasPolicy(meta);
	}



}
