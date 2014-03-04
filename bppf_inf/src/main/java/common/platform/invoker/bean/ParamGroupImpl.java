/**
 * File                 : ParamGroupImpl.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : apms
 * JDK version used     : JDK 1.5
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2009-5-28 下午03:16:58 [created]
 * Author               : Yunzhi Ling 凌云志
 * Email                : lingyunzhi@tisson.cn
 **/

package common.platform.invoker.bean;

import common.platform.invoker.caller.IServiceCall;


public class ParamGroupImpl extends ParamRow implements IParamGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1699434226558912418L;
	
	private String groupId; // 组名
	
	private ParamRows rows = new ParamRows(0); // 行数据
	
	private String packType; // 包结构
	
	private ParamGroupImpl() {
		// TODO Auto-generated constructor stub
	}
	
	public ParamGroupImpl(String groupId) {
		// TODO Auto-generated constructor stub
		this.groupId = groupId;
		this.packType = IServiceCall.PACKTYPE_COMPOSITE;
	}
	
	public ParamGroupImpl(String groupId, String packType) {
		// TODO Auto-generated constructor stub
		this.groupId = groupId;
		this.packType = packType;
	}
	
	/* (non-Javadoc)
	 * @see cn.tisson.apms.framework.soa.bean.IParamGroup#getPackType()
	 */
	public String getPackType() {
		// TODO Auto-generated method stub
		return packType;
	}
	
	public void setPackType(String packType) {
		// TODO Auto-generated method stub
		this.packType = packType;
	}
	
	/* (non-Javadoc)
	 * @see cn.tisson.apms.business.cum.platform.bean.IParamGroup#getGroupId()
	 */
	public String getGroupId() {
		// TODO Auto-generated method stub
		return groupId;
	}

	/* (non-Javadoc)
	 * @see cn.tisson.apms.business.cum.platform.bean.IParamGroup#getParamCount()
	 */
	public int getParamCount() {
		// TODO Auto-generated method stub
		seal();
		return rows.size() > 0 ? rows.get(0).size() : 0;
	}

	public int getRowCount() {
		// TODO Auto-generated method stub
		seal();
		return rows.size();
	}

	public boolean isDataEmpty() {
		// TODO Auto-generated method stub
		seal();
		return rows.size() < 1 || rows.get(0).size() < 1;
	}

	public void endRow() {
		// TODO Auto-generated method stub
		if (this.size() > 0) {
			rows.add(this.clone());
			rows.trimToSize();
			this.clear();
		}
	}

	public ParamRows getRows() {
		// TODO Auto-generated method stub
		seal();
		return rows;
	}
	
	/**
	 * 自动加入行
	 * @version: 1.00
	 * @history: 2009-5-28 下午04:13:53 [created]
	 * @author Yunzhi Ling 凌云志
	 * @see
	 */
	private void seal() {
		if (this.size() > 0 && rows.size() < 1) {
			endRow();
		}
	}
	
	@Override
	//mepf 修改稿空字符串同样能够put
	public String put(String key, String value) {
		// TODO Auto-generated method stub
		if (value == null /*|| value.intern() == "".intern()*/) {
			return null;
		} else {
			return super.put(key, value);
		}
	}
	
}

