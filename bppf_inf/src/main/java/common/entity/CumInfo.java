package common.entity;

public class CumInfo {
	
	private String custCode;     //客户编码
	private String custName;  //客户名称
	private String gender;  //性别
	private String telephone;  //固定电话
	private String email;  //电子邮件
	private String province;  //个人所在地-省份
	private String city;  //个人所在地-市
	private String district;  //个人所在地-区
	private String address;  //详细地址
	
	public CumInfo(){}
	public CumInfo(String custCode,String custName,String gender,
			String telephone,String email,String province,
			String city,String district,String address){
		
		this.custCode = custCode ;
		this.custName = custName ;
		this.gender = gender ;
		this.telephone = telephone ;
		this.email = email ;
		this.province = province ;
		this.city = city ;
		this.district = district ;
		this.address = address ;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCustCode() {
		return custCode;
	}
	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
}
