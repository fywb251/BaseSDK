package com.zdnst.push.tool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Pool {
	private static ExecutorService pool = Executors.newCachedThreadPool();

	public static ExecutorService getPool() {
		return pool;
	}

	public static void setPool(ExecutorService pool) {
		Pool.pool = pool;
	}

	public static <V> Future<V> run(Callable<V> call) {
		return pool.submit(call);
	}

	/** 执行任务 **/
	public static void run(Runnable runnable) {
		pool.execute(runnable);
	}
	
	// /**关闭线程池**/
	// public static void close(){
	// pool.shutdown();
	// }
}
