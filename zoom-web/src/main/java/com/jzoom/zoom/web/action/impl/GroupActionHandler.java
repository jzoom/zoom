package com.jzoom.zoom.web.action.impl;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jzoom.zoom.web.action.ActionHandler;

public class GroupActionHandler implements ActionHandler {
	
	private ActionHandler[] actionHandlers;
	
	public GroupActionHandler( ActionHandler... actionHandlers ) {
		this.actionHandlers = actionHandlers;
	}
	
	public static ActionHandler from( ActionHandler handler1,ActionHandler handler2 ) {
		if(handler1==null)
			return handler2;
		if(handler1 instanceof GroupActionHandler) {
			return from( (GroupActionHandler)handler1 , handler2);
		}
		return new GroupActionHandler(handler2,handler1);
	}
	
	public static GroupActionHandler from( GroupActionHandler groupActionHandler,ActionHandler handler ) {
		int len = groupActionHandler.actionHandlers.length ;
		ActionHandler[] actionHandlers = new ActionHandler[ len + 1 ];
		for(int i=1; i <= len ;++i) {
			actionHandlers[i] = groupActionHandler.actionHandlers[i];
		}
		actionHandlers[0] = handler;
		Arrays.fill(groupActionHandler.actionHandlers, null);
		groupActionHandler.actionHandlers = null;
		return new GroupActionHandler(actionHandlers);
	}
	

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response) {
		for (ActionHandler actionHandler : actionHandlers) {
			if(actionHandler.handle(request, response)) {
				return true;
			}
		}
		return false;
	}


	public ActionHandler[] getActionHandlers() {
		return actionHandlers;
	}


	public void setActionHandlers(ActionHandler[] actionHandlers) {
		this.actionHandlers = actionHandlers;
	}

	
}
