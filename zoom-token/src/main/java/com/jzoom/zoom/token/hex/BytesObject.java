package com.jzoom.zoom.token.hex;

import java.security.MessageDigest;
import java.util.Arrays;

import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.map.JsonSerializable;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.DefaultIdStrategy;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.jzoom.zoom.common.codec.Hex;
import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.token.JsonWebToken;

public class BytesObject<T> implements JsonWebToken<T> {

	private Schema<T> schema;
	private final byte[] key;
	private DefaultIdStrategy id;
	
	@SuppressWarnings("unchecked")
	public BytesObject(byte[] key){
		Class<T> type = (Class<T>) Classes.getTypeParams(getClass())[0];
		id = new DefaultIdStrategy();
		schema = RuntimeSchema.getSchema(type,id);
		this.key = key;
	}
	
	
	
	
	public T decode(String token){
		assert(token!=null);
		try{
			byte[] bytes = Hex.decodeHex(token);
			//64位去掉
			byte[] sign = Arrays.copyOfRange(bytes, bytes.length-32, bytes.length);
			//开头的
			byte[] src = Arrays.copyOf(bytes, bytes.length-32);
			
			if(equals(sha256(src, key),sign)){
				T result = schema.newMessage();
				ProtostuffIOUtil.mergeFrom(src, result , schema);
				return result;
			}
		}catch(Throwable t){
			
		}
		return null;
	}
	

	private static boolean equals(byte[] src,byte[] dest){
		if(src==null || dest == null){
			return false;
		}
		if(src.length!=dest.length){
			return false;
		}
		for(int i=0 , c = src.length; i < c; ++i){
			if(src[i]!=dest[i]){
				return false;
			}
		}
		return true;
	}
	
	private static byte[] sha256(byte[] src,byte[] key){
		try{
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(src);
			messageDigest.update(key);
			return messageDigest.digest();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		
	}
	
	public String encode(T data){
		assert(data!=null);
		byte[] src = ProtostuffIOUtil.toByteArray(
				data, 
				schema,
				LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
		return Hex.encodeHexStr(src) + Hex.encodeHexStr(sha256(src, key));
	}

}
