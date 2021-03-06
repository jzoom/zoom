package com.jzoom.zoom.caster;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.jackson.map.ObjectMapper;



/**
 *
 * This tool box can cast a value into another class. Caster.to( value ,
 * Integer.class)
 * 
 * 
 * 目前支持基本数据
 * 
 *
 * @author Administrator
 */
public class Caster {
	
	//日期格式
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	//时间格式
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	//毫秒
	public static final String DATE_TIME_MS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	
	/**
	 * 默认模式
	 */
	private static ObjectMapper mapper = new ObjectMapper();
	
	public static class CasterException extends RuntimeException {

		public CasterException(String message) {
			super(message);
		}
		
		public CasterException(Exception e) {
			super(e);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -4063979897278480246L;

	}
	
	
	public static interface CasterProvider{
		ValueCaster getCaster( Class<?> srcType,Class<?> toType );
	}
	
	public static CasterProvider[] providers;
	
	public static synchronized void registerCastProvider( CasterProvider provider ) {
		if(providers==null) {
			providers = new CasterProvider[] { provider };
		}else {
			CasterProvider[] oldCasterProviders = providers;
			providers = new CasterProvider[ providers.length + 1 ];
			
			for(int i=0,c=oldCasterProviders.length; i < c;++i) {
				providers[i] = oldCasterProviders[i];
			}
			providers[oldCasterProviders.length] = provider;
		}
	}

	private static Map<String, ValueCaster> map;

	static {
		map = new ConcurrentHashMap<String, ValueCaster>();

		register(Object.class, String.class, new Object2String());

		
		
		Caster.register(Double.class, boolean.class, new Double2Boolean());
		Caster.register(Float.class, boolean.class, new Float2Boolean());
		
		//数字到其他
		Caster.register(Number.class, double.class, new Number2Double());
		Caster.register(Number.class, int.class, new Number2Integer());
		Caster.register(Number.class, boolean.class, new Number2Boolean());
		Caster.register(Number.class, float.class, new Number2Float());
		Caster.register(Number.class, short.class, new Number2Short());
		Caster.register(Number.class, char.class, new Number2Char());
		Caster.register(Number.class, byte.class, new Number2Byte());
		Caster.register(Number.class, long.class, new Number2Long());

		//String 到 基本类型
		Caster.register(String.class, char.class, new String2Char());
		Caster.register(String.class, int.class, new String2Integer());
		Caster.register(String.class, boolean.class, new String2Boolean());
		Caster.register(String.class, long.class, new String2Long());
		Caster.register(String.class, short.class, new String2Short());
		Caster.register(String.class, byte.class, new String2Byte());
		Caster.register(String.class, double.class, new String2Double());
		Caster.register(String.class, float.class, new String2Float());
		
		
		//database
		
		
		
		//date
		Caster.register(java.util.Date.class, java.sql.Date.class, new Date2SqlDate());
				//除非你想用toString,否则下面这条不必要s
		//Caster.register(java.sql.Date.class, java.util.Date.class, new SqlDate2Date());
		
		
		
		//CLASS
		Caster.register(String.class, Class.class, new String2Class());
		
		//json
		Caster.register(String.class, Map.class, new String2Map());
		Caster.register(String.class, List.class, new String2List());
		
		//这里可以看情况是否要覆盖默认情况
		Caster.register(InputStream.class, Map.class, new InputStream2Map());
		Caster.register(InputStream.class, List.class, new InputStream2List());
		Caster.register(Reader.class, Map.class, new Reader2Map());
		Caster.register(Reader.class, List.class, new Reader2List());
		Caster.register(List.class, String.class, new JsonObject2String());
		Caster.register(Map.class, String.class, new JsonObject2String());
	
		
		//BigDecimal
		Caster.register(BigDecimal.class, boolean.class, new BigDecimal2Boolean());
		Caster.register(Integer.class, BigDecimal.class, new Integer2BigDecimal());
		Caster.register(Double.class, BigDecimal.class, new Double2BigDecimal());
		Caster.register(Long.class, BigDecimal.class, new Long2BigDecimal());
		Caster.register(Short.class, BigDecimal.class, new Short2BigDecimal());
		Caster.register(Boolean.class, BigDecimal.class, new Boolean2BigDecimal());
		Caster.register(String.class, BigDecimal.class, new String2BigDecimal());
		
		//time
		Caster.register(java.util.Date.class, java.sql.Time.class, new Date2SqlTime());
		//timestamp
		Caster.register(java.util.Date.class, java.sql.Timestamp.class, new Date2Timestamp());
		Caster.register(java.sql.Timestamp.class, String.class, new Timestamp2String());
		//规则是 转成Integer表示的是秒， 而long和double表示的是毫秒，其他数字类型不接受
		Caster.register(java.util.Date.class, int.class, new Date2Integer());
		Caster.register(java.util.Date.class, long.class, new Date2Long());
		
		
		//clob
		Caster.register(Clob.class, String.class, new Clob2String());
		Caster.register(NClob.class, String.class, new Clob2String());
		//blob
		Caster.register(Blob.class, String.class, new Blob2String());
		
	}
	private static class Blob2String implements ValueCaster{
		@Override
		public Object to(Object src) {
			Blob blob = (Blob)src;
			InputStream is = null;
			try {
				is = blob.getBinaryStream();
				byte[] data = new byte[(int)blob.length()];		// byte[] data = new byte[is.available()];
				is.read(data);
				is.close();
				return data;
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			finally {
				try {is.close();} catch (IOException e) {throw new RuntimeException(e);}
			}
		}
		
	}
	private static class Clob2String implements ValueCaster{
		@Override
		public Object to(Object src) {
			Clob clob = (Clob)src;
			Reader reader = null;
			try {
				reader = clob.getCharacterStream();
				char[] buffer = new char[(int)clob.length()];
				reader.read(buffer);
				return new String(buffer);
			} catch (IOException e) {
				throw new CasterException(e);
			} catch (SQLException e) {
				throw new CasterException(e);
			}
			finally {
				try {reader.close();} catch (IOException e) {throw new RuntimeException(e);}
			}
		}
		
	}
	
