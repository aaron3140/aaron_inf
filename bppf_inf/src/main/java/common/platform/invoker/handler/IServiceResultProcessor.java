/**
 * File                 : IServiceResultProcessor.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : payeasy
 * JDK version used     : JDK 1.5
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2009-5-19 下午03:39:51 [created]
 * Author               : Yunzhi Ling 凌云志
 * Email                : lingyunzhi@tisson.cn
 **/

package common.platform.invoker.handler;

import common.platform.invoker.exception.ServiceInvokeException;
import common.platform.provider.client.SocketSend;
import common.platform.provider.server.DataProcess;
import common.platform.provider.server.PackageDataSet;

public interface IServiceResultProcessor {

	/**
	 * 处理返回数据
	 * @version: 1.00
	 * @history: 2009-6-10 上午11:37:19 [created]
	 * @author Yunzhi Ling 凌云志
	 * @param moduleCode
	 * @param sender
	 * @param processor
	 * @return
	 * @throws ServiceInvokeException
	 * @see
	 */
	public PackageDataSet process(String moduleCode, SocketSend sender, DataProcess processor) throws ServiceInvokeException;
	
	/**
	 * 设置错误信息翻译器
	 * @version: 1.00
	 * @history: 2009-6-10 上午11:41:23 [created]
	 * @author Yunzhi Ling 凌云志
	 * @param errConverter
	 * @see
	 */
	public void setErrMsgConverter(IErrorMessageConverter errConverter);
	
}
