package framework.exception;

import java.net.ConnectException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.mortbay.log.Log;

import common.dao.TInfConsumeDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfErrorCodeDao;
import common.dao.TInfOrderBusCfgDao;
import common.dao.TSymErrorDao;
import common.dao.TbisTanOrderDao;
import common.entity.SymError;
import common.entity.TInfErrorCode;
import common.platform.invoker.exception.ServiceInvokeException;
import common.platform.provider.client.SocketSend;
import common.utils.Charset;
import common.utils.ErrorProcess;
import common.utils.JSONTool;
import common.utils.OrderConstant;
import common.utils.TInfErrorCodeUtil;
import common.xml.CommonRespAbs;
import common.xml.RespInfo;
import common.xml.XmlLoserIntf;

import framework.config.GlobalConstants;

public class ExceptionHandler {
	private static final Logger LOG = Logger.getLogger(ExceptionHandler.class);

	public static final String GWM_SPP_ERRCODE = "019999";

	public static final String GWM_MTP_ERRCODE = "019999";
	
	public static final String GWM_NET_ERRCODE = "011007";

	public static String toJson(Exception e, Long pk) {
		return toJson(e, pk, null);
	}

	public static String toJson(Exception e, Long pk, String moduleCode) {
		e.printStackTrace();
		LOG.error(e);
		String code = "";
		String reason = "";

		// 二阶段，异常返回ERRORCODE ERRORMSG SIG
		String[] jsonNames2 = { "ERRORCODE", "ERRORMSG", "SIG" };

		String[] jsonValues = new String[jsonNames2.length];
		for (int i = 0; i < jsonValues.length; i++)
			jsonValues[i] = "";

		if (e instanceof ServiceInvokeException) {
			ServiceInvokeException sie = (ServiceInvokeException) e;

			if (sie.getDataSet() != null) {
				code = sie.getDataSet().getByID("0001", "000");
				reason = sie.getDataSet().getByID("0002", "000");
				if(ErrorProcess.isTimeOut(code)){//超时统一转错误码
				    reason = getTimeOutReason(sie);
					code = GWM_NET_ERRCODE;
				}
				// 调用GWM或者交易平台的异常，查找T_SYM_ERROR表进行错误码转换
				if (moduleCode != null && code != null) {
					SymError symErr = TSymErrorDao
							.getErrorOut(code, moduleCode);
					if (symErr != null && !Charset.isEmpty(symErr.getCodeOut())) {
						code = symErr.getCodeOut();
						reason = symErr.getInfoOut();
					}
				}

			} else {
				reason = sie.getMessage();
				code = GWM_NET_ERRCODE;
			}
		} else if (e instanceof INFException) {
			INFException infe = (INFException) e;
			code = infe.getErrCode();
			reason = infe.getErrReason();

			//
			// INF内部异常，查找T_SYM_ERROR表进行错误码转换
			if (code != null) {
				SymError symErr = TSymErrorDao.getErrorOut(code,
						GlobalConstants.MODULE_CODE_MEPF);
				if (symErr != null && !Charset.isEmpty(symErr.getCodeOut())) {
					code = symErr.getCodeOut();
					reason = symErr.getInfoOut();
				}
			}

		} else if (e instanceof ConnectException) {
			code = GWM_SPP_ERRCODE;
			reason = SocketSend.CONNECT_ERR;
		}
		else {
			code = GWM_MTP_ERRCODE;
			reason = e.getMessage();
		}
		

		// 更新日志表
		if (pk != null)
			TInfDcoperlogDao.update(pk, code, reason);
		
		
		// 从内存中获取数据
		HashMap<String, TInfErrorCode> map = TInfErrorCodeUtil.map;
		if (map == null) {
			TInfErrorCodeDao dao = new TInfErrorCodeDao();
			TInfErrorCodeUtil.map = dao.getErrorMap();
			map = TInfErrorCodeUtil.map;
		}
		TInfErrorCode tInfErrorCode = map.get(code);
		if (tInfErrorCode != null) {
			String errorInfo = tInfErrorCode.getErrorInfo();
			if (errorInfo != null && !errorInfo.equals("")) {
				reason = errorInfo;
			}
		}
		
		jsonValues[0] = code;
		jsonValues[1] = reason;

		return JSONTool.createJson(jsonNames2, jsonValues);
	}

	public static String toXML(XmlINFException spe, INFLogID id) {
		return toXML(spe, id, null);
	}

