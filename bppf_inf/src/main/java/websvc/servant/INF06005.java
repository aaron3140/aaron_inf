package websvc.servant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumInfoDaoTemp;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.service.TransManage;
import common.utils.ChannelCode;
import common.utils.MathTool;
import common.utils.SagUtils;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf06005Request;
import common.xml.dp.DpInf06005Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author 邱亚建<br>
 *         时间：2013-7-1 上午11:39:21<br>
 *         类描述：个人账户余额查询接口
 */
public class INF06005 {
	private static final Log logger = LogFactory.getLog(INF06005.class);

	public static String svcInfName = "INF06005";

	public static String executeForMD5(String in0, String in1) {

		DpInf06005Response resp = new DpInf06005Response();

		RespInfo respInfo = null; // 返回信息头

		String md5Key = null;

		try {

			DpInf06005Request dpRequest = new DpInf06005Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			// 客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(dpRequest.getChannelCode())) {

				String tokenValidTime = TSymSysParamDao.getTokenValidTime();

				md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest
						.getStaffCode(), tokenValidTime);

				dpRequest.verifyByMD5(dpRequest.xmlSubString(in1), md5Key);

				TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest
						.getStaffCode());

			}

			String oldXml = execute(in0, in1);

			return ResponseVerifyAdder.pkgForMD5(oldXml, md5Key);

		} catch (Exception e) {
			String oldXml = ExceptionHandler.toXML(new XmlINFException(resp, e,
					respInfo), null);

			return ResponseVerifyAdder.pkgForMD5(oldXml, null);
		}

	}

	public static String execute(String in0, String in1) {
		DpInf06005Request dpRequest = null;

		DpInf06005Response resp = new DpInf06005Response();

		RespInfo respInfo = null;

		String responseCode = "";

		logger.info("INF06005请求参数：：" + in1);

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String keep = "";// 获取流水号

		String ip = "";

		String svcCode = WebSvcTool.getSvcCode(in0);

		String tmnNum = null;

		INFLogID infId = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			dpRequest = new DpInf06005Request(in1);

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
				
			} else if (ChannelCode.WS_CHANELCODE.equals(dpRequest.getChannelCode())){
				if (dpRequest.getTmnNumNo().length() < 12) {
					String tmnNumNo = transManage.getTermNumNoByExt(dpRequest.getTmnNumNo(),dpRequest.getCustCode());
					if(tmnNumNo!=null&&!"".equals(tmnNumNo)){
						dpRequest.setTmnNumNo(tmnNumNo);
					}else{
						throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.TMNNUMNO_NOT_MATCH_DESC);
					}
				}
			}

			// 验证客户编码和终端号是否对应
//			if(!ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())){
//				boolean flag = transManage.isCustCodeMatchTermNumNo(dpRequest.getCustCode(), dpRequest.getTmnNumNo());
//				if (!flag) {
//					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.CUSTCODE_NOT_MATCH_TERMNUMNO_DESC);
//				}
//			}

			String tradeSeq = SagUtils.getSeqNbr("yyyyMMddhhmmssSSS", 8);
			PackageDataSet ds = null;
			ds = queryAccountBalance(dpRequest,tradeSeq);// 查询账户余额

			String resultCode = (String) ds.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			String responseDesc = null;
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			}

			String systemNo = ds.getByID("6901", "690");
			
			String accountBalance = (String) ds.getParamByID("6923", "692").get(0);//账户余额
			
			//元转分
			accountBalance = MathTool.yuanToPoint(accountBalance);
			
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc, dpRequest.getTmnNumNo(), tradeSeq,
					systemNo, accountBalance, dpRequest.getRemark1(), dpRequest.getRemark2());
		} catch (XmlINFException spe) {

//			if (tInfOperInLog != null) {
//				// 插入信息到出站日志表
//				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, spe.getMessage(), "S0A");
//			}
			spe.setRespInfo(respInfo);

			return ExceptionHandler.toXML(spe, infId);

		} catch (Exception e) {

//			if (tInfOperInLog != null) {
//				// 插入信息到出站日志表
//				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
//			}
			return ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), infId);

		}
	}

	/**
	 * 查询账户余额
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet queryAccountBalance(DpInf06005Request dpRequest,String tradeSeq) throws Exception {

		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String requestDate = df.format(date);
		df = new SimpleDateFormat("HHmmss");
		String requestTime = df.format(date);

		String accepTareaCode = dpRequest.getAccepTareaCode();
		if (accepTareaCode == null || "".equals(accepTareaCode)) {
			// 区域编码为对应的地市区域编码，不传默认取企业账户对应的区域编码
			accepTareaCode = TCumInfoDaoTemp.queryAreacodeByCustCode(dpRequest.getCustCode());// 所属区域编码
		}

		/**
		 * 调用SAG0001,完成查询操作
		 */
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", "PXC104");// 服务编码
		g675.put("6752", dpRequest.getChannelCode60To20());// 渠道号
		g675.put("6753", tradeSeq);// 流水号
		g675.put("6754", requestDate);// 发起请求日期，格式YYYYMMDD
		g675.put("6755", requestTime);// 发起请求时间，格式HH24MISS
		g675.put("6756", "INF");// 接口平台编码，INF:表示INF前置平台 UPPS:标识交易核心平台 GOS：运营管理门户
		g675.endRow();

		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", "03010010");// 业务编码
		g676.put("6762", "0001");// 产品编码
		g676.put("6763", accepTareaCode);// 受理区域编码
		g676.put("6764", dpRequest.getMerId());// 前向商户编码
		g676.put("6765", dpRequest.getTmnNum());// 前向商户终端号
		g676.endRow();

		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", dpRequest.getAcctCode());
		g680.put("6802", "");
		g680.put("6803", "");
		g680.put("6804", "");
		g680.put("6805", "");
		g680.put("6806", "");
		g680.put("6807", "");
		g680.put("6808", dpRequest.getSearchDate());//查询时间(YYYYMMDDHH24MISS)
		g680.endRow();

		// 组成数据包,调用SAG0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SAG", "SAG0001", g675, g676, g680);

		// 返回结果
		return dataSet;

	}
}
