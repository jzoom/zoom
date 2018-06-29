package com.jzoom.zoom.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServerUtils {

	/**
	 * 获取所有的含有 .class文件的路径
	 * @return
	 */
	public static List<String> getClassPath() {
		String str =  System.getProperty("java.class.path");
		String[] parts = str.split( ":" ); 
		List<String> list = new ArrayList<String>();
		for (String part : parts) {
			if(part.endsWith("target/classes") || part.endsWith("target/test-classes")) {
				list.add(part);;
			}
		}
		return list;
	}
	
	/**
	 * 获取实际监控路径，为输入src的公用父路径，c:\s\target\classes c:\d\target\classes,那么实际监控路径为c:\
	 * @return
	 */
	public static Collection<String> getMonitorPath(List<String> src){
		
		Set<String> base = new HashSet<String>();
		
		for (String path : src) {
			int last = path.lastIndexOf("/target/classes");
			if(last < 0) {
				last = path.lastIndexOf("/target/test-classes");
				if(last < 0 ) {
					//??
					throw new RuntimeException();
				}
			}
			path = path.substring(0,last);
			path = path.substring(0,path.lastIndexOf("/"));
			base.add(path);
		}
		
		
		return base;
	}
}
