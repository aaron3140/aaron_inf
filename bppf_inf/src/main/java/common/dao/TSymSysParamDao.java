package common.dao;


import common.utils.SpringContextHelper;

public class TSymSysParamDao {
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	
	public static String getParam(String param_code) {
		String sql = "SELECT PARAM_VAL FROM T_SYM_SYSPARAM WHERE STAT = ? AND PARAM_CODE = ?";	
		return (String) DAO.queryForObject(sql, new Object[] {"S0A", param_code}, String.class);
	}

	public static String getTokenValidTime() {
		return getParam("TOKEN_VALIDTIME");
	}
	public static String getPESwitch() {
		return getParam("PE_SWITCH_FLAG");
	}
	
	public static String getVerifyValidTime() {
		return getParam("VERIFYCODE_VALIDTIME");
	}
}
