/**
 * File                 : IDataSetConverter.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : apms
 * JDK version used     : JDK 1.5
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2009-5-29 下午06:15:20 [created]
 * Author               : Yunzhi Ling 凌云志
 * Email                : lingyunzhi@tisson.cn
 **/

package common.platform.invoker.handler;

import common.platform.provider.server.PackageDataSet;

public interface IDataSetConverter {

	/**
	 * 将dataSet转换成各种bean
	 * @version: 1.00
	 * @history: 2009-5-30 下午06:26:10 [created]
	 * @author Yunzhi Ling 凌云志
	 * @param dataSet
	 * @return
	 * @see
	 */
	public Object convert(PackageDataSet dataSet);
	
}

