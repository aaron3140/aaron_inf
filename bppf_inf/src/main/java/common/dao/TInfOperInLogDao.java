package common.dao;

import java.util.Date;

import common.entity.TInfOperInLog;
import common.utils.SpringContextHelper;

public class TInfOperInLogDao {

	public static BaseDao DAO = SpringContextHelper.getInfBaseDaoBean();
	
	public boolean insert(TInfOperInLog tInfOperInLog) {
		//BaseDao DAO = SpringContextHelper.getInfBaseDaoBean();
		StringBuffer sb = new StringBuffer();
		
		sb.append("insert into T_INF_OPERINLOG ")
			.append("(OPERIN_ID, KEEP, CONNECT_IP, TNMNUM, SVC_CODE, IN_DATE, IN_TYPE, " +
					" OBJ_CODE, OBJ_VALUE,OBJ_CODE2 ,OBJ_VALUE2,STAT)")
			.append("values (?,?,?,?,?,sysdate,?,?,?,?,?,?)");    
		
		int count = DAO.insert(sb.toString(),new Object[]{tInfOperInLog.getOperInId(),tInfOperInLog.getKeep(),
			tInfOperInLog.getConnectIp(),tInfOperInLog.getTnmnum(),tInfOperInLog.getSvcCode(),tInfOperInLog.getInType()
			,tInfOperInLog.getObjCode(),tInfOperInLog.getObjValue(),tInfOperInLog.getObjCode2(),tInfOperInLog.getObjValue2()
			,tInfOperInLog.getStat()});
		
		if(count > 0) return true;
		return false ;
	}
	
	//seq
	public long getOperInId(){
		String sql = "select SQ_INF_OPERINLOG.NEXTVAL from dual";
		long operInId = DAO.queryForLong(sql) ;
		return operInId;
	}
	
	/**
	 * 修改准许通过
	 * chenwc--2012-6-19下午06:16:20
	 */
	public boolean updateAllow(long operInId,long allow){
		StringBuffer sb = new StringBuffer();
		
		sb.append("UPDATE T_INF_OPERINLOG ")
			.append(" SET ALLOW = ? where OPERIN_ID = ?");
		
		int count = DAO.insert(sb.toString(), new Object[]{allow,operInId});
		
		if(count > 0) {
			return true;
		}
		return false ;
	}
	
	/**
	 * 根据流水号查询入站日志 3天内的
	 * chenwc--2012-6-20上午09:13:48
	 */
	public boolean selectTInfOperInLogByKeep(String keep){
//		Date date = new Date();
//		String sql = "select count(*) from T_INF_OPERINLOG t where t.in_date >= (? - 3) and t.in_date <= ? and t.keep = ?";
//		int count = DAO.queryForInt(sql,new Object[] {date,date,keep}) ;
//		if(count ==1){
//			return false;
//		}
//		return true ;
		String sql = "select count(*) from T_INF_OPERINLOG t where t.in_date >= (sysdate - 3) and t.in_date <= sysdate and t.keep = ?";
		int count = DAO.queryForInt(sql,new Object[] {keep}) ;
        return count>1;
	}

}
