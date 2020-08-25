package io.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

/**
 * @author lcy
 * @DESC:
 * @date 2020/8/19.
 */
public class ZeroCopyTest {
    @Test
    public void wrapTest() {
        byte[] arr = {1, 2, 3, 4, 5};
        ByteBuf byteBuf = Unpooled.wrappedBuffer(arr);
        System.out.println(byteBuf.getByte(4));
        arr[4] = 6;
        System.out.println(byteBuf.getByte(4));
    }

    @Test
    public void sliceTest() {
        ByteBuf buffer1 = Unpooled.wrappedBuffer("hello".getBytes());
        ByteBuf newBuffer = buffer1.slice(1, 2);
        newBuffer.unwrap();
        System.out.println(newBuffer.toString());
    }

    @Test
    public void compositeTest() {
        ByteBuf buffer1 = Unpooled.buffer(3);
        buffer1.writeByte(1);
        ByteBuf buffer2 = Unpooled.buffer(3);
        buffer2.writeByte(4);
        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();
        CompositeByteBuf newBuffer = compositeByteBuf.addComponents(true, buffer1, buffer2);
        System.out.println(newBuffer);
    }

    @Test
    public void buff(){
        ByteBuf buffer = Unpooled.buffer(10);
        System.out.println(buffer.toString());
    }
}
