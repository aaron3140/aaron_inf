package framework.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.algorithm.MD5;
import common.dao.TInfDcoperlogDao;
import common.entity.PayWap;
import common.utils.Charset;
import common.utils.Merchant;
import common.utils.MerchantCache;
import common.utils.WebSvcTool;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class TestServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(arg0, arg1);
	}
	
    @Override
    protected void doPost(HttpServletRequest hReq, HttpServletResponse hRes) throws ServletException, IOException {
    	// TODO Auto-generated method stub
    	String str = hReq.getParameter("TRADESEQ");
    	OutputStream out= hRes.getOutputStream();
    	byte[] content =("ORDERID_"+"1222").getBytes();
        out.write(content); 
        out.flush();
        out.close();  
	//	System.out.print("xxxxxxxxxxxxxxxx"+str);
    }
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
