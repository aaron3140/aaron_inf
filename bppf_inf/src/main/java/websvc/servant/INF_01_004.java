package websvc.servant;

import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TInfOrderBusCfgDao;
import common.dao.TPnmPartnerDao;
import common.dao.TSymSysParamDao;
import common.entity.BankAcctInfo;
import common.entity.SignOrder;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.invoker.exception.ServiceInvokeException;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.SignBankManage;
import common.service.TInfOperInLogManager;
import common.utils.ChannelCode;
import common.utils.Charset;
import common.utils.MathTool;
import common.utils.OrderConstant;
import common.utils.PrivConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpAccountManagementRequest;
import common.xml.dp.DpAccountManagementResponse;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author Tisson CWC
 *  账户管理接口
 */
public class INF_01_004 {

	 public static String svcInfName = "INF01004";

	 private static final Log logger = LogFactory.getLog(INF_01_004.class);

	public static String execute(String in0, String in1) {

		DpAccountManagementRequest dpAccountManagementRequest = null;
		RespInfo respInfo = null;	// 返回信息头
		String agentcode = null;   //商户编码
		String channelCode = null;// 交易渠道
		String tmnNum = null;    //交易终端号
		String opertype = null;   //操作类型
		String txnamount=null;	//订单金额
		String orderSeq=null;	//订单号
		String concession=null;
		String finalAmount = null;
		
		DpAccountManagementResponse resp = new DpAccountManagementResponse();
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog=null;   
		
		String responseCode = "";
		String merId = "";
		String keep = "";//		获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		
		boolean isupdate = false;
		String orderseq ="";
		
		//转账处理标识
//		boolean isTran = false;
		
//		boolean isTransFlag = false;
		
//		TbisTanOrderDao tranDao = new TbisTanOrderDao();
		
		try{
			respInfo = new RespInfo(in1, "10");	// 返回信息头
			dpAccountManagementRequest = new DpAccountManagementRequest(in1);
			
			agentcode = dpAccountManagementRequest.getAgentCode();
			channelCode = dpAccountManagementRequest.getChannelCode();
			tmnNum = dpAccountManagementRequest.getTmnNum();
			merId = dpAccountManagementRequest.getMerId();
			opertype = dpAccountManagementRequest.getOpertype();
			String acctype = dpAccountManagementRequest.getAcctType();
			//账户类型为空默认为企业账户0001
			if(acctype==null||acctype.equals("")){
				acctype = PrivConstant.ENTER_ACCT_TYPE;
				dpAccountManagementRequest.setAcctType(acctype);
			}
			orderSeq = dpAccountManagementRequest.getOrderSeq();
			txnamount = dpAccountManagementRequest.getTxnamount();
			
			keep = dpAccountManagementRequest.getKeep();
			ip = dpAccountManagementRequest.getIp();
			
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "agentCode"
					, agentcode, "orderSeq", orderSeq, "S0A");
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
					
					//准予通过
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}
			
			//关联机构验证
			if(!TCumInfoDao.verifyMerIdCustCode(dpAccountManagementRequest.getAgentCode(), dpAccountManagementRequest.getMerId())){
				
				if(TCumInfoDao.getMerIdByCustCode(dpAccountManagementRequest.getAgentCode(),dpAccountManagementRequest.getMerId()))
				
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
			}
			
//			TCumInfoDao tCumInfoDao = new TCumInfoDao();
//			String orgMerId = tCumInfoDao.getOrgMerIdFromCustCode(dpAccountManagementRequest.getAgentCode());
//			if (!dpAccountManagementRequest.getMerId().equals(orgMerId)) {
//				throw new Exception("商户编码不是该接入机构下属");
//			}
			
