package framework.exception;

public class INFErrorDef {
	public static final String POSSEQNO_CONFLICT_CODE = "010006";
	public static final String POSSEQNO_CONFLICT_REASON = "违反数据唯一性约束";

	// ***参数不能为空
	public static final String INF_ParaNotNull_ERRCODE = "010011";
	// ***参数只能为数字
	public static final String INF_ParaNumOnly_ERRCODE = "010012";
	// 订单不存在
	public static final String INF_OrderNotExist_ERRCODE = "010013";
	// 暂不支持充值类型查询
	public static final String INF_QueryTypeOfCharge_ERRCODE = "010014";
	// 暂不支持所有账户查询
	public static final String INF_QueryTypeOfAllAcount_ERRCODE = "010015";
	// 开始日期不能大于结束日期
	public static final String INF_FromdateBiggerThanTodate_ERRCODE = "010016";
	// 日期格式不正确
	public static final String INF_DateFormatWrong_ERRCODE = "010017";
	// 业务管理平台商户编码不存在
	public static final String INF_PartnerIdNotExists_ERRCODE = "010018";
	// 产品号码与订单产品号码不符
	public static final String INF_ProductNoWrong_ERRCODE = "010019";
	// 交易金额与原订单不符
	public static final String INF_AmountWrong_ERRCODE = "010020";
	// 该订单没有支付信息
	public static final String INF_NoPaymentInfo_ERRCODE = "010021";
	// 账户选择错误
	public static final String INF_AcountWrong_ERRCODE = "010022";
	// 支付应用标识已过时，请重新支付应用获取列表
	public static final String INF_APPOUTDATE_ERRCODE = "010023";
	// 查询方式错误
	public static final String INF_QueryType_ERRCODE = "010024";
	// 商户身份认证失败
	public static final String INF_CustNotExists_ERRCODE = "010025";
	// 签名错误
	public static final String INF_SignMistakes_ERRCODE = "010026";
	// 条形码不正确
	public static final String INF_BarCodeWrong_ERRCODE = "010027";
	// 充值手机号不正确
	public static final String INF_ChargeNoWrong_ERRCODE = "010028";
	// 订单更新未完成
	public static final String INF_OrderUpdateWrong_ERRCODE = "010029";
	// 对应服务不存在
	public static final String INF_NOSERVICE_ERRCODE = "010030";
	// 翼支付客户端尚不支持您的终端类型
	public static final String INF_NOClient_ERRCODE = "010031";
	// 商户平台绑定IP不正确
	public static final String INF_IPWrong_ERRCODE = "010033";
	// 手机号码校验失败
	public static final String INF_InvalidProductNo_ERRCODE = "010034";
	// 订单未完成支付
	public static final String INF_OrderPayWrong_ERRCODE = "010035";
	// 短信下发次数超限
	public static final String INF_SmsMax_ERRCODE = "010036";
	// 短信验证码错误
	public static final String INF_AuthCodeWrong_ERRCODE = "010037";
	// 翼支付账户支付方式错误
	public static final String INF_PaytypeWrong_ERRCODE = "010038";
	// 暂不支持天翼支付卡方式支付
	public static final String INF_PaytypeUnsupported_ERRCODE = "010039";
	// 会话码校验失败
	public static final String INF_InvalidSessionKey_ERRCODE = "010040";
	// 订单已支付，不允许撤销
	public static final String INF_OrderCantnotRevoke_ERRCODE = "010041";
	// 订单已撤销
	public static final String INF_OrderStatIsS0X_ERRCODE = "010042";

	
	public static final String GWM_NET_ERRCODE = "011007";
	public static final String GWM_NET_ERRCODE_DESC = "订单状态未确定";

	// 转账金额不等于购卡所需金额
	public static final String INF_INCONSISTENT_AMOUNT_ERRCODE = "010043";
	public static final String INF_INCONSISTENT_AMOUNT_ERRDESC = "转账金额不等于购卡所需金额";

	public static final String INF_CARDBOOKSEQ_NOTEXIST_ERRCODE = "010044";
	public static final String INF_CARDBOOKSEQ_NOTEXIST_ERRDESC = "卡预定流水号不存在";

	// 预订金额不能超过预订额度
	public static final String INF_CARDBOOKSEQ_OUT_OF_LIMIT = "010045";
	public static final String INF_CARDBOOKSEQ_OUT_OF_LIMIT_ERRDESC = "非实名用户预定金额不能超过";