	public static String toXML(XmlINFException spe, INFLogID id, String moduleCode) {
		LOG.error(spe);

		Exception relateException = spe.getRelateException();
		
		String reqWebsvrCode = "";
		String respType = "";
		String keep = "";
		String result = "FAIL";
		
		RespInfo respInfo = spe.getRespInfo();
		if(respInfo != null) {
			reqWebsvrCode = respInfo.getReqWebsvrCode();
			respType = respInfo.getRespType();
			keep = respInfo.getKeep();
		}
		
		String code = "";
		String reason = "";

		if (relateException != null) {

			if (relateException instanceof ServiceInvokeException) {
				ServiceInvokeException sie = (ServiceInvokeException) relateException;

				if (sie.getDataSet() != null) {
					code = sie.getDataSet().getByID("0001", "000");
					reason = sie.getDataSet().getByID("0002", "000");
					if(ErrorProcess.isTimeOut(code)){//超时统一转错误码
					    reason = getTimeOutReason(sie);
						code = GWM_NET_ERRCODE;
					}
					// 调用GWM或者交易平台的异常，查找T_SYM_ERROR表进行错误码转换
					if (moduleCode != null && code != null) {
						SymError symErr = TSymErrorDao.getErrorOut(code,
								moduleCode);
						if (symErr != null && !Charset.isEmpty(symErr.getCodeOut())) {
							code = symErr.getCodeOut();
							reason = symErr.getInfoOut();
						}
					}

				} else {
					reason = sie.getMessage();
					code = GWM_NET_ERRCODE;
				}
			} else if (relateException instanceof ConnectException) {
				code = GWM_SPP_ERRCODE;
				reason = SocketSend.CONNECT_ERR;
			}
			else if (relateException instanceof INFException) {
				INFException infe = (INFException) relateException;
				code = infe.getErrCode();
				reason = infe.getErrReason();

				// INF内部异常，查找T_SYM_ERROR表进行错误码转换
				if (code != null) {
					SymError symErr = TSymErrorDao.getErrorOut(code,
							GlobalConstants.MODULE_CODE_MEPF);
					if (symErr != null && !Charset.isEmpty(symErr.getCodeOut())) {
						code = symErr.getCodeOut();
						reason = symErr.getInfoOut();
					}
				}

			} else {
				code = GWM_SPP_ERRCODE;
				reason = relateException.getMessage();
			}

		} else {// if (relateException != null) {
			code = GWM_SPP_ERRCODE;
			reason = spe.getMessage();
		}
		
		try{
		// 更新日志表
		if (id != null)
			TInfDcoperlogDao.saveOrUpdate(id, code, reason);
		}catch (Exception e){
			Log.info("数据库操作  exception");
			reason = reason+" "+e;
		}
		XmlLoserIntf req = spe.getXmlResp();
		if (req != null) {
			code = CommonRespAbs.newCode(code);
		}
		return new CommonRespAbs().toCommonXmlStr(reqWebsvrCode, respType, keep, result, code, reason);
	}
	
