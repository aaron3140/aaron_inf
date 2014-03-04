package websvc.servant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.algorithm.MD5;
import common.dao.TCumAcctDao;
import common.dao.TCumAttrDao;
import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfOrderBusCfgDao;
import common.dao.TPnmPartnerDao;
import common.dao.TbisTanOrderDao;
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
import common.utils.Charset;
import common.utils.MathTool;
import common.utils.OrderConstant;
import common.utils.PaymentTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf05001Request;
import common.xml.dp.DpInf05001Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 *  代收付接口INF05001
 */
public class INF05001 {

	public static String svcInfName = "INF05001";
	private static final Log log = LogFactory.getLog(INF05001.class);
	public static String execute(String in0, String in1) {

		DpInf05001Request dpRequest = null;
		DpInf05001Response resp = new DpInf05001Response();
		RespInfo respInfo = null;				// 返回信息头
		String tmnNum = null;  
		String orderSeq = null;  
		
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode = "";
		
		String keep = "";//获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		boolean isupdate = false;
		String outordercode = "";
		
		//转账处理标识
		boolean isTran = false;
		
		boolean isTransFlag = false;

		TbisTanOrderDao tranDao = new TbisTanOrderDao();
		try{
			respInfo = new RespInfo(in1, "20");				// 返回信息头
			dpRequest = new DpInf05001Request(in1);
			tmnNum = dpRequest.getTmnNum();
			orderSeq = dpRequest.getOrderSeq();
			keep = dpRequest.getKeep();
			ip = dpRequest.getIp();
			outordercode = dpRequest.getOrderSeq();
			dpRequest.setTransferFlag("00");//暂时不支持转账
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "ORDERSEQ"
					, orderSeq, "", "", "S0A");
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
			
			String custCode = null;
			TCumAcctDao acctDao = new TCumAcctDao();			
			String acctCode = acctDao.getAcctCodeByContractId(dpRequest.getTransContractId());  //签约ID对应的银行账号
			String netCode = PaymentTool.getNetCode(dpRequest.getTransContractId(),null,dpRequest.getBranchCode()); //签约ID对应的网点编码
			if (Charset.isEmpty(netCode)) {
			      throw new INFException(INFErrorDef.CONTRACT_NOT_NETCODE,
							INFErrorDef.CONTRACT_NOT_NETCODE_DESC);
		    }
			TCumInfoDao tCumInfoDao  =  new TCumInfoDao();
			custCode = tCumInfoDao.getCustCodeFromDSF(netCode); //网点编码对应的客户编码
			if (Charset.isEmpty(custCode)) {
				throw new Exception("客户编码为空");
			}
			
			//关联机构验证
			if(!TCumInfoDao.verifyMerIdCustCode(custCode, dpRequest.getMerId())){
				
				if(TCumInfoDao.getMerIdByCustCode(custCode,dpRequest.getMerId()))
				
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
			}
			
			String cardAccNbr = PaymentTool.getTissonCardAcct(custCode); //签约ID对应的天讯账户号
			if(!dpRequest.getBankAcct().equals(acctCode)){

				throw new INFException(INFErrorDef.BANKACCT_NOT_CONTRACTACCT,
						INFErrorDef.BANKACCT_NOT_CONTRACTACCT_DESC);
			}

			String deDuctChannel = null;    //代扣渠道  
			TCumAttrDao dao = new TCumAttrDao();
            List list = dao.getDeductChannel(custCode);
            if(list!=null&&list.size()>0){
            	deDuctChannel = ((Map)list.get(0)).get("VALUE1").toString();
            }
            log.info("deDuctChannel========"+deDuctChannel+" custCode===="+custCode);
            if(deDuctChannel==null||deDuctChannel.equals("")){
            	deDuctChannel = "D02";
            	log.info("(deDuctChannel=D02)  "+deDuctChannel);
            }
			PackageDataSet ds = null;
			String stramount = dpRequest.getTxnAmount();
			if (Double.valueOf(stramount)<1) {
				throw new Exception("金额不能少于1分钱");
			}
			dpRequest.setTxnAmount(MathTool.pointToYuan(stramount));
			
			if(dpRequest.getBusiType().equals("BT002"))
				throw new Exception("暂时不支持代付交易");
			
            //权限判断
			boolean flag=false;
			List privList = PayCompetenceManage.payFunc(custCode,dpRequest.getChannelCode());
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();
				if(dpRequest.getBusiType().equals("BT001")&&"ws_ColNPay_Collection".equals(str)){                           //
					flag = true;
				}
				if(dpRequest.getBusiType().equals("BT002")&&"ws_ColNPay_Payagent".equals(str)){                           //
					flag = true;
				}
			}
			if (!flag) {
				throw new Exception("没有签约实时代收付权限");
			}
			
