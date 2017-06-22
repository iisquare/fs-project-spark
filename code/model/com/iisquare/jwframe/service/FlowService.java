package com.iisquare.jwframe.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import com.iisquare.jwframe.mvc.ServiceBase;
import com.iisquare.jwframe.utils.DPUtil;
import com.iisquare.jwframe.utils.FileUtil;

@Service
@Scope("singleton")
public class FlowService extends ServiceBase {
	
	@Autowired
	protected WebApplicationContext webApplicationContext;
	private static List<Map<String, Object>> generateTree = null;
	private static Set<String> generateJars = null;
	
	public List<Map<String, Object>> generateTree(Map<String, Map<String, Object>> itemMap, String parent) {
		Map<Integer, Map<String, Object>> map = new TreeMap<>();
		for (Entry<String, Map<String, Object>> entry : itemMap.entrySet()) {
			String key = entry.getKey();
			Map<String, Object> value = entry.getValue();
			if(!parent.equals(value.get("parent"))) continue;
			if(value.containsKey("children")) value.put("children", generateTree(itemMap, key));
			map.put(DPUtil.parseInt(value.get("sort")), value);
		}
		return new ArrayList<>(map.values());
	}
	
	public String getPluginsPath() {
		String path = System.getProperty("ETL_HOME");
		if(null == path) {
			path = "";
		} else {
			path += "/";
		}
		return path + "plugins";
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> generateTree(boolean forceReload) {
		if(!forceReload && null != generateTree) return generateTree;
		File pluginsDir = new File(getPluginsPath());
		if(!pluginsDir.exists() || !pluginsDir.isDirectory()) return generateTree = new ArrayList<>();
		Map<String, Map<String, Object>> itemMap = new LinkedHashMap<>();
		for (File file : pluginsDir.listFiles()) {
			String json = FileUtil.getContent(file.getAbsolutePath() + "/config.json");
			if(null == json) continue;
			List<?> itemList = DPUtil.parseJSON(json, List.class);
			if(null == itemList) continue;
			for (Object obj : itemList) {
				Map<String, Object> item = (Map<String, Object>) obj;
				itemMap.put(item.get("id").toString(), item);
			}
		}
		return generateTree = generateTree(itemMap, "");
	}
	
	public Set<String> generateJars(boolean forceReload) {
		if(!forceReload && null != generateJars) return generateJars;
		Set<String> jarsSet = new HashSet<>();
		File pluginsDir = new File(getPluginsPath());
		if(!pluginsDir.exists() || !pluginsDir.isDirectory()) return generateJars = jarsSet;
		for (File file : pluginsDir.listFiles()) {
			if(!file.isDirectory()) continue;
			for (File jar : file.listFiles()) {
				if(!jar.getName().endsWith(".jar")) continue;
				jarsSet.add(jar.getAbsolutePath());
			}
		}
		return generateJars = jarsSet;
	}
	
}
