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

		public static Sort parse(String sort) {
			if(sort == null)
				return DESC;
			
			sort = sort.toUpperCase();
			
			for (Sort s : values()) {
				if(s.value.equals(sort)) {
					return s;
				}
			}
			
			return DESC;
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
		
		public static Symbo parse(String value) {
			if(">".equals(value)) {
				return GT;
			}
			if("<".equals(value)){
				return LT;
			}
			if(">=".equals(value)){
				return GTE;
			}
			if("<=".equals(value)){
				return LTE;
			}
			if("<>".equals(value)){
				return NEQ;
			}
			if("=".equals(value)) {
				return EQ;
			}
			return null;
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
	 * @param key
	 * @param value
	 * @return
	 */
	SqlBuilder where(String key,Object value);
	
	/**
	 * 
	 * @param key
	 * @param like
	 * @param value
	 * @return
	 */
	SqlBuilder like(String key,Like like,Object value);
	
	
	/**
	 * 
	 * @param key
	 * @param symbo		> < >= <= <> =
	 * @param value
	 * @return
	 */
	SqlBuilder where(String key,Symbo symbo,Object value);
	
	
	/**
	 * where {key} is null
	 * @param key
	 * @return
	 */
	SqlBuilder whereNull(String key);
	
	/**
	 * where {key} in (  ?,?,?  ), 1,2,3
	 * @param key
	 * @param values
	 * @return
	 */
	SqlBuilder whereIn(String key,Object...values);
	
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
	SqlBuilder having(String key,Symbo symbo,Object value);
	
	
	SqlBuilder orWhere(String key,Object value);
	
	SqlBuilder orWhere(String key,Symbo symbo,Object value);
	
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
	
	
	
	SqlBuilder whereNotNull(String key);
	
	
	
	SqlBuilder whereNotIn(String key,Object...values);
	
	/**
	 * 
	 * @param key
	 * @param like
	 * @param value
	 * @return
	 */
	SqlBuilder notLike(String key,Like like,Object value);
	
	
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

	SqlBuilder count();
	
	SqlBuilder avg(String field);
	
	/**
	 *  select("sum(count) as count,min(id),a ,b ","c","d")
	 * @param fields
	 * @return
	 */
	SqlBuilder select(String...fields);
	
	
	SqlBuilder set(String key,Object value);

	SqlBuilder setAll( Map<String, Object> data );
	
	
}
