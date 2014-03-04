package common.xml.dp;


public class RankListDetailRequest  extends BaseRequest{

	private String customerId;
	
	private String rankListType;
	
	private String searchMonth;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getRankListType() {
		return rankListType;
	}

	public void setRankListType(String rankListType) {
		this.rankListType = rankListType;
	}

	public String getSearchMonth() {
		return searchMonth;
	}

	public void setSearchMonth(String searchMonth) {
		this.searchMonth = searchMonth;
	}

}
