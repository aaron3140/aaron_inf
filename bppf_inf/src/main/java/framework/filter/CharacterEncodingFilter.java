/**
 * File                 : CharacterEncodingFilter.java
 * Copy Right           : ��Ѷ���ͨ�ż������޹�˾
 * Project              : ͨ��ͳһƽ̨
 * JDK version used     : JDK 1.5
 * Comments             : �ַ���������
 * Version              : 1.01
 * Modification history : 2009-01-13
 * Author               : 
 **/package framework.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Filter that sets the character encoding to be used in parsing the incoming
 * request, either unconditionally or only if the client did not specify a
 * character encoding. Configuration of this filter is based on the following
 * initialization parameters:
 * 
 * encoding - The character encoding to be configured for this request, either
 * conditionally or unconditionally based on the ignore initialization
 * parameter. This parameter is required, so there is no default.
 * 
 * ignore - If set to "true", any character encoding specified by the client is
 * ignored, and the value returned by the selectEncoding() method is set. If set
 * to "false, selectEncoding() is called only if the client has not already
 * specified an encoding. By default, this parameter is set to "true".
 * 
 * Although this filter can be used unchanged, it is also easy to subclass it
 * and make the selectEncoding() method more intelligent about what encoding to
 * choose, based on characteristics of the incoming request (such as the values
 * of the Accept-Language and User-Agent headers, or a value stashed in the
 * current user's session.
 */

public class CharacterEncodingFilter implements Filter {

	// ---------------------- Instance Variables

	/**
	 * The default character encoding to set for requests that pass through this
	 * filter.
	 */
	protected String encoding = null;

	/**
	 * The filter configuration object we are associated with. If this value is
	 * null, this filter instance is not currently configured.
	 */
	protected FilterConfig filterConfig = null;

	/**
	 * Should a character encoding specified by the client be ignored?
	 */
	protected boolean ignore = true;

	// ---------------------- Public Methods

	/**
	 * Place this filter into service.
	 * 
	 * @param filterConfig
	 *            The filter configuration object
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		this.encoding = filterConfig.getInitParameter("encoding");
		String value = filterConfig.getInitParameter("ignore");
		if (value == null)
			this.ignore = true;
		else if (value.equalsIgnoreCase("true"))
			this.ignore = true;
		else if (value.equalsIgnoreCase("yes"))
			this.ignore = true;
		else
			this.ignore = false;
	}

	/**
	 * Select and set (if specified) the character encoding to be used to
	 * interpret request parameters for this request.
	 * 
	 * @param request
	 *            The servlet request we are processing
	 * @param result
	 *            The servlet response we are creating
	 * @param chain
	 *            The filter chain we are processing
	 * 
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet error occurs
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// Conditionally select and set the character encoding to be used
		if (ignore || (request.getCharacterEncoding() == null)) {
			String encoding = selectEncoding(request);
			if (encoding != null)
				request.setCharacterEncoding(encoding);
		}
		// Pass control on to the next filter
		chain.doFilter(request, response);
	}

	/**
	 * Take this filter out of service.
	 */
	public void destroy() {
		this.encoding = null;
		this.filterConfig = null;
	}

	// ------------ Protected Methods

	/**
	 * Select an appropriate character encoding to be used, based on the
	 * characteristics of the current request and/or filter initialization
	 * parameters. If no character encoding should be set, return null.
	 * 
	 * The default implementation unconditionally returns the value configured
	 * by the encoding initialization parameter for this filter.
	 * 
	 * @param request
	 *            The servlet request we are processing
	 */
	protected String selectEncoding(ServletRequest request) {
		return (this.encoding);
	}
}
