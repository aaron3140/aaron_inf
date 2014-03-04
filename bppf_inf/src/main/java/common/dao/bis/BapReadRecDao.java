package common.dao.bis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

/**
 * 消息管理 消息管理分为消息和公告两大类。 公告为重大事件的正式公开，接收对象一般为所有人或某类群体，所以公告不能推送给单个商户；
 * 消息为一般事件的告知，接收对象一般为某类群体或个体。所以消息不能推送给所有商户。
 * 消息和公告每次阅读都要在BPPF_BIS.T_BAP_READREC增加一条阅读记录。
 * 消息需要对当前登录人员支持删除功能，新增记录时需要在BPPF_BIS
 * .T_BAP_STAFFREC插入一条未读记录，删除时把状态修改即可（门户网站/手机客户端实现）。
 * 消息发布渠道种类分为门户网站/手机客户端/短信三种类型，短信类型需要给相关接收人员下发短信通知，另外两种各自查库读取展示。
 * 消息范围种类分为按地区发布/按产品发布/按机构发布/按商户发布，对应接收单位为地区/产品线(支持多选)/机构(支持多选)/商户(支持多选)。
 * 
 * @author aaronMing
 * 
 */
 @Repository
public class BapReadRecDao extends BisDao {

	public static String ISSUE_MSG = "02";

	public static String ISSUE_NOTICE = "01";

	/**
	 * @author aaronMing
	 * @param readRec
	 * @return
	 * @throws DataAccessException
	 */
	public boolean insertReadRec(Map<String, Object> readRec)
			throws DataAccessException {
		String sql = "insert into t_BAP_READREC (READREC_ID, READ_TYPE, STAFF_ID, ORG_ID, READ_DATE,SOURCE_ID) "
				+ "  select SQ_BAP_READRECID.NEXTVAL,?, st.staff_id,st.org_id,sysdate,? from T_SYM_STAFF st where st.staff_Id = ? ";
		Object readType = readRec.get("readType");
		Object issueId = readRec.get("issueId");
		Object staffId = readRec.get("staffId");
		Object[] parms = { readType, issueId, staffId };
		// 已读记录插入
		baseDao.insert(sql, parms);

		String sqlupdate = " update  t_bap_staffrec br set br.READ_DATE=sysdate,br.stat='S0R' where br.issue_id=? and br.staff_id=? ";
		Object[] issueIdR = new Object[] { issueId, staffId };

		int has = baseDao.update(sqlupdate, issueIdR);
		if (has == 0) {
			String sqlInsert = " insert into t_bap_staffrec(issue_id,staff_id,read_date,stat) values(?,?,sysdate,'S0R' ) ";
			baseDao.insert(sqlInsert, new Object[] { issueId, staffId });
		}
		String sqlu = " update  t_bap_issue set READ_TIMES=(READ_TIMES+1) where issue_id=?";
		baseDao.update(sqlu, new Object[] { issueId });
		if (log.isInfoEnabled()) {
			log.info("消息已阅");
		}
		return true;
	}

	public int countStaffRecByIssueId(String issueId) {
		String sql = "select count(1) from t_bap_issue bi where  stat = 'S0A' and ISSUE_ID=?";
		log.info("查询消息issue_id:" + issueId + ",sql:" + sql);
		int count = baseDao.queryForInt(sql, new Object[] { issueId });
		if (log.isInfoEnabled()) {
			log.info("查询消息issue_id:" + issueId + ",存在条数:" + count);
		}
		return count;
	}

