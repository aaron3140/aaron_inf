package websvc.servant;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.ListOrderedMap;

import common.dao.BaseDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TOppPreOrderDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.ChannelCode;
import common.utils.SpringContextHelper;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02005Request;
import common.xml.dp.DpInf02005Responset;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 转账收款名单查询接口 
 * 
 * 
 */
public class INF02005 {
	public static String svcInfName = "02005";

	public static String execute(String in0, String in1) {
		//Long pk = null;
		DpInf02005Request request  = null;
		RespInfo respInfo = null;				// 返回信息头

		String custCode = null; // 商户编码
		String tmnNum = null;	//受理终端号
		String channelCode= "";
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode1 = "";
		String keep = "";//获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		DpInf02005Responset resp = null;
		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);
		try {
			respInfo = new RespInfo(in1,"20");				// 返回信息头
			request = new DpInf02005Request(in1);
			channelCode = request.getChannelCode();
			custCode = request.getCustCode();
			tmnNum = request.getTmnNum();
			keep = request.getKeep();
			ip = request.getIp();
			
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "agentCode"
					, custCode, "", "", "S0A");
			TInfOperInLogManager manager = new TInfOperInLogManager();
			//判断插入是否成功
			if(tInfOperInLog!=null){
				boolean flag = manager.selectTInfOperInLogByKeep(keep);
				//判断流水号是否可用
				if(flag){
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);
					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
							INFErrorDef.POSSEQNO_CONFLICT_REASON);
				}else{
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}
			
			boolean flag = false;
			List privList = PayCompetenceManage.payFunc(custCode);
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();
				if("cln_TransAcctQuery".equals(str)){
					flag = true;
				}
			}
			if (!flag) {
				throw new Exception("没有查询转账收款名单权限");
			}
			
			TOppPreOrderDao dao = new TOppPreOrderDao();
			List transAcctList = dao.getTransAcctList(custCode);
			if (transAcctList.size() < 1 ) {
				transAcctList = new ArrayList();
			}
			// 返回结果
			resp = new DpInf02005Responset();
			String xmlStr=resp.toXMLStr(respInfo.getReqWebsvrCode(),respInfo.getRespType(),respInfo.getKeep(),"SUCCESS", 
					"000000","成功",transAcctList);
			if (xmlStr==null||xmlStr.length()<1) {
				throw new Exception("获取信息出错");
			}
			return xmlStr;
		} catch (XmlINFException spe) {
			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode1, spe.getMessage(), "S0A");
			}
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode1, e.getMessage(), "S0A");
			}
			return ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), id);
		}
	}
	
	private static boolean setCumInfo(Hashtable<String,String> ht,String agentCode) {
		String [] keys={"ORGCODE","BUSSINESSLIC","LEGALREPRESENTATIVE","CONTACTER",
				"CONTACTPHONE","EMAIL","REGCERT","FINANCIALCONTACTER","FINANCIALPHONE","FINANCIALEMAIL","ADDR"};
		String [] attrIds={"2577","2582","2579","2589","2590","2591","2592","2580","2585","2586","2581"};
		
		BaseDao DAO = SpringContextHelper.getBaseDaoBean();
		String sql="";
		
		sql="select cust_id from t_cum_info a where a.cust_code= ? ";
		String custId =(String)DAO.queryForObject(sql, new Object[] {agentCode}, String.class);
		if (custId==null||custId.length()<1) {
			return false;
		}
		for (int i = 0; i < attrIds.length; i++) {
			ht.put(keys[i], "");
		}
		// 组织机构代码,业执照注册号,企业法人,业务联系人,联系手机,电子邮箱,税务登记证,财务联系人,财务联系手机,财务电子邮箱
	
		sql="select value1, attr_id from T_CUM_ATTR a where a.cust_id="+custId+" and a.attr_id in ('2577','2582','2579',"
			+ "'2589','2590','2591','2592','2580','2585','2586','2581')";
		List list = DAO.queryForList(sql);
		for (int j = 0, k = list.size(); j < k; j++) {
			Map map = (Map)list.get(j);
			String attrid = String.valueOf(map.get("attr_id"));
			String attrval = String.valueOf(map.get("value1"));
			if (attrid.equals(attrIds[0])) {
				ht.put(keys[0],attrval);
			}else if (attrid.equals(attrIds[1])) {
				ht.put(keys[0],attrval);
			}else if (attrid.equals(attrIds[2])) {
				ht.put(keys[2],attrval);
			}else if (attrid.equals(attrIds[3])) {
				ht.put(keys[3],attrval);
			}else if (attrid.equals(attrIds[4])) {
				ht.put(keys[4],attrval);
			}else if (attrid.equals(attrIds[5])) {
				ht.put(keys[5],attrval);
			}else if (attrid.equals(attrIds[6])) {
				ht.put(keys[6],attrval);
			}else if (attrid.equals(attrIds[7])) {
				ht.put(keys[7],attrval);
			}else if (attrid.equals(attrIds[8])) {
				ht.put(keys[8],attrval);
			}else if (attrid.equals(attrIds[9])) {
				ht.put(keys[9],attrval);
			}else if (attrid.equals(attrIds[10])) {
				ht.put(keys[10],attrval);
			}
		}
		
		// 所属行业
		sql="select * from (select b.dict_name from t_cum_attr a,t_sym_dict b"
			+" where a.value1=b.dict_id and b.dict_typeid='CUM_INDUSTRY'"
			+" and a.cust_id="+custId+" and a.attr_id=2588 and a.value1<>'12'"
			+" union"
			+" select a.value1 from t_cum_attr a where a.cust_id="+custId+" and a.attr_id=2604"
			+" and exists (select 1 from t_cum_attr where cust_id="+custId+" and attr_id=2588 and value1='12')) a where rownum<=1";
		String range=(String)DAO.queryForObject(sql,String.class);
		if (range==null) {
			range="";
		}
		ht.put("RANGE",range);
		
		ht.put("CONTACTADDR",getAllAreaName(agentCode));
		
		// 企业名称
//		sql="select max(b.prtn_name) prtn_name from t_cum_info a,t_pnm_partner b where a.prtn_id=b.prtn_id and a.cust_id="+custId;
//		String agentname=(String)DAO.queryForObject(sql,String.class);
//		if (agentname==null) {
//			agentname="";
//		}
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "2002");
		g002.put("0022", agentCode);
		g002.endRow();
		IServiceCall callerE = new ServiceCallImpl();
		PackageDataSet dataSet =null;
		try {
			dataSet = callerE.call("BIS","CUM0002", g002);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String agentname = dataSet.getByID("2003", "201");
		ht.put("AGENTNAME",agentname);
		
		return true;
	}
	
	private static String getAllAreaName(String custCode) {
		String sql = "select area_name from t_sym_area where stat = 'S0A'"
				+ " connect by area_code = prior parent_area start with area_code = (select max(area_code) from t_cum_info where cust_code='"
				+ custCode + "')";
		//
		BaseDao DAO = SpringContextHelper.getBaseDaoBean();
		List<ListOrderedMap> list=DAO.queryForList(sql);
		String result = "";
		int size;
		if(list != null && (size=list.size())>0){
			for(int i=size-1;i>=0;){
				result += list.get(i).get("AREA_NAME");
				if(i>0){
					result += "-";
				}
				i--;
			}
		}
		return result;
	}
	
//	private static boolean isEmpty(Object o) {
//		if (o==null) {
//			return true;
//		}
//		if (o instanceof String) {
//			if (((String) o).trim().length()<1) {
//				return true;
//			}
//		}
//		return false;
//	}
	
	public static String executeForMD5(String in0, String in1){
		DpInf02005Responset resp = new DpInf02005Responset();
		RespInfo respInfo = null;				// 返回信息头
		String md5Key = null;
		try {
			respInfo = new RespInfo(in1, "10");	
			DpInf02005Request dpRequest = new DpInf02005Request(in1);
			//客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(dpRequest.getChannelCode())) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				 md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest.getStaffCode(),
							tokenValidTime);
				dpRequest.verifyByMD5(dpRequest.xmlSubString(in1), md5Key);
				TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest.getStaffCode());
			}
			
			String oldXml = execute(in0, in1);
			
			return ResponseVerifyAdder.pkgForMD5(oldXml, md5Key);
		} catch (Exception e) {
			String oldXml= ExceptionHandler.toXML(new XmlINFException(
					resp, e, respInfo), null);
			
			return ResponseVerifyAdder.pkgForMD5(oldXml, null);
		}
	}
}
