package websvc.servant;


import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.algorithm.MD5;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TOppPreOrderDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.ChannelCode;
import common.utils.Charset;
import common.utils.MathTool;
import common.utils.WebSvcTool;
import common.xml.CommonReqAbs;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf05004Request;
import common.xml.dp.DpInf05004Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 付款接口
 * @author Administrator
 *
 */
//测试用  merid  8600000000169700
//测试用  
//测试用  tmnnum 90000163
public class INF05004 {
     
	private static final Log logger = LogFactory.getLog(INF05004.class);
	
	public static String svcInfName = "INF05004";

	public static String executeForMD5(String in0, String in1){
		DpInf05004Response resp = new DpInf05004Response();
		RespInfo respInfo = null;				// 返回信息头
		String md5Key = null;
		try {
			respInfo = new RespInfo(in1, "10");	
			DpInf05004Request dpRequest = new DpInf05004Request(in1);
			//客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(dpRequest.getChannelCode())) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest.getStaffCode(), tokenValidTime);
				dpRequest.verifyByMD5(dpRequest.xmlSubString(in1), md5Key);
				TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest.getStaffCode());
			}
			PackageDataSet dataSet = callCUM1003(dpRequest);
			String responseCode = dataSet.getByID("0001", "000");
			if (Long.valueOf(responseCode) == 0) {
				String oldXml = execute(in0, in1);				
				return ResponseVerifyAdder.pkgForMD5(oldXml, md5Key);
			}else{
				throw new Exception("密码校验失败!");
			}			
			
		} catch (Exception e) {
			return ExceptionHandler.toXML(new XmlINFException(
					resp, e, respInfo), null);
		}
	}
	
	public static String execute(String in0, String in1) {
		//Long pk = null;
		DpInf05004Request dpRequest = null;
		DpInf05004Response resp = null;
		RespInfo respInfo = null;				// 返回信息头
		String tmnNum = null;  
		String preOrderSeq = null;             //预受理单号
		String custCode=null;                 //商户编码
		
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode = "";
		
		String keep = "";//获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			respInfo = new RespInfo(in1, "10");				// 返回信息头
			dpRequest = new DpInf05004Request(in1);		
			tmnNum = dpRequest.getTmnNum();
			keep = dpRequest.getKeep();
			ip = dpRequest.getIp();
			preOrderSeq = dpRequest.getPreOrderSeq();
			custCode = dpRequest.getCustCode();
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "ORDERSEQ"
					, preOrderSeq, "", "", "S0A");
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
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}
			
			//付款权限判断
			boolean flag=false;
			List privList = PayCompetenceManage.payFunc(custCode);
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();
				if("cln_payment".equals(str)){                           // 付款的权限假设为：xxxx
					flag = true;
				}
			}
			if (!flag) {
				throw new Exception("没有付款权限");
			}
			
			
			//根据登陆用户名，查询该用户的角色，以及该角色所对应的权限
			//来判断该用户：单笔交易限额和日交易限额
//			String staffCode = dpRequest.getStaffCode();
//			String sql = "select distinct r.role_id from t_sym_staff s,t_sym_staffrole r where s.staff_code='"+staffCode+"'";
//			List list=dao.queryForList(sql);						
			
			//根据预受理单号 查出一条信息
			TOppPreOrderDao dao= new TOppPreOrderDao();	
			List list= dao.getTOppPreOrder(preOrderSeq);
			Map map = new HashMap();
			if(list.size()>0){
			  map=(Map)list.get(0);
			}else{
				throw new Exception("预受理订单号输入不正确");
			}
			 String payeeCode = (String)map.get("OBJ_CODE");					// 收款商户编码
			 String custCode2 = (String)map.get("CUST_CODE");					// 收款商户编码
			 String custStat = (String)map.get("CUST_STAT");					// 发起方审核状态
			 String actionCode = (String)map.get("ACTION_CODE");				// 操作编码
			 String orderId = (String)map.get("ORDER_ID");	                     // 订单编码
			 Date acctDate= (Date)map.get("ACCT_DATE");
			 BigDecimal a = (BigDecimal)map.get("PAY_MONEY");                   //交易金额
			 String amount=a.toString();			 
//			 String preId=(String)map.get("PRE_ID");                    // 预处理订单ID号			 
			 if(!custCode2.equals(custCode)){
				 throw new Exception("你的收款商户编码输入不正确");
			 }
