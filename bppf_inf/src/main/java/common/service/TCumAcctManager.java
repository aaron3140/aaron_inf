package common.service;

import common.dao.TCumAcctDao;

public class TCumAcctManager {

	/**
	 * 通过客户编码查询银行卡号
	 */
	public String getAcctCode(String custCode){
		TCumAcctDao tCumAcctDao=new TCumAcctDao();
		return tCumAcctDao.getAcctCode(custCode);
	}
}
