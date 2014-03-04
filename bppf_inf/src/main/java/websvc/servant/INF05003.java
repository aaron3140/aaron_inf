package websvc.servant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import common.algorithm.MD5;
import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TPnmPartnerDao;
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
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf05003Request;
import common.xml.dp.DpInf05003Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 
 * 付款到银行账户接口
 *
 */

public class INF05003 {
  
	public static String svcInfName = "INF05003";
  
	public static String execute(String in0,String in1){
		DpInf05003Request dpRequest=null;
		DpInf05003Response resp=null;
		RespInfo respInfo=null;        //返回信息头
		String tmnNum=null;
		String orderSeq=null;
		String transCode=null;        //商户编码
		String channelCode="";
		
		SagManager sagManager=new SagManager();
		TInfOperInLog tInfOperInLog=null;
		String responseCode="";
		
		String keep="";  //流水号
		String ip="";
		String svcCode=WebSvcTool.getSvcCode(in0);
		
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			respInfo=new RespInfo(in1,"20");   //返回消息头
			dpRequest=new DpInf05003Request(in1);
			dpRequest.setTxnAmount(MathTool.pointToYuan(dpRequest.getTxnAmount()));
			tmnNum=dpRequest.getTmnNum();
			keep=dpRequest.getKeep();
			ip=dpRequest.getIp();
			orderSeq=dpRequest.getOrderSeq();
			transCode=dpRequest.getTransCode();
			channelCode=dpRequest.getChannelCode();
			
	    	//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "tansCode",transCode,"ORDERSEQ"
					, orderSeq, "S0A");
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
			
			//付款商户关联机构验证
			if(!TCumInfoDao.verifyMerIdCustCode(dpRequest.getTransCode(), dpRequest.getMerId())){
				
				if(TCumInfoDao.getMerIdByCustCode(dpRequest.getTransCode(),dpRequest.getMerId()))
				
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
			}
			