//			 boolean roleFlag=false;
//			 for(int i=0;i<list.size();i++){
//				 Map map11=(Map)list.get(i);
//				 String roleId = ((BigDecimal)map11.get("ROLE_ID")).toString();
//				 if("225".equals(roleId)){
//						//查询单笔交易限额
//						String singlePaySql = "select cust_id, value1 from t_cum_attr where attr_id=10002 and cust_id=" +
//								"( select distinct cust_id from t_cum_info where cust_code='"+custCode+"' )";	
//                       List list1=dao.queryForList(singlePaySql);
//                        String value1=map1.get("value1").toString();
//                        String custId=map1.get("cust_id").toString();
//						
//						//查询日交易限额
//						String dayPaySQL = "select value1 from t_cum_attr where cust_id="+custId+" and attr_id=10001"; 
//						Map map2 = dao.queryForMap(dayPaySQL);
//						String dayPayLimit = (String) map2.get("value1");
//						
//						//查询日付款总金额
//						String totalPaySQL= "select sum(daypayed) as daypayed from (select sum(b.pay_money) daypayed from t_opp_preorder a,t_opp_ordfee b" +
//								" where a.pre_id=b.pre_id and b.stat='S0A' and a.stat='S0A' " +
//								"and trunc(a.eff_date)=trunc(sysdate) and cust_code='ti04@189.com' " +
//								" union all" +
//								" select sum(b.pay_money) from t_opp_preorder a,t_opp_ordfee b where a.pre_id=b.pre_id" +
//								" and b.stat='S0A' and a.stat='S0A'" +
//								"and cust_code='"+custCode+"' and a.pre_Id='"+preId+"') t;";
//						Map map3=dao.queryForMap(totalPaySQL);
//						String totalPayLimit=(String)map3.get("DAYPAYED");
//						
//						if(Double.valueOf(value1)<Double.valueOf(amount)){
//							throw new Exception("单笔交易金额超过限定金额值");
//						 }
//						if(Double.valueOf(dayPayLimit)<Double.valueOf(totalPayLimit)){
//							throw new Exception("你今天的交易金额已经超出日付款交易限额");
//						}
//						roleFlag=true;
//						break;
//					}else if("224".equals(roleId)){
//						roleFlag=true;
//						 break;
//					}
//			     }
//			  	  if(!roleFlag){
//			  		  throw new Exception("你不是操作员或管理员，没有权限进行付款操作");
//			  	  }
			      
					 PackageDataSet dataSet = null;			// 调用接口返回结果
					 boolean isSecuredTransVerify = false;	// 是否担保交易确认
					 
					 //判断是否是担保交易
//					 if("0103005".equals(actionCode) && "S0C".equals(custStat)){
//						 isSecuredTransVerify=true;
//					 }
//					 if(isSecuredTransVerify){
//						 //担保交易，订单编码不能为空
//						 if(orderId == null || "".equals(orderId)) {
//								throw new Exception("订单编码不能为空");
//							}
//					 }
					 
						// 获取付款客户的资金账户号
						String cardAcctNbr = getTissonCardAcct(custCode);
						if(cardAcctNbr == null) {
							throw new Exception("付款客户的卡户不存在");
						}
						
						// 获取收款客户的资金账户号
						String payeeCardAcctNbr = getTissonCardAcct(payeeCode);
						if(payeeCardAcctNbr == null) {
							throw new Exception("收款客户的卡户不存在");
						}
						
		//				 操作类型为担保交易确认
//						if(isSecuredTransVerify) {
//							// 交易确认/交易取消流程
//							dataSet = transVerifyOrCancle(map, cardAcctNbr, payeeCardAcctNbr, isSecuredTransVerify, amount);
//						} else {
						// 一般交易流程
							//1. 订单支付-即时到账
//							if(actionCode.equals("01030003")||actionCode.equals("01030001")) {
								dataSet = transProcess(map, cardAcctNbr, payeeCardAcctNbr, amount,keep,dpRequest);
								
							//2. 订单支付-担保交易申请
