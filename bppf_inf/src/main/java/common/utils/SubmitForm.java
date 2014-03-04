package common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

public class SubmitForm {
	private static final Logger log = Logger.getLogger(SubmitForm.class);
	String strUrl="";
	String responseStr="";
	boolean flag = false;
	int readTimeOut = 30 * 1000;
	
	public String getStrUrl() {
		return strUrl;
	}

	public void setStrUrl(String strUrl) {
		this.strUrl = strUrl;
	}

	/**
	 * 将内容提交,strKeyValues的格式为key1=value1&key2=value2
	 * @param strKeyValues
	 * Creator :czg
	 * DateTime:2010-12-26 上午02:03:29
	 */
	public void submitForm(String strKeyValues){
		responseStr="";
		try{
			URL url = new URL(this.strUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(readTimeOut);
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			PrintWriter out = new PrintWriter(connection.getOutputStream());
			out.print(strKeyValues);
		    out.close();
		    BufferedReader in= new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    String line=null;
		    while ((line = in.readLine()) != null) {
		    	responseStr+=line+System.getProperty("line.separator");
		    }
		    in.close();
		} catch(Exception e){
			log.info("回调地址无法连接!"+strUrl);
			log.info(e.getMessage());
//			log.info("----------------------------------------------------------------------------");
//			e.printStackTrace();
		}
		flag=true;
	}
	
	public void submitForm1(String strKeyValues)throws Exception{
		responseStr="";
		try{
			URL url = new URL(this.strUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(readTimeOut);
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			PrintWriter out = new PrintWriter(connection.getOutputStream());
			out.print(strKeyValues);
		    out.close();
		    BufferedReader in= new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    String line=null;
		    while ((line = in.readLine()) != null) {
		    	responseStr+=line+System.getProperty("line.separator");
		    }
		    in.close();
		} catch(Exception e){
			log.info("回调地址无法连接!"+strUrl);
			log.info(e.getMessage());
			throw e;
		}
//		flag=true;
	}
	
	public String getResponseStr() {
		return responseStr;
	}

	public static void main(String[] argv) throws Exception {
		//SubmitForm form=new SubmitForm();
//		form.setStrUrl("http://localhost:8080/TestWeb/accnbrunpack.do?method=unpackBatchSearch");
//		form.submitForm("batch_id=go&page=yes");
//		System.out.println(form.getResponseStr());
		//form.setStrUrl("http://webpay.bestpay.com.cn/payWebDirect.do");
		//form.submitForm("MERCHANTID=4401000001&SUBMERCHANTID=&ORDERSEQ=20120326174400120&ORDERREQTRANSEQ=20120326174400319&ORDERDATE=20120326&ORDERAMOUNT=5&PRODUCTAMOUNT=5&ATTACHAMOUNT=0&CURTYPE=RMB&ENCODETYPE=1&MERCHANTURL=scm/shopping/response.do?method=payReturnFront&BACKMERCHANTURL=scm/shopping/response.do?method=payReturnFront&ATTACH=&BUSICODE=0001&TMNUM=&CUSTOMERID=&PRODUCTID=&PRODUCTDESC=&MAC=E68528EFE9BEE2BC7B82C21313FAA56E&BANKID=BOC");
		//System.out.println(form.getResponseStr());
		
		
		PostMethod postMethod = new PostMethod("http://121.33.197.198/bppf/loginpay.do?method=receiveOrder");
		//PostMethod postMethod = new PostMethod("https://webgate.bestpay.com.cn/bestPayBackNotice.do");
		// 将表单的值放入postMethod中
		//postMethod.setRequestBody("MERCHANTID=4401000010&ORDERSEQ=120529006900531&ORDERDATE=20120529&ORDERAMOUNT=1&MAC=31696132EEB91B4434AB80B42BDF3AA1&ORDERREQTRANSEQ=120529006900531&PRODUCTAMOUNT=1&MERCHANTURL=http://121.33.197.198:8085/bppf/payrespfront.do&BACKMERCHANTURL=http://121.33.197.198:8085/bppf/payrespback.do&SUBMERCHANTID=&ATTACH=&BUSICODE=0001&TMNUM=&CUSTOMERID=&PRODUCTID=&PRODUCTDESC=&CURTYPE=RMB&ENCODETYPE=1&ATTACHAMOUNT=0");
		//INTERFACETYPE=10001&TRANDATE=20120611024501&ORDERAMOUNT=1&CUSTCODE=ti02@189.com&ENCODETYPE=1&DETAILS=1^1339397092001^120611006903304^20120611024501^00000000^1&MAC=40df5bb0def5a26f818ca90941f366b6&SIGNMSG=
		postMethod.setRequestBody("INTERFACENAME=BESTPAY_B2C&INTERFACEVERSION=1.0.0.0&MERID=8604400000104700&CHANNELTYPE=90&TMNNUM=90000124&CUSTCODE=bfb02@189.com&ORDERAMOUNT=3&ENCODETYPE=1&DETAILS=1^20120611105801^1001^bfb04@189.com^1^REMAK1|2^20120611105802^1001^bfb05@189.com^2^REMAK1&MAC=10518fdcadc0dca7d78c42540879251a&SIGNMSG=");
		
		// 释放连接方式
		postMethod.setRequestHeader("Connection","close");
		HttpClient client = new HttpClient();
		int ConnectionTimeOut = 30 * 1000;
		client.getHttpConnectionManager().getParams().setConnectionTimeout(ConnectionTimeOut);
		client.getHttpConnectionManager().getParams().setSoTimeout(ConnectionTimeOut);
		// 重用连接方式
		// Http http = Http.getInstance();
		// client = http.getHttpClient();
		// commTranStatus = "B";
		int statusCode = client.executeMethod(postMethod);
		
		System.out.println(postMethod.getResponseBodyAsString());
		System.out.println("statusCode======="+statusCode);
		
	}
}
