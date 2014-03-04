package common.xml;

import java.security.cert.X509Certificate;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultAttribute;

import common.algorithm.MD5;
import common.dao.BaseDao;
import common.dao.TSymCerDao;
import common.utils.ChannelCode;
import common.utils.Charset;
import common.utils.SpringContextHelper;
import common.utils.verify.NETCAPKI;

import framework.exception.INFErrorDef;
import framework.exception.INFException;

public abstract class CommonReqAbsLogin {
	private static final String CHARSET = "UTF-8";

	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	protected String merId;

	protected String channelCode;

	protected String tmnNum;

	protected String sign;

	protected String cer;
	
	private String keep;
	
	private String ip;

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getTmnNum() {
		return tmnNum;
	}

	public void setTmnNum(String tmnNum) {
		this.tmnNum = tmnNum;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getCer() {
		return cer;
	}

	public void setCer(String cer) {
		this.cer = cer;
	}

	public String getKeep() {
		return keep;
	}

	public void setKeep(String keep) {
		this.keep = keep;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public CommonReqAbsLogin(String xmlStr, Object reserved) throws Exception {
		Document doc = DocumentHelper.parseText(xmlStr);

		// 获取签名信息
		setVerifyParameters(doc);
		
		init(doc, reserved);
		
		//客户端接入采用MD5验证
		if (!ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
			//非客户端接入采用证书验证
			// 验证商户签名
			verify(xmlStr);
			// 校验机构接入号和终端号绑定关系
			verifyMeridTmnNum();
		}
		
		// 获取其他参数
		setParameters(doc, reserved);
		
		// 参数CheckerAnnotion标记检查
		callChecker();
	}

	public void verifyMeridTmnNum() throws Exception {
		BaseDao DAO = SpringContextHelper.getBaseDaoBean();
		String sql = "select distinct t.link_num from t_pnm_server t where t.prtn_id = " +
				"(select prtn.prtn_id from t_pnm_partner prtn where prtn.prtn_code = '" + merId + "')";
		String linkNum = null;
		try {
			linkNum = (String) DAO.queryForObject(sql, java.lang.String.class);
		} catch (Exception e) {
			throw new Exception("机构接入号的绑定终端号异常");
		}
		linkNum = Charset.trim(linkNum);
		if( ! linkNum.equals(tmnNum)) {
			throw new Exception("机构接入号和绑定终端号不匹配");
		}
		
	}

	@SuppressWarnings("unchecked")
	private void setVerifyParameters(Document doc) throws Exception {
		
//		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
			merId = getNodeText(doc, "MERID");
			channelCode = getNodeText(doc, "CHANNELCODE");
			tmnNum = getNodeText(doc, "TMNNUM");
			cer = getNodeText(doc, "CER");
			sign = getNodeText(doc, "SIGN");
//		}else{
//			merId = getNodeTextM(doc, "MERID");
//			channelCode = getNodeTextM(doc, "CHANNELCODE");
//			tmnNum = getNodeTextM(doc, "TMNNUM");
//			sign = getNodeTextM(doc, "SIGN");
//			cer = getNodeTextM(doc, "CER");
//		}
		
		Element element = (Element) getNodeM(doc, "CTRL-INFO");
		List<DefaultAttribute> list = element.attributes();
		for (DefaultAttribute attribute : list) {
			if("KEEP".equals(attribute.getName())){
				keep = attribute.getValue();
			}
			
			if("APPFROM".equals(attribute.getName())){
				String appFrom = attribute.getValue();
				String[] appFroms = appFrom.split("-");
				if(appFroms.length>0){
					ip = appFroms[appFroms.length-1];
				}
			}
		}
	}
	
	public void verifyByMD5(String xmlStr, String key) throws Exception {
		xmlStr = xmlStr + "<KEY>" + key + "</KEY>";
		String md5Str = MD5.MD5Encode(xmlStr);
		if (!md5Str.equals(sign)) {
			throw new INFException(INFErrorDef.MD5_SIGN_VAREFY_FAIL, "SIGN验证无效");
		}
	}
	
	public void verify(String xmlStr) throws Exception {
		// 将cer从base64转换为对象
		X509Certificate cerObj = NETCAPKI.getX509Certificate(cer);

		// 获取证书指纹
		String fingerprint = NETCAPKI.getX509CertificateInfo(cerObj, 1);

		// 检查证书是否有效或证书是否存在对应的表TSymCer记录
		if (!TSymCerDao.isCerStatValid(fingerprint)) {
			throw new INFException(INFErrorDef.CER_INVALID, "证书无效");
		}

		// 检查商户状态是否正常
		// MerchantCache.getSpInfo(merId, true);

		// 检查证书是否属于该商户
		if (!TSymCerDao.isCerOwnedByMerId(fingerprint, merId)) {
			throw new INFException(INFErrorDef.CER_NOTOWNED_BY_MER, "证书不匹配接入机构");
		}

		// 验证sign是否正确
		// Element e = (Element) getNodeM(doc, "PayPlatRequestParameter");
		// System.out.println("e:" + e.asXML());
		// byte[] src = e.asXML().getBytes(CHARSET);
		
		String pay = xmlSubString(xmlStr);
		if (!NETCAPKI.verifyPKCS1(pay.getBytes(CHARSET), NETCAPKI.base64Decode(sign), cerObj)) {
			throw new INFException(INFErrorDef.CER_SIGN_VAREFY_FAIL, "签名验证失败");
		}

	}
	
	public String xmlSubString(String xmlStr){
		String pay = xmlStr.substring(xmlStr.indexOf("<PayPlatRequestParameter>"),
				xmlStr.indexOf("</PayPlatRequestParameter>") + "</PayPlatRequestParameter>".length());
		return pay;
	}

	public void init(Document doc, Object reserved) throws Exception {
	};
	
	public abstract void setParameters(Document doc, Object reserved)
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

	protected String getNodeText(Document doc, String nodeName) {
		Element e = (Element) doc.selectSingleNode("//" + nodeName);
		return (e == null) ? "" : e.getTextTrim();
	}

	protected Node getNodeM(Document doc, String nodeName) throws Exception {
		Node n = doc.selectSingleNode("//" + nodeName);

		if (n == null) {
			throw new Exception(nodeName + "不能为空");
		}

		return n;
	}

	protected String getNodeTextM(Document doc, String nodeName)
			throws Exception {
		Element e = (Element) getNodeM(doc, nodeName);

		if ("".equals(e.getTextTrim())) {
			throw new Exception(nodeName + "不能为空");
		}

		return e.getTextTrim();
	}

}
