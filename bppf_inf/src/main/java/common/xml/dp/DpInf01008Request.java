package common.xml.dp;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;

import common.utils.ParamValid;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf01008Request extends CommonReqAbs {
	
	public DpInf01008Request(String xmlStr) throws Exception {
		super(xmlStr, null);

	}	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String objCode;
	
	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_STR)
	private String objType;
	
	@CheckerAnnotion(len = 30, type = CheckerAnnotion.TYPE_STR)
	private String orgCode;
	
	private List itemList;
	
	@Override
	public void init(Document doc, Object reserved) throws Exception {
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		
		ParamValid paramValid = new ParamValid();
		String resultDesc=paramValid.Valid(doc,"INF_01_008");
		if (!resultDesc.equals("0")) {
			throw new Exception (resultDesc);
		}
		objCode = getNodeTextM(doc, "OBJCODE");
		objType = getNodeTextM(doc, "OBJTYPE");
		orgCode = getNodeTextM(doc, "ORGCODE");
		Element element = (Element) getNodeM(doc, "EXTITEM");
		List<DefaultAttribute> list = element.attributes();
		itemList = new ArrayList();
		for (DefaultAttribute attribute : list) {
			itemList.add(attribute.getValue());
		}
	}


	public List getItemList() {
		return itemList;
	}

	public void setItemList(List itemList) {
		this.itemList = itemList;
	}

	public String getObjCode() {
		return objCode;
	}

	public void setObjCode(String objCode) {
		this.objCode = objCode;
	}

	public String getObjType() {
		return objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

}
