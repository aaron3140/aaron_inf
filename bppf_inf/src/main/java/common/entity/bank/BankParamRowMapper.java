package common.entity.bank;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class BankParamRowMapper implements RowMapper {

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		TBankInfo info = new TBankInfo();
		info.setBankId(rs.getBigDecimal("BANK_ID").intValue());
		info.setBankCode(rs.getString("BANK_CODE"));
		info.setBankName(rs.getString("BANK_NAME"));
		info.setOrderidLen(rs.getBigDecimal("ORDERID_LEN").intValue());
		info.setIntfName(rs.getString("INTF_NAME"));
		info.setIntfVer(rs.getString("INTF_VER"));
		info.setIntfType(rs.getString("INTF_TYPE"));
		info.setStatus(rs.getString("STATUS"));
		info.setBankType(rs.getString("BANK_TYPE"));
		
		TBankParam param = new TBankParam();
		param.setParamId(rs.getBigDecimal("PARAM_ID").intValue());
		param.setTBankInfo(info);
		param.setParamType(rs.getString("PARAM_TYPE"));
		param.setParamName(rs.getString("PARAM_NAME"));
		param.setParamDesc(rs.getString("PARAM_DESC"));
		param.setParamValue(rs.getString("PARAM_VALUE"));
		param.setParamLimit(rs.getString("PARAM_LIMIT"));
		param.setShowSeq(rs.getBigDecimal("SHOW_SEQ").intValue());
		param.setSubmit(rs.getString("SUBMIT"));
		param.setEditable(rs.getString("EDITABLE"));
		
		return param;
	}

}
