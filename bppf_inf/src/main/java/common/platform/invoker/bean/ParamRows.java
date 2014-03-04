/**
 * File                 : ParamRows.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : apms
 * JDK version used     : JDK 1.5
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2009-5-28 下午04:07:24 [created]
 * Author               : Yunzhi Ling 凌云志
 * Email                : lingyunzhi@tisson.cn
 **/

package common.platform.invoker.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ParamRows extends ArrayList<ParamRow> implements Serializable,
		List<ParamRow> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4634055476273777717L;
	
	public ParamRows() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	public ParamRows(int initialCapacity) {
		// TODO Auto-generated constructor stub
		super(initialCapacity);
	}
	
	

}

