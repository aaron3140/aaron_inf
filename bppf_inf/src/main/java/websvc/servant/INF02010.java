package websvc.servant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TCumInfoDaoTemp;
import common.dao.TInfConsumeDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
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
import common.utils.MathTool;
import common.utils.OrderConstant;
import common.utils.PrivConstant;
import common.utils.WebSvcTool;
import common.utils.WebSvcUtil;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02010Request;
import common.xml.dp.DpInf02010Response;

import framework.config.ActionInfoConfig;
import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author 邱亚建 2013-4-19 下午03:51:28<br>
 *         TODO Q币充值接口
 * 
 */
public class INF02010 {
	private static final Log logger = LogFactory.getLog(INF02010.class);

	public static String svcInfName = "INF02010";

	public static String execute(String in0, String in1) {
		DpInf02010Request dpRequest = null;

		DpInf02010Response resp = new DpInf02010Response();

		RespInfo respInfo = null;

		String responseCode = "";

		logger.info("INF02010请求参数：：" + in1);

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String keep = "";// 获取流水号

		String ip = "";

		String svcCode = WebSvcTool.getSvcCode(in0);

		String tmnNum = null;

		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);
		
		Long consumId = null;

		try {

			respInfo = new RespInfo(in1, "20");

			dpRequest = new DpInf02010Request(in1);
			
			// 客户端MD5校验--------------------------------------------
			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest.getStaffCode(),
					tokenValidTime);
			dpRequest.verifyByMD5(md5Key);
			TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest.getStaffCode());
			// -------------------------------------------------------------------


			// 判断有无交易查询权限
			List privList = PayCompetenceManage.payFunc(
					dpRequest.getCustCode(), dpRequest.getChannelCode());
			boolean r = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if (PrivConstant.IPOS_RECHARGE_QQ.equals(str)) {
					r = true;
					break;
				}

			}

			if (!r) {
				throw new Exception("你没有Q币充值的权限");
			}

			//关联机构验证
			if(!TCumInfoDao.verifyMerIdCustCode(dpRequest.getCustCode(), dpRequest.getMerId())){
				
				if(TCumInfoDao.getMerIdByCustCode(dpRequest.getCustCode(),dpRequest.getMerId()))
				
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
			}
			
			tmnNum = dpRequest.getTmnNum();

			keep = dpRequest.getKeep();

			ip = dpRequest.getIp();

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum,
					svcCode, "XML", "", "", "", "", OrderConstant.S0A);
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
			
			SignBankManage manage = new SignBankManage();
			String custId = manage.getCustIdByCode(dpRequest.getCustCode());
			if (null == custId || "".equals(custId)) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST,
						INFErrorDef.CUSTCODE_NOT_EXIST_DESC);
			}

			// 快捷交易验证
			VerifyConsumeEntity entity = new VerifyConsumeEntity(custId,
					dpRequest.getStaffCode(), dpRequest.getTxnAmount(),
					dpRequest.getPayPassword(), dpRequest.getChannelCode(),
					dpRequest.getTmnNum(), dpRequest.getIp());
			
			WebSvcUtil websvcutil = new WebSvcUtil();
			boolean flag = websvcutil.VerifyConsume(entity);
			
			TInfConsumeDao dao = new TInfConsumeDao();
			// 调用核心接口之前将消费记录插入到消费表
			TInfConsume c = new TInfConsume();

			consumId = dao.getConsumeId();
			c.setConsumeId(consumId);
			c.setCustId(Long.valueOf(custId));
			c.setOrderNo(dpRequest.getOrderNo());
			c.setAcctType("0007");
			c.setKeep(keep);
			c.setChannelType(dpRequest.getChannelCode());
			c.setTermId(dpRequest.getTmnNum());
			c.setActionCode("16010001");
			c.setPdLineId(String.valueOf(dao.getPdlineId()));
			c.setAmount(dpRequest.getTxnAmount());
			c.setStat(OrderConstant.S0A);
			c.setAcctDate(new Date());
			c.setRemark(dpRequest.getRemark1() + "::" + dpRequest.getRemark2());
			c.setSum_stat(websvcutil.getSum_stat());
			System.out.println("INF02010===="+websvcutil.getSum_stat());
			dao.insert(c);

			
			PackageDataSet ds = null;
			if (Double.valueOf(dpRequest.getTxnAmount()) < 1) {
				throw new Exception("金额不能少于1分钱");
			}
			// 单位转换：分转元
			String reAmount = MathTool.pointToYuan(dpRequest
					.getRechargeAmount());
			String txAmount = MathTool.pointToYuan(dpRequest.getTxnAmount());
			dpRequest.setRechargeAmount(reAmount);
			dpRequest.setTxnAmount(txAmount);

			ds = recharge(dpRequest);// 充值

			String resultCode = (String) ds.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			String responseDesc = null;
			// 返回结果为失败时，获取结果描述
			if (Long.valueOf(resultCode) == 0) {

				responseDesc = (String) ds.getParamByID("0002", "000").get(0);

				dao.updateOrderStat(consumId, OrderConstant.S0C);
				
				if(flag){
					
					dao.updateSumStat(custId);
				}
			} 

			String transSeq = ds.getByID("4002", "401");
			String txanAmount = "";
			ArrayList list = ds.getParamByID("6303", "600");
			if(list!=null && list.size()!=0){
				String txanAmounts = (String) list.get(0);// 交易金额
				// 单位转换：元转分
				txanAmount = MathTool.yuanToPoint(txanAmounts);
			}else{
				txanAmount = MathTool.yuanToPoint(dpRequest.getTxnAmount());
			}
			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, dpRequest.getOrderNo(),
					dpRequest.getRemark1(), dpRequest.getRemark2(), transSeq,
					txanAmount);

			return ResponseVerifyAdder.pkgForMD5(oXml, md5Key);

		} catch (XmlINFException spe) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
						keep, ip, svcCode, responseCode, spe.getMessage(),
						OrderConstant.S0A);
			}
			spe.setRespInfo(respInfo);

			String oXml = ExceptionHandler.toXML(spe, infId);

			return ResponseVerifyAdder.pkgForMD5(oXml, null);
		} catch (Exception e) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
						keep, ip, svcCode, responseCode, e.getMessage(),
						OrderConstant.S0A);
			}
			String oXml = ExceptionHandler.toConsumeXML(new XmlINFException(resp, e,
					respInfo), infId,consumId);

			return ResponseVerifyAdder.pkgForMD5(oXml, null);
		}
	}

