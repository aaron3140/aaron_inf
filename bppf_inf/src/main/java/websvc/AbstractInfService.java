package websvc;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.service.RegisterManger;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.Charset;
import common.xml.CommonReqAbs;
import common.xml.CommonRespAbs;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public abstract class AbstractInfService<M extends CommonReqAbs> {

	public final Log log = LogFactory.getLog(this.getFeaturedClass());

	public Map<String, Object> respBody = new HashMap<String, Object>();

	/**
	 * 接收子类具体
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected abstract Class getFeaturedClass();

	/**
	 * 通过请求参数封装为子类请求POJO对象
	 * 
	 * @param in1
	 * @return
	 * @throws INFException
	 * @throws Exception
	 */
	protected abstract M preHandle(String in1) throws INFException, Exception;

	/**
	 * 将具体子类业务信息传递到respInfo的返回对象中
	 * @throws INFException 
	 */
	protected abstract void Handle() throws DataAccessException, INFException;

	/**
	 * 统一在接口分发时处理的所有业务
	 * 
	 * @param in1
	 *            xml格式请求数据
	 * 
	 * @return
	 */
//	@RequestMapping(method = RequestMethod.POST)
	public String execute(String in1) {
		RespInfo respInfo = null;
		Long pid = 0L;
		Long oid = 0L;
		INFLogID infId = new INFLogID(getFeaturedClass().getName(),
				TInfDcoperlogDao.PARTY_GROUP_AG);
		RegisterManger manger = new RegisterManger();

		try {
			/*
			 * 初始化值
			 */
			 preHandle(in1);
			
			respInfo = new RespInfo(in1, "20");
			
			/*
			 * 具体子类业务处理
			 */
			Handle();

			if (StringUtils.isEmpty(respInfo.getResponseCode())) {
				respInfo.setResponseCode("000000");
				respInfo.setResponseContent(" 成功");
			}
			
			respInfo.setRespBody(respBody);
			String oXml = CommonRespAbs.toXMLStr(respInfo);

			return ResponseVerifyAdder.pkgForMD5(oXml);
		} catch (XmlINFException spe) {
			manger.delCusInfo(pid.toString(), oid.toString());
			spe.setRespInfo(respInfo);
			String oXml = ExceptionHandler.toXML(spe, infId);
			return ResponseVerifyAdder.pkgForMD5(oXml);
		} catch (DataAccessException e) {
			log.error("前置服务端数据库访问异常", e);
			manger.delCusInfo(pid.toString(), oid.toString());
			respInfo.setResponseCode("304102");
			respInfo.setResponseContent("	前置服务端数据库访问异常");
			String oXml = CommonRespAbs.toXMLStr(respInfo);
			return ResponseVerifyAdder.pkgForMD5(oXml);
		} catch (Exception e) {
			manger.delCusInfo(pid.toString(), oid.toString());
			String oXml = ExceptionHandler.toXML(new XmlINFException(e,
					respInfo), infId);
			return ResponseVerifyAdder.pkgForMD5(oXml);
		}
	}

	protected void checkCustCode(M request) throws INFException {
		// 校验客户编码和用户名是否匹配
		TCumInfoDao cumInfoDao = new TCumInfoDao();
		String custCodeByStaff = cumInfoDao.getCustCodeByStaff(request
				.getStaffCode());
		if (Charset.isEmpty(custCodeByStaff, true)) {
			throw new INFException("019999", "用户名不存在");
		}
		String custCode = request.getCustCode();
		if (!Charset.isEmpty(custCode, true)) {
			if (!StringUtils.equals(custCodeByStaff, custCode)) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST,
						INFErrorDef.CUSTCODE_NOT_MATCH_STAFF_DESC);
			}
		} else {
			request.setCustCode(custCodeByStaff);
		}
	}

	protected void insertInfOperInLog(M request) throws INFException {
		TInfOperInLog tInfOperInLog;
		TInfOperInLogManager man = new TInfOperInLogManager();
		SagManager sagManager = new SagManager();
		// 插入信息到入站日志表
		tInfOperInLog = sagManager.insertTInfOperInLog(request.getKeep(),
				request.getIp(), request.getTmnNum(), getFeaturedClass()
						.getSimpleName(), "XML", "", "", "", "", "S0A");
		// 判断插入是否成功
		if (tInfOperInLog != null) {

			boolean flag = man.selectTInfOperInLogByKeep(request.getKeep());
			// 判断流水号是否可用
			if (flag) {
				flag = man.updateAllow(tInfOperInLog.getOperInId(), 1);

				throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
						INFErrorDef.POSSEQNO_CONFLICT_REASON);
			} else {

				flag = man.updateAllow(tInfOperInLog.getOperInId(), 0);
			}
		}
	}

	protected void verifyByMD5(CommonReqAbs request, String in1) throws Exception {
		// 客户端MD5校验--------------------------------------------
		String tokenValidTime = TSymSysParamDao.getTokenValidTime();
		String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(
				request.getStaffCode(), tokenValidTime);
//		request.verifyByMD5(request.getCer(),md5Key);
		TInfLoginLogDao.updateRanduseTimeByStaffCode(request.getStaffCode());
	}

}