			//收款商户关联机构验证
			if(!"".equals(dpRequest.getRecvCode())&&!TCumInfoDao.verifyMerIdCustCode(dpRequest.getRecvCode(), dpRequest.getMerId())){
				
				if(TCumInfoDao.getMerIdByCustCode(dpRequest.getRecvCode(),dpRequest.getMerId()))
				
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_R_NOT_MATCH_MERG_DESC);
			}
			
		//	判断是否有付款权限
			boolean flag = false;
			List privList = PayCompetenceManage.payFunc(transCode, channelCode);
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();
				if("ws_PaytoBank".equals(str)){
					flag=true;
				}
			}
			if (!flag) {
				throw new Exception("没有付款到银行账户权限");
			}
			
			
			//验证手机号码和身份证号码
			String IDcard18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])(\\d{3}[xX\\d])$";
			String IDcard15 ="^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
			
			String phoneReg="^[0-9]{11,15}$";
			
			String phone = dpRequest.getPhone();
			String certCode = dpRequest.getCertCode();
			if(phone!=null&&!"".equals(phone)){
				if(!phone.matches(phoneReg)){
					throw new Exception("你的电话号码不正确");
				}
			}
			if(dpRequest.getCertId().equals("00")){
				if(certCode.length() == 15 ){
					if(!certCode.matches(IDcard15)){
						throw new Exception("你的身份证号码不正确");
					}
				}else if(certCode.length() == 18){
					if(!certCode.matches(IDcard18)){
						throw new Exception("你的身份证号码不正确");
					}
				}else {
					if(!certCode.matches(IDcard15)){
						throw new Exception("你的身份证号码长度不正确");
					}
				}
			}
			/**
			 * 计算手续费
			 */	
		
			TPnmPartnerDao dao = new TPnmPartnerDao();
			Map<String, String> map0 = dao.getPrntnCodeAndPrntType(dpRequest.getTransCode());
			
			IParamGroup g423 = new ParamGroupImpl("423");
			g423.put("4230", "0001"); 	//手续费
			g423.put("2011", dpRequest.getMerId());	//接入机构的对应的商户编码
			g423.put("4330", map0.get("PRTN_CODE"));	//实际做交易的商户编码
			g423.put("4331", map0.get("PRTN_TYPE"));	//实际做交易的商户类型
			g423.put("2002", dpRequest.getTransCode());	//实际做交易的商户编码
			g423.put("4051", "15010004");	//业务编码
			g423.put("4098", dpRequest.getBankId());   //银行编码
			g423.put("4006", dpRequest.getBankBelong());  //区域编码
			g423.put("4144", channelCode);  //渠道号
			g423.endRow();
			
			IParamGroup g402_0201 = new ParamGroupImpl("402");
			g402_0201.put("4025", dpRequest.getTxnAmount());	//订单金额
			g402_0201.put("4099", "0001");	//账户类型编码
			
			// 组成数据包,调用SCS0201接口
			IServiceCall caller2 = new ServiceCallImpl();
			PackageDataSet dataSet1 = null;
			
			dataSet1 = caller2.call("SCS", "SCS0201", g423, g402_0201);
			String resCode = dataSet1.getByID("0001", "000");
			
			String txnAmount= dpRequest.getTxnAmount();     //交易金额
			String finalAmount = txnAmount;                 //最终金额
			String concessionType = null;
			String concession = null;
			if (Long.valueOf(resCode) == 0) {
				String flagStr = dataSet1.getByID("4230", "423");
				if ("0001".equals(flagStr)) {
					concessionType = dataSet1.getByID("4328", "423");
					concession = dataSet1.getByID("4329", "423");
					if(concession==null || "".equals(concession)||Double.valueOf(concession)==0){
						concession="0";
					}
					double temp = 0;
					if ("DT003".equals(concessionType)) {
						temp = Double.parseDouble(txnAmount) + Double.parseDouble(concession);
						finalAmount = String.valueOf(temp);
					}else{
						temp = Double.parseDouble(txnAmount) - Double.parseDouble(concession);
						if (temp < 0) {
							throw new Exception("计算手续费金额后为负数");
						}
						txnAmount = String.valueOf(temp);
					}
				}
			}
					
			//对代付封包、解包
			PackageDataSet ps=null;			
			ps=payment(dpRequest,txnAmount,finalAmount,concession,concessionType);
			
			String resultCode=(String)ps.getParamByID("0001", "000").get(0);
			responseCode=resultCode;
			//返回结果失败时，抛出异常
			if(Long.valueOf(resultCode) != 0){
				String resultMsg=(String)ps.getParamByID("0002", "000").get(0);
				
				throw new Exception(resultMsg);
			}
			
			responseCode = ps.getByID("0001", "000");// 响应码，获取接口的000组的0001参数
			String responseDesc = ps.getByID("0002", "000");//响应码描述， 获取接口的000组的0002参数												
			
			//获取返回结果
			resp = new DpInf05003Response();
            String transSeq=ps.getByID("4002", "401");         //交易流水号
            orderSeq=dpRequest.getOrderSeq();                //订单号
            String tradeAmount=ps.getByID("6303", "600");       //交易金额  
            String handleAmount = concession ;     //手续费
            
           // 元转分
            String amount = MathTool.yuanToPoint(txnAmount);
            tradeAmount=MathTool.yuanToPoint(tradeAmount);
            String  handle="0";
            if(handleAmount!=null){
                 handle=MathTool.yuanToPoint(handleAmount);
            }
            
           // 插入信息到出站信息表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, 
					svcCode, responseCode, responseDesc, "S0A");
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode,
					responseDesc, transSeq, orderSeq, amount, tradeAmount, handle);						
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


	private static PackageDataSet payment(DpInf05003Request dpRequest, String txnAmount, String finalAmount, String concession, String concessionType) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String tradeTime = df.format(new Date());
		
		/**
		 * 调用CUM0002,根据客户编码获得该客户天讯卡户号
		 */
		// 查询明细信息
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "2002");// 查询条件：客户编码
		g002.put("0022", dpRequest.getTransCode());// 查询条件值
		
		// 组成数据包,调用CUM0002接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet packageDataSet = caller.call("BIS", "CUM0002", g002);
		
		// 获取返回值
		int count = packageDataSet.getParamSetNum("207");
		String cardAcctNbr = null;
		for(int i=0; i<count; i++) {
			// 获取卡户类型
			String cardAcctType = (String) packageDataSet.getParamByID("2048", "207").get(i);
			// 获取天讯卡户号
			if(cardAcctType.equals("ACCT002")) {
				cardAcctNbr = (String) packageDataSet.getParamByID("2049", "207").get(i);
				break;
			}
		}

		/**
		 *  一次路由
	     */
		TransManage tm = new TransManage();
		PackageDataSet dataSet2 = tm.firstRoute(dpRequest.getTransCode(), dpRequest.getBankBelong(), 
				dpRequest.getChannelCode(), "15010004", dpRequest.getMerId() ,
			    dpRequest.getTmnNum(), dpRequest.getTxnAmount(), "PT1004", dpRequest.getBankId());	
		String newActionCode = dataSet2.getByID("4051","423");
	    String newOrgCode = dataSet2.getByID("4098","423");

		/**
		* 调用订单支付 SCS0001 完成交易操作 
		*/
		//订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getTransCode());   //商户编码
		g401.put("4005", "OT001");                    //订单类型编码
		g401.put("4006", dpRequest.getBankBelong());  //收款人银行所属区域编码
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", tradeTime);// 受理时间
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());// 终端流水号
		g401.put("4028", dpRequest.getOrderSeq());// 订单号
		g401.put("4018", "0.0.0.0");// 操作原始来源
		g401.put("4012", "付款到银行账户");// 订单备注
		
		g401.put("4284", dpRequest.getMerId());//机构编码     //20130628 wanght
		g401.endRow();
		
		//订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", finalAmount);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", finalAmount);// 订单应付金额		
		g402.endRow();
		
	    //业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", newActionCode);// 业务编码
