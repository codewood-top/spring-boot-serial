package top.codewood.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AppExecutor {

	private static ScheduledExecutorService EXECUTOR;
	
	static {
		EXECUTOR = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);
	}
	
	public static ScheduledExecutorService getService() {
		return EXECUTOR;
	}
	
}
