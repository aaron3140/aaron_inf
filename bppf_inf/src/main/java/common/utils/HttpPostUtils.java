package common.utils;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

//import common.algorithm.CryptTool;

public class HttpPostUtils  {

	private static final int HTTP_POST_REQUEST_CONNECT_TIMEOUT = 30000;
	private static final int HTTP_POST_RESPONSE_READ_TIMEOUT = 30000;
	
	private static final int HTTP_POST_RESPONSE_BUFFER_SIZE = 1024;
	
	private static final String INTERNAL_CHARSET = "GBK";
		
	//implements IHttpChannel
    private Logger logger = Logger.getLogger(HttpPostUtils.class);
    private URL url;

    public HttpPostUtils(String url) throws Exception {
        this.url = new URL(url);
    }

    public String sendPostRequest(String content) throws Exception {
        logger.debug("try to open http connection to: '" + this.url.getPath()
                + ":" + this.url.getPort() + "'");
        HttpURLConnection conn = (HttpURLConnection) this.url.openConnection();
        
        conn.setConnectTimeout(HTTP_POST_REQUEST_CONNECT_TIMEOUT);
        conn.setReadTimeout(HTTP_POST_RESPONSE_READ_TIMEOUT);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        
        logger.debug("try to get output stream");
        OutputStream output = conn.getOutputStream();
        
        logger.debug("try to write '" + content + "'");
        output.write(content.getBytes("UTF-8"));
        output.flush();
        output.close();
        
        logger.debug("try to get input stream");
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn
                .getInputStream()));
        String line;
        StringBuffer buffer = new StringBuffer(HTTP_POST_RESPONSE_BUFFER_SIZE);
        
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        
        String value = buffer.toString();
        logger.debug("get resposne: '" + value + "'");
        
        value = URLDecoder.decode(value, INTERNAL_CHARSET);
        return value;
    }
    
    public String sendPostRequest(String content,String charset) throws Exception {
        logger.debug("try to open http connection to: '" + this.url.getPath()
                + ":" + this.url.getPort() + "'");
        HttpURLConnection conn = (HttpURLConnection) this.url.openConnection();
        
        conn.setConnectTimeout(HTTP_POST_REQUEST_CONNECT_TIMEOUT);
        conn.setReadTimeout(HTTP_POST_RESPONSE_READ_TIMEOUT);
        conn.setDoOutput(true);
        conn.setRequestMethod("GET");
        
        logger.debug("try to get output stream");
        OutputStream output = conn.getOutputStream();
        
        if(Charset.isEmpty(charset)){
        	charset = INTERNAL_CHARSET ;
        }
        logger.debug("try to write '" + content + "'");
        output.write(content.getBytes(charset));
        output.flush();
        output.close();
        
        logger.debug("try to get input stream");
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn
                .getInputStream(),charset));
        String line;
        StringBuffer buffer = new StringBuffer(HTTP_POST_RESPONSE_BUFFER_SIZE);
        
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        
        String value = buffer.toString();
        logger.debug("get resposne: '" + value + "'");
        
        value = URLDecoder.decode(value, INTERNAL_CHARSET);
        return value;
    }
    
    public static void main(String[] args) {
//		String url = "http://132.97.117.35:8480/infs/generateOrder.do";
//		String url = "http://localhost:8080/infs/generateOrder.do";
    	
		String url = "http://121.8.243.78:8480/infs/generateOrder.do";
//		String url = "http://121.8.243.78:9480/infs/generateOrder.do";
		String res; 
		try {
			String str= URLEncoder.encode("�����̻�", "GBK");
			String gn = URLEncoder.encode("��ʱ����", "GBK");
//			long round = Math.round(Math.random()*69019+1000);
//			String str= "testM";
//			String gn = "testP";
			long seq = 201200004;
			int s = 650;
			//18911069019
			for (int i = 69019; i < 70000; i++, seq++) {
				String source = "REQUESTSEQ=" + seq + "&CUSTCODE=18911"+ Charset.lpad(String.valueOf(i), 6, "0") +"&TRADETIME=20120704144156&PRTNCODE=0018888888&TXNAMOUNT=30&KEY=G7AXS7874305BV59";
				String mac = "";//CryptTool.md5Digest(source).toUpperCase();
				res = new HttpPostUtils(url).sendPostRequest("REQUESTSEQ="+seq+"&PRTNCODE=0018888888&PRTNNAME=" + str + "&CUSTCODE=18911"+ Charset.lpad(String.valueOf(i), 6, "0")+"&TXNAMOUNT=30&GOODSCODE=1002&GOODSNAME="+ gn +"&GOODPAYTYPE=0&GOODTYPE=0&GOODSNUM=1&TRADETIME=20120704144156&VERIFYCODE=9527&CUSTSVCPHONE=123&DESCRIPTION=&ENCODETYPE=1&MAC="+ mac, "GBK");
				System.out.println(res);
			}
//			String seq = "2012081600000004"; 
//			String source = "REQUESTSEQ=" + seq + "&CUSTCODE=18911206159&TRADETIME=20120704144156&PRTNCODE=0018888888&TXNAMOUNT=1&KEY=G7AXS7874305BV59";
//			String mac = CryptTool.md5Digest(source).toUpperCase();
//			res = new HttpPostUtils(url).sendPostRequest("REQUESTSEQ="+seq+"&PRTNCODE=0018888888&PRTNNAME=" + str + "&CUSTCODE=18911206159&TXNAMOUNT=1&GOODSCODE=1002&GOODSNAME="+ gn +"&GOODPAYTYPE=0&GOODTYPE=0&GOODSNUM=1&TRADETIME=20120704144156&VERIFYCODE=9527&CUSTSVCPHONE=123&DESCRIPTION=&ENCODETYPE=1&MAC="+ mac, "GBK");
//			System.out.println(res);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