	/**
	 * 异常时外部订单号控制状态修改
	 * @param spe
	 * @param id
	 * @param moduleCode
	 * @param isupdate
	 * @param tmnNum
	 * @param orderseq
	 * @return
	 */
	public static String toTanXML(XmlINFException spe, INFLogID id,TbisTanOrderDao tranDao) {
		LOG.error(spe);

		Exception relateException = spe.getRelateException();
		
		String reqWebsvrCode = "";
		String respType = "";  
		String keep = "";
		String result = "FAIL";
		
		RespInfo respInfo = spe.getRespInfo();
		if(respInfo != null) {
			reqWebsvrCode = respInfo.getReqWebsvrCode();
			respType = respInfo.getRespType();
			keep = respInfo.getKeep();
		}
		
		String code = "";
		String reason = "";

		if (relateException != null) {

			if (relateException instanceof ServiceInvokeException) {
				ServiceInvokeException sie = (ServiceInvokeException) relateException;

				if (sie.getDataSet() != null) {
					code = sie.getDataSet().getByID("0001", "000");
					reason = sie.getDataSet().getByID("0002", "000");
					//更新转账状态
					if(Long.valueOf(code) != 0){
						
						tranDao.updateTraOrderS0F(keep,code,reason);
					}
					if(ErrorProcess.isTimeOut(code)){
						
						tranDao.updateTraOrderS0P(keep);
						
						reason = getTimeOutReason(sie);
						code = GWM_NET_ERRCODE;
					}
					// 调用GWM或者交易平台的异常，查找T_SYM_ERROR表进行错误码转换
//					if (moduleCode != null && code != null) {
//						SymError symErr = TSymErrorDao.getErrorOut(code,
//								moduleCode);   
//						if (symErr != null && !Charset.isEmpty(symErr.getCodeOut())) {
//							code = symErr.getCodeOut();
//							reason = symErr.getInfoOut();
//						}
//					}

				} else {
					reason = sie.getMessage();
					code = GWM_NET_ERRCODE;
				}
			}  
			else if (relateException instanceof ConnectException) {
			code = GWM_SPP_ERRCODE;
			reason = SocketSend.CONNECT_ERR;
		     }
			else if (relateException instanceof INFException) {
				INFException infe = (INFException) relateException;
				code = infe.getErrCode();
				reason = infe.getErrReason();

				// INF内部异常，查找T_SYM_ERROR表进行错误码转换
				if (code != null) {
					SymError symErr = TSymErrorDao.getErrorOut(code,
							GlobalConstants.MODULE_CODE_MEPF);
					if (symErr != null && !Charset.isEmpty(symErr.getCodeOut())) {
						code = symErr.getCodeOut();
						reason = symErr.getInfoOut();
					}
				}

			} else {
				code = GWM_SPP_ERRCODE;
				reason = relateException.getMessage();
			}

		} else {// if (relateException != null) {
			code = GWM_SPP_ERRCODE;
			reason = spe.getMessage();
		}
		
		try{
		// 更新日志表
		if (id != null)
			TInfDcoperlogDao.saveOrUpdate(id, code, reason);
		}catch (Exception e){
			Log.info("数据库操作  exception");
			reason = reason+" "+e;
		}
		XmlLoserIntf req = spe.getXmlResp();
		if (req != null) {
			code = CommonRespAbs.newCode(code);
		}
		return new CommonRespAbs().toCommonXmlStr(reqWebsvrCode, respType, keep, result, code, reason);
	}
	
