package common.platform.provider.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import common.platform.invoker.ServiceConstant;
import common.platform.provider.bean.SocketConfig;

//import util.PeLog;
public class SocketSend {

	private static final Logger LOG = Logger.getLogger(SocketSend.class);
	
	private String ServerIP = SocketConfig.getSockIp();

	// 172.16.27.82
	private int ServerPort = SocketConfig.getSockPort();

	private int packageHeadLength = 72;

	private String ReceiveStr = null;

	int ReceiveLength = 0;

	byte[] SendBytes;

	byte[] ReceiveBytes;

	private Socket ConnSock = null;

	private DataOutputStream os = null;

	private DataInputStream is = null;

	// private Logger Log = Logger.getLogger(PeLog.class);
	private int flag = 1;// 时间控制标识

	private static int waitTime = SocketConfig.getSendTimeout(); 
	
	private static int connectTime = SocketConfig.getConnectTimeout();

	private static int receiveTimeOut = 15000;
	
	private static int recBuffer = 2048;

	private static int sendBuffer = 1024;

	public static final String CONNECT_ERR = "无法连接平台服务器"; // 连接出错信息

	public SocketSend() throws Exception {
		/* New a socket for connecting to SocketServer */

		ConnSock = new Socket();
		InetSocketAddress addr =  new   InetSocketAddress(ServerIP, ServerPort); 
		ConnSock.connect(addr, connectTime);
		
		LOG.info("Socket connect GW_IP:" + ServerIP + ", GW_PORT:"
				+ ServerPort);
		/* Get the reading and writing pointer from socket */
		is = new DataInputStream(ConnSock.getInputStream());
		os = new DataOutputStream(ConnSock.getOutputStream());
		ConnSock.setSoTimeout(waitTime);
		ConnSock.setReceiveBufferSize(recBuffer);
		ConnSock.setSendBufferSize(sendBuffer);
		// throw new IOException("自定义错误");

	}

	/**
	 * @param ip
	 * @param port
	 * @throws IOException
	 */
	public SocketSend(String ip, int port) throws Exception {
		/* New a socket for connecting to SocketServer */
		//try {
			this.ServerIP = ip;
			this.ServerPort = port;
			ConnSock = new Socket();
			InetSocketAddress addr =  new   InetSocketAddress(ServerIP, ServerPort); 
			ConnSock.connect(addr, connectTime);
			
			LOG.info("Socket connect GW_IP:" + ServerIP
					+ ", GW_PORT:" + ServerPort);
			/* Get the reading and writing pointer from socket */
			is = new DataInputStream(ConnSock.getInputStream());
			os = new DataOutputStream(ConnSock.getOutputStream());
			ConnSock.setSoTimeout(waitTime);
			ConnSock.setReceiveBufferSize(recBuffer);
			ConnSock.setSendBufferSize(sendBuffer);
//		} catch (IOException e) {
//			e.printStackTrace();
//			this.close();
//			throw new ServiceInvokeException(CONNECT_ERR);
//		}

	}

	public SocketSend(String whereToSend) throws Exception {
		/* New a socket for connecting to SocketServer */
		//try {
			String serverIp;
			int serverPort;

			if (ServiceConstant.GWM_CALL.equalsIgnoreCase(whereToSend)) {
				serverIp = SocketConfig.getGwmIp();
				serverPort = SocketConfig.getGwmPort();
			}else if (ServiceConstant.ISP_CALL.equalsIgnoreCase(whereToSend)) {
				serverIp = SocketConfig.getISPIP();
				serverPort = SocketConfig.getISPPORT();
			}else {
				serverIp = ServerIP;
				serverPort = ServerPort;
			}

			ConnSock = new Socket();
			InetSocketAddress addr =  new   InetSocketAddress(serverIp, serverPort); 
			ConnSock.connect(addr, connectTime);
			
			LOG.info("Socket connect GW_IP:" + serverIp
					+ ", GW_PORT:" + serverPort);
			/* Get the reading and writing pointer from socket */
			is = new DataInputStream(ConnSock.getInputStream());
			os = new DataOutputStream(ConnSock.getOutputStream());
			ConnSock.setSoTimeout(waitTime);
			ConnSock.setReceiveBufferSize(recBuffer);
			ConnSock.setSendBufferSize(sendBuffer);
//		} catch (IOException e) {
//			e.printStackTrace();
//			this.close();
//			throw new ServiceInvokeException(CONNECT_ERR);
//		}

	}

	/**
	 * @param application
	 * @throws Exception
	 */
	public SocketSend(ServletContext application) throws Exception {
		/* New a socket for connecting to SocketServer */
//		try {
			this.ServerIP = (String) application.getAttribute("PF_SOCKET_IP");
			this.ServerPort = (Integer) application
					.getAttribute("PF_SOCKET_PORT");
			//
			SocketConfig.setSockIp(this.ServerIP);
			SocketConfig.setSockPort(this.ServerPort);
			//
			ConnSock = new Socket();
			InetSocketAddress addr =  new   InetSocketAddress(ServerIP, ServerPort); 
			ConnSock.connect(addr, connectTime);
			
			LOG.info("Socket connect GW_IP:" + ServerIP
					+ ", GW_PORT:" + ServerPort);
			/* Get the reading and writing pointer from socket */
			is = new DataInputStream(ConnSock.getInputStream());
			os = new DataOutputStream(ConnSock.getOutputStream());
			ConnSock.setSoTimeout(waitTime);
			ConnSock.setReceiveBufferSize(recBuffer);
			ConnSock.setSendBufferSize(sendBuffer);
//		} catch (IOException e) {
//			e.printStackTrace();
//			this.close();
//			throw new ServiceInvokeException(CONNECT_ERR);
//		}

	}

