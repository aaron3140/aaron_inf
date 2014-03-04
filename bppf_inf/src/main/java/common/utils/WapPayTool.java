package common.utils;

import common.algorithm.MD5;

public class WapPayTool {

	public static String getMac(String MERCHANTID, String ORDERSEQ,
			String ORDERDATE, String ORDERAMOUNT) {

		String MAC = null;
		StringBuffer mac = new StringBuffer();

		mac.append("MERCHANTID=").append(MERCHANTID).append("&").append(
				"ORDERSEQ=").append(ORDERSEQ).append("&").append("ORDERDATE=")
				.append(ORDERDATE).append("&").append("ORDERAMOUNT=").append(
						ORDERAMOUNT).append("&").append("KEY=").append(
						"G7AXS7874305BV59");

		MAC = MD5.MD5Encode(mac.toString()).toUpperCase();

		return MAC;

	}

}
