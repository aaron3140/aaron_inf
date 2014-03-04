package common.dao;

import common.utils.SpringContextHelper;

public class TPayWapDao {
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();

	public static void insert(String orderId, String tranDate,String retCode,String retnInfo,String tradeSeq,String txnAmount,String MAC,String backUrl) {

		String sql = "insert into T_INF_WAPRSND (WAPRSND_ID,ORDERID,TRANDATE,RETNCODE,RETNINFO,TRADESEQ,TXNAMOUNT,CURTYPE,ENCODETYPE,MAC,CREATE_DATE,STAT,BACKMERCHANTURL) "
				+ " values (SQ_INF_WAPRSND.NEXTVAL,?,?,?,?,?,?,'RMB','1',?,sysdate,'S0A',?)";

		DAO.insert(sql,new Object[]{orderId,tranDate,retCode,retnInfo,tradeSeq,txnAmount,MAC,backUrl});
	}
}
