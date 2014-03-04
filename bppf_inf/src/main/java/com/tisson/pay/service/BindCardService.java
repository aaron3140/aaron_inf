package com.tisson.pay.service;

import mpi.client.data.TransData;

/**
 * 邱亚建 2013-12-3 上午09:15:53<br/>
 * 
 * 本类描述:绑卡服务类
 */
public interface BindCardService {

	/**
	 * 未知的交互结果
	 */
	public static final String UNKNOW_RETCODE = "UNKNOW";
	
	/**
	 * @param transdata
	 * @param bindType 绑卡类型，0回调时，1查询时
	 */
	String doBindCard(TransData transdata,String bindType) throws Exception;
	
}
