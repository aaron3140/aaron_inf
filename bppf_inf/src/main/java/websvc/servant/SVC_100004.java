package websvc.servant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.algorithm.RSA;
import common.dao.TCumInfoDao;
import common.dao.TCumInfoDaoTemp;
import common.dao.TInfDcoperlogDao;
import common.dao.TPdmIncludeDao;
import common.dao.TScsQueryRecDao;
import common.platform.invoker.ServiceConstant;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.bean.SocketConfig;
import common.platform.provider.server.PackageDataSet;
import common.utils.Charset;
import common.utils.Constant;
import common.utils.CurrencyTool;
import common.utils.DateTool;
import common.utils.DictTranslationUtils;
import common.utils.Merchant;
import common.utils.MerchantCache;
import common.utils.WebSvcTool;
import common.xml.dp.DpCardOrderConfirmRequest;
import common.xml.dp.DpCardOrderConfirmResponse;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author Tisson
 *  卡确定购买接口
 */
public class SVC_100004 {

	 public static String svcInfName = "100004";
	
	public static void main(String[] args) {
		

	}

	

	public static String execute(String in0, String in1) {
		Long pk = null;
		DpCardOrderConfirmRequest cardOrderConfirmRequest = null;
		DpCardOrderConfirmResponse resp = new DpCardOrderConfirmResponse();
		String agentCode = null;
		String areaCode = null;
		String tradeSeq = null;
		String oldTradeSeq = null;
		String txnChannel = null;
		String keep = null;
		
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		
		try {
			
			cardOrderConfirmRequest = new DpCardOrderConfirmRequest(in1);

			agentCode = cardOrderConfirmRequest.getAgentCode();
			
			areaCode = cardOrderConfirmRequest.getAreaCode();
			
			tradeSeq = cardOrderConfirmRequest.getTradeSeq();
			
			oldTradeSeq = cardOrderConfirmRequest.getOldTradeSeq();
			
			keep = cardOrderConfirmRequest.getKeep();
			
			//关联机构验证
			if(!TCumInfoDao.verifyMerIdCustCode(cardOrderConfirmRequest.getAgentCode(), cardOrderConfirmRequest.getMerId())){
				
				if(TCumInfoDao.getMerIdByCustCode(cardOrderConfirmRequest.getAgentCode(),cardOrderConfirmRequest.getMerId()))
				
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
			}
		
			//验证商户平台流水号唯一性
			boolean exist = WebSvcTool.checkTradeSeqAndAgent(tradeSeq, agentCode);
			
			if(exist){
				throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
						INFErrorDef.POSSEQNO_CONFLICT_REASON);
			}
			
//			 写日志表
			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
					svcInfName, "SCS0001", SocketConfig.getSockIp(),  
					"TRADESEQ", tradeSeq,"AGENTCODE",agentCode);
			id.setPk(pk);
//			得到sp商户信息
			Merchant spinfo = MerchantCache.getSpInfo(agentCode);
   
            //得到客户编码
            String custCode = spinfo.getCustCode(); 
            String termNo = spinfo.getTermNo();
            
    		areaCode = Charset.isEmpty(areaCode) ? spinfo.getAreaCode() : areaCode;
			
			String cardCustCode = "";
			if(cardOrderConfirmRequest.getUserEle() != null){
				cardCustCode = cardOrderConfirmRequest.getInNo();;
			}
            
			//查询获得OPER_ORIG作为卡预定流水号
			String operOrig = TScsQueryRecDao.getOperOrig(termNo, custCode, oldTradeSeq);
			if (Charset.isEmpty(operOrig)) {
				throw new INFException(INFErrorDef.INF_CARDBOOKSEQ_NOTEXIST_ERRCODE,
						INFErrorDef.INF_CARDBOOKSEQ_NOTEXIST_ERRDESC);
			}
			
			//查询DEAL_AMOUNT
			String dealAmount = TScsQueryRecDao.getDealAmount(termNo, custCode, oldTradeSeq);

			//金额限制验证
			if(cardOrderConfirmRequest.getUserEle() == null){
				Long bookLimit = Long.parseLong(TPdmIncludeDao.getBookMoneyLimit());
				if(Long.parseLong(dealAmount) > bookLimit){
					throw new INFException(INFErrorDef.INF_CARDBOOKSEQ_OUT_OF_LIMIT,
							INFErrorDef.INF_CARDBOOKSEQ_OUT_OF_LIMIT_ERRDESC + bookLimit + "元");
				}
			}
			
			String timeStamp3 = DateTool.getCurDate3();// 格式化日期yyyyMMddHHmmss
			
			//调SCS0001电子售卡
			
			txnChannel = cardOrderConfirmRequest.getTxnChannel();
			
