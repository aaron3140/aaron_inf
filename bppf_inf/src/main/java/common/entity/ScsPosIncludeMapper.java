package common.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ScsPosIncludeMapper implements RowMapper{

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

		ScsPosInclude entity = new ScsPosInclude(
				rs.getString("POSCODE"),
				rs.getString("CASH_LIMIT"),
				rs.getString("ERR_CODE"),
				rs.getString("ERR_MSG")
				);
		
		return entity;
	}

}
