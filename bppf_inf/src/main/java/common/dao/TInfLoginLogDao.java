package common.dao;

import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.utils.OrderConstant;
import common.utils.SpringContextHelper;

public class TInfLoginLogDao {

	public static BaseDao DAO = SpringContextHelper.getInfBaseDaoBean();
	private static final Log log = LogFactory.getLog(TInfLoginLogDao.class);
	
	public static boolean saveRegister(Map m) {
		
		boolean res = false;

		Long logid = (Long)m.get("LOG_ID");
		
		String staff =(String)m.get("STAFFCODE");
		
		String vcode = (String)m.get("VERIFYCODE");

		Object[] params  = new Object[]{logid,staff,vcode,"S0A"};
		
//		 int[] type = new int[]{Types.NUMERIC,Types.DATE,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.CHAR};
		
		String sql = "insert into T_INF_CLNREGLOG(REG_ID,REG_DATE,CUST_CODE,VERIFY_CODE,STAT) values(?,sysdate,?,?,?)";
		
		int r = DAO.update(sql, params);
		
		if(r !=0){
			res = true;
		}
		
		return res;
	}
	
	//获取验证码
	public static String getRVerifyCode(String custCode){

		String sql = "SELECT B.VERIFY_CODE FROM (SELECT A.VERIFY_CODE, ROWNUM RN FROM " +
				"(SELECT * FROM T_INF_CLNREGLOG WHERE CUST_CODE=? ORDER BY REG_DATE DESC) A WHERE ROWNUM = 1)B";

		Object v = DAO.queryForObject(sql, new Object[]{custCode}, String.class);
		
		return v!=null?v.toString():"";
	}
	
	
	public static boolean insert(Map m) {
		
		boolean res = false;
		
		Long logid = (Long)m.get("LOG_ID");
		String staff =(String)m.get("STAFFCODE");
		String cus = (String)m.get("CUSTCODE");
		String mob = (String)m.get("MOBILE");
		String ltype = (String)m.get("LOGTYPE");
		String vcode = (String)m.get("VERIFYCODE");
//		String vgt =(String)m.get("VERIGENTIME");
		String des = (String)m.get("DESRAND");
		Object grd = m.get("GENRANDTIME");
		Object rut = m.get("RANDUSETIME");
		String im = (String)m.get("IMEI");
		String ims = (String)m.get("IMSI");
		String wi =(String)m.get("WIFIMAC");
		String bl =(String)m.get("BLUEMAC");
		Object stat = (Object)m.get("STAT");
		String re = (String)m.get("REMARK");
		
		String verifyStat = (String)m.get("VERIFYSTAT");
		
		Object[] params  = new Object[]{logid,staff,cus,mob,ltype,vcode,des,
				grd,rut,im,ims,wi,bl,stat,re,verifyStat};
		
		 int[] type = new int[]{Types.NUMERIC,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,
				 Types.VARCHAR,Types.DATE,Types.DATE,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.CHAR,Types.VARCHAR,Types.CHAR};
		
		String sql = "insert into T_INF_LOGINLOG(LOG_ID,STAFFCODE,CUSTCODE," +
				"MOBILE,LOGTYPE,VERIFYCODE,VERIGENTIME,DESRAND," +
				"GENRANDTIME,RANDUSETIME,IMEI,IMSI,WIFIMAC,BLUEMAC,STAT,REMARK,VERIFYSTAT) values(?,?,?,?,?,?,sysdate,?,?,?,?,?,?,?,?,?,?)";
		int r = DAO.insert(sql, params,type);
		
		if(r !=0){
			res = true;
		}
		
		return res;
	}
	
	public static boolean insertLogin(Map m) {
		
		boolean res = false;
		Long logid = (Long)m.get("LOG_ID");
		String staff =(String)m.get("STAFFCODE");
		String cus = (String)m.get("CUSTCODE");
		String ltype = (String)m.get("LOGTYPE");
		String des = (String)m.get("DESRAND");
		String im = (String)m.get("IMEI");
		String ims = (String)m.get("IMSI");
		String wi =(String)m.get("WIFIMAC");
		String bl =(String)m.get("BLUEMAC");
		String stat = "S0A";
		String verifystat = "S0X";
		Object[] params  = new Object[]{logid,staff,cus,ltype,des,im,ims,wi,bl,stat,verifystat};
		 int[] type = new int[]{Types.NUMERIC,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.CHAR,Types.CHAR};
		
		String sql = "insert into T_INF_LOGINLOG(LOG_ID,STAFFCODE,CUSTCODE," +
				"LOGTYPE,DESRAND,GENRANDTIME,RANDUSETIME,IMEI,IMSI,WIFIMAC,BLUEMAC,STAT,VERIFYSTAT) values(?,?,?,?,?,sysdate,sysdate,?,?,?,?,?,?)";
		int r = DAO.insert(sql, params,type);
		if(r !=0){
			res = true;
		}
		return res;
	}
	
