package io.netty.decoder;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.poi.ss.formula.functions.T;

/**
 * @author lcy
 * @DESC:
 * @date 2020/8/28.
 */
public class JsonDecoder extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String strMsg = (String) msg;
        JsonMessageBo messageBo = JSON.parseObject(strMsg, JsonMessageBo.class);
        System.out.println(messageBo.toString());
        ctx.fireChannelRead(msg);
    }
}