//							}else if(actionCode.equals("01030005")){
//								dataSet = transGuaranteeProcess(map, cardAcctNbr, payeeCardAcctNbr, amount);
//							}
//						}
						
						// 获取返回结果				
						responseCode = dataSet.getByID("0001", "000");		           // 响应码
						String responseDesc = dataSet.getByID("0002", "000");	       // 响应码描述
						orderId = dataSet.getByID("4002", "401");		               // 订单号
						String transSeq=dataSet.getByID("6333", "600");                //交易流水号        
		                String objCode = payeeCode;                                    //收款账户编码
					    amount=dataSet.getByID("6303", "600");                         //交易金额
						String reqData=getDate(acctDate);                 //发起请求日期
						
						//把取回的orderid,和处理时间 再更新预处理单表										
						dao.update(orderId, preOrderSeq);
				         // 插入信息到出站信息表
						sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, 
								svcCode, responseDesc, responseDesc, "S0A");
						
						resp = new DpInf05004Response();
						if("0000".equals(responseCode)){
							responseCode="000000";
						}
						amount = MathTool.yuanToPoint(amount);
						return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), 
								"SUCCESS", responseCode,responseDesc,transSeq,preOrderSeq,custCode,objCode,amount,reqData);										
				
		} catch (XmlINFException spe) {
			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, spe.getMessage(), "S0A");
			}
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);		
		}catch(Exception e){
				if(tInfOperInLog!=null){
				   //插入信息到出站信息表			
				  sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, 
						svcCode, responseCode, e.getMessage(), "S0A");
				}
				return ExceptionHandler.toXML(new XmlINFException(
						resp, e, respInfo), id);
			}		
		}

	/**
	 * 调用CUM0002,根据客户编码获得该客户天讯资金账户号
	 */
	private static String getTissonCardAcct(String agentCode) throws Exception {
		
		// 查询明细信息
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "2002");// 查询条件：客户编码
		g002.put("0022", agentCode);// 查询条件值
		
		// 组成数据包,调用CUM0002接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet packageDataSet = caller.call("BIS", "CUM0002", g002);
		
		// 获取返回值
		int count = packageDataSet.getParamSetNum("207");
		String cardAcctNbr = null;
		for(int i=0; i<count; i++) {
			// 获取卡户类型
			String cardAcctType = (String) packageDataSet.getParamByID("2048", "207").get(i);
			// 获取天讯资金账户号
			if(cardAcctType.equals("ACCT002")) {
				cardAcctNbr = (String) packageDataSet.getParamByID("2049", "207").get(i);
				break;
			}
		}
		return cardAcctNbr;
	}
	
	/**
	 * 订单支付-担保交易申请
	 * @param map
	 * @param cardAcctNbr
	 * @param payeeCardAcctNbr
	 * @param amount 
	 * @return
	 * @throws Exception 
	 */
