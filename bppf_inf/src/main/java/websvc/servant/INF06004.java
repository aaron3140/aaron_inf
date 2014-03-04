package websvc.servant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import common.utils.DateTime;
import common.utils.MathTool;
import common.utils.SagUtils;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf06004Request;
import common.xml.dp.DpInf06004Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author 邱亚建<br>
 *         时间：2013-6-14 上午11:56:08<br>
 *         类描述：充值账户校验接口
 */
public class INF06004 {
	private static final Log logger = LogFactory.getLog(INF06004.class);

	public static String svcInfName = "INF06004";

	public static String executeForMD5(String in0, String in1) {

		DpInf06004Response resp = new DpInf06004Response();

		RespInfo respInfo = null; // 返回信息头

		String md5Key = null;

		try {

			DpInf06004Request dpRequest = new DpInf06004Request(in1);

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
		DpInf06004Request dpRequest = null;

		DpInf06004Response resp = new DpInf06004Response();

		RespInfo respInfo = null;

		String responseCode = "";

		logger.info("INF06004请求参数：：" + in1);

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String keep = "";// 获取流水号

		String ip = "";

		String svcCode = WebSvcTool.getSvcCode(in0);

		String tmnNum = null;

		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			dpRequest = new DpInf06004Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			tmnNum = dpRequest.getTmnNum();

			keep = dpRequest.getKeep();

			ip = dpRequest.getIp();

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum,
					svcCode, "XML", "", "", "", "", "S0A");
			TInfOperInLogManager manager = new TInfOperInLogManager();
			// 判断插入是否成功
			if (tInfOperInLog != null) {

				boolean flag = manager.selectTInfOperInLogByKeep(keep);
				// 判断流水号是否可用
				if (flag) {

					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);

					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
							INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {

					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}

			TransManage transManage = new TransManage();

			// IPOS处理
			if (ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())) {

				Map<String, String> map = transManage
						.getCustCodeByExtTermNumNo(dpRequest.getTmnNumNo());
				if (map != null && map.size() != 0) {
					String custCode = map.get("CUST_CODE");
					String tmnNumNo = map.get("TERM_CODE");
					dpRequest.setCustCode(custCode);
					dpRequest.setTmnNumNo(tmnNumNo);
				} else {
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_NOT_MATCH_DESC);
				}

			} else if (ChannelCode.WS_CHANELCODE.equals(dpRequest.getChannelCode())){
				if (dpRequest.getTmnNumNo().length() < 12) {
					// dpRequest.setTmnNumNo(transManage.getTermNumNoByExt(dpRequest.getTmnNumNo()));
					String tmnNumNo = transManage.getTermNumNoByExt(dpRequest
							.getTmnNumNo(), dpRequest.getCustCode());
					if (tmnNumNo != null && !"".equals(tmnNumNo)) {
						dpRequest.setTmnNumNo(tmnNumNo);
					} else {
						throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
								INFErrorDef.TMNNUMNO_NOT_MATCH_DESC);
					}
				}
			}
			// 判断有无交易查询权限
