package common.xml.dp;

import org.dom4j.Document;

import common.dao.BaseDao;
import common.utils.Charset;
import common.utils.SpringContextHelper;
import common.xml.CheckerAnnotion;
import common.xml.RegisterReqAbs;

public class DpInf00001Request extends RegisterReqAbs {

	public DpInf00001Request(String xmlStr) throws Exception {
		super(xmlStr, null);
		// TODO Auto-generated constructor stub
	}
	
	@CheckerAnnotion(len = 22, type = CheckerAnnotion.TYPE_NUM)
	private String tranSeq;

	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_NUM)
	private String bussType;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;
	
	public void verifyMeridTmnNum() throws Exception {
		BaseDao DAO = SpringContextHelper.getBaseDaoBean();
		String sql = "select distinct t.link_num from t_pnm_server t where t.prtn_id = " +
				"(select prtn.prtn_id from t_pnm_partner prtn where prtn.prtn_code = ?)";
		String linkNum = null;
		try {
			linkNum = (String) DAO.queryForObject(sql, new Object[]{merId}, java.lang.String.class);
		} catch (Exception e) {
			throw new Exception("机构接入号的绑定终端号异常");
		}
		linkNum = Charset.trim(linkNum);
		if( ! linkNum.equals(tmnNum)) {
			throw new Exception("机构接入号和绑定终端号不匹配");
		}
		
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		// TODO Auto-generated method stub

		tranSeq = getNodeTextM(doc, "TRANSEQ");
		
		bussType = getNodeTextM(doc, "BUSSTYPE");
		
		custCode = getNodeTextM(doc, "CUSTCODE");
		
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

	public String getTranSeq() {
		return tranSeq;
	}

	public void setTranSeq(String tranSeq) {
		this.tranSeq = tranSeq;
	}

	public String getBussType() {
		return bussType;
	}

	public void setBussType(String bussType) {
		this.bussType = bussType;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getRemark1() {
		return remark1;
	}

	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}
}
