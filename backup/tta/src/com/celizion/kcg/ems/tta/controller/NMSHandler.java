package com.celizion.kcg.ems.tta.controller;

import com.celizion.kcg.ems.tta.MessageBuilder;
import com.celizion.kcg.ems.tta.controller.handler.TTAProtocolHandler;
import com.celizion.kcg.ems.tta.define.PType;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NMSHandler implements ChannelElement {
	EventLoopGroup workerGroup = new NioEventLoopGroup();
	private Channel channel;

	public Channel connect(String host, int port) throws InterruptedException {
		ChannelFuture f = null;
		try {
			Bootstrap b = createBootstrap(host, port);

			// Start the client.
			f = b.connect(host, port).sync();
			log.info("connect to server(" + host + ", port=" + port + ")");

			channel = f.channel();
			return channel;
		} finally {
			// do nothing.
		}
	}

	private Bootstrap createBootstrap(String host, int port) {
		// @formatter:off
		Bootstrap b = new Bootstrap();
		b.group(workerGroup);
		b.channel(NioSocketChannel.class)
		 .option(ChannelOption.SO_KEEPALIVE, true);
		b.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				log.info("connection initialized to server(" + host + ", port=" + port + ")");
				ConnectionManager.instance.connected(null, ch, port);
				
				ch.pipeline().addLast(new TTAMessageDecoder(), new TTAMessageInBoundHandler(ch), new TTAMessageOutboundHandler());
				ch.closeFuture().addListener(new ChannelFutureListener() {
				    @Override
				    public void operationComplete(ChannelFuture future) throws Exception {
				    	log.info("connection closed to server(" + host + ", port=" + port + ")");
				    	ConnectionManager.instance.disconnected(ch);
				    }
				});
				
				
				// Send PortTypeConfirm to server after connected.
				TTAProtocolHandler.handleMessage(ch, MessageBuilder.control(PType.Control_ACCEPT));
			}
		});
		// @formatter:on
		return b;
	}

	@Override
	public Channel getChannel() {
		return channel;
	}

	@Override
	public void close() throws InterruptedException {
		if (channel != null && channel.isOpen()) {
			channel.close();
		}

		workerGroup.shutdownGracefully();
	}
}
