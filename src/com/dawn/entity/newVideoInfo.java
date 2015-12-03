package com.dawn.entity;

public class newVideoInfo {
	private String videoName;
	private String md5;

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public newVideoInfo() {
		super();
	}

	public newVideoInfo(String videoName, String md5) {
		super();
		this.videoName = videoName;
		this.md5 = md5;
	}

	@Override
	public boolean equals(Object obj) {
		boolean res = false;
		if (obj != null && obj instanceof newVideoInfo) {
			newVideoInfo vInfo = (newVideoInfo) obj;
			if (vInfo.getVideoName() != null
					&& vInfo.getVideoName().equals(this.videoName)
					&& vInfo.getMd5() != null
					&& vInfo.getMd5().equals(this.md5))
				res = true;

		} else {
			res = false;
		}
		return res;
	}

	@Override
	public String toString() {
		return this.md5 + "%%" + this.videoName;
	}

}
