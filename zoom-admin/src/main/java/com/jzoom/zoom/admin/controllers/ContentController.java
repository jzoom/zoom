package com.jzoom.zoom.admin.controllers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jzoom.zoom.admin.models.BaseDao;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.web.annotation.Controller;
import com.jzoom.zoom.web.annotation.JsonResponse;
import com.jzoom.zoom.web.annotation.Mapping;
import com.jzoom.zoom.web.annotation.Param;
import com.jzoom.zoom.web.exception.StatusException;
import com.jzoom.zoom.web.exception.StatusException.NotFoundException;
import com.jzoom.zoom.web.view.impl.StringView;

@Controller(key = "")
public class ContentController {
	
	public ContentController() {
		module2table.put("module", "sys_module");
		module2table.put("template", "sys_template");
		module2table.put("decoration", "sys_decoration");
		module2table.put("deco_table", "sys_deco_table");
		module2table.put("component", "sys_component");
	}

	@Inject("admin")
	private Dao dao;

	/**
	 * 模块到表的映射
	 */
	private Map<String, String> module2table = new ConcurrentHashMap<String, String>();

	/**
	 * 表对应的dao
	 */
	private Map<String, BaseDao> daoMap = new ConcurrentHashMap<String, BaseDao>();

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
	public List<Record> list(@Param(name = "{module}") String module,@Param(name="@")Map<String, Object> search) throws NotFoundException {
		return getDao(module).getList(search );
	}
 
	private BaseDao getDao(String module) throws NotFoundException {
		String table = module2table.get(module);
		if (table == null) {
			table = module;
		}
		BaseDao dao = daoMap.get(table);
		if (dao == null) {
			dao = new BaseDao(table);
			dao.setDao(this.dao);
			daoMap.put(table, dao);
		}

		return dao;
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
		Record record = dao.table("sys_page").where("url", module + method).fetch();
		if (record == null) {
			throw new StatusException.NotFoundException();
		}
		return new StringView(record.getString("template"));
	}

}
