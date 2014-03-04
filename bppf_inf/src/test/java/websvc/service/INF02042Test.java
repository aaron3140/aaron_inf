package websvc.service;

import org.junit.Test;

import websvc.InfService;
import websvc.servant.InfServiceTest;

import common.utils.SpringContextHelper;
import common.xml.dp.INF02042Request;

public class INF02042Test extends InfServiceTest {

//	@Autowired
	private InfService service;
	
	@Test
	public void testExecute() throws Exception {
		INF02042Request req= new INF02042Request();
		req.setMerId("8613062000084223");
		req.setChannelCode("20");
		req.setTmnNum("500012000001");
		req.setCustCode("test201391");
		req.setStaffCode("test201391");
		req.setIssueType("0");
		req.setOperType("0");
		req.setStart(1l);
		
//		JSONObject jsonObject = JSONObject.fromObject(req);
//		service.execute(jsonObject.toString());
		service = SpringContextHelper.context.getBean("INF02042", InfService.class);
		
		service.execute("");
//		service.setRequest(req);
//		 service.Handle();;
		
	}


}
