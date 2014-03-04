package common.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import common.entity.CumInfo;
import common.entity.CumInfoRowMapper;
import common.utils.Charset;
import common.utils.SpringContextHelper;

public class TCumInfoDaoTemp {
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	
	/**
	 * 查询客户姓名
	 * @return
	 */
	public static String getCustNameByCustCode(String custCode) {
		String sql;
		if (Charset.isEmpty(custCode)) {
			return "";
		} else {
			sql = "Select cust_name from t_cum_info where cust_code = ? ";
			return (String) DAO.queryForObject(sql, new Object[] {custCode}, String.class);
		}		
	}
	
	/**
	 * 查询合作商户名称
	 * @return
	 */
	public static String getPartnerName(String partnerId) {
		String sql;
		if (Charset.isEmpty(partnerId)) {
			return "";
		} else {
			sql = "select t.prtn_name from t_pnm_partner t ,t_cum_info c where t.prtn_id = c.prtn_id and c.cust_code = ?";
			return (String) DAO.queryForObject(sql,new Object[] {partnerId}, String.class);
		}		
	}
	/**
	 * *
	 * 
	 * @version 1.00
	 * @history: 2010-12-28 下午5:52:25 [created]
	 * @author Leyi Tang 唐乐毅 从接口中获取：商户id,根据商户id查询出所属代理商客户编码
	 * @param supplyOrgCode
	 *            商户id
	 * @return
	 */
	public static String getCustCodeBySupplyOrgCode(String supplyOrgCode) {
		String sql = "SELECT c.cust_code FROM t_pnm_attr a, t_cum_info c WHERE a.attr_id = '300' AND a.value1 = ? AND a.prtn_id = c.prtn_id";
		return (String) DAO.queryForObject(sql, new Object[] {supplyOrgCode}, String.class);
	}
	
	/**
	 * *
	 * 根据客户编码查询客户信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<CumInfo> getCumInfoByCustCode(String custCode) {

		StringBuffer sql = new StringBuffer();
		
		sql.append("select ")
		  .append("t.cust_code AS CUSTCODE,")
		  .append("t.cust_name AS CUSTNAME,")
		  //.append("(select d.dict_name from t_sym_dict d where d.dict_typeid = 'CUM_CUSTSEX' and d.dict_id = t.cust_sex) AS GENDER,")
		  .append("(decode(t.cust_sex,'M','1','F','2',3)) AS GENDER,")
		  .append("(select c.value1 from t_cum_contact c where c.cust_id = t.cust_id and c.contact_type = 'ADDR' and rownum<2 ) AS ADDRESS,")
		  .append("(select c.value1 from t_cum_contact c where c.cust_id = t.cust_id and c.contact_type = 'EMAIL' and rownum<2 ) AS EMAIL,")
		  .append("(select c.value1 from t_cum_contact c where c.cust_id = t.cust_id and c.contact_type = 'PSTN' and rownum<2 ) AS TELEPHONE,")
		  .append("(select c.value1 from t_cum_contact c where c.cust_id = t.cust_id and c.contact_type = 'PROVINCE' and rownum<2 ) AS  PROVINCE,")
		  .append("(select c.value1 from t_cum_contact c where c.cust_id = t.cust_id and c.contact_type = 'CITY' and rownum<2 ) AS CITY,")
		  .append("(select c.value1 from t_cum_contact c where c.cust_id = t.cust_id and c.contact_type = 'DISTRICT' and rownum<2 ) As DISTRICT ")
		  .append(" from t_cum_info t ")
		  .append(" where t.cust_code = ? ");

		return DAO.query(sql.toString(), new Object[] {custCode}, new CumInfoRowMapper());
	}
	/**
	 * *
	 * 根据客户编码查询客户是否存在
	 * @return
	 */
	public static boolean isExistCust(String custCode) {
		
		String sql = "select count(*) from t_cum_info t where t.stat='S0A' and t.cust_code = ?";
		int count = DAO.queryForInt(sql,new Object[] {custCode}) ;
		if(count > 0) return true;
		
		return false ;
	}
	/**
	 * *
	 * 根据客户编码查询合作商户是否存在
	 * @return
	 */
	public static boolean isExistPrtn(String custCode) {
		
		String sql = "select count(*) from t_cum_info t,t_pnm_partner c where t.prtn_id = c.prtn_id and t.stat='S0A' and t.cust_code = ?";
		int count = DAO.queryForInt(sql,new Object[]{custCode}) ;
		if(count > 0) return true;
		
		return false ;
	}

	/**
	 * *
	 * 更新客户联系信息
	 * @return
	 */
	public static int updateCumContactInfo(String custCode,Map<String,String> contactMap) {
		
		int n = 0;
		if(!contactMap.isEmpty()){	
			Set<String> keys = contactMap.keySet();
			Iterator it = keys.iterator();
			while(it.hasNext()){
				String sql = "update t_cum_contact set value1 = ? where cust_id= (select cust_id from t_cum_info where cust_code=?) and contact_type=?";
				String key = (String) it.next();
				String value = contactMap.get(key);
//				sql = sql +  " where cust_id= (select cust_id from t_cum_info where cust_code=?) and contact_type=?";
				DAO.update(sql,new Object[]{value,custCode,key});
				n++;
			}
		}	
		return n ;
	}
	
