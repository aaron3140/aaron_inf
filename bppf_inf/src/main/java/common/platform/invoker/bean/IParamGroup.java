/**
 * File                 : IParamGroup.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : apms
 * JDK version used     : JDK 1.5
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2009-5-28 下午03:11:10 [created]
 * Author               : Yunzhi Ling 凌云志
 * Email                : lingyunzhi@tisson.cn
 **/

package common.platform.invoker.bean;

import java.io.Serializable;

public interface IParamGroup extends Serializable, Cloneable {
	
	/**
	 * 设置包结构
	 * @version: 1.00
	 * @history: 2009-6-1 上午10:18:05 [created]
	 * @author Yunzhi Ling 凌云志
	 * @param packType
	 * @see
	 */
	public void setPackType(String packType);
	
	/**
	 * 获取包结构
	 * @version: 1.00
	 * @history: 2009-6-1 上午10:18:12 [created]
	 * @author Yunzhi Ling 凌云志
	 * @return
	 * @see
	 */
	public String getPackType();
	
	/**
	 * 获取组号码
	 * @version: 1.00
	 * @history: 2009-5-28 下午03:14:25 [created]
	 * @author Yunzhi Ling 凌云志
	 * @return
	 * @see
	 */
	public String getGroupId();

	/**
	 * 获取组内每行参数个数
	 * @version: 1.00
	 * @history: 2009-5-28 下午03:11:51 [created]
	 * @author Yunzhi Ling 凌云志
	 * @return
	 * @see
	 */
	public int getParamCount();
	
	
	/**
	 * 获取组内行数
	 * @version: 1.00
	 * @history: 2009-5-28 下午03:37:42 [created]
	 * @author Yunzhi Ling 凌云志
	 * @return
	 * @see
	 */
	public int getRowCount();
	
	/**
	 * 获取组集合
	 * @version: 1.00
	 * @history: 2009-5-28 下午04:09:42 [created]
	 * @author Yunzhi Ling 凌云志
	 * @return
	 * @see
	 */
	public ParamRows getRows();
	
	/**
	 * 把当前参数对封装为一行
	 * @version: 1.00
	 * @history: 2009-5-28 下午03:39:59 [created]
	 * @author Yunzhi Ling 凌云志
	 * @see
	 */
	public void endRow();
	
	/**
	 * 检查组是否为空
	 * @version: 1.00
	 * @history: 2009-5-28 下午03:39:08 [created]
	 * @author Yunzhi Ling 凌云志
	 * @return
	 * @see
	 */
	public boolean isDataEmpty();
	
	/**
	 * 增加一组参数
	 * @version: 1.00
	 * @history: 2009-5-28 下午05:02:09 [created]
	 * @author Yunzhi Ling 凌云志
	 * @param paramId
	 * @param paramValue
	 * @return 
	 * @see
	 */
	public Object put(String paramId, String paramValue);
	
}

