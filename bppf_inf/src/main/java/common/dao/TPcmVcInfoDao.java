package common.dao;

import common.utils.SpringContextHelper;

public class TPcmVcInfoDao {

	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();

	public static String getCardState(String cardNo) {

		String sql = "select card_state from t_pcm_vc_info where card_no = ?";

		return (String) DAO.queryForObject(sql,new Object[]{cardNo}, String.class);
	}
	
	public static boolean isCardExist(String cardNo) {
		String sql = "select card_no from t_pcm_vc_info where card_no = ?";
		
		return DAO.queryForObject(sql,new Object[]{cardNo}, String.class) == null ? false : true;
	}

}
