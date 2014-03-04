package common.utils.password;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.utils.exception.ServiceException;



/**
 * @author 邱亚建<br>
 *         时间：2013-7-17 上午10:18:01<br>
 *         类描述：重要信息【解密】工具类（解密由ImportantInfoDecrypter.java类加密的加密串）
 */
public class ImportantInfoDecrypter {

	private static final Logger log = LoggerFactory.getLogger(ImportantInfoDecrypter.class);

	private StandardPBEStringEncryptor encrypter;
	private String key;

	public ImportantInfoDecrypter(String key) {
		this.key = key;
		log.info("解密使用的key=["+key+"]");
	}

	/**
	 * 解密
	 * @param decryptStr 要解密的信息
	 * @return
	 * @throws ServiceException
	 */
	public String decrypt(String decryptStr) throws ServiceException {
		try {
			log.info("要解密的信息 decryptStr=[" + decryptStr + "]");
			this.encrypter = new StandardPBEStringEncryptor();
			encrypter.setPassword(this.key);
			String decrypted = this.encrypter.decrypt(decryptStr);
			//log.info("解密后 decryptStr=[" + decrypted + "]");
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

	public static void main(String[] args) throws ServiceException {
		String key = "d073c52a75a56dfe37402c3cedd9b4f7";
		ImportantInfoDecrypter decrypter = new ImportantInfoDecrypter(key);
		
		System.out.println(decrypter.decrypt("3iomWfFm+iPqg+jxW311DaSHSmjZCy4g"));
	}
	
}
