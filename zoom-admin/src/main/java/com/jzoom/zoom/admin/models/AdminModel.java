package com.jzoom.zoom.admin.models;

import java.util.List;
import java.util.Map;

import com.jzoom.zoom.dao.Page;


public interface AdminModel<T extends Map<String, Object>> {
	Page<T> getPage( Map<String, Object> search );
	int put(String id,Map<String, Object> data) ;
	int add(Map<String, Object> data) ;
	T fetch(String id);
	int del(String id);
}
