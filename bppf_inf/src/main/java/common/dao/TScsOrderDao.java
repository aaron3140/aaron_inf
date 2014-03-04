package common.dao;


import java.util.ArrayList;
import java.util.List;

import common.utils.Charset;
import common.utils.DBTool;
import common.utils.SpringContextHelper;

public class TScsOrderDao {

public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();

	/**根据工作流查询订单业务编码
	 * @param custCode
	 * @param tradeSeq
	 * @return
	 */
	public static String getOrderIdByTermSeq(String custCode,String tradeSeq){
		
		String sql = "select order_id from t_scs_order o, t_cum_info c where o.cust_id = c.cust_id and c.cust_code =? and o.term_seq = ?";
		
		return (String) DAO.queryForObject(sql,new Object[]{custCode,tradeSeq}, String.class);
	}
	
	public static List getOrderForPaymentInfo(String cardNo ,String actionCode, String txnChannel,
			String startTime,String endTime, String startRecord, String maxRecord){
		List<String> paraList = new ArrayList<String>();
		
		StringBuffer sql = new StringBuffer();
		sql.append("select (select prtn_name from t_pnm_partner where prtn_id = ") ;
		sql.append("(select prtn_id from t_cum_info where cust_id = a.CUST_ID) ) merchantName,");
		sql.append("(select VALUE1 from T_SCS_ACT_ATTR where ACTLIST_ID = b.ACTLIST_ID and ATTR_ID =(select attr_id from t_sym_attr where attr_code = 'SCS_GOODSCODE')) goodsCode, ");		
		sql.append("(select VALUE1 from T_SCS_ACT_ATTR where ACTLIST_ID = b.ACTLIST_ID and ATTR_ID =(select attr_id from t_sym_attr where attr_code = 'SCS_GOODSNAME')) goodsName, ");		
		sql.append("d.CHANNEL_TYPE txnChannel,f.action_code actionCode,");		
		sql.append("a.ORDER_ID orderId,a.TERM_SEQ tradeSeq,to_char(a.ACCT_DATE, 'YYYYMMDDHH24MISS') tradeTime,");		 
		sql.append(" (SELECT due_moeny FROM t_scs_ordfee WHERE order_id = a.order_id)  txnAmount, a.STAT payStat from t_scs_order a,  t_scs_actlist b, ");
		
		sql.append("(SELECT * FROM (SELECT tb.*,ROW_NUMBER () OVER (PARTITION BY order_id ORDER BY order_id) rn FROM t_scs_payment tb ");
		if(!Charset.isEmpty(cardNo)){
			sql.append(" where  acct_code = ? ");
			paraList.add(cardNo);
		}
	    sql.append(")  WHERE rn = 1) c, ");
		
		sql.append("(SELECT *   FROM (SELECT dd.CHANNEL_TYPE, dd.order_id, ROW_NUMBER () OVER (PARTITION BY order_id ORDER BY order_id) rn FROM T_SCS_PROC_INST dd) WHERE rn = 1) d, ");
		sql.append("t_pdm_action f  ");		 
		sql.append("where a.order_id = b.order_id and a.order_id = c.order_id ");

		sql.append(" and b.ACTION_ID = f.ACTION_ID ");	
		if(!Charset.isEmpty(actionCode)){
			sql.append("and f.action_code = ? ");
			paraList.add(actionCode);
		}
						 
		sql.append("and a.order_id = d.order_id ");
		if(!Charset.isEmpty(txnChannel)){
			sql.append("and d.CHANNEL_TYPE = ? ");
			paraList.add(txnChannel);
		}
		sql.append("and a.acct_date between to_date(?, 'YYYYMMDDHH24MISS') and to_date(?, 'YYYYMMDDHH24MISS') order by a.acct_date") ;
		paraList.add(startTime);
		paraList.add(endTime);
				
		return DAO.queryForList(		
				DBTool.createMSSql(sql.toString(), startRecord, maxRecord), paraList.toArray(new String[0]));
	
	}
	/**
	 * 查询一条订单记录
	 */
	public List getOrderByOrderCode(String ext_order_id,String term_seq){
		String sql = " select o.cust_id, p.acct_code,p.order_id,p.pay_money from t_scs_order o,t_scs_payment p where ext_order_id = ? and term_seq = ? and o.order_id=p.order_id";
		return DAO.queryForList(sql, new Object[]{ext_order_id,term_seq});
	}
	
	public List getOrderByOrderSeq(String ext_order_id){
		String sql = " select o.cust_id, p.acct_code,p.order_id,p.pay_money from t_scs_order o,t_scs_payment p where ext_order_id = ?  and o.order_id=p.order_id";
		return DAO.queryForList(sql, new Object[]{ext_order_id});
	}
	
	/**
	 * 查询一条订单编码和金额
	 */
	public List getOrderBykeep(String term_seq){
		String sql =" select  p.order_id,p.pay_money from t_scs_order o,t_scs_payment p where p.order_id= o.order_id and o.term_seq=? and o.order_type='OT101'";
		return DAO.queryForList(sql, new Object[]{term_seq});
	}

	public Double getPayMoney(String transSeq) {
		String sql = "select pay_money from t_scs_payment where order_id = ?";
		return (Double)DAO.queryForObject(sql, new Object[]{transSeq}, Double.class);
	}
}
