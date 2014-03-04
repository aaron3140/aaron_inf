package websvc.servant;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TInfConsumeDao;
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
import common.utils.OrderConstant;
import common.utils.PrivConstant;
import common.utils.RegexTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf02019Response;
import common.xml.dp.DpInf12019Request;

import framework.config.ActionInfoConfig;
import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author wanght 2013-12-05 <br>
 *         TODO 游戏充值(纯业务)
 * 
 */
public class INF12019 {

	private static final Log logger = LogFactory.getLog(INF02019.class);

	public static String svcInfName = "INF02019";

	public static String execute(String in0, String in1) {
		DpInf12019Request dpRequest = null;

		DpInf02019Response resp = new DpInf02019Response();

		RespInfo respInfo = null;

		String responseCode = "";

		logger.info("INF02019请求参数：：" + in1);

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			dpRequest = new DpInf12019Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			// 判断有无交易查询权限
			List privList = PayCompetenceManage.payFunc(
					dpRequest.getCustCode(), "80");
			boolean r = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();
				if (PrivConstant.WS_GAM_REC.equals(str)) {
					r = true;
					break;
				}
			}

			if (!r) {
				throw new Exception("你没有游戏充值的权限");
			}

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(),
					dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML",
					"", "", "", "", OrderConstant.S0A);

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

			SignBankManage manage = new SignBankManage();
			String custId = manage.getCustIdByCode(dpRequest.getCustCode());
			if (null == custId || "".equals(custId)) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST,
						INFErrorDef.CUSTCODE_NOT_EXIST_DESC);
			}

			// 对时间格式进行匹配 时间格式：yyyyMMddHHmmss
			if (!(dpRequest.getTradeTime().matches(RegexTool.DATE_FORMAT))) {

				throw new Exception("输入日期格式不正确");
			}

			if (Double.valueOf(dpRequest.getOrderaAmount()) < 1) {
				throw new Exception("金额不能少于1分钱");
			}

			TInfConsumeDao dao = new TInfConsumeDao();

			String acc = dpRequest.getGameCode() + dpRequest.getGameAcct();

			if (dpRequest.getGameCode().equals("1001")) {
				if (dpRequest.getGameAcct() != null
						&& !dpRequest.getGameAcct().equals("")) {
					acc = dpRequest.getGameCode() + dpRequest.getGameAcct();// 业务对象
				} else {
					acc = dpRequest.getGameCode() + dpRequest.getBattleAcct();// 业务对象
				}
			}
			if (dao.isExist(acc, dpRequest.getOrderaAmount())) {

				throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
						INFErrorDef.GAMEACCT_AMOUNT_REPEAT);
			}

			// 充值金额 分
			String rechargeAmount = dpRequest.getRechargeAmount();
			// 游戏充值
			PackageDataSet ds = null;

			String responseDesc = null;

			Hashtable<String, Object> map = process2(dpRequest, custId);

			responseCode = (String) map.get("ResultCode");

			responseDesc = (String) map.get("ResponseDesc");

			ds = (PackageDataSet) map.get("DS");

			String transSeq = ds.getByID("4002", "401");

			ArrayList list = ds.getParamByID("6303", "600");
			String txanAmounts = dpRequest.getOrderaAmount();
			if (list != null && list.size() != 0) {
				txanAmounts = (String) list.get(0);// 充入金额
			}

			// 单位转换：元转分
			String txanAmount = MathTool.yuanToPoint(txanAmounts);

			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, dpRequest.getOrderNo(),
					transSeq, txanAmount, rechargeAmount, dpRequest
							.getBattleAcct(), dpRequest.getGameAcct(),
					dpRequest.getGameCode(), dpRequest.getRemark1(), dpRequest
							.getRemark2());

		} catch (XmlINFException spe) {

			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, infId);

		} catch (Exception e) {

			return ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), infId);

		}
	}

	private static Hashtable<String, Object> process2(
			DpInf12019Request dpRequest, String custId) throws Exception {

		// 游戏充值
		PackageDataSet ds = null;

		// 单位转换：分转元
		String reAmount = MathTool.pointToYuan(dpRequest.getRechargeAmount());
		String orAmount = MathTool.pointToYuan(dpRequest.getOrderaAmount());
		dpRequest.setRechargeAmount(reAmount);
		dpRequest.setOrderaAmount(orAmount);

		ds = recharge(dpRequest);// 充值

		String resultCode = (String) ds.getParamByID("0001", "000").get(0);

		String responseDesc = null;

		// 返回结果为失败时，获取结果描述
		if (Long.valueOf(resultCode) == 0) {

			responseDesc = (String) ds.getParamByID("0002", "000").get(0);

		}

		Hashtable<String, Object> map = new Hashtable<String, Object>();

		map.put("ResultCode", resultCode);
		map.put("ResponseDesc", responseDesc);
		map.put("DS", ds);

		return map;

	}

	/**
	 * 调用SCS0001接口充值
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet recharge(DpInf12019Request dpRequest)
			throws Exception {
		// String areaCode =
		// TCumInfoDaoTemp.queryAreacodeByCustCode(dpRequest.getCustCode());//
		// 所属区域编码
		TCumAcctDao acctDao = new TCumAcctDao();
		String acctCode = acctDao.getAcctCode(dpRequest.getCustCode());// 银行帐号
		String bankCode = acctDao.getBankCode(acctCode); // 银行编码[通过银行帐号查询]

		// TCumInfoDao infoDao = new TCumInfoDao();
		String area_code = TCumInfoDao.getAreaCode(dpRequest.getCustCode());

		bankCode = "110000";
		// 一次路由
		// TransManage tm = new TransManage();
		// PackageDataSet dataSet2 = tm.firstRoute(dpRequest.getCustCode(),
		// areaCode, dpRequest.getChannelCode(), "07010002",
		// dpRequest.getMerId(),
		// dpRequest.getTmnNum(), dpRequest.getTxnAmount(), "PT1004", bankCode);

		// String newActionCode = dataSet2.getByID("4051", "423");
		String newActionCode = "16010001";
		// String newOrgCode = dataSet2.getByID("4098", "423");
		// DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		// String tradeTime = df.format(new Date());
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		// g401.put("4006", areaCode);// 所属区域编码
		g401.put("4006", area_code);// 所属区域编码 广州地区440100
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", dpRequest.getTradeTime());// 受理时间
		// g401.put("4012", "全国新宽联直充");// 订单描述
		g401.put("4012", convertCodeToChar(dpRequest.getGameCode()));// 订单描述
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
		// g401.put("4017", dpRequest.getOrderNo());// 订单号？？？
		g401.put("4017", dpRequest.getKeep());// 
		g401.put("4018", dpRequest.getTmnNum());// 受理终端号
		g401.put("4028", dpRequest.getOrderNo());// 外部订单号
		g401.put("4284", dpRequest.getMerId());// 机构编码 //20130628 wanght
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", dpRequest.getOrderaAmount());// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", dpRequest.getOrderaAmount());// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		// g404.put("4064", dpRequest.getCustCode());
		// 魔兽世界
		if (dpRequest.getGameCode().equals("1001")) {
			g404.put("4049", "0020");// 产品编码
		}
		// 街头蓝球
		else if (dpRequest.getGameCode().equals("2001")) {
			g404.put("4049", "0016");// 产品编码
		}
		// 蒸汽幻想/拍拍部落
		else if (dpRequest.getGameCode().equals("2002")) {
			g404.put("4049", "0017");// 产品编码
		}
		// 希望/问道
		else if (dpRequest.getGameCode().equals("2003")) {
			g404.put("4049", "0018");// 产品编码
		}
		// 冰川一卡通（远征ＯＬ）
		else if (dpRequest.getGameCode().equals("2004")) {
			g404.put("4049", "0021");// 产品编码
		}
		g404.put("4051", newActionCode);// 业务编码
		g404.put("4053", "1");// 业务数量
		// 1001 魔兽世界 1当游戏编码为1001魔兽世界时必填为战网账号
		// 2需要充值的游戏账号,当游戏编码为1001魔兽世界并且只有一个游戏账号时可不填，将默认充到这个游戏账号中
		if (dpRequest.getGameCode().equals("1001")) {
			if (dpRequest.getGameAcct() != null
					&& !dpRequest.getGameAcct().equals("")) {
				g404.put("4052", dpRequest.getGameAcct());// 业务对象
			} else {
				g404.put("4052", dpRequest.getBattleAcct());// 业务对象
			}
		} else {
			g404.put("4052", dpRequest.getGameAcct());// 业务对象
		}
		g404.put("4062", "");// 当该值与不为空时，已该值作为actlist的系统参考号。否则由核心交易平台平台生成
		g404.put("4072", newActionCode);
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", dpRequest.getOrderaAmount());// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", dpRequest.getOrderaAmount());// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");
		g407.put("4088", "0200");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_USERTYPE");
		g407.put("4088", "0");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_SPID");
		g407.put("4088", "10002001");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE1");
		g407.put("4088", "H003");
		g407.put("4089", "游戏ID");
		g407.put("4091", "01");
		// g407.put("4093", dpRequest.getQq());
		g407.put("4093", ActionInfoConfig.INF02010_PRO_ID); // 测试2030 生产 5178
		// g407.put("4093", "5178"); //测试2030 生产 5178
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE2");
		g407.put("4088", "H007");
		g407.put("4089", "其它参数");
		g407.put("4091", "01");
		g407.put("4093", "1");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE3");
		g407.put("4088", "H016");
		g407.put("4089", "充值账户");
		g407.put("4091", "01");
		g407.put("4093", dpRequest.getGameCode());
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE4");
		g407.put("4088", "H017");
		g407.put("4089", "确认充值账户");
		g407.put("4091", "01");
		g407.put("4093", dpRequest.getGameCode());
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE5");
		g407.put("4088", "H018");
		g407.put("4089", "游戏名称(代码)");
		g407.put("4091", "01");
		g407.put("4093", "987654");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE6");
		g407.put("4088", "H019");
		g407.put("4089", "服务器名称(代码)");
		g407.put("4091", "01");
		g407.put("4093", "");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_SERVID");
		g407.put("4088", "GGC401");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT2001");// 支付方式编码

		g408.put("4127", bankCode);// 前置支付机构,查卡表的bankcode
		g408.put("4098", bankCode);// 支付机构编码
		g408.put("4099", "0001");// 账户类型编码
		g408.put("4101", acctCode);// 账号
		g408.put("4102", "123456");// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", dpRequest.getOrderaAmount());// 支付金额
		g408.put("4109", "0003");// 国际网络号
		g408.put("4119", "");
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402,
				g404, g405, g407, g408);

		// 返回结果
		return dataSet;

	}

	/**
	 * 获取充值业务类型说明
	 * 
	 * @param rechargeType
	 * @return
	 */
	private static String convertCodeToChar(String rechargeType) {
		if ("1001".equals(rechargeType)) {
			return "魔兽世界(战网一卡通)-纯业务";
		} else if ("2001".equals(rechargeType)) {
			return "街头蓝球-纯业务";
		} else if ("2002".equals(rechargeType)) {
			return "蒸汽幻想/拍拍部落-纯业务";
		} else if ("2003".equals(rechargeType)) {
			return "希望/问道-纯业务";
		} else if ("2004".equals(rechargeType)) {
			return "冰川一卡通(远征ＯＬ)-纯业务";
		} else {
			return "全国新宽联直充-纯业务";
		}

	}
}
