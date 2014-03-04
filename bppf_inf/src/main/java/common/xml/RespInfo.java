package common.xml;

import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class RespInfo {
	
	private String respType;
	
	private String reqWebsvrCode;
	
	private String result;
	
	private String keep;
	
	private String responseCode;
	
	private  Map<String, Object> respBody;

	public Map<String, Object> getRespBody() {
		return respBody;
	}

	public void setRespBody(Map<String, Object> respBody) {
		this.respBody = respBody;
	}

	private String responseContent;

	public RespInfo(String xmlStr, String respType) throws Exception {
		Document doc = DocumentHelper.parseText(xmlStr);
		Element ele = (Element) doc.selectSingleNode("//CTRL-INFO");
		this.keep = ele.valueOf("@KEEP");
		this.reqWebsvrCode = ele.valueOf("@WEBSVRCODE");
		this.respType = respType;
	}

	public String getKeep() {
		return keep;
	}


	public String getReqWebsvrCode() {
		return reqWebsvrCode;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public String getResponseContent() {
		return responseContent;
	}
	
	
	
	public String getRespType() {
		return respType;
	}

	public String getResult() {
		return result;
	}

	public void setKeep(String keep) {
		this.keep = keep;
	}


	public void setReqWebsvrCode(String reqWebsvrCode) {
		this.reqWebsvrCode = reqWebsvrCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public void setResponseContent(String responseContent) {
		this.responseContent = responseContent;
	}

	public void setRespType(String respType) {
		this.respType = respType;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
}
