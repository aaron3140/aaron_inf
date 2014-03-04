package common.utils;

import common.dao.BaseDao;


public class DictTranslationUtils {
    
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	
    public static String dictTranslation(String dictId, String dictTypeId) throws Exception{
        
    	String sql;
		if (Charset.isEmpty(dictId) || Charset.isEmpty(dictTypeId)) {
			return "";
		} else {
			sql = "select dict_name from t_sym_dict where dict_id =? and dict_typeid = ? ";
			String r =  (String) DAO.queryForObject(sql, new Object[] {dictId, dictTypeId}, String.class);
			if (r == null) throw new Exception("字典编码" +dictTypeId + "和字典标识" + dictId + "对应的字典值不存在");
			return r;
		}
    }
    
   public static String dictReverseTranslation(String dictName, String dictTypeId) throws Exception{
        
    	String sql;
		if (Charset.isEmpty(dictName) || Charset.isEmpty(dictTypeId)) {
			return "";
		} else {
			sql = "select dict_id from t_sym_dict where dict_name = ? and dict_typeid = ? ";
			String r =  (String) DAO.queryForObject(sql, new Object[] {dictName, dictTypeId}, String.class);
			if (r == null) throw new Exception("字典编码" +dictTypeId + "和字典值" + dictName + "对应的字典标识不存在");
			return r;
		}
    }
}