	// 商户超出合作期限
	public static final String INF_MERCHANT_EXPIRE = "010046";

	// 卡不存在
	public static final String INF_CARD_NOT_EXIST = "010047";

	// 持卡人新增失败
	public static final String CUM_REG_FAIL = "010048";

	// 卡密码错误
	public static final String CARD_PWD_VALID_FAIL = "010049";


	//	证书已过期
	public static final String CER_INVALID = "010050";

	//	证书与商户不匹配
	public static final String CER_NOTOWNED_BY_MER = "010051";

	// 返回包添加签名信息失败
	public static final String RESPONSE_REPKG_FAIL = "010052";

	// 签名验证失败
	public static final String CER_SIGN_VAREFY_FAIL = "010053";

	// MD5签名验证失败
	public static final String MD5_SIGN_VAREFY_FAIL = "010054";
	
	// 银行编码错误
	public static final String INF_BANKCODE_ERROR = "010055";
	public static final String INF_BANKCODE_ERROR_DES = "银行编码错误";
	
	// 卡密码超过错误次数，自动锁卡
	public static final String CARD_PWD_FAIL_LOCK = "010056";
	
	// 客户编码与账户名不一致
	public static final String CUSTCODE_NOT_MATCH_ACCOUNTNAME = "019999";
	public static final String CUSTCODE_NOT_MATCH_ACCOUNTNAME_DESC = "客户编码与账户名不匹配";
	
	//订单类错误
	public static final String OUT_ORDERNO_REPEAT = "011001"; //订单重复 已支付
	
	public static final String OUT_ORDERNO_REPEAT_S0P = "011008";  //订单重复 处理中
	
	public static final String OUT_ORDERNO_REPEAT_FAIL = "011009";  //添加订单控制失败
	
	public static final String OUT_ORDERNO_REPEAT_DESC = "外部订单号重复"; 

	//密码校验类错误
	public static final String PAY_PWD_FAULT = "011002";
	public static final String PAY_PWD_FAULT_DESC = "密码校验失败";
	
	public static final String PAY_PWD_FAULT_NULL = "密码不能为空";
	
	public static final String PAY_AMOUNT_OUT = "已超过单笔限制";
	
	public static final String PAY_AMOUNT_MAX = "已超过累计限制";
	
	public static final String ROUTER_ERROR_CODE = "002135";
	public static final String ROUTER_ERROR_DESC = "该账户不支持校验";
	
	//参数逻辑类错误
	public static final String AGENTCODE_CRE_FAULT = "011014";
	public static final String AGENTCODE_CRE_FAULT_DESC = "代理商数据异常,请重新配置";
	public static final String CUSTCODE_NOT_EXIST = "011003";
	public static final String CUSTCODE_NOT_EXIST_DESC = " 客户编码不存在";
	public static final String  FAIL_TO_GET_CARD_INFO_DESC= "获取卡信息失败";
	public static final String CUSTCODE_NOT_MATCH_TERMNUMNO_DESC = "客户编码与终端号不匹配";
	public static final String CUSTCODE_NOT_MATCH_MERG_DESC = "不是当前机构下的企业账户或设定关系中的机构";
	public static final String CUSTCODE_R_NOT_MATCH_MERG_DESC = "收款方不是当前机构下的企业账户或设定关系中的机构";
	public static final String CUSTCODE_NOT_MATCH_STAFF_DESC = "客户编码与用户名不一致";
	
	public static final String BANKACCT_NOT_CONTRACTACCT = "011004"; //银行账户号不是签约id的账户号
	public static final String BANKACCT_NOT_CONTRACTACCT_DESC = "银行账户号不是签约id的账户号"; 
	public static final String CONTRACT_NOT_NETCODE = "011005"; //签约ID无对应网点编码
	public static final String CONTRACT_NOT_NETCODE_DESC = "签约ID已失效或不存在"; 
	public static final String BANKACCT_NOT_cardAccNbr = "011006"; //银行账户号无对应的天讯卡户号
	public static final String BANKACCT_NOT_cardAccNbr_DESC = "无对应的天讯卡户号"; 
	
	public static final String CUSTCODE_NOT_MATCH_DESC = "该外部终端号找不到客户";
	public static final String TMNNUMNO_NOT_MATCH_DESC = "该外部终端号找不到内部终端号";
	public static final String TMNNUMNO_NOT_MATCH_DESC_2 = "内部终端号不匹配";
	public static final String CUSTCODE_NOT_MATCH_DESC_3= "企业账户与机构号不匹配";
	
