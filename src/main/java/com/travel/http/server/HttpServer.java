package com.travel.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.travel.utils.ConfigTool;
public class HttpServer{
	private ExecutorServiceFactory threadPoolFactory = new ExecutorServiceFactory();
	private ExecutorService exe;
	//public ExecutorService handlerTimeOutExecutor;
	
	private static Log log = LogFactory.getLog(HttpServer.class);
	public void start(int port) throws Exception {
		log.info("start server");
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup(threadNum,exe);
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch)
								throws Exception {
							// server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
							ch.pipeline().addLast(new HttpResponseEncoder());
							// server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
							ch.pipeline().addLast(new HttpRequestDecoder());
							HttpServerInboundHandler handler = new HttpServerInboundHandler();
							//handler.handlerTimeOutExecutor = handlerTimeOutExecutor;
							ch.pipeline().addLast(new HttpObjectAggregator(1048576));
						//	ch.pipeline().addLast(new ReadTimeoutHandler(300,TimeUnit.MILLISECONDS));
						//	ch.pipeline().addLast(new WriteTimeoutHandler(300,TimeUnit.MILLISECONDS));
							ch.pipeline().addLast(handler);
						}
					}).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	private int threadNum = Integer.parseInt(ConfigTool.props.getProperty("rec_server_http_handler_thread_num", "10"));
	private int timeoutThreadNum = Integer.parseInt(ConfigTool.props.getProperty("rec_server_http_handler_timeout_thread_num", "10"));
	public void afterPropertiesSet() throws Exception {
		exe = threadPoolFactory.newThreadPoolWithFixedNumAndName("rec-server-http-handler-thread", threadNum, Thread.MAX_PRIORITY);
		//handlerTimeOutExecutor = threadPoolFactory.newThreadPoolWithFixedNumAndName("rec_server_http_handler_timeout_thread", timeoutThreadNum, Thread.MAX_PRIORITY);
	}
}