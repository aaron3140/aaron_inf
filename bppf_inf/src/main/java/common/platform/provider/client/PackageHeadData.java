package common.platform.provider.client;

/**
 * 组包头数据
 * @author zxx
 *
 */
public class PackageHeadData {
	public String strartSign;
	public String signNo;
	public String packageLength;
	public String packageType;
	public String structSign;
	public String checkSign;
	public String argumentNo;
	public String sendID;
	public String receiveID;
	public String space;
	public String mac;
	
	public void  setStrartSign(String strartSign){
		this.strartSign = strartSign;	
	}
	public void  setSignNo(String signNo){
		this.signNo = signNo;
	}
	public void  setPackageLength(String packageLength){
		this.packageLength = packageLength;
	}
	public void  setPackageType(String packageType){
		this.packageType = packageType;
	}	
	public void  setStructSign(String structSign){
		this.structSign = structSign;
	}	
	public void  setCheckSign(String checkSign){
		this.checkSign = checkSign;
	}	
	public void  setArgumentNo(String argumentNo){
		this.argumentNo = argumentNo;
	}	
	public void  setSendID(String sendID){
		this.sendID = sendID;
	}	
	public void  setReceiveID(String receiveID){
		this.receiveID = receiveID;
	}	
	public void  setSpace(String space){
		this.space = space;
	}	
	public void  setMac(String mac){
		this.mac = mac;
	}	
	public String getHeadOfPackageValue(){
		return this.strartSign
		+this.signNo
		+this.packageLength
		+this.packageType
		+this.structSign
		+this.checkSign
		+this.argumentNo
		+this.sendID
		+this.receiveID
		+this.space
		+this.mac;
	}
	
}
