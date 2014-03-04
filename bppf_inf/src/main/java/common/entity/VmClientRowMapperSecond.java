package common.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class VmClientRowMapperSecond implements RowMapper{

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		VmClient vmClient = new VmClient(rs.getString("CLIENT_ID"),rs.getString("FILE_ID"),
				rs.getString("VERSION"),rs.getString("VERSION_DESC"),rs.getString("STAT"),rs.getString("FILE_SIZE"));
		return vmClient;
	}

}
