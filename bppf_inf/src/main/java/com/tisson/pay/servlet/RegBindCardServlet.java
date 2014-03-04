package com.tisson.pay.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mpi.client.data.TransData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tisson.pay.config.BftProperties;
import com.tisson.pay.service.BindCardService;
import com.tisson.pay.service.impl.BftPayRequest;
import com.tisson.pay.service.impl.BftPayResponseimpl;
import com.tisson.pay.service.impl.BindCardServiceImpl;
import common.dao.TRegBindCardDao;

/**
 * 
 * 本类描述: 接收帮付通回调 [在线注册绑卡]
 * 
 * @version: 企业帐户前置接口 v1.0
 * @author: 广州天讯瑞达通讯技术有限公司(zhuxiaojun)
 * @email: zhuxiaojun@tisson.com
 * @time: 2013-9-10上午09:50:36
 */
public class RegBindCardServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7721218710093675348L;
	private static final Log log = LogFactory.getLog(RegBindCardServlet.class);

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String UserID = "";
		String MerID = "";
		String PayID = "";
		String OrderAmount = "";
		String TransDate = "";
		String TransTime = "";
		String SystemSSN = "";
		String ylSystemSSN = "";
		String sysCode = "";
		String BankCode = "";
		String BankCard = "";
		String TransType = "";
		String retStr = "";

		// 接收到的数据
		log.info("bft callback start BFT_CALLBACK 接收帮付通回调...");
		request.setCharacterEncoding("UTF-8");
		String encryptMsg = request.getParameter("EncryptMsg");
		log.info("BFT_CALLBACK 传回来的参数: " + encryptMsg);
		if (encryptMsg == null || encryptMsg.equals("")) {
			retStr = "参数传入错误";
			replyRequest(response, retStr);
			return;
		}

		TransData transdata = new TransData();
		BftPayRequest payInforRequest = new BftPayRequest();// 声明"支付通知报文"类对象
		transdata = payInforRequest.reveivePayInforRequest(encryptMsg);// 调用"支付通知报文"方法,取各数据元

		UserID = transdata.getUserID();// 取用户手机号
		MerID = transdata.getMerID();// 取商户编号
		PayID = transdata.getPayID();// 取商户订单号
		OrderAmount = transdata.getOrderAmount();// 取交易金额
		TransDate = transdata.getTransDate();// 取交易日期
		TransTime = transdata.getTransTime();// 取交易时间
		SystemSSN = transdata.getSystemSSN();// 取系统参考号
		ylSystemSSN = transdata.getYlSystemSSN();// 取银联系统参考号
		sysCode = transdata.getSysCode();// 取支付响应码
		BankCode = transdata.getBankCode();// 取银行代码
		BankCard = transdata.getBankCard();// 到银行卡号
		TransType = transdata.getTransType();// 取交易类型
		log.info("BFT_CALLBACK UserID=[" + UserID + "]");
		log.info("BFT_CALLBACK MerID=[" + MerID + "]");
		log.info("BFT_CALLBACK PayID=[" + PayID + "]");
		log.info("BFT_CALLBACK OrderAmount=[" + OrderAmount + "]");
		log.info("BFT_CALLBACK TransDate=[" + TransDate + "]");
		log.info("BFT_CALLBACK TransTime=[" + TransTime + "]");
		log.info("BFT_CALLBACK SystemSSN=[" + SystemSSN + "]");
		log.info("BFT_CALLBACK ylSystemSSN=[" + ylSystemSSN + "]");
		log.info("BFT_CALLBACK SysCode=[" + sysCode + "]");
		log.info("BFT_CALLBACK BankCode=[" + BankCode + "]");
		log.info("BFT_CALLBACK BankCard=[" + BankCard + "]");
		log.info("BFT_CALLBACK TransType=[" + TransType + "]");

		// 开发模拟测试数据开始==== 发布时注释掉
//		transdata.setUserID("18924288887");
//		transdata.setPayID("131203007289873");// 交易流水号

		// 开发模拟测试数据结束====
		String retCode = "T_0000";
		try {
			BindCardService service = new BindCardServiceImpl();
			retCode = service.doBindCard(transdata, "0");
		} catch (Exception e) {
			log.error("bft callback error 商户业务逻辑处理出错...原因: " + e.getMessage());
			log.info("更新绑卡记录 bind_state：S0F 失败， stat：S0X 无效");
			TRegBindCardDao dao = new TRegBindCardDao();
			dao.updateBindStateToFail(transdata.getPayID(), transdata.getSysCode() + "::" + transdata.getDescription() + "::" + e.getMessage());
			retCode = "T_0096";// 报文交互失败,其它错误
			replyToBft(response, transdata, retCode);
		}
		/**
		 * 商户业务逻辑处理【结束】
		 */
		// 回复帮付通
		replyToBft(response, transdata, retCode);
	}

	/**
	 * 回复帮付通
	 * 
	 * @param response
	 * @param transdata
	 * @param isBusSuccess
	 * @throws IOException
	 */
	private void replyToBft(HttpServletResponse response, TransData transdata, String retCode) throws IOException {
		// 向支付平台[帮付通]发送响应消息
		log.info("BFT_REPLY 回复帮付通...");
		BftPayResponseimpl payInforResponse = new BftPayResponseimpl();// 声明"支付通知应答报文"类对象
		transdata.setUserID(transdata.getUserID());// 用户手机号
		transdata.setMerID(BftProperties.getMerId());// 填帮付通分配商户编号
		transdata.setPayID(transdata.getPayID());// 订单号
		transdata.setRetCode(retCode);// 更新成功T_0000,无此订单T_0014,已更新过T_0025,更新失败或其他错误T_0096,请参见接口规范“报文交互响应码”
		transdata.setTransType("2002");// 交易类型
		log.info("BFT_REPLY 回复帮付通信息：retCode=" + retCode);

		String retStr = payInforResponse.sendPayInforResponse(transdata);// 调用工具包"支付通知应答"类,生成支付通知应答串
		replyRequest(response, retStr);
	}

	private static void replyRequest(HttpServletResponse response, String retStr) throws IOException {
		response.setCharacterEncoding("GBK");
		response.getWriter().print(retStr);
		response.getWriter().flush();
		response.getWriter().close();
		log.info("BFT_REPLY 回复内容: " + retStr);
		return;
	}

}
