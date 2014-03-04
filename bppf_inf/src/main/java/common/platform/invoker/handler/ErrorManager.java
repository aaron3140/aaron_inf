package common.platform.invoker.handler;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import common.dao.BaseDao;
import common.platform.invoker.ServiceConstant;
import common.platform.invoker.handler.IErrorMessageConverter;

public class ErrorManager implements IErrorMessageConverter {

	private BaseDao baseDao;

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	public BaseDao getBaseDao() {
		return baseDao;
	}

	public String getErrMsg(String moduleCode, String errCode) {
		String handler = null;
		try {
			handler = (String) baseDao.queryForObject(
					"select HANDLER from t_sym_error where STAT = 'S0A' and  MODULE_CODE = '"
							+ moduleCode + "' AND ERROR_CODE = '" + errCode
							+ "'", String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return handler;
	}

	public static void main(String[] args) {
		ApplicationContext CTX = new ClassPathXmlApplicationContext(
				"testSpring.xml");
		IErrorMessageConverter dao = (IErrorMessageConverter) CTX
				.getBean(ServiceConstant.ERROR_MANAGER);
		try {
			dao.getErrMsg("GWM", "40");
		} catch (Exception e) {
			System.out.println("1111");
			e.printStackTrace();
		}

	}
}
