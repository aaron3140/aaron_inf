package common.utils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.utils.verify.VCodeBuilder;
import sun.misc.BASE64Encoder;




/**
 * 短信下发工具
 * @author tisson
 * @time 2013-05-22
 */
public class SmsTool {
      Logger log = LoggerFactory.getLogger(SmsTool.class);
	
	  public int sendsms(String account,String url,Document document) throws Throwable {
	        log.info("------------------------开始发送下行短信------------------------");	        
	        PostMethod postMethod = null;
	        int code =0;
	        try {
	            postMethod = new PostMethod(url);
	            postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "GBK");
	            postMethod.addParameter("postType", "sendSms");
	            postMethod.addParameter("ea", "0");
	            postMethod.addParameter("account", account);
	            
	            log.info("请求参数：" + document.asXML());
	            postMethod.addParameter("smsMtMessage", document.asXML());
	            HttpClient httpClient = new HttpClient();
	            HttpConnectionManagerParams managerParams = httpClient.getHttpConnectionManager().getParams();
	            // 设置连接超时时间(单位毫秒)
	            managerParams.setConnectionTimeout(60000);
	            // 设置读数据超时时间(单位毫秒)
	            managerParams.setSoTimeout(120000);
	            httpClient.executeMethod(postMethod);
	            code = postMethod.getStatusCode();
	            if (code == HttpStatus.SC_OK) {
	                log.info("返回结果：" + new String(postMethod.getResponseBodyAsString().trim()));

	            }
	            if (postMethod != null)
	                postMethod.releaseConnection();
	            return code;
	        } catch (Exception e) {
	            try {
	                Thread.sleep(1000 * 60);
	            } catch (InterruptedException ex) {
	                log.error("向网关发送短信失败",ex);
	            }
	            return 0;
	        } finally {
	            if (postMethod != null)
	                postMethod.releaseConnection();
	            
	            log.info("------------------------结束发送下行短信------------------------");	           
	        }	        
	    }
	  
	  /**
	   * 
	   * @param password  MD5加密过的密文
	   * @param captcha   验证码
	   * @return
	   * @throws UnsupportedEncodingException
	   */
	public Document getDocument(String password,String mobile,String captcha) throws UnsupportedEncodingException{
		    Document document = DocumentHelper.createDocument();
		    BASE64Encoder baseEncoder = new BASE64Encoder();
		    
		    log.info(captcha);
		    String content = baseEncoder.encodeBuffer(captcha.getBytes("utf-8"));
	        Element resRoot = document.addElement("smsMt");
	        
//	        resRoot.addElement("account").setText("zh");
	        resRoot.addElement("password").setText(password);
	        resRoot.addElement("phoneNumber").setText(mobile);
	        resRoot.addElement("smsType").setText("20");
	        resRoot.addElement("priority").setText("3");
	        resRoot.addElement("smsId").setText("");	       	       
	        resRoot.addElement("content").setText(content);
	        resRoot.addElement("subCode").setText("12345");
	        resRoot.addElement("sendTime").setText(formateDate());
	        
	        return document;
	}
	/**
	 * 当前时间的yyyyMMddHHmmss的字符串形式
	 * @return
	 */
	public String formateDate(){
		SimpleDateFormat formate = new SimpleDateFormat("yyyyMMddHHmmss");
		return formate.format(new Date());
	}
	public static void main(String arg[]){
		String v = new VCodeBuilder().generate();
		String url = "http://116.228.55.189:8080/ema/servlet/smsEa";
		String msg = "你的翼支付登录验证码是: " + v;
		String mobile = "13556182709";
		
		SmsTool smstool = new SmsTool();
		String account	= "qyzh";
		String password = "cbff36039c3d0212b3e34c23dcde1456";
		Document document;
		int code = 0;
		try {
			document = smstool.getDocument(password, mobile, msg);
			try {
				code = smstool.sendsms(account, url, document);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(code == HttpStatus.SC_OK){
	}
	}
}