			/**
			 * 业务处理
			 */
			// 业务组件
			SignBankManage manage = new SignBankManage();
			// 获取ActionCode
			String actionCode = getActionCodeByOpertype(opertype);
			boolean flag = false;
			//如果是40就验证IVR权限 
			if(channelCode.equals("40")){
				if(opertype.equals("1")) {// 充值
					flag = PayCompetenceManage.getIvrFunc(dpAccountManagementRequest.getStaffCode(), PrivConstant.IVR_MANAGE_IVR_RECHARGE);
				} else if(opertype.equals("2")) {// 提现
					flag = PayCompetenceManage.getIvrFunc(dpAccountManagementRequest.getStaffCode(), PrivConstant.IVR_MANAGE_IVR_CASH);
				}
				if (!flag) {
					throw new Exception("你没有IVR操作权限");
				}
			}else{
				List privList = PayCompetenceManage.payFunc(dpAccountManagementRequest.getAgentCode(), channelCode);
				
				for (int i = 0; i < privList.size(); i++) {
					Map map = (Map)privList.get(i);
					String str = map.get("PRIV_URL").toString();
					
					if(ChannelCode.AGENT_CHANELCODE.equals(channelCode)){
						
						if(PrivConstant.IPOS_ACCT_TYPE.equals(acctype)){
							if(PrivConstant.IPOS_ACCTMNG_RECHARGE.equals(str)){
								flag = true;
								break;
							}else if(PrivConstant.IPOS_ACCTMNG_WITHDRAW.equals(str)){
								flag = true;
								break;
							}
						}else{
							if(PrivConstant.CLN_ACCTMNG_RECHARGE.equals(str)){
								flag = true;
								break;
							}else if(PrivConstant.CLN_ACCTMNG_WITHDRAW.equals(str)){
								flag = true;
								break;
							}
						}
					}else{
						if(PrivConstant.WS_ACCTMNG_WITHDRAW.equals(str)){
							flag = true;
							break;
						}else if(PrivConstant.WS_ACCTMNG_RECHARGE.equals(str)){
							flag = true;
							break;
						}
					}
					
				}
				
				if(!flag){
					throw new Exception("你没有操作权限");
				}
			}
			
//			判断有无交易查询权限
			
			
			if(actionCode == null) {
				throw new Exception("非法的操作类型");
			}
			// 获取客户ID
			String custId = manage.getCustIdByCode(agentcode);
			if(custId == null) {
				throw new Exception("该商户号不存在");
			}
			// 获取客户绑定银行卡列表
			List<BankAcctInfo> bankAcctList = manage.getBankAcctList(custId);
			if(bankAcctList==null || bankAcctList.size()==0) {
				throw new Exception("该商户的签约银行卡不存在");
			}
			if(bankAcctList.size() != 1) {
				throw new Exception("该商户存在多张签约银行卡");
			}
			// 生成授权银行卡充值/提现单据
			BankAcctInfo bankAcctInfo = bankAcctList.get(0);
			SignOrder order = new SignOrder();
			order.setMerId(merId);
			order.setKeep(keep);
			order.setChannelCode(channelCode);// 交易渠道
			order.setTmnNum(tmnNum);// 交易终端号
			order.setActionCode(actionCode);// 操作类型：充值/提现
			order.setTransSeq(orderSeq);// 交易序列号
			order.setBankAcctInfo(bankAcctInfo);// 客户绑定银行账户详细信息
			if (Double.valueOf(txnamount)<1) {
				throw new Exception("金额不能少于1分钱");
			}
			// 单位转换：分转元
			txnamount = MathTool.pointToYuan(txnamount);
			finalAmount = txnamount;
			order.setAmount(txnamount);// 交易金额
			order.setFinalAmount(txnamount);
			
			
			TPnmPartnerDao dao = new TPnmPartnerDao();
			Map<String, String> map = dao.getPrntnCodeAndPrntType(agentcode);
			
			IParamGroup g423 = new ParamGroupImpl("423");
			g423.put("4230", "0001"); 	//手续费
			g423.put("2011", dpAccountManagementRequest.getMerId());	//接入机构的对应的商户编码
			g423.put("4330", map.get("PRTN_CODE"));	//实际做交易的商户编码
			g423.put("4331", map.get("PRTN_TYPE"));	//实际做交易的商户类型
			g423.put("2002", agentcode);	//实际做交易的商户编码
			g423.put("4051", actionCode);	//业务编码
			//g423.put("4098", "866300");  //银行编码
			//g423.put("4006", "440100");  //区域编码
			g423.put("4098", bankAcctInfo.getBankCode());  //银行编码
			g423.put("4006", bankAcctInfo.getAreaCode());  //区域编码
			g423.put("4144", channelCode);  //渠道号
			g423.endRow();
			
