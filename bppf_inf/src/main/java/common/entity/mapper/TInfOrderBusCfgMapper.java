package common.entity.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import common.entity.TInfOrderBusCfg;


public class TInfOrderBusCfgMapper implements RowMapper {

	public Object mapRow(ResultSet rs, int arg1) throws SQLException {

		TInfOrderBusCfg bean = new TInfOrderBusCfg(rs.getString("CFG_ID"), rs
				.getString("SVC_CODE"), rs.getString("BUS_CODE"), rs
				.getString("BUS_NAME"), rs.getDate("CREATEDATE"),rs.getString("STAT"));
//		bean.setCfgId(rs.getString("CFG_ID"));
//		bean.setBusCode(rs.getString("BUS_CODE"));
//		bean.setBusName(rs.getString("BUS_NAME"));
//		bean.setSvcCode(rs.getString("SVC_CODE"));
//		bean.setStat(rs.getString("STAT"));
//		bean.setCreateDate(rs.getDate("CREATEDATE"));

		return bean;
	}

}
