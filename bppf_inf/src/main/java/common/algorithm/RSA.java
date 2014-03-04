package common.algorithm;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.log4j.Logger;

import common.utils.Charset;

public class RSA {

	private static Logger logger = Logger.getLogger(RSA.class);

	/**
	 * @see
	 * @author lyz
	 * @param plainText
	 * @return
	 */
	public static String encrypt(String plainText) {
		// TODO: 启用RSA开关
		if ("off".equalsIgnoreCase(RSAConfig.getSwitcher())) {
			return plainText;
		} else if (plainText == null || plainText.length() < 1) {
			return "";
		}
		String encText = plainText;
		try {
			byte[] bArr = Charset.hex2Bytes(RSAConfig.getKeyHex());
			// build public key
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bArr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
			//
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			byte[] plainTextBytes = plainText.getBytes("UTF-8");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			byte[] cipherTextBytes = cipher.doFinal(plainTextBytes);
			//
			encText = Charset.bytes2hex(cipherTextBytes);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			encText = plainText;
		}
		return encText;
	}

	/**
	 * @see
	 * @author lyz
	 * @return
	 */
	public static byte[] loadKeyFile(String keyFile) {
		byte[] bArr = new byte[0];
		try {
			FileInputStream fis = new FileInputStream(keyFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bArr = new byte[bis.available()];
			bis.read(bArr);
			bis.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			bArr = new byte[0];
		}
		return bArr;
	}

}
