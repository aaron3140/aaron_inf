package common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.BaseDao;
import common.entity.register.TSymStaff;
import common.utils.CreateConstant;
import common.utils.OrderConstant;
import common.utils.PasswordUtil;
import common.utils.PrtnUtil;
import common.utils.SpringContextHelper;
import common.xml.dp.DpInf02022Request;

public class RegisterManger {

	public BaseDao DAO = SpringContextHelper.getTBaseDaoBean();
	
	private static final Log log = LogFactory.getLog(RegisterManger.class);

	/**
	 * @param custCode
	 * @param
	 * @return
	 */
	public List getAgentByArea(String areaCode, String pDline) {
		String sql = " select p.prtn_code as AGENTCODE,p.prtn_name as AGENTNAME,p.prtn_type as AGENTTYPE from t_pnm_partner p where p.prtn_type = 'PT901' and ( area_code LIKE '";
		StringBuilder sb = new StringBuilder();
		String area = areaCode.substring(2, 4);
		if(area!=null&&area.equals("00")){
			sb.append(areaCode).append("')");
		}else{
			String proarea = areaCode.substring(0, 2)+"0000";
			String city = areaCode.substring(0, 4);
			sb.append(proarea).append("' or area_code LIKE '"+city+"%')");
		}
		
		sql = sql + sb.toString()+" and prtn_id in (select f.prtn_id from t_cum_info f where f.cust_id in (select t.cust_id from t_cum_attr t where t.attr_id = '4224' ";
		if (pDline != null && !"".equals(pDline)) {
			StringBuilder sb2 = new StringBuilder();
			sb2.append(" and (").append(attrLike(pDline.split("\\|")));
			sb2.append(") AND t.cust_id in (select tt.cust_id from t_cum_grprela a,t_cum_info tt where a.cust_id = tt.cust_id and a.is_create = 'Y')))");
			sql = sql + sb2.toString();
		} else {
			sql = sql
					+ "and (t.value1 like '%100%' or t.value1 like '%104%') AND t.cust_id in (select tt.cust_id from t_cum_grprela a,t_cum_info tt where a.cust_id = tt.cust_id and a.is_create = 'Y')))";
		}

		log.info("代理商查询 ："+sql);
		List list = DAO.queryForList(sql);
		return list;
	}

	private String attrLike(String[] pdArray) {

		StringBuilder sb = new StringBuilder();

		for (int i=0;i<pdArray.length;i++) {
			
			log.info("组装参数："+pdArray[i]+"	"+i);

			sb.append(" t.value1 like '%").append(pdArray[i]).append("%'");

			sb.append(" or");
		}

		return sb.substring(0, sb.length() - 2);
	}

	@SuppressWarnings("unchecked")
	public List<String> findRoleList(String pdStr) {

		String sql = "select to_char(r.role_id) from t_sym_role r where r.role_layer = (select s.param_val from t_sym_sysparam s where s.param_code = 'BPPF_ROLELAYER_ADMIN' and s.stat = 'S0A')"
				+ " and r.role_seq in (select ppr.rela_obj from t_pdm_pdline_rela ppr where ppr.pdline_id in ("
				+ pdStr + ") and ppr.rela_type = 'ROLE_SEQ')";

		List<String> r = (List<String>) DAO.queryForList(sql, String.class);

		return r;
	}

	public Long getOrgByPrtn(Long ptrnId) {

		String sql = "  SELECT ORG_ID FROM t_cum_priv WHERE prtn_id =(SELECT PRTN_ID FROM t_pnm_partner WHERE PRTN_ID=? )";

		Long r = DAO.queryForLong(sql, new Object[] { ptrnId });

		return r;
	}

	public String getCustId(String custCode) {

		String sql = "select cust_id from t_cum_info where cust_code=? ";

		String r = (String) DAO.queryForObject(sql, new Object[] { custCode },
				String.class);

		return r;
	}

	public Long getAgentByPtrnCode(String prtnCode) {

		String sql = "select p.prtn_id from t_pnm_partner p where p.prtn_type='PT403' start with p.prtn_code=? connect by prior p.parent_id=p.prtn_id";

		Long r = (Long) DAO.queryForObject(sql, new Object[] { prtnCode },
				Long.class);

		return r;
	}

