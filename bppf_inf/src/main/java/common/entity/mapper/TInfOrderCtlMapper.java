package common.entity.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import common.entity.TInfOrderCtl;

public class TInfOrderCtlMapper implements RowMapper {

	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		
		TInfOrderCtl bean = new TInfOrderCtl();
		
		bean.setKeep(rs.getString("KEEP"));
		bean.setOperDate(rs.getDate("OPER_DATE"));
		bean.setOperInfo(rs.getString("OPER_INFO"));
		bean.setOrderCode(rs.getString("ORDER_CODE"));
		bean.setOrderStat(rs.getString("ORDER_STAT"));
		bean.setRemark(rs.getString("REMARK"));
		bean.setStat(rs.getString("STAT"));
		bean.setTmnnum(rs.getString("TMNNUM"));
		
		return bean;
	}

}
