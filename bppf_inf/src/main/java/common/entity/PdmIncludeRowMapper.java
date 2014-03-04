package common.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class PdmIncludeRowMapper implements RowMapper {
	
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		PdmInclude include = new PdmInclude(rs.getString("APPLY_OBJ_ID2"),rs.getString("MEMO"));
		return include;
	}
}
