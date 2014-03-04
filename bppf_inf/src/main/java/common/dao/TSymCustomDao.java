package common.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.dao.impl.BaseDaoImpl;
import common.entity.TSymCustom;
import common.utils.SpringContextHelper;

/**
 * 
 * 本类描述:
 * 
 * @version: 企业帐户前置接口 v1.0
 * @author: 广州天讯瑞达通讯技术有限公司(zhuxiaojun)
 * @email: zhuxiaojun@tisson.com
 * @time: 2013-4-19下午11:52:16
 */

public class TSymCustomDao {
	private static final Logger log = LoggerFactory
			.getLogger(TSymCustomDao.class);

	public static BaseDao baseDao = SpringContextHelper.getBaseDaoBean();

	public static BaseDaoImpl dao = (BaseDaoImpl) SpringContextHelper
			.getBaseDaoBean();

	public static BaseDao infDao = SpringContextHelper.getInfBaseDaoBean();

	public boolean add(List<TSymCustom> models) throws Exception{
		// TODO Auto-generated method stub
		String sql = "insert into T_CUM_CUSTOM(CUSTOM_ID,CUST_ID,CUSTOM_TYPE,TH,TH_TYPE,PERIOD_TYPE,EFF_DATE,STAT) "
				+ "values (?, ?, ?, ?,?,'PR005',sysdate,'S0A')";

		log.info("sql=[" + sql + "]");

		Connection con = null;
		
		PreparedStatement prest = null;
		
		try {
			con = dao.getJdbcTemplate().getDataSource().getConnection();
			
			con.setAutoCommit(false);
			
			prest = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			for (TSymCustom model : models) {
				prest.setLong(1, model.getCustomId());
				prest.setLong(2, model.getCustId());
				prest.setString(3, model.getCustomType());
				prest.setString(4, model.getTh());
				prest.setString(5, model.getThType());
				prest.addBatch();
			}
			prest.executeBatch();
			
			con.commit();
			
			con.close();
			
			return true;
		} catch (SQLException e) {
			 try{
				 con.rollback();
				 con.close();
			 }catch(Exception ex){
				 
			 }		
			throw e;
		}

	}

	public boolean update(List<TSymCustom> models) throws Exception {
		// TODO Auto-generated method stub
		String sql = "update T_CUM_CUSTOM set TH=? WHERE CUST_ID =? and CUSTOM_TYPE =? and TH_TYPE=?";

		log.info("sql=[" + sql + "]");

		Connection con = null;
		
		PreparedStatement prest = null;
		
		try {
			con = dao.getJdbcTemplate().getDataSource().getConnection();
			
			con.setAutoCommit(false);
			
			prest = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			for (TSymCustom model : models) {
				prest.setString(1, model.getTh());
				prest.setLong(2, model.getCustId());
				prest.setString(3, model.getCustomType());
				prest.setString(4, model.getThType());
				prest.addBatch();
			}
			prest.executeBatch();
			
			con.commit();
			
			con.close();
			
			return true;
		} catch (SQLException e) {
			 try{
				 con.rollback();
				 con.close();
			 }catch(Exception ex){
				 
			 }
			throw e;
		}

	}
	/**
	 * 
	 * 
	 * @return
	 * @throws InfrastructureException
	 */
	public int quert(String CUST_ID, String CUSTOM_TYPE)
			throws Exception {
		String sql = "select count(TH) from T_CUM_CUSTOM where CUST_ID =? and CUSTOM_TYPE =?";
		log.info(sql);
		try {
			return (Integer) baseDao.queryForObject(sql, new Object[] {
					CUST_ID, CUSTOM_TYPE }, Integer.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public String getTh(String CUST_ID, String CUSTOM_TYPE, String TH_TYPE) throws Exception {
		String sql = "select nvl(sum(TH),0) AS TH from T_CUM_CUSTOM where CUST_ID =? and CUSTOM_TYPE =? and TH_TYPE=?";
		log.info(sql);
		try {
			return (String) baseDao.queryForObject(sql, new Object[] { CUST_ID,
					CUSTOM_TYPE, TH_TYPE }, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	
	/**
	 * 修改累积消费记录的状态
	 * @param CUST_ID
	 * @return
	 * @throws Exception
	 */
	public void updateAmountCount(String CUST_ID) throws Exception {
		log.info("custId=["+CUST_ID+"]");
		String sql = "update T_INF_CONSUME t set t.SUM_STAT='S0N' where  t.CUST_ID =? and t.SUM_STAT='S0A' "; //and t.SUM_STAT='S0A' and t.ORDER_STAT='S0C'";
		log.info(sql);
		try {
			infDao.update(sql, new Object[] { CUST_ID });
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public String getAmountCount(String CUST_ID) throws Exception {
		log.info("custId=["+CUST_ID+"]");
		String sql = "select nvl(sum(t.AMOUNT),0) as AMOUNT from T_INF_CONSUME t where  t.CUST_ID =? and t.SUM_STAT='S0A' and t.ORDER_STAT='S0C'";
		log.info(sql);
		try {
			return (String) infDao.queryForObject(sql,
					new Object[] { CUST_ID }, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// seq
	public long getOrderSeq() throws Exception {
		String sql = "select SQ_CUM_CUSTOM.NEXTVAL from dual";
		long seqid = baseDao.queryForLong(sql);
		return seqid;
	}

	/**
	 * 更新
	 * @param custId
	 * @param custType
	 * @param thType
	 */
	public void updateTh(String custId, String custType, String thType) throws Exception{
		String sql = "update T_CUM_CUSTOM set th = ? where CUST_ID =? and CUSTOM_TYPE =? and TH_TYPE=?";
		log.info(sql);
		try {
			baseDao.queryForObject(sql, new Object[] {"0", custId,
					custType, thType }, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}
}
