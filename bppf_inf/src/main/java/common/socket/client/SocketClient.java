package common.socket.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.socket.bean.SocketClientConfig;

/**
 * @title SocketClient.java
 * @description socket客户端
 * @date 2014-02-13 16:27
 * @author lichunan
 * @version 1.0
 */
public class SocketClient {

	private static final Logger log = LoggerFactory
			.getLogger(SocketClient.class);

	private Socket connSocket = null;
	private String socketServerIp = SocketClientConfig.socketServerIp;
	private int socketServerPort = SocketClientConfig.socketServerPort;
	private int soLinger = SocketClientConfig.soLinger;
	private int connectTimeOut = SocketClientConfig.connectTimeOut;
	private int recBufferSize = SocketClientConfig.recBufferSize;
	private int sendBufferSize = SocketClientConfig.sendBufferSize;
	private DataOutputStream os = null;// 输出流
	private DataInputStream is = null;// 输入流

	private int soTimeout = SocketClientConfig.soTimeout;

	public SocketClient() throws IOException {
		connSocket = new Socket();
		connSocket.setSoLinger(true, soLinger);
		connSocket.setSoTimeout(soTimeout);
		connSocket.setReceiveBufferSize(recBufferSize);// 对于Socket和ServerSocket如果需要指定缓冲区大小，必须在连接之前完成缓冲区的设定
		connSocket.setSendBufferSize(sendBufferSize);
		connSocket.setTcpNoDelay(true);// 关闭Nagle算法,立即发包
		InetSocketAddress addr = new InetSocketAddress(socketServerIp,
				socketServerPort);
		connSocket.connect(addr, connectTimeOut);// 连接服务器
		is = new DataInputStream(connSocket.getInputStream());// 获取输入流
		os = new DataOutputStream(connSocket.getOutputStream());// 获取输出流
	}

	public SocketClient(String socketServerIp, int socketServerPort)
			throws IOException {
		this.socketServerIp = socketServerIp;
		this.socketServerPort = socketServerPort;
		connSocket = new Socket();
		connSocket.setSoLinger(true, soLinger);
		connSocket.setSoTimeout(soTimeout);
		connSocket.setReceiveBufferSize(recBufferSize);// 对于Socket和ServerSocket如果需要指定缓冲区大小，必须在连接之前完成缓冲区的设定
		connSocket.setSendBufferSize(sendBufferSize);
		connSocket.setTcpNoDelay(true);// 关闭Nagle算法,立即发包
		InetSocketAddress addr = new InetSocketAddress(socketServerIp,
				socketServerPort);
		connSocket.connect(addr, connectTimeOut);// 连接服务器
		is = new DataInputStream(connSocket.getInputStream());// 获取输入流
		os = new DataOutputStream(connSocket.getOutputStream());// 获取输出流
	}

	/**
	 * 发送数据到服务端
	 * 
	 * @param sendData
	 * @return flag: true表示发送成功  false表示发送失败
	 */
	public boolean sendData(String sendData) {
		boolean flag = false;
		try {
			log.info("->发送的数据为:" + sendData);
			byte[] sendBytes = new String(sendData.getBytes(), "GBK").getBytes();
			os.write(sendBytes);
			os.flush();
			flag = true;
			log.info("->发送数据成功!");
		} catch (IOException e) {
			log.info("->发送数据失败,异常信息为:" + e.getMessage());
			flag = false;
			close();
		} 
		return flag;
	}
	
	/**
	 * 从服务端接收数据
	 * @return
	 */
	public String receiveData(){
		StringBuffer strBuffer = new StringBuffer("");
		try {
			int bufferSize = 8192;
			byte[] buf = new byte[bufferSize];
			int length = 0;
			while((length = is.read(buf)) != -1){
				String receiveStr = new String(buf, "GBK");
				strBuffer.append(receiveStr);
			}
			log.info("->接收的数据为：" + strBuffer.toString());
		} catch (IOException e) {
			log.info("->读取数据失败，异常信息为:" + e.getMessage());
			return null;
		} finally{
			close();
		}
		return strBuffer.toString();
	}
	
	/**
	 * 关闭资源
	 */
	public void close() {
		
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (connSocket != null) {
			try {
				connSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
}
