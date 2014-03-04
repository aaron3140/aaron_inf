package common.entity.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import common.entity.TSymCustom;

/**
 * 
 * 本类描述: 
 * @version: 企业帐户前置接口 v1.0 
 * @author: 广州天讯瑞达通讯技术有限公司(zhuxiaojun)
 * @email:  zhuxiaojun@tisson.com
 * @time: 2013-4-20上午12:04:00
 */
public class TSymCustomMapper implements RowMapper{

	public Object mapRow(ResultSet rs, int index) throws SQLException {
		// TODO Auto-generated method stub
		TSymCustom custom = new TSymCustom();
		custom.setCustomId(rs.getLong("CUSTOM_ID"));
		custom.setCustId(rs.getLong("CUST_ID"));
		custom.setCustomType(rs.getString("CUSTOM_TYPE"));

		custom.setTh(rs.getString("TH"));
		custom.setThType(rs.getString("TH_TYPE"));
		custom.setStat(rs.getString("STAT"));
		return custom;
	}

}
