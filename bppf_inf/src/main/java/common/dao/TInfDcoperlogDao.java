package common.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.utils.Charset;
import common.utils.SeqTool;
import common.utils.SpringContextHelper;
import common.xml.CommonRespAbs;
import framework.exception.INFLogID;

public class TInfDcoperlogDao {
	
	private static final Log log = LogFactory.getLog(TInfDcoperlogDao.class);
	
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();

	public static String SQ_INF_DCOPER = "SQ_INF_DCOPERLOG";

	public static String PARTY_GROUP_PO = "PO";

	public static String PARTY_GROUP_AG = "AG";

	/**
	 * 插入新记录
	 * 
	 * @author He Wenfeng 和文锋
	 * @param partyGroup
	 * @param servInfname
	 * @param servGwname
	 * @param pfaddr
	 * @param objCode
	 * @param objValue
	 * @return 返回主键
	 */
	public static long insert(String partyGroup, String servInfname,
			String servGwname, String pfaddr, String objCode, String objValue) {
		long id = Long.valueOf(SeqTool.getSeq(SQ_INF_DCOPER)).longValue();
		String sql = "insert into T_INF_DCOPERLOG "
				+ "(DCOPER_ID, PARTY_GROUP, SERV_INFNAME, SERV_GWNAME, PFADDR, OBJ_CODE, OBJ_VALUE, BEGIN_DATE, STAT)"
				+ "values (" + id
				+ ", ?, ?, ?, ?, ?, ?, sysdate, 'S0A')";

		DAO.insert(sql, new Object[] {partyGroup, servInfname, servGwname, pfaddr, objCode, objValue});

		return id;
	}
	
	/**
	 * 插入新记录
	 * 
	 * @author He Wenfeng 和文锋
	 * @param partyGroup
	 * @param servInfname
	 * @param servGwname
	 * @param pfaddr
	 * @param objCode
	 * @param objValue
	  * @param objCode2
	 * @param objValue2
	 * @return 返回主键
	 */
	public static long insert(String partyGroup, String servInfname,
			String servGwname, String pfaddr, String objCode, String objValue, String objCode2, String objValue2) {
		long id = Long.valueOf(SeqTool.getSeq(SQ_INF_DCOPER)).longValue();
		String sql = "insert into T_INF_DCOPERLOG "
				+ "(DCOPER_ID, PARTY_GROUP, SERV_INFNAME, SERV_GWNAME, PFADDR, OBJ_CODE, OBJ_VALUE, OBJ_CODE2, OBJ_VALUE2, BEGIN_DATE, STAT)"
				+ "values (" + id
				+ ", ?, ?, ?, ?, ?, ?, ?, ?, sysdate, 'S0A')";

		DAO.insert(sql, new Object[] {partyGroup, servInfname, servGwname, pfaddr, objCode, objValue, objCode2, objValue2});

		return id;
	}

	/**
	 * 更新记录
	 * 
	 * @author He Wenfeng 和文锋
	 * @param id
	 * @param retCode
	 * @param retInfo
	 * @return
	 */
	public static int update(long id, String retCode, String retInfo) {
		String newResCode = CommonRespAbs.newCode(retCode);
		String sql = "update T_INF_DCOPERLOG set  RET_CODE = ? , RET_INFO = ? , END_DATE = sysdate "
				+ " where DCOPER_ID = ?";

		return DAO.update(sql, new Object[] {newResCode, retInfo, id});
	}
	
	public static long insert(String partyGroup, String servInfname,
			String retCode, String  retInfo) {
		long id = Long.valueOf(SeqTool.getSeq(SQ_INF_DCOPER)).longValue();
		String sql = "insert into T_INF_DCOPERLOG "
				+ "(DCOPER_ID, PARTY_GROUP, SERV_INFNAME, BEGIN_DATE, END_DATE, STAT, RET_CODE, RET_INFO)"
				+ "values (?" 
				+ ", ?, ?, sysdate, sysdate, 'S0A', ?, ?)";

		DAO.insert(sql, new Object[] {id,partyGroup, servInfname, retCode, retInfo});

		return id;
	}

	public static long saveOrUpdate(INFLogID id, String retCode, String retInfo) {
		Long pk = id.getPk();
		String svcInfName = id.getSvcInfName();
		String partyGroup = id.getPartyGroup();
		if (pk != null) {
			return update(pk, retCode, retInfo);
		} else {
			return insert(partyGroup, svcInfName, retCode, retInfo);
		}
	}
	
