package com.jzoom.zoom.admin.controllers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jzoom.zoom.admin.models.BaseDao;
import com.jzoom.zoom.admin.models.BaseDao.DaoId;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Page;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.adapter.NameAdapter;
import com.jzoom.zoom.dao.driver.DbStructFactory;
import com.jzoom.zoom.dao.meta.ColumnMeta;
import com.jzoom.zoom.dao.meta.TableMeta;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.web.annotation.Controller;
import com.jzoom.zoom.web.annotation.JsonResponse;
import com.jzoom.zoom.web.annotation.Mapping;
import com.jzoom.zoom.web.annotation.Param;
import com.jzoom.zoom.web.exception.StatusException;
import com.jzoom.zoom.web.exception.StatusException.NotFoundException;
import com.jzoom.zoom.web.view.impl.StringView;

@Controller(key = "")
public class ContentController  implements AdminController{
	
	public ContentController() {
		module2table.put("module", "sys_module");
		module2table.put("template", "sys_template");
		module2table.put("decoration", "sys_decoration");
		module2table.put("deco_table", "sys_deco_table");
		module2table.put("component", "sys_component");
		
		module2table.put("sys_menu", "sys_menu");
		module2table.put("sys_page", "sys_page");
		module2table.put("sys_role", "sys_role");
		module2table.put("sys_type", "sys_type");
		module2table.put("sys_usercase", "sys_usercase");
		module2table.put("sys_user", "sys_user");
		module2table.put("sys_context", "sys_context");
		module2table.put("sys_mod", "sys_mod");
		module2table.put("sys_deco_table", "sys_deco_table");
		module2table.put("sys_decoration", "sys_decoration");
		module2table.put("sys_usecase", "sys_usecase");
		module2table.put("sys_component", "sys_component");
		module2table.put("sys_dic", "sys_dic");
		module2table.put("sys_plugin", "sys_plugin");
	}
	
	
	@Inject
	private Dao dao;

	@Inject("admin")
	private Dao admin;

	/**
	 * 模块到表的映射
	 */
	private Map<String, String> module2table = new ConcurrentHashMap<String, String>();

	/**
	 * 表对应的dao
	 */
	private Map<String, BaseDao> daoMap = new ConcurrentHashMap<String, BaseDao>();

 
	private BaseDao getDao(String module) throws NotFoundException {
		String table = module2table.get(module);
		Dao dao;
		DbStructFactory struct;
		if (table == null) {
			//default
			dao = this.dao;
			table = module;
			
		}else {
			dao = this.admin;
		}
		
		
		BaseDao baseDao = daoMap.get(table);
		if (baseDao == null) {
			synchronized (daoMap) {
				//查询一下
				DaoId id;
				try {
					struct = dao.getDbStructFactory();
					TableMeta meta = struct.getTableMeta(dao.ar(), table);
					struct.fill(dao.ar(), meta);
					NameAdapter adapter = dao.getNameAdapter(table);
					
					id = BaseDao.getDaoId(meta, adapter);
					
				}catch (Exception e) {
					id = BaseDao.newDaoId("id");
				}
				
				baseDao = new BaseDao(table,id);
				baseDao.setDao(dao);
				daoMap.put(table, baseDao);
			}
		}
		
		return baseDao;
	}
	

	@Mapping(value = "{module}/index", method = Mapping.GET)
	public StringView index(@Param(name = "{module}") String module) throws NotFoundException {
		return createPage(module, "/index");
	}
	
	@Mapping(value = "{module}/add", method = Mapping.GET)
	public StringView add(@Param(name = "{module}") String module) throws NotFoundException {
		return createPage(module, "/add");
	}
	

	@Mapping(value = "{module}/edit", method = Mapping.GET)
	public StringView edit(@Param(name = "{module}") String module) throws NotFoundException {
		return createPage(module, "/edit");
	}


	@Mapping(value = "{module}/index", method = { Mapping.POST })
	@JsonResponse
	public Page<Record> index(@Param(name = "{module}") String module,@Param(name="@")Map<String, Object> search) throws NotFoundException {
		return getDao(module).getPage(search );
	}
	
	
	@Mapping(value="{module}/list",method= {Mapping.POST})
	@JsonResponse
	public List<Record> list(@Param(name = "{module}") String module,@Param(name="@")Map<String, Object> params) throws NotFoundException{
		return getDao(module).getList(  params );
	}
	
	
	@JsonResponse
	@Mapping(value = "{module}/add", method = { Mapping.POST })
	public int add(@Param(comment = "提交的表单信息", name = Param.BODY) Map<String, Object> data,
			@Param(name = "{module}") String module) throws NotFoundException {
		return getDao(module).add(data);
	}

	@JsonResponse
	@Mapping(value = "{module}/del/{id}", method = { Mapping.POST })
	public int del(@Param(name = "{module}") String module, @Param(name = "{id}") String id) throws NotFoundException {
		return getDao(module).del(id);
	}

	@JsonResponse
	@Mapping(value = "{module}/get/{id}", method = { Mapping.POST })
	public Record get(@Param(name = "{module}") String module, @Param(name = "{id}") String id)
			throws NotFoundException {
		return getDao(module).fetch(id);
	}
	
	@JsonResponse
	@Mapping(value = "{module}/put/{id}", method = { Mapping.POST })
	public int put(@Param(name = "{module}") String module, @Param(name = "{id}") String id,
			@Param(name="@") Map<String, Object> data)
			throws NotFoundException {
		return getDao(module).put(id, data);
	}
	
	private StringView createPage(String module, String method) throws NotFoundException {
		Record record = admin.table("sys_page").where("url", module + method).fetch();
		if (record == null) {
			throw new StatusException.NotFoundException();
		}
		return new StringView(record.getString("template"));
	}

}
