package com.iisquare.jwframe.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import com.iisquare.jwframe.Configuration;
import com.iisquare.jwframe.dao.RoleDao;
import com.iisquare.jwframe.dao.SettingDao;
import com.iisquare.jwframe.dao.UserDao;
import com.iisquare.jwframe.mvc.ServiceBase;
import com.iisquare.jwframe.utils.DPUtil;
import com.iisquare.jwframe.utils.ServiceUtil;

@Service
@Scope("singleton")
public class RoleService extends ServiceBase {
	
	@Autowired
	protected WebApplicationContext webApplicationContext;
	@Autowired
	protected Configuration configuration;
	
	public Map<String, String> getStatusMap() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", "禁用");
		map.put("1", "正常");
		return map;
	}
	
	public int delete(Object ...ids) {
		if(DPUtil.empty(ids)) return -1;
		RoleDao dao = webApplicationContext.getBean(RoleDao.class);
		return dao.where("id in ("
			+ DPUtil.implode(",", DPUtil.arrayToIntegerArray(ids)) + ")", new HashMap<>()).delete().intValue();
	}
	
	public Map<Object, Object> search(Map<String, Object> map, String orderBy, int page, int pageSize) {
		StringBuilder condition = new StringBuilder("1=1");
		Map<String, Object> params = new HashMap<String, Object>();
		Object name = map.get("name");
		if(!DPUtil.empty(name)) {
			condition.append(" and name like :name");
			params.put(":name", "%" + name + "%");
		}
		Object status = map.get("status");
		if(!DPUtil.empty(status)) {
			condition.append(" and status = :status");
			params.put(":status", status);
		}
		Object description = map.get("description");
		if(!DPUtil.empty(description)) {
			condition.append(" and description like :description");
			params.put(":description", "%" + description + "%");
		}
		Object timeStart = map.get("timeStart");
		if(!DPUtil.empty(timeStart)) {
			condition.append(" and update_time >= :timeStart");
			params.put(":timeStart", DPUtil.dateTimeToMillis(timeStart, configuration.getDateTimeFormat()));
		}
		Object timeEnd = map.get("timeEnd");
		if(!DPUtil.empty(timeEnd)) {
			condition.append(" and update_time <= :timeEnd");
			params.put(":timeEnd", DPUtil.dateTimeToMillis(timeEnd, configuration.getDateTimeFormat()));
		}
		RoleDao dao = webApplicationContext.getBean(RoleDao.class);
		int total = dao.where(condition.toString(), params).count().intValue();
		List<Map<String, Object>> rows = dao.orderBy(orderBy).page(page, pageSize).all();
		rows = ServiceUtil.fillFields(rows, new String[]{"status"}, new Map<?, ?>[]{getStatusMap()}, null);
		UserDao userDao = webApplicationContext.getBean(UserDao.class);
		rows = ServiceUtil.fillRelations(rows, userDao,
				new String[]{"create_uid", "update_uid"}, new String[]{"id", "username", "name"}, null);
		return DPUtil.buildMap(new String[]{"total", "rows"}, new Object[]{total, rows});
	}
	
	public boolean exists(String type, String key) {
		SettingDao settingDao = webApplicationContext.getBean(SettingDao.class);
		Number result = settingDao.select("content")
				.where("type=:type and parameter=:parameter", ":type", type, ":parameter", key).count();
		return null != result && result.intValue() > 0;
	}
	
	public boolean setProperty(String type, String key, String value) {
		if(null == type) type = "system";
		SettingDao settingDao = webApplicationContext.getBean(SettingDao.class);
		Number result;
		if(exists(type, key)) {
			Map<String, Object> data = new HashMap<>();
			data.put("content", value);
			result = settingDao.where("type=:type and parameter=:parameter", ":type", type, ":parameter", key).update(data);
		} else {
			Map<String, Object> data = new HashMap<>();
			data.put("type", type);
			data.put("parameter", key);
			data.put("content", value);
			result = settingDao.insert(data);
		}
		return null != result;
	}
	
	public String getProperty(String type, String key, String defaultValue) {
		if(null == type) type = "system";
		SettingDao settingDao = webApplicationContext.getBean(SettingDao.class);
		Map<String, Object> info = settingDao.select("content")
				.where("type=:type and parameter=:parameter", ":type", type, ":parameter", key).one();
		if(null == info) return defaultValue;
		Object value = info.get("content");
		if(null == value) return defaultValue;
		return value.toString();
	}
	
	public boolean insert(Map<String, Object> data) {
		SettingDao settingDao = webApplicationContext.getBean(SettingDao.class);
		return null != settingDao.insert(data);
	}
	
	public boolean update(Map<String, Object> data) {
		SettingDao settingDao = webApplicationContext.getBean(SettingDao.class);
		return null != settingDao.where("type=:type and parameter=:parameter",
			":type", data.get("type"), ":parameter", data.get("parameter")).update(data);
	}
	
	public boolean save(Map<String, Object> data) {
		if(exists(DPUtil.parseString(data.get("type")), DPUtil.parseString(data.get("parameter")))) {
			return insert(data);
		} else {
			return update(data);
		}
	}
	
	public Map<String, Object> getInfo(String type, String key) {
		if(null == type || null == key) return null;
		SettingDao settingDao = webApplicationContext.getBean(SettingDao.class);
		return settingDao.where("type=:type and parameter=:parameter", ":type", type, ":parameter", key).one();
	}

}
