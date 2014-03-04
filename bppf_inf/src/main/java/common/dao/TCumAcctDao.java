package common.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import common.utils.Charset;
import common.utils.SpringContextHelper;


public class TCumAcctDao {
	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();

	/**
	 * 通过客户编码查询银行卡号
	 */
	public String getAcctCode(String custCode){
		String sql="select acct_code from t_cum_acct t where t.cust_id = (select a.cust_id"
			  +" from t_cum_info a where a.cust_code = ?) and t.acct_type='ACCT002'";
		
		String acctCode = (String) DAO.queryForObject(sql, new Object[] {custCode}, String.class);
		return acctCode; 
	}
	/**
	 * 通过客户编码查询【母卡】银行卡号
	 */
	public String getParentAcctCode(String custCode){
		String sql = "select acct.ACCT_CODE from t_cum_info info1,t_cum_acct acct where info1.cust_id=acct.cust_id and acct.acct_type='ACCT002' and info1.cust_type='C02'and info1.prtn_id= (select p.parent_id from t_cum_info info, t_pnm_partner p  where  info.prtn_id= p.prtn_id and info.cust_code=?)";
		String acctCode = (String) DAO.queryForObject(sql, new Object[] {custCode}, String.class);
		return acctCode; 
	}
	/**
	 * 通过客户编码查询【子卡】银行卡号列表
	 */
	/**
	 * @param custCode
	 * @param startNum 开始 包含
	 * @param endNum 结束 包含
	 * @return
	 */
	public List getChildAcctCodeList(String custCode){
		String sql = "select acct.acct_code from t_cum_acct acct where  acct.acct_type='ACCT002' and acct.cust_id in (select info.cust_id  from t_pnm_partner p,t_cum_info info where info.cust_type='C02' and p.prtn_id = info.prtn_id and p.parent_id = (select p.prtn_id from t_pnm_partner p,t_cum_info info where p.prtn_id = info.prtn_id and info.cust_code=?))";
		List list = DAO.queryForList(sql, new Object[]{custCode},String.class) ;
		return list; 
	}
	
	/**
	 * 根据母卡的客户编码查询子卡的总数量
	 * @param custCode
	 * @return
	 */
	public String getChildInfoListCount(String custCode){
		String sql = "select count(1) from (select acct.acct_code,acct.stat,info.cust_id,info.cust_name,info.cust_code from t_cum_acct acct,t_cum_info info where acct.cust_id=info.cust_id and acct.cust_id in (select info.cust_id  from t_pnm_partner p,t_cum_info info where info.cust_type='C02' and p.prtn_id = info.prtn_id and p.parent_id = (select p.prtn_id from t_pnm_partner p,t_cum_info info where p.prtn_id = info.prtn_id and info.cust_code=?)))";
		int count = DAO.queryForInt(sql,  new Object[]{custCode});
		return String.valueOf(count);
	}
	/**
	 * 根据母卡的客户编码查询子卡的卡号和客户编码
	 * key:ACCT_CODE,CUST_CODE
	 * @param custCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,String>> getChildInfoListMap(String custCode,String startNum,String endNum){
//		String sql = "select acct.acct_code,info.cust_code from t_cum_acct acct,t_cum_info info where acct.cust_id=info.cust_id and acct.cust_id in (select info.cust_id  from t_pnm_partner p,t_cum_info info where info.cust_type='C02' and p.prtn_id = info.prtn_id and p.parent_id = (select p.prtn_id from t_pnm_partner p,t_cum_info info where p.prtn_id = info.prtn_id and info.cust_code=?))";
		if(Charset.isEmpty(startNum, true)||Long.valueOf(startNum)<=0){
			startNum="1";
		}
		if(Charset.isEmpty(endNum, true)||Long.valueOf(endNum)<=0){
			endNum="10";
		}
//		String sql = "select * from (select rownum rn,acct.acct_code,acct.stat,info.cust_id,info.cust_name,info.cust_code from t_cum_acct acct,t_cum_info info where acct.cust_id=info.cust_id and acct.cust_id in (select info.cust_id  from t_pnm_partner p,t_cum_info info where info.cust_type='C02' and p.prtn_id = info.prtn_id and p.parent_id = (select p.prtn_id from t_pnm_partner p,t_cum_info info where p.prtn_id = info.prtn_id and info.cust_code=?))) where rn>=? and rn<=?";
		String sql = "select * from (select rownum rn,newtable.* from (select acct.acct_code,info.stat,info.cust_id,info.cust_name,info.cust_code from t_cum_acct acct,t_cum_info info where acct.cust_id=info.cust_id and acct.cust_id in (select info.cust_id  from t_pnm_partner p,t_cum_info info where info.cust_type='C02' and p.prtn_id = info.prtn_id and p.parent_id = (select p.prtn_id from t_pnm_partner p,t_cum_info info where p.prtn_id = info.prtn_id and info.cust_code=?)) order by cust_id) newtable) where rn>=? and rn<=?";
		List list = DAO.queryForList(sql, new Object[]{custCode,startNum,endNum});
		
		if(list!=null&& list.size()!=0){
			return list; 
		}
		return Collections.EMPTY_LIST; 
	}
	
	
	/**
	 * 根据母卡的客户编码查询指定子卡的卡号和客户编码
	 * @param childCustCode
	 * @param parentCustCode
	 * @return
	 */
	public  List<Map<String,String>> getChildInfoMap(String childCustCode,String parentCustCode){
		String sql="select acct.acct_code,info.stat,info.cust_id,info.cust_name,info.cust_code  from t_cum_acct acct,t_cum_info info where acct.cust_id=info.cust_id and info.cust_code =? and acct.cust_id in (select info.cust_id  from t_pnm_partner p,t_cum_info info where info.cust_type='C02' and p.prtn_id = info.prtn_id and p.parent_id = (select p.prtn_id from t_pnm_partner p,t_cum_info info where p.prtn_id = info.prtn_id and info.cust_code=?))";
		List list = DAO.queryForList(sql, new Object[]{childCustCode,parentCustCode});
		
		if(list!=null&& list.size()!=0){
			return list; 
		}
		return Collections.EMPTY_LIST; 
	}
	/**
	 * 判读是否为子母卡
	 * @param childCustCode
	 * @param parentCustCode
	 * @return
	 */
	public boolean isChildAndParentCard(String childCustCode,String parentCustCode){
		String sql="select count(1) from t_cum_acct acct,t_cum_info info where acct.cust_id=info.cust_id and info.cust_code =? and acct.cust_id in (select info.cust_id  from t_pnm_partner p,t_cum_info info where info.cust_type='C02' and p.prtn_id = info.prtn_id and p.parent_id = (select p.prtn_id from t_pnm_partner p,t_cum_info info where p.prtn_id = info.prtn_id and info.cust_code=?))";
		int count = DAO.queryForInt(sql, new Object[] {childCustCode,parentCustCode});
		return count!=0; 
	}
	