			IParamGroup g402 = new ParamGroupImpl("402");
			g402.put("4025", txnamount);	//订单金额
			g402.put("4099", acctype);	//账户类型编码
			
			// 组成数据包,调用SCS0201接口
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet dataSet1 = null;
			String mobile = null;
			try {
				dataSet1 = caller.call("SCS", "SCS0201", g423, g402);
				String resCode = dataSet1.getByID("0001", "000");
				if (Long.valueOf(resCode) == 0) {
					String flagStr = dataSet1.getByID("4230", "423");
					if ("0001".equals(flagStr)) {
						String concessionType = dataSet1.getByID("4328", "423");
						concession = dataSet1.getByID("4329", "423");
						if(concession==null || "".equals(concession)||Double.valueOf(concession)==0){
							concession="0";
						}
						
						order.setConcession(concession);
						order.setConcessionType(concessionType);
						finalAmount = txnamount;
						double temp = 0;
						if ("DT003".equals(concessionType)) {
							temp = Double.parseDouble(txnamount) + Double.parseDouble(concession);
							finalAmount = String.valueOf(temp);
						}else{
							temp = Double.parseDouble(txnamount) - Double.parseDouble(concession);
							if (temp < 0) {
								throw new Exception("计算手续费金额后为负数");
							}
							txnamount = String.valueOf(temp);
						}
						order.setAmount(txnamount);// 交易金额
						order.setFinalAmount(finalAmount);//最终交易金额
					}
				}
				
				// 根据客户编码，调用CUM0003查询联系信息
				IParamGroup g0003_200 = new ParamGroupImpl("200"); // 包头
				g0003_200.put("2002",agentcode);
				g0003_200.endRow();

				IParamGroup g0003_002 = new ParamGroupImpl("002"); // 包头
				g0003_002.put("0011","207");
				g0003_002.endRow();
				
				// 组成数据包,调用CUM0003接口
				IServiceCall caller2 = new ServiceCallImpl();
				PackageDataSet dataSet = caller2.call("BIS","CUM0003", g0003_200,g0003_002);// 组成交易数据包,调用CUM0003接口
				
				String resultCode1 = (String) dataSet.getParamByID("0001", "000").get(0);
				//返回结果为失败时，抛出异常
				if(Long.valueOf(resultCode1) != 0) {
					String resultMsg = (String) dataSet.getParamByID("0002", "000").get(0);
					throw new Exception(resultMsg);
				}
				
				//获取客户编码的手机号				
				int count = dataSet.getParamSetNum("202");
				for(int i=0;i<count;i++){
					String code = (String)dataSet.getParamByID("2016", "202").get(i);
					if(code.equals("MOB")){
						mobile = dataSet.getByID("2018", "202");
						break;
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof ServiceInvokeException) {
					ServiceInvokeException sie  = (ServiceInvokeException)e;
					PackageDataSet dataSet2 = sie.getDataSet();
					String resCode = dataSet2.getByID("0001", "000");
					if (!"0011".equals(resCode)) {
						throw e;
					}
				}else{
					throw e;
				}
			}
			String buscode = TInfOperInLogManager.convertBussCode(dpAccountManagementRequest.getOpertype());
			orderseq = dpAccountManagementRequest.getOrderSeq();
			isupdate = TInfOperInLogManager.verifyOrder(orderseq,keep,tmnNum,svcInfName,buscode);
			
			//转账预处理
//			if(PrivConstant.ENTER_ACCT_TYPE.equals(dpAccountManagementRequest.getAcctType())&&"01".equals(dpAccountManagementRequest.getTransferFlag())){
//				
//				if(dpAccountManagementRequest.getColleCustCode().equals(dpAccountManagementRequest.getAgentCode())) {
//					
//					throw new Exception("收款方客户编码和付款方客户编码不能一样");
//				}
//				
//				isTransFlag = true;
//				
//				//收款商户关联机构验证
//				if(!TCumInfoDao.verifyMerIdCustCode(dpAccountManagementRequest.getColleCustCode(), dpAccountManagementRequest.getMerId())){
//					
//					if(TCumInfoDao.getMerIdByCustCode(dpAccountManagementRequest.getColleCustCode(),dpAccountManagementRequest.getMerId()))
//					
//						throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
//								INFErrorDef.CUSTCODE_R_NOT_MATCH_MERG_DESC);
//				}
//
//				logger.info("1..转账预处理");
//				Hashtable<String,Object> rt = new Hashtable<String,Object>();
//				rt.put("KEEP", dpAccountManagementRequest.getKeep());
//				rt.put("ORDER_CODE", dpAccountManagementRequest.getOrderSeq());
//				rt.put("PAY_CUSTCODE", dpAccountManagementRequest.getAgentCode());
//				rt.put("COLLE_CUSTCODE", dpAccountManagementRequest.getColleCustCode());
//				rt.put("PAY_MONEY", dpAccountManagementRequest.getTxnamount());
//				rt.put("RECHARGE_STAT", OrderConstant.S0P);
//				rt.put("TRAN_STAT", OrderConstant.S0A);
//				
//				tranDao.saveTanOrder(rt);
//				
//				
//			}
			// 调用接口，完成代收/代付
			PackageDataSet dataSet = manage.RechargeAndWithdrawal(agentcode, order,mobile,dpAccountManagementRequest);
     		String resultCode = (String) dataSet.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			
			//更新订单控制状态
			TInfOrderBusCfgDao cfgDao = new TInfOrderBusCfgDao();
			if(isupdate&&responseCode.equals("0000")){

            	cfgDao.updateTInfOrderStat(tmnNum, orderseq, OrderConstant.S0C);
			}
			
			String responseDesc = dataSet.getByID("0002", "000");		// 响应码描述
			String respTransSeq = dataSet.getByID("4002", "401");		// 订单号
			txnamount = dataSet.getByID("6303", "600");					// 交易金额
			
			// 单位转换：元转分
			finalAmount = MathTool.yuanToPoint(finalAmount);
			txnamount = MathTool.yuanToPoint(txnamount);
			if (!Charset.isEmpty(concession)) {
				concession = MathTool.yuanToPoint(concession);
			}else{
				concession = "";
			}
			
			//转账
//			if(PrivConstant.ENTER_ACCT_TYPE.equals(dpAccountManagementRequest.getAcctType())&&"01".equals(dpAccountManagementRequest.getTransferFlag())){
//				
//				isTran = true;
//				logger.info("3..支付成功 更新");
//				//更新充值状态
//				tranDao.updateRecSucStat(keep,respTransSeq,responseCode,responseDesc);
//				
//				//调用转账接口
//				dataSet =transfer(dpAccountManagementRequest,order);
//				
//				String tranOrderId = dataSet.getByID("4002", "401");
//			
//				logger.info("4..转账成功 更新");
//				//更新更新成功记录
//				tranDao.updateTraOrder(keep,tranOrderId);
//				
//			}
			
			//插入信息到出站日志表
//			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, responseDesc, "S0A");
		
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(),
					"SUCCESS", responseCode, responseDesc, orderSeq, respTransSeq, txnamount, finalAmount, concession, opertype,dpAccountManagementRequest.getRemark1(),dpAccountManagementRequest.getRemark2());
			
		} catch (XmlINFException spe) {
			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, spe.getMessage(), "S0A");
			}
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			
//			if(isTran){
//				
//				return ExceptionHandler.toTanXML(new XmlINFException(
//						resp, e, respInfo), id, tranDao);
//			}else{
				
				if (e instanceof SocketTimeoutException) {

					return ExceptionHandler.toXML(new XmlINFException(resp,new Exception("调用接口超时"), respInfo), id);
				}else{
					
					return ExceptionHandler.toOutOrderXML(new XmlINFException(
							resp, e, respInfo), id,null,isupdate,tmnNum, orderseq,null,false);
				}
