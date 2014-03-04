/**
 * File                 : ServiceCallImpl.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : apms
 * JDK version used     : JDK 1.5
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2009-5-28 下午03:21:49 [created]
 * Author               : Yunzhi Ling 凌云志
 * Email                : lingyunzhi@tisson.cn
 **/

package common.platform.invoker.caller;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import svcPostSocket.SocketSrvConfig;

import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamRow;
import common.platform.invoker.bean.ParamRows;
import common.platform.invoker.handler.IServiceResultProcessor;
import common.platform.invoker.handler.ServiceExceptionProcessor;
import common.platform.provider.client.DataPackage;
import common.platform.provider.client.SocketSend;
import common.platform.provider.server.DataProcess;
import common.platform.provider.server.PackageDataSet;

public class ServiceCallImpl implements IServiceCall {

	private String reqType; // 响应类型

	public ServiceCallImpl() {
		// TODO Auto-generated constructor stub
		this.reqType = IServiceCall.REQTYPE_REQUEST; // 默认查询请求
	}

	public PackageDataSet call(String intfName, IParamGroup... groups)
			throws Exception {
		// 初始化通信器
		SocketSend sender = null;
		sender = new SocketSend();
		// 初始化处理器
		DataProcess processor = new DataProcess();
		// 初始化包对象
		DataPackage pac = new DataPackage(IServiceCall.PACKTYPE_COMPOSITE); // 默认复合包
		pac.setPackageType(intfName + this.reqType);
		pac.setCheckSign("0");
		// 初始化结果集
		PackageDataSet dataSet = null;
		// 录入参数组和参数
		int i = 0;
		// 遍历组
		for (IParamGroup group : groups) {
			if (!group.isDataEmpty()) {
				pac.setStruct(group.getPackType()); // 设置包机构 *
				//
				pac.newParamSet(group.getGroupId()); // 组编码 *
				pac.setParamNo(group.getParamCount()); // 组参数个数 *
				// 遍历行
				ParamRows rows = group.getRows();
				for (ParamRow row : rows) {
					i = pac.addSetRow(); // 加入一行 *
					// 遍历参数
					Set<Entry<String, String>> params = row.entrySet();
					for (Entry<String, String> param : params) {
						// 复合
						if (group.getPackType().intern() == "1".intern()) {
							pac.addParam(i, param.getKey(), param.getValue()); // 参数 *
						}
						// 扁平
						else {
							pac.addParam(param.getKey(), param.getValue()); // 参数 *
						}
					}
				}
				pac.addParamSet(); // 封装一组 *
			}
		}
		// 完成封装
		String dataToSend = pac.done();
		// 发送数据
		boolean sendOk = sender.SendData(dataToSend);
		// 处理接收结果
		if (sendOk) {
			// TODO: 模块名，暂定截取接口名称前三位
			String moduleCode = intfName.substring(0, 3);
			//
			IServiceResultProcessor resultProcessor = new ServiceExceptionProcessor();
			resultProcessor.setErrMsgConverter(null);
			dataSet = resultProcessor.process(moduleCode, sender, processor);
		}
		return dataSet;
	}
	
	public PackageDataSet call(String server, String intfName, IParamGroup... groups)
			throws Exception {
		// 初始化通信器
		SocketSend sender = null;
		
		// 获取指定服务器的IP和端口号
		Map<String,String> scParam = SocketSrvConfig.getParam();
		String srvName = server;
		String srvIP = scParam.get(srvName+"IP");
		srvIP = srvIP==null?"":srvIP;	
		String strPort = scParam.get(srvName+"PORT");
		Integer srvPort =Integer.parseInt(strPort==null?"-1":strPort);
		
		sender = new SocketSend(srvIP, srvPort);
		// 初始化处理器
		DataProcess processor = new DataProcess();
		// 初始化包对象
		DataPackage pac = new DataPackage(IServiceCall.PACKTYPE_COMPOSITE); // 默认复合包
		pac.setPackageType(intfName + this.reqType);
		pac.setCheckSign("0");
		// 初始化结果集
		PackageDataSet dataSet = null;
		// 录入参数组和参数
		int i = 0;
		// 遍历组
		for (IParamGroup group : groups) {
			if (!group.isDataEmpty()) {
				pac.setStruct(group.getPackType()); // 设置包机构 *
				//
				pac.newParamSet(group.getGroupId()); // 组编码 *
				pac.setParamNo(group.getParamCount()); // 组参数个数 *
				// 遍历行
				ParamRows rows = group.getRows();
				for (ParamRow row : rows) {
					i = pac.addSetRow(); // 加入一行 *
					// 遍历参数
					Set<Entry<String, String>> params = row.entrySet();
					for (Entry<String, String> param : params) {
						// 复合
						if (group.getPackType().intern() == "1".intern()) {
							pac.addParam(i, param.getKey(), param.getValue()); // 参数 *
						}
						// 扁平
						else {
							pac.addParam(param.getKey(), param.getValue()); // 参数 *
						}
					}
				}
				pac.addParamSet(); // 封装一组 *
			}
		}
		// 完成封装
		String dataToSend = pac.done();
		// 发送数据
		boolean sendOk = sender.SendData(dataToSend);
		// 处理接收结果
		if (sendOk) {
			// TODO: 模块名，暂定截取接口名称前三位
			String moduleCode = intfName.substring(0, 3);
			//
			IServiceResultProcessor resultProcessor = new ServiceExceptionProcessor();
			resultProcessor.setErrMsgConverter(null);
			dataSet = resultProcessor.process(moduleCode, sender, processor);
		}
		return dataSet;
	}
	


	/**
	 * @return the reqType
	 */
	public String getReqType() {
		return reqType;
	}

	/**
	 * @param reqType
	 *            the reqType to set
	 */
	public void setReqType(String reqType) {
		this.reqType = reqType;
	}

}
