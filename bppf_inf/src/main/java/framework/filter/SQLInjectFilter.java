package framework.filter;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.utils.HttpServletTool;
import common.utils.JSONTool;

public class SQLInjectFilter extends HttpServlet implements Filter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String inj_str = "'|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|; |or|-|+|,";

	protected FilterConfig filterConfig = null;

	/**
	 * Should a character encoding specified by the client be ignored?
	 */
	protected boolean ignore = true;

	public void init(FilterConfig config) throws ServletException {
		this.filterConfig = config;
		this.inj_str = filterConfig.getInitParameter("keywords");
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		Iterator values = req.getParameterMap().values().iterator();// 获取所有的表单参数
		while (values.hasNext()) {
			String[] value = (String[]) values.next();
			for (int i = 0; i < value.length; i++) {
				String illegalStr = sql_inj(value[i]);
				if (sql_inj(value[i]) != null) {
					// TODO这里发现sql注入代码的业务逻辑代码
					
					HttpServletTool.out(res, JSONTool.createStandardErrJsonStr("019998", value[i] + "含有不合法字符串：" + illegalStr ,""));
					
					return;
				}
			}
		}
		chain.doFilter(request, response);
	}

	public String sql_inj(String str) {
		String[] inj_stra = inj_str.split("\\|");
		for (int i = 0; i < inj_stra.length; i++) {
			if (str.indexOf(" " + inj_stra[i] + " ") >= 0) {
				return inj_stra[i];
			}
		}
		return null;
	}

}
