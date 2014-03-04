package common.dao;

import common.utils.SpringContextHelper;


public class TOppOrdfeeDao {
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();

	/**
	 * 获取Sequence
	 */
	public String getSequence(){
		String sql="select SQ_OPP_ORDFEE.NEXTVAL from dual";
		return (String)DAO.queryForObject(sql, String.class);
	}
	
	public String addOrdfee(String preId, String dueMoney, String payMoney){
		String ordfeeID = getSequence();
		String sql = "insert into t_opp_ordfee(ordfee_id,pre_id,currency_code,due_moeny,pay_money,stat) values(?,?,?,?,?,?)";
		DAO.insert(sql, new Object[]{ordfeeID,preId,"CNY",dueMoney,payMoney,"S0A"});
		return ordfeeID;
	}
	
}