	public boolean isExistStaffCode(String staffCode) {

		String sql = "SELECT count(1) FROM T_SYM_STAFF WHERE STAFF_CODE=?";

		int r = DAO.queryForInt(sql, new Object[] { staffCode });

		return r > 0 ? true : false;
	}

	public Map findCustByPtrnId(String ptrnCode) {

		String sql = "select CUST_CODE,CUST_NAME FROM T_Cum_Info WHERE prtn_id =(select prtn_id from T_Pnm_Partner where prtn_code=?)";

		Map r =  DAO.queryForMap(sql, new Object[] { ptrnCode });

		return r;
	}
	
	public Long findGroupNo(String grpType,String cust_name) {

		String sql = "select distinct g.grp_id from t_cum_group g where g.grp_type = ? " +
				"and g.grp_id in (select r.grp_id from t_cum_grprela r where r.cust_id in " +
				"(select i.cust_id from t_cum_info i where i.cust_name = ?)  and r.is_create = 'Y')";

		Long r =  DAO.queryForLong(sql, new Object[] { grpType, cust_name});

		return r;
	}

	/**
	 * 获取企业功能权限
	 * 
	 * @param session
	 * @param pdStr
	 * @param custId
	 * @return
	 * @throws Exception
	 */
	public List<String> findFuncprivsByCust(String pdline, Long prtnId) {
		String sql = "select m.attr_id " + "from (select DISTINCT p.oper_mode,"
				+ "fp.attr_id," + "fp.sub_attr_id," + "fp.func_name,"
				+ "fp.ext_flag," + "fp.show_seq" + " from t_sym_funcpriv fp"
				+ " left join t_sym_priv p"
				+ " on fp.priv_id = p.priv_id, t_pdm_pdline_rela ppr"
				+ " where ppr.rela_type = 'FUNCPRIV'"
				+ " and p.oper_mode is not null" + " and ppr.pdline_id in ("
				+ pdline + ")" + " and fp.func_id = ppr.rela_obj"
				+ " and fp.stat = 'S0A') m," + " t_cum_attr a"
				+ " where m.sub_attr_id = a.attr_id" + " and a.cust_id in "
				+ " (select cust_id" + " from t_cum_info"
				+ " where prtn_id = ? and stat = 'S0A')"
				+ " order by m.oper_mode, m.show_seq";

		List<String> r = (List<String>) DAO.queryForList(sql,
				new Object[] { prtnId }, String.class);

		return r;
	}

	public List<String> findFuncId(List<String> funcprivs) {

		List<String> r = new ArrayList<String>();

		if (funcprivs != null && funcprivs.size() > 0) {
			String attrStr = "";
			for (String attrId : funcprivs) {
				attrStr += attrId + ",";
			}
			attrStr = attrStr.substring(0, attrStr.length() - 1);

			String sql = "select func_id from t_sym_funcpriv where attr_id in ("
					+ attrStr + ")";

			r = (List<String>) DAO.queryForList(sql, String.class);

		}
		return r;

	}

	public void builPartner(String parentId, String orgId) {

		DAO.builPartner(parentId, orgId);
	}

	/**
	 * 判断商户名是否已存在
	 * 
	 * @see
	 */
	public boolean checkPrtnName(String prtnName) {

		String sql = "select count(1) from t_pnm_partner  t where t.prtn_name=?";
		int r = DAO.queryForInt(sql, new Object[] { prtnName });

		return r > 0 ? true : false;
	}

	/**
	 * 判断商户编码是否已存在
	 * 
	 * @see
	 */
	public boolean checkCustCode(String custCode) {

		String sql = "select count(1) from t_cum_info t where t.cust_code=?";
		int r = DAO.queryForInt(sql, new Object[] { custCode });

		return r > 0 ? true : false;

	}

	public void delCusInfo(String prtnId, String orgId) {

		String sql = "BEGIN DELETE FROM T_CUM_INFO WHERE PRTN_ID=" + prtnId
				+ " ;" + "DELETE FROM T_PNM_PARTNER WHERE PRTN_ID=" + prtnId
				+ " ; " + "DELETE FROM T_SYM_ORG WHERE ORG_ID=" + orgId + " ; "
				+ "DELETE FROM T_CUM_PRIV WHERE PRTN_ID=" + prtnId + " ; END;";

		DAO.update(sql);

	}

