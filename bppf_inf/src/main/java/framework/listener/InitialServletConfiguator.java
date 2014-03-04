package framework.listener;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import framework.config.*;

public class InitialServletConfiguator extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4147737301721715622L;

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		ConfigReader cr = new ConfigReader(config.getServletContext());
		cr.initStartUp();
	}
}
