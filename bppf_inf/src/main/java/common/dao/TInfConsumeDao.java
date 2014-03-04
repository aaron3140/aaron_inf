package common.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.entity.TInfConsume;
import common.utils.SpringContextHelper;

public class TInfConsumeDao {
	private static final Log log = LogFactory.getLog(TInfConsumeDao.class);
	public static BaseDao infDao = SpringContextHelper.getInfBaseDaoBean();
	public static BaseDao bisDao = SpringContextHelper.getBaseDaoBean();

	public boolean insert(TInfConsume c) {

		log.info("插入消费记录,id为[" + c.getConsumeId() + "]");
		try {
			StringBuffer sb = new StringBuffer("insert into T_INF_CONSUME ");
			sb.append("(CONSUME_ID, CUST_ID, ORDERNO, ACCTTYPE, KEEP, CHANNEL_TYPE, TERM_ID, ACTION_CODE, PDLINE_ID,AMOUNT,ACCT_DATE,STAT,SUM_STAT,REMARK)")
					.append("values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			int count = infDao.insert(sb.toString(), new Object[] { c.getConsumeId(), c.getCustId(), c.getOrderNo(), c.getAcctType(), c.getKeep(), c.getChannelType(),
					c.getTermId(), c.getActionCode(), c.getPdLineId(), c.getAmount(), c.getAcctDate(), c.getStat(),c.getSum_stat(), c.getRemark() });
			if (count > 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @return 消费id，从序列中获取
	 */
	public long getConsumeId() {
		String sql = "select SQ_INF_CONSUME.NEXTVAL from dual";
		return infDao.queryForLong(sql);
	}
	
	public long getPdlineId() {
		String sql = "select SQ_PDM_PDLINE.nextval from dual";
		return bisDao.queryForLong(sql);
	}

	public boolean updateSumStat(String custId) throws Exception {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		Calendar cal = Calendar.getInstance();
//		String dateStr = cal.get(Calendar.YEAR)+ "-"+(cal.get(Calendar.MONTH) + 1)+"-1 00:00:00";
//		Date date = sdf.parse(dateStr);
		String sql = "update t_inf_consume set sum_stat='S0N' where cust_id=?";
		int n = infDao.update(sql, new Object[]{custId});
		log.info("支付密码输入正确，更新["+n+"]条消费记录的状态为S0X,custId=["+custId+"]");
		return n>0;
	}
	
	public void updateOrderStat(Long Id,String stat) throws Exception {

		String sql = "update t_inf_consume set ORDER_STAT=? where CONSUME_ID=?";
		infDao.update(sql, new Object[]{stat,Id});

	}
	
	public void updateOrderStatE(Long Id,String stat) {

		String sql = "update t_inf_consume set ORDER_STAT=? where CONSUME_ID=?";
		
		try{
			
			infDao.update(sql, new Object[]{stat,Id});
			
		}catch(Exception e){
			
			log.info(e.getMessage());
		}
	}
	
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Calendar cal = Calendar.getInstance();
		String dateStr = cal.get(Calendar.YEAR)+ "-"+(cal.get(Calendar.MONTH) + 1)+"-1 00:00:00";
		Date date = sdf.parse(dateStr);
		System.out.println(date);
	}
	
	public List<Map> getTConsumeList() {

		String sql ="select t.ORDERNO,t.TERM_ID from T_INF_CONSUME t where t.order_stat='S0P' and t.acct_date>(sysdate-1)";
		
		List<Map> list = infDao.queryForList(sql);
//		Query query = session.createSQLQuery(sql);
//		List<Object[]> list = query.list();
		return list;

	}
	
	public boolean isExist(String gameAcc,String amount) {

		String sql ="select count(consume_id) from t_inf_consume where remark=? and amount=? and acct_date > trunc(sysdate - 5/1440,'mi')";
		
		int r  = infDao.queryForInt(sql, new Object[]{gameAcc,amount});

		return r>0;

	}
	
	public void updateTConsume(String orderNo,String stat) {

//		Session session = HibernateSessionFactory.getSession();
		String sql="update T_INF_CONSUME set ORDER_STAT=? where ORDERNO=?";
		
		infDao.update(sql, new Object[]{stat,orderNo});
//		Connection con = null;
//		try {
//			
//			con = session.connection();
//			con.setAutoCommit(false);
//			Statement stmt = con.createStatement();
//			stmt.executeUpdate(sql);
//
//			con.commit();
////			session.connection().createStatement().executeUpdate(sql);
//		} catch (Exception e) {
//			e.printStackTrace();
//			try {
//				con.rollback();
//			} catch (SQLException e1) {
//			}
//		}

	}
}
