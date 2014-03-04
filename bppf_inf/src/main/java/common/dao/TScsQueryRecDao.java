package common.dao;

import common.utils.Constant;
import common.utils.SeqTool;
import common.utils.SpringContextHelper;

public class TScsQueryRecDao {


	public static BaseDao easDAO = SpringContextHelper.getEasBaseDaoBean();

	private static final String SQ_SCS_QUERYREC = "SQ_SCS_QUERYREC";

	
	public static Long insert(long amount, String tradeSeq, String termNo, String custCode,
			String agentCode,String cardOrderSeq){
		
		long id = Long.valueOf(SeqTool.getSeq(SQ_SCS_QUERYREC)).longValue();
		
	  String sql = "INSERT INTO T_SCS_QUERY_REC (REC_ID, CREATE_DATE, DEAL_AMOUNT, " 
		  +"SEND_TIME, TERM_SEQ, DEAL_TIME, DEAL_DATE, EVENT_SEQ, TERM_ID, " 
	  	  +"ACT_STAFF, ACT_CODE, SUB_ACT, QRY_FLAG, QRY_TYPE, QRY_INFO, QRY_PWD, " 
	  	  +"BOOK_DATE, RET_CODE, RET_MSG, OPER_ORIG, OPER_ORIG1)"
	  	  +"VALUES ( ?,sysdate,?,to_char(sysdate, 'YYYYMMDDHH24MISS')," 
	  	  +"?,to_char(sysdate, 'HH24MISS'),to_char(sysdate, 'YYYYMMDD'),'-1'," 
	  	  +"?,?,?,'','','0002',?,'','','','',?,'' )";

	  easDAO.insert(sql,new Object[]{id,amount,tradeSeq,termNo,custCode,Constant.DP_E_CARD_ACTION_CODE,agentCode,cardOrderSeq});
	  
	  return id;
	}
	
	public static String getOperOrig(String termNo, String custCode,String termSeq){
		
		
	  String sql = "select OPER_ORIG from T_SCS_QUERY_REC " 
		  +"where TERM_ID = ? and ACT_STAFF = ? " 
		  +"and ACT_CODE = ? and TERM_SEQ = ?";

	  return (String) easDAO.queryForObject(sql, new Object[]{termNo,custCode,Constant.DP_E_CARD_ACTION_CODE,termSeq}, String.class);
	}
	
	public static String getDealAmount(String termNo, String custCode,String termSeq){
		
		
		  String sql = "select DEAL_AMOUNT from T_SCS_QUERY_REC " 
			  +"where TERM_ID = ? and ACT_STAFF = ? " 
			  +"and ACT_CODE = ? and TERM_SEQ = ?";

		  return (String) easDAO.queryForObject(sql, new Object[]{termNo,custCode,Constant.DP_E_CARD_ACTION_CODE,termSeq}, String.class);
		}
	
}
