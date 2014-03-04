package websvc.servant;

import static org.junit.Assert.fail;
import net.sf.json.JSONObject;

import org.junit.Test;

import com.tisson.common.util.HttpUtils;
import common.utils.SignUtil;
import common.xml.dp.DpInf13002Response;
import common.xml.dp.RankListQueryRequest;

public class INF13002Test {

	
	@Test
	public void responseTest() {
		try {
			RankListQueryRequest rankListQueryRequest = new RankListQueryRequest();
			rankListQueryRequest.setCustomerId("13335381413@189.cn");
			rankListQueryRequest.setInvokeId("INF");
			rankListQueryRequest.setSign(SignUtil.getSign(rankListQueryRequest,
					"bestpay987654321"));
			String json = JSONObject.fromObject(rankListQueryRequest)
					.toString();
			String rtnString = HttpUtils.queryJsonData(
					"http://127.0.0.1:8081/queryRankListPosition", json);
			JSONObject jsonObject = JSONObject.fromObject(rtnString);
			System.out.println(rtnString);
			DpInf13002Response dpInf13002Response = new DpInf13002Response();
			String xmlstr = dpInf13002Response.toXMLStr("REQWEBSVRCODE", "RESPONSETYPE", "KEEP", "RESULT", "RESPONSECODE", "RESPONSECONTENT", "custCode", jsonObject);
			System.out.println(xmlstr);
		} catch (Exception e) {
			fail("Not yet implemented");
		}
		
	}
	
}
