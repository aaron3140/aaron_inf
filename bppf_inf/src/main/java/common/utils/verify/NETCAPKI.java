package common.utils.verify;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import common.algorithm.BASE64Encoder;
import common.utils.NetcapkiUtil;

public class NETCAPKI {
	public NETCAPKI() {
	}

	// certificate转化成base64编码
	public static String getX509CertificateString(
			java.security.cert.X509Certificate oCert) throws Exception {
		return new BASE64Encoder().encode(oCert.getEncoded());
	}

	// Base64 编码证书的文件转换成certificate
	public static java.security.cert.X509Certificate getX509Certificate(
			String sBase64Cert) throws CertificateException, IOException{
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		// final java.io.ByteArrayInputStream streamCertificate = new java.io.
		// ByteArrayInputStream
		// (sBase64Cert.getBytes("UTF-8"));

		final java.io.ByteArrayInputStream streamCertificate = new java.io.ByteArrayInputStream(
				new sun.misc.BASE64Decoder().decodeBuffer(sBase64Cert));

		X509Certificate testCert = (java.security.cert.X509Certificate) cf
				.generateCertificate(streamCertificate);
		;

		return testCert;

	}

	/*
	// 获取服务器证书
	public static java.security.cert.X509Certificate getSrvX509Certificate()
			throws Exception {
		String srvKeyStore = UtilProperties.getKeyStore();
		String srvKeyStorePwd = UtilProperties.getKeyStorePwd();
		String srvAlias = UtilProperties.getAlias();
		if (srvKeyStore.length() <= 0) {
			throw new Exception("配置文件中keystore 未配置");
		}

		if (srvAlias.length() <= 0) {
			throw new Exception("配置文件中alias 未配置");
		}
		if (srvKeyStorePwd.length() <= 0) {
			throw new Exception("配置文件中password 未配置");
		}

		File file = new File(srvKeyStore);
		if (!file.exists()) {
			throw new Exception("配置文件中配置的服务器证书不存在");
		}
		java.security.KeyStore keystore = java.security.KeyStore
				.getInstance("JKS");
		InputStream fileinputstream = new FileInputStream(file);
		keystore.load(fileinputstream, srvKeyStorePwd.toCharArray());
		fileinputstream.close();
		return (X509Certificate) keystore.getCertificate(srvAlias);
	}*/
	
	/**
	 * 获取服务器证书
	 */
	public static java.security.cert.X509Certificate getSrvX509Certificate()
			throws Exception {
		if(NetcapkiUtil.x509Certificate==null){
			String srvKeyStore = UtilProperties.getKeyStore();
			String srvKeyStorePwd = UtilProperties.getKeyStorePwd();
			String srvAlias = UtilProperties.getAlias();
			if (srvKeyStore.length() <= 0) {
				throw new Exception("配置文件中keystore 未配置");
			}

			if (srvAlias.length() <= 0) {
				throw new Exception("配置文件中alias 未配置");
			}
			if (srvKeyStorePwd.length() <= 0) {
				throw new Exception("配置文件中password 未配置");
			}

			File file = new File(srvKeyStore);
			if (!file.exists()) {
				throw new Exception("配置文件中配置的服务器证书不存在");
			}
			java.security.KeyStore keystore = java.security.KeyStore
					.getInstance("JKS");
			InputStream fileinputstream = new FileInputStream(file);
			keystore.load(fileinputstream, srvKeyStorePwd.toCharArray());
			fileinputstream.close();
			NetcapkiUtil.x509Certificate=(X509Certificate) keystore.getCertificate(srvAlias);
		}
		return NetcapkiUtil.x509Certificate;
	}

	// 获取证书信息
	public static String getX509CertificateInfo(
			java.security.cert.X509Certificate oCert, int iValueType) throws NoSuchAlgorithmException, CertificateEncodingException
			 {
		if (oCert == null) {
			return "";
		}
		if (iValueType == 1) {
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte ab[] = md.digest(oCert.getEncoded());
			return hexToStr(ab);
		} else if (iValueType == 2) {
			return oCert.getSerialNumber().toString();
		} else if (iValueType == 3) {
			return oCert.getSubjectDN().getName();
		} else if (iValueType == 4) {
			return oCert.getIssuerDN().getName();
		}

		else if (iValueType == 5) {
			return oCert.getNotBefore().toString();
		} else if (iValueType == 6) {
			return oCert.getNotAfter().toString();
		} else if (iValueType == 7) {
			return oCert.getKeyUsage().toString();
		} else if (iValueType == 11) {
			return oCert.getNotAfter().toString();
		} else if (iValueType == 12) {
			return getCN(oCert);
		} else if (iValueType == 13) {
			return getO(oCert);
		} else if (iValueType == 14) {
			return getL(oCert);
		} else if (iValueType == 15) {
			return getEmailAddress(oCert);
		} else if (iValueType == 16) {
			return getOU(oCert);
		}

		else if (iValueType == 17) {
			return getC(oCert);
		}

		else if (iValueType == 18) {
			return getST(oCert);
		} else if (iValueType == 21) {
			return "1";
		}

		return "";

	}

