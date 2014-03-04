
/**
 * File                 : IServiceCall.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : apms
 * JDK version used     : JDK 1.5
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2009-5-28 下午03:18:47 [created]
 * Author               : Yunzhi Ling 凌云志
 * Email                : lingyunzhi@tisson.cn
 **/

package common.platform.invoker.caller;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.exception.ServiceInvokeException;
import common.platform.provider.server.PackageDataSet;


public interface IServiceCall {
	
	public static final String PACKTYPE_FLAT = "0"; // 扁平包
	public static final String PACKTYPE_COMPOSITE = "1"; // 复合包
	
	public static final String REQTYPE_REQUEST = "0"; // 查询
	public static final String REQTYPE_RESPONSE = "1"; // 回复

	/**
	 * 通用调服务框架 
	 * @version: 1.00
	 * @history: 2009-5-28 下午03:19:51 [created]
	 * @author Yunzhi Ling 凌云志
	 * @param intfName
	 * @param groups
	 * @return
	 * @throws ServiceInvokeException
	 * @see
	 */
	public PackageDataSet call(String intfName, IParamGroup... groups)
			throws Exception;
	
	
	/**
	 * 通用调服务框架：手动选定发送的服务器
	 * @version: 1.00
	 * @history: 2012-2-3 下午04:35:59 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param server
	 * @param intfName
	 * @param groups
	 * @return
	 * @throws Exception
	 * @see
	 */
	public PackageDataSet call(String server, String intfName, IParamGroup... groups)
			throws Exception;
	
	/**
	 * 设置请求标识
	 * @version: 1.00
	 * @history: 2009-6-1 上午10:17:44 [created]
	 * @author Yunzhi Ling 凌云志
	 * @param reqType
	 * @see
	 */
	public void setReqType(String reqType);
	
	/**
	 * 获取请求标识
	 * @version: 1.00
	 * @history: 2009-6-1 上午10:17:51 [created]
	 * @author Yunzhi Ling 凌云志
	 * @return
	 * @see
	 */
	public String getReqType();
	
}

