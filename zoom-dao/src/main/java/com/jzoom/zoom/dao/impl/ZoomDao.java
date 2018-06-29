package com.jzoom.zoom.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.ConnectionExecutor;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Databases;
import com.jzoom.zoom.dao.Entity;
import com.jzoom.zoom.dao.EntityManager;
import com.jzoom.zoom.dao.SqlDriver;
import com.jzoom.zoom.dao.driver.mysql.MysqlDriver;
import com.jzoom.zoom.dao.meta.TableMeta;

/**
 * dao
 * @author jzoom
 *
 */
public class ZoomDao implements Dao {
	
	private static final Log log = LogFactory.getLog(Dao.class);
	
	private SqlDriver sqlDriver;
	private DataSource dataSource;
	private EntityManager entityManager;
	
	/**
	 * 创建一个Dao对象
	 * @param dataSource
	 * @param lazyLoad  是否在需要使用的时候才创建各种相关对象：如绑定实体类等,改成true，启动时间将缩减500ms左右！
	 */
	public ZoomDao(DataSource dataSource,boolean lazyLoad) {
		this.dataSource = dataSource;
		this.entityManager = new SimpleEntityManager();
		if(lazyLoad) {
			return;
		}
		
		load();
	}
	
	public void execute( ConnectionExecutor executor  ) {
		ar().execute( executor );
	}
	
	@Override
	public TableMeta getTableMeta(String table) {
		
		return null;
	}
	private void load() {
		Connection connection = null;
		//需要绑定entities
		try {
			connection = dataSource.getConnection();
			String name = connection.getMetaData().getDatabaseProductName();
			log.info(String.format("检测到数据库产品名称%s",name));
			sqlDriver = createDriver(name);
			
		} catch (SQLException e) {
			throw new RuntimeException("创建Dao失败,连接数据库错误",e);
		}finally {
			DaoUtil.close(connection);
		}
		
	}
	
	private SqlDriver createDriver(String productName) {
		if(Databases.MYSQL.equals(productName)) {
			return new MysqlDriver();
		}
		
		throw new RuntimeException(String.format("不支持的数据库产品:%s",productName));
	}
	
	private ThreadLocal<Ar> local = new ThreadLocal<Ar>();

	@Override
	public Ar ar() {
		Ar ar = local.get();
		if(ar==null) {
			ar = createAr();
			local.set(ar);
		}
		
		
		return ar;
	}


	private Ar createAr() {
		if(sqlDriver==null) {
			synchronized (this) {
				if(sqlDriver == null) {
					load();
				}
			}
		}
		return new ActiveRecord(dataSource, sqlDriver,entityManager );
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




}
