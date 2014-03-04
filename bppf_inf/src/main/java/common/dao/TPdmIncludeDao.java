package common.dao;

import java.util.List;

import common.entity.PdmInclude;
import common.entity.PdmIncludeRowMapper;
import common.entity.ScsPhnInclude;
import common.entity.ScsPhnIncludeRowMapper;
import common.entity.ScsPosInclude;
import common.entity.ScsPosIncludeMapper;
import common.utils.Charset;
import common.utils.SpringContextHelper;

public class TPdmIncludeDao {
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	
	public static String getTimesForWrongPwdLimit(){
		String sql = "select APPLY_OBJ_ID from  T_PDM_INCLUDE where "
		 +"APPLY_OBJ_TYPE = 'CARDPWD_FAILLIMIT' and  OBJ_TYPE = 'PCPF_INF' and OBJ_ID = 'CARD' and STAT = 'S0A'";
		return (String)DAO.queryForObject(sql.toString(), String.class);
	}
	
	public static String getBookMoneyLimit(){
		String sql = "select APPLY_OBJ_ID from  T_PDM_INCLUDE where "
		 +" APPLY_OBJ_TYPE = 'BOOK_MONEYLIMIT' and  OBJ_TYPE = 'PCPF_INF' and OBJ_ID = 'CARD' and STAT = 'S0A'";
		return (String)DAO.queryForObject(sql.toString(), String.class);
	}
	/**
	 * 通过归属地查询
	 * @version 1.00
	 * @history: 2011-04-26 下午3:39:25 [created]
	 * @author Leyi Tang 唐乐毅
	 * @param supplyOrgCode
	 *            商户id
	 * @return
	 */
	public static PdmInclude getByLocation(String location) {
		String objId = "OPENACCT_CHECK";
		return getByLocationAndObjId(location,objId);
	}

	private static PdmInclude getByLocationAndObjId(String location,String objId) {
		String newLocation = "";
		if (!Charset.isEmpty(location)) {
			if (location.trim().length() == 1) {
				newLocation = Charset.lpad(location, 2, "0");
			}else {
				newLocation = location.trim();
			}
		}
		String sql = "select t.apply_obj_id2,t.memo from t_pdm_include t where t.obj_type = 'MEPF_INF' and OBJ_ID = '" + objId +
				"' and APPLY_OBJ_TYPE='LOCATION' and APPLY_OBJ_ID= '" + newLocation + "' and APPLY_OBJ_TYPE2='READY'";
		 List list = DAO.query(sql, new PdmIncludeRowMapper());
		 PdmInclude include = null;
		 if (list != null && list.size() > 0) {
			 include = (PdmInclude) list.get(0);
		 }
		 return include;
	}
	
	/**
	 * 通过11888归属地查询
	 * @version 1.00
	 * @history: 2011-04-26 下午3:39:25 [created]
	 * @author Leyi Tang 唐乐毅
	 * @param supplyOrgCode
	 *            商户id
	 * @return
	 */
	public static PdmInclude getBy11888Location(String location) {
		String objId = "11888CHG_CHECK";
		return getByLocationAndObjId(location,objId);
	}
	
	/**
	 * 查找升级计划设定的最低版本minVer
	 * @return
	 */
	public static String getMinVer(String clientSyle){
		String sql = "select apply_obj_id2 minVer from T_PDM_INCLUDE " +
				"where OBJ_TYPE='MEPF_INF' and OBJ_ID = 'CLIENTUP_PLAN' and APPLY_OBJ_ID3 = '" + clientSyle + "' ";
		String minVer = (String)DAO.queryForObject(sql.toString(), String.class);
		return minVer;
	}
	
	/**
	 * 查找是否启用升级计划planStart
	 * planStart=Y，启用升级计划；planStart=N，不启用升级计划
	 * @return
	 */
	public static String isNPlanStart(String clientSyle){
		String sql = "select apply_obj_id planStart from T_PDM_INCLUDE " +
				"where OBJ_TYPE='MEPF_INF' and OBJ_ID = 'CLIENTUP_PLAN' and APPLY_OBJ_ID3 = '" + clientSyle + "' ";
		String planStart = (String)DAO.queryForObject(sql.toString(), String.class);
		return planStart;
	}
	
