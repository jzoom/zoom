package com.jzoom.zoom.admin.controllers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jzoom.zoom.admin.models.BaseDao;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.web.annotation.Controller;
import com.jzoom.zoom.web.annotation.JsonResponse;
import com.jzoom.zoom.web.annotation.Mapping;
import com.jzoom.zoom.web.annotation.Param;
import com.jzoom.zoom.web.exception.StatusException;
import com.jzoom.zoom.web.exception.StatusException.NotFoundException;
import com.jzoom.zoom.web.view.impl.StringView;

@Controller()
public class AutoController {
	
	@Inject
	private IocContainer ioc;
	
	@Inject("admin")
	private Dao defaultDao;
	
	/**
	 * 表对应的dao
	 */
	private Map<String, BaseDao> daoMap = new ConcurrentHashMap<String, BaseDao>();

	@Mapping(value = "{dao}/{module}/index", method = Mapping.GET)
	public StringView index(
			@Param(name="{dao}") String dao,
			@Param(name = "{module}") String module) throws NotFoundException {
		return createPage(dao,module, "/index");
	}
	
	@Mapping(value = "{dao}/{module}/add", method = Mapping.GET)
	public StringView add(
			@Param(name="{dao}") String dao,
			@Param(name = "{module}") String module
			) throws NotFoundException {
		return createPage(dao,module, "/add");
	}
	

	@Mapping(value = "{dao}/{module}/edit", method = Mapping.GET)
	public StringView edit(
			@Param(name="{dao}") String dao,
			@Param(name = "{module}") String module) throws NotFoundException {
		return createPage(dao,module, "/edit");
	}

	@Mapping(value = "{dao}/{module}/index", method = { Mapping.POST })
	@JsonResponse
	public List<Record> list(
			@Param(name="{dao}") String dao,
			@Param(name = "{module}") String module,@Param(name="@")Map<String, Object> search) throws NotFoundException {
		return getDao(dao,module).getList(search );
	}
	
	

	@JsonResponse
	@Mapping(value = "{dao}/{module}/add", method = { Mapping.POST })
	public int add(@Param(comment = "提交的表单信息", name = Param.BODY) Map<String, Object> data,
			
			@Param(name="{dao}") String dao,
			@Param(name = "{module}") String module) throws NotFoundException {
		return getDao(dao,module).add(data);
	}

	@JsonResponse
	@Mapping(value = "{dao}/{module}/del/{id}", method = { Mapping.POST })
	public int del(
			@Param(name="{dao}") String dao,
			@Param(name = "{module}") String module, @Param(name = "{id}") String id) throws NotFoundException {
		return getDao(dao,module).del(id);
	}

	@JsonResponse
	@Mapping(value = "{dao}/{module}/get/{id}", method = { Mapping.POST })
	public Record get(
			@Param(name="{dao}") String dao,
			@Param(name = "{module}") String module, @Param(name = "{id}") String id)
			throws NotFoundException {
		return getDao(dao,module).fetch(id);

	}
	
	@JsonResponse
	@Mapping(value = "{dao}/{module}/put/{id}", method = { Mapping.POST })
	public int put(
			@Param(name="{dao}") String dao,
			@Param(name = "{module}") String module, @Param(name = "{id}") String id,
			@Param(name="@") Map<String, Object> data)
			throws NotFoundException {
		return getDao(dao,module).put(id, data);
	}
	
	private StringView createPage(
			String dao,String module, String method) throws NotFoundException {
		Record record = defaultDao.table("sys_page").where("url", module + method).fetch();
		if (record == null) {
			throw new StatusException.NotFoundException();
		}
		return new StringView(record.getString("template"));
	}
	
	private String getKey(String db, String module) {
		return new StringBuilder().append(db).append(":").append(module).toString();
	}

	private BaseDao getDao( String db, String module) throws NotFoundException {
		String key = getKey(db, module);
		BaseDao baseDao = daoMap.get(  key );
		if (baseDao == null) {
			synchronized (daoMap) {
				baseDao = new BaseDao(module,"id");
				Dao dao = ioc.get(db);
				baseDao.setDao(  dao );
				daoMap.put(key, baseDao);
			}
			
			
		}

		return baseDao;
	}

}
