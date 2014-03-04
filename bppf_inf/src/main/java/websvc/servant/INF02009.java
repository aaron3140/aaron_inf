package websvc.servant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TSymCustomDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.entity.TSymCustom;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.SignBankManage;
import common.service.TInfOperInLogManager;
import common.utils.PrivConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02009Request;
import common.xml.dp.DpInf02009Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 快捷交易设置接口
 * 
 * 
 */
public class INF02009 {
	public static String svcInfName = "INF02009";

	public static String execute(String in0, String in1) {

		DpInf02009Request dbRequest = null;

		RespInfo respInfo = null; // 返回信息头

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		DpInf02009Response resp = null;

		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			respInfo = new RespInfo(in1, "20"); // 返回信息头

			dbRequest = new DpInf02009Request(in1);
			
			// 客户端MD5校验--------------------------------------------
			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dbRequest.getStaffCode(),
					tokenValidTime);
			dbRequest.verifyByMD5(md5Key);
			TInfLoginLogDao.updateRanduseTimeByStaffCode(dbRequest.getStaffCode());
			// -------------------------------------------------------------------

			
//			判断有无交易查询权限
			List privList = PayCompetenceManage.payFunc(dbRequest.getCustCode(), dbRequest.getChannelCode());
			boolean p = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if(PrivConstant.IPOS_TRADE_SET.equals(str)){
					p = true;
					break;
				}

			}
			
			if(!p){
				throw new Exception("你没有快捷交易设置的权限");
			}

			//密码验证
			PackageDataSet dataSet = callCUM1003(dbRequest);
			String resCode = dataSet.getByID("0001", "000");
			if (Long.valueOf(resCode) != 0) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
						INFErrorDef.PAY_PWD_FAULT);
			}
			
			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dbRequest.getKeep(),
					dbRequest.getIp(), dbRequest.getTmnNum(), svcCode, "XML",
					"agentCode", dbRequest.getCustCode(), "", "", "S0A");
			TInfOperInLogManager manager = new TInfOperInLogManager();
			// 判断插入是否成功
			if (tInfOperInLog != null) {
				boolean flag = manager.selectTInfOperInLogByKeep(dbRequest
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

			// 业务组件
			SignBankManage manage = new SignBankManage();

			// 获取客户ID
			String custId = manage.getCustIdByCode(dbRequest.getCustCode());
			if (custId == null) {
				throw new Exception("该商户号不存在");
			}

			TSymCustomDao buss = new TSymCustomDao();

			int r = buss.quert(custId, "T22");

			if (r == 0) {
				
				add(buss, custId, dbRequest);
			} else {
				
				update(buss, custId, dbRequest);
			}

			buss.updateAmountCount(custId);//修改累积消费记录的状态
			
			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
					dbRequest.getKeep(), dbRequest.getIp(), svcCode, "",
					"000000", "S0A");

			// 返回结果
			resp = new DpInf02009Response();

			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS", "000000",
					"成功", dbRequest.getOrderNo(), dbRequest.getPeramount(),
					dbRequest.getAllamount(), dbRequest.getRemark1(), dbRequest
							.getRemark2());

			return ResponseVerifyAdder.pkgForMD5(oXml, md5Key);

		} catch (XmlINFException spe) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
						dbRequest.getKeep(), dbRequest.getIp(), svcCode, "",
						spe.getMessage(), "S0A");
			}
			spe.setRespInfo(respInfo);

			String oXml = ExceptionHandler.toXML(spe, id);

			return ResponseVerifyAdder.pkgForMD5(oXml, null);

		} catch (Exception e) {
			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
						dbRequest.getKeep(), dbRequest.getIp(), svcCode, "", e
								.getMessage(), "S0A");
			}
			String oXml = ExceptionHandler.toXML(new XmlINFException(resp, e,
					respInfo), id);

			return ResponseVerifyAdder.pkgForMD5(oXml, null);
		}
	}

	private static PackageDataSet callCUM1003(
			DpInf02009Request dpRequest) throws Exception {

		String staff = dpRequest.getStaffCode();

		String verityType = "0001"; // 支付密码

		String tmnNum = dpRequest.getTmnNum();

		IParamGroup g200 = new ParamGroupImpl("200");
		g200.put("2901", "2171");
		g200.put("2902", staff);
		g200.put("2903", "2007");
		g200.put("2904", dpRequest.getPayPassword());
		g200.put("2172", "0001");
		g200.put("2173", verityType);
		// g200.put("2025", null);
		g200.endRow();

		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", dpRequest.getChannelCode());
		g211.put("2077", tmnNum);
		g211.put("2078", null);
		g211.put("2085", dpRequest.getIp());
		g211.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("BIS", "CUM1003", g200, g211);

		return dataSet;
	}
	
	private static void add(TSymCustomDao buss, String custId,
			DpInf02009Request dbRequest) throws Exception {

		// 单笔
		TSymCustom model1 = new TSymCustom();

		model1.setCustomId(buss.getOrderSeq());

		model1.setCustId(Long.parseLong(custId));

		model1.setCustomType("T22");

		model1.setTh(dbRequest.getPeramount());

		model1.setThType("TH004");

		// 累计
		TSymCustom model2 = new TSymCustom();

		model2.setCustomId(buss.getOrderSeq());

		model2.setCustId(Long.parseLong(custId));

		model2.setCustomType("T22");

		model2.setTh(dbRequest.getAllamount());

		model2.setThType("TH005");

		List<TSymCustom> l = new ArrayList<TSymCustom>();

		l.add(model1);

		l.add(model2);

		buss.add(l);
	}

	private static void update(TSymCustomDao buss, String custId,
			DpInf02009Request dbRequest) throws Exception {

		// 单笔
		TSymCustom model1 = new TSymCustom();

		model1.setCustId(Long.parseLong(custId));

		model1.setCustomType("T22");

		model1.setTh(dbRequest.getPeramount());

		model1.setThType("TH004");

		// 累计
		TSymCustom model2 = new TSymCustom();

		model2.setCustId(Long.parseLong(custId));

		model2.setCustomType("T22");

		model2.setTh(dbRequest.getAllamount());

		model2.setThType("TH005");

		List<TSymCustom> l = new ArrayList<TSymCustom>();

		l.add(model1);

		l.add(model2);

		buss.update(l);
	}

}
