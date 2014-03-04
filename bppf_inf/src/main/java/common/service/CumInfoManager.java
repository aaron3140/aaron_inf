package common.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import common.dao.TCumInfoDao;
import common.dao.TCumInfoDaoTemp;
import common.entity.CumInfo;
import common.utils.Charset;

@Transactional
public class CumInfoManager {
	
	@Transactional(rollbackFor=Exception.class)
	public static void saveOrUpdateCumInfo(CumInfo cumInfo) {

		Map<String,String> map = new HashMap<String,String>();
		String custCode = cumInfo.getCustCode();
		String custId = TCumInfoDaoTemp.queryCustIdByCustCode(custCode);
		String sex = cumInfo.getGender();
		if("1".equals(sex)){
			sex = "M";
		}else if("2".equals(sex)){
			sex = "F";
		}else{
			sex = "U";
		}	
		TCumInfoDaoTemp.updateCumInfoSex(custCode,sex);

		if(!Charset.isEmpty(cumInfo.getAddress()))
			map.put("ADDR", cumInfo.getAddress());
		if(!Charset.isEmpty(cumInfo.getEmail()))
			map.put("EMAIL", cumInfo.getEmail());
		if(!Charset.isEmpty(cumInfo.getTelephone()))
			map.put("PSTN", cumInfo.getTelephone());
		if(!Charset.isEmpty(cumInfo.getProvince()))
			map.put("PROVINCE", cumInfo.getProvince());
		if(!Charset.isEmpty(cumInfo.getCity()))
			map.put("CITY", cumInfo.getCity());
		if(!Charset.isEmpty(cumInfo.getDistrict()))
			map.put("DISTRICT", cumInfo.getDistrict());
		
		TCumInfoDaoTemp.deleteCumContactInfo(custCode,map);
		TCumInfoDaoTemp.insertCumContactInfo(custId,map);
	}
	
	
	/**
	 * 通过merId查询客户编码
	 */
	public static String getCustCode(String merId){
		TCumInfoDao tCumInfoDao=new TCumInfoDao();
		return tCumInfoDao.getCustCode(merId);
	}


}
