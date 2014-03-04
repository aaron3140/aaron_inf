package common.dao;

import java.util.List;

import common.utils.SpringContextHelper;

public class TSymPrivDao {
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	
	/**
	 * 查询用户所拥有的权限
	 */
	public List allFuncriv(String staffCode){
		String sql = "select p.* from t_sym_funcpriv fp,t_sym_priv p  " +
				"where fp.priv_id=p.priv_id and fp.func_id IN " +
				"(select a.attr_value from t_sym_staffattr a where a.staff_id= ? and a.attr_type='SA1001') " +
				"and oper_mode = 'INFCLN'";		
		List list = DAO.queryForList(sql,new Object[]{staffCode});
		return list;
	}
}
