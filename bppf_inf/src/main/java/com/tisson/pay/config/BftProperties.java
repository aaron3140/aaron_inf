package com.tisson.pay.config;

/**
 * 
 * 本类描述: 帮付通配置信息
 */
public class BftProperties {
	private static String KeyStore = "";
	private static String HostURL = "";
	private static String MerId = "";
	private static String MerName = "";
	private static String MerHomeUrl = "";
	private static String MerBackUrl = "";
	/**
	 * 订单金额，单位：元
	 */
	private static String OrderAmount = "";

	public static String getOrderAmount() {
		return OrderAmount;
	}

	public static void setOrderAmount(String orderAmount) {
		OrderAmount = orderAmount;
	}

	public static String getKeyStore() {
		return KeyStore;
	}

	public static String getHostURL() {
		return HostURL;
	}

	public static void setKeyStore(String keyStore) {
		KeyStore = keyStore;
	}

	public static void setHostURL(String hostURL) {
		HostURL = hostURL;
	}

	public static String getMerId() {
		return MerId;
	}

	public static void setMerId(String merId) {
		MerId = merId;
	}

	public static String getMerName() {
		return MerName;
	}

	public static void setMerName(String merName) {
		MerName = merName;
	}

	public static String getMerHomeUrl() {
		return MerHomeUrl;
	}

	public static void setMerHomeUrl(String merHomeUrl) {
		MerHomeUrl = merHomeUrl;
	}

	public static String getMerBackUrl() {
		return MerBackUrl;
	}

	public static void setMerBackUrl(String merBackUrl) {
		MerBackUrl = merBackUrl;
	}

}
