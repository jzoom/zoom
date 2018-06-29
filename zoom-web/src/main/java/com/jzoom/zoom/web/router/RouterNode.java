package com.jzoom.zoom.web.router;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.web.action.ActionHandler;
import com.jzoom.zoom.web.action.impl.GroupActionHandler;


/**
 * 路由节点
 * @author jzoom
 *
 * @param 
 */
public class RouterNode {

	
	Map<String, RouterNode> children;
	
	int level;
	
	private ActionHandler action;
	
	PatternRouterNode pattern;
	
	
	
	public RouterNode(int level) {
		this.level = level;
	}
	
	public ActionHandler match(String[] parts, HttpServletRequest request) {
		
		final int level = this.level;
		if(parts.length == level) {
			return action;
		}
		String current = parts[level];
		if(children==null) {
			//已经是最终节点了
			if(pattern==null) {
				return null;
			}
			return pattern.match(parts, request);
		}
		
		//查找同级别的
		RouterNode node = children.get(current);
		if(node==null) {
			//模糊匹配的处理,对于同一级别的，如果没有找到模糊匹配，那么就是null
			if(pattern==null)return null;
			return pattern.match(parts, request);
		}
		
		return node.match(parts, request);
	}

	/**
	 * 注册路由
	 * @param parts
	 * @param rule
	 * @param value
	 */
	public void register(String[] parts, RouterParamRule rule, ActionHandler value) {
		if(parts ==null || parts.length==0) {
			throw new RuntimeException("parts长度必须大于0");
		}
		final int level = this.level;
		String current = parts[level];
		String paramName = rule.getParamName(current);
		if(paramName!=null) {
			//不一定有下级
			if(pattern==null) {
				pattern = new PatternRouterNode(level+1,paramName);
			}
			if(parts.length-1==level) {
				pattern.setAction(value);
				return;
			}
			pattern.register(parts, rule,value);
		}else {
			//当前的key为下级注册
			if(children==null) {
				children = new ConcurrentHashMap<String, RouterNode>();
			}
			RouterNode child = children.get(current);
			if(child==null) {
				child = new RouterNode(level+1);
				children.put(current, child);
			}
			if(parts.length-1==level) {
				child.setAction(value);
				return;
			}
			child.register(parts, rule,value);
		}
		
		
	}

	/**
	 * 清除所有路由
	 */
	public void clear() {
		if(children!=null) {
			for (Entry<String,RouterNode> entry : children.entrySet()) {
				entry.getValue().clear();
			}
			children.clear();
			children = null;
		}
		
		if(pattern!=null) {
			pattern.clear();
			pattern = null;
		}
		
		if(action!=null) {
			Classes.destroy(action);
			action = null;
		}
		
		
	}

	public ActionHandler getAction() {
		return action;
	}

	public void setAction(ActionHandler action) {
		this.action = GroupActionHandler.from(this.action, action);
	}

}
