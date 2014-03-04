package common.algorithm;

import java.security.MessageDigest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
/**
 * MD5的算法在RFC1321中定义在RFC1321中，
 * 给出了Test suite用来检验你的实现是否正确：
 * MD5 ("") = d41d8cd98f00b204e9800998ecf8427e
 * MD5 ("a") = 0cc175b9c0f1b6a831c399e269772661
 * MD5 ("abc") = 900150983cd24fb0d6963f7d28e17f72
 * MD5 ("message digest") = f96b697d7cb7938d525a2f31aaf161d0
 * MD5 ("abcdefghijklmnopqrstuvwxyz") = c3fcd3d76192e4007dfb496cca67e13b
 * 
 */

/**
 * @author lyz
 * 
 */
public class MD5 {
	
	private static Logger logger = Logger.getLogger(MD5.class); 
	
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * @see 传入参数：一个字节数组 传出参数：字节数组的MD5结果字符串
	 * @author lyz
	 * @param bytesSrc
	 * @return
	 */
	public static String getMD5(byte[] bytesSrc) {
		String result = "";
		// 用来将字节转换成16进制表示的字符
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			md.update(bytesSrc);
			// MD5的计算结果是一个128 位的长整数，字节表示是16个字节
			byte tmp[] = md.digest(); 
			// 每个字节用16进制表示，使用两个字符，表示成16进制需要32个字符
			char str[] = new char[16 * 2];
			// 表示转换结果中对应的字符位置
			int k = 0; 
			// 从第一个字节开始，对 MD5 的每一个字节
			for (int i = 0; i < 16; i++) { 
				// 转换成 16 进制字符的转换
				byte byte0 = tmp[i]; // 取第i个字节
				// 取字节中高 4 位的数字转换，>>> 为逻辑右移，将符号位一起右移
				str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
				// 取字节中低 4 位的数字转换
				str[k++] = HEX_DIGITS[byte0 & 0xf];
			}
			// 换后的结果转换为字符串
			result = new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return result;
	}
	public static String MD5Encode(String origin){
		String resultString = null;
			try {
				resultString=new String(origin);
				MessageDigest md = MessageDigest.getInstance("MD5");
				resultString=byteArrayToHexString(md.digest(resultString.getBytes("utf-8")));
			}catch (Exception ex){}
			return resultString;
		}
	public static String byteArrayToHexString(byte[] b){
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++){
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}
	private static String byteToHexString(byte b){
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}
	private final static String[] hexDigits = {
	      "0", "1", "2", "3", "4", "5", "6", "7", 
	      "8", "9", "a", "b", "c", "d", "e", "f"}; 

	/**
	 * @see
	 * @author lyz
	 * @param args
	 */
	public static void main(String args[]) {
		// 计算"a"的MD5代码，应该为：0cc175b9c0f1b6a831c399e269772661
//		String md5 = MD5.getMD5("mzl123456".getBytes());
//		System.out.println(md5);
//		String md5_2 = MD5.getMD5((md5+"aienbiei22&*#*(@ieizewbxwerq?").getBytes());
//		System.out.println(md5_2);
//		String md5_3 = MD5.getMD5(md5_2.getBytes());
//		System.out.println(md5_3);
//		String md5 = MD5Encode(MD5Encode("15817019933123456aienbiei22&*#*(@ieizewbxwerq?"));
//		String md5 = MD5Encode(MD5Encode("zxj001846682aienbiei22&*#*(@ieizewbxwerq?")); //cc4217d2875c61a72087e2a3882857b0
//		String md5 = MD5Encode(MD5Encode("zxj0011234567aienbiei22&*#*(@ieizewbxwerq?")); //80c7ae1221886ef42c0e058f23b02914
//		String md5 = MD5Encode(MD5Encode("zxj001QSQ68M3Zaienbiei22&*#*(@ieizewbxwerq?"));//d665f58e66dd7d5b8dbc6826d2f6c9e4
		//key:MYK2PI3T572SQAKG
		//4578166554c13956b63149821a29fea2
		//"PayPlatRequestParameter":{"CTRL-INFO":{"WEBSVRNAME":"快捷交易查询接口","WEBSVRCODE":"INF02013","APPFROM":"440000-APP001-001-127.0.0.1","KEEP":"90000086201305220842491926"},"PARAMETERS":{"CUSTCODE":"dubo","REMARK1":"","REMARK2":""}}
		String key = "A2QEIJZV6O74WXQG";
		String srcpay = "{'PayPlatRequestParameter':{'CTRL-INFO':{'WEBSVRNAME':'快捷交易查询接口','WEBSVRCODE':'INF02013','APPFROM':'440000-APP001-001-127.0.0.1','KEEP':'90000086201305220842491926'},'PARAMETERS':{'CUSTCODE':'dubo','REMARK1':'','REMARK2':''}}}";
		JSONObject object = JSONObject.fromObject(srcpay);
		JSONObject payObject = object.getJSONObject("PayPlatRequestParameter");
		System.out.println(payObject.toString());
		String pay = payObject.toString()+"<KEY>" + key + "</KEY>";
		String md5Str = MD5.MD5Encode(pay);
		System.out.println(md5Str);
	}

}
