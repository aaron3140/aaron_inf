/**
 * File                 : ServiceExceptionProcessor.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : payeasy
 * JDK version used     : JDK 1.5
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2009-5-19 下午03:36:24 [created]
 * Author               : Yunzhi Ling 凌云志
 * Email                : lingyunzhi@tisson.cn
 **/

package common.platform.invoker.handler;

import org.apache.log4j.Logger;

import common.platform.invoker.exception.ServiceInvokeException;
import common.platform.provider.client.SocketSend;
import common.platform.provider.server.DataProcess;
import common.platform.provider.server.PackageDataSet;
import common.utils.Charset;
/**
 * 处理调用接口异常
 * @author sonsy
 *
 */
public class ServiceExceptionProcessor implements IServiceResultProcessor {
	
	private static final Logger LOG = Logger.getLogger(ServiceExceptionProcessor.class);
	private IErrorMessageConverter errMsgConverter;

	/**
	 * 处理调用接口异常
	 * @version: 1.00
	 * @history: 2009-5-19 下午03:38:38 [created]
	 * @author Yunzhi Ling 凌云志
	 * @param moduleCode
	 * @param sender
	 * @param processor
	 * @param dataSet
	 * @return 
	 * @throws ServiceInvokeException
	 * @see
	 */
	public PackageDataSet process(String moduleCode, SocketSend sender, DataProcess processor) throws ServiceInvokeException {
		String dataReceived = sender.ReceiveData();
		LOG.info("action -> data received:" + dataReceived); // debug
		if (dataReceived.length() < 1) {
			throw new ServiceInvokeException("调用服务超时");
		}
		//
		PackageDataSet dataSet = null;
		
		try {
			dataSet = processor.process(dataReceived.getBytes());
			String invkResult = (String)dataSet.getParamByID("0001", "000").get(0);
			LOG.info("-> invoking result: " + invkResult); // debug
			
			//重庆水电煤调用错误会返回invkResult=B2等其他字母+数字的组合		
			//修改判断 hwf 2011年07月11日
			if (!"000000".equals(Charset.lpad(invkResult, 6, "0"))) {
//				if (!"000000".equals(Charset.lpad(invkResult, 6, "0")) && !"000040".equals(Charset.lpad(invkResult, 6, "0")) ) {//结果码为0040时不抛异常  qiuyajian 2013-09-10 9:49
				String invkMsg = ServiceInvokeException.GLOBAL_USER_ERR;
				if (dataSet != null) {
					invkMsg = dataSet.getByID("0002", "000") == null ? invkMsg : dataSet.getByID("0002", "000");
				}
				
				// 翻译返回码
				if (errMsgConverter != null) {
					String msgToUser = errMsgConverter.getErrMsg(moduleCode, invkResult);
					//ServiceInvokeException sie = new ServiceInvokeException(msgToUser == null ? ServiceInvokeException.GLOBAL_USER_ERR : msgToUser);
					invkMsg = msgToUser == null ? invkMsg : msgToUser;
				}
				
				ServiceInvokeException sie = new ServiceInvokeException(invkMsg);
				sie.setDataSet(dataSet);
				throw sie;
				
			}
		} catch (ServiceInvokeException sie) {
			// TODO: handle exception
			throw sie;
		} catch (Exception e) {
			// TODO: handle exception
			throw new ServiceInvokeException(e.getMessage());
		}
		return dataSet;
	}

	/**
	 * @param errMsgConverter the errMsgConverter to set
	 */
	public void setErrMsgConverter(IErrorMessageConverter errMsgConverter) {
		this.errMsgConverter = errMsgConverter;
	}

}
