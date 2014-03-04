package common.dao;

import java.util.List;

import common.utils.SpringContextHelper;

public class TCumAttrDao {
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();

	/**
	 * 通过商户编码查询权限
	 */
	public static List getPrivByPrtnCode(String prtn_code) {
		String sql = "select ca.cust_id, ca.attr_id, pv.func_id, pv.func_name from t_cum_attr ca,"
				+ " t_sym_funcpriv pv where pv.sub_attr_id = ca.attr_id and ca.cust_id ="
				+ " (select i.cust_id from t_cum_info i where i.prtn_id = (select p.prtn_id from"
				+ " t_pnm_partner p where p.prtn_code = ?) and i.cust_type = 'C02')";

		List list = DAO.queryForList(sql, new Object[] { prtn_code });
		return list;
	}

	public static String getPdline(String custCode) {

		String sql = "select a.value1 from t_cum_attr a where a.cust_id =(select i.cust_id from t_cum_info i where i.cust_code = ? ) and a.attr_id = 4224";

		String pdls = (String) DAO.queryForObject(sql,
				new Object[] { custCode }, String.class);
		return pdls;
	}

	/**
	 * 通过客户编码查询地址
	 * 
	 * @param custCode
	 * @author lichunan
	 * @return
	 */
	public static String getAddr(String custCode) {
		String sql = "select a.value1 from t_cum_attr a where a.cust_id =(select i.cust_id from t_cum_info i where i.cust_code = ? ) and a.attr_id = 2581";
		String addr = (String) DAO.queryForObject(sql,
				new Object[] { custCode }, String.class);
		return addr;
	}

	/**
	 * 通过客户编码查询权限
	 * 
	 * @version: 1.00
	 * @history: 2012-9-24 下午04:04:18 [created]
	 * @author HaiHong Liu
	 * @param cust_code
	 * @param channelCode
	 * @return
	 * @see
	 */
	public static List getPrivByCustCode(String cust_code, String channelCode) {
		StringBuffer sb = new StringBuffer();
		sb.append("select fp.show_seq,fp.func_id,fp.func_name,p.priv_type,p.priv_url,p.oper_mode");
		sb.append(" from t_sym_funcpriv fp left join t_sym_priv p on fp.priv_id = p.priv_id where 1 = 1");
		sb.append(" and fp.stat = 'S0A' and fp.priv_id in");
		sb.append(" (select priv_id from t_sym_funcpriv where attr_id in");
		sb.append(" (select attr_id from t_cum_attr where cust_id =");
		sb.append(" (select cust_id from t_cum_info where cust_code = ?)))");
		sb.append(" and p.oper_mode = ?");
		String type;
		if (channelCode.equals("20") && channelCode != null) {
			type = "INFCLN";
		} else {
			type = "INFWS";
		}

		List list = DAO.queryForList(sb.toString(), new Object[] { cust_code,
				type });
		return list;
	}

	/**
	 * 通过工号查询权限
	 * 
	 * @version: 1.00
	 * @history: 2012-9-24 下午04:04:18 [created]
	 * @author HaiHong Liu
	 * @param cust_code
	 * @param channelCode
	 * @return
	 * @see
	 */
	public static boolean getIvrFunc(String staffId, String ivr) {
		try {
			String sql = "SELECT SR.ROLE_ID FROM T_SYM_STAFFROLE SR WHERE SR.ROLE_ID='224' AND SR.STAFF_ID=? AND SR.ROLE_ID IN (SELECT RP.ROLE_ID FROM T_SYM_ROLEPRIV RP WHERE PRIV_ID=(SELECT P.PRIV_ID FROM T_SYM_PRIV P WHERE P.PRIV_DESC='IVR' AND P.PRIV_URL=?))";
			List list = DAO.queryForList(sql, new Object[] { staffId, ivr });
			if (list.size() > 0) {
				return true;
			}
			return false;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	// 密码修改获取权限
	public static List getPrivByCC(String cust_code) {
		StringBuffer sb = new StringBuffer();
		sb.append("select fp.show_seq,fp.func_id,fp.func_name,p.priv_type,p.priv_url,p.oper_mode");
		sb.append(" from t_sym_funcpriv fp left join t_sym_priv p on fp.priv_id = p.priv_id where 1 = 1");
		sb.append(" and fp.stat = 'S0A' and fp.priv_id in");
		sb.append(" (select priv_id from t_sym_funcpriv where attr_id in");
		sb.append(" (select attr_id from t_cum_attr ta where exists ");
		sb.append(" (select cust_id from t_cum_info ti where ti.cust_id = ta.cust_id and cust_code = ?)))");
		// sb.append(" and p.oper_mode = ?");
		// String type;
		// if(channelCode.equals("20") && channelCode != null){
		// type = "INFCLN";
		// }else{
		// type = "INFWS";
		// }

		List list = DAO.queryForList(sb.toString(), new Object[] { cust_code });
		return list;
	}

	/**
	 * 通过客户编码查询权限
	 */
	public static List getPrivByCustCode(String cust_code) {
		String sql = "select * from t_sym_funcpriv where attr_id in (select attr_id from"
				+ " t_cum_attr where cust_id = (select cust_id from t_cum_info where cust_code =?))";
		List list = DAO.queryForList(sql, new Object[] { cust_code });
		return list;
	}

	/**
	 * 通过客户编码查询代扣渠道
	 * 
	 * @param cust_code
	 * @return
	 */
	public List getDeductChannel(String cust_code) {
		String sql = "select a.value1 from t_cum_attr a where a.attr_id = 2633 "
				+ "and exists (select 1  from t_cum_info i where i.cust_code = ?  and a.cust_id = i.cust_id)";
		List list = DAO.queryForList(sql, new Object[] { cust_code });
		return list;
	}

	/**
	 * 通过客户编码查询证件号码
	 * 
	 * @param custCode
	 * @author lichunan
	 * @return
	 */
	public static String getCertNo(String custCode) {
		String sql = "select value1 from T_CUM_ATTR where attr_id=2587 and cust_id =(select cust_id from t_cum_info where cust_code = ?)";
		String certNo = (String) DAO.queryForObject(sql,
				new Object[] { custCode }, String.class);
		return certNo;
	}

	/**
	 * 通过客户编码查询项目号
	 * 
	 * @param custCode
	 * @author lichunan
	 * @return
	 */
	public static String getProjectNo(String custCode) {
		String sql = "select r.value1 from t_cum_attr r left join t_cum_info c on r.cust_id = c.cust_id where c.cust_code = ? and r.attr_id = 2818";
		String projectNo = (String) DAO.queryForObject(sql,
				new Object[] { custCode }, String.class);
		return projectNo;
	}

}
