package common.utils;

import org.apache.cxf.common.util.StringUtils;

public class DBTool {

	/**
	 * 构建新的sql，跳过startRecord条记录后，返回maxRecord指定个数的记录
	 * @param sql
	 * @param maxRecord
	 * @param startRecord
	 * @return
	 */
	public static final String createMSSql(String sql, String startRecord, String maxRecord) {
		if(StringUtils.isEmpty(startRecord)){
			startRecord = "0";
		}
		
		StringBuffer newSql = new StringBuffer();
		newSql.append("select * from (select rownum no_ , A_.* from (" + sql + ") A_ ");
		
		if(!StringUtils.isEmpty(maxRecord)){
			newSql.append(" where  rownum < (to_number('")
			.append(maxRecord)
			.append("') + to_number('")
			.append(startRecord)
			.append("') + 1) ");
		}
		
		newSql.append(") B_ where B_.no_ > to_number('")
		.append(startRecord)
		.append("')");
		return newSql.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        String sql = "select * from t_scs_order where trunc(acct_date) = trunc(to_date('20110802', 'YYYYMMDD')) ";
        System.out.println(createMSSql(sql, "2", "3"));
        System.out.println(createMSSql(sql, null, "3"));
        System.out.println(createMSSql(sql, "", "3"));
        System.out.println(createMSSql(sql, "2", null));
        System.out.println(createMSSql(sql, "2", ""));
        System.out.println(createMSSql(sql, "", ""));
	}

}
