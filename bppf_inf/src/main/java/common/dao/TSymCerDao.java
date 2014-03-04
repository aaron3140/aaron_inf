package common.dao;


import common.utils.SpringContextHelper;

public class TSymCerDao {
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	
	public static boolean isCerOwnedByMerId(String fingerprint, String merId) {
		String sql = "select b.cer_id from T_SYM_CERXOBJ a, T_SYM_CER b where a.cer_id = b.cer_id and a.OBJ_TYPE = 'MER_ID' and "
                               + " a.OBJ_CODE = ? and b.FINGERPRINT = ? ";	
		String id = (String) DAO.queryForObject(sql, new Object[] {merId, fingerprint}, String.class);
		return (id == null) ? false : true;
	}
	public static boolean isCerOwnedByMer(String fingerprint, String merId) {
		String sql = "select b.cer_id from T_SYM_CERXOBJ a, T_SYM_CER b where a.cer_id = b.cer_id and a.OBJ_TYPE = 'MER_ID' and "
                               + " a.OBJ_CODE in (select p.PRTN_CODE from t_pnm_partner p where p.PRTN_CODE=? and p.STAT='S0A' and p.PRTN_TYPE='PT403') and b.FINGERPRINT = ? ";	
		String id = (String) DAO.queryForObject(sql, new Object[] {merId, fingerprint}, String.class);
		return (id == null) ? false : true;
	}
	public static boolean isCerStatValid(String fingerprint) {
		String sql = "select b.cer_id from T_SYM_CER b where b.FINGERPRINT = ? and b.stat = 'S0A' ";
		String id = (String) DAO.queryForObject(sql, new Object[] {fingerprint}, String.class);
		return (id == null) ? false : true;
	}
	
	
}
