package common.dao;

import java.util.List;

import common.utils.SpringContextHelper;

public class TSymAreaNewDao {
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	/**
	 *判断是否为地区编码 
	 */
	public boolean isAreaCode(String areaCode,String areaType){
		boolean flag =false;
		String sql = "";
		List list = null;
		if(areaType.equalsIgnoreCase("02")){
		 sql ="select * from t_sym_area where area_code=?";
		 list = DAO.queryForList(sql,new Object[]{areaCode});
		}else{
			 sql ="select * from t_sym_area where area_code=? and area_type=?";
			 list = DAO.queryForList(sql,new Object[]{areaCode,areaType});
		}
		if(list!=null&&list.size()>0){
			flag = true;
		}
		return flag;
	}
	
}
