package common.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.dao.TInfErrorCodeDao;
import common.entity.TInfErrorCode;
import common.utils.Charset;
import common.utils.TInfErrorCodeUtil;

import framework.exception.INFErrorDef;

public class CommonRespAbs implements XmlLoserIntf {

	protected static final String TAG_NAME1 = "PayPlatResponseParameter";

	protected static final String TAG_NAME_11 = "RESPONSE-INFO";

	protected static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	protected static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	protected static final String TAG_NAME_11_ATTR_3 = "KEEP";

	protected static final String TAG_NAME_11_ATTR_4 = "RESULT";

	protected static final String TAG_NAME_12 = "RESPONSECODE";

	protected static final String TAG_NAME_13 = "RESPONSECONTENT";

	public String toCommonXmlStr(String webSvrCode, String responseType,
			String keep, String result, String responseCode,
			String responseContent)  {
		// responseCode补充到6位
		// responseCode补充到6位
		if(Charset.isEmpty(responseCode, true)||responseCode.length()<4){
//			responseCode=INFErrorDef.GWM_NET_ERRCODE;
//			responseContent=INFErrorDef.GWM_NET_ERRCODE_DESC;
			responseCode="011007";
			responseContent="订单状态未确定";
		}
		String newResCode = Charset.lpad(responseCode, 6, "0");
		// 从内存中获取数据
		HashMap<String, TInfErrorCode> map = TInfErrorCodeUtil.map;
		if (map == null) {
			TInfErrorCodeDao dao = new TInfErrorCodeDao();
			TInfErrorCodeUtil.map = dao.getErrorMap();
			map = TInfErrorCodeUtil.map;
		}
		TInfErrorCode tInfErrorCode = map.get(responseCode);
		if (tInfErrorCode != null) {
			if (tInfErrorCode.getErrorCode() != null) {
				newResCode = tInfErrorCode.getErrorCode();
			}
			String errorInfo = tInfErrorCode.getErrorInfo();
			if (errorInfo != null && !errorInfo.equals("")) {
				responseContent = errorInfo;
			}
		}

		// TInfErrorCodeDao dao = new TInfErrorCodeDao();
		// responseContent=dao.getErrorInfo(newResCode,responseContent);

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, webSvrCode);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, responseType);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, keep);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, result);

		root.addElement(TAG_NAME_12).addText(newResCode);
		root.addElement(TAG_NAME_13).addText(responseContent);

		return doc.asXML();
	}

	/**
	 * 转换respInfo.respBody，支持深度list,map
	 * 
	 * @param respInfo
	 * @return
	 */
	public static String toXMLStr(RespInfo respInfo) {

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, respInfo.getReqWebsvrCode());
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, respInfo.getRespType());
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, respInfo.getKeep());
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, "SUCCESS");

		root.addElement(TAG_NAME_12).addText(
				newCode(respInfo.getResponseCode()));
		root.addElement(TAG_NAME_13).addText(respInfo.getResponseContent());

		Element dataSet = root.addElement("RESULTDATESET");
		Map<String, Object> respBody = respInfo.getRespBody();
		if(null==respBody||respBody.size()==0)
			return doc.asXML();
		if (respBody.entrySet().size() != 1&&!(respBody.values().iterator().next() instanceof List))
			convertSingelToXml(dataSet, respBody);
		else if (null != respBody)
			convertListToXml(dataSet, respBody);

		return doc.asXML();

	}

	@SuppressWarnings("unchecked")
	private static void convertListToXml(Element dataSet,
			Map<String, Object> respBody) {
		Set<Entry<String, Object>> entrySet = respBody.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (key != null) {
				if(key.equals("countNoReadItem")||key.equals("countItem"))
					dataSet.addElement(key.toString()).addText(value.toString());
				else if (value == null) {
					dataSet.addAttribute(key.toString(), "");
				} else if (value instanceof List) {
					 Element addListElement = dataSet.addElement("CARDITEM");
					for (Map<String, Object> subEntity : (List<Map<String, Object>>) value) {
						Element addElement = addListElement.addElement(key);
						convertListToXml(addElement, subEntity);
					}
				} else if (value instanceof Map) {
					Element addElement = dataSet.addElement(key);
					convertSingelToXml(addElement, (Map<String, Object>) value);
				} else
					dataSet.addAttribute(key.toString(), value.toString());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void convertSingelToXml(Element dataSet,
			Map<String, Object> respBody) {
		Set<Entry<String, Object>> entrySet = respBody.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (key != null) {
				if(key.equals("countNoReadItem")||key.equals("countItem"))
					dataSet.addElement(key.toString()).addText(value.toString());
				else if (value == null) {
					dataSet.addAttribute(key.toString(), "");
				} else if (value instanceof List) {
					for (Map<String, Object> subEntity : (List<Map<String, Object>>) value) {
						Element addElement = dataSet.addElement(key);
						convertListToXml(addElement, subEntity);
					}
				} else if (value instanceof Map) {
					Element addElement = dataSet.addElement(key);
					convertListToXml(addElement, (Map<String, Object>) value);
				} else
					dataSet.addElement(key.toString())
							.addText(value.toString());
			}
		}
	}

	public String makeFailXmlStr(String resCode, String reason) {
		String newResCode = newCode(resCode);
		return toCommonXmlStr("", "", "", "FAIL", newResCode, reason);
	}

	// responseCode补充到6位
	public static String newCode(String resCode) {
	    if(Charset.isEmpty(resCode, true)||resCode.length()<4){
	         resCode="011007";
        }
		return Charset.lpad(resCode, 6, "0");
	}

}
