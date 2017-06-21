package com.iisquare.jwframe.test;

import java.util.List;
import java.util.Map;

import com.iisquare.jwframe.service.FlowService;
import com.iisquare.jwframe.utils.DPUtil;

public class Tester {

	public static void main(String[] args) throws Exception {
		FlowService flowService = new FlowService();
		List<Map<String, Object>> list = flowService.generateTree(true);
		System.out.println(DPUtil.stringifyJSON(list));
	}

}
