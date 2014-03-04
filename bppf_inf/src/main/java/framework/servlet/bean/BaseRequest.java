package framework.servlet.bean;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.utils.Charset;
import common.xml.StringAttrChecker;

public abstract class BaseRequest {
	public BaseRequest(HttpServletRequest request) throws Exception {

		init();

		setParameters(request);

		callChecker();
	}

	public void init() throws Exception {
	};

	public abstract void setParameters(HttpServletRequest request)
			throws Exception;

	protected void callChecker() throws Exception {
		StringAttrChecker.checkFields(this);
	}

	protected String getAttr(Element ele, String attrName) {
		return ele.valueOf("@" + attrName);
	}

	protected String getAttrM(Element ele, String attrName) throws Exception {
		String s = getAttr(ele, attrName);
		if (Charset.isEmpty(s))
			throw new Exception(attrName + "不能为空");
		return s;
	}
	
}