	/**
	 * 异常时外部订单号控制状态修改
	 * @param spe
	 * @param id
	 * @param moduleCode
	 * @param isupdate
	 * @param tmnNum
	 * @param orderseq
	 * @return
	 */
	public static String toOutOrderXML(XmlINFException spe, INFLogID id, String moduleCode,boolean isupdate,String tmnNum,String orderseq,TbisTanOrderDao tranDao,boolean isTransFlag) {
		LOG.error(spe);

		Exception relateException = spe.getRelateException();
		
		String reqWebsvrCode = "";
		String respType = "";
		String keep = "";
		String result = "FAIL";
		
		RespInfo respInfo = spe.getRespInfo();
		if(respInfo != null) {
			reqWebsvrCode = respInfo.getReqWebsvrCode();
			respType = respInfo.getRespType();
			keep = respInfo.getKeep();
		}
		
		String code = "";
		String reason = "";

		if (relateException != null) {

			if (relateException instanceof ServiceInvokeException) {
				ServiceInvokeException sie = (ServiceInvokeException) relateException;

				if (sie.getDataSet() != null) {
					code = sie.getDataSet().getByID("0001", "000");
					reason = sie.getDataSet().getByID("0002", "000");
					if(tranDao!=null){
		        		//转账预处理
						if(isTransFlag){
							
							LOG.info("2..支付失败 更新");
							tranDao.updateRecStat(keep,code,reason);
						}
		        	}
					//更新订单控制状态
					if(isupdate&&!ErrorProcess.isTimeOut(code)){//失败
						TInfOrderBusCfgDao cfgDao = new TInfOrderBusCfgDao();
			        	cfgDao.updateTInfOrderStat(tmnNum, orderseq, OrderConstant.S0F);
					}else if(ErrorProcess.isTimeOut(code)){
					    reason = getTimeOutReason(sie);
						code = GWM_NET_ERRCODE;
					}
					// 调用GWM或者交易平台的异常，查找T_SYM_ERROR表进行错误码转换
					if (moduleCode != null && code != null) {
						SymError symErr = TSymErrorDao.getErrorOut(code,
								moduleCode);
						if (symErr != null && !Charset.isEmpty(symErr.getCodeOut())) {
							code = symErr.getCodeOut();
							reason = symErr.getInfoOut();
						}
					}

				} else {
					reason = sie.getMessage();
					code = GWM_NET_ERRCODE;
				}
			} else if (relateException instanceof INFException) {
				INFException infe = (INFException) relateException;
				code = infe.getErrCode();
				reason = infe.getErrReason();

				// INF内部异常，查找T_SYM_ERROR表进行错误码转换
				if (code != null) {
					SymError symErr = TSymErrorDao.getErrorOut(code,
							GlobalConstants.MODULE_CODE_MEPF);
					if (symErr != null && !Charset.isEmpty(symErr.getCodeOut())) {
						code = symErr.getCodeOut();
						reason = symErr.getInfoOut();
					}
				}

			} else if (relateException instanceof ConnectException) {
				code = GWM_SPP_ERRCODE;
				reason = SocketSend.CONNECT_ERR;
			}
			else {
				code = GWM_SPP_ERRCODE;
				reason = relateException.getMessage();
			}

		} else {// if (relateException != null) {
			code = GWM_SPP_ERRCODE;
			reason = spe.getMessage();
		}
		
		try{
		// 更新日志表
		if (id != null)
			TInfDcoperlogDao.saveOrUpdate(id, code, reason);
		}catch (Exception e){
			Log.info("数据库操作  exception");
			reason = reason+" "+e;
		}
		XmlLoserIntf req = spe.getXmlResp();
		if (req != null) {
			code = CommonRespAbs.newCode(code);
		}
		return new CommonRespAbs().toCommonXmlStr(reqWebsvrCode, respType, keep, result, code, reason);
	}
	public static String toConsumeXML(XmlINFException spe, INFLogID id, Long consumeId) {
		LOG.error(spe);

		TInfConsumeDao dao = new TInfConsumeDao();
		
		Exception relateException = spe.getRelateException();
		
		String reqWebsvrCode = "";
		String respType = "";
		String keep = "";
		String result = "FAIL";
		
		RespInfo respInfo = spe.getRespInfo();
		if(respInfo != null) {
			reqWebsvrCode = respInfo.getReqWebsvrCode();
			respType = respInfo.getRespType();
			keep = respInfo.getKeep();
		}
		
		String code = "";
		String reason = "";

		if (relateException != null) {

			if (relateException instanceof ServiceInvokeException) {
				ServiceInvokeException sie = (ServiceInvokeException) relateException;

				if (sie.getDataSet() != null) {
					code = sie.getDataSet().getByID("0001", "000");
					reason = sie.getDataSet().getByID("0002", "000");
					
					if (ErrorProcess.isTimeOut(code)) {

						dao.updateOrderStatE(consumeId, OrderConstant.S0P);
						reason = getTimeOutReason(sie);
						code = GWM_NET_ERRCODE;
					} /*else if(code.equals(ErrorProcess.PASSERROR)){
						String num = sie.getDataSet().getByID("6047", "601");
						reason = INFErrorDef.PAY_PWD_FAULT_DESC+num+"次";
						code = INFErrorDef.PAY_PWD_FAULT;
					}*/
					else{

						dao.updateOrderStatE(consumeId, OrderConstant.S0F);
					}
					
					// 调用GWM或者交易平台的异常，查找T_SYM_ERROR表进行错误码转换
//					if (moduleCode != null && code != null) {
//						SymError symErr = TSymErrorDao.getErrorOut(code,
//								moduleCode);
//						if (symErr != null && !Charset.isEmpty(symErr.getCodeOut())) {
//							code = symErr.getCodeOut();
//							reason = symErr.getInfoOut();
//						}
//					}

				} else {
					reason = sie.getMessage();
					code = GWM_NET_ERRCODE;
				}
			} else if (relateException instanceof INFException) {
				INFException infe = (INFException) relateException;
				code = infe.getErrCode();
				reason = infe.getErrReason();

				// INF内部异常，查找T_SYM_ERROR表进行错误码转换
				if (code != null) {
					SymError symErr = TSymErrorDao.getErrorOut(code,
							GlobalConstants.MODULE_CODE_MEPF);
					if (symErr != null && !Charset.isEmpty(symErr.getCodeOut())) {
						code = symErr.getCodeOut();
						reason = symErr.getInfoOut();
					}
				}

			}  else if (relateException instanceof ConnectException) {
				code = GWM_SPP_ERRCODE;
				reason = SocketSend.CONNECT_ERR;
			}
			else {
				code = GWM_SPP_ERRCODE;
				reason = relateException.getMessage();
			}

		} else {// if (relateException != null) {
			code = GWM_SPP_ERRCODE;
			reason = spe.getMessage();
		}
		
		try{
		// 更新日志表
		if (id != null)
			TInfDcoperlogDao.saveOrUpdate(id, code, reason);
		}catch (Exception e){
			Log.info("数据库操作  exception");
			reason = reason+" "+e;
		}
		XmlLoserIntf req = spe.getXmlResp();
		if (req != null) {
			code = CommonRespAbs.newCode(code);
		}
		return new CommonRespAbs().toCommonXmlStr(reqWebsvrCode, respType, keep, result, code, reason);
	}

