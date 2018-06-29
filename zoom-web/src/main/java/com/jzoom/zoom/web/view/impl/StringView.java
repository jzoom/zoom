package com.jzoom.zoom.web.view.impl;

import javax.servlet.http.HttpServletResponse;

import com.jzoom.zoom.web.view.View;

public class StringView implements View {
	
	private String str;
	
	public StringView( String str ) {
		this.str = str;
	}

	@Override
	public void render( HttpServletResponse response) throws Exception {
		response.getWriter().print(str);
	}

}
