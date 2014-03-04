package com.tisson.pay.service.impl;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.tisson.pay.config.BftProperties;
import com.tisson.pay.service.IBftPayResponse;

import framework.config.ConfigReader;
import framework.config.GlobalConstants;

import mpi.client.data.TransData;
import mpi.client.tools.ClassTools;
import mpi.client.tools.GetKey;
import mpi.client.tools.TelTran;
import mpi.client.tools.TransDataTools;
import mpi.client.trans.PayInforResponse;

/**
 * 帮付通回调业务处理
 * @author sonsy
 *
 */
public class BftPayResponseimpl extends PayInforResponse implements IBftPayResponse{
	private static final Logger logger = Logger.getLogger(BftPayResponseimpl.class);
	public static void main(String args[]){
		//初始化
    	try {
			Properties p = ConfigReader.readConfig(GlobalConstants.BFT_CONF);
			String keyStore = p.getProperty("KeyStore");
			String hostURL = p.getProperty("HostURL");
			BftProperties.setKeyStore(keyStore);
			BftProperties.setHostURL(hostURL);
			logger.info(">>> keyStore: " + keyStore);
			logger.info(">>> hostURL: " + hostURL);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		logger.info("初始化BftProperties配置...完成");
		
		String UserID="";
		String MerID="";
		String PayID="";
		String OrderAmount="";
		String TransDate="";
		String TransTime="";
		String SystemSSN="";
		String ylSystemSSN="";
		String SysCode="";
		String BankCode="";
		String BankCard="";
		String TransType="";
		String retStr="";
		
		/**
		 * 商户需提供外网地址供支付平台访问;
		 * 假定商户地址http://www.test.com;
		 * 支付中心访问http://www.test.com?EncryptMsg=0C24300B077E727775630B2344785955247C641730310A2528207B6063455D4A08057F7507777C730177747601000C0870727475757170770470777602060C0F057601707B65102129062734520B1E7A202F29120A0779767475747E04070801797472757072717774756016574F717C7C352731377174757775727401070908737474640C31202137042B2943584C0575716C7273650B3621203400535379552E342C367E650B3621203407554279552E342C367E650B2C242B2835535A79552E342C367E651036242B350257425D0535243136717375766311342758456C512C247F72777173757475747300106B413235272F10100A793177767704060C0A76707372717672623C29153F45425D5512120C7F37717475777572740107090873747464112630072A21237B10645D4C05243121312A34302C2A287B1065414B022E26277E131B747575766074575653022030267E0205050404070777770C0D70766400222D2F072A21237B75757A1E162032163A3321796308233474575B5314332E7F651736252B36123F4653050971717064077673777C777F7103777C7D02717174062D20
		 */
		
		//取"支付通知报文"串
		String EncryptMsg="0C24300B077E727775630B2344785955247C641730310A2528207B6063455D4A08057F7507777C730177747601000C0870727475757170770470777602060C0F057601707B65102129062734520B1E7A202F29120A0779767475747E04070801797472757072717774756016574F717C7C352731377174757775727401070908737474640C31202137042B2943584C0575716C7273650B3621203400535379552E342C367E650B3621203407554279552E342C367E650B2C242B2835535A79552E342C367E651036242B350257425D0535243136717375766311342758456C512C247F72777173757475747300106B413235272F10100A793177767704060C0A76707372717672623C29153F45425D5512120C7F37717475777572740107090873747464112630072A21237B10645D4C05243121312A34302C2A287B1065414B022E26277E131B747575766074575653022030267E0205050404070777770C0D70766400222D2F072A21237B75757A1E162032163A3321796308233474575B5314332E7F651736252B36123F4653050971717064077673777C777F7103777C7D02717174062D20";	
		
		TransData transdata = new TransData();
		BftPayRequest payInforRequest = new BftPayRequest();//声明"支付通知报文"类对象
		transdata=payInforRequest.reveivePayInforRequest(EncryptMsg);//调用"支付通知报文"方法,取各数据元
		
		UserID=transdata.getUserID();//取用户手机号
		MerID=transdata.getMerID();//取商户编号
		PayID=transdata.getPayID();//取商户订单号
		OrderAmount=transdata.getOrderAmount();//取交易金额
		TransDate=transdata.getTransDate();//取交易日期
		TransTime=transdata.getTransTime();//取交易时间
		SystemSSN=transdata.getSystemSSN();//取系统参考号
		ylSystemSSN=transdata.getYlSystemSSN();//取银联系统参考号
		SysCode=transdata.getSysCode();//取支付响应码
		BankCode=transdata.getBankCode();//取银行代码
		BankCard=transdata.getBankCard();//到银行卡号
		TransType=transdata.getTransType();//取交易类型
		
		/**
		 * 此处为商户业务逻辑处理......
		 */
		BftPayResponseimpl payInforResponse = new BftPayResponseimpl();//声明"支付通知应答报文"类对象

//		transdata.setUserID(UserID);//用户手机号
		transdata.setUserID("15812345678");//用户手机号
		transdata.setMerID("649");//XXX,填帮付通分配商户编号
//		transdata.setPayID(PayID);//订单号
		transdata.setPayID("test01304031537221459");//订单号
		transdata.setRetCode("T_0000");//更新成功T_0000,无此订单T_0014,已更新过T_0025,更新失败或其他错误T_0096,请参见接口规范“报文交互响应码”
		transdata.setTransType("2005");//交易类型

		retStr = payInforResponse.sendPayInforResponse(transdata);//调用工具包"支付通知应答"类,生成支付通知应答串
		
		//retStr做为应答，放到Response中
	}
	
	/*    */   public String sendPayInforResponse(TransData transdata)
	/*    */   {
	/* 20 */     ClassTools classtools = new ClassTools();
	/* 21 */     TransDataTools transdatatools = new TransDataTools();
	/* 22 */     TelTran teltran = new TelTran();
	/* 24 */     String sSuffix = "sjfWAP";
	/* 25 */     String sMd5Len = "22";
	/* 26 */     String sMastKey = "AA22CCDD88FF00DD";
	/*    */ 
	/* 28 */     String KeyStore = BftProperties.getKeyStore();
	/* 29 */     GetKey getkey = null;
	/*    */ 
	/* 31 */     String errorStr = "";
	/* 32 */     String toEncStr = "";
	/* 33 */     String retStr = "";
	/* 34 */     String sKeyStr = "";
	/* 35 */     boolean encbln = false;
	/*    */ 
	/* 37 */     logger.info("<<支付通知应答>>开始处理中......");
	/* 38 */     errorStr = transdatatools.checkPayNoticeResponse(transdata);
	/*    */ 
	/* 40 */     if (errorStr.length() != 0) {
	/* 41 */       logger.info("<<支付通知应答>>操作失败,处理结束......");
	/* 42 */       return retStr;
	/*    */     }
	/*    */ 
	/* 45 */     if ((KeyStore == null) || (KeyStore.length() == 0)) {
	/* 46 */       logger.info("<<支付通知应答>>读取证书文件路径(KeyStore)错误,请查看MPI.properties文件!");
	/* 47 */       logger.info("<<支付通知应答>>操作失败,处理结束......");
	/* 48 */       return retStr;
	/*    */     }
	/*    */ 
	/* 51 */     getkey = new GetKey(KeyStore);
	/* 52 */     sKeyStr = getkey.getkeystr();
	/*    */ 
	/* 54 */     if ((sKeyStr.equals("filerroe")) || (sKeyStr.length() == 0)) {
	/* 55 */       logger.info("<<支付通知应答>>读取证书文件路径(KeyStore)错误,请查看MPI.properties文件!");
	/* 56 */       logger.info("<<支付通知应答>>操作失败,处理结束......");
	/* 57 */       return retStr;
	/*    */     }
	/*    */ 
	/* 61 */     logger.info("<<支付通知应答>>读取证书文件成功!");
	/* 62 */     if (errorStr.length() == 0) {
	/* 63 */       toEncStr = classtools.sndTransDataInfor(transdata);
	/* 64 */       logger.info("<<支付通知应答>>明文[" + toEncStr + "]");
	/* 65 */       encbln = teltran.EncryptMsg(sMastKey, sSuffix, sMd5Len, toEncStr, 
	/* 66 */         sKeyStr);
	/*    */     }
	/* 68 */     if (encbln) {
	/* 69 */       retStr = teltran.LastResult;
	/* 70 */       logger.info("<<支付通知应答>>密文[" + retStr + "]");
	/* 71 */       logger.info("<<支付通知应答>>操作成功,处理结束......");
	/*    */     } else {
	/* 73 */       retStr = teltran.LastErrMsg;
	/* 74 */       logger.info("<<支付通知应答>>[" + retStr + "]");
	/* 75 */       logger.info("<<支付通知应答>>操作失败,处理结束......");
	/*    */     }
	/*    */ 
	/* 78 */     return retStr;
	/*    */   }
}