	public boolean delReadRec(String issueId, String staffId)
			throws DataAccessException {
		String sql = "update T_BAP_STAFFREC bsf set stat = 'SOX' WHERE ISSUE_ID=? AND  staff_Id=? ";
		baseDao.update(sql, new Object[] { issueId, staffId });
		if (log.isInfoEnabled()) {
			log.info("消息已删 issue_id:" + issueId + ",staffId:" + staffId);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, String>> selectReadReces(Map<String, String> readRec) {
		List<String> listargs = new ArrayList<String>();
		StringBuffer sql = issueNoticeSqlHandle(readRec, listargs);
		return baseDao.queryForList(sql.toString(), listargs.toArray());
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, String>> selectIssueForPage(
			Map<String, String> readRec, Long start, Long page) {
		List<String> listargs = new ArrayList<String>();
		StringBuffer sql = null;
		if (page <= 1)
			page = 10l;

		String issueType = readRec.get("issueType");
		// 查询消息或者公告
		if (issueType.equals(ISSUE_NOTICE)) {
			sql = issueNoticeSqlHandle(readRec, listargs);
		} else if (issueType.equals(ISSUE_MSG)) {
			sql = issueMsgSqlHandle(readRec, listargs);
		}
		sql.append(" order by bi.ISSUE_DATE desc ");
		return baseDao.queryForPage(sql.toString(), listargs.toArray(), start,
				page);
	}

	/**
	 * 消息查询条件拼凑
	 * 
	 * @param readRec
	 * @param listargs
	 * @return
	 */
	private StringBuffer issueMsgSqlHandle(Map<String, String> readRec,
			List<String> listargs) {
		StringBuffer sql = new StringBuffer();
		sql.append(
				" select bi.issue_id ,bi.issue_type,bi.issue_name,bi.issue_date,bi.range,bi.channel, bi.read_times,")
				.append("  decode(br.stat, 'S0R', 1, 0) msgstat,st.staff_name ")

				.append(" from  t_bap_issue bi, T_SYM_STAFF st, t_bap_staffrec br ")

				.append(" where   bi.stat='S0A' and bi.staff_id = st.staff_id    ")
				.append("and bi.issue_id = br.issue_id  and br.stat in ('S0R', 'S0A') ");

		String staffId = readRec.get("staffId");
		if (staffId != null && StringUtils.isNotEmpty(staffId)) {
			sql.append(" and br.staff_id=? ");
			listargs.add(staffId);
		}

		issueCondition(readRec, listargs, sql);
		return sql;
	}

	/**
	 * 公告查询条件拼凑
	 * 
	 * @param readRec
	 * @param listargs
	 * @return
	 */
	private StringBuffer issueNoticeSqlHandle(Map<String, String> readRec,
			List<String> listargs) {
		StringBuffer sql = new StringBuffer();
		 listargs.add(readRec.get("staffId"));
		sql.append(
				" select bi.issue_id ,bi.issue_type,bi.issue_name,bi.issue_date,bi.range,bi.channel, bi.read_times,")
				 .append(" (select count(1)  from t_bap_staffrec brc  where brc.issue_id = bi.issue_id and brc.staff_id=? and brc.stat='S0R' ) msgstat,")
				.append(" st.staff_name from  t_bap_issue bi, T_SYM_STAFF st  ")
				.append(" where   bi.stat='S0A' and bi.staff_id = st.staff_id(+)  ");

		issueCondition(readRec, listargs, sql);
		return sql;
	}

	/**
	 * 公告消息公共查询条件
	 * 
	 * @param readRec
	 * @param listargs
	 * @param sql
	 */
	private void issueCondition(Map<String, String> readRec,
			List<String> listargs, StringBuffer sql) {
		String issueType = readRec.get("issueType");
		if (issueType != null && StringUtils.isNotEmpty(issueType)) {
			sql.append(" and bi.issue_type=? ");
			listargs.add(issueType);
		}

		String issueChannels = readRec.get("issueChannel");
		if (issueChannels != null && StringUtils.isNotEmpty(issueChannels)) {
			// String[] issueChannel = issueChannels.split(",");
			sql.append(" and  bi.channel like '%" + issueChannels + "%' ");
			// for(String channel:issueChannel)
			// sql.append(" and  bi.channel like '" + channel + "%' ");
		}

		String issueScope = readRec.get("issueScope");
		if (issueScope != null && StringUtils.isNotEmpty(issueScope)) {
			sql.append(" and bi.range=? ");
			listargs.add(issueScope);
		}

		// boolean hasDate = false;
		String issueDateStart = readRec.get("issueDateStart");
		if (issueDateStart != null && StringUtils.isNotEmpty(issueDateStart)) {
			sql.append("and bi.ISSUE_DATE >= to_date(?, 'yyyy-mm-dd hh24:mi:ss')   ");
			listargs.add(issueDateStart);
			// hasDate = true;
		}

		String issueDateEnd = readRec.get("issueDateEnd");
		if (issueDateEnd != null && StringUtils.isNotEmpty(issueDateEnd)) {
			sql.append(" and  bi.ISSUE_DATE <= to_date(?, 'yyyy-mm-dd hh24:mi:ss') ");
			listargs.add(issueDateEnd);
			// hasDate = true;
		}
		// if (hasDate)
		// sql.append(" order by  bi.ISSUE_DATE desc ");
	}

	@SuppressWarnings("rawtypes") 
	public String selectIssueContent(String issueId) {
		String sql = "select bi.CONTENTS from t_bap_issue bi where bi.issue_id=? ";
		Map queryForMap = baseDao.queryForMap(sql, new Object[] { issueId });
		if (queryForMap != null && queryForMap.get("CONTENTS") != null) {
			return queryForMap.get("CONTENTS").toString();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeaturedClass() {
		return this.getClass();
	}

	public int countNoReadMsgId(Map<String, String> readRec) {
		return countIssueId(readRec, true);
	}

	public int countIssueId(Map<String, String> readRec) {
		return countIssueId(readRec, false);
	}

	public int countIssueId(Map<String, String> readRec, boolean noRead) {
		int count = 0;
		List<String> listargs = new ArrayList<String>();

		String issueType = readRec.get("issueType");
		if (ISSUE_MSG.equals(issueType)) {
			StringBuffer sql = new StringBuffer();

			sql.append("select count(1) from t_bap_issue bi, t_bap_staffrec br where "
					+ "   bi.stat='S0A' and bi.issue_id = br.issue_id and br.staff_id=? and  br.stat in ('S0R', 'S0A')  ");
			listargs.add(readRec.get("staffId"));
			issueCondition(readRec, listargs, sql);
			if (noRead) {
				sql.append(" and br.stat='S0A' ");
				count = baseDao.queryForInt(sql.toString(), listargs.toArray());
			} else
				count = baseDao.queryForInt(sql.toString(), listargs.toArray());
		} else if (ISSUE_NOTICE.equals(issueType)) {
			StringBuffer sql = new StringBuffer();

			sql.append("select count(1) from t_bap_issue bi    where bi.stat='S0A'  ");
			issueCondition(readRec, listargs, sql);
			if (noRead) {
				listargs.add(readRec.get("staffId"));
				sql.append(" and not exists (select null from t_bap_staffrec br where br.issue_id=bi.issue_id and  br.staff_id=? and  br.stat='S0R' ) ");
				count = baseDao.queryForInt(sql.toString(), listargs.toArray());
			} else
				count = baseDao.queryForInt(sql.toString(), listargs.toArray());
		}
		return count;
	}

	public int countNoticeId(Map<String, String> readRec) {
		return countIssueId(readRec);
	}

	public int countMsgId(Map<String, String> readRec) {
		return countIssueId(readRec);
	}

	public int countNoReadNoticeId(Map<String, String> readRec) {
		return countIssueId(readRec, true);
	}

}
