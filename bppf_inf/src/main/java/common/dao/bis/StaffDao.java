package common.dao.bis;

import org.springframework.stereotype.Repository;

@Repository
public class StaffDao extends BisDao {

	public  String getIdByCode(String code){
		
		String sql = "select staff_id from t_sym_staff where staff_code=?";
		
		String id = (String)baseDao.queryForObject(sql, new Object[]{code}, String.class);
		  
		return id;  
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeaturedClass() {
		return getClass();
	}
}
