package common.dao;

import java.util.Hashtable;

import org.apache.log4j.Logger;

import common.utils.SpringContextHelper;

public class TbisTanOrderDao {
	
	protected static final Logger log = Logger.getLogger(TbisTanOrderDao.class);
	
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	
	public void saveTanOrder(Hashtable<String,Object> map) {

		StringBuffer sb = new StringBuffer();

		sb
				.append("insert into T_OPP_TRANSFERORDER ")
				.append(
						"(REC_CODE, KEEP, ORDER_CODE, PAY_CUSTCODE,COLLE_CUSTCODE,PAY_MONEY,CREATE_DATE,RECHARGE_STAT,TRAN_STAT,BUS_TYPE,STAT)")
				.append(
						"values (SQ_OPP_TRANSFERORDER.NEXTVAL,?,?,?,?,?,sysdate,?,?,?,?)");

		try {
			DAO.insert(sb.toString(), new Object[] { map.get("KEEP"),
					map.get("ORDER_CODE"), map.get("PAY_CUSTCODE"),
					map.get("COLLE_CUSTCODE"), map.get("PAY_MONEY"),
					map.get("RECHARGE_STAT"), map.get("TRAN_STAT"), map.get("BUS_TYPE"), "S0A" });
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
	
	public void updateRecStat(String keep,String reCode,String reDesc) {
		String sql = "update T_OPP_TRANSFERORDER set RESULT_CODE=? ,RESULT_MSG=?,RECHARGE_STAT=?,UPDATE_DATE=sysdate where KEEP=? ";
		try {
			DAO.update(sql, new Object[] { reCode, reDesc, "S0F",keep });

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateRecSucStat(String keep,String orderId,String reCode,String reDesc) {
		
		String sql = "update T_OPP_TRANSFERORDER set RECHARGE_CODE=? ,RESULT_CODE=? ,RESULT_MSG=?,RECHARGE_STAT=?,UPDATE_DATE=sysdate where KEEP=? ";
		try {
			DAO.update(sql, new Object[] { orderId,reCode, reDesc,"S0C", keep });

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateTraOrder(String keep,String tranOrderId) {
		
		String sql = "update T_OPP_TRANSFERORDER set TRAN_CODE=? ,TRAN_STAT=?,UPDATE_DATE=sysdate where KEEP=? ";
		try {
			DAO.update(sql, new Object[] { tranOrderId,"S0C", keep });

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateTraOrderS0P(String keep) {
		String sql = "update T_OPP_TRANSFERORDER set TRAN_STAT=?,UPDATE_DATE=sysdate where KEEP=? ";
		try {
			DAO.update(sql, new Object[] { "S0P", keep });

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateTraOrderS0F(String keep,String reCode,String reDesc) {
		String sql = "update T_OPP_TRANSFERORDER set RESULT_CODE=? ,RESULT_MSG=? ,TRAN_STAT=?,UPDATE_DATE=sysdate where KEEP=? ";
		try {
			DAO.update(sql, new Object[] { reCode,reDesc,"S0F", keep });

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
