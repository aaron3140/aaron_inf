package common.socket.bean;

/**
 * @title SocketClientConfig.java
 * @description socket客户端配置信息
 * @date 2014-02-13 16:19
 * @author lichunan
 * @version 1.0
 */
public class SocketClientConfig {
	/**
	 * socket服务端ip地址
	 */
	public static String socketServerIp = "";
	/**
	 * socket服务端端口
	 */
	public static int socketServerPort = -1;
	/**
	 * 连接超时时间,单位为ms
	 */
	public static int connectTimeOut = 15000;
	/**
	 * 读数据超时时间,单位为ms
	 */
	public static int soTimeout = 70000;
	/**
	 * 延迟关闭的时间,单位为s
	 */
	public static int soLinger = 2;
	/**
	 * 输入缓冲区大小
	 */
	public static int recBufferSize = 2048;
	/**
	 * 输出缓冲区大小
	 */
	public static int sendBufferSize = 1024;
	
	/**
	 * socket服务端ip地址
	 */
	public static void setSocketServerIp(String socketServerIp) {
		SocketClientConfig.socketServerIp = socketServerIp;
	}
	
	/**
	 * socket服务端端口
	 */
	public static void setSocketServerPort(int socketServerPort) {
		SocketClientConfig.socketServerPort = socketServerPort;
	}
	
	/**
	 * 连接超时时间,单位为ms
	 */
	public static void setConnectTimeOut(int connectTimeOut) {
		SocketClientConfig.connectTimeOut = connectTimeOut;
	}
	
	/**
	 * 读数据超时时间,单位为ms
	 */
	public static void setSoTimeout(int soTimeout) {
		SocketClientConfig.soTimeout = soTimeout;
	}
	
	/**
	 * 延迟关闭的时间,单位为s
	 */
	public static void setSoLinger(int soLinger) {
		SocketClientConfig.soLinger = soLinger;
	}
	
	/**
	 * 输入缓冲区大小
	 */
	public static void setRecBufferSize(int recBufferSize) {
		SocketClientConfig.recBufferSize = recBufferSize;
	}
	
	/**
	 * 输出缓冲区大小
	 */
	public static void setSendBufferSize(int sendBufferSize) {
		SocketClientConfig.sendBufferSize = sendBufferSize;
	}
	
	
}
