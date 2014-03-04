package websvc;


public interface InfService {
	
	/**
	 * 统一在接口分发时处理的所有业务
	 * @param in1 xml格式请求数据
	 * @return
	 */
	public  String execute(String in1);

}