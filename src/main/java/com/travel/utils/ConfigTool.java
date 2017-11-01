package com.travel.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigTool {

	public static Properties props;

	static {
		props = new Properties();
		try {
			// InputStream inputStream =
			// ConfigTool.class.getClassLoader().getResourceAsStream("system.properties");
			InputStream inputStream = new FileInputStream("system.properties");
			props.load(inputStream);
			inputStream.close(); // 关闭流
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		System.out.println(ConfigTool.props.getProperty("redis_flag"));
	}

	/**
	 * 返回${${key}.keys[0]}的值
	 * 
	 * @param defultValue
	 * @param key
	 * @param keys
	 * @return
	 */
	public static String getProperty(String defultValue, String key,
			String... keys) {
		String prop = props.getProperty(key);
		if (prop == null) {
			return defultValue;
		}

		for (String otherKey : keys) {
			prop = props.getProperty(prop + "." + otherKey);
			if (prop == null) {
				return defultValue;
			}
		}

		return prop;
	}

	/**
	 * 返回${env.key}
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getEnvProperty(String key, String defaultValue) {
//		String env = props.getProperty("env");
//		if (env == null) {
//			return defaultValue;
//		}
		String env = "";
		String value = props.getProperty(env + key);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}
}
