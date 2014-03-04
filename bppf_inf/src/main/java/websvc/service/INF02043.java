package websvc.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import websvc.AbstractInfService;
import websvc.InfService;

import common.dao.bis.BapReadRecDao;
import common.dao.bis.StaffDao;
import common.utils.HtmlUtil;
import common.xml.dp.INF02043Request;

import framework.exception.INFException;

/**
 * 消息管理 --消息详情 消息管理 --查询消息 客户端系统向翼支付企业账户平台发起消息管理请求 接口使用者 客户端系统 应用场景
 * 手机客户端，发起的消息、公告删除，已阅，浏览次数管理
 * 
 * @author aaronMing
 * 
 */
@Service
public class INF02043 extends AbstractInfService<INF02043Request> implements
		InfService {

	@Autowired
	private BapReadRecDao bapReadRecDao;

	@Autowired
	private StaffDao staffDao;

	private INF02043Request request;

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeaturedClass() {
		return this.getClass();
	}

	@Override
	protected INF02043Request preHandle(String in1) throws Exception {
		request = new INF02043Request(in1);
		this.verifyByMD5(request, in1);
		this.insertInfOperInLog(request);
		String issueId = request.getIssueId();
		int validIssueId = bapReadRecDao.countStaffRecByIssueId(issueId);
		if (validIssueId == 0) {
			String errCode = "304101";
			String errReason = "消息ID不存在";
			throw new INFException(errCode, errReason);
		}
		this.checkCustCode(request);
		request.setStaffId(staffDao.getIdByCode(request.getStaffCode()));
		return request;

	}

	@Override
	protected void Handle() {
		String issueId = request.getIssueId();
		// 详情读取
		String recContent = bapReadRecDao.selectIssueContent(issueId);
		recContent = HtmlUtil.convertToValidHtmlForJson(recContent);
		// 已阅操作
		String staffId = request.getStaffId();
		String issueType = request.getIssueType();
		Map<String, Object> readRec = new HashMap<String, Object>();
		readRec.put("readType", issueType);
		readRec.put("staffId", staffId);
		readRec.put("issueId", issueId);
		bapReadRecDao.insertReadRec(readRec);

		respBody.put("CONTENTS", recContent);
	}

}
