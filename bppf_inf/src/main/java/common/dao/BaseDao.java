package common.dao;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import common.entity.register.TSymStaff;

public interface BaseDao {

	public Map getSeq(String seqType, String seqId);

	public void updateSeq(String seqType, String seqId, int nextVal);

	public void tran_SaveSplit(Hashtable<String,Object> param) throws Exception;
	
	public void tran_AaveAttr(String custId, TSymStaff staff,
			List<String> roles, List<String> func, List<String> funcId,
			List<Object[]> parm) throws Exception;

	public void addPartner(String id, String prtnName, String ptrnCode,
			String regType, String areaCode, String parentId);

	public void builPartner(String parentId, String orgId);

	public void addOrg(String id, String orgCode, String orgName,
			String areaCode, String parentOrgId);

	public JdbcTemplate getTemplate();

	public void writeLog(String message);

	public int insert(String sql);

	public int insert(String sql, Object[] values);

	public int insert(String sql, Object[] values, int[] types);

	public int update(String sql);

	public int update(String sql, Object[] parms);

	public int update(String sql, Object[] parms, int[] types);

	public List query(String sql, RowMapper rowMapper);

	public List query(String sql, Object[] args, RowMapper rowMapper);

	public Map queryForMap(String sql);

	public Map queryForMap(String sql, Object[] values);

	public Map queryForMap(String sql, Object[] values, int[] types);

	public Object queryForObject(String sql, Class c);

	public Object queryForObject(String sql, Object[] args, Class requiredType);

	public Object queryForObject(String sql, Object[] args, RowMapper rowMapper);

	public int queryForInt(String sql);

	public int queryForInt(String sql, Object[] values);

	public int queryForInt(String sql, Object[] values, int[] types);

	public long queryForLong(String sql);

	public long queryForLong(String sql, Object[] values);

	public long queryForLong(String sql, Object[] values, int[] types);

	public List queryForList(String sql);

	public List queryForList(String sql, Class elementType);

	public List queryForList(String sql, Object[] args, Class elementType);

	public List queryForList(String sql, Object[] values);

	public List queryForList(String sql, Object[] values, int[] types);

	public List queryForPage(String sql,Object[] args, long index, long page);
	
	public List queryForPage(String sql, long starrow, long endrow);

	public String getPageSql(String sql, long index, long page);

	public long getLongPrimaryKey(String seqName);

	public int getIntPrimaryKey(String seqName);

	public int batchUpdate(String sql, BatchPreparedStatementSetter setter);

	public String getBlob(final String sql, final String blobField,
			final String idVal);

}