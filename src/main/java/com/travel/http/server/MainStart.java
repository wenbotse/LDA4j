package com.travel.http.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.travel.utils.ConfigTool;

public class MainStart {
	final static Log log = LogFactory.getLog(MainStart.class);

	public static void main(String args[]) throws Exception {
		int port = Integer.parseInt(ConfigTool.props.getProperty("topic_model_service_port", "10005"));
		HttpServer server = new HttpServer();
		server.afterPropertiesSet();
		log.info("Http Server listening on "+port);
		try {
			server.start(port);
		} catch (Exception e1) {
			log.warn("exception",e1);
		}
	}
}
