package common.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class CumInfoRowMapper implements RowMapper{

	
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		CumInfo cumInfo = new CumInfo(rs.getString("CUSTCODE"),rs.getString("CUSTNAME"),
				rs.getString("GENDER"),rs.getString("TELEPHONE"),rs.getString("EMAIL"),
				rs.getString("PROVINCE"),rs.getString("CITY"),rs.getString("DISTRICT"),rs.getString("ADDRESS"));
		
		return cumInfo;
	}  
    
}