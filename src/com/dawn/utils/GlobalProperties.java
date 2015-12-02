package com.dawn.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

public class GlobalProperties {
	private static Properties prop = null;
	private static ReentrantLock rl = new ReentrantLock();

	private GlobalProperties() {
	}

	public static Properties get() {
		if (prop == null) {
			rl.lock();
			if (prop == null) {
				try {
					InputStream is=	GlobalProperties.class.getResourceAsStream("/Config.properties");
					prop=new Properties();
					prop.load(is);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			rl.unlock();
		}
		return prop;
	}
}
