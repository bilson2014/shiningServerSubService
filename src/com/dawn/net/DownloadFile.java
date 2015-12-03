package com.dawn.net;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dawn.utils.GlobalProperties;
import com.dawn.utils.TyuServerUtils;

public class DownloadFile {

	public DownloadFile(List<String> fileList) {
		super();
		this.fileList = fileList;
	}

	private List<String> fileList = null;
	private static int maxDownloadCount = 5;
	private String urlBase = GlobalProperties.get().get("urlBase").toString();
	private String locatePath = GlobalProperties.get().get("mediaBasePath")
			.toString();

	// CyclicBarrier cdl;

	

	public boolean downloadFile() {
		 ExecutorService es = Executors.newFixedThreadPool(maxDownloadCount);
		// 利用固定大小线程池实现控制下载线程数量
		// cdl = new CyclicBarrier(fileList.size() + 1);
		try {
			for (int i = 0; i < this.fileList.size(); i++) {
				es.submit(new downloadThread(fileList.get(i)));
			}
			es.shutdown();  
			while (true) {  
			    if (es.isTerminated()) { 
			        break;  
			    }
			    Thread.sleep(500);  
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}  
		TyuServerUtils.logDeBug("update thread"," ExecutorService  --> shutdown（update finish）");
		return true;
	}

	// 下载线程

	class downloadThread implements Runnable {
		private String fileName;

		public downloadThread(String fileName) {
			super();
			this.fileName = fileName;
		}

		@Override
		public void run() {
			int result = -1;
			while (true) {
				// -1:下载失败 0：文件已经存在 1:下载成功
				try {
					result = HttpUrlConnection.downLoadFile(urlBase + fileName,
							locatePath + "//" + fileName, null, null);
					if (result == 1 || result == 0) {
						// 如果为下载成功，能进入这段代码
						TyuServerUtils.logDeBug("download thread",fileName+"   download succeed");
						break;
					}
					// 一分钟尝试一次
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