	public static byte[] signPKCS7(byte[] bSource, boolean IsNotHasSource)
			throws Exception {
		byte[] signedBytes;
		String strSignMode = net.netca.jce.SimplePKCS7SignerInfo.SHA1;
		String srvKeyStore = UtilProperties.getKeyStore();
		String srvKeyStorePwd = UtilProperties.getKeyStorePwd();
		String srvAlias = UtilProperties.getAlias();
		if (srvKeyStore.length() <= 0) {
			throw new Exception("配置文件中keystore 未配置");
		}

		if (srvAlias.length() <= 0) {
			throw new Exception("配置文件中alias 未配置");
		}
		if (srvKeyStorePwd.length() <= 0) {
			throw new Exception("配置文件中password 未配置");
		}

		File file = new File(srvKeyStore);
		if (!file.exists()) {
			throw new Exception("配置文件中配置的服务器证书不存在");
		}
		net.netca.jce.SimpleKeyStore keystore = new net.netca.jce.SimpleKeyStore();
		keystore.open(srvKeyStore, srvKeyStorePwd);

		X509Certificate cert = (X509Certificate) keystore
				.getCertificate(srvAlias);
		java.security.cert.Certificate[] chain = keystore
				.getCertificateChain(srvAlias);

		net.netca.jce.SimplePKCS7SignerInfo signerInfo = new net.netca.jce.SimplePKCS7SignerInfo();
		signerInfo.setCertificate(cert);
		signerInfo.setDigestAlgorithm(strSignMode);

		net.netca.jce.SimplePKCS7SignedData signedData = new net.netca.jce.SimplePKCS7SignedData();
		signedData.addCertficates(chain);
		signedData.setContent(bSource);
		signedData.setDetached(IsNotHasSource);
		signedBytes = signedData.sign(signerInfo, keystore, srvKeyStorePwd); // New
																				// String(keystore.getKeyStorePs())
		return signedBytes;
	}

	public static java.security.cert.X509Certificate verifyPKCS7(
			byte[] bSource, byte[] bSignature, boolean isNotHasSource)
			throws Exception {
		ByteArrayInputStream baIn = new ByteArrayInputStream(bSignature);
		net.netca.jce.SimplePKCS7SignedData signedData2 = new net.netca.jce.SimplePKCS7SignedData(
				baIn);
		if (isNotHasSource) {// 没有原文，设置原文
			signedData2.setContent(bSource);
			signedData2.setDetached(isNotHasSource);
		}
		if (signedData2.verify()) {
			if (isNotHasSource) {// 没有原文
				net.netca.jce.SimplePKCS7SignerInfo signerInfo2[] = signedData2
						.getSimplePKCS7SignerInfos();
				X509Certificate signcert = signerInfo2[0].getCertificate();
				return signcert;

			} else {// 有原文，与原文比对
				if (Arrays.equals(bSource, signedData2.getContent())) {
					// 验证签名成功
					net.netca.jce.SimplePKCS7SignerInfo signerInfo2[] = signedData2
							.getSimplePKCS7SignerInfos();
					X509Certificate signcert = signerInfo2[0].getCertificate();
					return signcert;
				}

			}
			throw new Exception("签名与原文不符");
		}
		throw new Exception("签名信息验证不通过");
	}

	/**
	 * 功能：PKCS#1签名 纯签名，签名数据较短，验证要有签名证书才可验证
	 * 
	 * @param abyte0
	 *            byte[] 原始信息
	 * @return byte[] 签名结果
	 */

	public static byte[] signPKCS1(byte[] bSource) throws Exception {
		PrivateKey pk = getPrivateKey();
		return signPKCS1(bSource, pk);
	}

	public static byte[] signPKCS1(byte[] bSource, PrivateKey pk)
			throws Exception {
		Signature sig = Signature.getInstance("SHA1withRSA"); // SHA1withRSA
																// sha1WithRSA
		sig.initSign(pk);
		sig.update(bSource);
		return sig.sign();

	}

