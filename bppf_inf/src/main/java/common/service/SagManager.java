package common.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import common.algorithm.MD5;
import common.dao.TItfGuaranteetaskDao;
import common.entity.ParamSAG0002;
import common.entity.ParamSAG0006;
import common.entity.TInfOperInLog;
import common.entity.TInfOperOutLog;
import common.entity.TItfGuaranteetask;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.utils.DateTime;
import common.xml.CommonReqAbs;

public class SagManager {
	
	/**
	 * 调用SAG0002,业务网关查询
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public PackageDataSet createSAG0002(ParamSAG0002 params) throws Exception {
		
		//包头控制信息
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", params.getServCode());//服务编码������� ��
		g675.put("6752", params.getChannelCode());//渠道号�
		g675.put("6753", params.getTradeSeq());//流水号��ˮ��
		g675.put("6754", DateTime.nowDate8Bit());// 发起请求日期格式YYYYMMDD
		g675.put("6755", DateTime.nowTime6Bit());// 发起请求时间格式HH24MISS
		g675.put("6756", params.getInfCode());//接口平台编码
		
		//业务基础信息
		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", params.getActionCode());//业务编码
		g676.put("6762", params.getProdCode());//产品编码
		g676.put("6763", params.getReceiverCode());//受理区域编码
		g676.put("6764", params.getMerid());//前向商户编码
		g676.put("6765", params.getTnmNum());//前向商户终端号
		
		//鉴权基本信息
		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", params.getObjCode());//用户标识
		g680.put("6802", params.getObjType());// 用户标识类型
		g680.put("6803", params.getOrgCode());//鉴权单位代码
		
		//鉴权附加信息
		IParamGroup g682 = new ParamGroupImpl("682");
		List itemList = params.getItemList();
		for (int i=0;i<itemList.size();i++) {
			g682.put("6820", "SAG_ITEM"+i);//鉴权项标识
			g682.put("6821", "");//鉴权项名称
			g682.put("6822", "01");//鉴权项数据类型
			g682.put("6823", String.valueOf(itemList.get(i)));//鉴权项内容
			g682.endRow();
		}
		
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet packageDataSet = caller.call("SAG", "SAG0002", g675,g676,g680,g682);
		
		return packageDataSet;
	}
	
	/**
	 * 调用SAG0006,业务网关回调
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public PackageDataSet createSAG0006(ParamSAG0006 params) throws Exception {
		
		//包头控制信息
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", params.getServCode());//服务编码������� ��
		g675.put("6752", params.getChannelCode());//渠道号�
		g675.put("6753", params.getTradeSeq());//流水号��ˮ��
		g675.put("6754", DateTime.nowDate8Bit());// 发起请求日期格式YYYYMMDD
		g675.put("6755", DateTime.nowTime6Bit());// 发起请求时间格式HH24MISS
		g675.put("6756", params.getInfCode());//接口平台编码
		
		//业务基础信息
		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", params.getActionCode());//业务编码
		g676.put("6762", params.getProdCode());//产品编码
		g676.put("6763", params.getReceiverCode());//受理区域编码
		g676.put("6764", "");//前向商户编码
		g676.put("6765", "");//前向商户终端号
		
		//鉴权基本信息
		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", params.getCallBackMsg());//回调报文
		
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet packageDataSet = caller.call("SAG", "SAG0006", g675,g676,g680);
		
		return packageDataSet;
	}
	
	
	/**
	 * 调用SCS0001接口
	 * @version: 1.00
	 * @history: 2012年6月14日 09:31:22 [created]
	 * @author WenChao chen 陈文超
	 * @param opertype
	 * @return
	 * @see
	 */
	public PackageDataSet callSCS0001(String keep, String custCode,String areaCode,String eventSeq,String tmnNum,String payTime,String channelCode,String termSeq
			,String objType,String payAmount,String objCode,String orgCode,String callBackURL,String acctCode,CommonReqAbs request)throws Exception {
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", custCode);// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		if(areaCode!=null && !"".equals(areaCode)){
			g401.put("4006", areaCode);// 所属区域编码
		}else{
			g401.put("4006", "000000");// 所属区域编码
		}
		g401.put("4007", tmnNum);// 受理终端号
		g401.put("4008", payTime);// 受理时间
//		g401.put("4012", "");// 订单备注
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", channelCode);// 渠道类型编码
		/* 外部订单号改成4028
		  g401.put("4017", orderSeq);// 终端流水号
		  	keep值放4017
		  */
		g401.put("4017", keep);// 终端流水号
		g401.put("4028", termSeq);// 终端流水号
		g401.put("4018", "0.0.0.0");// 操作原始来源
		
