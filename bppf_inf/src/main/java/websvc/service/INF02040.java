package websvc.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import websvc.AbstractInfService;
import websvc.InfService;

import common.dao.bis.BapReadRecDao;
import common.dao.bis.StaffDao;
import common.xml.dp.INF02040Request;

/**
 * 消息管理 --消息、公告总未条数
 * 
 * @author aaronMing
 * 
 */
@Service
public class INF02040 extends AbstractInfService<INF02040Request> implements
		InfService {

	@Autowired
	private BapReadRecDao bapReadRecDao;

	@Autowired
	private StaffDao staffDao;

	private INF02040Request request;

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeaturedClass() {
		return this.getClass();
	}

	@Override
	protected INF02040Request preHandle(String in1) throws Exception {
		request = new INF02040Request(in1);
		this.verifyByMD5(request, in1);
		this.insertInfOperInLog(request);
		this.checkCustCode(request);
		request.setStaffId(staffDao.getIdByCode(request.getStaffCode()));
		return request;

	}

	@Override
	protected void Handle() {
		// 已阅操作
		String staffId = request.getStaffId();
		Map<String, String> readRec = new HashMap<String, String>();
		readRec.put("staffId", staffId);
		readRec.put("issueChannel", "02");//手机端
		
		readRec.put("issueType", BapReadRecDao.ISSUE_NOTICE);
		int countNoReadNotice = bapReadRecDao.countNoReadNoticeId(readRec);

		readRec.put("issueType", BapReadRecDao.ISSUE_MSG);
		int countNoReadMsg = bapReadRecDao.countNoReadMsgId(readRec);
		
		respBody.put("countNoReadMsg", countNoReadMsg);
		respBody.put("countNoReadNotice", countNoReadNotice);
	}

}