	/**
	 * 查找归属地入参location的升级计划时间upTime（YYYY-MM-DD格式）
	 * @param location
	 * @return
	 */
	public static String getUpTime(String location, String clientSyle){
		String sql = "select apply_obj_id2 upTime from T_PDM_INCLUDE " +
				" where OBJ_TYPE='MEPF_INF' " +
				" and OBJ_ID = 'CLIENTUP_TIMEPLAN' " +
				" and APPLY_OBJ_TYPE='LOCATION' " +
				" and apply_obj_id = '" + location + "'" +
				" and APPLY_OBJ_TYPE2='UPTIME' " + 
				" and APPLY_OBJ_ID3 = '" + clientSyle + "' ";
		String upTime = (String)DAO.queryForObject(sql.toString(), String.class);
		return upTime;
	}
	
	
	/**
	 * 重庆水电煤的获取供应商接口读取MSG字段
	 * @param location
	 * @return
	 */
	public static String getWegMsg(){
		String sql = "select memo from T_PDM_INCLUDE " +
				" where OBJ_TYPE='MEPF_INF' " +
				" and OBJ_ID = 'WEG_MSG' " +
				" and APPLY_OBJ_TYPE='WEG_MSG' " +
				" and apply_obj_id = 'WEG_MSG' ";
		return (String)DAO.queryForObject(sql.toString(), String.class);
	}
	
	/**
	 *获取好友推荐短信模板内容接口
	 * @param location
	 * @return
	 */
	public static String friendGetmsg(String appObjType){
		String sql = "select memo from T_PDM_INCLUDE " +
				" where OBJ_TYPE='MEPF_INF' " +
				" and OBJ_ID = 'FRIEND_GETMSG' " +
				" and APPLY_OBJ_TYPE='"+appObjType+"' ";
		return (String)DAO.queryForObject(sql.toString(), String.class);
	}
	
	/**
	 *好友推荐确认接口
	 * @param location
	 * @return
	 */
	public static void friendRecommend(String refType,String productNo,String friendNum){
		String sql = "insert into T_CUM_REF (REF_ID,CUST_CODE,REF_NO,REF_TYPE,CREATE_DATE,STAT) " +
				" values (SQ_CUM_REF.nextval,'#1','#2','#3',sysdate,'S0A')";
		sql=sql.replace("#1", productNo).replace("#2", friendNum).replace("#3", refType);
		DAO.insert(sql);
	}
	
	/**
	 * 查找积分价格获取接口订单号ORDERID关联的积分单价value1
	 * @return
	 */
	public static String getValue1(String orderId){
		String sql = "select value1 from T_SCS_ACTLIST a, T_SCS_ACT_ATTR b, T_SYM_ATTR c " +
				"where  A.ACTLIST_ID = B.ACTLIST_ID and A.ORDER_ID = '"+orderId+"' and C.ATTR_ID = B.ATTR_ID and C.ATTR_CODE = 'SCS_JF_UNITPRICE'";
		return (String)DAO.queryForObject(sql.toString(), String.class);
	}
	
	/**
	 * 查找积分支付单价APPLY_OBJ_ID2
	 * @return
	 */
	public static String getApplyObjId2(String location){
		String sql = "select APPLY_OBJ_ID2 from T_PDM_INCLUDE " +
				"where  OBJ_TYPE = 'MEPF_INF' and OBJ_ID = 'ORDERCREDIT' and APPLY_OBJ_TYPE='LOCATION' and APPLY_OBJ_ID='"+location+"'";
		return (String)DAO.queryForObject(sql.toString(), String.class);
	}
	
	
	