//	private static PackageDataSet transGuaranteeProcess(Map map, String cardAcctNbr, String payeeCardAcctNbr, String amount,CommonReqAbs request) throws Exception {
//		// TODO Auto-generated method stub
//		String agentCode = (String)map.get("CUST_CODE");						//商户编码
//		String txnChannel = (String)map.get("CHANNEL_TYPE");					//交易渠道
//		String channelCode = (String)map.get("TERM_ID");						//终端号
//		String areaCode = (String)map.get("AREA_CODE");							//区域编码
//		String actionCode = (String)map.get("ACTION_CODE");					    //操作编码
//		String txnAmount =  amount;			                                    //交易金额
//		String payeeCode =  (String)map.get("OBJ_CODE");	  	 				//收款商户编码
//		String tradeSeq = (String)map.get("TERM_SEQ");							//交易流水号
//		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//		String tradeTime = df.format(map.get("EFF_DATE"));				//交易时间
//		String mark1 =  (String)map.get("MEMO");		//备注字段1
//		
//		TCumInfoDao infoDao = new TCumInfoDao();
//		String objName = infoDao.getCustName(payeeCode);     //收款商户名称
//		/**
//		 * 调用SCS0001,完成交易操作
//		 */
//		// 订单受理信息
//		IParamGroup g401 = new ParamGroupImpl("401");
//		g401.put("4004", agentCode);// 客户编码
//		g401.put("4005", "OT001");// 订单类型编码：业务类订单
//		if(!Charset.isEmpty(areaCode)){
//		   g401.put("4006", areaCode);// 所属区域编码
//		}
//		g401.put("4007", channelCode);// 受理终端号
//		if(Charset.isEmpty(tradeTime)){
//			throw new Exception("受理时间为空");
//		}
//		g401.put("4008", tradeTime);// 受理时间
//		g401.put("4012", "担保交易");// 受理时间
//		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名//////////////////
//		g401.put("4144", txnChannel);// 渠道类型编码
//		g401.put("4017", tradeSeq);// 终端流水号
//		g401.put("4018", "0.0.0.0");// 操作原始来源//////////////////
//		g401.put("4280", objName);   //收款商户名称
//		
//		g401.put("4284", request.getMerId());//机构编码     //20130628 wanght
//		g401.endRow();
//		
//		// 订单费用信息
//		IParamGroup g402 = new ParamGroupImpl("402");
//		g402.put("4021", "0001");// 币种编码
//		g402.put("4023", txnAmount);// 订单原始金额
//		g402.put("4024", "0");// 订单优惠金额
//		g402.put("4025", txnAmount);// 订单应付金额
//		g402.endRow();
//		
//		// 业务单信息
//		IParamGroup g404 = new ParamGroupImpl("404");
//		g404.put("4047", "1");// 业务单序号
//		g404.put("4049", "0001");// 产品编码//////////////////
//		g404.put("4051", actionCode);// 业务编码
//		g404.put("4052", payeeCode);// 业务对象//////////////////
//		g404.put("4053", "1");// 业务数量
//		g404.put("4906", "");
//		g404.endRow();
//		
//		// 业务单费用信息
//		IParamGroup g405 = new ParamGroupImpl("405");
//		g405.put("4047", "1");// 业务单序号
//		g405.put("4021", "0001");// 币种编码
//		g405.put("4066", txnAmount);// 业务单原始金额
//		g405.put("4067", "0");// 业务单优惠金额
//		g405.put("4068", txnAmount);// 业务单应付金额
//		g405.put("4071", "101");// 费用项标识
//		g405.endRow();
//		
//		// 业务属性信息
//		IParamGroup g407 = new ParamGroupImpl("407");
//		g407.put("4047", "1");// 业务单序号
//		g407.put("4051", actionCode);// 业务编码
//		g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
//		g407.put("4088", "0301");//属性值1///////////////////////
//		g407.put("4080", "0");// 控制标识
//		g407.endRow();
//		g407.put("4047", "1");// 业务单序号
//		g407.put("4051", actionCode);// 业务编码
//		g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
//		g407.put("4088", payeeCardAcctNbr);// 属性值1///////////////////////
//		g407.put("4080", "0");// 控制标识
//		g407.endRow();
//		g407.put("4047", "1");// 业务单序号
//		g407.put("4051", actionCode);// 业务编码
//		g407.put("4087", "SCS_DSTACCTTYPE");// 业务属性编码
//		g407.put("4088", "0001");// 属性值1/////////////////////////
//		g407.put("4080", "0");// 控制标识
//		g407.endRow();
//		
//		if(!Charset.trim(mark1).equals("")) {
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_MARK1");// 业务属性编码
//			g407.put("4088", mark1);// 属性值1/////////////////////////
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//		}
//		
//		// 支付单信息
//		IParamGroup g408 = new ParamGroupImpl("408");
//		g408.put("4103", "1");// 扣款顺序号
//		g408.put("4097", "PT0004");// 支付方式编码///////////////
//		g408.put("4098", "110000");// 支付机构编码
//		g408.put("4099", "0001");// 账户类型编码/////////////////
//		g408.put("4101", cardAcctNbr);// 账号
//		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
//		g408.put("4021", "0001");// 币种编码
//		g408.put("4104", txnAmount);// 支付金额
//		g408.endRow();
//		
//		// 组成数据包,调用SCS0001接口
//		IServiceCall caller = new ServiceCallImpl();
//		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);
//		
//		// 返回结果
//		return dataSet;
//	}
/**
 * 一般交易流程
 * @param map
 * @param cardAcctNbr
 * @param payeeCardAcctNbr
 * @param amount 
 * @return
 * @throws Exception 
 */
	private static PackageDataSet transProcess(Map map, String cardAcctNbr, String payeeCardAcctNbr, String amount,String keep,CommonReqAbs request) throws Exception {
		// TODO Auto-generated method stub
		String agentCode = (String)map.get("CUST_CODE");						//商户编码
		String txnChannel = (String)map.get("CHANNEL_TYPE");					//交易渠道
		String channelCode = (String)map.get("TERM_ID");						//终端号
		String areaCode = (String)map.get("AREA_CODE");							//区域编码
		String actionCode = (String)map.get("ACTION_CODE");					    //操作编码
		String txnAmount =  amount;			                                    //交易金额
		String payeeCode =  (String)map.get("OBJ_CODE");	  	 				//收款商户编码
		String tradeSeq = keep;							//交易流水号
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String tradeTime = df.format(map.get("ACCT_DATE"));				        //交易时间
		String mark1 =  (String)map.get("MEMO");							    //备注字段1
		
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", agentCode);// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		if(!Charset.isEmpty(areaCode)){
		  g401.put("4006", areaCode);// 所属区域编码
		}
		g401.put("4007", channelCode);// 受理终端号
		if(Charset.isEmpty(tradeTime)){
			throw new Exception("受理时间为空");
		}
		g401.put("4008", tradeTime);// 受理时间
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名//////////////////
		g401.put("4144", txnChannel);// 渠道类型编码
		if(Charset.isEmpty(tradeSeq)){
			throw new Exception("交易流水号为空");
		}
		g401.put("4017", tradeSeq);// 终端流水号
		g401.put("4018", "0.0.0.0");// 操作原始来源//////////////////
		
		g401.put("4284", request.getMerId());//机构编码     //20130628 wanght
		g401.endRow();
		
		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", txnAmount);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", txnAmount);// 订单应付金额
		g402.endRow();
		
		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码//////////////////
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", payeeCode);// 业务对象//////////////////
		g404.put("4053", "1");// 业务数量
		g404.endRow();
		
		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "101");// 费用项标识
		g405.endRow();
		
		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
		g407.put("4088", "0301");//属性值1///////////////////////
		g407.put("4080", "1");// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
		g407.put("4088", payeeCardAcctNbr);// 属性值1///////////////////////
		g407.put("4080", "1");// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTACCTTYPE");// 业务属性编码
		g407.put("4088", "0001");// 属性值1/////////////////////////
		g407.put("4080", "1");// 控制标识
		g407.endRow();
		
		if(!Charset.trim(mark1).equals("")) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_MARK1");// 业务属性编码
			g407.put("4088", mark1);// 属性值1/////////////////////////
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}
		
		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码///////////////
		g408.put("4098", "110000");// 支付机构编码
		g408.put("4099", "0001");// 账户类型编码/////////////////
		g408.put("4101", cardAcctNbr);// 账号
		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", txnAmount);// 支付金额
		g408.endRow();
		
		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);
		
		// 返回结果
		return dataSet;
		
	}
	
	public  static String getDate(Date date){
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");				
		String time = df.format(date);
		return time;
	}
