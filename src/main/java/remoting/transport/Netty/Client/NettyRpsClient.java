package remoting.transport.Netty.Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import remoting.dto.RpcRequest;
import remoting.dto.RpcResponse;
import remoting.handler.NettyClientHandler;
import remoting.transport.Netty.Codec.NettyKryoDecoder;
import remoting.transport.Netty.Codec.NettyKryoEncoder;
import serialize.KryoSerializer;

/**
 * @author Anvil Liu
 * @createTime 2021/7/20
 */
@Slf4j
public class NettyRpsClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyRpsClient.class);
    private final String host;
    private final int port;
    private static final Bootstrap b;

    public NettyRpsClient (String host,int port) {
        this.host = host;
        this.port = port;
    }

    static {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        b = new Bootstrap();
        KryoSerializer kryoSerializer = new KryoSerializer();
        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcResponse.class));
                        ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcRequest.class));
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }

    public RpcResponse sendMessage (RpcRequest rpcRequest) {
        try {
            ChannelFuture f = b.connect(host,port).sync();
            logger.info("clieant connect  {}",host + ":" + port);
            Channel futureChannel = f.channel();
            logger.info("send message");
            if (futureChannel != null) {
                futureChannel.writeAndFlush(rpcRequest).addListener(future -> {
                    if (future.isSuccess()) {
                        logger.info("client send message: [{}]",rpcRequest.toString());
                    } else {
                        logger.info("send failed:",future.cause());
                    }
                });
                futureChannel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                return futureChannel.attr(key).get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
