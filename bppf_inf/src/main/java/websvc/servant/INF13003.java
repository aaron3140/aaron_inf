package websvc.servant;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import svcPostSocket.SocketSrvConfig;

import com.tisson.common.util.HttpUtils;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.SignBankManage;
import common.service.TInfOperInLogManager;
import common.utils.MathTool;
import common.utils.PrivConstant;
import common.utils.SignUtil;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf12039Request;
import common.xml.dp.DpInf13003Request;
import common.xml.dp.DpInf13003Response;
import common.xml.dp.RankListDetailRequest;
import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF13003 {

	public static String svcInfName = "INF13003";

	private static final Log logger = LogFactory.getLog(INF13003.class);

	public static String execute(String in0, String in1) {

		DpInf13003Request dpRequest = null;
		RespInfo respInfo = null; // 返回信息头
		
		TInfOperInLog tInfOperInLog = null;
		String svcCode = WebSvcTool.getSvcCode(in0);
		DpInf13003Response resp = null;

		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			dpRequest = new DpInf13003Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode()); // 返回信息头

			// 插入信息到入站日志表
			SagManager sagManager = new SagManager();
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(),
					dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML",
					"custCode", dpRequest.getCustCode(), "staffCode", dpRequest.getStaffCode(), "S0A");

			TInfOperInLogManager manager = new TInfOperInLogManager();
			// 判断插入是否成功
			if (tInfOperInLog != null) {
				boolean flag = manager.selectTInfOperInLogByKeep(dpRequest
						.getKeep());
				// 判断流水号是否可用
				if (flag) {
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);
					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
							INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}
			
			// 校验客户编码和用户名是否匹配
			TCumInfoDao cumInfoDao = new TCumInfoDao();
			String custCodeByStaff = cumInfoDao.getCustCodeByStaff(dpRequest
					.getStaffCode());
			if (!StringUtils.equals(custCodeByStaff, dpRequest.getCustCode())) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST,
						INFErrorDef.CUSTCODE_NOT_MATCH_STAFF_DESC);
			}

			// 判断有无交易查询权限
			List<?> privList = PayCompetenceManage.payFunc(dpRequest
					.getCustCode(), dpRequest.getChannelCode());
			boolean re = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if (PrivConstant.CLN_STA_DISPLAY.equals(str)) {
					re = true;
					break;
				}

			}

			if (!re) {
				throw new Exception("你没有排行榜查询权限");
			}

			// 业务组件
			SignBankManage manage = new SignBankManage();

			// 获取客户ID
			String custId = manage.getCustIdByCode(dpRequest.getCustCode());

			if (null == custId || "".equals(custId)) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST,
						INFErrorDef.CUSTCODE_NOT_EXIST_DESC);
			}


			RankListDetailRequest rankListDetailRequest = new RankListDetailRequest();
			rankListDetailRequest.setCustomerId(dpRequest.getCustCode());
			rankListDetailRequest.setInvokeId("INF");
			rankListDetailRequest.setRankListType(dpRequest.getRankListType());
			rankListDetailRequest.setSearchMonth(dpRequest.getSearchMonth());
			Map<String,String> scParam = SocketSrvConfig.getParam();
			String key = scParam.get("ODSSIGNKEY");
			key = key==null?"":key;	
			rankListDetailRequest.setSign(SignUtil.getSign(rankListDetailRequest, key));
			String json = JSONObject.fromObject(rankListDetailRequest).toString();
			
			String srvIP = scParam.get("ODSIP");
			srvIP = srvIP==null?"":srvIP;	
			String strPort = scParam.get("ODSPORT");
			Integer srvPort =Integer.parseInt(strPort==null?"-1":strPort);			
			String prjName = scParam.get("ODSPRJNAME");

			String rtnString = HttpUtils.queryJsonData("http://"+srvIP+":"+srvPort+"/" + prjName + "/queryRankListInfo", json);
			
			JSONObject jsonObject = JSONObject.fromObject( rtnString);  
			
			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
					dpRequest.getKeep(), dpRequest.getIp(), svcCode, "",
					"000000", "S0A");
			
			String retCode = jsonObject.getString("retCode");
			String retMsg = jsonObject.getString("retMsg");
			
			if(retCode.equalsIgnoreCase("100")){
				retCode = "000000";
			}else{
				retCode = "000" + retCode;
			}

			// 返回结果
			resp = new DpInf13003Response();

			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS", 
					retCode, retMsg, dpRequest.getCustCode(),jsonObject);

		} catch (XmlINFException spe) {

			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);

		} catch (Exception e) {

			return ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), id);
		}
	}


}
