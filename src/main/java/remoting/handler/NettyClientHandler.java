package remoting.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import remoting.dto.RpcResponse;

import static com.sun.activation.registries.LogSupport.log;

/**
 * @author Anvil Liu
 * @createTime 2021/7/31
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext context,Object msg) {
        try {
            RpcResponse rpcResponse = (RpcResponse) msg;
            logger.info("client receive msg:[{}]",rpcResponse.toString());
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            context.channel().attr(key).set(rpcResponse);
            context.channel().close();
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught (ChannelHandlerContext context,Throwable cause) {
        logger.error("client caught exception",cause);
        context.close();
    }
}
