package common.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.entity.TRegBindCard;
import common.utils.Charset;
import common.utils.SpringContextHelper;

public class TRegBindCardDao {
	public static BaseDao bisDao = SpringContextHelper.getBaseDaoBean();
	public static BaseDao infDao = SpringContextHelper.getInfBaseDaoBean();
	private static Log log = LogFactory.getLog(TRegBindCardDao.class);

	/**
	 * 根据交易流水号获取绑卡订单信息
	 * @param custCode
	 * @param transSeq
	 * @throws Exception
	 */
	public Map getOrderInfoByTransSeq(String transSeq) throws Exception {
		 String sql = "select * from t_cum_regbindcard c  where  c.bind_orderno=? and stat='S0A'";
//		 String sql = "select * from T_INF_REGBINDCARD c  where  c.bind_orderno=?";
		 log.info(" bind_orderno=["+transSeq+"]");
		Map map = bisDao.queryForMap(sql, new Object[]{transSeq});
		return map;
	}
	public Map getOrderInfoByTransSeq(String transSeq,String bindState)  {
		String sql = "select * from t_cum_regbindcard c  where  c.bind_orderno=? and c.bind_state=? and stat='S0A'";
//		 String sql = "select * from T_INF_REGBINDCARD c  where  c.bind_orderno=?";
		log.info(" bind_orderno=["+transSeq+"] bindState=["+bindState+"]");
		Map map = null;
		try {
			map = bisDao.queryForMap(sql, new Object[]{transSeq,bindState});
		} catch (Exception e) {
			log.error("没有找到符合条件的订单信息."+e.getMessage());
			return null;
		}
		return map;
	}
	public String getOrderStat(String orderNo) throws Exception {
		 String sql = "select stat from t_cum_regbindcard c  where  c.bind_orderno=?";
		String stat= (String) bisDao.queryForObject(sql, new Object[]{orderNo},String.class);
		log.info(" bind_orderno=["+orderNo+"]  query result  --stat=["+stat+"]");
		return stat;
	}
	/**
	 * 根据交易流水号更新绑卡订单信息
	 * @param custCode
	 * @param transSeq
	 * @throws Exception
	 */
	public int updateBindStatToS0D(TRegBindCard card) throws Exception {
		List<String> params = new ArrayList<String>();
		StringBuffer sb = new StringBuffer("update t_cum_regbindcard set bind_state='S0D'");
		String bankCode = card.getBankCode();
		String bankName = card.getBankName();
		String areaCode = card.getAreaCode();
		String bankAcct = card.getBankAcct();
		String transAccName = card.getTransAccName();
		String cerNo = card.getCerNo();
		String openPhone = card.getOpenPhone();
		String remark = card.getRemark();
		if(!Charset.isEmpty(bankCode, true)){
			sb.append(",BANK_CODE=?");
			params.add(bankCode);
		}
		if(!Charset.isEmpty(bankName, true)){
			sb.append(",BANK_NAME=?");
			params.add(bankName);
		}
		if(!Charset.isEmpty(areaCode, true)){
			sb.append(",AREA_CODE=?");
			params.add(areaCode);
		}
		if(!Charset.isEmpty(bankAcct, true)){
			sb.append(",BANK_ACCT=?");
			params.add(bankAcct);
		}
		if(!Charset.isEmpty(transAccName, true)){
			sb.append(",TRANSACC_NAME=?");
			params.add(transAccName);
		}
		if(!Charset.isEmpty(cerNo, true)){
			sb.append(",CER_NO=?");
			params.add(cerNo);
		}
		if(!Charset.isEmpty(openPhone, true)){
			sb.append(",OPEN_PHONE=?");
			params.add(openPhone);
		}
		if(!Charset.isEmpty(remark, true)){
			sb.append(",REMARK=?");
			params.add(remark);
		}
		sb.append(" where  bind_orderno=? and stat='S0A' and bind_state='S0A'");
		params.add(card.getBindOrderNo());
		log.info("sql="+sb.toString());
		log.info(card);
		Object[] object = new Object[params.size()];
		for (int i = 0; i < params.size(); i++) {
			object[i] = params.get(i);
		}
		int n = bisDao.update(sb.toString(), object);
		log.info("更新["+n+"]条记录 bind_order_no="+card.getBindOrderNo());
		return n;
	}
	
	
	public void add(TRegBindCard card) throws Exception {
		long bindId = getBindId();
		long regId = getRegIdByCustCode(card.getCustCode());
		String sql = "insert into t_cum_regbindcard(BIND_ID,REG_ID,STAFF_CODE,CUST_CODE,BIND_DATE,BIND_ORDERNO,BIND_STATE,BANK_CODE,BANK_NAME,BANK_OPEN,AREA_CODE,BANK_ACCT,TRANSACC_NAME,CER_NO,OPEN_PHONE,STAT,REMARK) values(?,?,?,?,sysdate,?,?,?,?,?,?,?,?,?,?,?,?)";
		bisDao.insert(sql, new Object[] { bindId, regId, card.getStaffCode(), card.getCustCode(), card.getBindOrderNo(),//
				card.getBindState(), card.getBankCode(), card.getBankName(), card.getBankOpen(), card.getAreaCode(), card.getBankAcct(),//
				card.getTransAccName(), card.getCerNo(), card.getOpenPhone(), card.getStat(), card.getRemark() });
	}

