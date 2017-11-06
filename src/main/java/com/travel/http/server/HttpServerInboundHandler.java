package com.travel.http.server;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hankcs.lda.main.Predictor;
import com.travel.utils.TravelUtils;

public class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {

	private static Log log = LogFactory.getLog(HttpServerInboundHandler.class);
	private static boolean debug = false;
	private HttpRequest request;
	private Predictor predictor = new Predictor();
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		debug = false;
		if (msg instanceof HttpRequest) {
			request = (HttpRequest) msg;
			String uri = request.uri();
			String clientIP = request.headers().get("X-Forwarded-For");
			if (clientIP == null) {
				InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
				clientIP = insocket.getAddress().getHostAddress();
			}
			if (TravelUtils.isEquals(uri, "/favicon.ico")) {
				return;
			}
			if (TravelUtils.isEquals(uri, "/")) {
				log.info("recieve invalid client ip=" + clientIP);
			} else {
				log.info("recieve client ip=" + clientIP);
			}
			log.info("request url:" + uri);
		}
		ResponseData responseData = new ResponseData();
		if (msg instanceof HttpContent) {
			HttpContent content = (HttpContent) msg;
			ByteBuf buf = content.content();
			// System.out.println(buf.toString(io.netty.util.CharsetUtil.UTF_8));
			buf.release();
			// 解析请求参数
			QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
			Map<String, List<String>> params = queryStringDecoder.parameters();

			List<String> docs = params.get("doc");
			StringBuilder responseMsg = new StringBuilder();
			for(String doc : docs){
				for(String l : predictor.predictTopic(doc)){
					responseMsg.append(l).append("</br>");
				}
			}
			responseData.setData(responseMsg.toString());
		}
		responseData.setCode(1);
		outputResult(ctx, responseData, debug);
	}

	public void outputResult(final ChannelHandlerContext ctx, ResponseData responseData, boolean debug)
			throws UnsupportedEncodingException {
		final FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
				Unpooled.wrappedBuffer(responseData.getData().getBytes("UTF-8")));
		response.headers().set(CONTENT_TYPE, "text/html");
		response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
		ChannelFuture f = ctx.writeAndFlush(response);
		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				ctx.close();
			}
		});
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if (cause.getMessage().equals("Connection reset by peer")) {
			log.error(cause.getMessage());
		} else {
			log.error(cause.getMessage(), cause);
		}
		ctx.close();
	}
}