	private static class Date2Long implements ValueCaster{

		@Override
		public Object to(Object src) {
			Date date = (Date)src;
			return date.getTime();
		}
		
	}
	private static class Date2Integer implements ValueCaster{

		@Override
		public Object to(Object src) {
			Date date = (Date)src;
			return (int)(date.getTime() / 1000);
		}
		
	}
	
	private static class  Timestamp2String implements ValueCaster {
		@Override
		public Object to(Object src) {
			java.sql.Timestamp date = (java.sql.Timestamp)src;
			return new SimpleDateFormat(DATE_TIME_FORMAT).format(date);
		}
	}
	private static class  Date2Timestamp implements ValueCaster {

		@Override
		public Object to(Object src) {
			java.util.Date date = (java.util.Date)src;
			return new java.sql.Timestamp(date.getTime());
		}
	}
	
	private static class  Date2SqlTime implements ValueCaster {

		@Override
		public Object to(Object src) {
			java.util.Date date = (java.util.Date)src;
			return new java.sql.Time(date.getTime());
		}
	}
	
	private static class  Boolean2BigDecimal implements ValueCaster {

		@Override
		public Object to(Object src) {
			Boolean number = (Boolean)src;
			return new BigDecimal(number ? 1 : 0);
		}
	}
	private static class  Short2BigDecimal implements ValueCaster {

		@Override
		public Object to(Object src) {
			Short number = (Short)src;
			return new BigDecimal(number);
		}
	}
	
	private static class  Long2BigDecimal implements ValueCaster {

		@Override
		public Object to(Object src) {
			Long number = (Long)src;
			return new BigDecimal(number);
		}
	}
	private static class  String2BigDecimal implements ValueCaster {

		@Override
		public Object to(Object src) {
			String number = (String)src;
			return new BigDecimal(number);
		}
	}
	
	private static class  Integer2BigDecimal implements ValueCaster {

		@Override
		public Object to(Object src) {
			Integer number = (Integer)src;
			return new BigDecimal(number);
		}

	}
	

	private static class  Double2BigDecimal implements ValueCaster {

		@Override
		public Object to(Object src) {
			Double number = (Double)src;
			return new BigDecimal(number);
		}
	}
	
	private static class String2Map implements ValueCaster {

		@Override
		public Object to(Object src) {
			try {
				return mapper.readValue( (String)src,Map.class );
			} catch (Exception e) {
				throw new CasterException(e);
			}
		}

	}

	private static class InputStream2Map implements ValueCaster {

