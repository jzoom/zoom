package com.jzoom.zoom.web;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 
 * 本框架的目的：
 *    急速开发java接口，拥有比php更高的开发效率
 * 
 * 特性：
 * 1、简单，几乎零配置，无须记忆
 * 2、代码即为文档，一旦写出了接口，就可生成对应文档和调用规范
 * 3、代码即调试，可自动生成调试界面
 * 4、存储调用历史（可配置），并可复现调用
 * 5、支持oauth2协议
 * 6、支持restful风格
 * 
 * 扩展：
 * 1、易于接入和集成各种数据库组件
 * 
 * 
 * 功能：
 * 一、Controller
 *    
 *    @Action(provider=JsonAction.class)			表示对数据的处理，可方便扩展
 *    @Controller(mapping="/test")				表示路径映射
 *    class TestController{
 *      
 *      @Method(GET)                 可不写，默认支持全部
 *      @RequestMapping("method1")   可不写,在使用jdk编译的情况下
 *    	public void method1(){
 *    		
 *      }
 *    }
 *    
 *    参数特性:
 *    
 *    HttpServletRequest
 *    HttpServletResponse
 *    HttpSession
 *    Map
 *    List
 *    基本数据(int,float,double,short,byte,long,boolean)
 *    基本对象(String,Integer,Float,Double,Short,Byte,Long,Booean)
 *    基本数据和基本对象数组，如String[]
 *    配置对象(Date)
 *    扩展对象(任意，需要定制数据的Parser)
 *    
 *    
 *    思路：
 *    1、解析原始数据，并转成中间数据
 *       比如，接收一个完整的json数据，转成Map
 *    2、将中间数据转成方法的调用参数数组
 *    3、调用方法
 *    
 *    组件：
 *    1、路由：用于将url映射到对应的类和方法
 *    2、aop：
 *    3、action factory：根据Controller配置生成action
 *    4、参数解析器
 *    5、action
 *    6、
 *    
 *    
 *    
 *    
 *    
 * 
 * 
 * @author jzoom
 *
 */
public class ZoomFilter implements Filter {

	private ZoomWeb web;
	private static final Log log = LogFactory.getLog(ZoomFilter.class);
	
	public ZoomFilter() {
		
	}
	
	
	@Override
	public void destroy() {
		if(web!=null) {
			web.destroy();
			web = null;
		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		try {
			if(!web.handle( req,resp )) {
				chain.doFilter(req, resp);
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		
		try {
			web = new ZoomWeb();
			web.init();
		}catch (Throwable e) {
			log.error("启动失败!",e);
		}
		
	}

}
