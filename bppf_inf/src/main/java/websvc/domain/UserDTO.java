package websvc.domain;


public class UserDTO extends CommonRequest {

	private Long id;
	private String loginName;
	private String name;
	private String email;


	public String getEmail() {
		return email;
	}

	public Long getId() {
		return id;
	}

	public String getLoginName() {
		return loginName;
	}

	public String getName() {
		return name;
	}

	public void setEmail(String value) {
		email = value;
	}

	public void setId(Long value) {
		id = value;
	}

	public void setLoginName(String value) {
		loginName = value;
	}

	public void setName(String value) {
		name = value;
	}

}
