package websvc.service;

import net.sf.json.JSONObject;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import websvc.servant.InfServiceTest;

import common.xml.dp.INF02041Request;

public class INF02041Test extends InfServiceTest {

	@Autowired
	private INF02041 service;

	@Test
	public void testExecute() {
		INF02041Request req = new INF02041Request();
		req.setMerId("8613062000084223");
		req.setChannelCode("20");
		req.setTmnNum("500012000001");
		req.setCustCode("test201391");
		req.setStaffCode("test201391");
		req.setIssueType("0");
		req.setOperType("0");
		req.setIssueId("32");

		JSONObject jsonObject = JSONObject.fromObject(req);
		service.execute(jsonObject.toString());

	}

}