//			}
			
//			if(tInfOperInLog!=null){
//			//插入信息到出站日志表
//			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode
//					, responseCode, e.getMessage(), "S0A");
//		}
			
//			if(tInfOperInLog!=null){
//			//插入信息到出站日志表
//			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode
//					, responseCode, e.getMessage(), "S0A");
//		}
			
		}
		
	}
	
	/**
	 * 调用SCS0001接口转账
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet transfer(DpAccountManagementRequest dpRequest,SignOrder order)
			throws Exception {
		
		TCumAcctDao acctDao = new TCumAcctDao();
		String acctCode = acctDao.getAcctCode(dpRequest.getAgentCode());// 
		
		String acctCodeR = acctDao.getAcctCode(dpRequest.getColleCustCode());// 
		
		TCumInfoDao infoDao = new TCumInfoDao();
		String area_code = infoDao.getAreaCode(dpRequest.getAgentCode());
		
		System.out.println("area_code:::"+area_code);

		String bankCode = "110000";

		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String tradeTime = df.format(new Date());
		
		String actionCode="01030001";
		
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getAgentCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", area_code);// 所属区域编码 广州地区440100
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", tradeTime);// 受理时间
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());// 
		g401.put("4018", "0.0.0.0");// 受理终端号
		g401.put("4280", dpRequest.getColleCustCode());// 收款方

		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", order.getAmount());// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", order.getAmount());// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码 // 全国新宽联0007 腾讯QQ 0031 改0039 电子售卡
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", dpRequest.getColleCustCode());//
		g404.put("4053", "1");// 业务数量
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", order.getAmount());// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", order.getAmount());// 业务单应付金额
		g405.put("4071", "101");// 费用项标识
		g405.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");
		g407.put("4088", "0301");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTCAACCODE");
		g407.put("4088", acctCodeR);
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTACCTTYPE");
		g407.put("4088", "0001");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", bankCode);// 
		g408.put("4099", dpRequest.getAcctType());// 账户类型编码
		g408.put("4101", acctCode);// 账号
		g408.put("4102", "123456");// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", order.getAmount());// 支付金额
		
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402,
				g404, g405, g407, g408);

		// 返回结果
		return dataSet;

	}
	
	public static void main(String[] args) {
		System.out.println(Double.valueOf("0.00")==0);
	}
	
	/**
	 * 根据操作类型获取ActionCode
	 * @version: 1.00
	 * @history: 2012-4-1 下午05:43:59 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param opertype
	 * @return
	 * @see
	 */
	private static String getActionCodeByOpertype(String opertype) {
		String result = null;
		if(opertype.equals("1")) {// 充值
			return SignBankManage.AC_AUTH_BANKCARD_RECHARGE;
		} else if(opertype.equals("2")) {// 提现
			return SignBankManage.AC_AUTH_BANKCARD_WITHD;
		}
		return result;
	}
	
	public static String executeForMD5(String in0, String in1){
		DpAccountManagementResponse resp = new DpAccountManagementResponse();
		RespInfo respInfo = null;				// 返回信息头
		String md5Key = null;
		
		try {
			respInfo = new RespInfo(in1, "10");	
			DpAccountManagementRequest dpRequest = new DpAccountManagementRequest(in1);
			
			//客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(dpRequest.getChannelCode())) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				 md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest.getStaffCode(),
							tokenValidTime);
				dpRequest.verifyByMD5(dpRequest.xmlSubString(in1), md5Key);
				TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest.getStaffCode());
			}
			
			PackageDataSet dataSet = callCUM1003(dpRequest);
			String responseCode = dataSet.getByID("0001", "000");
			if (Long.valueOf(responseCode) == 0) {
				String oldXml = execute(in0, in1);
				
				return ResponseVerifyAdder.pkgForMD5(oldXml, md5Key);
			}else{
				String num = dataSet.getByID("6047", "601");
				throw new INFException(INFErrorDef.PAY_PWD_FAULT,INFErrorDef.PAY_PWD_FAULT_DESC+num+"次");
			}
		} catch (Exception e) {
			
			String oXml =ExceptionHandler.toXML(new XmlINFException(
					resp, e, respInfo), null);
			
			return ResponseVerifyAdder.pkgForMD5(oXml, null);
		}
		
	}
	
	
	private static PackageDataSet callCUM1003(DpAccountManagementRequest dpRequest)throws Exception{
		
		String staff = dpRequest.getStaffCode();
		
		String verityType = "0001";	//支付密码
		
		String tmnNum = dpRequest.getTmnNum();
		
		IParamGroup g200 = new ParamGroupImpl("200");
		g200.put("2901", "2171");
		g200.put("2902", staff);
		g200.put("2903", "2007");
		g200.put("2904", dpRequest.getPassword());
		g200.put("2172", "0001");
		g200.put("2173", verityType);
//		g200.put("2025", null);	
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

}

