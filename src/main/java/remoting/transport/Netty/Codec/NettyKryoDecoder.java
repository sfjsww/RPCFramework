package remoting.transport.Netty.Codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serialize.Serializer;

import java.util.List;

import static com.sun.activation.registries.LogSupport.log;

/**
 * @author Anvil Liu
 * @createTime 2021/7/31
 */
@AllArgsConstructor
@Slf4j
public class NettyKryoDecoder extends ByteToMessageDecoder {
    private static final Logger log = LoggerFactory.getLogger(NettyKryoDecoder.class);
    private final Serializer serializer;
    private final Class<?> genericClass;

    private static final int BODY_LENTH = 4;


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        if (byteBuf.readableBytes() >= BODY_LENTH) {
            byteBuf.markReaderIndex();
            int dataLenth = byteBuf.readInt();
            if (dataLenth < 0 || byteBuf.readableBytes() < 0) {
                log.error("data length or byteBuf readableBytes is no valid!");
                return;
            }
            if (byteBuf.readableBytes() < dataLenth) {
                byteBuf.resetReaderIndex();
                return;
            }
            byte[] body = new byte[dataLenth];
            byteBuf.readBytes(body);
            Object obj = serializer.deserialize(body,genericClass);
            list.add(obj);
            log.info("successfully decode byteBuf to Obj");
        }
    }
}
