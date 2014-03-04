package websvc.servant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TInfDcoperlogDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.service.TransManage;
import common.utils.ChannelCode;
import common.utils.PrivConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf06001Request;
import common.xml.dp.DpInf06001Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author 邱亚建 2013-4-7 下午04:28:08<br>
 *         TODO 终端签到接口
 * 
 */
public class INF06001 {
	private static final Log logger = LogFactory.getLog(INF06001.class);

	public static String svcInfName = "INF06001";

	public static String executeForMD5(String in0, String in1) {
		String oXml = execute(in0, in1);
		
		return ResponseVerifyAdder.pkgForMD5(oXml, null);
	}

	@SuppressWarnings("unchecked")
	public static String execute(String in0, String in1) {
		DpInf06001Request dpRequest = null;

		DpInf06001Response resp = new DpInf06001Response();

		RespInfo respInfo = null;

		String responseCode = "";

		logger.info("INF06001请求参数：：" + in1);

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String keep = "";// 获取流水号

		String ip = "";

		String svcCode = WebSvcTool.getSvcCode(in0);

		String tmnNum = null;

		INFLogID infId = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			dpRequest = new DpInf06001Request(in1);
			
			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			tmnNum = dpRequest.getTmnNum();

			keep = dpRequest.getKeep();

			ip = dpRequest.getIp();

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "", "", "", "", "S0A");
			TInfOperInLogManager manager = new TInfOperInLogManager();
			// 判断插入是否成功
			if (tInfOperInLog != null) {

				boolean flag = manager.selectTInfOperInLogByKeep(keep);
				// 判断流水号是否可用
				if (flag) {

					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);

					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE, INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}
			
			TransManage transManage = new TransManage();

			// IPOS处理
			if (ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())) {

				Map<String,String> map = transManage.getCustCodeByExtTermNumNo(dpRequest.getTmnNumNo());
				if(map!=null&&map.size()!=0){
					String custCode = map.get("CUST_CODE");
					String tmnNumNo = map.get("TERM_CODE");
					dpRequest.setCustCode(custCode);
					dpRequest.setTmnNumNo(tmnNumNo);
				}else{
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.CUSTCODE_NOT_MATCH_DESC);
				}
				/*String cust = transManage.getCustCodeByExtTermNumNo(dpRequest.getTmnNumNo());
				if (cust == null || "".equals(cust)) {
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.CUSTCODE_NOT_MATCH_DESC);
				} else {
					dpRequest.setCustCode(cust);
					dpRequest.setTmnNumNo(transManage.getTermNumNoByExt(dpRequest.getTmnNumNo()));

				}*/
			} else {
				if (dpRequest.getTmnNumNo().length() < 12) {
//					dpRequest.setTmnNumNo(transManage.getTermNumNoByExt(dpRequest.getTmnNumNo()));
					String tmnNumNo = transManage.getTermNumNoByExt(dpRequest.getTmnNumNo(),dpRequest.getCustCode());
					if(tmnNumNo!=null&&!"".equals(tmnNumNo)){
						dpRequest.setTmnNumNo(tmnNumNo);
					}else{
						throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.TMNNUMNO_NOT_MATCH_DESC);
					}
				}
			}
			//判断有无交易查询权限
			List privList = PayCompetenceManage.payFunc(dpRequest.getCustCode(), dpRequest.getChannelCode());
			boolean r = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if(PrivConstant.WS_TERMINAL_CHECK.equals(str)){
					r = true;
					break;
				}

			}
			
			if(!r){
				throw new Exception("你没有终端签到的权限");
			}
			
			// 验证客户编码和终端号是否对应
//			if(!ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())){
//				boolean flag = transManage.isCustCodeMatchTermNumNo(dpRequest.getCustCode(), dpRequest.getTmnNumNo());
//				if (!flag) {
//					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.CUSTCODE_NOT_MATCH_TERMNUMNO_DESC);
//				}
//			}
			
			PackageDataSet ds = null;

			ds = signIn(dpRequest);// 签到

			String resultCode = (String) ds.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			String responseDesc = null;
			// 返回结果为失败时，获取结果描述
			if (Long.valueOf(resultCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			}

			// String keyType = ds.getByID("8060", "805");// 密钥类别
			// String keyValue = ds.getByID("8061", "805");// 密钥值
			// String keyMacValue = ds.getByID("8062", "805");// 密钥MAC值
			ArrayList keyTypes = ds.getParamByID("8060", "805");
			ArrayList keyValues = ds.getParamByID("8061", "805");
			ArrayList keyMacValues = ds.getParamByID("8062", "805");

			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc,
					dpRequest.getOrderSeq(), dpRequest.getRemark1(), dpRequest.getRemark2(), dpRequest.getTmnNumNo(), keyTypes, keyValues,
					keyMacValues, dpRequest.getNetworkNo());
		} catch (XmlINFException spe) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, spe.getMessage(), "S0A");
			}
			spe.setRespInfo(respInfo);

			return ExceptionHandler.toXML(spe, infId);

		} catch (Exception e) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
			}
			return ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), infId);

		}
	}

	/**
	 * 调用TMN0017接口签到
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet signIn(DpInf06001Request dpRequest) throws Exception {

		/**
		 * 调用TMN0017,完成签到操作
		 */

		IParamGroup g801 = new ParamGroupImpl("801");// 终端基本信息
		g801.put("8031", dpRequest.getTmnNumNo());// 终端号
		g801.put("8059", dpRequest.getPsamCardNo());// Psam卡号或键盘密码
		g801.put("8060", dpRequest.getRandom());// 随机数,ET0001时必须
		g801.endRow();

		IParamGroup g802 = new ParamGroupImpl("802");// 控制信息
		g802.put("8003", dpRequest.getCondition());// 控制条件
		g802.put("8004", dpRequest.getEncryption());// 加密方式
		g802.put("8005", dpRequest.getOrderSeq());// 流水号
		g802.endRow();

		// 组成数据包,调用TMN0017接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("BIS", "TMN0017", g801, g802);
		return dataSet;
	}
}
