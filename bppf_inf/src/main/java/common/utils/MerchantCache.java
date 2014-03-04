package common.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import common.dao.BaseDao;

import framework.exception.INFException;
import framework.exception.INFErrorDef;

/**
 * 将所有SP商户的受理终端号、连接数、cust_code、cust_name等信息读入哈希表，key为
 * sp商户的接入号码，即商户id（SUPPLYORGCODE）
 * 
 * @author Administrator
 * 
 */
public class MerchantCache {
	
	private static HashMap<String, Merchant> PRTN_CODE_INFO_MAP = new HashMap<String, Merchant>();
	
	private static final String SYMBOLS = "|";
	
	private static final String VALID_STAT = "S0N";
	
	private static final String EXPIRE_DATE_ATTRID = "206";
	
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	
	//	 对应PRTN_CODE_INFO__MAP(根据合作商户编码作为key)
	public static Merchant getSpInfo(String partnerId) throws Exception {
		return getSpInfo(partnerId, false);
	}
	
	public synchronized static Merchant getSpInfo(String partnerId, boolean checkStatAndExpire) throws Exception {
		Merchant info = PRTN_CODE_INFO_MAP.get(partnerId);

		if (info == null) {
			Merchant newInfo = getFromDB(partnerId);
			
			if (newInfo == null) {
				throw new INFException(INFErrorDef.INF_PartnerIdNotExists_ERRCODE,
						"商户编码为"+partnerId+"的商户不存在或商户状态不正常");
			}
			
			if (isExpire(partnerId)) {
				throw new INFException(INFErrorDef.INF_MERCHANT_EXPIRE,
						"商户编码为"+partnerId+"的商户的合作期限已过期");
			}
			
			PRTN_CODE_INFO_MAP.put(partnerId, newInfo);
			return newInfo;
			
		} else if (checkStatAndExpire) {
			if (!isStatValid(partnerId)) {
				throw new INFException(INFErrorDef.INF_PartnerIdNotExists_ERRCODE,
						"商户编码为"+partnerId+"的商户不存在或商户状态不正常");
			}
			
			if (isExpire(partnerId)) {
				throw new INFException(INFErrorDef.INF_MERCHANT_EXPIRE,
						"商户编码为"+partnerId+"的商户的合作期限已过期");
			}
		}
		
		return info;
	}
	
	
	//状态是否合法
	private static boolean isStatValid(String prtnCode) {
		String sql = " select prtn_id from t_pnm_partner where prtn_code = ? and stat = '" + VALID_STAT + "' ";
		String prtnId = (String) DAO.queryForObject(sql, new Object[] {prtnCode}, String.class);
		return prtnId == null ? false : true;
	}
	
	//是否超过合作期限
	private static boolean isExpire(String prtnCode) {
		String sql = "select value1 from t_pnm_partner a, t_pnm_attr b where a.prtn_id = b.prtn_id and b.attr_id = '" + EXPIRE_DATE_ATTRID + "' and a.prtn_code = ? ";
		String expireDate = (String) DAO.queryForObject(sql, new Object[] {prtnCode}, String.class);
		String nowDate = DateTool.formatCurDate("yyyy-MM-dd");
		return (nowDate.compareTo(expireDate) > 0) ? true : false;
	}
	
	private static Merchant getFromDB(String prtnCode) {
		String sql = "  select  "
				+ "   to_char(prtn_id) as prtn_id, "
				+ "  (select value1 from t_pnm_attr where attr_id = '206'  and prtn_id  = a.prtn_id) co_end_date, "
				+ "  (select value1 from t_pnm_attr where attr_id = '208'  and prtn_id  = a.prtn_id) term_no, "
				+ "  (select value1 from t_pnm_attr where attr_id = '301'  and prtn_id  = a.prtn_id) bind_pos, "
				+ "  (select value1 from t_pnm_attr where attr_id = '302'  and prtn_id  = a.prtn_id) link_limit, "
				+ "  (select value1 from t_pnm_attr where attr_id = '207'  and prtn_id  = a.prtn_id) unioncard_prefix, "
				+ "  (select value2 from t_pnm_attr where attr_id = '207'  and prtn_id  = a.prtn_id) unioncard_name, "
				+ "  (select value1 from t_pnm_attr where attr_id = '304'  and prtn_id  = a.prtn_id) url, "
				+ "  (select value1 from t_pnm_attr where attr_id = '203'  and prtn_id  = a.prtn_id) risk_level, "
				+ "  (select value1 from t_pnm_attr where attr_id = '204'  and prtn_id  = a.prtn_id) ind_code, "
				+ "  (select value1 from t_pnm_attr where attr_id = '403'  and prtn_id  = a.prtn_id) wap_key, "
				+ "  prtn_code as prtn_code, "
				+ "  prtn_name as prtn_name, "
				+ "  (select cust_code from t_cum_info where prtn_id = a.prtn_id) cust_code, "
				+ "  (select cust_name from t_cum_info where prtn_id = a.prtn_id) cust_name, "
				+ "  (select cust_id from t_cum_info where prtn_id = a.prtn_id) cust_id, "
				+ "  (select area_code from t_cum_info where prtn_id = a.prtn_id) area_code "
				+ "  from t_pnm_partner a where prtn_code = ? and a.stat='" + VALID_STAT + "'";
		
		
		List rows = DAO.queryForList(sql, new Object[] {prtnCode});
		Iterator it = rows.iterator();
		Merchant newInfo = null;
		String sourceIp = "";
		String prtn_id = "";
		
		while (it.hasNext()) {
			newInfo = new Merchant();
			Map map = (Map) it.next();
			newInfo.setCoEndDate((String) map.get("co_end_date"));
			newInfo.setUnioncardPrefix((String) map.get("unioncard_prefix"));
			newInfo.setUnioncardName((String) map.get("unioncard_name"));
			newInfo.setRiskLevel((String) map.get("risk_level"));
			newInfo.setIndCode((String) map.get("ind_code"));	
			newInfo.setWapKey((String) map.get("wap_key"));
			newInfo.setTermNo((String) map.get("term_no"));
			newInfo.setBindPos((String) map.get("bind_pos"));
			newInfo.setLinkLimit((String) map.get("link_limit"));
			newInfo.setUrl((String) map.get("url"));
			newInfo.setPrtnCode((String) map.get("prtn_code"));
			newInfo.setPrtnName((String) map.get("prtn_name"));
			newInfo.setCustCode((String) map.get("cust_code"));
			newInfo.setCustName((String) map.get("cust_name"));
			newInfo.setAreaCode((String) map.get("area_code"));
			
			prtn_id = (String) map.get("prtn_id");
			String sql2 = " select serv_ip as serv_ip from t_pnm_server t where t.prtn_id = ? ";
			List rows2 = DAO.queryForList(sql2, new Object[] {prtn_id});
			Iterator it2 = rows2.iterator();
			while (it2.hasNext()){
				Map map2 = (Map) it2.next();
				sourceIp = sourceIp + SYMBOLS + (String) map2.get("serv_ip");
			}
			newInfo.setSourceIp(sourceIp);
			break;
		}

		return newInfo;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