		@Override
		public Object to(Object src) {
			InputStream is = (InputStream)src;
			try {
				return mapper.readValue( is,Map.class );
			} catch (Exception e) {
				throw new CasterException(e);
			} finally {
				try{is.close();}catch (Exception e) {}
			}
		}

	}

	private static class InputStream2List implements ValueCaster {

		@Override
		public Object to(Object src) {
			InputStream is = (InputStream)src;
			try {
				return mapper.readValue(is, List.class );
			} catch (Exception e) {
				throw new CasterException(e);
			}finally {
				try{is.close();}catch (Exception e) {}
			}
		}

	}
	private static class Reader2Map implements ValueCaster {

		@Override
		public Object to(Object src) {
			Reader is = (Reader)src;
			try {
				return mapper.readValue( is,Map.class );
			} catch (Exception e) {
				throw new CasterException(e);
			} finally {
				try{is.close();}catch (Exception e) {}
			}
		}

	}

	private static class Reader2List implements ValueCaster {

		@Override
		public Object to(Object src) {
			Reader is = (Reader)src;
			try {
				return mapper.readValue(is, List.class );
			} catch (Exception e) {
				throw new CasterException(e);
			}finally {
				try{is.close();}catch (Exception e) {}
			}
		}

	}
	
	
	private static class String2List implements ValueCaster {

		@Override
		public Object to(Object src) {
			try {
				return mapper.readValue( (String)src,List.class );
			} catch (Exception e) {
				throw new CasterException(e);
			}
		}
	}
	
	private static class JsonObject2String implements ValueCaster {

		@Override
		public Object to(Object src) {
			try {
				return mapper.writeValueAsString(src);
			} catch (Exception e){
				throw new CasterException(e);
			}
		}
	}
	
	

	private static class String2Class implements ValueCaster {

		@Override
		public Object to(Object src) {
			try {
				return Class.forName((String)src);
			} catch (ClassNotFoundException e) {
				throw new CasterException(e);
			}
		}

	}
	
	
	

	
	private static class Date2SqlDate implements ValueCaster {

		@Override
		public Object to(Object src) {
			java.util.Date date = (java.util.Date)src;
			return new java.sql.Date(date.getTime());
		}

	}
	
	
	
	private static class String2Byte implements ValueCaster {

		@Override
		public Object to(Object src) {
			return Byte.parseByte((String)src);
		}

	}
	
	private static class String2Char implements ValueCaster {

		@Override
		public Object to(Object src) {
			return ((String)src).charAt(0);
		}

	}
	private static class String2Short implements ValueCaster {

		@Override
		public Object to(Object src) {
			return Short.parseShort((String) src);
		}

	}
	
	
	private static class String2Float implements ValueCaster {

		@Override
		public Object to(Object src) {
			return Float.parseFloat((String) src);
		}

	}

	private static class String2Boolean implements ValueCaster {

		@Override
		public Object to(Object src) {
			return Boolean.parseBoolean((String) src);
		}

	}
	
	

	
	private static class String2Integer implements ValueCaster {

		@Override
		public Object to(Object src) {
			String str =  ((String) src).trim();
			if (str.contains(".")) {
				return (int) (double) Double.parseDouble(str);
			}
			if(str.length()==0) {
				return 0;
			}
			return Integer.parseInt(str);
		}

	}

	private static class String2Long implements ValueCaster {

		@Override
		public Object to(Object src) {
			String str =  ((String) src).trim();
			if (str.contains(".")) {
				return (long) (double) Double.parseDouble(str);
			}
			if(str.length()==0) {
				return 0;
			}
			return Long.parseLong(str );
		}

	}
	private static class String2Double implements ValueCaster {

		@Override
		public Object to(Object src) {
			return Double.parseDouble((String) src);
		}

	}
	private static class Number2Long implements ValueCaster {

		@Override
		public Object to(Object src) {
			return ((Number) src).longValue();
		}
	}
	private static class Number2Byte implements ValueCaster {

		@Override
		public Object to(Object src) {
			return ((Number) src).byteValue();
		}
	}
	
	private static class Number2Char implements ValueCaster {

		@Override
		public Object to(Object src) {
			return (char) ((Number) src).shortValue();
		}

	}
	
	private static class Number2Integer implements ValueCaster {

		@Override
		public Object to(Object src) {
			return ((Number) src).intValue();
		}

	}
	
