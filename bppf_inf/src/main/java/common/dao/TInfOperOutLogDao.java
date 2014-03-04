package common.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.entity.TInfOperOutLog;
import common.utils.SpringContextHelper;

public class TInfOperOutLogDao {
	private static final Log logger = LogFactory.getLog(TInfOperOutLogDao.class);
	public static BaseDao DAO = SpringContextHelper.getInfBaseDaoBean();
		
	public boolean insert(TInfOperOutLog tOperOutLog) {

		long operOutId = getOperOutId();
		StringBuffer sb = new StringBuffer();
		
		sb.append("insert into T_INF_OPEROUTLOG ")
			.append("(OPEROUT_ID, OPERIN_ID, KEEP, CONNECT_IP, SVC_CODE, OUT_DATE, RET_CODE, RET_IFNO, STAT)")
			.append("values (?,?,?,?,?,sysdate,?,?,?)");    
		
		int count = DAO.insert(sb.toString(),new Object[]{operOutId,tOperOutLog.getOperInId(),tOperOutLog.getKeep(),
			tOperOutLog.getConnectIp(),tOperOutLog.getSvcCode(),tOperOutLog.getRetCode(),tOperOutLog.getRetInfo(),
			tOperOutLog.getStat()});

		if(count > 0) return true;
		return false ;
	}
	
	//seq
	public long getOperOutId(){
		String sql = "select SQ_INF_OPEROUTLOG.NEXTVAL from dual";
		long operInId = DAO.queryForLong(sql) ;
		return operInId;
	}
}
