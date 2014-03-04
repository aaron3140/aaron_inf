package common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import common.dao.BaseDao;

	/**
	 * 合作伙伴工具类
	 * 
	 * @author Administrator
	 * 
	 */
	public class PrtnUtil {

//		private static final String SEQ_ID = "-1";// 序号标识
//
//		private static final String SEQ_TYPE = "PNM_PRTNCODE";// 序号类型
//
		private static final String SET_ASIDE = "86";// 预留码
		
		// orgCode第5-7位默认值
		private static final String DEFAULT_VALUE = "001";
		
		private static final String SEQ_TYPE = "SYM_ORGCODE";// 序号类型
		
		private static final String SEQ_ID = "-1";// 序号标识
//
//		private static final String CUM_TYPE_BISI = "1";// 客户类别：企业客户
//
//		private static final String CUM_TYPE_PERS = "2";// 客户类别：个人商户
//
//		private static final String CUM_TYPE_ORG = "3";// 客户类别：机构
//
//		private static final String ACCT_IDTY_CM = "0";// 账户标识：普通支付资金主账户
//
//		private static final String ACCT_IDTY_GM = "1";// 账户标识：集团账户主账户
//
//		private static final String ACCT_IDTY_GL = "2";// 账户标识：集团账户下级账户
//
//		private static final String ACCT_IDTY_CPM = "3";// 账户标识：资金池账户主账户
//
//		private static final String ACCT_IDTY_CPL = "4";// 账户标识：资金池账户下级账户
//
//		private static final String ACCT_IDTY_IB = "5";// 账户标识：个体商户
//
//		private static final String LOW_ACCT_MAIN = "00";// 子账户编码：主账户
		
		public static BaseDao DAO = SpringContextHelper.getTBaseDaoBean();

		/**
		 * 生成唯一的合作伙伴 编码
		 * 
		 * @param useOutsideTransaction
		 * @param cumType
		 * @param areaCode
		 * @return
		 */
		public static String getPrtnCode() {

			StringBuffer cumCode = new StringBuffer();
			// 生成预留码
			cumCode.append(SET_ASIDE);

			// 生成年月日(yyMMdd) 6位
			cumCode.append(new SimpleDateFormat("yyMMdd").format(new Date()));

			// 读取seq ,不足左补零 补齐8位定长
			Long prtnRand = DAO.getLongPrimaryKey("SQ_PNM_PRTNRAND");
			cumCode.append(Charset.lpad(prtnRand.toString(), 8, "0"));

			return cumCode.toString();

		}
		
		/**
		 * 生成唯一的代理商机构编码
		 * @version: 1.00
		 * @history: 2009-5-26 下午01:52:47 [created]
		 * @author Yunzhi Ling 凌云志
		 * @param parentOrgCode
		 * @return
		 * @see
		 */
		public static String getOrgCode( String areaCode) {
			
			StringBuffer orgCode = new StringBuffer();
			// 截取区域编码前四位
			orgCode.append(areaCode.substring(0, 4));
			// 第5-7位默认值
			orgCode.append(DEFAULT_VALUE);

			Map p = DAO.getSeq(SEQ_TYPE, SEQ_ID);
			
			// 获取下一位序列值
			int nextVal = Integer.parseInt(p.get("NEXTVAL").toString());
			
			// 获取序列长度
			int length = Integer.parseInt(p.get("LENGTH").toString());
			
			// 根据序列长度向左补0
			String seq = Charset.lpad(String.valueOf(nextVal), length, "0");
			
			orgCode.append(seq);
			
			
			// 获取最大值
			int maxVal = Integer.parseInt(p.get("MAX_VAL").toString());
			
			// 如果当前值超过最大值并且此序列可以循环,重置nextValue
			if ("Y".equals(p.get("IS_CYCLE").toString()) && nextVal + 1 > maxVal) {
				
				nextVal = 1;
			} else {
				// 否则序列值+1
				nextVal = nextVal + 1;
			}
			
			DAO.updateSeq(SEQ_TYPE, SEQ_ID, nextVal);
			
			// 返回生成的机构编码
			return orgCode.toString();
		}
}
