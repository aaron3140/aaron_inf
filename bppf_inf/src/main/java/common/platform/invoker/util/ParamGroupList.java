package common.platform.invoker.util;

import java.util.ArrayList;

@SuppressWarnings (value = {"serial"})
public class ParamGroupList extends ArrayList<ParamGroup> {
	
	private String groupId = "";
	
	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * 
	 */
	private ParamGroupList() {
		// TODO Auto-generated constructor stub
		super(0);
	}
	
	/**
	 * @param groupId
	 */
	public ParamGroupList(String groupId) {
		super(0);
		if (groupId == null) {
			throw new NullPointerException("参数组ID不能为空");
		}
		try {
			Integer.parseInt(groupId);
		} catch (Exception e) {
			// TODO: handle exception
			throw new NumberFormatException("参数组ID必须为数字");
		}
		this.groupId = groupId;
	}
	
}
