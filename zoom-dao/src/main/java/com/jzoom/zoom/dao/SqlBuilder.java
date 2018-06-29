package com.jzoom.zoom.dao;

import java.util.Map;

public interface SqlBuilder {
	
	public enum Sort{
		ASC("ASC"),
		DESC("DESC");
		
		private String value;
		
		Sort(String value) {
			this.value = value;
		}
		
		public String value() {
			return value;
		}
		
	}
	
	public enum Join{
		INNER,
		LEFT,
		RIGHT
	}
	
	/**
	 * 符号
	 * @author jzoom
	 *
	 */
	public enum Symbo{
		GT(">"),				//>
		LT("<"),				//<
		GTE(">="),				//>=
		LTE("<="),				//<=
		NEQ("<>"),				//<>
		EQ("=");				//=
		
		private String value;
		
		Symbo(String value) {
			this.value = value;
		}
		
		public String value() {
			return value;
		}
	}
	
	/**
	 * 
	 * @author jzoom
	 *
	 */
	public enum Like{
		
		BOTH("%%%s%%"),				//like '%{}%'
		FIRST("%%%s"),				//like '%{}'
		LAST	("%s%%"),				//like '{}%'
		LEFT("%%%s"),				//like '%{}'
		RIGHT("%s%%"); 				//like '{}%'
		
		Like(String format){
			this.format = format;
		}
		
		private String format;
		public String toValue(Object value) {
			return String.format(format, value);
		}
	}
	
	
	public interface Condition{
		void where(SqlBuilder where);
	}
	/**
	 * 等于
	 * @param name
	 * @param value
	 * @return
	 */
	SqlBuilder where(String name,Object value);
	
	/**
	 * 
	 * @param name
	 * @param like
	 * @param value
	 * @return
	 */
	SqlBuilder like(String name,Like like,String value);
	
	
	/**
	 * 
	 * @param name
	 * @param symbo		> < >= <= <> =
	 * @param value
	 * @return
	 */
	SqlBuilder where(String name,Symbo symbo,String value);
	
	
	/**
	 * where {name} is null
	 * @param name
	 * @return
	 */
	SqlBuilder whereNull(String name);
	
	/**
	 * where {name} in (  ?,?,?  ), 1,2,3
	 * @param name
	 * @param values
	 * @return
	 */
	SqlBuilder whereIn(String name,Object...values);
	
	/**
	 * 相当于 where    (    condition     ) 
	 * @param where
	 * @return
	 */
	SqlBuilder where(Condition where);
	
	/**
	 * 
	 * having('sum(user)',Symbo.gt,50)
	 * 
	 * @return
	 */
	SqlBuilder having(String name,Symbo symbo,Object value);
	
	
	SqlBuilder orWhere(String name,Object value);
	
	SqlBuilder orWhere(String name,Symbo symbo,Object value);
	
	/**
	 * whereCondition ("a=? and b=? and c=?", 1 , 2, 3)
	 * 注意这里不要写成  a=1 and b=2 and c=3,
	 * 
	 * @param value
	 * @return
	 */
	SqlBuilder whereCondition(String value,Object...values);
	
	
	/**
	 * or where ...
	 * @param condition
	 * @return
	 */
	SqlBuilder orWhere(Condition condition);
	
	
	
	SqlBuilder whereNotNull(String name);
	
	
	
	SqlBuilder whereNotIn(String name,Object...values);
	
	/**
	 * 
	 * @param name
	 * @param like
	 * @param value
	 * @return
	 */
	SqlBuilder notLike(String name,Like like,Object value);
	
	
	/**
	 * .innerJoin("table_a","table_a.id=table_b.id",)
	 * @param table
	 * @param on
	 * @param join
	 * @return
	 */
	SqlBuilder innerJoin(String otherTable,String on);
	
	
	
	
	SqlBuilder union(SqlBuilder builder);
	
	
	SqlBuilder unionAll(SqlBuilder builder);
	

	
	/**
	 * 按照排序
	 * @param field
	 * @param sort
	 * @return
	 */
	SqlBuilder orderBy(String field,Sort sort);
	

	SqlBuilder groupBy(String field);
	
	

	/**
	 * 指定表
	 * @param table
	 * @return
	 */
	SqlBuilder table(String table);
	
	SqlBuilder sum(String field);
	
	SqlBuilder max(String field);
	
	SqlBuilder min(String field);

	SqlBuilder count(String field);
	
	SqlBuilder avg(String field);
	
	/**
	 *  select("sum(count) as count,min(id),a ,b ","c","d")
	 * @param fields
	 * @return
	 */
	SqlBuilder select(String...fields);
	
	
	SqlBuilder set(String name,Object value);

	SqlBuilder setAll( Map<String, Object> data );
	
	
}
