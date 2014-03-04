package common.dao;

import common.utils.SpringContextHelper;

public class TPnmAttrDao {

	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	
	public String getKeyByCustCode(String custCode){
		StringBuffer sb = new StringBuffer();
		sb.append("select a.value1 from t_pnm_attr a where 1 = 1 and a.attr_id = 2006 and a.prtn_id =(select p.parent_id")
			.append(" from t_cum_info t, t_pnm_partner p where t.cust_code = ? and t.prtn_id = p.prtn_id)");
		String keyValue = (String) DAO.queryForObject(sb.toString(), new Object[] {custCode}, String.class);
		return keyValue;
	}
}
