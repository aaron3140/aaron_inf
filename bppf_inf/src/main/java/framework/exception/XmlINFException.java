package framework.exception;

import common.xml.CommonRespAbs;
import common.xml.RespInfo;
import common.xml.XmlLoserIntf;

public class XmlINFException extends INFException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

	private XmlLoserIntf xmlResp = null;
	
	private RespInfo respInfo = null;
	
	private Exception relateException = null;
	
	
	public RespInfo getRespInfo() {
		return respInfo;
	}

	public void setRespInfo(RespInfo respInfo) {
		this.respInfo = respInfo;
	}

	public void setRelateException(Exception relateException) {
		this.relateException = relateException;
	}
	
	public Exception getRelateException() {
		return relateException;
	}
	
	
	public XmlINFException(String msg, XmlLoserIntf xmlResp) {
		super(msg);
		this.xmlResp = xmlResp;
	}
	
	public XmlINFException(String msg, XmlLoserIntf xmlResp, RespInfo respInfo) {
		super(msg);
		this.xmlResp = xmlResp;
		this.respInfo = respInfo;
	}
	
	public XmlINFException(XmlLoserIntf xmlResp, Exception relateException) {
		super(relateException.getMessage());
		this.xmlResp = xmlResp;
		this.relateException = relateException;
	}
	
	public XmlINFException(XmlLoserIntf xmlResp, Exception relateException, RespInfo respInfo) {
		super(relateException.getMessage());
		this.xmlResp = xmlResp;
		this.relateException = relateException;
		this.respInfo = respInfo;
	}
	
	public XmlINFException(Exception relateException, RespInfo respInfo) {
		super(relateException.getMessage());
		this.xmlResp = new CommonRespAbs();
		this.relateException = relateException;
		this.respInfo = respInfo;
	}

	public void setXmlResp(XmlLoserIntf xmlResp) {
		this.xmlResp = xmlResp;
	}
	
	public XmlLoserIntf getXmlResp() {
		return xmlResp;
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
