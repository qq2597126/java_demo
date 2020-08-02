package utils;

import java.nio.ByteBuffer;

public class NioUtils {

    /**
     *
     * @param in
     * @param out
     */
    public static void addByteBuffer(ByteBuffer in,ByteBuffer out){
        ByteBuffer oldByteBuffer = in;
        out.flip();
        // 判断是否扩容
        if(in.capacity() > (in.position() + out.limit())){
            in.put(out.array());
        }else{
            int newCapacity = oldByteBuffer.position()+ (oldByteBuffer.position() >> 1)+out.limit();
            oldByteBuffer.flip();
            in = ByteBuffer.allocate(newCapacity);
            in.put(oldByteBuffer.array());
            in.put(out.array());
        }
    }
}
