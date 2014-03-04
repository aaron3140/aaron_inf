/**
 * File                 : SPPaymentService.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : MEPF_INF
 * JDK version used     : JDK 1.5
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2010-12-28下午5:48:49 [created]
 * Author               : Leyi Tang 唐乐毅
 * Email                : 459518965@qq.com
 **/
package websvc;

import javax.jws.WebService;
import javax.jws.WebParam;

/**
 * 代理商接口功能的接口
 * 
 * @version: 1.00
 * @history: 2010-12-28 下午5:49:42 [created]
 * @author Leyi Tang 唐乐毅
 */
@WebService(targetNamespace = "http://websvc/", name = "DealProcessorService")
public interface DealProcessorService {
	/**
	 * 统一接口
	 * 
	 * @version 1.00
	 * @history: 2011-01-18 上午11:40:55 [created]
	 * @param in0
	 * @param in1
	 * @return String
	 * @see
	 */
	public String dispatchCommand(
			@WebParam(name = "arg0", targetNamespace = "http://websvc/") String in0,
			@WebParam(name = "arg1", targetNamespace = "http://websvc/") String in1);

	/**
	 * 统二接口
	 * 
	 * @version 1.00
	 * @history: 2011-01-18 上午11:40:55 [created]
	 * @param in0
	 * @param in1
	 * @return String
	 * @see
	 */
	public String dispatchCommandEXT(
			@WebParam(name = "arg0", targetNamespace = "http://websvc/") String in0,
			@WebParam(name = "arg1", targetNamespace = "http://websvc/") String in1);

	/**
	 * 统三接口
	 * 
	 * @version 1.00
	 * @history: 2011-01-18 上午11:40:55 [created]
	 * @param in0
	 * @param in1
	 * @return String
	 * @see
	 */
	public String dispatchCommandIPOS(
			@WebParam(name = "arg0", targetNamespace = "http://websvc/") String in0,
			@WebParam(name = "arg1", targetNamespace = "http://websvc/") String in1);

	public String dispatchCommandJsonLibIPOS(
			@WebParam(name = "arg0", targetNamespace = "http://websvc/") String in0,
			@WebParam(name = "arg1", targetNamespace = "http://websvc/") String in1);
}