	private static class Number2Short implements ValueCaster {

		@Override
		public Object to(Object src) {
			return ((Number) src).shortValue();
		}

	}

	
	private static class Number2Float implements ValueCaster {

		@Override
		public Object to(Object src) {
			return ((Number) src).floatValue();
		}

	}
	
	private static class Double2Boolean implements ValueCaster {

		@Override
		public Object to(Object src) {
			return ((Double)src).equals(0.0d) ? Boolean.FALSE : Boolean.TRUE;
		}
	}
	
	private static class Float2Boolean implements ValueCaster {

		@Override
		public Object to(Object src) {
			return ((Float)src).equals(0.0f) ? Boolean.FALSE : Boolean.TRUE;
		}
	}
	//BigDecemil?
	

	private static class BigDecimal2Boolean implements ValueCaster {

		@Override
		public Object to(Object src) {
			return ((BigDecimal)src).doubleValue() == 0.0d ? Boolean.FALSE : Boolean.TRUE;
		}
	}
	
	private static class Number2Boolean implements ValueCaster {

		@Override
		public Object to(Object src) {
			return ((Number) src).intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
		}

	}

	private static class Number2Double implements ValueCaster {

		@Override
		public Object to(Object src) {
			return ((Number) src).doubleValue();
		}

	}

	
	
	/**
	 *
	 * T cast some class to another class, you must register the tow classes and
	 * a caster .
	 *
	 *
	 * @param src
	 * @param to
	 * @param caster
	 */
	public static void register(Class<?> src, Class<?> to, ValueCaster caster) {
		if(to==null) {
			throw new NullPointerException();
		}
		
		if(to.isPrimitive()) {
			map.put(getKey(src, getWrapClass(to)), caster);
		}
		
		map.put(getKey(src, to), caster);
	}

	/**
	 * Get convrt src and target key in map
	 * 
	 * @param src
	 * @param to
	 * @return
	 */
	private static String getKey(Class<?> src, Class<?> to) {
		return new StringBuilder().append(src.getName()).append("2").append(to.getName()).toString();
	}

	/**
	 * Extra class to array. Including self, super class, interfaces
	 * 
	 * @param src
	 * @return
	 */
	private static String[] extraClass(Class<?> src) {
		Set<Class<?>> set = new LinkedHashSet<Class<?>>();

		extraClass(set, src);

		String[] result = new String[set.size()];
		int i = 0;
		for (Class<?> type : set) {
			result[i++] = type.getName();
		}
		return result;
	}

	private static void extraClass(Set<Class<?>> set, Class<?> src) {
		if (src == null) {
			return;
		}
		if (src.isInterface()) {
			extraInterface(set, src);
		} else {
			if (src == Object.class) {
				set.add(Object.class);
				return;
			}

			Class<?>[] interfaces = src.getInterfaces();
			for (Class<?> inter : interfaces) {
				extraInterface(set, inter);
			}

			extraClass(set, src.getSuperclass());
			set.add(src);
		}

	}

	private static void extraInterface(Set<Class<?>> set, Class<?> src) {
		set.add(src);
		src = src.getSuperclass();
		if (src == null)
			return;
		extraInterface(set, src);
	}
	private static Class<?> getWrapClass(Class<?> type) {
		if (type == int.class) {
			return Integer.class;
		}
		if (type == boolean.class) {
			return Boolean.class;
		}
		if (type == double.class) {
			return Double.class;
		}
		if (type == float.class) {
			return Float.class;
		}
		if (type == long.class) {
			return Long.class;
		}
		if (type == short.class) {
			return Short.class;
		}
		if (type == byte.class) {
			return Byte.class;
		}
		if (type == char.class) {
			return Character.class;
		}
		throw new RuntimeException("impossible");
	}
	private static Object getDefaultValue(Class<?> targetClass) {
		if (targetClass == int.class) {
			return 0;
		}
		if (targetClass == boolean.class) {
			return false;
		}
		if (targetClass == double.class) {
			return (double) 0;
		}
		if (targetClass == float.class) {
			return (float) 0;
		}
		if (targetClass == long.class) {
			return 0L;
		}
		if (targetClass == short.class) {
			return (short) 0;
		}
		if (targetClass == byte.class) {
			return (byte) 0;
		}
		if (targetClass == char.class) {
			return (char) 0;
		}
		throw new RuntimeException("impossible");
	}

