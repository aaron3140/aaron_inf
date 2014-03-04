package websvc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import websvc.AbstractInfService;
import websvc.InfService;

import common.dao.bis.BapReadRecDao;
import common.dao.bis.StaffDao;
import common.utils.HtmlUtil;
import common.xml.dp.INF02042Request;

import framework.exception.INFErrorDef;
import framework.exception.INFException;

/**
 * 消息管理 --查询消息 客户端系统向翼支付企业账户平台发起消息管理请求 接口使用者 客户端系统 应用场景
 * 手机客户端，发起的消息、公告删除，已阅，浏览次数管理
 * 
 * @author aaronMing
 * 
 */
@Transactional
@Service
public class INF02042 extends AbstractInfService<INF02042Request> implements
		InfService {

	@Autowired
	private BapReadRecDao bapReadRecDao;
	
	@Autowired
	private StaffDao staffDao; 

	private INF02042Request request;
	
	public void setRequest(INF02042Request request) {
		this.request = request;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeaturedClass() {
		return this.getClass();
	}

	@Override
	protected INF02042Request preHandle(String in1) throws Exception {
		request = new INF02042Request(in1);
		this.verifyByMD5(request,in1);
		this.insertInfOperInLog(request);
		this.checkCustCode(request);
		request.setStaffId(staffDao.getIdByCode(request.getStaffCode()));
		
		return request;
		
	}

	@Override
	protected  void Handle() throws INFException { 
		String staffId = request.getStaffId();
		String issueType = request.getIssueType();
		if(issueType==null)
			throw new INFException("", "请传入消息类型");
		Map<String, String> readRec = new HashMap<String, String>();
		readRec.put("issueType", issueType);
		readRec.put("staffId", staffId);
		readRec.put("issueChannel", request.getIssueChannel());
		readRec.put("issueScope", request.getIssueScope());
		readRec.put("issueDateStart", request.getIssueDateStart());
		readRec.put("issueDateEnd", request.getIssueDateEnd());
		
		int count=0;
		int noReadCount=0;
		if(issueType.equals(BapReadRecDao.ISSUE_MSG))
		{
			int countMsg = bapReadRecDao.countMsgId(readRec);
			count = countMsg;
			int countNoReadMsg = bapReadRecDao.countNoReadMsgId(readRec);
			noReadCount=countNoReadMsg;
			
		}else if(issueType.equals(BapReadRecDao.ISSUE_NOTICE))
		{
			int countNotice = bapReadRecDao.countNoticeId(readRec);
			count = countNotice;
			int countNoReadNotice = bapReadRecDao.countNoReadNoticeId(readRec);
			noReadCount = countNoReadNotice;
		}
		if(count==0) 
			throw new INFException(INFErrorDef.NO_TRANSACTION_RECORD, INFErrorDef.NO_TRANSACTION_RECORD_DESC);
		List<Map<String, String>> readReces = bapReadRecDao.selectIssueForPage(readRec, request.getStart(), request.getPage());
		for(Map<String,String> recode:readReces){
			String title = recode.get("ISSUE_NAME");
			title = HtmlUtil.convertToValidHtmlForJson(title);
			recode.put("ISSUE_NAME", title);
		}
		respBody.put("countItem", count);
		respBody.put("countNoReadItem", noReadCount);
		respBody.put("msgs", readReces);
	}


}
