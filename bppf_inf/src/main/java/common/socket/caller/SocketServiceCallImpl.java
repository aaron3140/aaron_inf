package common.socket.caller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import common.socket.client.SocketClient;

/**
 * @title SocketServiceCallImpl.java
 * @description 调用socket实现业务接口实现类
 * @date 2014-02-17 19:55
 * @author lichunan
 * @version 1.0
 *
 */
public class SocketServiceCallImpl implements ISocketServiceCall {
	
	private static final Log logger = LogFactory.getLog(SocketServiceCallImpl.class);
	
	@Override
	public JSONObject call(JSONObject requestJSONObject) throws Exception {
		SocketClient socketClient = new SocketClient();
		boolean sendOk = socketClient
				.sendData(requestJSONObject.toString());
		JSONObject responseJSONObject = null;
		if(sendOk){
			logger.info("->socket客户端发送成功!");
			String response = socketClient.receiveData();
			responseJSONObject = new JSONObject(response);
		}
		return responseJSONObject;
	}
	
}
