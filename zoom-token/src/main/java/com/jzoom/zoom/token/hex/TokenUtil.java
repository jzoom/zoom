package com.jzoom.zoom.token.hex;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;


/**
 * 思路：
 * 1、在用户需要登录的时候，向服务器端申请客户端token
 * 	客户端token与服务端token配对
 * 
 * 客户端token含如下内容：
 * (1) id: 根据设备号生成的随机数,本随机数用于索引服务端token
 * (2) pub: 离2017年1月1日到现在的秒数
 * (3) exp: 超时时间,秒数
 * (4) 签名 : sha256 + 固定64位秘钥
 * 
 * 
 * 在下发token的是否附带并在客户端保存
 * signKey: md5签名秘钥
 * publicKey: rsa加密公钥1024
 * 
 *
 * 
 * 客户端token的过期时间为30分钟
 * 
 * refreshToken: 另一个token，用于在客户端token过期之后换取新的token
 * 含如下内容：
 * (1) uid:用户号(aes 加密存储,公共秘钥)
 * (2) pub: 离2017年1月1日到现在的秒数
 * (3) exp: 超时时间,秒数
 * (4) 签名: sha256 + 固定64位秘钥
 * 
 * 服务端token含如下内容：
 * (1) id: 与客户端id同
 * (2) signKey: md5签名秘钥，与客户端signKey同
 * (3) uid: 用户id							起始为空值，在登录之后为实际用户id
 * (4) deviceId : 用户设备号,用于推送
 * (5) phone: 用户手机号
 * (6) privateKey: 用户数据解密秘钥
 * 
 * 2、在客户端在调用用户个人接口的时候，在http header上送客户端token
 * 形式: headers : { token :`${token}` }
 * 
 * 3、服务端验证客户端token:
 * 相同的签名方式，使用服务端签名重新签名一次并与客户端签名做比对
 * 
 * 4、服务器端验证客户端签名：
 * 使用想用的签名方式，重新签名一次客户端数据
 * 
 * 5、超时机制：
 * 客户端超时：客户端颁发时间+超时时间 < 现在时间就超时了
 * server端token：memcached超时，如果没有找到，那么就超时了，
 * 要求客户端使用refreshToken换取客户端签名
 * 
 * 6、refreshToken换取客户端签名流程：
 * 过期判断： 两周,只做客户端信息判断
 * (1) 根据uid查询用户，确定用户状态
 * (2) 如正常则颁发客户端token与服务端token配对
 * (3) refreshToken的正常过期时间为2周
 * (4) 离过期时间3天，下发refreshToken,并更新
 * 优点：不需要refreshToken存储
 * 如果用户两周没有用，则需要重新登录。
 * 
 * 
 * 
 * 
 * 
 * @author renxueliang
 *
 */
public class TokenUtil {
	private static final long START_TIME ;
	
	static{
		try {
			START_TIME = new SimpleDateFormat("yyyyMMdd").parse("20170101").getTime();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	

	private static int getPubTime(){
		return (int) ((System.currentTimeMillis() - START_TIME)/1000);
	}
	
	
	public static boolean isTimeout(int pub, int exp) {
		return pub + exp < getPubTime();
	}
	
	
	public static ClientToken createToken( String userId , long duration, TimeUnit unit ) {
		
		ClientToken token = new ClientToken();
		token.setId(userId);
		token.setPub( TokenUtil.getPubTime() );
		token.setExp(  (int) unit.toSeconds(duration)  );
		return token;
	}
	
	
}
