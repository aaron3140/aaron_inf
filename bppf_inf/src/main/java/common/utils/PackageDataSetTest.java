package common.utils;

import java.util.Map.Entry;
import java.util.Set;

import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamRow;
import common.platform.invoker.bean.ParamRows;
import common.platform.invoker.caller.IServiceCall;
import common.platform.provider.client.DataPackage;
import common.platform.provider.server.DataProcess;
import common.platform.provider.server.PackageDataSet;

public class PackageDataSetTest {
	public static String getSendStr(String server, String intfName, IParamGroup... groups){
		String reqType = IServiceCall.REQTYPE_REQUEST; // 默认查询请求
		// 初始化处理器
		DataProcess processor = new DataProcess();
		// 初始化包对象
		DataPackage pac = new DataPackage(IServiceCall.PACKTYPE_COMPOSITE); // 默认复合包
		pac.setPackageType(intfName + reqType);
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
		return dataToSend;
	}
}
