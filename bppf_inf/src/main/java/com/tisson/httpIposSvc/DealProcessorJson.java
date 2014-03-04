package com.tisson.httpIposSvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tisson.common.util.RequestJson;
import com.tisson.common.util.ResponseJson;

import websvc.DealProcessorService;
import websvc.impl.DealProcessorServiceImpl;
import net.sf.json.JSONObject;

/**
 * 
 * 本类描述:
 * 
 * @version: 企业帐户前置接口 v1.0
 * @author: 广州天讯瑞达通讯技术有限公司(zhuxiaojun)
 * @email: zhuxiaojun@tisson.com
 * @time: 2013-4-30上午09:50:36
 */
public class DealProcessorJson extends HttpServlet {
	private static final Log log = LogFactory.getLog(DealProcessorJson.class);
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/json;utf-8");
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		String result = null;

		JSONObject JSONREQUEST = JSONObject.fromObject(request
				.getParameter("JSONREQUEST"));
		log.info("json in: "+JSONREQUEST.toString());
		JSONObject object0 = (JSONObject) (JSONREQUEST
				.getJSONArray("JSONREQUEST")).get(0);
		JSONObject object1 = (JSONObject) (JSONREQUEST
				.getJSONArray("JSONREQUEST")).get(1);
		// 将json转为xml
		RequestJson requestJson = new RequestJson(object0, object1);
		String in0 = requestJson.getIn0();
		String in1 = requestJson.getIn1();

		try {
		DealProcessorService dealService = new DealProcessorServiceImpl();
		result = dealService.dispatchCommandIPOS(in0, in1);
			// 将xml转为json
			ResponseJson responseJson = new ResponseJson(result);
			result = responseJson.xml2Json();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result = null;
//			e.printStackTrace();
		}
		log.info("json out: " + result);
		response.getWriter().print(result);
	}
}
