package common.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;

import common.utils.Charset;
import common.utils.SpringContextHelper;

public class TCumInfoDao {
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	public static BaseDao infDao = SpringContextHelper.getInfBaseDaoBean();
	private static Log log = LogFactory.getLog(TCumInfoDao.class);

	/**
	 * 根据客户编码查询绑卡状态
	 * 
	 * @param custCode
	 * @return
	 */
	public String getCardBindStat(String custCode) {
		String sql = "select bind_state from T_CUM_REGBINDCARD where  cust_code=? and stat='S0A' order by bind_date desc";
		log.info("custCode=[" + custCode + "]");
		log.info("sql=[" + sql + "]");
		// String stat = (String) infDao.queryForObject(sql, new Object[] { custCode }, String.class);
		List list = DAO.queryForList(sql, new Object[] { custCode });
		String stat = "";
		if (list != null && !list.isEmpty()) {
			stat = ((Map) list.get(0)).get("bind_state").toString();
		}
		return stat;
	}

	/**
	 * 通过custCode查询资金管理模式
	 * 
	 * @param custCode
	 * @return
	 */
	public String getBankMode(String custCode) {
		String sql = "select bankroll_type from T_PNM_PARTNER where prtn_id = (select prtn_id from T_CUM_INFO where cust_code = ?)";
		log.info("custCode=[" + custCode + "]");
		log.info("sql=[" + sql + "]");
		String bankMode = (String) DAO.queryForObject(sql, new Object[] { custCode }, String.class);
		if (Charset.isEmpty(bankMode, true)) {
			bankMode = "BT1001";
		}
		return bankMode;
	}

	/**
	 * 机构企业账户匹配验证
	 * 
	 * @return
	 */
	public static boolean verifyMerIdCustCode(String custCode, String merId) {

		boolean r = false;

		String m = getOrgMerIdFromCustCode(custCode);

		if (m != null && m.equals(merId)) {

			r = true;
		}

		return r;
	}

	/**
	 * 查询关联机构
	 */
	public static List<Map> getRelationMerId(String merId) {
		String sql = "select obj_code from t_cum_obj o  where o.prod_id=200 and o.cust_id in "
				+ "(select i.cust_id from t_cum_info i where i.prtn_id=(select prtn_id from t_pnm_partner where prtn_code='" + merId + "')) and o.stat='S0A'";

		List<Map> obj = DAO.queryForList(sql);

		return obj;
	}

	/**
	 * 查询关联机构
	 */
	public static String getMerIdByCust(String cust_code) {
		String sql = "select p.prtn_code from t_pnm_partner p where p.prtn_type='PT403' start with p.prtn_id="
				+ "(select i.prtn_Id from t_cum_info i where i.cust_type='C02' and i.cust_code=? )connect by prior p.parent_id=p.prtn_id";

		String merId = (String) DAO.queryForObject(sql, new Object[] { cust_code }, String.class);
		return merId;

	}

	public static boolean getMerIdByCustCode(String custCode, String merId) {

		String mer = getMerIdByCust(custCode);

		List<Map> mers = getRelationMerId(merId);

		boolean r = true;

		for (Map m : mers) {

			if (m.get("OBJ_CODE").equals(mer)) {

				r = false;

				break;
			}
		}

		return r;

	}

	/**
	 * 通过CUSTCODE查询关联机构
	 */
	public static boolean getMerIdByCustCode1(String custCode, String merId) {
		String sql = "select count(o.obj_code) from t_cum_obj o " + "where o.cust_id=(select i.cust_id from t_cum_info i "
				+ "where i.prtn_id=(select p.prtn_id from t_pnm_partner p " + "where p.prtn_type='PT403' start with p.prtn_id=(select i.prtn_Id " + "from t_cum_info i "
				+ "where i.cust_type='C02' and i.cust_code=?)connect by prior p.parent_id=p.prtn_id))and o.prod_id=200 and o.obj_code=?";

		Long count = DAO.queryForLong(sql, new Object[] { custCode, merId });

		return (count < 1) ? true : false;
	}

	/**
	 * * 根据客户编码查询客户是否存在
	 * 
	 * @return
	 */
	public static boolean isExistCust(String custCode) {

		String sql = "select count(*) from t_cum_info t where t.stat='S0A' and t.cust_code = ?";
		int count = DAO.queryForInt(sql, new Object[] { custCode });
		if (count > 0)
			return true;

		return false;
	}

	/**
	 * * 根据客户编码查询客户是否存在
	 * 
	 * @return
	 */
	public static boolean isRegExistCust(String custCode) {

		String sql = "select count(*) from t_cum_info t where t.cust_code = ?";
		int count = DAO.queryForInt(sql, new Object[] { custCode });
		if (count > 0)
			return true;

		return false;
	}
	/**
	 * * 根据客户编码查询客户是否代收类型
	 * 
	 * @return
	 */
	public static boolean isDSFCust(String custCode) {

		String sql = "select count(1) from t_cum_info t where t.stat='S0A' and t.cust_code = ? and t.cust_type= 'C03'";
		int count = DAO.queryForInt(sql, new Object[] { custCode });
		if (count > 0)
			return true;
		return false;
	}