	public Long addOrg(String parentPId, String areaCode, String orgName) {

		Long id = DAO.getLongPrimaryKey("SQ_SYM_ORGID");

		String orgCode = PrtnUtil.getOrgCode(areaCode);

		// 根据上级合作伙伴id查找上级机构

		String sql = "select t.ORG_ID from T_CUM_PRIV t where t.PRTN_ID=? and t.STAT=?";

		Long orgParentId = DAO.queryForLong(sql, new Object[] { parentPId,
				"S0A" });

		DAO.addOrg(id.toString(), orgCode, orgName, areaCode, orgParentId
				.toString());

		return id;

	}

	public Long addPartner(String prtnName, String ptrnCode, String regType,
			String areaCode, String parentId) {

		Long id = DAO.getLongPrimaryKey("SQ_PNM_PRTN");
		DAO.addPartner(id.toString(), prtnName, ptrnCode, regType, areaCode,
				parentId);

		return id;
	}

	public void tran_AaveAttr(String custId, TSymStaff staff,
			List<String> roles, List<String> func, List<String> funcId,
			List<Object[]> parm) throws Exception {

		DAO.tran_AaveAttr(custId, staff, roles, func, funcId, parm);
	}

//	String oMsg = "";
//
//	public String custRollBack(final long prtnId) {
//		String sql = "{call P_CUM_DELALLINFO(?,?)}";
//
//		String o_Msg;
//
//		o_Msg = (String) DAO.getTemplate().execute(sql,
//				new CallableStatementCallback() {
//					@Override
//					public String doInCallableStatement(CallableStatement cs)
//							throws SQLException, DataAccessException {
//						cs.setLong(1, prtnId);
//						cs.registerOutParameter(2, Types.INTEGER);// 注册返回参数类型
//						cs.executeUpdate();
//
//						return null;
//					}
//				});
//
//		System.out.println("!!o_Msg: " + o_Msg);
//
//		return o_Msg;
//
//	}

	/**
	 * 创建账号 lizy
	 * 
	 * @param staff
	 * @param custCode
	 * @param newOrgId
	 * @return
	 * @throws Exception
	 */
	public TSymStaff addSymStaff(DpInf02022Request dpRequest)
			throws Exception {

		TSymStaff staff = new TSymStaff();

		String staff_aux = dpRequest.getCustName().split("@")[0];

		staff_aux = staff_aux.length() > 17 ? staff_aux.substring(0, 17)
				: staff_aux;

		if (isExistStaffCode(staff_aux)) {
			for (int i = 1; i < 999; i++) {
				String _seq = "";
				if (i < 10)
					_seq = "00" + i;
				else if (i < 100)
					_seq = "0" + i;
				else
					_seq = "" + i;

				if (!isExistStaffCode(staff_aux + _seq)) {
					staff_aux += _seq;
					break;
				}
			}
		}

		String pwd = PasswordUtil.generate();
		staff.setPwd(pwd);
		String password = PasswordUtil.encryptPwd(pwd, staff_aux);
		staff.setPassword(password);
		
		String clPwd = PasswordUtil.generateP();
		staff.setClPwd(clPwd);
		String payPwd = PasswordUtil.encryptPwd(clPwd, staff_aux);
		staff.setPayPwd(payPwd);
		
		
		staff.setStaffId(staff_aux);
		staff.setStaffCode(staff_aux);
		staff.setStaffName(dpRequest.getApplyer());
		staff.setMobile(dpRequest.getCustName());
		staff.setEmail(dpRequest.getEmail());
		staff.setStat(OrderConstant.S0A);
		staff.setPwdStat(OrderConstant.S0A);
		staff.setSex("M");
		staff.setPwdCtrl("0000000000");
		staff.setPwdTrytimes(CreateConstant.PASSWORD_TRYTIMES);
		staff.setPwdErrtimes(0l);
		staff.setExtFlag(CreateConstant.SYM_STAFF_EXTFLAG);
		
		staff.setCertType("01");
		staff.setCertNbr(dpRequest.getCertNo());

		return staff;
	}

