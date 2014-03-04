package websvc.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import websvc.AbstractInfService;
import websvc.InfService;

import common.dao.bis.BapReadRecDao;
import common.dao.bis.StaffDao;
import common.xml.dp.INF02041Request;

import framework.exception.INFException;

/**
 * 
 * 消息管理 --已阅vs删除 客户端系统向翼支付企业账户平台发起消息管理请求 接口使用者 客户端系统 应用场景
 * 手机客户端，发起的消息、公告删除，已阅，浏览次数管理
 * 
 * @author aaronMing
 * 
 */
@Service
//@Controller
public class INF02041 extends AbstractInfService<INF02041Request> implements
		InfService {

	// @Resource(name = "bapReadRecDao")
	@Autowired
	private BapReadRecDao bapReadRecDao;

	@Autowired
	private StaffDao staffDao; 
	
	private INF02041Request request;

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeaturedClass() {
		return this.getClass();
	}

	@Override
	protected INF02041Request preHandle(String in1) throws Exception {
		request = new INF02041Request(in1);
		this.verifyByMD5(request, in1);
		this.insertInfOperInLog(request);
//		bapReadRecDao = BapReadRecDao.getInstance();
//		staffDao = StaffDao.getInstance();
		String issueId = request.getIssueId();
		int validIssueId = bapReadRecDao.countStaffRecByIssueId(issueId);
		if (validIssueId == 0) {
			String errCode = "304101";
			String errReason = "消息ID不存在";
			throw new INFException(errCode, errReason);
		}
		request.setStaffId(staffDao.getIdByCode(request.getStaffCode()));
		this.checkCustCode(request);
		return request;
	}

	@Override
	protected void Handle() throws DataAccessException {
		String operType = request.getOperType();
		String issueId = request.getIssueId();
		String staffId = request.getStaffId();
		String issueType = request.getIssueType();

		// 已阅
		if (operType.equals("0")) {
			Map<String, Object> readRec = new HashMap<String, Object>();
			readRec.put("readType", issueType);
			readRec.put("staffId", staffId);
			readRec.put("issueId", issueId);
			//if (!bapReadRecDao.isExistsReadRecOfIssueId(issueId))
				bapReadRecDao.insertReadRec(readRec);
		} else if (operType.equals("1")) // 删除
		{
			
			bapReadRecDao.delReadRec(issueId, staffId);
		}
	}

}
