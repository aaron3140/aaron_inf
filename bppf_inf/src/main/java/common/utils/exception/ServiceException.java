package common.utils.exception;

/**
 * 异常捕获类
 * 本类描述: 
 * @author: 王树爽
 * @Time 2012-12-18 下午2:17:52
 */

public class ServiceException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceException() {
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}
}