	/**
	 * 通过客户编码查询银行卡号
	 * acctType：ACCT001-银行卡号 ACCT002-企业账户号
	 */
	public String getAcctCode(String custCode,String acctType){
		String sql="select acct_code from t_cum_acct t where t.cust_id = (select a.cust_id"
			+" from t_cum_info a where a.cust_code = ?) and t.acct_type=?";
		String acctCode = (String) DAO.queryForObject(sql, new Object[] {custCode,acctType}, String.class);
		if(!Charset.isEmpty(acctCode, true)){
			acctCode = acctCode.trim();
		}
		return acctCode; 
	}
	/**
	 * 通过客户编码查询银行卡开户名
	 */
	public String getAccName(String custCode,String acctType){
		String sql="select acct_name from t_cum_acct t where t.cust_id = (select a.cust_id"
			+" from t_cum_info a where a.cust_code = ?) and t.acct_type=?";
		
		String acctName = (String) DAO.queryForObject(sql, new Object[] {custCode,acctType}, String.class);
		return acctName; 
	}
	
	/**
	 * 通过签约ID查询银行卡号
	 * 
	 * */
	public String getAcctCodeByContractId(String contractId){
		String sql = " select acct.acct_code from t_cum_acct acct where acct.acct_id=" +
				"(select t.acct_id  from t_cum_acct_attr  t where t.value1=? and rownum<=1)";
	   List list = DAO.queryForList(sql, new Object[]{contractId}) ;
	   String acctCode = null;
	   if(list!=null&&list.size()>0){
			acctCode = ((Map)list.get(0)).get("ACCT_CODE").toString();
		}
	   return acctCode;
	}
	
/**
 * 通过银行卡号,客户编码（不一定正确）查询客户编码；要是传入的客户编码是错误的返回null
 * @param acctCode
 * @param custCode
 * @return
 */
	public String getCustCode(String acctCode,String custCode){
		String sql = "select info.cust_code from t_cum_info info where info.cust_id = " +
				"( select acct.cust_id from t_cum_acct acct where acct.acct_code=?  and acct.acct_type='ACCT001' and rownum<=1 )";				
		if (!Charset.isEmpty(custCode)){
			sql =sql+"  and info.prtn_id =( select prtn_id from t_cum_info where cust_code='"+custCode+"')";
		}
		List list = DAO.queryForList(sql, new Object[]{acctCode}) ;
		if(list!=null&&list.size()>0){
			custCode = ((Map)list.get(0)).get("CUST_CODE").toString();
			return custCode;
		}else{
			return null;
		}		
	}
	
