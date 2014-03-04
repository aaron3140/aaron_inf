package framework.filter;

import httpPostSvc.HttpPostDispatcher;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.utils.FileUtil;

public class HTTPPostFilter extends HttpServlet implements Filter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		String fileName="WEB_"+request.getParameter("WEBSVRCODE")+".txt";
		File f=new File(FileUtil.PAGE_PARAM_PATH+fileName);
		f.deleteOnExit();
		
		FileUtil.createPath(FileUtil.PAGE_PARAM_PATH,"");
		
		String str="";
		Map map=((HttpServletRequest)request).getParameterMap();
		//map.entrySet().iterator();
		Iterator iter=map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry=(Map.Entry)iter.next();
			
			if (entry.getKey()!=null&&entry.getValue()!=null) {
				str+=entry.getKey()+"="+request.getParameter(entry.getKey().toString())+System.getProperty("line.separator");
			}
		}
		
		FileUtil.writeFile(FileUtil.PAGE_PARAM_PATH+fileName, str);
		
		HttpPostDispatcher.dispatch((HttpServletRequest) request, (HttpServletResponse) response);
	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