	public static int update(Map m){
		
		String cus = (String)m.get("CUSTCODE");
		String staf = (String)m.get("STAFFCODE");
		String mob = (String)m.get("MOBILE");
		String ltype = (String)m.get("LOGTYPE");
		String vcode = (String)m.get("VERIFYCODE");
        vcode = vcode.toUpperCase();
		String des = (String)m.get("DESRAND");
//		Object grd = m.get("GENRANDTIME");
//		Object rut = m.get("RANDUSETIME");
		String im = (String)m.get("IMEI");
		String ims = (String)m.get("IMSI");
		String wi =(String)m.get("WIFIMAC");
		String bl =(String)m.get("BLUEMAC");
		
		String verifyStat = (String)m.get("VERIFYSTAT");
		String sql = "update T_INF_LOGINLOG set CUSTCODE=?,LOGTYPE=?,DESRAND=?," +
				"GENRANDTIME=sysdate,RANDUSETIME=sysdate,IMEI=?,IMSI=?,WIFIMAC=?,BLUEMAC=?,VERIFYSTAT=? where " +
				"VERIFYCODE=? and STAFFCODE=?";
		Object[] params  = new Object[]{cus,ltype,des,im,ims,wi,bl,verifyStat,vcode,staf};

		
		int r = DAO.update(sql, params);
		
		return r;
	}
	//seq
	public static long getLoginId(){
		String sql = "select SQ_INF_OPERINLOG.NEXTVAL from dual";
		long operInId = DAO.queryForLong(sql) ;
		return operInId;
	}
	
	//seq
	public static long getLoginId(String sqeName){
//		String sql = "select SQ_INF_OPERINLOG.NEXTVAL from dual";
		long operInId = DAO.getLongPrimaryKey(sqeName) ;
		return operInId;
	}
	
	//获取加密令牌
	public static String getMD5Key(String cust_code, String validSecond){
		String sql = "select DESRAND from T_INF_LOGINLOG where RANDUSETIME " +
				"> trunc(sysdate - ? / 86400,'mi') and stat = ? and custcode = ? order by RANDUSETIME desc";
		List list = DAO.queryForList(sql, new Object[]{Integer.parseInt(validSecond), "S0A", cust_code});
		if (list.size() > 0 ) {
			return ((Map<String, String>)list.get(0)).get("DESRAND");
		}
		return null;
	}
	
	//获取验证码
	public static String getVerifyCode(String staffCode,String verifyCode, String validSecond){
		String verifycode = null;
		String sql = "select VERIFYCODE from T_INF_LOGINLOG where VERIGENTIME " +
				"> trunc(sysdate - ? / 86400,'mi') and VERIFYSTAT = ? and VERIFYCODE = ? and STAFFCODE=? order by VERIGENTIME desc";
		log.info("verifyCodesql:"+sql +" date:validSecond "+validSecond+" verifyCode: "+verifyCode+" staffCode: "+staffCode);
		List list = DAO.queryForList(sql, new Object[]{Integer.parseInt(validSecond), OrderConstant.S0A, verifyCode.toUpperCase(),staffCode});
		if (list.size() > 0 ) {
			verifycode = ((Map<String, String>)list.get(0)).get("VERIFYCODE");
		}
		log.info("return verifycode:"+verifycode);
		return verifycode;
	}
	
	//获取加密令牌
	public static String getMD5KeyByStaffCode(String staffCode, String validSecond){
//		String sql = "select DESRAND from T_INF_LOGINLOG where RANDUSETIME " +
//		"> trunc(sysdate - ? / 86400,'mi') and stat = ? and staffcode = ? order by RANDUSETIME desc";
//		List list = DAO.queryForList(sql, new Object[]{Integer.parseInt(validSecond), "S0A", staffCode});
//		if (list.size() > 0 ) {
//			String desrand = ((Map<String, String>)list.get(0)).get("DESRAND");
//			log.info("server_desrand:"+desrand);
//			return ((Map<String, String>)list.get(0)).get("DESRAND");
//		}
//		return null;
		
		String desRand =null;
		try{
			
			String sql = "SELECT VAL_DESRAND FROM T_INF_VALIDATE WHERE VALDES_USETIME > trunc(sysdate - ? / 86400,'mi') AND STAFFCODE = ?";
			
			desRand = (String)DAO.queryForObject(sql, new Object[]{Integer.parseInt(validSecond), staffCode},String.class);
		}catch(Exception e){}
		
		return desRand;
		
	}
	
	//更新加密令牌最后使用时间
	public static void updateRanduseTime(String cust_code){
		String sql = "update t_inf_loginlog set RANDUSETIME = sysdate where log_id = " +
				"(select log_id from (select log_id from t_inf_loginlog where custcode" +
				" = ? and stat = ? order by RANDUSETIME desc)where rownum = 1)";
		DAO.update(sql, new Object[]{cust_code,"S0A"});
	}
	
	//更新加密令牌最后使用时间
	public static void updateRanduseTimeByStaffCode(String staffCode){
//		String sql = "update t_inf_loginlog set RANDUSETIME = sysdate where log_id = " +
//		"(select log_id from (select log_id from t_inf_loginlog where RANDUSETIME is not null and staffcode" +
//		" = ? and stat = ? order by RANDUSETIME desc)where rownum = 1)";
		
		String sql ="UPDATE T_INF_VALIDATE SET VALDES_USETIME = sysdate WHERE STAFFCODE=? ";
		DAO.update(sql, new Object[]{staffCode});
	}

	/**
	 * 获取上次登录信息
	 * @param staffCode
	 */
	public static Map getLastLoginInfoByStaffCode(String staffCode){
		String sql = "select imei,imsi from  t_inf_loginlog  where staffcode=? and stat='S0A' and VERIFYSTAT='S0X' order by RANDUSETIME desc ";
		List list = DAO.queryForList(sql, new Object[]{staffCode});
		if(list!=null && !list.isEmpty()){
			Map map = (Map) list.get(0);
			return map;
		}
		return Collections.EMPTY_MAP;
	}
}
