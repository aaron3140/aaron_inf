package common.service;

import common.dao.TInfOperOutLogDao;
import common.entity.TInfOperOutLog;

public class TInfOperOutLogManager {

	public boolean insert(TInfOperOutLog tOperOutLog) {
		TInfOperOutLogDao tInfOperOutLogDao = new TInfOperOutLogDao();
		return tInfOperOutLogDao.insert(tOperOutLog);
	}
}