//		g404.put("4052", dpRequest.getRevaccNo());// 业务对象
		g404.put("4052", dpRequest.getRevaccName());// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4062", "");// 业务系统参考号
		g404.put("4064", "");// 业务单所属客户编码
		g404.endRow();
		
		//业务单手续费
		if (concessionType != null && concession != null && !concession.equals("0")) {
			g404.put("4047", "2");// 业务单序号
			g404.put("4049", "0001");// 产品编码
			g404.put("4051", "01050001");// 业务编码
			g404.put("4052", "系统账户");// 业务对象
			g404.put("4053", "1");// 业务数量
			g404.put("4062", "");// 业务系统参考号
			g404.put("4064", "");// 业务单所属客户编码
			g404.endRow();
		}
		
	    //业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();
		
       // 业务单费用手续费信息
		if (concessionType != null && concession != null && !concession.equals("0")) {
			g405.put("4047", "2");// 业务单序号
			g405.put("4021", "0001");// 币种编码
			g405.put("4066", concession);// 业务单原始金额(手续费)
			g405.put("4067", "0");// 业务单优惠金额
			g405.put("4068", concession);// 业务单应付金额(手续费)
			g405.put("4071", "103");// 费用项标识
			g405.endRow();
		}
	
		//业务单属性信息
		IParamGroup g407 = new ParamGroupImpl("407");	

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_TRADETYPE");// 发送机构标识码
		g407.put("4088", "020011");//发送机构标识码
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_CERTID");// 发送机构标识码
		g407.put("4088", dpRequest.getCertId());//发送机构标识码
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_CERTCODE");// 发送机构标识码
		g407.put("4088", dpRequest.getCertCode());//发送机构标识码
		g407.put("4080", "0");// 控制标识
		g407.endRow();	
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_BANKBELONG");// 银行帐号归属地
		g407.put("4088", dpRequest.getBankBelong());//银行帐号归属地
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_BANKID");// 业务属性编码
		g407.put("4088", dpRequest.getBankId());// 属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_BANKCARDID");// 卡折标识
		g407.put("4088", dpRequest.getBankCardId());//卡折标识编码
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_BANKCARDTYPE");// 对公对私标识
		g407.put("4088", dpRequest.getBankCardType());// 对公对私标识
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
		g407.put("4088", "0200");//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
		g407.put("4088", dpRequest.getRevaccNo());// 属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_PAYTYPE");// 业务属性编码
		g407.put("4088", "PT1004");//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_PAYORG");// 业务属性编码
		g407.put("4088", newOrgCode);//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		if(!Charset.isEmpty(dpRequest.getPhone())){			
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_PHONENUM");// 联系电话
			g407.put("4088", dpRequest.getPhone());// 联系电话
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}
		
		if(!Charset.isEmpty(dpRequest.getAddr())){
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_ADDREASS");// 联系地址
			g407.put("4088", dpRequest.getAddr());// 联系地址
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}
		
		if (!Charset.isEmpty(dpRequest.getBankSubId())) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_SUBBANK");// 发送机构标识码
			g407.put("4088", dpRequest.getBankSubId());//发送机构标识码
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}
		
		if (!Charset.isEmpty(dpRequest.getRemark())) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_REMARK");// 联系电话
			g407.put("4088", dpRequest.getRemark());// 联系电话
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_BANKNAME");// 银行帐号户名
		g407.put("4088", dpRequest.getRevaccName());//银行帐号户名
		g407.put("4080", "0");// 控制标识
		g407.endRow();
										
		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", "110000");// 支付机构编码	
		g408.put("4099", "0001");// 账户类型编码
		g408.put("4101", cardAcctNbr);// 账号
//		g408.put("0000", "银行卡号");// 账号
		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", finalAmount);// 支付金额
		g408.endRow();
		
		// 组成数据包,调用SCS0001接口
		IServiceCall caller0 = new ServiceCallImpl();
		PackageDataSet dataSet0;
		dataSet0 = caller0.call("SCS", "SCS0001", g401, g402, g404, g405,g407, g408);
		
		return dataSet0;
	}
	
}
