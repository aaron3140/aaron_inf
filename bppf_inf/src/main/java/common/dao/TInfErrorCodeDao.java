package common.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.util.logging.resources.logging;

import common.entity.TInfErrorCode;
import common.utils.Charset;
import common.utils.SpringContextHelper;

public class TInfErrorCodeDao {
	private static Log log = LogFactory.getLog(TInfErrorCodeDao.class);
	public static BaseDao DAO = SpringContextHelper.getInfBaseDaoBean();

	/**
	 * 获取错误码转义表数据
	 * 
	 * @param errorCode
	 * @param inlineCode
	 * @param errorInfo
	 * @param systemCode
	 * @param moduleCode
	 * @param stat
	 * @return
	 */
	public List getList(String errorCode, String inlineCode, String errorInfo, String systemCode, String moduleCode, String stat) {
		Boolean bError = false;
		String sql = "SELECT E.ERROR_CODE ERRORCODE,E.INLINE_CODE INLINECODE,E.ERROR_INFO ERRORINFO,E.SYSTEM_CODE SYSTEMCODE,E.MODULE_CODE MODULECODE,E.STAT STAT FROM T_INF_ERRORCODE E";
		if (!Charset.isEmpty(errorCode)) {
			sql = sql + " WHERE E.ERROR_CODE = '" + errorCode + "'";
			bError = true;
		}
		if (!Charset.isEmpty(inlineCode)) {
			if (bError) {
				sql = sql + " AND E.INLINE_CODE = '" + inlineCode + "'";
			} else {
				sql = sql + " WHERE E.INLINE_CODE = '" + inlineCode + "'";
				bError = true;
			}
		}
		if (!Charset.isEmpty(errorInfo)) {
			if (bError) {
				sql = sql + " AND E.ERROR_INFO LIKE '%" + errorInfo + "%'";
			} else {
				sql = sql + " WHERE E.ERROR_INFO LIKE '%" + errorInfo + "%'";
				bError = true;
			}
		}
		if (!Charset.isEmpty(systemCode)) {
			if (bError) {
				sql = sql + " AND E.SYSTEM_CODE = '" + systemCode + "'";
			} else {
				sql = sql + " WHERE E.SYSTEM_CODE = '" + systemCode + "'";
				bError = true;
			}
		}
		if (!Charset.isEmpty(moduleCode)) {
			if (bError) {
				sql = sql + " AND E.MODULE_CODE =  '" + moduleCode + "'";
			} else {
				sql = sql + " WHERE E.MODULE_CODE =  '" + moduleCode + "'";
				bError = true;
			}
		}
		if (!Charset.isEmpty(stat)) {
			if (bError) {
				sql = sql + " AND E.STAT = '" + stat + "'";
			} else {
				sql = sql + " WHERE E.STAT = '" + stat + "'";
				bError = true;
			}
		}
		return DAO.queryForList(sql);
	}

	/**
	 * 获取错误码转义表数据
	 * 
	 * @param posseqnoConflictCode
	 * @param posseqnoConflictReason
	 * @return
	 */
	public String getErrorInfo(String posseqnoConflictCode, String posseqnoConflictReason) {
		String sql = "SELECT E.ERROR_INFO ERRORINFO FROM T_INF_ERRORCODE E WHERE E.ERROR_CODE = ? and E.STAT = ?";
		Object[] object = new Object[] { posseqnoConflictCode, "S0A" };
		List list = DAO.queryForList(sql, object);
		if (list.size() > 0) {
			return (String) ((Map) list.get(0)).get("ERRORINFO");
		}
		return posseqnoConflictReason;
	}

	/**
	 * 获取错误码转义表数据
	 * 
	 * @param posseqnoConflictCode
	 * @param posseqnoConflictReason
	 * @return
	 */
	public HashMap<String, TInfErrorCode> getErrorMap() {
		HashMap<String, TInfErrorCode> map = new HashMap<String, TInfErrorCode>();
		String sql = "SELECT E.ERROR_CODE ERRORCODE,E.INLINE_CODE INLINECODE,E.ERROR_INFO ERRORINFO FROM T_INF_ERRORCODE E WHERE E.STAT = ?";
		Object[] object = new Object[] { "S0A" };
		try{
		List list = DAO.queryForList(sql, object);
		for (int i = 0; i < list.size(); i++) {
			TInfErrorCode errorCode = new TInfErrorCode();
			if (((Map) list.get(i)).get("ERRORCODE") != null) {
				errorCode.setErrorCode((String) ((Map) list.get(i)).get("ERRORCODE"));
			}
			if (((Map) list.get(i)).get("ERRORINFO") != null) {
				errorCode.setErrorInfo((String) ((Map) list.get(i)).get("ERRORINFO"));
			}
			if (((Map) list.get(i)).get("INLINECODE") != null) {
				errorCode.setInlineCode((String) ((Map) list.get(i)).get("INLINECODE"));
			}
			map.put(errorCode.getInlineCode(), errorCode);
		}}catch(Exception ex){
		    log.info("错误码处理异常getErrorMap:"+ex.getMessage());
		    ex.printStackTrace();
		}
		return map;
	}

}
