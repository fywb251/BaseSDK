package com.zdnst.push.tool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

	private static ExecutorService pool = Executors.newCachedThreadPool();

	public static void run(Runnable runnable){
		pool.execute(runnable);
	};
}
