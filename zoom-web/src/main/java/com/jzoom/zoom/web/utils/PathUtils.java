package com.jzoom.zoom.web.utils;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

public class PathUtils {


	private static String webRootPath;
	
	private static String rootClassPath;
	
	
	public static void setWebRootPath(String webRootPath) {
		PathUtils.webRootPath = webRootPath;
	}
	/**
	 * 获取网站根目录
	 * @return
	 */
	public static String getWebRootPath(){
		if(webRootPath==null){
			synchronized (PathUtils.class) {
				try {
					String path = PathUtils.class.getResource("/").toURI().getPath();
					webRootPath = new File(path).getParentFile().getParentFile().getCanonicalPath();
					if(!webRootPath.endsWith(File.separator)){
						webRootPath = new StringBuilder(webRootPath).append( File.separator ).toString();
					}
				} catch (Exception e) {
					throw new RuntimeException("自动识别默认web目录失败，请使用PathUtil.setWebRootPath设置web根目录",e);
				}
			}
		}
		return webRootPath;
		
	}

	/**
	 * 获取WEB-INF/classes目录
	 * @return
	 */
	public static String getRootClassPath() {
		if (rootClassPath == null) {
			try {
				String path = PathUtils.class.getClassLoader().getResource("").toURI().getPath();
				rootClassPath = new File(path).getAbsolutePath();
			}
			catch (Exception e) {
				String path = PathUtils.class.getClassLoader().getResource("").getPath();
				rootClassPath = new File(path).getAbsolutePath();
			}
		}
		return rootClassPath;
	}
	
	/**
	 * 获取WEB-INF
	 * @param name
	 * @return
	 */
	public static File getWebInfPath(String name) {
		File file = new File( PathUtils.getWebRootPath() + "src/main/webapp/WEB-INF" + File.separator + name);
		if(!file.exists()){
			file = new File( PathUtils.getWebRootPath() + "WEB-INF" + File.separator + name);
		}
		return file;
	}
	
	/**
	 * 将路径解析为绝对路径  ,如果路径为相对路径 ./  / 开头，那么是相对于web root的，即为应用程序根目录
	 * @param path
	 * @return
	 */
	public static File resolve(String path) {
		if(StringUtils.isEmpty(path)) {
			return new File(PathUtils.getWebRootPath());
		}
		
		if(path.startsWith("/")) {
			return new File(path);
		}
		
		if(path.startsWith("~")) {
			//用户目录
			String userDir = System.getProperty("usr.dir");
			path = path.substring(1);
			return new File(userDir,path);
		}
		
		
		
		return new File( PathUtils.getWebRootPath(), path);
	}
}
