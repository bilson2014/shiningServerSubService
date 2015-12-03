package com.dawn.net;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.dawn.ServiceStatus;
import com.dawn.entity.newVideoInfo;
import com.dawn.utils.FileTools;
import com.dawn.utils.GlobalProperties;
import com.dawn.utils.TyuServerUtils;

public class SyncVideoThread implements Runnable {
	public String MasterServerURl = GlobalProperties.get().getProperty(
			"MasterSyncUrl");
	private String locatePath = GlobalProperties.get().get("mediaBasePath")
			.toString();

	@Override
	public void run() {
		List<String> downloadList = DownloadInit();
		if (downloadList != null && downloadList.size() > 0) {
			// 如果有更新 则更改自身状态
			ServiceStatus.setStatus(false);
			TyuServerUtils.logDeBug("update thread", "find update");
			TyuServerUtils.logDeBug("update thread", "update list -->"
					+ downloadList.toString());
			DownloadFile df = new DownloadFile(downloadList);
			df.downloadFile();
			while (true) {
				System.out.println("````````````````");
				if (DownloadInit().size() <= 0) {
					// 检测到更新为零时 修改状态
					ServiceStatus.setStatus(true);
					TyuServerUtils.logDeBug("update thread",
							"change the server status to update ：    "
									+ ServiceStatus.getStatus());
					ServiceStatus.stopDownloadRun();
					break;
				} else {
					df.downloadFile();
				}
			}
		} else {
			ServiceStatus.stopDownloadRun();
		}
		ServiceStatus.setStatus(true);
	}

	/*
	 * 初始化下载列表
	 */
	public List<String> DownloadInit() {
		List<newVideoInfo> locateFile = FileTools.getLocateFileList();
		String videoListJsonStr = HttpUrlConnection.sendGet(MasterServerURl,
				"utf-8");
		if (videoListJsonStr == null || videoListJsonStr.equals(""))
			return null;
		List<String> downloadList = GenerateDownloadList(locateFile,
				getMasterFile(videoListJsonStr));
		return downloadList;
	}

	/*
	 * 匹配本地文件生成下载列表------------------
	 */

	/*
	 * 比对本地远程列表生成下载列表
	 */
	private List<String> GenerateDownloadList(List<newVideoInfo> locateFile,
			List<newVideoInfo> MasterFile) {
		List<String> downloadList = new ArrayList<String>();
		newVideoInfo masterVideoInfo;
		if (locateFile == null || locateFile.size() < 0) {
			for (int i = 0; i < MasterFile.size(); i++) {
				downloadList.add(MasterFile.get(i).getVideoName());
			}
			return downloadList;
		}
		if (MasterFile == null || MasterFile.size() < 0) {
			return null;
		}
		
		for (int i = 0; i < MasterFile.size(); i++) {
			masterVideoInfo = MasterFile.get(i);
			if (!locateFile.contains(masterVideoInfo)) {

				if (masterVideoInfo.getMd5().length()  > 0)
					downloadList.add(masterVideoInfo.getVideoName());

				File f = new File(locatePath, masterVideoInfo.getVideoName());
				if (f.exists()) {
					TyuServerUtils.logDeBug("update thread", f.getName()
							+ "  `~  ~`  被删除 由于不完整");
					f.delete();
				}
			}
		}
		return downloadList;
	}

	/*
	 * json -- > list
	 */
	private List<newVideoInfo> getMasterFile(String jsonStr) {
		JSONArray jsonarray = JSONArray.parseArray(jsonStr);
		List<newVideoInfo> list = new ArrayList<newVideoInfo>();
		if (jsonarray != null && jsonarray.size() > 0) {
			for (int i = 0; i < jsonarray.size(); i++) {
				list.add(jsonarray.getObject(i, newVideoInfo.class));
			}
		}
		return list;
	}

}
