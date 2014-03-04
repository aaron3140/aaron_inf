package common.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import common.utils.IposConstant;
import common.utils.SpringContextHelper;

public class TPnmPartnerDao {
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	/**
	 * * 根据合作伙伴编码查询合作伙伴标识
	 * 
	 * @return
	 */
	public String getPrtnId(String merId) {
		String sql = "select p.prtn_id from t_pnm_partner p where p.prtn_code = ?";
		String prtnId = (String) DAO.queryForObject(sql,
				new Object[] { merId }, String.class);
		return prtnId;
	}

	public Map<String, String> getPrntnCodeAndPrntType(String custCode) {
		String sql = "select t.prtn_code,t.prtn_type from t_pnm_partner t ,"
				+ "t_cum_info t2 where t.prtn_id = t2.prtn_id and t2.cust_code = ?";
		Map<String, String> map = DAO.queryForMap(sql,
				new Object[] { custCode });
		return map;
	}

	/**
	 * 查出相同机构下面的所有商户编码
	 * 
	 * @param prnt_code
	 * @return
	 */
	public HashSet<String> querySameOrganPrntCode(String prntCode) {
		String sql = "SELECT PRTN_CODE FROM T_PNM_PARTNER START WITH prtn_code = ? CONNECT BY PRIOR prtn_id = parent_id and stat ='S0A'";
		List list = DAO.queryForList(sql, new Object[] { prntCode });
		HashSet<String> hs = new HashSet<String>();
		for (int i = 0, l = list.size(); i < l; i++) {
			Map<String, String> map = (Map<String, String>) list.get(i);
			hs.add(map.get("PRTN_CODE"));
		}
		return hs;
	}

	public String getCustCodeByStaff(String staffCode) {
		String sql = "select info.cust_code  from t_cum_info info where info.cust_type='C02' "
				+ "and info.prtn_id=(select priv.prtn_id from t_cum_priv priv,t_sym_staff staff "
				+ "where staff.org_id=priv.org_id and staff.staff_code=? and priv.stat = 'S0A')";
		String custCode = (String) DAO.queryForObject(sql,
				new Object[] { staffCode }, String.class);

		return custCode;
	}

	public String getPrtnCodeByCustCode(String custCode) {
		String sql = "select prtn_code from t_pnm_partner p where p.prtn_type='PT403' start with p.prtn_id="
				+ "(select i.prtn_Id from t_cum_info i where i.cust_type='C02' and i.cust_code=?)"
				+ " connect by prior p.parent_id=p.prtn_id";

		String prtnCode = (String) DAO.queryForObject(sql,
				new Object[] { custCode }, String.class);

		return prtnCode;
	}

	public String getRegTypeByStaff(String statCode) {

		String sql = " select t.reg_type from T_PNM_PARTNER t,t_cum_priv p,t_sym_staff s where t.prtn_id=p.prtn_id and s.org_id=p.org_id  and p.stat = 'S0A' and s.staff_code=?";

		String prtType = (String) DAO.queryForObject(sql,
				new Object[] { statCode }, String.class);

		if (IposConstant.REG_PRT1001.equals(prtType)) {

			return "0";
		} else if (IposConstant.REG_PRT1002.equals(prtType)) {
			return "1";
		} else {
			return "-1";
		}
	}

	public String getPlineIdByCusCode(String custCode) {

		String sql = " SELECT AA.VALUE1 FROM  T_CUM_ATTR AA WHERE AA.ATTR_ID=4224 AND AA.CUST_ID=(SELECT C.CUST_ID FROM T_CUM_INFO C WHERE C.CUST_CODE=? AND C.CUST_TYPE='C02')";

		String pLineId = (String) DAO.queryForObject(sql,
				new Object[] { custCode }, String.class);

		return pLineId;

	}
	/**
	 * 根据客户编码获得合作伙伴编码
	 */
	public String getPrtnCode301ByCustCode(String custCode){
		String sql = "select prtn_code from t_pnm_partner p where rownum=1 and 1=1 start with p.prtn_id=" +
		"(select i.prtn_Id from t_cum_info i where i.cust_type='C02' and i.cust_code=?)" +
		" connect by prior p.parent_id=p.prtn_id";
//		log.info(sql+"=======332========="+custCode);
		String prtnCode = (String) DAO.queryForObject(sql, new Object[] {custCode}, String.class);
		
		return prtnCode;
	}
	public String getPTypeNameByCusCode(List<String> ids) {

		Object[] p = ids.toArray();
		
		StringBuffer sb = new StringBuffer();

		String sql = " select pdline_name from T_PDM_PDLINE where pdline_id in(?)";
		
		if(p.length>1){
			sql = " select pdline_name from T_PDM_PDLINE where pdline_id in(?,?)";
		}

		List<String> names = (List<String>) DAO.queryForList(sql, p, String.class);
			
			for(String s:names){
				
				if (!"".equals(s)) {

					if ("手机IPOS".equals(s)) {

						sb.append("0002");

						sb.append("|");
					} else if ("企业账户".equals(s)) {

						sb.append("0001");

						sb.append("|");
					} else {

						sb.append("|");
					}
				}
				
			}
			if (sb.length() > 0) {

				sb.deleteCharAt(sb.length() - 1);
			}

		return sb.toString();

	}

	public List<String> convertS(String str){
		
		String[] ps = str.split(",");
		
		List<String> r = new ArrayList<String>();
		
		for(int i=0;i<ps.length;i++){
			
			if (!"".equals(ps[i])) {
				
				r.add(ps[i]);
			}
		}
		return r;
	}
	
	public String convert(String str) {

		String[] ps = str.trim().split(",");

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < ps.length; i++) {

			if (!"".equals(ps[i])) {
				
				sb.append(ps[i]);
				
				sb.append("|");
			}
		}

		if (sb.length() > 0) {

			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}
	
}
