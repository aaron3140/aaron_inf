package common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import common.dao.BaseDao;

public class SeqTool {
	public static final String SQ_INF_PKTSEQ = "SQ_INF_PKTSEQ";

	public static final String SQ_SYM_TRADESEQ = "SQ_SYM_TRADESEQ";

	public static BaseDao DAO = SpringContextHelper.getBaseDaoBean();

	public static int seq = 0;

	public static String getSeq(String seqType) {
		return (String) DAO.queryForObject("select " + seqType
				+ ".nextval from dual", String.class);

	}

	public static String getSqInfPktseq() {
		return getSeq(SQ_INF_PKTSEQ);
	}

	public static String getUniqueSeq() {
		String result = DateTool.formatCurDate("yyyyMMddhhmmss");
		seq++;
		if (seq >= 10000) {
			seq = 0;
		}
		result += Charset.lpad(String.valueOf(seq), 4, "0");
		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ApplicationContext CTX = new ClassPathXmlApplicationContext(
				"testSpring.xml");
		BaseDao dao = (BaseDao) CTX.getBean("baseDAO");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String TimeStampDay = simpleDateFormat.format(new Date());// 格式化日期
		// yyyyMMdd
		String aa = (String) (dao.queryForObject("select " + SQ_INF_PKTSEQ
				+ ".nextval from dual", String.class));
		String SACCEPTSEQNO = Charset.lpad(aa, 6, "0");
		System.out.println(TimeStampDay + SACCEPTSEQNO);
	}

	public static String getSqSymTradeseq() {
		return getSeq(SQ_SYM_TRADESEQ);
	}

}
