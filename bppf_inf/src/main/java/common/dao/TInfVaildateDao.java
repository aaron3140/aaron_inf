package common.dao;

import java.util.Map;

import common.utils.SpringContextHelper;

public class TInfVaildateDao {

	public static BaseDao DAO = SpringContextHelper.getInfBaseDaoBean();
	
	// 获取验证码
	public static Map getStaffByCode(String staffCode) {

		String sql = " SELECT  T.*  FROM T_INF_VALIDATE T WHERE T.STAFFCODE=? ";

		Map v = null;
			
		try{
			v = DAO.queryForMap(sql, new Object[] { staffCode });
		}catch(Exception e){}

		return v ;
	}
	
	public static Map getVCode(String staffCode,String vCodeValidTime) {

		String sql = " SELECT  T.*  FROM T_INF_VALIDATE T WHERE T.STAFFCODE=? AND T.STAT=? AND T.VALCODE_TIME > trunc(sysdate - ? / 86400,'mi')";

		Map v = null;
		
		try{
			v = DAO.queryForMap(sql, new Object[] { staffCode,"S0A", vCodeValidTime});
		}catch(Exception e){}

		return v ;
	}

	public static void updateVCode(Map m) {

		String staffCode = (String) m.get("STAFFCODE");

		String valCode = (String) m.get("VAL_CODE");

		Object[] params = new Object[] {valCode,"S0A",staffCode };

		String sql = "UPDATE T_INF_VALIDATE T SET T.VAL_CODE=?,T.STAT=?,T.VALCODE_TIME=sysdate WHERE T.STAFFCODE=?";

		int r = DAO.update(sql, params);

	}
	
	public static void updateVCode1(Map<String,String> m) {

		String staffCode = m.get("STAFFCODE");

		String valCode = m.get("VAL_CODE");
		
		String desRand = m.get("VAL_DESRAND");
		
		String imei = m.get("IMEI");
		
		String imsi = m.get("IMSI");

		Object[] params = new Object[] {valCode,"S0X",desRand,imei,imsi,staffCode };

		String sql = "UPDATE T_INF_VALIDATE T SET T.VAL_CODE=?,T.STAT=?,T.VALCODE_TIME=sysdate,T.VAL_DESRAND=?,T.VALDES_USETIME=sysdate,T.IMEI=?,T.IMSI=? WHERE T.STAFFCODE=?";

		int r = DAO.update(sql, params);

	}
	
	public static void updateVCode2(String staffCode) {

		Object[] params = new Object[] {"S0X",staffCode };

		String sql = "UPDATE T_INF_VALIDATE T SET T.STAT=? WHERE T.STAFFCODE=?";

		int r = DAO.update(sql, params);

	}
	
	public static void saveVCode(Map m) {

		String staffCode = (String) m.get("STAFFCODE");

		String valCode = (String) m.get("VAL_CODE");

		Object[] params = new Object[] { staffCode, valCode, "S0A" };

		String sql = "INSERT INTO T_INF_VALIDATE(VAL_ID,STAFFCODE,VAL_CODE,VALCODE_TIME,STAT) VALUES(SQ_INF_VALIDATE.NEXTVAL,?,?,sysdate,?)";

		int r = DAO.update(sql, params);

	}
	
}