	/**判断APPLY_OBJ_ID是否等于“Y”
	 * @return
	 */
	public static boolean isNPlanStartForUCodeCheck()
	{
		String sql = "select APPLY_OBJ_ID from T_PDM_INCLUDE " +
			"where  OBJ_TYPE = 'MEPF_INF' and OBJ_ID = 'SMS_WHITE' and APPLY_OBJ_TYPE = 'ENABLE' and ';'||memo||';' like '%;05;%'";
		
		String value = (String) DAO.queryForObject(sql, String.class);
		if("Y".equals(value))
			return true ;
		return false;
	}
	
	/**
	 * 
	 * 方法的描述: 通过OBJ_TYPE和OBJ_ID得到MEMO
	 * @author: jmx  
	 * @version: Jan 10, 2012 3:31:11 PM
	 * @param objType
	 * @param objId
	 * @return
	 * @return String
	 */
	public static String getMemoByObjTypeId(String objType,String objId){
		String sql = "select MEMO FROM T_PDM_INCLUDE where  STAT ='S0A' AND  OBJ_TYPE = '"+objType+"' and OBJ_ID = '"+objId+"'";
		String value = (String) DAO.queryForObject(sql, String.class);
		return value;
	}
	
	/**
	 * 
	 * 方法的描述: 体验卡充值区域限制
	 */
	public static boolean expcardCheck(String location){
		String sql = "select count(*) FROM T_PDM_INCLUDE where  STAT ='S0A' AND  OBJ_TYPE = 'MEPF_INF' and OBJ_ID = 'EXPCARD' and apply_obj_Type = 'LOCATION' and apply_obj_id = '"+location+"'";
		int value = DAO.queryForInt(sql);
		return value > 0 ? true : false ;
	}
	
	/**
	 * 
	 * 方法的描述: 是否可积分支付
	 */
	public static boolean canCreditpayNopw( ){
		String sql = "select apply_obj_id from T_PDM_INCLUDE where OBJ_TYPE = 'MEPF_INF' and OBJ_ID = 'creditpayNopw' and stat = 'S0A' and APPLY_OBJ_TYPE = 'STATUS' ";
		String value = (String) DAO.queryForObject(sql, String.class);
		return "Y".equals(value);
	}
	
	/**
	 * 
	 * 方法的描述: 是否发送抽奖机会
	 */
	public static boolean canSendActivity( ){
		String sql = "select apply_obj_id from T_PDM_INCLUDE where OBJ_TYPE = 'MEPF_INF' and OBJ_ID = 'SENDACTIVITY' and stat = 'S0A' and APPLY_OBJ_TYPE = 'STATUS' ";
		String value = (String) DAO.queryForObject(sql, String.class);
		return "Y".equals(value);
	}

	public static String getApplyObjId2ForCRM(String location) {
		// TODO Auto-generated method stub
		String sql ="select APPLY_OBJ_ID2 from t_pdm_include where STAT = 'S0A' AND APPLY_OBJ_TYPE = 'LOCATION' AND APPLY_OBJ_ID = '"+location+"' AND OBJ_ID = 'openaccountconfirm'  AND TO_DATE(substr(APPLY_OBJ_ID3, 0, instr(APPLY_OBJ_ID3, '|') - 1), 'YYYYMMDD') <= TRUNC(SYSDATE) AND TO_DATE(substr(APPLY_OBJ_ID3, instr(APPLY_OBJ_ID3, '|') + 1),'YYYYMMDD') >= TRUNC(SYSDATE) AND APPLY_OBJ_TYPE2 = 'MONEY'";
		String value = (String) DAO.queryForObject(sql, String.class);
		return value;
	}
	
	public static String getApplyObjId2ForNotCRM(String location) {
		// TODO Auto-generated method stub
		String sql ="select APPLY_OBJ_ID2 from t_pdm_include where STAT = 'S0A' AND APPLY_OBJ_TYPE = 'LOCATION' AND APPLY_OBJ_ID = '"+location+"' AND OBJ_ID = 'openaccountNoncrm'  AND TO_DATE(substr(APPLY_OBJ_ID3, 0, instr(APPLY_OBJ_ID3, '|') - 1), 'YYYYMMDD') <= TRUNC(SYSDATE) AND TO_DATE(substr(APPLY_OBJ_ID3, instr(APPLY_OBJ_ID3, '|') + 1),'YYYYMMDD') >= TRUNC(SYSDATE) AND APPLY_OBJ_TYPE2 = 'MONEY'";
		String value = (String) DAO.queryForObject(sql, String.class);
		return value;
	}
	
