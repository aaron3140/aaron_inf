/**
 * File                 : ServiceInvokeException.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : payeasy
 * JDK version used     : JDK 1.5
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2009-5-18 下午06:30:33 [created]
 * Author               : Yunzhi Ling 凌云志
 * Email                : lingyunzhi@tisson.cn
 **/

package common.platform.invoker.exception;

import common.platform.provider.server.PackageDataSet;



public class ServiceInvokeException extends Exception {
	
	public final static String GLOBAL_USER_ERR = "网络繁忙，请稍后再试";
	public final static String CONNECT_ERR = "无法连接平台服务器";
	
	private PackageDataSet dataSet; // 接口原始返回结果集
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2004310564915647954L;
	
	private ServiceInvokeException() {
		// TODO Auto-generated constructor stub
	}
	
	public ServiceInvokeException(String invokeMsg) {
		super(invokeMsg);
	}
	
	public ServiceInvokeException(Throwable cause) {
		super(cause);
	}
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return super.getMessage();
	}

	public PackageDataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(PackageDataSet dataSet) {
		this.dataSet = dataSet;
	}

}