			//转账预处理
			if(deDuctChannel.equals("D01")&&"01".equals(dpRequest.getTransferFlag())){
				
				if (dpRequest.getColleCustCode()
						.equals(custCode)) {

					throw new Exception("收款方客户编码和付款方客户编码不能一样");
				}
				
				isTransFlag = true;
				
				//收款商户关联机构验证
				if(!TCumInfoDao.verifyMerIdCustCode(dpRequest.getColleCustCode(), dpRequest.getMerId())){
					
					if(TCumInfoDao.getMerIdByCustCode(dpRequest.getColleCustCode(),dpRequest.getMerId()))
					
						throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
								INFErrorDef.CUSTCODE_R_NOT_MATCH_MERG_DESC);
				}

				log.info("1..转账预处理");
				Hashtable<String,Object> rt = new Hashtable<String,Object>();
				rt.put("KEEP", dpRequest.getKeep());
				rt.put("ORDER_CODE", dpRequest.getOrderSeq());
				rt.put("PAY_CUSTCODE", custCode);
				rt.put("COLLE_CUSTCODE", dpRequest.getColleCustCode());
				rt.put("PAY_MONEY", stramount);
				rt.put("RECHARGE_STAT", OrderConstant.S0P);
				rt.put("TRAN_STAT", OrderConstant.S0A);
				
				tranDao.saveTanOrder(rt);
				
				
			}
			
			String buscode = TInfOperInLogManager.convetBussType(dpRequest.getBusiType());
			isupdate = TInfOperInLogManager.verifyOrder(outordercode,keep,tmnNum,svcInfName,buscode);
			try {
				if(deDuctChannel==null || deDuctChannel.equals("D02")){        //代扣渠道1 代收
				   ds = bt001(dpRequest,custCode,netCode);
				}else if(deDuctChannel.equals("D01")){         //代扣渠道2  代收入账
					if(cardAccNbr==null){

						throw new INFException(INFErrorDef.BANKACCT_NOT_cardAccNbr,
								INFErrorDef.BANKACCT_NOT_cardAccNbr_DESC);
					}
					ds = bt002(dpRequest,custCode,cardAccNbr,netCode);
				}
			} catch (Exception e) {

				log.info(e.getMessage());
				if(ds==null)
				throw e;
			}
			
			responseCode = (String) ds.getParamByID("0001", "000").get(0);
			
			//更新订单控制状态
			TInfOrderBusCfgDao cfgDao = new TInfOrderBusCfgDao();
			if(isupdate&&responseCode.equals("0000")){

            	cfgDao.updateTInfOrderStat(tmnNum, outordercode, OrderConstant.S0C);
			}
//			else if(isupdate&&!ErrorProcess.isTimeOut(responseCode)){
//
//            	cfgDao.updateTInfOrderStat(tmnNum, outordercode, OrderConstant.S0F);
//			}
			
			//返回结果为失败时，抛出异常