		g401.put("4284", request.getMerId());//机构编码     //20130628 wanght
		g401.endRow();
		
		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", payAmount);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", payAmount);// 订单应付金额
		g402.endRow();
		
		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", "02010001");// 业务编码
		g404.put("4052", objCode);// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.endRow();
		
		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", payAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", payAmount);// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();
		
		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", "02010001");// 业务编码
		g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
		g407.put("4088", "0200");//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", "02010001");// 业务编码
		g407.put("4087", "SCS_USERTYPE");// 业务属性编码
		g407.put("4088", objType);//属性值1
		g407.put("4080", "1");// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", "02010001");// 业务编码
		g407.put("4087", "SCS_COMPCODE");// 业务属性编码
		g407.put("4088", orgCode);//属性值1
		g407.put("4080", "1");// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", "02010001");// 业务编码
		g407.put("4087", "SCS_CALLBACKURL");// 业务属性编码
		g407.put("4088", callBackURL);//属性值1
		g407.put("4080", "1");// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", "02010001");// 业务编码
		g407.put("4087", "SCS_EVENTSEQ");// 业务属性编码
		g407.put("4088", eventSeq);// 属性值1
		g407.put("4080", "1");// 控制标识
		g407.endRow();
		
		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", "110810");// 支付机构编码
		g408.put("4099", "0001");// 账户类型编码
		g408.put("4101", acctCode);// 账号
//		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", payAmount);// 支付金额
		g408.endRow();
		
		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);		
		return dataSet;
	}
	
	/**
	 * 生成定时任务
	 * @param url
	 * @param param
	 * @param expectedValue
	 */
	public void makeTask(String url,String param,String expectedValue) {
		SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal=Calendar.getInstance();
		Calendar tomorrow=Calendar.getInstance();
		tomorrow.add(Calendar.DAY_OF_MONTH,1);
		tomorrow.set(Calendar.HOUR_OF_DAY,0);
		tomorrow.set(Calendar.MINUTE,0);
		tomorrow.set(Calendar.SECOND,0);
		tomorrow.set(Calendar.MILLISECOND,0);
		TItfGuaranteetask task = new TItfGuaranteetask();
		task.setUrl(url);
		task.setParam(param);
		task.setExpectedValue(expectedValue);
		task.setRuntimes(1);
		task.setCreateTime(sdf.format(cal.getTime()));
		task.setLastRuntime(sdf.format(cal.getTime()));
		
		task.setStat("S0A");
		cal.add(Calendar.MINUTE,1);
		task.setNextRuntime(sdf.format(cal.getTime()));
		task.setEndTime(sdf.format(tomorrow.getTime()));
		
		TItfGuaranteetaskDao dao = new TItfGuaranteetaskDao();
		dao.insert(task);
	}
	
	/**
	 * 对数据进行MD5匹配校验
	 * @version: 1.00
	 * @history:  2012-3-28 16:59:57 [created]
	 * @author guohong zhao
	 * @param batchCode
	 * @param tradeDirection 
	 * @param custCode 
	 * @return
	 * @see
	 */
	public boolean verify(String src ,String mac) {
		
		String result = MD5.getMD5(src.getBytes());
		if(mac ==null || mac.equals("")) {
			return false;
		}
		if(mac.equals(result)) {
			return true;
		}
		return false;
	}
	
	//插入信息到入站日志表
	public TInfOperInLog insertTInfOperInLog(String keep,String connectIp,String tmnNum,String svcCode,String inType
			,String objCode,String objValue,String objCode2,String objValue2,String stat){
		TInfOperInLog tInfOperInLog = new TInfOperInLog();
		TInfOperInLogManager tInfOperInLogManager = new TInfOperInLogManager();
		
		Long operInId = tInfOperInLogManager.getOperInId();
		
		tInfOperInLog.setOperInId(operInId);//日志标识
		tInfOperInLog.setKeep(keep);//操作流水号
		tInfOperInLog.setConnectIp(connectIp);//接入方IP
		tInfOperInLog.setTnmnum(tmnNum);//终端号
		tInfOperInLog.setSvcCode(svcCode);//服务编码
		tInfOperInLog.setInType(inType);//入站方式
		tInfOperInLog.setObjCode(objCode);//对象类型
		tInfOperInLog.setObjValue(objValue);//对象值
		tInfOperInLog.setObjCode2(objCode2);//对象类型
		tInfOperInLog.setObjValue2(objValue2);//对象值
		tInfOperInLog.setStat(stat);//状态
		
		boolean flag = tInfOperInLogManager.insert(tInfOperInLog);
		
		if(flag){
			return tInfOperInLog;
		}else{
			return null;
		}
	}
	
	//插入信息到出站日志表
	public boolean insertTInfOperOutLog(long operInId,String keep,String connectIp,String svcCode,String retCode,String retInfo,String stat){
		TInfOperOutLog tInfOperOutLog = new TInfOperOutLog();
		TInfOperOutLogManager tInfOperOutLogManager = new TInfOperOutLogManager();
		
		tInfOperOutLog.setOperInId(operInId);//入站日志标识
		tInfOperOutLog.setKeep(keep);//相应流水号
		tInfOperOutLog.setConnectIp(connectIp);//系统IP
		tInfOperOutLog.setSvcCode(svcCode);//服务编码
		tInfOperOutLog.setRetCode(retCode);//结果编码
		tInfOperOutLog.setRetInfo(retInfo);//结果说明
		tInfOperOutLog.setStat(stat);//状态
		
		return tInfOperOutLogManager.insert(tInfOperOutLog);
	}
}
