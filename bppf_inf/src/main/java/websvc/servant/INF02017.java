package websvc.servant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import common.dao.TInfConsumeDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TSymCustomDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.entity.TSymCustom;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.SignBankManage;
import common.service.TInfOperInLogManager;
import common.utils.PrivConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02017Request;
import common.xml.dp.DpInf02017Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 快捷交易关闭接口
 * 
 * 
 */
public class INF02017 {
	public static String svcInfName = "INF02017";

	public static String execute(String in0, String in1) {

		DpInf02017Request dbRequest = null;

		RespInfo respInfo = null; // 返回信息头

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		DpInf02017Response resp = null;

		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			respInfo = new RespInfo(in1, "20"); // 返回信息头

			dbRequest = new DpInf02017Request(in1);

			// 客户端MD5校验--------------------------------------------
			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dbRequest.getStaffCode(),
					tokenValidTime);
			dbRequest.verifyByMD5(md5Key);
			TInfLoginLogDao.updateRanduseTimeByStaffCode(dbRequest.getStaffCode());
			//-------------------------------------------------------------------

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dbRequest.getKeep(), dbRequest.getIp(), dbRequest.getTmnNum(), svcCode, "XML", "agentCode", dbRequest.getCustCode(), "",
					"", "S0A");
			TInfOperInLogManager manager = new TInfOperInLogManager();
			// 判断插入是否成功
			if (tInfOperInLog != null) {
				boolean flag = manager.selectTInfOperInLogByKeep(dbRequest.getKeep());
				// 判断流水号是否可用
				if (flag) {
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);
					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE, INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}

			//判断有无交易查询权限
			List privList = PayCompetenceManage.payFunc(dbRequest.getCustCode(), dbRequest.getChannelCode());
			boolean r = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if(PrivConstant.CLN_TRADE_CLOSE.equals(str)){
					r = true;
					break;
				}

			}
			
			if(!r){
				throw new Exception("你没有快捷交易关闭的权限");
			}
			// 业务组件
			SignBankManage manage = new SignBankManage();

			// 获取客户ID
			String custId = manage.getCustIdByCode(dbRequest.getCustCode());
			if (custId == null) {
				throw new Exception("该商户号不存在");
			}

			TSymCustomDao buss = new TSymCustomDao();

			// 订单号
			String orderNo = dbRequest.getOrderNo();
			// 交易时间
			String treadeTime = dbRequest.getTradeTime();

			//将单笔和累计金额置为0
			update(buss,custId);
			//将累计清0
			TInfConsumeDao consumedao = new TInfConsumeDao();
			consumedao.updateSumStat(custId);
			// 当月累计消费
			String allTrade = buss.getAmountCount(custId);

			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), dbRequest.getKeep(), dbRequest.getIp(), svcCode, "", "000000", "S0A");

			// 返回结果
			resp = new DpInf02017Response();

			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", "000000", "成功", orderNo, treadeTime, "0", "0",
					allTrade, dbRequest.getRemark1(), dbRequest.getRemark2());

			return ResponseVerifyAdder.pkgForMD5(oXml, md5Key);

		} catch (XmlINFException spe) {
			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), dbRequest.getKeep(), dbRequest.getIp(), svcCode, "", spe.getMessage(), "S0A");
			}

			spe.setRespInfo(respInfo);

			String oXml = ExceptionHandler.toXML(spe, id);

			return ResponseVerifyAdder.pkgForMD5(oXml, null);

		} catch (Exception e) {
			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), dbRequest.getKeep(), dbRequest.getIp(), svcCode, "", e.getMessage(), "S0A");
			}
			String oXml = ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), id);

			return ResponseVerifyAdder.pkgForMD5(oXml, null);
		}
	}

	private static void update(TSymCustomDao buss, String custId) throws Exception {

		// 单笔
		TSymCustom model1 = new TSymCustom();

		model1.setCustId(Long.parseLong(custId));

		model1.setCustomType("T22");

		model1.setTh("0");

		model1.setThType("TH004");

		// 累计
		TSymCustom model2 = new TSymCustom();

		model2.setCustId(Long.parseLong(custId));

		model2.setCustomType("T22");

		model2.setTh("0");

		model2.setThType("TH005");

		List<TSymCustom> l = new ArrayList<TSymCustom>();

		l.add(model1);

		l.add(model2);

		buss.update(l);
	}
}
