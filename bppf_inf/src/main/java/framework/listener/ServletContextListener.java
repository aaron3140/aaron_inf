/**
 * File                 : ServletContextListener.java
 * Copy Right           : 天讯瑞达通信技术有限公司
 * Project              : 通用统一平台
 * JDK version used     : JDK 1.5
 * Comments             : 定时执行类
 * Version              : 1.01
 * Modification history : 2009-01-13
 * Author               : 
 **/

package framework.listener;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;

import websvc.task.InfOrderStatTask;
import framework.config.ConfigReader;
import framework.config.GlobalConstants;
import framework.config.PayWapConfig;


/**
 * @author lyz
 *
 */
public class ServletContextListener implements
		javax.servlet.ServletContextListener {

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		// TODO Auto-generated method stub
		try {
			ScheduledExecutorService scheduExec = Executors.newScheduledThreadPool(1);
			
			// 担保接口回应参数定时发送
			Properties pro = ConfigReader.readConfig(GlobalConstants.TASK_CONF);
			String flag = pro.getProperty("timertask.run");
			System.out.println("【定时任务参数】初始化:"+flag);
			if(flag.equals("1")){
//				ItfGuaranteetask ig=new ItfGuaranteetask();
//				scheduExec.scheduleWithFixedDelay(ig, 1000*40, 1000*60*30, TimeUnit.MILLISECONDS);
				
				
				//启动定时扫描更新前置订单控制表处理中状态,通过查核心接口更新最新状态
//				log.info("InfOrderStatTask is start ---------");
				InfOrderStatTask ordertask = new InfOrderStatTask();
				scheduExec.scheduleWithFixedDelay(ordertask, 1000 * 40, 1000 * 60  , TimeUnit.MILLISECONDS);
			}
//			Field [] fs=PrivConstants.class.getDeclaredFields();
//			for (int i=0;i<fs.length;i++) {
//				if (fs[i].getName().startsWith("PRIV_")) {
//					event.getServletContext().setAttribute("PrivConstants_"+fs[i].getName(), fs[i].get(null));
//				}
//			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