			IParamGroup g401 = new ParamGroupImpl("401");
			g401.put("4004", custCode);
			g401.put("4005", Constant.BUSI_PAY_ORDER_CODE);
			g401.put("4006", areaCode);
			g401.put("4007", termNo);
			g401.put("4008", timeStamp3);
			g401.put("4012", "电子售卡");
			g401.put("4016", "LG001");
			g401.put("4144", txnChannel);
			g401.put("4017", tradeSeq);
			g401.put("4018", custCode);
			
			g401.put("4284", cardOrderConfirmRequest.getMerId());//机构编码     //20130628 wanght
			g401.endRow();

			IParamGroup g402 = new ParamGroupImpl("402");
			g402.put("4021", "0001");
			g402.put("4023", dealAmount);
			g402.put("4024", "0");
			g402.put("4025", dealAmount);
			g402.endRow();

			IParamGroup g404 = new ParamGroupImpl("404");
			g404.put("4047", "1");
			g404.put("4049", Constant.DP_PROD_CODE);//
			g404.put("4051", Constant.DP_E_CARD_ACTION_CODE);//
			g404.put("4052", cardCustCode);
			g404.put("4053", "1");
			g404.put("4906", "0");
			g404.endRow();

			IParamGroup g405 = new ParamGroupImpl("405");
			g405.put("4047", "1");
			g405.put("4021", Constant.DP_PROD_CODE);//
			g405.put("4066", dealAmount);
			g405.put("4067", "0");
			g405.put("4068", dealAmount);
			g405.put("4071", "101");
			g405.endRow();

			IParamGroup g407 = new ParamGroupImpl("407");
			
			g407.put("4047", "1");
			g407.put("4051", Constant.DP_E_CARD_ACTION_CODE);
			g407.put("4087", "SCS_OCCUPYSEQ");
			g407.put("4088", operOrig);
			g407.put("4080", "0");
			g407.endRow();
				
			IParamGroup g408 = new ParamGroupImpl("408");
			
			if(cardOrderConfirmRequest.getAcctEle() != null){
				String acctNo = cardOrderConfirmRequest.getAcctNo();
				String money = cardOrderConfirmRequest.getMoney();
				String moneyYuan = null;
				if(!Charset.isEmpty(money)){
					moneyYuan = CurrencyTool.fen2Yuan(money);// 交易金额 -以元为单位
				}
				String acctName = cardOrderConfirmRequest.getAcctName();
				
				if(moneyYuan != null && CurrencyTool.compare(moneyYuan, dealAmount) != 0 ){
					throw new INFException(INFErrorDef.INF_INCONSISTENT_AMOUNT_ERRCODE,
							INFErrorDef.INF_INCONSISTENT_AMOUNT_ERRDESC);
				}
				
				g408.put("4103", "1");
				g408.put("4097", Constant.PT_DP_E_CARD_CODE);
				g408.put("4098", "000002");
				g408.put("4100", acctName);
				g408.put("4101", acctNo);
				g408.put("4102", "123456");
				g408.put("4021", "0001");
				g408.put("4104", moneyYuan);
				g408.endRow();
				
				
			}else{
				g408.put("4103", "1");
				g408.put("4097", Constant.PT_DP_E_CARD_CODE);
				g408.put("4098", "000000");
				g408.put("4101", custCode);
				g408.put("4102", "000000");
				g408.put("4021", "0001");
				g408.put("4104", dealAmount);
				g408.endRow();
			}

			
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet packageDataSet = caller.call("SCS0001", g401, g402,
					g404, g405, g407, g408);// 组成SCS0001交易数据包
			String responseCode = packageDataSet.getByID("0001", "000");// 获取SCS0001接口的000组的0001参数
			// 返回响应码
			String responseDesc = packageDataSet.getByID("0002", "000");// 获取SCS0001接口的000组的0002参数
			// 返回响应码描述
			//String cardSeq = packageDataSet.getByID("", "153");//TODO 卡序号 
			List cardNo = packageDataSet.getParamByID("1530", "153"); //卡号
			List cardPwd = packageDataSet.getParamByID("1535", "153"); //卡密码
			List cardAmtFenList = packageDataSet.getParamByID("1539", "153"); //卡面值
			List cardType = packageDataSet.getParamByID("1589", "153"); //卡类别
			List subCardType = packageDataSet.getParamByID("1533", "153"); //卡类型
			List expDate = packageDataSet.getParamByID("1537", "153"); //有效期
			
			//私钥加密 暂时取消
			List<String> pwdEncryptList = new ArrayList<String>();
			for(Object cardPwdObj:cardPwd){
				String data = cardPwdObj.toString();
				//String pwdEncrypt = "";
				//pwdEncrypt = RSACipher.encryptByPrivateKey(data);
				pwdEncryptList.add(data);
			}
			
			
			List<String> cardAmtReList = new ArrayList<String>();
			
