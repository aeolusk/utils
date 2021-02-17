package com.celizion.kcg.ems.tta.controller;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.function.BiConsumer;

import com.celizion.kcg.ems.tta.Message;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NEHandler implements ChannelElement {
	private EventLoopGroup bossGroup = new NioEventLoopGroup();
	private EventLoopGroup workerGroup = new NioEventLoopGroup();
	private Channel channel;

	public void start(int port) throws InterruptedException {
		this.start(port, null);
	}

	public void start(int port, BiConsumer<ConnectionContext, Message> defCommandHandler) throws InterruptedException {
		log.info("Start server... port(" + port + ")");

		try {
			ServerBootstrap b = createServerBootstrap(port, defCommandHandler);

			// Bind and start to accept incoming connections.
			ChannelFuture f = b.bind(port).sync();
			channel = f.channel();
			ConnectionManager.instance.registerServer(this, port);
		} finally {
		}
	}

	private ServerBootstrap createServerBootstrap(int port, BiConsumer<ConnectionContext, Message> defCommandHandler) {
		ServerBootstrap b = new ServerBootstrap();
		// @formatter:off
		b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					log.info("connected to client(" + ch.remoteAddress().getAddress().getHostAddress() + ":" + port + ")");

					ConnectionManager.instance.connected(NEHandler.this, ch, port);
					ch.pipeline().addLast(new TTAMessageDecoder(), new TTAMessageInBoundHandler(ch), new TTAMessageOutboundHandler());

					final String ipAddr = ((InetSocketAddress)ch.remoteAddress()).getAddress().getHostAddress();
					ch.closeFuture().addListener(new ChannelFutureListener() {
					    @Override
					    public void operationComplete(ChannelFuture future) throws Exception {
					    	log.info("connection closed to client(" + ipAddr + ":" + port + ")");
					    	ConnectionManager.instance.disconnected(ch);
					    }
					});
					
					if(defCommandHandler != null)
						ConnectionManager.instance.find(ch).addMessageHandler(defCommandHandler);
				}
			}).option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, true);
		// @formatter:on
		return b;
	}

	public void startIPv6Only(int port, BiConsumer<ConnectionContext, Message> consumer)
			throws InterruptedException, UnknownHostException {
		log.info("Start server(IPv6)... port(" + port + ")");

		try {
			ServerBootstrap b = createServerBootstrap(port, consumer);
			final InetAddress localhostIPv6 = InetAddress.getByName("::1");

			// Bind and start to accept incoming connections.
			ChannelFuture f = b.bind(localhostIPv6, port).sync();
			channel = f.channel();
			ConnectionManager.instance.registerServer(this, port);
		} finally {
		}
	}

	@Override
	public Channel getChannel() {
		return channel;
	}

	@Override
	public void close() throws InterruptedException {
		if (channel != null && channel.isOpen())
			channel.close();

		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}
}
