package common.xml.dp;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.utils.MathTool;
import common.xml.CommonRespAbs;

/**
 * @title DpInf02049Response.java
 * @description 理财明细列表查询接口回复类
 * @date 2014-02-11 09:35
 * @author lichunan
 * @version 1.0
 */
public class DpInf02049Response extends CommonRespAbs {
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "RESULTDATESET";

	private static final String TAG_NAME_14_1 = "ITEMNUM";

	private static final String TAG_NAME_14_2 = "DATAS";

	private static final String TAG_NAME_14_2_1 = "DETAILITEM";

	private static final String[] DETAIL_ATTR = { "DETAILTYPE", "DETAILAMOUNT",
			"DETAILDATE" };

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE,
			String KEEP, String RESULT, String RESPONSECODE,
			String RESPONSECONTENT, String ITEMNUM,
			List<Map<String, Object>> list) {

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);

		root.addElement(TAG_NAME_12).addText(newCode(RESPONSECODE));
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);

		if (list != null && list.size() > 0) {
			Element dataSet = root.addElement(TAG_NAME_14);
			dataSet.addElement(TAG_NAME_14_1).addText(ITEMNUM);
			Element datas = dataSet.addElement(TAG_NAME_14_2);
			for (int i = 0; i < list.size(); i++) {
				Element item = datas.addElement(TAG_NAME_14_2_1);
				Map<String, Object> map = list.get(i);
				for (String attr : DETAIL_ATTR) {
					String output = convert(attr);
					String value = (String) map.get(output);
					if("amount".equals(output)){
						value = MathTool.yuanToPoint(value);
					}
					item.addAttribute(attr, value);
				}
			}
		}
		return doc.asXML();
	}
	
	/**
	 * 进行转换操作
	 * @param input
	 * @return
	 */
	private String convert(String input){
		String output = "";
		if("DETAILTYPE".equals(input)){
			output = "operType";
		}else if("DETAILAMOUNT".equals(input)){
			output = "amount";
		}else if("DETAILDATE".equals(input)){
			output = "operTime";
		}
		return output;
	}
	
}