	public static List<Object[]> attrInfo(DpInf02022Request dpRequest,
			String custId, String plines) {

		List<Object[]> dataSet = new ArrayList<Object[]>();

		// 企业地址
		Object[] o1 = new Object[3];
		o1[0] = custId;
		o1[1] = CreateConstant.ATTRID_ADDRESS;
		o1[2] = dpRequest.getEnterAddress();
		dataSet.add(o1);

		// 所属行业
		Object[] o2 = new Object[3];
		o2[0] = custId;
		o2[1] = CreateConstant.ATTRID_INDCODE;
		o2[2] = dpRequest.getTrade();
		dataSet.add(o2);

		// 营业执照编码
		if(dpRequest.getBusinessLicence()!=null||!"".equals(dpRequest.getBusinessLicence())){
			
			Object[] o3 = new Object[3];
			o3[0] = custId;
			o3[1] = CreateConstant.ATTRID_CUM_ICBP;
			o3[2] = dpRequest.getBusinessLicence();
			dataSet.add(o3);
		}

		// 业务联系人
		Object[] o4 = new Object[3];
		o4[0] = custId;
		o4[1] = CreateConstant.ATTRID_INDCONTACT_MAN;
		o4[2] = dpRequest.getApplyer();
		dataSet.add(o4);

		// 证件号码
		Object[] o5 = new Object[3];
		o5[0] = custId;
		o5[1] = CreateConstant.ATTRID_IDENTITY;
		o5[2] = dpRequest.getCertNo();
		dataSet.add(o5);

		// 电子邮箱
		Object[] o6 = new Object[3];
		o6[0] = custId;
		o6[1] = CreateConstant.ATTRID_EMAIL2;
		o6[2] = dpRequest.getEmail();
		dataSet.add(o6);

		// 产品编码
		Object[] o = new Object[3];
		o[0] = custId;
		o[1] = CreateConstant.CUM_PDLINE_STR;
		o[2] = "," + plines + ",";
		dataSet.add(o);

		if ("PRT1002".equals(dpRequest.getRegType())) {
			// 企业法人
			Object[] o7 = new Object[3];
			o7[0] = custId;
			o7[1] = CreateConstant.ATTRID_ENT_PERSON;
			o7[2] = dpRequest.getEnterPerson();
			dataSet.add(o7);

			// 组织机构代码
			Object[] o8 = new Object[3];
			o8[0] = custId;
			o8[1] = CreateConstant.ATTRID_END_AGENT_NO;
			o8[2] = dpRequest.getEnterOrgCode();
			dataSet.add(o8);
		}

		// 联系手机
		Object[] o9 = new Object[3];
		o9[0] = custId;
		o9[1] = CreateConstant.ATTRID_MOD_NBR2;
		o9[2] = dpRequest.getCustName();
		dataSet.add(o9);

		// 资金管理模式
		Object[] o10 = new Object[3];
		o10[0] = custId;
		o10[1] = CreateConstant.PNM_BANKROLLTYPE;
		o10[2] = "BT1001";
		dataSet.add(o10);

		// 结算方式
		Object[] o11 = new Object[3];
		o11[0] = custId;
		o11[1] = CreateConstant.PNM_REWARDTYPE;
		if ("PRT1002".equals(dpRequest.getRegType())) {
			
			o11[2] = "1003";
		}else{
			
			o11[2] = "1002";
		}
		dataSet.add(o11);

		// VIP等级
		Object[] o12 = new Object[3];
		o12[0] = custId;
		o12[1] = CreateConstant.CUM_VIPLEV;
		o12[2] = "COM";
		dataSet.add(o12);
		
		// 税金标识
		Object[] o13 = new Object[3];
		o13[0] = custId;
		o13[1] = CreateConstant.CUM_TAXESFLAG;
		if ("PRT1002".equals(dpRequest.getRegType())) {
			
			o13[2] = "false";
		}else{
			
			o13[2] = "true";
		}
		
		dataSet.add(o13);
		
		// 多用户
		Object[] o14 = new Object[3];
		o14[0] = custId;
		o14[1] = CreateConstant.MUTI_ACCT;
		o14[2] = "true";
		dataSet.add(o14);
		
		//税务登记证
		if(dpRequest.getReveNbr()!=null||!"".equals(dpRequest.getReveNbr())){
			
			Object[] o15 = new Object[3];
			o15[0] = custId;
			o15[1] = CreateConstant.ATTRID_REVE_NBR;
			o15[2] = dpRequest.getReveNbr();
			dataSet.add(o15);

		}
		
		return dataSet;
	}

}
