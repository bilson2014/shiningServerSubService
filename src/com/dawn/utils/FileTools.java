package com.dawn.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.dawn.entity.newVideoInfo;

public class FileTools {
	/*
	 * 检查本地文件完整性
	 */
	public static List<String> checkLocateFileIntegrity() {
		String mediaPath = GlobalProperties.get().get("mediaBasePath")
				.toString();
		File file = new File(mediaPath);
		File[] array = file.listFiles();
		List<String> videofileList = new ArrayList<String>();
		File f;
		if (array != null && array.length > 0) {
			for (int i = 0; i < array.length; i++) {
				String[] videoName = getFileName(array[i]);
				if (videoName != null && videoName[1].equals(".mp4")) {
					f = new File(mediaPath, videoName[0] + ".jpg");
					// 如果 略缩图不存在则为未同步成功子项,可能video也为同步成功,所以删除从新同步
					if (!f.exists()) {
						array[i].delete();
					} else {
						videofileList.add(array[i].getName());
					}
				}
			}
		}
		return videofileList;
	}

	public static List<newVideoInfo> getLocateFileList() {
		String mediaPath = GlobalProperties.get().get("mediaBasePath")
				.toString();
		File file = new File(mediaPath);
		File[] array = file.listFiles();
		List<newVideoInfo> videofileList = null;
		if (array != null && array.length > 0) {
			videofileList = new ArrayList<newVideoInfo>();
			for (int i = 0; i < array.length; i++) {
				String[] videoName = getFileName(array[i]);
				if (videoName != null && videoName[1].equals(".mp4")) {
					newVideoInfo vi = new newVideoInfo();
					vi.setVideoName(array[i].getName());
					vi.setMd5(MD5Utils.getQuickFileMD5String(array[i]));
					videofileList.add(vi);
				}
			}
		}
		return videofileList;
	}

	/*
	 * 获取文件名 string[0] --> filename string[2] --> filetype
	 */
	public static String[] getFileName(File file) {
		String[] name = null;
		String fileName = file.getName();
		int lastindex = fileName.lastIndexOf('.');
		if (lastindex >= 1) {
			name = new String[2];
			name[0] = fileName.substring(0, lastindex);
			name[1] = fileName.substring(lastindex, fileName.length());
		}
		return name;
	}

	public static void main(String[] args) {
		checkLocateFileIntegrity();
	}
}
