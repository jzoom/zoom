package com.jzoom.zoom.admin.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jzoom.zoom.admin.entities.DecoTableVo;
import com.jzoom.zoom.admin.entities.DecoTableVo.DecoColumn;
import com.jzoom.zoom.common.json.JSON;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.adapter.NameAdapter;
import com.jzoom.zoom.dao.meta.ColumnMeta;
import com.jzoom.zoom.dao.meta.TableMeta;
import com.jzoom.zoom.ioc.annonation.Inject;

public class TableModel {

	@Inject
	private Dao dao;

	@Inject("admin")
	private Dao admin;

	@SuppressWarnings("unchecked")
	public DecoTableVo getTable(String table, boolean getmap) {
		TableMeta data = dao.getDbStructFactory().getTableMeta(dao.ar(), table);
		// NameAdapter adapter = dao.getPolicy(table);
		dao.getDbStructFactory().fill(dao.ar(), data);
		// 下发数据
		Record record = admin.table("sys_deco_table").where("target_table", table).fetch();
		List<Record> columns = admin.table("sys_decoration").where("target_table", table).get();
		NameAdapter nameAdapter = dao.getNameAdapter(table);
		DecoTableVo vo = new DecoTableVo();
		vo.setComment(data.getComment());
		vo.setName(data.getName());
		List<DecoColumn> list = new ArrayList<DecoTableVo.DecoColumn>();
		Map<String, DecoColumn> map = new HashMap<String, DecoColumn>();
		for (ColumnMeta columnMeta : data.getColumns()) {
			DecoColumn decoColumn = new DecoColumn();
			decoColumn.setColumn(columnMeta.getName());
			decoColumn.setName(nameAdapter.getFieldName(columnMeta.getName()));
			decoColumn.setComment(columnMeta.getComment());
			map.put(columnMeta.getName(), decoColumn);
			list.add(decoColumn);
		}
		vo.setColumns(list);
		ColumnMeta[] primaryKeys = data.getPrimaryKeys();
		vo.setPrimaryKey(  primaryKeys.length > 0 ? nameAdapter.getFieldName(primaryKeys[0].getName()) : "id" );
		if (record != null) {
			vo.setComment(record.getString("comment"));
			if (columns.size() > 0) {
				for (Record record2 : columns) {
					String name = record2.getString("target_column");
					DecoColumn decoColumn = map.get(name);
					decoColumn.setComment(record2.getString("comment"));
					decoColumn.setType(record2.getString("type"));
					String prop = record2.getString("prop");

					if (prop != null && prop.length() > 0) {
						if (getmap) {
							try {
								Map<String, Object> propMap = JSON.parse(prop, Map.class);
								decoColumn.setProp(propMap);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else {
							decoColumn.setProp(prop);
						}

					}
				}
			}
		}

		return vo;
	}

	public List<Record> getTempltes(String template) {
		List<Record> list = admin.table("sys_template").where("type", template).get();

		return list;
	}

	public void insertOrUpdate(String name, String content) {
		int count = admin.table("sys_page").where("url", name).set("template", content).update();
		if (count <= 0) {
			admin.table("sys_page").set("url", name).set("template", content).insert();
		}
	}
}