	@SuppressWarnings("unchecked")
	public static <T> T to(Object src, Class<?> targetClass) {
		if (src == null) {
			if (targetClass.isPrimitive()) {
				// default of primitive value
				return (T) getDefaultValue(targetClass);
			}
			return null;
		}
		
		if(targetClass==null) {
			
			//这里没有什么好办法，只能先返回null再说
			return null;
		}

		Class<?> srcType = src.getClass();
		if (srcType == targetClass || targetClass.isAssignableFrom(srcType)) {
			return (T) src;
		}
		String key = getKey(srcType, targetClass);
		ValueCaster caster = map.get(key);
		if (caster == null) {
			caster = get(srcType, targetClass);
			map.put(key, caster);
		}

		return (T) caster.to(src);
	}

	static final class WrapValueCaster implements ValueCaster {
		private final ValueCaster caster;

		WrapValueCaster(ValueCaster caster) {
			this.caster = caster;
		}

		@Override
		public final Object to(Object src) {
			if (src == null)
				return null;
			return caster.to(src);
		}
	}

	static final class WrapPriValueCaster implements ValueCaster {
		private final ValueCaster caster;
		private final Class<?> type;

		WrapPriValueCaster(ValueCaster caster, Class<?> toType) {
			this.caster = caster;
			this.type = toType;
		}

		@Override
		public final Object to(Object src) {
			if (src == null) {
				return getDefaultValue(type);
			}
			return caster.to(src);
		}
	}

	/**
	 * Get a wrapped ValueCaster that check null value and return default value when value is null.
	 * 
	 * @param srcType
	 * @param toType
	 * @return
	 */
	public static ValueCaster wrap(Class<?> srcType, Class<?> toType) {
		if (srcType == null || toType == null) {
			throw new NullPointerException("srcType and toType must not be null");
		}
		if (toType.isPrimitive()) {
			//转化的类型指定类的包装类
			return new WrapPriValueCaster(get(srcType, getWrapClass(toType)  ), toType);
		}
		
		
		return new WrapValueCaster(get(srcType, toType));
	}

	private static class EqValueCaster implements ValueCaster {

		@Override
		public Object to(Object src) {
			return src;
		}

	}

	/**
	 * dest 是否 是 src的包装类
	 *
	 *
	 * isWrapClass(int.class,Integer.class)==true
	 * isWrapClass(Integer.class,int.class)==false
	 *
	 *
	 * @param src
	 *            如 int.class
	 * @param dest
	 *            如 Integer.class
	 * @return
	 */
	private static boolean isWrapClass(Class<?> src, Class<?> dest) {
		try {
			
			return ((Class<?>) dest.getField("TYPE").get(null)) == src;
		} catch (Exception e) {
			return false;
		}
	}

	private static EqValueCaster eqValueCaster = new EqValueCaster();

	private static ValueCaster get(Class<?> srcType, Class<?> toType) {
		/**
		 * 如果是int 转 Integer 或者是Integer 转 int,则可以相互转化
		 */
		if (isWrapClass(srcType, toType) || isWrapClass(toType, srcType)) {
			return eqValueCaster;
		}

		/**
		 * 否则需要将class全部解出来，以便加以判断
		 *
		 * 解出来是指：
		 *
		 * 超类（递归） 接口 、接口超类（递归）
		 *
		 */
		String[] srcs = extraClass(srcType);
		String[] tos = extraClass(toType);

		for (String s : srcs) {
			for (String t : tos) {
				String key = new StringBuilder(s).append("2").append(t).toString();
				ValueCaster caster = map.get(key);
				if (caster == null) {
					continue;
				}
				return caster;
			}
		}
		
		
		final CasterProvider[] providers = Caster.providers;
		if(providers!=null) {
			for (CasterProvider casterProvider : providers) {
				ValueCaster caster = casterProvider.getCaster(srcType, toType);
				if(caster!=null) {
					String key = new StringBuilder(srcType.toString()).append("2").append(toType.toString()).toString();
					map.put(key, caster);
					return caster;
				}
			}
		}


		throw new CasterException(String.format("Cannot cast %s to %s ", srcType.getName(), toType.getName()));
	}

	private static class Object2String implements ValueCaster {

		@Override
		public Object to(Object src) {
			return String.valueOf(src);
		}

	}

}
