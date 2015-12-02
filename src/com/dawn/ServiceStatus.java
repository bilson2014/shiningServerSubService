package com.dawn;

import java.util.concurrent.locks.ReentrantLock;

import com.dawn.net.SyncVideoThread;
import com.dawn.utils.TyuServerUtils;

public class ServiceStatus {

	private static String serviceStatus = "ok";

	static ReentrantLock rl1 = new ReentrantLock();
	static ReentrantLock rl2 = new ReentrantLock();
	public static Thread downloadThread = null;

	public static String getStatus() {
		rl1.lock();
		String res = serviceStatus;
		rl1.unlock();
		return res;
	}

	public static void setStatus(boolean isok) {
		rl1.lock();
		if (isok) {
			TyuServerUtils.logDeBug("update thread","change the server status to update ：    --> ok");
			serviceStatus = "ok";
		} else {
			TyuServerUtils.logDeBug("update thread","change the server status to update ：   -->pause");
			serviceStatus = "pause";
		}
		rl1.unlock();
	}

	public static void startDownloadRun() {
		rl2.lock();
		if (downloadThread == null || !downloadThread.isAlive()) {
			downloadThread = new Thread(new SyncVideoThread());
			downloadThread.start();
			TyuServerUtils.logDeBug("sync thread","syncvideo thread start");
		} else {
			TyuServerUtils.logDeBug("startDownloadRun","skip the syncvideo");
		}
		rl2.unlock();
	}

	public static void stopDownloadRun() {
		rl2.lock();
		downloadThread=null;
		TyuServerUtils.logDeBug("sync thread","sync thread stop");
		TyuServerUtils.logDeBug("****","****************************************");
		rl2.unlock();
	}

}
