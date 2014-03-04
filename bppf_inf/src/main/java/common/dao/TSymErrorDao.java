package common.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import common.entity.SymError;
import common.utils.SpringContextHelper;

public class TSymErrorDao {
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	
	public static SymError getErrorOut(String errorCode, String moduleCode) {
		String sql = " select CODE_OUT, INFO_OUT from T_SYM_ERROR "
						+ " where MODULE_CODE = ? and "
						+ " ERROR_CODE = ? and stat = 'S0A' ";
		
		List rows =  DAO.queryForList(sql,new Object[]{moduleCode,errorCode});
		Iterator it = rows.iterator();
		while(it.hasNext()) {
			Map map = (Map) it.next();
		    return new SymError((String) map.get("CODE_OUT"), (String) map.get("INFO_OUT"));
		}
		
		return null;
	}

	
}
