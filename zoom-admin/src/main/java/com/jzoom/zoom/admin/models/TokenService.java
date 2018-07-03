package com.jzoom.zoom.admin.models;

import java.util.concurrent.TimeUnit;

import com.jzoom.zoom.token.hex.ClientToken;
import com.jzoom.zoom.token.hex.HexToken;
import com.jzoom.zoom.token.hex.TokenUtil;


public class TokenService {

	private HexToken hexToken;
	
	public TokenService() {
		hexToken = new HexToken( "Zoom".getBytes() );
	}
	
	public String generateToken( String userId ) {
		return hexToken.encode(TokenUtil.createToken(userId ,12 ,TimeUnit.HOURS ));
	}

	public ClientToken verifyToken(String token) {
		if(token==null || token.isEmpty()) {
			return null;
		}
		return hexToken.decode(token);
	}
	
}
