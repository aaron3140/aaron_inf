package common.xml.dp;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;

import common.utils.ParamValid;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf01009Request extends CommonReqAbs {
	
	public DpInf01009Request(String xmlStr) throws Exception {
		super(xmlStr, null);

	}	
	@CheckerAnnotion(len = 30, type = CheckerAnnotion.TYPE_STR)
	private String eventSeq;
	
	@CheckerAnnotion(len = 30, type = CheckerAnnotion.TYPE_STR)
	private String objCode;
	
	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_STR)
	private String objType;
	
	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_STR)
	private String payAmount;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String paytime;
	
	@CheckerAnnotion(len = 30, type = CheckerAnnotion.TYPE_STR)
	private String orgCode;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String callBackURL;
	
	private List itemList;
	
	@Override
	public void init(Document doc, Object reserved) throws Exception {
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		
		ParamValid paramValid = new ParamValid();
		String resultDesc=paramValid.Valid(doc,"INF_01_009");
		if (!resultDesc.equals("0")) {
			throw new Exception (resultDesc);
		}
		eventSeq = getNodeTextM(doc, "EVENTSEQ");
		objCode = getNodeTextM(doc, "OBJCODE");
		objType = getNodeTextM(doc, "OBJTYPE");
		payAmount = getNodeTextM(doc, "PAYAMOUNT");
		paytime = getNodeTextM(doc, "PAYTIME");
		orgCode = getNodeTextM(doc, "ORGCODE");
		callBackURL = getNodeTextM(doc, "CALLBACKURL");
		Element element = (Element) getNodeM(doc, "EXTITEM");
		List<DefaultAttribute> list = element.attributes();
		itemList = new ArrayList();
		for (DefaultAttribute attribute : list) {
			itemList.add(attribute.getValue());
		}
	}

	public String getEventSeq() {
		return eventSeq;
	}

	public void setEventSeq(String eventSeq) {
		this.eventSeq=eventSeq;
	}

	public String getObjCode() {
		return objCode;
	}

	public void setObjCode(String objCode) {
		this.objCode=objCode;
	}

	public String getObjType() {
		return objType;
	}

	public void setObjType(String objType) {
		this.objType=objType;
	}

	public String getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(String payAmount) {
		this.payAmount=payAmount;
	}

	public String getPaytime() {
		return paytime;
	}

	public void setPaytime(String paytime) {
		this.paytime=paytime;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode=orgCode;
	}

	public String getCallBackURL() {
		return callBackURL;
	}

	public void setCallBackURL(String callBackURL) {
		this.callBackURL=callBackURL;
	}

	public List getItemList() {
		return itemList;
	}

	public void setItemList(List itemList) {
		this.itemList=itemList;
	}
}
