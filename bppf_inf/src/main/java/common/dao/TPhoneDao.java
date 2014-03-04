package common.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import common.utils.SpringContextHelper;

public class TPhoneDao {

	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	private static Log log = LogFactory.getLog(TPhoneDao.class);

	/**
	 * 获取工号
	 * 
	 * @return
	 */
	public List getStaffId(String phone) {
		try {
			String sql = "SELECT SR.STAFF_ID STAFF_ID FROM T_SYM_STAFFROLE SR,T_SYM_ROLE R WHERE SR.STAFF_ID IN (SELECT STAFF_ID FROM T_SYM_STAFF S WHERE  MOBILE=?) AND  SR.ROLE_ID=R.ROLE_ID AND R.STAT='S0A' AND SR.ROLE_ID='224'";
			Object[] object = new Object[] { phone };
			List list = DAO.queryForList(sql, object);
			if (list.size() <=0) {
				return null;
			}
//			return (String) ((Map) list.get(0)).get("STAFF_ID");
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取企业客户
	 * @return
	 */
	public String getCustName(String staffId) {
		try {
			String sql = "SELECT c.cust_name cust_name FROM t_cum_info c WHERE c.PRTN_ID IN (SELECT PRTN_ID FROM T_CUM_PRIV WHERE ORG_ID IN (SELECT ORG_ID  FROM T_SYM_STAFF  WHERE STAFF_ID = ?) AND STAT = 'S0A') and c.STAT = 'S0A'";
			Object[] object = new Object[] { staffId };
			List list = DAO.queryForList(sql, object);
			if (list.size() > 0) {
				return (String) ((Map) list.get(0)).get("cust_name");
			}
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