	public static PrivateKey getPrivateKey() throws Exception {
		String srvKeyStore = UtilProperties.getKeyStore();
		String srvKeyStorePwd = UtilProperties.getKeyStorePwd();
		String srvAlias = UtilProperties.getAlias();
		if (srvKeyStore.length() <= 0) {
			throw new Exception("配置文件中keystore 未配置");
		}

		if (srvAlias.length() <= 0) {
			throw new Exception("配置文件中alias 未配置");
		}
		if (srvKeyStorePwd.length() <= 0) {
			throw new Exception("配置文件中password 未配置");
		}

		File file = new File(srvKeyStore);
		if (!file.exists()) {
			throw new Exception("配置文件中配置的服务器证书不存在");
		}

		net.netca.jce.SimpleKeyStore ks = new net.netca.jce.SimpleKeyStore();
		ks.open(srvKeyStore, srvKeyStorePwd);
		PrivateKey pk = (PrivateKey) ks.getPrivateKey(srvAlias, srvKeyStorePwd);
		ks.close();

		return pk;
	}

	public static boolean verifyPKCS1(byte[] bSource, byte[] bSignature,
			java.security.cert.X509Certificate oCert) throws Exception {
		Signature sig = Signature.getInstance("SHA1withRSA");
		sig.initVerify(oCert.getPublicKey());
		sig.update(bSource);
		return sig.verify(bSignature);
	}

	public static String base64Encode(byte[] sSource) throws Exception {

		return new BASE64Encoder().encode(sSource);
	}

	public static byte[] base64Decode(String sSource) throws Exception {
		return new sun.misc.BASE64Decoder().decodeBuffer(sSource);
	}

	/**
	 * 主测试软件
	 * 
	 * @param arg
	 *            String[]
	 * @throws Exception
	 */
	public static void main(String arg[]) throws Exception {
		// 获取服务器证书
		java.security.cert.X509Certificate oCert = getSrvX509Certificate();
		for (int i = 1; i < 8; i++) {
			System.out.println("" + i + ":" + getX509CertificateInfo(oCert, i)
					+ "\n");
		}
		for (int i = 11; i < 19; i++) {
			System.out.println("" + i + ":" + getX509CertificateInfo(oCert, i)
					+ "\n");
		}

		String b64 = getX509CertificateString(oCert);
		System.out.println(b64);
		System.out.println("-----------------------------------");
		System.out.println(getX509CertificateString(getX509Certificate(b64)));

		System.out.println("证书指纹：" + getX509CertificateInfo(oCert, 1));

		byte[] bSource = "我是陆汉民".getBytes();
		/**
		 * ********************** PKCS7 签名
		 * ******************************************
		 */
		// 带原文签名
		byte[] bSignData = signPKCS7(bSource, false);
		java.security.cert.X509Certificate cert2 = verifyPKCS7(bSource,
				bSignData, false);
		// 不带原文签名
		byte[] bSignData2 = signPKCS7(bSource, true);
		java.security.cert.X509Certificate cert3 = verifyPKCS7(bSource,
				bSignData2, true);
		/**
		 * ********************** PKCS1 签名
		 * ******************************************
		 */
		byte[] bSignData3 = signPKCS1(bSource);
		boolean isOK = verifyPKCS1(bSource, bSignData3, oCert);
	}

	private static String hexToStr(byte pBytes[]) {
		byte NUMBER_KEY = 48;
		byte UPPER_KEY = 55;
		byte LOWER_KEY = 87;
		byte HEX_KEY = 16;
		String result = "";
		for (int i = 0; i < pBytes.length; i++) {
			int tmpInt = (new Byte(pBytes[i])).intValue();
			if (tmpInt < 0) {
				tmpInt += 256;
			}
			byte strList[] = new byte[2];
			strList[1] = (new Integer(tmpInt % 16)).byteValue();
			strList[0] = (new Integer((tmpInt / 16) % 16)).byteValue();
			if (strList[1] > 9 && strList[1] < 16) {
				strList[1] += UPPER_KEY;
			}
			if (strList[1] >= 0 && strList[1] < 10) {
				strList[1] += NUMBER_KEY;
			}
			if (strList[0] > 9 && strList[0] < 16) {
				strList[0] += UPPER_KEY;
			}
			if (strList[0] >= 0 && strList[0] < 10) {
				strList[0] += NUMBER_KEY;
			}
			result = result + new String(strList);
		}

		return result;
	}

	/**
	 * 得到邮件地址
	 */

	private static String getEmailAddress(
			java.security.cert.X509Certificate cert) {
		if (cert != null) {
			try {
				sun.security.x509.X500Name p = (sun.security.x509.X500Name) cert
						.getSubjectDN();
				sun.security.util.ObjectIdentifier objectIdentifier = new sun.security.util.ObjectIdentifier(
						"1.2.840.113549.1.9.1");
				sun.security.util.DerValue dv = p
						.findMostSpecificAttribute(objectIdentifier);

				return dv.getAsString();
			} catch (Exception e) {
				String dn = cert.getSubjectDN().getName();
				return parseDN(dn, "EMAILADDRESS");
			}
		}
		return null;

	}

