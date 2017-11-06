package com.travel.http.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class ExecutorServiceFactory {

	public ExecutorService newThreadPoolWithFixedNumAndName(String threadName,
			int nThreads, int priority) {
		BasicThreadFactory threadFactory = new BasicThreadFactory.Builder()
				.namingPattern(threadName + "-%d").priority(priority).daemon(true).build();
		return Executors.newFixedThreadPool(nThreads, threadFactory);
	}
	public ScheduledExecutorService newScheduledExecutorService(String threadName,
			int nThreads,int priority){
		BasicThreadFactory threadFactory = new BasicThreadFactory.Builder()
			.namingPattern(threadName + "-%d").priority(priority).daemon(true).build();
		return Executors.newScheduledThreadPool(nThreads, threadFactory);
	}
}
