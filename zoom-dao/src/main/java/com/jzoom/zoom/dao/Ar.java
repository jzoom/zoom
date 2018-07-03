package com.jzoom.zoom.dao;

import java.util.List;
import java.util.Map;

import com.jzoom.zoom.dao.SqlBuilder.Sort;

/**
 * ActiveRecord 接口
 * 别名系统： 也叫做逻辑名称，相对于数据库原始字段和表的物理名称
 * 实践证明：
 * 1、使用别名比原始名称要容易记忆，
 * 2、在一个系统中的别名是有限的   如常见的； name/title/thumb/image/bg/nick等
 * 3、找到一种方法，将表中的所有物理名称全部映射成为逻辑名称
 * 4、逻辑名称可用于网络传输、程序等
 * 5、自动生成逻辑名称的常量
 * @author jzoom
 *
 */
public interface Ar  {
	
	
	/**
	 * 凡是符合条件的都返回，需要确定返回的数据数量是少量的，否则引起程序运行慢
	 * @return
	 */
	List<Record> get();
	
	/**
	 * 凡是符合条件的都返回，需要确定返回的数据数量是少量的，否则引起程序运行慢
	 * @param classOfT		取数的实体类  
	 * @return
	 */
	<T> List<T> get(Class<T> classOfT);
	
	List<Record> limit(int position,int pageSize);
	
	Page<Record> page(int position,int pageSize);
	
	<T> List<T> limit(Class<T> classOfT,int position,int pageSize);
	
	<T> Page<T> page(Class<T> classOfT,int position,int pageSize);
	
	List<Record> executeQuery(String sql,Object...args);
	/**
	 * 更新记录
	 * @param record
	 * @return
	 */
	int update(Map<String, Object> record);
	
	/**
	 * 在设置了数据的情况下
	 * @return
	 */
	int update();
	
	Ar setAll(Map<String, Object> record);
	
	
	Ar set(String key,Object value);
	
	Record fetch();
	
	/**
	 * 获取一个实体类
	 * @param classOfT
	 * @return
	 */
	<T> T fetch(Class<T> classOfT);
	/**
	 * 插入一个实体对象或者Record
	 * 实体对象不必实现注册，但是最好开启启动注册检查
	 * @param data
	 * @return
	 */
	int insert(Object data);
	
	/**
	 * 指定表名称,这个选项在参数为实体对象的时候依然有效
	 * @param table
	 * @return
	 */
	Ar table(String table);

	<T> T execute(ConnectionExecutor executor);
	
	/**
	 * 删除
	 * @return 删除了几条记录
	 */
	int delete();
	
	
	//######################################################
	
	Ar where(String key,Object value);

	
	Ar orderBy(String field, Sort sort);

	Ar select(String...select);
	
	
	
}