//			List privList = PayCompetenceManage.payFunc(
//					dpRequest.getCustCode(), dpRequest.getChannelCode());
//			boolean r = false;
//			for (int i = 0; i < privList.size(); i++) {
//				Map map = (Map) privList.get(i);
//				String str = map.get("PRIV_URL").toString();
//
//				if (PrivConstant.WS_ACCOUNT_VAIL.equals(str)) {
//					r = true;
//					break;
//				}
//
//			}
//
//			if (!r) {
//				throw new Exception("你没有充值账户校验的权限");
//			}

			// 验证客户编码和终端号是否对应
			// if(!ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())){
			// boolean flag =
			// transManage.isCustCodeMatchTermNumNo(dpRequest.getCustCode(),
			// dpRequest.getTmnNumNo());
			// if (!flag) {
			// throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
			// INFErrorDef.CUSTCODE_NOT_MATCH_TERMNUMNO_DESC);
			// }
			// }

			String tradeSeq = SagUtils.getSeqNbr("yyyyMMddhhmmssSSS", 8);
			
			PackageDataSet ds = null;
			
			//金额分转元
			dpRequest.setReamount(MathTool.pointToYuan(dpRequest.getReamount()));
			
			List<String> list = new ArrayList<String>();
			
			if("0".equals(dpRequest.getVerify())){
				
				ds = validateAccount(dpRequest, tradeSeq);
				
			}else if("1".equals(dpRequest.getVerify())){
				
				ds = sag0001(dpRequest,"03010020","0042");
				
			}else if("2".equals(dpRequest.getVerify()) || "3".equals(dpRequest.getVerify())){
				
				ds = authentificationFixBroadband(dpRequest, "01010015", "04040900");
				
				list = detailMessage(ds);
			}
			else{
				
				ds = validateAccount(dpRequest, tradeSeq);
			}
			
			String resultCode = (String) ds.getParamByID("0001", "000").get(0);
			
			responseCode = resultCode;
			String responseDesc = null;
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			}

			String systemNo = ds.getByID("6901", "690");
			
			// String orderSeq = "";
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, dpRequest.getTmnNumNo(),
					tradeSeq, systemNo, dpRequest.getRemark1(), dpRequest
							.getRemark2(), list);
		} catch (XmlINFException spe) {

//			if (tInfOperInLog != null) {
//				// 插入信息到出站日志表
//				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
//						keep, ip, svcCode, responseCode, spe.getMessage(),
//						"S0A");
//			}
			spe.setRespInfo(respInfo);

			return ExceptionHandler.toXML(spe, infId);

		} catch (Exception e) {

//			if (tInfOperInLog != null) {
//				// 插入信息到出站日志表
//				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
//						keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
//			}
			return ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), infId);

		}
	}

	/**
	 * 调用SAG0001接口充值
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet sag0001(DpInf06004Request dpRequest,
			String actionCode, String proudCode) throws Exception {

		/**
		 * 调用SAG0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", "GTC201");// 
		g675.put("6752", dpRequest.getChannelCode60To20());// 

		g675.put("6753", dpRequest.getTmnNum()
				+ SagUtils.getSeqNbr("yyyyMMddhhmmss", 4));// 
		g675.put("6754", DateTime.nowDate8Bit());// 
		g675.put("6755", DateTime.nowTime6Bit());// 
		g675.put("6756", "INF");// 

		g675.endRow();

		// 订单费用信息
		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", actionCode);// 
		g676.put("6762", proudCode);// 
		g676.put("6763", "000000");// 
		g676.put("6764", dpRequest.getMerId());// 
		g676.put("6765", dpRequest.getTmnNum());// 

		g676.endRow();

		// 业务单信息
		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", dpRequest.getAcctCode());// 

		g680.put("6802", "0");// 

		g680.put("6803", "");// 

		g680.endRow();

		// 业务单费用信息
		IParamGroup g682 = new ParamGroupImpl("682");

		g682.put("6820", "G002");//
		g682.put("6821", "卡类型");// 
		g682.put("6822", "01");// 
		g682.put("6823", "001");// 

		g682.endRow();

		// 组成数据包,调用SAG0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SAG", "SAG0001", g675, g676,
				g680, g682);

		// 返回结果
		return dataSet;

	}

	
	/**
	 * 充值账户校验
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet validateAccount(DpInf06004Request dpRequest,
			String tradeSeq) throws Exception {

		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String requestDate = df.format(new Date());
		df = new SimpleDateFormat("HHmmss");
		String requestTime = df.format(new Date());

		/**
		 * 调用SAG0002,完成校验操作
		 */
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", "PXC201");// 服务编码
		g675.put("6752", dpRequest.getChannelCode60To20());// 渠道号
		g675.put("6753", tradeSeq);// 流水号
		g675.put("6754", requestDate);// 发起请求日期，格式YYYYMMDD
		g675.put("6755", requestTime);// 发起请求时间，格式HH24MISS
		g675.put("6756", "INF");// 接口平台编码，INF:表示INF前置平台 UPPS:标识交易核心平台 GOS：运营管理门户
		g675.endRow();

		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", "03010010");// 业务编码
		g676.put("6762", "0003");// 产品编码
		g676.put("6763", dpRequest.getAccepTareaCode());// 受理区域编码
		g676.put("6764", dpRequest.getMerId());// 前向商户编码
		g676.put("6765", dpRequest.getTmnNum());// 前向商户终端号
		g676.endRow();

		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", dpRequest.getAcctCode());
		g680.put("6802", "0");
		g680.put("6803", "");
		g680.put("6804", dpRequest.getReamount());// 充值金额
		g680.endRow();

		// 组成数据包,调用SAG0002接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller
				.call("SAG", "SAG0002", g675, g676, g680);

		// 返回结果
		return dataSet;

	}
	/**
	 * [全国固话宽带]-号码鉴权
	 * @param dpRequest
	 * @param actionCode
	 * @param proudCode
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet authentificationFixBroadband (DpInf06004Request dpRequest,
			String actionCode, String proudCode) throws Exception{
		
		// 订单受理信息
		IParamGroup g675 = new ParamGroupImpl("675");
		//服务编码
		g675.put("6751", "GTC202");
		//渠道号 
		g675.put("6752", dpRequest.getChannelCode60To20());
		//流水号
		g675.put("6753", dpRequest.getTmnNum()
				+ SagUtils.getSeqNbr("yyyyMMddhhmmss", 4));
		//发起请求日期 
		g675.put("6754", DateTime.nowDate8Bit());
		//发起请求时间 
		g675.put("6755", DateTime.nowTime6Bit());
		//接口平台编码 
		g675.put("6756", "INF"); 
		
		g675.endRow();

		// 订单费用信息
		IParamGroup g676 = new ParamGroupImpl("676");
		//业务编码
		g676.put("6761", actionCode);
		//产品编码 
		g676.put("6762", proudCode);
		//受理区域编码 
		g676.put("6763", dpRequest.getAccepTareaCode());
		//前向商户编码 
		g676.put("6764", dpRequest.getMerId());
		//前向商户终端号 
		g676.put("6765", dpRequest.getTmnNum()); 

		g676.endRow();

		// 业务单信息
		IParamGroup g680 = new ParamGroupImpl("680");
		//鉴权号码
		g680.put("6801", dpRequest.getAcctCode());// 
		//鉴权类型
		g680.put("6802", dpRequest.getVerify());
		//查询区域编码
		g680.put("6803", "");
		//查询起始日期 
		g680.put("6804", "");
		//查询结束日期
		g680.put("6805", "");
		//记录开始编号
		g680.put("6806", "");
		//最大记录数
		g680.put("6807", "");
		//查询时间
		g680.put("6808", DateTime.now14Bit());
		
		g680.endRow();

		// 组成数据包,调用SAG0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SAG", "SAG0001", g675, g676,
				g680);

		// 返回结果
		return dataSet;
	}
	
	/**
	 * 根据固话宽带返回明细报文
	 * @param dataSet
	 * @return
	 * @throws Exception 
	 */
	private static List<String> detailMessage(PackageDataSet ds) throws Exception{
		
		if(ds == null){
			throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH);
		}
		
		List<String> list = new ArrayList<String>();
		
		list.add(ds.getByID("6920", "692").toString());
		
		list.add(ds.getByID("6921", "692").toString());
		
		list.add(ds.getByID("6922", "692").toString());
		
		list.add(ds.getByID("6923", "692").toString());
		
		return list;
		
	}
}
