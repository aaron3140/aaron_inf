/**
 *
 */
package cn.tisson.intf.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author Foging
 *
 */
public class ApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext  = new ClassPathXmlApplicationContext("applicationContext.xml");

    /**
     * 构造函数
     */
    public ApplicationContextHolder() { }
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		applicationContext = context;
	}

    public static ApplicationContext getContext() {
        return applicationContext;
    }

	/**
	 * @param beanName
	 * @return bean instance
	 */
	public static Object getBean(String beanName){
		return applicationContext.getBean(beanName);
	}
}