	public static final String GAMEACCT_AMOUNT_REPEAT = "同一游戏账号5分钟内请勿做同金额重复充值";
	
	// 参数格式类错误
	public static final String CUSTCODE_NOT_MATCH = "019999";

	
	public static final String PAYTYPE_UNSUPPORT = "019999";
	public static final String PAYTYPE_UNSUPPORT_DESC = "不不支持的支付方式";
	public static final String CAN_NOT_FIND_ROUTE = "019999";
	public static final String CAN_NOT_FIND_ROUTE_DESC = "找不到路由";
	public static final String CAN_NOT_FIND_ROUTE_DESC_2 = "账号未找到区域编码或银行编码";
	public static final String STARTNUM_EQUAL_ENDNUM = "019999";
	public static final String STARTNUM_EQUAL_ENDNUM_DESC = "查询开始序号不能等于或大于结束序号";
	
	public static final String NO_TRANSACTION_RECORD = "011010";
	public static final String NO_TRANSACTION_RECORD_DESC = "没有查询到交易记录";
	
	public static final String ERROR_CHILD_CARD = "操作的不是该账户下的子卡";
	
	public static final String NO_POWER_OF_100 = "019999";
	public static final String NO_POWER_OF_100_DESC = "日交易限额不支持小数，精确到元";
	
	public static final String NO_TAX_OF_100 = "019999";
	public static final String NO_TAX_OF_100_DESC = "金额不支持小数，精确到元";

	public static final String DUPLICATE_BANK_ACCT = "019999";
	public static final String DUPLICATE_BANK_ACCT_DESC = "银行帐号已绑定该企业账户";
	public static final String CUST_CODE_IS_BINDING = "011013";
	public static final String CUST_CODE_IS_BINDING_DESC = "该商户绑卡中";
	
	public static final String INPUT_VERIFYCODE_FIRST= "011011";//客户端首次登录，需要验证码
	public static final String INPUT_VERIFYCODE_NOT_THE_SAME= "011012";//非本机登录，需要验证码
	public static final String INPUT_VERIFYCODE_FIRST_DESC = "客户端首次登录，需要验证码";
	public static final String INPUT_VERIFYCODE_NOT_THE_SAME_DESC = "登录设备发生变动";

	public static final String VERIFYCODE_ERROR= "019999";
	public static final String VERIFYCODE_ERROR_DESC = "验证码错误或有效期已过";

	public static final String REALLY_NAME_NOT_THE_SAME= "019999";
	public static final String REALLY_NAME_NOT_THE_SAME_DESC = "开户姓名与注册申请人姓名不一致";
	public static final String CERNO_NOT_THE_SAME= "019999";
	public static final String CERNO_NOT_THE_SAME_DESC = "银行开户证件号与注册申请人证件号不一致";
	public static final String WRONG_BANK_ACCT= "019999";
	public static final String WRONG_BANK_ACCT_DESC = "银行卡需为借记卡";
	public static final String GET_ACCTTYPE_FAIL= "019999";
	public static final String GET_ACCTTYPE_FAIL_DESC = "获取账户类型失败";
	
	public static final String ORDER_NO_NOT_EXITS= "019999";
	public static final String ORDER_NO_NOT_EXITS_DESC = "订单号不存在";
	public static final String ORDER_NOT_MATCH_DESC = "没有找到符合条件的订单";

	public static final String PASSWORD_NOT_THE_SAME= "019999";
	public static final String PASSWORD_NOT_THE_SAME_DESC = "两次输入的密码不一致";
	
	public static final String STAFF_CODE_MOBILE= "019999";
	public static final String STAFF_CODE_MOBILE_DESC = "获取工号手机号码出错";
	
	public static final String DEFAULT_STAFF_CODE= "019999";
	public static final String DEFAULT_STAFF_CODE_DESC = "获取默认工号出错";
	/**
	 * 字段效验错误
	 */
	public static final String FILED_ERROR= "019990";
	/**
	 * 输入日期格式错误
	 */
	public static final String INPUT_DATE_FORMAT_ERROR = "019999";
	public static final String INPUT_DATE_FORMAT_ERROR_DESC = "输入日期格式错误";
}
