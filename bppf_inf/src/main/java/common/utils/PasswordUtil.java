package common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.algorithm.MD5;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.xml.CommonReqAbs;

import framework.exception.INFErrorDef;
import framework.exception.INFException;

public class PasswordUtil {

	private static final Log logger = LogFactory.getLog(PasswordUtil.class);

	private static final String ADD_KEY = "aienbiei22&*#*(@ieizewbxwerq?";

	private final static String HEX_TABLE_STRING = "0123456789ABCDEF";

	public static String ConvertPassword(String username, String password) {

		String pwd = MD5
				.MD5Encode(MD5.MD5Encode(username + password + ADD_KEY));

		// 获取随机数
		PackageDataSet ds = cum0014();

		String cum = ds.getByID("2174", "217");

		// 组装旧密码
		String[] s = cum.split("_");

		String oldPassword = pwd;

		oldPassword = s[0] + "_" + MD5.MD5Encode(s[0] + oldPassword + s[1]);

		return oldPassword;
	}

	private static PackageDataSet cum0014() {

		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "CUM_RAND");
		g002.endRow();

		IServiceCall caller = new ServiceCallImpl();

		PackageDataSet dataSet = null;
		try {
			dataSet = caller.call("BIS", "CUM0014", g002);
		} catch (Exception e) {

		}
		return dataSet;
	}

	public static PackageDataSet callCUM1003(CommonReqAbs dpRequest,
			String staffCode, String password, String passType)
			throws Exception {

		String verityType = "0001"; // 支付密码

		if ("1".equals(passType)) {

			verityType = "0002";
		}

		String tmnNum = dpRequest.getTmnNum();

		IParamGroup g200 = new ParamGroupImpl("200");
		g200.put("2901", "2171");
		g200.put("2902", staffCode);
		g200.put("2903", "2007");
		g200.put("2904", password);
		g200.put("2172", "0001");
		g200.put("2173", verityType);

		g200.endRow();

		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", dpRequest.getChannelCode());
		g211.put("2077", tmnNum);
		g211.put("2078", null);
		g211.put("2085", dpRequest.getIp());
		g211.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("BIS", "CUM1003", g200, g211);

		return dataSet;
	}

	public static void AuthenticationPassWord3(CommonReqAbs dpRequest,
			String staff, String password) throws Exception {

		PackageDataSet ds = null;
		// 密码鉴权
		ds = callCUM1003(dpRequest, staff, password, "1");

		String resCode = ds.getByID("0001", "000");

		if (Long.valueOf(resCode) != 0) {

			throw new INFException(INFErrorDef.PAY_PWD_FAULT,
					INFErrorDef.PAY_PWD_FAULT_DESC);
		}
	}
	
	
	public static void AuthenticationPassWord(CommonReqAbs dpRequest,
			String staff, String password, String eCard, String pasmNo,
			String passFlag) throws Exception {

		PackageDataSet ds = null;
		// 密码鉴权
		if (ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())) {

			logger.info("BCD密文密码>>>>>" + password);

			// 解析BCD密码
			String pwd = getDpassWord(password, eCard, pasmNo, passFlag);

			// logger.info("BCD明文密码>>>>>"+pwd);
			// 明文转换成密文
			pwd = ConvertPassword(staff, pwd);
			logger.info("CUM加密后的密文>>>>>" + pwd);

			ds = callCUM1003(dpRequest, staff, pwd, passFlag);

		} else {

			ds = callCUM1003(dpRequest, staff, password, "2");
		}

		String resCode = ds.getByID("0001", "000");

		if (Long.valueOf(resCode) != 0) {

			throw new INFException(INFErrorDef.PAY_PWD_FAULT,
					INFErrorDef.PAY_PWD_FAULT_DESC);
		}
	}

	public static void AuthenticationPassWord1(CommonReqAbs dpRequest,
			String staff, String password, String eCard, String pasmNo,
			String passFlag) throws Exception {

		PackageDataSet ds = null;
		// 密码鉴权
		logger.info("BCD密文密码>>>>>" + password);

		// 解析BCD密码
		String pwd = getDpassWord(password, eCard, pasmNo, passFlag);

		// logger.info("BCD明文密码>>>>>"+pwd);
		// 明文转换成密文
		pwd = ConvertPassword(staff, pwd);
		logger.info("CUM加密后的密文>>>>>" + pwd);

		ds = callCUM1003(dpRequest, staff, pwd, passFlag);

		String resCode = ds.getByID("0001", "000");

		if (Long.valueOf(resCode) != 0) {

			throw new INFException(INFErrorDef.PAY_PWD_FAULT,
					INFErrorDef.PAY_PWD_FAULT_DESC);
		}

	}

	public static String getDpassWord(String pass, String ecardNo,
			String pasmNo, String flag) throws Exception {

		int f = Integer.parseInt(flag);

		// 调用解密机
		String dPass = scm0005(pasmNo, pass);

		// 调用处理逻辑
		String rPass = changePass(dPass, ecardNo, f);
		// 获取明文
		if (1 != f) {

			rPass = subPass(rPass);
		}

		return rPass;
	}

	private static String scm0005(String pasm, String password)
			throws Exception {

		IParamGroup g850 = new ParamGroupImpl("850");
		g850.put("8501", "TEK");
		g850.put("8503", "002");
		g850.endRow();

		IParamGroup g851 = new ParamGroupImpl("851");
		g851.put("8511", pasm);
		g851.put("8516", password);
		g851.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCM", "SCM0005", g850, g851);

		String pwd = (String) dataSet.getParamByID("8517", "851").get(0);

		return pwd;

	}

	private static String changePass(String pass, String ecardno, int flag)
			throws Exception {

		String src_return = "";

		if (pass == null || ecardno == null) {

			throw new Exception("密码或E卡参数错误!");
		} else if (ecardno.length() <= 13) {

			throw new Exception("E卡长度错误!");
		}

		int ecardno_length = ecardno.length();

		if (1 == flag) {// 老板密码

			src_return = toStringHex(pass.substring(4));
		} else if (2 == flag) {// 交易密码

			String ecardno_12 = ecardno.substring(ecardno_length - 13,
					ecardno_length - 1);
			String ecardno_16 = lpad(ecardno_12, 16, "0");

			src_return = getYhString(pass, ecardno_16);
		}
		return src_return;
	}

	/**
	 * JAVA字符串异或运算
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String getYhString(String str1, String str2) {

		String returnstr = "";

		byte[] b1 = hexToByte(str1);

		byte[] b2 = hexToByte(str2);

		int temp = 0;

		if (b1.length <= b2.length) {

			temp = b2.length;
		} else {

			temp = b1.length;
		}
		byte[] s = new byte[temp];

		for (int i = 0; i < temp; i++) {

			s[i] = (byte) (b1[i] ^ b2[i]);
		}

		returnstr = byteArrayToHexString(s);// new String(s);//s.toString();

		logger.debug("异或运算后:>> " + returnstr);
		return returnstr;
	}

	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/**
	 * 16进制转化字符串
	 * */
	private static String toStringHex(String s) {

		byte[] baKeyword = new byte[s.length() / 2];

		for (int i = 0; i < baKeyword.length; i++) {

			try {

				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
						i * 2, i * 2 + 2), 16));

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
		try {

			s = new String(baKeyword, "utf-8");// UTF-16le:Not

		} catch (Exception e1) {

			e1.printStackTrace();
		}
		return s;
	}

	/**
	 * 向左补字符
	 * 
	 * @param input
	 * @param len
	 * @param padChar
	 * @return
	 */
	private static String lpad(String input, int len, String padChar) {

		if (input.length() >= len) {

			return input;
		}

		StringBuffer sb = new StringBuffer(input);

		for (int i = 0, j = len - input.length(); i < j; i++) {

			sb.insert(0, padChar);
		}

		return sb.toString();
	}

	private static String subPass(String rpass) {

		String l = rpass.substring(0, 2);

		int len = Integer.parseInt(l);

		String p = rpass.substring(2, len + 2);

		return p;

	}

	/**
	 * 将16进制的ASCII字符串转化为BCD的字节数组.
	 * 
	 * @param hexString
	 *            16进制字符串
	 * @return 字节数组
	 */
	public static byte[] hexToByte(String hexString) {
		int len = hexString.length();
		byte[] numByte = hexString.toUpperCase().getBytes();
		byte[] bcdCode = new byte[len];
		for (int i = 0; i < len; i++) {
			bcdCode[i] = (byte) HEX_TABLE_STRING.indexOf(numByte[i]);
		}

		return compressByte(bcdCode);
	}

	/**
	 * 压缩字节数组.
	 * 
	 * @param srcByte
	 *            带压缩字节数组
	 * @return 字节数组
	 */
	public static byte[] compressByte(byte[] srcByte) {
		int len = srcByte.length / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] += (byte) ((srcByte[2 * i] << 4) + srcByte[2 * i + 1]);

		return result;
	}

	public static String generate() {

		char[] vCode = new char[6];

		for (int i = 0; i < 6; i++) {

			vCode[i] = getNextChar();
		}

		return String.valueOf(vCode);
	}

	public static String generateP() {

		char[] vCode = new char[8];

		for (int i = 0; i < 8; i++) {

			vCode[i] = getNextCharP();
		}

		return String.valueOf(vCode);
	}

	private static char getNextChar() {

		char[] charSeq = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

		return charSeq[randInRange(0, charSeq.length)];
	}

	private static char getNextCharP() {

		char[] charSeq = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
				'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
				'Y', 'Z' };

		return charSeq[randInRange(0, charSeq.length)];
	}

	private static int randInRange(int x, int y) {
		return x + (int) (Math.random() * (y - x));
	}

	// 生成密码的时候，添加登录用户名，加密密码明文，得到的密文保存到数据库
	public static String encryptPwd(String pwd, String staffId)
			throws Exception {
		String s = staffId + pwd + ADD_KEY;
		return MD5.MD5Encode(MD5.MD5Encode(s));
	}

	
	/**
	 * 拼装短信参数
	 * @param roleName
	 * @param staffId
	 * @param loginPwd
	 * @param payPwd
	 * @param newLoginPwd
	 * @param newPayPwd
	 * @return
	 */
	public static String getContent(String custName, String roleName, String staffId, String loginPwd,
			String payPwd, String newLoginPwd, String newPayPwd) {
		StringBuffer sb = new StringBuffer("");
		
		String separator = "###";
		if (!Charset.isEmpty(custName))
			sb.append("CUST_NAME" + separator + custName + "|");
		if (!Charset.isEmpty(roleName))
			sb.append("ROLE_NAME" + separator + roleName + "|");
		if (!Charset.isEmpty(staffId))
			sb.append("STAFF_ID" + separator + staffId + "|");
		if (!Charset.isEmpty(loginPwd))
			sb.append("LOGIN_PWD" + separator + loginPwd + "|");
		if (!Charset.isEmpty(payPwd))
			sb.append("PAY_PWD" + separator + payPwd + "|");
		if (!Charset.isEmpty(newLoginPwd))
			sb.append("NEW_LOGIN_PWD" + separator + newLoginPwd + "|");
		if (!Charset.isEmpty(newPayPwd))
			sb.append("NEW_PWD_PWD" + separator + newPayPwd + "|");

		String content = !Charset.isEmpty(sb.toString()) ? sb.toString()
				.substring(0, sb.toString().length() - 1) : "";
		return content;
	}
	
	/**
	 * 创建短信打包
	 * @param touchId 模版id
	 * @param type (CUST_CODE 企业/PRTN_CODE 机构) 类型
	 * @param typeValue type = CUST_CODE,填CUSTCODE
	 * 					type = PRTN_CODE,填PRTNCODE
	 * @param phoneNum 手机号码
	 * @param content 模版短信参数
	 * @return
	 * @throws Exception
	 */
	public static PackageDataSet scs4003(String touchId, String type,
			String typeValue, String phoneNum, String content , String staffId) throws Exception {
		
		String date = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date());

		IParamGroup g416 = new ParamGroupImpl("416");
		g416.put("4231", touchId);
		g416.put("4252", type);
		g416.put("4253", typeValue);
		g416.put("4254", "001");
		g416.put("4255", "LT001");
		g416.put("4256", phoneNum);
		g416.put("4258", content);
		g416.put("4259", date);
		g416.put("4260", staffId);
		g416.endRow();

		IServiceCall caller = new ServiceCallImpl();
		
		PackageDataSet dataSet = null;
		try{
			
			dataSet = caller.call("SCS", "SCS4003", g416);
			
		}catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return dataSet;
	}
	
	
	public static void main(String[] args) throws Exception {

		String pass = "0620F594DFBFFBAD";// 加密机后的密码串 0012313131313131
		// 06119011DFCFFEDA
		String ecardno = "1088100204004522";
		int f = 2; // 1:为老板密码 2为交易密码

		// 调用处理逻辑
		String rPass = changePass(pass, ecardno, f);
		// 获取明文
		if (1 != f) {

			rPass = subPass(rPass);
		}

		System.out.println("最终结果:>>" + rPass);

	}
}
