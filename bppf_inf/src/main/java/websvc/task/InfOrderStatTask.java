package websvc.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TInfConsumeDao;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.utils.OrderConstant;

import java.util.List;
import java.util.Map;


/**
 * 
 * 本类描述: 订单控制定时任务
 * (定时更新前置订单控制表的外订单状态,为保证与核心订单状态一至)
 * @version: 企业帐户前置接口 v1.0 
 * @author: 广州天讯瑞达通讯技术有限公司(zhuxiaojun)
 * @email:  zhuxiaojun@tisson.com
 * @time: 2013-4-7下午05:09:31
 */
public class InfOrderStatTask implements Runnable {

	private static final Log log = LogFactory.getLog(InfOrderStatTask.class);

	public void run() {
		
		consumeStatProcess();
	}
	
	private void consumeStatProcess(){
		
		TInfConsumeDao dao = new TInfConsumeDao();
		
		List<Map> list = dao.getTConsumeList();
		
		if(list!=null&&list.size()>0){
			
			log.info("扫描到"+list.size()+"笔消费记录需更新状态");
			
			PackageDataSet data = null;
			String tmnnum = null;
			String orderNo = null;
			
			for(int i=0;i<list.size();i++){
				
				orderNo = (String)list.get(i).get("ORDERNO");
				
				tmnnum = (String)list.get(i).get("TERM_ID");
				
				try {
					
					data = queryOrderProcess(tmnnum,orderNo);
				} catch (Exception e) {
		
					log.info("调用SCS0014服务 获取消费单状态失败");
					break;
				}
				if(data!=null){
					
				    String stat = data.getByID("0001", "000");
				    
					String paystat= data.getByID("4013", "401");//支付状态 4014 4015
					
					if(stat!=null&&stat.equals("0000")){
						
						if(paystat!=null){
							
							dao.updateTConsume(orderNo,paystat);
							
							log.info("更新消费单状态成功:"+paystat);
						}else{

							dao.updateTConsume(orderNo,OrderConstant.S0F);
							
							log.info("更新消费单状态成功");
						}
					}
				}
			}
		}else
			log.info("扫描消费记录完成 size 0");
	}

	/**
	 * 获得订单信息
	 * @param tmnnum 终端号
	 * @param order  外部订单号
	 * @return 订单信息数据包
	 * @throws Exception 
	 */
	private PackageDataSet queryOrderProcess(String tmnnum,String order) throws Exception{
		PackageDataSet packdata = null;
		IParamGroup g001 = new ParamGroupImpl("001");
		g001.put("0012", "1"); // 开始记录
		g001.put("0013", "1"); // 结束记录
		g001.endRow();
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "4007");// 查询条件：终端号
		g002.put("0022", tmnnum);
		g002.endRow();
		g002.put("0021", "4028");// 查询条件：外部订单号
		g002.put("0022", order);
		g002.endRow();
		
		// 组成数据包,调用CUM0002接口
		IServiceCall caller = new ServiceCallImpl();
	    packdata = caller.call("SCS", "SCS0014", g001,g002);
		return packdata;
	}

}
