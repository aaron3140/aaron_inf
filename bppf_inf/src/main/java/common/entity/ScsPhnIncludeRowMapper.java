package common.entity;
/**
 * 话费充值规则限制映射类
 */
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ScsPhnIncludeRowMapper implements RowMapper {
	
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		ScsPhnInclude include = new ScsPhnInclude(rs.getString("TIMELIMIT"),
				rs.getString("MINVALUE"),
				rs.getString("SCSFLAG"));
		
		return include;
	}
}
