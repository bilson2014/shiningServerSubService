package com.dawn.entity;

public class VideoInfo {
	private String videoName;
	private long size;

	@Override
	public boolean equals(Object obj) {
		super.equals(obj);
		VideoInfo vi = (VideoInfo) obj;
		boolean istrue = false;
		if (vi.getVideoName().equals(videoName) && vi.getSize() == size)
			istrue = true;
		else
			istrue = false;
		return istrue;
	}

	@Override
	public String toString() {
		super.toString();
		String str = videoName + " ~~ " + size;
		return str;
	}

	public VideoInfo() {
		super();
	}

	public VideoInfo(String videoName, long size) {
		super();
		this.videoName = videoName;
		this.size = size;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
