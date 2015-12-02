package com.dawn.controller;

import org.junit.Test;

import com.dawn.ServiceStatus;
import com.dawn.utils.TyuServerUtils;
import com.jfinal.core.Controller;

public class SubMianController extends Controller {

	public void test() {
		renderText(ServiceStatus.getStatus());
	}

	@Test
	public void syncVideo() {
		ServiceStatus.startDownloadRun();
		renderText("sync service start");
	}

}
