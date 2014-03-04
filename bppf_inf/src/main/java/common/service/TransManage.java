package common.service;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import common.algorithm.MD5;
import common.dao.BaseDao;
import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.entity.SignOrder;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.utils.Charset;
import common.utils.SeqTool;
import common.utils.SpringContextHelper;
import common.xml.CommonReqAbs;
import common.xml.dp.DpInf02032Request;
import common.xml.dp.DpInf5002TradeRequest;
import common.xml.dp.TradeRequest;

/**
 * 交易管理 File : TransManage.java Copy Right : 天讯瑞达通信技术有限公司 www.tisson.cn Project : bppf_inf JDK version used : JDK 1.6 Comments : Version : 1.00 Modification history : 2012-4-5
 * 下午03:47:09 [created] Author : Zhilong Luo 罗志龙 Email : luozhilong@tisson.cn
 **/
public class TransManage {

	private BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	private Logger log = Logger.getLogger(TransManage.class);

	/**
	 * 检测用户名和客户编码是否对应
	 * @param custCode
	 * @param staffId
	 * @return
	 * @throws Exception
	 */
	public  boolean isCustCodeMatchStaffId(String custCode,String staffId)throws Exception{
		String sql = "select count(1) from t_cum_info info where info.stat='S0A' and info.cust_type='C02' and info.cust_code='"+custCode+"' and info.prtn_id=" +
				"(select priv.prtn_id from t_cum_priv priv where priv.org_id=" + 
				"(select sf.org_id from t_sym_staff sf where sf.staff_id='"
					+staffId+"' and sf.stat='S0A') and priv.stat='S0A')";
		int n = DAO.queryForInt(sql);
		return n >0; 
	}
	
	/**
	 * 根据订单号获取预处理号
	 * 
	 * @version: 1.00
	 * @history: 2012-4-8 下午11:33:24 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param orderId
	 * @return
	 * @see
	 */
	public Long getPreIdByOrderId(String orderId) {
		String sql = "select t.pre_id from t_opp_preorder t where t.order_id = '" + orderId + "'";
		Long preId = (Long) DAO.queryForObject(sql, java.lang.Long.class);
		return preId;
	}

	/**
	 * 获取业务单/扣款顺序号
	 * 
	 * @version: 1.00
	 * @history: 2012-4-8 下午11:43:26 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param preId
	 * @return
	 * @see
	 */
	public Integer getConfirmCount(Long preId, String confType) {
		String sql = "select count(*) from t_opp_confirm t where t.pre_id='" + preId + "' and t.conf_type='" + confType + "'";
		Integer count = (Integer) DAO.queryForObject(sql, java.lang.Integer.class);
		return count;
	}

	/**
	 * 插入担保交易的操作记录
	 * 
	 * @version: 1.00
	 * @history: 2012-4-9 上午12:11:55 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param preId
	 * @param confAmount
	 * @param confType
	 * @param stat
	 * @return
	 * @see
	 */
	public long insertTOppConfirm(Long preId, Double confAmount, String confType, String stat) {
		long id = Long.valueOf(SeqTool.getSeq("SQ_OPP_CONFIRM")).longValue();
		String sql = "insert into T_OPP_CONFIRM (CFR_ID, PRE_ID, CONF_AMOUNT, CONF_TYPE, CONF_DATE, STAT)" + " values (" + id
				+ ", ?, ?, ?, sysdate, ?)";
		DAO.insert(sql, new Object[] { preId, confAmount, confType, stat });
		return id;
	}

	/**
	 * 生成预处理单批次号
	 * 
	 * @version: 1.00
	 * @history: 2012-4-10 上午10:43:35 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param detailSize
	 * @return
	 * @see
	 */
	private String getBatchCode(Integer detailSize) {
		DateFormat dateDF = new SimpleDateFormat("yyMMdd");
		DecimalFormat preIdDF = new DecimalFormat("000000000"); // 位数不足就在前面补0
		DecimalFormat sizeDF = new DecimalFormat("0000"); // 位数不足就在前面补0

		String time = dateDF.format(Calendar.getInstance().getTime());// 当前时间
		String size = sizeDF.format(detailSize);// 单据个数：4位
		String preId = preIdDF.format(Integer.parseInt(SeqTool.getSeq("SQ_OPP_PREORDER")));// 预处理号:9位
		String batchCode = time + size + preId;// 生成批次号
		// 返回批次号
		return batchCode;
	}

	/**
	 * 插入预处理表
	 * 
	 * @version: 1.00
	 * @history: 2012-4-10 下午06:34:13 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param tradeRequest
	 * @return
	 * @see
	 */
	public long insertOppOrder(TradeRequest tradeRequest) {

		String batchCode = getBatchCode(1);// 批次号
		DateFormat dateDF = new SimpleDateFormat("yyyyMMddHHmmss");
		String preCode = dateDF.format(Calendar.getInstance().getTime()) + 1;// 预处理号
		String termSeq = tradeRequest.getOrderSeq();// 外系统的订单号(终端流水号)
		String orderId = tradeRequest.getTransSeq();// 订单号(交易流水号)
		String custCode = tradeRequest.getAgentCode();// 付款方
		String objCode = tradeRequest.getPayeeCode();// 支付对象
		String actionCode = tradeRequest.getActionCode();// 业务编码
		String orderType = "OT001";// 订单类型
		String channelType = tradeRequest.getChannelCode();// 渠道类型
		String termId = tradeRequest.getTmnNum();// 受理终端号
		String areaCode = tradeRequest.getAreaCode();// 区域编码

		String txnAmount = tradeRequest.getTxnAmount();
		Double amount = Double.parseDouble(txnAmount);// 交易金额

		// 插入订单预处理表
		long preId = Long.valueOf(SeqTool.getSeq("SQ_OPP_PREORDER")).longValue();
		String sql = "insert into T_OPP_PREORDER (PRE_ID, BATCH_CODE, PRE_CODE, TERM_SEQ, ORDER_ID, "
				+ "CUST_CODE, OBJ_CODE, ACTION_CODE, ORDER_TYPE, CHANNEL_TYPE, TERM_ID, AREA_CODE, "
				+ "LAUNCH_OPER_ID, LAUNCH_ADMIN_ID, HANDLE_OPER_ID, HANDEL_ADMIN_ID, ACCT_DATE, "
				+ "BEGIN_DATE, EFF_DATE, MEMO, CUST_STAT, OBJ_STAT, STAT) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, null, null, null, null, sysdate, null, sysdate, null, 'S0C', null, 'S0A')";
		DAO.insert(sql, new Object[] { preId, batchCode, preCode, termSeq, orderId, custCode, objCode, actionCode, orderType, channelType, termId,
				areaCode });

		// 插入预订单费用表
		long ordFeeId = Long.valueOf(SeqTool.getSeq("SQ_OPP_ORDFEE")).longValue();
		sql = "insert into T_OPP_ORDFEE (ORDFEE_ID, PRE_ID, CURRENCY_CODE, PRIM_MONEY, DISC_MONEY, DUE_MOENY, PAY_MONEY, STAT) "
				+ "values (?, ?, 'CNY', null, null, ?, ?, 'S0A')";
		DAO.insert(sql, new Object[] { ordFeeId, String.valueOf(preId), amount, amount });

		return preId;
	}

