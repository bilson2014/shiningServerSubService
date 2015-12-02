package com.dawn;

import com.dawn.controller.SubMianController;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;

public class Config extends JFinalConfig {
public static long 	pollingTime=600000;
	@Override
	public void configConstant(Constants me) {
		// TODO Auto-generated method stub
		me.setEncoding("utf-8");

	}

	@Override
	public void configRoute(Routes me) {
		// TODO Auto-generated method stub
		me.add("/smc", SubMianController.class);
	}

	@Override
	public void configPlugin(Plugins me) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configInterceptor(Interceptors me) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configHandler(Handlers me) {
		// TODO Auto-generated method stub

	}

	// 系统启动时
	@Override
	public void afterJFinalStart() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					SubMianController tc = new SubMianController();
					while (true) {
						tc.syncVideo();
						Thread.sleep(pollingTime);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void beforeJFinalStop() {
		super.beforeJFinalStop();
	}

}