	/**
	 * 更新记录
	 * 
	 * @author He Wenfeng 和文锋
	 * @param id
	 * @param retCode
	 * @param retInfo
	 * @return
	 */
	public static int updateObj2(long id, String objCode2, String objValue2) {
		//responseCode补充到6位
		String sql = "update T_INF_DCOPERLOG set  OBJ_CODE2 = ? , OBJ_VALUE2 = ? "
				+ " where DCOPER_ID = ? ";

		return DAO.update(sql, new Object[] {objCode2, objValue2, id});
	}
	
	/**
	 * 检查表中ACCEPTSEQNO是否重复
	 * @param seq
	 * @param infName
	 * @return
	 */
	public static boolean isAcceptSeqNoExists(String seq, String infName) {
		String sql = "select DCOPER_ID from T_INF_DCOPERLOG where  obj_value = ?" 
				+" and  obj_code = 'ACCEPTSEQNO' and SERV_INFNAME = ?";
		return (DAO.queryForObject(sql,new Object[]{seq,infName}, Long.class) == null) ? false : true;
	}
	
	private static boolean isObjValueExists(String objCode, String objValue) {
		String sql = "select DCOPER_ID from T_INF_DCOPERLOG where  obj_value = ? and  obj_code = ? ";
		return (DAO.queryForObject(sql,new Object[]{objValue,objCode}, Long.class) == null) ? false : true;
	}
	
	private static boolean isObjValue2Exists(String objCode, String objValue) {
		String sql = "select count(*) from T_INF_DCOPERLOG where  obj_value2 = ? and  obj_code2 = ?";
		return (DAO.queryForInt(sql,new Object[]{objValue,objCode}) == 0 ) ? false : true;
	}
	
	private static boolean isObjValueExists(String objCode, String objValue,String objCode2,String objValue2) {
		String sql = "select count(1) from T_INF_DCOPERLOG where  obj_value = ?" 
			+ " and  obj_code = ? and obj_code2 = ? and obj_value2 = ? ";
		long l = DAO.queryForLong(sql,new Object[]{objValue,objCode,objCode2,objValue2});
		return ( l == 0L) ? false : true;
	}
	
	
	
	/**
	 * 检查表中ACCEPTSEQNO是否重复
	 * @param seq
	 * @return
	 */
	public static boolean isAcceptSeqNoExists(String seq) {
		return isObjValueExists("ACCEPTSEQNO", seq);
	}
	
	/**
	 * 检查表中POSSEQNO是否重复
	 * @param seq
	 * @return
	 */
	public static boolean isPosSeqNoExists(String seq) {
		return isObjValueExists("POSSEQNO", seq);
	}
	
	/**
	 * 检查表中POSSEQNO是否重复
	 * @param seq
	 * @return
	 */
	public static boolean isSeqNoExists(String seq) {
		return isObjValue2Exists("SEQNO", seq);
	}
	
	/**
	 * 检查表中strInternalOrderID是否重复
	 * @param seq
	 * @return
	 */
	public static boolean isStrInternalOrderIDExists(String seq) {
		return isObjValueExists("strInternalOrderID", seq);
	}
	
	/**
	 * 检查表中clientOrderSeq是否重复
	 * @param clientOrderSeq
	 * @return
	 */
	public static boolean isClientOrderSeqExists(String clientOrderSeq){
		return isObjValueExists("CLIENTORDERSEQ", clientOrderSeq);
	}
	
	/**
	 * 检查表中clientOrderSeq是否重复
	 * @param clientOrderSeq
	 * @return
	 */
	public static boolean isPartnerOrderIdExists(String partnerOrderId,String partnerId){
		return isObjValueExists("PARTNERORDERID", partnerOrderId,"PARTNERID",partnerId);
	}
	
	
	/**
	 * @param partnerOrderId
	 * @param partnerId
	 * @return
	 */
	public static boolean isTradeSeqExists(String tradeSeq,String agentCode){
		return isObjValueExists("TRADESEQ", tradeSeq, "AGENTCODE", agentCode);
	}
	
	public static boolean isRequestSeqExists(String requestSeq,String agentCode){
		return isObjValueExists("REQUESTSEQ", requestSeq, "AGENTCODE", agentCode);
	}
	
	
	/**
	 * 检查表中WEGID是否重复
	 * @param seq
	 * @return
	 */
	public static boolean isWegIdExists(String seq) {
		return isObjValueExists("WEGID", seq);
	}

}
