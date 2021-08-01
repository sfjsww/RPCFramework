package remoting.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import remoting.dto.RpcRequest;
import remoting.dto.RpcResponse;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Anvil Liu
 * @createTime 2021/7/31
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static final AtomicInteger atomicInteger = new AtomicInteger(1);

    @Override
    public void channelRead(ChannelHandlerContext context,Object msg) {
        try {
            RpcRequest rpcRequest = (RpcRequest) msg;
            logger.info("server receive msg : [{}], times : [{}]",rpcRequest.toString(),atomicInteger.getAndIncrement());
            RpcResponse messageFromServer = RpcResponse.builder().message("message from server").build();
            ChannelFuture f = context.writeAndFlush(messageFromServer);
            f.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context,Throwable cause) throws Exception {
        logger.error("server catch exception",cause);
        context.close();
    }
}
