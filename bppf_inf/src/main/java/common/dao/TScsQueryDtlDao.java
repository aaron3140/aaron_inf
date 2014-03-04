package common.dao;

import common.utils.SpringContextHelper;

public class TScsQueryDtlDao {

	
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	
	public static void insert(long recId, long dtlSeq, String dtlId, String dtlName, 
			String dataType, String dataInfo){
		
	  String sql = "INSERT INTO T_SCS_QUERY_DTL (REC_ID, DTL_SEQ, EVENT_SEQ,DTL_ID, DTL_NAME, " 
		  +"DATA_TYPE, DTL_INFO, SHOW_FLAG, CTRL_FLAG)"
		  +"VALUES ( ?,?,'-1',?,?,?,?,'Y','0')";
		  DAO.insert(sql,new Object[]{recId,dtlSeq,dtlId,dtlName,dataType,dataInfo});
	  
	}
	
}
