package websvc.servant;

import static org.junit.Assert.fail;
import net.sf.json.JSONObject;

import org.junit.Test;

import com.tisson.common.util.HttpUtils;

import common.utils.SignUtil;
import common.xml.dp.DpInf13001Response;
import common.xml.dp.StaRequest;

public class INF13001Test {

	
	@Test
	public void responseTest() {
		try {
			DpInf13001Response dpInf13001Response = new DpInf13001Response();
			StaRequest staRequest = new StaRequest();
			staRequest.setCustomerId("13335381413@189.cn");
			staRequest.setInvokeId("INF");
			staRequest
					.setSign(SignUtil.getSign(staRequest, "bestpay987654321"));
			String json = JSONObject.fromObject(staRequest).toString();
			String rtnString = HttpUtils.queryJsonData(
					"http://127.0.0.1:8080/bppf_inf/queryMyBalance", json);
			System.out.println(rtnString);
			JSONObject jsonObject = JSONObject.fromObject(rtnString);
			String xmlStr = dpInf13001Response.toXMLStr("REQWEBSVRCODE", "RESPONSETYPE",
					"KEEP", "RESULT", "RESPONSECODE", "RESPONSECONTENT",
					"custCode", jsonObject);
			System.out.println(xmlStr);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test Fail:" + e.getMessage());
			
		}
	}
	
}
