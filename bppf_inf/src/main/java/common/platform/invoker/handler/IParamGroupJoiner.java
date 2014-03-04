/**
 * 自动组处理器：工厂/责任链模式
 * File                 : IParamGroupJoiner.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : apms
 * JDK version used     : JDK 1.5
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2009-5-28 下午04:31:44 [created]
 * Author               : Yunzhi Ling 凌云志
 * Email                : lingyunzhi@tisson.cn
 **/

package common.platform.invoker.handler;

import common.platform.provider.client.DataPackage;

public interface IParamGroupJoiner {
	
	/**
	 * 设置下一个链成员
	 * @version: 1.00
	 * @history: 2009-5-28 下午04:36:36 [created]
	 * @author Yunzhi Ling 凌云志
	 * @param joiner
	 * @see
	 */
	public void setSuccessor(IParamGroupJoiner joiner);
	
	/**
	 * 获取自动加入组处理器
	 * @version: 1.00
	 * @history: 2009-5-28 下午04:33:47 [created]
	 * @author Yunzhi Ling 凌云志
	 * @param intfName
	 * @return
	 * @see
	 */
	public IParamGroupJoiner getJoiner(String intfName);

	/**
	 * 自动加入某个组
	 * @version: 1.00
	 * @history: 2009-5-28 下午04:33:59 [created]
	 * @author Yunzhi Ling 凌云志
	 * @param currentIndex
	 * @param dp
	 * @param extendParams
	 * @see
	 */
	public void join(int currentIndex, DataPackage dp, String... extendParams);
	
}

