/**
 * 
 */
package common.platform.provider.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import common.utils.ObjectUtils;

/**
 * @author zxx
 * 
 */
public class PackageDataSet implements Serializable {
	
	private static final long serialVersionUID = 7460127421056203478L;
	
	/** 扁平或者复合 */
	private String sign;
	// the number of table
	/** 数据组标号 */
	private int noOfArray;
	// table id
	private String[] idOfArgumentArray;
	// number of row
	private int[] noOfArrayRowSet;
	// number of argument
	private int[] noOfArgumentSet;
	private int noOfArgument;
	// data set
	private String[][][] argumentArrayData;

	/**
	 * 设置结构标识 扁平或者复合
	 * 
	 * @param sign
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}

	/**
	 * 获取结构标识，扁平或者复合
	 * @return
	 * @author Khaos
	 */
	public String getSign() {
		return this.sign;
	}

	/**
	 * 设置参数组个数
	 * 
	 * @param 参数组个数
	 */
	public void setNoOfArray(int noOfArray) {
		this.noOfArray = noOfArray;
	}

	/**
	 * 设置参数组编号集合
	 * 
	 * @param idOfArgumentArray
	 */
	public void setIdOfArgumentArray(String[] idOfArgumentArray) {
		this.idOfArgumentArray = idOfArgumentArray;
	}

	/**
	 * 设置参数组行数集合
	 * 
	 * @param int []
	 *            noOfArrayRow
	 */
	public void setNoOfArrayRowSet(int[] noOfArrayRowSet) {
		this.noOfArrayRowSet = noOfArrayRowSet;
	}

	/**
	 * 设置参数组行内参数个数集合
	 * 
	 * @param noOfArgument
	 */
	public void setNoOfArgumentSet(int[] noOfArgumentSet) {
		this.noOfArgumentSet = noOfArgumentSet;
	}

	/**
	 * 设置参数组行内参数个数集合
	 * 
	 * @param noOfArgument
	 */
	public void setNoOfArgument(int noOfArgument) {
		this.noOfArgument = noOfArgument;
	}

	/**
	 * 设置参数组数据集合
	 * 
	 * @param argumentArrayData
	 */
	public void setArgumentArrayData(String[][][] argumentArrayData) {
		this.argumentArrayData = argumentArrayData;
	}

	public PackageDataSet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 获取参数组内参数个数
	 * 
	 * @param 参数组编号
	 * @return 参数组内参数个数
	 */
	public int getParamSetNo(String paramID) {
		int ParamSetNo = 0;
		// 
		if (sign == "0") {// 扁平
			ParamSetNo = noOfArgument;
		} else {// 符合
			for (int i = 0; i < noOfArray; i++) {
				if (idOfArgumentArray[i].trim().equals(paramID)) {// 查找到参数组
					ParamSetNo = noOfArrayRowSet[i] * noOfArgumentSet[i];// 计算参数组内参数个数,行*列
					break;
				}
			}
		}
		return ParamSetNo;
		// return ParamSetNo;
	}

	/**
	 * 获取参数组内参数行数
	 * 
	 * @paramID 参数组编号
	 * @return 参数组内行数
	 */
	public int getParamSetNum(String paramID) {
		if (sign == "0") {// 扁平
			return noOfArgument;
		} else {// 符合
			for (int i = 0; i < noOfArray; i++) {
				if (idOfArgumentArray[i].trim().equals(paramID)) {// 查找到参数组
					return noOfArrayRowSet[i];
				}
			}
			return 0;
		}
	}

	/**
	 * 获取参数编号
	 * 
	 * @param paramNo
	 *            参数位置
	 * @param paramID
	 *            参数组编号
	 * @return
	 */
	public String getParamID(int paramNo, String paramID) {
		String param = null;
		try {
			param = getParam(paramNo, paramID).substring(0, 4).trim();
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return param;
	}

	/**
	 * 获取参数值
	 * 
	 * @param paramNo
	 *            参数位置
	 * @param paramID
	 *            参数组编号
	 * @return
	 */
	public String getParamValue(int paramNo, String paramID) {
		String param = null;
		try {
			param = getParam(paramNo, paramID).substring(4).trim();
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return param;

	}

	/**
	 * 获取参数(0,4 是参数编号 4以后是参数值)
	 * 
	 * @param paramNo
	 * @param paramID
	 * @return
	 */
	public String getParam(int paramNo, String paramID) {
		String param = null;
		if (sign == "0") {// 扁平
			param = argumentArrayData[0][0][paramNo];
		} else {// 复合
			for (int i = 0; i < noOfArray; i++) {
				if (idOfArgumentArray[i].trim().equals(paramID)) {// 查找到参数组
					int row, column;
					row = paramNo / noOfArgumentSet[i];
					if (row + 1 > noOfArrayRowSet[i])
						return null;
					column = paramNo - row * noOfArgumentSet[i];
					System.out.print("row:" + row);
					System.out.print("column:" + column);
					param = argumentArrayData[i][row][column];
					break;
				}
			}
		}
		return param;
	}

	/**
	 * @see 接口修改要求：加入判断，当000组0001参数当为0000时返回0 // lyz 2008-09-19
	 * 通过参数编号查找数据集合
	 * 
	 * @param paramID
	 *            参数编号
	 * @param groupID
	 *            参数组编号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList getParamByID(String paramID, String groupID) {
		ArrayList list = new ArrayList();
		if (sign == "0") {// 扁平
			for (int i = 0; i < this.noOfArgument; i++) {
				if (argumentArrayData[0][0][i].substring(0, 4).equals(paramID)) {
					String value = argumentArrayData[0][0][i].substring(4);
					//hwf 注释掉当000组0001参数当为0000时返回0
//					if (
//						"000".equals(groupID) &&
//						"0001".equals(paramID) &&
//						"0000".equals(value)
//					) {
//						value = "0";
//					}
					list.add(value);
				}
			}
		} else {
			for (int i = 0; i < noOfArray; i++) {
				if (idOfArgumentArray[i].trim().equals(groupID)) {// 查找到参数组
					for (int j = 0; j < noOfArrayRowSet[i]; j++) {
						for (int k = 0; k < noOfArgumentSet[i]; k++) {
							if (argumentArrayData[i][j][k].substring(0, 4)
									.equals(paramID)) {// 查找参数编号的参数值
								String value = argumentArrayData[i][j][k].substring(4);
								//hwf 注释掉当000组0001参数当为0000时返回0
//								if (
//									"000".equals(groupID) &&
//									"0001".equals(paramID) &&
//									"0000".equals(value)
//								) {
//									value = "0";
//								}
								list.add(value);
							}
						}
					}
				}
			}

		}
		list.trimToSize();
		return list;
	}
	
	public String getByID(String paramID, String groupID) {
		List<String> result = getParamByID(paramID, groupID);
		return (!ObjectUtils.isEmpty(result) ? result.get(0) : "");
	}
}