	/**
	 * 得到部门信息
	 */
	private static String getOU(java.security.cert.X509Certificate cert) {
		if (cert != null) {
			try {
				sun.security.x509.X500Name p = (sun.security.x509.X500Name) cert
						.getSubjectDN();
				return p.getOrganizationalUnit();
			} catch (Exception e) {
				String dn = cert.getSubjectDN().getName();
				return parseDN(dn, "OU");
			}
		}
		return null;

	}

	/**
	 * 得到机构信息
	 */
	private static String getO(java.security.cert.X509Certificate cert) {
		if (cert != null) {
			try {
				sun.security.x509.X500Name p = (sun.security.x509.X500Name) cert
						.getSubjectDN();
				return p.getOrganization();
			} catch (Exception e) {
				String dn = cert.getSubjectDN().getName();
				return parseDN(dn, "O");
			}
		}
		return null;
	}

	/**
	 * 得到省信息
	 */
	private static String getST(java.security.cert.X509Certificate cert) {
		if (cert != null) {
			try {
				sun.security.x509.X500Name p = (sun.security.x509.X500Name) cert
						.getSubjectDN();
				return p.getState();
			} catch (Exception e) {
				String dn = cert.getSubjectDN().getName();
				return parseDN(dn, "ST");
			}
		}
		return null;

	}

	/**
	 * 得到市/地区信息
	 */
	private static String getL(java.security.cert.X509Certificate cert) {
		if (cert != null) {
			try {
				sun.security.x509.X500Name p = (sun.security.x509.X500Name) cert
						.getSubjectDN();
				return p.getLocality();
			} catch (Exception e) {
				String dn = cert.getSubjectDN().getName();
				return parseDN(dn, "L");
			}
		}
		return null;

	}

	/**
	 * 得到国家信息
	 */
	private static String getC(java.security.cert.X509Certificate cert) {
		if (cert != null) {
			try {
				sun.security.x509.X500Name p = (sun.security.x509.X500Name) cert
						.getSubjectDN();
				return p.getCountry();
			} catch (Exception e) {
				String dn = cert.getSubjectDN().getName();
				return parseDN(dn, "C");
			}
		}
		return null;

	}

	/**
	 * 得到姓名信息
	 */
	private static String getCN(java.security.cert.X509Certificate cert) {
		if (cert != null) {
			try {
				sun.security.x509.X500Name p = (sun.security.x509.X500Name) cert
						.getSubjectDN();
				return p.getCommonName();
			} catch (Exception e) {
				String dn = cert.getSubjectDN().getName();
				return parseDN(dn, "CN");
			}
		}
		return null;

	}

	/**
	 * 得到姓名信息
	 */
	private static String getDNValue(java.security.cert.X509Certificate cert,
			boolean isSubject, String name) {
		if (cert != null) {
			String dn;
			if (isSubject) {
				dn = cert.getSubjectDN().getName();
			} else
				dn = cert.getIssuerDN().getName();
			return parseDN(dn, name);
		}
		return null;
	}

	/**
	 * 解析x509证书 解析原则： 关键是找到符合条件的字符起始、截止位。 缺点:,会截断字符
	 * 1.找到需要解析的字符，如"CN"，取整个字符在"CN"后面的字符
	 * 2.判断后面的字符是否有"=",并且等号很近（C,O特例，否则必须在1到2个字符间） 3.取等号后面的字符，取"," 4.找到=和,之间的字符
	 * 5.上述条件不满足，就循环找下一个满足条件的。
	 */
	private static String parseDN(String dn, String name) {

		String superName = name.toUpperCase();
		String superDn = dn.toUpperCase();
		int beginDot = 0; // 临时开始点
		int endDot = 0; // 结束点

		while (true) {
			int begin = superDn.indexOf(superName); // 开始点
			if (begin < 0)
				return null; // 找不到

			superDn = superDn.substring(begin + superName.length(), superDn
					.length()); // 取后面的串；
			int begin2 = superDn.indexOf("=");
			if (begin2 < 0)
				return null; // 后面没等号
			else if (begin2 > 1) {
				beginDot = beginDot + begin + superName.length();
				continue; // 后面=号过远
			} else if ((superName.equals("C") || superName.equals("O"))
					&& begin2 == 1) { // 区别C和CN
				beginDot = beginDot + begin + superName.length();
				continue; // 后面=号过远
			}
			superDn = superDn
					.substring(begin2 + "=".length(), superDn.length()); // 取后面的串；
			int end = superDn.indexOf(",");
			beginDot = beginDot + begin + superName.length() + begin2
					+ "=".length();
			if (end < 0) { // 后面没等号
				endDot = beginDot + superDn.length();
				return dn.substring(beginDot, endDot);
			} else { // 后面=号过远
				endDot = beginDot + end;
				return dn.substring(beginDot, endDot);
			}

		}

	}

}