	/**
	 * 通过merId查询客户编码
	 */
	public static String getCustCode(String merId) {
		String sql = "select cust_code from t_cum_info t where t.prtn_id = (select prtn_id" + " from t_pnm_partner p where p.prtn_code = ?) and t.cust_type='C02'";

		String custCode = (String) DAO.queryForObject(sql, new Object[] { merId }, String.class);
		return custCode;
	}

	/**
	 * 通过代收网点查询企业账户
	 */
	public String getCustCodeFromDSF(String cust_code) {
		String sql = "select cust_code from t_cum_info where prtn_id = (select prtn_id from t_cum_info t where t.cust_code = ?) and cust_type='C02'";
		try {
			return (String) DAO.queryForObject(sql, new Object[] { cust_code }, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 通过客户编码查询接入机构编码
	 */
	public static String getOrgMerIdFromCustCode(String cust_code) {
		String sql = "select prtn_code from t_pnm_partner p where p.prtn_type='PT403' start with p.prtn_id="
				+ "(select i.prtn_Id from t_cum_info i where i.cust_type='C02' and i.cust_code=?)" + " connect by prior p.parent_id=p.prtn_id";
		String custCode = (String) DAO.queryForObject(sql, new Object[] { cust_code }, String.class);
		return custCode;
	}

	/**
	 * 通过客户编码查询获取终端号
	 */
	public static String getTmnNumFromMerId(String prtnCode) {
		String termCode = null;
		String sql = " select distinct t.link_num from t_pnm_server t where t.prtn_id = " + "(select prtn.prtn_id from t_pnm_partner prtn where prtn.prtn_code = ?)";
		termCode = (String) DAO.queryForObject(sql, new Object[] { prtnCode }, String.class);
		return termCode;
	}

	/**
	 * 通过客户编码查询客户名称
	 */
	public static String getCustName(String cust_code) {
		String sql = "select cust_name from t_cum_info where cust_code=?";
		List list = DAO.queryForList(sql, new Object[] { cust_code });
		String custName = ((Map) list.get(0)).get("CUST_NAME").toString();
		return custName;
	}

	/**
	 * 通过客户编码查询区域编码
	 */
	public static String getAreaCode(String cust_code) {
		
		String custName ="";
		String sql = "select AREA_CODE from t_cum_info where cust_code=?";
		List list = DAO.queryForList(sql, new Object[] { cust_code });
		if(list.size()>0){
			custName = ((Map) list.get(0)).get("AREA_CODE").toString();
		}
		
		return custName;
	}
	/**
	 * 通过手机号码(前七位)查询其省份编码
	 */
	public static String getProvinceCodeByPhoneNum(String phoneNum) {
		
		 phoneNum =phoneNum.substring(0, 7);//取前七位
		String sql = "select province_code from T_SYM_REWARD_CASE where case_code=?";
		String provinceCode = "";
		log.info("sql="+sql  + "  --case_code="+phoneNum);
		try {
			provinceCode = (String) DAO.queryForObject(sql, new Object[] { phoneNum }, String.class);
		} catch (Exception e) {
			log.error("查询手机号码对应的身份编码出错, "+e.getMessage());
			return "";
		}
		
		return provinceCode;
	}

	/**
	 * 通过客户编码查询合作伙伴编码
	 */
	public String getPrtnCode(String cust_code) {
		String sql = " select prtn_code from t_pnm_partner  where prtn_id=" + "(select prtn_id from t_cum_info where cust_code=?)";
		List list = DAO.queryForList(sql, new Object[] { cust_code });
		String prtnCode = ((Map) list.get(0)).get("PRTN_CODE").toString();
		return prtnCode;
	}

	/**
	 * 通过客户编码查询合作伙伴标识
	 */
	public String getPrtnId(String cust_code) {
		String sql = " select prtn_id from t_pnm_partner  where prtn_id=" + "(select prtn_id from t_cum_info where cust_code=?)";
		List list = DAO.queryForList(sql, new Object[] { cust_code });
		String prtnCode = ((Map) list.get(0)).get("PRTN_ID").toString();
		return prtnCode;
	}

	/**
	 * 通过签约id 查询网点的客户编码(客户类型为C03)
	 */
	public String getCustCodeByContractId(String contractId) {
		String sql = " select cust_code from t_cum_info where cust_id ="
				+ "(select cust_id from t_cum_acct where acct_id =(select acct_id from t_cum_acct_attr where value1 =? and attr_id=2810))";
		try {
			return (String) DAO.queryForObject(sql, new Object[] { contractId }, String.class);
		} catch (Exception erdae) {
			log.info(erdae);
			return null;
		}
	}

	/**
	 * 通过签约id 查询所属企业账户的客户编码(客户类型为C02)
	 */
	public String getCustCodeBySignId(String contractId) {
		String sql = "SELECT cust_code FROM t_cum_info WHERE cust_type='C02' and prtn_id ="
				+ "(select prtn_id from t_cum_info where cust_id =(select cust_id from t_cum_acct where acct_id ="
				+ "(select acct_id from t_cum_acct_attr where value1 =? and attr_id=2810)))";
		try {
			return (String) DAO.queryForObject(sql, new Object[] { contractId }, String.class);
		} catch (Exception erdae) {
			log.info(erdae);
			return null;
		}
	}

	/**
	 * 通过客户ID查询支付对象
	 */
	public String getObjCode(String custId) {
		String sql = "select obj_code from t_cum_obj where cust_id =? ";
		try {
			return (String) DAO.queryForObject(sql, new Object[] { custId }, String.class);
		} catch (EmptyResultDataAccessException erdae) {
			erdae.printStackTrace();
			return null;
		}
	}

	/**
	 * 验证客户编码，custid可能是签约网点的客户编码id,也可以是企业账户id
	 * 
	 * @param custId
	 * @return
	 */
	public boolean validateCustCode(String custId, String custCode) {
		String sql = "SELECT count(*) FROM t_cum_info t WHERE t.cust_code =? and t.prtn_id =(SELECT prtn_id FROM t_cum_info WHERE cust_id = ? ) and cust_type='C02'";
		int count = DAO.queryForInt(sql, new Object[] { custCode, custId });
		if (count > 0)
			return true;
		return false;
	}

	/**
	 * 根据工号获取企业账户客户编码
	 * 
	 * @param staffCode
	 * @return
	 */
	public String getCustCodeByStaff(String staffCode) {
		try {
			String sql = "select info.cust_code  from t_cum_info info where info.cust_type='C02' "
					+ "and info.prtn_id=(select priv.prtn_id from t_cum_priv priv,t_sym_staff staff "
					+ "where staff.org_id=priv.org_id and staff.staff_code=? and priv.stat = 'S0A')";
			String custCode = (String) DAO.queryForObject(sql, new Object[] { staffCode }, String.class);

			return custCode;
		} catch (EmptyResultDataAccessException erdae) {
			erdae.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据工号查询对应的证件号码
	 * 
	 * @param custCode
	 * @return
	 */
	public static final String getCertNbrByStaffCode(String staffCode) {
		try {
			String sql = "select staff.cert_nbr from t_cum_priv priv,t_sym_staff staff where  staff.staff_code=? and staff.org_id=priv.org_id and priv.stat = 'S0A'";
			String nbr = (String) DAO.queryForObject(sql, new Object[] { staffCode }, String.class);
			return nbr;
		} catch (EmptyResultDataAccessException erdae) {
			erdae.printStackTrace();
			return null;
		}
	}
	/**
	 * 根据用户名获取对应的手机号码
	 * @param staffCode
	 * @return
	 */
	public static final String getMobileByStaffCode(String staffCode) {
		try {
			String sql = "select mobile from t_sym_staff where staff_code=? and stat = 'S0A'";
			log.info("sql="+ sql + "      [staffCode="+staffCode+"]");
			String mobile = (String) DAO.queryForObject(sql, new Object[] { staffCode }, String.class);
			return mobile;
		} catch (EmptyResultDataAccessException erdae) {
			erdae.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 根据客户编码获取默认工号
	 * 
	 * @param custCode
	 * @return
	 */
	public static final String getDefaultStaffCodeByCustCode(String custCode) {
		try {
			String sql = "select staff.staff_code from t_cum_priv priv,t_sym_staff staff where staff.org_id=priv.org_id and priv.prtn_id=(select prtn_id from t_cum_info where cust_code=?) and  staff.ext_flag='DEF' and priv.stat = 'S0A'";
			String staffCode = (String) DAO.queryForObject(sql, new Object[] { custCode }, String.class);

			return staffCode;
		} catch (EmptyResultDataAccessException erdae) {
			erdae.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据cust_id查询注册时间
	 * 
	 * @param custId
	 */
	public String getRegDateByCustId(String custId) {
		String sql = "select eff_date from t_cum_info where cust_id=?";
		String date = (String) DAO.queryForObject(sql, new Object[] { custId }, String.class);
		return date;
	}

	/**
	 * 根据cust_code查询注册渠道号
	 * 
	 * @param custCode
	 * @return
	 */
	public String getRegCanal(String custCode) {
		try {
			String sql = "select info.channel_type  from t_cum_info info where info.cust_code=?";
			String regChanal = (String) DAO.queryForObject(sql, new Object[] { custCode }, String.class);
			if (Charset.isEmpty(regChanal, true)) {
				regChanal = "";
			}
			return regChanal;
		} catch (EmptyResultDataAccessException erdae) {
			erdae.printStackTrace();
			return null;
		}
	}
}