	/**
	 * 插入预处理表
	 * 
	 * @version: 1.00
	 * @history: 2012-4-10 下午06:34:13 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param tradeRequest
	 * @return
	 * @see
	 */
	public long insertOppOrder(DpInf5002TradeRequest tradeRequest) {

		String batchCode = getBatchCode(1);// 批次号
		DateFormat dateDF = new SimpleDateFormat("yyyyMMddHHmmss");
		String preCode = dateDF.format(Calendar.getInstance().getTime()) + 1;// 预处理号
		String termSeq = tradeRequest.getOrderSeq();// 外系统的订单号(终端流水号)
		String orderId = tradeRequest.getTransSeq();// 订单号(交易流水号)
		String custCode = tradeRequest.getAgentCode();// 付款方
		String objCode = tradeRequest.getPayeeCode();// 支付对象
		String actionCode = tradeRequest.getActionCode();// 业务编码
		String orderType = "OT001";// 订单类型
		String channelType = tradeRequest.getChannelCode();// 渠道类型
		String termId = tradeRequest.getTmnNum();// 受理终端号
		String areaCode = tradeRequest.getAreaCode();// 区域编码

		String txnAmount = tradeRequest.getTxnAmount();
		Double amount = Double.parseDouble(txnAmount);// 交易金额

		// 插入订单预处理表
		long preId = Long.valueOf(SeqTool.getSeq("SQ_OPP_PREORDER")).longValue();
		String sql = "insert into T_OPP_PREORDER (PRE_ID, BATCH_CODE, PRE_CODE, TERM_SEQ, ORDER_ID, "
				+ "CUST_CODE, OBJ_CODE, ACTION_CODE, ORDER_TYPE, CHANNEL_TYPE, TERM_ID, AREA_CODE, "
				+ "LAUNCH_OPER_ID, LAUNCH_ADMIN_ID, HANDLE_OPER_ID, HANDEL_ADMIN_ID, ACCT_DATE, "
				+ "BEGIN_DATE, EFF_DATE, MEMO, CUST_STAT, OBJ_STAT, STAT) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, null, null, null, null, sysdate, null, sysdate, null, 'S0C', null, 'S0A')";
		DAO.insert(sql, new Object[] { preId, batchCode, preCode, termSeq, orderId, custCode, objCode, actionCode, orderType, channelType, termId,
				areaCode });

		// 插入预订单费用表
		long ordFeeId = Long.valueOf(SeqTool.getSeq("SQ_OPP_ORDFEE")).longValue();
		sql = "insert into T_OPP_ORDFEE (ORDFEE_ID, PRE_ID, CURRENCY_CODE, PRIM_MONEY, DISC_MONEY, DUE_MOENY, PAY_MONEY, STAT) "
				+ "values (?, ?, 'CNY', null, null, ?, ?, 'S0A')";
		DAO.insert(sql, new Object[] { ordFeeId, String.valueOf(preId), amount, amount });

		return preId;
	}

	/**
	 * 根据客户编码获得该客户天讯卡户号
	 * 
	 * @version: 1.00
	 * @history: 2012-2-23 下午05:23:51 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param agentCode
	 * @return
	 * @throws Exception
	 * @see
	 */
	public String getTissonCardAcct(String agentCode) throws Exception {
		/**
		 * 调用CUM0002,根据客户编码获得该客户天讯卡户号
		 */
		// 查询明细信息
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "2002");// 查询条件：客户编码
		g002.put("0022", agentCode);// 查询条件值

		// 组成数据包,调用CUM0002接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet packageDataSet = caller.call("BIS", "CUM0002", g002);

