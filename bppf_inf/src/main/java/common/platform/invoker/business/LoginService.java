package common.platform.invoker.business;

/*
 * 登录相关接口
 */
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.invoker.exception.ServiceInvokeException;
import common.platform.provider.server.PackageDataSet;

import framework.config.ConfigReader;
import framework.exception.INFException;

//@Service
public class LoginService {

	public static String svcInfName = "LoginService";

	private static final Log logger = LogFactory.getLog(LoginService.class);

	public static void main(String[] args) {
		try {
			new LoginService().cumRandNum();
		} catch (INFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param request
	 * @return
	 * @throws INFException
	 */
	public String cumRandNum() throws INFException {

		new ConfigReader().initStartUp();
		
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "CUM_RAND");
		g002.endRow();

		IServiceCall caller = new ServiceCallImpl();

		PackageDataSet dataSet = new PackageDataSet();
		try {
			dataSet = caller.call("BIS", "CUM0014", g002);
		} catch (IOException e) {
		} catch (ServiceInvokeException e) {
			// TODO Auto-generated catch block
			String responseCode = dataSet.getByID("0001", "000");
			String responseDesc = dataSet.getByID("0002", "000");
			throw new INFException(responseCode, responseDesc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String cum = dataSet.getByID("2174", "217");
		System.out.println(cum);
		return cum;
	}

}
