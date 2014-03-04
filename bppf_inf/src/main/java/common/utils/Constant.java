package common.utils;

public class Constant {
	
	//订单编码
	public static final String BUSI_PAY_ORDER_CODE = "OT001";    //业务类订单（由客户端发起的业务订单）
	
	public static final String MGR_PAY_ORDER_CODE = "OT002";    //管理类订单（由管理平台发起的业务订单）
	
	public static final String BUSI_REFUND_ORDER_CODE = "OT101";    //业务类退款订单（由商户平台发起、手机收银台发起的业务退款订单）
	
	public static final String MGR_REFUND_ORDER_CODE = "OT102";    //管理类退款订单（由管理平台业务退款订单）
	
	//产品编码
	public static final String DP_PROD_CODE = "0001";
	
	public static final String PAY_PROD_CODE = "0017";    //支付产品编码
	
	public static final String RECHARGE_PROD_CODE = "0018";    //充值产品编码
	
	public static final String RECV_PROD_CODE = "0019";    //收款产品编码
	
	//业务编码
	public static final String DP_PAYMENT_ACTION_CODE = "0101";  //支付消费
	
	public static final String DP_E_CARD_ACTION_CODE = "0201";  //电子售卡
	
	public static final String EPAY_ACITON_CODE = "0501";    //翼支付账户支付业务编码
	
	public static final String MTPAY_ACITON_CODE = "0502";    //手机收银台业务编码
	
	public static final String HB_POINT_ACITON_CODE = "0503";    //号百积分支付业务编码
	
	public static final String CASH_PAY_ACITON_CODE = "0504";    //现金充值业务编码
	
	public static final String WAP_PAY_ACITON_CODE = "0505";    //WAP网银支付业务编码
	
	public static final String WAP_RECHARGE_ACITON_CODE = "0506";    //WAP网银充值业务编码
	
	public static final String PAY11888_ACITON_CODE = "0507";    //11888充值业务编码
	
	public static final String LIFE_PAY_ACITON_CODE = "0508";    //生活缴费业务编码
	
	public static final String MB_RECHARGE_ACITON_CODE = "0509";    //手机充值业务编码
	
	public static final String CP_CHARGE_ACITON_CODE = "0510";    //彩票返奖业务编码
	
	public static final String WEG_ACTION_CODE = "0511"; //水电煤业务编码
	
	public static final String EPAY_CARD_ACTION_CODE = "0512"; //天翼支付卡支付业务编码
	
	public static final String EPAY_WEG_ACTION_CODE = "0513"; //融合支付平台水电煤销帐
	
	public static final String EPAY_RECHARGE_CARD_ACTION_CODE = "0514"; //天翼支付卡充值业务编码
	
	public static final String CDS_RECHARGE_ACTION_CODE = "0515"; //银行划扣充值
	
	//平台编码	
	public static final String  PT_DP_E_CARD_CODE = "PT0000";   //电子售卡
	
    public static final String  PT_HB_WAP_CODE = "PT0001";   //号百网关平台编码
	
	public static final String  PT_EPAY_CODE = "PT0002";     //全国融合支付平台编码
	
	public static final String  PT_HB_POINT_CODE = "PT0003"; //号百积分平台编码
	
	public static final String  PT_DP_PAYMENT_CODE = "PT0004";   //预付费卡平台支付
	
	
	//支付机构
	public static final String DP_PAYMENT_AGENT_NO = "8811000000000011";
}