	public static String toXMLERROR(XmlINFException spe, INFLogID id) {
		LOG.error(spe);

		Exception relateException = spe.getRelateException();
		
		String reqWebsvrCode = "";
		String respType = "";
		String keep = "";
		String result = "FAIL";
		
		RespInfo respInfo = spe.getRespInfo();
		if(respInfo != null) {
			reqWebsvrCode = respInfo.getReqWebsvrCode();
			respType = respInfo.getRespType();
			keep = respInfo.getKeep();
		}
		
		String code = "";
		String reason = "";

		if (relateException != null) {

			if (relateException instanceof ServiceInvokeException) {
				ServiceInvokeException sie = (ServiceInvokeException) relateException;

				if (sie.getDataSet() != null) {
					code = sie.getDataSet().getByID("0001", "000");
					reason = sie.getDataSet().getByID("0002", "000");
					
					if(INFErrorDef.ROUTER_ERROR_CODE.equals(code)){
						reason = INFErrorDef.ROUTER_ERROR_DESC;
					}
					if(ErrorProcess.isTimeOut(code)){//超时统一转错误码
					    reason = getTimeOutReason(sie);
						code = GWM_NET_ERRCODE;
					}
					// 调用GWM或者交易平台的异常，查找T_SYM_ERROR表进行错误码转换
//					if (moduleCode != null && code != null) {
//						SymError symErr = TSymErrorDao.getErrorOut(code,
//								moduleCode);
//						if (symErr != null && !Charset.isEmpty(symErr.getCodeOut())) {
//							code = symErr.getCodeOut();
//							reason = symErr.getInfoOut();
//						}
//					}

				} else {
					reason = sie.getMessage();
					code = GWM_NET_ERRCODE;
				}

			} else if (relateException instanceof INFException) {
				INFException infe = (INFException) relateException;
				code = infe.getErrCode();
				reason = infe.getErrReason();

				// INF内部异常，查找T_SYM_ERROR表进行错误码转换
				if (code != null) {
					SymError symErr = TSymErrorDao.getErrorOut(code,
							GlobalConstants.MODULE_CODE_MEPF);
					if (symErr != null && !Charset.isEmpty(symErr.getCodeOut())) {
						code = symErr.getCodeOut();
						reason = symErr.getInfoOut();
					}
				}

			} else if (relateException instanceof ConnectException) {
				code = GWM_SPP_ERRCODE;
				reason = SocketSend.CONNECT_ERR;
			}
			else {
				code = GWM_SPP_ERRCODE;
				reason = relateException.getMessage();
			}

		} else {// if (relateException != null) {
			code = GWM_SPP_ERRCODE;
			reason = spe.getMessage();
		}
		
		try{
		// 更新日志表
		if (id != null)
			TInfDcoperlogDao.saveOrUpdate(id, code, reason);
		}catch (Exception e){
			Log.info("数据库操作  exception");
			reason = reason+" "+e;
		}
		XmlLoserIntf req = spe.getXmlResp();
		if (req != null) {
			code = CommonRespAbs.newCode(code);
		}
		return new CommonRespAbs().toCommonXmlStr(reqWebsvrCode, respType, keep, result, code, reason);
	}

	public static String getTimeOutReason(ServiceInvokeException sie){
	    String reason = INFErrorDef.GWM_NET_ERRCODE_DESC;
        if(sie.getMessage()!=null&&sie.getMessage().length()>0)
            reason = INFErrorDef.GWM_NET_ERRCODE_DESC + "("+sie.getMessage()+")";
        return reason;
	}
}
