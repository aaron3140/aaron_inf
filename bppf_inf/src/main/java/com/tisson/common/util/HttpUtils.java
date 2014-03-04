package com.tisson.common.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpUtils {

	/**
	 * 通过URL、方法、参数 、编码 获得服务端反回的字符串
	 * @param url
	 * @param method
	 * @param params
	 * @return
	 */
	public static String openUrl(String url, String method, Map params, String enc){
		
		String response = null;
		
		if(method.equals("GET")){
			url = url + "?" + encodeUrl(params);
		}
		//System.out.println("url:"+url);
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
//			conn.setRequestProperty("User-Agent", System.getProperties()
//					.getProperty("http.agent")
//					);
			conn.setReadTimeout(50000); //设置超时时间
			if(method.equals("POST")){
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.getOutputStream().write(encodeUrl(params).getBytes("UTF-8"));
			}
			response = read(conn.getInputStream(),enc);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		return response;
	}
	
	public static String queryJsonData(String urlStr,String jsonString)throws Exception{
			URL url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type",
					"application/json");
			connection.connect();
			// POST请求
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			out.writeBytes(jsonString);
			out.flush();
			out.close();

			String str = read(connection.getInputStream(),"utf8");
			
			// 断开连接
			connection.disconnect();
			return str;
	}

	/**
	 * 将InputStream按一定编码读成字符串
	 * @param in
	 * @param enc
	 * @return
	 * @throws IOException
	 */
	private static String read(InputStream in, String enc) throws IOException {
		StringBuilder sb = new StringBuilder();
		InputStreamReader isr = null;
		BufferedReader r = null;
		if(enc != null){
			//按指定的编码读入流
			r = new BufferedReader(new InputStreamReader(in,enc), 1000);
		}else{
			//按默认的编码读入
			r = new BufferedReader(new InputStreamReader(in), 1000);
		}

		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}
	/**
	 * 将HashMap parameters类型的参数封闭到URL中
	 * 
	 * @param hm
	 * @return
	 */
	public static String encodeUrl(Map hm) {
		if (hm == null)
			return "";
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		Iterator<String> it =  hm.keySet().iterator();
		while (it.hasNext()) {
			if (first)
				first = false;
			else
				sb.append("&");
			String key = it.next();
			sb.append(key + "=" + hm.get(key));
			}
		return sb.toString();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<String , String> hm = new HashMap<String , String>();
		hm.put("1", "OOO");
		hm.put("3", "OOO");
		hm.put("2", "OOO");
		hm.put("5", "OOO");
		hm.put("4", "OOO");
		String sb="";
		sb = HttpUtils.encodeUrl(hm);
		System.out.println(sb.toString());
	}

}
