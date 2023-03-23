package jp.mochisystems.erc._mc._core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ERC_Logger {
	 
	public static Logger logger = LogManager.getLogger("ERC");
 
	public static void error(String msg) {
		ERC_Logger.logger.error(msg);
	}
 
	public static void info(String msg) {
		ERC_Logger.logger.info(msg);
	}
 		
	public static void warn(String msg) {
		ERC_Logger.logger.warn(msg);
	}
}