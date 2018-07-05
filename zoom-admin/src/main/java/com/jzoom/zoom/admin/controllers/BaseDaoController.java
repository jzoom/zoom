package com.jzoom.zoom.admin.controllers;

import java.util.List;
import java.util.Map;

import com.jzoom.zoom.admin.models.BaseDao;
import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.web.annotation.JsonResponse;
import com.jzoom.zoom.web.annotation.Mapping;
import com.jzoom.zoom.web.annotation.Param;
import com.jzoom.zoom.web.utils.WebUtils;

public class BaseDaoController<T extends BaseDao> {
	
	@Inject
	private Dao baseDao;

	private T model;
	
	
	@SuppressWarnings("unchecked")
	@Inject
	public void init() {
		Class<?> type = (Class<?>) Classes.getTypeParams(getClass())[0];
		try {
			model = (T) WebUtils.getIoc().get(type);
			model.setDao(baseDao);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	@Mapping(value="index",method= {Mapping.GET})
	public Object index(){
		return null;
	}
	
	@Mapping(value="add",method= {Mapping.GET})
	public Object add() {
		return null;
	}
	
	@Mapping(value="edit",method= {Mapping.GET})
	public Object edit() {
		return null;
	}
	
	
	
	
	@Mapping(value="index",method= {Mapping.POST})
	@JsonResponse
	public List<Record> list(){
		return model.getList();
	}
	
	/**
	 * 新增
	 * @param data
	 */
	@JsonResponse
	@Mapping(value="add",method= {Mapping.POST})
	public void add( 
			@Param( comment="提交的表单信息",name=Param.BODY)  Map<String, Object> data
		) {
		model.add( data );
	}
	
	
	/**
	 * 获取详细
	 * @param id
	 * @return
	 */
	@JsonResponse
	@Mapping(value="get/{id}",method= {Mapping.POST})
	public Record get(@Param(name="{id}",comment="id") String id) {
		return model.fetch(id);
	}
	
	/**
	 * 修改
	 * @param data
	 */
	@JsonResponse
	@Mapping(value="put/{id}",method= {Mapping.POST})
	public void put( @Param(name="@") Map<String, Object> data, @Param(name="{id}") String id ) {
		 model.put(id,data);
	}
	
	@Mapping(value="del/{id}",method= {Mapping.POST})
	@JsonResponse
	public int del(@Param(name="{id}")String id) {
		return model.del(id);
	}
}