//	private static boolean VerifyConsume(DpInf02010Request dpRequest,String custId) throws Exception{
//		
//		boolean flag = false;
//			TSymCustomDao buss = new TSymCustomDao();
//			// 设置累计交易
//			String maxMount = buss.getTh(custId, "T22", "TH005");
//			
//			if(!"0".equals(maxMount)){
//				
//				//设置 单笔交易
//				String one = buss.getTh(custId, "T22", "TH004");
//				
//				// 累计消费
//				String allTrade = buss.getAmountCount(custId);
//				
//				int allAmount = Integer.parseInt(dpRequest.getTxnAmount())+Integer.parseInt(allTrade);
//				
//				if (allAmount > Integer.parseInt(maxMount)||Integer.parseInt(dpRequest.getTxnAmount())>Integer.parseInt(one)) {
//				
//					// 如果输入了支付密码则校验
//					String pwd = dpRequest.getPayPassword();
//					if (Charset.isEmpty(pwd, true)) {
//						
//						throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
//								INFErrorDef.PAY_PWD_FAULT_NULL);
//			
//					}else{
//						
//						PackageDataSet dataSet = callCUM1003(dpRequest);
//						String resCode = dataSet.getByID("0001", "000");
//						if (Long.valueOf(resCode) != 0) {
//							throw new INFException(INFErrorDef.PAY_PWD_FAULT,
//									INFErrorDef.PAY_PWD_FAULT_DESC);
//						}
//					}
//					
//					// 校验通过，更新用户当月所有消费记录的状态为S0X
////					dao.updateSumStat(custId);
//					if(allAmount > Integer.parseInt(maxMount))
//					flag = true;
//				}
//				
//			}else{
//				
//				// 如果输入了支付密码则校验
//				String pwd = dpRequest.getPayPassword();
//				if (Charset.isEmpty(pwd, true)) {
//					
//					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
//							INFErrorDef.PAY_PWD_FAULT_NULL);
//		
//				}else{
//					
//					PackageDataSet dataSet = callCUM1003(dpRequest);
//					String resCode = dataSet.getByID("0001", "000");
//					if (Long.valueOf(resCode) != 0) {
//						throw new INFException(INFErrorDef.PAY_PWD_FAULT,
//								INFErrorDef.PAY_PWD_FAULT_DESC);
//					}
//				}
//			}
//			return flag;
//		}
	/**
	 * 调用SCS0001接口充值
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet recharge(DpInf02010Request dpRequest)
			throws Exception {

		String areaCode = TCumInfoDaoTemp.queryAreacodeByCustCode(dpRequest
				.getCustCode());// 所属区域编码
		TCumAcctDao acctDao = new TCumAcctDao();
		String acctCode = acctDao.getAcctCode(dpRequest.getCustCode());// 银行帐号
		String bankCode = acctDao.getBankCode(acctCode); // 银行编码[通过银行帐号查询]

		TCumInfoDao infoDao = new TCumInfoDao();
		String area_code = infoDao.getAreaCode(dpRequest.getCustCode());

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
		g401.put("4012", "全国Q币直充");// 订单描述
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
		// g401.put("4017", dpRequest.getOrderNo());// 订单号？？？
		g401.put("4017", dpRequest.getKeep());// 
		g401.put("4018", dpRequest.getTmnNum());// 操作原始来源
		g401.put("4028", dpRequest.getOrderNo());// 外部订单号
		
		g401.put("4284", dpRequest.getMerId());//机构编码     //20130628 wanght
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", dpRequest.getTxnAmount());// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", dpRequest.getTxnAmount());// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		// g404.put("4064", dpRequest.getCustCode());
		g404.put("4049", "0039");// 产品编码 // 全国新宽联0007 腾讯QQ 0031  改0039
		g404.put("4051", newActionCode);// 业务编码
		g404.put("4052", dpRequest.getQq());// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4062", "");// 当该值与不为空时，已该值作为actlist的系统参考号。否则由核心交易平台平台生成
		g404.put("4072", newActionCode);
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", dpRequest.getTxnAmount());// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", dpRequest.getTxnAmount());// 业务单应付金额
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
		g407.put("4093", ActionInfoConfig.INF02010_PRO_ID);  //测试2030 生产 5178
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
		g407.put("4093", dpRequest.getQq());
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE4");
		g407.put("4088", "H017");
		g407.put("4089", "确认充值账户");
		g407.put("4091", "01");
		g407.put("4093", dpRequest.getQq());
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
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", bankCode);// 支付机构编码
		g408.put("4099", "0007");// 账户类型编码
		g408.put("4101", acctCode);// 账号
		g408.put("4102", dpRequest.getPayPassword());// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", dpRequest.getTxnAmount());// 支付金额
		g408.put("4127", bankCode);// 前置支付机构,查卡表的bankcode
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

//	private static PackageDataSet callCUM1003(DpInf02010Request dpRequest)
//			throws Exception {
//
//		String staff = dpRequest.getStaffCode();
//
//		String verityType = "0001"; // 支付密码
//
//		String tmnNum = dpRequest.getTmnNum();
//
//		IParamGroup g200 = new ParamGroupImpl("200");
//		g200.put("2901", "2171");
//		g200.put("2902", staff);
//		g200.put("2903", "2007");
//		g200.put("2904", dpRequest.getPayPassword());
//		g200.put("2172", "0001");
//		g200.put("2173", verityType);
//		// g200.put("2025", null);
//		g200.endRow();
//
//		IParamGroup g211 = new ParamGroupImpl("211");
//		g211.put("2076", dpRequest.getChannelCode());
//		g211.put("2077", tmnNum);
//		g211.put("2078", null);
//		g211.put("2085", dpRequest.getIp());
//		g211.endRow();
//
//		IServiceCall caller = new ServiceCallImpl();
//		PackageDataSet dataSet = caller.call("BIS", "CUM1003", g200, g211);
//
//		return dataSet;
//	}
}
