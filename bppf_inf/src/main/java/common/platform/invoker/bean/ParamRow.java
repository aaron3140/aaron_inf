/**
 * File                 : ParamRow.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : apms
 * JDK version used     : JDK 1.5
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2009-5-28 下午03:48:15 [created]
 * Author               : Yunzhi Ling 凌云志
 * Email                : lingyunzhi@tisson.cn
 **/

package common.platform.invoker.bean;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParamRow extends LinkedHashMap<String, String>
		implements Map<String, String>, Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2741734612098204286L;

	@Override
	public ParamRow clone() {
		// TODO Auto-generated method stub
		ParamRow target = new ParamRow();
		Iterator<Entry<String, String>> it = this.entrySet().iterator();
        while (it.hasNext()) {
        	Entry<String, String> entry = it.next();
            target.put(entry.getKey(), entry.getValue());
        }
        return target;
	}

}

