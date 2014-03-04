package common.socket.caller;


import org.json.JSONObject;

/**
 * @title IServiceCall.java
 * @description 调用socket实现业务接口类
 * @date 2014-02-17 19:47
 * @author lichunan
 * @version 1.0
 */
public interface ISocketServiceCall {
	public JSONObject call(JSONObject requestJSONObject)throws Exception;
}
