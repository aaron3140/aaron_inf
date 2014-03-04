package websvc.servant;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TInfConsumeDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TPnmPartnerDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfConsume;
import common.entity.TInfOperInLog;
import common.entity.VerifyConsumeEntity;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.SignBankManage;
import common.service.TInfOperInLogManager;
import common.utils.DateTime;
import common.utils.MathTool;
import common.utils.OrderConstant;
import common.utils.PrivConstant;
import common.utils.RegexTool;
import common.utils.SagUtils;
import common.utils.WebSvcTool;
import common.utils.WebSvcUtil;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02018Request;
import common.xml.dp.DpInf02018Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF02018 {

	public static String svcInfName = "INF02018";

	private static final Log logger = LogFactory.getLog(INF02018.class);

	public static String execute(String in0, String in1) {

		DpInf02018Request dpRequest = null;// 本接口的字段信息包含公共信息

		RespInfo respInfo = null; // 返回信息头

		SagManager sagManager = new SagManager();// SAG网关类

		TInfOperInLog tInfOperInLog = null;// 日志类

		String svcCode = WebSvcTool.getSvcCode(in0);// 获取服务编码

		DpInf02018Response resp = null;// 用于封装返回的xml报文

		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);// svcInfName&partyGroup

		Long consumId = null;

		try {

			dpRequest = new DpInf02018Request(in1);// 一系列的验证和接口参数的设置

			respInfo = new RespInfo(in1, dpRequest.getChannelCode()); // 根据in1这个xml文件设置返回的信息头

			// 客户端MD5校验--------------------------------------------
			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest.getStaffCode(), tokenValidTime);
			dpRequest.verifyByMD5(md5Key);// 根据加密令牌md5Key用md5对cer进行mdf编码然后跟sign比较，相等则通过
			TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest.getStaffCode());// 更新加密令牌最后使用时间
			// -------------------------------------------------------------------

			// 判断有无交易查询权限
			List privList = PayCompetenceManage.payFunc(dpRequest.getCustCode(), dpRequest.getChannelCode());
			boolean re = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();
				// List listTest=(List)privList.get(i);
				// String strTest=listTest.get("PRIV_URL").toString();
				if (PrivConstant.IPOS_ELE_VEND.equals(str)) {
					re = true;
					break;
				}

			}

			// if (!re) {
			// throw new Exception("你没有电子售卡的权限");
			// }

			TInfOperInLogManager manager = new TInfOperInLogManager();

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(), dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML", "agentCode", dpRequest.getCustCode(), "",
					"", "S0A");

			// 判断插入是否成功
			if (tInfOperInLog != null) {
				boolean flag = manager.selectTInfOperInLogByKeep(dpRequest.getKeep());// 根据流水号查询入站日志
				// 判断流水号是否可用
				if (flag) {// 流水号对应的日志数大于一，重复不允许交易
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);
					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE, INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}

			// 业务组件
			SignBankManage manage = new SignBankManage();

			// 获取客户ID
			String custId = manage.getCustIdByCode(dpRequest.getCustCode());

			if (null == custId || "".equals(custId)) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST, INFErrorDef.CUSTCODE_NOT_EXIST_DESC);
			}

			if (Double.valueOf(dpRequest.getCardAmount()) < 1) {
				throw new Exception("金额不能少于1分钱");
			}
			// 对时间格式进行匹配 时间格式：yyyyMMddHHmmss
			if (!(dpRequest.getTradeTime().matches(RegexTool.DATE_FORMAT))) {

				throw new Exception("输入日期格式不正确");
			}
			// 业务编码
			String actionCode = "09010001";

			// 返回接口面值
			String cardMount = dpRequest.getCardAmount();

			// 单位转换：分转元
			String cardAmount = MathTool.pointToYuan(dpRequest.getCardAmount());

			dpRequest.setCardAmount(cardAmount);

			// 卡类型转换
			dpRequest.setCardTypeCode(convertCardType(dpRequest.getCardTypeCode(), cardAmount));

			// 核心面值
			String a_mount = convertCardMount(dpRequest.getCardAmount());

			// 产品编码
			String proudCode = dpRequest.getCardTypeCode() + a_mount + "00";

			String responseDesc = "";

			String resultCode = "";

			String transSeq = "";

			String expDdate = "";

			String cardPassword = "";

			String cardNo = "";
			
			String payAmount = cardMount;

			if ("9".equals(dpRequest.getPayType())) {

				Hashtable<String, Object> map = process2(dpRequest, actionCode, proudCode, custId, a_mount);

				responseDesc = (String) map.get("ResponseDesc");

				resultCode = (String) map.get("ResultCode");

				transSeq = (String) map.get("TransSeq");

				Map<String, String> r = (Map<String, String>) map.get("Map");

				expDdate = r.get("ExpDdate");

				cardPassword = r.get("CardPassword");

				cardNo = r.get("CardNo");
			} else {

				Hashtable<String, Object> map = process1(dpRequest, actionCode, proudCode, custId, cardMount, a_mount);

				responseDesc = (String) map.get("ResponseDesc");

				resultCode = (String) map.get("ResultCode");

				transSeq = (String) map.get("TransSeq");

				consumId = (Long) map.get("ConsumId");
				
				payAmount = (String) map.get("PayAmount");

				Map<String, String> r = (Map<String, String>) map.get("Map");

				expDdate = r.get("ExpDdate");

				cardPassword = r.get("CardPassword");

				cardNo = r.get("CardNo");

			}

			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), dpRequest.getKeep(), dpRequest.getIp(), svcCode, "", "000000", "S0A");

			// 返回结果
			resp = new DpInf02018Response();
			String oXml = "";
			try {
				oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", resultCode, responseDesc, dpRequest.getOrderNo(),
						transSeq, cardMount, cardNo, cardPassword, expDdate, dpRequest.getRemark1(), dpRequest.getRemark2(),payAmount);
			} catch (Exception e) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.FAIL_TO_GET_CARD_INFO_DESC);
			}
			// 添加签名信息
			return ResponseVerifyAdder.pkgForMD5(oXml, md5Key);

		} catch (XmlINFException spe) {
			// if (tInfOperInLog != null) {
			// // 插入信息到出站日志表
			// sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
			// dpRequest.getKeep(), dpRequest.getIp(), svcCode, "",
			// spe.getMessage(), "S0A");
			// }

			spe.setRespInfo(respInfo);

			String oXml = ExceptionHandler.toXML(spe, id);

			return ResponseVerifyAdder.pkgForMD5(oXml, null);

		} catch (Exception e) {
			// if (tInfOperInLog != null) {
			// // 插入信息到出站日志表
			// sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
			// dpRequest.getKeep(), dpRequest.getIp(), svcCode, "", e
			// .getMessage(), "S0A");
			// }
			String oXml = ExceptionHandler.toConsumeXML(new XmlINFException(resp, e, respInfo), id, consumId);

			return ResponseVerifyAdder.pkgForMD5(oXml, null);
		}
	}

	private static Hashtable<String, Object> process2(DpInf02018Request dpRequest, String actionCode, String proudCode, String custId, String a_mount) throws Exception {

		PackageDataSet ds = null;

		// 售卡预占
		ds = sag0003(dpRequest, actionCode, proudCode, a_mount);

		String responseDesc = "";

		String resultCode = "";

		String tranSeq = "";

		resultCode = (String) ds.getParamByID("0001", "000").get(0);

		if (Long.valueOf(resultCode) == 0) {

			tranSeq = (String) ds.getParamByID("6901", "690").get(0);

		}

		// 售卡
		ds = buyCard(dpRequest, tranSeq, actionCode, proudCode, a_mount,dpRequest.getCardAmount(),null);

		resultCode = (String) ds.getParamByID("0001", "000").get(0);

		Map<String, String> r = new HashMap<String, String>();

		// 返回结果为失败时，获取结果描述
		if (Long.valueOf(resultCode) == 0) {

			responseDesc = (String) ds.getParamByID("0002", "000").get(0);

			r = parseResult(unpack(ds));
		}

		String transSeq = ds.getByID("4002", "401");

		Hashtable<String, Object> map = new Hashtable<String, Object>();

		map.put("ResultCode", resultCode);
		map.put("ResponseDesc", responseDesc);
		map.put("TransSeq", transSeq);
		map.put("Map", r);

		return map;

	}

	private static Hashtable<String, Object> process1(DpInf02018Request dpRequest, String actionCode, String proudCode, String custId, String cardMount, String a_mount)
			throws Exception {
		
		//--------------------------------------------------------------------------
		//获取溢价
		 TCumInfoDao infoDao = new TCumInfoDao();
		 String areaCode = infoDao.getAreaCode(dpRequest.getCustCode());
		String concession = scs0201(dpRequest,actionCode,proudCode,areaCode);
		
		DecimalFormat df = new DecimalFormat("#.00");
		String payAmount=df.format(Double.parseDouble(cardMount)+Double.parseDouble(concession));
		//-----------------------------------------------------------------------------

		Long consumId = null;
		// 快捷交易验证
		VerifyConsumeEntity entity = new VerifyConsumeEntity(custId, dpRequest.getStaffCode(), payAmount, dpRequest.getPayPassword(), dpRequest.getChannelCode(), dpRequest
				.getTmnNum(), dpRequest.getIp());

		WebSvcUtil websvcutil = new WebSvcUtil();

		boolean flag = websvcutil.VerifyConsume(entity);

		TInfConsumeDao dao = new TInfConsumeDao();

		consumId = dao.getConsumeId();

		PackageDataSet ds = null;

		// 售卡预占
		ds = sag0003(dpRequest, actionCode, proudCode, a_mount);

		String responseDesc = "";

		String resultCode = "";

		String tranSeq = "";

		resultCode = (String) ds.getParamByID("0001", "000").get(0);

		if (Long.valueOf(resultCode) == 0) {

			tranSeq = (String) ds.getParamByID("6901", "690").get(0);

		}

		// 调用核心接口之前将消费记录插入到消费表
		addConsume(dpRequest, consumId, dao, custId, websvcutil.getSum_stat(), actionCode, payAmount);

		// 售卡
		ds = buyCard(dpRequest, tranSeq, actionCode, proudCode, a_mount,payAmount,concession);

		resultCode = (String) ds.getParamByID("0001", "000").get(0);

		Map<String, String> r = new HashMap<String, String>();

		// 返回结果为失败时，获取结果描述
		if (Long.valueOf(resultCode) == 0) {

			responseDesc = (String) ds.getParamByID("0002", "000").get(0);

			r = parseResult(unpack(ds));

			dao.updateOrderStat(consumId, OrderConstant.S0C);

			if (flag) {

				dao.updateSumStat(custId);
			}
		}

		String transSeq = ds.getByID("4002", "401");

		Hashtable<String, Object> map = new Hashtable<String, Object>();

		map.put("ResultCode", resultCode);
		map.put("ResponseDesc", responseDesc);
		map.put("TransSeq", transSeq);
		map.put("Map", r);
		map.put("ConsumId", consumId);
		map.put("PayAmount", payAmount);

		return map;

	}

	private static void addConsume(DpInf02018Request dpRequest, Long consumId, TInfConsumeDao dao, String custId, String stat, String actionCode, String cardMount) {

		TInfConsume c = new TInfConsume();

		c.setConsumeId(consumId);

		c.setCustId(Long.valueOf(custId));

		c.setOrderNo(dpRequest.getOrderNo());

		c.setAcctType("0007");

		c.setKeep(dpRequest.getKeep());

		c.setChannelType(dpRequest.getChannelCode());

		c.setTermId(dpRequest.getTmnNum());

		c.setActionCode(actionCode);

		c.setPdLineId(String.valueOf(dao.getPdlineId()));

		c.setAmount(cardMount);

		c.setStat(OrderConstant.S0A);

		c.setAcctDate(new Date());

		c.setRemark(dpRequest.getRemark1() + "::" + dpRequest.getRemark2());

		c.setSum_stat(stat);

		dao.insert(c);
	}

	private static Map<String, String> parseResult(List<List<Map<String, String>>> list) {

		Map<String, String> m = new HashMap<String, String>();

		for (int i = 0; i < list.size(); i++) {

			List<Map<String, String>> data = list.get(i);

			for (int j = 0; j < data.size(); j++) {

				Map<String, String> map = data.get(j);
				if ("K009".equals(map.get("KEY"))) {
					// 卡号
					// cardNo = map.get("VALUE");
					m.put("CardNo", map.get("VALUE"));

				} else if ("K014".equals(map.get("KEY"))) {
					// 卡密码
					// cardPassword = map.get("VALUE");
					m.put("CardPassword", map.get("VALUE"));
				} else if ("K010".equals(map.get("KEY"))) {
					// 面值
					String cardMount = map.get("VALUE");
					// 单位转换：元转分
					cardMount = MathTool.yuanToPoint(cardMount);

					m.put("CardMount", cardMount);

				} else if ("K011".equals(map.get("KEY"))) {

					// 有效开始日期
				} else if ("K012".equals(map.get("KEY"))) {
					// 有效截止日期
					// expDdate = map.get("VALUE");

					m.put("ExpDdate", map.get("VALUE"));
				} else if ("K015".equals(map.get("KEY"))) {
					// 卡序列号
				}

			}
		}
		return m;

	}

	private static List<List<Map<String, String>>> unpack(PackageDataSet ds) {
		ArrayList value6920 = ds.getParamByID("6920", "692");
		ArrayList value6923 = ds.getParamByID("6923", "692");
		List<List<Map<String, String>>> data = new ArrayList<List<Map<String, String>>>();
		int num = 7;// R**每一个账单有21个参数
		int allNum = ds.getParamSetNum("692");
		int lenght = allNum / num;

		for (int i = 0; i < lenght; i++) {
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			data.add(list);
			int startIndex = i * num;// 本次循环取数据开始的索引【包含】
			int endIndex = (i + 1) * num;// 本次循环取数据结束的索引【不包含】

			for (int j = startIndex; j < endIndex; j++) {
				String key = (String) value6920.get(j);
				String value = (String) value6923.get(j);

				Map<String, String> map = new HashMap<String, String>();
				list.add(map);
				map.put("KEY", key);
				map.put("VALUE", value);
			}
		}
		return data;
	}

	private static String convertCardMount(String amount) {

		String m = "01";

		if ("10".equals(amount)) {

			m = "01";
		} else if ("20".equals(amount)) {

			m = "02";
		} else if ("30".equals(amount)) {

			m = "03";
		} else if ("50".equals(amount)) {

			m = "05";
		} else if ("100".equals(amount)) {

			m = "11";
		} else if ("200".equals(amount)) {

			m = "12";
		} else if ("300".equals(amount)) {

			m = "13";
		} else if ("500".equals(amount)) {

			m = "15";
		} else if ("1000".equals(amount)) {

			m = "21";
		}

		return m;
	}

	private static String convertCardType(String cardType, String cardAmount) {

		String type = "0100";

		if ("1001".equals(cardType)) {// 电信
			if ("30".equals(cardAmount)) {
				type = "0100";
			} else {
				type = "0102";
			}
		} else if ("2003".equals(cardType)) {// 天下通

			type = "0402";
		} else if ("1002".equals(cardType)) {// 联通

			type = "0320";
		} else if ("2004".equals(cardType)) {// 翼充卡

			type = "0501";
		}

		return type;
	}

	/**
	 * 前置卡类型
	 * 
	 * @param cardType
	 * @return
	 */
	private static String convertSearchArea(String cardType) {

		String area = "440100";

		if ("0100".equals(cardType)) {// 电信

			area = "440100";
		} else if ("0402".equals(cardType)) {// 天下通

			area = "440100";
		} else if ("0320".equals(cardType)) {// 联通

			area = "440100";
		} else if ("0501".equals(cardType)) {// 翼充卡

			area = "440100";
		}

		return area;
	}

	/**
	 * 售卡预占
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet sag0003(DpInf02018Request dpRequest, String actionCode, String proudCode, String a_mount) throws Exception {

		TCumInfoDao infoDao = new TCumInfoDao();

		String area_code = infoDao.getAreaCode(dpRequest.getCustCode());

		/**
		 * 调用SAG0003,完成交易操作
		 */
		// 包头控制信息
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", "GCS301");// 服务编码
		g675.put("6752", dpRequest.getChannelCode());// 
		String keep = dpRequest.getTmnNum() + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		g675.put("6753", keep);// 流水号
		g675.put("6754", DateTime.nowDate8Bit());// 发起请求日期
		g675.put("6755", DateTime.nowTime6Bit());// 发起请求时间
		g675.put("6756", "INF");// INF:表示INF前置平台

		g675.endRow();

		// 业务基础信息
		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", actionCode);// 业务编码
		g676.put("6762", proudCode);// 产品编码
		g676.put("6763", area_code);// 受理区域编码
		g676.put("6764", dpRequest.getMerId());// 前向商户编码
		g676.put("6765", dpRequest.getTmnNum());// 前向商户终端号

		g676.endRow();

		// 预订基本信息
		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", dpRequest.getCardTypeCode());// 卡类型
		g680.put("6802", "0");// 用户标识类型
		g680.put("6803", "");// 预订单位代码
		g680.put("6804", dpRequest.getTradeTime());// 预订时间
		g680.put("6805", convertSearchArea(dpRequest.getCardTypeCode()));// 查询区域编码

		g680.endRow();

		// 预订附加信息
		IParamGroup g682 = new ParamGroupImpl("682");
		g682.put("6820", "K001");// 预订内容编码
		g682.put("6821", "面值");// 
		g682.put("6822", "01");// 文本（可直接显示）
		g682.put("6823", a_mount);// 面值
		g682.put("6826", "");// 明细项属性集合
		g682.put("6827", "");// 明细项属性集合分隔符
		g682.endRow();

		g682.put("6820", "K006");// 预订内容编码
		g682.put("6821", "赠送返还");// 
		g682.put("6822", "01");// 文本（可直接显示）
		g682.put("6823", "00");// 预订内容
		g682.put("6826", "");// 明细项属性集合
		g682.put("6827", "");// 明细项属性集合分隔符
		g682.endRow();

		g682.put("6820", "K008");// 预订内容编码
		g682.put("6821", "需求卡数");// 
		g682.put("6822", "01");// 文本（可直接显示）
		g682.put("6823", "1");// 预订内容
		g682.put("6826", "");// 明细项属性集合
		g682.put("6827", "");// 明细项属性集合分隔符
		g682.endRow();

		// 组成数据包,调用SAG0003接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SAG", "SAG0003", g675, g676, g680, g682);

		// 返回结果
		return dataSet;

	}

	/**
	 * 查询溢价
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static String scs0201(DpInf02018Request dpRequest,String actionCode,
			String prodCode,String areaCode) throws Exception {
		
		TPnmPartnerDao dao = new TPnmPartnerDao();
		Map<String, String> map = dao.getPrntnCodeAndPrntType(dpRequest
				.getCustCode());
		
		IParamGroup g423 = new ParamGroupImpl("423");
		g423.put("4230", "0003"); 	//溢价
		g423.put("2011", dpRequest.getMerId());	//接入机构的对应的商户编码
		g423.put("4330", map.get("PRTN_CODE"));	//实际做交易的商户编码
		g423.put("4331", map.get("PRTN_TYPE"));	//实际做交易的商户类型
		g423.put("2002", dpRequest.getCustCode());	//实际做交易的商户编码
		g423.put("4051", actionCode);	//业务编码
		g423.put("4049", prodCode);	//产品编码
		g423.put("4098", "110000");  //银行编码
		g423.put("4006", areaCode);  //区域编码
		g423.put("4144", dpRequest.getChannelCode());  //渠道号
		g423.endRow();
		
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4025", dpRequest.getCardAmount());	//订单金额
		
		g402.put("4099", "0007");	//账户类型编码

		String concession ="0";

		try{
			// 组成数据包,调用SCS0201接口
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet dataSet = caller.call("SCS", "SCS0201", g423, g402);
			
			String resCode = dataSet.getByID("0001", "000");
			
			if (Long.valueOf(resCode) == 0) {
				
				String flagStr = dataSet.getByID("4230", "423");
				
				if ("0003".equals(flagStr)) {
					
					concession = dataSet.getByID("4329", "423");
					
					if (concession == null || "".equals(concession)
							|| Double.valueOf(concession) == 0) {
						
						concession = "0";
					}
				}
			}
		}catch(Exception e){}

		// 返回结果  单位转换：元转分
		return MathTool.yuanToPoint(concession);

	}
	
	/**
	 * 调用SCS0001接口充值
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet buyCard(DpInf02018Request dpRequest, String tranSeq, String actionCode, String proudCode, String a_mount,String payAmount,String concession) throws Exception {

		TCumAcctDao acctDao = new TCumAcctDao();
		String acctCode = acctDao.getAcctCode(dpRequest.getCustCode());// 银行帐号
		// String bankCode = acctDao.getBankCode(acctCode); // 银行编码[通过银行帐号查询]

		
		String bankCode = "110000";
		
		// 单位转换：分转元
		payAmount = MathTool.pointToYuan(payAmount);
		concession = MathTool.pointToYuan(concession);
		
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单

		g401.put("4006", convertSearchArea(dpRequest.getCardTypeCode()));// 所属区域编码 广州地区440100
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", dpRequest.getTradeTime());// 受理时间

		if (dpRequest.getPayType().equals("9")) {
			g401.put("4012", "现金支付-电子售卡");// 订单描述
		} else {
			g401.put("4012", "电子售卡");// 订单描述
		}
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());// 
		g401.put("4018", dpRequest.getTmnNum());// 受理终端号
		g401.put("4028", dpRequest.getOrderNo());// 外部订单号
		// g401.put("4284", dpRequest.getMerId());//机构编码 //20130628 wanght
		g401.endRow();
		
		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", dpRequest.getCardAmount());// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", payAmount);// 订单应付金额
		g402.put("4030", concession);// 溢价
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号

		g404.put("4049", proudCode);// 产品编码 // 全国新宽联0007 腾讯QQ 0031 改0039 电子售卡
		// 01000300
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", dpRequest.getCustCode());//
		g404.put("4053", "1");// 业务数量
		g404.put("4062", tranSeq);// 交易流水
		g404.put("4072", actionCode);
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", dpRequest.getCardAmount());// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", dpRequest.getCardAmount());// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();

		// 业务单费用信息
		IParamGroup g406 = new ParamGroupImpl("406");
		g406.put("4047", "1");// 业务单序号
		g406.put("4049", proudCode);// 产品编码
		g406.put("4077", "SCS_QUERYAREACODE");// 
		g406.put("4078", convertSearchArea(dpRequest.getCardTypeCode()));//
		// g406.put("4079", "");//
		g406.endRow();

		g406.put("4047", "1");// 业务单序号
		g406.put("4049", proudCode);// 产品编码
		g406.put("4077", "SCS_CARDTYPE");// 
		g406.put("4078", dpRequest.getCardTypeCode());//
		// g406.put("4079", "");//
		g406.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");
		g407.put("4088", "0200");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_USERTYPE");
		g407.put("4088", "0");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE1");
		g407.put("4088", "K001");
		g407.put("4089", "面值");
		g407.put("4091", "01");
		g407.put("4093", a_mount);
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE2");
		g407.put("4088", "K006");
		g407.put("4089", "赠送返还");
		g407.put("4091", "01");
		g407.put("4093", "00"); //
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_SERVID");
		g407.put("4088", "GCS501");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		if (dpRequest.getPayType().equals("9")) {
			g408.put("4097", "PT2001");// 支付方式编码
		} else {
			g408.put("4097", "PT0004");// 支付方式编码
		}
		g408.put("4127", bankCode);// 前置支付机构,查卡表的bankcode
		g408.put("4098", bankCode);// 
		g408.put("4099", "0007");// 账户类型编码
		g408.put("4101", acctCode);// 账号
		g408.put("4102", dpRequest.getPayPassword());// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", payAmount);// 支付金额

		g408.put("4109", "0003");// 国际网络号

		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g406, g407, g408);

		// 返回结果
		return dataSet;

	}

}