	public long getBindId() throws Exception {
		 String sql = "select SQ_CUM_REGBINDCARD.NEXTVAL from dual";
		 long bindId = bisDao.queryForLong(sql);
		 log.info("bindId=["+bindId+"]");
		return bindId;
	}

	public long getRegIdByCustCode(String custCode) throws Exception {
		String sql = "select reg_id from T_INF_CLNREGLOG where cust_code=? and stat='S0A' order by reg_date desc";
		long id = -1;//根据custcode没有找到对应的注册id
		try {
			List list = infDao.queryForList(sql, new Object[] { custCode });
			if(list!=null && !list.isEmpty()){
				id = (Long) list.get(0);
			}
		} catch (Exception e) {
//			e.printStackTrace();
			log.info("没有找到对应的注册记录 custCode=["+custCode+"]");
		}
		log.info("regId=[" + id + "] custCode=[" + custCode + "]");
		return id;
	}


	/**
	 * 是否存在有效的绑卡信息
	 * @param custCode
	 * @param orderNo
	 * @return  true 存在，fasle不存在
	 */
	public boolean hasBindCardInfo(String custCode) {
		String sql = "select count(1) from t_cum_regbindcard  where cust_code=? and bind_state='S0C' and stat='S0A'";
		log.info("cust_code=[" + custCode + "]");
		int n = bisDao.queryForInt(sql, new Object[]{custCode});
		return n>0;
	}
	/**
	 * 更新订单状态为无效【修改绑卡信息时调用】
	 * @param custCode
	 * @param orderNo
	 */
	public void updateStateToS0X(String custCode) {
		String sql = "update t_cum_regbindcard set stat='S0X' where cust_code=? and bind_state='S0C' and stat='S0A'";
		log.info("cust_code=[" + custCode + "]");
		bisDao.update(sql, new Object[]{custCode});
	}
	/**
	 * 更新订单状态为成功
	 * @param custCode
	 * @param orderNo
	 */
	public void updateBindStateToSuccess(String custCode, String orderNo) {
		String sql = "update t_cum_regbindcard set bind_state='S0C' where cust_code=? and bind_orderno=? and stat='S0A'";
		log.info("cust_code=[" + custCode + "] bind_orderno=[" + orderNo + "]");
		bisDao.update(sql, new Object[]{custCode,orderNo});
	}
	/**
	 * 更新订单状态为失败
	 * @param custCode
	 * @param orderNo
	 */
	public void updateBindStateToFail(String orderNo,String remark) {
		String sql = "update t_cum_regbindcard set bind_state='S0F',stat='S0X',remark=? where  bind_orderno=? and stat='S0A'";
		log.info(" bind_orderno=[" + orderNo + "]");
		bisDao.update(sql, new Object[]{remark,orderNo});
	}
	/**
	 * 更新订单状态为失败
	 * @param custCode
	 * @param orderNo  
	 */
//	private void updateBindStateToFailByOrderNo( String orderNo) {
//		String sql = "update t_cum_regbindcard set bind_state='S0F',stat='S0X' where  bind_orderno=? and stat='S0A'";
//		log.info("bind_orderno=[" + orderNo + "]");
//		infDao.update(sql, new Object[]{orderNo});
//	}


