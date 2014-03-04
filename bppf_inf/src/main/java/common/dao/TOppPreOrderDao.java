package common.dao;

import java.util.List;
import java.util.Map;

import common.utils.Charset;
import common.utils.SpringContextHelper;


public class TOppPreOrderDao {
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	/**
	 * 获取Sequence
	 */
	public String getSequence(){
		String sql="select SQ_OPP_PREORDER.NEXTVAL from dual";
		return (String)DAO.queryForObject(sql, String.class);
	}
	
	public void addPreorder(String preId, String batchCode, String preCode, String staffCode,
			String agentCode,String payeeCode,String tmnNum,String channelCode,String areaCode, String remark1 ,String stat){
		String sql = "insert into t_opp_preorder (pre_id,batch_code,pre_code,acct_date,order_type," +
				"handle_oper_id,action_code,cust_code,obj_code,term_id,channel_type,area_code,memo" +
				",cust_stat,obj_stat,stat) values(?,?,?,sysdate,'OT001',?,'01030001',?,?,?,?,?,?,'S0G',?,'S0A')";
		DAO.insert(sql, new Object[]{preId,batchCode,preCode,staffCode,
				agentCode,payeeCode,tmnNum,channelCode,areaCode,remark1,stat});
	}
	
	/**
	 * 通过客户编码查询转账收款名单
	 */
	public List getTransAcctList(String custCode){
			String sql="select (select cust_code" +
					" from t_cum_info where cust_id = T_CUM_OBJ.cust_id) CUSTCODE " +
					",T_CUM_OBJ.obj_code OBJCODE,T_CUM_INFO.cust_name OBJNAME," +
					"T_CUM_OBJ.stat STAT" +
					" from T_CUM_OBJ,(select cust_code,max(cust_name) cust_name from T_CUM_INFO" +
					" where  T_CUM_INFO.stat='S0A' group by cust_code) T_CUM_INFO ,T_CUM_OBJ_APPROVE" +
					" where T_CUM_INFO.cust_code=T_CUM_OBJ.obj_code and T_CUM_OBJ_APPROVE.obj_id=" +
					"T_CUM_OBJ.obj_id and T_CUM_OBJ_APPROVE.appr_Type='1001' and T_CUM_OBJ_APPROVE.stat='S0A'" +
					"and T_CUM_OBJ.stat ='S0A' and T_CUM_OBJ.cust_Id =(select cust_id from t_cum_info where cust_code = ?)";
		return DAO.queryForList(sql, new Object[]{custCode});
	}
	
	/**
	 * 查询付款单
	 * @param custCode
	 * @param objCode
	 * @param stat
	 * @return
	 */
	public List getPayBillList(String custCode, String objCode, String stat){
		String sql="select opp.pre_code PREORDERID, opp.obj_code OBJCODE, opp.cust_code CUSTCODE, cum.cust_name OBJNAME," +
				" to_char(fee.due_moeny * 100) AMOUNT, to_char(opp.acct_date,'yyyymmddhh24miss') REQDATE,opp.obj_stat STAT " +
				" , opp.memo from T_Opp_Preorder opp,t_cum_info cum, t_opp_ordfee fee where cum.cust_code = opp.obj_code" +
				" and fee.pre_id = opp.pre_id and opp.stat = 'S0A' and opp.cust_Stat ='S0G' and opp.cust_code = ? ";
		Object[] object = new Object[]{custCode};
		if (!Charset.isEmpty(objCode) && !Charset.isEmpty(stat)) {
			sql = sql + " and opp.obj_code = ? and opp.obj_Stat = ?";
			object = new Object[]{custCode,objCode,stat};
		}else if (!Charset.isEmpty(objCode)) {
			sql = sql + " and opp.obj_code = ?";
			object = new Object[]{custCode,objCode};
		}else if (!Charset.isEmpty(stat)) {
			sql = sql + "and opp.obj_Stat = ?";
			object = new Object[]{custCode, stat};
		}
		sql += " ORDER BY REQDATE DESC";
		return DAO.queryForList(sql, object);
	}
	
	/**
	 *  通过预处理单号，获取一条记录
	 */
	public List getTOppPreOrder(String preCode){
		String  sql="select *  from t_opp_preorder p,t_opp_ordfee o " +
				"where p.stat='S0A' and p.cust_stat='S0G' and o.pre_id=p.pre_id and p.pre_code= ?"; 	
		List list = DAO.queryForList(sql, new Object[]{preCode});				
	    return list; 
	}
	
	/**
	 * 更新记录
	 */
	public void update(String orderId,String preOrderSeq){
//		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");				
//		String date = df.format(new Date());
		String sql="update t_opp_preorder set order_id=? ,cust_stat = 'S0C',eff_date=sysdate where pre_code=?";
		DAO.update(sql, new Object[]{orderId,preOrderSeq});
	}
	
	public Map<String,String> getPreOrderBySeq(String tranSeq){

		Map<String,String> r = null;
		try{
			String sql="SELECT T.* FROM T_OPP_PREORDER T WHERE T.TERM_SEQ=? AND T.STAT=? ";
			
			r = DAO.queryForMap(sql, new Object[]{tranSeq,"S0C"});
		}catch(Exception e){}
		
		return r;
	}
	
	public void updateBySeq(String tranSeq){

		try{
			String sql="UPDATE T_OPP_PREORDER T SET T.STAT=? WHERE T.TERM_SEQ=? ";
			
			DAO.update(sql, new Object[]{"S0A",tranSeq});
			
		}catch(Exception e){}
		
	}
	
	public Long getOrderAmount(String tranSeq){

		String sql="SELECT SUM(F.DUE_MOENY) FROM T_OPP_ORDFEE F,T_OPP_PREORDER P WHERE P.PRE_ID = F.PRE_ID AND P.TERM_SEQ=?";
		
		Long r = DAO.queryForLong(sql, new Object[]{tranSeq});
		
		return r;
	}
	
	public List<Map<String,String>> getCallBackByBatch(String termNo){

		String sql="select ps.serv_ip,ps.serv_port,ps.link_info, trim(pa.stat),'' value1 ,ps.link_type from t_pnm_server ps,t_pnm_partner pa where ps.link_num=? and ps.stat='S0A' and ps.prtn_id=pa.prtn_id and pa.stat='S0A'";
		
		List<Map<String,String>> r = DAO.queryForList(sql, new Object[]{termNo});
		
		return r;
	}
	
}
