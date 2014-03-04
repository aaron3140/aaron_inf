package common.service;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.BaseDao;
import common.utils.SpringContextHelper;
import common.xml.dp.DpInf05104Request;
import common.xml.dp.DpInf05105Request;

public class SplitMapper {

	public static BaseDao DAO = SpringContextHelper.getTBaseDaoBean();

	private final Log log = LogFactory.getLog(SplitMapper.class);

	@SuppressWarnings("unchecked")
	public void saveSplit(DpInf05104Request dpRequest) throws Exception {

		Hashtable param = new Hashtable();

		param.put("CUSTCODE", dpRequest.getCustCode());

		param.put("PLANCODE", dpRequest.getPlanCode());

		param.put("PLANNAME", dpRequest.getPlanName());

		param.put("PLANDESC", dpRequest.getPlanDesc());

		param.put("PLANTYPE", dpRequest.getPlanType());

		param.put("PLANCUSTCODE", dpRequest.getPlanCustCode().split("\\|"));

		param.put("PLANVALUE", dpRequest.getPlanValue().split("\\|"));

		DAO.tran_SaveSplit(param);

	}

	public List<Map<String, Object>> querySplit(DpInf05105Request dpRequest)
			throws Exception {

		String sql = "SELECT P.PLAN_CODE,P.PLAN_NAME,P.PLAN_DESC,P.PLAN_TYPE,D.PLAN_OBJECT AS PLAN_CUSTCODE,D.PLAN_VALUE,P.PLAN_DATE FROM  T_PDM_SUBLEDGER P LEFT JOIN T_PDM_SUBLEDGER_DETAIL D ON P.PLAN_ID=D.PLAN_ID ";
		if(dpRequest.getPlanCode()!=null&&!"".equals(dpRequest.getPlanCode())){
			sql =sql+" AND P.PLAN_CODE LIKE '"+dpRequest.getPlanCode()+"%'";
		}
		if(dpRequest.getPlanName()!=null&&!"".equals(dpRequest.getPlanName())){
			sql =sql+" AND P.PLAN_NAME LIKE '"+dpRequest.getPlanName()+"%'";
		}
		if(dpRequest.getPlanType()!=null&&!"".equals(dpRequest.getPlanType())){
			sql =sql+" AND P.PLAN_TYPE = '"+dpRequest.getPlanType()+"'";
		}

		List<Map<String, Object>> r = DAO.queryForList(sql);

		return r;
	}

}
