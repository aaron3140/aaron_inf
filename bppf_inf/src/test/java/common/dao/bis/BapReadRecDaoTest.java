package common.dao.bis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import common.dao.AbstractDaoTest;


public class BapReadRecDaoTest extends AbstractDaoTest {

	
	@Autowired
	BapReadRecDao bapReadRecDao;
	
	@Test
	public void testInsertReadRec() {
		Map<String, Object> readRec = new HashMap<String, Object>();
		readRec.put("readType", "01");
		readRec.put("staffCode", "admin");
		bapReadRecDao.insertReadRec(readRec);
	}

	@Test
	public void testDelReadRec() {
		String issueId = "1";
		String staffCode = "1";
		bapReadRecDao.delReadRec(issueId, staffCode);
	}
	
	@Test
	public void testSelectReadRec() {
		String content = bapReadRecDao.selectIssueContent("555");
		if(logger.isInfoEnabled())
		{
			logger.info(content);
		}
	}

	@Test
	public void testSelectReadRecesForPage() {
		Map<String, String> readRec = new HashMap<String, String>();
		readRec.put("staffCode", "test201391");
//		readRec.put("custCode", "test201391");
//		readRec.put("merId", "8613062000084223");
//		readRec.put("channelCode", "20");
//		readRec.put("tmnNum", "500012000001");
//		readRec.put("operType", "01");
//		readRec.put("tmnNum", "500012000001");
		readRec.put("issueType", "01");
		readRec.put("issueChannel","01,02");
		readRec.put("issueScope","04");
		readRec.put("issueDate", "2013/12/27 11:15:30");
		
		List<Map<String, String>> readReces = bapReadRecDao.selectIssueForPage(readRec, 2l, 10l);
		if(logger.isInfoEnabled())
		for(Map<String, String> rec:readReces)
		{
			logger.info(rec);
		}
	}

}