	/**
	 * *
	 * 更新客户联系信息
	 * @return
	 */
	public static int deleteCumContactInfo(String custCode,Map<String,String> contactMap) {
		
		int n = 0;
		if(!contactMap.isEmpty()){	
			Set<String> keys = contactMap.keySet();
			Iterator it = keys.iterator();
			while(it.hasNext()){
				String sql = "delete from  t_cum_contact where cust_id= (select cust_id from t_cum_info where cust_code=?) and contact_type=?";
				String key = (String) it.next();
//				sql += " where cust_id= (select cust_id from t_cum_info where cust_code=?) and contact_type=?";
				DAO.update(sql,new Object[]{custCode,key});
				n++;
			}
		}	
		return n ;
	}
	
	/**
	 * *
	 * 插入客户联系信息
	 * @return
	 */
	public static int insertCumContactInfo(String custId,Map<String,String> contactMap) {
		
		int n = 0;
		if(!contactMap.isEmpty()){	
			Set<String> keys = contactMap.keySet();
			Iterator it = keys.iterator();
			while(it.hasNext()){
				String sql = "insert into t_cum_contact values(SQ_CUM_CONT.nextval,?,?,'U01',?,NULL,NULL,'S0A',NULL,NULL)";
				String key = (String) it.next();
				String value = contactMap.get(key);
//				sql += "?,'U01',?,NULL,NULL,'S0A',NULL,NULL)";
				DAO.insert(sql,new Object[]{custId,key,value});
				n++;
			}
		}	
		return n ;
	}
	
	/**
	 * *
	 * 更新客户性别
	 * @return
	 */
	public static int updateCumInfoSex(String custCode,String sex) {
		
		int n = 0;
		String sql = "update t_cum_info set CUST_SEX = ? where cust_code = ?";
		n = DAO.update(sql,new Object[]{sex,custCode});
		return n ;
	}
	
	/**
	 * *
	 * 更新客户类型
	 * @return
	 */
	public static int updateCumInfoCustType(String custCode,String custType) {
		
		int n = 0;
		String sql = "update t_cum_info set CUST_TYPE = ? where cust_code = ?";
		n = DAO.update(sql,new Object[]{custType,custCode});
		return n ;
	}
	
	/**
	 * *
	 * 更新客户名
	 * @return
	 */
	public static int updateCumInfoCustName(String custCode,String custName) {
		
		int n = 0;
		String sql = "update t_cum_info set CUST_NAME = ? where cust_code = ?";
		n = DAO.update(sql,new Object[]{custName,custCode});
		return n ;
	}
	
	/**
	 * *
	 * 根据custcode查询custId
	 * @return
	 */
	public static String  queryCustIdByCustCode(String custCode) {
		
		String sql = "select cust_id from t_cum_info where cust_code = ?";
		
		return (String) DAO.queryForObject(sql,new Object[]{custCode}, String.class);
	}
	
	/**
	 * *
	 * 根据custcode查询areacode
	 * @return
	 */
	public static String queryAreacodeByCustCode(String custCode) {
		
		String sql = "select area_code from t_cum_info where cust_code = ?";
		
		return (String) DAO.queryForObject(sql,new Object[]{custCode}, String.class);
	}
		
	/**
	 * 查询合作商户编码
	 * @return
	 */
	public static String getCustCodeByPartnerId(String partnerId) {
		String sql;
		if (Charset.isEmpty(partnerId)) {
			return "";
		} else {
			sql = "select c.cust_code from t_pnm_partner t ,t_cum_info c where t.prtn_id = c.prtn_id and t.prtn_code = ?";
			return (String) DAO.queryForObject(sql, new Object[]{partnerId}, String.class);
		}		
	}
	/**
	 * 根据订单查询用户归属地
	 * @return
	 */
	public static String getAreaCodeByOrderId(String orderId) {
		String sql;
		if (Charset.isEmpty(orderId)) {
			return "";
		} else {
			sql = "Select area_code from t_cum_info where cust_code = " +
					"(select obj_code from t_scs_actlist where order_id = ?)";
			return (String) DAO.queryForObject(sql, new Object[]{orderId}, String.class);
		}		
	}
	
	/**
	 * 查询cardCustCode
	 * @return
	 */
	public static String getCardCustCode(String cardNo) {
		String sql = "select a.cust_code "
			 +" from t_cum_info a, t_cum_attr b "
			 +" where A.CUST_ID = B.CUST_ID and "
			 +" b.value1 = ? and b.attr_id >= 1000000000 ";

		return (String) DAO.queryForObject(sql, new Object[]{cardNo}, String.class);
	}		
	
	public static int insertCustAttr(Long custId, final List cardNo, final int attrId){
		final int size = cardNo.size();
		
		String sql = "insert into t_cum_attr (cust_id, attr_id, value1, stat)" +
				" values ( '" +custId +"', ?, ?, 'S0A')";

		return DAO.batchUpdate(sql, new BatchPreparedStatementSetter(){

			public int getBatchSize() {
				return size;
			}

			public void setValues(PreparedStatement arg0, int arg1) throws SQLException {
				arg0.setInt(1, attrId + arg1);
				arg0.setString(2, (String)cardNo.get(arg1));
			}
			
		});
	}
	
	public static int getCustAttrMaxAttrId(Long custId){
		String sql = "select max(ATTR_ID) from t_cum_attr where cust_id = ?" ;

		return  DAO.queryForInt(sql,new Object[]{custId});
	}
}