	/**
	 * 发送数据
	 * 
	 * @param 数据包
	 * @return
	 */
	public boolean SendData(String data) {// String SendStr
		boolean state;
		try {
			LOG.info("my send string is:" + data);
			/* Send the package to gateway */
			byte[] SendBytes = new String(data).getBytes();
			os.write(SendBytes);
			// os.writeBytes(data);
			os.flush();
			state = true;
		} catch (IOException e) {
			e.printStackTrace();
			state = false;
			this.close();
		}
		return state;
	}

	public String ReceiveData() {
		/* Waiting for response from gateway */
		int off = 0;
		int length = 0, packageLength = 0;
		int t;
//		LOG.info("action");
		try {
			byte[] ReceiveBytes1 = new byte[packageHeadLength];// 放置包头信息
			byte[] ReceiveBytes2 = null;
			// 读出包头数据
			// 循环读出长度为packageHeadLength的数据 暂定为72
			// 添加时间控制start、end两个变量，
			// 防止is读不出数据的时候进入死循环，zgh 2011-4-25
			long start = System.currentTimeMillis();
			long end = -1;
			while (flag > 0) {// 超时控制
				//大约15秒视为超时无法读出数据
				if((end-start)> receiveTimeOut ) {
					break;
				}
				if ((t = is.read(ReceiveBytes1, off, packageHeadLength - off)) > 0) {
					length = length + t;
					off = off + t;
					if (length >= packageHeadLength) {
						break;
					}
				}
				end = System.currentTimeMillis();
			}
			if (length < packageHeadLength) {// 是否完全读到包头数据
				ReceiveLength = 0;
			} else {
				try {
					// 验证包头
					if ((new String(ReceiveBytes1, "GBK")).substring(0, 4)
							.equals("FFFF")) {
						// 正确 读取包体长度
						packageLength = Integer.valueOf((new String(
								ReceiveBytes1, "GBK")).substring(14, 22));
						if (packageLength >= packageHeadLength) {
							ReceiveBytes2 = new byte[packageLength - length];
							int bodyLength = packageLength - length;
							off = 0;
							length = 0;
							if(bodyLength > 0){
								// 循环读出包体数据
								while (flag > 0) {
									if ((t = is.read(ReceiveBytes2, off, bodyLength
											- off)) > 0) {
										length = length + t;
										off = off + t;
										if (length >= bodyLength) {
											break;
										}
									}
									
								}
							}
							if (length < bodyLength || flag == -1) {// 没有读完，或者时间控制超时
								// 均算无效包
								return null;
							}
						}
						if (packageLength > 0) {
							ReceiveBytes = new byte[packageLength];
							ReceiveBytes = addByte(ReceiveBytes1,
									ReceiveBytes2, packageHeadLength,
									packageLength - packageHeadLength);
							ReceiveLength = packageLength;
						} else {
							ReceiveBytes = null;
							ReceiveLength = packageLength;
						}
					} else {// 包头数据错误
						ReceiveLength = 0;// 接收错误，算收到数据为0
					}

				} catch (Exception e) {
					this.close();
					return null;
				}
				// 包头包体数据整合
			}
			close();
		} catch (IOException e) {
			this.close();
			ReceiveStr = "";
		}

		if (ReceiveLength <= 0) {
			ReceiveStr = "";
		} else {
			try {
				ReceiveStr = new String(ReceiveBytes, "GBK");
			} catch (UnsupportedEncodingException e) {
				this.close();
				ReceiveStr = "";
			}
		}
		try {
			byte[] a = ReceiveStr.getBytes("UTF-8");
			ReceiveStr = new String(a, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ReceiveStr;
	}

	/**
	 * 连接2个byte[] 返回新的byte[]
	 * 
	 * @param ReceiveBytes_temp1
	 * @param ReceiveBytes_temp2
	 * @param length1
	 * @param length2
	 * @return
	 */
	public byte[] addByte(byte[] ReceiveBytes_temp1, byte[] ReceiveBytes_temp2,
			int length1, int length2) {

		byte[] ReceiveBytes = new byte[length1 + length2];
		for (int i = 0; i < length1; i++) {
			ReceiveBytes[i] = ReceiveBytes_temp1[i];
		}
		int j = length1;
		for (int i = 0; i < length2; i++) {
			ReceiveBytes[i + j] = ReceiveBytes_temp2[i];
		}
		return ReceiveBytes;
	}

	/**
	 * close is os ConnSock
	 * 
	 */
	public void close() {
		try {
			this.is.close();
			this.os.close();
			this.ConnSock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setReceiveTimeOut(int time) {
		this.receiveTimeOut = time;
	}
}
