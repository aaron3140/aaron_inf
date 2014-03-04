package common.entity;

public class PdmInclude {
	private String apply_obj_id2;

	private String memo;

	public PdmInclude() {}

	public PdmInclude(String apply_obj_id2, String memo) {
		this.apply_obj_id2 = apply_obj_id2;
		this.memo = memo;
	}

	public String getApply_obj_id2() {
		return apply_obj_id2;
	}

	public void setApply_obj_id2(String apply_obj_id2) {
		this.apply_obj_id2 = apply_obj_id2;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}
