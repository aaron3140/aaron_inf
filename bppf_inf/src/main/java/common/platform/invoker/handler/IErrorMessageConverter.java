/**
 * File                 : IErrorMessageConverter.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : apms
 * JDK version used     : JDK 1.5
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2009-6-10 上午11:38:25 [created]
 * Author               : Yunzhi Ling 凌云志
 * Email                : lingyunzhi@tisson.cn
 **/

package common.platform.invoker.handler;

public interface IErrorMessageConverter {

	/**
	 * 获取翻译后的错误信息
	 * @version: 1.00
	 * @history: 2009-6-10 上午11:40:24 [created]
	 * @author Yunzhi Ling 凌云志
	 * @param moduleCode
	 * @param errCode
	 * @return
	 * @see
	 */
	public String getErrMsg(String moduleCode, String errCode);
	
}

