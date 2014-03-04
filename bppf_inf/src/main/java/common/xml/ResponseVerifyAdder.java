package common.xml;

import java.security.PrivateKey;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.algorithm.MD5;
import common.utils.Charset;
import common.utils.verify.NETCAPKI;

import framework.exception.INFErrorDef;

public class ResponseVerifyAdder {

	private static final String CHARSET = "UTF-8";

	private static final String TAG_NAME1 = "Response";

	private static final String TAG_NAME_11 = "VerifyParameter";

	private static final String TAG_NAME_111 = "SIGN";

	private static final String TAG_NAME_112 = "CER";

	private static String SERVER_CER_BASE64 = null;
	
	private static PrivateKey PRIVATE_KEY = null;

	public static String pkg(String oldXml) {
		try {
			if (SERVER_CER_BASE64 == null) {
				SERVER_CER_BASE64 = NETCAPKI.getX509CertificateString(NETCAPKI
						.getSrvX509Certificate());
				PRIVATE_KEY = NETCAPKI.getPrivateKey();
			}

			Document doc1 = DocumentHelper.parseText(oldXml);

			Document doc2 = DocumentHelper.createDocument();
			Element root = doc2.addElement(TAG_NAME1);
			Element e1 = root.addElement(TAG_NAME_11);
			// sign
			Element e11 = e1.addElement(TAG_NAME_111);
			byte[] signB = NETCAPKI.signPKCS1(doc1.getRootElement().asXML()
					.getBytes(CHARSET), PRIVATE_KEY);
			e11.addText(NETCAPKI.base64Encode(signB));

			// cer
			Element e12 = e1.addElement(TAG_NAME_112);
			e12.addText(SERVER_CER_BASE64);

			//
			root.add(doc1.getRootElement());

			return doc2.asXML();
		} catch (Exception e) {
			return new CommonRespAbs().makeFailXmlStr(
					INFErrorDef.RESPONSE_REPKG_FAIL, "返回包添加签名信息失败");
		}
	}
	
	public static String pkgForMD5(String oldXml) {
		return pkgForMD5(oldXml, null);
	}
	public static String pkgForMD5(String oldXml,String key) {
		try {
			
			Document doc1 = DocumentHelper.parseText(oldXml);
			
			Document doc2 = DocumentHelper.createDocument();
			Element root = doc2.addElement(TAG_NAME1);
			Element e1 = root.addElement(TAG_NAME_11);
			// sign
			Element e11 = e1.addElement(TAG_NAME_111);
			
			String encode = "";
			if (!Charset.isEmpty(key)) {
				encode = MD5.MD5Encode(oldXml + "<KEY>" + key + "</KEY>" );
			}
			e11.addText(encode);
			
			// cer
			Element e12 = e1.addElement(TAG_NAME_112);
			e12.addText("");
			
			//
			root.add(doc1.getRootElement());
			
			return doc2.asXML();
		} catch (Exception e) {
			return new CommonRespAbs().makeFailXmlStr(
					INFErrorDef.RESPONSE_REPKG_FAIL, "返回包添加签名信息失败");
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
	}

}
