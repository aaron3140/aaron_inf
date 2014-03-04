package common.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class VmClientRowMapper implements RowMapper {

public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		VmClient vmClient = new VmClient();
		vmClient.fromGetLatestClient(
				rs.getString("FILE_ID"),
				rs.getString("VERSION"),
				rs.getString("VERSION_DESC"),
				rs.getString("client_id"),
				rs.getString("IS_FORCEUP"),
				rs.getString("FILE_SIZE"));		
		return vmClient;
	}

}
