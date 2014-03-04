package com.tisson.pay.service.impl;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import mpi.client.data.TransData;
import mpi.client.tools.ClassTools;
import mpi.client.tools.GetKey;
import mpi.client.tools.TelTran;
import mpi.client.tools.TransDataTools;

import com.tisson.pay.config.BftProperties;
import com.tisson.pay.service.IBftOrderPayRequest;

import framework.config.ConfigReader;
import framework.config.GlobalConstants;

public class BftOrderPayRequestimpl implements IBftOrderPayRequest {
	private static final Logger logger = Logger.getLogger(BftOrderPayRequestimpl.class);
	public static void main(String args[]) {
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
		
		// 模拟商户唯一订单号
		Date dateNow = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = formatter.format(dateNow);
		String payid = "test" + date;
		String reqStr = "";

		// 发送"支付请求"数据包
		TransData transdata = new TransData();// 定义请求数据包
		
		transdata.setMerID("649");// XXX,填帮付通分配商户编号
		transdata.setMerName("XXX门户网");// 商户简称
		transdata.setUserID("15812345678");// 用户手机号
		String aa = "";
		try {
			aa = new String("国健步如飞".getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		transdata.setUserName(aa);// 用户姓名
		transdata.setBankCard("6228480040050604517");
		transdata.setBankPID("362334123456789");
		transdata.setPayID(payid);// 商户订单号
		transdata.setOrderAmount("0.01");// 订单金额
		transdata.setTransDate(date.substring(0, 8));// 交易日期
		transdata.setTransTime(date.substring(8));// 交易时间
		transdata.setPageType("3");// 3=HTML5
		transdata.setMerHomeUrl("http://www.163.com?Type=12&name=23");// 用户支付完成后,返回商户页面地址
		transdata.setMerBackUrl("http://www.sina.com?Type=45&name=67");// 用户取消支付,,返回商户页面地址
		transdata.setTransType("3001");// 交易类型
		
		
		BftOrderPayRequestimpl orderpay = new BftOrderPayRequestimpl();// 声明"支付请求报文"类对像
		reqStr = orderpay.sendOrderPayRequest(transdata);// 调用"订单支付请求"方法,生成请求串
		logger.info("======"+reqStr);
		//商户需要自行编写程序,页面重定向到-->reqStr;
		//即,重定向到-->http://218.206.25.149:8083/sjfpay_yzf/sjfpay.do?EncryptMsg=1933224302B222F122137362F295863717C7C67173126310D00787202720E017C0A73717674777376777772747401050E0D707376707774007106757E607B534A71057C737B70650921370B272B530B606019640171660071610707630701137B7C64077A641630360A2428237B13720D1D027467017B660200630727285D66717C7C6712233A0A0079312035320406090B7175727572757470777D600944525D4A002C2D372D3779746B75776079445C5D33072727022E2B312B317B6079445C5D33002136022E2B312B317B60795E59562F32272E022E2B312B317B606244595632052336267E76747476767206011E6C33202C31172A2921787470760204001E12383136262E17170B78603F5A65414B35242F11100D79621720320559525D0567123B31002C202178630427585D7B5933257F6401222A2F062A22230B106F5931153B32267E75620820340E595B5D6D332D7F2A373734617604637470130A7E3636356C7275776A262A2B6305706C4131246771077276617773272B46130B7A2F202F276670007676630B234474595B2A14302E7E2B30303560750713047E1D73073535346D372D2B246825595B1D0B07153B322666770071706374005755486472002C222E21617601707110645D4C05243121312A34302C2A287B10645D55203329737E6516212824342D040B1E6A242C2330287079621137272845624148247C7372737262007072757F040F0F0D00050701737072010B01		
		
	}
	
	/*    */   public String sendOrderPayRequest(TransData transdata)
	/*    */   {
	/* 20 */     ClassTools classtools = new ClassTools();
	/* 21 */     TransDataTools transdatatools = new TransDataTools();
	/* 22 */     TelTran teltran = new TelTran();
	/* 24 */     String sSuffix = "sjfWAP";
	/* 25 */     String sMd5Len = "22";
	/* 26 */     String sMastKey = "AA22CCDD88FF00DD";
	/*    */ 
	/* 28 */     String KeyStore = BftProperties.getKeyStore();
	/* 29 */     String HostURL = BftProperties.getHostURL();
	/* 30 */     GetKey getkey = null;
	/*    */ 
	/* 32 */     String errorStr = "";
	/* 33 */     String toEncStr = "";
	/* 34 */     String retStr = "";
	/* 35 */     String sKeyStr = "";
	/* 36 */     boolean encbln = false;
	/*    */ 
	/* 38 */     logger.info("<<支付请求报文>>开始处理中......");
	/* 39 */     errorStr = transdatatools.checkOrderPayRequest(transdata);
	/*    */ 
	/* 41 */     if (errorStr.length() != 0) {
	/* 42 */       logger.info("<<支付请求报文>>操作失败,处理结束......");
	/* 43 */       return retStr;
	/*    */     }
	/*    */ 
	/* 46 */     if ((KeyStore == null) || (KeyStore.length() == 0)) {
	/* 47 */       logger.info("<<支付请求报文>>读取证书文件路径(KeyStore)错误,请查看MPI.properties文件!");
	/* 48 */       logger.info("<<支付请求报文>>操作失败,处理结束......");
	/* 49 */       return retStr;
	/*    */     }
	/*    */ 
	/* 52 */     getkey = new GetKey(KeyStore);
	/* 53 */     sKeyStr = getkey.getkeystr();
	/*    */ 
	/* 55 */     if ((sKeyStr.equals("filerroe")) || (sKeyStr.length() == 0)) {
	/* 56 */       logger.info("<<支付请求报文>>读取证书文件路径(KeyStore)错误,请查看MPI.properties文件!");
	/* 57 */       logger.info("<<支付请求报文>>操作失败,处理结束......");
	/* 58 */       return retStr;
	/*    */     }
	/*    */ 
	/* 61 */     if ((HostURL == null) || (HostURL.length() == 0)) {
	/* 62 */       logger.info("<<支付请求报文>>读取服务器地址(HostURL)错误,请查看MPI.properties文件!");
	/* 63 */       logger.info("<<支付请求报文>>操作失败,处理结束......");
	/* 64 */       return retStr;
	/*    */     }
	/*    */ 
	/* 67 */     logger.info("<<支付请求报文>>读取证书文件成功!");
	/* 68 */     logger.info("<<支付请求报文>>读取服务器地址成功!");
	/* 69 */     if (errorStr.length() == 0) {
	/* 70 */       toEncStr = classtools.sndTransDataInfor(transdata);
	/* 71 */       logger.info("<<支付请求报文>>明文[" + toEncStr + "]");
	/* 72 */       encbln = teltran.EncryptMsg(sMastKey, sSuffix, sMd5Len, toEncStr, 
	/* 73 */         sKeyStr);
	/*    */     }
	/* 75 */     if (encbln) {
	/* 76 */       retStr = teltran.LastResult;
	/* 77 */       logger.info("<<支付请求报文>>密文[" + retStr + "]");
	/* 78 */       logger.info("<<支付请求报文>>操作成功,处理结束......");
	/* 79 */       retStr = HostURL + "?EncryptMsg=" + retStr;
	/* 80 */       logger.info("<<支付请求报文>>[" + retStr + "]");
	/*    */     } else {
	/* 82 */       retStr = teltran.LastErrMsg;
	/* 83 */       logger.info("<<支付请求报文>>[" + retStr + "]");
	/* 84 */       logger.info("<<支付请求报文>>操作失败,处理结束......");
	/*    */     }
	/*    */ 
	/* 87 */     return retStr;
	/*    */   }
}
//运行日志如下:
/**
 *  mpi.client.trans.OrderPayRequest 2013-04-07 17:21:31,343 -- INFO -- <<支付请求报文>>开始处理中......
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- Trans Data Begin:  
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- UserID          =[15812345678]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- MerID           =[XXX]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- MerName         =[XXX门户网]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- UsrName         =[张三]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- BankPID         =[]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- PayID           =[test20130407172131]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- OrderAmount     =[0.01]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- OrderFeeAmount  =[]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- OrderActAmount  =[]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- OhannselAmount  =[]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- TransDate       =[20130407]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- TransTime       =[172131]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- SystemSSN       =[]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- ylSystemSSN     =[]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- RetCode         =[]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- SysCode         =[]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- BankCard        =[]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- BankCode        =[]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- WapType         =[1]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- MerHomeUrl      =[http://www.163.com?Type=12&name=23]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- MerBackUrl      =[http://www.sina.com?Type=45&name=67]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- RetDescription  =[]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- Remark1         =[]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- Remark2         =[]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- Remark3         =[]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- TransType       =[1001]
	mpi.client.data.TransData 2013-04-07 17:21:31,343 -- INFO -- Trans Data End.  
	mpi.client.trans.OrderPayRequest 2013-04-07 17:21:31,343 -- INFO -- <<支付请求报文>>读取证书文件成功!
	mpi.client.trans.OrderPayRequest 2013-04-07 17:21:31,343 -- INFO -- <<支付请求报文>>读取服务器地址成功!
	mpi.client.trans.OrderPayRequest 2013-04-07 17:21:31,343 -- INFO -- <<支付请求报文>>明文[serialVersionUID=&UserID=15812345678&MerID=193&MerName=XXX%C3%C5%BB%A7%CD%F8&UsrName=%D5%C5%C8%FD&BankPID=&PayID=test20130407172131&OrderAmount=0.01&OrderFeeAmount=&OrderActAmount=&OhannselAmount=&TransDate=20130407&TransTime=172131&SystemSSN=&ylSystemSSN=&RetCode=&SysCode=&BankCard=&BankCode=&WapType=1&MerHomeUrl=http%3A%2F%2Fwww.163.com%3FType%3D12%26amp%3Bname%3D23&MerBackUrl=http%3A%2F%2Fwww.sina.com%3FType%3D45%26amp%3Bname%3D67&RetDescription=&Remark1=&Remark2=&Remark3=&TransType=1001&]
	mpi.client.trans.OrderPayRequest 2013-04-07 17:21:31,343 -- INFO -- <<支付请求报文>>密文[1933224302B222F122137362F295863717C7C67173126310D00787202720E017C0A73717674777376777772747401050E0D707376707774007106757E607B534A71057C737B70650921370B272B530B606019640171660071610707630701137B7C64077A641630360A2428237B13720D1D027467017B660200630727285D66717C7C6712233A0A0079312035320406090B71757275727476757674600944525D4A002C2D372D3779746B75776079445C5D33072727022E2B312B317B6079445C5D33002136022E2B312B317B60795E59562F32272E022E2B312B317B606244595632052336267E76747476767206011E6C33202C31172A2921787471740705091E12383136262E17170B78603F5A65414B35242F11100D79621720320559525D0567123B31002C202178630427585D7B5933257F6401222A2F062A22230B106F5931153B32267E75620820340E595B5D6D332D7F2A373734617604637470130A7E3636356C7275776A262A2B6305706C4131246771077276617773272B46130B7A2F202F276670007676630B234474595B2A14302E7E2B30303560750713047E1D73073535346D372D2B246825595B1D0B07153B322666770071706374005755486472002C222E21617601707110645D4C05243121312A34302C2A287B10645D55203329737E6516212824342D040B1E6A242C2330287079621137272845624148247C7372737262007072757F040F0F0D00050701737072010B01]
	mpi.client.trans.OrderPayRequest 2013-04-07 17:21:31,343 -- INFO -- <<支付请求报文>>操作成功,处理结束......
	mpi.client.trans.OrderPayRequest 2013-04-07 17:21:31,343 -- INFO -- <<支付请求报文>>[http://218.206.25.149:8083/wappay_wbd/wappay_wbd.do?EncryptMsg=1933224302B222F122137362F295863717C7C67173126310D00787202720E017C0A73717674777376777772747401050E0D707376707774007106757E607B534A71057C737B70650921370B272B530B606019640171660071610707630701137B7C64077A641630360A2428237B13720D1D027467017B660200630727285D66717C7C6712233A0A0079312035320406090B71757275727476757674600944525D4A002C2D372D3779746B75776079445C5D33072727022E2B312B317B6079445C5D33002136022E2B312B317B60795E59562F32272E022E2B312B317B606244595632052336267E76747476767206011E6C33202C31172A2921787471740705091E12383136262E17170B78603F5A65414B35242F11100D79621720320559525D0567123B31002C202178630427585D7B5933257F6401222A2F062A22230B106F5931153B32267E75620820340E595B5D6D332D7F2A373734617604637470130A7E3636356C7275776A262A2B6305706C4131246771077276617773272B46130B7A2F202F276670007676630B234474595B2A14302E7E2B30303560750713047E1D73073535346D372D2B246825595B1D0B07153B322666770071706374005755486472002C222E21617601707110645D4C05243121312A34302C2A287B10645D55203329737E6516212824342D040B1E6A242C2330287079621137272845624148247C7372737262007072757F040F0F0D00050701737072010B01]
 */