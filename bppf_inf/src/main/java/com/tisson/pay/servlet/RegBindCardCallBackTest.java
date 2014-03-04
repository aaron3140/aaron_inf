package com.tisson.pay.servlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 邱亚建 2013-9-27 下午04:57:21<br/>
 * 
 * 本类描述: 帮付通绑卡模拟回调测试类
 */
public class RegBindCardCallBackTest {

	public static void main(String[] args) {
//		http://183.63.191.42:8081/bppf_inf/servlet/RegBindCard?EncryptMsg=143257400A07797401080571010075747271000275747574000802760507727302767105767306737B1E0B234279007977750B1413223D0D7C0574760103757470730302707276760A007374167F36202433735F2C362A30050976761E00621033205C4107223021050A76770301747573676640222D37105155237B0101717D757814613A303021556B15080D0274757270020371727571010C7F2A5C163D2812384146262E1717760574760103757470730303767A707D1E6B3F35735F20217C71021401222A2F7B5934220D06767679750A02737774740D087076040575736703535C28002B205D057676000262162435715D27267974081E1234515E37103831570F71737476000A7F7F070676707102070A7277067C7D7602
		String callbackUrl = "http://192.168.95.146:8080/bppf_inf/servlet/RegBindCard";// 回调地址
		RegBindCardCallBackTest test = new RegBindCardCallBackTest();
		// 模拟回调的参数
		StringBuffer sb = new StringBuffer();
		sb.append("UserId=13800138000")//
				.append("&MerId=649")//
				.append("&PayId=123456")//
				.append("&OrderAmount=100")//
				.append("&TransDate=20130927")// yyyyMMdd
				.append("&TransTime=171200")// hhmmss
				.append("&SystemSSN=654321")//
				.append("&ylSystemSSN=987654")//
				.append("&RetCode=000000")//
				.append("&SysCode=000000")//
				.append("&BankCard=9897974646465")//
				.append("&BankCode=866000")//
				.append("&Description=交易成功")//
				.append("&TransType=2002");
		
		String EncryptMsg="143257400A07797401080571010075747271000275747574000802760507727302767105767306737B1E0B234279007977750B1413223D0D7C0574760103757470730302707276760A007374167F36202433735F2C362A30050976761E00621033205C4107223021050A76770301747573676640222D37105155237B0101717D757814613A303021556B15080D0274757270020371727571010C7F2A5C163D2812384146262E1717760574760103757470730303767A707D1E6B3F35735F20217C71021401222A2F7B5934220D06767679750A02737774740D087076040575736703535C28002B205D057676000262162435715D27267974081E1234515E37103831570F71737476000A7F7F070676707102070A7277067C7D7602";
		test.setStrUrl(callbackUrl+"?EncryptMsg="+EncryptMsg);
		test.submitForm("");
		System.out.println(test.getResponseStr());
	}

	String strUrl = "";
	String responseStr = "";

	public String getStrUrl() {
		return strUrl;
	}

	public void setStrUrl(String strUrl) {
		this.strUrl = strUrl;
	}

	public void submitForm(String strKeyValues) {
		responseStr = "";
		try {
			URL url = new URL(this.strUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			PrintWriter out = new PrintWriter(connection.getOutputStream());
			out.print(strKeyValues);
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				responseStr += line + System.getProperty("line.separator");
			}
			in.close();
			System.out.println("获取到的返回值,responseStr=" + responseStr);
		} catch (Exception e) {
			System.out.println("访问[" + strUrl + "]时出错");
			e.printStackTrace();
		}
	}

	public String getResponseStr() {
		return responseStr;
	}

}
