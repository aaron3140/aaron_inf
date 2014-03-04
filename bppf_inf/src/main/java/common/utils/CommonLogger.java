package common.utils;

import org.apache.log4j.*;

public class CommonLogger {
	private static Logger LOG = null;

	public static void info(String info, Throwable thr) {
		LOG.info(info, thr);
	}

	public static void error(String err, Throwable thr) {
		LOG.error(err, thr);
	}

	public static void init(String proPath) {
		PropertyConfigurator.configure(proPath);
		LOG = Logger.getLogger(CommonLogger.class);
	}

	public static void info(String info) {
		LOG.info(info);
	}

	public static void error(String err) {
		LOG.error(err);
	}

	public static void warn(String warn) {
		LOG.warn(warn);
	}

}
