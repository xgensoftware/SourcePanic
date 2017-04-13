package com.slobodastudio.smspanic.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadThreadManager {

	private static DownloadThreadManager instance = new DownloadThreadManager();
	private static ExecutorService executor;

	private DownloadThreadManager() {

		executor = Executors.newSingleThreadExecutor();
	}

	public static DownloadThreadManager getInstance() {

		return instance;
	}

	public void sendDownloadTask(Runnable task) {

		executor.execute(task);
	}

	public void destroySelf() {

		executor.shutdown();
	}
}
