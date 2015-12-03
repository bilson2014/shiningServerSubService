package com;


public class ConfigDefine {
	public final static boolean DEBUG = true;

	// server
	public final static String HOST = DEBUG ? "http://192.168.1.110:8080/shiningCenterService/"
			: "http://182.92.154.162:8080/shiningCenterService/";
	/*
	 * db
	 */
	public static String DB_IP = "127.0.0.1";
	public static String DB_PORT = "3306";
	public static String DB_NAME = "shining";

	public static String getSqlAddress() {
		return String
				.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8",
						new Object[] { DB_IP, DB_PORT, DB_NAME });
	}
}
