package common.utils.password;

import java.io.InputStream;
import java.util.Properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.utils.exception.ServiceException;

/**
 * @author 邱亚建<br>
 *         时间：2013-7-17 上午10:18:01<br>
 *         类描述：重要信息【加密】工具类 生成加密串时，请使用该类生成，对应的解密类为ImportantInfoDecrypter.java
 */
public class ImportantInfoEncrypter {

	private static final Logger log = LoggerFactory.getLogger(ImportantInfoEncrypter.class);

	private StandardPBEStringEncryptor encrypter;
	private String key;

	public ImportantInfoEncrypter(String key) {
		this.key = key;
	}

	/**
	 * 加密
	 * 
	 * @param encryptStr
	 *            要加密的信息
	 * @return
	 * @throws ServiceException
	 */
	public String encrypt(String encryptStr) throws ServiceException {
		try {
			log.info("要加密的信息encryptStr=[" + encryptStr + "]");
			this.encrypter = new StandardPBEStringEncryptor();
			encrypter.setPassword(this.key);
			String decrypted = this.encrypter.encrypt(encryptStr);
			return decrypted;
		} catch (Exception e) {
			throw new ServiceException("[CRITICAL ERROR] Error enountered during decryption - check decryption algorithm & key match that used for encryption", e);
		}
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public static void main(String[] args) throws Exception {
		InputStream is = ImportantInfoEncrypter.class.getClassLoader().getResourceAsStream("config/encryptkey.properties");
		Properties pro = new Properties();
		pro.load(is);
		// String key = "d073c52a75a56dfe37402c3cedd9b4f7";//加密解密使用的key值
		String key = pro.getProperty("ENCRYPT.KEY");// 加密解密使用的key值
		ImportantInfoEncrypter encrypter = new ImportantInfoEncrypter(key);// 加密
		ImportantInfoDecrypter decrypter = new ImportantInfoDecrypter(key);// 解密

		String oldStr = "BPS2012";// 要加密的字符信息
		String newStr = encrypter.encrypt(oldStr);
		System.out.println("[" + oldStr + "]加密后为：[" + newStr + "]");
		System.out.println("[" + newStr + "]解密后为：[" + decrypter.decrypt(newStr) + "]");

	}

}
