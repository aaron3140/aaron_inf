package framework.config;

public class PayWapConfig {

    public static String CONNECT_BACKMERCHANT_TIMELIMIT;
    
    public static String WAIT_BACKMERCHANT_RES_TIMELIMIT;
    
    public static String PAYWAP_TIMELIMIT;
    
    public static String PAYWAP_ACCOUNT;
    
    public static String PAYWAP_PASSWORD;
    
    public static String PAYWAP_URL;
    
    public static String PAGE_PARAM_PATH;
    
       
    public static String getPAYWAP_URL() {
		return PAYWAP_URL;
	}

	public static void setPAYWAP_URL(String paywap_url) {
		PAYWAP_URL = paywap_url;
	}

	public static String getPAYWAP_ACCOUNT() {
		return PAYWAP_ACCOUNT;
	}

	public static void setPAYWAP_ACCOUNT(String paywap_account) {
		PAYWAP_ACCOUNT = paywap_account;
	}

	public static String getPAYWAP_PASSWORD() {
		return PAYWAP_PASSWORD;
	}

	public static void setPAYWAP_PASSWORD(String paywap_password) {
		PAYWAP_PASSWORD = paywap_password;
	}

	public static void setCONNECT_BACKMERCHANT_TIMELIMIT(
			String connect_backmerchant_timelimit) {
		CONNECT_BACKMERCHANT_TIMELIMIT = connect_backmerchant_timelimit;
	}
    
    public static String getCONNECT_BACKMERCHANT_TIMELIMIT() {
		return CONNECT_BACKMERCHANT_TIMELIMIT;
	}
    
    public static void setPAYWAP_TIMELIMIT(String paywap_timelimit) {
		PAYWAP_TIMELIMIT = paywap_timelimit;
	}
    
    public static String getPAYWAP_TIMELIMIT() {
		return PAYWAP_TIMELIMIT;
	}
    
    public static void setWAIT_BACKMERCHANT_RES_TIMELIMIT(
			String wait_backmerchant_res_timelimit) {
		WAIT_BACKMERCHANT_RES_TIMELIMIT = wait_backmerchant_res_timelimit;
	}
    
    public static String getWAIT_BACKMERCHANT_RES_TIMELIMIT() {
		return WAIT_BACKMERCHANT_RES_TIMELIMIT;
	}

	public static String getPAGE_PARAM_PATH() {
		return PAGE_PARAM_PATH;
	}

	public static void setPAGE_PARAM_PATH(String pAGEPARAMPATH) {
		PAGE_PARAM_PATH = pAGEPARAMPATH;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