//			if(Long.valueOf(responseCode) != 0) {
//				String resultMsg = (String) ds.getParamByID("0002", "000").get(0);
//				
//				//转账预处理
//				if(deDuctChannel.equals("D01")&&"01".equals(dpRequest.getTransferFlag())){
//					
//					log.info("2..支付失败 更新");
//					tranDao.updateRecStat(keep,responseCode,resultMsg);
//				}
//				
//				throw new Exception(resultMsg);
//			}
			
			String responseDesc = ds.getByID("0002", "000");// 获取接口的000组的0002参数
	
			String orderId = ds.getByID("4002", "401");
			
			//转账
			if(deDuctChannel.equals("D01")&&"01".equals(dpRequest.getTransferFlag())){
				
				isTran = true;
				log.info("3..支付成功 更新");
				//更新充值状态
				tranDao.updateRecSucStat(keep,orderId,responseCode,responseDesc);
				
				//调用转账接口
				ds =transfer(dpRequest,custCode);
				
				String tranOrderId = ds.getByID("4002", "401");
			
				log.info("4..转账成功 更新");
				//更新更新成功记录
				tranDao.updateTraOrder(keep,tranOrderId);
				
			}
			
			resp = new DpInf05001Response();
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), 
					"SUCCESS", responseCode, responseDesc,orderId ,outordercode);
		} catch (XmlINFException spe) {
//			if(tInfOperInLog!=null){
//				//插入信息到出站日志表
//				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, spe.getMessage(), "S0A");
//			}
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
//			if(tInfOperInLog!=null){
//				//插入信息到出站日志表
//				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
//			}
			
			if(isTran){
				
				return ExceptionHandler.toTanXML(new XmlINFException(
						resp, e, respInfo), id, tranDao);
			}else{
				return ExceptionHandler.toOutOrderXML(new XmlINFException(
						resp, e, respInfo), id,null,isupdate,tmnNum, outordercode,tranDao,isTransFlag);
			}
			
		}
	}
	
	/**
	 * 调用SCS0001接口转账
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet transfer(DpInf05001Request dpRequest,
			String custCode)
			throws Exception {

		TCumAcctDao acctDao = new TCumAcctDao();
		String acctCode = acctDao.getAcctCode(custCode);// 
		
		String acctCodeR = acctDao.getAcctCode(dpRequest.getColleCustCode());// 
		
		TCumInfoDao infoDao = new TCumInfoDao();
		String area_code = infoDao.getAreaCode(custCode);
		
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
		g401.put("4004", custCode);// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", area_code);// 所属区域编码 广州地区440100
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", tradeTime);// 受理时间
//		g401.put("4012", "电子售卡");// 订单描述
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());// 
		g401.put("4018", "0.0.0.0");// 受理终端号
		g401.put("4280", dpRequest.getColleCustCode());// 收款方

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
		g404.put("4049", "0001");// 产品编码 // 全国新宽联0007 腾讯QQ 0031 改0039 电子售卡
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", dpRequest.getColleCustCode());//
		g404.put("4053", "1");// 业务数量
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", dpRequest.getTxnAmount());// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", dpRequest.getTxnAmount());// 业务单应付金额
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
		g408.put("4099", "0001");// 账户类型编码
		g408.put("4101", acctCode);// 账号
		g408.put("4102", "123456");// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", dpRequest.getTxnAmount());// 支付金额
		
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402,
				g404, g405, g407, g408);

		// 返回结果
		return dataSet;

	}
	
	private static PackageDataSet bt001(DpInf05001Request dpRequest, String custCode, String netCode) throws Exception {
		
		//查询付款方的详细信息
		
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String tradeTime = df.format(new Date());

		// 根据客户编码，调用CUM0003查询联系信息
		IParamGroup g0003_200 = new ParamGroupImpl("200"); // 包头
		g0003_200.put("2002",netCode);
		g0003_200.endRow();
		

		IParamGroup g0003_002 = new ParamGroupImpl("002"); // 包头
		g0003_002.put("0011","201");
		g0003_002.endRow();
		g0003_002.put("0011","202");
		g0003_002.endRow();
		g0003_002.put("0011","203");
		g0003_002.endRow();
		g0003_002.put("0011","204");
		g0003_002.endRow();
		g0003_002.put("0011","206");
		g0003_002.endRow();
		g0003_002.put("0011","207");
		g0003_002.endRow();
		g0003_002.put("0011","213");
		g0003_002.endRow();
		
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet DataSet = caller.call("BIS","CUM0003", g0003_200,g0003_002);// 组成交易数据包,调用CUM0003接口
		
		// 获取201组
		String cer_id= DataSet.getByID("2009","201");
		String cer_num=DataSet.getByID("2010","201");
		
		//获取206组
		String objCode = DataSet.getByID("2045","206");
		
		// 获取207组卡户号
		int count = DataSet.getParamSetNum("207");
		String bankCode = "";
		String bankAcctNbr = "";
		String bankName = "";
		String bankAcctName = "";
		String bankfilialeName = "";
		String bankBelong = "";
		String bankSubCode = "";
		String card_id = "";
		String cardType = ""; //卡折标识
		String signContractId="";
		String addr = "";
		String mobile = "";
		String privateFlag ="";  //对公对私标识
		for (int i = 0; i < count; i++) {
			// 获取卡户类型
			String cardAcctType=(String)DataSet.getParamByID("2048","207").get(i);
			// 获取银行卡户号
			if("ACCT001".equals(cardAcctType)){
				bankAcctNbr=(String)DataSet.getParamByID("2049","207").get(i);
				bankName=(String)DataSet.getParamByID("C050","207").get(i);
				bankCode=(String)DataSet.getParamByID("2050","207").get(i);
				bankAcctName=(String)DataSet.getParamByID("2051","207").get(i);
				card_id=(String)DataSet.getParamByID("C052","207").get(i);
				cardType=(String)DataSet.getParamByID("2052","207").get(i);
				
			}
		}
		
		// 获取216组客户扩展信息
		int count3 = DataSet.getParamSetNum("216");
		for (int i = 0; i < count3; i++) {
			// 联系类型编码
			String attrType=(String)DataSet.getParamByID("2069","216").get(i);
			// 获取银行
			if("2569".equals(attrType)){
				bankName=(String)DataSet.getParamByID("2071","216").get(i);
			}
			// 获取签约id
			if("2810".equals(attrType)){
				signContractId=(String)DataSet.getParamByID("2071","216").get(i);
			}
			// 开户联系电话
			if("2814".equals(attrType)){
				mobile=(String)DataSet.getParamByID("2071","216").get(i);
			}
			// 对公对私标识
			if("2570".equals(attrType)){
				privateFlag=(String)DataSet.getParamByID("2071","216").get(i);
			}
			// 证件类型
			if("2566".equals(attrType)){
				cer_id=(String)DataSet.getParamByID("2071","216").get(i);
			}
			// 证件号
			if("2567".equals(attrType)){
				cer_num=(String)DataSet.getParamByID("2071","216").get(i);
			}
			// 账户归属地
			if("2568".equals(attrType)){
				bankBelong = (String)DataSet.getParamByID("2071","216").get(i);
			}
			// 联系地址
			if("8038".equals(attrType)){
				addr=(String)DataSet.getParamByID("2071","216").get(i);
			}
		}
		
		//开户银行的基本信息：以页面传递过来的参数为准，只有页面传递过来为null时，使用CUM0003查询到的参数值
//		String areaCode=comparedTwo(dpRequest.getAreacode(), bankBelong);  //账户归属地
		
		TPnmPartnerDao dao = new TPnmPartnerDao();
		String patnercode = dao.getPrtnCode301ByCustCode(custCode);
		log.info("商户编码:=======45======="+patnercode);
//		一次路由
		TransManage tm = new TransManage();
		PackageDataSet dataSet2 = tm.firstRoute(custCode, bankBelong, dpRequest.getChannelCode(), "15010001", patnercode
				, dpRequest.getTmnNum(), dpRequest.getTxnAmount(), "PT1004",bankCode );
		
		String newActionCode = dataSet2.getByID("4051","423");
		String newOrgCode = dataSet2.getByID("4098","423");
//		String newActionCode = "15010001";
//		String newOrgCode = "86000002";
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", netCode);// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", bankBelong);// 所属区域编码
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", tradeTime);// 受理时间
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());// 终端流水号
		g401.put("4028", dpRequest.getOrderSeq());// 订单号
		g401.put("4018", "0.0.0.0");// 操作原始来源
		
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
		g404.put("4049", "0015");// 产品编码
		g404.put("4051", newActionCode);// 业务编码
		if(objCode != null&& !objCode.equals(""))
		   g404.put("4052", objCode);// 业务对象
		g404.put("4053", "1");// 业务数量
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
		
		if (!Charset.isEmpty(cer_id)) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_CERTID");// 证件类型
			g407.put("4088", cer_id);//发送机构标识码
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}		
		
		if (!Charset.isEmpty(cer_num)) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_CERTCODE");// 发送机构标识码
			g407.put("4088", cer_num);//证件号码
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}
		
		if (!Charset.isEmpty(bankCode)) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_BANKID");// 发送机构标识码
			g407.put("4088", bankCode);//银行代码
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}
		

		if (!Charset.isEmpty(cardType)) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_BANKCARDID");// 卡折标识
			g407.put("4088", cardType);//卡折标识编码
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}
		
		if (!Charset.isEmpty(mobile)) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_PHONENUM");// 联系电话
			g407.put("4088", mobile);// 联系电话
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}
		
		if (!Charset.isEmpty(addr)) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_ADDREASS");// 联系地址
			g407.put("4088", addr);// 联系地址
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}
		
		if (!Charset.isEmpty(privateFlag)) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_BANKCARDTYPE");// 对公对私标识
			g407.put("4088", privateFlag);// 对公对私标识
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}
		
		if (!Charset.isEmpty(bankAcctName)) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_BANKNAME");// 银行帐号户名
			g407.put("4088", bankAcctName);//银行帐号户名
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}
		
		if (!Charset.isEmpty(bankBelong)) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_BANKBELONG");// 银行帐号归属地
			g407.put("4088", bankBelong);//银行帐号归属地
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "NET_SIGNID");// 业务属性编码
		g407.put("4088", dpRequest.getTransContractId());//签约id
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_VERIFYTYPE");// 代收付类型
		g407.put("4088", "0000");//发送机构标识码
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT1004");// 支付方式编码
		g408.put("4098", newOrgCode);// 支付机构编码	
		g408.put("4099", "110810");// 账户类型编码
		g408.put("4100", bankName);// 开户名称
		g408.put("4101", dpRequest.getBankAcct());// 账号
		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", dpRequest.getTxnAmount());// 支付金额
		g408.put("4127", bankCode);// 前置支付机构
		g408.endRow();
		
		// 组成数据包,调用SCS0001接口
		PackageDataSet ds= caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);
		
		// 返回结果
		return ds;

	}

	private static PackageDataSet bt002(DpInf05001Request dpRequest, String custCode, String cardAccNbr, String netCode) throws Exception {
			
           //查询付款方的详细信息
		
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			String tradeTime = df.format(new Date());

			// 根据客户编码，调用CUM0003查询联系信息
			IParamGroup g0003_200 = new ParamGroupImpl("200"); // 包头
			g0003_200.put("2002",netCode);
			g0003_200.endRow();
			

			IParamGroup g0003_002 = new ParamGroupImpl("002"); // 包头
			g0003_002.put("0011","201");
			g0003_002.endRow();
			g0003_002.put("0011","202");
			g0003_002.endRow();
			g0003_002.put("0011","203");
			g0003_002.endRow();
			g0003_002.put("0011","204");
			g0003_002.endRow();
			g0003_002.put("0011","206");
			g0003_002.endRow();
			g0003_002.put("0011","207");
			g0003_002.endRow();
			g0003_002.put("0011","213");
			g0003_002.endRow();
			
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet DataSet = caller.call("BIS","CUM0003", g0003_200,g0003_002);// 组成交易数据包,调用CUM0003接口
			
			// 获取201组
			String cer_id= DataSet.getByID("2009","201");
			String cer_num=DataSet.getByID("2010","201");
			
			//获取206组
			String objCode = DataSet.getByID("2045","206");
			
			// 获取207组卡户号
			int count = DataSet.getParamSetNum("207");
			String bankCode = "";
			String bankAcctNbr = "";
			String bankName = "";
			String bankAcctName = "";
			String bankfilialeName = "";
			String bankBelong = "";
			String bankSubCode = "";
			String card_id = "";
			String cardType = ""; //卡折标识
			String signContractId="";
			String addr = "";
			String mobile = "";
			String privateFlag ="";  //对公对私标识
			for (int i = 0; i < count; i++) {
				// 获取卡户类型
				 String cardAcctType=(String)DataSet.getParamByID("2048","207").get(i);
				// 获取银行卡户号
				if("ACCT001".equals(cardAcctType)){
					bankAcctNbr=(String)DataSet.getParamByID("2049","207").get(i);
					bankName=(String)DataSet.getParamByID("C050","207").get(i);
					bankCode=(String)DataSet.getParamByID("2050","207").get(i);
					bankAcctName=(String)DataSet.getParamByID("2051","207").get(i);
					card_id=(String)DataSet.getParamByID("C052","207").get(i);
					cardType=(String)DataSet.getParamByID("2052","207").get(i);
				}
			}
			
			// 获取216组客户扩展信息
			int count3 = DataSet.getParamSetNum("216");
			for (int i = 0; i < count3; i++) {
				// 联系类型编码
				String attrType=(String)DataSet.getParamByID("2069","216").get(i);
				// 获取银行
				if("2569".equals(attrType)){
					bankName=(String)DataSet.getParamByID("2071","216").get(i);
				}
				// 获取签约id
				if("2810".equals(attrType)){
					signContractId=(String)DataSet.getParamByID("2071","216").get(i);
				}
				// 开户联系电话
				if("2814".equals(attrType)){
					mobile=(String)DataSet.getParamByID("2071","216").get(i);
				}
				// 对公对私标识
				if("2570".equals(attrType)){
					privateFlag=(String)DataSet.getParamByID("2071","216").get(i);
				}
				// 证件类型
				if("2566".equals(attrType)){
					cer_id=(String)DataSet.getParamByID("2071","216").get(i);
				}
				// 证件号
				if("2567".equals(attrType)){
					cer_num=(String)DataSet.getParamByID("2071","216").get(i);
				}
				// 账户归属地
				if("2568".equals(attrType)){
					bankBelong = (String)DataSet.getParamByID("2071","216").get(i);
				}
				// 联系地址
				if("8038".equals(attrType)){
					addr=(String)DataSet.getParamByID("2071","216").get(i);
				}
			}
			
			//开户银行的基本信息：以页面传递过来的参数为准，只有页面传递过来为null时，使用CUM0003查询到的参数值
//			String areaCode=comparedTwo(dpRequest.getAreacode(), bankBelong);  //账户归属地
			TPnmPartnerDao dao = new TPnmPartnerDao();
			String patnercode = dao.getPrtnCode301ByCustCode(custCode);
//			一次路由
			TransManage tm = new TransManage();
			PackageDataSet dataSet2 = tm.firstRoute(custCode, bankBelong, dpRequest.getChannelCode(), "15010008", patnercode
					, dpRequest.getTmnNum(), dpRequest.getTxnAmount(), "PT1004",bankCode );
			
			String newActionCode = dataSet2.getByID("4051","423");
			String newOrgCode = dataSet2.getByID("4098","423");
			System.out.println("newActionCode业务编码:==========="+newActionCode);
//			String newOrgCode = "86000002";
			/**
			 * 调用SCS0001,完成交易操作
			 */
			// 订单受理信息
			IParamGroup g401 = new ParamGroupImpl("401");
			g401.put("4004", netCode);// 客户编码
			g401.put("4005", "OT001");// 订单类型编码：业务类订单
			g401.put("4006", bankBelong);// 所属区域编码
			g401.put("4007", dpRequest.getTmnNum());// 受理终端号
			g401.put("4008", tradeTime);// 受理时间
			g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
			g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
			g401.put("4017", dpRequest.getKeep());// 终端流水号
			g401.put("4028", dpRequest.getOrderSeq());// 订单号
			g401.put("4018", "0.0.0.0");// 操作原始来源
			
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
			g404.put("4049", "0015");// 产品编码
			g404.put("4051", newActionCode);// 业务编码	
			if(objCode != null&& !objCode.equals(""))
			    g404.put("4052", objCode);// 业务对象
			g404.put("4053", "1");// 业务数量
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
			
			if (!Charset.isEmpty(cer_id)) {
				g407.put("4047", "1");// 业务单序号
				g407.put("4051", newActionCode);// 业务编码
				g407.put("4087", "SCS_CERTID");// 证件类型
				g407.put("4088", cer_id);//发送机构标识码
				g407.put("4080", "0");// 控制标识
				g407.endRow();
			}
			
			if (!Charset.isEmpty(cer_num)) {
				g407.put("4047", "1");// 业务单序号
				g407.put("4051", newActionCode);// 业务编码
				g407.put("4087", "SCS_CERTCODE");// 发送机构标识码
				g407.put("4088", cer_num);//证件号码
				g407.put("4080", "0");// 控制标识
				g407.endRow();
			}
			
			if (!Charset.isEmpty(bankCode)) {
				g407.put("4047", "1");// 业务单序号
				g407.put("4051", newActionCode);// 业务编码
				g407.put("4087", "SCS_BANKID");// 发送机构标识码
				g407.put("4088", bankCode);//银行代码
				g407.put("4080", "0");// 控制标识
				g407.endRow();
			}
			

			if (!Charset.isEmpty(cardType)) {
				g407.put("4047", "1");// 业务单序号
				g407.put("4051", newActionCode);// 业务编码
				g407.put("4087", "SCS_BANKCARDID");// 卡折标识
				g407.put("4088", cardType);//卡折标识编码
				g407.put("4080", "0");// 控制标识
				g407.endRow();
			}
			
			if (!Charset.isEmpty(mobile)) {
				g407.put("4047", "1");// 业务单序号
				g407.put("4051", newActionCode);// 业务编码
				g407.put("4087", "SCS_PHONENUM");// 联系电话
				g407.put("4088", mobile);// 联系电话
				g407.put("4080", "1");// 控制标识
				g407.endRow();
			}
			
			if (!Charset.isEmpty(addr)) {
				g407.put("4047", "1");// 业务单序号
				g407.put("4051", newActionCode);// 业务编码
				g407.put("4087", "SCS_ADDREASS");// 联系地址
				g407.put("4088", addr);// 联系地址
				g407.put("4080", "0");// 控制标识
				g407.endRow();
			}
			
			if (!Charset.isEmpty(privateFlag)) {
				g407.put("4047", "1");// 业务单序号
				g407.put("4051", newActionCode);// 业务编码
				g407.put("4087", "SCS_BANKCARDTYPE");// 对公对私标识
				g407.put("4088", privateFlag);// 对公对私标识
				g407.put("4080", "0");// 控制标识
				g407.endRow();
			}
			
			if (!Charset.isEmpty(bankAcctName)) {
				g407.put("4047", "1");// 业务单序号
				g407.put("4051", newActionCode);// 业务编码
				g407.put("4087", "SCS_BANKNAME");// 银行帐号户名
				g407.put("4088", bankAcctName);//银行帐号户名
				g407.put("4080", "0");// 控制标识
				g407.endRow();
			}
			
			if (!Charset.isEmpty(bankBelong)) {
				g407.put("4047", "1");// 业务单序号
				g407.put("4051", newActionCode);// 业务编码
				g407.put("4087", "SCS_BANKBELONG");// 银行帐号归属地
				g407.put("4088", bankBelong);//银行帐号归属地
				g407.put("4080", "0");// 控制标识
				g407.endRow();
			}
			
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "NET_SIGNID");// 业务属性编码
			g407.put("4088", dpRequest.getTransContractId());//签约id
			g407.put("4080", "0");// 控制标识
			g407.endRow();
			
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_VERIFYTYPE");// 代收付类型
			g407.put("4088", "0000");//发送机构标识码
			g407.put("4080", "0");// 控制标识
			g407.endRow();
			 
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_DEALTYPE");// 处理码
			g407.put("4088", "0100");//发送机构标识码
			g407.put("4080", "0");// 控制标识
			g407.endRow();
			
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_DSTCAACCODE");// 企业账户号
			g407.put("4088", cardAccNbr);//发送机构标识码
			g407.put("4080", "0");// 控制标识
			g407.endRow();
			
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_DSTACCTTYPE");// 企业账户类型
			g407.put("4088", "0001");//发送机构标识码
			g407.put("4080", "0");// 控制标识
			g407.endRow();
			
			// 支付单信息
			IParamGroup g408 = new ParamGroupImpl("408");
			g408.put("4103", "1");// 扣款顺序号
			g408.put("4097", "PT1004");// 支付方式编码
			g408.put("4098", newOrgCode);// 支付机构编码	
			g408.put("4099", "110810");// 账户类型编码
			g408.put("4100", bankName);// 开户名称
			g408.put("4101", dpRequest.getBankAcct());// 账号
			g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
			g408.put("4021", "0001");// 币种编码
			g408.put("4104", dpRequest.getTxnAmount());// 支付金额
			g408.put("4127", bankCode);// 前置支付机构
			g408.endRow();
			
			// 组成数据包,调用SCS0001接口
			PackageDataSet ds= caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);
			
			// 返回结果
			return ds;

		

	}

	/**
	 * 比较两个字符串，返回非空的字符串;
	 * 如果两个都不为空返回str1;
	 * 如果都为空就返回null;
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String comparedTwo(String str1,String str2){
		boolean flag1 = false;
		boolean flag2 = false;
		if(str1!=null&&!str1.equals("")){
			flag1 = true;
		}
		if(str2!=null&&!str2.equals("")){
			flag2 = true;		
		}
		if(flag1){
			return str1;
		}else if(!flag1&&flag2){
			return str2;
		}else{
		 return null;
		}
	}
	
}
