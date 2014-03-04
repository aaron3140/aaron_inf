package com.tisson.httpIposSvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import websvc.DealProcessorService;
import websvc.impl.DealProcessorServiceImpl;

import com.tisson.common.util.JsonTools;

/**
 * 
 * 本类描述:json-lib
 * 
 * @version: 企业帐户前置接口 v1.1
 * @author: aaronMing
 * @email: shuangming.yang@gmail.com
 * @time: 2013-4-30上午09:50:36
 */
@SuppressWarnings("serial")

public class DealJsonToPOJO extends HttpServlet {
	private static final Log log = LogFactory.getLog(DealJsonToPOJO.class);

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/json;utf-8");
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		String result = null;

		JSONObject JSONREQUEST = JSONObject.fromObject(request
				.getParameter("JSONREQUEST"));
		log.info("json in: " + JSONREQUEST.toString());
		JSONObject object0 = (JSONObject) (JSONREQUEST
				.getJSONArray("JSONREQUEST")).get(0);
		JSONObject object1 = (JSONObject) (JSONREQUEST
				.getJSONArray("JSONREQUEST")).get(1);
		// 将json转为xml
		String in0 = object0.getString("in0");
		String in1 = JsonTools.json2xml(object1.toString());

		try {
			DealProcessorService dealService = new DealProcessorServiceImpl();
//			result = dealService.dispatchCommandIPOS(in0, in1);
			result = dealService.dispatchCommandJsonLibIPOS(in0, in1);
			// 将xml转为json
			result = JsonTools.xml2json(result);
		} catch (Exception e) {
			result = null;
			 e.printStackTrace();
		}
		log.info("json out: " + result);
		response.getWriter().print(result);
	}
}
