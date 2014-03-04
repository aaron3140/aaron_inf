package com.tisson.pay.service.impl;

import java.util.Properties;

import org.apache.log4j.Logger;

import mpi.client.data.TransData;
import mpi.client.tools.ClassTools;
import mpi.client.tools.GetKey;
import mpi.client.tools.TelTran;
import mpi.client.tools.TransDataTools;
import mpi.client.trans.PayResultRequest;
import mpi.clinet.http.HttpClientPost;

import com.tisson.pay.config.BftProperties;
import com.tisson.pay.service.IBftPayResult;

import framework.config.ConfigReader;
import framework.config.GlobalConstants;

/**
 * 
 * 本类描述:针对帮付通结果信息查询
 * 
 * @version: 企业帐户前置接口 v1.0
 * @author: 广州天讯瑞达通讯技术有限公司(zhuxiaojun)
 * @email: zhuxiaojun@tisson.com
 * @time: 2013-9-10上午09:50:36
 */
public class BftPayResultImpl extends PayResultRequest implements IBftPayResult{
	 private static final Logger logger = Logger.getLogger(BftPayResultImpl.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
		
		// TODO Auto-generated method stub
		// 接收"结果查询报文"数据元
		String userid="";
		String payid="";
		String orderamount="";
		String RetCode="";
		String TransDate="";
		String TransTime="";
		String SystemSSN="";
		String ylSystemSSN="";
		String SysCode="";
		String BankCard="";
		String BankCode="";
		String TransType="";

				
		TransData transdataRep = new TransData();// 定义应答数据包
		TransData transdataReq = new TransData();// 定义请求数据包

		transdataReq.setMerID("649");// XXX,填帮付通分配商户编号
		transdataReq.setUserID("15812345678");// 用户手机号
		transdataReq.setPayID("test01304031537221459");// 商户订单号
		transdataReq.setOrderAmount("0.01");// 订单金额
		transdataReq.setTransType("2005");// 交易类型

		BftPayResultImpl payresult = new BftPayResultImpl();// 声明"结果查询报文"类对像
		transdataRep = payresult.sendPayFindRequest(transdataReq);//调用"结果查询请求"方法,同时取应答
		
		//从应答数据包取数据元
		userid=transdataRep.getUserID();
		payid=transdataRep.getPayID();
		orderamount=transdataRep.getOrderAmount();
		RetCode=transdataRep.getRetCode();
		TransDate=transdataRep.getTransDate();
		TransTime=transdataRep.getTransTime();
		SystemSSN=transdataRep.getSystemSSN();
		ylSystemSSN=transdataRep.getYlSystemSSN();
		SysCode=transdataRep.getSysCode();
		BankCard=transdataRep.getBankCard();
		BankCode=transdataRep.getBankCode();
		TransType=transdataRep.getTransType();
	}