	/**
	 * 第一次绑卡还是修改绑卡信息 true:第一次绑卡 false：修改
	 * @param custCode
	 * @return
	 */
	public boolean isFristBindCardOrChange(String custCode,String bankAcct) {
		String sql = "select count(1) from t_cum_regbindcard where cust_code=? and bank_acct=? and bind_state='S0C' and stat='S0A'";
		log.info("cust_code=[" + custCode + "] bank_acct=["+bankAcct+"]");
		int n = bisDao.queryForInt(sql, new Object[]{custCode,bankAcct});
		return n==0;
	}


	public String getBankAcctByCustCode(String custCode) {
		String sql = "select BANK_ACCT from t_cum_regbindcard where cust_code=? and stat='S0C'";
		log.info("cust_code=[" + custCode + "]");
		String bankAcct = (String) bisDao.queryForObject(sql, String.class);
		return bankAcct;
	}


	/**
	 * 是否处于绑卡中
	 * @param custCode
	 * @param bankAcct
	 * @return
	 */
	public boolean isBinding(String custCode) {
		String sql = "select count(1) from t_cum_regbindcard where cust_code=?  and bind_state='S0D' and stat='S0A'";
		log.info("cust_code=[" + custCode + "] ");
		int n = bisDao.queryForInt(sql, new Object[]{custCode});
		return n>0;
	}
	
	/**
	 * 根据客户编码和订单号将绑卡状态为S0A且订单状态为S0A的订单改为无效S0X
	 * @param custCode
	 * @param orderNo
	 */
	public void updateOrderToS0X(String custCode,String orderNo,String remark) {
		String sql = "update t_cum_regbindcard set stat='S0X',remark=? where cust_code=? and bind_orderno<>? and stat='S0A' and bind_state='S0A'";
		log.info("sql="+sql);
		log.info("cust_code=[" + custCode + "] bind_orderno=[" + orderNo + "]");
		bisDao.update(sql, new Object[]{remark,custCode,orderNo});
	}
	
	/**
	 * 
	 * @param custCode
	 */
	public void updateBindStatFromS0D2S0A(String custCode,String remark) {
		String sql = "update t_cum_regbindcard set bind_state='S0A',stat='S0X',remark=? where cust_code=? and bind_state='S0D' and stat='S0A'";
		log.info("sql="+sql);
		log.info("cust_code=[" + custCode + "] ");
		bisDao.update(sql, new Object[]{remark,custCode});
	}
	/**
	 * 根据客户编码查询绑卡状态
	 * @param custCode
	 * @return
	 */
	public String getBindStateByCustCode(String custCode) {
		String sql = "select bind_state from  t_cum_regbindcard  where cust_code=? and stat='S0A' order by bind_date desc";
		log.info("sql="+sql);
		log.info("cust_code=[" + custCode + "] ");
		String bindState = "S00";//未绑卡
		try {
			List list = bisDao.queryForList(sql, new Object[]{custCode});
			if(list!=null && !list.isEmpty()){
				bindState = (String) ((Map)list.get(0)).get("BIND_STATE");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return bindState;
	}
	/**
	 * 根据客户编码查询订单号
	 * @param custCode
	 * @return
	 */
	public Map getOrderInfoByCustCode(String custCode) {
		String sql = "select bind_orderno,open_phone from t_cum_regbindcard where cust_code=? and stat='S0A' and (bind_state='S0A' or bind_state='S0D') order by bind_date desc";
		log.info("sql="+sql);
		log.info("cust_code=[" + custCode + "] ");
		try {
			List list = bisDao.queryForList(sql, new Object[]{custCode});
			if(list!=null&& !list.isEmpty()){
				return (Map) list.get(0);
			}
			return null;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}
}
