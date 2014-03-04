/*    */ package com.tisson.pay.service.impl;
/*    */ 
/*    */ import mpi.client.data.TransData;
/*    */ import mpi.client.tools.ClassTools;
/*    */ import mpi.client.tools.GetKey;
/*    */ import mpi.client.tools.TelTran;
import mpi.client.trans.PayInforRequest;

import org.apache.log4j.Logger;

import com.tisson.pay.config.BftProperties;
/*    */ 
/*    */ public class BftPayRequest extends PayInforRequest
/*    */ {
/* 13 */   private static final Logger logger = Logger.getLogger(BftPayRequest.class);
/*    */ 
/*    */   public TransData reveivePayInforRequest(String payinfor) {
/* 16 */     TransData transdata = new TransData();
/* 17 */     ClassTools classtools = new ClassTools();
/* 18 */     TelTran teltran = new TelTran();
/*    */ 
/* 21 */     String sSuffix = "sjfWAP";
/* 22 */     String sMd5Len = "22";
/* 23 */     String sMastKey = "AA22CCDD88FF00DD";
/* 24 */     boolean decbln = false;
/*    */ 
/* 26 */     String KeyStore = BftProperties.getKeyStore();
/* 27 */     GetKey getkey = null;
/*    */ 
/* 29 */     String sKeyStr = "";
/* 30 */     String retStr = "";
/*    */ 
/* 32 */     getkey = new GetKey(KeyStore);
/* 33 */     sKeyStr = getkey.getkeystr();
/* 34 */     logger.info("<<支付通知请求>>开始处理中......");
/* 35 */     if ((sKeyStr.equals("filerroe")) || (sKeyStr.length() == 0)) {
/* 36 */       logger.info("<<支付通知请求>>读取证书文件路径(KeyStore)错误,请查看MPI.properties文件!");
/* 37 */       logger.info("<<支付通知请求>>操作失败,处理结束......");
/* 38 */       return transdata;
/*    */     }
/*    */ 
/* 41 */     logger.info("<<支付通知请求>>读取证书文件成功!");
/*    */ 
/* 43 */     decbln = teltran.DecryptMsg(sMastKey, sSuffix, sMd5Len, payinfor, 
/* 44 */       sKeyStr);
/*    */ 
/* 46 */     if (decbln) {
/* 47 */       retStr = teltran.LastResult;
/* 48 */       transdata = classtools.createTransData(retStr);
/* 49 */       transdata.printTransData();
/* 50 */       logger.info("<<支付通知请求>>明文[" + retStr + "]");
/* 51 */       logger.info("<<支付通知请求>>操作成功,处理结束......");
/*    */     } else {
/* 53 */       retStr = teltran.LastErrMsg;
/* 54 */       logger.info("<<支付通知请求>>[" + retStr + "]");
/* 55 */       logger.info("<<支付通知请求>>操作失败,处理结束......");
/*    */     }
/* 57 */     return transdata;
/*    */   }
/*    */ }

/* Location:           D:\天讯瑞达-公司项目\ipos改造\在线注册\手机支付工具包\java\sjfpay_mpi.jar
 * Qualified Name:     mpi.client.trans.PayInforRequest
 * JD-Core Version:    0.6.0
 */