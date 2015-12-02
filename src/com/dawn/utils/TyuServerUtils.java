package com.dawn.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Model;

public class TyuServerUtils {
	public static Logger logger = Logger.getLogger(TyuServerUtils.class);
	public static Logger debugLogger = Logger.getLogger("DebugLogger");
	public static Log logger_day = LogFactory.getLog("ACCESSLOG_DAY");
	public static Log logger_hour = LogFactory.getLog("ACCESSLOG_HOUR");
	public static Log logger_error = LogFactory.getLog("exception");
	public static SimpleDateFormat date_format = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.CHINA);

	public static SimpleDateFormat date_format_mm = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm", Locale.CHINA);

	// public static TyuChangeCharset charset = new TyuChangeCharset();

	public static void logCommon(HttpServletRequest aReq) {
		try {
			String common = aReq.getHeader("tyucommon");
			String query = aReq.getQueryString();
			String url = aReq.getRequestURI();
			logger_day.info(url + "?" + query + "=>>>" + common);
		} catch (Exception localException) {
		}
	}

	public static void logDeBug(String name,String log) {
    debugLogger.info(name+"  ---  >  "+log);
	}

	public static String getTime() {
		String res = "";
		res = date_format_mm.format(new Date());
		return res;
	}

	public static void renderZipBytes(ServletResponse aRep, String aContent)
			throws IOException {
		ZipOutputStream zout = new ZipOutputStream(aRep.getOutputStream());
		byte[] resss = aContent.getBytes();
		ZipEntry entry = new ZipEntry("resp");
		entry.setSize(resss.length);
		zout.putNextEntry(entry);
		zout.write(resss);
		zout.flush();
		zout.close();
	}

	public static void writeZipBytes(OutputStream aOut, String aContent)
			throws IOException {
		ZipOutputStream zout = new ZipOutputStream(aOut);
		byte[] resss = aContent.getBytes();
		ZipEntry entry = new ZipEntry("resp");
		entry.setSize(resss.length);
		zout.putNextEntry(entry);
		zout.write(resss);
		zout.flush();
		zout.close();
	}

	public static String readZipBytes(InputStream aInput) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		try {
			while ((len = aInput.read(buffer)) >= 0) {
				baos.write(buffer, 0, len);
			}

			ZipInputStream zipin = new ZipInputStream(new ByteArrayInputStream(
					baos.toByteArray()));
			int raw_size = baos.size();
			baos.close();

			String res = null;
			int content_size = 0;
			ZipEntry entry;
			if ((entry = zipin.getNextEntry()) != null) {
				System.out.println(entry.toString());
				baos = new ByteArrayOutputStream();
				while ((len = zipin.read(buffer)) >= 0) {
					baos.write(buffer, 0, len);
				}
				res = new String(baos.toByteArray(), 0, baos.size(), "UTF-8");
				content_size = baos.size();
				baos.close();
			}

			zipin.close();
			System.out
					.println(String.format(
							"raw:%d,content:%d",
							new Object[] { Integer.valueOf(raw_size),
									Integer.valueOf(content_size) }));
			return res;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getUTF8Para(HttpServletRequest request, String aKey) {
		String tag1 = request.getParameter(aKey);
		if (!StringUtils.isEmpty(tag1)) {
			try {
				tag1 = new String(tag1.getBytes("ISO-8859-1"), "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return tag1;
		}
		return null;
	}

	public static void logError(Exception aErr) {
		try {
			logger_error.error("error inof:", aErr);
		} catch (Exception localException) {
		}
	}

	public static JSONObject parseCommonData(String common) {
		JSONObject jobj = new JSONObject();
		try {
			String[] parts = common.split("\\|");
			for (int i = 0; i < parts.length; ++i) {
				String[] pair = parts[i].split("=");
				jobj.put(pair[0], pair[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobj;
	}

	public static String getPostString(HttpServletRequest aReq) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream is = aReq.getInputStream();
			byte[] buffer = new byte[1024];
			int len = is.read(buffer);
			while (len > 0) {
				baos.write(buffer, 0, len);

				len = is.read(buffer);
			}
			is.close();
			String base = new String(baos.toByteArray(), 0, baos.size(),
					"UTF-8");
			return new TyuChangeCharset().toGBK(base);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String getPostStringZip(HttpServletRequest aReq) {
		try {
			InputStream is = aReq.getInputStream();
			String res = readZipBytes(is);
			is.close();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String Object2JsonString(Object aObject) {
		JSONObject jobj = new JSONObject();
		try {
			Field[] fields = aObject.getClass().getFields();
			for (int i = 0; i < fields.length; ++i) {
				Object value = fields[i].get(aObject);
				jobj.put(fields[i].getName(), value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobj.toString();
	}

	public static void JsonString2Object(String aJson, Object container) {
		JSONObject jobj = JSONObject.parseObject(aJson);
		try {
			Field[] fields = container.getClass().getFields();
			for (int i = 0; i < fields.length; ++i) {
				Object value = jobj.get(fields[i].getName());
				fields[i].set(container, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String Model2Json(Model object) {
		JSONObject jb = new JSONObject();
		String[] keys = object.getAttrNames();
		for (int i = 0; i < keys.length; ++i) {
			jb.put(keys[i], object.get(keys[i]));
		}
		return jb.toString();
	}

	public static void Json2Model(String aJson, Model aModel) {
		JSONObject jb = JSONObject.parseObject(aJson);
		Set<String> keys = jb.keySet();
		for (String key : keys)
			aModel.set(key, jb.get(key));
	}

	public static void testApi() {
		TestObj obj = new TestObj();
		obj.name = "鍗фЫ";
		obj.value = "浣犲ぇ鐖?";
		obj.id = 10086;
		String ooo = Object2JsonString(obj);
		System.out.println(ooo);
		TestObj in2 = new TestObj();
		JsonString2Object(ooo, in2);
		System.out.println(in2);
	}

	public static String getInfo(String aUrl) {
		String res = null;
		try {
			URL url = new URL(aUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");

			conn.setRequestProperty("Content-Type", "application/octet-stream");

			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1626.1 Safari/537.36");

			conn.connect();

			System.out.println("statecode:" + conn.getResponseCode());
			System.out.println("Type:" + conn.getContentType());

			res = printResponse(conn);
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static String postInfo(String aUrl, String aPostInfo) {
		String res = null;
		try {
			URL url = new URL(aUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-length",
					aPostInfo.getBytes().length + "");
			conn.setRequestProperty("Content-Type", "application/octet-stream");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");

			conn.connect();
			conn.getOutputStream().write(aPostInfo.getBytes());

			System.out.println("statecode:" + conn.getResponseCode());
			System.out.println("Type:" + conn.getContentType());

			res = printResponse(conn);
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static String printResponse(HttpURLConnection conn) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buffer = new byte[1024];
		int len = 0;
		try {
			while ((len = conn.getInputStream().read(buffer)) >= 0) {
				baos.write(buffer, 0, len);
			}
			String res = new String(baos.toByteArray(), 0, baos.size(), "utf-8");
			baos.close();
			return res;
		} catch (IOException e) {
			e.printStackTrace();
			try {
				baos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	public static boolean getBaiduImageFile(String aUrl, String aLocalPath) {
		FileOutputStream fos = null;
		try {
			File file = new File(aLocalPath);
			if (file.exists())
				file.delete();
			else {
				file.getParentFile().mkdirs();
			}
			URL url = new URL(aUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");

			conn.connect();

			fos = new FileOutputStream(aLocalPath);
			byte[] buffer = new byte[10240];
			int len = 0;

			while ((len = conn.getInputStream().read(buffer)) >= 0) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
			fos.close();
			fos = null;
			return true;
		} catch (Exception e) {
			System.out.println("error:" + aUrl);
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				new File(aLocalPath).delete();
			}
		}

		return false;
	}

	public static void saveToFile(String aFileName, String data) {
		try {
			if ((data == null) || (aFileName == null)) {
				return;
			}
			new File(aFileName).delete();

			OutputStream outStream = new FileOutputStream(aFileName);

			BufferedOutputStream bout = null;

			bout = new BufferedOutputStream(outStream);

			byte[] b = data.getBytes();

			int len = b.length;

			bout.write(b, 0, len);

			bout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static double getParamDouble(Controller aCont, String aKey,
			double aDefault) {
		String tmp = aCont.getPara(aKey);
		if (StringUtils.isEmpty(tmp))
			return aDefault;
		try {
			double val = Double.parseDouble(tmp);
			return val;
		} catch (NumberFormatException e) {
		}
		return aDefault;
	}

	public static long getParamLong(Controller aCont, String aKey, long aDefault) {
		String tmp = aCont.getPara(aKey);
		if (StringUtils.isEmpty(tmp))
			return aDefault;
		try {
			long val = Long.parseLong(tmp);
			return val;
		} catch (NumberFormatException e) {
		}
		return aDefault;
	}

	public static int getParamInt(Controller aCont, String aKey, int aDefault) {
		String tmp = aCont.getPara(aKey);
		if (StringUtils.isEmpty(tmp))
			return aDefault;
		try {
			int val = Integer.parseInt(tmp);
			return val;
		} catch (NumberFormatException e) {
		}
		return aDefault;
	}

	public static String getParamString(Controller aCont, String aKey,
			boolean MustHasValue) throws Exception {
		String tmp = aCont.getPara(aKey);
		if ((MustHasValue) && (StringUtils.isEmpty(tmp))) {
			throw new Exception(aKey + " can not be null");
		}
		return tmp;
	}

	static class TestObj {
		public String name;
		public int id;
		public String value;
	}
}