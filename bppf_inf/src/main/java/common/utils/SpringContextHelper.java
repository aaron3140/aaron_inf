package common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import websvc.DealProcessorService;

import common.dao.BaseDao;

public class SpringContextHelper implements ApplicationContextAware {
	public static ApplicationContext context;

	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		// 在加载Spring时自动获得context
		SpringContextHelper.context = context;
	}

	public static Object getBean(String beanName) {
		return context.getBean(beanName);
	}
	
	public static BaseDao getBaseDaoBean() {
		return (BaseDao) getBean("bisBaseDAO");
	}
	
	public static BaseDao getTBaseDaoBean() {
		return (BaseDao) getBean("bisBaseDAOProxy");
	}
	
	public static BaseDao getTInfDaoBean() {
		return (BaseDao) getBean("infBaseDAOProxy");
	}
	
	public static BaseDao getEasBaseDaoBean() {
//		return (BaseDao) getBean("easBaseDAO");
		return null;//bppf_eas账户已从proxool.xml中去掉，
	}
	
	public static BaseDao getInfBaseDaoBean() {
		return (BaseDao) getBean("infBaseDAO");
	}
	public static BaseDao getTvsBaseDaoBean() {
		return (BaseDao) getBean("tvsBaseDAO");
	}
	
	public static DealProcessorService getDealProcessorServiceBean() {
		return (DealProcessorService) getBean("DealProcessorService");
	}
}
