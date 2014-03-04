package com.tisson.pay.config;

/**
 * @author 邱亚建 2013年12月18日 上午8:47:36<br>
 * 
 *         本来描述：帮付通报文交互响应码
 */
public enum RetCode {

	T_0000("报文交互成功"), //
	T_0014("报文交互失败,订单号码不存在"), //
	T_0025("报文交互失败,订单状态已更新"), //
	T_0095("报文交互失败,请求格式不正确"), //
	T_0096("报文交互失败,其它错误"), //
	T_XXXX("未知交互结果");

	String desc;

	RetCode(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

}
