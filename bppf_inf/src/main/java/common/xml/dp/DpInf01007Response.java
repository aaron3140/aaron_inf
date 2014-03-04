package common.xml.dp;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.dao.TInfDcoperlogDao;
import common.utils.ValueUtil;
import common.xml.CommonRespAbs;

/**
 * 交易明细查询接口
 * 
 */

public class DpInf01007Response extends CommonRespAbs {
	private static final Log log = LogFactory.getLog(DpInf01007Response.class);
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "RESULTDATESET";

	private static final String TAG_NAME_14_1 = "DATAS";

	private static final String[] ORDER_ATTR = { "TRANSSEQ", "ORDERSEQ",
			"CUSTCODE", "ORDERTYPE", "AREACODE", "TMNNUM", "ORDERTIME", "MEMO",
			"ORDERSTAT", "ORDERAMOUNT", "PAYSTAT" };

	private static final String[] BUSI_ATTR = { "BUSITIME", "BUSINAME",
			"BUSIPRICE", "BUSISTAT","OBJECTCODE" ,"BUSISYSREFNO"};

	private static final String[] PAY_ATTR = { "PAYTYPENAME", "PAYORGNAME",
			"ACCOUNT", "PAYAMOUNT", "CURRENCY", "PAYSTAT","PAYSYSREFNO" };
	private static final String[] BUSATTACH_ATTR = { "ITEMCODE", "ITEMNAME","ITEMTYPE","ITEMVALUE"};

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE,
			String KEEP, String RESULT, String RESPONSECODE,
			String RESPONSECONTENT, List<Map<String, String>> list) {
		String newResCode = newCode(RESPONSECODE);
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);
		root.addElement(TAG_NAME_12).addText(newResCode);
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);

		Element dataSet = root.addElement(TAG_NAME_14);
		Element datas = dataSet.addElement(TAG_NAME_14_1);
		Element item = datas.addElement("ORDERITEM");

		Map<String, String> mapOrder = list.get(list.size() - 1);

		for (String attr : ORDER_ATTR) {
			item.addAttribute(attr, ValueUtil.s(mapOrder.get(attr)));
		}

		Element busiDetails = dataSet.addElement("BUSIDETAILS");
		if (list.size() >= 3) {
			for (int i = 0; i < list.size() - 1; i++) {
				Map<String, String> mapItem = list.get(i);
				if (mapItem.get("MapType").equals("404")) {
					Element orderItem = busiDetails.addElement("BUSIDETAIL");
					for (String attr : BUSI_ATTR) {
						orderItem
								.addAttribute(attr, ValueUtil.s(mapItem.get(attr)));
					}
				}
				
			}
		}

		Element payDetails = dataSet.addElement("PAYDETAILS");
		if (list.size() >= 3) {
			for (int i = 0; i < list.size() - 1; i++) {
				Map<String, String> mapItem = list.get(i);
				if (mapItem.get("MapType").equals("408")) {
					Element orderItem = payDetails.addElement("PAYDETAIL");
					for (String attr : PAY_ATTR) {
						log.info("PAY_ATTR : " + mapItem.get(attr));
						orderItem.addAttribute(attr, ValueUtil.s(mapItem
								.get(attr)));
					}
				}

			}
		}
		Element busAttachDetails = dataSet.addElement("BUSATTACH");
		if (list.size() >= 3) {
			for (int i = 0; i < list.size() - 1; i++) {
				Map<String, String> mapItem = list.get(i);
				if (mapItem.get("MapType").equals("692")) {
					Element orderItem = busAttachDetails.addElement("BUSATTACHITEM");
					for (String attr : BUSATTACH_ATTR) {
						log.info("BUSATTACH_ATTR : " + mapItem.get(attr));
						orderItem.addAttribute(attr, ValueUtil.s(mapItem
								.get(attr)));
					}
				}
				
			}
		}
		return doc.asXML();
	}

	private static boolean is404busistat(String attr) {

		return (attr.equals("未处理") || attr.equals("处理中") || attr.equals("处理超时")
				|| attr.equals("处理失败") || attr.equals("处理成功") || attr
				.equals("已退款"));

	}
}
