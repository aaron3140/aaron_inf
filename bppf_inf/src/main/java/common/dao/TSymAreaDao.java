package common.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import common.utils.SpringContextHelper;

public class TSymAreaDao {
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	

	@SuppressWarnings("unchecked")
	public static List<String> getAreaCodeConnBy(String custCode) {
		String sql = "select area_code  from t_sym_area start with area_code = (select area_code from t_cum_info where cust_code = ? and stat = 'S0A') "
                                                                                                        + " connect by prior parent_area = area_code";
		List rows =  DAO.queryForList(sql,new Object[]{custCode});
		List<String> list = new ArrayList<String>();
		Iterator it = rows.iterator();
		while(it.hasNext()) {
		    Map map = (Map) it.next();
		    list.add((String) map.get("area_code"));
		}
		
		return list;
	}
	
	public static String getAreaNamebyAreaCode(String areaCode){
		String sql="select area_name from t_sym_area where area_code = ?";
		
		return (String) DAO.queryForObject(sql, new Object[]{areaCode}, String.class);
	}
	
}
