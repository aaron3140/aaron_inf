package common.dao;

import common.utils.SpringContextHelper;

public class TSymStaffLogDao {

	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	
	public static String getMobile(String staff)throws Exception{
		
		String sql = "select mobile from t_sym_staff where staff_code=?";
		
		String mobile = (String)DAO.queryForObject(sql, new Object[]{staff}, String.class);
		  
		return mobile;  
	}
	
	/**
	 * 查询证件类型
	 * @param staffCode
	 * @author lichunan
	 * @return
	 */
	public static String getCertType(String staffCode){

		String sql = "select cert_type from t_sym_staff where staff_code=? and stat = 'S0A'";
		
		String certType = (String)DAO.queryForObject(sql, new Object[]{staffCode}, String.class);
		  
		return certType; 
	}
	
	/**
	 * 查询email地址
	 * @param staffCode
	 * @author lichunan
	 * @return
	 */
	public static String getEmail(String staffCode){
		String sql = "select email from t_sym_staff where staff_code=? and stat = 'S0A'";
		
		String email = (String)DAO.queryForObject(sql, new Object[]{staffCode}, String.class);
		  
		return email;
	}
	
	/**
	 * 查询固定电话
	 * @param staffCode
	 * @author lichunan
	 * @return
	 */
	public static String getPhone(String staffCode){
		String sql = "select phone from t_sym_staff where staff_code=? and stat = 'S0A'";
		
		String phone = (String)DAO.queryForObject(sql, new Object[]{staffCode}, String.class);
		  
		return phone;
	}
	
	public static boolean vertifyStaff(String staff)throws Exception{
		
		String sql = "select count(STAFF_ID) from t_sym_staff where staff_code=? and pwd_stat='S0A' and stat='S0A'";
		
		int count = (Integer)DAO.queryForInt(sql, new Object[]{staff});
		
		if(count>0){
			
			return true;
		}else{
			
			return false;
		}
		  
	}
}