	public static String getApplyObjId2Forbestpaypw(String location) {
		// TODO Auto-generated method stub
		String sql ="select APPLY_OBJ_ID2  from t_pdm_include where STAT = 'S0A' AND APPLY_OBJ_TYPE = 'LOCATION' AND APPLY_OBJ_ID = '"+location+"' AND OBJ_ID = 'bestpay.pw' AND TO_DATE(substr(APPLY_OBJ_ID3,0,instr(APPLY_OBJ_ID3,'|')-1),'YYYYMMDD') <= TRUNC(SYSDATE) AND TO_DATE(substr(APPLY_OBJ_ID3,instr(APPLY_OBJ_ID3,'|')+1),'YYYYMMDD') >= TRUNC(SYSDATE) AND APPLY_OBJ_TYPE2 = 'MONEY'";
		String value = (String) DAO.queryForObject(sql, String.class);
		return value;
	}
	/**
	 * 获取分享信息
	 * @param txntype
	 * @return
	 */
	public static String getShareInfoByTxntype(String txntype){
		String sql = "select MEMO from t_pdm_include where STAT='S0A' and OBJ_TYPE = 'MEPF_INF' AND OBJ_ID = 'SHAREINFO' AND APPLY_OBJ_TYPE = 'TXNTYPE' AND APPLY_OBJ_ID = '"+txntype+"'";
		return (String)DAO.queryForObject(sql, String.class);
	}
	/**
	 * 
	 * @param posCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ScsPosInclude getPosInclude(String posCode){
		ScsPosInclude entity = null;
		List<ScsPosInclude> list = null;
		String sql1 = "select apply_obj_id as POSCODE, obj_id as CASH_LIMIT,apply_obj_id2 as ERR_CODE,apply_obj_id3 as ERR_MSG "+
						"from t_pdm_include where STAT = 'S0A' and obj_type = 'CASH_LIMIT' and apply_obj_type = 'POSCODE' and apply_obj_id = '"+posCode+"'";
		
		String sql2 = "select apply_obj_id as POSCODE, obj_id as CASH_LIMIT,apply_obj_id2 as ERR_CODE,apply_obj_id3 as ERR_MSG "+
		"from t_pdm_include where STAT = 'S0A' and obj_type = 'CASH_LIMIT' and apply_obj_type = 'POSCODE' and apply_obj_id = 'DEFAULT'";

		
		list = DAO.query(sql1, new ScsPosIncludeMapper());
		if(list!=null && list.size() >0){
			entity = list.get(0);
		}else{
			list = DAO.query(sql2, new ScsPosIncludeMapper());
			if(list!=null && list.size() >0){
				entity = list.get(0);
			}
		}
		
		return entity;
	}
	
	
	/**
	 * 获取话费充值规则限制
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ScsPhnInclude getPhnInclude(){
		ScsPhnInclude entity = null;
		List<ScsPhnInclude> list = null;
		String sql = "select substr(APPLY_OBJ_ID2,0,instr(APPLY_OBJ_ID2,'|')-1) as TIMELIMIT,"
					 +" substr(APPLY_OBJ_ID2,instr(APPLY_OBJ_ID2,'|')+1) as MINVALUE,"
					 +" APPLY_OBJ_ID3 AS SCSFLAG"
					 +" from t_pdm_include where STAT = 'S0A' "
					 +" AND APPLY_OBJ_TYPE = 'TXNTYPE'"
					 +" AND APPLY_OBJ_ID = '0701'"
					 +" AND OBJ_TYPE = 'MEPF_INF'"
					 +" AND OBJ_ID = 'ACTIVITY'";

		list = DAO.query(sql, new ScsPhnIncludeRowMapper());
		if(list!=null && list.size() >0){
			entity = list.get(0);
		}
		
		return entity;
	}
}
