package websvc.servant;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf01013Request;
import common.xml.dp.DpInf01013Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 
 * 本类描述: 实时解签接口
 * 
 * @version: 企业帐户前置接口 v1.0
 * @author: 广州天讯瑞达通讯技术有限公司(zhuxiaojun)  
 * @email: zhuxiaojun@tisson.com
 * @time: 2013-3-4上午11:02:19
 */
public class INF01013 {
	private static final Log log = LogFactory.getLog(INF01013.class);
	public static String svcInfName = "INF01013";

	public static String execute(String in0, String in1) {
		// TODO Auto-generated method stub
		DpInf01013Request dpRequest = null;
		DpInf01013Response response = new DpInf01013Response();
		RespInfo respInfo = null;				// 返回信息头
		String tmnNum = null;  
		String orderSeq = null;  
		
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode = "";
		
		String tempStr = "";
		String tempVal = "";
		String keep = "";//获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		INFLogID infId = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try {
			respInfo = new RespInfo(in1, "20");				// 返回信息头
			dpRequest = new DpInf01013Request(in1);
			tmnNum = dpRequest.getTmnNum();
			orderSeq = dpRequest.getOrderSeq();
			keep = dpRequest.getKeep();
			ip = dpRequest.getIp();
			
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "ORDERSEQ"
					, orderSeq, "", "", "S0A");
			TInfOperInLogManager manager = new TInfOperInLogManager();
			//判断插入是否成功
			if(tInfOperInLog!=null){
				boolean flag = manager.selectTInfOperInLogByKeep(keep);
				//判断流水号是否可用
				if(flag){
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);
					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
							INFErrorDef.POSSEQNO_CONFLICT_REASON);
				}else{
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}
			//权限判断
			boolean flag = false;
			List privList = PayCompetenceManage.payFunc(dpRequest.getCustCode(), dpRequest.getChannelCode());
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();
				if("ws_ColNPay_UnSign".equals(str)){
					flag = true;
					break;
				}
			}
			if (!flag) {
				throw new Exception("没有实时解签交易权限");
			}
			
			if(dpRequest.getBankAcct()==null||dpRequest.getBankAcct().equals(""))
				throw new Exception("很抱歉，系统暂时还不支持外部ID解除签约,请输入银行账号");
			TCumInfoDao infoDao = new TCumInfoDao();
			String netCode = infoDao.getCustCodeByContractId(dpRequest.getContractId());  //网点客户编码
			if(netCode == null){
				throw new Exception("你输入的签约ID不正确");
			}
		
			String orgMerId = infoDao.getOrgMerIdFromCustCode(dpRequest.getCustCode());
			if (!dpRequest.getMerId().equals(orgMerId)) {
				throw new Exception("客户编码不是该接入机构下属");
			}
			String custCode = infoDao.getCustCodeBySignId(dpRequest.getContractId());
			if(custCode!=null&&!custCode.equals(dpRequest.getCustCode()))
				throw new Exception("客户编码与签约ID不一致");
			
			PackageDataSet ps = liftSign(dpRequest,netCode);
			String resultCode = (String) ps.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			//返回结果为失败时，抛出异常
			if(Long.valueOf(resultCode) != 0) {
				String resultMsg = (String) ps.getParamByID("0002", "000").get(0);
				throw new Exception(resultMsg);
			}
			
//			String str = infoDao.getCustCodeFromDSF(netCode);    //代理商客户编码
//			if(str!=null)
//			custCode =str;
			responseCode = ps.getByID("0001", "000");// 获取接口的000组的0001参数
			String responseDesc = ps.getByID("0002", "000");// 获取接口的000组的0002参数
			return response.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode,responseDesc,
					dpRequest.getOrderSeq(), custCode,dpRequest.getContractId(),dpRequest.getExternalId(),
					dpRequest.getBankAcct(),"",dpRequest.getRemark1(),dpRequest.getRemark2());

		} catch (XmlINFException spe) {
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, infId);
		} catch (Exception e) {
			return ExceptionHandler.toXML(
					new XmlINFException(response, e, respInfo), infId);
		}
	}

	private static PackageDataSet liftSign(DpInf01013Request dpRequest, String netCode) throws Exception {
		IParamGroup g218 = new ParamGroupImpl("218");
		g218.put("2002", netCode);
		g218.put("2149", dpRequest.getContractId());
		g218.put("2159", dpRequest.getBankAcct());
		g218.endRow();
		
		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", dpRequest.getChannelCode());
		g211.put("2077", dpRequest.getTmnNum());
		g211.put("2078", dpRequest.getCustCode());
		g211.endRow();
		
		IServiceCall caller = new ServiceCallImpl();
		return caller.call("BIS","CUM0015",g218,g211);

	}
}