		// 获取返回值
		int count = packageDataSet.getParamSetNum("207");
		String cardAcctNbr = null;
		for (int i = 0; i < count; i++) {
			// 获取卡户类型
			String cardAcctType = (String) packageDataSet.getParamByID("2048", "207").get(i);
			// 获取天讯卡户号
			if (cardAcctType.equals("ACCT002")) {
				cardAcctNbr = (String) packageDataSet.getParamByID("2049", "207").get(i);
				break;
			}
		}
		return cardAcctNbr;
	}

	/**
	 * 一般交易处理流程
	 * 
	 * @version: 1.00
	 * @history: 2012-2-23 下午05:23:20 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param trade
	 * @param cardAcctNbr
	 * @param payeeCardAcctNbr
	 * @return
	 * @throws Exception
	 * @see
	 */
	public PackageDataSet transProcess(TradeRequest trade, String cardAcctNbr, String payeeCardAcctNbr,CommonReqAbs request) throws Exception {

		String agentCode = trade.getAgentCode(); // 商户编码
		String channelCode = trade.getChannelCode(); // 交易渠道
		String tmnNum = trade.getTmnNum(); // 终端号
		String areaCode = trade.getAreaCode(); // 区域编码
		String actionCode = trade.getActionCode(); // 操作编码
		String txnAmount = trade.getTxnAmount(); // 交易金额
		String payeeCode = trade.getPayeeCode(); // 收款商户编码
		String orderSeq = trade.getOrderSeq(); // 订单号
		String tradeTime = trade.getTradeTime(); // 交易时间
		String mark1 = trade.getMark1(); // 备注字段1
		String mark2 = trade.getMark2(); // 备注字段2

		TCumInfoDao infoDao = new TCumInfoDao();
		String objName = infoDao.getCustName(payeeCode); // 收款商户名称

		String dealType = "0301"; // 处理类型
		String ctrlFlag = "1"; // 控制标识：0:存入数据库;1:不存数据库
		if (actionCode.equals("01030005")) {
			ctrlFlag = "0";// 担保交易需要把信息插入数据库
			dealType = "0501";// 担保交易处理类型
		}

		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", agentCode);// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", tmnNum);// 受理终端号
		g401.put("4008", tradeTime);// 受理时间
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", channelCode);// 渠道类型编码
		/*
		 * 外部订单号改成4028 g401.put("4017", orderSeq);// 终端流水号 keep值放4017
		 */
		g401.put("4017", trade.getKeep());// 终端流水号
		g401.put("4028", orderSeq);// 订单号
		g401.put("4018", "0.0.0.0");// 操作原始来源
		g401.put("4280", objName); // 收款商户名称
		
		g401.put("4284", request.getMerId());//机构编码     //20130628 wanght
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", txnAmount);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", txnAmount);// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", payeeCode);// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "101");// 费用项标识
		g405.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
		g407.put("4088", dealType);// 属性值1
		g407.put("4080", ctrlFlag);// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
		g407.put("4088", payeeCardAcctNbr);// 属性值1
		g407.put("4080", ctrlFlag);// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTACCTTYPE");// 业务属性编码
		g407.put("4088", "0001");// 属性值1
		g407.put("4080", ctrlFlag);// 控制标识
		g407.endRow();

		if (!Charset.trim(mark1).equals("")) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_MARK1");// 业务属性编码
			g407.put("4088", mark1);// 属性值1
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}

		if (!Charset.trim(mark2).equals("")) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_MARK2");// 业务属性编码
			g407.put("4088", mark2);// 属性值1
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", "110000");// 支付机构编码
		g408.put("4099", "0001");// 账户类型编码
		g408.put("4101", cardAcctNbr);// 账号
		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", txnAmount);// 支付金额
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);

		// 返回结果
		return dataSet;
	}

	/**
	 * 一般交易处理流程
	 * 
	 * @version: 1.00
	 * @history: 2012-2-23 下午05:23:20 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param trade
	 * @param cardAcctNbr
	 * @param payeeCardAcctNbr
	 * @return
	 * @throws Exception
	 * @see
	 */
	public PackageDataSet transProcess(DpInf5002TradeRequest trade, String cardAcctNbr, String payeeCardAcctNbr,CommonReqAbs request) throws Exception {
		String agentCode = trade.getAgentCode(); // 商户编码
		String channelCode = trade.getChannelCode(); // 交易渠道
		String tmnNum = trade.getTmnNum(); // 终端号
		String areaCode = trade.getAreaCode(); // 区域编码
		String actionCode = trade.getActionCode(); // 操作编码
		String txnAmount = trade.getTxnAmount(); // 交易金额
		String payeeCode = trade.getPayeeCode(); // 收款商户编码
		String orderSeq = trade.getOrderSeq(); // 订单号
		String tradeTime = trade.getTradeTime(); // 交易时间
		String mark1 = trade.getMark1(); // 备注字段1
		String mark2 = trade.getMark2(); // 备注字段2

		TCumInfoDao infoDao = new TCumInfoDao();
		String objName = infoDao.getCustName(payeeCode); // 收款商户名称

		String dealType = "0301"; // 处理类型
		String ctrlFlag = "1"; // 控制标识：0:存入数据库;1:不存数据库
		if (actionCode.equals("01030005")) {
			ctrlFlag = "0";// 担保交易需要把信息插入数据库
			dealType = "0501";// 担保交易处理类型
		}

		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", agentCode);// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", tmnNum);// 受理终端号
		g401.put("4008", tradeTime);// 受理时间
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", channelCode);// 渠道类型编码
		/*
		 * 外部订单号改成4028 g401.put("4017", orderSeq);// 终端流水号 keep值放4017
		 */
		g401.put("4017", trade.getKeep());// 终端流水号
		g401.put("4028", orderSeq);// 订单号
		g401.put("4018", "0.0.0.0");// 操作原始来源
		g401.put("4280", objName); // 收款商户名称
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", txnAmount);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", txnAmount);// 订单应付金额
		
		g401.put("4284", request.getMerId());//机构编码     //20130628 wanght
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", payeeCode);// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "101");// 费用项标识
		g405.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
		g407.put("4088", dealType);// 属性值1
		g407.put("4080", ctrlFlag);// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
		g407.put("4088", payeeCardAcctNbr);// 属性值1
		g407.put("4080", ctrlFlag);// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTACCTTYPE");// 业务属性编码
		g407.put("4088", "0001");// 属性值1
		g407.put("4080", ctrlFlag);// 控制标识
		g407.endRow();

		if (!Charset.trim(mark1).equals("")) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_MARK1");// 业务属性编码
			g407.put("4088", mark1);// 属性值1
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}

		if (!Charset.trim(mark2).equals("")) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_MARK2");// 业务属性编码
			g407.put("4088", mark2);// 属性值1
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", "110000");// 支付机构编码
		g408.put("4099", "0001");// 账户类型编码
		g408.put("4101", cardAcctNbr);// 账号
		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", txnAmount);// 支付金额
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);

		// 返回结果
		return dataSet;
	}

	/**
	 * 担保交易确认
	 * 
	 * @version: 1.00
	 * @history: 2012-2-23 下午05:22:53 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param trade
	 * @param cardAcctNbr
	 * @param payeeCardAcctNbr
	 * @return
	 * @throws Exception
	 * @see
	 */
	public PackageDataSet guaranteeVerify(TradeRequest trade, String cardAcctNbr, String payeeCardAcctNbr) throws Exception {
		String transSeq = trade.getTransSeq(); // 订单号
		String channelCode = trade.getChannelCode();// 交易渠道
		String payeeCode = trade.getPayeeCode(); // 收款商户编码
		String txnAmount = trade.getTxnAmount(); // 交易金额：分为单位
		String actionCode = trade.getActionCode(); // 操作编码
		String mark1 = trade.getMark1(); // 备注字段1
		String mark2 = trade.getMark2(); // 备注字段2

		// 插入担保交易的操作记录数据
		Double confAmount = Double.valueOf(txnAmount); // 确认的金额
		String confType = "10001"; // 确认类型：确认
		String stat = "S0C"; // 状态
		Long preId = getPreIdByOrderId(transSeq); // 预处理ID
		if (preId == null) {
			throw new Exception("担保交易申请订单不存在");
		}
		Integer count = getConfirmCount(preId, confType);// 扣款顺序号
		count += 2;

		/**
		 * 调用SCS0013,完成交易确认/交易取消操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4002", transSeq);// 订单编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4142", "OT007");
		g401.put("4144", channelCode);// 交易渠道
		g401.put("4017", trade.getKeep());// 终端流水号
		g401.put("4018", trade.getTmnNum());// 操作原始来源
		g401.put("4028", trade.getOrderSeq());// 外部订单号
		g401.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", count.toString());// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", payeeCode);// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4123", "spl");
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", count.toString());// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();

		/*
		 * // 业务属性信息 IParamGroup g407 = new ParamGroupImpl("407"); if(!Charset.trim(mark1).equals("")) { g407.put("4047", count.toString());// 业务单序号 g407.put("4051", actionCode);//
		 * 业务编码 g407.put("4087", "SCS_MARK1");// 业务属性编码 g407.put("4088", mark1);// 属性值1 g407.put("4080", "0");// 控制标识 g407.endRow(); } if(!Charset.trim(mark2).equals("")) {
		 * g407.put("4047", count.toString());// 业务单序号 g407.put("4051", actionCode);// 业务编码 g407.put("4087", "SCS_MARK2");// 业务属性编码 g407.put("4088", mark2);// 属性值1 g407.put("4080",
		 * "0");// 控制标识 g407.endRow(); }
		 */
//		IParamGroup g408 = new ParamGroupImpl("408");
//		g408.put("4103", "1");
//		g408.put("4098", "PT4115");
//		g408.put("4097", "PT0004");
//		g408.put("4021", "PT4115");
//		g408.put("4102", "PT4115");
//		g408.put("4104", "PT4115");
//		g408.endRow();
		
		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", "110000");// 支付机构编码
		g408.put("4099", "0001");// 账户类型编码
		g408.put("4101", cardAcctNbr);// 账号
		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", txnAmount);// 支付金额
		g408.endRow();

		IParamGroup g480 = new ParamGroupImpl("480");
		g480.put("4802", "成功");
		g480.put("4803", "0000");
		g480.put("4805", "S0A");
		g480.endRow();

		PackageDataSet dataSet = null;
		try {
			// 组成数据包,调用SCS0013接口
			IServiceCall caller = new ServiceCallImpl();
			dataSet = caller.call("SCS", "SCS0013", g401, g404, g405, g408, g480);
		} catch (Exception e) {
			stat = "S0F";
			// 插入担保确认失败记录
			insertTOppConfirm(preId, confAmount, confType, stat);
			throw new Exception(e.getMessage());
		}

		// 插入担保确认成功记录
		insertTOppConfirm(preId, confAmount, confType, stat);

		// 返回结果
		return dataSet;
	}

	/**
	 * 担保交易确认
	 * 
	 * @version: 1.00
	 * @history: 2012-2-23 下午05:22:53 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param trade
	 * @param cardAcctNbr
	 * @param payeeCardAcctNbr
	 * @return
	 * @throws Exception
	 * @see
	 */
	public PackageDataSet guaranteeVerify(DpInf5002TradeRequest trade, String cardAcctNbr, String payeeCardAcctNbr) throws Exception {
		String transSeq = trade.getTransSeq(); // 订单号
		String channelCode = trade.getChannelCode();// 交易渠道
		String payeeCode = trade.getPayeeCode(); // 收款商户编码
		String txnAmount = trade.getTxnAmount(); // 交易金额：分为单位
		String actionCode = trade.getActionCode(); // 操作编码
		String mark1 = trade.getMark1(); // 备注字段1
		String mark2 = trade.getMark2(); // 备注字段2

		// 插入担保交易的操作记录数据
		Double confAmount = Double.valueOf(txnAmount); // 确认的金额
		String confType = "10001"; // 确认类型：确认
		String stat = "S0C"; // 状态
		Long preId = getPreIdByOrderId(transSeq); // 预处理ID
		if (preId == null) {
			throw new Exception("担保交易申请订单不存在");
		}
		Integer count = getConfirmCount(preId, confType);// 扣款顺序号
		count += 2;

		/**
		 * 调用SCS0013,完成交易确认/交易取消操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4002", transSeq);// 订单编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4144", channelCode);// 交易渠道
		g401.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", count.toString());// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", payeeCode);// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4123", "spl");
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", count.toString());// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "101");// 费用项标识
		g405.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		if (!Charset.trim(mark1).equals("")) {
			g407.put("4047", count.toString());// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_MARK1");// 业务属性编码
			g407.put("4088", mark1);// 属性值1
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}
		if (!Charset.trim(mark2).equals("")) {
			g407.put("4047", count.toString());// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_MARK2");// 业务属性编码
			g407.put("4088", mark2);// 属性值1
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}

		IParamGroup g480 = new ParamGroupImpl("480");
		g480.put("4802", "成功");
		g480.put("4803", "0000");
		g480.put("4805", "S0A");
		g480.put("4806", "SCS_GUASURE");
		g480.endRow();

		PackageDataSet dataSet = null;
		try {
			// 组成数据包,调用SCS0013接口
			IServiceCall caller = new ServiceCallImpl();
			dataSet = caller.call("SCS", "SCS0013", g401, g404, g405, g407, g480);
		} catch (Exception e) {
			stat = "S0F";
			// 插入担保确认失败记录
			insertTOppConfirm(preId, confAmount, confType, stat);
			throw new Exception(e.getMessage());
		}

		// 插入担保确认成功记录
		insertTOppConfirm(preId, confAmount, confType, stat);

		// 返回结果
		return dataSet;
	}

	/**
	 * 担保交易取消
	 * 
	 * @version: 1.00
	 * @history: 2012-2-23 下午05:22:53 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param trade
	 * @param cardAcctNbr
	 * @param payeeCardAcctNbr
	 * @return
	 * @throws Exception
	 * @see
	 */
	public PackageDataSet guaranteeCancle(TradeRequest trade, String cardAcctNbr, String payeeCardAcctNbr) throws Exception {
		String transSeq = trade.getTransSeq(); // 订单号
		String channelCode = trade.getChannelCode();// 交易渠道
		String txnAmount = trade.getTxnAmount(); // 交易金额
		String actionCode = trade.getActionCode(); // 操作编码
		String mark1 = trade.getMark1(); // 备注字段1
		String mark2 = trade.getMark2(); // 备注字段2

		// 插入担保交易的操作记录数据
		Double confAmount = Double.valueOf(txnAmount); // 确认的金额
		String confType = "10002"; // 确认类型：取消
		String stat = "S0C"; // 状态
		Long preId = getPreIdByOrderId(transSeq); // 预处理ID
		if (preId == null) {
			throw new Exception("担保交易申请订单不存在");
		}
		Integer count = getConfirmCount(preId, confType);// 扣款顺序号
		count += 2;

		/**
		 * 调用SCS0013,完成交易确认/交易取消操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4002", transSeq);// 订单编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4142", "OT006");
		g401.put("4144", channelCode);// 交易渠道
		g401.put("4017", trade.getKeep());// 终端流水号
		g401.put("4018", trade.getTmnNum());// 操作原始来源
		g401.put("4028", trade.getOrderSeq());// 外部订单号
		g401.endRow();

		// 订单受理信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 
		g404.put("4049", "001");// 
		g404.put("4051", "01030005");// 
		g404.put("4051", "");// 
		g404.put("4053", "1");// 
		g404.put("4051", "");// 
		g404.endRow();

		// 业务单信息
		IParamGroup g407 = new ParamGroupImpl("407");
		// if(!Charset.trim(mark1).equals("")) {
		// g407.put("4047", count.toString());// 业务单序号
		// g407.put("4051", actionCode);// 业务编码
		// g407.put("4087", "SCS_MARK1");// 业务属性编码
		// g407.put("4088", mark1);// 属性值1
		// g407.put("4080", "0");// 控制标识
		// g407.endRow();
		// }
		// if(!Charset.trim(mark2).equals("")) {
		// g407.put("4047", count.toString());// 业务单序号
		// g407.put("4051", actionCode);// 业务编码
		// g407.put("4087", "SCS_MARK2");// 业务属性编码
		// g407.put("4088", mark2);// 属性值1
		// g407.put("4080", "0");// 控制标识
		// g407.endRow();
		// }

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", count.toString());// 扣款顺序号
		g408.put("4097", "PT9001");// 支付方式编码
		g408.put("4098", "110000");// 支付机构编码
		g408.put("4099", "0001");// 账户类型编码
		g408.put("4101", cardAcctNbr);// 账号
		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", txnAmount);// 支付金额
		g408.endRow();

		IParamGroup g480 = new ParamGroupImpl("480");
		g480.put("4802", "成功");
		g480.put("4803", "0000");
		g480.put("4805", "S0A");
		g480.endRow();

		// 组成数据包,调用SCS0013接口
		PackageDataSet dataSet = null;
		try {
			IServiceCall caller = new ServiceCallImpl();
			dataSet = caller.call("SCS", "SCS0013", g401, g407, g408, g480);
		} catch (Exception e) {
			stat = "S0F";
			// 插入担保取消失败记录
			insertTOppConfirm(preId, confAmount, confType, stat);
			throw new Exception(e.getMessage());
		}
		// 插入担保成功失败记录
		insertTOppConfirm(preId, confAmount, confType, stat);

		// 返回结果
		return dataSet;
	}

	/**
	 * 担保交易取消
	 * 
	 * @version: 1.00
	 * @history: 2012-2-23 下午05:22:53 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param trade
	 * @param cardAcctNbr
	 * @param payeeCardAcctNbr
	 * @return
	 * @throws Exception
	 * @see
	 */
	public PackageDataSet guaranteeCancle(DpInf5002TradeRequest trade, String cardAcctNbr, String payeeCardAcctNbr) throws Exception {
		String transSeq = trade.getTransSeq(); // 订单号
		String channelCode = trade.getChannelCode();// 交易渠道
		String txnAmount = trade.getTxnAmount(); // 交易金额
		String actionCode = trade.getActionCode(); // 操作编码
		String mark1 = trade.getMark1(); // 备注字段1
		String mark2 = trade.getMark2(); // 备注字段2

		// 插入担保交易的操作记录数据
		Double confAmount = Double.valueOf(txnAmount); // 确认的金额
		String confType = "10002"; // 确认类型：取消
		String stat = "S0C"; // 状态
		Long preId = getPreIdByOrderId(transSeq); // 预处理ID
		if (preId == null) {
			throw new Exception("担保交易申请订单不存在");
		}
		Integer count = getConfirmCount(preId, confType);// 扣款顺序号
		count += 2;

		/**
		 * 调用SCS0013,完成交易确认/交易取消操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4002", transSeq);// 订单编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4144", channelCode);// 交易渠道
		g401.endRow();

		// 订单受理信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 
		g404.put("4049", "001");// 
		g404.put("4051", "01030005");// 
		g404.put("4051", "");// 
		g404.put("4053", "1");// 
		g404.put("4051", "");// 
		g404.endRow();

		// 业务单信息
		IParamGroup g407 = new ParamGroupImpl("407");
		// if(!Charset.trim(mark1).equals("")) {
		// g407.put("4047", count.toString());// 业务单序号
		// g407.put("4051", actionCode);// 业务编码
		// g407.put("4087", "SCS_MARK1");// 业务属性编码
		// g407.put("4088", mark1);// 属性值1
		// g407.put("4080", "0");// 控制标识
		// g407.endRow();
		// }
		// if(!Charset.trim(mark2).equals("")) {
		// g407.put("4047", count.toString());// 业务单序号
		// g407.put("4051", actionCode);// 业务编码
		// g407.put("4087", "SCS_MARK2");// 业务属性编码
		// g407.put("4088", mark2);// 属性值1
		// g407.put("4080", "0");// 控制标识
		// g407.endRow();
		// }

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", count.toString());// 扣款顺序号
		g408.put("4097", "PT9001");// 支付方式编码
		g408.put("4098", "110000");// 支付机构编码
		g408.put("4099", "0001");// 账户类型编码
		g408.put("4101", cardAcctNbr);// 账号
		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", txnAmount);// 支付金额
		g408.endRow();

		IParamGroup g480 = new ParamGroupImpl("480");
		g480.put("4802", "成功");
		g480.put("4803", "0000");
		g480.put("4805", "S0A");
		g480.put("4806", "SCS_REPAYGUA");
		g480.endRow();

		// 组成数据包,调用SCS0013接口
		PackageDataSet dataSet = null;
		try {
			IServiceCall caller = new ServiceCallImpl();
			dataSet = caller.call("SCS", "SCS0013", g401, g407, g408, g480);
		} catch (Exception e) {
			stat = "S0F";
			// 插入担保取消失败记录
			insertTOppConfirm(preId, confAmount, confType, stat);
			throw new Exception(e.getMessage());
		}
		// 插入担保成功失败记录
		insertTOppConfirm(preId, confAmount, confType, stat);

		// 返回结果
		return dataSet;
	}

	/**
	 * 预授权交易确认/交易取消流程
	 * 
	 * @version: 1.00
	 * @history: 2012-2-23 下午05:22:53 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param trade
	 * @param cardAcctNbr
	 * @param payeeCardAcctNbr
	 * @return
	 * @throws Exception
	 * @see
	 */
	public PackageDataSet authVerifyOrCancle(TradeRequest trade, String cardAcctNbr, String payeeCardAcctNbr, boolean isVerify) throws Exception {
		String transSeq = trade.getTransSeq(); // 订单号
		String channelCode = trade.getChannelCode();// 交易渠道
		String payeeCode = trade.getPayeeCode(); // 收款商户编码
		String txnAmount = trade.getTxnAmount(); // 交易金额：分为单位
		String actionCode = trade.getActionCode(); // 操作编码
		String mark1 = trade.getMark1(); // 备注字段1
		String mark2 = trade.getMark2(); // 备注字段2

		/**
		 * 调用SCS0013,完成交易确认/交易取消操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4002", transSeq);// 订单编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4144", channelCode);// 交易渠道
		g401.put("4017", trade.getKeep());
		g401.put("4018", trade.getTmnNum());// 操作原始来源
		g401.put("4028", trade.getOrderSeq());// 外部订单号
		g401.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", payeeCode);// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4906", "");// 业务数量
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "101");// 费用项标识
		g405.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
		g407.put("4088", "0401");// 属性值1
		g407.put("4080", "1");// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
		g407.put("4088", payeeCardAcctNbr);// 属性值1
		g407.put("4080", "1");// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTACCTTYPE");// 业务属性编码
		g407.put("4088", "0001");// 属性值1
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		// if(!Charset.trim(mark1).equals("")) {
		// g407.put("4047", "1");// 业务单序号
		// g407.put("4051", actionCode);// 业务编码
		// g407.put("4087", "SCS_MARK1");// 业务属性编码
		// g407.put("4088", mark1);// 属性值1
		// g407.put("4080", "0");// 控制标识
		// g407.endRow();
		// }
		//		
		// if(!Charset.trim(mark2).equals("")) {
		// g407.put("4047", "1");// 业务单序号
		// g407.put("4051", actionCode);// 业务编码
		// g407.put("4087", "SCS_MARK2");// 业务属性编码
		// g407.put("4088", mark2);// 属性值1
		// g407.put("4080", "0");// 控制标识
		// g407.endRow();
		// }

		/*
		 * // 支付单信息 IParamGroup g408 = new ParamGroupImpl("408"); g408.put("4103", "1");// 扣款顺序号 g408.put("4097", "PT0004");// 支付方式编码 g408.put("4098", "110000");// 支付机构编码
		 * g408.put("4099", "0001");// 账户类型编码 g408.put("4101", cardAcctNbr);// 账号 g408.put("4102", MD5.MD5Encode("123456"));// 支付密码 g408.put("4021", "0001");// 币种编码
		 * g408.put("4104", txnAmount);// 支付金额 g408.endRow();
		 */

		IParamGroup g480 = new ParamGroupImpl("480");
		// 判断操作类型
		if (isVerify) {
			// 交易确认
			g480.put("4805", "S0A");
		} else {
			// 交易取消
			g480.put("4805", "S0X");
		}
		g480.endRow();

		// 组成数据包,调用SCS0013接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0013", g401, g404, g407, g480);

		// 返回结果
		return dataSet;
	}

	/**
	 * 预授权交易确认/交易取消流程
	 * 
	 * @version: 1.00
	 * @history: 2012-2-23 下午05:22:53 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param trade
	 * @param cardAcctNbr
	 * @param payeeCardAcctNbr
	 * @return
	 * @throws Exception
	 * @see
	 */
	public PackageDataSet authVerifyOrCancle(DpInf5002TradeRequest trade, String cardAcctNbr, String payeeCardAcctNbr, boolean isVerify)
			throws Exception {
		String transSeq = trade.getTransSeq(); // 订单号
		String channelCode = trade.getChannelCode();// 交易渠道
		String payeeCode = trade.getPayeeCode(); // 收款商户编码
		String txnAmount = trade.getTxnAmount(); // 交易金额：分为单位
		String actionCode = trade.getActionCode(); // 操作编码
		String mark1 = trade.getMark1(); // 备注字段1
		String mark2 = trade.getMark2(); // 备注字段2

		/**
		 * 调用SCS0013,完成交易确认/交易取消操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4002", transSeq);// 订单编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4144", channelCode);// 交易渠道
		g401.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", payeeCode);// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "101");// 费用项标识
		g405.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
		g407.put("4088", "0401");// 属性值1
		g407.put("4080", "1");// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
		g407.put("4088", payeeCardAcctNbr);// 属性值1
		g407.put("4080", "1");// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTACCTTYPE");// 业务属性编码
		g407.put("4088", "0001");// 属性值1
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		// if(!Charset.trim(mark1).equals("")) {
		// g407.put("4047", "1");// 业务单序号
		// g407.put("4051", actionCode);// 业务编码
		// g407.put("4087", "SCS_MARK1");// 业务属性编码
		// g407.put("4088", mark1);// 属性值1
		// g407.put("4080", "0");// 控制标识
		// g407.endRow();
		// }
		//		
		// if(!Charset.trim(mark2).equals("")) {
		// g407.put("4047", "1");// 业务单序号
		// g407.put("4051", actionCode);// 业务编码
		// g407.put("4087", "SCS_MARK2");// 业务属性编码
		// g407.put("4088", mark2);// 属性值1
		// g407.put("4080", "0");// 控制标识
		// g407.endRow();
		// }

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", "110000");// 支付机构编码
		g408.put("4099", "0001");// 账户类型编码
		g408.put("4101", cardAcctNbr);// 账号
		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", txnAmount);// 支付金额
		g408.endRow();

		IParamGroup g480 = new ParamGroupImpl("480");
		// 判断操作类型
		if (isVerify) {
			// 交易确认
			g480.put("4805", "S0A");
		} else {
			// 交易取消
			g480.put("4805", "S0X");
		}
		g480.endRow();

		// 组成数据包,调用SCS0013接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0013", g401, g404, g405, g407, g408, g480);

		// 返回结果
		return dataSet;
	}

	public PackageDataSet auth(TradeRequest trade, String cardAcctNbr, String payeeCardAcctNbr,CommonReqAbs request) throws Exception {
		String agentCode = trade.getAgentCode(); // 商户编码
		String channelCode = trade.getChannelCode(); // 交易渠道
		String tmnNum = trade.getTmnNum(); // 终端号
		String areaCode = trade.getAreaCode(); // 区域编码
		String actionCode = trade.getActionCode(); // 操作编码
		String txnAmount = trade.getTxnAmount(); // 交易金额
		String payeeCode = trade.getPayeeCode(); // 收款商户编码
		String orderSeq = trade.getOrderSeq(); // 订单号
		String tradeTime = trade.getTradeTime(); // 交易时间
		String mark1 = trade.getMark1(); // 备注字段1
		String mark2 = trade.getMark2(); // 备注字段2

		TCumInfoDao infoDao = new TCumInfoDao();
		String objName = infoDao.getCustName(payeeCode); // 收款商户名称

		String dealType = "0301"; // 处理类型
		String ctrlFlag = "1"; // 控制标识：0:存入数据库;1:不存数据库
		if (actionCode.equals("01030005")) {
			ctrlFlag = "0";// 担保交易需要把信息插入数据库
			dealType = "0501";// 担保交易处理类型
		}

		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", agentCode);// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", tmnNum);// 受理终端号
		g401.put("4008", tradeTime);// 受理时间
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", channelCode);// 渠道类型编码
		/*
		 * 外部订单号改成4028 g401.put("4017", orderSeq);// 终端流水号 keep值放4017
		 */
		g401.put("4017", trade.getKeep());// 终端流水号
		g401.put("4028", orderSeq);// 订单号
		g401.put("4018", "0.0.0.0");// 操作原始来源
		g401.put("4280", objName); // 收款商户名称
		
		g401.put("4284", request.getMerId());//机构编码     //20130628 wanght
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", txnAmount);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", txnAmount);// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", payeeCode);// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "101");// 费用项标识
		g405.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", "110000");// 支付机构编码
		g408.put("4099", "0001");// 账户类型编码
		g408.put("4101", cardAcctNbr);// 账号
		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", txnAmount);// 支付金额
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g408);

		// 返回结果
		return dataSet;
	}

	public PackageDataSet auth(DpInf5002TradeRequest trade, String cardAcctNbr, String payeeCardAcctNbr,CommonReqAbs request) throws Exception {
		String agentCode = trade.getAgentCode(); // 商户编码
		String channelCode = trade.getChannelCode(); // 交易渠道
		String tmnNum = trade.getTmnNum(); // 终端号
		String areaCode = trade.getAreaCode(); // 区域编码
		String actionCode = trade.getActionCode(); // 操作编码
		String txnAmount = trade.getTxnAmount(); // 交易金额
		String payeeCode = trade.getPayeeCode(); // 收款商户编码
		String orderSeq = trade.getOrderSeq(); // 订单号
		String tradeTime = trade.getTradeTime(); // 交易时间
		String mark1 = trade.getMark1(); // 备注字段1
		String mark2 = trade.getMark2(); // 备注字段2

		TCumInfoDao infoDao = new TCumInfoDao();
		String objName = infoDao.getCustName(payeeCode); // 收款商户名称

		String dealType = "0301"; // 处理类型
		String ctrlFlag = "1"; // 控制标识：0:存入数据库;1:不存数据库
		if (actionCode.equals("01030005")) {
			ctrlFlag = "0";// 担保交易需要把信息插入数据库
			dealType = "0501";// 担保交易处理类型
		}

		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", agentCode);// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", tmnNum);// 受理终端号
		g401.put("4008", tradeTime);// 受理时间
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", channelCode);// 渠道类型编码
		/*
		 * 外部订单号改成4028 g401.put("4017", orderSeq);// 终端流水号 keep值放4017
		 */
		g401.put("4017", trade.getKeep());// 终端流水号
		g401.put("4028", orderSeq);// 订单号
		g401.put("4018", "0.0.0.0");// 操作原始来源
		g401.put("4280", objName); // 收款商户名称
		
		g401.put("4284", request.getMerId());//机构编码     //20130628 wanght
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", txnAmount);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", txnAmount);// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", payeeCode);// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "101");// 费用项标识
		g405.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", "110000");// 支付机构编码
		g408.put("4099", "0001");// 账户类型编码
		g408.put("4101", cardAcctNbr);// 账号
		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", txnAmount);// 支付金额
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g408);

		// 返回结果
		return dataSet;
	}

	public PackageDataSet firstRoute(String custCode, String areacode, String channelCode, String actionCode, String merId, String tmnnum,
			String amount, String payType, String oriCode) throws Exception {
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4004", custCode); // 客户编码
		g404.put("4144", channelCode); // 渠道类型
		g404.put("4006", areacode); // 所属区域编码
		g404.put("4007", tmnnum); // 受理终端号
		g404.put("4051", actionCode); // 业务编码
		g404.endRow();

		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("2011", merId); // 所属合作伙伴编码
		g408.put("4023", amount); // 订单原始金额
		g408.put("4097", payType); // 支付方式编码
		g408.put("4098", oriCode); // 支付机构编码
		g408.endRow();

		// 组成数据包,调用SCS0019接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0019", g404, g408);

		return dataSet;
	}

	/**
	 * 根据终端号查询回调地址
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String getCallBackUrlByTremId(String tremId) throws Exception {
		// String sql =
		// "select ps.serv_ip,ps.serv_port,ps.link_info, trim(pa.stat),'' value1 ,ps.link_type from t_pnm_server ps,t_pnm_partner pa where ps.link_num='90000163' and ps.stat='S0A' and ps.prtn_id=pa.prtn_id and pa.stat='S0A'";
		String sql = "select ps.link_info from t_pnm_server ps,t_pnm_partner pa where ps.link_num='" + tremId
				+ "' and ps.stat='S0A' and ps.prtn_id=pa.prtn_id and pa.stat='S0A' and ps.link_type='PLUGINACTSER'";
		// String url = (String) DAO.queryForObject(sql, String.class);
		List list = DAO.queryForList(sql);
		String url = null;
		if (list != null && list.size() > 0) {
			Map map = (Map) list.get(0);
			url = (String) map.get("LINK_INFO");
		}
		return url;
	}

	/**
	 * 生成定时任务,担保的三个接口 czg--2012-4-18上午10:24:52
	 * 
	 * @throws Exception
	 */
	public void makeTask(String url, String param, String expectedValue) {
		try {
			Calendar cal = Calendar.getInstance();
			Calendar tomorrow = Calendar.getInstance();
			tomorrow.add(Calendar.DAY_OF_MONTH, 2);
			tomorrow.set(Calendar.HOUR_OF_DAY, 0);
			tomorrow.set(Calendar.MINUTE, 0);
			tomorrow.set(Calendar.SECOND, 0);
			tomorrow.set(Calendar.MILLISECOND, 0);
			Date clTime = cal.getTime();
			cal.add(Calendar.MINUTE, 1);
			Date nrTime = cal.getTime();
			String sql = "INSERT INTO T_ITF_GUARANTEETASK(TASK_ID,URL,PARAM,EXPECTED_VALUE,RUNTIMES,CREATE_TIME,LAST_RUNTIME,STAT,NEXT_RUNTIME,END_TIME) VALUES(?,?,?,?,?,?,?,?,?,?)";
			Object[] values = { getTaskIdSeq(), url, param, expectedValue, 1, clTime, clTime, "S0A", nrTime, tomorrow.getTime() };
			DAO.insert(sql, values);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("添加信息到定时任务表异常");
		}
	}

	public long getTaskIdSeq() {
		String sql = "SELECT SQ_ITF_GUARANTEETASK.nextval FROM DUAL";
		long taskId = DAO.queryForLong(sql);
		log.info("taskId=[" + taskId + "]");
		return taskId;
	}

	/**
	 * 查询客户编码和终端号是否对应
	 * @param custCode
	 * @param termNumNo
	 * @return
	 */
	public boolean isCustCodeMatchTermNumNo(String custCode, String termNumNo) {
		String sql = "SELECT count(1) FROM T_CUM_CHANNEL CC, T_CUM_INFO CI WHERE CC.TERM_ID = (select term_id from T_TMN_INFO where term_code='"+termNumNo+"' or ex_termcode='"+termNumNo+"') AND CI.CUST_ID = CC.CUST_ID AND CI.CUST_CODE='"+custCode+"'";
//		long n = DAO.queryForLong(sql, new Object[] { custCode.trim(), termNumNo.trim() });
		long n = DAO.queryForLong(sql);
		log.info("sql=[" + sql + "]");
		return n >=1;
	}
	/**
	 * 根据客户编码查找内部终端号
	 * @param custCode
	 * @return
	 */
	public boolean getTermNumNoByCustCode(String custCode,String termNumNo) {
		String sql = "select count(1) from T_TMN_INFO tmn,T_CUM_INFO info,T_CUM_CHANNEL cc where CC.TERM_ID=tmn.TERM_ID and info.CUST_ID=cc.CUST_ID and tmn.STAT='S0A' and info.CUST_CODE=? and tmn.TERM_CODE=?";
		log.info("sql=[" + sql + "]  cust_code=["+custCode+"]  termNumNo=["+termNumNo+"]");
		int n= (Integer)DAO.queryForObject(sql, new Object[]{ custCode, termNumNo}, Integer.class);
		return n>0;
	}
	
	/**
	 * 通过外部终端号查询客户编码
	 * @param termNumNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,String> getCustCodeByExtTermNumNo(String termNumNo) {
//		String sql = "SELECT CI.CUST_CODE FROM T_CUM_CHANNEL CC, T_CUM_INFO CI WHERE CC.TERM_ID = (select term_id from T_TMN_INFO where ex_termcode=? ) AND CI.CUST_ID = CC.CUST_ID";
//		String r = (String)DAO.queryForObject(sql, new Object[] { termNumNo.trim()}, String.class );
//		String sql = "SELECT CI.CUST_CODE,TI.TERM_CODE FROM T_CUM_CHANNEL CC, T_CUM_INFO CI,T_TMN_INFO TI WHERE CC.TERM_ID = (select term_id from T_TMN_INFO where ex_termcode=? ) AND CI.CUST_ID = CC.CUST_ID";
		String sql = "select tmn.TERM_CODE,info.CUST_CODE from T_TMN_INFO tmn,T_CUM_INFO info,T_CUM_CHANNEL cc where tmn.ex_termcode=? and CC.TERM_ID=tmn.TERM_ID and info.CUST_ID=cc.CUST_ID and tmn.STAT='S0A'";
		 List list = DAO.queryForList(sql, new Object[] { termNumNo.trim()});
		 Map<String,String> map = null;
		 if(list!=null&&list.size()!=0){
			 map= (Map<String,String>)list.get(0);//CUST_CODE  TERM_CODE
		 }
		return map;
	}
	
	/**
	 * 通过外部终端号查询终端号
	 * @param custCode
	 * @param termNumNo
	 * @return
	 */
	public String getTermNumNoByExt(String extTermNumNo) {
		String sql = "select term_code from T_TMN_INFO where ex_termcode=? ";
		String r = (String)DAO.queryForObject(sql, new Object[] { extTermNumNo.trim()}, String.class );
		return r;
	}
	/**
	 * 通过外部终端号查询终端号
	 * @param custCode
	 * @param termNumNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getTermNumNoByExt(String extTermNumNo,String	custCode) {
//		String sql = "SELECT TI.TERM_CODE FROM T_CUM_CHANNEL CC, T_CUM_INFO CI,T_TMN_INFO TI WHERE CC.TERM_ID = (select term_id from T_TMN_INFO where ex_termcode=? ) AND CI.CUST_ID = CC.CUST_ID AND CI.CUST_CODE=?";
		String sql = "select tmn.TERM_CODE from T_TMN_INFO tmn,T_CUM_INFO info,T_CUM_CHANNEL cc where tmn.ex_termcode=? and CC.TERM_ID=tmn.TERM_ID and info.CUST_ID=cc.CUST_ID and tmn.STAT='S0A' and info.CUST_CODE=?";
		List list = DAO.queryForList(sql, new Object[] { extTermNumNo.trim(),custCode.trim()});
		log.info("extTermNumNo=["+extTermNumNo+"]  custCode=["+custCode+"]");
		if(list!=null&& list.size()!=0){
			Map map = (Map) list.get(0);
			return (String) map.get("TERM_CODE") ;
		}
		return "";
	}

}