			for(Object cardAmtObj:cardAmtFenList){
				String cardAmtFen = cardAmtObj.toString();
				String cardAmtYuan = CurrencyTool.fen2Yuan(cardAmtFen, 0);
				String cardAmtRe = DictTranslationUtils.dictReverseTranslation(cardAmtYuan, "INF_CARDAMT");
				cardAmtReList.add(cardAmtRe);
			}
			
			if(cardOrderConfirmRequest.getUserEle() != null){
				//调用CUM0001
				String userType = cardOrderConfirmRequest.getUserType();
				String userName = cardOrderConfirmRequest.getUserName();
				String orgName = cardOrderConfirmRequest.getOrgName();
				String idType = cardOrderConfirmRequest.getIdType();
				String idNo = cardOrderConfirmRequest.getInNo();
				String address = cardOrderConfirmRequest.getAddress();
				String phone = cardOrderConfirmRequest.getPhone();
				//获得cust type客户类型
				String custName = userName;
				String custType = "D01";
				if("00".equals(userType)){
					custName = userName;
					custType =  "D01";
				}else if("01".equals(userType)){
					custName = orgName;
					custType =  "D02";
				} 
				
				String custIdStr = TCumInfoDaoTemp.queryCustIdByCustCode(idNo);
                if(custIdStr == null){
				
					IParamGroup c201 = new ParamGroupImpl("201"); // 客户基本信息(必传组)
					c201.put("2002", idNo); // 客户编码
					c201.put("C003", "U"); // 客户类型
					c201.put("2003",custName);//客户名称
					c201.put("2004", custType);//客户类型
					
					c201.put("2007", RSA.encrypt("123456")); // 客户密码
					c201.put("2008", areaCode); // 所属区域编码
					// c201.put("2011", partCode); //合作伙伴
					c201.put("2009", idType);
					c201.put("2010", idNo);
					c201.put("C013", "X"); // 是否开通电子银行
					c201.endRow();
					
					IParamGroup c202 = new ParamGroupImpl("202");
					c202.put("2016", "ADDR"); // 
					c202.put("2018", address); 
					c202.endRow();
					
					c202.put("2016", "MOB"); // 
					c202.put("2018", phone); 
					c202.endRow();
					
					IParamGroup c203 = new ParamGroupImpl("203");
					c203.put("2024", "CUM_OCCUPYSEQ"); // 
					c203.put("2025", operOrig); 				
					c203.endRow();
	
					IParamGroup c211 = new ParamGroupImpl("211");
					c211.put("2076", "OTHER"); // 
					c211.put("2077", ServiceConstant.SITE_TERM_ID); 
					c211.put("2078", spinfo.getCustCode()); 
					c211.put("2079", "99");
					c211.endRow();
	
					try {
						caller.call("CUM0001", c201, c202, c203, c211);
					} catch (Exception e) {
						throw new INFException(INFErrorDef.CUM_REG_FAIL, "新增持卡人失败");
					}
					
					//插入t_cum_attr记录
					custIdStr = TCumInfoDaoTemp.queryCustIdByCustCode(idNo);
					long custId = Long.parseLong(custIdStr);
					TCumInfoDaoTemp.insertCustAttr(custId, cardNo, 1000000000);
				} else {
					//更新客户信息
					TCumInfoDaoTemp.updateCumInfoCustName(idNo, custName);
					TCumInfoDaoTemp.updateCumInfoCustType(idNo, custType);
					
					Map<String, String> contactMap = new HashMap<String, String>();
					contactMap.put("ADDR", address);
					contactMap.put("MOB", phone);
					TCumInfoDaoTemp.updateCumContactInfo(idNo, contactMap);
					
					//插入t_cum_attr记录
					long custId = Long.parseLong(custIdStr);
					int maxValue = TCumInfoDaoTemp.getCustAttrMaxAttrId(custId);
					if(maxValue == 0){
						TCumInfoDaoTemp.insertCustAttr(custId, cardNo, 1000000000);
					}else{
						TCumInfoDaoTemp.insertCustAttr(custId, cardNo, maxValue +1);
					}
                }
				
			}
			
			//更新日志
			TInfDcoperlogDao.update(pk, responseCode, responseDesc);
			
			return resp.toXMLStr("SUCCESS", keep, svcInfName,
					responseCode, responseDesc, "", cardType, subCardType, cardAmtReList, cardNo, pwdEncryptList, expDate);
			
		} catch (XmlINFException spe) {
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			return ExceptionHandler.toXML(new XmlINFException(
					resp, e), id);
		}
	}

}