/**
 * 担保交易
 * @param map
 * @param cardAcctNbr
 * @param payeeCardAcctNbr
 * @param isSecuredTransVerify
 * @param amount 
 * @return
 * @throws Exception 
 */
	private static PackageDataSet transVerifyOrCancle(Map map, String cardAcctNbr, String payeeCardAcctNbr, boolean isVerify, String amount) throws Exception {
		String orderId = (String)map.get("ORDER_ID");							// 订单编码
		String payeeCode = (String)map.get("OBJ_CODE");							// 收款商户编码
		String txnAmount = amount;	                                            // 交易金额
		String actionCode = (String)map.get("ACTION_CODE");						// 操作编码
		String mark1 = (String)map.get("MEMO");
		/**
		 * 调用SCS0013,完成交易确认/交易取消操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4002", orderId);// 订单编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.endRow();
		
		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码//////////////////
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", payeeCode);// 业务对象//////////////////
		g404.put("4053", "1");// 业务数量
		g404.endRow();
		
		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "101");// 费用项标识
		g405.endRow();
		
		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
		g407.put("4088", "0301");//属性值1///////////////////////
		g407.put("4080", "1");// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
		g407.put("4088", payeeCardAcctNbr);// 属性值1///////////////////////
		g407.put("4080", "1");// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTACCTTYPE");// 业务属性编码
		g407.put("4088", "0001");// 属性值1/////////////////////////
		g407.put("4080", "1");// 控制标识
		g407.endRow();
		
		if(!Charset.trim(mark1).equals("")) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_MARK1");// 业务属性编码
			g407.put("4088", mark1);// 属性值1/////////////////////////
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}
	

		
		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码///////////////
		g408.put("4098", "110000");// 支付机构编码
		g408.put("4099", "0001");// 账户类型编码/////////////////
		g408.put("4101", cardAcctNbr);// 账号
		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", txnAmount);// 支付金额
		g408.endRow();
		
		IParamGroup g480 = new ParamGroupImpl("480");
		// 判断操作类型
		if(isVerify) {
			// 交易确认
			g480.put("4805", "S0A");
		} else {
			// 交易取消
			g480.put("4805", "S0X");
		}
		g480.endRow();
		
		// 组成数据包,调用SCS0013接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0013", g401, g404, g405, g407, g408, g480);
		
		// 返回结果
		return dataSet;
	}
	
	private static PackageDataSet callCUM1003(DpInf05004Request dpRequest)throws Exception{
		
		String staff = dpRequest.getStaffCode();
		
		String verityType = "0001";	//支付密码
		
		String tmnNum = dpRequest.getTmnNum();
		
		IParamGroup g200 = new ParamGroupImpl("200");
		g200.put("2901", "2171");
		g200.put("2902", staff);
		g200.put("2903", "2007");
		g200.put("2904", dpRequest.getPassWord());
		g200.put("2172", "0001");
		g200.put("2173", verityType);
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
