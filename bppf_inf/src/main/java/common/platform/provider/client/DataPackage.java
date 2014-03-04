/**
 * 
 */
package common.platform.provider.client;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author zxx
 * @组包
 */
public class DataPackage {

	private String paramSetCode = "";// 参数组编号
	private int paramSetNo;// 参数组个数
	private String rowValue = "";// 行数据
	private ArrayList rowValues;
	private int rowNo;// 行数
	private int paramNo = 0; // 参数个数
	private int paramNo_temp = 0;
	private ArrayList paramNos;
	private String struct;// 结构标识 0 扁平 1复合
	private String paramSetValue;// 参数组数据
	private String packageLength;// 包总长度
	private String paramOrSetNo;// 包头中参数或参数组个数
	private String signNo = "1234567890"; // socket签名流水号，动态生成
	private String packageType = "12345678";
	private String checkSign = "1";
	private static String sendID = "12"; // TOMCAT启动时赋值，SOCKET_SEND_ID
	private String receiveID = "12";
	private String space = "12345678912345678912345678";
	
	

	public void setCheckSign(String checkSign) {
		this.checkSign = checkSign;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public void setReceiveID(String receiveID) {
		this.receiveID = receiveID;
	}

	public static void setSendID(String sendID) {
		DataPackage.sendID = sendID;
	}

	public void setSignNo(String signNo) {
		this.signNo = signNo;
	}

	public void setSpace(String space) {
		this.space = space;
	}

	public DataPackage(String struct) {
		this.struct = struct;
		if (struct == "0") {
			// 扁平
			this.rowValue = "";
			this.rowNo = 0;

		} else if (struct == "1") {
			// 复合
			this.paramSetNo = 0;
			this.paramSetValue = "";
		}
	}

	/**
	 * 添加参数
	 * 
	 */
	public void addParam(String argumentCode, String argumentValue) {
		// argumentNo ++
		String argumentValueLength;
		argumentValueLength = LeftFill(String
				.valueOf(argumentValue.getBytes().length), '0', 4);
		this.paramNo_temp++;// 参数个数加1
		// 添加数据行值 参数编号(4)+参数值长度(4)+参数值
		this.rowValue = this.rowValue + argumentCode + argumentValueLength
				+ argumentValue;
	}

	/**
	 * @param 行数
	 *            参数编号 参数值 添加参数,复合结构
	 */
	@SuppressWarnings("unchecked")
	public void addParam(int rowNo, String argumentCode, String argumentValue) {
		if (rowNo >= this.rowNo) {// 已经添加了这行
			if (Integer.valueOf(this.paramNos.get(rowNo - 1).toString()) <= this.paramNo)// 还有参数可添加
				this.rowValues.set(rowNo - 1, this.rowValues.get(rowNo - 1)
						+ argumentCode
						+ LeftFill(String
								.valueOf(argumentValue.getBytes().length), '0',
								4) + argumentValue);
			this.paramNos.set(rowNo - 1, Integer.valueOf(
					this.paramNos.get(rowNo - 1).toString()).intValue() + 1); // 已添加参数个数
		}
	}

	/**
	 * 新建组内参数行
	 * 
	 */
	@SuppressWarnings("unchecked")
	public int addSetRow() {
		if (this.struct == "1") {
			this.rowNo++;// 行数＋1
			this.rowValues.add("");
			this.paramNos.add(0);
			// if (this.paramNo_temp > 0 && this.paramNo ==0)
			// this.paramNo = this.paramNo_temp;
		}
		return this.rowNo;
	}

	public void removeSetRow(int rowNo) {
		if (rowNo <= this.rowNo) {// 判断有没有这行数据
			this.rowValues.remove(rowNo - 1);
			this.paramNos.remove(rowNo - 1);
			this.rowNo--;
		}

	}

	/**
	 * @see 新建参数组 初始化数据，调用addSetRow新建参数行，调用addParam添加参数 之后调用addParamSet添加参数组
	 */
	public void newParamSet(String paramSetCode) {
		this.paramSetCode = paramSetCode;
		this.rowNo = 0;
		this.rowValue = "";
		this.paramSetNo++; // 参数组个数加1
		this.paramNo = 0;
		this.rowValues = new ArrayList();
		this.paramNos = new ArrayList();
	}

	/**
	 * @see 添加参数组，在newParamSet后执行
	 * 
	 */
	public void addParamSet() {
		// 参数组数据是由 参数组编号(3)＋组内行数(3)＋行内参数个数(3)＋参数数据
		// 判断行内参数个数，不足的补空
		fillParam();
		this.paramSetValue = this.paramSetValue + this.paramSetCode
				+ LeftFill(String.valueOf(this.rowNo), '0', 3)
				+ LeftFill(String.valueOf(this.paramNo), '0', 2)
				+ this.rowValue;

	}

	/**
	 * 设定参数组行内参数个数
	 * 
	 * @return
	 */
	public void setParamNo(int paramNo) {
		this.paramNo = paramNo;
	}

	/**
	 * 添加参数完毕，封装包头包体数据
	 * 
	 * @return
	 */
	public String done() {
		String packageData = "";

		if (this.struct == "0") {
			// 扁平
			// 得到包总长度
			this.packageLength = LeftFill(String.valueOf(72 + this.rowValue
					.getBytes().length), '0', 8);
			// 参数个数
			this.paramOrSetNo = LeftFill(String.valueOf(this.paramNo_temp),
					'0', 2);
			packageData = setPackageHead() + this.rowValue;
		} else if (this.struct == "1") {
			// 复合
			// 得到包总长度
			this.packageLength = LeftFill(String
					.valueOf(72 + this.paramSetValue.getBytes().length), '0', 8);
			// 参数组个数
			this.paramOrSetNo = LeftFill(String.valueOf(this.paramSetNo), '0',
					2);
			packageData = setPackageHead() + this.paramSetValue;
		}
		return packageData;
	}

	/**
	 * 添加包头信息
	 */
	public String setPackageHead() {
		@SuppressWarnings("unused")
		String headOfPackageValue;
		PackageHeadData packageHeadData = new PackageHeadData();
		packageHeadData.setStrartSign("FFFF");// 包开始标识
		//TODO: 生成流水号

		Calendar cal = Calendar.getInstance();
		String seed = "" + (cal.getTimeInMillis() + System.nanoTime());
		seed = seed.length() > 10 ? seed.substring(seed.length() - 10, seed.length()) : seed;
		this.signNo = seed;
		packageHeadData.setSignNo(this.signNo);// 包的标识号 10位
		//
		packageHeadData.setPackageLength(this.packageLength);// 包总长度 8位
		packageHeadData.setPackageType(this.packageType);// 包的类型 8位
		packageHeadData.setStructSign(this.struct);// 结构标识 1位
		packageHeadData.setCheckSign(this.checkSign);// 校验标识 1位
		packageHeadData.setArgumentNo(this.paramOrSetNo);// 参数 or 参数组个数 2位
		packageHeadData.setSendID(DataPackage.sendID);// 设置发送系统id 2位
		packageHeadData.setReceiveID(this.receiveID);// 设置接受系统id 2位
		packageHeadData.setSpace(this.space);// 设置预留空间 26位
		packageHeadData.setMac("00000000");// 设置鉴证码 MAC 8位
		return headOfPackageValue = packageHeadData.getHeadOfPackageValue();
	}

	/**
	 * 判断所有行内参数个数是否达到设定的值， 不足补空值
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void fillParam() {
		for (int i = 0; i < this.rowNo; i++) {
			for (int j = Integer.valueOf(this.paramNos.get(i).toString()); j < this.paramNo; j++) {// 每行的参数个数

				this.rowValues.set(i, this.rowValues.get(i) + "00000000");

			}
			this.rowValue = this.rowValue + this.rowValues.get(i).toString();
		}
	}

	/**
	 * 将数据长度转换成包体需要的字符长度
	 * 
	 * @param string ,
	 *            char ,length
	 */
	public String LeftFill(String str, char ch, int len) {
		while (str.length() < len)
			str = ch + str;
		return str;
	}

	public void setStruct(String struct) {
		this.struct = struct;
	}

}
