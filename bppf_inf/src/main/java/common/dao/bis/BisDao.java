package common.dao.bis;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.BaseDao;

public abstract class BisDao {

	 final Log log = LogFactory.getLog(this.getFeaturedClass());
	 
	@SuppressWarnings("rawtypes")
	protected  abstract Class  getFeaturedClass();
	 
	@Resource(name = "bisBaseDAO")
	protected BaseDao baseDao ;

	public BisDao() {
		super();
	}

}