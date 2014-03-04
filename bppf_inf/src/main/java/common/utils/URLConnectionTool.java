package common.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

public class URLConnectionTool {
	private static Logger log = Logger.getLogger(URLConnectionTool.class);
	
	public static String readContentFromPost(String POST_URL, String params,
			String connectTimeLimit, String backResTimeLimit)
			throws Exception {
		// Post请求的url，与get不同的是不需要带参数
		HttpURLConnection connection = null;
		OutputStream out = null;
		InputStream in = null;
		//URL postUrl = new URL("http://localhost:8080/PCPF_INF/test.do");
		String result = "";
		 URL postUrl = new URL(POST_URL);
		// 打开连接
		try {
			connection = (HttpURLConnection) postUrl.openConnection();
			// 设置是否向connection输出，因为这个是post请求，参数要放在
			// http正文内，因此需要设为true
			connection.setDoOutput(true);
			connection.setDoInput(true);
			// 有的请求不支持POST请求
			connection.setConnectTimeout(Integer.parseInt(connectTimeLimit));
			connection.setReadTimeout(Integer.parseInt(backResTimeLimit));
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			// connection.setRequestProperty("Content-Type" ,"text/html");
			log.info("连接后台地址：" + POST_URL);
			connection.connect();
			log.info("连接成功");
			out = connection.getOutputStream();
			byte[] content = (params).getBytes();
			log.info("发送内容为：" + params);
			out.write(content);
			out.flush();
			log.info("发送成功");
			
			log.info("读取后台地址返回");
			byte[] retByte = new byte[1024];
			in = connection.getInputStream();
			in.read(retByte);
			String ret = new String(retByte);

			log.info("读取成功，后台返回：" + ret);
		} catch (Exception e) {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
			if (connection != null) {
				connection.disconnect();
			}
			
			log.error("连接后台异常", e);
			throw e;
		}
		return result;
	}
}
