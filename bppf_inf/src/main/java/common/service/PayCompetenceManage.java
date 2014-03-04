package common.service;

import java.util.List;

import common.dao.TCumAttrDao;
import common.dao.TCumInfoDao;

public class PayCompetenceManage {
	/**
	 * 判断有无交易查询权限
	 * 
	 * @version: 1.00
	 * @history: 2012-9-24 下午03:54:40 [created]
	 * @author HaiHong Liu
	 * @param channelCode
	 * @see
	 */
	public static List payFunc(String agentCode,String channelCode)throws Exception{
		List privList = TCumAttrDao.getPrivByCustCode(agentCode,channelCode);
		return privList;
	}
	
	public static String getPdline(String custCode)throws Exception{
		
		String pdlines = TCumAttrDao.getPdline(custCode);
		
		return pdlines;
	}
	
	public static List payFunc(String agentCode)throws Exception{
		List privList = TCumAttrDao.getPrivByCC(agentCode);
		return privList;
	}
	
	public static List getDeductChannel(String cust_code)throws Exception{
		TCumAttrDao attrDao=new TCumAttrDao();
		return attrDao.getDeductChannel(cust_code);
	}
	
	public static String getOrgMerIdFromCustCode(String cust_code)throws Exception{
		TCumInfoDao attrDao=new TCumInfoDao();
		return attrDao.getOrgMerIdFromCustCode(cust_code);
	}
	
	public static String getTmnNumFromMerId(String prtnCode){
		return TCumInfoDao.getTmnNumFromMerId(prtnCode);
	}
	
	public static String getCustCodeByStaff(String staffCode)throws Exception{
		TCumInfoDao attrDao=new TCumInfoDao();
		return attrDao.getCustCodeByStaff(staffCode);
	}
	
	
	public static boolean getIvrFunc(String staffId,String ivr)throws Exception{
		return TCumAttrDao.getIvrFunc(staffId, ivr);
	}
	
}