	/**
	 * 通过银行卡号查询银行编码
	 * @param acctCode
	 * @return bankCode
	 */
	private String getBankCode(String acctCode, Integer acctType){
		String sql = "select bank_code from t_sym_bankcard where (account_prefix=? or account_prefix=? or account_prefix=?) and account_length=?";
		
		List<Object> param = new ArrayList<Object>();
		param.add(acctCode.substring(0, 6));
		param.add(acctCode.substring(0, 5));
		param.add(acctCode.substring(0, 4));
		param.add(acctCode.length());
		
		if (acctType != null) {
			sql += " and account_type=?";
			param.add(acctType);
		}
	    List<?> list = DAO.queryForList(sql, param.toArray(), String.class);	
	    if(list!=null && list.size()>0){
			return (String)list.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 通过银行卡号查询银行编码
	 * @param acctCode
	 * @return bankCode
	 */
	public String getBankCode(String acctCode){
		return getBankCode(acctCode, null);
	}
	
	
	/**
	 * 根据卡号判断是否为借记卡
	 * @param acctCode
	 * @return
	 */
	public Boolean isDebitCard(String acctCode){
		String sql = "select count(*) from t_sym_bankcard where (account_prefix=? or account_prefix=? or account_prefix=?) and account_length=?  and account_type='1'";
		
		List<Object> param = new ArrayList<Object>();
		param.add(acctCode.substring(0, 6));
		param.add(acctCode.substring(0, 5));
		param.add(acctCode.substring(0, 4));
		param.add(acctCode.length());
		
	   int num = DAO.queryForInt(sql, param.toArray());	
		return num>0;
	}
	
	/**
	 * 判断是否是信用卡
	 * @param acctCode
	 * @return bankCode
	 */
	public Boolean getIsCreditCard(String acctCode){
		return getBankCode(acctCode, 2) != null;
	}
	
	public Boolean getIsCard(String acctCode){
		return getBankCode(acctCode, 1) != null;
	}
	
	/**
	 * 通过银行卡号查询银行编码(旧)
	 * @param acctCode
	 * @return bankCode
	 */
	/*public String getBankCodeOld(String acctCode){
		String sql = "select bank_code from bppf_pgw.t_pgw_bankcard where account_prefix=? or account_prefix=? or account_prefix=?";
//		String sql = "select bank_code from t_pgw_bankcard where account_prefix=? or account_prefix=? or account_prefix=?";
//	    String bankCode = (String) DAO.queryForObject(sql, new Object[] {acctCode.subSequence(0, 6),acctCode.subSequence(0, 5),acctCode.subSequence(0, 4)}, String.class);	
		String bankCode = (String) infDAO.queryForObject(sql, new Object[] {acctCode.subSequence(0, 6),acctCode.subSequence(0, 5),acctCode.subSequence(0, 4)}, String.class);	
		return bankCode;
	}*/
	
//	/**传递过来的客户编码可以是三级网点编码、也可以是二级企业客户编码
//	 * 通过客户编码查询天讯卡户号
//	 */
//	public String getCardAccNbr(String custCode){
//		String sql = "select ACCT.ACCT_CODE from t_cum_info info1, T_CUM_ACCT ACCT " +
//				"where INFO1.CUST_ID = ACCT.CUST_ID " +
//				"AND info1.prtn_id =(select info.prtn_id from t_cum_info info where info.cust_code = ?) " +
//				"AND INFO1.CUST_TYPE = 'C02' " +
//				"AND ACCT.ACCT_TYPE = 'ACCT002'";
//		String cardAccNbr = (String) DAO.queryForObject(sql, new Object[] {custCode}, String.class);
//		return cardAccNbr;
//	}
	
	/**
	 * 通过银行卡号查询区域编码（账号归属地）
	 */
	public String getAreaCode(String acctCode){
	   String sql = "select value1 from t_cum_acct_attr " +
				"where acct_id=(select acct_id from t_cum_acct_attr where value1=? and rownum=1)  and attr_id=2568";
	   List list = DAO.queryForList(sql, new Object[]{acctCode}) ;
	   String areaCode =null;
	   if(list!=null&&list.size()>0){
		   areaCode = ((Map)list.get(0)).get("VALUE1").toString();
		}
	   return areaCode;
	}
}
