package common.dao.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import common.dao.BaseDao;
import common.entity.register.TSymStaff;
import common.utils.CreateConstant;

public class BaseDaoImpl implements BaseDao {

	private static final Log logger = LogFactory.getLog(BaseDaoImpl.class);

	private JdbcTemplate jdbcTemplate;

	private boolean showSql = true;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public boolean isShowSql() {
		return showSql;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	/**
	 * @description 写log
	 * @param message
	 */
	public void writeLog(String message) {
		if (showSql || logger.isInfoEnabled())
			logger.info(message);
	}

	/**
	 * @description 调用JDBCTEMPLATE进行插入
	 * @param sql
	 * @return 返回插入的行数
	 */
	public int insert(String sql) {
		writeLog(sql);
		return getJdbcTemplate().update(sql);
	}

	public List queryForList(String sql) {
		writeLog(sql);
		return getJdbcTemplate().queryForList(sql);
	}

	public int insert(String sql, Object[] values) {
		writeLog(sql);
		return getJdbcTemplate().update(sql, values);
	}

	/**
	 * @param sql
	 * @param values
	 * @param types
	 * @return 返回插入的行数
	 */
	public int insert(String sql, Object[] values, int[] types) {
		writeLog(sql);
		return getJdbcTemplate().update(sql, values, types);
	}

	/**
	 * @description 调用jdbcTemplate进行插入
	 * @param sql
	 * @return 返回更新的行数
	 */
	public int update(String sql) {
		writeLog(sql);
		return getJdbcTemplate().update(sql);
	}

	public int update(String sql, Object[] parms) {
		writeLog(sql);
		return getJdbcTemplate().update(sql, parms);
	}

	/**
	 * @param sql
	 * @param values
	 * @param types
	 * @return 返回更新的行数
	 */
	public int update(String sql, Object[] parms, int[] types) {
		writeLog(sql);
		return getJdbcTemplate().update(sql, parms, types);
	}

	/**
	 * @param sql
	 * @param values
	 * @param types
	 * @return List
	 */
	public List queryForList(String sql, Object[] values, int[] types) {
		writeLog(sql);
		return getJdbcTemplate().queryForList(sql, values, types);
	}

	public List queryForList(String sql, Class elementType) {
		writeLog(sql);
		return getJdbcTemplate().queryForList(sql, elementType);
	}

	public List queryForList(String sql, Object[] args, Class elementType) {
		writeLog(sql);
		return getJdbcTemplate().queryForList(sql, args, elementType);
	}

	public List queryForList(String sql, Object[] values) {
		writeLog(sql);
		return getJdbcTemplate().queryForList(sql, values);
	}

	public List query(String sql, Object[] args, RowMapper rowMapper) {
		writeLog(sql);
		return getJdbcTemplate().query(sql, args, rowMapper);
	}

	public List query(String sql, RowMapper rowMapper) {
		writeLog(sql);
		return getJdbcTemplate().query(sql, rowMapper);
	}

	/**
	 * @description SQL查询,返回MAP
	 * @param sql
	 * @return Map
	 */
	public Map queryForMap(String sql) {
		writeLog(sql);
		return getJdbcTemplate().queryForMap(sql);
	}

	/**
	 * @description SQL查询,返回MAP
	 * @param sql
	 * @return Map
	 */
	public Map queryForMap(String sql, Object[] values, int[] types) {
		writeLog(sql);
		return getJdbcTemplate().queryForMap(sql, values, types);
	}

	public Map queryForMap(String sql, Object[] values) {
		writeLog(sql);
		return getJdbcTemplate().queryForMap(sql, values);
	}

	/**
	 * @description SQL查询,返回总行数
	 * @param sql
	 * @return int
	 */
	public int queryForInt(String sql) {
		writeLog(sql);
		return getJdbcTemplate().queryForInt(sql);
	}

	public int queryForInt(String sql, Object[] values) {
		writeLog(sql);
		return getJdbcTemplate().queryForInt(sql, values);
	}

	/**
	 * @description SQL查询,返回总行数
	 * @param sql
	 * @return int
	 */
	public int queryForInt(String sql, Object[] values, int[] types) {
		writeLog(sql);
		return getJdbcTemplate().queryForInt(sql, values, types);
	}

	public long queryForLong(String sql, Object[] values) {
		writeLog(sql);
		return getJdbcTemplate().queryForLong(sql, values);
	}

	/**
	 * @description SQL查询,返回总行数
	 * @param sql
	 * @return int
	 */
	public long queryForLong(String sql, Object[] values, int[] types) {
		writeLog(sql);
		return getJdbcTemplate().queryForLong(sql, values, types);
	}

	/**
	 * SQL查询，返回c类型的值
	 * 
	 * @param sql
	 * @param c
	 * @return
	 */
	public Object queryForObject(String sql, Class c) {
		writeLog(sql);
		try {
			return getJdbcTemplate().queryForObject(sql, c);
		} catch (EmptyResultDataAccessException e) {// queryForObject无记录时，抛异常EmptyResultDataAccessException
			return null;
		}
	}

	public Object queryForObject(String sql, Object[] args, Class requiredType) {
		writeLog(sql);
		try {
			return getJdbcTemplate().queryForObject(sql, args, requiredType);
		} catch (EmptyResultDataAccessException e) {// queryForObject无记录时，抛异常EmptyResultDataAccessException
			return null;
		}
	}

	/**
	 * @description SQL查询,返回总行数,类型为Long
	 * @param sql
	 * @return long
	 */
	public long queryForLong(String sql) {
		writeLog(sql);
		return getJdbcTemplate().queryForLong(sql);
	}

	/**
	 * @description SQL oracle 分页查询
	 * @param sql
	 * @return
	 */
	public List queryForPage(String sql, long starrow, long endrow) {

		sql = getPageSql(sql, starrow, endrow);
		writeLog(sql);
		return getJdbcTemplate().queryForList(sql);
	}
	
	public List queryForPage(String sql,Object[] args, long index, long page) {
		
		sql = getPageSql(sql, index, page);
		writeLog(sql);
		return getJdbcTemplate().queryForList(sql,args);
	}

	public String getPageSql(String sql, long index, long page) {
		StringBuffer strBuffer = new StringBuffer();

		strBuffer
				.append("SELECT * FROM ( SELECT row_.*, rownum rownum_ FROM (");
		strBuffer.append(sql);
		strBuffer.append(") row_ WHERE rownum <= ");
		strBuffer.append(index*page);
		strBuffer.append(") WHERE rownum_ >=");
		strBuffer.append((index-1)*page+1);

		return strBuffer.toString();
	}

	/**
	 * 获取序列值
	 * 
	 * @author ajm
	 * @param seqName
	 *            序列名
	 * @return
	 */
	public long getLongPrimaryKey(String seqName) {
		String sql = "select " + seqName + ".Nextval from dual";
		writeLog(sql);
		return getJdbcTemplate().queryForLong(sql);
	}

	/**
	 * @param seqName
	 *            序列名
	 * @return
	 */
	public int getIntPrimaryKey(String seqName) {
		String sql = "select " + seqName + ".Nextval from dual";
		writeLog(sql);
		return getJdbcTemplate().queryForInt(sql);
	}

	/**
	 * 
	 * @param sql
	 * @param setter
	 * @return 批量操作影响行数
	 */
	public int batchUpdate(String sql, BatchPreparedStatementSetter setter) {
		writeLog(sql);
		int[] batchResult = getJdbcTemplate().batchUpdate(sql, setter);
		int rowsAffected = 0;
		for (int i = 0; i < batchResult.length; i++) {
			if (batchResult[i] > 0
					|| batchResult[i] == PreparedStatement.SUCCESS_NO_INFO)
				rowsAffected += 1;
		}

		return rowsAffected;
	}

	public String getBlob(final String sql, final String blobField,
			final String idVal) {
		if (StringUtils.isEmpty(sql) || StringUtils.isEmpty(blobField)
				|| StringUtils.isEmpty(idVal) || StringUtils.isEmpty(idVal))
			return null;

		ConnectionCallback queryCallback = new ConnectionCallback() {
			public Object doInConnection(Connection con) throws SQLException,
					DataAccessException {
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setString(1, idVal);
				ResultSet rset = pstmt.executeQuery();
				StringBuffer content = new StringBuffer();
				if (rset.next()) {
					Clob contentClob = (Clob) rset.getClob(blobField);
					if (contentClob != null) {
						Reader r = contentClob.getCharacterStream();
						BufferedReader br = new BufferedReader(r);
						try {
							String line = br.readLine();
							while (line != null) {
								content.append(line);
								line = br.readLine();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return content;
			}

		};
		return ((StringBuffer) getJdbcTemplate().execute(queryCallback))
				.toString();
	}

	public Object queryForObject(String sql, Object[] args, RowMapper rowMapper) {

		writeLog(sql);
		try {
			return getJdbcTemplate().queryForObject(sql, args, rowMapper);

		} catch (EmptyResultDataAccessException e) {// queryForObject无记录时，抛异常EmptyResultDataAccessException
			return null;
		}
	}

	@Override
	public JdbcTemplate getTemplate() {

		return jdbcTemplate;
	}

	public void tran_SaveSplit(Hashtable<String,Object> param) throws Exception {

		JdbcTemplate t = this.getJdbcTemplate();
		
		long planId = queryForLong("SELECT  SQ_PDM_SUBLEDGER.NEXTVAL FROM DUAL");
		
		savePlan(planId,t,param);
		
		String[] pcts = (String[])param.get("PLANCUSTCODE");
		
		String[] pvs = (String[])param.get("PLANVALUE");
		
		String planType = (String)param.get("PLANTYPE");
		
		for(int i=0;i<pcts.length;i++){
			
			saveDetail(t,planId,pcts[i],pvs[i],planType);
		}
	
	}
	
	private void savePlan(long planId,JdbcTemplate t,Hashtable<String,Object> param){
		
		StringBuffer sb = new StringBuffer("INSERT INTO T_PDM_SUBLEDGER ");
		sb.append("(PLAN_ID, PLAN_CODE, PLAN_NAME, PLAN_DESC, PLAN_TYPE, CUSTCODE, PLAN_DATE,EFF_DATE,STAT)")
				.append("VALUES (?,?,?,?,?,?,sysdate,sysdate,?)");
		t.update(sb.toString(), new Object[] {planId,param.get("PLANCODE"), param.get("PLANNAME"), param.get("PLANDESC"), param.get("PLANTYPE"),
			param.get("CUSTCODE"),"S0A" });
	}
	
	private void saveDetail(JdbcTemplate t,long planId,String planCust,String planValue,String planType){
		
		StringBuffer sb = new StringBuffer("INSERT INTO T_PDM_SUBLEDGER_DETAIL ");
		sb.append("(DETAIL_ID, PLAN_ID, PLAN_OBJECT, PLAN_TYPE, PLAN_VALUE, STAT)")
				.append("VALUES (SQ_PDM_SUBLEDGER_SQ.NEXTVAL,?,?,?,?,?)");
		t.update(sb.toString(), new Object[] {planId, planCust, planType, planValue,"S0A" });
	}
	
	
	public void tran_AaveAttr(String custId, TSymStaff staff,
			List<String> roles, List<String> func, List<String> funcId,
			List<Object[]> parm) throws Exception {

		JdbcTemplate t = this.getJdbcTemplate();

		// 保存管理员
		addStaff(t, staff);

		// 保存属性
		for (String ro : roles) {

			addRole(t, staff.getStaffId(), ro);
		}

		// 保存其他信息
		batchInsertRows(t, parm);

		// 企业账户授权
		for (String attr : func) {

			addfuncp(t, custId, attr);
		}

		// 审核
		addProv(custId);
		// 工号授权
		for (String fid : funcId) {

			addStaffAttr(t, staff.getStaffId(),
					CreateConstant.STAFF_ATTR_TYPE_SA1001, fid);
		}

		//支付密码信息
		addStaffAttr(t, staff.getStaffId(),
				CreateConstant.STAFF_ATTR_TYPE_SA0100, staff.getPayPwd());
		
		// 更新客户状态
		updateCus(custId);
	}

	// 更新客户状态
	public void updateCus(String custId) throws Exception {

		String sql = "UPDATE T_CUM_INFO SET OPER_FLAG=? WHERE  CUST_ID=?";

		getJdbcTemplate().update(sql, new Object[] { "S0A", custId });
	}

	public Map getSeq(String seqType, String seqId) {

		String sql = "select SEQ_TYPE,SEQ_ID,NEXTVAL,LENGTH,MAX_VAL,IS_CYCLE from T_SYM_SEQ_EXT where SEQ_TYPE=? and SEQ_ID=? ";
		writeLog(sql);
		return getJdbcTemplate().queryForMap(sql,
				new Object[] { seqType, seqId });
	}

	/*
	 * 
	 */
	public void builPartner(String parentId, String orgId) {

		String sql = "INSERT INTO T_CUM_PRIV (CUST_SEQ,PRTN_ID,ORG_ID,STAT)"
				+ " VALUES(SQ_CUM_PRIV.NEXTVAL,?,?,?)";

		getJdbcTemplate().update(sql, new Object[] { parentId, orgId, "S0A" });
	}

	/*
	 * 插入Partner对象到数据库，已提交
	 */
	public void addProv(String custId) throws Exception {

		String sql = "INSERT INTO T_CUM_OBJ_APPROVE (APPR_ID,OBJ_ID,APPR_TYPE,APPLY_DATE,APPLY_STAFF,APPR_DATE,APPR_STAFF,NEXT_STAT,LASTAPPR_STAT)"
				+ " VALUES(SQ_CUM_OBJAPPROVE.NEXTVAL,?,?,sysdate,?,sysdate,?,?,?)";

		getJdbcTemplate().update(
				sql,
				new Object[] { custId, CreateConstant.APPR_TYPE_ENTER, "admin",
						"admin", "ADD", "S0A" });
	}

	/*
	 * 插入Partner对象到数据库，已提交
	 */
	public void addPartner(String id, String prtnName, String ptrnCode,
			String regType, String areaCode, String parentId) {

		String sql = "INSERT INTO T_Pnm_Partner (PRTN_ID,PRTN_CODE,PRTN_NAME,PRTN_TYPE,AREA_CODE,PARENT_ID,REG_TYPE,STAT)"
				+ " VALUES(?,?,?,?,?,?,?,?)";

		getJdbcTemplate().update(
				sql,
				new Object[] { id, ptrnCode, prtnName, "PT401", areaCode,
						parentId, regType, "S0A" });
	}

	/*
	 * 插入Org对象到数据库，已提交
	 */
	public void addOrg(String id, String orgCode, String orgName,
			String areaCode, String parentOrgId) {

		String sql = "INSERT INTO T_SYM_ORG (ORG_ID,ORG_CODE,ORG_NAME,PARENT_ORGID,ORG_TYPE,ORG_AREA,STAT)"
				+ " VALUES(?,?,?,?,?,?,?)";

		getJdbcTemplate().update(
				sql,
				new Object[] { id, orgCode, orgName, parentOrgId,
						CreateConstant.ORG_TYPE_OT003, areaCode, "S0A" });
	}

	public void addStaff(JdbcTemplate t, TSymStaff staff) throws Exception {

		String sql = "INSERT INTO T_SYM_STAFF (STAFF_ID,STAFF_CODE,STAFF_NAME,ORG_ID,SEX,PASSWORD,PWD_STAT,PWD_CTRL,PWD_ERRTIMES,PWD_TRYTIMES,STAT,EXT_FLAG,MOBILE,EMAIL,CERT_TYPE,CERT_NBR)"
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		t.update(sql, new Object[] { staff.getStaffId(), staff.getStaffCode(),
				staff.getStaffName(), staff.getOrgId(), staff.getSex(),
				staff.getPassword(), staff.getPwdStat(), staff.getPwdCtrl(),
				staff.getPwdErrtimes(), staff.getPwdTrytimes(),
				staff.getStat(), staff.getExtFlag(),staff.getMobile(),staff.getEmail(),staff.getCertType(),staff.getCertNbr() });

	}

	public void addRole(JdbcTemplate t, String staffId, String roleId)
			throws Exception {

		String sql = "INSERT INTO T_SYM_STAFFROLE (STAFF_ID,ROLE_ID) VALUES(?,?)";

		t.update(sql, new Object[] { staffId, roleId });

	}

	public void batchInsertRows(JdbcTemplate t, final List<Object[]> dataSet)
			throws Exception {

		String sql = "INSERT INTO T_CUM_ATTR (CUST_ID,ATTR_ID,VALUE1,STAT) VALUES(?,?,?,?)";

		BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {

			public int getBatchSize() {
				return dataSet.size();
			}

			public void setValues(PreparedStatement ps, int i) {

				Object[] obj = dataSet.get(i);

				try {
					ps.setLong(1, Integer.parseInt(obj[0].toString()));
					ps.setLong(2, Integer.parseInt(obj[1].toString()));
					ps.setString(3, obj[2].toString());
					ps.setString(4, "S0A");
				} catch (Exception e) {

					e.printStackTrace();

				}
			}

		};
		t.batchUpdate(sql, setter);
	}

	public void addfuncp(JdbcTemplate t, String custId, String attrId)
			throws Exception {

		String sql = "INSERT INTO T_CUM_ATTR (CUST_ID,ATTR_ID,VALUE1,STAT)"
				+ " VALUES(?,?,?,?)";

		t.update(sql, new Object[] { custId, attrId, "true", "S0A" });

	}

	public void addStaffAttr(JdbcTemplate t, String staffId, String attrType,
			String attrValue) {

		String sql = "INSERT INTO T_SYM_STAFFATTR (STAFF_ID,ATTR_ID,ATTR_TYPE,ATTR_VALUE,STAT)"
				+ " VALUES(?,SQ_SYM_STAFFATTR.NEXTVAL,?,?,?)";

		t.update(sql, new Object[] { staffId, attrType, attrValue, "S0A" });

	}

	@Override
	public void updateSeq(String seqType, String seqId, int nextVal) {

		String sql = "UPDATE T_SYM_SEQ_EXT SET NEXTVAL =? where SEQ_TYPE=? and SEQ_ID=?";

		getJdbcTemplate().update(sql, new Object[] { nextVal, seqType, seqId });

	}

}
