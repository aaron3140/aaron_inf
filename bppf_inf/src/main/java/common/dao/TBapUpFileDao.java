package common.dao;

import common.utils.SpringContextHelper;

public class TBapUpFileDao {

	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	
	public static String getPath(String fileID){
		StringBuffer sb = new StringBuffer();
		sb.append("select path from t_bap_upfile where file_id = ?");
		String path = (String) DAO.queryForObject(sb.toString(), new Object[] {fileID}, String.class);
		return path;
	}
}
