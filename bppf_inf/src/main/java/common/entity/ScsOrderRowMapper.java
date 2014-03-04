package common.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ScsOrderRowMapper implements RowMapper{

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		ScsOrder scsOrder = new ScsOrder(
				rs.getString("ACCEPTSEQNO"),
				rs.getString("TXNAMOUNTY"),
				rs.getString("PRODUCTNO"));
		
		return scsOrder;
	}

}
