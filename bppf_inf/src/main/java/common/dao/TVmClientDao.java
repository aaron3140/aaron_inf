package common.dao;

import java.util.List;

import common.entity.VmClient;
import common.entity.VmClientRowMapper;
import common.entity.VmClientRowMapperSecond;
import common.utils.SpringContextHelper;

public class TVmClientDao {
	public static BaseDao DAO = SpringContextHelper.getTvsBaseDaoBean();
	
	public static final String CLIENT_STYLE_NOR = "NORMAL";
	
	public static final String CLIENT_STYLE_LITE = "LITE";
	
	public static final String CLIENT_STYLE_DIFF = "DIFFNET";
	
	/**
	 * 客户端版本管理接口
	 * @version 1.00
	 * @history: 2011-03-23 下午04:55:16 [create]
	 * @author LeYi Tang 唐乐毅
	 * @param imsi
	 * @param currentVersion
	 * @param system
	 * @param sysVersion
	 * @param phone
	 * @return
	 */
	/*@SuppressWarnings("unchecked")
	public static List<VmClient> getLatestClient(String system, String sysVersion, String phone, String clientStyle) {
		StringBuffer sql = new StringBuffer();
		sql.append("select b.FILE_ID, b.version, b.client_id, b.IS_FORCEUP from ")
		.append("(select maxvercheck(VERSION) max_ver,  CLIENTTYPE_ID from T_VM_CLIENT ")
		.append(" where CLIENTTYPE_ID in ")
		.append("(select CLIENTTYPE_ID from T_VM_CLIENTTYPE ")
		.append(" where ")
		.append(" ';'||TERMMODEL_IDLIST||';' like ")
		.append(" (select '%;'||TERMMODEL_ID||';%' from T_VM_TERMMODEL where MODEL = '")
		.append(phone)
		.append("') ")
		.append(" and ';'||TERMOS_IDLIST||';' like (select '%;'||TERMOS_ID||';%' from T_VM_TERMOS")
		.append(" where OS = '")
		.append(system)
		.append("' and OS_VER = '")
		.append(sysVersion)
		.append("')) and stat = 'S0P'")
		.append(" and client_style = '")
		.append(clientStyle)
		.append("' group by CLIENTTYPE_ID) a left join T_VM_CLIENT b")
		.append(" on a.max_ver = b.version and a.CLIENTTYPE_ID = b.CLIENTTYPE_ID");
		return DAO.query(sql.toString(), new VmClientRowMapper());
	}*/
	
	/**
	 * 客户端版本管理接口
	 * @version 1.00
	 * @history: 2011-03-23 下午04:55:16 [create]
	 * @author LeYi Tang 唐乐毅
	 * @param imsi
	 * @param currentVersion
	 * @param system
	 * @param sysVersion
	 * @param phone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<VmClient> getLatestClient(String system, String sysVersion, String phone, String clientStyle, String productNo) {
		StringBuffer sql = new StringBuffer();
		sql.append("select b.FILE_ID, b.version,b.version_desc, b.client_id, b.IS_FORCEUP ,(select to_char(file_size) from t_bap_upfile  where file_id = b.FILE_ID) as FILE_SIZE from ")
		.append("(select maxvercheck(VERSION) max_ver,  CLIENTTYPE_ID from T_VM_CLIENT ")
		.append(" where CLIENTTYPE_ID in ")
		.append("(select CLIENTTYPE_ID from T_VM_CLIENTTYPE ")
		.append(" where ")
		.append(" ';'||TERMMODEL_IDLIST||';' like ")
		.append(" (select '%;'||TERMMODEL_ID||';%' from T_VM_TERMMODEL where MODEL = '")
		.append(phone)
		.append("') ")
		.append(" and ';'||TERMOS_IDLIST||';' like (select '%;'||TERMOS_ID||';%' from T_VM_TERMOS")
		.append(" where OS = '")
		.append(system)
		.append("' and OS_VER = '")
		.append(sysVersion)
		.append("')) and stat = 'S0P'")
		.append(" and client_style = '")
		.append(clientStyle)
		.append("' and UP_OBJ in( 'COM'/*,(select VIP_LEV from t_cum_info where cust_code = '"+productNo+"')*/) ")
		.append(" group by CLIENTTYPE_ID) a left join T_VM_CLIENT b")
		.append(" on a.max_ver = b.version and a.CLIENTTYPE_ID = b.CLIENTTYPE_ID and b.client_style = '"+clientStyle+"'");
		return DAO.query(sql.toString(), new VmClientRowMapper());
	}
	
//	@SuppressWarnings("unchecked")
//	public static List<NewVmClient> getLatestClient(String appType, String appFileType) {
//		StringBuffer sql = new StringBuffer();
//		sql.append("c.*,t.CLIENTTYPE_NAME ");
//		sql.append("from T_VM_CLIENT c,T_VM_CLIENTTYPE t ");
//		sql.append("where c.CLIENTTYPE_ID=t.CLIENTTYPE_ID ");
//		sql.append("and c.STAT='S0P' and c.UP_OBJ='COM' ");
//		sql.append("and t.CLIENTTYPE_NAME='"+appType+"' ");
//		sql.append("and c.CLIENT_STYLE='"+appFileType+"' ");
//		sql.append("order by c.CREATE_DATE DESC ");
//		
//		return DAO.query(sql.toString(), new NewVmClientRowMapper());
//	}
	
	/**
	 * 客户端版本管理接口
	 * @version 1.00
	 * @history: 2011-03-23 下午04:55:16 [create]
	 * @author LeYi Tang 唐乐毅
	 * @param imsi
	 * @param currentVersion
	 * @param system
	 * @param sysVersion
	 * @param phone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<VmClient> queryCurClient(String currentVersion, String system, String sysVersion,
			String phone, String clientStyle) {
		StringBuffer sql = new StringBuffer();
		sql.append("select client_id,file_id,version,version_desc,stat,(select to_char(file_size) from t_bap_upfile  where file_id = b.FILE_ID) as FILE_SIZE from T_VM_CLIENT b where CLIENTTYPE_ID in")
		.append(" (select CLIENTTYPE_ID from T_VM_CLIENTTYPE")
		.append("  where ';'||TERMMODEL_IDLIST||';' like")
		.append(" (select '%;'||TERMMODEL_ID||';%' from T_VM_TERMMODEL where MODEL = '")
		.append(phone)
		.append("') ")
		.append(" and ';'||TERMOS_IDLIST||';' like")
		.append(" (select '%;'||TERMOS_ID||';%' from T_VM_TERMOS")
		.append(" where OS = '")
		.append(system)
		.append("' and OS_VER = '")
		.append(sysVersion)
		.append("')) and version = '")
		.append(currentVersion)
		.append("'  and client_style = '")
		.append(clientStyle)
		.append("' ");
		
		return DAO.query(sql.toString(), new VmClientRowMapperSecond());
	}
	
	public static int checkIsExist(String phone,String system, String sysVersion){
		StringBuffer sql = new StringBuffer();
		sql.append("select count(1) from T_VM_CLIENTTYPE")
        .append(" where ';'||TERMMODEL_IDLIST||';' like  (select '%;'||TERMMODEL_ID||';%'")
        .append(" from T_VM_TERMMODEL where MODEL = '")
        .append(phone)
        .append("')")
        .append(" and ';'||TERMOS_IDLIST||';' like (select '%;'||TERMOS_ID||';%' from T_VM_TERMOS where OS = '")
        .append(system)
        .append("' and OS_VER = '")
        .append(sysVersion)
        .append("')");
        return DAO.queryForInt(sql.toString());
	}
}
