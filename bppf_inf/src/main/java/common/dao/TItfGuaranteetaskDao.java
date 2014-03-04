package common.dao;

import common.entity.TItfGuaranteetask;
import common.utils.SpringContextHelper;

public class TItfGuaranteetaskDao {
public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	public boolean insert(TItfGuaranteetask task) {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("insert into t_itf_guaranteetask ")
			.append("(TASK_ID, URL, PARAM, EXPECTED_VALUE, CREATE_TIME, END_TIME, RUNTIMES, LAST_RUNTIME, STAT,NEXT_RUNTIME)")
			.append("values (SQ_ITF_GUARANTEETASK.NEXTVAL, '").append(task.getUrl())
			.append("', '").append(task.getParam()).append("', '").append(task.getExpectedValue())
			.append("',to_date('").append(task.getCreateTime()).append("','yyyy-mm-dd hh24:mi:ss'),to_date('").append(task.getEndTime())
			.append("','yyyy-mm-dd hh24:mi:ss'),").append(task.getRuntimes()).append(",to_date('").append(task.getLastRuntime())
			.append("','yyyy-mm-dd hh24:mi:ss'),'").append(task.getStat()).append("',to_date('").append(task.getNextRuntime()).append("','yyyy-mm-dd hh24:mi:ss'))");    
		
		int count = DAO.insert(sb.toString());
		
		if(count > 0) return true;
		return false ;
	}
}
