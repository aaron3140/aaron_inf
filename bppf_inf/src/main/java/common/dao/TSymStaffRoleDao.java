package common.dao;

import common.utils.SpringContextHelper;

public class TSymStaffRoleDao {

	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();

	/**
	 * 是否管理员
	 * 
	 * @param staffCode
	 * @return
	 * @throws Exception
	 */
	public boolean isAdminRole(String staffCode) throws Exception {

		String sql = "select t2.role_id from t_sym_staff t1,t_sym_staffrole t2 where t1.staff_id = t2.staff_id and t2.role_id = ? and staff_code=?";

		int i = DAO.queryForInt(sql, new Object[] { "224", staffCode });

		if (i > 0) {
			return true;
		}

		return false;
	}

	public boolean adminRole(String staffCode) throws Exception {

//		String sql = "select t2.role_id from t_sym_staff t1,t_sym_staffrole t2 where t1.staff_id = t2.staff_id  and staff_code=?";
		String sql = "select count(t2.role_id) from t_sym_staff t1,t_sym_staffrole t2 where t1.staff_id = t2.staff_id  and staff_code=? and t2.role_id='224'";

		Integer roleId = (Integer) DAO.queryForObject(sql,
				new Object[] { staffCode}, Integer.class);

		if (roleId.intValue()>0) {
			return true;
		}
		return false;
	}
	/**
	 * 判断staffCode是否属于roleIds
	 * @param roleIds
	 * @param staffCode
	 * @return
	 * @throws Exception
	 */
	public boolean adminRole(String roleIds,String staffCode) throws Exception {

//		String sql = "select count(s.staff_id) from t_sym_staff s,t_sym_staffrole t where s.staff_id=t.staff_id and s.staff_code=? and t.role_id in (select r.role_id from t_sym_role r where r.ROLE_SEQ='12')";

		String sql = "select count(s.staff_id) from t_sym_staff s,t_sym_staffrole t where s.staff_id=t.staff_id and s.staff_code=? and t.role_id in (371,372)";
		Integer roleId = (Integer) DAO.queryForObject(sql,
				new Object[] { staffCode}, Integer.class);

		if (roleId.intValue()>0) {
			return true;
		}
		return false;
	}
}
