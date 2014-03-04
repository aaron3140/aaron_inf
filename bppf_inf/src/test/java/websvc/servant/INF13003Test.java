package websvc.servant;

import static org.junit.Assert.fail;
import net.sf.json.JSONObject;

import org.junit.Test;

import com.tisson.common.util.HttpUtils;
import common.utils.SignUtil;
import common.xml.dp.DpInf13003Response;
import common.xml.dp.RankListDetailRequest;

public class INF13003Test {

	
	@Test
	public void responseTest() {
		try {
			RankListDetailRequest rankListDetailRequest = new RankListDetailRequest();
			rankListDetailRequest.setCustomerId("13335381413@189.cn");
			rankListDetailRequest.setInvokeId("INF");
			rankListDetailRequest.setRankListType("1");
			rankListDetailRequest.setSearchMonth("");
			rankListDetailRequest.setSign(SignUtil.getSign(
					rankListDetailRequest, "bestpay987654321"));
			String json = JSONObject.fromObject(rankListDetailRequest)
					.toString();
			String rtnString = HttpUtils.queryJsonData(
					"http://127.0.0.1:8081/queryRankListInfo", json);
			System.out.println(rtnString);
			JSONObject jsonObject = JSONObject.fromObject(rtnString);
			DpInf13003Response dpInf13003Response = new DpInf13003Response();
			String xmlStr = dpInf13003Response.toXMLStr("REQWEBSVRCODE", "RESPONSETYPE", "KEEP", "RESULT", "RESPONSECODE", "RESPONSECONTENT", "custCode", jsonObject);
			System.out.println(xmlStr);
		} catch (Exception e) {
			e.printStackTrace();
			fail("fail");
		}  
	}
	
}
