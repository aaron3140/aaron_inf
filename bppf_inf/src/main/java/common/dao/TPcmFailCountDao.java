package common.dao;

import java.util.List;

import common.utils.SpringContextHelper;

/** dao for T_PCM_FAILCOUNT
 * @author Tisson
 *
 */
public class TPcmFailCountDao {
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	
	public static List getFailCount(String cardNo){
		String sql = "Select card_no, fail_count, count_date from T_PCM_FAILCOUNT " 
				+"where card_no = ?";
		
		return DAO.queryForList(sql.toString(),new Object[]{cardNo});
	}

	public static void insert(String cardNo, int failCount) {
		
		String sql = "insert into T_PCM_FAILCOUNT (CARD_NO,FAIL_COUNT,COUNT_DATE) "
			+" values (?,?,trunc(sysdate))";
		
		DAO.insert(sql,new Object[]{cardNo,failCount});
	}

	public static void update(String cardNo, int failCount) {
		String sql = "update T_PCM_FAILCOUNT set FAIL_COUNT =?,COUNT_DATE=trunc(sysdate) where CARD_NO=?";
		
		DAO.update(sql,new Object[]{failCount,cardNo});
		
	}
}
