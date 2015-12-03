package com.dawn.utils;

import com.ConfigDefine;

public class ConsoleLog {

	public static void printf(String str) {
		if (ConfigDefine.DEBUG) {
			System.out.println(str);
		}
	}
}
