package common.dao;

import common.utils.SpringContextHelper;

public class TPhoneAreaDao {

	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	
	public static String getAreaByPhone(String phone){
		
		String sql = "SELECT T.PROVINCE_CODE FROM T_SYM_REWARD_CASE T WHERE T.CASE_CODE=?";
		
		String  r = (String)DAO.queryForObject(sql, new Object[] {phone}, String.class);
		
		return r;
	}
}