	   public TransData sendPayFindRequest(TransData transdata) {
		     TransData retTransData = new TransData();
		     ClassTools classtools = new ClassTools();
		    TransDataTools transdatatools = new TransDataTools();
		     TelTran teltran = new TelTran();
		     String sSuffix = "sjfWAP";
		     String sMd5Len = "22";
		     String sMastKey = "AA22CCDD88FF00DD";
		
		     String KeyStore = BftProperties.getKeyStore();
		     String HostURL = BftProperties.getHostURL();
		     GetKey getkey = null;
		 
		     String errorStr = "";
		     String toEncStr = "";
		     String retStr = "";
		     String retHttpStr = "";
		/*  36 */     String sKeyStr = "";
		/*  37 */     String sTransData = "";
		/*  38 */     boolean encbln = false;
		/*  39 */     boolean decbln = false;
		/*     */ 
		/*  41 */     logger.info("<<结果查询报文>>开始处理中......");
		/*  42 */     errorStr = transdatatools.checkPayFindRequest(transdata);
		/*     */ 
		/*  44 */     if (errorStr.length() != 0) {
		/*  45 */       logger.info("<<结果查询报文>>操作失败,处理结束......");
		/*  46 */       return retTransData;
		/*     */     }
		/*     */ 
		/*  49 */     if ((KeyStore == null) || (KeyStore.length() == 0)) {
		/*  50 */       logger.info("<<结果查询报文>>读取证书文件路径(KeyStore)错误,请查看MPI.properties文件!");
		/*  51 */       logger.info("<<结果查询报文>>操作失败,处理结束......");
		/*  52 */       return retTransData;
		/*     */     }
		/*     */ 
		/*  55 */     getkey = new GetKey(KeyStore);
		/*  56 */     sKeyStr = getkey.getkeystr();
		/*     */ 
		/*  58 */     if ((sKeyStr.equals("filerroe")) || (sKeyStr.length() == 0)) {
		/*  59 */       logger.info("<<结果查询报文>>读取证书文件路径(KeyStore)错误,请查看MPI.properties文件!");
		/*  60 */       logger.info("<<结果查询报文>>操作失败,处理结束......");
		/*  61 */       return retTransData;
		/*     */     }
		/*     */ 
		/*  64 */     if ((HostURL == null) || (HostURL.length() == 0)) {
		/*  65 */       logger.info("<<结果查询报文>>读取服务器地址(HostURL)错误,请查看MPI.properties文件!");
		/*  66 */       logger.info("<<结果查询报文>>操作失败,处理结束......");
		/*  67 */       return retTransData;
		/*     */     }
		/*     */ 
		/*  70 */     logger.info("<<结果查询报文>>读取证书文件成功!");
		/*  71 */     logger.info("<<结果查询报文>>读取服务器地址成功!");
		/*  72 */     if (errorStr.length() == 0) {
		/*  73 */       toEncStr = classtools.sndTransDataInfor(transdata);
		/*  74 */       logger.info("<<结果查询报文>>明文[" + toEncStr + "]");
		/*  75 */       encbln = teltran.EncryptMsg(sMastKey, sSuffix, sMd5Len, toEncStr, 
		/*  76 */         sKeyStr);
		/*     */     }
		/*  78 */     if (encbln) {
		/*  79 */       retStr = teltran.LastResult;
		/*  80 */       logger.info("<<结果查询报文>>密文[" + retStr + "]");
		/*  81 */       logger.info("<<结果查询报文>>操作成功,处理结束......");
		/*  82 */       retStr = HostURL + "?EncryptMsg=" + retStr;
		/*  83 */       logger.info("<<结果查询报文>>[" + retStr + "]");
		/*     */     } else {
		/*  85 */       retStr = teltran.LastErrMsg;
		/*  86 */       logger.info("<<结果查询报文>>[" + retStr + "]");
		/*  87 */       logger.info("<<结果查询报文>>操作失败,处理结束......");
		/*     */     }
		/*  89 */     retHttpStr = HttpClientPost.httpClientPost(retStr);
		/*     */ 
		/*  91 */     if ((retHttpStr == null) || (retHttpStr.equals("null")) || 
		/*  92 */       (retHttpStr.length() == 0)) {
		/*  93 */       logger.info("<<结果查询报文>>[与银联支付中心通讯异常]");
		/*  94 */       logger.info("<<结果查询报文>>操作失败,处理结束......");
		/*  95 */       return retTransData;
		/*     */     }
		/*  97 */     logger.info("<<结果查询应答>>密文[" + retHttpStr + "]");
		/*  98 */     decbln = teltran.DecryptMsg(sMastKey, sSuffix, sMd5Len, retHttpStr, 
		/*  99 */       sKeyStr);
		/* 100 */     if (decbln) {
		/* 101 */       sTransData = teltran.LastResult;
		/* 102 */       logger.info("<<结果查询应答>>明文[" + sTransData + "]");
		/* 103 */       logger.info("<<结果查询应答>>操作成功,处理结束......");
		/*     */     } else {
		/* 105 */       sTransData = teltran.LastErrMsg;
		/* 106 */       logger.info("<<结果查询应答>>明文[" + sTransData + "]");
		/* 107 */       logger.info("<<结果查询应答>>操作失败,处理结束......");
		/* 108 */       return retTransData;
		/*     */     }
		/*     */ 
		/* 111 */     retTransData = classtools.createTransData(sTransData);
		/*     */ 
		/* 113 */     return retTransData;
		   }
		 }
