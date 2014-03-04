/**
 * File                 : CumGroupJoiner.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : apms
 * JDK version used     : JDK 1.5
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2009-5-28 下午04:34:33 [created]
 * Author               : Yunzhi Ling 凌云志
 * Email                : lingyunzhi@tisson.cn
 **/

package common.platform.invoker.handler;

import common.utils.Charset;
import common.platform.invoker.ServiceConstant;
import common.platform.provider.client.DataPackage;

public class CumGroupJoiner implements IParamGroupJoiner {
	
	private IParamGroupJoiner nextJoiner; // 下一个对象
	
	private String INTF_PREFIX = "CUM"; // CUM专用joiner
	
	/**
	 * 对外隐藏，工厂模式
	 * getJoiner() 工厂方法
	 */
	private CumGroupJoiner() {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see cn.tisson.apms.framework.soa.handler.IParamGroupJoiner#getJoiner(java.lang.String)
	 */
	public IParamGroupJoiner getJoiner(String intfName) {
		// TODO Auto-generated method stub
		// 异常，返回空
		if (Charset.isEmpty(intfName)) {
			return null;
		}
		// 匹配，返回自身
		else if (intfName.startsWith(INTF_PREFIX)) {
			return this;
		}
		// 不匹配，返回责任链下一成员
		else {
			// 存在下一成员
			if (nextJoiner != null) {
				return nextJoiner.getJoiner(intfName);
			}
			// 不存在
			else {
				return null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see cn.tisson.apms.framework.soa.handler.IParamGroupJoiner#join(int, java.lang.String)
	 */
	public void join(int currentIndex, DataPackage dp, String... extendParams) {
		// TODO Auto-generated method stub
		dp.newParamSet("211");
		dp.setParamNo(4);
		currentIndex = dp.addSetRow();
		dp.addParam(currentIndex, "2076", ServiceConstant.SITE_CHANNEL_TYPE);
		dp.addParam(currentIndex, "2077", ServiceConstant.SITE_TERM_ID);
		dp.addParam(currentIndex, "2078", extendParams[0]);
		dp.addParam(currentIndex, "2079", extendParams[1]);
		dp.addParamSet();
	}

	public void setSuccessor(IParamGroupJoiner joiner) {
		// TODO Auto-generated method stub
		
	}

}

